/** ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package fr.univlorraine.ecandidat.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu_;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusInterne;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPro;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatStage;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.siscol.WSBac;
import fr.univlorraine.ecandidat.entities.siscol.WSCursusInterne;
import fr.univlorraine.ecandidat.repositories.CandidatBacOuEquRepository;
import fr.univlorraine.ecandidat.repositories.CandidatCursusInterneRepository;
import fr.univlorraine.ecandidat.repositories.CandidatCursusPostBacRepository;
import fr.univlorraine.ecandidat.repositories.CandidatCursusProRepository;
import fr.univlorraine.ecandidat.repositories.CandidatStageRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatBacListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatCursusExterneListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatFormationProListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatStageListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.views.windows.CandidatBacWindow;
import fr.univlorraine.ecandidat.views.windows.CandidatCursusExterneWindow;
import fr.univlorraine.ecandidat.views.windows.CandidatCursusProWindow;
import fr.univlorraine.ecandidat.views.windows.CandidatStageWindow;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;

/** Gestion du parcours d'un candidat
 *
 * @author Kevin Hergalant */
@Component
public class CandidatParcoursController {
	/* Injections */
	private Logger logger = LoggerFactory.getLogger(CandidatParcoursController.class);
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient CandidatBacOuEquRepository candidatBacOuEquRepository;
	@Resource
	private transient CandidatCursusInterneRepository candidatCursusInterneRepository;
	@Resource
	private transient CandidatCursusPostBacRepository candidatCursusPostBacRepository;
	@Resource
	private transient CandidatCursusProRepository candidatCursusProRepository;
	@Resource
	private transient CandidatStageRepository candidatStageRepository;

	/** Edition du bac */
	public void editBac(final Candidat candidat, final CandidatBacListener listener) {
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_BAC);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		CandidatBacOuEqu bac = candidat.getCandidatBacOuEqu();
		Boolean edition = true;
		if (bac == null) {
			bac = new CandidatBacOuEqu();
			bac.setTemUpdatableBac(true);
			bac.setIdCandidat(candidat.getIdCandidat());
			bac.setCandidat(candidat);
			edition = false;
		}

		CandidatBacWindow window = new CandidatBacWindow(bac, edition);
		window.addBacWindowListener(e -> {
			listener.bacModified(e);
		});
		UI.getCurrent().addWindow(window);
	}

	/** Enregistre un bac
	 *
	 * @param bac
	 */
	public CandidatBacOuEqu saveBac(final CandidatBacOuEqu bac) {
		return candidatBacOuEquRepository.save(bac);
	}

	/** Renvoie les info de bac
	 *
	 * @param candidatBacOuEqu
	 * @return les infos du bac */
	public List<SimpleTablePresentation> getInformationsBac(final CandidatBacOuEqu candidatBacOuEqu) {
		List<SimpleTablePresentation> liste = new ArrayList<>();
		liste.add(new SimpleTablePresentation(1, CandidatBacOuEqu_.anneeObtBac.getName(), applicationContext.getMessage("infobac."
				+ CandidatBacOuEqu_.anneeObtBac.getName(), null, UI.getCurrent().getLocale()), candidatBacOuEqu.getAnneeObtBac()));
		liste.add(new SimpleTablePresentation(2, CandidatBacOuEqu_.siScolBacOuxEqu.getName(), applicationContext.getMessage("infobac."
				+ CandidatBacOuEqu_.siScolBacOuxEqu.getName(), null, UI.getCurrent().getLocale()), candidatBacOuEqu.getSiScolBacOuxEqu() == null ? null
						: candidatBacOuEqu.getSiScolBacOuxEqu().getLibBac()));
		liste.add(new SimpleTablePresentation(3, CandidatBacOuEqu_.siScolMentionNivBac.getName(), applicationContext.getMessage("infobac."
				+ CandidatBacOuEqu_.siScolMentionNivBac.getName(), null, UI.getCurrent().getLocale()), candidatBacOuEqu.getSiScolMentionNivBac() == null ? null
						: candidatBacOuEqu.getSiScolMentionNivBac().getLibMnb()));
		liste.add(new SimpleTablePresentation(4, CandidatBacOuEqu_.siScolPays.getName(), applicationContext.getMessage("infobac."
				+ CandidatBacOuEqu_.siScolPays.getName(), null, UI.getCurrent().getLocale()), candidatBacOuEqu.getSiScolPays() == null ? null : candidatBacOuEqu.getSiScolPays().getLibPay()));

		if (candidatBacOuEqu.getSiScolPays() != null && candidatBacOuEqu.getSiScolPays().equals(cacheController.getPaysFrance())) {
			liste.add(new SimpleTablePresentation(5, CandidatBacOuEqu_.siScolDepartement.getName(), applicationContext.getMessage("infobac."
					+ CandidatBacOuEqu_.siScolDepartement.getName(), null, UI.getCurrent().getLocale()), candidatBacOuEqu.getSiScolDepartement() == null ? null
							: candidatBacOuEqu.getSiScolDepartement().getLibDep()));
			liste.add(new SimpleTablePresentation(6, CandidatBacOuEqu_.siScolCommune.getName(), applicationContext.getMessage("infobac."
					+ CandidatBacOuEqu_.siScolCommune.getName(), null, UI.getCurrent().getLocale()), candidatBacOuEqu.getSiScolCommune() == null ? null
							: candidatBacOuEqu.getSiScolCommune().getLibCom()));
			liste.add(new SimpleTablePresentation(7, CandidatBacOuEqu_.siScolEtablissement.getName(), applicationContext.getMessage("infobac."
					+ CandidatBacOuEqu_.siScolEtablissement.getName(), null, UI.getCurrent().getLocale()), candidatBacOuEqu.getSiScolEtablissement() == null ? null
							: candidatBacOuEqu.getSiScolEtablissement().getLibEtb()));
		}

		return liste;
	}

	/** Edition d'un cursus */
	public void editCursusPostBac(final Candidat candidat, CandidatCursusPostBac cursus, final CandidatCursusExterneListener listener) {
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_CURSUS_EXTERNE);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		Boolean nouveau = false;
		if (cursus == null) {
			cursus = new CandidatCursusPostBac();
			cursus.setCandidat(candidat);
			nouveau = true;
		}

		CandidatCursusExterneWindow window = new CandidatCursusExterneWindow(cursus, nouveau);
		window.addCursusPostBacWindowListener(e -> {
			candidat.addCursusPostBac(e);
			listener.cursusModified(candidat.getCandidatCursusPostBacs());
		});
		UI.getCurrent().addWindow(window);
	}

	/** Enregistre un cursus
	 *
	 * @param cursus
	 * @return le cursus post bac */
	public CandidatCursusPostBac saveCursusPostBac(final CandidatCursusPostBac cursus) {
		return candidatCursusPostBacRepository.save(cursus);
	}

	/** Supprime un cursus
	 *
	 * @param candidat
	 * @param cursus
	 * @param listener
	 */
	public void deleteCursusPostBac(final Candidat candidat, final CandidatCursusPostBac cursus, final CandidatCursusExterneListener listener) {
		Assert.notNull(cursus, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_CURSUS_EXTERNE);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("cursusexterne.confirmDelete", null, UI.getCurrent().getLocale()), applicationContext.getMessage("cursusexterne.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			candidatCursusPostBacRepository.delete(cursus);
			candidat.getCandidatCursusPostBacs().remove(cursus);
			listener.cursusModified(candidat.getCandidatCursusPostBacs());
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Enregistre un cursus pro
	 *
	 * @param cursus
	 * @return le cursus pro */
	public CandidatCursusPro saveCursusPro(final CandidatCursusPro cursus) {
		return candidatCursusProRepository.save(cursus);
	}

	/** Edition d'une formation pro */
	public void editFormationPro(final Candidat candidat, CandidatCursusPro cursus, final CandidatFormationProListener listener) {
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_FORMATION_PRO);

		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		Boolean nouveau = false;
		if (cursus == null) {
			cursus = new CandidatCursusPro();
			cursus.setCandidat(candidat);
			nouveau = true;
		}

		CandidatCursusProWindow window = new CandidatCursusProWindow(cursus, nouveau);
		window.addCursusProWindowListener(e -> {
			candidat.addCursusPro(e);
			listener.formationProModified(candidat.getCandidatCursusPros());
		});
		UI.getCurrent().addWindow(window);
	}

	/** Supprime un cursus pro
	 *
	 * @param candidat
	 * @param cursus
	 * @param listener
	 */
	public void deleteFormationPro(final Candidat candidat, final CandidatCursusPro cursus, final CandidatFormationProListener listener) {
		Assert.notNull(cursus, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_FORMATION_PRO);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("formationPro.confirmDelete", null, UI.getCurrent().getLocale()), applicationContext.getMessage("formationPro.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			candidatCursusProRepository.delete(cursus);
			candidat.getCandidatCursusPros().remove(cursus);
			listener.formationProModified(candidat.getCandidatCursusPros());
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Enregistre un stage
	 *
	 * @param stage
	 * @return le stage */
	public CandidatStage saveStage(final CandidatStage stage) {
		return candidatStageRepository.save(stage);
	}

	/** Edition d'un stage */
	public void editStage(final Candidat candidat, CandidatStage stage, final CandidatStageListener listener) {
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_STAGE);

		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		Boolean nouveau = false;
		if (stage == null) {
			stage = new CandidatStage();
			stage.setCandidat(candidat);
			nouveau = true;
		}

		CandidatStageWindow window = new CandidatStageWindow(stage, nouveau);
		window.addCursusProWindowListener(e -> {
			candidat.addStage(e);
			listener.stageModified(candidat.getCandidatStage());
		});
		UI.getCurrent().addWindow(window);
	}

	/** Supprime un stage
	 *
	 * @param candidat
	 * @param stage
	 * @param listener
	 */
	public void deleteStage(final Candidat candidat, final CandidatStage stage, final CandidatStageListener listener) {
		Assert.notNull(stage, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_STAGE);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("stage.confirmDelete", null, UI.getCurrent().getLocale()), applicationContext.getMessage("stage.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			candidatStageRepository.delete(stage);
			candidat.getCandidatStage().remove(stage);
			listener.stageModified(candidat.getCandidatStage());
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Renvoie un bac grace aux données apogee
	 *
	 * @param bacApogee
	 * @param candidat
	 * @param needToDeleteDataApogee
	 * @return le bac provenant d'apogee */
	public CandidatBacOuEqu getBacByApogeeData(final WSBac bacApogee, final Candidat candidat, final Boolean needToDeleteDataApogee) {
		if (bacApogee != null) {
			if (candidat.getCandidatBacOuEqu() != null) {
				candidatBacOuEquRepository.delete(candidat.getCandidatBacOuEqu());
				candidat.setCandidatBacOuEqu(null);
			}
			SiScolPays pays = null;
			SiScolDepartement dpt = tableRefController.getDepartementByCode(bacApogee.getCodDep());
			if (dpt != null) {
				pays = cacheController.getPaysFrance();
			}
			Integer anneeObt = null;
			try {
				anneeObt = Integer.valueOf(bacApogee.getDaaObtBacIba());
			} catch (Exception e) {
			}
			SiScolCommune commune = null;
			SiScolEtablissement etab = tableRefController.getEtablissementByCode(bacApogee.getCodEtb());
			SiScolMentionNivBac mention = tableRefController.getMentionNivBacByCode(bacApogee.getCodMnb());
			SiScolBacOuxEqu bacOuEqu = tableRefController.getBacOuEquByCode(bacApogee.getCodBac());
			if (bacOuEqu == null) {
				return null;
			}
			CandidatBacOuEqu candidatBacOuEqu = new CandidatBacOuEqu(candidat.getIdCandidat(), anneeObt, bacOuEqu, commune, dpt, etab, mention, pays, candidat, false);
			if (MethodUtils.validateBean(candidatBacOuEqu, logger)) {
				return candidatBacOuEquRepository.save(candidatBacOuEqu);
			}
			return null;
		} else {
			/*
			 * if (candidat.getTemUpdatableCandidat() && candidat.getCandidatBacOuEqu()!=null && !candidat.getCandidatBacOuEqu().getTemUpdatableBac()){
			 * candidat.getCandidatBacOuEqu().setTemUpdatableBac(true);
			 * return candidatBacOuEquRepository.save(candidat.getCandidatBacOuEqu());
			 * }
			 */
			if (needToDeleteDataApogee && candidat.getCandidatBacOuEqu() != null) {
				candidatBacOuEquRepository.delete(candidat.getCandidatBacOuEqu());
				candidat.setCandidatBacOuEqu(null);
			}
			return candidat.getCandidatBacOuEqu();
		}
	}

	/** Renvoie la liste des cursus interne grace aux données apogee
	 *
	 * @param listeCursusApogee
	 * @param candidat
	 * @return la liste des cursus interne */
	public List<CandidatCursusInterne> getCursusInterne(final List<WSCursusInterne> listeCursusApogee, final Candidat candidat, final Boolean needToDeleteDataApogee) {
		if (listeCursusApogee != null && listeCursusApogee.size() > 0) {
			if (candidat.getCandidatCursusInternes() != null && candidat.getCandidatCursusInternes().size() > 0) {
				candidat.getCandidatCursusInternes().forEach(e -> candidatCursusInterneRepository.delete(e));
				candidat.getCandidatCursusInternes().clear();
			}
			List<CandidatCursusInterne> liste = new ArrayList<>();
			listeCursusApogee.forEach(cursus -> {
				Integer anneeObt = null;
				try {
					anneeObt = Integer.valueOf(cursus.getCodAnu());
				} catch (Exception e) {
				}
				SiScolTypResultat result = tableRefController.getTypeResultatByCode(cursus.getCodTre());
				SiScolMention mention = tableRefController.getMentionByCode(cursus.getCodMen());

				CandidatCursusInterne cursusInterne = new CandidatCursusInterne(anneeObt, cursus.getCodVet(), cursus.getLibVet(), result, mention, candidat, cursus.getNotVet());
				if (MethodUtils.validateBean(cursusInterne, logger)) {
					liste.add(candidatCursusInterneRepository.save(cursusInterne));
				}

			});
			return liste;
		} else {
			if (needToDeleteDataApogee) {
				if (candidat.getCandidatCursusInternes() != null && candidat.getCandidatCursusInternes().size() > 0) {
					candidat.getCandidatCursusInternes().forEach(e -> candidatCursusInterneRepository.delete(e));
					candidat.getCandidatCursusInternes().clear();
				}
			}
			return candidat.getCandidatCursusInternes();
		}
	}
}
