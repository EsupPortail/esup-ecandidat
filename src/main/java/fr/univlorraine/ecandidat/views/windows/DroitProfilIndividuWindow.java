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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.DemoController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.IndividuController;
import fr.univlorraine.ecandidat.controllers.PeopleController;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil_;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.services.people.People;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.CustomException;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de recherche d'individu Ldap
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class DroitProfilIndividuWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient PeopleController peopleController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient DemoController demoController;

	public static final String[] PEOPLE_FIELDS_ORDER = { "uid", "supannCivilite", "sn", "givenName", "displayName" };

	/* Composants */
	protected VerticalLayout infoSuppLayout;
	private final HorizontalLayout searchLayout;
	private final TextField searchBox;
	private final Label loginModification;
	private final OneClickButton btnSearch;
	private final GridFormatting<People> grid = new GridFormatting<>(People.class);
	private final OneClickButton btnValider;
	private final OneClickButton btnAnnuler;
	private final ComboBox cbDroitProfil;

	/* Variable */
	protected Boolean isModificationMode = false;

	/* Listener */
	private DroitProfilIndividuListener droitProfilIndividuListener;

	/** Constructeur de la fenêtre de profil */
	public DroitProfilIndividuWindow(final String type) {
		final List<DroitProfil> listeProfilDispo = droitProfilController.getListDroitProfilByType(type);

		/* Style */
		setWidth(980, Unit.PIXELS);
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
		setCaption(applicationContext.getMessage("window.search.people.title", null, UI.getCurrent().getLocale()));

		/* Commande layout */
		final HorizontalLayout commandeLayout = new HorizontalLayout();
		commandeLayout.setWidth(100, Unit.PERCENTAGE);

		/* Recherche */
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

		loginModification = new Label("", ContentMode.HTML);
		loginModification.setVisible(false);
		layout.addComponent(loginModification);

		btnSearch = new OneClickButton(applicationContext.getMessage("window.search", null, UI.getCurrent().getLocale()));
		btnSearch.addClickListener(e -> performSearch());

		searchLayout = new HorizontalLayout();
		searchLayout.setSpacing(true);
		searchLayout.addComponent(searchBox);
		searchLayout.addComponent(btnSearch);

		/* DroitLayout */
		final HorizontalLayout droitLayout = new HorizontalLayout();
		droitLayout.setSpacing(true);
		final Label labelDroit = new Label(applicationContext.getMessage("window.search.profil", null, UI.getCurrent().getLocale()));
		droitLayout.addComponent(labelDroit);
		droitLayout.setComponentAlignment(labelDroit, Alignment.MIDDLE_RIGHT);

		final BeanItemContainer<DroitProfil> container = new BeanItemContainer<>(DroitProfil.class, listeProfilDispo);
		cbDroitProfil = new ComboBox();
		cbDroitProfil.setTextInputAllowed(false);
		cbDroitProfil.setContainerDataSource(container);
		cbDroitProfil.setNullSelectionAllowed(false);
		cbDroitProfil.setImmediate(true);
		cbDroitProfil.setItemCaptionPropertyId(DroitProfil_.codProfil.getName());
		cbDroitProfil.setValue(listeProfilDispo.get(0));

		droitLayout.addComponent(cbDroitProfil);
		droitLayout.setComponentAlignment(labelDroit, Alignment.MIDDLE_LEFT);

		/* Login Apogee pour les gestionnaires */
		infoSuppLayout = new VerticalLayout();
		infoSuppLayout.setSpacing(true);
		final Label labelInfoSuppLayout = new Label(applicationContext.getMessage("window.search.people.option", null, UI.getCurrent().getLocale()), ContentMode.HTML);
		labelInfoSuppLayout.addStyleName(ValoTheme.LABEL_H4);
		labelInfoSuppLayout.addStyleName(ValoTheme.LABEL_COLORED);
		infoSuppLayout.addComponent(labelInfoSuppLayout);
		infoSuppLayout.setVisible(false);

		/* Ajout des commandes */
		commandeLayout.addComponent(searchLayout);
		commandeLayout.addComponent(droitLayout);
		layout.addComponent(commandeLayout);

		/* La grid de recherche */
		grid.initColumn(PEOPLE_FIELDS_ORDER, "window.search.people.", "uid");
		grid.setColumnWidth("uid", 125);
		grid.setColumnWidth("supannCivilite", 88);

		final HorizontalLayout tableLayout = new HorizontalLayout();
		tableLayout.setSpacing(true);
		tableLayout.setSizeFull();
		infoSuppLayout.setSizeUndefined();
		infoSuppLayout.setHeight(100, Unit.PERCENTAGE);
		tableLayout.addComponent(grid);
		tableLayout.addComponent(infoSuppLayout);
		tableLayout.setExpandRatio(grid, 1.0f);

		layout.addComponent(tableLayout);
		layout.setExpandRatio(tableLayout, 1.0f);

		/* Boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnValider = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnValider.setEnabled(false);
		btnValider.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnValider.addClickListener(e -> {
			performAction();
		});
		buttonsLayout.addComponent(btnValider);
		buttonsLayout.setComponentAlignment(btnValider, Alignment.MIDDLE_RIGHT);

		grid.addSelectionListener(e -> {
			// Le bouton d'enregistrement est actif seulement si un PeopleLdap est sélectionné.
			final boolean isSelected = grid.getSelectedItem() instanceof People;
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
	 * Passe en mode modif
	 * @param droitProfilInd
	 *                          le profil a modifier
	 */
	protected void switchToModifMode(final DroitProfilInd droitProfilInd) {
		isModificationMode = true;
		setCaption(applicationContext.getMessage("window.search.people.title.mod", null, UI.getCurrent().getLocale()));
		setWidth(350, Unit.PIXELS);
		searchLayout.setVisible(false);
		loginModification.setVisible(true);
		grid.setVisible(false);
		btnValider.setEnabled(true);

		cbDroitProfil.setValue(droitProfilInd.getDroitProfil());
		loginModification.setValue("Login : <b>" + droitProfilInd.getIndividu().getLoginInd() + "</b>");

		infoSuppLayout.setWidth(100, Unit.PERCENTAGE);
	}

	/**
	 * ajoute une option au layout d'options
	 * @param c
	 *             le composant
	 */
	protected void addOption(final Component c) {
		addOption(c, Alignment.MIDDLE_LEFT, null);
	}

	/**
	 * ajoute une option alignée au layout d'options
	 * @param c
	 *             le composant
	 */
	protected void addOption(final Component c, final Alignment align, final Float expendRatio) {
		infoSuppLayout.addComponent(c);
		infoSuppLayout.setComponentAlignment(c, align);
		if (!infoSuppLayout.isVisible()) {
			infoSuppLayout.setVisible(true);
		}
		if (expendRatio != null) {
			infoSuppLayout.setExpandRatio(c, expendRatio);
		}
		center();
	}

	protected void setMaxExpendRatio() {
		infoSuppLayout.setHeight(100, Unit.PERCENTAGE);
	}

	protected void setMinExpendRatio() {
		infoSuppLayout.setHeightUndefined();
	}

	public void setOptionLayoutWidth(final int width) {
		infoSuppLayout.setWidth(width, Unit.PIXELS);
	}

	/** Effectue la recherche */
	private void performSearch() {
		if (searchBox.getValue().equals(null) || searchBox.getValue().equals("") || searchBox.getValue().length() < ConstanteUtils.NB_MIN_CAR_PERS) {
			Notification.show(applicationContext.getMessage("window.search.morethan", new Object[] { ConstanteUtils.NB_MIN_CAR_PERS }, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
		} else {
			grid.removeAll();
			if (demoController.getDemoMode()) {
				grid.addItems(demoController.findListIndividuLdapDemo());
			} else {
				grid.addItems(peopleController.getPeopleByFilter(searchBox.getValue()));
			}
		}
	}

	/** Vérifie les données et si c'est ok, fait l'action du listener */
	protected void performAction() {
		if (droitProfilIndividuListener != null && checkData()) {
			final Individu individu = getIndividu();
			final DroitProfil droit = getDroitProfil();
			if (individu != null && droit != null) {
				droitProfilIndividuListener.btnOkClick(individu, droit);
				close();
			}
		}
	}

	/** @return true si les données sont bonnes */
	protected Boolean checkData() {
		final People valPeople = grid.getSelectedItem();
		final DroitProfil valDroit = (DroitProfil) cbDroitProfil.getValue();
		if (!isModificationMode && valPeople == null) {
			Notification.show(applicationContext.getMessage("window.search.selectrow", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
			return false;
		} else if (valDroit == null) {
			Notification.show(applicationContext.getMessage("window.search.noright", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Renvoi l'individu construit a partir du people Ldap
	 * @return l'individu
	 */
	protected Individu getIndividu() {
		if (isModificationMode) {
			return null;
		} else {
			final People people = grid.getSelectedItem();
			final Individu individu = new Individu(people);
			try {
				individuController.validateIndividuBean(individu, UI.getCurrent().getLocale());
				return individu;
			} catch (final CustomException e) {
				Notification.show(e.getMessage(), Notification.Type.WARNING_MESSAGE);
				return null;
			}
		}
	}

	protected DroitProfil getDroitProfil() {
		final DroitProfil droit = (DroitProfil) cbDroitProfil.getValue();
		return droit;
	}

	/**
	 * Défini le 'DroitProfilIndividuListener' utilisé
	 * @param droitProfilIndividuListener
	 */
	public void addDroitProfilIndividuListener(final DroitProfilIndividuListener droitProfilIndividuListener) {
		this.droitProfilIndividuListener = droitProfilIndividuListener;
	}

	/** Interface pour récupérer un click sur Oui ou Non. */
	public interface DroitProfilIndividuListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param individu
		 * @param droit
		 */
		void btnOkClick(Individu individu, DroitProfil droit);

	}

}
