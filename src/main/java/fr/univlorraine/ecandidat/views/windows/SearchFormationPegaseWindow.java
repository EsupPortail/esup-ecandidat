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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.FormationController;
import fr.univlorraine.ecandidat.controllers.IndividuController;
import fr.univlorraine.ecandidat.entities.siscol.pegase.FormationPegase;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de recherche de formation apogee
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class SearchFormationPegaseWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormationController formationController;
	@Resource
	private transient IndividuController individuController;

	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	public static final String[] FIELDS_ORDER = { FormationPegase.FIELD_NAME_CODE, FormationPegase.FIELD_NAME_LIB, FormationPegase.FIELD_NAME_ESPACEL };

	/* Composants */
	private final GridFormatting<FormationPegase> grid = new GridFormatting<>(FormationPegase.class);
	private final TextField searchBox;
	private final OneClickButton btnSearch;
	private final OneClickButton btnValider;
	private final OneClickButton btnAnnuler;

	/* Listener */
	private FormationListener formationListener;

	/**
	 * Crée une fenêtre de recherche de formaiton pegase
	 */
	public SearchFormationPegaseWindow() {
		/* Style */
		setWidth(900, Unit.PIXELS);
		setHeight(500, Unit.PIXELS);
		setModal(true);
		setResizable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);

		/* Titre */
		setCaption(applicationContext.getMessage("window.search.pegase.title", null, UI.getCurrent().getLocale()));

		/* Recherche */
		final HorizontalLayout searchLayout = new HorizontalLayout();
		searchBox = new TextField();
		searchBox.addShortcutListener(new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(final Object sender, final Object target) {
				performSearch();
			}
		});
		searchBox.focus();

		btnSearch = new OneClickButton(applicationContext.getMessage("window.search", null, UI.getCurrent().getLocale()));
		btnSearch.addClickListener(e -> performSearch());
		final Label labelLimit = new Label(applicationContext.getMessage("formation.window.pegase.limit", new Object[] { ConstanteUtils.NB_MAX_RECH_FORM_PEGASE }, UI.getCurrent().getLocale()));

		searchLayout.setSpacing(true);
		searchLayout.addComponent(searchBox);
		searchLayout.setComponentAlignment(searchBox, Alignment.BOTTOM_LEFT);
		searchLayout.addComponent(btnSearch);
		searchLayout.setComponentAlignment(btnSearch, Alignment.BOTTOM_LEFT);
		searchLayout.addComponent(labelLimit);
		searchLayout.setComponentAlignment(labelLimit, Alignment.MIDDLE_LEFT);

		layout.addComponent(searchLayout);

		/* Table de Resultat de recherche */
		grid.initColumn(FIELDS_ORDER, "form.pegase.", FormationPegase.FIELD_NAME_CODE);
		grid.setColumnWidth(FormationPegase.FIELD_NAME_CODE, 120);
		grid.setColumnWidth(FormationPegase.FIELD_NAME_ESPACEL, 240);
		grid.setExpendColumn(FormationPegase.FIELD_NAME_LIB);

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

		btnValider = new OneClickButton(applicationContext.getMessage("btnValid", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnValider.setEnabled(false);
		btnValider.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnValider.addClickListener(e -> {
			performAction();
		});
		buttonsLayout.addComponent(btnValider);
		buttonsLayout.setComponentAlignment(btnValider, Alignment.MIDDLE_RIGHT);

		grid.addSelectionListener(e -> {
			// Le bouton d'enregistrement est actif seulement si une vet est sélectionnée.
			final boolean isSelected = grid.getSelectedItem() instanceof FormationPegase;
			btnValider.setEnabled(isSelected);
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

	/** Vérifie els donnée et si c'est ok, fait l'action (renvoie le AnneeUni) */
	private void performAction() {
		if (formationListener != null) {
			final FormationPegase form = grid.getSelectedItem();
			if (form == null) {
				Notification.show(applicationContext.getMessage("window.search.selectrow", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
				return;
			} else {
				formationListener.btnOkClick(form);
				close();
			}
		}
	}

	/**
	 * Effectue la recherche
	 * @param codCgeUserApo
	 * @param codCgeUser
	 */
	private void performSearch() {
		final String search = searchBox.getValue();

		if (StringUtils.isNotBlank(search) && search.length() < ConstanteUtils.NB_MIN_CAR_FORM) {
			Notification.show(applicationContext.getMessage("window.search.morethan", new Object[] { ConstanteUtils.NB_MIN_CAR_FORM }, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
			return;
		} else {
			grid.removeAll();
			try {
				grid.addItems(siScolService.getListFormationPegase(search, ConstanteUtils.NB_MAX_RECH_FORM_PEGASE));
			} catch (final SiScolException e) {
				Notification.show(applicationContext.getMessage("siscol.connect.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				close();
			}
		}
	}

	/**
	 * Défini le 'FormationListener' utilisé
	 * @param formationListener
	 */
	public void addFormationListener(final FormationListener formationListener) {
		this.formationListener = formationListener;
	}

	/** Interface pour récupérer un click sur Oui ou Non. */
	public interface FormationListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param vet
		 *               la vet a renvoyer
		 */
		void btnOkClick(FormationPegase form);

	}

}
