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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatParcoursController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPro;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPro_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextArea;

/**
 * Fenêtre d'édition de cursus pro
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CandidatCursusProWindow extends CandidatScolariteWindow {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 8279285838139858898L;
		
	public static final String[] FIELDS_ORDER_FORMATIONS = {
		CandidatCursusPro_.anneeCursusPro.getName(),
		CandidatCursusPro_.intituleCursusPro.getName(),
		CandidatCursusPro_.dureeCursusPro.getName(),		
		CandidatCursusPro_.organismeCursusPro.getName(),
		CandidatCursusPro_.objectifCursusPro.getName()
	};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;
	@Resource
	private transient TableRefController tableRefController;

	/* Composants */
	private CustomBeanFieldGroup<CandidatCursusPro> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;
	private CursusProWindowListener cursusProWindowListener;
	/**
	 * Crée une fenêtre d'édition de cursus pro
	 * @param cursus à éditer
	 */
	public CandidatCursusProWindow(CandidatCursusPro cursus, Boolean nouveau) {		
		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		if (nouveau){
			setCaption(applicationContext.getMessage("formationpro.window.new", null, UI.getCurrent().getLocale()));
		}else{
			setCaption(applicationContext.getMessage("formationpro.window.update", null, UI.getCurrent().getLocale()));
		}

		
		/*Layout adresse*/		
		fieldGroup = new CustomBeanFieldGroup<CandidatCursusPro>(CandidatCursusPro.class,ConstanteUtils.TYP_FORM_CANDIDAT);
		fieldGroup.setItemDataSource(cursus);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER_FORMATIONS) {
			Field<?> field;
			if (fieldName.equals(CandidatCursusPro_.objectifCursusPro.getName())){
				field = fieldGroup.buildAndBind(applicationContext.getMessage("formationpro." + fieldName, null, UI.getCurrent().getLocale()), fieldName, RequiredTextArea.class);
			}else{
				field = fieldGroup.buildAndBind(applicationContext.getMessage("formationpro." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
			}
			
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);
		}
		
		/*Gestion de l'année d'obtention*/
		Integer anneeN1 = LocalDate.now().getYear()+1;
		String conversionError = applicationContext.getMessage("validation.parse.annee", null, UI.getCurrent().getLocale());
		RequiredIntegerField rif = (RequiredIntegerField) fieldGroup.getField(CandidatCursusPro_.anneeCursusPro.getName());
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
				/* Valide la saisie du cursus*/
				fieldGroup.commit();
				/* Enregistre le cursus saisi */
				cursusProWindowListener.btnOkClick(candidatParcoursController.saveCursusPro(cursus));
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
	 * Défini le 'cursusProWindowListener' utilisé
	 * @param cursusProWindowListener
	 */
	public void addCursusProWindowListener(CursusProWindowListener cursusProWindowListener) {
		this.cursusProWindowListener = cursusProWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui.
	 */
	public interface CursusProWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param candidatCursusPro 
		 */
		public void btnOkClick(CandidatCursusPro candidatCursusPro);

	}
}
