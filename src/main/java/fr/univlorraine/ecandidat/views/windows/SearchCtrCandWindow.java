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

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature_;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de recherche de centre de candidature
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class SearchCtrCandWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CentreCandidatureController centreCandidatureController;

	public static final String[] FIELDS_ORDER = { CentreCandidature_.codCtrCand.getName(), CentreCandidature_.libCtrCand.getName() };

	/* Composants */
	private final GridFormatting<CentreCandidature> grid = new GridFormatting<>(CentreCandidature.class);
	private final OneClickButton btnValider;
	private final OneClickButton btnAnnuler;

	/* Listener */
	private CentreCandidatureListener centreCandidatureListener;

	/**
	 * Crée une fenêtre de recherche de centre de candidature
	 */
	public SearchCtrCandWindow() {
		/* Style */
		setWidth(740, Unit.PIXELS);
		setHeight(480, Unit.PIXELS);
		setModal(true);
		setResizable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);

		/* Titre */
		setCaption(applicationContext.getMessage("ctrCand.window.search.title", null, UI.getCurrent().getLocale()));

		/* Table de Resultat de recherche */
		grid.addItems(centreCandidatureController.getListCentreCandidature());
		grid.initColumn(FIELDS_ORDER, "ctrCand.table.", CentreCandidature_.codCtrCand.getName());
		grid.setColumnWidth(CentreCandidature_.codCtrCand.getName(), 180);
		grid.setExpendColumn(CentreCandidature_.libCtrCand.getName());

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
			// Le bouton d'enregistrement est actif seulement si un ctrCand est sélectionnée.
			final boolean isSelected = grid.getSelectedItem() instanceof CentreCandidature;
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

	/**
	 * Vérifie els donnée et si c'est ok, fait l'action (renvoie le CentreCandidature)
	 */
	private void performAction() {
		if (centreCandidatureListener != null) {
			final CentreCandidature ctrCand = grid.getSelectedItem();
			if (ctrCand == null) {
				Notification.show(applicationContext.getMessage("window.search.selectrow", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
				return;
			} else {
				centreCandidatureListener.btnOkClick(ctrCand);
				close();
			}
		}
	}

	/**
	 * Défini le 'CentreCandidatureListener' utilisé
	 * @param centreCandidatureListener
	 */
	public void addCentreCandidatureListener(final CentreCandidatureListener centreCandidatureListener) {
		this.centreCandidatureListener = centreCandidatureListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface CentreCandidatureListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param centre le CentreCandidature a renvoyer
		 */
		void btnOkClick(CentreCandidature centre);

	}

}
