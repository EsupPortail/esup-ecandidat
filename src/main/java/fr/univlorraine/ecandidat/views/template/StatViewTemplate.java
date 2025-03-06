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

import java.util.Comparator;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.CheckBox;
import com.vaadin.v7.ui.Grid.FooterCell;
import com.vaadin.v7.ui.Grid.FooterRow;
import com.vaadin.v7.ui.Grid.HeaderRow;
import com.vaadin.v7.ui.Grid.RowReference;
import com.vaadin.v7.ui.Grid.RowStyleGenerator;
import com.vaadin.v7.ui.Grid.SelectionMode;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CampagneController;
import fr.univlorraine.ecandidat.controllers.StatController;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.StatFormationPresentation;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting.FilterListener;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import jakarta.annotation.Resource;

/**
 * Template de tableau de bord
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class StatViewTemplate extends VerticalLayout {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient StatController statController;
	@Resource
	private transient CampagneController campagneController;

	/* Composants */
	private RequiredComboBox<Campagne> cbCampagne;
	private GridFormatting<StatFormationPresentation> grid;
	private FooterRow footerRow;

	/* Variables */
	private StatFormationPresentation footerStat;

	protected CheckBox cbDisplayHs = new CheckBox();

	/**
	 * Initialise le template
	 * @param title
	 * @param code
	 * @param libelle
	 */
	public void init(final String title, final String code, final String libelle, final String libelleHs) {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		/* Titre */
		final Label titleParam = new Label(title);
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);

		/* Boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		/* Choix de campagne */
		final HorizontalLayout campagneLayout = new HorizontalLayout();
		campagneLayout.setWidth(100, Unit.PERCENTAGE);
		campagneLayout.setSpacing(true);
		buttonsLayout.addComponent(campagneLayout);

		/* Label campagne */
		final Label labelCampagne = new Label(
			applicationContext.getMessage("stat.change.camp", null, UI.getCurrent().getLocale()));
		labelCampagne.setSizeUndefined();
		campagneLayout.addComponent(labelCampagne);
		campagneLayout.setComponentAlignment(labelCampagne, Alignment.MIDDLE_LEFT);

		/* ListeBox campagne */
		final List<Campagne> listeCampagne = campagneController.getCampagnes();
		listeCampagne.sort(Comparator.comparing(Campagne::getCodCamp).reversed());
		cbCampagne = new RequiredComboBox<>(listeCampagne, Campagne.class);
		cbCampagne.setValue(campagneController.getCampagneActive());
		campagneLayout.addComponent(cbCampagne);
		campagneLayout.setExpandRatio(cbCampagne, 1);
		campagneLayout.setComponentAlignment(cbCampagne, Alignment.BOTTOM_LEFT);
		cbCampagne.addValueChangeListener(e -> {
			majContainer();
		});

		cbDisplayHs.setCaption(libelleHs);
		campagneLayout.addComponent(cbDisplayHs);
		campagneLayout.setComponentAlignment(cbDisplayHs, Alignment.MIDDLE_LEFT);
		cbDisplayHs.addValueChangeListener(e -> {
			majContainer();
		});

		/* Export des candidatures désactivé */
		/* OneClickButton btnExportCandidature = new
		 * OneClickButton(applicationContext.getMessage("stat.export.candidature.btn",
		 * null, UI.getCurrent().getLocale()), FontAwesome.FILE_EXCEL_O);
		 * btnExportCandidature.setDescription(applicationContext.getMessage(
		 * "stat.export.candidature.btn", null, UI.getCurrent().getLocale()));
		 * btnExportCandidature.addClickListener(e->{ CtrCandExportWindow window = new
		 * CtrCandExportWindow(securityCtrCandFonc.getCtrCand());
		 * UI.getCurrent().addWindow(window); }); */

		/* La grid */
		grid = new GridFormatting<>(StatFormationPresentation.class);
		grid.initColumn(new String[] { StatFormationPresentation.CHAMPS_COD,
			StatFormationPresentation.CHAMPS_LIB,
			StatFormationPresentation.CHAMPS_LIB_SUPP,
			StatFormationPresentation.CHAMPS_NB_CANDIDATURE_TOTAL,
			StatFormationPresentation.CHAMPS_NB_CANDIDATURE_CANCEL,
			StatFormationPresentation.CHAMPS_NB_STATUT_ATTENTE,
			StatFormationPresentation.CHAMPS_NB_STATUT_RECEPTIONNE,
			StatFormationPresentation.CHAMPS_NB_STATUT_COMPLET,
			StatFormationPresentation.CHAMPS_NB_STATUT_INCOMPLET,
			StatFormationPresentation.CHAMPS_NB_AVIS_FAVORABLE,
			StatFormationPresentation.CHAMPS_NB_AVIS_DEFAVORABLE,
			StatFormationPresentation.CHAMPS_NB_AVIS_LISTECOMP,
			StatFormationPresentation.CHAMPS_NB_AVIS_LISTEATTENTE,
			StatFormationPresentation.CHAMPS_NB_AVIS_PRESELECTION,
			StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL,
			StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL_VALIDE,
			StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL_NON_VALIDE,
			StatFormationPresentation.CHAMPS_NB_CONFIRM,
			StatFormationPresentation.CHAMPS_NB_DESIST,
			StatFormationPresentation.CHAMPS_CAPACITE_ACCUEIL }, "stat.table.", StatFormationPresentation.CHAMPS_LIB);
		grid.setSizeFull();
		grid.setFrozenColumnCount(2);
		grid.setSelectionMode(SelectionMode.NONE);

		/* Largeur des colonnes */
		grid.setColumnWidth(StatFormationPresentation.CHAMPS_COD, 150);
		grid.setColumnsWidth(300, StatFormationPresentation.CHAMPS_LIB, StatFormationPresentation.CHAMPS_LIB_SUPP);

		grid.setColumnsWidth(115, StatFormationPresentation.CHAMPS_NB_CANDIDATURE_TOTAL,
			StatFormationPresentation.CHAMPS_NB_CANDIDATURE_CANCEL,
			StatFormationPresentation.CHAMPS_NB_STATUT_ATTENTE,
			StatFormationPresentation.CHAMPS_NB_STATUT_RECEPTIONNE,
			StatFormationPresentation.CHAMPS_NB_STATUT_COMPLET,
			StatFormationPresentation.CHAMPS_NB_STATUT_INCOMPLET,
			StatFormationPresentation.CHAMPS_NB_AVIS_FAVORABLE,
			StatFormationPresentation.CHAMPS_NB_AVIS_DEFAVORABLE,
			StatFormationPresentation.CHAMPS_NB_AVIS_LISTECOMP,
			StatFormationPresentation.CHAMPS_NB_AVIS_LISTEATTENTE,
			StatFormationPresentation.CHAMPS_NB_AVIS_PRESELECTION, StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL,
			StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL_VALIDE,
			StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL_NON_VALIDE, StatFormationPresentation.CHAMPS_NB_CONFIRM,
			StatFormationPresentation.CHAMPS_NB_DESIST);

		grid.removeFilterCells(StatFormationPresentation.CHAMPS_NB_CANDIDATURE_TOTAL,
			StatFormationPresentation.CHAMPS_NB_CANDIDATURE_CANCEL,
			StatFormationPresentation.CHAMPS_NB_STATUT_ATTENTE,
			StatFormationPresentation.CHAMPS_NB_STATUT_RECEPTIONNE,
			StatFormationPresentation.CHAMPS_NB_STATUT_COMPLET,
			StatFormationPresentation.CHAMPS_NB_STATUT_INCOMPLET,
			StatFormationPresentation.CHAMPS_NB_AVIS_FAVORABLE,
			StatFormationPresentation.CHAMPS_NB_AVIS_DEFAVORABLE,
			StatFormationPresentation.CHAMPS_NB_AVIS_LISTECOMP,
			StatFormationPresentation.CHAMPS_NB_AVIS_LISTEATTENTE,
			StatFormationPresentation.CHAMPS_NB_AVIS_PRESELECTION, StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL,
			StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL_VALIDE,
			StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL_NON_VALIDE, StatFormationPresentation.CHAMPS_NB_CONFIRM,
			StatFormationPresentation.CHAMPS_NB_DESIST);

		/* Header */
		final HeaderRow headerRow = grid.prependHeaderRow();
		headerRow.setStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		headerRow.join(StatFormationPresentation.CHAMPS_COD, StatFormationPresentation.CHAMPS_LIB).setText("");
		headerRow
			.join(StatFormationPresentation.CHAMPS_NB_CANDIDATURE_TOTAL,
				StatFormationPresentation.CHAMPS_NB_CANDIDATURE_CANCEL)
			.setText(applicationContext.getMessage("stat.table.header.join.nbCandidature", null,
				UI.getCurrent().getLocale()));
		headerRow
			.join(StatFormationPresentation.CHAMPS_NB_STATUT_ATTENTE,
				StatFormationPresentation.CHAMPS_NB_STATUT_RECEPTIONNE,
				StatFormationPresentation.CHAMPS_NB_STATUT_COMPLET,
				StatFormationPresentation.CHAMPS_NB_STATUT_INCOMPLET)
			.setText(applicationContext.getMessage("stat.table.header.join.statut", null,
				UI.getCurrent().getLocale()));
		headerRow
			.join(StatFormationPresentation.CHAMPS_NB_AVIS_FAVORABLE,
				StatFormationPresentation.CHAMPS_NB_AVIS_DEFAVORABLE,
				StatFormationPresentation.CHAMPS_NB_AVIS_LISTECOMP,
				StatFormationPresentation.CHAMPS_NB_AVIS_LISTEATTENTE,
				StatFormationPresentation.CHAMPS_NB_AVIS_PRESELECTION)
			.setText(applicationContext.getMessage("stat.table.header.join.avis", null,
				UI.getCurrent().getLocale()));
		headerRow
			.join(StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL,
				StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL_VALIDE,
				StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL_NON_VALIDE)
			.setText(applicationContext.getMessage("stat.table.header.join.avis.total", null,
				UI.getCurrent().getLocale()));
		headerRow.join(StatFormationPresentation.CHAMPS_NB_CONFIRM, StatFormationPresentation.CHAMPS_NB_DESIST).setText(
			applicationContext.getMessage("stat.table.header.join.confirm", null, UI.getCurrent().getLocale()));

		addComponent(grid);
		setExpandRatio(grid, 1);

		/* Ajout des totaux dans le footer */
		footerRow = grid.prependFooterRow();
		footerRow.setStyleName(StyleConstants.GRID_FOOTER);
		final FooterCell cellTxt = footerRow.join(StatFormationPresentation.CHAMPS_COD, StatFormationPresentation.CHAMPS_LIB);
		cellTxt.setText(applicationContext.getMessage("stat.table.footer.join.sum", null, UI.getCurrent().getLocale()));
		cellTxt.setStyleName(StyleConstants.GRID_FOOTER_TITLE);
		grid.addFilterListener(new FilterListener() {

			@Override
			public void filter() {
				updateFooter();
			}
		});

		grid.setRowStyleGenerator(new RowStyleGenerator() {

			@Override
			public String getStyle(final RowReference row) {
				final StatFormationPresentation pres = (StatFormationPresentation) row.getItemId();
				if (!pres.getTes()) {
					return StyleConstants.GRID_ROW_STAT_HS;
				}
				return null;
			}
		});

		/* Export */
		final Button btnExport = new Button(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()),
			FontAwesome.FILE_EXCEL_O);
		btnExport.setDescription(applicationContext.getMessage("btnExport", null, UI.getCurrent().getLocale()));
		buttonsLayout.addComponent(btnExport);
		buttonsLayout.setComponentAlignment(btnExport, Alignment.MIDDLE_RIGHT);
		btnExport.setDisableOnClick(true);
		new OnDemandFileDownloader(new OnDemandStreamFile() {
			@Override
			public OnDemandFile getOnDemandFile() {
				final OnDemandFile file = statController.generateExport(getCampagne(), code, libelle, grid.getItems(),
					footerStat, getLibelleExport(), getLibelleSuppExport(), getDisplayCapaciteAccueil());
				if (file != null) {
					btnExport.setEnabled(true);
					return file;
				}
				btnExport.setEnabled(true);
				return null;
			}
		}, btnExport);
	}

	/**
	 * @return la campagne
	 */
	protected Campagne getCampagne() {
		return (Campagne) cbCampagne.getValue();
	}

	/**
	 * @return le témoin en service
	 */
	protected Boolean getDisplayHs() {
		return cbDisplayHs.getValue();
	}

	/**
	 * Met a jour le container
	 */
	protected void majContainer() {
	}

	/**
	 * Met a jour le container
	 */
	protected void majContainer(final List<StatFormationPresentation> listeStat) {
		grid.removeAndAddAll(listeStat);
		updateFooter();
	}

	/**
	 * Renvoi le libellé de colonne de l'export
	 */
	protected String getLibelleExport() {
		return null;
	}

	/**
	 * Renvoi le libellé de colonne de l'export
	 */
	protected String getLibelleSuppExport() {
		return null;
	}

	/**
	 * Renvoi true si on affiche la capacite d'accueil
	 */
	protected Boolean getDisplayCapaciteAccueil() {
		return false;
	}

	/**
	 * Supprime la colonne de libellé supplémentaire
	 */
	protected void removeColonnes(final String... propertys) {
		for (final String property : propertys) {
			grid.removeColumn(property);
		}
	}

	/**
	 * :odifie le footer
	 */
	private void updateFooter() {
		footerStat = new StatFormationPresentation();
		footerStat.setFooter();
		for (final StatFormationPresentation stat : grid.getItems()) {
			/* Nombre de candidature total */
			footerStat.setNbCandidatureTotal(
				footerStat.getNbCandidatureTotal() + MethodUtils.getLongValue(stat.getNbCandidatureTotal()));
			/* Nombre de candidature cancel */
			footerStat.setNbCandidatureCancel(
				footerStat.getNbCandidatureCancel() + MethodUtils.getLongValue(stat.getNbCandidatureCancel()));
			/* Les statuts de dossier */
			footerStat.setNbStatutAttente(
				footerStat.getNbStatutAttente() + MethodUtils.getLongValue(stat.getNbStatutAttente()));
			footerStat.setNbStatutComplet(
				footerStat.getNbStatutComplet() + MethodUtils.getLongValue(stat.getNbStatutComplet()));
			footerStat.setNbStatutIncomplet(
				footerStat.getNbStatutIncomplet() + MethodUtils.getLongValue(stat.getNbStatutIncomplet()));
			footerStat.setNbStatutReceptionne(
				footerStat.getNbStatutReceptionne() + MethodUtils.getLongValue(stat.getNbStatutReceptionne()));
			/* Les avis */
			footerStat.setNbAvisFavorable(
				footerStat.getNbAvisFavorable() + MethodUtils.getLongValue(stat.getNbAvisFavorable()));
			footerStat.setNbAvisDefavorable(
				footerStat.getNbAvisDefavorable() + MethodUtils.getLongValue(stat.getNbAvisDefavorable()));
			footerStat.setNbAvisListeAttente(
				footerStat.getNbAvisListeAttente() + MethodUtils.getLongValue(stat.getNbAvisListeAttente()));
			footerStat.setNbAvisListeComp(
				footerStat.getNbAvisListeComp() + MethodUtils.getLongValue(stat.getNbAvisListeComp()));
			footerStat.setNbAvisPreselection(
				footerStat.getNbAvisPreselection() + MethodUtils.getLongValue(stat.getNbAvisPreselection()));
			/* Total des avis */
			footerStat.setNbAvisTotal(footerStat.getNbAvisTotal() + MethodUtils.getLongValue(stat.getNbAvisTotal()));
			footerStat.setNbAvisTotalValide(
				footerStat.getNbAvisTotalValide() + MethodUtils.getLongValue(stat.getNbAvisTotalValide()));
			footerStat.setNbAvisTotalNonValide(
				footerStat.getNbAvisTotalNonValide() + MethodUtils.getLongValue(stat.getNbAvisTotalNonValide()));
			/* Les confirmations */
			footerStat.setNbConfirm(footerStat.getNbConfirm() + MethodUtils.getLongValue(stat.getNbConfirm()));
			footerStat.setNbDesist(footerStat.getNbDesist() + MethodUtils.getLongValue(stat.getNbDesist()));
			/* Capacite accueil totale */
			if (getDisplayCapaciteAccueil()) {
				footerStat.setCapaciteAccueil(
					footerStat.getCapaciteAccueil() + MethodUtils.getLongValue(stat.getCapaciteAccueil()));
			}
		}
		/* Les totaux */
		/* Nombre de candidature total */
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_CANDIDATURE_TOTAL)
			.setText(footerStat.getNbCandidatureTotal().toString());
		/* Nombre de candidature total */
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_CANDIDATURE_CANCEL)
			.setText(footerStat.getNbCandidatureCancel().toString());
		/* Les statuts de dossier */
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_STATUT_ATTENTE)
			.setText(footerStat.getNbStatutAttente().toString());
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_STATUT_RECEPTIONNE)
			.setText(footerStat.getNbStatutReceptionne().toString());
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_STATUT_COMPLET)
			.setText(footerStat.getNbStatutComplet().toString());
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_STATUT_INCOMPLET)
			.setText(footerStat.getNbStatutIncomplet().toString());
		/* Les avis */
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_AVIS_FAVORABLE)
			.setText(footerStat.getNbAvisFavorable().toString());
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_AVIS_DEFAVORABLE)
			.setText(footerStat.getNbAvisDefavorable().toString());
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_AVIS_LISTECOMP)
			.setText(footerStat.getNbAvisListeComp().toString());
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_AVIS_LISTEATTENTE)
			.setText(footerStat.getNbAvisListeAttente().toString());
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_AVIS_PRESELECTION)
			.setText(footerStat.getNbAvisPreselection().toString());
		/* Total des avis */
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL)
			.setText(footerStat.getNbAvisTotal().toString());
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL_VALIDE)
			.setText(footerStat.getNbAvisTotalValide().toString());
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_AVIS_TOTAL_NON_VALIDE)
			.setText(footerStat.getNbAvisTotalNonValide().toString());
		/* Les confirmations */
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_CONFIRM).setText(footerStat.getNbConfirm().toString());
		footerRow.getCell(StatFormationPresentation.CHAMPS_NB_DESIST).setText(footerStat.getNbDesist().toString());
		/* Capacite accueil */
		if (getDisplayCapaciteAccueil()) {
			footerRow.getCell(StatFormationPresentation.CHAMPS_CAPACITE_ACCUEIL).setText(footerStat.getCapaciteAccueil().toString());
		}
	}
}
