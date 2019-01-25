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

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.repositories.MotivationAvisRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionCandidatureRepository;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.ScolMotivationAvisWindow;

/**
 * Gestion de l'entité motivationAvis
 *
 * @author Kevin Hergalant
 */
@Component
public class MotivationAvisController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient MotivationAvisRepository motivationAvisRepository;
	@Resource
	private transient TypeDecisionCandidatureRepository typeDecisionCandidatureRepository;

	/**
	 * @return liste des motivationAvis
	 */
	public List<MotivationAvis> getMotivationAvisEnServiceByCtrCand(final CentreCandidature ctrCand) {
		// motiv de la scol centrale
		List<MotivationAvis> liste = motivationAvisRepository.findByTesMotivAndCentreCandidatureIdCtrCand(true, null);
		// motiv pour les ctrCand
		if (ctrCand != null) {
			liste.addAll(motivationAvisRepository.findByTesMotivAndCentreCandidatureIdCtrCand(true, ctrCand.getIdCtrCand()));
		}
		liste.sort((h1, h2) -> h1.getCodMotiv().compareTo(h2.getCodMotiv()));
		return liste;
	}

	/**
	 * @param idCtrCand
	 * @return la liste des MotivationAvis par centre de candidature
	 */
	public List<MotivationAvis> getMotivationAvisByCtrCand(
			final Integer idCtrCand) {
		return motivationAvisRepository.findByCentreCandidatureIdCtrCand(idCtrCand);
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau motivationAvis.
	 */
	public void editNewMotivationAvis(final CentreCandidature ctrCand) {
		MotivationAvis motiv = new MotivationAvis(userController.getCurrentUserLogin());
		motiv.setTesMotiv(true);
		motiv.setI18nLibMotiv(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_MOTIV_LIB)));
		motiv.setCentreCandidature(ctrCand);
		UI.getCurrent().addWindow(new ScolMotivationAvisWindow(motiv));
	}

	/**
	 * Ouvre une fenêtre d'édition de motivationAvis.
	 *
	 * @param motivationAvis
	 */
	public void editMotivationAvis(final MotivationAvis motivationAvis) {
		Assert.notNull(motivationAvis, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(motivationAvis, null)) {
			return;
		}
		ScolMotivationAvisWindow window = new ScolMotivationAvisWindow(motivationAvis);
		window.addCloseListener(e -> lockController.releaseLock(motivationAvis));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un motivationAvis
	 *
	 * @param motivationAvis
	 */
	public void saveMotivationAvis(MotivationAvis motivationAvis) {
		Assert.notNull(motivationAvis, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (motivationAvis.getIdMotiv() != null && !lockController.getLockOrNotify(motivationAvis, null)) {
			return;
		}
		motivationAvis.setUserModMotiv(userController.getCurrentUserLogin());
		motivationAvis.setI18nLibMotiv(i18nController.saveI18n(motivationAvis.getI18nLibMotiv()));
		motivationAvis = motivationAvisRepository.saveAndFlush(motivationAvis);

		lockController.releaseLock(motivationAvis);
	}

	/**
	 * Supprime une motivationAvis
	 *
	 * @param motivationAvis
	 */
	public void deleteMotivationAvis(final MotivationAvis motivationAvis) {
		Assert.notNull(motivationAvis, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		if (typeDecisionCandidatureRepository.countByMotivationAvis(motivationAvis) > 0) {
			Notification.show(applicationContext.getMessage("motivAvis.error.delete", new Object[] {TypeDecisionCandidature.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(motivationAvis, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("motivAvis.window.confirmDelete", new Object[] {motivationAvis.getCodMotiv()}, UI.getCurrent().getLocale()),
				applicationContext.getMessage("motivAvis.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(motivationAvis, null)) {
				motivationAvisRepository.delete(motivationAvis);
				/* Suppression du lock */
				lockController.releaseLock(motivationAvis);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(motivationAvis);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Verifie l'unicité du code
	 *
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodMotivUnique(final String cod, final Integer id) {
		MotivationAvis motiv = motivationAvisRepository.findByCodMotiv(cod);
		if (motiv == null) {
			return true;
		} else {
			if (motiv.getIdMotiv().equals(id)) {
				return true;
			}
		}
		return false;
	}
}
