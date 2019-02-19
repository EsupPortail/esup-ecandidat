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
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.entities.siscol.Diplome;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de recherche de formation apogee
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class SearchDiplomeApoWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;

	private static final String CHAMPS_COD_DIP = "id.codDip";
	private static final String CHAMPS_COD_VRS_VDI = "id.codVrsVdi";
	private static final String CHAMPS_LIB_DIP = "libDip";

	public static final String[] FIELDS_ORDER = {CHAMPS_COD_DIP, CHAMPS_COD_VRS_VDI, CHAMPS_LIB_DIP};

	/* Composants */
	private GridFormatting<Diplome> grid = new GridFormatting<>(Diplome.class);
	private OneClickButton btnValider;
	private OneClickButton btnAnnuler;

	/* Listener */
	private DiplomeListener diplomeListener;

	/**
	 * Crée une fenêtre de recherche de formaiton apogée
	 *
	 * @param liste
	 */
	public SearchDiplomeApoWindow(final List<Diplome> liste) {
		/* Style */
		setWidth(900, Unit.PIXELS);
		setHeight(500, Unit.PIXELS);
		setModal(true);
		setResizable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);

		/* Titre */
		setCaption(applicationContext.getMessage("window.search.diplome.title", null, Locale.getDefault()));

		/* Table de Resultat de recherche */
		grid.initColumn(FIELDS_ORDER, "diplome.", CHAMPS_COD_DIP);
		grid.addItems(liste);
		grid.addSelectionListener(e -> {
			// Le bouton d'enregistrement est actif seulement si un Diplome est sélectionnée.
			boolean isSelected = grid.getSelectedItem() instanceof Diplome;
			btnValider.setEnabled(isSelected);
		});
		grid.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				grid.select(e.getItemId());
				btnValider.click();
			}
		});
		grid.setColumnWidth(CHAMPS_COD_DIP, 150);
		grid.setColumnWidth(CHAMPS_COD_VRS_VDI, 150);
		// grid.setColumnWidth(CHAMPS_LIB_DIP, 240);

		layout.addComponent(grid);
		layout.setExpandRatio(grid, 1.0f);

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
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

		/* Centre la fenêtre */
		center();
	}

	/** Vérifie els donnée et si c'est ok, fait l'action (renvoie le AnneeUni) */
	private void performAction() {
		if (diplomeListener != null) {
			Diplome diplome = grid.getSelectedItem();
			if (diplome == null) {
				Notification.show(applicationContext.getMessage("window.search.selectrow", null, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
				return;
			} else {
				diplomeListener.btnOkClick(diplome);
				close();
			}
		}
	}

	/**
	 * Défini le 'DiplomeListener' utilisé
	 *
	 * @param diplomeListener
	 */
	public void addDiplomeListener(final DiplomeListener diplomeListener) {
		this.diplomeListener = diplomeListener;
	}

	/** Interface pour récupérer un click sur Oui ou Non. */
	public interface DiplomeListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 *
		 * @param diplome
		 *            le diplome a renvoyer
		 */
		void btnOkClick(Diplome diplome);

	}

}
