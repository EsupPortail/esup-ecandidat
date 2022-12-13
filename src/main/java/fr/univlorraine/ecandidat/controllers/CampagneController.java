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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.repositories.CampagneRepository;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.CompteMinimaRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.AdminCampagneWindow;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 */
@Component
public class CampagneController {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient BatchController batchController;
	@Resource
	private transient CampagneRepository campagneRepository;
	@Resource
	private transient CompteMinimaRepository compteMinimaRepository;
	@Resource
	private transient CandidatureRepository candidatureRepository;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/** @return liste des campagnes */
	public List<Campagne> getCampagnes() {
		return campagneRepository.findAll();
	}

	/** @return la campagne active */
	public Campagne getCampagneEnServiceToCache() {
		final List<Campagne> liste = campagneRepository.findByTesCampAndTypSiScolAndDatArchivCampIsNull(true, siScolService.getTypSiscol());
		if (liste == null || liste.size() == 0) {
			return null;
		}
		return liste.get(0);
	}

	/** @return la campagne active */
	public Campagne getCampagneActive() {
		final Campagne campagne = cacheController.getCampagneEnService();
		if (campagne == null) {
			return null;
		}

		if (campagne.getDatFinCamp().isBefore(LocalDate.now()) || campagne.getDatDebCamp().isAfter(LocalDate.now())) {
			return null;
		}

		return campagne;
	}

	/** @return true si la campagne est ouverte aux candidats */
	public Boolean isCampagneActiveCandidat(final Campagne campagne) {
		if (campagne == null) {
			return false;
		} else if (campagne.getDatFinCandidatCamp() != null && campagne.getDatFinCandidatCamp().isBefore(LocalDate.now())) {
			return false;
		}
		return true;
	}

	/** @return la campagne active à archiver (ne tient pas compte du typSiscol */
	public Campagne getCampagneEnServiceToArchive() {
		final List<Campagne> liste = campagneRepository.findByTesCampAndDatArchivCampIsNull(true);
		if (liste == null || liste.size() == 0) {
			return null;
		}
		return liste.get(0);
	}

	/** Ouvre une fenêtre d'édition d'un nouveau campagne. */
	public void editNewCampagne() {
		final List<Campagne> listeCampagneToActivate = campagneRepository.findByDatActivatPrevCampIsNotNullAndDatActivatEffecCampIsNull();
		if (listeCampagneToActivate.size() > 0) {
			Notification.show(applicationContext.getMessage("campagne.error.new", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		final Campagne campToArchive = getCampagneEnServiceToArchive();
		/* Verrou */
		if (campToArchive != null && !lockController.getLockOrNotify(campToArchive, null)) {
			return;
		}

		final Campagne nouvelleCampagne = new Campagne();
		nouvelleCampagne.setTypSiScol(siScolService.getTypSiscol());
		nouvelleCampagne.setI18nLibCamp(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_CAMP_LIB)));

		final AdminCampagneWindow window = new AdminCampagneWindow(nouvelleCampagne, campToArchive);
		window.addCloseListener(e -> {
			if (campToArchive != null) {
				lockController.releaseLock(campToArchive);
			}
		});
		UI.getCurrent().addWindow(window);

	}

	/**
	 * Ouvre une fenêtre d'édition de campagne.
	 * @param campagne
	 */
	public void editCampagne(final Campagne campagne) {
		Assert.notNull(campagne, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(campagne, null)) {
			return;
		}
		final AdminCampagneWindow window = new AdminCampagneWindow(campagne, null);
		window.addCloseListener(e -> lockController.releaseLock(campagne));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un campagne
	 * @param campagne
	 * @param campagneAArchiver
	 */
	public void saveCampagne(Campagne campagne, final Campagne campagneAArchiver) {
		Assert.notNull(campagne, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		if (campagne.getIdCamp() == null) {
			if (campagneRepository.findAll().size() == 0) {
				campagne.setTesCamp(true);
				campagne.setDatActivatPrevCamp(LocalDateTime.now());
				campagne.setDatActivatEffecCamp(LocalDateTime.now());
			} else {
				campagne.setTesCamp(false);
			}
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(campagne, null)) {
			return;
		}

		if (campagneAArchiver != null) {
			if (!lockController.getLockOrNotify(campagneAArchiver, null)) {
				return;
			}
			campagne.setCampagneArchiv(campagneAArchiver);
		}
		campagne.setI18nLibCamp(i18nController.saveI18n(campagne.getI18nLibCamp()));
		campagne = campagneRepository.saveAndFlush(campagne);
		cacheController.reloadCampagneEnService(true);
		lockController.releaseLock(campagne);
		if (campagneAArchiver != null) {
			lockController.releaseLock(campagneAArchiver);
		}
	}

	/**
	 * Supprime une campagne
	 * @param campagne
	 */
	public void deleteCampagne(final Campagne campagne) {
		Assert.notNull(campagne, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		if (campagne.getDatActivatEffecCamp() != null) {
			Notification.show(applicationContext.getMessage("campagne.error.delete.active", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		if (compteMinimaRepository.countByCampagne(campagne) > 0) {
			Notification.show(applicationContext.getMessage("campagne.error.delete", new Object[] { CompteMinima.class.getSimpleName() }, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(campagne, null)) {
			return;
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("campagne.window.confirmDelete", new Object[] { campagne.getCodCamp() }, UI.getCurrent().getLocale()),
			applicationContext.getMessage("campagne.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(campagne, null)) {
				campagneRepository.delete(campagne);
				cacheController.reloadCampagneEnService(true);
				/* Suppression du lock */
				lockController.releaseLock(campagne);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(campagne);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Archive une campagne et active l'autre
	 * @param batchHisto
	 */
	public void archiveCampagne(final BatchHisto batchHisto) {
		batchController.addDescription(batchHisto, "Lancement du batch d'archivage de campagne");
		final List<Campagne> listeCampagne = campagneRepository.findByDatActivatEffecCampIsNullAndDatActivatPrevCampIsNotNull();
		listeCampagne.forEach(campagne -> {
			if (campagne.getDatActivatPrevCamp().isBefore(LocalDateTime.now())) {
				batchController.addDescription(batchHisto, "Activation campagne : " + campagne);
				batchController.addDescription(batchHisto, "Archivage des candidatures pour la campagne : " + campagne.getCampagneArchiv());
				// On place les dates des formations dans les candidatures
				// candidatureController.archiveCandidatureDateFormation(campagne.getCampagneArchiv());
				final List<Candidature> liste = candidatureRepository.findByCandidatCompteMinimaCampagne(campagne.getCampagneArchiv());
				Integer i = 0;
				Integer cpt = 0;
				for (final Candidature candidature : liste) {
					candidature.setDatAnalyseForm(candidature.getFormation().getDatAnalyseForm());
					candidature.setDatConfirmForm(candidature.getFormation().getDatConfirmForm());
					candidature.setDelaiConfirmForm(candidature.getFormation().getDelaiConfirmForm());
					candidature.setDatDebDepotForm(candidature.getFormation().getDatDebDepotForm());
					candidature.setDatFinDepotForm(candidature.getFormation().getDatFinDepotForm());
					candidature.setDatRetourForm(candidature.getFormation().getDatRetourForm());
					candidature.setDatPubliForm(candidature.getFormation().getDatPubliForm());
					candidature.setDatJuryForm(candidature.getFormation().getDatJuryForm());
					candidatureRepository.save(candidature);
					i++;
					cpt++;
					if (i.equals(1000)) {
						batchController.addDescription(batchHisto, "Archivage des candidatures : mise à jour des dates de formation pour " + cpt + " candidatures ok");
						i = 0;
					}
				}

				campagne.setDatActivatEffecCamp(LocalDateTime.now());
				campagne.setTesCamp(true);
				campagne = campagneRepository.save(campagne);
				campagne.getCampagneArchiv().setDatArchivCamp(LocalDateTime.now());
				campagne.getCampagneArchiv().setTesCamp(false);
				campagneRepository.save(campagne.getCampagneArchiv());

				cacheController.reloadCampagneEnService(true);
				batchController.addDescription(batchHisto, "Activation campagne terminé : " + campagne);
			}
		});
		batchController.addDescription(batchHisto, "Fin batch d'archivage de campagne");
	}

	/**
	 * Enregistre la date de destruction de la campagne
	 * @param  campagne
	 * @return          la campagne enregistrée
	 */
	public Campagne saveDateDestructionCampagne(final Campagne campagne) {
		campagne.setDatDestructEffecCamp(LocalDateTime.now());
		campagne.setCompteMinimas(new ArrayList<CompteMinima>());
		return campagneRepository.save(campagne);
	}

	/**
	 * @param  camp
	 * @return      la date prévisionnelle de destruction de dossier
	 */
	public LocalDateTime getDateDestructionDossier(final Campagne camp) {
		if (camp.getDatArchivCamp() != null) {
			return camp.getDatArchivCamp().plusDays(parametreController.getNbJourArchivage());
		}
		return null;
	}

	/**
	 * Verifie que le code de la campagne est unique
	 * @param  cod
	 * @param  idCamp
	 * @return        true si le code est unique
	 */
	public Boolean isCodCampUnique(final String cod, final Integer idCamp) {
		final Campagne camp = campagneRepository.findByCodCampAndTypSiScol(cod, siScolService.getTypSiscol());
		if (camp == null) {
			return true;
		} else {
			if (camp.getIdCamp().equals(idCamp)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param  campagne
	 * @return          le libellé de la campagne
	 */
	public String getLibelleCampagne(final Campagne campagne) {
		return getLibelleCampagne(campagne, null);
	}

	/**
	 * @param  campagne
	 * @param  codLangue
	 * @return           le libellé de la campagne
	 */
	public String getLibelleCampagne(final Campagne campagne, final String codLangue) {
		if (campagne.getI18nLibCamp() != null) {
			if (codLangue != null) {
				return i18nController.getI18nTraduction(campagne.getI18nLibCamp(), codLangue);
			} else {
				return i18nController.getI18nTraduction(campagne.getI18nLibCamp());
			}
		} else {
			return campagne.getLibCamp();
		}
	}
}
