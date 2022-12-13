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

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.FichierFiabilisation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCand;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCandPK;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCandPK;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Question;
import fr.univlorraine.ecandidat.entities.ecandidat.QuestionCand;
import fr.univlorraine.ecandidat.entities.ecandidat.QuestionCandPK;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.FichierFiabilisationRepository;
import fr.univlorraine.ecandidat.repositories.FichierRepository;
import fr.univlorraine.ecandidat.repositories.FormulaireCandRepository;
import fr.univlorraine.ecandidat.repositories.FormulaireCandidatRepository;
import fr.univlorraine.ecandidat.repositories.PjCandRepository;
import fr.univlorraine.ecandidat.repositories.QuestionCandRepository;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.mail.FormulaireMailBean;
import fr.univlorraine.ecandidat.utils.bean.presentation.FormulairePresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.QuestionPresentation;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.views.windows.CandidatQuestionWindow;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandActionPjWindow;
import fr.univlorraine.ecandidat.views.windows.InfoWindow;
import fr.univlorraine.ecandidat.views.windows.UploadWindow;

/**
 * Gestion des pièces
 * @author Kevin Hergalant
 */
@Component
public class CandidaturePieceController {
	private final Logger logger = LoggerFactory.getLogger(CandidaturePieceController.class);
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient PieceJustifController pieceJustifController;
	@Resource
	private transient FormulaireController formulaireController;
	@Resource
	private transient QuestionController questionController;
	@Resource
	private transient LockCandidatController lockCandidatController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient CandidatPieceController candidatPieceController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient CandidatureRepository candidatureRepository;
	@Resource
	private transient PjCandRepository pjCandRepository;
	@Resource
	private transient QuestionCandRepository questionCandRepository;
	@Resource
	private transient FichierRepository fichierRepository;
	@Resource
	private transient FichierFiabilisationRepository fichierFiabilisationRepository;
	@Resource
	private transient FormulaireCandRepository formulaireCandRepository;
	@Resource
	private transient FormulaireCandidatRepository formulaireCandidatRepository;

	@Resource
	private transient DateTimeFormatter formatterDateTime;

	/**
	 * @param  candidature
	 * @return             la liste des pj d'une candidature
	 */
	public List<PjPresentation> getPjCandidature(final Candidature candidature) {
		final List<PjPresentation> liste = new ArrayList<>();
		final TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
		final TypeStatutPiece statutValide = tableRefController.getTypeStatutPieceValide();
		final Boolean isDemat = candidature.getFormation().getTemDematForm();

		int i = 1;
		for (final PieceJustif e : pieceJustifController.getPjForCandidature(candidature, true)) {
			liste.add(getPjPresentation(e, candidature, statutAtt, statutValide, i, isDemat));
			i++;
		}

		return liste;
	}

	/**
	 * @param  pj
	 * @param  candidature
	 * @param  statutAtt
	 * @param  statutValide
	 * @param  isDemat
	 * @return              une piece de presentation
	 */
	private PjPresentation getPjPresentation(final PieceJustif pj,
		final Candidature candidature,
		final TypeStatutPiece statutAtt,
		final TypeStatutPiece statutValide,
		final Integer order,
		final Boolean isDemat) {
		final String libPj = i18nController.getI18nTraduction(pj.getI18nLibPj());
		final PjCand pjCand = getPjCandFromList(pj, candidature, isDemat);

		String libStatut = null;
		String codStatut = null;
		String commentaire = null;
		LocalDateTime datModification = null;
		Integer idCandidature = null;
		PjCandidat pjCandidatFromApogee = null;
		String userMod = null;

		Fichier fichier = null;
		if (pjCand != null) {
			fichier = pjCand.getFichier();
			if (pjCand.getTypeStatutPiece() != null) {
				libStatut = i18nController.getI18nTraduction(pjCand.getTypeStatutPiece().getI18nLibTypStatutPiece());
				codStatut = pjCand.getTypeStatutPiece().getCodTypStatutPiece();
			}
			commentaire = pjCand.getCommentPjCand();
			datModification = pjCand.getDatModPjCand();
			idCandidature = pjCand.getCandidature().getIdCand();
			userMod = getLibModStatut(pjCand.getUserModStatutPjCand(), pjCand.getDatModStatutPjCand());
		} else {
			if (isDemat) {
				pjCandidatFromApogee = candidatPieceController.getPjCandidat(pj.getCodApoPj(),
					candidature.getCandidat());
				if (pjCandidatFromApogee != null) {
					fichier = new Fichier();
					fichier.setFileFichier(pjCandidatFromApogee.getNomFicPjCandidat());
					fichier.setNomFichier(pjCandidatFromApogee.getNomFicPjCandidat());
					commentaire = applicationContext.getMessage("file.from.another.system",
						null,
						UI.getCurrent().getLocale());
					libStatut = i18nController.getI18nTraduction(statutValide.getI18nLibTypStatutPiece());
					codStatut = statutValide.getCodTypStatutPiece();
				} else {
					libStatut = i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece());
					codStatut = statutAtt.getCodTypStatutPiece();
					idCandidature = candidature.getIdCand();
				}
			} else {
				libStatut = i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece());
				codStatut = statutAtt.getCodTypStatutPiece();
				idCandidature = candidature.getIdCand();
			}
		}
		final Boolean commun = pj.getTemCommunPj() && !pj.getTemUnicitePj();
		return new PjPresentation(pj,
			libPj,
			fichier,
			codStatut,
			libStatut,
			commentaire,
			pj.getTemConditionnelPj(),
			commun,
			datModification,
			idCandidature,
			order,
			pjCandidatFromApogee,
			userMod);
	}

	/**
	 * @param  piece
	 * @param  isDemat
	 * @param  listPjCand
	 * @return            Renvoi un fichier si il existe
	 */
	public PjCand getPjCandFromList(final PieceJustif piece, final Candidature candidature, final Boolean isDemat) {
		final Optional<PjCand> pjCandOpt = candidature.getPjCands()
			.stream()
			.filter(e -> e.getId().getIdPj().equals(piece.getIdPj()))
			.findAny();
		if (pjCandOpt.isPresent()) {
			return pjCandOpt.get();
		}
		if (!isDemat) {
			return null;
		}
		// si on ne trouve pas et que la pièce est commune, on va chercher dans les
		// autres candidatures
		if (piece.getTemCommunPj() && !piece.getTemUnicitePj()) {
			final List<PjCand> liste = pjCandRepository
				.findByIdIdPjAndCandidatureCandidatIdCandidatAndCandidatureFormationTemDematFormOrderByDatModPjCandDesc(
					piece.getIdPj(),
					candidature.getCandidat().getIdCandidat(),
					true);
			if (liste.size() > 0) {
				return liste.get(0);
			}
		}
		return null;
	}

	/**
	 * @param  candidature
	 * @return             la liste des questions d'une candidature
	 */
	public List<QuestionPresentation> getQuestionCandidature(final Candidature candidature) {
		final List<QuestionPresentation> liste = new ArrayList<>();
		final TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
		final TypeStatutPiece statutValide = tableRefController.getTypeStatutPieceValide();

		int i = 1;
		for (final Question e : questionController.getQuestionForCandidature(candidature, true)) {
			liste.add(getQuestionPresentation(e, candidature, statutAtt, statutValide, i));
			i++;
		}

		return liste;
	}

	/**
	 * @param  question
	 * @param  candidature
	 * @param  statutAtt
	 * @param  statutValide
	 * @return              une question de presentation
	 */
	private QuestionPresentation getQuestionPresentation(final Question question,
		final Candidature candidature,
		final TypeStatutPiece statutAtt,
		final TypeStatutPiece statutValide,
		final Integer order) {
		final String libQuestion = i18nController.getI18nTraduction(question.getI18nLibQuestion());
		final QuestionCand questionCand = getQuestionCandFromList(question, candidature);

		String libStatut = null;
		String codStatut = null;
		String reponse = null;
		LocalDateTime datModification = null;
		Integer idCandidature = null;

		if (questionCand != null) {
			if (questionCand.getTypeStatutPiece() != null) {
				libStatut = i18nController
					.getI18nTraduction(questionCand.getTypeStatutPiece().getI18nLibTypStatutPiece());
				codStatut = questionCand.getTypeStatutPiece().getCodTypStatutPiece();
			}
			reponse = questionCand.getReponseQuestionCand();
			datModification = questionCand.getDatModQuestionCand();
			idCandidature = questionCand.getCandidature().getIdCand();
		} else {
			libStatut = i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece());
			codStatut = statutAtt.getCodTypStatutPiece();
			idCandidature = candidature.getIdCand();
		}
		final Boolean commun = question.getTemCommunQuestion() && !question.getTemUniciteQuestion();
		return new QuestionPresentation(question,
			libQuestion,
			codStatut,
			libStatut,
			reponse,
			question.getTemConditionnelQuestion(),
			commun,
			datModification,
			idCandidature,
			order);
	}

	/**
	 * @param  question
	 * @param  isDemat
	 * @param  listQuestionCand
	 * @return                  Renvoi un fichier si il existe
	 */
	public QuestionCand getQuestionCandFromList(final Question question, final Candidature candidature) {
		final Optional<QuestionCand> questionCandOpt = candidature.getQuestionCands()
			.stream()
			.filter(e -> e.getId().getIdQuestion().equals(question.getIdQuestion()))
			.findAny();
		if (questionCandOpt.isPresent()) {
			return questionCandOpt.get();
		}
		// si on ne trouve pas et que la pièce est commune, on va chercher dans les
		// autres candidatures
		if (question.getTemCommunQuestion() && !question.getTemUniciteQuestion()) {
			final List<QuestionCand> liste = questionCandRepository
				.findByIdIdQuestionAndCandidatureCandidatIdCandidatOrderByDatModQuestionCandDesc(
					question.getIdQuestion(),
					candidature.getCandidat().getIdCandidat());
			if (liste.size() > 0) {
				return liste.get(0);
			}
		}
		return null;
	}

	/**
	 * @param  candidature
	 * @return             la liste des formulaires d'une candidature
	 */
	public List<FormulairePresentation> getFormulaireCandidature(final Candidature candidature) {
		final String numDossier = candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin();
		final List<FormulairePresentation> liste = new ArrayList<>();
		final TypeStatutPiece statutAT = tableRefController.getTypeStatutPieceAttente();
		final TypeStatutPiece statutNC = tableRefController.getTypeStatutPieceNonConcerne();
		final TypeStatutPiece statutTR = tableRefController.getTypeStatutPieceTransmis();

		final List<FormulaireCand> listeFormulaireCand = candidature.getFormulaireCands();
		final List<FormulaireCandidature> listeFormulaireCandidature = candidature.getFormulaireCandidatures();
		final List<FormulaireCandidat> listeFormulaireCandidat = candidature.getCandidat().getFormulaireCandidats();

		formulaireController.getFormulaireForCandidature(candidature).forEach(e -> {
			final String libForm = i18nController.getI18nTraduction(e.getI18nLibFormulaire());
			String urlForm = i18nController.getI18nTraduction(e.getI18nUrlFormulaire());

			/* Possibilité d'ajout du numdossier dans l'url sous la forme ${numDossier} */
			if (urlForm != null) {
				urlForm = urlForm.replaceAll(ConstanteUtils.VAR_REGEX_FORM_NUM_DOSSIER, numDossier);
				urlForm = urlForm.replaceAll(ConstanteUtils.VAR_REGEX_FORM_NUM_DOSSIER_OLD, numDossier);
				urlForm = urlForm.replaceAll(ConstanteUtils.VAR_REGEX_FORM_ID_CANDIDATURE,
					String.valueOf(candidature.getIdCand()));
			}

			String libStatut = null;
			String codStatut = null;
			String reponses = null;

			/* On recherche d'abord les réponses */
			final FormulaireCandidature formulaireCandidature = getFormulaireCandidatureFromList(e,
				listeFormulaireCandidature);
			final FormulaireCandidat formulaireCandidat = getFormulaireCandidatFromList(e, listeFormulaireCandidat);
			if (formulaireCandidature != null) {
				codStatut = statutTR.getCodTypStatutPiece();
				libStatut = i18nController.getI18nTraduction(statutTR.getI18nLibTypStatutPiece());
				reponses = formulaireCandidature.getReponsesFormulaireCand();
			} else if (formulaireCandidat != null) {
				codStatut = statutTR.getCodTypStatutPiece();
				libStatut = i18nController.getI18nTraduction(statutTR.getI18nLibTypStatutPiece());
				reponses = formulaireCandidat.getReponsesFormulaireCandidat();
			} else {
				/* Si pas de réponse on recherche une ligne dans formulaireCand pour vérifier
				 * qu'il est concerné */
				final FormulaireCand formulaireCand = getFormulaireCandFromList(e, listeFormulaireCand);
				if (formulaireCand != null) {
					codStatut = statutNC.getCodTypStatutPiece();
					libStatut = i18nController.getI18nTraduction(statutNC.getI18nLibTypStatutPiece());
				} else {
					/* Ni de réponse, ni non concerné --> en attente */
					codStatut = statutAT.getCodTypStatutPiece();
					libStatut = i18nController.getI18nTraduction(statutAT.getI18nLibTypStatutPiece());
				}
			}
			liste.add(new FormulairePresentation(e,
				libForm,
				urlForm,
				codStatut,
				libStatut,
				e.getTemConditionnelFormulaire(),
				reponses));
		});

		return liste;
	}

	/**
	 * @param  formulaire
	 * @param  listFormulaireCand
	 * @return                    recherche une reponse non concerné a un formulaire
	 */
	private FormulaireCand getFormulaireCandFromList(final Formulaire formulaire,
		final List<FormulaireCand> listFormulaireCand) {
		final Optional<FormulaireCand> formulaireCandOpt = listFormulaireCand.stream()
			.filter(e -> e.getId().getIdFormulaire().equals(formulaire.getIdFormulaire()))
			.findAny();
		if (formulaireCandOpt.isPresent()) {
			return formulaireCandOpt.get();
		}
		return null;
	}

	/**
	 * @param  formulaire
	 * @param  listFormulaireCandidat
	 * @param  idCandidat
	 * @return                        recherche une reponse a formulaire
	 */
	private FormulaireCandidature getFormulaireCandidatureFromList(final Formulaire formulaire,
		final List<FormulaireCandidature> listFormulaireCandidature) {
		final Optional<FormulaireCandidature> formulaireCandOpt = listFormulaireCandidature.stream()
			.filter(e -> e.getId().getIdFormulaireLimesurvey().equals(formulaire.getIdFormulaireLimesurvey()))
			.sorted((e1, e2) -> (e2.getDatReponseFormulaireCand().compareTo(e1.getDatReponseFormulaireCand())))
			.findFirst();
		if (formulaireCandOpt.isPresent()) {
			return formulaireCandOpt.get();
		}
		return null;
	}

	/**
	 * @param  formulaire
	 * @param  listFormulaireCandidat
	 * @param  idCandidat
	 * @return                        recherche une reponse a formulaire
	 */
	private FormulaireCandidat getFormulaireCandidatFromList(final Formulaire formulaire,
		final List<FormulaireCandidat> listFormulaireCandidat) {
		final Optional<FormulaireCandidat> formulaireCandOpt = listFormulaireCandidat.stream()
			.filter(e -> e.getId().getIdFormulaireLimesurvey().equals(formulaire.getIdFormulaireLimesurvey()))
			.sorted((e1,
				e2) -> (e2.getDatReponseFormulaireCandidat().compareTo(e1.getDatReponseFormulaireCandidat())))
			.findFirst();
		if (formulaireCandOpt.isPresent()) {
			return formulaireCandOpt.get();
		}
		return null;
	}

	/**
	 * relance des formulaires
	 * @param listeToRelance
	 * @param candidature
	 */
	public void relanceFormulaires(final List<FormulairePresentation> listeToRelance, final Candidature candidature) {
		final ConfirmWindow confirmWindow = new ConfirmWindow(
			applicationContext.getMessage("formulaireComp.relance.window.msg",
				new String[]
				{ String.valueOf(listeToRelance.size()) },
				UI.getCurrent().getLocale()),
			applicationContext.getMessage("formulaireComp.relance.window", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(event -> {
			final String codLangue = candidature.getCandidat().getLangue().getCodLangue();
			final String numDossier = candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin();

			final FormulaireMailBean mailBean = new FormulaireMailBean();
			listeToRelance.forEach(e -> {
				final String libForm = i18nController.getI18nTraduction(e.getFormulaire().getI18nLibFormulaire(),
					codLangue);
				String urlForm = i18nController.getI18nTraduction(e.getFormulaire().getI18nUrlFormulaire(), codLangue);
				/* Possibilité d'ajout du numdossier dans l'url sous la forme ${numDossier} */
				if (urlForm != null) {
					urlForm = urlForm.replaceAll(ConstanteUtils.VAR_REGEX_FORM_NUM_DOSSIER, numDossier);
					urlForm = urlForm.replaceAll(ConstanteUtils.VAR_REGEX_FORM_NUM_DOSSIER_OLD, numDossier);
					urlForm = urlForm.replaceAll(ConstanteUtils.VAR_REGEX_FORM_ID_CANDIDATURE,
						String.valueOf(candidature.getIdCand()));
				}
				mailBean.addFormulaire(libForm, urlForm);
			});

			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),
				NomenclatureUtils.MAIL_CANDIDATURE_RELANCE_FORMULAIRE,
				mailBean,
				candidature,
				codLangue);
			Notification.show(
				applicationContext.getMessage("formulaireComp.relance.notif", null, UI.getCurrent().getLocale()),
				Type.WARNING_MESSAGE);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * @param  codStatut
	 * @param  notification
	 * @return              true si le statut de dossier de la candidatures permet de transmettre
	 *                      le dossier
	 */
	public Boolean isOkToTransmettreCandidatureStatutDossier(final String codStatut, final Boolean notification) {
		if (codStatut.equals(NomenclatureUtils.TYPE_STATUT_ATT)
			|| (codStatut.equals(NomenclatureUtils.TYPE_STATUT_INC))) {
			return true;
		} else {
			if (notification) {
				Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.statut",
					null,
					UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			return false;
		}
	}

	/**
	 * @param  listePj
	 * @param  notification
	 * @return              true si les pieces de la candidatures permettent de transmettre le
	 *                      dossier
	 */
	public Boolean isOkToTransmettreCandidatureStatutPiece(final List<PjPresentation> listePj,
		final Boolean notification) {
		for (final PjPresentation pj : listePj) {
			if (pj.getCodStatut() == null) {
				if (notification) {
					Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.pj.attente",
						new Object[]
						{ pj.getLibPj() },
						UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
				return false;
			} else if (pj.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE)) {
				if (notification) {
					Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.pj.attente",
						new Object[]
						{ pj.getLibPj() },
						UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
				return false;
			} else if (pj.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_REFUSE)) {
				if (notification) {
					Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.pj.refus",
						new Object[]
						{ pj.getLibPj() },
						UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * @param  listeForm
	 * @param  notification
	 * @return              true si les formulaires de la candidatures permettent de transmettre
	 *                      le dossier
	 */
	public Boolean isOkToTransmettreCandidatureFormulaire(final List<FormulairePresentation> listeForm,
		final Boolean notification) {
		if (!parametreController.getIsBlocTransForm()) {
			return true;
		}
		for (final FormulairePresentation form : listeForm) {
			if (form.getCodStatut() == null) {
				if (notification) {
					Notification.show(
						applicationContext.getMessage("candidature.validPJ.erreur.form",
							new Object[]
							{ form.getLibFormulaire() },
							UI.getCurrent().getLocale()),
						Type.WARNING_MESSAGE);
				}
				return false;
			} else if (form.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE)) {
				if (notification) {
					Notification.show(
						applicationContext.getMessage("candidature.validPJ.erreur.form",
							new Object[]
							{ form.getLibFormulaire() },
							UI.getCurrent().getLocale()),
						Type.WARNING_MESSAGE);
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * @param  listeForm
	 * @param  notification
	 * @return              true si les formulaires de la candidatures permettent de transmettre
	 *                      le dossier
	 */
	public Boolean isOkToTransmettreCandidatureQuestion(final List<QuestionPresentation> listeQuestion,
		final Boolean notification) {
		for (final QuestionPresentation question : listeQuestion) {
			if (question.getCodStatut() == null) {
				if (notification) {
					Notification.show(
						applicationContext.getMessage("candidature.validPJ.erreur.question",
							new Object[]
							{ question.getLibQuestion() },
							UI.getCurrent().getLocale()),
						Type.WARNING_MESSAGE);
				}
				return false;
			} else if (question.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE)) {
				if (notification) {
					Notification.show(
						applicationContext.getMessage("candidature.validPJ.erreur.question",
							new Object[]
							{ question.getLibQuestion() },
							UI.getCurrent().getLocale()),
						Type.WARNING_MESSAGE);
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * @param  candidature
	 * @param  listePj
	 * @param  listForm
	 * @param  listener
	 * @param  notification
	 * @return              vérifie que l'etat de la candidature permet de transmettre le dossier
	 */
	private Boolean isOkToTransmettreCandidature(final Candidature candidature,
		final List<PjPresentation> listePj,
		final List<FormulairePresentation> listForm,
		final List<QuestionPresentation> listeQuestion,
		final CandidatureListener listener,
		final Boolean notification) {
		return (isOkToTransmettreCandidatureStatutDossier(candidature.getTypeStatut().getCodTypStatut(), notification)
			&& isOkToTransmettreCandidatureStatutPiece(listePj, notification)
			&& isOkToTransmettreCandidatureFormulaire(listForm, notification)
			&& isOkToTransmettreCandidatureQuestion(listeQuestion, notification));
	}

	/**
	 * Transmet le dossier apres le click sur le bouton transmettre
	 * @param candidature
	 * @param listePj
	 * @param listFOrm
	 * @param listener
	 */
	public void transmettreCandidatureAfterClick(final Candidature candidature,
		final List<PjPresentation> listePj,
		final List<FormulairePresentation> listForm,
		final List<QuestionPresentation> listeQuestion,
		final CandidatureListener listener) {
		if (isOkToTransmettreCandidature(candidature, listePj, listForm, listeQuestion, listener, true)) {
			transmettreCandidature(candidature,
				listener,
				applicationContext
					.getMessage("candidature.validPJ.window.confirm", null, UI.getCurrent().getLocale()));
		}
	}

	/**
	 * Transmet le dossier apres un depot de pièce
	 * @param candidature
	 * @param listePj
	 * @param listForm
	 * @param listener
	 * @param dateLimiteRetour
	 */
	public void transmettreCandidatureAfterDepot(final Candidature candidature,
		final List<PjPresentation> listePj,
		final List<FormulairePresentation> listForm,
		final List<QuestionPresentation> listeQuestion,
		final CandidatureListener listener,
		final String dateLimiteRetour) {
		if (isOkToTransmettreCandidature(candidature, listePj, listForm, listeQuestion, listener, false)) {
			UI.getCurrent()
				.addWindow(new InfoWindow(
					applicationContext.getMessage("candidature.validPJ.window.info.tite",
						null,
						UI.getCurrent().getLocale()),
					applicationContext.getMessage("candidature.validPJ.window.info.afteraction",
						new Object[]
						{ dateLimiteRetour },
						UI.getCurrent().getLocale()),
					425,
					null));
			// transmettreCandidature(candidature, listener,
			// applicationContext.getMessage("candidature.validPJ.window.confirm.afteraction",
			// null, UI.getCurrent().getLocale()));
		}
	}

	/**
	 * Transmet la canidature
	 * @param candidature
	 * @param listener
	 * @param message
	 */
	private void transmettreCandidature(final Candidature candidature,
		final CandidatureListener listener,
		final String message) {
		final ConfirmWindow confirmWindow = new ConfirmWindow(message,
			applicationContext
				.getMessage("candidature.validPJ.window.confirmTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(event -> {
			candidature.setTypeStatut(tableRefController.getTypeStatutReceptionne());
			candidature.setDatReceptDossierCand(LocalDate.now());
			candidature.setDatModTypStatutCand(LocalDateTime.now());
			candidature.setDatTransDossierCand(LocalDateTime.now());

			final Candidature candidatureSave = candidatureRepository.save(candidature);

			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),
				NomenclatureUtils.MAIL_STATUT_RE,
				null,
				candidature,
				candidature.getCandidat().getLangue().getCodLangue());

			if (candidature.getFormation().getCommission().getTemAlertTransComm()) {
				mailController.sendMailByCod(candidature.getFormation().getCommission().getMailAlert(),
					NomenclatureUtils.MAIL_COMMISSION_ALERT_TRANSMISSION,
					null,
					candidature,
					null);
			}

			listener.transmissionDossier(candidatureSave);

			Notification.show(
				applicationContext.getMessage("candidature.validPJ.success", null, UI.getCurrent().getLocale()),
				Type.TRAY_NOTIFICATION);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Methode inutilisée, controle dans le batch
	 * @param  pieceJustif
	 * @param  candidature
	 * @return             true si une pièce existe alors qu'on veut l'ajouter
	 */
	/* public Boolean controlPjAdd(PjPresentation pieceJustif, Candidature
	 * candidature, CandidatureListener listener){ if
	 * (!candidature.getFormation().getTemDematForm()){ return false; } if
	 * (pieceJustif.getPjCandidatFromApogee() != null){ return false; } Boolean
	 * needToReload = false; //Le fichier de la pièce devrai être a null, sinon ca
	 * veut dire que le listener d'ajout de pièce n'a pas fonctionné if
	 * (pieceJustif.getFilePj()!=null){ logger.
	 * debug("Ajout PJ en erreur, rechargement demandé, la pièce devrait être null.."
	 * ); needToReload = true; }else if (pieceJustif.getPJCommune()){ //Si commune,
	 * on recherche la pièce dans toutes candidatures pour trouver si un fichier a
	 * été déposé List<PjCand> listePjCand = pjCandRepository.
	 * findByIdIdPjAndCandidatureCandidatIdCandidatAndCandidatureFormationTemDematFormOrderByDatModPjCandDesc
	 * (pieceJustif.getPieceJustif().getIdPj(),
	 * candidature.getCandidat().getIdCandidat(), true); if (listePjCand!=null &&
	 * listePjCand.size()>0 &&
	 * listePjCand.stream().filter(e->e.getFichier()!=null).count()>0){
	 * logger.debug("Ajout PJ en erreur, piece commune nok"); needToReload = true; }
	 * }else{ //Sinon, on recherche dans la candidature PjCandPK pk = new
	 * PjCandPK(pieceJustif.getPieceJustif().getIdPj(),candidature.getIdCand());
	 * PjCand pjCand = pjCandRepository.findOne(pk); if (pjCand!=null &&
	 * pjCand.getFichier()!=null){
	 * logger.debug("Ajout PJ en erreur, piece non commune nok"); needToReload =
	 * true; } } if (needToReload){
	 * logger.debug("Ajout PJ en erreur, rechargement demandé");
	 * Notification.show(applicationContext.getMessage("pj.add.error", null,
	 * UI.getCurrent().getLocale()), Type.WARNING_MESSAGE); Candidature
	 * candidatureLoad = candidatureRepository.findOne(candidature.getIdCand());
	 * listener.reloadAllPiece(getPjCandidature(candidatureLoad), candidatureLoad);
	 * return true; } return false; } */

	/**
	 * Methode qui vérifie si la PJ a été modifiée par qqun d'autre obligatoire pour
	 * les PJ commune qui peuvent etre modifiées dans une autre candidature on se
	 * base sur la date de modification
	 * @param  pieceJustif
	 * @return             true si la PJ a été modifiée
	 */
	public Boolean isPjModified(final PjPresentation pieceJustif,
		final Candidature candidature,
		final Boolean showNotif,
		final CandidatureListener listener) {
		if (!candidature.getFormation().getTemDematForm()) {
			return false;
		}
		if (pieceJustif.getPjCandidatFromApogee() != null) {
			logger.debug("Pas de verification pièce pour les PJ d'Apogée :  " + pieceJustif);
			return false;
		}
		logger.debug("Verification pièce : " + pieceJustif);
		Boolean needToReload = false;

		if (pieceJustif.getPJCommune()) {
			final List<PjCand> listePjCand = pjCandRepository
				.findByIdIdPjAndCandidatureCandidatIdCandidatAndCandidatureFormationTemDematFormOrderByDatModPjCandDesc(
					pieceJustif.getPieceJustif().getIdPj(),
					candidature.getCandidat().getIdCandidat(),
					true);
			PjCand pjCandFind = null;
			if (listePjCand != null && listePjCand.size() > 0) {
				// on cherche d'abord en priorité si la pièce est présente sur la candidature
				final Optional<PjCand> pjCandOpt = listePjCand.stream()
					.filter(e -> e.getCandidature().getIdCand().equals(pieceJustif.getIdCandidature()))
					.findFirst();
				if (pjCandOpt.isPresent()) {
					pjCandFind = pjCandOpt.get();
				} else {
					pjCandFind = listePjCand.get(0);
				}
			}
			logger.debug("Pièces trouvées : " + listePjCand.size() + " : " + pjCandFind);

			// nouvelle pièce, si il en existe une, on recharge
			if (pieceJustif.getDatModification() == null) {
				// la piece etait vide et on en a ajouté une
				if (pjCandFind != null) {
					logger.debug("Cas no1, pièces nouvelle et pièce trouvée : " + pieceJustif.getDatModification()
						+ " - "
						+ pjCandFind);
					needToReload = true;
				}
			}
			/* ce n'est pas une nouvelle pièce, on vérifie : - qu'elle n'a pas été supprimée
			 * en route - que sa date de modif est différente */
			else {

				// piece supprimée
				if (pjCandFind == null) {
					logger.debug("Cas no2, pièces vide : " + pjCandFind);
					needToReload = true;
				}
				// piece modifiée
				// Modif avant :
				// else if ((pjCandFind== null || pjCandFind.getFichier()==null ||
				// pjCandFind.getFichier().equals(pieceJustif.getFilePj())) &&
				// !pjCandFind.getDatModPjCand().equals(pieceJustif.getDatModification())){
				else if (!pjCandFind.getDatModPjCand().equals(pieceJustif.getDatModification())) {
					needToReload = true;
					logger.debug("Cas no3, dates différente : " + pieceJustif.getDatModification()
						+ " - "
						+ pjCandFind.getDatModPjCand()
						+ " - test =  "
						+ pjCandFind.getDatModPjCand().equals(pieceJustif.getDatModification()));
				}
			}
		} else {
			// si pièce non commune, présente dans la fenetre mais absente en base
			if (pieceJustif.getDatModification() != null) {
				final PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(), candidature.getIdCand());
				final PjCand pjCand = pjCandRepository.findOne(pk);

				if (pjCand == null) {
					logger.debug("Cas no4, pièce non commune mais supprimée");
					needToReload = true;
				}
			}
		}

		/* Si on est dans un des deux cas, on recharge la liste de pièces */
		if (needToReload) {
			logger.debug("Rechargement demandé");
			if (showNotif) {
				Notification.show(applicationContext.getMessage("pj.modified", null, UI.getCurrent().getLocale()),
					Type.WARNING_MESSAGE);
			}
			final Candidature candidatureLoad = candidatureRepository.findOne(candidature.getIdCand());
			listener.reloadAllPiece(getPjCandidature(candidatureLoad), candidatureLoad);
			return true;
		}

		return false;
	}

	/**
	 * Methode qui vérifie si la question a été modifiée par qqun d'autre
	 * obligatoire pour les questions commune qui peuvent etre modifiées dans une
	 * autre candidature on se base sur la date de modification
	 * @param  question
	 * @return          true si la question a été modifiée
	 */
	public Boolean isQuestionModified(final QuestionPresentation question,
		final Candidature candidature,
		final Boolean showNotif,
		final CandidatureListener listener) {
		if (!candidature.getFormation().getTemDematForm()) {
			return false;
		}
		logger.debug("Verification question : " + question);
		Boolean needToReload = false;

		if (question.getQuestionCommune()) {
			final List<QuestionCand> listeQuestionCand = questionCandRepository
				.findByIdIdQuestionAndCandidatureCandidatIdCandidatOrderByDatModQuestionCandDesc(
					question.getQuestion().getIdQuestion(),
					candidature.getCandidat().getIdCandidat());
			QuestionCand questionCandFind = null;
			if (listeQuestionCand != null && listeQuestionCand.size() > 0) {
				// on cherche d'abord en priorité si la question est présente sur la candidature
				final Optional<QuestionCand> questionCandOpt = listeQuestionCand.stream()
					.filter(e -> e.getCandidature().getIdCand().equals(question.getIdCandidature()))
					.findFirst();
				if (questionCandOpt.isPresent()) {
					questionCandFind = questionCandOpt.get();
				} else {
					questionCandFind = listeQuestionCand.get(0);
				}
			}
			logger.debug("Questions trouvées : " + listeQuestionCand.size() + " : " + questionCandFind);

			// nouvelle question, si il en existe une, on recharge
			if (question.getDatModification() == null) {
				// la question etait vide et on en a ajouté une
				if (questionCandFind != null) {
					logger.debug("Cas no1, questions nouvelle et question trouvée : " + question.getDatModification()
						+ " - "
						+ questionCandFind);
					needToReload = true;
				}
			}
			/* ce n'est pas une nouvelle question, on vérifie : - qu'elle n'a pas été
			 * supprimée en route - que sa date de modif est différente */
			else {

				// question supprimée
				if (questionCandFind == null) {
					logger.debug("Cas no2, questions vide : " + questionCandFind);
					needToReload = true;
				}
				// question modifiée
				else if (!questionCandFind.getDatModQuestionCand().equals(question.getDatModification())) {
					needToReload = true;
					logger.debug("Cas no3, dates différente : " + question.getDatModification()
						+ " - "
						+ questionCandFind.getDatModQuestionCand()
						+ " - test =  "
						+ questionCandFind.getDatModQuestionCand().equals(question.getDatModification()));
				}
			}
		} else {
			// si question non commune, présente dans la fenetre mais absente en base
			if (question.getDatModification() != null) {
				final QuestionCandPK pk = new QuestionCandPK(candidature.getIdCand(),
					question.getQuestion().getIdQuestion());
				final QuestionCand questionCand = questionCandRepository.findOne(pk);

				if (questionCand == null) {
					logger.debug("Cas no4, question non commune mais supprimée");
					needToReload = true;
				}
			}
		}

		/* Si on est dans un des deux cas, on recharge la liste de questions */
		if (needToReload) {
			logger.debug("Rechargement demandé");
			if (showNotif) {
				Notification.show(applicationContext.getMessage("question.modified", null, UI.getCurrent().getLocale()),
					Type.WARNING_MESSAGE);
			}
			final Candidature candidatureLoad = candidatureRepository.findOne(candidature.getIdCand());
			listener.reloadAllQuestion(getQuestionCandidature(candidatureLoad), candidatureLoad);
			return true;
		}

		return false;
	}

	/**
	 * Ajoute un fichier a une pj
	 * @param pieceJustif
	 * @param candidature
	 * @param listener
	 */
	public void addFileToPieceJustificative(final PjPresentation pieceJustif,
		final Candidature candidature,
		final CandidatureListener listener) {
		/* Verification que le service n'est pas en maintenance */
		if (fileController.isFileServiceMaintenance(true)) {
			return;
		}

		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}

		final String user = userController.getCurrentNoDossierCptMinOrLogin();

		final String cod = ConstanteUtils.TYPE_FICHIER_PJ_CAND + "_"
			+ candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin()
			+ "_"
			+ candidature.getIdCand()
			+ "_"
			+ pieceJustif.getPieceJustif().getIdPj();
		final UploadWindow uw = new UploadWindow(cod,
			ConstanteUtils.TYPE_FICHIER_CANDIDAT,
			candidature,
			pieceJustif.getPJCommune(),
			false);
		uw.addUploadWindowListener(file -> {
			if (file == null) {
				return;
			}
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}

			final PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(), pieceJustif.getIdCandidature());
			PjCand pjCand = pjCandRepository.findOne(pk);

			if (pjCand == null) {
				pjCand = new PjCand(pk, user, candidature, pieceJustif.getPieceJustif());
			}

			final Fichier fichier = fileController.createFile(file, user, ConstanteUtils.TYPE_FICHIER_CANDIDAT);
			if (isPjModified(pieceJustif, candidature, true, listener)) {
				FichierFiabilisation fichierFiabilisation = new FichierFiabilisation(fichier);
				fichierFiabilisation = fichierFiabilisationRepository.save(fichierFiabilisation);
				try {
					fileController.deleteFichier(fichier);
					fichierFiabilisationRepository.delete(fichierFiabilisation);
				} catch (final FileException e) {
				}
				uw.close();
				return;
			}

			pjCand.setLibFilePjCand(fichier.getNomFichier());
			pjCand.setUserModPjCand(user);
			pjCand.setFichier(fichier);

			final TypeStatutPiece statutTr = tableRefController.getTypeStatutPieceTransmis();
			pjCand.setTypeStatutPiece(statutTr);

			pjCandRepository.save(pjCand);

			// obligé de recharger l'objet car le datetime est arrondi :(
			final PjCand pjCandSave = pjCandRepository.findOne(pk);

			pieceJustif.setFilePj(fichier);
			pieceJustif.setCodStatut(statutTr.getCodTypStatutPiece());
			pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutTr.getI18nLibTypStatutPiece()));
			pieceJustif.setDatModification(pjCandSave.getDatModPjCand());

			candidature.setUserModCand(user);
			candidature.updatePjCand(pjCandSave);
			candidature.setDatModCand(LocalDateTime.now());
			final Candidature candidatureSave = candidatureRepository.save(candidature);

			listener.pjModified(pieceJustif, candidatureSave);

			Notification.show(applicationContext.getMessage("window.upload.success",
				new Object[]
				{ file.getFileName() },
				UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
			uw.close();

		});
		UI.getCurrent().addWindow(uw);
	}

	/**
	 * Ajoute une réponse a une question
	 * @param question
	 * @param candidature
	 * @param listener
	 */
	public void addReponseToQuestion(final QuestionPresentation question,
		final Candidature candidature,
		final CandidatureListener listener) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}

		final String user = userController.getCurrentNoDossierCptMinOrLogin();

		final CandidatQuestionWindow textWindow = new CandidatQuestionWindow(question,
			applicationContext.getMessage("question.window.reponseTitle", null, UI.getCurrent().getLocale()),
			false);
		textWindow.addBtnOuiListener(value -> {
			if (lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				if (isQuestionModified(question, candidature, true, listener)) {
					return;
				}

				final QuestionCandPK pk = new QuestionCandPK(question.getIdCandidature(),
					question.getQuestion().getIdQuestion());
				QuestionCand questionCand = questionCandRepository.findOne(pk);

				if (questionCand == null) {
					questionCand = new QuestionCand(pk, user, candidature, question.getQuestion());
				}

				questionCand.setUserModQuestionCand(user);
				questionCand.setReponseQuestionCand(value);

				final TypeStatutPiece statutTr = StringUtils.hasText(questionCand.getReponseQuestionCand())
					? tableRefController.getTypeStatutPieceTransmis()
					: tableRefController.getTypeStatutPieceAttente();
				questionCand.setTypeStatutPiece(statutTr);

				questionCandRepository.save(questionCand);

				// obligé de recharger l'objet car le datetime est arrondi :(
				final QuestionCand questionCandSave = questionCandRepository.findOne(pk);

				question.setCodStatut(statutTr.getCodTypStatutPiece());
				question.setLibStatut(i18nController.getI18nTraduction(statutTr.getI18nLibTypStatutPiece()));
				question.setDatModification(questionCandSave.getDatModQuestionCand());
				question.setReponse(questionCandSave.getReponseQuestionCand());

				candidature.setUserModCand(user);
				candidature.updateQuestionCand(questionCandSave);
				candidature.setDatModCand(LocalDateTime.now());
				final Candidature candidatureSave = candidatureRepository.save(candidature);

				listener.questionModified(question, candidatureSave);

				Notification.show(
					applicationContext.getMessage("question.answer.success", null, UI.getCurrent().getLocale()),
					Type.TRAY_NOTIFICATION);
				textWindow.close();

				/* Suppression du lock */
				lockCandidatController.releaseLockCandidature(candidature);
			}
		});
		UI.getCurrent().addWindow(textWindow);
	}

	/**
	 * Supprime la réponse d'une question
	 * @param question
	 * @param candidature
	 * @param listener
	 */
	public void deleteReponseToQuestion(final QuestionPresentation question, final Candidature candidature, final CandidatureListener listener) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(
			applicationContext.getMessage("question.window.answer.confirmDelete", new Object[]
			{ question.getLibQuestion() },
				UI.getCurrent().getLocale()),
			applicationContext.getMessage("question.window.answer.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(file -> {
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}
			if (isQuestionModified(question, candidature, true, listener)) {
				return;
			}
			final QuestionCandPK pk = new QuestionCandPK(question.getIdCandidature(),
				question.getQuestion().getIdQuestion());
			final QuestionCand questionCand = questionCandRepository.findOne(pk);
			questionCandRepository.delete(questionCand);

			final TypeStatutPiece typStatutAtt = tableRefController.getTypeStatutPieceAttente();
			question.setCodStatut(typStatutAtt.getCodTypStatutPiece());
			question.setLibStatut(i18nController.getI18nTraduction(typStatutAtt.getI18nLibTypStatutPiece()));
			question.setDatModification(null);
			question.setReponse(null);

			candidature.setUserModCand(userController.getCurrentNoDossierCptMinOrLogin());
			candidature.removeQuestionCand(questionCand);
			candidature.setDatModCand(LocalDateTime.now());
			final Candidature candidatureSave = candidatureRepository.save(candidature);

			listener.questionModified(question, candidatureSave);

			Notification.show(
				applicationContext.getMessage("question.answer.success", null, UI.getCurrent().getLocale()),
				Type.TRAY_NOTIFICATION);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Change le statut est concerne d'une pj
	 * @param pieceJustif
	 * @param isConcerned
	 * @param candidature
	 * @param listener
	 */
	public void setIsConcernedPieceJustificative(final PjPresentation pieceJustif,
		final Boolean isConcerned,
		final Candidature candidature,
		final CandidatureListener listener) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		final String user = userController.getCurrentUserLogin();
		if (isConcerned) {
			final ConfirmWindow confirmWindow = new ConfirmWindow(
				applicationContext.getMessage("pj.window.concerne", new Object[]
				{ pieceJustif.getLibPj() },
					UI.getCurrent().getLocale()),
				applicationContext.getMessage("pj.window.conditionnel.title", null, UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}

				if (isPjModified(pieceJustif, candidature, true, listener)) {
					return;
				}

				PjCand pjCand = null;
				if (pieceJustif.getPJCommune()) {
					final List<PjCand> listePjCand = pjCandRepository
						.findByIdIdPjAndCandidatureCandidatIdCandidatAndCandidatureFormationTemDematFormOrderByDatModPjCandDesc(
							pieceJustif.getPieceJustif().getIdPj(),
							candidature.getCandidat().getIdCandidat(),
							true);
					if (listePjCand != null && listePjCand.size() > 0) {
						// on cherche d'abord en priorité si la pièce est présente sur la candidature
						final Optional<PjCand> pjCandOpt = listePjCand.stream()
							.filter(e -> e.getCandidature().getIdCand().equals(pieceJustif.getIdCandidature()))
							.findFirst();
						if (pjCandOpt.isPresent()) {
							pjCand = pjCandOpt.get();
						} else {
							pjCand = listePjCand.get(0);
						}
					}
				} else {
					final PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(), candidature.getIdCand());
					pjCand = pjCandRepository.findOne(pk);
				}

				if (pjCand != null && pjCand.getFichier() == null) {
					pjCandRepository.delete(pjCand);
					candidature.setUserModCand(user);
					candidature.setDatModCand(LocalDateTime.now());
					candidature.removePjCand(pjCand);

					final TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
					pieceJustif.setCodStatut(statutAtt.getCodTypStatutPiece());
					pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
					pieceJustif.setDatModification(null);

					final Candidature candidatureSave = candidatureRepository.save(candidature);
					listener.pjModified(pieceJustif, candidatureSave);
				}
			});
			UI.getCurrent().addWindow(confirmWindow);
		} else {
			final ConfirmWindow confirmWindow = new ConfirmWindow(
				applicationContext.getMessage("pj.window.nonConcerne", new Object[]
				{ pieceJustif.getLibPj() },
					UI.getCurrent().getLocale()),
				applicationContext.getMessage("pj.window.conditionnel.title", null, UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}
				if (isPjModified(pieceJustif, candidature, true, listener)) {
					return;
				}

				PjCand pjCand = null;
				final PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(), candidature.getIdCand());
				if (pieceJustif.getPJCommune()) {
					final List<PjCand> listePjCand = pjCandRepository
						.findByIdIdPjAndCandidatureCandidatIdCandidatAndCandidatureFormationTemDematFormOrderByDatModPjCandDesc(
							pieceJustif.getPieceJustif().getIdPj(),
							candidature.getCandidat().getIdCandidat(),
							true);
					if (listePjCand != null && listePjCand.size() > 0) {
						// on cherche d'abord en priorité si la pièce est présente sur la candidature
						final Optional<PjCand> pjCandOpt = listePjCand.stream()
							.filter(e -> e.getCandidature().getIdCand().equals(pieceJustif.getIdCandidature()))
							.findFirst();
						if (pjCandOpt.isPresent()) {
							pjCand = pjCandOpt.get();
						} else {
							pjCand = listePjCand.get(0);
						}
					}
				} else {
					pjCand = pjCandRepository.findOne(pk);
				}

				if (pjCand == null) {

					pjCand = new PjCand(pk, user, candidature, pieceJustif.getPieceJustif());
					pjCand.setLibFilePjCand(null);
					pjCand.setUserModPjCand(user);
					pjCand.setFichier(null);

					final TypeStatutPiece statutNotConcern = tableRefController.getTypeStatutPieceNonConcerne();
					pieceJustif.setCodStatut(statutNotConcern.getCodTypStatutPiece());
					pieceJustif.setLibStatut(
						i18nController.getI18nTraduction(statutNotConcern.getI18nLibTypStatutPiece()));

					pjCand.setTypeStatutPiece(statutNotConcern);
					pjCand = pjCandRepository.saveAndFlush(pjCand);

					// obligé de recharger l'objet car le datetime est arrondi :(
					final PjCand pjCandSave = pjCandRepository.findOne(pk);

					pieceJustif.setFilePj(null);
					pieceJustif.setCodStatut(statutNotConcern.getCodTypStatutPiece());
					pieceJustif.setLibStatut(
						i18nController.getI18nTraduction(statutNotConcern.getI18nLibTypStatutPiece()));
					pieceJustif.setIdCandidature(candidature.getIdCand());
					pieceJustif.setDatModification(pjCandSave.getDatModPjCand());

					candidature.setUserModCand(user);
					candidature.updatePjCand(pjCandSave);
					candidature.setDatModCand(LocalDateTime.now());
					final Candidature candidatureSave = candidatureRepository.save(candidature);
					listener.pjModified(pieceJustif, candidatureSave);
				}

			});

			UI.getCurrent().addWindow(confirmWindow);
		}
	}

	/**
	 * Change le statut est concerné d'un formulaire
	 * @param formulaire
	 * @param isConcerned
	 * @param candidature
	 * @param listener
	 */
	public void setIsConcernedFormulaire(final FormulairePresentation formulaire,
		final Boolean isConcerned,
		final Candidature candidature,
		final CandidatureListener listener) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		final String user = userController.getCurrentUserLogin();
		if (isConcerned) {
			final ConfirmWindow confirmWindow = new ConfirmWindow(
				applicationContext.getMessage("formulaire.window.concerne",
					new Object[]
					{ formulaire.getLibFormulaire() },
					UI.getCurrent().getLocale()),
				applicationContext.getMessage("formulaire.window.conditionnel.title",
					null,
					UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}
				final FormulaireCandPK pk = new FormulaireCandPK(formulaire.getFormulaire().getIdFormulaire(),
					candidature.getIdCand());
				final FormulaireCand formulaireCand = formulaireCandRepository.findOne(pk);
				if (formulaireCand != null) {
					formulaireCandRepository.delete(formulaireCand);
					candidature.setUserModCand(user);
					candidature.setDatModCand(LocalDateTime.now());
					candidature.removeFormulaireCand(formulaireCand);

					/* Vérification qu'il y a des réponses, si oui on passe à transmis, sinon en
					 * attente */
					if (formulaire.getReponses() != null && !formulaire.getReponses().isEmpty()) {
						final TypeStatutPiece statutTrans = tableRefController.getTypeStatutPieceTransmis();
						formulaire.setCodStatut(statutTrans.getCodTypStatutPiece());
						formulaire
							.setLibStatut(i18nController.getI18nTraduction(statutTrans.getI18nLibTypStatutPiece()));
					} else {
						final TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
						formulaire.setCodStatut(statutAtt.getCodTypStatutPiece());
						formulaire.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
					}

					final Candidature candidatureSave = candidatureRepository.save(candidature);
					listener.formulaireModified(formulaire, candidatureSave);
				}
			});
			UI.getCurrent().addWindow(confirmWindow);
		} else {
			final ConfirmWindow confirmWindow = new ConfirmWindow(
				applicationContext.getMessage("formulaire.window.nonConcerne",
					new Object[]
					{ formulaire.getLibFormulaire() },
					UI.getCurrent().getLocale()),
				applicationContext.getMessage("formulaire.window.conditionnel.title",
					null,
					UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}
				final FormulaireCandPK pk = new FormulaireCandPK(formulaire.getFormulaire().getIdFormulaire(),
					candidature.getIdCand());
				FormulaireCand formulaireCand = formulaireCandRepository.findOne(pk);
				if (formulaireCand == null) {

					formulaireCand = new FormulaireCand(pk, user, candidature, formulaire.getFormulaire());
					formulaireCand.setUserModFormulaireCand(user);

					final TypeStatutPiece statutNotConcern = tableRefController.getTypeStatutPieceNonConcerne();
					formulaire.setCodStatut(statutNotConcern.getCodTypStatutPiece());
					formulaire.setLibStatut(
						i18nController.getI18nTraduction(statutNotConcern.getI18nLibTypStatutPiece()));
					formulaireCand = formulaireCandRepository.save(formulaireCand);

					candidature.setUserModCand(user);
					candidature.updateFormulaireCand(formulaireCand);
					candidature.setDatModCand(LocalDateTime.now());
					final Candidature candidatureSave = candidatureRepository.save(candidature);
					listener.formulaireModified(formulaire, candidatureSave);
				}

			});

			UI.getCurrent().addWindow(confirmWindow);
		}
	}

	/**
	 * Change le statut est concerne d'une question
	 * @param question
	 * @param isConcerned
	 * @param candidature
	 * @param listener
	 */
	public void setIsConcernedQuestion(final QuestionPresentation question,
		final Boolean isConcerned,
		final Candidature candidature,
		final CandidatureListener listener) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		final String user = userController.getCurrentUserLogin();
		if (isConcerned) {
			final ConfirmWindow confirmWindow = new ConfirmWindow(
				applicationContext.getMessage("question.window.concerne",
					new Object[]
					{ question.getLibQuestion() },
					UI.getCurrent().getLocale()),
				applicationContext.getMessage("question.window.conditionnel.title",
					null,
					UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}

				if (isQuestionModified(question, candidature, true, listener)) {
					return;
				}

				QuestionCand questionCand = null;
				if (question.getQuestionCommune()) {
					final List<QuestionCand> listeQuestionCand = questionCandRepository
						.findByIdIdQuestionAndCandidatureCandidatIdCandidatOrderByDatModQuestionCandDesc(
							question.getQuestion().getIdQuestion(),
							candidature.getCandidat().getIdCandidat());
					if (listeQuestionCand != null && listeQuestionCand.size() > 0) {
						// on cherche d'abord en priorité si la question est présente sur la candidature
						final Optional<QuestionCand> questionCandOpt = listeQuestionCand.stream()
							.filter(e -> e.getCandidature().getIdCand().equals(question.getIdCandidature()))
							.findFirst();
						if (questionCandOpt.isPresent()) {
							questionCand = questionCandOpt.get();
						} else {
							questionCand = listeQuestionCand.get(0);
						}
					}
				} else {
					final QuestionCandPK pk = new QuestionCandPK(candidature.getIdCand(),
						question.getQuestion().getIdQuestion());
					questionCand = questionCandRepository.findOne(pk);
				}

				if (questionCand != null) {
					questionCandRepository.delete(questionCand);
					candidature.setUserModCand(user);
					candidature.setDatModCand(LocalDateTime.now());
					candidature.removeQuestionCand(questionCand);

					final TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
					question.setCodStatut(statutAtt.getCodTypStatutPiece());
					question.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
					question.setDatModification(null);

					questionCand.setTypeStatutPiece(statutAtt);
					questionCand = questionCandRepository.saveAndFlush(questionCand);

					// obligé de recharger l'objet car le datetime est arrondi :(
					final QuestionCand questionCandSave = questionCandRepository.findOne(questionCand.getId());

					question.setCodStatut(statutAtt.getCodTypStatutPiece());
					question.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
					question.setIdCandidature(candidature.getIdCand());
					question.setDatModification(questionCandSave.getDatModQuestionCand());

					candidature.setUserModCand(user);
					candidature.updateQuestionCand(questionCandSave);
					candidature.setDatModCand(LocalDateTime.now());
					final Candidature candidatureSave = candidatureRepository.save(candidature);
					listener.questionModified(question, candidatureSave);
				}
			});
			UI.getCurrent().addWindow(confirmWindow);
		} else {
			final ConfirmWindow confirmWindow = new ConfirmWindow(
				applicationContext.getMessage("question.window.nonConcerne",
					new Object[]
					{ question.getLibQuestion() },
					UI.getCurrent().getLocale()),
				applicationContext.getMessage("question.window.conditionnel.title",
					null,
					UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}
				if (isQuestionModified(question, candidature, true, listener)) {
					return;
				}

				QuestionCand questionCand = null;
				final QuestionCandPK pk = new QuestionCandPK(candidature.getIdCand(),
					question.getQuestion().getIdQuestion());
				if (question.getQuestionCommune()) {
					final List<QuestionCand> listeQuestionCand = questionCandRepository
						.findByIdIdQuestionAndCandidatureCandidatIdCandidatOrderByDatModQuestionCandDesc(
							question.getQuestion().getIdQuestion(),
							candidature.getCandidat().getIdCandidat());
					if (listeQuestionCand != null && listeQuestionCand.size() > 0) {
						// on cherche d'abord en priorité si la question est présente sur la candidature
						final Optional<QuestionCand> questionCandOpt = listeQuestionCand.stream()
							.filter(e -> e.getCandidature().getIdCand().equals(question.getIdCandidature()))
							.findFirst();
						if (questionCandOpt.isPresent()) {
							questionCand = questionCandOpt.get();
						} else {
							questionCand = listeQuestionCand.get(0);
						}
					}
				} else {
					questionCand = questionCandRepository.findOne(pk);
				}

				if (questionCand == null) {

					questionCand = new QuestionCand(pk, user, candidature, question.getQuestion());
					questionCand.setUserModQuestionCand(user);
				}

				final TypeStatutPiece statutNotConcern = tableRefController.getTypeStatutPieceNonConcerne();
				question.setCodStatut(statutNotConcern.getCodTypStatutPiece());
				question.setLibStatut(i18nController.getI18nTraduction(statutNotConcern.getI18nLibTypStatutPiece()));

				questionCand.setTypeStatutPiece(statutNotConcern);
				questionCand = questionCandRepository.saveAndFlush(questionCand);

				// obligé de recharger l'objet car le datetime est arrondi :(
				final QuestionCand questionCandSave = questionCandRepository.findOne(pk);

				question.setCodStatut(statutNotConcern.getCodTypStatutPiece());
				question.setLibStatut(i18nController.getI18nTraduction(statutNotConcern.getI18nLibTypStatutPiece()));
				question.setIdCandidature(candidature.getIdCand());
				question.setDatModification(questionCandSave.getDatModQuestionCand());

				candidature.setUserModCand(user);
				candidature.updateQuestionCand(questionCandSave);
				candidature.setDatModCand(LocalDateTime.now());
				final Candidature candidatureSave = candidatureRepository.save(candidature);
				listener.questionModified(question, candidatureSave);
			});

			UI.getCurrent().addWindow(confirmWindow);
		}
	}

	/**
	 * Ajoute un fichier en PJ
	 * @param pieceJustif
	 * @param candidature
	 * @param listener
	 */
	public void deleteFileToPieceJustificative(final PjPresentation pieceJustif,
		final Candidature candidature,
		final CandidatureListener listener) {
		/* Vérifie si le service de fichier est en maintenance */
		if (fileController.isFileServiceMaintenance(true)) {
			return;
		}
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		if (!fileController.isModeFileStockageOk(pieceJustif.getFilePj(), false)) {
			return;
		}
		final Fichier fichier = pieceJustif.getFilePj();
		final ConfirmWindow confirmWindow = new ConfirmWindow(
			applicationContext.getMessage("file.window.confirmDelete", new Object[]
			{ fichier.getNomFichier() },
				UI.getCurrent().getLocale()),
			applicationContext.getMessage("file.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(file -> {
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}
			if (isPjModified(pieceJustif, candidature, true, listener)) {
				return;
			}
			final PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(), pieceJustif.getIdCandidature());
			final PjCand pjCand = pjCandRepository.findOne(pk);
			final String user = userController.getCurrentNoDossierCptMinOrLogin();

			removeFileToPj(pjCand);

			final TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
			pieceJustif.setFilePj(null);

			pieceJustif.setCodStatut(statutAtt.getCodTypStatutPiece());
			pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
			pieceJustif.setCommentaire(null);
			pieceJustif.setDatModification(null);

			candidature.setUserModCand(user);
			candidature.setDatModCand(LocalDateTime.now());

			candidature.removePjCand(pjCand);
			final Candidature candidatureSave = candidatureRepository.save(candidature);
			listener.pjModified(pieceJustif, candidatureSave);

		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/* @Transactional(rollbackFor=FileException.class) public void
	 * removeFileToPj(PjCand pjCand) throws FileException{ Fichier fichier =
	 * pjCand.getFichier(); pjCandRepository.delete(pjCand); if (fichier != null){
	 * fileController.deleteFichier(fichier,false); } } */

	/**
	 * Supprime un fichier d'une PJCand
	 * @param pjCand
	 */
	public void removeFileToPj(final PjCand pjCand) {
		final Fichier fichier = pjCand.getFichier();
		final PjCandPK idPjCand = pjCand.getId();
		pjCandRepository.delete(pjCand);
		if (fichier != null) {
			FichierFiabilisation fichierFiabilisation = new FichierFiabilisation(fichier);
			fichierFiabilisation.setIdPj(idPjCand.getIdPj());
			fichierFiabilisation.setIdCand(idPjCand.getIdCand());
			fichierFiabilisation = fichierFiabilisationRepository.save(fichierFiabilisation);
			try {
				fileController.deleteFichier(fichier);
				fichierFiabilisationRepository.delete(fichierFiabilisation);
			} catch (final FileException e) {
			}
		}
	}

	/**
	 * Utilisé dans le bacth de destruction Les fichiers physiques doivent être
	 * supprimés à la main
	 * @param  pjCand
	 * @throws FileException
	 */
	public void removeFileToPjManually(final PjCand pjCand) throws FileException {
		final Fichier fichier = pjCand.getFichier();
		pjCandRepository.delete(pjCand);
		if (fichier != null) {
			fichierRepository.delete(fichier);
		}
	}

	/**
	 * Change le statut d'une liste de pj
	 * @param listePj
	 * @param candidature
	 * @param listener
	 */
	public void changeStatutPj(final List<PjPresentation> listePj,
		final Candidature candidature,
		final CandidatureListener listener) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}

		final CtrCandActionPjWindow window = new CtrCandActionPjWindow(listePj);
		window.addChangeStatutPieceWindowListener((t, c) -> {
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}

			/* Verification de modif de piece */
			for (final PjPresentation pj : listePj) {
				if (isPjModified(pj, candidature, false, listener)) {
					Notification.show(applicationContext.getMessage("pjs.modified", null, UI.getCurrent().getLocale()),
						Type.WARNING_MESSAGE);
					return;
				}
			}

			final String user = userController.getCurrentUserLogin();
			listePj.forEach(e -> {
				final PjCandPK pk = new PjCandPK(e.getPieceJustif().getIdPj(), e.getIdCandidature());
				PjCand pjCand = pjCandRepository.findOne(pk);

				if (pjCand == null) {
					pjCand = new PjCand(pk, user, candidature, e.getPieceJustif());
				}
				pjCand.setTypeStatutPiece(t);
				pjCand.setCommentPjCand(c);
				pjCand.setUserModPjCand(user);
				pjCand.setDatModStatutPjCand(LocalDateTime.now());
				pjCand.setUserModStatutPjCand(user);
				pjCandRepository.save(pjCand);

				final PjCand pjCandSave = pjCandRepository.findOne(pk);

				candidature.updatePjCand(pjCandSave);
				if (pjCandSave.getTypeStatutPiece() != null) {
					e.setLibStatut(i18nController
						.getI18nTraduction(pjCandSave.getTypeStatutPiece().getI18nLibTypStatutPiece()));
					e.setCodStatut(pjCandSave.getTypeStatutPiece().getCodTypStatutPiece());
				}
				e.setCommentaire(c);
				e.setDatModification(pjCandSave.getDatModPjCand());
				e.setUserModStatut(getLibModStatut(pjCand.getUserModStatutPjCand(), pjCand.getDatModStatutPjCand()));
			});
			candidature.setUserModCand(user);
			final Candidature candidatureSave = candidatureRepository.save(candidature);
			listener.pjsModified(listePj, candidatureSave);
		});
		UI.getCurrent().addWindow(window);
	}

	/**
	 * @param  userModStatut
	 * @param  datModStatut
	 * @return               le libelle de l'utilisateur ayant modifié un statut de PJ
	 */
	private String getLibModStatut(final String userModStatut, final LocalDateTime datModStatut) {
		if (userModStatut != null && datModStatut != null) {
			return individuController.getLibIndividu(userModStatut) + " ("
				+ formatterDateTime.format(datModStatut)
				+ ")";
		}
		return null;
	}

	/**
	 * @param pj
	 */
	public void checkPJAdmin(final PjPresentation pj,
		final Candidature candidature,
		final CandidatureListener listener) {
		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		if (!userController.isAdmin()) {
			return;
		}

		final PjCandPK pk = new PjCandPK(pj.getPieceJustif().getIdPj(), pj.getIdCandidature());
		final PjCand pjCand = pjCandRepository.findOne(pk);
		if (pjCand == null) {
			Notification.show(applicationContext.getMessage("pj.admin.pjnotexist", null, UI.getCurrent().getLocale()),
				Type.WARNING_MESSAGE);
			return;
		}
		final Fichier fichier = pjCand.getFichier();
		final String user = userController.getCurrentNoDossierCptMinOrLogin();
		if (fichier != null) {
			try {
				if (!fileController.testDemat(false)) {
					return;
				}
				final Boolean exist = fileController.existFile(fichier);
				if (!exist) {
					final ConfirmWindow confirmWindow = new ConfirmWindow(
						applicationContext.getMessage("pj.admin.window.filenotexist",
							null,
							UI.getCurrent().getLocale()),
						applicationContext.getMessage("pj.admin.window.title", null, UI.getCurrent().getLocale()));
					confirmWindow.addBtnOuiListener(event -> {
						pjCandRepository.delete(pjCand);
						fichierRepository.delete(fichier);
						final TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
						pj.setFilePj(null);

						pj.setCodStatut(statutAtt.getCodTypStatutPiece());
						pj.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
						pj.setCommentaire(null);
						pj.setDatModification(null);
						candidature.setUserModCand(user);
						candidature.setDatModCand(LocalDateTime.now());
						candidature.removePjCand(pjCand);
						final Candidature candidatureSave = candidatureRepository.save(candidature);
						listener.pjModified(pj, candidatureSave);

						Notification.show(
							applicationContext.getMessage("pj.admin.success", null, UI.getCurrent().getLocale()),
							Type.TRAY_NOTIFICATION);
					});
					UI.getCurrent().addWindow(confirmWindow);
				} else {
					Notification.show(applicationContext.getMessage("pj.admin.error.fileexist",
						null,
						UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
			} catch (final FileException e) {
				return;
			}
		}
	}

	/**
	 * @param  liste
	 * @param  pieceJustif
	 * @return             un zip contenant tous les dossiers
	 */
	public OnDemandFile downlaodMultiplePjZip(final List<Candidature> liste, final PieceJustif pieceJustif) {
		final ByteArrayInOutStream out = new ByteArrayInOutStream();
		final ZipOutputStream zos = new ZipOutputStream(out);
		Boolean error = false;
		int nbPj = 0;
		for (final Candidature candidature : liste) {
			final PjCand pj = getPjCandFromList(pieceJustif, candidature, true);
			if (pj == null) {
				continue;
			}
			final Fichier file = pj.getFichier();
			if (file == null) {
				continue;
			}
			final InputStream is = fileController.getInputStreamFromFichier(file);
			if (is == null) {
				continue;
			}
			try {
				final String fileName = applicationContext.getMessage("candidature.download.pj.file.name",
					new Object[]
					{ candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin(),
						candidature.getCandidat().getNomPatCandidat(),
						candidature.getCandidat().getPrenomCandidat(),
						pieceJustif.getCodPj(),
						candidature.getIdCand().toString(),
						file.getNomFichier() },
					UI.getCurrent().getLocale());
				zos.putNextEntry(new ZipEntry(fileName));
				int count;
				final byte data[] = new byte[2048];
				while ((count = is.read(data, 0, 2048)) != -1) {
					zos.write(data, 0, count);
				}
				zos.closeEntry();
				nbPj++;
			} catch (final IOException e) {
				error = true;
				logger.error("erreur a la génération d'un dossier lors du zip", e);
			} finally {
				/* Nettoyage des ressources */
				MethodUtils.closeRessource(is);
			}
		}
		if (error) {
			Notification.show(applicationContext.getMessage("candidature.download.pj.error.file",
				null,
				UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		}
		try {
			zos.finish();
			zos.close();
			if (nbPj == 0) {
				Notification.show(applicationContext.getMessage("candidature.download.pj.nopj",
					null,
					UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return null;
			}
			return new OnDemandFile(pieceJustif.getCodPj() + ".zip", out.getInputStream());
		} catch (final IOException e) {
			logger.error("erreur a la génération du zip", e);
			Notification.show(applicationContext.getMessage("candidature.download.pj.error.zip",
				null,
				UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return null;
		} finally {
			/* Nettoyage des ressources */
			MethodUtils.closeRessource(zos);
			MethodUtils.closeRessource(out);
		}
	}
}
