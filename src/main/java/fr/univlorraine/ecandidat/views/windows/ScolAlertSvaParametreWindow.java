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

import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.AlertSvaController;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.utils.bean.presentation.ParametreSvaPresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredStringCheckBox;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxPresentation;

/**
 * Fenêtre d'édition de date d'alertSva
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class ScolAlertSvaParametreWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 1789664007659398677L;

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient AlertSvaController alertSvaController;
	
	private ChangeAlertSVAWindowListener changeAlertSVAWindowListener;

	/* Composants */
	private CustomBeanFieldGroup<ParametreSvaPresentation> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;


	/** Crée une fenêtre d'édition de date d'alerte SVA
	 * @param parametreDate
	 * @param parametreDefinitif
	 */
	public ScolAlertSvaParametreWindow(Parametre parametreDate, Parametre parametreDefinitif) {
		/* Style */
		setModal(true);
		setWidth(600,Unit.PIXELS);
		setResizable(true);
		setClosable(true);
		
		ParametreSvaPresentation parametre = new ParametreSvaPresentation(parametreDate.getValParam(),parametreDefinitif.getValParam());

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("alertSva.date.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(ParametreSvaPresentation.class);
		fieldGroup.setItemDataSource(parametre);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		
		ComboBoxPresentation field = fieldGroup.buildAndBind(applicationContext.getMessage("alertSva.date.title", null, UI.getCurrent().getLocale()), 
				ParametreSvaPresentation.CHAMPS_DATE, ComboBoxPresentation.class);
		field.setListe(alertSvaController.getListeDateSVA());
		field.setWidth(100, Unit.PERCENTAGE);
		formLayout.addComponent(field);
		
		RequiredStringCheckBox fieldDefinitif = fieldGroup.buildAndBind(applicationContext.getMessage("alertSva.definitif.title", null, UI.getCurrent().getLocale()), 
				ParametreSvaPresentation.CHAMPS_DEFINITF, RequiredStringCheckBox.class);
		fieldDefinitif.setWidth(100, Unit.PERCENTAGE);
		formLayout.addComponent(fieldDefinitif);

		layout.addComponent(formLayout);

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
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la alertSva saisie */
				changeAlertSVAWindowListener.btnOkClick(parametre);
				/* Ferme la fenêtre */
				close();
			} catch (CommitException ce) {
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}
	
	/**
	 * Défini le 'ChangeAlertSVAWindowListener' utilisé
	 * @param changeAlertSVAWindowListener
	 */
	public void addChangeAlertSVAWindowListener(ChangeAlertSVAWindowListener changeAlertSVAWindowListener) {
		this.changeAlertSVAWindowListener = changeAlertSVAWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui.
	 */
	public interface ChangeAlertSVAWindowListener extends Serializable {

		/** Appelé lorsque Oui est cliqué.
		 * @param parametreSVA
		 */
		public void btnOkClick(ParametreSvaPresentation parametreSVA);

	}
}
