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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEquPK;
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
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolOptionBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolSpecialiteBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.siscol.WSBac;
import fr.univlorraine.ecandidat.entities.siscol.WSCursusInterne;
import fr.univlorraine.ecandidat.repositories.CandidatBacOuEquRepository;
import fr.univlorraine.ecandidat.repositories.CandidatCursusInterneRepository;
import fr.univlorraine.ecandidat.repositories.CandidatCursusPostBacRepository;
import fr.univlorraine.ecandidat.repositories.CandidatCursusProRepository;
import fr.univlorraine.ecandidat.repositories.CandidatStageRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
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

/**
 * Gestion du parcours d'un candidat
 * @author Kevin Hergalant
 */
@Component
public class CandidatParcoursController {
	/* Injections */
	private final Logger logger = LoggerFactory.getLogger(CandidatParcoursController.class);
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

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/** Edition du bac */
	public void editBac(final Candidat candidat, final CandidatBacListener listener) {
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		final String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_BAC);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		CandidatBacOuEqu bac = candidat.getCandidatBacOuEqu();
		Boolean edition = true;
		if (bac == null) {
			bac = new CandidatBacOuEqu();
			bac.setTemUpdatableBac(true);
			bac.setId(new CandidatBacOuEquPK(candidat.getIdCandidat(), siScolService.getTypSiscol()));
			bac.setCandidat(candidat);
			edition = false;
		}

		final CandidatBacWindow window = new CandidatBacWindow(bac, edition);
		window.addBacWindowListener(e -> {
			listener.bacModified(e);
		});
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Vérifie le bac si besoin
	 * @param  bac
	 * @return     null si pas d'erreur
	 */
	public String checkBac(final CandidatBacOuEqu bac) {
		return siScolService.checkBacSpecialiteOption(bac);
	}

	/**
	 * Enregistre un bac
	 * @param bac
	 * @param listSpe
	 */
	public CandidatBacOuEqu saveBac(final CandidatBacOuEqu bac) {
		return candidatBacOuEquRepository.save(bac);
	}

	/**
	 * Renvoie les info de bac
	 * @param  candidatBacOuEqu
	 * @return                  les infos du bac
	 */
	public List<SimpleTablePresentation> getInformationsBac(final CandidatBacOuEqu candidatBacOuEqu) {
		/* Infos générales */
		final List<SimpleTablePresentation> liste = new ArrayList<>();
		if (candidatBacOuEqu.getAnneeObtBac() != null) {
			addInfoBac(liste, 1, CandidatBacOuEqu_.anneeObtBac.getName(), Optional.of(String.valueOf(candidatBacOuEqu.getAnneeObtBac())));
		}
		addInfoBac(liste, 2, CandidatBacOuEqu_.siScolBacOuxEqu.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolBacOuxEqu()).map(SiScolBacOuxEqu::getLibBac));
		addInfoBac(liste, 3, CandidatBacOuEqu_.siScolMentionNivBac.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolMentionNivBac()).map(SiScolMentionNivBac::getLibMnb));
		addInfoBac(liste, 4, CandidatBacOuEqu_.siScolPays.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolPays()).map(SiScolPays::getLibPay));

		/* Cas du pays France */
		if (candidatBacOuEqu.getSiScolPays() != null && candidatBacOuEqu.getSiScolPays().equals(cacheController.getPaysFrance())) {
			addInfoBac(liste, 5, CandidatBacOuEqu_.siScolDepartement.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolDepartement()).map(SiScolDepartement::getLibDep));
			addInfoBac(liste, 6, CandidatBacOuEqu_.siScolCommune.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolCommune()).map(SiScolCommune::getLibCom));
			addInfoBac(liste, 7, CandidatBacOuEqu_.siScolEtablissement.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolEtablissement()).map(SiScolEtablissement::getLibEtb));
		}

		/* Specialité/Options */
		addInfoBac(liste, 8, CandidatBacOuEqu_.siScolSpe1BacTer.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolSpe1BacTer()).map(SiScolSpecialiteBac::getLibSpeBac));
		addInfoBac(liste, 9, CandidatBacOuEqu_.siScolSpe2BacTer.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolSpe2BacTer()).map(SiScolSpecialiteBac::getLibSpeBac));
		addInfoBac(liste, 10, CandidatBacOuEqu_.siScolSpeBacPre.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolSpeBacPre()).map(SiScolSpecialiteBac::getLibSpeBac));
		addInfoBac(liste, 11, CandidatBacOuEqu_.siScolOpt1Bac.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolOpt1Bac()).map(SiScolOptionBac::getLibOptBac));
		addInfoBac(liste, 12, CandidatBacOuEqu_.siScolOpt2Bac.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolOpt2Bac()).map(SiScolOptionBac::getLibOptBac));
		addInfoBac(liste, 13, CandidatBacOuEqu_.siScolOpt3Bac.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolOpt3Bac()).map(SiScolOptionBac::getLibOptBac));
		addInfoBac(liste, 14, CandidatBacOuEqu_.siScolOpt4Bac.getName(), Optional.ofNullable(candidatBacOuEqu.getSiScolOpt4Bac()).map(SiScolOptionBac::getLibOptBac));
		return liste;
	}

	/**
	 * Ajoute une info de bac
	 * @param liste
	 * @param index
	 * @param property
	 * @param value
	 */
	public void addInfoBac(final List<SimpleTablePresentation> liste, final int index, final String property, final Optional<String> value) {
		if (value.isPresent()) {
			liste.add(new SimpleTablePresentation(index, property, applicationContext.getMessage("infobac." + property, null, UI.getCurrent().getLocale()), value.orElse(null)));
		}
	}

	/** Edition d'un cursus */
	public void editCursusPostBac(final Candidat candidat, CandidatCursusPostBac cursus, final CandidatCursusExterneListener listener) {
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		final String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_CURSUS_EXTERNE);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		Boolean nouveau = false;
		if (cursus == null) {
			cursus = new CandidatCursusPostBac();
			cursus.setTypSiScol(siScolService.getTypSiscol());
			cursus.setCandidat(candidat);
			nouveau = true;
		}

		final CandidatCursusExterneWindow window = new CandidatCursusExterneWindow(cursus, nouveau);
		window.addCursusPostBacWindowListener(e -> {
			candidat.addCursusPostBac(e);
			listener.cursusModified(candidat.getCandidatCursusPostBacs());
		});
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un cursus
	 * @param  cursus
	 * @return        le cursus post bac
	 */
	public CandidatCursusPostBac saveCursusPostBac(final CandidatCursusPostBac cursus) {
		return candidatCursusPostBacRepository.save(cursus);
	}

	/**
	 * Supprime un cursus
	 * @param candidat
	 * @param cursus
	 * @param listener
	 */
	public void deleteCursusPostBac(final Candidat candidat, final CandidatCursusPostBac cursus, final CandidatCursusExterneListener listener) {
		Assert.notNull(cursus, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		final String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_CURSUS_EXTERNE);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("cursusexterne.confirmDelete", null, UI.getCurrent().getLocale()),
			applicationContext.getMessage("cursusexterne.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			candidatCursusPostBacRepository.delete(cursus);
			candidat.getCandidatCursusPostBacs().remove(cursus);
			listener.cursusModified(candidat.getCandidatCursusPostBacs());
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Enregistre un cursus pro
	 * @param  cursus
	 * @return        le cursus pro
	 */
	public CandidatCursusPro saveCursusPro(final CandidatCursusPro cursus) {
		return candidatCursusProRepository.save(cursus);
	}

	/** Edition d'une formation pro */
	public void editFormationPro(final Candidat candidat, CandidatCursusPro cursus, final CandidatFormationProListener listener) {
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		final String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_FORMATION_PRO);

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

		final CandidatCursusProWindow window = new CandidatCursusProWindow(cursus, nouveau);
		window.addCursusProWindowListener(e -> {
			candidat.addCursusPro(e);
			listener.formationProModified(candidat.getCandidatCursusPros());
		});
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Supprime un cursus pro
	 * @param candidat
	 * @param cursus
	 * @param listener
	 */
	public void deleteFormationPro(final Candidat candidat, final CandidatCursusPro cursus, final CandidatFormationProListener listener) {
		Assert.notNull(cursus, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		final String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_FORMATION_PRO);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("formationPro.confirmDelete", null, UI.getCurrent().getLocale()),
			applicationContext.getMessage("formationPro.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			candidatCursusProRepository.delete(cursus);
			candidat.getCandidatCursusPros().remove(cursus);
			listener.formationProModified(candidat.getCandidatCursusPros());
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Enregistre un stage
	 * @param  stage
	 * @return       le stage
	 */
	public CandidatStage saveStage(final CandidatStage stage) {
		return candidatStageRepository.save(stage);
	}

	/** Edition d'un stage */
	public void editStage(final Candidat candidat, CandidatStage stage, final CandidatStageListener listener) {
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		final String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_STAGE);

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

		final CandidatStageWindow window = new CandidatStageWindow(stage, nouveau);
		window.addCursusProWindowListener(e -> {
			candidat.addStage(e);
			listener.stageModified(candidat.getCandidatStage());
		});
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Supprime un stage
	 * @param candidat
	 * @param stage
	 * @param listener
	 */
	public void deleteStage(final Candidat candidat, final CandidatStage stage, final CandidatStageListener listener) {
		Assert.notNull(stage, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		final String lockError = candidatController.getLockError(candidat.getCompteMinima(), ConstanteUtils.LOCK_STAGE);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("stage.confirmDelete", null, UI.getCurrent().getLocale()),
			applicationContext.getMessage("stage.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			candidatStageRepository.delete(stage);
			candidat.getCandidatStage().remove(stage);
			listener.stageModified(candidat.getCandidatStage());
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Renvoie un bac grace aux données apogee
	 * @param  bacSiScol
	 * @param  candidat
	 * @param  needToDeleteDataSiScol
	 * @return                        le bac provenant d'apogee
	 */
	public CandidatBacOuEqu getBacBySiScolData(final WSBac bacSiScol, final Candidat candidat, final Boolean needToDeleteDataSiScol) {
		if (bacSiScol != null) {
			if (candidat.getCandidatBacOuEqu() != null) {
				candidatBacOuEquRepository.delete(candidat.getCandidatBacOuEqu());
				candidat.setCandidatBacOuEqu(null);
			}
			SiScolPays pays = null;
			final SiScolDepartement dpt = tableRefController.getDepartementByCode(bacSiScol.getCodDep());
			if (dpt != null) {
				pays = cacheController.getPaysFrance();
			} else {
				pays = tableRefController.getPaysByCode(bacSiScol.getCodPays());
			}
			Integer anneeObt = null;
			try {
				anneeObt = Integer.valueOf(bacSiScol.getDaaObtBacIba());
			} catch (final Exception e) {
			}
			final SiScolCommune commune = tableRefController.getCommuneByCode(bacSiScol.getCodCom());
			final SiScolEtablissement etab = tableRefController.getEtablissementByCode(bacSiScol.getCodEtb());
			final SiScolMentionNivBac mention = tableRefController.getMentionNivBacByCode(bacSiScol.getCodMnb());
			final SiScolBacOuxEqu bacOuEqu = tableRefController.getBacOuEquByCode(bacSiScol.getCodBac());
			if (bacOuEqu == null) {
				return null;
			}

			/* Spécialités */
			final SiScolSpecialiteBac speBacPre = tableRefController.getSpecialiteBacByCode(bacApogee.getCodSpeBacPre());
			final SiScolSpecialiteBac spe1Bac = tableRefController.getSpecialiteBacByCode(bacApogee.getCodSpe1Bac());
			final SiScolSpecialiteBac spe2Bac = tableRefController.getSpecialiteBacByCode(bacApogee.getCodSpe2Bac());

			/* Options */
			final SiScolOptionBac opt1Bac = tableRefController.getOptionBacByCode(bacApogee.getCodOpt1Bac());
			final SiScolOptionBac opt2Bac = tableRefController.getOptionBacByCode(bacApogee.getCodOpt2Bac());
			final SiScolOptionBac opt3Bac = tableRefController.getOptionBacByCode(bacApogee.getCodOpt3Bac());
			final SiScolOptionBac opt4Bac = tableRefController.getOptionBacByCode(bacApogee.getCodOpt4Bac());

			final CandidatBacOuEqu candidatBacOuEqu = new CandidatBacOuEqu(candidat.getIdCandidat(), anneeObt, bacOuEqu, commune, dpt, etab, mention, pays, candidat, false, speBacPre, spe1Bac, spe2Bac, opt1Bac, opt2Bac, opt3Bac, opt4Bac);
			final CandidatBacOuEqu candidatBacOuEqu = new CandidatBacOuEqu(candidat.getIdCandidat(), anneeObt, bacOuEqu, commune, dpt, etab, mention, pays, candidat, false, siScolService.getTypSiscol());
			if (MethodUtils.validateBean(candidatBacOuEqu, logger)) {
				return candidatBacOuEquRepository.save(candidatBacOuEqu);
			}
			return null;
		} else {
			/* if (candidat.getTemUpdatableCandidat() && candidat.getCandidatBacOuEqu()!=null && !candidat.getCandidatBacOuEqu().getTemUpdatableBac()){
			 * candidat.getCandidatBacOuEqu().setTemUpdatableBac(true);
			 * return candidatBacOuEquRepository.save(candidat.getCandidatBacOuEqu());
			 * } */
			if (needToDeleteDataSiScol && candidat.getCandidatBacOuEqu() != null) {
				candidatBacOuEquRepository.delete(candidat.getCandidatBacOuEqu());
				candidat.setCandidatBacOuEqu(null);
			}
			return candidat.getCandidatBacOuEqu();
		}
	}

	/**
	 * Renvoie la liste des cursus interne grace aux données apogee
	 * @param  listeCursusSiScol
	 * @param  candidat
	 * @return                   la liste des cursus interne
	 */
	public List<CandidatCursusInterne> getCursusInterne(final List<WSCursusInterne> listeCursusSiScol, final Candidat candidat, final Boolean needToDeleteDataSiScol) {
		if (listeCursusSiScol != null && listeCursusSiScol.size() > 0) {
			if (candidat.getCandidatCursusInternes() != null && candidat.getCandidatCursusInternes().size() > 0) {
				candidat.getCandidatCursusInternes().forEach(e -> candidatCursusInterneRepository.delete(e));
				candidat.getCandidatCursusInternes().clear();
			}
			final List<CandidatCursusInterne> liste = new ArrayList<>();
			listeCursusSiScol.forEach(cursus -> {
				Integer anneeObt = null;
				try {
					anneeObt = Integer.valueOf(cursus.getCodAnu());
				} catch (final Exception e) {
				}
				final SiScolTypResultat result = tableRefController.getTypeResultatByCode(cursus.getCodTre());
				final SiScolMention mention = tableRefController.getMentionByCode(cursus.getCodMen());

				final CandidatCursusInterne cursusInterne = new CandidatCursusInterne(anneeObt, cursus.getCodVet(), cursus.getLibVet(), result, mention, candidat, cursus.getNotVet(), cursus.getBarNotVet(), siScolService.getTypSiscol());
				if (MethodUtils.validateBean(cursusInterne, logger)) {
					liste.add(candidatCursusInterneRepository.save(cursusInterne));
				}

			});
			return liste;
		} else {
			if (needToDeleteDataSiScol) {
				if (candidat.getCandidatCursusInternes() != null && candidat.getCandidatCursusInternes().size() > 0) {
					candidat.getCandidatCursusInternes().forEach(e -> candidatCursusInterneRepository.delete(e));
					candidat.getCandidatCursusInternes().clear();
				}
			}
			return candidat.getCandidatCursusInternes();
		}
	}

}
