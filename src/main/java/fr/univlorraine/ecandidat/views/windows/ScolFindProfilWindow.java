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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.CommissionMembre_;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil_;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire_;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre d'édition de droit-profil
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class ScolFindProfilWindow extends Window {

	public static final String[] FIELDS_ORDER = { DroitProfilInd_.individu.getName() + "." + Individu_.loginInd.getName(),
		DroitProfilInd_.individu.getName() + "." + Individu_.libelleInd.getName(),
		DroitProfilInd_.droitProfil.getName() + "." + DroitProfil_.libProfil.getName(),
		DroitProfilInd_.gestionnaire.getName() + "." + Gestionnaire_.centreCandidature.getName() + "." + CentreCandidature_.libCtrCand.getName(),
		DroitProfilInd_.commissionMembre.getName() + "." + CommissionMembre_.commission.getName() + "." + Commission_.libComm.getName()
	};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient DroitProfilController droitProfilController;

	private final TextField searchBox;
	private final GridFormatting<DroitProfilInd> grid = new GridFormatting<>(DroitProfilInd.class);

	/**
	 * Crée une fenêtre d'édition de DroitProfil
	 * @param droitProfil
	 *                        le profil à éditer
	 */
	public ScolFindProfilWindow() {
		/* Titre */
		setCaption(applicationContext.getMessage("droitprofil.search.window", null, UI.getCurrent().getLocale()));

		/* Style */
		setModal(true);
		setWidth(1000, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		searchBox = new TextField();
		searchBox.addShortcutListener(new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {

			@Override
			public void handleAction(final Object sender, final Object target) {
				performSearch();
			}
		});
		searchBox.focus();

		final OneClickButton btnSearch = new OneClickButton(applicationContext.getMessage("window.search", null, UI.getCurrent().getLocale()));
		btnSearch.addClickListener(e -> performSearch());
		final HorizontalLayout searchLayout = new HorizontalLayout();
		searchLayout.setSpacing(true);
		searchLayout.addComponent(searchBox);
		searchLayout.addComponent(btnSearch);
		layout.addComponent(searchLayout);

		/* La grid de recherche */
		grid.initColumn(FIELDS_ORDER, "droitprofil.search.", DroitProfilInd_.individu.getName() + "." + Individu_.loginInd.getName());
		grid.setColumnWidth(DroitProfilInd_.individu.getName() + "." + Individu_.loginInd.getName(), 125);
		layout.addComponent(grid);

		/* Ajoute le bouton fermer */
		final OneClickButton btnClose = new OneClickButton(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnClose.addClickListener(e -> close());
		layout.addComponent(btnClose);
		layout.setComponentAlignment(btnClose, Alignment.MIDDLE_CENTER);

		/* Centre la fenêtre */
		center();
	}

	private void performSearch() {
		if (searchBox.getValue().equals(null) || searchBox.getValue().equals("") || searchBox.getValue().length() < ConstanteUtils.NB_MIN_CAR_PERS) {
			Notification.show(applicationContext.getMessage("window.search.morethan", new Object[] { ConstanteUtils.NB_MIN_CAR_PERS }, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
		} else {
			grid.removeAll();
			grid.addItems(droitProfilController.searchDroitByFilter(searchBox.getValue()));
		}
	}

}
