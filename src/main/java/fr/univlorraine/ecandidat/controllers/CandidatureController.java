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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.converter.XDocConverterException;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import fr.opensagres.xdocreport.template.formatter.NullImageBehaviour;
import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.PjOpi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.repositories.OpiRepository;
import fr.univlorraine.ecandidat.repositories.PjOpiRepository;
import fr.univlorraine.ecandidat.services.file.SignaturePdfManager;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCommission;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureCandidatViewListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.OdfListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierAvis;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierBac;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierCandidat;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierCandidature;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierCursusExterne;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierCursusInterne;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierCursusPro;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierDate;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierMotivationAvis;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierPj;
import fr.univlorraine.ecandidat.utils.bean.export.ExportDossierStage;
import fr.univlorraine.ecandidat.utils.bean.export.ExportLettreCandidat;
import fr.univlorraine.ecandidat.utils.bean.mail.CandidatMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.ChangeCodOpiMailBean;
import fr.univlorraine.ecandidat.utils.bean.presentation.FormulairePresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.views.CandidatCandidaturesView;
import fr.univlorraine.ecandidat.views.OffreFormationView;
import fr.univlorraine.ecandidat.views.windows.CandidatureWindow;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandOdfCandidatureWindow;

/** Gestion des Candidatures
 *
 * @author Kevin Hergalant */
@Component
public class CandidatureController {

	private Logger logger = LoggerFactory.getLogger(CandidatureController.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient LockCandidatController lockCandidatController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient AdresseController adresseController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient CandidatureCtrCandController ctrCandCandidatureController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient SignaturePdfManager signaturePdfManager;
	@Resource
	private transient CandidatureGestionController decisionCandidatureController;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient MotivationAvisController motivationAvisController;
	@Resource
	private transient CandidatureGestionController candidatureGestionController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient CandidatureRepository candidatureRepository;
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient PjOpiRepository pjOpiRepository;
	@Resource
	private transient OpiRepository opiRepository;
	@Resource
	private transient DateTimeFormatter formatterDate;
	@Resource
	private transient DateTimeFormatter formatterDateTime;

	/** Edition d'une nouvelle candidature */
	public void editNewCandidature() {
		MainUI.getCurrent().navigateToView(OffreFormationView.NAME);
	}

	/** @param idCandidature
	 * @return la candidature chargée */
	public Candidature loadCandidature(final Integer idCandidature) {
		return candidatureRepository.findOne(idCandidature);
	}

	/** Candidate à une formation
	 *
	 * @param idForm
	 * @param listener
	 */
	public void candidatToFormation(final Integer idForm, final OdfListener listener, final Boolean isTest) {
		/* On recupere l'authentification */
		Authentication auth = userController.getCurrentAuthentication();
		if (userController.isAnonymous(auth)) {
			return;
		}
		if (!userController.isCandidat(auth) && userController.getNoDossierCandidat(auth) == null) {
			Notification.show(applicationContext.getMessage("odf.choose.candidat", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		/* Vérification du compte à minima */
		CompteMinima cptMin = candidatController.getCompteMinima();
		if (cptMin == null) {
			Notification.show(applicationContext.getMessage("cptmin.load.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		Candidat candidat = cptMin.getCandidat();
		/* Vérification du candidat-->info perso */
		if (candidat == null) {
			Notification.show(applicationContext.getMessage("candidat.load.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		/* Vérification du candidat-->adresse */
		if (candidat.getAdresse() == null) {
			Notification.show(applicationContext.getMessage("candidat.load.adresse.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		/* Vérification du candidat-->bac */
		CandidatBacOuEqu bacOuEqu = candidat.getCandidatBacOuEqu();
		if (bacOuEqu == null) {
			Notification.show(applicationContext.getMessage("candidat.load.bac.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		/* Vérification année du bac et INE obligatoire */
		SiScolBacOuxEqu siScolBacOuxEqu = bacOuEqu.getSiScolBacOuxEqu();
		if (bacOuEqu.getAnneeObtBac() != null && siScolBacOuxEqu.getTemCtrlIneBac()
				&& siScolBacOuxEqu.getAnnCtrlIneBac() != null
				&& bacOuEqu.getAnneeObtBac() > Integer.valueOf(siScolBacOuxEqu.getAnnCtrlIneBac())
				&& candidatController.getINEObligatoire(candidat.getSiScolPaysNat())
				&& (candidat.getIneCandidat() == null || candidat.getCleIneCandidat() == null
						|| candidat.getIneCandidat().equals("") || candidat.getCleIneCandidat().equals(""))) {
			Notification.show(applicationContext.getMessage("candidat.load.bac.ine.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		Formation formation = formationRepository.findOne(idForm);
		if (formation == null || !formation.getTesForm()) {
			Notification.show(applicationContext.getMessage("candidature.formation.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			if (listener != null) {
				listener.updateOdf();
			}
			return;
		}
		if (!isTest) {
			if (candidat.getCandidatures().stream().filter(candidature -> candidature.getDatAnnulCand() == null
					&& candidature.getFormation().getIdForm().equals(idForm)).findAny().isPresent()) {
				Notification.show(applicationContext.getMessage("candidature.formation.allready", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}
		}

		/* On défini les variables */
		String user = userController.getCurrentNoDossierCptMinOrLogin(auth);
		TypeTraitement typTraitForm = formation.getTypeTraitement();
		if (typTraitForm.equals(tableRefController.getTypeTraitementAccesDirect())) {
			typTraitForm = tableRefController.getTypeTraitementEnAttente();
		}

		if (userController.isGestionnaireCandidat(auth)) {
			candidatToFormationGestionnaire(candidat, formation, user, typTraitForm);
		} else {
			/* Verif que les dates sont bien dans l'interval */
			if (!MethodUtils.isDateIncludeInInterval(LocalDate.now(), formation.getDatDebDepotForm(), formation.getDatFinDepotForm())) {
				Notification.show(applicationContext.getMessage("candidature.date.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}
			/*
			 * Verif que le nb de candidatures du candidat sur ce centre ne depassent pas le
			 * nb parametre
			 */
			CentreCandidature ctrCand = formation.getCommission().getCentreCandidature();

			Integer nbMax;
			Long nbCand;
			String message;
			if (parametreController.getNbVoeuxMaxIsEtab()) {
				nbMax = parametreController.getNbVoeuxMax();
				nbCand = candidatureRepository.getNbCandByEtab(candidat.getIdCandidat());
				message = applicationContext.getMessage("candidature.etab.error", null, UI.getCurrent().getLocale());
			} else {
				nbMax = ctrCand.getNbMaxVoeuxCtrCand();
				nbCand = candidatureRepository.getNbCandByCtrCand(ctrCand.getIdCtrCand(), candidat.getIdCandidat());
				message = applicationContext.getMessage("candidature.ctrCand.error", null, UI.getCurrent().getLocale());
			}

			if (nbCand >= nbMax) {
				Notification.show(message, Type.WARNING_MESSAGE);
				return;
			}
			candidatToFormationCandidat(candidat, formation, user, typTraitForm, isTest);
		}
	}

	/** La candiature est faite par un candidat
	 *
	 * @param candidat
	 * @param formation
	 * @param user
	 * @param typTraitForm
	 * @param isTest
	 */
	private void candidatToFormationCandidat(final Candidat candidat, final Formation formation, final String user,
			final TypeTraitement typTraitForm, final Boolean isTest) {
		if (isTest) {
			saveCandidature(new Candidature(user, candidat, formation, typTraitForm, tableRefController.getTypeStatutEnAttente(), false, false), false);
		} else {
			ConfirmWindow win = new ConfirmWindow(applicationContext.getMessage("candidature.confirm", new Object[] {formation.getLibForm()}, UI.getCurrent().getLocale()));
			win.addBtnOuiListener(e -> {
				Candidature candidature = saveCandidature(new Candidature(user, candidat, formation, typTraitForm, tableRefController.getTypeStatutEnAttente(), false, false), false);
				if (candidature != null) {
					MainUI.getCurrent().navigateToView(CandidatCandidaturesView.NAME + "/" + candidature.getIdCand());
				}
			});
			UI.getCurrent().addWindow(win);
		}
	}

	/*
	 * public void testCandidature(Candidat candidat, Integer idFormation){
	 * Candidature candidature = new Candidature("Test",candidat,
	 * formationRepository.findOne(idFormation),tableRefController.
	 * getTypeTraitementAccesControle(),
	 * tableRefController.getTypeStatutEnAttente(), false, false);
	 * candidature.setCandidat(candidat); MethodUtils.validateBean(candidature,
	 * logger); candidature = candidatureRepository.save(candidature); TypeDecision
	 * ty = typeDecisionRepository.findOne(1); TypeDecisionCandidature
	 * typeDecisionCandidature = new TypeDecisionCandidature(candidature, ty);
	 * typeDecisionCandidature.setTemValidTypeDecCand(true);
	 * typeDecisionCandidature.setUserCreTypeDecCand("Test");
	 * typeDecisionCandidature.setTemAppelTypeDecCand(false);
	 * MethodUtils.validateBean(typeDecisionCandidature,
	 * LoggerFactory.getLogger(this.getClass()));
	 * typeDecisionCandidatureRepository.save(typeDecisionCandidature); }
	 */

	/** La candidature est faite par un gestionnaire
	 *
	 * @param candidat
	 * @param formation
	 * @param user
	 * @param typTraitForm
	 */
	private void candidatToFormationGestionnaire(final Candidat candidat, final Formation formation, final String user,
			final TypeTraitement typTraitForm) {
		String msgWin = applicationContext.getMessage("candidature.gest.window.msg", new Object[] {
				candidat.getNomPatCandidat() + " " + candidat.getPrenomCandidat(), formation.getLibForm()}, UI.getCurrent().getLocale());
		CtrCandOdfCandidatureWindow window = new CtrCandOdfCandidatureWindow(msgWin);

		window.addOdfCandidatureListener(typeCandidature -> {
			if (typeCandidature.equals(ConstanteUtils.OPTION_CLASSIQUE)) {
				Candidature candidature = saveCandidature(new Candidature(user, candidat, formation, typTraitForm, tableRefController.getTypeStatutEnAttente(), false, false), false);
				if (candidature == null) {
					return;
				}
			} else if (typeCandidature.equals(ConstanteUtils.OPTION_PROP)) {
				Candidature candidature = new Candidature(user, candidat, formation, typTraitForm, tableRefController.getTypeStatutComplet(), true, true);
				candidature = saveCandidature(candidature, true);
				if (candidature != null) {
					ctrCandCandidatureController.saveTypeDecisionCandidature(candidature, formation.getTypeDecisionFav(), false, user);
				} else {
					return;
				}
			} else {
				return;
			}
			MainUI.getCurrent().navigateToView(CandidatCandidaturesView.NAME);
		});
		UI.getCurrent().addWindow(window);
	}

	/** Enregistre une candidature
	 *
	 * @param candidature
	 * @param isProposition
	 * @return la candidature */
	private Candidature saveCandidature(Candidature candidature, final Boolean isProposition) {
		/* On vérifie */
		List<Candidature> candidatureCheckAllreadyExist = candidatureRepository.findByFormationIdFormAndCandidatIdCandidatAndDatAnnulCandIsNull(candidature.getFormation().getIdForm(), candidature.getCandidat().getIdCandidat());
		if (candidatureCheckAllreadyExist.size() > 0) {
			Notification.show(applicationContext.getMessage("unexpected.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return null;
		}

		candidature = candidatureRepository.save(candidature);

		if (isProposition) {
			/* envoi du mail à la commission */
			if (candidature.getFormation().getCommission().getTemAlertPropComm()) {
				mailController.sendMailByCod(candidature.getFormation().getCommission().getMailComm(), NomenclatureUtils.MAIL_COMMISSION_ALERT_PROPOSITION, null, candidature, null);
			}
			Notification.show(applicationContext.getMessage("candidature.proposition.success", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		} else {
			/* envoi du mail au candidat */
			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(), NomenclatureUtils.MAIL_CANDIDATURE, null, candidature, candidature.getCandidat().getLangue().getCodLangue());
			Notification.show(applicationContext.getMessage("candidature.success", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		}
		return candidature;
	}

	/** Ouvre la fenetre pour le candidat
	 *
	 * @param candidature
	 * @param listener
	 */
	public void openCandidatureCandidat(final Candidature candidature, final Boolean isArchive,
			final CandidatureCandidatViewListener listener) {
		if (candidature == null) {
			return;
		}

		Authentication auth = userController.getCurrentAuthentication();
		Candidature candidatureLoad = candidatureRepository.findOne(candidature.getIdCand());
		if (candidatureLoad == null || candidatureLoad.getDatAnnulCand() != null
				|| (candidatureLoad.getCandidat().getCompteMinima().getCampagne().getDatArchivCamp() != null
						&& !isArchive)) {
			Notification.show(applicationContext.getMessage("candidature.open.error", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
			listener.candidatureCanceled(candidature);
			return;
		}
		candidatureLoad.setLastTypeDecision(getLastTypeDecisionCandidature(candidatureLoad));

		/* Si les valeurs ont changé entre temps, on update la vue candidat */
		String libLastTypDecLoad = getLibLastTypeDecisionCandidature(candidatureLoad.getLastTypeDecision(), true);
		String libLastTypDec = getLibLastTypeDecisionCandidature(candidature.getLastTypeDecision(), true);
		if ((!libLastTypDecLoad.equals(libLastTypDec))
				|| (!candidatureLoad.getTypeStatut().equals(candidature.getTypeStatut())) ||
				/* Ajout des controle sur le type de traitement pour les gestionnaires */
				(userController.isGestionnaireCandidat(auth)
						&& !candidatureLoad.getTypeTraitement().equals(candidature.getTypeTraitement()))
				|| (userController.isGestionnaireCandidat(auth)
						&& !candidatureLoad.getTemValidTypTraitCand().equals(candidature.getTemValidTypTraitCand())))
		/* Fin Ajout des controle sur le type de traitement pour les gestionnaires */
		{
			Notification.show(applicationContext.getMessage("candidature.open.modify", null, UI.getCurrent().getLocale()), Notification.Type.TRAY_NOTIFICATION);
			listener.statutDossierModified(candidatureLoad);
		}

		/* Blocage ouverture-->verif des droits */
		Boolean blocage = true;
		if (isCandidatOfCandidature(candidature)) {
			blocage = false;
		} else if (userController.isAdmin(auth) || userController.isScolCentrale(auth)) {
			blocage = false;
		} else {
			SecurityCentreCandidature scc = userController.getCentreCandidature(auth);
			SecurityCommission sc = userController.getCommission(auth);
			if (hasRightToOpenCandidature(candidature, scc, sc)) {
				blocage = false;
			}
		}
		if (blocage) {
			Notification.show(applicationContext.getMessage("candidature.open.noright", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
			return;
		}

		/* Verrou */
		Boolean locked = false;
		if (!lockCandidatController.getLockOrNotifyCandidature(candidatureLoad)) {
			locked = true;
		}

		/* L'utilisateur n'est pas bloqué */
		List<DroitFonctionnalite> listeDroitFonc = null;
		if (!userController.isCandidat()) {
			listeDroitFonc = droitProfilController.getCandidatureFonctionnalite(ConstanteUtils.TYP_GESTION_CANDIDATURE_CANDIDAT, candidatureLoad);
		}
		CandidatureWindow window = new CandidatureWindow(candidatureLoad, locked, false, isArchive, listeDroitFonc);
		if (listener != null) {
			window.addCandidatureCandidatListener(listener);
		}
		window.addCloseListener(e -> lockCandidatController.releaseLockCandidature(candidatureLoad));
		UI.getCurrent().addWindow(window);
	}

	/** Supprime un lock de candidature
	 *
	 * @param candidature
	 */
	public void removeLockCandidat(final Candidature candidature) {
		lockCandidatController.releaseLockCandidature(candidature);
	}

	/** @param cand
	 * @param scc
	 * @param sc
	 * @return true si l'utilisateur a le droit de regarder la candidature */
	public Boolean hasRightToOpenCandidature(final Candidature cand, final SecurityCentreCandidature scc,
			final SecurityCommission sc) {
		if (scc != null
				&& scc.getIdCtrCand().equals(cand.getFormation().getCommission().getCentreCandidature().getIdCtrCand())
				&& scc.getListFonctionnalite() != null
				&& scc.getListFonctionnalite().stream().filter(e -> e.getId().getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_FENETRE_CAND)).findAny().isPresent()) {
			if (scc.getIsGestAllCommission()
					|| (scc.getListeIdCommission() != null && scc.getListeIdCommission().stream().filter(e -> e.equals(cand.getFormation().getCommission().getIdComm())).findAny().isPresent())) {
				return true;
			}
		} else if (sc != null && sc.getIdComm().equals(cand.getFormation().getCommission().getIdComm())
				&& sc.getListFonctionnalite() != null
				&& sc.getListFonctionnalite().stream().filter(e -> e.getId().getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_FENETRE_CAND)).findAny().isPresent()) {
			return true;
		}
		return false;
	}

	/** Ouvre la fenetre pour le gestionnaire
	 *
	 * @param candidature
	 * @param canceled
	 * @param archived
	 * @param listeDroitFonc
	 */
	public void openCandidatureGestionnaire(final Candidature candidature, final Boolean canceled,
			final Boolean archived, final List<DroitFonctionnalite> listeDroitFonc) {
		if (candidature == null) {
			return;
		}

		Candidature candidatureLoad = candidatureRepository.findOne(candidature.getIdCand());
		if (candidatureLoad == null || (candidatureLoad.getDatAnnulCand() != null && !canceled)
				|| (candidatureLoad.getCandidat().getCompteMinima().getCampagne().getDatArchivCamp() != null
						&& !archived)) {
			Notification.show(applicationContext.getMessage("candidature.open.error", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
			return;
		}
		candidatureLoad.setLastTypeDecision(getLastTypeDecisionCandidature(candidatureLoad));

		Boolean locked = false;
		if (!lockCandidatController.getLockOrNotifyCandidature(candidatureLoad)) {
			locked = true;
		}

		CandidatureWindow window = new CandidatureWindow(candidatureLoad, locked, canceled, archived, listeDroitFonc);
		window.addCloseListener(e -> lockCandidatController.releaseLockCandidature(candidatureLoad));
		UI.getCurrent().addWindow(window);
	}

	/** @param candidature
	 * @return une liste de données perso à afficher */
	public List<SimpleTablePresentation> getInformationsCandidature(final Candidature candidature,
			final Boolean isCandidatOfCandidature) {
		List<SimpleTablePresentation> liste = new ArrayList<>();
		Formation formation = candidature.getFormation();
		TypeDecisionCandidature typeDecision = getLastTypeDecisionCandidature(candidature);

		liste.add(new SimpleTablePresentation("candidature." + Candidature_.formation.getName() + "." + Formation_.libForm.getName(), applicationContext.getMessage("candidature."
				+ Candidature_.formation.getName() + "." + Formation_.libForm.getName(), null, UI.getCurrent().getLocale()), formation.getLibForm()));
		SimpleTablePresentation stpStatutDossier = new SimpleTablePresentation("candidature." + ConstanteUtils.CANDIDATURE_LIB_STATUT, applicationContext.getMessage("candidature."
				+ ConstanteUtils.CANDIDATURE_LIB_STATUT, null, UI.getCurrent().getLocale()), i18nController.getI18nTraduction(candidature.getTypeStatut().getI18nLibTypStatut()));
		stpStatutDossier.setShortValue(candidature.getTypeStatut().getCodTypStatut());
		liste.add(stpStatutDossier);

		/* gestionnaire-->On affiche le type de traitement */
		if (!isCandidatOfCandidature) {
			String libTypTraitement = i18nController.getI18nTraduction(candidature.getTypeTraitement().getI18nLibTypTrait());
			if (candidature.getTemValidTypTraitCand()) {
				libTypTraitement = libTypTraitement + " ("
						+ applicationContext.getMessage("valide", null, UI.getCurrent().getLocale()) + ")";
			} else {
				libTypTraitement = libTypTraitement + " ("
						+ applicationContext.getMessage("non.valide", null, UI.getCurrent().getLocale()) + ")";
			}
			liste.add((new SimpleTablePresentation("candidature." + ConstanteUtils.CANDIDATURE_LIB_TYPE_TRAITEMENT, applicationContext.getMessage("candidature."
					+ ConstanteUtils.CANDIDATURE_LIB_TYPE_TRAITEMENT, null, UI.getCurrent().getLocale()), libTypTraitement)));
		}

		String libTypDecision = getLibLastTypeDecisionCandidature(typeDecision, isCandidatOfCandidature);
		String commentaire = null;
		String codeTypeDecision = NomenclatureUtils.TYP_AVIS_ATTENTE;

		/*
		 * La decision n'est pas null et le candidat est candidiat avec un avis validé
		 */
		if (typeDecision != null
				&& (!isCandidatOfCandidature || (isCandidatOfCandidature && typeDecision.getTemValidTypeDecCand()))) {
			if (typeDecision.getTemValidTypeDecCand()) {
				if (!isCandidatOfCandidature) {
					libTypDecision = libTypDecision + " ("
							+ applicationContext.getMessage("valide", null, UI.getCurrent().getLocale()) + ")";
				}
				if (candidature.getTemAcceptCand() != null && candidature.getTemAcceptCand()) {
					libTypDecision = libTypDecision + " : " + applicationContext.getMessage("candidature.confirm.label", null, UI.getCurrent().getLocale());
				} else if (candidature.getTemAcceptCand() != null && !candidature.getTemAcceptCand()) {
					libTypDecision = libTypDecision + " : " + applicationContext.getMessage("candidature.desist.label", null, UI.getCurrent().getLocale());
				}
			} else {
				libTypDecision = libTypDecision + " ("
						+ applicationContext.getMessage("non.valide", null, UI.getCurrent().getLocale()) + ")";
			}
			if (typeDecision.getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisPreselect())) {
				libTypDecision = libTypDecision + "<br>"
						+ ctrCandCandidatureController.getComplementPreselect(typeDecision);

			}
			codeTypeDecision = typeDecision.getTypeDecision().getTypeAvis().getCodTypAvis();
			if (!isCandidatOfCandidature || typeDecision.getTypeDecision().getTemAffCommentTypDec()) {
				commentaire = typeDecision.getCommentTypeDecCand();
			}
		}
		SimpleTablePresentation stpDecision = new SimpleTablePresentation("candidature." + ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION, applicationContext.getMessage("candidature."
				+ ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION, null, UI.getCurrent().getLocale()), libTypDecision);
		stpDecision.setShortValue(codeTypeDecision);
		liste.add(stpDecision);

		/* On ajoute le commentaire lié à l'avis à la suite */
		if (commentaire != null && !commentaire.equals("")) {
			liste.add(new SimpleTablePresentation("candidature."
					+ ConstanteUtils.CANDIDATURE_COMMENTAIRE, applicationContext.getMessage("candidature." + ConstanteUtils.CANDIDATURE_COMMENTAIRE, null, UI.getCurrent().getLocale()), commentaire));
		}

		/* gestionnaire-->On affiche le numéro OPI */
		if (!isCandidatOfCandidature) {
			String opi = applicationContext.getMessage("candidature.no.opi", null, UI.getCurrent().getLocale());
			if (candidature.getOpi() != null && candidature.getOpi().getDatPassageOpi() != null) {
				if (candidature.getOpi().getCodOpi() != null) {
					opi = candidature.getOpi().getCodOpi();
				} else {
					opi = parametreController.getPrefixeOPI()
							+ candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin();
				}
				opi = applicationContext.getMessage("candidature.valOpi", new Object[] {opi, formatterDateTime.format(candidature.getOpi().getDatPassageOpi())}, UI.getCurrent().getLocale());
			}

			liste.add(new SimpleTablePresentation("candidature."
					+ ConstanteUtils.CANDIDATURE_OPI, applicationContext.getMessage("candidature." + ConstanteUtils.CANDIDATURE_OPI, null, UI.getCurrent().getLocale()), opi));
		}

		return liste;
	}

	/** @param candidature
	 * @param isCandidatOfCandidature
	 * @return les infos de dates de la candidature */
	public List<SimpleTablePresentation> getInformationsDateCandidature(final Candidature candidature,
			final Boolean isCandidatOfCandidature) {
		List<SimpleTablePresentation> liste = new ArrayList<>();
		Formation formation = candidature.getFormation();
		/* On recupere les dates de la formation */
		LocalDate datAnalyseForm = formation.getDatAnalyseForm();
		LocalDate datRetourForm = formation.getDatRetourForm();
		LocalDate datConfirmForm = formation.getDatConfirmForm();
		LocalDate datJuryForm = formation.getDatJuryForm();
		LocalDate datPubliForm = formation.getDatPubliForm();

		/*
		 * Si candidature archivée, on prend les dates stockées dans la candidature
		 * On vérifie si la date de retour est valorisée, dans le cas contraire, on est dans l'ancien mode où on
		 * affiche uniquement les dates de formations
		 */
		if (candidature.getCandidat().getCompteMinima().getCampagne().getDatArchivCamp() != null
				&& candidature.getDatRetourForm() != null) {
			datAnalyseForm = candidature.getDatAnalyseForm();
			datRetourForm = candidature.getDatRetourForm();
			datConfirmForm = candidature.getDatConfirmForm();
			datJuryForm = candidature.getDatJuryForm();
			datPubliForm = candidature.getDatPubliForm();
		}

		if (datAnalyseForm != null) {
			liste.add(getDatePresentation(datAnalyseForm, Candidature_.formation.getName() + "." + Candidature_.datAnalyseForm.getName()));
		}
		if (datRetourForm != null) {
			liste.add(getDatePresentation(datRetourForm, Candidature_.formation.getName() + "." + Candidature_.datRetourForm.getName()));
		}
		if (datConfirmForm != null) {
			liste.add(getDatePresentation(datConfirmForm, Candidature_.formation.getName() + "." + Candidature_.datConfirmForm.getName()));
		}
		if (datJuryForm != null) {
			liste.add(getDatePresentation(datJuryForm, Candidature_.formation.getName() + "." + Candidature_.datJuryForm.getName()));
		}
		if (datPubliForm != null) {
			liste.add(getDatePresentation(datPubliForm, Candidature_.formation.getName() + "." + Candidature_.datPubliForm.getName()));
		}
		/* Le candidat est gestionnaire-->On affiche la date de reception */
		if (!isCandidatOfCandidature) {
			if (candidature.getDatReceptDossierCand() != null) {
				liste.add(getDatePresentation(candidature.getDatReceptDossierCand(), Candidature_.datReceptDossierCand.getName()));
			}
			if (candidature.getDatTransDossierCand() != null) {
				liste.add(getDatePresentation(candidature.getDatTransDossierCand(), Candidature_.datTransDossierCand.getName()));
			}

		}
		return liste;
	}

	/** @param date
	 * @param propertyId
	 * @return un objet de présentation de date */
	private SimpleTablePresentation getDatePresentation(final Temporal date, final String propertyId) {
		return new SimpleTablePresentation("candidature." + propertyId, applicationContext.getMessage("candidature." + propertyId, null, UI.getCurrent().getLocale()), formatterDate.format(date));
	}

	/** @param typeDecision
	 *            la decision
	 * @param isCandidatOfCandidature
	 * @return le libellé de la derniere decision */
	public String getLibLastTypeDecisionCandidature(final TypeDecisionCandidature typeDecision,
			final Boolean isCandidatOfCandidature) {
		String decision = applicationContext.getMessage("candidature.no.decision", null, UI.getCurrent().getLocale());

		/*
		 * La decision n'est pas null et le candidat est gestionnaire ou le candidat est
		 * candidiat avec un avis validé
		 */
		if (typeDecision != null
				&& (!isCandidatOfCandidature || (isCandidatOfCandidature && typeDecision.getTemValidTypeDecCand()))) {
			decision = i18nController.getI18nTraduction(typeDecision.getTypeDecision().getI18nLibTypDec());
			MotivationAvis motiv = typeDecision.getMotivationAvis();
			if (typeDecision.getListCompRangTypDecCand() != null) {
				decision = decision + " - "
						+ applicationContext.getMessage("candidature.rang", null, UI.getCurrent().getLocale()) + " "
						+ typeDecision.getListCompRangTypDecCand();
			}
			if (motiv != null) {
				decision = decision + " - " + i18nController.getI18nTraduction(motiv.getI18nLibMotiv());
			}
		}
		return decision;
	}

	/** @param candidature
	 * @return la derniere decision prise */
	public TypeDecisionCandidature getLastTypeDecisionCandidature(final Candidature candidature) {
		Optional<TypeDecisionCandidature> decOpt = candidature.getTypeDecisionCandidatures().stream().sorted((e1, e2) -> (e2.getDatCreTypeDecCand().compareTo(e1.getDatCreTypeDecCand())))
				// .filter(e->e.getTemValidTypeDecCand())
				.findFirst();
		if (decOpt.isPresent()) {
			return decOpt.get();
		}
		return null;
	}

	/** Modifie la confirmation ou le desistement
	 *
	 * @param candidature
	 * @param confirm
	 * @param listener
	 */
	public void setConfirmationCandidature(final Candidature candidature, final Boolean confirm,
			final CandidatureListener listener) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}

		String txt;
		if (confirm) {
			txt = applicationContext.getMessage("candidature.confirm.window", null, UI.getCurrent().getLocale());
		} else {
			txt = applicationContext.getMessage("candidature.desist.window", null, UI.getCurrent().getLocale());
		}
		ConfirmWindow confirmWindow = new ConfirmWindow(txt);
		confirmWindow.addBtnOuiListener(e -> {
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}

			/* on verifie que le voeux n'a pas été déjà confirmé */
			Boolean lastConfirm = candidature.getTemAcceptCand();

			candidature.setTemAcceptCand(confirm);
			candidature.setDatAcceptCand(LocalDateTime.now());
			candidature.setUserAcceptCand(userController.getCurrentNoDossierCptMinOrLogin());
			listener.infosCandidatureModified(candidatureRepository.save(candidature));
			if (confirm) {
				decisionCandidatureController.generateOpi(candidature, true);
			} else {
				/*
				 * Desistement --> on verifie que le voeux n'avait pas été déjà confirmé-->dans
				 * ce cas, on rejoue l'OPI
				 */
				if (lastConfirm != null && lastConfirm) {
					decisionCandidatureController.generateOpi(candidature, false);
				}
			}
			String typeMail = (confirm) ? NomenclatureUtils.MAIL_CANDIDATURE_CONFIRM
					: NomenclatureUtils.MAIL_CANDIDATURE_DESIST;
			String msgNotif = (confirm)
					? applicationContext.getMessage("candidature.confirm.success", null, UI.getCurrent().getLocale())
					: applicationContext.getMessage("candidature.desist.success", null, UI.getCurrent().getLocale());
			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(), typeMail, null, candidature, candidature.getCandidat().getLangue().getCodLangue());
			Notification.show(msgNotif, Type.WARNING_MESSAGE);
			if (!confirm) {
				decisionCandidatureController.candidatFirstCandidatureListComp(candidature.getFormation());
			}

		});
		UI.getCurrent().addWindow(confirmWindow);

	}

	/** @param candidature
	 * @return true si l'utilisateur est un candidat valide */
	public Boolean isCandidatOfCandidature(final Candidature candidature) {
		if (userController.isCandidatValid() && candidatController.getCompteMinima() != null
				&& candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin().equals(candidatController.getCompteMinima().getNumDossierOpiCptMin())) {
			return true;
		}
		return false;
	}

	/** Annule une candidature
	 *
	 * @param candidature
	 * @param listener
	 * @param candidatureCandidatListener
	 */
	public void cancelCandidature(final Candidature candidature, final CandidatureListener listener,
			final CandidatureCandidatViewListener candidatureCandidatListener) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("candidature.cancel.window", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}

			List<PjCand> listePiecesCommunes = new ArrayList<>();

			for (PjCand pjCand : candidature.getPjCands()) {
				try {
					if (pjCand.getPieceJustif().getTemCommunPj() && !pjCand.getPieceJustif().getTemUnicitePj()) {
						listePiecesCommunes.add(pjCand);
					} else {
						candidaturePieceController.removeFileToPj(pjCand);
					}
				} catch (Exception e1) {
					Notification.show(applicationContext.getMessage("candidature.cancel.error.pj", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}
			}

			candidature.setPjCands(listePiecesCommunes);
			candidature.setDatAnnulCand(LocalDateTime.now());
			candidature.setUserAnnulCand(userController.getCurrentNoDossierCptMinOrLogin());
			listener.candidatureCanceled(candidatureRepository.save(candidature));

			if (candidatureCandidatListener != null) {
				candidatureCandidatListener.candidatureCanceled(candidature);
			}
			/* envoi du mail au candidat */
			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(), NomenclatureUtils.MAIL_CANDIDATURE_ANNULATION, null, candidature, candidature.getCandidat().getLangue().getCodLangue());

			/* envoi du mail à la commission */
			if (candidature.getFormation().getCommission().getTemAlertAnnulComm()) {
				mailController.sendMailByCod(candidature.getFormation().getCommission().getMailComm(), NomenclatureUtils.MAIL_COMMISSION_ALERT_ANNULATION, null, candidature, null);
			}

			Notification.show(applicationContext.getMessage("candidature.cancel.success", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** @param candidature
	 * @return true si la formation est demat */
	public Boolean isCandidatureDematerialise(final Candidature candidature) {
		return candidature.getFormation().getTemDematForm() && parametreController.getIsUtiliseDemat();
	}

	/** @param candidature
	 * @return le type de lettre a envoyer */
	public String getTypeLettre(final Candidature candidature, final String mode) {
		if (candidature == null || candidature.getLastTypeDecision() == null
				|| (mode.equals(ConstanteUtils.TYP_LETTRE_DOWNLOAD)
						&& !candidature.getFormation().getCommission().getTemEditLettreComm())
				|| (mode.equals(ConstanteUtils.TYP_LETTRE_MAIL)
						&& !candidature.getFormation().getCommission().getTemMailLettreComm())) {
			return null;
		}

		/* Lettre d'admission */
		if (candidature.getLastTypeDecision().getTypeDecision().getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_FAV)
				&& candidature.getLastTypeDecision().getTemValidTypeDecCand()
				&& (candidature.getTemAcceptCand() == null
						|| (candidature.getTemAcceptCand() != null && candidature.getTemAcceptCand()))
				&& candidature.getDatAnnulCand() == null) {
			/*
			 * Si le parametre de telechargement apres réponse est coché et que le candidat
			 * n'a pas donné de rponse, on n'autorise pas le téléchargement
			 */
			if (parametreController.getIsDownloadLettreAfterAccept() && candidature.getTemAcceptCand() == null
					&& candidature.getDatAnnulCand() == null) {
				return null;
			}
			return ConstanteUtils.TEMPLATE_LETTRE_ADM;
		}

		/* Lettre de refus */
		else if (candidature.getLastTypeDecision().getTypeDecision().getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_DEF) && candidature.getLastTypeDecision().getTemValidTypeDecCand()
				&& candidature.getDatAnnulCand() == null) {
			return ConstanteUtils.TEMPLATE_LETTRE_REFUS;
		}
		return null;
	}

	/** @param candidature
	 * @return le nom de fichier de la lettre */
	public String getNomFichierLettre(final Candidature candidature, final String mode) {
		String typeLettre = getTypeLettre(candidature, mode);
		if (typeLettre != null && typeLettre.equals(ConstanteUtils.TEMPLATE_LETTRE_ADM)) {
			return applicationContext.getMessage("candidature.lettre.file.adm", new Object[] {
					candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin() + "_"
							+ candidature.getCandidat().getNomPatCandidat() + "_"
							+ candidature.getCandidat().getPrenomCandidat(),
					candidature.getFormation().getCodForm()}, UI.getCurrent().getLocale());
		}
		/* Lettre de refus */
		else if (typeLettre != null && typeLettre.equals(ConstanteUtils.TEMPLATE_LETTRE_REFUS)) {
			return applicationContext.getMessage("candidature.lettre.file.ref", new Object[] {
					candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin() + "_"
							+ candidature.getCandidat().getNomPatCandidat() + "_"
							+ candidature.getCandidat().getPrenomCandidat(),
					candidature.getFormation().getCodForm()}, UI.getCurrent().getLocale());
		}
		return "";
	}

	/** @param candidature
	 * @return l'inputstream pour le telechargement de la lettre */
	public InputStream downloadLettre(final Candidature candidature, final String mode) {
		String templateLettre = getTypeLettre(candidature, mode);
		if (templateLettre == null) {
			return null;
		}

		Candidat candidat = candidature.getCandidat();
		CompteMinima cptMin = candidat.getCompteMinima();
		Formation formation = candidature.getFormation();
		Commission commission = formation.getCommission();

		String adresseCandidat = adresseController.getLibelleAdresse(candidat.getAdresse(), "\n");
		String adresseCommission = adresseController.getLibelleAdresse(commission.getAdresse(), "\n");

		/* Les dates utiles */
		String dateConfirm = MethodUtils.formatDate(formation.getDatConfirmForm(), formatterDate);
		String dateJury = MethodUtils.formatDate(formation.getDatJuryForm(), formatterDate);

		String libAvis = "";
		String commentaire = null;
		String motif = "";
		String dateValidAvis = "";
		TypeDecisionCandidature typeDecisionCand = candidature.getLastTypeDecision();
		if (typeDecisionCand != null && typeDecisionCand.getTypeDecision() != null) {
			// date de validation de l'avis
			dateValidAvis = MethodUtils.formatDate(typeDecisionCand.getDatValidTypeDecCand(), formatterDate);

			// libellé de l'avis
			libAvis = i18nController.getI18nTraduction(typeDecisionCand.getTypeDecision().getI18nLibTypDec());
			// motif pour un avis défavorable
			if (typeDecisionCand.getMotivationAvis() != null
					&& templateLettre.equals(ConstanteUtils.TEMPLATE_LETTRE_REFUS)) {
				motif = i18nController.getI18nTraduction(typeDecisionCand.getMotivationAvis().getI18nLibMotiv());
			}
			// commentaire
			if (typeDecisionCand.getCommentTypeDecCand() != null && !typeDecisionCand.getCommentTypeDecCand().equals("")
					&& typeDecisionCand.getTypeDecision().getTemAffCommentTypDec()) {
				commentaire = typeDecisionCand.getCommentTypeDecCand();
			}
		}

		ExportLettreCandidat data = new ExportLettreCandidat(cptMin.getNumDossierOpiCptMin(), candidat.getCivilite().getLibCiv(), candidat.getNomPatCandidat(), candidat.getNomUsuCandidat(), candidat.getPrenomCandidat(), formatterDate.format(candidat.getDatNaissCandidat()), adresseCandidat, campagneController.getLibelleCampagne(cptMin.getCampagne()), commission.getLibComm(), adresseCommission, formation.getCodForm(), formation.getLibForm(), commission.getSignataireComm(), libAvis, commentaire, motif, dateConfirm, dateJury, dateValidAvis);

		InputStream fichierSignature = null;
		if (commission.getFichier() != null) {
			fichierSignature = fileController.getInputStreamFromFichier(commission.getFichier());
		}

		return generateLettre(templateLettre, data, fichierSignature);
	}

	/** @param templateLettre
	 * @param data
	 * @param fichierSignature
	 * @return l'inputstram de la lettre */
	public InputStream generateLettre(final String templateLettre, final ExportLettreCandidat data,
			final InputStream fichierSignature) {
		InputStream template = MethodUtils.getXDocReportTemplate(templateLettre, i18nController.getLangueCandidat(), cacheController.getLangueDefault().getCodLangue());
		if (template == null) {
			return null;
		}
		ByteArrayInOutStream out = new ByteArrayInOutStream();
		try {
			/* Ajout date et heure */
			data.setDateHeure(formatterDateTime.format(LocalDateTime.now()));
			data.setDate(formatterDate.format(LocalDateTime.now()));

			/* Generation du template */
			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(template, TemplateEngineKind.Velocity);

			/* Variables */
			IContext context = report.createContext();
			context.put("data", data);

			/* Pour l'image */
			FieldsMetadata metadata = new FieldsMetadata();
			if (fichierSignature != null) {
				metadata.addFieldAsImage("imageSignature");
				IImageProvider originalSizeLogo = new ByteArrayImageProvider(fichierSignature, true);
				context.put("imageSignature", originalSizeLogo);
			} else {
				/* Si pas de fichier, on supprime l'image */
				metadata.addFieldAsImage("imageSignature", NullImageBehaviour.RemoveImageTemplate);
			}
			report.setFieldsMetadata(metadata);

			/* On vérifie si on affiche le tableau de signataire */
			if (data.getLibelleSignature().equals("") && fichierSignature == null) {
				context.put("showSignataire", false);
			} else {
				context.put("showSignataire", true);
			}

			/* Transformation en pdf */

			Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);

			report.convert(context, options, out);

			return signaturePdfManager.signPdf(out);
		} catch (Exception e) {
			// probleme de taille de signature XDocConverterException + StackOverflowError
			if (e.getClass() != null && e instanceof XDocConverterException && e.getCause() != null
					&& e.getCause() instanceof StackOverflowError) {
				Notification.show(applicationContext.getMessage("candidature.lettre.download.sign.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			} else {
				Notification.show(applicationContext.getMessage("candidature.lettre.download.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				logger.error("erreur a la création de la lettre", e);
			}

			return null;
		} finally {
			// fermeture des fichiers
			MethodUtils.closeRessource(fichierSignature);
			MethodUtils.closeRessource(template);
			MethodUtils.closeRessource(out);
		}
	}

	/** @param candidature
	 * @param listePresentation
	 * @param listeDatePresentation
	 * @param adresse
	 * @param listePj
	 * @param listeForm
	 * @return l'InputStream d'export
	 * @throws IOException
	 * @throws XDocReportException
	 */
	private ByteArrayInputStream generateDossier(final Candidature candidature,
			final List<SimpleTablePresentation> listePresentation,
			final List<SimpleTablePresentation> listeDatePresentation, final String adresse,
			final List<PjPresentation> listePj, final List<FormulairePresentation> listeForm)
			throws IOException, XDocReportException {
		InputStream in = null;
		ByteArrayInOutStream out = new ByteArrayInOutStream();
		try {
			// 1) Load Docx file by filling Velocity template engine and cache
			// it to the registry
			in = MethodUtils.getXDocReportTemplate(ConstanteUtils.TEMPLATE_DOSSIER, i18nController.getLangueCandidat(), cacheController.getLangueDefault().getCodLangue());
			if (in == null) {
				return null;
			}

			/* Chargement des données utiles */
			Candidat candidat = candidature.getCandidat();
			CompteMinima cptMin = candidat.getCompteMinima();
			Formation formation = candidature.getFormation();
			Commission commission = formation.getCommission();

			/* Utilisation de la demat */
			Boolean isDematerialisation = isCandidatureDematerialise(candidature);

			/* On place les données dans des bean speciales export */
			ExportDossierCandidature exportCandidature = new ExportDossierCandidature(campagneController.getLibelleCampagne(cptMin.getCampagne()), commission.getLibComm(), adresseController.getLibelleAdresse(commission.getAdresse(), "\n"), commission.getMailComm(), commission.getTelComm(), formation, MethodUtils.formatToExportHtml(i18nController.getI18nTraduction(commission.getI18nCommentRetourComm())));

			ExportDossierCandidat exportCandidat = new ExportDossierCandidat(cptMin, candidat, formatterDate.format(candidat.getDatNaissCandidat()), adresseController.getLibelleAdresse(candidat.getAdresse(), "\n"), candidat.getIneCandidat(), candidat.getCleIneCandidat());

			ExportDossierBac exportDossierBac = new ExportDossierBac(candidat);

			List<ExportDossierCursusInterne> listeCursusInterne = new ArrayList<>();
			candidat.getCandidatCursusInternes().forEach(e -> listeCursusInterne.add(new ExportDossierCursusInterne(e)));
			listeCursusInterne.sort((p1, p2) -> p1.getAnnee().compareTo(p2.getAnnee()));

			List<ExportDossierCursusExterne> listeCursusExterne = new ArrayList<>();
			candidat.getCandidatCursusPostBacs().forEach(e -> listeCursusExterne.add(new ExportDossierCursusExterne(e, tableRefController.getLibelleObtenuCursusByCode(e.getObtenuCursus()))));
			listeCursusExterne.sort((p1, p2) -> p1.getAnnee().compareTo(p2.getAnnee()));

			List<ExportDossierStage> listeStage = new ArrayList<>();
			candidat.getCandidatStage().forEach(e -> listeStage.add(new ExportDossierStage(e)));
			listeStage.sort((p1, p2) -> p1.getAnnee().compareTo(p2.getAnnee()));

			List<ExportDossierCursusPro> listeCursusPro = new ArrayList<>();
			candidat.getCandidatCursusPros().forEach(e -> listeCursusPro.add(new ExportDossierCursusPro(e)));
			listeCursusPro.sort((p1, p2) -> p1.getAnnee().compareTo(p2.getAnnee()));

			List<ExportDossierMotivationAvis> listeMotivationAvis = new ArrayList<>();
			List<ExportDossierAvis> listeAvis = new ArrayList<>();
			List<ExportDossierPj> listeExportPj = new ArrayList<>();

			if (!isDematerialisation) {
				listePj.forEach(e -> {
					if (!(e.getFilePj() == null && e.getPJConditionnel() && e.getCodStatut() != null
							&& e.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE))) {
						listeExportPj.add(new ExportDossierPj(e.getLibPj(), e.getLibStatut(), e.getCommentaire()));
					}
				});
				// listePj.forEach(e->listeExportPj.add(new
				// ExportDossierPj(e.getLibPj(),e.getLibStatut(),e.getCommentaire())));
				motivationAvisController.getMotivationAvisEnService().forEach(e -> listeMotivationAvis.add(new ExportDossierMotivationAvis(i18nController.getI18nTraduction(e.getI18nLibMotiv()))));
				typeDecisionController.getTypeDecisionsEnService().forEach(e -> listeAvis.add(new ExportDossierAvis(i18nController.getI18nTraduction(e.getI18nLibTypDec()), e.getTypeAvis().getCodTypAvis())));
				listeAvis.sort((p1, p2) -> p1.getOrder().compareTo(p2.getOrder()));
			} else {
				listePj.forEach(e -> {
					ExportDossierPj exportDossierPj = new ExportDossierPj(e.getLibPj(), e.getLibStatut(), e.getCommentaire());
					if (e.getFilePj() != null && e.getFilePj().getFileFichier() != null) {
						exportDossierPj.setLibFichier(e.getFilePj().getNomFichier());
					}
					listeExportPj.add(exportDossierPj);
				});
			}

			ExportDossierDate listeDates = new ExportDossierDate(MethodUtils.getLibByPresentationCode(listeDatePresentation, "candidature." + Candidature_.formation.getName() + "."
					+ Formation_.datRetourForm.getName()), MethodUtils.getLibByPresentationCode(listeDatePresentation, "candidature." + Candidature_.formation.getName() + "."
							+ Formation_.datConfirmForm.getName()), MethodUtils.getLibByPresentationCode(listeDatePresentation, "candidature." + Candidature_.formation.getName() + "."
									+ Formation_.datJuryForm.getName()), MethodUtils.getLibByPresentationCode(listeDatePresentation, "candidature."
											+ Candidature_.formation.getName() + "." + Formation_.datPubliForm.getName()));

			IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Velocity);

			// 2) Create fields metadata to manage lazy loop (#foreach velocity) for table
			// row.
			/*
			 * FieldsMetadata metadata = report.createFieldsMetadata(); metadata.load(
			 * "cursusInterne", ExportDossierCursusInterne.class, true );
			 */

			// 3) Create context Java model
			IContext context = report.createContext();
			// Register project
			context.put("dateheure", formatterDateTime.format(LocalDateTime.now()));
			context.put("adresseEcandidat", loadBalancingController.getApplicationPathForCandidat());
			context.put("candidature", exportCandidature);
			context.put("candidat", exportCandidat);
			context.put("bac", exportDossierBac);
			context.put("cursusInternes", listeCursusInterne);
			context.put("affichageCursusInterne", listeCursusInterne.size() > 0);
			context.put("cursusExternes", listeCursusExterne);
			context.put("affichageCursusExterne", listeCursusExterne.size() > 0);
			context.put("stages", listeStage);
			context.put("affichageStage", listeStage.size() > 0);
			context.put("cursusPros", listeCursusPro);
			context.put("affichageCursusPro", listeCursusPro.size() > 0);
			context.put("listeAvis", listeAvis);
			context.put("listeMotivationAvis", listeMotivationAvis);
			context.put("dates", listeDates);
			context.put("listePiecesJustifs", listeExportPj);
			context.put("non-dematerialisation", !isDematerialisation);
			context.put("affichagePjDemat", (listeExportPj.size() > 0 && isDematerialisation));

			// 4) Generate report by merging Java model with the Docx
			Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.XWPF);

			report.convert(context, options, out);
			in.close();
			return out.getInputStream();
		} catch (Exception e) {
			return null;
		} finally {
			// fermeture des fichiers
			MethodUtils.closeRessource(in);
			MethodUtils.closeRessource(out);
		}
	}

	/** @param liste
	 * @return un zip ou pdf contentant tout les dossiers Si un seul dossier, on ajoute les PJ */
	public OnDemandFile downlaodMultipleDossier(final List<Candidature> liste, final Commission commission) {
		if (liste == null || liste.size() == 0 || liste.size() > parametreController.getNbDossierDownloadMax()) {
			return null;
		} else if (liste.size() == 1) {
			Candidature candidature = liste.get(0);
			return downloadDossier(candidature, getInformationsCandidature(candidature, false), getInformationsDateCandidature(candidature, false), adresseController.getLibelleAdresseCommission(candidature.getFormation().getCommission(), "<br>"), candidaturePieceController.getPjCandidature(candidature), candidaturePieceController.getFormulaireCandidature(candidature), true);
		} else {
			String nomFichier = applicationContext.getMessage("candidature.download.multiple.file", new Object[] {commission.getLibComm(),
					DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now())}, UI.getCurrent().getLocale());
			if (parametreController.getIsDownloadMultipleModePdf()) {
				return downlaodMultipleDossierPdf(liste, nomFichier);
			} else {
				return downlaodMultipleDossierZip(liste, nomFichier);
			}
		}
	}

	/** @param liste
	 * @param nameFile
	 * @return un zip contenant tous les dossiers */
	private OnDemandFile downlaodMultipleDossierZip(final List<Candidature> liste, final String nameFile) {
		ByteArrayInOutStream out = new ByteArrayInOutStream();
		ZipOutputStream zos = new ZipOutputStream(out);
		Boolean error = false;
		for (Candidature candidature : liste) {
			OnDemandFile bisDossier = null;
			try {
				// le dossier outStream
				bisDossier = downloadDossier(candidature, getInformationsCandidature(candidature, false), getInformationsDateCandidature(candidature, false), adresseController.getLibelleAdresseCommission(candidature.getFormation().getCommission(), "<br>"), candidaturePieceController.getPjCandidature(candidature), candidaturePieceController.getFormulaireCandidature(candidature), parametreController.getIsDownloadMultipleAddPj());
				String fileName = applicationContext.getMessage("candidature.download.file", new Object[] {
						candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin() + "_"
								+ candidature.getCandidat().getNomPatCandidat() + "_"
								+ candidature.getCandidat().getPrenomCandidat(),
						candidature.getFormation().getCodForm()}, UI.getCurrent().getLocale());
				zos.putNextEntry(new ZipEntry(fileName));
				int count;
				byte data[] = new byte[2048];
				while ((count = bisDossier.getInputStream().read(data, 0, 2048)) != -1) {
					zos.write(data, 0, count);
				}
				zos.closeEntry();
			} catch (IOException e) {
				error = true;
				logger.error("erreur a la génération d'un dossier lors du zip", e);
			} finally {
				/* Nettoyage des ressources */
				// MethodUtils.closeRessource(bisDossier);
			}
		}
		if (error) {
			Notification.show(applicationContext.getMessage("candidature.download.multiple.error.file", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		}
		try {
			zos.finish();
			zos.close();
			return new OnDemandFile(nameFile + ".zip", out.getInputStream());
		} catch (IOException e) {
			logger.error("erreur a la génération du zip", e);
			Notification.show(applicationContext.getMessage("candidature.download.multiple.error.zip", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return null;
		} finally {
			/* Nettoyage des ressources */
			MethodUtils.closeRessource(zos);
			MethodUtils.closeRessource(out);
		}
	}

	/** @param liste
	 * @param nameFile
	 * @return un pdf contenant tous les dossiers */
	private OnDemandFile downlaodMultipleDossierPdf(final List<Candidature> liste, final String nameFile) {
		ByteArrayInOutStream out = new ByteArrayInOutStream();
		PDFMergerUtility ut = new PDFMergerUtility();
		Boolean error = false;
		for (Candidature candidature : liste) {
			OnDemandFile bisDossier = null;
			try {
				bisDossier = downloadDossier(candidature, getInformationsCandidature(candidature, false), getInformationsDateCandidature(candidature, false), adresseController.getLibelleAdresseCommission(candidature.getFormation().getCommission(), "<br>"), candidaturePieceController.getPjCandidature(candidature), candidaturePieceController.getFormulaireCandidature(candidature), parametreController.getIsDownloadMultipleAddPj());
				;
				ut.addSource(bisDossier.getInputStream());
			} catch (Exception e) {
				error = true;
				logger.error("erreur a la génération d'un dossier lors du zip", e);
			} finally {
				/* Nettoyage des ressources */
				// MethodUtils.closeRessource(bisDossier);
			}
		}
		if (error) {
			Notification.show(applicationContext.getMessage("candidature.download.multiple.error.file", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		}

		try {
			ut.setDestinationFileName(nameFile);
			ut.setDestinationStream(out);
			ut.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
			return new OnDemandFile(nameFile + ".pdf", out.getInputStream());
		} catch (IOException e) {
			logger.error("erreur a la génération du pdf multiple", e);
			Notification.show(applicationContext.getMessage("candidature.download.multiple.error.pdf", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return null;
		} finally {
			/* Nettoyage des ressources */
			MethodUtils.closeRessource(out);
		}
	}

	/** telecharge le dossier
	 *
	 * @param candidature
	 * @param listePresentation
	 * @param listeDatePresentation
	 * @param adresse
	 * @param listePj
	 * @param listeForm
	 * @return l'InputStream du dossier */
	public OnDemandFile downloadDossier(final Candidature candidature,
			final List<SimpleTablePresentation> listePresentation,
			final List<SimpleTablePresentation> listeDatePresentation, final String adresse,
			final List<PjPresentation> listePj, final List<FormulairePresentation> listeForm, final Boolean addPj) {
		String fileName = applicationContext.getMessage("candidature.download.file", new Object[] {
				candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin() + "_"
						+ candidature.getCandidat().getNomPatCandidat() + "_"
						+ candidature.getCandidat().getPrenomCandidat(),
				candidature.getFormation().getCodForm()}, UI.getCurrent().getLocale());

		// Les parametres des PJ
		Boolean enableAddApogeePJDossier = parametreController.getIsEnableAddPJApogeeDossier();

		// Font
		PDFont font = PDType1Font.HELVETICA_BOLD;
		// le dossier outStream
		ByteArrayInputStream bisDossier = null;
		// liste des InputStream à fermer
		List<InputStream> listeInputStreamToClose = new ArrayList<>();
		try {
			bisDossier = generateDossier(candidature, listePresentation, listeDatePresentation, adresse, listePj, listeForm);
			if (bisDossier == null) {
				Notification.show(applicationContext.getMessage("candidature.download.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return null;
			}
		} catch (IOException | XDocReportException e2) {
			Notification.show(applicationContext.getMessage("candidature.download.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			logger.error("erreur a la génération du dossier", e2);
			return null;
		}
		ByteArrayInOutStream out = new ByteArrayInOutStream();
		InputStream is = null;
		try {
			PDFMergerUtility ut = new PDFMergerUtility();
			ut.addSource(bisDossier);
			Boolean errorEncoding = false;
			Boolean errorImg = false;
			Integer nbFilePJ = 0;
			for (PjPresentation pj : listePj) {
				if (pj.getFilePj() != null) {
					nbFilePJ++;
				}
			}

			if (addPj && nbFilePJ > 0
					&& !fileController.isFileServiceMaintenance(applicationContext.getMessage("file.service.maintenance.dossier", null, UI.getCurrent().getLocale()))) {
				for (PjPresentation e : listePj) {
					// listePj.forEach(e->{
					try {
						// titre header
						String textHeader = e.getLibPj();
						// le fichier ne doit pas etre null
						// la piece ne doit pas provenir d'apogée
						// ou
						// la pièce doit provenir d'apogée mais le temoin de context pourt ajouter les
						// PJ doit etre à true
						if (e.getFilePj() != null && (e.getPjCandidatFromApogee() == null
								|| (e.getPjCandidatFromApogee() != null && enableAddApogeePJDossier))) {
							Fichier file = e.getFilePj();
							String nameFile = file.getNomFichier();
							InputStream inputStreamFile = fileController.getInputStreamFromPjPresentation(e);
							/*
							 * On doit fermer l'inputStream apres le merge donc je stock le stream et le
							 * ferme apres
							 */
							listeInputStreamToClose.add(inputStreamFile);
							// cas du PDF
							if (inputStreamFile != null && MethodUtils.isPdfFileName(nameFile)) {
								// chargement page A4

								// si demande d'ajout de header, on ajoute le text
								/*
								 * if (enableAddPJHeader){ PDRectangle PAGE_SIZE_A4 = PDRectangle.A4; //on
								 * charge le document PDDocument document = PDDocument.load(inputStreamFile);
								 * //on supprime les sécurités document.setAllSecurityToBeRemoved(true);
								 * //on cherche la premiere page et on créé un document PDPage firstPage =
								 * (PDPage) document.getDocumentCatalog().getPages().get(0); PDPageContentStream
								 * contentStream = new PDPageContentStream(document, firstPage,
								 * AppendMode.PREPEND, true);
								 * //Ajout du header addHeaderPJ(textHeader, font, PAGE_SIZE_A4, contentStream,
								 * enableAddPJHeader);
								 * //fermeture writer contentStream.close();
								 * ByteArrayInOutStream baosImg = new ByteArrayInOutStream();
								 * document.save(baosImg); //Creation du flux ByteArrayInputStream bis =
								 * baosImg.getInputStream();
								 * //Ajout de la page au document ut.addSource(bis); document.close();
								 * inputStreamFile.close(); }else{ //sinon on ajoute directement l'inputStream
								 * ut.addSource(inputStreamFile); //On doit fermer l'inputStream apres le merge
								 * pour les pdf (??) donc je stock le stream et le ferme apres
								 * listeInputStreamToClose.add(inputStreamFile); }
								 */
								// on ajoute directement l'inputStream
								ut.addSource(inputStreamFile);
							} else if (inputStreamFile != null && MethodUtils.isImgFileName(nameFile)) {
								// creation document
								PDDocument document = new PDDocument();
								ByteArrayInOutStream baosImg = new ByteArrayInOutStream();
								ByteArrayInputStream bis = null;

								try {
									// chargement page A4
									PDRectangle PAGE_SIZE_A4 = PDRectangle.A4;

									// creation page
									PDPage page = new PDPage(PAGE_SIZE_A4);

									// ajout de la page
									document.addPage(page);

									// Stream du document
									PDPageContentStream contentStream = new PDPageContentStream(document, page);

									// margin top est calculée si un text de titre a été ajouté
									Float marginTop = addHeaderPJ(textHeader, font, PAGE_SIZE_A4, contentStream);

									// on ajoute la marge sous le text (ou la marge depuis le haut du doc si pas de
									// text)
									marginTop = marginTop + ConstanteUtils.DOSSIER_MARGIN;

									// creation de l'image
									PDImageXObject img = null;
									// JPG
									if (MethodUtils.isJpgFileName(nameFile)) {
										img = JPEGFactory.createFromStream(document, inputStreamFile);
									}
									// PNG
									else if (MethodUtils.isPngFileName(nameFile)) {
										img = LosslessFactory.createFromImage(document, ImageIO.read(inputStreamFile));
									}

									// calcul de la largeur et hauteur de l'image
									Float imgWidth = (float) img.getWidth();
									Float imgHeight = (float) img.getHeight();

									// calcul de la largeur et hauteur de la page moins les deux marges
									Float a4Width = PAGE_SIZE_A4.getWidth() - 2 * ConstanteUtils.DOSSIER_MARGIN;
									Float a4Height = PAGE_SIZE_A4.getHeight() - ConstanteUtils.DOSSIER_MARGIN
											- marginTop;

									// calcul du coef à appliquer si l'image est trop grande
									Float coef = 1.0f;
									if (imgWidth > a4Width) {
										coef = a4Width / imgWidth;
										imgWidth = imgWidth * coef;
										imgHeight = imgHeight * coef;
									}

									// si la hauteur est toujours trop grande malgres le coef de largeur, on caclul
									// le nouveau coef
									if (imgHeight > a4Height) {
										coef = a4Height / imgHeight;
										imgWidth = imgWidth * coef;
										imgHeight = imgHeight * coef;
									}

									// ecriture de l'image
									contentStream.drawImage(img, ConstanteUtils.DOSSIER_MARGIN, PAGE_SIZE_A4.getHeight() - imgHeight - marginTop, imgWidth, imgHeight);
									// il faut d'abord fermer le flux
									MethodUtils.closeRessource(contentStream);
									document.save(baosImg);

									/* Creation du flux */
									bis = baosImg.getInputStream();

									/* Ajout de la page au document */
									ut.addSource(bis);
								} catch (Exception e1) {
									errorImg = true;
								} finally {
									/* Nettoyage des ressources */
									MethodUtils.closeRessource(document);
									MethodUtils.closeRessource(bis);
									MethodUtils.closeRessource(baosImg);
								}
							}
						}
					} catch (Exception e1) {
						errorEncoding = true;
					}
				}
				;
			}
			if (errorEncoding) {
				Notification.show(applicationContext.getMessage("candidature.download.encoding.pj", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			} else if (errorImg) {
				Notification.show(applicationContext.getMessage("candidature.download.error.img", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			ut.setDestinationFileName(fileName);
			ut.setDestinationStream(out);
			ut.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
			is = signaturePdfManager.signPdf(out);
			return new OnDemandFile(fileName, is);
		} catch (Exception e) {
			try {
				out = new ByteArrayInOutStream();
				PDFMergerUtility ut = new PDFMergerUtility();

				ut.addSource(generateDossier(candidature, listePresentation, listeDatePresentation, adresse, listePj, listeForm));
				ut.setDestinationFileName(fileName);
				ut.setDestinationStream(out);
				ut.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
				is = signaturePdfManager.signPdf(out);
				Notification.show(applicationContext.getMessage("candidature.download.error.pj", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return new OnDemandFile(fileName, is);
			} catch (Exception e2) {
				Notification.show(applicationContext.getMessage("candidature.download.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				logger.error("erreur a la génération du dossier", e2);
				return null;
			}
		} finally {
			// fermeture des fichiers
			MethodUtils.closeRessource(is);
			MethodUtils.closeRessource(out);
			MethodUtils.closeRessource(bisDossier);

			/* besoin de fermer les pdf apres coup */
			listeInputStreamToClose.forEach(inputStreamFile -> {
				MethodUtils.closeRessource(inputStreamFile);

			});
			listeInputStreamToClose.clear();
		}
	}

	/** @param textHeader
	 * @param font
	 * @param PAGE_SIZE_A4
	 * @param contentStream
	 * @return ajoute un header a la piece
	 * @throws IOException
	 */
	private Float addHeaderPJ(final String textHeader, final PDFont font, final PDRectangle PAGE_SIZE_A4,
			final PDPageContentStream contentStream) throws IOException {
		Float marginTop = 0f;
		// si font Ok, on ajoute le text
		if (font != null && ConstanteUtils.DOSSIER_ADD_HEADER_IMG) {

			// calcul de la largeur et hauteur du txt
			Float titleWidth = font.getStringWidth(textHeader) / 1000 * ConstanteUtils.DOSSIER_FONT_SIZE;
			Float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000
					* ConstanteUtils.DOSSIER_FONT_SIZE;

			// calcul de la marge du haut : hauteur du text + marge
			marginTop = titleHeight + ConstanteUtils.DOSSIER_MARGIN;

			// calcul de la position du text
			Float xText = (PAGE_SIZE_A4.getWidth() - 2 * ConstanteUtils.DOSSIER_MARGIN - titleWidth) / 2;
			Float yText = PAGE_SIZE_A4.getHeight() - marginTop;

			// ecriture du text
			contentStream.beginText();
			contentStream.setFont(PDType1Font.HELVETICA_BOLD, ConstanteUtils.DOSSIER_FONT_SIZE);
			contentStream.newLineAtOffset(xText, yText);
			contentStream.showText(textHeader);
			contentStream.endText();
		}
		return marginTop;
	}

	/** Renvoi les candidatures non annulées d'un candidat
	 *
	 * @param candidat
	 * @return les candidatures non annulées d'un candidat */
	public List<Candidature> getCandidatures(final Candidat candidat) {
		List<Candidature> liste = new ArrayList<>();
		Authentication auth = userController.getCurrentAuthentication();
		if (userController.getSecurityUserCandidat(auth) != null) {
			liste.addAll(candidat.getCandidatures().stream().filter(e -> e.getDatAnnulCand() == null).collect(Collectors.toList()));
		} else {
			SecurityCentreCandidature scc = userController.getCentreCandidature(auth);

			if (scc != null) {
				if (scc.getIsAdmin()) {
					liste.addAll(candidat.getCandidatures().stream().filter(cand -> !liste.contains(cand) && cand.getDatAnnulCand() == null).collect(Collectors.toList()));
				} else {
					if (scc.getIsGestAllCommission()) {
						liste.addAll(candidat.getCandidatures().stream().filter(cand -> !liste.contains(cand) && cand.getDatAnnulCand() == null
								&& cand.getFormation().getCommission().getCentreCandidature().getIdCtrCand().equals(scc.getIdCtrCand())).collect(Collectors.toList()));
					} else {
						liste.addAll(candidat.getCandidatures().stream().filter(cand -> !liste.contains(cand) && cand.getDatAnnulCand() == null
								&& MethodUtils.isIdInListId(cand.getFormation().getCommission().getIdComm(), scc.getListeIdCommission())).collect(Collectors.toList()));
					}

				}
			}
			SecurityCommission sc = userController.getCommission(auth);
			if (sc != null) {
				if (sc.getIsAdmin()) {
					liste.addAll(candidat.getCandidatures().stream().filter(cand -> !liste.contains(cand) && cand.getDatAnnulCand() == null).collect(Collectors.toList()));
				} else {
					liste.addAll(candidat.getCandidatures().stream().filter(cand -> !liste.contains(cand) && cand.getDatAnnulCand() == null
							&& cand.getFormation().getCommission().getIdComm().equals(sc.getIdComm())).collect(Collectors.toList()));
				}

			}
		}
		liste.forEach(e -> e.setLastTypeDecision(getLastTypeDecisionCandidature(e)));
		return liste;
	}

	/** Envoi un mail de changement de code OPI
	 *
	 * @param candidat
	 * @param newCode
	 * @param libFormationImpactee
	 */
	public void sendMailChangeCodeOpi(final Candidat candidat, final String newCode,
			final String libFormationImpactee) {
		String locale = candidat.getLangue().getCodLangue();
		CandidatMailBean candidatMailBean = mailController.getCandidatMailBean(candidat, locale);
		ChangeCodOpiMailBean mailBean = new ChangeCodOpiMailBean(newCode, libFormationImpactee, candidatMailBean);
		mailController.sendMailByCod(candidat.getCompteMinima().getMailPersoCptMin(), NomenclatureUtils.MAIL_CANDIDATURE_MODIF_COD_OPI, mailBean, null, candidat.getLangue().getCodLangue());
	}

	/** @param candidat
	 * @return la liste des opi d'un candidat */
	public List<Opi> getListOpiByCandidat(final Candidat candidat, final Boolean isBatch) {
		/*
		 * Si OPI immediate :
		 * On prend :
		 * OPI sans OPIPJ
		 * OPI avec une date de déversement deja passée
		 * OPI desisté
		 * Si BATCH, on prend tous les OPI
		 */
		List<Opi> listOpi = opiRepository.findByCandidatureCandidatIdCandidat(candidat.getIdCandidat());
		if (isBatch) {
			return listOpi;
		}

		/* Mode synchrone */
		List<Opi> listOpiToRet = new ArrayList<>();
		listOpi.forEach(opi -> {
			/*
			 * OPI avec avec une date de déversement deja passée
			 * OPI sans OPIPJ
			 * OPI desisté
			 */
			if (opi.getDatPassageOpi() != null || (opi.getCandidature().getTemAcceptCand() != null && !opi.getCandidature().getTemAcceptCand())
					|| candidaturePieceController.getPJToDeverse(opi.getCandidature()).size() == 0) {
				listOpiToRet.add(opi);
			}
		});
		return listOpiToRet;
	}

	/** Traite la liste des OPI
	 *
	 * @param candidat
	 * @param listeOpi
	 * @param isCodOpiIntEpoFromEcandidat
	 * @param codOpiIntEpo
	 */
	public void traiteListOpiCandidat(final Candidat candidat, final List<Opi> listeOpi,
			final Boolean isCodOpiIntEpoFromEcandidat, final String codOpiIntEpo, final String logComp) {
		logger.debug("traiteListOpiCandidat " + codOpiIntEpo + " fromEcv2 = " + isCodOpiIntEpoFromEcandidat + logComp
				+ " - " + listeOpi.size() + " opi");
		String libFormation = "";
		for (Opi opi : listeOpi) {
			/* On enregistre la date de passage */
			opi.setDatPassageOpi(LocalDateTime.now());
			opi.setCodOpi(codOpiIntEpo);
			opiRepository.save(opi);
			if (!isCodOpiIntEpoFromEcandidat) {
				libFormation = libFormation + "<li>" + opi.getCandidature().getFormation().getLibForm() + "</li>";
			}
		}
		/*
		 * Si le code OPI est different de celui de eCandidat, on envoi un mail au
		 * candidat
		 */
		if (!isCodOpiIntEpoFromEcandidat && libFormation != null && !libFormation.equals("")) {
			logger.debug("Envoi du mail de modification" + logComp);
			sendMailChangeCodeOpi(candidat, codOpiIntEpo, "<ul>" + libFormation + "</ul>");
		}
	}

	/** Traite les PJ
	 *
	 * @param listeOpi
	 * @param codOpiIntEpo
	 * @param logComp
	 * @param isBatch
	 */
	public void traiteListOpiPjCandidat(final List<Opi> listeOpi, final String codOpiIntEpo, final long codIndOpi, final String logComp, final Boolean isBatch) {
		logger.debug("traiteListOpiPjCandidat " + codOpiIntEpo + logComp + " - " + listeOpi.size() + " opi");
		for (Opi opi : listeOpi) {
			/* Traitement des PJ OPI si dématerialisation */
			if (isCandidatureDematerialise(opi.getCandidature()) && parametreController.getIsUtiliseOpiPJ()) {
				logger.debug("Deversement PJ OPI dans table eCandidat" + logComp);
				candidaturePieceController.deversePjOpi(opi, codOpiIntEpo, codIndOpi);
			}
		}
		if (isBatch) {
			/* Deversement dans Apogée */
			List<PjOpi> listePjOpi = pjOpiRepository.findByIdCodOpiAndDatDeversementIsNull(codOpiIntEpo);
			logger.debug("Tentative deversement PJ OPI WS Apogée " + codOpiIntEpo + logComp + " - " + listePjOpi.size() + " pjOPI");
			listePjOpi.forEach(pjOpi -> {
				candidatureGestionController.deversePjOpi(pjOpi);
			});
		}
	}

	/** Traite la liste des OPI desistement
	 *
	 * @param candidat
	 * @param listeOpiDesistementATraiter
	 * @param logComp
	 */
	public void traiteListOpiDesistCandidat(final Candidat candidat, final List<Opi> listeOpiDesistementATraiter,
			final String logComp) {
		logger.debug("traiteListOpiDesistCandidat " + logComp + " - " + listeOpiDesistementATraiter.size() + " opi");
		for (Opi opi : listeOpiDesistementATraiter) {
			/* On enregistre la date de passage */
			opi.setDatPassageOpi(LocalDateTime.now());
			opiRepository.save(opi);
		}
	}
}
