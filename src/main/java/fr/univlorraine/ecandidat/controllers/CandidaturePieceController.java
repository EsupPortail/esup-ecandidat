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
import java.time.format.DateTimeFormatter;
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

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.FichierFiabilisation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCand;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCandPK;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCandPK;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.PjOpi;
import fr.univlorraine.ecandidat.entities.ecandidat.PjOpiPK;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.FichierFiabilisationRepository;
import fr.univlorraine.ecandidat.repositories.FichierRepository;
import fr.univlorraine.ecandidat.repositories.FormulaireCandRepository;
import fr.univlorraine.ecandidat.repositories.PjCandRepository;
import fr.univlorraine.ecandidat.repositories.PjOpiRepository;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.FormulairePresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandActionPjWindow;
import fr.univlorraine.ecandidat.views.windows.InfoWindow;
import fr.univlorraine.ecandidat.views.windows.UploadWindow;

/** Gestion des pièces
 *
 * @author Kevin Hergalant */
@Component
public class CandidaturePieceController {
	private Logger logger = LoggerFactory.getLogger(CandidaturePieceController.class);
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
	private transient LockCandidatController lockCandidatController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient CandidatPieceController candidatPieceController;
	@Resource
	private transient CandidatureRepository candidatureRepository;
	@Resource
	private transient PjCandRepository pjCandRepository;
	@Resource
	private transient FichierRepository fichierRepository;
	@Resource
	private transient FichierFiabilisationRepository fichierFiabilisationRepository;
	@Resource
	private transient FormulaireCandRepository formulaireCandRepository;
	@Resource
	private transient PjOpiRepository pjOpiRepository;

	@Resource
	private transient DateTimeFormatter formatterDateTime;

	/** @param candidature
	 * @return la liste des pj d'une candidature */
	public List<PjPresentation> getPjCandidature(final Candidature candidature) {
		List<PjPresentation> liste = new ArrayList<>();
		TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
		TypeStatutPiece statutValide = tableRefController.getTypeStatutPieceValide();
		Boolean isDemat = candidature.getFormation().getTemDematForm();

		int i = 1;
		for (PieceJustif e : pieceJustifController.getPjForCandidature(candidature, true)) {
			liste.add(getPjPresentation(e, candidature, statutAtt, statutValide, i, isDemat));
			i++;
		}

		return liste;
	}

	/** @param pj
	 * @param candidature
	 * @param statutAtt
	 * @param statutValide
	 * @param isDemat
	 * @return une piece de presentation */
	private PjPresentation getPjPresentation(final PieceJustif pj, final Candidature candidature, final TypeStatutPiece statutAtt,
			final TypeStatutPiece statutValide, final Integer order, final Boolean isDemat) {
		String libPj = i18nController.getI18nTraduction(pj.getI18nLibPj());
		PjCand pjCand = getPjCandFromList(pj, candidature, isDemat);

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
				pjCandidatFromApogee = candidatPieceController.getPjCandidat(pj.getCodApoPj(), candidature.getCandidat());
				if (pjCandidatFromApogee != null) {
					fichier = new Fichier();
					fichier.setFileFichier(pjCandidatFromApogee.getNomFicPjCandidat());
					fichier.setNomFichier(pjCandidatFromApogee.getNomFicPjCandidat());
					commentaire = applicationContext.getMessage("file.from.another.system", null, UI.getCurrent().getLocale());
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
		Boolean commun = pj.getTemCommunPj() && !pj.getTemUnicitePj();
		return new PjPresentation(pj, libPj, fichier, codStatut, libStatut, commentaire, pj.getTemConditionnelPj(), commun, datModification, idCandidature, order, pjCandidatFromApogee, userMod);
	}

	/** @param piece
	 * @param isDemat
	 * @param listPjCand
	 * @return Renvoi un fichier si il existe */
	private PjCand getPjCandFromList(final PieceJustif piece, final Candidature candidature, final Boolean isDemat) {
		Optional<PjCand> pjCandOpt = candidature.getPjCands().stream().filter(e -> e.getId().getIdPj().equals(piece.getIdPj())).findAny();
		if (pjCandOpt.isPresent()) {
			return pjCandOpt.get();
		}
		if (!isDemat) {
			return null;
		}
		// si on ne trouve pas et que la pièce est commune, on va chercher dans les
		// autres candidatures
		if (piece.getTemCommunPj() && !piece.getTemUnicitePj()) {
			List<PjCand> liste = pjCandRepository.findByIdIdPjAndCandidatureCandidatIdCandidatAndCandidatureFormationTemDematFormOrderByDatModPjCandDesc(piece.getIdPj(), candidature.getCandidat().getIdCandidat(), true);
			if (liste.size() > 0) {
				return liste.get(0);
			}
		}
		return null;
	}

	/** @param candidature
	 * @return la liste des formulaires d'une candidature */
	public List<FormulairePresentation> getFormulaireCandidature(final Candidature candidature) {
		String numDossier = candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin();
		List<FormulairePresentation> liste = new ArrayList<>();
		TypeStatutPiece statutAT = tableRefController.getTypeStatutPieceAttente();
		TypeStatutPiece statutNC = tableRefController.getTypeStatutPieceNonConcerne();
		TypeStatutPiece statutTR = tableRefController.getTypeStatutPieceTransmis();

		List<FormulaireCand> listeFormulaireCand = candidature.getFormulaireCands();
		List<FormulaireCandidat> listeFormulaireCandidat = candidature.getCandidat().getFormulaireCandidats();

		formulaireController.getFormulaireForCandidature(candidature).forEach(e -> {
			String libForm = i18nController.getI18nTraduction(e.getI18nLibFormulaire());
			String urlForm = i18nController.getI18nTraduction(e.getI18nUrlFormulaire());

			/*
			 * Possibilité d'ajout du numdossier dans l'url sous la forme ${numDossierOpi}
			 */
			if (urlForm != null) {
				urlForm = urlForm.replaceAll(ConstanteUtils.VAR_REGEX_FORM_NUM_DOSSIER, numDossier);
			}

			String libStatut = null;
			String codStatut = null;
			String reponses = null;

			/* On recherche d'abord les réponses */
			FormulaireCandidat formulaireCandidat = getFormulaireCandidatFromList(e, listeFormulaireCandidat);
			if (formulaireCandidat != null) {
				codStatut = statutTR.getCodTypStatutPiece();
				libStatut = i18nController.getI18nTraduction(statutTR.getI18nLibTypStatutPiece());
				reponses = formulaireCandidat.getReponsesFormulaireCandidat();
			} else {
				/*
				 * Si pas de réponse on recherche une ligne dans formulaireCand pour vérifier
				 * qu'il est concerné
				 */
				FormulaireCand formulaireCand = getFormulaireCandFromList(e, listeFormulaireCand);
				if (formulaireCand != null) {
					codStatut = statutNC.getCodTypStatutPiece();
					libStatut = i18nController.getI18nTraduction(statutNC.getI18nLibTypStatutPiece());
				} else {
					/* Ni de réponse, ni non concerné --> en attente */
					codStatut = statutAT.getCodTypStatutPiece();
					libStatut = i18nController.getI18nTraduction(statutAT.getI18nLibTypStatutPiece());
				}
			}
			liste.add(new FormulairePresentation(e, libForm, urlForm, codStatut, libStatut, e.getTemConditionnelFormulaire(), reponses));
		});

		return liste;
	}

	/** @param formulaire
	 * @param listFormulaireCand
	 * @return recherche une reponse non concerné a un formulaire */
	private FormulaireCand getFormulaireCandFromList(final Formulaire formulaire, final List<FormulaireCand> listFormulaireCand) {
		Optional<FormulaireCand> formulaireCandOpt = listFormulaireCand.stream().filter(e -> e.getId().getIdFormulaire().equals(formulaire.getIdFormulaire())).findAny();
		if (formulaireCandOpt.isPresent()) {
			return formulaireCandOpt.get();
		}
		return null;
	}

	/** @param formulaire
	 * @param listFormulaireCandidat
	 * @param idCandidat
	 * @return recherche une reponse a formulaire */
	private FormulaireCandidat getFormulaireCandidatFromList(final Formulaire formulaire,
			final List<FormulaireCandidat> listFormulaireCandidat) {
		Optional<FormulaireCandidat> formulaireCandOpt = listFormulaireCandidat.stream().filter(e -> e.getId().getIdFormulaireLimesurvey().equals(formulaire.getIdFormulaireLimesurvey())).sorted((e1,
				e2) -> (e2.getDatReponseFormulaireCandidat().compareTo(e1.getDatReponseFormulaireCandidat()))).findFirst();
		if (formulaireCandOpt.isPresent()) {
			return formulaireCandOpt.get();
		}
		return null;
	}

	/** @param codStatut
	 * @param notification
	 * @return true si le statut de dossier de la candidatures permet de transmettre
	 *         le dossier */
	public Boolean isOkToTransmettreCandidatureStatutDossier(final String codStatut, final Boolean notification) {
		if (codStatut.equals(NomenclatureUtils.TYPE_STATUT_ATT)
				|| (codStatut.equals(NomenclatureUtils.TYPE_STATUT_INC))) {
			return true;
		} else {
			if (notification) {
				Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.statut", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			return false;
		}
	}

	/** @param listePj
	 * @param notification
	 * @return true si les pieces de la candidatures permettent de transmettre le
	 *         dossier */
	public Boolean isOkToTransmettreCandidatureStatutPiece(final List<PjPresentation> listePj, final Boolean notification) {
		for (PjPresentation pj : listePj) {
			if (pj.getCodStatut() == null) {
				if (notification) {
					Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.pj", new Object[] {pj.getLibPj()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
				return false;
			} else if (pj.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE)) {
				if (notification) {
					Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.pj.attente", new Object[] {pj.getLibPj()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
				return false;
			} else if (pj.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_REFUSE)) {
				if (notification) {
					Notification.show(applicationContext.getMessage("candidature.validPJ.erreur.pj.refus", new Object[] {pj.getLibPj()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
				return false;
			}
		}
		return true;
	}

	/** @param candidature
	 * @param listePj
	 * @param listener
	 * @param notification
	 * @return vérifie que l'etat de la candidature permet de transmettre le dossier */
	private Boolean isOkToTransmettreCandidature(final Candidature candidature, final List<PjPresentation> listePj,
			final CandidatureListener listener, final Boolean notification) {
		return (isOkToTransmettreCandidatureStatutDossier(candidature.getTypeStatut().getCodTypStatut(), notification)
				&& isOkToTransmettreCandidatureStatutPiece(listePj, notification));
	}

	/** Transmet le dossier apres le click sur le bouton transmettre
	 *
	 * @param candidature
	 * @param listePj
	 * @param listener
	 */
	public void transmettreCandidatureAfterClick(final Candidature candidature, final List<PjPresentation> listePj,
			final CandidatureListener listener) {
		if (isOkToTransmettreCandidature(candidature, listePj, listener, true)) {
			transmettreCandidature(candidature, listener, applicationContext.getMessage("candidature.validPJ.window.confirm", null, UI.getCurrent().getLocale()));
		}
	}

	/** Transmet le dossier apres un depot de pièce
	 *
	 * @param candidature
	 * @param listePj
	 * @param listener
	 * @param dateLimiteRetour
	 */
	public void transmettreCandidatureAfterDepot(final Candidature candidature, final List<PjPresentation> listePj,
			final CandidatureListener listener, final String dateLimiteRetour) {
		if (isOkToTransmettreCandidature(candidature, listePj, listener, false)) {
			UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("candidature.validPJ.window.info.tite", null, UI.getCurrent().getLocale()), applicationContext.getMessage("candidature.validPJ.window.info.afteraction", new Object[] {
					dateLimiteRetour}, UI.getCurrent().getLocale()), 425, null));
			// transmettreCandidature(candidature, listener,
			// applicationContext.getMessage("candidature.validPJ.window.confirm.afteraction",
			// null, UI.getCurrent().getLocale()));
		}
	}

	/** Transmet la canidature
	 *
	 * @param candidature
	 * @param listener
	 * @param message
	 */
	private void transmettreCandidature(final Candidature candidature, final CandidatureListener listener, final String message) {
		ConfirmWindow confirmWindow = new ConfirmWindow(message, applicationContext.getMessage("candidature.validPJ.window.confirmTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(event -> {
			candidature.setTypeStatut(tableRefController.getTypeStatutReceptionne());
			candidature.setDatReceptDossierCand(LocalDate.now());
			candidature.setDatModTypStatutCand(LocalDateTime.now());
			candidature.setDatTransDossierCand(LocalDateTime.now());

			Candidature candidatureSave = candidatureRepository.save(candidature);

			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(), NomenclatureUtils.MAIL_STATUT_RE, null, candidature, candidature.getCandidat().getLangue().getCodLangue());

			if (candidature.getFormation().getCommission().getTemAlertTransComm()) {
				mailController.sendMailByCod(candidature.getFormation().getCommission().getMailComm(), NomenclatureUtils.MAIL_COMMISSION_ALERT_TRANSMISSION, null, candidature, null);
			}

			listener.transmissionDossier(candidatureSave);

			Notification.show(applicationContext.getMessage("candidature.validPJ.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Methode inutilisée, controle dans le batch
	 *
	 * @param pieceJustif
	 * @param candidature
	 * @return true si une pièce existe alors qu'on veut l'ajouter */
	/*
	 * public Boolean controlPjAdd(PjPresentation pieceJustif, Candidature
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
	 * PjCand pjCand = pjCandRepository.findOne(pk);
	 * if (pjCand!=null && pjCand.getFichier()!=null){
	 * logger.debug("Ajout PJ en erreur, piece non commune nok"); needToReload =
	 * true; } } if (needToReload){
	 * logger.debug("Ajout PJ en erreur, rechargement demandé");
	 * Notification.show(applicationContext.getMessage("pj.add.error", null,
	 * UI.getCurrent().getLocale()), Type.WARNING_MESSAGE); Candidature
	 * candidatureLoad = candidatureRepository.findOne(candidature.getIdCand());
	 * listener.reloadAllPiece(getPjCandidature(candidatureLoad), candidatureLoad);
	 * return true; } return false; }
	 */

	/** Methode qui vérifie si la PJ a été modifiée par qqun d'autre obligatoire pour
	 * les PJ commune qui peuvent etre modifiées dans une autre candidature on se
	 * base sur la date de modification
	 *
	 * @param pieceJustif
	 * @return true si la PJ a été modifiée */
	public Boolean isPjModified(final PjPresentation pieceJustif, final Candidature candidature, final Boolean showNotif,
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
			List<PjCand> listePjCand = pjCandRepository.findByIdIdPjAndCandidatureCandidatIdCandidatAndCandidatureFormationTemDematFormOrderByDatModPjCandDesc(pieceJustif.getPieceJustif().getIdPj(), candidature.getCandidat().getIdCandidat(), true);
			PjCand pjCandFind = null;
			if (listePjCand != null && listePjCand.size() > 0) {
				// on cherche d'abord en priorité si la pièce est présente sur la candidature
				Optional<PjCand> pjCandOpt = listePjCand.stream().filter(e -> e.getCandidature().getIdCand().equals(pieceJustif.getIdCandidature())).findFirst();
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
							+ " - " + pjCandFind);
					needToReload = true;
				}
			}
			/*
			 * ce n'est pas une nouvelle pièce, on vérifie : - qu'elle n'a pas été supprimée
			 * en route - que sa date de modif est différente
			 */
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
					logger.debug("Cas no3, dates différente : " + pieceJustif.getDatModification() + " - "
							+ pjCandFind.getDatModPjCand() + " - test =  "
							+ pjCandFind.getDatModPjCand().equals(pieceJustif.getDatModification()));
				}
			}
		} else {
			// si pièce non commune, présente dans la fenetre mais absente en base
			if (pieceJustif.getDatModification() != null) {
				PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(), candidature.getIdCand());
				PjCand pjCand = pjCandRepository.findOne(pk);

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
				Notification.show(applicationContext.getMessage("pj.modified", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			Candidature candidatureLoad = candidatureRepository.findOne(candidature.getIdCand());
			listener.reloadAllPiece(getPjCandidature(candidatureLoad), candidatureLoad);
			return true;
		}

		return false;
	}

	/** Ajoute un fichier a une pj
	 *
	 * @param pieceJustif
	 * @param candidature
	 * @param listener
	 */
	public void addFileToPieceJustificative(final PjPresentation pieceJustif, final Candidature candidature,
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

		String user = userController.getCurrentNoDossierCptMinOrLogin();

		String cod = ConstanteUtils.TYPE_FICHIER_PJ_CAND + "_"
				+ candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin() + "_" + candidature.getIdCand()
				+ "_" + pieceJustif.getPieceJustif().getIdPj();
		UploadWindow uw = new UploadWindow(cod, ConstanteUtils.TYPE_FICHIER_CANDIDAT, candidature, pieceJustif.getPJCommune(), false);
		uw.addUploadWindowListener(file -> {
			if (file == null) {
				return;
			}
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}

			PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(), pieceJustif.getIdCandidature());
			PjCand pjCand = pjCandRepository.findOne(pk);

			if (pjCand == null) {
				pjCand = new PjCand(pk, user, candidature, pieceJustif.getPieceJustif());
			}

			Fichier fichier = fileController.createFile(file, user, ConstanteUtils.TYPE_FICHIER_CANDIDAT);
			if (isPjModified(pieceJustif, candidature, true, listener)) {
				FichierFiabilisation fichierFiabilisation = new FichierFiabilisation(fichier);
				fichierFiabilisation = fichierFiabilisationRepository.save(fichierFiabilisation);
				try {
					fileController.deleteFichier(fichier);
					fichierFiabilisationRepository.delete(fichierFiabilisation);
				} catch (FileException e) {
				}
				uw.close();
				return;
			}

			pjCand.setLibFilePjCand(fichier.getNomFichier());
			pjCand.setUserModPjCand(user);
			pjCand.setFichier(fichier);

			TypeStatutPiece statutTr = tableRefController.getTypeStatutPieceTransmis();
			pjCand.setTypeStatutPiece(statutTr);

			pjCandRepository.save(pjCand);

			// obligé de recharger l'objet car le datetime est arrondi :(
			PjCand pjCandSave = pjCandRepository.findOne(pk);

			pieceJustif.setFilePj(fichier);
			pieceJustif.setCodStatut(statutTr.getCodTypStatutPiece());
			pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutTr.getI18nLibTypStatutPiece()));
			pieceJustif.setDatModification(pjCandSave.getDatModPjCand());

			candidature.setUserModCand(user);
			candidature.updatePjCand(pjCandSave);
			candidature.setDatModCand(LocalDateTime.now());
			Candidature candidatureSave = candidatureRepository.save(candidature);

			listener.pjModified(pieceJustif, candidatureSave);

			Notification.show(applicationContext.getMessage("window.upload.success", new Object[] {file.getFileName()}, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
			uw.close();

		});
		UI.getCurrent().addWindow(uw);
	}

	/** Change le statut est concerne d'une pj
	 *
	 * @param pieceJustif
	 * @param isConcerned
	 * @param candidature
	 * @param listener
	 */
	public void setIsConcernedPieceJustificative(final PjPresentation pieceJustif, final Boolean isConcerned,
			final Candidature candidature, final CandidatureListener listener) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		String user = userController.getCurrentUserLogin();
		if (isConcerned) {
			ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("pj.window.concerne", new Object[] {
					pieceJustif.getLibPj()}, UI.getCurrent().getLocale()), applicationContext.getMessage("pj.window.conditionnel.title", null, UI.getCurrent().getLocale()));
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
					List<PjCand> listePjCand = pjCandRepository.findByIdIdPjAndCandidatureCandidatIdCandidatAndCandidatureFormationTemDematFormOrderByDatModPjCandDesc(pieceJustif.getPieceJustif().getIdPj(), candidature.getCandidat().getIdCandidat(), true);
					if (listePjCand != null && listePjCand.size() > 0) {
						// on cherche d'abord en priorité si la pièce est présente sur la candidature
						Optional<PjCand> pjCandOpt = listePjCand.stream().filter(e -> e.getCandidature().getIdCand().equals(pieceJustif.getIdCandidature())).findFirst();
						if (pjCandOpt.isPresent()) {
							pjCand = pjCandOpt.get();
						} else {
							pjCand = listePjCand.get(0);
						}
					}
				} else {
					PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(), candidature.getIdCand());
					pjCand = pjCandRepository.findOne(pk);
				}

				if (pjCand != null && pjCand.getFichier() == null) {
					pjCandRepository.delete(pjCand);
					candidature.setUserModCand(user);
					candidature.setDatModCand(LocalDateTime.now());
					candidature.removePjCand(pjCand);

					TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
					pieceJustif.setCodStatut(statutAtt.getCodTypStatutPiece());
					pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
					pieceJustif.setDatModification(null);

					Candidature candidatureSave = candidatureRepository.save(candidature);
					listener.pjModified(pieceJustif, candidatureSave);
				}
			});
			UI.getCurrent().addWindow(confirmWindow);
		} else {
			ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("pj.window.nonConcerne", new Object[] {
					pieceJustif.getLibPj()}, UI.getCurrent().getLocale()), applicationContext.getMessage("pj.window.conditionnel.title", null, UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}
				if (isPjModified(pieceJustif, candidature, true, listener)) {
					return;
				}

				PjCand pjCand = null;
				PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(), candidature.getIdCand());
				if (pieceJustif.getPJCommune()) {
					List<PjCand> listePjCand = pjCandRepository.findByIdIdPjAndCandidatureCandidatIdCandidatAndCandidatureFormationTemDematFormOrderByDatModPjCandDesc(pieceJustif.getPieceJustif().getIdPj(), candidature.getCandidat().getIdCandidat(), true);
					if (listePjCand != null && listePjCand.size() > 0) {
						// on cherche d'abord en priorité si la pièce est présente sur la candidature
						Optional<PjCand> pjCandOpt = listePjCand.stream().filter(e -> e.getCandidature().getIdCand().equals(pieceJustif.getIdCandidature())).findFirst();
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

					TypeStatutPiece statutNotConcern = tableRefController.getTypeStatutPieceNonConcerne();
					pieceJustif.setCodStatut(statutNotConcern.getCodTypStatutPiece());
					pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutNotConcern.getI18nLibTypStatutPiece()));

					pjCand.setTypeStatutPiece(statutNotConcern);
					pjCand = pjCandRepository.saveAndFlush(pjCand);

					// obligé de recharger l'objet car le datetime est arrondi :(
					PjCand pjCandSave = pjCandRepository.findOne(pk);

					pieceJustif.setFilePj(null);
					pieceJustif.setCodStatut(statutNotConcern.getCodTypStatutPiece());
					pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutNotConcern.getI18nLibTypStatutPiece()));
					pieceJustif.setIdCandidature(candidature.getIdCand());
					pieceJustif.setDatModification(pjCandSave.getDatModPjCand());

					candidature.setUserModCand(user);
					candidature.updatePjCand(pjCandSave);
					candidature.setDatModCand(LocalDateTime.now());
					Candidature candidatureSave = candidatureRepository.save(candidature);
					listener.pjModified(pieceJustif, candidatureSave);
				}

			});

			UI.getCurrent().addWindow(confirmWindow);
		}
	}

	/** Change le statut est concerné d'un formulaire
	 *
	 * @param formulaire
	 * @param isConcerned
	 * @param candidature
	 * @param listener
	 */
	public void setIsConcernedFormulaire(final FormulairePresentation formulaire, final Boolean isConcerned,
			final Candidature candidature, final CandidatureListener listener) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		String user = userController.getCurrentUserLogin();
		if (isConcerned) {
			ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("formulaire.window.concerne", new Object[] {
					formulaire.getLibFormulaire()}, UI.getCurrent().getLocale()), applicationContext.getMessage("formulaire.window.conditionnel.title", null, UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}
				FormulaireCandPK pk = new FormulaireCandPK(formulaire.getFormulaire().getIdFormulaire(), candidature.getIdCand());
				FormulaireCand formulaireCand = formulaireCandRepository.findOne(pk);
				if (formulaireCand != null) {
					formulaireCandRepository.delete(formulaireCand);
					candidature.setUserModCand(user);
					candidature.setDatModCand(LocalDateTime.now());
					candidature.removeFormulaireCand(formulaireCand);

					TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
					formulaire.setCodStatut(statutAtt.getCodTypStatutPiece());
					formulaire.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));

					Candidature candidatureSave = candidatureRepository.save(candidature);
					listener.formulaireModified(formulaire, candidatureSave);
				}
			});
			UI.getCurrent().addWindow(confirmWindow);
		} else {
			ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("formulaire.window.nonConcerne", new Object[] {
					formulaire.getLibFormulaire()}, UI.getCurrent().getLocale()), applicationContext.getMessage("formulaire.window.conditionnel.title", null, UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(event -> {
				/* Verrou */
				if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
					return;
				}
				FormulaireCandPK pk = new FormulaireCandPK(formulaire.getFormulaire().getIdFormulaire(), candidature.getIdCand());
				FormulaireCand formulaireCand = formulaireCandRepository.findOne(pk);
				if (formulaireCand == null) {

					formulaireCand = new FormulaireCand(pk, user, candidature, formulaire.getFormulaire());
					formulaireCand.setUserModFormulaireCand(user);

					TypeStatutPiece statutNotConcern = tableRefController.getTypeStatutPieceNonConcerne();
					formulaire.setCodStatut(statutNotConcern.getCodTypStatutPiece());
					formulaire.setLibStatut(i18nController.getI18nTraduction(statutNotConcern.getI18nLibTypStatutPiece()));
					formulaireCand = formulaireCandRepository.save(formulaireCand);

					candidature.setUserModCand(user);
					candidature.updateFormulaireCand(formulaireCand);
					candidature.setDatModCand(LocalDateTime.now());
					Candidature candidatureSave = candidatureRepository.save(candidature);
					listener.formulaireModified(formulaire, candidatureSave);
				}

			});

			UI.getCurrent().addWindow(confirmWindow);
		}
	}

	/** Ajoute un fichier en PJ
	 *
	 * @param pieceJustif
	 * @param candidature
	 * @param listener
	 */
	public void deleteFileToPieceJustificative(final PjPresentation pieceJustif, final Candidature candidature,
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
		Fichier fichier = pieceJustif.getFilePj();
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("file.window.confirmDelete", new Object[] {
				fichier.getNomFichier()}, UI.getCurrent().getLocale()), applicationContext.getMessage("file.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(file -> {
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}
			if (isPjModified(pieceJustif, candidature, true, listener)) {
				return;
			}
			PjCandPK pk = new PjCandPK(pieceJustif.getPieceJustif().getIdPj(), pieceJustif.getIdCandidature());
			PjCand pjCand = pjCandRepository.findOne(pk);
			String user = userController.getCurrentNoDossierCptMinOrLogin();

			removeFileToPj(pjCand);

			TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
			pieceJustif.setFilePj(null);

			pieceJustif.setCodStatut(statutAtt.getCodTypStatutPiece());
			pieceJustif.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
			pieceJustif.setCommentaire(null);
			pieceJustif.setDatModification(null);

			candidature.setUserModCand(user);
			candidature.setDatModCand(LocalDateTime.now());

			candidature.removePjCand(pjCand);
			Candidature candidatureSave = candidatureRepository.save(candidature);
			listener.pjModified(pieceJustif, candidatureSave);

		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/*
	 * @Transactional(rollbackFor=FileException.class) public void
	 * removeFileToPj(PjCand pjCand) throws FileException{ Fichier fichier =
	 * pjCand.getFichier(); pjCandRepository.delete(pjCand); if (fichier != null){
	 * fileController.deleteFichier(fichier,false); } }
	 */

	/** Supprime un fichier d'une PJCand
	 *
	 * @param pjCand
	 */
	public void removeFileToPj(final PjCand pjCand) {
		Fichier fichier = pjCand.getFichier();
		PjCandPK idPjCand = pjCand.getId();
		pjCandRepository.delete(pjCand);
		if (fichier != null) {
			FichierFiabilisation fichierFiabilisation = new FichierFiabilisation(fichier);
			fichierFiabilisation.setIdPj(idPjCand.getIdPj());
			fichierFiabilisation.setIdCand(idPjCand.getIdCand());
			fichierFiabilisation = fichierFiabilisationRepository.save(fichierFiabilisation);
			try {
				fileController.deleteFichier(fichier);
				fichierFiabilisationRepository.delete(fichierFiabilisation);
			} catch (FileException e) {
			}
		}
	}

	/** Utilisé dans le bacth de destruction Les fichiers physiques doivent être
	 * supprimés à la main
	 *
	 * @param pjCand
	 * @throws FileException
	 */
	public void removeFileToPjManually(final PjCand pjCand) throws FileException {
		Fichier fichier = pjCand.getFichier();
		pjCandRepository.delete(pjCand);
		if (fichier != null) {
			fichierRepository.delete(fichier);
		}
	}

	/** Change le statut d'une liste de pj
	 *
	 * @param listePj
	 * @param candidature
	 * @param listener
	 */
	public void changeStatutPj(final List<PjPresentation> listePj, final Candidature candidature, final CandidatureListener listener) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}

		CtrCandActionPjWindow window = new CtrCandActionPjWindow(listePj);
		window.addChangeStatutPieceWindowListener((t, c) -> {
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return;
			}

			/* Verification de modif de piece */
			for (PjPresentation pj : listePj) {
				if (isPjModified(pj, candidature, false, listener)) {
					Notification.show(applicationContext.getMessage("pjs.modified", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}
			}

			String user = userController.getCurrentUserLogin();
			listePj.forEach(e -> {
				PjCandPK pk = new PjCandPK(e.getPieceJustif().getIdPj(), e.getIdCandidature());
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

				PjCand pjCandSave = pjCandRepository.findOne(pk);

				candidature.updatePjCand(pjCandSave);
				if (pjCandSave.getTypeStatutPiece() != null) {
					e.setLibStatut(i18nController.getI18nTraduction(pjCandSave.getTypeStatutPiece().getI18nLibTypStatutPiece()));
					e.setCodStatut(pjCandSave.getTypeStatutPiece().getCodTypStatutPiece());
				}
				e.setCommentaire(c);
				e.setDatModification(pjCandSave.getDatModPjCand());
				e.setUserModStatut(getLibModStatut(pjCand.getUserModStatutPjCand(), pjCand.getDatModStatutPjCand()));
			});
			candidature.setUserModCand(user);
			Candidature candidatureSave = candidatureRepository.save(candidature);
			listener.pjsModified(listePj, candidatureSave);
		});
		UI.getCurrent().addWindow(window);
	}

	/** @param userModStatut
	 * @param datModStatut
	 * @return le libelle de l'utilisateur ayant modifié un statut de PJ */
	private String getLibModStatut(final String userModStatut, final LocalDateTime datModStatut) {
		if (userModStatut != null && datModStatut != null) {
			return individuController.getLibIndividu(userModStatut) + " (" + formatterDateTime.format(datModStatut) + ")";
		}
		return null;
	}

	/** @param pj
	 */
	public void checkPJAdmin(final PjPresentation pj, final Candidature candidature, final CandidatureListener listener) {
		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return;
		}
		if (!userController.isAdmin()) {
			return;
		}

		PjCandPK pk = new PjCandPK(pj.getPieceJustif().getIdPj(), pj.getIdCandidature());
		PjCand pjCand = pjCandRepository.findOne(pk);
		if (pjCand == null) {
			Notification.show(applicationContext.getMessage("pj.admin.pjnotexist", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		Fichier fichier = pjCand.getFichier();
		String user = userController.getCurrentNoDossierCptMinOrLogin();
		if (fichier != null) {
			try {
				if (!fileController.testDemat(false)) {
					return;
				}
				Boolean exist = fileController.existFile(fichier);
				if (!exist) {
					ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("pj.admin.window.filenotexist", null, UI.getCurrent().getLocale()), applicationContext.getMessage("pj.admin.window.title", null, UI.getCurrent().getLocale()));
					confirmWindow.addBtnOuiListener(event -> {
						pjCandRepository.delete(pjCand);
						fichierRepository.delete(fichier);
						TypeStatutPiece statutAtt = tableRefController.getTypeStatutPieceAttente();
						pj.setFilePj(null);

						pj.setCodStatut(statutAtt.getCodTypStatutPiece());
						pj.setLibStatut(i18nController.getI18nTraduction(statutAtt.getI18nLibTypStatutPiece()));
						pj.setCommentaire(null);
						pj.setDatModification(null);
						candidature.setUserModCand(user);
						candidature.setDatModCand(LocalDateTime.now());
						candidature.removePjCand(pjCand);
						Candidature candidatureSave = candidatureRepository.save(candidature);
						listener.pjModified(pj, candidatureSave);

						Notification.show(applicationContext.getMessage("pj.admin.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
					});
					UI.getCurrent().addWindow(confirmWindow);
				} else {
					Notification.show(applicationContext.getMessage("pj.admin.error.fileexist", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
			} catch (FileException e) {
				return;
			}
		}
	}

	/** Deverse les PJ dans la table des PJ OPI
	 *
	 * @param opi
	 * @param codOpiIntEpo
	 */
	public void deversePjOpi(final Opi opi, final String codOpiIntEpo) {
		if (opi == null || opi.getDatPassageOpi() == null || opi.getCodOpi() == null) {
			return;
		}
		Candidature candidature = opi.getCandidature();

		List<PieceJustif> listPiece = pieceJustifController.getPjForCandidature(candidature, false);
		logger.debug("deversement PJ OPI " + codOpiIntEpo + " nombre de PJ : " + listPiece.size());
		listPiece.stream().filter(e -> e.getCodApoPj() != null).forEach(pj -> {
			PjCand pjCand = getPjCandFromList(pj, candidature, true);
			if (pjCand != null && pjCand.getFichier() != null && pjCand.getTypeStatutPiece() != null
					&& pjCand.getTypeStatutPiece().equals(tableRefController.getTypeStatutPieceValide())) {

				logger.debug("deversement PJ OPI " + codOpiIntEpo + " PJ : " + pjCand.getPieceJustif());
				/* On créé la clé primaire */
				PjOpiPK pk = new PjOpiPK(codOpiIntEpo, pj.getCodApoPj());

				/* On charge une eventuelle piece */
				PjOpi pjOpi = pjOpiRepository.findOne(pk);

				/* Dans le cas ou il y a deja une PJ Opi */
				if (pjOpi != null) {
					/*
					 * on va vérifier que la pièce n'a pas été déversée et que le fichier existe
					 * encore
					 */
					if (pjOpi.getDatDeversement() == null && fichierRepository.findOne(pjOpi.getIdFichier()) == null) {
						// dans ce cas, on supprime
						pjOpiRepository.delete(pjOpi);
						pjOpi = null;
					}
				}

				/* On l'insert */
				if (pjOpi == null) {
					pjOpi = new PjOpi();
					pjOpi.setId(pk);
					pjOpi.setCandidat(candidature.getCandidat());
					pjOpi.setDatDeversement(null);
					pjOpi.setIdFichier(pjCand.getFichier().getIdFichier());
					pjOpi = pjOpiRepository.save(pjOpi);
					logger.debug("Ajout PJ OPI " + pjOpi);
				}
			}
		});
	}
}
