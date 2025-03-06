/**
 *  ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fr.univlorraine.ecandidat.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.LockCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.LockCandidatPK;
import fr.univlorraine.ecandidat.repositories.LockCandidatRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.LockCandidatListener;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import jakarta.annotation.Resource;

/**
 * Controller gérant les appels Ldap
 * @author Kevin Hergalant
 */
@Component
public class LockCandidatController {

	/* Logger */
	private final Logger logger = LoggerFactory.getLogger(LockCandidatController.class);

	/* applicationContext pour les messages */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockCandidatRepository lockCandidatRepository;
	@Resource
	private transient LoadBalancingController loadBalancingController;

	/**
	 * @return la liste des locks de l'application
	 */
	public List<LockCandidat> getListLockMore24Heure() {
		LocalDateTime timeLess24 = LocalDateTime.now();
		timeLess24 = timeLess24.minusHours(24);
		return lockCandidatRepository.findByDatLockBefore(timeLess24);
	}

	/**
	 * supprime un lock
	 * @param lock
	 * @param listener
	 */
	public void deleteLock(final LockCandidat lock, final LockCandidatListener listener) {
		final ConfirmWindow confirmWindow = new ConfirmWindow(
			applicationContext.getMessage("lock.candidat.window.confirmDelete", new Object[] { lock.getId().getRessourceLock(), lock.getId().getNumDossierOpiCptMin() }, UI.getCurrent().getLocale()),
			applicationContext.getMessage("lock.candidat.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			if (!lockCandidatRepository.existsById(lock.getId())) {
				/* Contrôle que le lock existe encore */
				Notification.show(applicationContext.getMessage("lock.candidat.error.delete", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			} else {
				lockCandidatRepository.deleteById(lock.getId());
				Notification.show(applicationContext.getMessage("lock.candidat.delete.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
			}
			listener.lockCandidatDeleted(lock);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Supprime tous les locks
	 * @param listeLock
	 * @param listener
	 */
	public void deleteAllLock(final List<LockCandidat> listeLock, final LockCandidatListener listener) {
		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("lock.candidat.all.window.confirmDelete", null, UI.getCurrent().getLocale()),
			applicationContext.getMessage("lock.candidat.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			Boolean allLockDeleted = true;
			for (final LockCandidat lock : listeLock) {
				if (!lockCandidatRepository.existsById(lock.getId())) {
					/* Contrôle que le lock existe encore */
					allLockDeleted = false;
				} else {
					lockCandidatRepository.deleteById(lock.getId());
				}
			}

			if (!allLockDeleted) {
				Notification.show(applicationContext.getMessage("lock.candidat.all.error.delete", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			} else {
				Notification.show(applicationContext.getMessage("lock.candidat.all.delete.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
			}

			listener.lockCandidatAllDeleted();
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * @return l'id de l'ui de l'utilisateur
	 */
	private String getUIId() {
		final MainUI ui = (MainUI) UI.getCurrent();
		if (ui == null) {
			return null;
		} else {
			return ui.getUiId();
		}
	}

	/**
	 * Supprime tous les locks d'une instance
	 */
	public void cleanAllLockCandidatForInstance() {
		cleanAllLockCandidatForInstance(loadBalancingController.getIdInstance());
	}

	/**
	 * Supprime tous les locks d'une instance
	 */
	public void cleanAllLockCandidatForInstance(final String idInstance) {
		logger.info("Nettoyage des locks pour l'instance " + idInstance);
		lockCandidatRepository.deleteAllInBatch(lockCandidatRepository.findByInstanceIdLock(idInstance));
	}

	/**
	 * créé un lock
	 * @param  cptMin
	 * @param  ressource
	 * @return           true si le lock a bien été enregistré, false si la ressource est deja lockée
	 */
	public Boolean getLock(final CompteMinima cptMin, final String ressource) {
		/* Suite au bug de strasbourg (cptMin à null), ajout d'un log de warn */
		if (cptMin == null || cptMin.getNumDossierOpiCptMin() == null) {
			logger.warn("Compte à minima ou NumDossierOpi null, cela ne devrait jamais arriver, cptMin = " + cptMin);
			return false;
		}

		final String uiId = getUIId();
		if (uiId == null) {
			return false;
		}

		final LockCandidatPK lockPk = new LockCandidatPK(cptMin.getNumDossierOpiCptMin(), ressource);
		final LockCandidat lock = lockCandidatRepository.findById(lockPk).orElse(null);

		if (lock != null && (!lock.getUiIdLock().equals(uiId) || !lock.getInstanceIdLock().equals(loadBalancingController.getIdInstance()))) {
			return false;
		}
		if (lock != null && lock.getUiIdLock().equals(uiId) && lock.getInstanceIdLock().equals(loadBalancingController.getIdInstance())) {
			return true;
		}
		try {
			lockCandidatRepository.saveAndFlush(new LockCandidat(lockPk, loadBalancingController.getIdInstance(), uiId));
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * Vérifie qu'une ressource est lockée
	 * @return true si la ressource verrouillée pour une autre UI, false sinon
	 */
	public boolean checkLock(final CompteMinima cptMin, final String ressource) {
		final String uiId = getUIId();
		if (uiId == null) {
			return true;
		}
		final LockCandidat lock = lockCandidatRepository.findById(new LockCandidatPK(cptMin.getNumDossierOpiCptMin(), ressource)).orElse(null);
		if (lock != null && !lock.getUiIdLock().equals(uiId)) {
			return true;
		}
		return false;
	}

	/**
	 * Rend un verrou, après avoir vérifié qu'il appartient à l'UI courante
	 * @param cptMin
	 * @param ressource
	 */
	public void releaseLock(final CompteMinima cptMin, final String ressource) {
		final String uiId = getUIId();
		if (uiId == null || cptMin == null || ressource == null) {
			return;
		}
		final LockCandidat lock = lockCandidatRepository.findById(new LockCandidatPK(cptMin.getNumDossierOpiCptMin(), ressource)).orElse(null);
		if (lock != null && lock.getUiIdLock().equals(uiId) && lock.getInstanceIdLock().equals(loadBalancingController.getIdInstance())) {
			removeLock(lock);
		}
	}

	/**
	 * Rend un verrou de candidature
	 * @param candidature
	 */
	public void releaseLockCandidature(final Candidature candidature) {
		releaseLock(candidature.getCandidat().getCompteMinima(), ConstanteUtils.LOCK_CAND + "_" + candidature.getIdCand());
	}

	/**
	 * Supprime tout les locks de l'UI
	 * @param uiId
	 */
	public void removeAllLockUI(final String uiId) {
		if (uiId == null) {
			return;
		}
		lockCandidatRepository.deleteAllInBatch(lockCandidatRepository.findByUiIdLockAndInstanceIdLock(uiId, loadBalancingController.getIdInstance()));
	}

	/**
	 * Supprime un verrou
	 * @param cptMin
	 * @param ressource
	 */
	/* public void removeLock(CompteMinima cptMin, String ressource) {
	 * LockCandidat lock = lockCandidatRepository.getOne(new LockCandidatPK(cptMin.getNumDossierOpiCptMin(), ressource));
	 * if (lock!=null) {
	 * removeLock(lock);
	 * }
	 * } */

	/**
	 * Supprime un verrou
	 * @param lock
	 */
	private void removeLock(final LockCandidat lock) {
		lockCandidatRepository.delete(lock);
	}

	/**
	 * @param  candidature
	 * @return             true si la candidature est lockée
	 */
	public boolean getLockOrNotifyCandidature(final Candidature candidature) {
		if (candidature == null) {
			return false;
		}
		return getLockOrNotify(candidature.getCandidat().getCompteMinima(),
			ConstanteUtils.LOCK_CAND + "_" + candidature.getIdCand(),
			applicationContext.getMessage("lock.message.candidature", new Object[] { candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin() }, UI.getCurrent().getLocale()));
	}

	/**
	 * Verrouille une ressource pour l'UI courante
	 * @param  cptMin
	 * @param  ressource
	 * @param  msgIfAlreadyLocked message affiché si la ressource est déjà verrouillée pour une autre UI. Si cette propriété vaut null, un message par défaut est
	 *                               affiché.
	 * @return                    true si la ressource est bien verrouillée pour l'UI courante, false sinon
	 */
	private boolean getLockOrNotify(final CompteMinima cptMin, final String ressource, String msgIfAlreadyLocked) {
		final boolean ret = getLock(cptMin, ressource);
		if (!ret) {
			if (msgIfAlreadyLocked == null || msgIfAlreadyLocked.isEmpty()) {
				msgIfAlreadyLocked = applicationContext.getMessage("lock.message.candidat", null, UI.getCurrent().getLocale());
			}
			Notification.show(msgIfAlreadyLocked, Notification.Type.WARNING_MESSAGE);
		}
		return ret;
	}
}
