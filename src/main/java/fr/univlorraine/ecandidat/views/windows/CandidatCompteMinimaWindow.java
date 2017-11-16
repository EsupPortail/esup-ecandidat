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
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;

/**
 * Fenêtre d'édition de compte a minima
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CandidatCompteMinimaWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 1789664007659398677L;
	
	private static final String codeConfirmMailPerso = "confirmMailPersoCptMin";

	public static final String[] FIELDS_ORDER_CPT_MIN = {CompteMinima_.nomCptMin.getName(),CompteMinima_.prenomCptMin.getName(),CompteMinima_.mailPersoCptMin.getName(),codeConfirmMailPerso};
	public static final String[] FIELDS_ORDER_MAIL = {CompteMinima_.mailPersoCptMin.getName(),codeConfirmMailPerso};
	public String[] FIELDS_ORDER;

	
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;

	/* Composants */
	private CompteMinimaWindowListener compteMinimaWindowListener;
	private CustomBeanFieldGroup<CompteMinima> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de compteMinima
	 * @param compteMinima la compteMinima à éditer
	 */
	public CandidatCompteMinimaWindow(CompteMinima compteMinima, Boolean changementMail, Boolean createByGestionnaire) {
		if (changementMail){
			FIELDS_ORDER = FIELDS_ORDER_MAIL;
		}else{
			FIELDS_ORDER = FIELDS_ORDER_CPT_MIN;
		}
		/* Style */
		setModal(true);
		setWidth(550,Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		if (changementMail){
			setCaption(applicationContext.getMessage("compteMinima.editmail.title", null, UI.getCurrent().getLocale()));
			layout.addComponent(new Label(applicationContext.getMessage("compteMinima.editmail.warning", null, UI.getCurrent().getLocale())));
		}else{
			setCaption(applicationContext.getMessage("compteMinima.window", null, UI.getCurrent().getLocale()));
			if (!createByGestionnaire){
				layout.addComponent(new Label(applicationContext.getMessage("compteMinima.create.warning", null, UI.getCurrent().getLocale())));	
			}					
		}

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(CompteMinima.class);
		fieldGroup.setItemDataSource(compteMinima);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			String caption = applicationContext.getMessage("compteMinima.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			if (fieldName.equals(CompteMinima_.mailPersoCptMin.getName()) || fieldName.equals(codeConfirmMailPerso)){
				field.addValidator(new EmailValidator(applicationContext.getMessage("validation.error.mail", null, Locale.getDefault())));
				if (fieldName.equals(codeConfirmMailPerso)){
					field.setRequired(true);
					field.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
				}
			}
			formLayout.addComponent(field);
		}
		
		RequiredTextField eMailField = ((RequiredTextField)fieldGroup.getField(CompteMinima_.mailPersoCptMin.getName()));
		RequiredTextField eMailConfirmField = ((RequiredTextField)fieldGroup.getField(codeConfirmMailPerso));	
		
		
		/*Link morevaadin = new Link("More Vaadin", new ExternalResource("http://morevaadin.com/")); 
		new TooltipExtension().extend(morevaadin);
		layout.addComponent(morevaadin);*/
		
		//new TooltipExtension().extend(eMailConfirmField);
		//eMailField.setValue("kevin.hergalant@univ-lorraine.fr");

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
				/*Verif la confirmation de mail est égale au mail*/
				if (eMailField.getValue()!=null && !eMailField.getValue().equals("") && eMailConfirmField.getValue()!=null && !eMailConfirmField.getValue().equals("") &&
						eMailField.isValid() && eMailConfirmField.isValid() &&
						!eMailConfirmField.getValue().equals(eMailField.getValue())){
					Notification.show(applicationContext.getMessage("compteMinima.mail.confirm.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}
				
				/*Verif meme mail*/
				if (changementMail && eMailField.getValue()!=null && eMailField.getValue().equals(compteMinima.getMailPersoCptMin())){
					Notification.show(applicationContext.getMessage("compteMinima.editmail.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}
				/*Verif de l'adresse mail*/
				if (candidatController.searchCptMinByEMail(eMailField.getValue()) != null){
					Notification.show(applicationContext.getMessage("compteMinima.mail.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}
				
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la compteMinima saisie */
				if (compteMinimaWindowListener != null){
					compteMinimaWindowListener.btnOkClick(compteMinima);
				}
				
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
	 * Défini le 'CompteMinimaWindowListener' utilisé
	 * @param compteMinimaWindowListener
	 */
	public void addCompteMinimaWindowListener(CompteMinimaWindowListener compteMinimaWindowListener) {
		this.compteMinimaWindowListener = compteMinimaWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface CompteMinimaWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param cptMin 
		 */
		public void btnOkClick(CompteMinima cptMin);

	}
}
