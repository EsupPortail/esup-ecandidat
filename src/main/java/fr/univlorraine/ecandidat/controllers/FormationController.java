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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.I18nTraduction;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.siscol.Vet;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandFormationDatesWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandFormationWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandPieceComplementaireWindow;

/** Gestion de l'entité formation
 *
 * @author Kevin Hergalant */
@Component
public class FormationController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient CandidatureRepository candidatureRepository;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient DateTimeFormatter formatterDate;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;
	@Resource
	private transient OffreFormationController offreFormationController;

	/** @return liste des formations */
	public List<Formation> getFormations() {
		return formationRepository.findAll();
	}

	/** @param securityCtrCand
	 * @return liste des formations d'un centre de candidature */
	public List<Formation> getFormationsByCtrCand(final SecurityCtrCandFonc securityCtrCand) {
		List<Formation> listeFormation = formationRepository.findByCommissionCentreCandidatureIdCtrCand(securityCtrCand.getCtrCand().getIdCtrCand());
		Campagne campagne = campagneController.getCampagneActive();
		if (securityCtrCand.getIsGestAllCommission()) {
			return listeFormation.stream().map(e -> alimenteFormationData(campagne, e)).collect(Collectors.toList());
		} else {
			return listeFormation.stream().map(e -> alimenteFormationData(campagne, e)).filter(formation -> hasRighToSeeFormation(formation, securityCtrCand)).collect(Collectors.toList());
		}
	}

	/** @param f
	 * @return la formation alimentée en data supplémentaires */
	public Formation alimenteFormationData(final Formation f) {
		return alimenteFormationData(campagneController.getCampagneActive(), f);
	}

	/** @param campagne
	 * @param f
	 * @return la formation alimentée en data supplémentaires */
	public Formation alimenteFormationData(final Campagne campagne, final Formation f) {
		String code = null;

		if (!f.getTesForm()) {
			code = ConstanteUtils.FLAG_RED;
		} else {
			if (campagne == null) {
				code = ConstanteUtils.FLAG_BLUE;
			} else {
				LocalDate dateDeb = f.getDatDebDepotForm();
				LocalDate dateFin = f.getDatDebDepotForm();
				LocalDate dateRetour = f.getDatRetourForm();
				LocalDate dateConfirm = f.getDatConfirmForm();
				LocalDate datePubli = f.getDatPubliForm();
				LocalDate dateJury = f.getDatJuryForm();
				LocalDate dateAnalyse = f.getDatAnalyseForm();
				LocalDate datePreselect = f.getPreselectDateForm();
				if (!MethodUtils.isDateIncludeInInterval(dateDeb, campagne.getDatDebCamp(), campagne.getDatFinCamp())
						|| !MethodUtils.isDateIncludeInInterval(dateFin, campagne.getDatDebCamp(), campagne.getDatFinCamp())
						|| !MethodUtils.isDateIncludeInInterval(dateRetour, campagne.getDatDebCamp(), campagne.getDatFinCamp())
						|| !MethodUtils.isDateIncludeInInterval(dateConfirm, campagne.getDatDebCamp(), campagne.getDatFinCamp())
						|| !MethodUtils.isDateIncludeInInterval(datePubli, campagne.getDatDebCamp(), campagne.getDatFinCamp())
						|| !MethodUtils.isDateIncludeInInterval(dateJury, campagne.getDatDebCamp(), campagne.getDatFinCamp())
						|| !MethodUtils.isDateIncludeInInterval(dateAnalyse, campagne.getDatDebCamp(), campagne.getDatFinCamp())
						|| !MethodUtils.isDateIncludeInInterval(datePreselect, campagne.getDatDebCamp(), campagne.getDatFinCamp())) {
					code = ConstanteUtils.FLAG_YELLOW;
				} else {
					code = ConstanteUtils.FLAG_GREEEN;
				}
			}
		}
		f.setFlagEtat(code);
		f.setDateVoeux(applicationContext.getMessage("formation.table.dateVoeux.label", new Object[] {
				formatterDate.format(f.getDatDebDepotForm()), formatterDate.format(f.getDatFinDepotForm())}, UI.getCurrent().getLocale()));
		return f;
	}

	/** Ouvre une fenêtre d'édition d'un nouveau formation. */
	public void editNewFormation(final SecurityCtrCandFonc securityCtrCand) {
		CentreCandidature ctrCand = securityCtrCand.getCtrCand();
		Formation form = new Formation(userController.getCurrentUserLogin());

		I18n i18n = new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_FORM_INFO_COMP));
		if (ctrCand.getInfoCompCtrCand() != null) {
			I18nTraduction trad = new I18nTraduction(ctrCand.getInfoCompCtrCand(), i18n, cacheController.getLangueDefault());
			List<I18nTraduction> i18nTraductions = new ArrayList<>();
			i18nTraductions.add(trad);
			i18n.setI18nTraductions(i18nTraductions);
		}
		form.setI18nInfoCompForm(i18n);

		form.setTemDematForm(ctrCand.getTemDematCtrCand());
		form.setDatConfirmForm(ctrCand.getDatConfirmCtrCand());
		form.setDatDebDepotForm(ctrCand.getDatDebDepotCtrCand());
		form.setDatAnalyseForm(ctrCand.getDatAnalyseCtrCand());
		form.setDatFinDepotForm(ctrCand.getDatFinDepotCtrCand());
		form.setDatJuryForm(ctrCand.getDatJuryCtrCand());
		form.setDatPubliForm(ctrCand.getDatPubliCtrCand());
		form.setDatRetourForm(ctrCand.getDatRetourCtrCand());
		form.setTemListCompForm(ctrCand.getTemListCompCtrCand());
		form.setTypeDecisionFav(ctrCand.getTypeDecisionFav());

		UI.getCurrent().addWindow(new CtrCandFormationWindow(form, securityCtrCand));
	}

	/** Ouvre une fenêtre d'édition de formation.
	 *
	 * @param formation
	 * @param securityCtrCand
	 */
	public void editFormation(final Formation formation, final SecurityCtrCandFonc securityCtrCand) {
		Assert.notNull(formation, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(formation, null)) {
			return;
		}
		if (formation.getI18nInfoCompForm() == null) {
			formation.setI18nInfoCompForm(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_FORM_INFO_COMP)));
		}
		CtrCandFormationWindow window = new CtrCandFormationWindow(formation, securityCtrCand);
		window.addCloseListener(e -> lockController.releaseLock(formation));
		UI.getCurrent().addWindow(window);
	}

	/** Edite les pieces complémentaires d'une formation
	 *
	 * @param formations
	 * @param ctrCand
	 */
	public void editPieceCompFormation(final List<Formation> formations, final CentreCandidature ctrCand) {
		/* Verrou */
		if (!checkLockFormations(formations)) {
			unlockFormations(formations);
			return;
		}

		List<PieceJustif> pieceJustifs = new ArrayList<>();
		List<Formulaire> formulaires = new ArrayList<>();
		if (formations.size() == 1) {
			Formation form = formations.get(0);
			pieceJustifs = form.getPieceJustifs();
			formulaires = form.getFormulaires();
		} else {
			/* On calcule les PJ et formulaires communs */
			Formation form = formations.get(0);
			pieceJustifs = form.getPieceJustifs();
			formulaires = form.getFormulaires();

			for (Formation formation : formations) {
				if (pieceJustifs.size() != 0) {
					pieceJustifs.retainAll(formation.getPieceJustifs());
				}
				if (formulaires.size() != 0) {
					formulaires.retainAll(formation.getFormulaires());
				}
				if (pieceJustifs.size() == 0 && formulaires.size() == 0) {
					break;
				}
			}
		}

		CtrCandPieceComplementaireWindow window = new CtrCandPieceComplementaireWindow(formations, ctrCand, pieceJustifs, formulaires);
		window.addCloseListener(e -> unlockFormations(formations));
		UI.getCurrent().addWindow(window);
	}

	/** Enregistre les PJ et formulaires d'une ou plusieurs formations
	 *
	 * @param formations
	 * @param listPj
	 * @param listFormulaire
	 */
	public void savePiecesComplementaires(final List<Formation> formations, final List<PieceJustif> listPj,
			final List<Formulaire> listFormulaire) {
		formations.forEach(form -> {
			/* Verrou */
			if (!lockController.getLockOrNotify(form, null)) {
				return;
			}
			form.setFormulaires(listFormulaire);
			form.setPieceJustifs(listPj);
			form.setUserModForm(userController.getCurrentUserLogin());
			formationRepository.saveAndFlush(form);
		});
	}

	/** Edition des dates en masse
	 *
	 * @param formations
	 * @param ctrCand
	 */
	public void editDates(final List<Formation> formations, final CentreCandidature ctrCand) {
		if (!checkLockFormations(formations)) {
			unlockFormations(formations);
			return;
		}
		Formation form = new Formation();
		if (formations.size() == 1) {
			Formation oneForm = formations.get(0);
			form.setTesForm(oneForm.getTesForm());
			form.setDatConfirmForm(oneForm.getDatConfirmForm());
			form.setDatDebDepotForm(oneForm.getDatDebDepotForm());
			form.setDatAnalyseForm(oneForm.getDatAnalyseForm());
			form.setDatFinDepotForm(oneForm.getDatFinDepotForm());
			form.setDatJuryForm(oneForm.getDatJuryForm());
			form.setDatPubliForm(oneForm.getDatPubliForm());
			form.setDatRetourForm(oneForm.getDatRetourForm());
		} else {
			form.setTesForm(true);
			form.setDatConfirmForm(ctrCand.getDatConfirmCtrCand());
			form.setDatDebDepotForm(ctrCand.getDatDebDepotCtrCand());
			form.setDatAnalyseForm(ctrCand.getDatAnalyseCtrCand());
			form.setDatFinDepotForm(ctrCand.getDatFinDepotCtrCand());
			form.setDatJuryForm(ctrCand.getDatJuryCtrCand());
			form.setDatPubliForm(ctrCand.getDatPubliCtrCand());
			form.setDatRetourForm(ctrCand.getDatRetourCtrCand());
		}
		CtrCandFormationDatesWindow window = new CtrCandFormationDatesWindow(form, formations);
		window.addCloseListener(e -> unlockFormations(formations));
		UI.getCurrent().addWindow(window);
	}

	/** Enregistre les dates des formations en masse
	 *
	 * @param formDate
	 * @param formations
	 */
	public void saveDatesFormation(final Formation formDate, final List<Formation> formations) {
		formations.forEach(form -> {
			form.setTesForm(formDate.getTesForm());
			form.setDatConfirmForm(formDate.getDatConfirmForm());
			form.setDatDebDepotForm(formDate.getDatDebDepotForm());
			form.setDatAnalyseForm(formDate.getDatAnalyseForm());
			form.setDatFinDepotForm(formDate.getDatFinDepotForm());
			form.setDatJuryForm(formDate.getDatJuryForm());
			form.setDatPubliForm(formDate.getDatPubliForm());
			form.setDatRetourForm(formDate.getDatRetourForm());
			saveFormation(form);
		});
	}

	/** @param formations
	 * @return true si une formation est lockée */
	private Boolean checkLockFormations(final List<Formation> formations) {
		for (Formation f : formations) {
			if (!lockController.getLockOrNotify(f, null)) {
				return false;
			}
		}
		return true;
	}

	/** Unlock la liste de formations
	 *
	 * @param formations
	 */
	private void unlockFormations(final List<Formation> formations) {
		for (Formation f : formations) {
			lockController.releaseLock(f);
		}
	}

	/** Enregistre un formation
	 *
	 * @param formation
	 */
	public void saveFormation(Formation formation) {
		Assert.notNull(formation, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (formation.getIdForm() != null && !lockController.getLockOrNotify(formation, null)) {
			return;
		}
		formation.setUserModForm(userController.getCurrentUserLogin());
		/* Pour les i18n nullable, attention a les supprimer si besoin */
		Integer idI18n = i18nController.getIdI18nNullable(formation.getI18nInfoCompForm());
		formation.setI18nInfoCompForm(i18nController.saveI18nNullable(formation.getI18nInfoCompForm()));
		formation = formationRepository.saveAndFlush(formation);
		if (formation.getI18nInfoCompForm() == null && idI18n != null) {
			i18nController.deleteI18nNullable(idI18n);
		}

		/* Si tes à non : Suppression dans l'offre */
		if (!formation.getTesForm()) {
			offreFormationController.removeFormation(formation);
		} else {
			offreFormationController.addFormation(formation);
		}
	}

	/** Supprime une formation
	 *
	 * @param formation
	 */
	public void deleteFormation(final Formation formation) {
		Assert.notNull(formation, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		if (candidatureRepository.countByFormation(formation) > 0) {
			Notification.show(applicationContext.getMessage("formation.error.delete", new Object[] {Candidature.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(formation, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("formation.window.confirmDelete", new Object[] {
				formation.getCodForm()}, UI.getCurrent().getLocale()), applicationContext.getMessage("formation.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(formation, null)) {
				formationRepository.delete(formation);
				offreFormationController.removeFormation(formation);
				/* Suppression du lock */
				lockController.releaseLock(formation);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(formation);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Verifie l'unicité du code
	 *
	 * @param cod
	 * @param id
	 * @return true si le code est unique */
	public Boolean isCodFormUnique(final String cod, final Integer id) {
		Formation form = formationRepository.findByCodForm(cod);
		if (form == null) {
			return true;
		} else {
			if (form.getIdForm().equals(id)) {
				return true;
			}
		}
		return false;
	}

	/** @param search
	 * @return la liste des VET d'un CGE et d'une recherche
	 * @throws SiScolException
	 */
	public List<Vet> getVetByCGE(final String search) throws SiScolException {
		if (siScolService.isImplementationApogee()) {
			SecurityCentreCandidature ctrCand = userController.getCentreCandidature();
			if (ctrCand != null) {
				if (ctrCand.getIsAdmin()) {
					return siScolService.getListFormation(null, search);
				} else {
					if (ctrCand.getCodCGE() != null) {
						return siScolService.getListFormation(ctrCand.getCodCGE(), search);
					}
				}
			}
		}
		return new ArrayList<>();
	}

	/** @param formation
	 * @return true si l'utilisateur a le droit de voir la formation */
	public Boolean hasRighToSeeFormation(final Formation formation, final SecurityCtrCandFonc securityCtrCand) {
		if (securityCtrCand == null || securityCtrCand.getCtrCand() == null) {
			return false;
		} else if (securityCtrCand.getCtrCand().equals(formation.getCommission().getCentreCandidature())) {
			if (securityCtrCand.getIsGestAllCommission() || MethodUtils.isIdInListId(formation.getCommission().getIdComm(), securityCtrCand.getListeIdCommission())) {
				return true;
			}
		}
		return false;
	}

}
