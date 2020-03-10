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

import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne_;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat_;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de recherche de candidat
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class SearchCandidatWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;

	public static final String[] PEOPLE_FIELDS_ORDER = {
		CompteMinima_.campagne.getName() + "." + Campagne_.codCamp.getName(),
		CompteMinima_.numDossierOpiCptMin.getName(),
		CompteMinima_.nomCptMin.getName(),
		CompteMinima_.prenomCptMin.getName(),
		CompteMinima_.loginCptMin.getName(),
		CompteMinima_.supannEtuIdCptMin.getName(),
		CompteMinima_.candidat.getName() + "." + Candidat_.nomPatCandidat.getName(),
		CompteMinima_.candidat.getName() + "." + Candidat_.prenomCandidat.getName() };

	/* Composants */
	private final TextField searchBox;
	private final OneClickButton btnSearch;
	private final CheckBox cbExactSearch;
	private final CheckBox cbOtherYears;
	private final GridFormatting<CompteMinima> grid = new GridFormatting<>(CompteMinima.class);
	private final OneClickButton btnValider;
	private final OneClickButton btnAnnuler;

	/* Listener */
	private CompteMinimaListener compteMinimaListener;

	/** Crée une fenêtre de recherche de candidat */
	public SearchCandidatWindow() {

		/* Style */
		setWidth(1100, Unit.PIXELS);
		setHeight(550, Unit.PIXELS);
		setModal(true);
		setResizable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setHeight(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);

		/* Titre */
		setCaption(applicationContext.getMessage("window.search.candidat.title",
			new Object[]
			{ ConstanteUtils.NB_MAX_RECH_CPT_MIN },
			UI.getCurrent().getLocale()));

		/* Recherche */
		final HorizontalLayout searchLayout = new HorizontalLayout();
		searchBox = new TextField();
		searchBox.addShortcutListener(new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {

			/** serialVersionUID **/
			private static final long serialVersionUID = 4119756957960484247L;

			@Override
			public void handleAction(final Object sender, final Object target) {
				performSearch();
			}
		});
		searchBox.focus();

		btnSearch = new OneClickButton(applicationContext.getMessage("window.search", null, UI.getCurrent().getLocale()));
		btnSearch.addClickListener(e -> performSearch());

		cbExactSearch = new CheckBox(applicationContext.getMessage("window.search.candidat.exact.search", null, UI.getCurrent().getLocale()));
		cbExactSearch.setValue(false);

		cbOtherYears = new CheckBox(applicationContext.getMessage("window.search.candidat.all.campagne", null, UI.getCurrent().getLocale()));
		cbOtherYears.setValue(false);
		searchLayout.setSpacing(true);
		searchLayout.addComponent(searchBox);
		searchLayout.addComponent(btnSearch);
		searchLayout.addComponent(cbExactSearch);
		searchLayout.addComponent(cbOtherYears);
		searchLayout.setComponentAlignment(cbExactSearch, Alignment.MIDDLE_LEFT);
		searchLayout.setComponentAlignment(cbOtherYears, Alignment.MIDDLE_LEFT);

		/* Ajout des commandes */
		layout.addComponent(searchLayout);

		/* Table de Resultat de recherche */
		grid.initColumn(PEOPLE_FIELDS_ORDER, "cptMin.", CompteMinima_.nomCptMin.getName());
		grid.setColumnWidth(CompteMinima_.campagne.getName() + "." + Campagne_.codCamp.getName(), 100);
		grid.setColumnWidth(CompteMinima_.numDossierOpiCptMin.getName(), 135);
		grid.setColumnWidth(CompteMinima_.loginCptMin.getName(), 90);
		grid.setColumnWidth(CompteMinima_.supannEtuIdCptMin.getName(), 110);
		grid.setColumnsWidth(145,
			CompteMinima_.prenomCptMin.getName(),
			CompteMinima_.candidat.getName() + "." + Candidat_.nomPatCandidat.getName(),
			CompteMinima_.candidat.getName() + "." + Candidat_.prenomCandidat.getName());

		layout.addComponent(grid);
		layout.setExpandRatio(grid, 1.0f);

		/* Boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnValider = new OneClickButton(applicationContext.getMessage("btnOpen", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnValider.setEnabled(false);
		btnValider.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnValider.addClickListener(e -> {
			performAction();
		});
		buttonsLayout.addComponent(btnValider);
		buttonsLayout.setComponentAlignment(btnValider, Alignment.MIDDLE_RIGHT);

		grid.addSelectionListener(e -> {
			// Le bouton d'enregistrement est actif seulement si un CompteMinima est sélectionnée.
			final boolean IsSelected = grid.getSelectedItem() instanceof CompteMinima;
			btnValider.setEnabled(IsSelected);
		});
		grid.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				grid.select(e.getItemId());
				btnValider.click();
			}
		});

		/* Centre la fenêtre */
		center();
	}

	/** Effectue la recherche */
	private void performSearch() {
		if (searchBox.getValue().equals(null) || searchBox.getValue().equals("")
			|| searchBox.getValue().length() < ConstanteUtils.NB_MIN_CAR_CAND) {
			Notification.show(
				applicationContext.getMessage("window.search.morethan",
					new Object[]
					{ ConstanteUtils.NB_MIN_CAR_CAND },
					UI.getCurrent().getLocale()),
				Notification.Type.WARNING_MESSAGE);
		} else {
			grid.removeAndAddAll(candidatController.getCptMinByFilter(searchBox.getValue(),
				cbOtherYears.getValue(),
				cbExactSearch.getValue()));
		}
	}

	/** Vérifie els donnée et si c'est ok, fait l'action (renvoie le PeopleLdap) */
	private void performAction() {
		if (compteMinimaListener != null) {
			final CompteMinima cpt = grid.getSelectedItem();
			if (cpt == null) {
				Notification.show(applicationContext.getMessage("window.search.selectrow", null, UI.getCurrent().getLocale()),
					Notification.Type.WARNING_MESSAGE);
				return;
			} else {
				compteMinimaListener.btnOkClick(cpt);
				close();
			}
		}
	}

	/**
	 * Défini le 'compteMinimaListener' utilisé
	 * @param compteMinimaListener
	 */
	public void addCompteMinimaListener(final CompteMinimaListener compteMinimaListener) {
		this.compteMinimaListener = compteMinimaListener;
	}

	/** Interface pour récupérer un click sur Oui ou Non. */
	public interface CompteMinimaListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param cptMin
		 *                   le cptMin a renvoyer
		 */
		void btnOkClick(CompteMinima cptMin);

	}

}
