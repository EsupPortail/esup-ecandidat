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
package fr.univlorraine.ecandidat.views.template;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;

import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.Position;
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
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.TagController;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.AlertSva;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat_;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis_;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCatExoExt;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement_;
import fr.univlorraine.ecandidat.services.security.SecurityCommissionFonc;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureMasseListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.ComboBoxFilterPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.ComboBoxFilterPresentation.TypeFilter;
import fr.univlorraine.ecandidat.vaadin.components.GridConverter.BigDecimalMonetaireToStringConverter;
import fr.univlorraine.ecandidat.vaadin.components.GridConverter.TagsToHtmlSquareConverter;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TagImageSource;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxCommission;
import fr.univlorraine.ecandidat.views.windows.CtrCandDownloadPJWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandExportWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandPreferenceViewWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandPreferenceViewWindow.PreferenceViewListener;

@SuppressWarnings("serial")
public class CandidatureViewTemplate extends VerticalLayout implements CandidatureMasseListener {

	private static final String LAST_TYPE_DECISION_PREFIXE = "lastTypeDecision.";

	public static final String[] FIELDS_ORDER = { Candidature_.tags.getName(),
		Candidature_.candidat.getName() + "." + Candidat_.compteMinima.getName() + "." + CompteMinima_.numDossierOpiCptMin.getName(),
		Candidature_.candidat.getName() + "." + Candidat_.nomPatCandidat.getName(),
		Candidature_.candidat.getName() + "." + Candidat_.prenomCandidat.getName(),
		Candidature_.candidat.getName() + "." + Candidat_.compteMinima.getName() + "." + CompteMinima_.temFcCptMin.getName(),
		Candidature_.formation.getName() + "." + Formation_.codForm.getName(),
		Candidature_.formation.getName() + "." + Formation_.libForm.getName(),
		Candidature_.typeStatut.getName() + "." + TypeStatut_.libTypStatut.getName(),
		Candidature_.typeTraitement.getName() + "." + TypeTraitement_.libTypTrait.getName(),
		Candidature_.temValidTypTraitCand.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.temValidTypeDecCand.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.motivationAvis.getName() + "." + MotivationAvis_.libMotiv.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.commentTypeDecCand.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.preselectDateTypeDecCand.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.preselectHeureTypeDecCand.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.preselectLieuTypeDecCand.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.listCompRangTypDecCand.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.listCompRangReelTypDecCand.getName(),
		Candidature_.temAcceptCand.getName(),
		Candidature_.datTransDossierCand.getName(),
		Candidature_.datReceptDossierCand.getName(),
		Candidature_.datCompletDossierCand.getName(),
		Candidature_.datIncompletDossierCand.getName(),
		Candidature_.datNewConfirmCand.getName(),
		Candidature_.datNewRetourCand.getName(),
		Candidature_.siScolCatExoExt.getName() + "." + SiScolCatExoExt.DISPLAY_LIB_FIELD,
		Candidature_.cmtCatExoExtCand.getName(),
		Candidature_.mntChargeCand.getName(),
		Candidature_.opi.getName() + "." + Opi_.datPassageOpi.getName(),
		Candidature_.opi.getName() + "." + Opi_.codOpi.getName(),
		Candidature_.datAnnulCand.getName(),
		Candidature_.userAnnulCand.getName() };

	public static final String[] FIELDS_ORDER_VISIBLE = { Candidature_.tags.getName(),
		Candidature_.candidat.getName() + "." + Candidat_.compteMinima.getName() + "." + CompteMinima_.numDossierOpiCptMin.getName(),
		Candidature_.candidat.getName() + "." + Candidat_.nomPatCandidat.getName(),
		Candidature_.candidat.getName() + "." + Candidat_.prenomCandidat.getName(),
		Candidature_.formation.getName() + "." + Formation_.codForm.getName(),
		Candidature_.formation.getName() + "." + Formation_.libForm.getName(),
		Candidature_.typeStatut.getName() + "." + TypeStatut_.libTypStatut.getName(),
		Candidature_.typeTraitement.getName() + "." + TypeTraitement_.libTypTrait.getName(),
		Candidature_.temValidTypTraitCand.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.temValidTypeDecCand.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.motivationAvis.getName() + "." + MotivationAvis_.libMotiv.getName(),
		LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.commentTypeDecCand.getName(),
		Candidature_.temAcceptCand.getName() };

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
	protected transient TableRefController tableRefController;
	@Resource
	private transient AdresseController adresseController;
	@Resource
	private transient AlertSvaController alertSvaController;
	@Resource
	private transient TagController tagController;
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

	private final Map<String, String> sortCorresMap = new HashMap<String, String>() {
		{
			put("tags", "tagsSortable");
		}
	};
	private final GridFormatting<Candidature> candidatureGrid = new GridFormatting<>(Candidature.class, sortCorresMap);
	private final ComboBoxCommission cbCommission = new ComboBoxCommission();
	private final VerticalLayout layout = new VerticalLayout();
	private PopupView pvLegende;
	private final Label titleView = new Label();
	private final Label nbCandidatureLabel = new Label();
	private final Label nbCandidatureLabelSelected = new Label();

	private final OneClickButton btnOpen = new OneClickButton(FontAwesome.SEARCH);
	private final OneClickButton btnAction = new OneClickButton(FontAwesome.GAVEL);
	private final OneClickButton btnExport = new OneClickButton(FontAwesome.FILE_EXCEL_O);
	private final OneClickButton btnDownload = new OneClickButton(FontAwesome.CLOUD_DOWNLOAD);
	private final OneClickButton btnDownloadPj = new OneClickButton(FontAwesome.FILES_O);

	private Boolean modeModif = false;

	/* Listes utiles */
	private List<Tag> listeTags = new ArrayList<>();
	private List<AlertSva> listeAlertesSva = new ArrayList<>();

	/** Initialise la vue */
	/**
	 * @param modeModification
	 * @param typGestionCandidature
	 * @param isCanceled
	 * @param isArchived
	 */
	public void init(final Boolean modeModification, final String typGestionCandidature, final Boolean isCanceled, final Boolean isArchived) {
		modeModif = modeModification;

		/* Style */
		setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		addComponent(layout);

		/* Titre */
		final HorizontalLayout hlTitle = new HorizontalLayout();
		hlTitle.setWidth(100, Unit.PERCENTAGE);
		hlTitle.setSpacing(true);
		titleView.setSizeUndefined();
		titleView.addStyleName(StyleConstants.VIEW_TITLE);
		hlTitle.addComponent(titleView);

		/* PopUp Légende */
		pvLegende = new PopupView(applicationContext.getMessage("legend.popup.link", null, UI.getCurrent().getLocale()), null);
		pvLegende.setVisible(false);
		hlTitle.addComponent(pvLegende);
		hlTitle.setComponentAlignment(pvLegende, Alignment.MIDDLE_LEFT);

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

		final Label labelSpaceNb = new Label(" - ");
		labelSpaceNb.setSizeUndefined();
		labelSpaceNb.addStyleName(ValoTheme.LABEL_COLORED);
		labelSpaceNb.addStyleName(StyleConstants.LABEL_ITALIC);
		hlTitle.addComponent(labelSpaceNb);
		hlTitle.setComponentAlignment(labelSpaceNb, Alignment.BOTTOM_RIGHT);

		nbCandidatureLabelSelected.setSizeUndefined();
		nbCandidatureLabelSelected.addStyleName(ValoTheme.LABEL_COLORED);
		nbCandidatureLabelSelected.addStyleName(StyleConstants.LABEL_MORE_BOLD);
		nbCandidatureLabelSelected.addStyleName(StyleConstants.LABEL_ITALIC);
		hlTitle.addComponent(nbCandidatureLabelSelected);
		hlTitle.setComponentAlignment(nbCandidatureLabelSelected, Alignment.BOTTOM_RIGHT);

		layout.addComponent(hlTitle);

		/* Les droits sur les candidatures */
		final List<DroitFonctionnalite> listeDroitFonc = droitProfilController.getCandidatureFonctionnalite(typGestionCandidature, null);

		Boolean droitOpenCandidature = false;
		final Authentication auth = userController.getCurrentAuthentication();
		if (MethodUtils.isGestionCandidatureCtrCand(typGestionCandidature)) {
			/* Récupération du centre de candidature en cours */
			securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE, auth);
			if (securityCtrCandFonc.hasNoRight()) {
				return;
			}

			/* Verification que l'utilisateur a le droit d'ouvrir la candidature */
			final SecurityCtrCandFonc openCandidatureFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FENETRE_CAND, auth);
			if (!openCandidatureFonc.hasNoRight()) {
				droitOpenCandidature = true;
			}

			/* Chooser de commissions */
			final List<Commission> liste = commissionController.getCommissionsEnServiceByCtrCand(securityCtrCandFonc.getCtrCand(),
				securityCtrCandFonc.getIsGestAllCommission(),
				securityCtrCandFonc.getListeIdCommission(),
				isArchived);
			liste.sort(Comparator.comparing(Commission::getGenericLibelleAlternatif));
			cbCommission.setWidth(500, Unit.PIXELS);
			cbCommission.setItemCaptionPropertyId(ConstanteUtils.GENERIC_LIBELLE_ALTERNATIF);
			cbCommission.setTextInputAllowed(true);
			cbCommission.filterListValue(liste);
			cbCommission.setFilteringMode(FilteringMode.CONTAINS);
			cbCommission.setItemCaptionMode(ItemCaptionMode.PROPERTY);
			cbCommission.setPageLength(25);

			if (liste.size() > 0) {
				final Integer idCommEnCours = preferenceController.getPrefCandIdComm();
				if (idCommEnCours != null) {
					if (!cbCommission.setCommissionValue(idCommEnCours)) {
						cbCommission.setValue(liste.get(0));
					}
				} else {
					cbCommission.setValue(liste.get(0));
				}
			}

			cbCommission.addValueChangeListener(e -> {
				majContainer();
				preferenceController.setPrefCandIdComm(getCommission());
			});

			/* Filtrage */
			final Panel panelCommission = new Panel();
			panelCommission.setWidth(100, Unit.PERCENTAGE);
			final HorizontalLayout filtreLayout = new HorizontalLayout();
			filtreLayout.setMargin(true);
			layout.addComponent(panelCommission);
			filtreLayout.setSpacing(true);

			final Label labelFiltre = new Label(applicationContext.getMessage("candidature.change.commission", null, UI.getCurrent().getLocale()));
			filtreLayout.addComponent(labelFiltre);
			filtreLayout.setComponentAlignment(labelFiltre, Alignment.MIDDLE_LEFT);
			filtreLayout.addComponent(cbCommission);
			filtreLayout.setComponentAlignment(cbCommission, Alignment.BOTTOM_LEFT);

			// popup astuce
			final PopupView pvAstuce = new PopupView(createPopUpAstuce());
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
			final SecurityCommissionFonc openCandidatureFonc = userController.getCommissionFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FENETRE_CAND, auth);
			if (!openCandidatureFonc.hasNoRight()) {
				droitOpenCandidature = true;
			}
		}

		/* Mise a jour des listes utilisées */
		listeAlertesSva = alertSvaController.getAlertSvaEnService();
		listeTags = tagController.getTagEnServiceByCtrCand(getCentreCandidature());

		final Boolean droitOpenCandidatureFinal = droitOpenCandidature;

		/* Boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		/* Bouton d'ouverture de candidature */
		btnOpen.setCaption(applicationContext.getMessage("btnOpen", null, UI.getCurrent().getLocale()));
		btnOpen.setEnabled(false);
		btnOpen.addClickListener(e -> {
			final Candidature candidature = getCandidatureSelected();
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
				final List<Candidature> listeCheck = getListeCandidatureSelected();
				if (listeCheck.size() == 0) {
					Notification.show(applicationContext.getMessage("candidature.noselected", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				} else if (listeCheck.size() > ConstanteUtils.SIZE_MAX_EDITION_MASSE) {
					Notification.show(applicationContext.getMessage("candidature.toomuchselected", new Object[] { ConstanteUtils.SIZE_MAX_EDITION_MASSE }, UI.getCurrent().getLocale()),
						Type.WARNING_MESSAGE);
					return;
				} else {
					candidatureCtrCandController.editActionCandidatureMasse(listeCheck, listeDroitFonc, getCentreCandidature(), this);
				}
			});
			buttonsLayout.addComponent(btnAction);
			buttonsLayout.setComponentAlignment(btnAction, Alignment.MIDDLE_CENTER);
		}

		/* Les options */
		final HorizontalLayout hlOption = new HorizontalLayout();
		hlOption.setSpacing(true);
		buttonsLayout.addComponent(hlOption);
		buttonsLayout.setComponentAlignment(hlOption, Alignment.MIDDLE_RIGHT);

		/* Export de la liste de candidature */
		btnExport.setDescription(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()));
		btnExport.addClickListener(e -> {
			@SuppressWarnings("unchecked")
			final List<Candidature> listeCand = (List<Candidature>) candidatureGrid.getContainerDataSource().getItemIds();
			if (listeCand.size() == 0) {
				return;
			}
			final CtrCandExportWindow window = new CtrCandExportWindow(getCommission(), listeCand);
			UI.getCurrent().addWindow(window);
		});
		hlOption.addComponent(btnExport);

		/* Filtres de comboBox perso */
		final String libFilterNull = applicationContext.getMessage("filter.null", null, UI.getCurrent().getLocale());
		final String libFilterExceptAtt = applicationContext.getMessage("filter.except.attente", null, UI.getCurrent().getLocale());
		final List<ComboBoxFilterPresentation> listeCbFilter = new ArrayList<>();

		/* Filtre de tag */
		listeCbFilter.add(new ComboBoxFilterPresentation(Candidature_.tags.getName(), getComboBoxFilterTag(), getNullTag(), TypeFilter.LIST_CONTAINS));

		/* Les filtres sur les Strings */
		listeCbFilter.add(new ComboBoxFilterPresentation(Candidature_.typeStatut.getName() + "." + TypeStatut_.libTypStatut.getName(),
			libFilterExceptAtt,
			tableRefController.getTypeStatutEnAttente().getLibTypStatut(),
			getComboBoxTypStatut(libFilterExceptAtt)));
		listeCbFilter.add(new ComboBoxFilterPresentation(Candidature_.typeTraitement.getName() + "." + TypeTraitement_.libTypTrait.getName(),
			libFilterExceptAtt,
			tableRefController.getTypeTraitementEnAttente().getLibTypTrait(),
			getComboBoxTypTrait(libFilterExceptAtt)));
		listeCbFilter.add(new ComboBoxFilterPresentation(LAST_TYPE_DECISION_PREFIXE + TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName(),
			getComboBoxTypDec(libFilterNull),
			libFilterNull,
			TypeFilter.EQUALS));

		/* La colonne de tag n'est plus automatiquement visibles si aucun tags en service */
		final String[] fieldsOrderVisibletoUse = (listeTags.size() != 0) ? FIELDS_ORDER_VISIBLE : (String[]) ArrayUtils.removeElement(FIELDS_ORDER_VISIBLE, Candidature_.tags.getName());

		/* Les préférences */
		final Integer frozen = preferenceController.getPrefCandFrozenColonne(1);
		final String[] visibleColonne = preferenceController.getPrefCandColonnesVisible(fieldsOrderVisibletoUse, FIELDS_ORDER);
		final String[] orderColonne = preferenceController.getPrefCandColonnesOrder(FIELDS_ORDER);
		final List<SortOrder> sortColonne = preferenceController.getPrefCandSortColonne(FIELDS_ORDER);

		/* Bouton de modification de preferences */
		final OneClickButton btnPref = new OneClickButton(FontAwesome.COG);
		btnPref.setDescription(applicationContext.getMessage("preference.view.btn", null, UI.getCurrent().getLocale()));
		btnPref.addClickListener(e -> {
			final CtrCandPreferenceViewWindow window = new CtrCandPreferenceViewWindow(candidatureGrid.getColumns(),
				candidatureGrid.getFrozenColumnCount(),
				FIELDS_ORDER.length,
				candidatureGrid.getSortOrder());
			window.addPreferenceViewListener(new PreferenceViewListener() {

				@Override
				public void saveInSession(final String valeurColonneVisible, final String valeurColonneOrder, final Integer frozenCols, final List<SortOrder> listeSortOrder) {
					preferenceController.savePrefCandInSession(valeurColonneVisible, valeurColonneOrder, frozenCols, listeSortOrder, true);
					candidatureGrid.setFrozenColumnCount(frozenCols);
					candidatureGrid.sort(listeSortOrder);
				}

				@Override
				public void saveInDb(final String valeurColonneVisible, final String valeurColonneOrder, final Integer frozenCols, final List<SortOrder> listeSortOrder) {
					preferenceController.savePrefCandInDb(valeurColonneVisible, valeurColonneOrder, frozenCols, listeSortOrder);
					candidatureGrid.setFrozenColumnCount(frozenCols);
					candidatureGrid.sort(listeSortOrder);
				}

				@Override
				public void initPref() {
					preferenceController.initPrefCand();
					candidatureGrid.setFrozenColumnCount(1);
					candidatureGrid.initColumn(FIELDS_ORDER, FIELDS_ORDER_VISIBLE, FIELDS_ORDER, "candidature.table.", preferenceController.getDefaultSortOrder(), listeCbFilter);
					candidatureGrid.sort();
				}
			});
			UI.getCurrent().addWindow(window);
		});

		/* Download du dossier */
		btnDownload.setEnabled(false);

		final Integer nb = parametreController.getNbDownloaMultipliedMax();
		if (nb.equals(1)) {
			btnDownload.setDescription(applicationContext.getMessage("candidature.download.btn", null, UI.getCurrent().getLocale()));
		} else {
			btnDownload.setDescription(applicationContext.getMessage("candidature.download.multiple.btn", new Object[] { nb }, UI.getCurrent().getLocale()));
		}

		new OnDemandFileDownloader(new OnDemandStreamFile() {
			@Override
			public OnDemandFile getOnDemandFile() {
				final OnDemandFile file = candidatureController.downlaodMultipleDossier(getListeCandidatureSelected(), getCommission());
				if (file != null) {
					btnDownload.setEnabled(true);
					return file;
				}
				btnDownload.setEnabled(true);
				return null;
			}
		}, btnDownload);

		/* Download des PJ */
		btnDownloadPj.setEnabled(false);
		btnDownloadPj.setDescription(applicationContext.getMessage("candidature.download.pj.btn", new Object[] { nb }, UI.getCurrent().getLocale()));
		btnDownloadPj.addClickListener(e -> {
			UI.getCurrent().addWindow(new CtrCandDownloadPJWindow(getCommission(), getListeCandidatureSelected()));
		});

		hlOption.addComponent(btnExport);
		hlOption.addComponent(btnDownload);
		hlOption.addComponent(btnDownloadPj);
		hlOption.addComponent(btnPref);

		/* Grid des candidatures */
		candidatureGrid.initColumn(FIELDS_ORDER, visibleColonne, orderColonne, "candidature.table.", sortColonne, listeCbFilter);

		/* Ajout des colonnes gelées */
		candidatureGrid.setFrozenColumnCount(frozen);

		/* Ajout du flag */
		candidatureGrid.setColumnConverter(Candidature_.tags.getName(), new TagsToHtmlSquareConverter());
		candidatureGrid.setColumnRenderer(Candidature_.tags.getName(), new HtmlRenderer());

		/* Formatage monnétaire */
		candidatureGrid.setColumnConverter(Candidature_.mntChargeCand.getName(), new BigDecimalMonetaireToStringConverter());
		candidatureGrid.setColumnRenderer(Candidature_.mntChargeCand.getName(), new HtmlRenderer());

		/* Mise a jour des données lors du filtre */
		candidatureGrid.addFilterListener(() -> {
			deselectFilter();
		});

		/* Mode de selection de la grid */
		if (modeModif) {
			candidatureGrid.setSelectionMode(SelectionMode.MULTI);
			final MultiSelectionModel selection = (MultiSelectionModel) candidatureGrid.getSelectionModel();
			selection.setSelectionLimit(ConstanteUtils.SIZE_MAX_EDITION_MASSE);
			candidatureGrid.addSelectionListener(e -> {
				if (candidatureGrid.getSelectedRows().size() == ConstanteUtils.SIZE_MAX_EDITION_MASSE) {
					Notification.show(applicationContext.getMessage("candidature.maxselected", new Object[] { ConstanteUtils.SIZE_MAX_EDITION_MASSE }, UI.getCurrent().getLocale()),
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
				final MultiSelectionModel selection = (MultiSelectionModel) candidatureGrid.getSelectionModel();
				selection.deselectAll();
				try {
					selection.select(e.getItemId());
				} catch (final Exception e1) {
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
				final SingleSelectionModel selection = (SingleSelectionModel) candidatureGrid.getSelectionModel();
				try {
					selection.select(e.getItemId());
				} catch (final Exception e1) {
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
		candidatureGrid.setColumnWidth(Candidature_.tags.getName(), 100);
		candidatureGrid.setColumnWidth(Candidature_.datNewConfirmCand.getName(), 210);
		candidatureGrid.setColumnWidth(Candidature_.datNewRetourCand.getName(), 180);

		layout.addComponent(candidatureGrid);
		layout.setExpandRatio(candidatureGrid, 1);
		candidatureGrid.setSizeFull();

		/* Ajoute les alertes SVA */
		addAlertSva(isCanceled, isArchived);

		/* Ajoute la légende */
		addLegend();
	}

	/** On supprime */
	protected void detachView() {
		// removeAlertSva();
	}

	/**
	 * Modifie l'état des boutons
	 * @param listeDroitFonc
	 */
	private void setButtonState(final Boolean droitOpenCandidature, final List<DroitFonctionnalite> listeDroitFonc) {
		final Integer nbCandidaturesSelected = candidatureGrid.getSelectedRows().size();

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
		if (nbCandidaturesSelected == 0 || nbCandidaturesSelected > parametreController.getNbDownloaMultipliedMax()) {
			btnDownload.setEnabled(false);
			btnDownloadPj.setEnabled(false);
		} else {
			btnDownload.setEnabled(true);
			btnDownloadPj.setEnabled(true);
		}

		/* Bouton action */
		if (nbCandidaturesSelected == 1) {
			btnAction.setEnabled(true);
		} else if (candidatureGrid.getSelectedRows().size() > 1 && (listeDroitFonc.stream()
			.filter(e -> !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_VISU_HISTO_AVIS) && !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_POST_IT)
				&& !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_NUM_OPI)
				&& !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_OPEN_CANDIDAT))
			.count() > 0)) {
			btnAction.setEnabled(true);
		} else {
			btnAction.setEnabled(false);
		}
	}

	/** @return la liste des candidatures selectionnées */
	private List<Candidature> getListeCandidatureSelected() {
		final List<Candidature> listeSelected = new ArrayList<>();
		if (modeModif) {
			candidatureGrid.getSelectedRows().forEach(candItem -> {
				try {
					final Candidature c = candidatureGrid.getItem(candItem);
					listeSelected.add(c);
				} catch (final Exception e) {
				}
			});
		} else {
			if (candidatureGrid.getSelectedRow() != null) {
				try {
					final Candidature c = candidatureGrid.getSelectedItem();
					listeSelected.add(c);
				} catch (final Exception e) {
				}
			}
		}
		return listeSelected;
	}

	/** @return la candidature selectionnée. Si plus d'une, alors on renvoi null */
	@SuppressWarnings("unchecked")
	private Candidature getCandidatureSelected() {
		final List<Candidature> liste = getListeCandidatureSelected();
		if (liste.size() == 1) {
			// return liste.get(0);
			/* Obligé de passer par le container car le modele n'est pas a jour, meme apres un reset */
			final Candidature candi = ((BeanItem<Candidature>) candidatureGrid.getContainerDataSource().getItem(liste.get(0))).getBean();
			return candi;

		}
		return null;
	}

	/** Met à jour le nombre de candidatures */
	private void majNbCandidatures() {
		nbCandidatureLabel
			.setValue(applicationContext.getMessage("candidature.table.nombre", new Object[]
			{ candidatureGrid.getContainerDataSource().getItemIds().size() }, UI.getCurrent().getLocale()));
		nbCandidatureLabelSelected.setValue(applicationContext.getMessage("candidature.table.nombre.select", new Object[] { getListeCandidatureSelected().size() }, UI.getCurrent().getLocale()));
	}

	/**
	 * Passe au mode d'erreur
	 * @param mode
	 */
	protected void switchToErrorMode(final Boolean mode) {
		layout.setVisible(!mode);
	}

	/**
	 * Modifie le titre pour le centre de candidature
	 * @param code
	 */
	protected void setTitle(final String code) {
		if ((securityCtrCandFonc == null || securityCtrCandFonc.hasNoRight()) && (securityCommissionFonc == null || securityCommissionFonc.hasNoRight())) {
			return;
		} else if (securityCtrCandFonc != null && !securityCtrCandFonc.hasNoRight()) {
			titleView.setValue(applicationContext.getMessage(code, new Object[] { securityCtrCandFonc.getCtrCand().getLibCtrCand() }, UI.getCurrent().getLocale()));
		} else if (securityCommissionFonc != null && !securityCommissionFonc.hasNoRight()) {
			titleView.setValue(applicationContext.getMessage("candidature.commission.title", new Object[] { securityCommissionFonc.getCommission().getLibComm() }, UI.getCurrent().getLocale()));
		}

	}

	/**
	 * @param  commission
	 * @return            la liste des candidature
	 */
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

	/**
	 * Retourne le centre de candidature en cours :
	 * - soit le centre de candidature si ecran droit de centre de candidature
	 * - soit le centre de candidature de la commission si droit de commission
	 * @return le CentreCandidature en cours
	 */
	private CentreCandidature getCentreCandidature() {
		if (securityCtrCandFonc != null) {
			return securityCtrCandFonc.getCtrCand();
		} else if (securityCommissionFonc != null) {
			return securityCommissionFonc.getCommission().getCentreCandidature();
		}
		return null;
	}

	/**
	 * Verifie que la candidature appartient bien à la commission
	 * @param  entity
	 * @return        true si la candidature appartient à la commission
	 */
	protected Boolean isEntityApartientCommission(final Candidature entity) {
		final Commission commission = getCommission();
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
			final MultiSelectionModel selection = (MultiSelectionModel) candidatureGrid.getSelectionModel();
			if (selection.getSelectedRows().size() > 0) {
				selection.deselectAll();
			}
		}
		candidatureGrid.removeAll();
		final Commission commission = getCommission();
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
		final String dateSva = parametreController.getAlertSvaDat();
		final Boolean definitifSva = parametreController.getAlertSvaDefinitif();
		if (listeAlertesSva.size() == 0) {
			return;
		}

		/* Ajout du css SVA */
		candidatureGrid.setRowStyleGenerator(new RowStyleGenerator() {

			@Override
			public String getStyle(final RowReference row) {
				return getStyleSva((Candidature) row.getItemId(), listeAlertesSva, dateSva, definitifSva);
			}
		});
	}

	/**
	 * @param isArchived
	 * @param isCanceled
	 */
	private void addLegend() {
		/* Param sva */
		final String alertSvaDat = parametreController.getAlertSvaDat();

		/* Vérification qu'on a bien une légende à afficher */
		if ((listeAlertesSva.size() == 0 || alertSvaDat == null || alertSvaDat.equals(NomenclatureUtils.CAND_DAT_NO_DAT)) && listeTags.size() == 0) {
			return;
		}

		final VerticalLayout vlAlert = new VerticalLayout();
		vlAlert.setMargin(true);
		vlAlert.setSpacing(true);

		/* Ajout de la légende d'alertes SVA */
		if (listeAlertesSva.size() != 0 && alertSvaDat != null && !alertSvaDat.equals(NomenclatureUtils.CAND_DAT_NO_DAT)) {
			final Label labelTitleSva = new Label(
				applicationContext.getMessage("alertSva.popup.title", new Object[]
				{ alertSvaController.getLibelleDateSVA(parametreController.getAlertSvaDat()) }, UI.getCurrent().getLocale()));
			labelTitleSva.addStyleName(ValoTheme.LABEL_LARGE);
			labelTitleSva.addStyleName(ValoTheme.LABEL_BOLD);
			vlAlert.addComponent(labelTitleSva);

			listeAlertesSva.forEach(alert -> {
				vlAlert.addComponent(new Label(
					getHtmlLegend(alert.getColorSva(), applicationContext.getMessage("alertSva.popup.alert", new Object[]
					{ alert.getNbJourSva() }, UI.getCurrent().getLocale())),
					ContentMode.HTML));
			});
		}
		/* Ajout de la légende de Tag */
		if (listeTags.size() != 0) {
			final Label labelTitleTag = new Label(applicationContext.getMessage("tag.popup.title", null, UI.getCurrent().getLocale()));
			labelTitleTag.addStyleName(ValoTheme.LABEL_LARGE);
			labelTitleTag.addStyleName(ValoTheme.LABEL_BOLD);
			vlAlert.addComponent(labelTitleTag);

			listeTags.forEach(tag -> {
				vlAlert.addComponent(new Label(getHtmlLegend(tag.getColorTag(), tag.getLibTag()), ContentMode.HTML));
			});
		}

		final Content content = new Content() {

			@Override
			public String getMinimizedValueAsHTML() {
				return applicationContext.getMessage("legend.popup.link", null, UI.getCurrent().getLocale());
			}

			@Override
			public Component getPopupComponent() {
				return vlAlert;
			}
		};

		/* Legende alertes SVA */
		pvLegende.setContent(content);
		pvLegende.setVisible(true);
	}

	/**
	 * @param  color
	 * @param  text
	 * @return       la légende en HTML
	 */
	private String getHtmlLegend(final String color, final String text) {
		return "<div style='display:inline-block;border:1px solid;width:20px;height:20px;background:" + color
			+ ";'></div><div style='height:100%;display: inline-block;vertical-align: super;'>&nbsp;"
			+ text
			+ "</div>";
	}

	/** Créé la popup d'astuce */
	private Content createPopUpAstuce() {
		final VerticalLayout vlAstuce = new VerticalLayout();
		vlAstuce.setMargin(true);
		vlAstuce.setSpacing(true);

		final Label labelTitle = new Label(applicationContext.getMessage("candidature.change.commission.astuce.title", null, UI.getCurrent().getLocale()));
		labelTitle.addStyleName(ValoTheme.LABEL_LARGE);
		labelTitle.addStyleName(ValoTheme.LABEL_BOLD);
		vlAstuce.addComponent(labelTitle);

		vlAstuce.addComponent(new Label(applicationContext.getMessage("candidature.change.commission.astuce.content", null, UI.getCurrent().getLocale()), ContentMode.HTML));

		return new Content() {

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

	/**
	 * @return une combobox pour les tags
	 */
	@SuppressWarnings("unchecked")
	public ComboBox getComboBoxFilterTag() {
		/* Tag spécifique */
		final Tag tagAll = new Tag();
		tagAll.setIdTag(0);
		tagAll.setLibTag(applicationContext.getMessage("filter.all", null, UI.getCurrent().getLocale()));

		/* Liste des tags */
		final ComboBox sampleIdCB = new ComboBox();
		sampleIdCB.setPageLength(20);
		sampleIdCB.setTextInputAllowed(false);

		final BeanItemContainer<Tag> container = new BeanItemContainer<>(Tag.class);
		container.addNestedContainerProperty(Tag.PROPERTY_ICON);
		container.addBean(tagAll);
		container.addBean(getNullTag());
		listeTags.forEach(e -> {
			container.addItem(e).getItemProperty(Tag.PROPERTY_ICON).setValue(new StreamResource(new TagImageSource(e.getColorTag()), e.getIdTag() + ".png"));
		});
		sampleIdCB.setNullSelectionItemId(tagAll);
		sampleIdCB.setContainerDataSource(container);
		sampleIdCB.setItemCaptionPropertyId(Tag_.libTag.getName());
		sampleIdCB.setItemIconPropertyId(Tag.PROPERTY_ICON);
		sampleIdCB.setImmediate(true);
		return sampleIdCB;
	}

	/**
	 * @return un tag correspondant au tag null dans la combobox de recherche de candidature : utilisé pour n'afficher que les tags qui sont null
	 */
	public Tag getNullTag() {
		final Tag tagNull = new Tag();
		tagNull.setIdTag(-1);
		tagNull.setLibTag(applicationContext.getMessage("filter.null", null, UI.getCurrent().getLocale()));
		return tagNull;
	}

	/**
	 * @param  libExcept
	 * @return           la combo des types de statut
	 */
	private ComboBox getComboBoxTypStatut(final String libExcept) {
		final List<String> list = new ArrayList<>();
		cacheController.getListeTypeStatut().forEach(e -> list.add(e.getLibTypStatut()));
		return generateComboBox(list, null, libExcept);
	}

	/**
	 * @param  libExcept
	 * @return           la combo des types de traitement
	 */
	private ComboBox getComboBoxTypTrait(final String libExcept) {
		final List<String> list = new ArrayList<>();
		cacheController.getListeTypeTraitement().stream().sorted((f1, f2) -> f2.getCodTypTrait().compareTo(f1.getCodTypTrait())).forEach(e -> list.add(e.getLibTypTrait()));
		return generateComboBox(list, null, libExcept);
	}

	/**
	 * @param  libNull
	 * @return         la combo des types de decision
	 */
	private ComboBox getComboBoxTypDec(final String libNull) {
		final List<String> list = new ArrayList<>();
		typeDecisionController.getTypeDecisionsEnServiceByCtrCand(getCentreCandidature()).forEach(e -> list.add(e.getLibTypDec()));
		return generateComboBox(list, libNull, null);
	}

	/**
	 * @param  liste
	 * @param  libNull
	 * @return         une combo grace a la liste
	 */
	private ComboBox generateComboBox(final List<String> liste, final String libNull, final String libExcept) {
		final ComboBox sampleIdCB = new ComboBox();
		sampleIdCB.setPageLength(20);
		sampleIdCB.setTextInputAllowed(false);
		final BeanItemContainer<String> dataList = new BeanItemContainer<>(String.class);
		dataList.addBean(applicationContext.getMessage("filter.all", null, UI.getCurrent().getLocale()));
		if (libNull != null) {
			dataList.addBean(libNull);
		}
		if (libExcept != null) {
			dataList.addBean(libExcept);
		}
		dataList.addAll(liste);
		sampleIdCB.setNullSelectionItemId(applicationContext.getMessage("filter.all", null, UI.getCurrent().getLocale()));
		sampleIdCB.setContainerDataSource(dataList);
		sampleIdCB.setImmediate(true);
		return sampleIdCB;
	}

	/**
	 * @param  candidature
	 * @param  listeAlerteSva
	 * @param  dateSva
	 * @param  definitifSva
	 * @return                le style sva correspondand
	 */
	public String getStyleSva(final Candidature candidature, final List<AlertSva> listeAlerteSva, final String dateSva, final Boolean definitifSva) {
		if (dateSva.equals(NomenclatureUtils.CAND_DAT_NO_DAT) || listeAlerteSva.size() == 0) {
			return null;
		}

		final TypeDecisionCandidature typeDecisionCandidature = candidature.getLastTypeDecision();
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
		final Long nbDays = ChronoUnit.DAYS.between(dateToUse, LocalDate.now());
		Integer nbJourSva = null;
		for (final AlertSva alert : listeAlerteSva) {
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

	// private void removeAlertSva() {
	// String js =
	// "function effaceCss(){\r\n" +
	// " var arrayStyleToRemove = [];\r\n" +
	// " var j = 0;\r\n" +
	// " for(var i= 0; i < document.getElementsByTagName('style').length; i++)\r\n" +
	// " { \r\n" +
	// " var style = document.getElementsByTagName('style')[i];\r\n" +
	// " if (style.innerHTML.indexOf('" + StyleConstants.GRID_ROW_SVA + "') >= 0){\r\n" +
	// " console.log(style.innerHTML);\r\n" +
	// " arrayStyleToRemove[j] = style;\r\n" +
	// " console.log(j);\r\n" +
	// " j++;\r\n" +
	// " }\r\n" +
	// " }\r\n" +
	// " console.log(arrayStyleToRemove.length);\r\n" +
	// " for(var j= 0; j < arrayStyleToRemove.length; j++){\r\n" +
	// " \r\n" +
	// " arrayStyleToRemove[j].remove();\r\n" +
	// " }\r\n" +
	// "}\r\n" +
	// "function injectCss(){\r\n" +
	// " var style = document.createElement('style'); // is a node\r\n" +
	// " style.innerHTML = '.v-grid .v-grid-row-sva-45 .v-grid-cell { background-color: #ff4165; }';\r\n" +
	// " document.getElementsByTagName(\"head\")[0].appendChild(style);\r\n" +
	// "}" +
	// "effaceCss();\r\n" +
	// "injectCss();";
	//
	// Page.getCurrent().getJavaScript().execute(js);
	// }

	/**
	 * Decoche les elements invisibles
	 * @param entity
	 */
	private void deselectFilter() {
		if (modeModif) {
			final List<Candidature> listeToDeselect = new ArrayList<>();
			getListeCandidatureSelected().forEach(e -> {
				if (!candidatureGrid.getContainerDataSource().getItemIds().contains(e)) {
					listeToDeselect.add(e);
				}
			});
			((MultiSelectionModel) candidatureGrid.getSelectionModel()).deselect(listeToDeselect);
		}
		majNbCandidatures();
	}

	/**
	 * Supprime une entité de la table
	 * @param entity
	 */
	public void removeEntity(final Candidature entity) {
		if (!isEntityApartientCommission(entity)) {
			return;
		}
		candidatureGrid.removeItem(entity);
		deselectFilter();
	}

	/**
	 * Persisite une entité de la table
	 * @param entity
	 */
	public void addEntity(final Candidature entity) {
		if (!isEntityApartientCommission(entity)) {
			return;
		}
		candidatureGrid.addItem(entity);
		deselectFilter();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureMasseListener#actionMasse()
	 */
	@Override
	public void actionMasse() {
		if (parametreController.getIsWarningCandSelect()) {
			final Integer nbSelected = getListeCandidatureSelected().size();
			if (nbSelected > 0) {
				final Notification notif = new Notification(applicationContext.getMessage("candidature.action.selected", new Object[] { nbSelected }, UI.getCurrent().getLocale()), Type.ERROR_MESSAGE);
				notif.setPosition(Position.MIDDLE_CENTER);
				notif.setDelayMsec(3000);
				notif.show(Page.getCurrent());
			}
		}
	}
}
