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
package fr.univlorraine.ecandidat.views;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.Grid.CellDescriptionGenerator;
import com.vaadin.v7.ui.Grid.CellReference;
import com.vaadin.v7.ui.Grid.MultiSelectionModel;
import com.vaadin.v7.ui.Grid.SelectionMode;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.v7.ui.renderers.ImageRenderer;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.FormationController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission_;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.GridConverter.StringToThemeRessourceConverter;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * Page de gestion des formations du centre de candidature
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CtrCandFormationView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandFormationView extends VerticalLayout implements View, EntityPushListener<Formation> {

	public static final String NAME = "ctrCandFormationView";

	public static final String[] FIELDS_ORDER = { Formation.FLAG_COLUMN_NAME,
		Formation_.codForm.getName(),
		Formation_.libForm.getName(),
		Formation_.commission.getName() + "." + Commission_.libComm.getName(),
		Formation_.temDematForm.getName(),
		Formation_.tesForm.getName(),
		Formation.DAT_VOEUX_COLUMN_NAME,
		Formation_.datRetourForm.getName(),
		Formation_.capaciteForm.getName() };

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormationController formationController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient EntityPusher<Formation> formationEntityPusher;

	@Resource
	private transient DateTimeFormatter formatterDate;

	/* Le droit sur la vue */
	private SecurityCtrCandFonc securityCtrCandFonc;

	/* Composants */
	private final GridFormatting<Formation> formationGrid = new GridFormatting<>(Formation.class);

	/** Initialise la vue */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		/* Récupération du centre de canidature en cours */
		securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FORMATION);
		if (securityCtrCandFonc.hasNoRight()) {
			return;
		}

		/* Titre */
		final HorizontalLayout hlTitle = new HorizontalLayout();
		hlTitle.setSpacing(true);
		addComponent(hlTitle);

		final Label titleParam = new Label(applicationContext.getMessage("formation.title", new Object[] { securityCtrCandFonc.getCtrCand().getLibCtrCand() }, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		hlTitle.addComponent(titleParam);
		final PopupView puv = new PopupView(applicationContext.getMessage("formation.table.flagEtat.tooltip", null, UI.getCurrent().getLocale()), getLegendLayout());
		hlTitle.addComponent(puv);
		hlTitle.setComponentAlignment(puv, Alignment.MIDDLE_LEFT);

		/* Boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		/* Nouvelle formation */
		final OneClickButton btnNew = new OneClickButton(applicationContext.getMessage("formation.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			formationController.editNewFormation(securityCtrCandFonc);
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);

		/* Edition de formation */
		final OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			final Formation f = getFormation();
			if (f instanceof Formation) {
				formationController.editFormation(f, securityCtrCandFonc);
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);

		/* Edition de dates */
		//
		final OneClickButton btnEditDate = new OneClickButton(applicationContext.getMessage("formation.btnEditDate", null, UI.getCurrent().getLocale()), FontAwesome.CALENDAR);
		btnEditDate.setDescription(applicationContext.getMessage("formation.btnEditDate.desc", null, UI.getCurrent().getLocale()));
		btnEditDate.setEnabled(false);
		btnEditDate.addClickListener(e -> {
			formationController.editDates(getFormations(), securityCtrCandFonc.getCtrCand());
		});
		buttonsLayout.addComponent(btnEditDate);
		buttonsLayout.setComponentAlignment(btnEditDate, Alignment.MIDDLE_CENTER);

		/* Edition des pièces */
		final OneClickButton btnEditPieceComp = new OneClickButton(applicationContext.getMessage("formation.btnEditPiece", null, UI.getCurrent().getLocale()), FontAwesome.FILE_TEXT_O);
		btnEditPieceComp.setEnabled(false);
		btnEditPieceComp.addClickListener(e -> {
			formationController.editPieceCompFormation(getFormations(), securityCtrCandFonc.getCtrCand());
		});
		buttonsLayout.addComponent(btnEditPieceComp);
		buttonsLayout.setComponentAlignment(btnEditPieceComp, Alignment.MIDDLE_CENTER);

		/* Suppression formation */
		final OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			final Formation f = getFormation();
			if (f instanceof Formation) {
				formationController.deleteFormation(f);
			}
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);

		final OneClickButton btnExport = new OneClickButton(FontAwesome.FILE_EXCEL_O);
		/* Export de la liste des formations */
		btnExport.setDescription(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()));
		btnExport.setDisableOnClick(true);
		new OnDemandFileDownloader(new OnDemandStreamFile() {
			@Override
			public OnDemandFile getOnDemandFile() {
				@SuppressWarnings("unchecked")
				final List<Formation> listeForm = (List<Formation>) formationGrid.getContainerDataSource().getItemIds();

				if (listeForm.size() == 0) {
					btnExport.setEnabled(true);
					return null;
				}

				/* Téléchargement */
				final OnDemandFile file = formationController.generateExport(listeForm, securityCtrCandFonc);
				if (file != null) {
					btnExport.setEnabled(true);
					return file;
				}
				btnExport.setEnabled(true);
				return null;
			}
		}, btnExport);
		buttonsLayout.addComponent(btnExport);
		buttonsLayout.setComponentAlignment(btnExport, Alignment.MIDDLE_RIGHT);

		/* Table des formations */
		formationGrid.initColumn(FIELDS_ORDER, "formation.table.", Formation_.codForm.getName());
		formationGrid.setSelectionMode(SelectionMode.MULTI);
		formationGrid.addItems(formationController.getFormationsByCtrCand(securityCtrCandFonc));
		formationGrid.getColumn(Formation.FLAG_COLUMN_NAME).setRenderer(new ImageRenderer(), new StringToThemeRessourceConverter());
		formationGrid.setCellDescriptionGenerator(new CellDescriptionGenerator() {

			@Override
			public String getDescription(final CellReference cell) {
				final Formation f = (Formation) cell.getItemId();
				if (cell.getPropertyId().equals(Formation.FLAG_COLUMN_NAME)) {
					try {
						String code = null;
						if (cell.getPropertyId().equals(Formation.FLAG_COLUMN_NAME)) {
							code = f.getFlagEtat();
							if (code != null) {
								return applicationContext.getMessage("formation.table.flagEtat.tooltip." + code, null, UI.getCurrent().getLocale());
							}
						}
					} catch (final Exception e) {
					}
				}
				return null;
			}
		});
		formationGrid.removeFilterCells(Formation.FLAG_COLUMN_NAME);
		formationGrid.setColumnsWidth(62, Formation.FLAG_COLUMN_NAME);
		formationGrid.setColumnsWidth(130, Formation_.codForm.getName());
		formationGrid.setColumnsWidth(143, Formation_.temDematForm.getName());
		formationGrid.setColumnsWidth(100, Formation_.tesForm.getName());
		formationGrid.setColumnsWidth(200, Formation.DAT_VOEUX_COLUMN_NAME);
		formationGrid.setColumnsWidth(260, Formation_.commission.getName() + "." + Commission_.libComm.getName());
		formationGrid.addSelectionListener(e -> {
			/* Les boutons d'édition et de suppression de formation sont actifs seulement si
			 * une formation est sélectionnée. */
			final Integer nb = formationGrid.getSelectedRows().size();
			btnEdit.setEnabled(nb == 1);
			btnDelete.setEnabled(nb == 1);
			btnEditPieceComp.setEnabled(nb >= 1);
			btnEditDate.setEnabled(nb >= 1);
		});

		formationGrid.addItemClickListener(e -> {
			/* Suivant le mode de slection de la grid on fait un traitement */
			final MultiSelectionModel selection = (MultiSelectionModel) formationGrid.getSelectionModel();
			selection.deselectAll();
			try {
				selection.select(e.getItemId());
			} catch (final Exception e1) {
				return;
			}
		});

		addComponent(formationGrid);
		setExpandRatio(formationGrid, 1);
		formationGrid.setSizeFull();

		/* Gestion du readOnly */
		if (securityCtrCandFonc.isWrite()) {
			formationGrid.addItemClickListener(e -> {
				if (e.isDoubleClick()) {
					formationGrid.select(e.getItemId());
					btnEdit.click();
				}
			});
			buttonsLayout.setVisible(true);
		} else {
			buttonsLayout.setVisible(false);
		}

		/* Inscrit la vue aux mises à jour de formation */
		formationEntityPusher.registerEntityPushListener(this);
	}

	/** @return la formation selectionnée */
	private Formation getFormation() {
		final List<Formation> liste = getFormations();
		if (liste.size() == 0 || liste.size() > 1) {
			return null;
		}
		return liste.get(0);
	}

	/** @return les formations selectionnées */
	private List<Formation> getFormations() {
		return formationGrid.getSelectedRows().stream().map(e -> (Formation) e).collect(Collectors.toList());
	}

	/** @return le layout de légende */
	private VerticalLayout getLegendLayout() {
		final VerticalLayout vlLegend = new VerticalLayout();
		// vlLegend.setWidth(300, Unit.PIXELS);
		vlLegend.setMargin(true);
		vlLegend.setSpacing(true);

		final Label labelTitle = new Label(applicationContext.getMessage("formation.table.flagEtat.tooltip", null, UI.getCurrent().getLocale()));
		labelTitle.addStyleName(StyleConstants.VIEW_TITLE);

		vlLegend.addComponent(labelTitle);

		vlLegend.addComponent(getLegendLineLayout(ConstanteUtils.FLAG_GREEEN));
		vlLegend.addComponent(getLegendLineLayout(ConstanteUtils.FLAG_RED));
		vlLegend.addComponent(getLegendLineLayout(ConstanteUtils.FLAG_YELLOW));
		vlLegend.addComponent(getLegendLineLayout(ConstanteUtils.FLAG_BLUE));
		return vlLegend;
	}

	/**
	 * @param  txtCode
	 * @return         une ligne de légende
	 */
	private HorizontalLayout getLegendLineLayout(final String txtCode) {
		final HorizontalLayout hlLineLegend = new HorizontalLayout();
		hlLineLegend.setWidth(100, Unit.PERCENTAGE);
		hlLineLegend.setSpacing(true);

		final Image flagImg = new Image(null, new ThemeResource("images/icon/Flag-" + txtCode + "-icon.png"));
		final Label label = new Label(applicationContext.getMessage("formation.table.flagEtat.tooltip." + txtCode, null, UI.getCurrent().getLocale()));
		hlLineLegend.addComponent(flagImg);
		hlLineLegend.setComponentAlignment(flagImg, Alignment.MIDDLE_LEFT);
		hlLineLegend.addComponent(label);
		hlLineLegend.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
		hlLineLegend.setExpandRatio(label, 1);
		return hlLineLegend;
	}

	/** @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent) */
	@Override
	public void enter(final ViewChangeEvent event) {
	}

	/** @see com.vaadin.ui.AbstractComponent#detach() */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de formation */
		formationEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/** @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object) */
	@Override
	public void entityPersisted(final Formation entity) {
		formationGrid.removeItem(entity);
		if (formationController.hasRighToSeeFormation(entity, securityCtrCandFonc)) {
			formationGrid.addItem(formationController.alimenteFormationData(entity));
			formationGrid.sort();
		}
	}

	/** @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object) */
	@Override
	public void entityUpdated(final Formation entity) {
		formationGrid.removeItem(entity);
		if (formationController.hasRighToSeeFormation(entity, securityCtrCandFonc)) {
			formationGrid.addItem(formationController.alimenteFormationData(entity));
			formationGrid.sort();
		}
	}

	/** @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object) */
	@Override
	public void entityDeleted(final Formation entity) {
		formationGrid.removeItem(entity);
	}
}
