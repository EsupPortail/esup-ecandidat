/** ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License. */
package fr.univlorraine.ecandidat.views.template;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid.MultiSelectionModel;
import com.vaadin.ui.Grid.RowReference;
import com.vaadin.ui.Grid.RowStyleGenerator;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SingleSelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.PopupView.Content;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.AdresseController;
import fr.univlorraine.ecandidat.controllers.AlertSvaController;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.CandidatureController;
import fr.univlorraine.ecandidat.controllers.CandidatureCtrCandController;
import fr.univlorraine.ecandidat.controllers.CandidaturePieceController;
import fr.univlorraine.ecandidat.controllers.CommissionController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.PreferenceController;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.AlertSva;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat_;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement_;
import fr.univlorraine.ecandidat.services.security.SecurityCommissionFonc;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.ComboBoxFilterPresentation;
import fr.univlorraine.ecandidat.vaadin.components.GridConverter.TagToHtmlSquareConverter;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxCommission;
import fr.univlorraine.ecandidat.views.windows.CtrCandExportWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandPreferenceViewWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandPreferenceViewWindow.PreferenceViewListener;

public class CandidatureViewTemplate extends VerticalLayout {

	/** serialVersionUID **/
	private static final long serialVersionUID = -6354555121920208233L;

	private static final String LAST_TYPE_DECISION_PREFIXE = "lastTypeDecision.";

	public static final String[] FIELDS_ORDER = {Candidature_.tag.getName(),
			Candidature_.candidat.getName() + "." + Candidat_.compteMinima.getName() + "." + CompteMinima_.numDossierOpiCptMin.getName(),
			Candidature_.candidat.getName() + "." + Candidat_.nomPatCandidat.getName(), Candidature_.candidat.getName() + "." + Candidat_.prenomCandidat.getName(),
			Candidature_.candidat.getName() + "." + Candidat_.compteMinima.getName() + "." + CompteMinima_.temFcCptMin.getName(), Candidature_.formation.getName() + "." + Formation_.codForm.getName(),
			Candidature_.formation.getName() + "." + Formation_.libForm.getName(), Candidature_.typeStatut.getName() + "." + TypeStatut_.libTypStatut.getName(),
			Candidature_.typeTraitement.getName() + "." + TypeTraitement_.libTypTrait.getName(), Candidature_.temValidTypTraitCand.getName(),
			LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName(),
			LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.temValidTypeDecCand.getName(),
			LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.motivationAvis.getName() + "." + MotivationAvis_.libMotiv.getName(),
			LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.commentTypeDecCand.getName(), LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.preselectDateTypeDecCand.getName(),
			LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.preselectHeureTypeDecCand.getName(), LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.preselectLieuTypeDecCand.getName(),
			LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.listCompRangTypDecCand.getName(), Candidature_.temAcceptCand.getName(), Candidature_.datTransDossierCand.getName(),
			Candidature_.datReceptDossierCand.getName(), Candidature_.datCompletDossierCand.getName(), Candidature_.datIncompletDossierCand.getName(), Candidature_.datAnnulCand.getName(),
			Candidature_.userAnnulCand.getName()};

	public static final String[] FIELDS_ORDER_VISIBLE = {Candidature_.tag.getName(),
			Candidature_.candidat.getName() + "." + Candidat_.compteMinima.getName() + "." + CompteMinima_.numDossierOpiCptMin.getName(),
			Candidature_.candidat.getName() + "." + Candidat_.nomPatCandidat.getName(), Candidature_.candidat.getName() + "." + Candidat_.prenomCandidat.getName(),
			Candidature_.formation.getName() + "." + Formation_.codForm.getName(), Candidature_.formation.getName() + "." + Formation_.libForm.getName(),
			Candidature_.typeStatut.getName() + "." + TypeStatut_.libTypStatut.getName(), Candidature_.typeTraitement.getName() + "." + TypeTraitement_.libTypTrait.getName(),
			Candidature_.temValidTypTraitCand.getName(), LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName(),
			LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.temValidTypeDecCand.getName(),
			LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.motivationAvis.getName() + "." + MotivationAvis_.libMotiv.getName(),
			LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.commentTypeDecCand.getName(), Candidature_.temAcceptCand.getName()};

	/* Injections */
	@Resource
	protected transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	protected transient CandidatureController candidatureController;
	@Resource
	protected transient CandidaturePieceController candidaturePieceController;
	@Resource
	protected transient CommissionController commissionController;
	@Resource
	protected transient CandidatureCtrCandController candidatureCtrCandController;
	@Resource
	private transient AdresseController adresseController;
	@Resource
	private transient AlertSvaController alertSvaController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient PreferenceController preferenceController;
	@Resource
	private transient DroitProfilController droitProfilController;

	/* Les droit sur la vue */
	private SecurityCtrCandFonc securityCtrCandFonc;
	private SecurityCommissionFonc securityCommissionFonc;

	private GridFormatting<Candidature> candidatureGrid = new GridFormatting<>(Candidature.class);
	private ComboBoxCommission cbCommission = new ComboBoxCommission();
	private VerticalLayout layout = new VerticalLayout();
	private PopupView pvSva;
	private Label titleView = new Label();
	private Label nbCandidatureLabel = new Label();

	private OneClickButton btnOpen = new OneClickButton(FontAwesome.SEARCH);
	private OneClickButton btnAction = new OneClickButton(FontAwesome.GAVEL);
	private OneClickButton btnExport = new OneClickButton(FontAwesome.FILE_EXCEL_O);
	private OneClickButton btnDownload = new OneClickButton(FontAwesome.CLOUD_DOWNLOAD);

	private Boolean modeModif = false;

	/** Initialise la vue */
	/** @param modeModification
	 * @param typGestionCandidature
	 * @param isCanceled
	 * @param isArchived
	 */
	public void init(final Boolean modeModification, final String typGestionCandidature, final Boolean isCanceled, final Boolean isArchived) {
		this.modeModif = modeModification;
		/* Style */
		setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		addComponent(layout);

		/* Titre */
		HorizontalLayout hlTitle = new HorizontalLayout();
		hlTitle.setWidth(100, Unit.PERCENTAGE);
		hlTitle.setSpacing(true);
		titleView.setSizeUndefined();
		titleView.addStyleName(StyleConstants.VIEW_TITLE);
		hlTitle.addComponent(titleView);

		/* PopUp SVA */
		pvSva = new PopupView(applicationContext.getMessage("alertSva.popup.link", null, UI.getCurrent().getLocale()), null);
		pvSva.setVisible(false);
		hlTitle.addComponent(pvSva);
		hlTitle.setComponentAlignment(pvSva, Alignment.MIDDLE_LEFT);

		final Label spacer1 = new Label();
		spacer1.setWidth(100, Unit.PERCENTAGE);
		hlTitle.addComponent(spacer1);
		hlTitle.setExpandRatio(spacer1, 1);

		/* Label du nombre de candidatures */
		nbCandidatureLabel.setSizeUndefined();
		nbCandidatureLabel.addStyleName(ValoTheme.LABEL_COLORED);
		nbCandidatureLabel.addStyleName(StyleConstants.LABEL_ITALIC);
		hlTitle.addComponent(nbCandidatureLabel);
		hlTitle.setComponentAlignment(nbCandidatureLabel, Alignment.BOTTOM_RIGHT);
		layout.addComponent(hlTitle);

		/* Les droits sur les candidatures */
		List<DroitFonctionnalite> listeDroitFonc = droitProfilController.getCandidatureFonctionnalite(typGestionCandidature, null);

		Boolean droitOpenCandidature = false;
		Authentication auth = userController.getCurrentAuthentication();
		if (MethodUtils.isGestionCandidatureCtrCand(typGestionCandidature)) {
			/* Récupération du centre de candidature en cours */
			securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE, auth);
			if (securityCtrCandFonc.hasNoRight()) {
				return;
			}

			/* Verification que l'utilisateur a le droit d'ouvrir la candidature */
			SecurityCtrCandFonc openCandidatureFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FENETRE_CAND, auth);
			if (!openCandidatureFonc.hasNoRight()) {
				droitOpenCandidature = true;
			}

			/* Chooser de commissions */
			List<Commission> liste = commissionController.getCommissionsEnServiceByCtrCand(securityCtrCandFonc.getCtrCand(), securityCtrCandFonc.getIsGestAllCommission(),
					securityCtrCandFonc.getListeIdCommission(), isArchived);
			liste.sort(Comparator.comparing(Commission::getGenericLibelleAlternatif));
			cbCommission.setWidth(500, Unit.PIXELS);
			cbCommission.setItemCaptionPropertyId(ConstanteUtils.GENERIC_LIBELLE_ALTERNATIF);
			cbCommission.setTextInputAllowed(true);
			cbCommission.filterListValue(liste);
			cbCommission.setFilteringMode(FilteringMode.CONTAINS);
			cbCommission.setItemCaptionMode(ItemCaptionMode.PROPERTY);
			cbCommission.setPageLength(25);

			if (liste.size() > 0) {
				Integer idCommEnCours = preferenceController.getPrefCandIdComm();
				if (idCommEnCours != null) {
					if (!cbCommission.setCommissionValue(idCommEnCours)) {
						cbCommission.setValue(liste.get(0));
					}
				} else {
					cbCommission.setValue(liste.get(0));
				}

			}

			/* Filtrage */
			Panel panelCommission = new Panel();
			panelCommission.setWidth(100, Unit.PERCENTAGE);
			HorizontalLayout filtreLayout = new HorizontalLayout();
			filtreLayout.setMargin(true);
			layout.addComponent(panelCommission);
			filtreLayout.setSpacing(true);

			Label labelFiltre = new Label(applicationContext.getMessage("candidature.change.commission", null, UI.getCurrent().getLocale()));
			filtreLayout.addComponent(labelFiltre);
			filtreLayout.setComponentAlignment(labelFiltre, Alignment.MIDDLE_LEFT);
			filtreLayout.addComponent(cbCommission);
			filtreLayout.setComponentAlignment(cbCommission, Alignment.BOTTOM_LEFT);

			OneClickButton btnChange = new OneClickButton(applicationContext.getMessage("btnChange", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
			btnChange.addClickListener(e -> {
				majContainer();
				preferenceController.setPrefCandIdComm(getCommission());
			});
			filtreLayout.addComponent(btnChange);
			filtreLayout.setComponentAlignment(btnChange, Alignment.BOTTOM_LEFT);

			// popup astuce
			PopupView pvAstuce = new PopupView(createPopUpAstuce());
			filtreLayout.addComponent(pvAstuce);
			filtreLayout.setComponentAlignment(pvAstuce, Alignment.MIDDLE_LEFT);
			panelCommission.setContent(filtreLayout);
		} else if (MethodUtils.isGestionCandidatureCommission(typGestionCandidature)) {
			/* Récupération de la commission en cours en cours */
			securityCommissionFonc = userController.getCommissionFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE, auth);
			if (securityCommissionFonc.hasNoRight()) {
				return;
			}

			/* Verification que l'utilisateur a le droit d'ouvrir la candidature */
			SecurityCommissionFonc openCandidatureFonc = userController.getCommissionFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FENETRE_CAND, auth);
			if (!openCandidatureFonc.hasNoRight()) {
				droitOpenCandidature = true;
			}
		}
		final Boolean droitOpenCandidatureFinal = droitOpenCandidature;

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		/* Bouton d'ouverture de candidature */
		btnOpen.setCaption(applicationContext.getMessage("btnOpen", null, UI.getCurrent().getLocale()));
		btnOpen.setEnabled(false);
		btnOpen.addClickListener(e -> {
			Candidature candidature = getCandidatureSelected();
			if (droitOpenCandidatureFinal && candidature != null) {
				candidatureController.openCandidatureGestionnaire(candidature, isCanceled, isArchived, listeDroitFonc);
			}
		});
		buttonsLayout.addComponent(btnOpen);
		buttonsLayout.setComponentAlignment(btnOpen, Alignment.MIDDLE_LEFT);

		if (modeModif && listeDroitFonc.stream().filter(e -> !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_FENETRE_CAND)).count() > 0) {
			/* Bouton d'action */
			btnAction.setCaption(applicationContext.getMessage("btnAction", null, UI.getCurrent().getLocale()));
			btnAction.setEnabled(false);
			btnAction.addClickListener(e -> {
				List<Candidature> listeCheck = getListeCandidatureSelected();
				if (listeCheck.size() == 0) {
					Notification.show(applicationContext.getMessage("candidature.noselected", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				} else if (listeCheck.size() > ConstanteUtils.SIZE_MAX_EDITION_MASSE) {
					Notification.show(applicationContext.getMessage("candidature.toomuchselected", new Object[] {ConstanteUtils.SIZE_MAX_EDITION_MASSE}, UI.getCurrent().getLocale()),
							Type.WARNING_MESSAGE);
					return;
				} else {
					candidatureCtrCandController.editActionCandidatureMasse(listeCheck, listeDroitFonc);
				}
			});
			buttonsLayout.addComponent(btnAction);
			buttonsLayout.setComponentAlignment(btnAction, Alignment.MIDDLE_CENTER);
		}

		/* Les options */
		HorizontalLayout hlOption = new HorizontalLayout();
		hlOption.setSpacing(true);
		buttonsLayout.addComponent(hlOption);
		buttonsLayout.setComponentAlignment(hlOption, Alignment.MIDDLE_RIGHT);

		/* Export de la liste de candidature */
		btnExport.setDescription(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()));
		btnExport.addClickListener(e -> {
			@SuppressWarnings("unchecked")
			List<Candidature> listeCand = (List<Candidature>) candidatureGrid.getContainerDataSource().getItemIds();
			if (listeCand.size() == 0) {
				return;
			}
			CtrCandExportWindow window = new CtrCandExportWindow(getCommission(), listeCand);
			UI.getCurrent().addWindow(window);
		});
		hlOption.addComponent(btnExport);

		/* Filtres de comboBox perso */
		String libFilterNull = applicationContext.getMessage("filter.null", null, UI.getCurrent().getLocale());
		List<ComboBoxFilterPresentation> listeCbFilter = new ArrayList<>();

		listeCbFilter.add(new ComboBoxFilterPresentation(Candidature_.typeStatut.getName() + "." + TypeStatut_.libTypStatut.getName(),
				getComboBoxFilterComponent(Candidature_.typeStatut.getName() + "." + TypeStatut_.libTypStatut.getName(), null), null));
		listeCbFilter.add(new ComboBoxFilterPresentation(Candidature_.typeTraitement.getName() + "." + TypeTraitement_.libTypTrait.getName(),
				getComboBoxFilterComponent(Candidature_.typeTraitement.getName() + "." + TypeTraitement_.libTypTrait.getName(), null), null));
		listeCbFilter.add(new ComboBoxFilterPresentation(LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName(),
				getComboBoxFilterComponent(LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName(), libFilterNull), libFilterNull));

		/* La colonne de tag n'est plus automatiquement visibles si aucun tags en service */
		final String[] fieldsOrderVisibletoUse = (cacheController.getTagEnService().size() != 0) ? FIELDS_ORDER_VISIBLE
				: (String[]) ArrayUtils.removeElement(FIELDS_ORDER_VISIBLE, Candidature_.tag.getName());

		/* Les préférences */
		Integer frozen = preferenceController.getPrefCandFrozenColonne(1);
		String[] visibleColonne = preferenceController.getPrefCandColonnesVisible(fieldsOrderVisibletoUse);
		String[] orderColonne = preferenceController.getPrefCandColonnesOrder(FIELDS_ORDER);
		String sortColonne = preferenceController.getPrefCandSortColonne(Candidature_.idCand.getName());
		SortDirection sortDirection = preferenceController.getPrefCandSortDirection(SortDirection.ASCENDING);

		/* Bouton de modification de preferences */
		OneClickButton btnPref = new OneClickButton(FontAwesome.COG);
		btnPref.setDescription(applicationContext.getMessage("preference.view.btn", null, UI.getCurrent().getLocale()));
		btnPref.addClickListener(e -> {
			CtrCandPreferenceViewWindow window = new CtrCandPreferenceViewWindow(candidatureGrid.getColumns(), candidatureGrid.getFrozenColumnCount(), FIELDS_ORDER.length,
					candidatureGrid.getSortOrder());
			window.addPreferenceViewListener(new PreferenceViewListener() {

				/** serialVersionUID **/
				private static final long serialVersionUID = -3704380033163261859L;

				@Override
				public void saveInSession(final String valeurColonneVisible, final String valeurColonneOrder, final Integer frozenCols, final String sortColonne, final String sortDirection) {
					preferenceController.savePrefCandInSession(valeurColonneVisible, valeurColonneOrder, frozenCols, sortColonne, sortDirection, true);
					candidatureGrid.setFrozenColumnCount(frozenCols);
				}

				@Override
				public void saveInDb(final String valeurColonneVisible, final String valeurColonneOrder, final Integer frozenCols, final String sortColonne, final String sortDirection) {
					preferenceController.savePrefCandInDb(valeurColonneVisible, valeurColonneOrder, frozenCols, sortColonne, sortDirection);
					candidatureGrid.setFrozenColumnCount(frozenCols);
				}

				@Override
				public void initPref() {
					preferenceController.initPrefCand();
					candidatureGrid.setFrozenColumnCount(1);
					candidatureGrid.initColumn(FIELDS_ORDER, fieldsOrderVisibletoUse, orderColonne, "candidature.table.", Candidature_.idCand.getName(), sortDirection, listeCbFilter);
					candidatureGrid.sort();
				}
			});
			UI.getCurrent().addWindow(window);
		});

		/* Download du dossier */
		btnDownload.setEnabled(false);

		Integer nb = parametreController.getNbDossierDownloadMax();
		if (nb.equals(1)) {
			btnDownload.setDescription(applicationContext.getMessage("candidature.download.btn", null, UI.getCurrent().getLocale()));
		} else {
			btnDownload.setDescription(applicationContext.getMessage("candidature.download.multiple.btn", new Object[] {nb}, UI.getCurrent().getLocale()));
		}

		new OnDemandFileDownloader(new OnDemandStreamFile() {
			@Override
			public OnDemandFile getOnDemandFile() {
				OnDemandFile file = candidatureController.downlaodMultipleDossier(getListeCandidatureSelected(), getCommission());
				if (file != null) {
					btnDownload.setEnabled(true);
					return file;
				}
				btnDownload.setEnabled(true);
				return null;
			}
		}, btnDownload);

		hlOption.addComponent(btnExport);
		hlOption.addComponent(btnDownload);
		hlOption.addComponent(btnPref);

		/* Grid des candidatures */
		candidatureGrid.initColumn(FIELDS_ORDER, visibleColonne, orderColonne, "candidature.table.", sortColonne, sortDirection, listeCbFilter);

		/* Ajout des colonnes gelées */
		candidatureGrid.setFrozenColumnCount(frozen);

		/* Ajout du flag */
		candidatureGrid.setColumnConverter(Candidature_.tag.getName(), new TagToHtmlSquareConverter());
		candidatureGrid.setColumnRenderer(Candidature_.tag.getName(), new HtmlRenderer());

		/* Mise a jour des données lors du filtre */
		candidatureGrid.addFilterListener(() -> {
			majNbCandidatures();
		});

		/* Mode de selection de la grid */
		if (modeModif) {
			candidatureGrid.setSelectionMode(SelectionMode.MULTI);
			MultiSelectionModel selection = (MultiSelectionModel) candidatureGrid.getSelectionModel();
			selection.setSelectionLimit(ConstanteUtils.SIZE_MAX_EDITION_MASSE);
			candidatureGrid.addSelectionListener(e -> {
				if (candidatureGrid.getSelectedRows().size() == ConstanteUtils.SIZE_MAX_EDITION_MASSE) {
					Notification.show(applicationContext.getMessage("candidature.maxselected", new Object[] {ConstanteUtils.SIZE_MAX_EDITION_MASSE}, UI.getCurrent().getLocale()),
							Type.TRAY_NOTIFICATION);

				}
			});
		} else {
			candidatureGrid.setSelectionMode(SelectionMode.SINGLE);
		}

		/* Selection de la grid */
		candidatureGrid.addSelectionListener(e -> {
			setButtonState(droitOpenCandidatureFinal, listeDroitFonc);
			majNbCandidatures();
		});

		/* CLique sur un item */
		candidatureGrid.addItemClickListener(e -> {
			/* Suivant le mode de slection de la grid on fait un traitement */
			if (modeModif) {
				MultiSelectionModel selection = (MultiSelectionModel) candidatureGrid.getSelectionModel();
				selection.deselectAll();
				try {
					selection.select(e.getItemId());
				} catch (Exception e1) {
					Notification.show(applicationContext.getMessage("candidature.select.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					majContainer();
					return;
				}
				setButtonState(droitOpenCandidatureFinal, listeDroitFonc);
				if (droitOpenCandidatureFinal && e.isDoubleClick()) {
					if (e.getItemId() instanceof Candidature) {
						candidatureController.openCandidatureGestionnaire((Candidature) e.getItemId(), isCanceled, isArchived, listeDroitFonc);
					}
				}
				majNbCandidatures();
			} else {
				SingleSelectionModel selection = (SingleSelectionModel) candidatureGrid.getSelectionModel();
				try {
					selection.select(e.getItemId());
				} catch (Exception e1) {
					Notification.show(applicationContext.getMessage("candidature.select.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					majContainer();
					return;
				}

				setButtonState(droitOpenCandidatureFinal, listeDroitFonc);
				if (e.isDoubleClick()) {
					if (droitOpenCandidatureFinal && e.getItemId() instanceof Candidature) {
						candidatureController.openCandidatureGestionnaire((Candidature) e.getItemId(), isCanceled, isArchived, listeDroitFonc);
					}
				}
				majNbCandidatures();
			}

		});

		/* Ajustement de la taille de colonnes */
		candidatureGrid.setColumnWidth(Candidature_.candidat.getName() + "." + Candidat_.compteMinima.getName() + "." + CompteMinima_.numDossierOpiCptMin.getName(), 120);
		candidatureGrid.setColumnWidth(Candidature_.candidat.getName() + "." + Candidat_.nomPatCandidat.getName(), 130);
		candidatureGrid.setColumnWidth(Candidature_.candidat.getName() + "." + Candidat_.prenomCandidat.getName(), 120);
		candidatureGrid.setColumnWidth(Candidature_.formation.getName() + "." + Formation_.codForm.getName(), 130);
		candidatureGrid.setColumnWidth(Candidature_.formation.getName() + "." + Formation_.libForm.getName(), 200);
		candidatureGrid.setColumnWidth(LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.commentTypeDecCand.getName(), 120);
		candidatureGrid.setColumnWidth(LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.motivationAvis.getName() + "." + MotivationAvis_.libMotiv.getName(), 120);
		candidatureGrid.setColumnWidth(Candidature_.typeStatut.getName() + "." + TypeStatut_.libTypStatut.getName(), 145);
		candidatureGrid.setColumnWidth(Candidature_.typeTraitement.getName() + "." + TypeTraitement_.libTypTrait.getName(), 157);
		candidatureGrid.setColumnWidth(Candidature_.temAcceptCand.getName(), 151);
		candidatureGrid.setColumnWidth(Candidature_.tag.getName(), 57);

		layout.addComponent(candidatureGrid);
		layout.setExpandRatio(candidatureGrid, 1);
		candidatureGrid.setSizeFull();

		/* Ajoute les alertes SVA */
		addAlertSva(isCanceled, isArchived);
	}

	/** On supprime */
	protected void detachView() {
		// removeAlertSva();
	}

	/** Modifie l'état des boutons
	 *
	 * @param listeDroitFonc
	 */
	private void setButtonState(final Boolean droitOpenCandidature, final List<DroitFonctionnalite> listeDroitFonc) {
		Integer nbCandidaturesSelected = candidatureGrid.getSelectedRows().size();

		/* Bouton open */
		if (nbCandidaturesSelected == 1) {
			if (droitOpenCandidature) {
				btnOpen.setEnabled(true);
			} else {
				btnOpen.setEnabled(false);
			}
		} else {
			btnOpen.setEnabled(false);
		}

		/* Bouton download */
		if (nbCandidaturesSelected == 0 || nbCandidaturesSelected > parametreController.getNbDossierDownloadMax()) {
			btnDownload.setEnabled(false);
		} else {
			btnDownload.setEnabled(true);
		}

		/* Bouton action */
		if (nbCandidaturesSelected == 1) {
			btnAction.setEnabled(true);
		} else if (candidatureGrid.getSelectedRows().size() > 1 && (listeDroitFonc.stream()
				.filter(e -> !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_VISU_HISTO_AVIS) && !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_POST_IT)
						&& !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_NUM_OPI) && !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_OPEN_CANDIDAT))
				.count() > 0)) {
			btnAction.setEnabled(true);
		} else {
			btnAction.setEnabled(false);
		}
	}

	/** @return la liste des candidatures selectionnées */
	@SuppressWarnings({"unchecked", "rawtypes"})
	private List<Candidature> getListeCandidatureSelected() {
		List<Candidature> listeSelected = new ArrayList();
		if (modeModif) {
			candidatureGrid.getSelectedRows().forEach(candItem -> {
				try {
					Candidature c = candidatureGrid.getItem(candItem);
					listeSelected.add(c);
				} catch (Exception e) {
				}
			});
		} else {
			if (candidatureGrid.getSelectedRow() != null) {
				try {
					Candidature c = candidatureGrid.getSelectedItem();
					listeSelected.add(c);
				} catch (Exception e) {
				}
			}
		}
		return listeSelected;
	}

	/** @return la candidature selectionnée. Si plus d'une, alors on renvoi null */
	@SuppressWarnings("unchecked")
	private Candidature getCandidatureSelected() {
		List<Candidature> liste = getListeCandidatureSelected();
		if (liste.size() == 1) {
			// return liste.get(0);
			/* Obligé de passer par le container car le modele n'est pas a jour, meme apres un reset */
			Candidature candi = ((BeanItem<Candidature>) candidatureGrid.getContainerDataSource().getItem(liste.get(0))).getBean();
			return candi;

		}
		return null;
	}

	/** Met à jour le nombre de candidatures */
	private void majNbCandidatures() {
		nbCandidatureLabel.setValue(applicationContext.getMessage("candidature.table.nombre",
				new Object[] {candidatureGrid.getContainerDataSource().getItemIds().size(), getListeCandidatureSelected().size()}, UI.getCurrent().getLocale()));
	}

	/** Passe au mode d'erreur
	 *
	 * @param mode
	 */
	protected void switchToErrorMode(final Boolean mode) {
		layout.setVisible(!mode);
	}

	/** Modifie le titre pour le centre de candidature
	 *
	 * @param code
	 */
	protected void setTitle(final String code) {
		if ((securityCtrCandFonc == null || securityCtrCandFonc.hasNoRight()) && (securityCommissionFonc == null || securityCommissionFonc.hasNoRight())) {
			return;
		} else if (securityCtrCandFonc != null && !securityCtrCandFonc.hasNoRight()) {
			titleView.setValue(applicationContext.getMessage(code, new Object[] {securityCtrCandFonc.getCtrCand().getLibCtrCand()}, UI.getCurrent().getLocale()));
		} else if (securityCommissionFonc != null && !securityCommissionFonc.hasNoRight()) {
			titleView.setValue(applicationContext.getMessage("candidature.commission.title", new Object[] {securityCommissionFonc.getCommission().getLibComm()}, UI.getCurrent().getLocale()));
		}

	}

	/** @param commission
	 * @return la liste des candidature */
	protected List<Candidature> getListeCandidature(final Commission commission) {
		return new ArrayList<>();
	}

	/** @return la commission */
	protected Commission getCommission() {
		if (securityCommissionFonc != null) {
			return securityCommissionFonc.getCommission();
		}
		return (Commission) cbCommission.getValue();
	}

	/** Verifie que la candidature appartient bien à la commission
	 *
	 * @param entity
	 * @return true si la candidature appartient à la commission */
	protected Boolean isEntityApartientCommission(final Candidature entity) {
		Commission commission = getCommission();
		if (commission == null) {
			return false;
		}

		if (entity.getFormation().getCommission().getIdComm().equals(commission.getIdComm())) {
			return true;
		}

		return false;
	}

	/** Met à jour le container grace a la commission */
	protected void majContainer() {
		if (modeModif) {
			MultiSelectionModel selection = (MultiSelectionModel) candidatureGrid.getSelectionModel();
			if (selection.getSelectedRows().size() > 0) {
				selection.deselectAll();
			}
		}
		candidatureGrid.removeAll();
		Commission commission = getCommission();
		if (commission != null) {
			candidatureGrid.addItems(getListeCandidature(commission));
		}
		majNbCandidatures();
	}

	/** Ajoute les alertes SVA */
	protected void addAlertSva(final Boolean isCanceled, final Boolean isArchived) {
		if (isCanceled || isArchived) {
			return;
		}
		List<AlertSva> listeAlerteSva = alertSvaController.getAlertSvaEnService();
		String dateSva = parametreController.getAlertSvaDat();
		Boolean definitifSva = parametreController.getAlertSvaDefinitif();
		if (listeAlerteSva.size() == 0) {
			return;
		}

		/* Ajout du css SVA */
		candidatureGrid.setRowStyleGenerator(new RowStyleGenerator() {

			/** serialVersionUID **/
			private static final long serialVersionUID = -4321160176275490773L;

			@Override
			public String getStyle(final RowReference row) {
				return getStyleSva((Candidature) row.getItemId(), listeAlerteSva, dateSva, definitifSva);
			}
		});

		/* Legende alertes SVA */
		pvSva.setContent(createPopUpContent(listeAlerteSva, dateSva));
		pvSva.setVisible(true);
	}

	/** Créé la popup d'astuce */
	private Content createPopUpAstuce() {
		VerticalLayout vlAstuce = new VerticalLayout();
		vlAstuce.setMargin(true);
		vlAstuce.setSpacing(true);

		Label labelTitle = new Label(applicationContext.getMessage("candidature.change.commission.astuce.title", null, UI.getCurrent().getLocale()));
		labelTitle.addStyleName(ValoTheme.LABEL_LARGE);
		labelTitle.addStyleName(ValoTheme.LABEL_BOLD);
		vlAstuce.addComponent(labelTitle);

		vlAstuce.addComponent(new Label(applicationContext.getMessage("candidature.change.commission.astuce.content", null, UI.getCurrent().getLocale()), ContentMode.HTML));

		return new Content() {
			/** serialVersionUID **/
			private static final long serialVersionUID = -4599757106887300854L;

			@Override
			public String getMinimizedValueAsHTML() {
				return applicationContext.getMessage("candidature.change.commission.astuce.link", null, UI.getCurrent().getLocale());
			}

			@Override
			public Component getPopupComponent() {
				return vlAstuce;
			}
		};
	}

	/** Créé la popup SVA
	 *
	 * @param listeAlerteSva
	 * @param dateSva
	 * @return le contenu de la popup SVA */
	private Content createPopUpContent(final List<AlertSva> listeAlerteSva, final String dateSva) {
		VerticalLayout vlAlert = new VerticalLayout();
		vlAlert.setMargin(true);
		vlAlert.setSpacing(true);

		Label labelTitle = new Label(applicationContext.getMessage("alertSva.popup.title", new Object[] {alertSvaController.getLibelleDateSVA(dateSva)}, UI.getCurrent().getLocale()));
		labelTitle.addStyleName(ValoTheme.LABEL_LARGE);
		labelTitle.addStyleName(ValoTheme.LABEL_BOLD);
		vlAlert.addComponent(labelTitle);

		listeAlerteSva.forEach(alert -> {
			vlAlert.addComponent(new Label("<div style='display:inline-block;border:1px solid;width:20px;height:20px;background:" + alert.getColorSva()
					+ ";'></div><div style='height:100%;display: inline-block;vertical-align: super;'>"
					+ applicationContext.getMessage("alertSva.popup.alert", new Object[] {alert.getNbJourSva()}, UI.getCurrent().getLocale()) + "</div>", ContentMode.HTML));
		});
		return new Content() {
			/** serialVersionUID **/
			private static final long serialVersionUID = -4599757106887300854L;

			@Override
			public String getMinimizedValueAsHTML() {
				return applicationContext.getMessage("alertSva.popup.link", null, UI.getCurrent().getLocale());
			}

			@Override
			public Component getPopupComponent() {
				return vlAlert;
			}
		};
	}

	/** @param propertyId
	 * @param libNull
	 * @return les filtres perso en ComboBox */
	public ComboBox getComboBoxFilterComponent(final Object propertyId, final String libNull) {
		List<String> list = new ArrayList<>();
		if (propertyId.equals(Candidature_.typeTraitement.getName() + "." + TypeTraitement_.libTypTrait.getName())) {
			cacheController.getListeTypeTraitement().forEach(e -> list.add(e.getLibTypTrait()));
			return generateComboBox(list, libNull);
		} else if (propertyId.equals(Candidature_.typeStatut.getName() + "." + TypeStatut_.libTypStatut.getName())) {
			cacheController.getListeTypeStatut().forEach(e -> list.add(e.getLibTypStatut()));
			return generateComboBox(list, libNull);
		} else if (propertyId.equals(LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName())) {
			typeDecisionController.getTypeDecisionsEnService().forEach(e -> list.add(e.getLibTypDec()));
			return generateComboBox(list, libNull);
		}
		return null;
	}

	/** @param liste
	 * @param libNull
	 * @return une combo grace a la liste */
	private ComboBox generateComboBox(final List<String> liste, final String libNull) {
		ComboBox sampleIdCB = new ComboBox();
		sampleIdCB.setPageLength(20);
		sampleIdCB.setTextInputAllowed(false);
		BeanItemContainer<String> dataList = new BeanItemContainer<>(String.class);
		dataList.addBean(applicationContext.getMessage("filter.all", null, UI.getCurrent().getLocale()));
		if (libNull != null) {
			dataList.addBean(libNull);
		}
		dataList.addAll(liste);
		sampleIdCB.setNullSelectionItemId(applicationContext.getMessage("filter.all", null, UI.getCurrent().getLocale()));
		sampleIdCB.setContainerDataSource(dataList);
		sampleIdCB.setImmediate(true);
		return sampleIdCB;
	}

	/** @param candidature
	 * @param listeAlerteSva
	 * @param dateSva
	 * @param definitifSva
	 * @return le style sva correspondand */
	public String getStyleSva(final Candidature candidature, final List<AlertSva> listeAlerteSva, final String dateSva, final Boolean definitifSva) {
		if (dateSva.equals(NomenclatureUtils.CAND_DAT_NO_DAT) || listeAlerteSva.size() == 0) {
			return null;
		}

		TypeDecisionCandidature typeDecisionCandidature = candidature.getLastTypeDecision();
		// dans le cas de seulement les avis définitif
		if (definitifSva) {
			if (typeDecisionCandidature != null && typeDecisionCandidature.getTemValidTypeDecCand() && typeDecisionCandidature.getTypeDecision().getTemDefinitifTypDec()) {
				return null;
			}
		}
		// autres cas
		else {
			if (typeDecisionCandidature != null && typeDecisionCandidature.getTemValidTypeDecCand()) {
				return null;
			}
		}

		/* On cherche la date à utiliser */
		LocalDate dateToUse = null;
		switch (dateSva) {
		case NomenclatureUtils.CAND_DAT_CRE:
			dateToUse = MethodUtils.convertLocalDateTimeToDate(candidature.getDatCreCand());
			break;
		case NomenclatureUtils.CAND_DAT_ANNUL:
			dateToUse = MethodUtils.convertLocalDateTimeToDate(candidature.getDatAnnulCand());
			break;
		case NomenclatureUtils.CAND_DAT_ACCEPT:
			dateToUse = MethodUtils.convertLocalDateTimeToDate(candidature.getDatAcceptCand());
			break;
		case NomenclatureUtils.CAND_DAT_TRANS:
			dateToUse = MethodUtils.convertLocalDateTimeToDate(candidature.getDatTransDossierCand());
			break;
		case NomenclatureUtils.CAND_DAT_RECEPT:
			dateToUse = candidature.getDatReceptDossierCand();
			break;
		case NomenclatureUtils.CAND_DAT_COMPLET:
			dateToUse = candidature.getDatCompletDossierCand();
			break;
		case NomenclatureUtils.CAND_DAT_INCOMPLET:
			dateToUse = candidature.getDatIncompletDossierCand();
			break;
		default:
			dateToUse = null;
			break;
		}

		if (dateToUse == null) {
			return null;
		}

		/* Calcul le nombre de jour */
		Long nbDays = ChronoUnit.DAYS.between(dateToUse, LocalDate.now());
		Integer nbJourSva = null;
		for (AlertSva alert : listeAlerteSva) {
			if (nbDays >= alert.getNbJourSva() && (nbJourSva == null || nbJourSva < alert.getNbJourSva())) {
				nbJourSva = alert.getNbJourSva();
			}
		}

		/* On renvoi la couleur de la ligne */
		if (nbJourSva != null) {
			return StyleConstants.GRID_ROW_SVA + "-" + nbJourSva;
		}

		return null;
	}

	/** Supprime une entité de la table
	 *
	 * @param entity
	 */
	public void removeEntity(final Candidature entity) {
		if (!isEntityApartientCommission(entity)) {
			return;
		}
		candidatureGrid.removeItem(entity);
		majNbCandidatures();
	}

	/** Persisite une entité de la table
	 *
	 * @param entity
	 */
	public void addEntity(final Candidature entity) {
		if (!isEntityApartientCommission(entity)) {
			return;
		}
		candidatureGrid.addItem(entity);
		majNbCandidatures();
	}
}
