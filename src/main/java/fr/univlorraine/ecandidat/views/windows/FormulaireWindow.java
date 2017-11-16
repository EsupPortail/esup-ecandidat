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

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.FormulaireController;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire_;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nField;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nUrlValidator;

/**
 * Fenêtre d'édition de formulaire
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class FormulaireWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -4216291942687296918L;

	public static final String[] FIELDS_ORDER = {Formulaire_.idFormulaireLimesurvey.getName(),Formulaire_.codFormulaire.getName(),Formulaire_.libFormulaire.getName(),Formulaire_.tesFormulaire.getName(),Formulaire_.temCommunFormulaire.getName(),Formulaire_.temConditionnelFormulaire.getName(),Formulaire_.i18nLibFormulaire.getName(),Formulaire_.i18nUrlFormulaire.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormulaireController formulaireController;

	/* Composants */
	private CustomBeanFieldGroup<Formulaire> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de formulaire
	 * @param formulaire la formulaire à éditer
	 */
	public FormulaireWindow(Formulaire formulaire) {
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
		setCaption(applicationContext.getMessage("formulaire.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(Formulaire.class);
		fieldGroup.setItemDataSource(formulaire);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			String caption = applicationContext.getMessage("formulaire.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);
		}
		
		((I18nField)fieldGroup.getField(Formulaire_.i18nUrlFormulaire.getName())).addValidator(new I18nUrlValidator(applicationContext.getMessage("validation.i18n.url.malformed", null, UI.getCurrent().getLocale())));
		((I18nField)fieldGroup.getField(Formulaire_.i18nLibFormulaire.getName())).addCenterListener(e-> {if(e){center();}});
		((I18nField)fieldGroup.getField(Formulaire_.i18nUrlFormulaire.getName())).addCenterListener(e-> {if(e){center();}});

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
				/*Si le code de profil existe dejà --> erreur*/
				if (!formulaireController.isCodFormUnique((String) fieldGroup.getField(Formulaire_.codFormulaire.getName()).getValue(),(String) fieldGroup.getField(Formulaire_.idFormulaireLimesurvey.getName()).getValue(), formulaire.getIdFormulaire())){
					Notification.show(applicationContext.getMessage("formulaire.error.cod.nonuniq", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}				
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la formulaire saisie */
				formulaireController.saveFormulaire(formulaire);
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
}
