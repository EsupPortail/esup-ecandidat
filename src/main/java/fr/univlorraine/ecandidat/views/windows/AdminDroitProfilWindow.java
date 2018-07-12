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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil_;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxPresentation;

/** Fenêtre d'édition de droit-profil
 * 
 * @author Kevin Hergalant */
@Configurable(preConstruction = true)
public class AdminDroitProfilWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -2056861405725050563L;

	public static final String[] FIELDS_ORDER = {DroitProfil_.codProfil.getName(), DroitProfil_.libProfil.getName(), DroitProfil_.typProfil.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient DroitProfilController droitProfilController;

	/* Composants */
	private CustomBeanFieldGroup<DroitProfil> fieldGroup;
	private ListSelect leftSelect;
	private ListSelect rightSelect;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/* Fonctionnalité */
	private CheckBox cbReadOnly;
	private BeanItemContainer<DroitFonctionnalite> containerLeft;
	private BeanItemContainer<DroitFonctionnalite> containerRight;
	private HashMap<DroitFonctionnalite, Boolean> fonctionnaliteMap = new HashMap<>();

	/** Crée une fenêtre d'édition de DroitProfil
	 * 
	 * @param droitProfil
	 *            le profil à éditer */
	@SuppressWarnings("unchecked")
	public AdminDroitProfilWindow(final DroitProfil droitProfil) {
		/* Style */
		setModal(true);
		setWidth(800, Unit.PIXELS);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("droitprofil.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(DroitProfil.class);
		fieldGroup.setItemDataSource(droitProfil);
		FormLayout formLayout = new FormLayout();
		formLayout.setSpacing(true);
		formLayout.setWidth(100, Unit.PERCENTAGE);
		for (String fieldName : FIELDS_ORDER) {
			Field<?> field = null;
			if (fieldName.equals(DroitProfil_.typProfil.getName())) {
				field = fieldGroup.buildAndBind(applicationContext.getMessage("droitprofil.table." + fieldName, null, UI.getCurrent().getLocale()), fieldName, ComboBoxPresentation.class);
				((ComboBoxPresentation) field).setListe(droitProfilController.getListTypDroitProfil());
				field.addValueChangeListener(e -> majFonctionnalite(droitProfil));
				if (droitProfil.getTypProfil() != null) {
					field.setEnabled(false);
				}
			} else {
				field = fieldGroup.buildAndBind(applicationContext.getMessage("droitprofil.table." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
			}
			formLayout.addComponent(field);
			field.setWidth(100, Unit.PERCENTAGE);
		}

		layout.addComponent(formLayout);

		/* Liste des fonctionnalités */
		Label titleFonc = new Label(applicationContext.getMessage("droitprofil.window.fonctionnalite", null, UI.getCurrent().getLocale()));
		titleFonc.addStyleName(ValoTheme.LABEL_COLORED);

		layout.addComponent(titleFonc);

		/* Containers */
		containerLeft = new BeanItemContainer<>(DroitFonctionnalite.class);
		containerRight = new BeanItemContainer<>(DroitFonctionnalite.class);

		/* Listtes de gauche et droite */
		leftSelect = new ListSelect(applicationContext.getMessage("droitprofil.window.fonc.dispo", null, UI.getCurrent().getLocale()));
		rightSelect = new ListSelect(applicationContext.getMessage("droitprofil.window.fonc.select", null, UI.getCurrent().getLocale()));
		initListSelect(leftSelect, containerLeft);
		initListSelect(rightSelect, containerRight);

		/* Layout bouton milieu */
		VerticalLayout layoutBtn = new VerticalLayout();
		layoutBtn.setHeight(100, Unit.PERCENTAGE);
		layoutBtn.setSpacing(true);
		OneClickButton btnGoRight = new OneClickButton(FontAwesome.ARROW_CIRCLE_RIGHT);
		OneClickButton btnGoLeft = new OneClickButton(FontAwesome.ARROW_CIRCLE_LEFT);
		layoutBtn.addComponent(btnGoRight);
		layoutBtn.setComponentAlignment(btnGoRight, Alignment.BOTTOM_CENTER);
		layoutBtn.addComponent(btnGoLeft);
		layoutBtn.setComponentAlignment(btnGoLeft, Alignment.TOP_CENTER);

		/* Action sur la liste de droite --> mise à jour de la case a cocher readonly */
		rightSelect.addValueChangeListener(e -> {
			Set<DroitFonctionnalite> collectionRight = (Set<DroitFonctionnalite>) rightSelect.getValue();
			if (collectionRight == null || collectionRight.size() == 0) {
				cbReadOnly.setValue(false);
				cbReadOnly.setEnabled(false);
			} else {
				cbReadOnly.setEnabled(true);
			}
			if (isAllReadOnlyState(collectionRight, true)) {
				cbReadOnly.setValue(true);
			} else if (isAllReadOnlyState(collectionRight, false)) {
				cbReadOnly.setValue(false);
			}
		});

		/* action du bouton mise à droite */
		btnGoRight.addClickListener(e -> {
			Set<DroitFonctionnalite> collectionLeft = (Set<DroitFonctionnalite>) leftSelect.getValue();
			collectionLeft.forEach(fonc -> {
				containerLeft.removeItem(fonc);
				containerRight.addBean(fonc);
				leftSelect.setValue(null);
				rightSelect.setValue(null);
				fonctionnaliteMap.put(fonc, true);
			});
		});

		/* action du bouton mise à gauche */
		btnGoLeft.addClickListener(e -> {
			Set<DroitFonctionnalite> collectionRight = (Set<DroitFonctionnalite>) rightSelect.getValue();
			collectionRight.forEach(fonc -> {
				containerRight.removeItem(fonc);
				containerLeft.addBean(fonc);
				leftSelect.setValue(null);
				rightSelect.setValue(null);
				fonctionnaliteMap.remove(fonc);
			});
		});

		/* Layout contenant le tout */
		HorizontalLayout hlTwinSelect = new HorizontalLayout();
		hlTwinSelect.setSpacing(true);
		hlTwinSelect.setWidth(100, Unit.PERCENTAGE);
		hlTwinSelect.addComponent(leftSelect);
		hlTwinSelect.setExpandRatio(leftSelect, 1);
		hlTwinSelect.addComponent(layoutBtn);
		hlTwinSelect.setExpandRatio(layoutBtn, 0.2f);
		hlTwinSelect.addComponent(rightSelect);
		hlTwinSelect.setExpandRatio(rightSelect, 1);
		layout.addComponent(hlTwinSelect);

		/* Case à cocher readonly + action dessus */
		cbReadOnly = new CheckBox(applicationContext.getMessage("droitprofil.window.fonc.readonly", null, UI.getCurrent().getLocale()));
		cbReadOnly.setEnabled(false);
		cbReadOnly.addValueChangeListener(e -> {
			Boolean val = (Boolean) e.getProperty().getValue();
			Set<DroitFonctionnalite> collectionRight = (Set<DroitFonctionnalite>) rightSelect.getValue();
			collectionRight.forEach(fonc -> {
				fonctionnaliteMap.put(fonc, val);
			});

		});
		layout.addComponent(cbReadOnly);
		layout.setComponentAlignment(cbReadOnly, Alignment.MIDDLE_RIGHT);

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {
				/* Si le code de profil existe dejà --> erreur */
				if (droitProfil.getIdProfil() == null && droitProfilController.existCodeProfil((String) fieldGroup.getField(DroitProfil_.codProfil.getName()).getValue())) {
					Notification.show(applicationContext.getMessage("window.error.cod.nonuniq", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				/* Si la liste de fonctionnalité est vide--erreur */
				if (fonctionnaliteMap.size() == 0) {
					Notification.show(applicationContext.getMessage("droitprofil.window.error.role", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la langue saisie */
				droitProfilController.saveDroitProfil(droitProfil, fonctionnaliteMap);
				/* Ferme la fenêtre */
				close();
			} catch (CommitException ce) {
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		majFonctionnalite(droitProfil);

		/* Centre la fenêtre */
		center();
	}

	/** Recherche si tout les role sont dans le meme etat readonly --> soit tous non, soit tous oui
	 * 
	 * @param collectionRight
	 * @param readOnly
	 * @return true tout les role sont dans le même état */
	private Boolean isAllReadOnlyState(final Set<DroitFonctionnalite> collectionRight, final Boolean readOnly) {
		for (DroitFonctionnalite df : collectionRight) {
			if (fonctionnaliteMap.get(df) != readOnly) {
				return false;
			}
		}
		return true;
	}

	/** Initialise les listes
	 * 
	 * @param listSelect
	 * @param container
	 */
	private void initListSelect(final ListSelect listSelect, final BeanItemContainer<DroitFonctionnalite> container) {
		listSelect.setWidth(100, Unit.PERCENTAGE);
		listSelect.setMultiSelect(true);
		listSelect.setNullSelectionAllowed(false);
		listSelect.setContainerDataSource(container);
		listSelect.setImmediate(true);
		listSelect.setRows(17);
		listSelect.setItemCaptionPropertyId(DroitFonctionnalite_.libFonc.getName());
	}

	private void majFonctionnalite(final DroitProfil droitProfil) {
		containerLeft.removeAllItems();
		containerRight.removeAllItems();
		fonctionnaliteMap.clear();
		String typProfil = ((ComboBoxPresentation) fieldGroup.getField(DroitProfil_.typProfil.getName())).getValue();
		if (typProfil == null) {
			return;
		}
		/* Listes des fonctionnalités */
		List<DroitFonctionnalite> listeRight = new ArrayList<>();
		List<DroitFonctionnalite> listeLeft = new ArrayList<>();
		/* Construction des listes */
		if (droitProfil.getDroitProfilFoncs() != null && droitProfil.getDroitProfilFoncs().size() != 0) {
			droitProfilController.getDroitFonctionnalitesByTypProfil(typProfil).forEach(e -> {
				Optional<DroitProfilFonc> dp = new ArrayList<>(droitProfil.getDroitProfilFoncs()).stream().filter(f -> f.getDroitFonctionnalite().getCodFonc().equals(e.getCodFonc())).findFirst();
				if (dp.isPresent()) {
					listeRight.add(e);
					fonctionnaliteMap.put(e, dp.get().getTemReadOnly());
				} else {
					listeLeft.add(e);
				}
			});
		} else {
			listeLeft.addAll(droitProfilController.getDroitFonctionnalitesByTypProfil(typProfil));
		}
		containerLeft.addAll(listeLeft);
		containerRight.addAll(listeRight);
	}
}
