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

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil_;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire_;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu_;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.CustomPanel;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import jakarta.annotation.Resource;

/**
 * Fenêtre d'ajout de profil gestionnaire de candidat en masse
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class ScolGestCandidatMasseWindow extends Window {

	public static final String[] FIELDS_ORDER = {
		Gestionnaire_.droitProfilInd.getName() + "." + DroitProfilInd_.individu.getName() + "." + Individu_.loginInd.getName(),
		Gestionnaire_.droitProfilInd.getName() + "." + DroitProfilInd_.individu.getName() + "." + Individu_.libelleInd.getName() };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient CentreCandidatureController centreCandidatureController;

	private DroitProfilMasseListener droitProfilMasseListener;

	/* Composants */
	private final ComboBox cbDroitProfil;
	private final RequiredComboBox<CentreCandidature> cbCtrCand;
	private final OneClickButton btnSearch;
	private final OneClickButton btnEnregistrer;
	private final OneClickButton btnAnnuler;
	private final OneClickButton btnDelete;
	private final TableFormating individuTable = new TableFormating();
	private final BeanItemContainer<Gestionnaire> containerTable = new BeanItemContainer<>(Gestionnaire.class);

	/**
	 * Crée une fenêtre d'ajout de profil gestionnaire de candidat en masse
	 */
	public ScolGestCandidatMasseWindow() {

		final List<DroitProfil> listeProfilDispo = droitProfilController.getListDroitProfilByType(NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT);
		/* Style */
		setModal(true);
		setWidth(850, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Panel d'info */
		final CustomPanel panelInfo =
			new CustomPanel(applicationContext.getMessage("informations", null, UI.getCurrent().getLocale()), applicationContext.getMessage("droitprofilind.gestcand.window.info", null, UI.getCurrent().getLocale()),
				FontAwesome.INFO_CIRCLE);
		panelInfo.setWidthMax();
		panelInfo.addLabelStyleName(ValoTheme.LABEL_TINY);
		layout.addComponent(panelInfo);

		/* Container */
		final BeanItemContainer<DroitProfil> container = new BeanItemContainer<>(DroitProfil.class, listeProfilDispo);

		/* Titre */
		setCaption(applicationContext.getMessage("droitprofilind.gestcand.window.title", null, UI.getCurrent().getLocale()));

		/* Commande layout */
		final HorizontalLayout commandeLayout = new HorizontalLayout();
		commandeLayout.setWidth(100, Unit.PERCENTAGE);
		commandeLayout.setSpacing(true);
		layout.addComponent(commandeLayout);

		final HorizontalLayout rechercheLayout = new HorizontalLayout();
		rechercheLayout.setSpacing(true);
		commandeLayout.addComponent(rechercheLayout);
		commandeLayout.setExpandRatio(rechercheLayout, 1);
		final Label label = new Label(applicationContext.getMessage("droitprofilind.gestcand.window.btn.ctr", null, UI.getCurrent().getLocale()));
		rechercheLayout.addComponent(label);
		rechercheLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);

		final BeanItemContainer<CentreCandidature> containerCtr = new BeanItemContainer<>(CentreCandidature.class);
		containerCtr.addAll(centreCandidatureController.getCentreCandidatures());
		cbCtrCand = new RequiredComboBox<>(true);
		cbCtrCand.setContainerDataSource(containerCtr);
		rechercheLayout.addComponent(cbCtrCand);
		btnSearch = new OneClickButton(applicationContext.getMessage("window.search", null, UI.getCurrent().getLocale()));
		btnSearch.addClickListener(e -> performSearch());
		rechercheLayout.addComponent(btnSearch);

		/* Suppression */
		btnDelete = new OneClickButton(applicationContext.getMessage("droitprofilind.gestcand.window.delete", null, UI.getCurrent().getLocale()));
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (individuTable.getValue() instanceof Gestionnaire) {
				final Gestionnaire gest = (Gestionnaire) individuTable.getValue();
				individuTable.removeItem(gest);
			}

		});
		commandeLayout.addComponent(btnDelete);
		commandeLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);

		/* Table des individus */
		containerTable.addNestedContainerProperty(Gestionnaire_.droitProfilInd.getName() + "." + DroitProfilInd_.individu.getName() + "." + Individu_.loginInd.getName());
		containerTable.addNestedContainerProperty(Gestionnaire_.droitProfilInd.getName() + "." + DroitProfilInd_.individu.getName() + "." + Individu_.libelleInd.getName());
		individuTable.setContainerDataSource(containerTable);
		individuTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		//individuTable.setWidth(new Float("99.99"), Unit.PERCENTAGE);
		individuTable.setWidth(100, Unit.PERCENTAGE);
		individuTable.setHeight(300, Unit.PIXELS);
		for (final String fieldName : FIELDS_ORDER) {
			individuTable.setColumnHeader(fieldName, applicationContext.getMessage("droit." + fieldName, null, UI.getCurrent().getLocale()));
		}
		individuTable.setSortContainerPropertyId(Individu_.loginInd.getName());
		individuTable.setColumnCollapsingAllowed(true);
		individuTable.setColumnReorderingAllowed(true);
		individuTable.setSelectable(true);
		individuTable.setImmediate(true);
		individuTable.addItemSetChangeListener(e -> individuTable.sanitizeSelection());
		individuTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de individu sont actifs seulement si une individu est sélectionnée. */
			final boolean individuIsSelected = individuTable.getValue() instanceof Gestionnaire;
			btnDelete.setEnabled(individuIsSelected);
		});
		layout.addComponent(individuTable);

		/* DroitLayout */
		final HorizontalLayout droitLayout = new HorizontalLayout();
		droitLayout.setSpacing(true);
		final Label labelDroit = new Label(applicationContext.getMessage("window.search.profil", null, UI.getCurrent().getLocale()));
		droitLayout.addComponent(labelDroit);
		droitLayout.setComponentAlignment(labelDroit, Alignment.MIDDLE_RIGHT);
		layout.addComponent(droitLayout);

		cbDroitProfil = new ComboBox();
		cbDroitProfil.setTextInputAllowed(false);
		cbDroitProfil.setContainerDataSource(container);
		cbDroitProfil.setNullSelectionAllowed(false);
		cbDroitProfil.setImmediate(true);
		cbDroitProfil.setItemCaptionPropertyId(DroitProfil_.codProfil.getName());
		cbDroitProfil.setValue(listeProfilDispo.get(0));
		droitLayout.addComponent(cbDroitProfil);

		/* Ajoute les boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("droitprofilind.gestcand.window.btn.ok", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			if (cbDroitProfil.getValue() instanceof DroitProfil && containerTable.getItemIds().size() > 0) {
				droitProfilMasseListener.btnOkClick(containerTable.getItemIds(), (DroitProfil) cbDroitProfil.getValue());
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	private void performSearch() {
		containerTable.removeAllItems();
		final CentreCandidature ctr = (CentreCandidature) cbCtrCand.getValue();
		if (ctr != null) {
			containerTable.addAll(ctr.getGestionnaires());
		}
	}

	/**
	 * Défini le 'DroitProfilMasseListener' utilisé
	 * @param droitProfilMasseListener
	 */
	public void addDroitProfilMasseListener(final DroitProfilMasseListener droitProfilMasseListener) {
		this.droitProfilMasseListener = droitProfilMasseListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui
	 */
	public interface DroitProfilMasseListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param listeGestionnaire
		 * @param droit
		 */
		void btnOkClick(List<Gestionnaire> listeGestionnaire, DroitProfil droit);

	}
}
