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
package fr.univlorraine.ecandidat.views.windows;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.AdresseController;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatureController;
import fr.univlorraine.ecandidat.controllers.CandidatureCtrCandController;
import fr.univlorraine.ecandidat.controllers.CandidaturePieceController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.IndividuController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.entities.ecandidat.PostIt;
import fr.univlorraine.ecandidat.entities.ecandidat.PostIt_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureCandidatViewListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.FormulairePresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.QuestionPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileLayout;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Fenêtre d'édition de candidature
 * @author Kevin Hergalant
 */
@Configurable(preConstruction = true)
@SuppressWarnings("serial")
public class CandidatureWindow extends Window implements CandidatureListener {

	/* Champs de PJ */
	public static final String[] FIELDS_ORDER_PJ = { PjPresentation.CHAMPS_CHECK,
		PjPresentation.CHAMPS_LIB_PJ,
		PjPresentation.CHAMPS_FILE_PJ,
		PjPresentation.CHAMPS_LIB_STATUT,
		PjPresentation.CHAMPS_CONDITIONNEL,
		PjPresentation.CHAMPS_COMMENTAIRE,
		PjPresentation.CHAMPS_USER_MOD };
	/* Champs de Questions */
	public static final String[] FIELDS_ORDER_QUESTION = { QuestionPresentation.CHAMPS_LIB_QUESTION,
		QuestionPresentation.CHAMPS_LIB_STATUT,
		QuestionPresentation.CHAMPS_REPONSE,
		QuestionPresentation.CHAMPS_CONDITIONNEL };
	public static final String[] FIELDS_ORDER_FORMULAIRE = { FormulairePresentation.CHAMPS_LIB,
		FormulairePresentation.CHAMPS_URL,
		FormulairePresentation.CHAMPS_LIB_STATUT,
		FormulairePresentation.CHAMPS_CONDITIONNEL,
		FormulairePresentation.CHAMPS_REPONSES };
	public static final String[] FIELDS_ORDER_POST_IT = { PostIt_.datCrePostIt.getName(),
		PostIt_.userCrePostIt.getName(),
		PostIt_.messagePostIt.getName() };

	@Resource
	private transient ApplicationContext applicationContext;

	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient CandidatureCtrCandController candidatureCtrCandController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient AdresseController adresseController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient IndividuController individuController;

	private final Label labelPj = new Label("", ContentMode.HTML);
	private final BeanItemContainer<PjPresentation> pjContainer = new BeanItemContainer<>(PjPresentation.class);
	private final TableFormating pjTable = new TableFormating(pjContainer);

	private final BeanItemContainer<FormulairePresentation> formulaireContainer = new BeanItemContainer<>(
		FormulairePresentation.class);
	private final TableFormating formulaireTable = new TableFormating(formulaireContainer);

	private final Label labelQuestion = new Label("", ContentMode.HTML);
	private final BeanItemContainer<QuestionPresentation> questionContainer = new BeanItemContainer<>(
		QuestionPresentation.class);
	private final TableFormating questionTable = new TableFormating(questionContainer);

	private final BeanItemContainer<PostIt> postItContainer = new BeanItemContainer<>(PostIt.class);
	private Tab tabPostIt;
	private final TableFormating postItTable = new TableFormating(postItContainer);

	private final GridLayout gridInfoLayout = new GridLayout(2, 5);
	private final GridLayout gridDateLayout = new GridLayout(2, 4);

	/* La candidature */
	private Candidature candidature;
	private List<SimpleTablePresentation> listePresentation;
	private List<SimpleTablePresentation> listeDatePresentation;

	/* Composants */
	private OneClickButton btnDownload;
	private OneClickButton btnAction;
	private OneClickButton btnClose;
	private OneClickButton btnConfirm;
	private OneClickButton btnDesist;
	private OneClickButton btnCancel;
	private OneClickButton btnTransmettre;
	private OneClickButton btnDownloadLettre;

	/* Relance de formulaire */
	HorizontalLayout hlRelanceForm = new HorizontalLayout();

	/** Boolean d'autorisation de modif */
	private Boolean isDematerialise;

	/**
	 * Boolean permettant de savoir si l'utilisateur a accès aux boutons d'action
	 */
	private Boolean isAutorizedToUpdate;

	/**
	 * Boolean permettant de savoir si l'utilisateur est un gestionnaire de la
	 * candidature
	 */
	private Boolean hasAccessFenetreCand;

	/** Boolean permettant de savoir si l'utilisateur est le candidat */
	private Boolean isCandidatOfCandidature;

	/** Date limite de rerour de la candidature */
	private String dateLimiteRetour;

	/** Le listener d'ecoute de la vue candidature */
	private CandidatureCandidatViewListener candidatureCandidatListener;

	/**
	 * Crée une fenêtre d'édition de candidature
	 * @param candidatureWindow
	 * @param isLocked
	 * @param isCanceled
	 * @param archived
	 * @param listeDroit
	 */
	public CandidatureWindow(final Candidature candidatureWindow,
		final Boolean isLocked,
		final Boolean isCanceled,
		final Boolean archived,
		final List<DroitFonctionnalite> listeDroit) {
		candidature = candidatureWindow;

		/* Les droits */

		/* Est-on candidat? */
		final Boolean isCandidat = candidatureController.isCandidatOfCandidature(candidatureWindow);

		/* Est-on candidat et en mode ecriture */
		isCandidatOfCandidature = isCandidat && !isLocked && !isCanceled && !archived;

		/* Est-on gestionnaire avec accès à la fonctionnalité de gestionnaire de fenetre
		 * de cette candidature */
		hasAccessFenetreCand = droitProfilController
			.hasAccessToFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FENETRE_CAND, listeDroit, false)
			&& !isLocked
			&& !isCanceled
			&& !archived;

		/* Est-on autorisé à modifier la candidature? */
		isAutorizedToUpdate = (isCandidatOfCandidature || hasAccessFenetreCand) && !isLocked
			&& !isCanceled
			&& !archived;

		/* Temoi de demat */
		isDematerialise = candidatureController.isCandidatureDematerialise(candidatureWindow);

		/* Style */
		setModal(true);
		setSizeFull();
		setResizable(false);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaptionAsHtml(true);
		updateCaptionValue();

		/* Definition des valeurs de liste */
		listePresentation = candidatureController.getInformationsCandidature(candidature, isCandidat);
		listeDatePresentation = candidatureController.getInformationsDateCandidature(candidature, isCandidat);
		dateLimiteRetour = MethodUtils.getLibByPresentationCode(listeDatePresentation,
			"candidature." + Candidature_.formation.getName() + "." + Formation_.datRetourForm.getName());

		/* Phrase Non-Demat */
		if (!isDematerialise) {
			final Label labelNonDemat = new Label(applicationContext.getMessage("pieceJustificative.nodemat.title",
				new Object[] { dateLimiteRetour },
				UI.getCurrent().getLocale()));
			labelNonDemat.addStyleName(StyleConstants.LABEL_SAUT_LIGNE);
			labelNonDemat.setSizeUndefined();
			labelNonDemat.addStyleName(ValoTheme.LABEL_H4);
			labelNonDemat.addStyleName(StyleConstants.LABEL_MORE_BOLD);
			layout.addComponent(labelNonDemat);
			layout.setComponentAlignment(labelNonDemat, Alignment.MIDDLE_CENTER);
		}

		/* Info + adresse */
		final HorizontalLayout hlPresentationAdr = new HorizontalLayout();
		hlPresentationAdr.setSizeFull();
		hlPresentationAdr.setSpacing(true);
		layout.addComponent(hlPresentationAdr);
		layout.setExpandRatio(hlPresentationAdr, 1);

		/* Grid d'info */
		gridInfoLayout.setSizeUndefined();
		gridInfoLayout.setWidth(100, Unit.PERCENTAGE);
		gridInfoLayout.setMargin(true);
		gridInfoLayout.setSpacing(true);
		gridInfoLayout.setColumnExpandRatio(0, 0);
		gridInfoLayout.setColumnExpandRatio(1, 1);
		final Panel panelInfo = new Panel(
			applicationContext.getMessage("candidature.info.title", null, UI.getCurrent().getLocale()),
			gridInfoLayout);
		panelInfo.addStyleName(StyleConstants.PANEL_COLORED);
		panelInfo.setSizeFull();
		hlPresentationAdr.addComponent(panelInfo);
		hlPresentationAdr.setExpandRatio(panelInfo, 1);
		updateCandidaturePresentation(listePresentation);

		/* Grid des dates */
		gridDateLayout.setSizeUndefined();
		gridDateLayout.setWidth(100, Unit.PERCENTAGE);
		gridDateLayout.setMargin(true);
		gridDateLayout.setSpacing(true);
		gridDateLayout.setColumnExpandRatio(0, 0);
		gridDateLayout.setColumnExpandRatio(1, 1);
		final Panel panelDateInfo = new Panel(
			applicationContext.getMessage("candidature.info.date.title", null, UI.getCurrent().getLocale()),
			gridDateLayout);
		panelDateInfo.addStyleName(StyleConstants.PANEL_COLORED);
		panelDateInfo.setSizeFull();
		hlPresentationAdr.addComponent(panelDateInfo);
		hlPresentationAdr.setExpandRatio(panelDateInfo, 0.7f);
		updateCandidatureDatePresentation(listeDatePresentation);

		/* Adresse de contact */
		final VerticalLayout vlAdr = new VerticalLayout();
		vlAdr.setSizeUndefined();
		vlAdr.setWidth(100, Unit.PERCENTAGE);
		vlAdr.setMargin(true);
		final Panel panelAdr = new Panel(
			applicationContext.getMessage("candidature.adresse.title", null, UI.getCurrent().getLocale()),
			vlAdr);
		panelAdr.addStyleName(StyleConstants.PANEL_COLORED);
		panelAdr.setSizeFull();
		hlPresentationAdr.addComponent(panelAdr);
		hlPresentationAdr.setExpandRatio(panelAdr, 0.7f);

		final Label labelAdr = new Label(
			adresseController.getLibelleAdresseCommission(candidature.getFormation().getCommission(), "<br>"));
		labelAdr.setContentMode(ContentMode.HTML);
		labelAdr.addStyleName(StyleConstants.LABEL_SAUT_LIGNE);
		vlAdr.addComponent(labelAdr);

		/* Ce listener pour mettre a jour la window */
		final CandidatureListener listener = this;

		/* Onglets */
		final TabSheet sheet = new TabSheet();
		sheet.addStyleName(StyleConstants.TABSHEET_LARGE_CAPTION);
		// sheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		sheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
		sheet.setImmediate(true);
		sheet.setSizeFull();
		layout.addComponent(sheet);
		layout.setExpandRatio(sheet, 2);

		/* Boolean pour l'affichage de l'information sur le tabsheet */
		Boolean isTabFormulaireDisplay = false;
		Boolean isTabQuestionDisplay = false;
		Boolean isTabInfoCompDisplay = false;

		/* Les pieces */
		final VerticalLayout vlPJ = new VerticalLayout();
		vlPJ.setSizeFull();
		vlPJ.setSpacing(true);
		sheet.addTab(vlPJ,
			applicationContext.getMessage("candidature.pj", null, UI.getCurrent().getLocale()),
			FontAwesome.FILE_TEXT_O);

		final HorizontalLayout hlTitlePj = new HorizontalLayout();
		hlTitlePj.setWidth(100, Unit.PERCENTAGE);
		hlTitlePj.addStyleName(StyleConstants.MOYENNE_MARGE);
		hlTitlePj.setSpacing(true);

		labelPj.addStyleName(ValoTheme.LABEL_BOLD);
		hlTitlePj.addComponent(labelPj);
		hlTitlePj.setComponentAlignment(labelPj, Alignment.MIDDLE_LEFT);
		hlTitlePj.setExpandRatio(labelPj, 1);

		final List<PjPresentation> listePj = candidaturePieceController.getPjCandidature(candidature);
		pjContainer.addAll(listePj);

		if (hasAccessFenetreCand) {
			/* Bouton tout selectionner */
			final OneClickButton selectAllPjBtn = new OneClickButton(null, FontAwesome.RECYCLE);
			selectAllPjBtn.setDescription(applicationContext.getMessage("pieceJustificative.allselect.libbtn",
				null,
				UI.getCurrent().getLocale()));
			selectAllPjBtn.addStyleName(ValoTheme.BUTTON_SMALL);
			selectAllPjBtn.addClickListener(e -> {
				final List<PjPresentation> listeToCheck = pjContainer.getItemIds()
					.stream()
					.filter(pj -> !pj.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE))
					.collect(Collectors.toList());
				listeToCheck.forEach(pj -> {
					if (pj instanceof PjPresentation) {
						if (pj.getPjCandidatFromApogee() == null) {
							pj.setCheck(true);
						} else {
							pj.setCheck(false);
						}
					}
				});
				pjTable.refreshRowCache();
			});

			hlTitlePj.addComponent(selectAllPjBtn);
			hlTitlePj.setComponentAlignment(selectAllPjBtn, Alignment.MIDDLE_CENTER);

			/* Bouton tout deselectionner */
			final OneClickButton deselectAllPjBtn = new OneClickButton(null, FontAwesome.REFRESH);
			deselectAllPjBtn.setDescription(applicationContext.getMessage("pieceJustificative.alldeselect.libbtn",
				null,
				UI.getCurrent().getLocale()));
			deselectAllPjBtn.addStyleName(ValoTheme.BUTTON_SMALL);
			deselectAllPjBtn.addClickListener(e -> {
				final List<PjPresentation> listeToCheck = pjContainer.getItemIds()
					.stream()
					.collect(Collectors.toList());
				listeToCheck.forEach(pj -> {
					if (pj instanceof PjPresentation) {
						pj.setCheck(false);
					}
				});
				pjTable.refreshRowCache();
			});

			hlTitlePj.addComponent(deselectAllPjBtn);
			hlTitlePj.setComponentAlignment(deselectAllPjBtn, Alignment.MIDDLE_CENTER);

			/* Bouton enregistrer etat PJ */
			final OneClickButton editPj = new OneClickButton(null, FontAwesome.PENCIL);
			editPj.setDescription(applicationContext.getMessage("pieceJustificative.noselected.libbtn",
				null,
				UI.getCurrent().getLocale()));
			editPj.addStyleName(ValoTheme.BUTTON_SMALL);
			editPj.addClickListener(e -> {
				final List<PjPresentation> listeCheck = pjContainer.getItemIds()
					.stream()
					.filter(pj -> pj.getCheck() != null && pj.getCheck() == true)
					.collect(Collectors.toList());
				if (listeCheck.size() == 0) {
					Notification.show(applicationContext.getMessage("pieceJustificative.noselected",
						null,
						UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				} else {
					Boolean trouveNonConcerne = false;
					final String codeNonConcerne = NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE;

					for (final PjPresentation pj : listeCheck) {
						if (pj.getCodStatut().equals(codeNonConcerne)) {
							trouveNonConcerne = true;
						}
					}
					if (!trouveNonConcerne) {
						candidaturePieceController.changeStatutPj(listeCheck, candidature, listener);
					} else {
						Notification.show(applicationContext.getMessage("pieceJustificative.nonconcerned",
							null,
							UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					}
				}
			});
			hlTitlePj.addComponent(editPj);
			hlTitlePj.setComponentAlignment(editPj, Alignment.MIDDLE_RIGHT);
		}

		vlPJ.addComponent(hlTitlePj);

		/* Table des pj */
		if (!fileController.getModeDematBackoffice().equals(ConstanteUtils.TYPE_FICHIER_STOCK_NONE)) {
			pjTable.addGeneratedColumn(PjPresentation.CHAMPS_LIB_PJ, new ColumnGenerator() {
				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final PjPresentation pieceJustif = (PjPresentation) itemId;
					final Fichier file = pieceJustif.getPieceJustif().getFichier();
					final String libPj = pieceJustif.getLibPj();
					if (pieceJustif.getPieceJustif().getFichier() == null) {
						return new Label(libPj);
					} else {
						final OnDemandFileLayout fileLayout = new OnDemandFileLayout(libPj);

						/* Viewer si JPG */
						if (MethodUtils.isImgFileName(file.getNomFichier())) {
							fileLayout.addBtnViewerClickListener(e -> {
								if (!fileController.existFileInDb(file)) {
									return;
								}
								final InputStream is = fileController.getInputStreamFromFichier(file);
								final ImageViewerWindow iv = new ImageViewerWindow(
									new OnDemandFile(file.getNomFichier(), is),
									null);
								UI.getCurrent().addWindow(iv);
							});
							/* Opener si PDF */
						} else if (MethodUtils.isPdfFileName(file.getNomFichier())) {
							fileLayout.addBtnViewerPdfBrowserOpener(new OnDemandStreamFile() {
								@Override
								public OnDemandFile getOnDemandFile() {
									if (!fileController.existFileInDb(file)) {
										return null;
									}
									final InputStream is = fileController.getInputStreamFromFichier(file);
									return new OnDemandFile(file.getNomFichier(), is);
								}
							});
						}
						/* Download */
						fileLayout.addBtnDownloadFileDownloader(new OnDemandStreamFile() {
							@Override
							public OnDemandFile getOnDemandFile() {
								if (!fileController.existFileInDb(file)) {
									return null;
								}
								final InputStream is = fileController.getInputStreamFromFichier(file);
								if (is != null) {
									return new OnDemandFile(file.getNomFichier(), is);
								}
								return null;

							}
						});

						return fileLayout;
					}
				}

			});
		}

		/* Variable utilisée pour compter le nombre de PJ ou forumlaires qui possedent
		 * des boutons de modifications Utilisé si le lock d'une candidature doit être
		 * supprimé pour un candidat ou non */
		final AtomicInteger nbPjOrFormOrQuestionUpdatable = new AtomicInteger(0);

		String[] fieldsOrderPjToUse = FIELDS_ORDER_PJ;
		/* On est en dématerialisé, on affiche les colonnes d'edition de piece */
		if (isDematerialise) {
			labelPj.setValue(applicationContext.getMessage("pieceJustificative.demat",
				new Object[] { dateLimiteRetour },
				UI.getCurrent().getLocale()));
			pjTable.addGeneratedColumn(PjPresentation.CHAMPS_FILE_PJ, new ColumnGenerator() {
				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final PjPresentation pieceJustif = (PjPresentation) itemId;
					if (pieceJustif.getFilePj() == null
						&& !pieceJustif.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)) {
						if (isAutorizedToUpdatePJ(pieceJustif.getCodStatut())) {
							final OneClickButton btnAdd = new OneClickButton(FontAwesome.PLUS);
							btnAdd.addStyleName(StyleConstants.ON_DEMAND_FILE_LAYOUT);
							btnAdd.setDescription(
								applicationContext.getMessage("file.btnAdd", null, UI.getCurrent().getLocale()));
							btnAdd.addClickListener(e -> {
								// desactivation du controle sur l'ajout de fichier-->nettoyage dans le batch
								/* if (candidaturePieceController.controlPjAdd(pieceJustif, candidature,
								 * listener)){ return; } */
								if (candidaturePieceController.isPjModified(pieceJustif, candidature, true, listener)) {
									return;
								}
								candidaturePieceController.addFileToPieceJustificative(pieceJustif,
									candidature,
									listener);
							});
							// on incremente le nombre de PJ ou formulaire updatable-->Savoirt si on
							// supprime le lock à la fin
							nbPjOrFormOrQuestionUpdatable.getAndAdd(1);
							return btnAdd;
						} else {
							return null;
						}

					} else if (!pieceJustif.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)) {
						final OnDemandFileLayout fileLayout = new OnDemandFileLayout(
							pieceJustif.getFilePj().getNomFichier());
						if (isAutorizedToUpdatePJ(pieceJustif.getCodStatut())
							&& pieceJustif.getPjCandidatFromApogee() == null) {
							/* Bouton suppression */
							fileLayout.addBtnDelClickListener(e -> {
								if (candidaturePieceController.isPjModified(pieceJustif, candidature, true, listener)) {
									return;
								}
								candidaturePieceController.deleteFileToPieceJustificative(pieceJustif,
									candidature,
									listener);
							});
							// on incremente le nombre de PJ ou formulaire updatable-->Savoir si on supprime
							// le lock à la fin
							nbPjOrFormOrQuestionUpdatable.getAndAdd(1);
						}
						/* Viewer si JPG */
						if (MethodUtils.isImgFileName(pieceJustif.getFilePj().getNomFichier())) {
							fileLayout.addBtnViewerClickListener(e -> {
								if (candidaturePieceController.isPjModified(pieceJustif, candidature, true, listener)) {
									return;
								}
								final InputStream is = fileController.getInputStreamFromPjPresentation(pieceJustif);
								if (is != null) {
									final ImageViewerWindow iv = new ImageViewerWindow(
										new OnDemandFile(pieceJustif.getFilePj().getNomFichier(), is),
										null);
									UI.getCurrent().addWindow(iv);
								}
							});
							/* Opener si PDF */
						} else if (MethodUtils.isPdfFileName(pieceJustif.getFilePj().getNomFichier())) {
							fileLayout.addBtnViewerPdfBrowserOpener(new OnDemandStreamFile() {

								@Override
								public OnDemandFile getOnDemandFile() {
									if (candidaturePieceController.isPjModified(pieceJustif,
										candidature,
										true,
										listener)) {
										return null;
									}
									final InputStream is = fileController.getInputStreamFromPjPresentation(pieceJustif);
									if (is != null) {
										return new OnDemandFile(pieceJustif.getFilePj().getNomFichier(), is);
									}
									return null;
								}
							});
						}

						/* Bouton download */
						fileLayout.addBtnDownloadFileDownloader(new OnDemandStreamFile() {
							@Override
							public OnDemandFile getOnDemandFile() {
								if (candidaturePieceController.isPjModified(pieceJustif, candidature, true, listener)) {
									return null;
								}
								final InputStream is = fileController.getInputStreamFromPjPresentation(pieceJustif);
								if (is != null) {
									return new OnDemandFile(pieceJustif.getFilePj().getNomFichier(), is);
								}
								return null;
							}
						});

						/* Bouton d'admin */
						if (parametreController.getIsEnableAdminPJ() && userController.isAdmin()
							&& isAutorizedToUpdate) {
							fileLayout.addBtnAdminClickListener(e -> {
								if (candidaturePieceController.isPjModified(pieceJustif, candidature, true, listener)) {
									return;
								}
								candidaturePieceController.checkPJAdmin(pieceJustif, candidature, listener);
							});
						}

						return fileLayout;
					}
					return null;
				}
			});
		} else {
			labelPj.setValue(
				applicationContext.getMessage("pieceJustificative.nodemat", null, UI.getCurrent().getLocale()));
			fieldsOrderPjToUse = ArrayUtils.removeElement(fieldsOrderPjToUse, PjPresentation.CHAMPS_FILE_PJ);
		}

		/* Si le gestionnaire a droit de toucher aux pièces */
		if (hasAccessFenetreCand) {
			pjTable.addGeneratedColumn(PjPresentation.CHAMPS_CHECK, new ColumnGenerator() {

				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final PjPresentation pj = (PjPresentation) itemId;
					if (pj.getPjCandidatFromApogee() == null) {
						final CheckBox cb = new CheckBox();
						cb.setValue(pj.getCheck());
						cb.addValueChangeListener(e -> {
							pj.setCheck(cb.getValue());
						});
						return cb;
					}
					return null;
				}
			});
		} else {
			fieldsOrderPjToUse = ArrayUtils.removeElement(fieldsOrderPjToUse, PjPresentation.CHAMPS_CHECK);
			fieldsOrderPjToUse = ArrayUtils.removeElement(fieldsOrderPjToUse, PjPresentation.CHAMPS_USER_MOD);
		}

		if (listePj.stream().filter(e -> e.getPJConditionnel()).count() > 0) {
			pjTable.addGeneratedColumn(PjPresentation.CHAMPS_CONDITIONNEL, new ColumnGenerator() {

				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final PjPresentation pieceJustif = (PjPresentation) itemId;

					if (pieceJustif.getFilePj() == null && pieceJustif.getPJConditionnel()) {
						if (isAutorizedToUpdatePJ(pieceJustif.getCodStatut())) {
							// on incremente le nombre de PJ ou formulaire updatable-->Savoir si on supprime
							// le lock à la fin
							nbPjOrFormOrQuestionUpdatable.getAndAdd(1);

							if (!pieceJustif.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)) {
								final OneClickButton btn = new OneClickButton(applicationContext
									.getMessage("pj.btn.nonConcerne", null, UI.getCurrent().getLocale()),
									FontAwesome.THUMBS_O_DOWN);
								btn.addClickListener(e -> {
									if (candidaturePieceController.isPjModified(pieceJustif,
										candidature,
										true,
										listener)) {
										return;
									}
									candidaturePieceController.setIsConcernedPieceJustificative(pieceJustif,
										false,
										candidature,
										listener);
								});
								return getLayoutBtnConditionnel(btn);
							} else {
								final OneClickButton btn = new OneClickButton(applicationContext.getMessage(
									"pj.btn.concerne",
									null,
									UI.getCurrent().getLocale()), FontAwesome.THUMBS_O_UP);
								btn.addClickListener(e -> {
									if (candidaturePieceController.isPjModified(pieceJustif,
										candidature,
										true,
										listener)) {
										return;
									}
									candidaturePieceController.setIsConcernedPieceJustificative(pieceJustif,
										true,
										candidature,
										listener);
								});
								return getLayoutBtnConditionnel(btn);
							}
						} else {
							if (pieceJustif.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)) {
								return applicationContext.getMessage("pj.btn.nonConcerne",
									null,
									UI.getCurrent().getLocale());
							}
						}
					}
					return null;
				}
			});
		} else {
			fieldsOrderPjToUse = ArrayUtils.removeElement(fieldsOrderPjToUse, PjPresentation.CHAMPS_CONDITIONNEL);
		}

		pjTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
		pjTable.addStyleName(StyleConstants.TABLE_BORDER_TOP);
		pjTable.setVisibleColumns((Object[]) fieldsOrderPjToUse);
		for (final String fieldName : fieldsOrderPjToUse) {
			if (PjPresentation.CHAMPS_CHECK.equals(fieldName)) {
				pjTable.setColumnHeader(fieldName, "");
			} else {
				pjTable.setColumnHeader(fieldName,
					applicationContext.getMessage("pieceJustificative." + fieldName,
						null,
						UI.getCurrent().getLocale()));
			}
		}
		pjTable.setColumnCollapsingAllowed(true);
		pjTable.setColumnReorderingAllowed(true);
		pjTable.setSortContainerPropertyId(PjPresentation.CHAMPS_ORDER);
		pjTable.setSelectable(false);
		pjTable.setImmediate(true);
		pjTable.setSizeFull();
		pjTable.setColumnWidth(PjPresentation.CHAMPS_USER_MOD, 160);
		pjTable.setColumnWidth(PjPresentation.CHAMPS_LIB_PJ, 500);
		pjTable.setColumnWidth(PjPresentation.CHAMPS_FILE_PJ, 350);
		vlPJ.addComponent(pjTable);
		vlPJ.setExpandRatio(pjTable, 1);

		/* Formulaires */
		final List<FormulairePresentation> listeFormulaire = candidaturePieceController
			.getFormulaireCandidature(candidature);

		if (listeFormulaire.size() > 0) {
			isTabFormulaireDisplay = true;
			final VerticalLayout vlForm = new VerticalLayout();
			vlForm.setSizeFull();
			vlForm.setSpacing(true);
			sheet.addTab(vlForm,
				applicationContext.getMessage("candidature.formulaire", null, UI.getCurrent().getLocale()),
				FontAwesome.PENCIL_SQUARE_O);

			/* HorizontalLayout de relance */
			hlRelanceForm.addStyleName(StyleConstants.MOYENNE_MARGE);
			hlRelanceForm.setSpacing(true);

			final Label labelRelanceForm = new Label(
				applicationContext.getMessage("formulaireComp.relance.label", null, UI.getCurrent().getLocale()),
				ContentMode.HTML);
			labelRelanceForm.addStyleName(ValoTheme.LABEL_BOLD);
			hlRelanceForm.addComponent(labelRelanceForm);
			hlRelanceForm.setComponentAlignment(labelRelanceForm, Alignment.MIDDLE_LEFT);
			hlRelanceForm.setExpandRatio(labelRelanceForm, 1);

			/* Bouton Relance */
			final OneClickButton btnRelance = new OneClickButton(
				applicationContext.getMessage("formulaireComp.relance.btn", null, UI.getCurrent().getLocale()),
				FontAwesome.ENVELOPE_O);
			btnRelance.setDescription(
				applicationContext.getMessage("formulaireComp.relance.btn", null, UI.getCurrent().getLocale()));
			btnRelance.addStyleName(ValoTheme.BUTTON_SMALL);
			btnRelance.addClickListener(e -> {
				candidaturePieceController.relanceFormulaires(getListToRelance(), candidature);
			});

			hlRelanceForm.addComponent(btnRelance);
			hlRelanceForm.setComponentAlignment(btnRelance, Alignment.MIDDLE_RIGHT);
			vlForm.addComponent(hlRelanceForm);

			final HorizontalLayout hlTitleForm = new HorizontalLayout();
			hlTitleForm.addStyleName(StyleConstants.MOYENNE_MARGE);
			hlTitleForm.setWidth(100, Unit.PERCENTAGE);
			hlTitleForm.setSpacing(true);

			final Label labelFormulaire = new Label(
				applicationContext.getMessage("formulaireComp.title", null, UI.getCurrent().getLocale()),
				ContentMode.HTML);
			labelFormulaire.addStyleName(ValoTheme.LABEL_BOLD);
			hlTitleForm.addComponent(labelFormulaire);
			hlTitleForm.setComponentAlignment(labelFormulaire, Alignment.MIDDLE_LEFT);
			hlTitleForm.setExpandRatio(labelFormulaire, 1);

			/* Bouton Voir les réponses */
			final OneClickButton showResponse = new OneClickButton(
				applicationContext.getMessage("formulaireComp.btn.show.reponse", null, UI.getCurrent().getLocale()),
				FontAwesome.SEARCH_PLUS);
			showResponse.setEnabled(false);
			showResponse.setDescription(applicationContext.getMessage("formulaireComp.btn.show.reponse",
				null,
				UI.getCurrent().getLocale()));
			showResponse.addStyleName(ValoTheme.BUTTON_SMALL);
			showResponse.addClickListener(e -> {
				if (formulaireTable.getValue() instanceof FormulairePresentation) {
					final FormulairePresentation pres = (FormulairePresentation) formulaireTable.getValue();
					if (pres.getReponses() != null) {
						String ret = "";
						final StringTokenizer st = new StringTokenizer(pres.getReponses(), "\\{;\\}");

						while (st.hasMoreElements()) {
							ret = ret + st.nextElement() + "<br>";
						}

						UI.getCurrent()
							.addWindow(new InfoWindow(applicationContext.getMessage("formulaireComp.reponses",
								null,
								UI.getCurrent().getLocale()), ret, 500, 70));
					}

				}
			});

			hlTitleForm.addComponent(showResponse);
			hlTitleForm.setComponentAlignment(showResponse, Alignment.MIDDLE_RIGHT);

			vlForm.addComponent(hlTitleForm);

			/* Ajout des elements formulaire dans le container */
			formulaireContainer.addAll(listeFormulaire);

			/* La table */
			formulaireTable.addGeneratedColumn(FormulairePresentation.CHAMPS_URL, new ColumnGenerator() {
				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final FormulairePresentation formulaire = (FormulairePresentation) itemId;
					final String url = formulaire.getUrlFormulaire();
					if (url != null
						&& !formulaire.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)) {
						final OneClickButton urlBtn = new OneClickButton(url, FontAwesome.FILE_ZIP_O);
						urlBtn.addStyleName(ValoTheme.BUTTON_LINK);
						urlBtn.addStyleName(StyleConstants.INVERTED_LINK);
						final BrowserWindowOpener urlBwo = new BrowserWindowOpener(url);
						urlBwo.extend(urlBtn);
						return urlBtn;
					} else if (url != null) {
						return url;
					}
					return null;
				}

			});
			formulaireTable.addGeneratedColumn(FormulairePresentation.CHAMPS_REPONSES, new ColumnGenerator() {
				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final FormulairePresentation formulaire = (FormulairePresentation) itemId;
					if (formulaire.getReponses() != null) {
						final String ret = formulaire.getReponses().replaceAll("\\{;\\}", " / ");
						return MethodUtils.replaceLast(ret, " / ", "");
					}
					return null;
				}

			});

			String[] fieldsOrderFormulaireToUse = FIELDS_ORDER_FORMULAIRE;

			if (listeFormulaire.stream().filter(e -> e.getConditionnel()).count() > 0) {
				formulaireTable.addGeneratedColumn(FormulairePresentation.CHAMPS_CONDITIONNEL, new ColumnGenerator() {
					@Override
					public Object generateCell(final Table source, final Object itemId, final Object columnId) {
						final FormulairePresentation formulaire = (FormulairePresentation) itemId;
						if (formulaire.getConditionnel()) {
							if (isAutorizedToUpdatePJ(formulaire.getCodStatut())) {
								// on incremente le nombre de PJ ou formulaire updatable-->Savoir si on supprime
								// le lock à la fin
								nbPjOrFormOrQuestionUpdatable.getAndAdd(1);

								if (!formulaire.getCodStatut()
									.equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)) {
									final OneClickButton btn = new OneClickButton(
										applicationContext.getMessage("formulaire.btn.nonConcerne",
											null,
											UI.getCurrent().getLocale()),
										FontAwesome.THUMBS_O_DOWN);
									btn.addClickListener(e -> {
										candidaturePieceController.setIsConcernedFormulaire(formulaire,
											false,
											candidature,
											listener);
									});
									return getLayoutBtnConditionnel(btn);
								} else {
									final OneClickButton btn = new OneClickButton(applicationContext
										.getMessage("formulaire.btn.concerne", null, UI.getCurrent().getLocale()),
										FontAwesome.THUMBS_O_UP);
									btn.addClickListener(e -> {
										candidaturePieceController.setIsConcernedFormulaire(formulaire,
											true,
											candidature,
											listener);
									});
									return getLayoutBtnConditionnel(btn);
								}
							} else {
								if (formulaire.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)) {
									return applicationContext.getMessage("formulaire.btn.nonConcerne",
										null,
										UI.getCurrent().getLocale());
								}
							}
						}
						return null;
					}
				});
			} else {
				fieldsOrderFormulaireToUse = ArrayUtils.removeElement(fieldsOrderFormulaireToUse,
					FormulairePresentation.CHAMPS_CONDITIONNEL);
			}

			/* Table des formulaires */
			formulaireTable.setVisibleColumns((Object[]) fieldsOrderFormulaireToUse);
			for (final String fieldName : fieldsOrderFormulaireToUse) {
				formulaireTable.setColumnHeader(fieldName,
					applicationContext.getMessage("formulaireComp." + fieldName,
						null,
						UI.getCurrent().getLocale()));
			}
			formulaireTable.setColumnCollapsingAllowed(true);
			formulaireTable.setColumnReorderingAllowed(true);
			formulaireTable.setSelectable(true);
			formulaireTable.setImmediate(true);
			formulaireTable.setSortContainerPropertyId(FormulairePresentation.CHAMPS_LIB);
			formulaireTable.addValueChangeListener(e -> {
				/* Les boutons d'ouverture de reponse. */
				if (formulaireTable.getValue() instanceof FormulairePresentation) {
					final FormulairePresentation pres = (FormulairePresentation) formulaireTable.getValue();
					if (pres.getReponses() != null) {
						showResponse.setEnabled(true);
						return;
					}
				}
				showResponse.setEnabled(false);
			});

			vlForm.addComponent(formulaireTable);
			vlForm.setExpandRatio(formulaireTable, 1);
			formulaireTable.setSizeFull();

			/* Mise a jour du layout de relance */
			updateRelanceFormLayout();
		}

		/* Les questions */
		final List<QuestionPresentation> listeQuestion = candidaturePieceController.getQuestionCandidature(candidature);
		if (listeQuestion.size() > 0) {
			isTabQuestionDisplay = true;
			final VerticalLayout vlQuestion = new VerticalLayout();
			vlQuestion.setSizeFull();
			vlQuestion.setSpacing(true);
			sheet.addTab(vlQuestion,
				applicationContext.getMessage("candidature.question", null, UI.getCurrent().getLocale()),
				FontAwesome.QUESTION);

			final HorizontalLayout hlTitleQuestion = new HorizontalLayout();
			hlTitleQuestion.setWidth(100, Unit.PERCENTAGE);
			hlTitleQuestion.addStyleName(StyleConstants.MOYENNE_MARGE);
			hlTitleQuestion.setSpacing(true);

			labelQuestion.addStyleName(ValoTheme.LABEL_BOLD);
			hlTitleQuestion.addComponent(labelQuestion);
			hlTitleQuestion.setComponentAlignment(labelQuestion, Alignment.MIDDLE_LEFT);
			hlTitleQuestion.setExpandRatio(labelQuestion, 1);

			questionContainer.addAll(listeQuestion);

			vlQuestion.addComponent(hlTitleQuestion);

			/* Table des Questions */
			String[] fieldsOrderQuestionToUse = FIELDS_ORDER_QUESTION;

			questionTable.addGeneratedColumn(QuestionPresentation.CHAMPS_REPONSE, new ColumnGenerator() {

				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final QuestionPresentation question = (QuestionPresentation) itemId;

					if (!question.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)) {
						String reponse = question.getReponse() != null ? question.getReponse() : "";
						if (reponse.length() > 100) {
							reponse = reponse.substring(0, 100) + "....";
						}
						final Label reponseLabel = new Label(reponse);
						reponseLabel.setDescription(question.getReponse() != null ? question.getReponse() : "");
						if (isAutorizedToUpdateQuestion(question.getCodStatut())) {
							// on incremente le nombre de PJ ou formulaire ou question updatable-->Savoir si
							// on supprime le lock à la fin
							nbPjOrFormOrQuestionUpdatable.getAndAdd(1);

							/* Bouton d'edition */
							final OneClickButton btnAnswer = new OneClickButton(FontAwesome.PENCIL);
							btnAnswer.addStyleName(StyleConstants.ON_DEMAND_FILE_LAYOUT);
							btnAnswer.setDescription(
								applicationContext.getMessage("question.btnAnswer", null, UI.getCurrent().getLocale()));
							btnAnswer.addClickListener(e -> {
								if (candidaturePieceController.isQuestionModified(question, candidature, true, listener)) {
									return;
								}
								candidaturePieceController.addReponseToQuestion(question, candidature, listener);
							});

							/* Layout de retour */
							final HorizontalLayout layout = new HorizontalLayout(btnAnswer);
							layout.setSpacing(true);

							if (question.getReponse() != null) {
								/* Suppression d'une réponse */
								final OneClickButton btnDelete = new OneClickButton(FontAwesome.TRASH);
								btnDelete.addStyleName(StyleConstants.ON_DEMAND_FILE_LAYOUT);
								btnDelete.setDescription(
									applicationContext.getMessage("question.btnDeleteAnswer", null, UI.getCurrent().getLocale()));
								btnDelete.addClickListener(e -> {
									if (candidaturePieceController.isQuestionModified(question, candidature, true, listener)) {
										return;
									}
									candidaturePieceController.deleteReponseToQuestion(question, candidature, listener);
								});

								/* Visu d'une réponse */
								final OneClickButton btnViewer = new OneClickButton(FontAwesome.EYE);
								btnViewer.addStyleName(StyleConstants.ON_DEMAND_FILE_LAYOUT);
								btnViewer.setDescription(
									applicationContext.getMessage("question.btnShowAnswer", null, UI.getCurrent().getLocale()));
								btnViewer.addClickListener(e -> {
									if (candidaturePieceController.isQuestionModified(question, candidature, true, listener)) {
										return;
									}
									final CandidatQuestionWindow textWindow = new CandidatQuestionWindow(question,
										applicationContext.getMessage("question.window.reponseTitle", null, UI.getCurrent().getLocale()),
										true);
									UI.getCurrent().addWindow(textWindow);
								});

								layout.addComponents(btnDelete, btnViewer);
							}
							layout.addComponents(reponseLabel);
							layout.setComponentAlignment(reponseLabel, Alignment.MIDDLE_LEFT);
							return layout;
						} else {
							return reponseLabel;
						}
					}
					return null;
				}
			});

			if (listeQuestion.stream().filter(e -> e.getQuestionConditionnel()).count() > 0) {
				questionTable.addGeneratedColumn(QuestionPresentation.CHAMPS_CONDITIONNEL, new ColumnGenerator() {

					@Override
					public Object generateCell(final Table source, final Object itemId, final Object columnId) {
						final QuestionPresentation question = (QuestionPresentation) itemId;

						if (question.getReponse() == null && question.getQuestionConditionnel()) {
							if (isAutorizedToUpdateQuestion(question.getCodStatut())) {
								// on incremente le nombre de PJ ou formulaire ou question updatable-->Savoir si
								// on supprime
								// le lock à la fin
								nbPjOrFormOrQuestionUpdatable.getAndAdd(1);

								if (!question.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)) {
									final OneClickButton btn = new OneClickButton(applicationContext
										.getMessage("question.btn.nonConcerne", null, UI.getCurrent().getLocale()),
										FontAwesome.THUMBS_O_DOWN);
									btn.addClickListener(e -> {
										if (candidaturePieceController.isQuestionModified(question,
											candidature,
											true,
											listener)) {
											return;
										}
										candidaturePieceController.setIsConcernedQuestion(question,
											false,
											candidature,
											listener);
									});
									return getLayoutBtnConditionnel(btn);
								} else {
									final OneClickButton btn = new OneClickButton(applicationContext
										.getMessage("question.btn.concerne", null, UI.getCurrent().getLocale()),
										FontAwesome.THUMBS_O_UP);
									btn.addClickListener(e -> {
										if (candidaturePieceController.isQuestionModified(question,
											candidature,
											true,
											listener)) {
											return;
										}
										candidaturePieceController.setIsConcernedQuestion(question,
											true,
											candidature,
											listener);
									});
									return getLayoutBtnConditionnel(btn);
								}
							} else {
								if (question.getCodStatut().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)) {
									return applicationContext.getMessage("question.btn.nonConcerne",
										null,
										UI.getCurrent().getLocale());
								}
							}
						}
						return null;
					}
				});
			} else

			{
				fieldsOrderQuestionToUse = ArrayUtils.removeElement(fieldsOrderQuestionToUse,
					QuestionPresentation.CHAMPS_CONDITIONNEL);
			}

			questionTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
			questionTable.addStyleName(StyleConstants.TABLE_BORDER_TOP);
			questionTable.setVisibleColumns((Object[]) fieldsOrderQuestionToUse);
			for (final String fieldName : fieldsOrderQuestionToUse) {
				questionTable.setColumnHeader(fieldName,
					applicationContext.getMessage("question." + fieldName, null, UI.getCurrent().getLocale()));
			}
			questionTable.setColumnCollapsingAllowed(true);
			questionTable.setColumnReorderingAllowed(true);
			questionTable.setSortContainerPropertyId(QuestionPresentation.CHAMPS_ORDER);
			questionTable.setSelectable(false);
			questionTable.setImmediate(true);
			questionTable.setSizeFull();
			questionTable.setColumnWidth(QuestionPresentation.CHAMPS_LIB_QUESTION, 500);
			questionTable.setColumnWidth(QuestionPresentation.CHAMPS_REPONSE, 750);
			vlQuestion.addComponent(questionTable);
			vlQuestion.setExpandRatio(questionTable, 1);
		}

		/* Sheet info comp */
		// String infoComp = candidature.getFormation().getInfoCompForm();
		final String infoComp = i18nController.getI18nTraduction(candidature.getFormation().getI18nInfoCompForm());
		if (infoComp != null && !infoComp.equals("")) {
			isTabInfoCompDisplay = true;
			final VerticalLayout vlInfoComp = new VerticalLayout();
			vlInfoComp.setSizeFull();

			final VerticalLayout vlInfoCompContent = new VerticalLayout();
			vlInfoCompContent.setSizeUndefined();
			vlInfoCompContent.setWidth(100, Unit.PERCENTAGE);
			vlInfoCompContent.setMargin(true);

			final Label labelInfoComp = new Label(infoComp);
			labelInfoComp.setContentMode(ContentMode.HTML);
			labelInfoComp.addStyleName(StyleConstants.LABEL_SAUT_LIGNE);
			// labelInfoComp.remoStyleName("v-label-undef-w");
			vlInfoCompContent.addComponent(labelInfoComp);

			final Panel panelInfoComp = new Panel(vlInfoCompContent);
			panelInfoComp.setSizeFull();
			panelInfoComp.addStyleName(StyleConstants.PANEL_WITHOUT_BORDER);
			vlInfoComp.addComponent(panelInfoComp);
			vlInfoComp.setExpandRatio(panelInfoComp, 1);

			sheet.addTab(vlInfoComp,
				applicationContext.getMessage("candidature.infoscomp", null, UI.getCurrent().getLocale()),
				FontAwesome.INFO);
		}

		/* Les postIt */
		/* Est-on autorisé à lire le bloc-notes? */
		if (droitProfilController.hasAccessToFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_POST_IT,
			listeDroit,
			true)) {
			final VerticalLayout vlPostIt = new VerticalLayout();
			vlPostIt.setSizeFull();

			/* Verification que l'utilisateur a le droit d'ecrire un postit */
			if (!isLocked && !isCanceled
				&& !archived
				&& droitProfilController
					.hasAccessToFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_POST_IT, listeDroit, false)) {
				final OneClickButton btnWrite = new OneClickButton(
					applicationContext.getMessage("postit.add.button", null, UI.getCurrent().getLocale()),
					FontAwesome.EDIT);
				btnWrite.addClickListener(e -> {
					final CtrCandPostItAddWindow window = new CtrCandPostItAddWindow(
						new PostIt(userController.getCurrentUserLogin(), candidature));
					window.addPostItWindowListener(p -> {
						addPostIt(p);
					});
					UI.getCurrent().addWindow(window);
				});
				final OneClickButton btnDelete = new OneClickButton(
					applicationContext.getMessage("postit.delete.button", null, UI.getCurrent().getLocale()),
					FontAwesome.TRASH);
				btnDelete.addClickListener(e -> {
					final ConfirmWindow confirmWindow = new ConfirmWindow(
						applicationContext.getMessage("postit.window.confirmDelete",
							null,
							UI.getCurrent().getLocale()),
						applicationContext.getMessage("postit.window.confirmDeleteTitle",
							null,
							UI.getCurrent().getLocale()));
					confirmWindow.addBtnOuiListener(f -> {
						final PostIt postIt = (PostIt) postItTable.getValue();
						candidatureCtrCandController.deletePostIt(postIt);
						removePostIt(postIt);
					});
					UI.getCurrent().addWindow(confirmWindow);
				});
				btnDelete.setEnabled(false);
				postItTable.addValueChangeListener(e -> {
					final PostIt postIt = (PostIt) postItTable.getValue();
					if (postIt != null && postIt.getUserCrePostIt() != null
						&& (postIt.getUserCrePostIt().equals(userController.getCurrentUserLogin())
							|| userController.isAdmin())) {
						btnDelete.setEnabled(true);
					} else {
						btnDelete.setEnabled(false);
					}
				});

				final HorizontalLayout buttonsPostiItLayout = new HorizontalLayout();
				buttonsPostiItLayout.setWidth(100, Unit.PERCENTAGE);
				buttonsPostiItLayout.setMargin(true);
				buttonsPostiItLayout.addComponent(btnWrite);
				buttonsPostiItLayout.setComponentAlignment(btnWrite, Alignment.MIDDLE_CENTER);
				buttonsPostiItLayout.addComponent(btnDelete);
				buttonsPostiItLayout.setComponentAlignment(btnWrite, Alignment.MIDDLE_CENTER);
				vlPostIt.addComponent(buttonsPostiItLayout);
				postItTable.addStyleName(StyleConstants.TABLE_BORDER_TOP);
			}
			postItTable.addItemSetChangeListener(e -> postItTable.sanitizeSelection());
			postItTable.addStyleName(ValoTheme.TABLE_BORDERLESS);
			postItTable.setColumnCollapsingAllowed(false);
			postItTable.setColumnReorderingAllowed(false);
			postItTable.setSortContainerPropertyId(PostIt_.datCrePostIt.getName());
			postItTable.setColumnWidth(PostIt_.datCrePostIt.getName(), 180);
			postItTable.setColumnWidth(PostIt_.userCrePostIt.getName(), 180);
			postItTable.setSortAscending(false);
			postItTable.setSelectable(true);
			postItTable.setImmediate(true);
			postItTable.setSizeFull();
			postItTable.setVisibleColumns((Object[]) FIELDS_ORDER_POST_IT);
			for (final String fieldName : FIELDS_ORDER_POST_IT) {
				postItTable.setColumnHeader(fieldName,
					applicationContext.getMessage("postit.table." + fieldName, null, UI.getCurrent().getLocale()));
			}
			postItTable.addGeneratedColumn(PostIt_.userCrePostIt.getName(), new ColumnGenerator() {
				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final PostIt postIt = (PostIt) itemId;
					final String user = postIt.getUserCrePostIt();
					return individuController.getLibIndividu(user);
				}
			});

			vlPostIt.addComponent(postItTable);
			vlPostIt.setExpandRatio(postItTable, 1f);
			postItContainer.addAll(candidatureCtrCandController.getPostIt(candidature));
			postItTable.sort();
			tabPostIt = sheet.addTab(vlPostIt,
				applicationContext.getMessage("postit.candidature.sheet", null, UI.getCurrent().getLocale()),
				FontAwesome.COMMENTS);
			majTabPostItCaption();
		}

		/* On defini le caption du tabsheet */
		if (isTabFormulaireDisplay || isTabQuestionDisplay || isTabInfoCompDisplay) {
			sheet.setCaptionAsHtml(true);
			sheet.setIcon(FontAwesome.WARNING);
			final List<String> listSheetDisplay = new ArrayList<String>();
			if (isTabFormulaireDisplay) {
				listSheetDisplay.add((applicationContext.getMessage("candidature.warning.onglet.formulairecomp", null, UI.getCurrent().getLocale())));
			}
			if (isTabInfoCompDisplay) {
				listSheetDisplay.add((applicationContext.getMessage("candidature.warning.onglet.infocomp", null, UI.getCurrent().getLocale())));
			}
			if (isTabQuestionDisplay) {
				listSheetDisplay.add((applicationContext.getMessage("candidature.warning.onglet.question", null, UI.getCurrent().getLocale())));
			}

			/* Un seul onglet */
			if (listSheetDisplay.size() == 1) {
				sheet.setCaption(applicationContext.getMessage("candidature.warning.onglet", new Object[] { listSheetDisplay.get(0) }, UI.getCurrent().getLocale()));
			}
			/* Plusieurs onglets */
			if (listSheetDisplay.size() > 1) {
				sheet.setCaption(applicationContext.getMessage("candidature.warning.onglets", new Object[] { String.join(", ", listSheetDisplay) }, UI.getCurrent().getLocale()));
			}
		}

		/* Ajoute les boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		/* Fermeture candidature */
		btnClose = new OneClickButton(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()),
			FontAwesome.TIMES);
		btnClose.addClickListener(e ->

		close());
		buttonsLayout.addComponent(btnClose);
		buttonsLayout.setComponentAlignment(btnClose, Alignment.MIDDLE_LEFT);

		/* Bouton d'action */
		if (listeDroit != null && listeDroit.stream()
			.filter(e -> !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_FENETRE_CAND))
			.count() > 0
			&& !isLocked
			&& !isCanceled
			&& !archived) {
			btnAction = new OneClickButton(
				applicationContext.getMessage("btnAction", null, UI.getCurrent().getLocale()),
				FontAwesome.GAVEL);
			btnAction.addClickListener(e -> {
				candidatureCtrCandController.editActionCandidature(candidature, listener, listeDroit);
			});
			buttonsLayout.addComponent(btnAction);
			buttonsLayout.setComponentAlignment(btnAction, Alignment.MIDDLE_CENTER);
		}

		/* Bouton de confirmation */
		btnConfirm = new OneClickButton(
			applicationContext.getMessage("candidature.confirmation", null, UI.getCurrent().getLocale()),
			FontAwesome.THUMBS_UP);
		btnConfirm.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		btnConfirm.addClickListener(e -> {
			candidatureController.setConfirmationCandidature(candidature, true, listener);
		});
		buttonsLayout.addComponent(btnConfirm);
		buttonsLayout.setComponentAlignment(btnConfirm, Alignment.MIDDLE_CENTER);

		/* Bouton de desistement */
		btnDesist = new OneClickButton(
			applicationContext.getMessage("candidature.desistement", null, UI.getCurrent().getLocale()),
			FontAwesome.THUMBS_DOWN);
		btnDesist.addStyleName(ValoTheme.BUTTON_DANGER);
		btnDesist.addClickListener(e -> {
			candidatureController.setConfirmationCandidature(candidature, false, listener);
		});
		buttonsLayout.addComponent(btnDesist);
		buttonsLayout.setComponentAlignment(btnDesist, Alignment.MIDDLE_CENTER);

		/* Bouton d'annulation */
		btnCancel = new OneClickButton(
			applicationContext.getMessage("candidature.cancel", null, UI.getCurrent().getLocale()),
			FontAwesome.ERASER);
		btnCancel.addClickListener(e -> {
			candidatureController.cancelCandidature(candidature, listener, candidatureCandidatListener);
		});
		buttonsLayout.addComponent(btnCancel);
		buttonsLayout.setComponentAlignment(btnCancel, Alignment.MIDDLE_CENTER);

		/* Bouton de transmission */
		btnTransmettre = new OneClickButton(
			applicationContext.getMessage("candidature.transmettre", null, UI.getCurrent().getLocale()),
			FontAwesome.SEND);
		btnTransmettre.addClickListener(e -> {
			candidaturePieceController.transmettreCandidatureAfterClick(candidature,
				pjContainer.getItemIds(),
				formulaireContainer.getItemIds(),
				questionContainer.getItemIds(),
				this);
		});
		buttonsLayout.addComponent(btnTransmettre);
		buttonsLayout.setComponentAlignment(btnTransmettre, Alignment.MIDDLE_CENTER);

		/* Bouton lettre admission */
		btnDownloadLettre = new OneClickButton(FontAwesome.ENVELOPE);
		buttonsLayout.addComponent(btnDownloadLettre);
		buttonsLayout.setComponentAlignment(btnDownloadLettre, Alignment.MIDDLE_RIGHT);
		new OnDemandFileDownloader(new OnDemandStreamFile() {
			@Override
			public OnDemandFile getOnDemandFile() {
				final String locale = i18nController.getLangueCandidat();
				return new OnDemandFile(
					candidatureController.getNomFichierLettre(candidatureWindow,
						ConstanteUtils.TYP_LETTRE_DOWNLOAD,
						locale),
					candidatureController.downloadLettre(candidature,
						ConstanteUtils.TYP_LETTRE_DOWNLOAD,
						locale,
						true));
			}
		}, btnDownloadLettre);

		updateBtnAction();
		updateBtnTransmettre();

		/* Bouton de téléchargement */
		btnDownload = new OneClickButton(
			applicationContext.getMessage("candidature.download", null, UI.getCurrent().getLocale()),
			FontAwesome.CLOUD_DOWNLOAD);
		btnDownload.addStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonsLayout.addComponent(btnDownload);
		buttonsLayout.setComponentAlignment(btnDownload, Alignment.MIDDLE_RIGHT);

		new OnDemandFileDownloader(new OnDemandStreamFile() {
			@Override
			public OnDemandFile getOnDemandFile() {
				/* Verification de modif de piece */
				for (final PjPresentation pj : pjContainer.getItemIds()) {
					if (candidaturePieceController.isPjModified(pj, candidature, false, listener)) {
						Notification.show(
							applicationContext.getMessage("pjs.modified", null, UI.getCurrent().getLocale()),
							Type.WARNING_MESSAGE);
						return null;
					}
				}
				return candidatureController.downloadDossier(candidature,
					listePresentation,
					listeDatePresentation,
					pjContainer.getItemIds(),
					true,
					formulaireContainer.getItemIds(),
					questionContainer.getItemIds());
			}
		}, btnDownload);

		/* Suppression évenutelle du lock pour le candidat-->A faire apres l'attach pour
		 * que les colonnes générées soit générées On vérifie que les différents boutons
		 * d'action ne sont pas visibles ou utilisables * */
		addAttachListener(e -> {
			if (isCandidatOfCandidature && !(nbPjOrFormOrQuestionUpdatable.get() > 0)
				&& !btnConfirm.isVisible()
				&& !btnDesist.isVisible()
				&& !btnCancel.isVisible()
				&& (!btnTransmettre.isVisible()
					|| !btnTransmettre.getStyleName().equals(ValoTheme.BUTTON_FRIENDLY))) {
				candidatureController.removeLockCandidat(candidatureWindow);
			}
		});

		/* Centre la fenêtre */
		center();
	}

	/** @return la liste des formulaires à relancer */
	private List<FormulairePresentation> getListToRelance() {
		return formulaireContainer.getItemIds()
			.stream()
			.filter(e -> e.getReponses() == null
				&& !e.getCodStatut().equals(tableRefController.getTypeStatutPieceNonConcerne().getCodTypStatutPiece()))
			.collect(Collectors.toList());
	}

	/** Met a jour le layout de relance de formulaires */
	private void updateRelanceFormLayout() {
		/* Si gestionnaire avec les droits d'acces sur la fenêtre */
		if (hasAccessFenetreCand) {
			/* Vérification que la liste de relance n'est pas vide */
			if (getListToRelance().size() > 0) {
				hlRelanceForm.setVisible(true);
				return;
			}
		}
		/* Dans tous les autres cas, le layout est invisible */
		hlRelanceForm.setVisible(false);
	}

	/** Modifie le titre de la fenetre */
	private void updateCaptionValue() {
		final StringBuilder sb = new StringBuilder("");
		/* Ajoute les tags */
		if (!isCandidatOfCandidature && candidature.getTags() != null) {
			candidature.getTags()
				.stream()
				.filter(e -> e.getTesTag())
				.map(e -> MethodUtils.getHtmlColoredSquare(e.getColorTag(), e.getLibTag(), 20, null) + "&nbsp;")
				.forEach(sb::append);
		}
		setCaption(sb.toString() + applicationContext.getMessage("candidature.window.title",
			new Object[] { candidatController.getLibelleTitle(candidature.getCandidat().getCompteMinima()) },
			UI.getCurrent().getLocale()));
	}

	/**
	 * @param  btn
	 * @return     le layout de bouton conditionnel
	 */
	private HorizontalLayout getLayoutBtnConditionnel(final OneClickButton btn) {
		btn.addStyleName(ValoTheme.BUTTON_TINY);
		final HorizontalLayout layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.addComponent(btn);
		layout.setComponentAlignment(btn, Alignment.MIDDLE_CENTER);
		return layout;
	}

	/** Met a jour le nombre de post-it */
	private void majTabPostItCaption() {
		try {
			if (tabPostIt != null) {
				String captionPostIt = applicationContext.getMessage("postit.candidature.sheet",
					null,
					UI.getCurrent().getLocale());
				final Integer nbPostIt = postItContainer.getItemIds().size();
				if (nbPostIt > 0) {
					captionPostIt = captionPostIt + "*";
				}
				tabPostIt.setCaption(captionPostIt);
				tabPostIt.setDescription(applicationContext.getMessage("postit.candidature.sheet.desc",
					new Object[] { nbPostIt },
					UI.getCurrent().getLocale()));
			}
		} catch (final Exception e) {
		}
	}

	/** Modifie l'etat des boutons de transmission */
	private void updateBtnTransmettre() {
		if (!isAutorizedToUpdate || !isDematerialise
			|| !isAutorizedToUpdateCandidature()
			|| !candidaturePieceController
				.isOkToTransmettreCandidatureStatutDossier(candidature.getTypeStatut().getCodTypStatut(), false)) {
			btnTransmettre.setVisible(false);
		} else {
			btnTransmettre.setVisible(true);
			if (candidaturePieceController.isOkToTransmettreCandidatureStatutPiece(pjContainer.getItemIds(), false)
				&& candidaturePieceController
					.isOkToTransmettreCandidatureFormulaire(formulaireContainer.getItemIds(), false)
				&& candidaturePieceController
					.isOkToTransmettreCandidatureQuestion(questionContainer.getItemIds(), false)) {
				btnTransmettre.addStyleName(ValoTheme.BUTTON_FRIENDLY);
			} else {
				btnTransmettre.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
			}
		}
	}

	/** Modifie l'etat des boutons d'annulation, de confirmation et desistement */
	private void updateBtnAction() {
		if (isAutorizedToUpdate && candidature != null) {
			final TypeDecisionCandidature td = candidature.getLastTypeDecision();

			/* Mise a jour des boutons de confirmation et desistement */
			if (td != null && td.getTypeDecision().getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_FAV)
				&& td.getTemValidTypeDecCand()
				&& candidature.getTemAcceptCand() == null
				&& candidature.getDatAnnulCand() == null) {
				if (hasAccessFenetreCand) {
					btnConfirm.setVisible(true);
					btnDesist.setVisible(true);
				} else if (isAutorizedToConfirmCandidature()) {
					btnConfirm.setVisible(true);
					btnDesist.setVisible(true);
				} else {
					btnConfirm.setVisible(false);
					btnDesist.setVisible(false);
				}
			} else if (td != null
				&& td.getTypeDecision().getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_FAV)
				&& td.getTemValidTypeDecCand()
				&& candidature.getTemAcceptCand() != null
				&& candidature.getTemAcceptCand()
				&& candidature.getDatAnnulCand() == null
				&& (
				// soit on est gestionnaire et le bouton desist s'affiche
				hasAccessFenetreCand ||
				// soit on est candidat et on est ok dans les dates
					isAutorizedToConfirmCandidature())) {
				btnConfirm.setVisible(false);
				btnDesist.setVisible(true);
			} else {
				btnConfirm.setVisible(false);
				btnDesist.setVisible(false);
			}

			if (td == null && candidature.getTypeStatut().getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_ATT)) {
				btnCancel.setVisible(true);
			} else {
				btnCancel.setVisible(false);
			}

		} else {
			btnConfirm.setVisible(false);
			btnDesist.setVisible(false);
			btnCancel.setVisible(false);
		}

		/* Update des bouton de download de lettre */

		/* Lettre d'admission */
		final String typeLettre = candidatureController.getTypeLettre(candidature, ConstanteUtils.TYP_LETTRE_DOWNLOAD);
		if (typeLettre != null && typeLettre.equals(ConstanteUtils.TEMPLATE_LETTRE_ADM)) {
			btnDownloadLettre.setCaption(applicationContext.getMessage("candidature.lettre.download.adm",
				null,
				UI.getCurrent().getLocale()));
			btnDownloadLettre.setVisible(true);
		}
		/* Lettre de refus */
		else if (typeLettre != null && typeLettre.equals(ConstanteUtils.TEMPLATE_LETTRE_REFUS)) {
			btnDownloadLettre.setCaption(applicationContext.getMessage("candidature.lettre.download.ref",
				null,
				UI.getCurrent().getLocale()));
			btnDownloadLettre.setVisible(true);
		} else {
			btnDownloadLettre.setVisible(false);
		}
	}

	/**
	 * Met a jour le panel d'info
	 * @param listePresentation
	 */
	private void updateCandidaturePresentation(final List<SimpleTablePresentation> listePresentation) {
		int i = 0;
		gridInfoLayout.removeAllComponents();
		gridInfoLayout.setRows(listePresentation.size());
		for (final SimpleTablePresentation e : listePresentation) {
			final Label title = new Label(e.getTitle());
			title.addStyleName(ValoTheme.LABEL_BOLD);
			title.setSizeUndefined();
			gridInfoLayout.addComponent(title, 0, i);
			final Label value = new Label((String) e.getValue(), ContentMode.HTML);
			if ((e.getCode().equals("candidature." + ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION)
				&& e.getShortValue() != null
				&& !e.getShortValue().equals(NomenclatureUtils.TYP_AVIS_ATTENTE))
				|| (e.getCode().equals("candidature." + ConstanteUtils.CANDIDATURE_LIB_STATUT)
					&& e.getShortValue() != null
					&& !e.getShortValue().equals(NomenclatureUtils.TYPE_STATUT_ATT))) {
				title.addStyleName(ValoTheme.LABEL_COLORED);
				value.addStyleName(ValoTheme.LABEL_COLORED);
				value.addStyleName(ValoTheme.LABEL_BOLD);
			}
			value.setWidth(100, Unit.PERCENTAGE);
			gridInfoLayout.addComponent(value, 1, i);
			i++;
		}
	}

	/**
	 * Met à jour le panel de dates
	 * @param listePresentation
	 */
	private void updateCandidatureDatePresentation(final List<SimpleTablePresentation> listePresentation) {
		int i = 0;
		gridDateLayout.removeAllComponents();
		if (listePresentation.size() > 0) {
			gridDateLayout.setRows(listePresentation.size());
			for (final SimpleTablePresentation e : listePresentation) {
				final Label title = new Label(e.getTitle());
				title.addStyleName(ValoTheme.LABEL_BOLD);
				title.setSizeUndefined();
				gridDateLayout.addComponent(title, 0, i);
				final Label value = new Label((String) e.getValue());
				if (e.getCode()
					.equals(
						"candidature." + Candidature_.formation.getName() + "." + Formation_.datRetourForm.getName())) {
					title.addStyleName(ValoTheme.LABEL_COLORED);
					value.addStyleName(ValoTheme.LABEL_COLORED);
					value.addStyleName(ValoTheme.LABEL_BOLD);
				}
				value.setWidth(100, Unit.PERCENTAGE);
				gridDateLayout.addComponent(value, 1, i);
				i++;
			}
		}
	}

	/**
	 * @return true si l'utilisateur a le droit de confirmer sa candidature les pj
	 */
	private Boolean isAutorizedToConfirmCandidature() {
		if (!isCandidatOfCandidature) {
			return false;
		}
		final LocalDate dateConfirm = candidatureController.getDateConfirmCandidat(candidature);
		if (dateConfirm == null || (dateConfirm != null
			&& (dateConfirm.isAfter(LocalDate.now()) || dateConfirm.isEqual(LocalDate.now())))) {
			return true;
		}
		return false;
	}

	/** @return true si l'utilisateur a le droit de modifier les pj */
	private Boolean isAutorizedToUpdateCandidature() {
		if (isAutorizedToUpdate) {
			if (!candidatureController.getDateRetourCandidat(candidature).isBefore(LocalDate.now())
				|| hasAccessFenetreCand) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param  pieceJustif
	 * @return             true si l'utilisateur a le droit de modifier la pj
	 */
	private Boolean isAutorizedToUpdatePJ(final String codStatutPiece) {
		if (hasAccessFenetreCand && isAutorizedToUpdate) {
			return true;
		}
		if (!isAutorizedToUpdateCandidature()) {
			return false;
		} else {
			final String statutCandidature = candidature.getTypeStatut().getCodTypStatut();
			if (statutCandidature.equals(NomenclatureUtils.TYPE_STATUT_REC)
				|| (statutCandidature.equals(NomenclatureUtils.TYPE_STATUT_COM))) {
				return false;
			} else {
				if (codStatutPiece.equals(NomenclatureUtils.TYP_STATUT_PIECE_VALIDE)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @param  question
	 * @return          true si l'utilisateur a le droit de modifier la question
	 */
	private Boolean isAutorizedToUpdateQuestion(final String codStatutPiece) {
		if (hasAccessFenetreCand && isAutorizedToUpdate) {
			return true;
		}
		if (!isAutorizedToUpdateCandidature()) {
			return false;
		} else {
			final String statutCandidature = candidature.getTypeStatut().getCodTypStatut();
			if (statutCandidature.equals(NomenclatureUtils.TYPE_STATUT_REC)
				|| (statutCandidature.equals(NomenclatureUtils.TYPE_STATUT_COM))) {
				return false;
			} else {
				if (codStatutPiece.equals(NomenclatureUtils.TYP_STATUT_PIECE_VALIDE)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Ajoute un listener de candidature
	 * @param candidatureCandidatListener
	 */
	public void addCandidatureCandidatListener(final CandidatureCandidatViewListener candidatureCandidatListener) {
		this.candidatureCandidatListener = candidatureCandidatListener;
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#pjModified(fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation,
	 *      fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void pjModified(final PjPresentation pieceJustif, final Candidature candidature) {
		pjContainer.removeItem(pieceJustif);

		// nouveau V2.3.0 : on filtre sur les type de traitement
		if (pieceJustif.getPieceJustif().getTypeTraitement() == null
			|| pieceJustif.getPieceJustif().getTypeTraitement().equals(candidature.getTypeTraitement())) {
			pjContainer.addBean(pieceJustif);
		} else {
			Notification.show(
				applicationContext.getMessage("pj.typetrait.different", null, UI.getCurrent().getLocale()),
				Type.TRAY_NOTIFICATION);
		}

		pjTable.sort();
		this.candidature = candidature;
		updateBtnTransmettre();
		candidaturePieceController.transmettreCandidatureAfterDepot(this.candidature,
			pjContainer.getItemIds(),
			formulaireContainer.getItemIds(),
			questionContainer.getItemIds(),
			this,
			dateLimiteRetour);
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#questionModified(fr.univlorraine.ecandidat.utils.bean.presentation.QuestionPresentation,
	 *      fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void questionModified(final QuestionPresentation question, final Candidature candidature) {
		questionContainer.removeItem(question);
		questionContainer.addBean(question);
		questionTable.sort();
		this.candidature = candidature;
		updateBtnTransmettre();
		candidaturePieceController.transmettreCandidatureAfterDepot(this.candidature,
			pjContainer.getItemIds(),
			formulaireContainer.getItemIds(),
			questionContainer.getItemIds(),
			this,
			dateLimiteRetour);

	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#formulaireModified(fr.univlorraine.ecandidat.utils.bean.presentation.FormulairePresentation,
	 *      fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void formulaireModified(final FormulairePresentation formulaire, final Candidature candidature) {
		formulaireContainer.removeItem(formulaire);
		formulaireContainer.addBean(formulaire);
		formulaireTable.sort();
		this.candidature = candidature;
		updateRelanceFormLayout();
		updateBtnTransmettre();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#candidatureCanceled(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void candidatureCanceled(final Candidature candidature) {
		close();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#pjsModified(java.util.List,
	 *      fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void pjsModified(final List<PjPresentation> listePj, final Candidature candidature) {
		listePj.forEach(e -> {
			e.setCheck(false);
			pjContainer.removeItem(e);
			pjContainer.addBean(e);
		});
		pjTable.sort();
		this.candidature = candidature;
		updateBtnTransmettre();
	}

	/* (non-Javadoc)
	 *
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#
	 * reloadAllPiece(java.util.List) */
	@Override
	public void reloadAllPiece(final List<PjPresentation> listePj, final Candidature candidatureLoad) {
		pjContainer.removeAllItems();
		pjContainer.addAll(listePj);
		pjTable.sort();
		candidature = candidatureLoad;
		updateBtnTransmettre();
	}

	/* (non-Javadoc)
	 *
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#
	 * reloadAllQuestion(java.util.List) */
	@Override
	public void reloadAllQuestion(final List<QuestionPresentation> listeQuestion, final Candidature candidatureLoad) {
		questionContainer.removeAllItems();
		questionContainer.addAll(listeQuestion);
		questionTable.sort();
		candidature = candidatureLoad;
		updateBtnTransmettre();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#infosCandidatureModified(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void infosCandidatureModified(final Candidature candidature) {
		/* Infos */
		listePresentation = candidatureController.getInformationsCandidature(candidature, isCandidatOfCandidature);
		updateCandidaturePresentation(listePresentation);
		/* Dates */
		listeDatePresentation = candidatureController.getInformationsDateCandidature(candidature,
			isCandidatOfCandidature);
		updateCandidatureDatePresentation(listeDatePresentation);

		this.candidature = candidature;
		this.candidature.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(candidature));
		updateBtnAction();
		updateBtnTransmettre();
		updateCaptionValue();
		if (candidatureCandidatListener != null) {
			candidatureCandidatListener.statutDossierModified(this.candidature);
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#transmissionDossier(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void transmissionDossier(final Candidature candidatureSave) {
		infosCandidatureModified(candidatureSave);
		/* on trie la table pour mettre a jour les boutons de delete de fichier et ne
		 * plus les afficher si c'est transmis */
		pjTable.sort();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#openCandidat()
	 */
	@Override
	public void openCandidat() {
		close();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#candidatureDeleted(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void candidatureDeleted(final Candidature candidature) {
		close();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener#candidatureAnnulCanceled(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void candidatureAnnulCanceled(final Candidature candidatureSave) {
		close();
	}

	@Override
	public void addPostIt(final PostIt postIt) {
		postItContainer.addItem(postIt);
		postItTable.sort();
		majTabPostItCaption();
	}

	@Override
	public void removePostIt(final PostIt postIt) {
		postItContainer.removeItem(postIt);
		postItTable.sort();
		majTabPostItCaption();
	}
}
