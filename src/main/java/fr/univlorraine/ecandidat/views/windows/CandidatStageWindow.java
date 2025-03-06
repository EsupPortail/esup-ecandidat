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
import java.time.LocalDate;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import com.vaadin.v7.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatParcoursController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatStage;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatStage_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextArea;

/**
 * Fenêtre d'édition de stage
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CandidatStageWindow extends CandidatScolariteWindow {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 8279285838139858898L;
	
	public static final String[] FIELDS_ORDER_STAGE = {
		CandidatStage_.anneeStage.getName(),
		CandidatStage_.dureeStage.getName(),
		CandidatStage_.nbHSemStage.getName(),
		CandidatStage_.organismeStage.getName(),
		CandidatStage_.descriptifStage.getName()
	};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;
	@Resource
	private transient TableRefController tableRefController;

	/* Composants */
	private CustomBeanFieldGroup<CandidatStage> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;
	private StageWindowListener stageWindowListener;
	/**
	 * Crée une fenêtre d'édition de stage
	 * @param stage le stage à éditer
	 */
	public CandidatStageWindow(CandidatStage stage, Boolean nouveau) {	
		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		if (nouveau){
			setCaption(applicationContext.getMessage("stage.window.new", null, UI.getCurrent().getLocale()));
		}else{
			setCaption(applicationContext.getMessage("stage.window.update", null, UI.getCurrent().getLocale()));
		}
		
		/*Layout adresse*/		
		fieldGroup = new CustomBeanFieldGroup<CandidatStage>(CandidatStage.class,ConstanteUtils.TYP_FORM_CANDIDAT);
		fieldGroup.setItemDataSource(stage);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);		
		
		for (String fieldName : FIELDS_ORDER_STAGE) {
			Field<?> field;
			if (fieldName.equals(CandidatStage_.descriptifStage.getName())){
				field = fieldGroup.buildAndBind(applicationContext.getMessage("stage." + fieldName, null, UI.getCurrent().getLocale()), fieldName, RequiredTextArea.class);
			}else{
				field = fieldGroup.buildAndBind(applicationContext.getMessage("stage." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
			}
			
			if (fieldName.equals(CandidatStage_.anneeStage.getName())){
				
				
			}
			
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);			
		}
		
		/*Gestion de l'année d'obtention*/
		Integer anneeN1 = LocalDate.now().getYear()+1;
		String conversionError = applicationContext.getMessage("validation.parse.annee", null, UI.getCurrent().getLocale());
		RequiredIntegerField rif = (RequiredIntegerField) fieldGroup.getField(CandidatStage_.anneeStage.getName());
		rif.setConversionError(conversionError);
		rif.removeAllValidators();
		rif.addValidator(value->{					
			if (value==null){
				return;
			}
			Integer integerValue = null;
			try{
				integerValue = Integer.valueOf(value.toString());				
			}catch (Exception e){
				throw new InvalidValueException(conversionError);
			}
			if (value!=null && (integerValue<1900 || integerValue>anneeN1)){
				throw new InvalidValueException(conversionError);
			}
		});
		/*Fin Gestion de l'année d'obtention*/
		
		layout.addComponent(formLayout);
		layout.setExpandRatio(formLayout, 1);
		
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
				/* Valide la saisie du stage*/
				fieldGroup.commit();
				/* Enregistre le cursus saisi */
				stageWindowListener.btnOkClick(candidatParcoursController.saveStage(stage));
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
	 * Défini le 'stageWindowListener' utilisé
	 * @param stageWindowListener
	 */
	public void addCursusProWindowListener(StageWindowListener stageWindowListener) {
		this.stageWindowListener = stageWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui.
	 */
	public interface StageWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param candidatStage 
		 */
		public void btnOkClick(CandidatStage candidatStage);

	}
}
