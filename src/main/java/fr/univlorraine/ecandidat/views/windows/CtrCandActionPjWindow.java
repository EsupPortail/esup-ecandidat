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

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fr.univlorraine.ecandidat.controllers.CandidatureController;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;

/**
 * Fenêtre d'action sur une ou plusieurs pièces justif
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CtrCandActionPjWindow extends Window{
	

	/** serialVersionUID **/
	private static final long serialVersionUID = -7776558654950981770L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatureController candidatureController;
	
	public static final String[] FIELDS_ORDER = {PjCand_.typeStatutPiece.getName(),
		PjCand_.commentPjCand.getName()};
	
	/* Composants */
	private CustomBeanFieldGroup<PjCand> fieldGroup;
	private FormLayout formLayout;
	
	private OneClickButton btnValid;
	private OneClickButton btnClose;
	
	private ChangeStatutPieceWindowListener changeStatutPieceWindowListener;

	/**
	 * Crée une fenêtre d'action sur une ou plusieurs pièces justif
	 * @param listePj la liste des pièces à manipuler
	 */
	@SuppressWarnings("unchecked")
	public CtrCandActionPjWindow(List<PjPresentation> listePj) {
		/* Style */
		setModal(true);
		setWidth(550,Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("pj.action.window", null, UI.getCurrent().getLocale()));
        
        /*Le field group pour la decision*/
        fieldGroup = new CustomBeanFieldGroup<>(PjCand.class);
		fieldGroup.setItemDataSource(new PjCand());
		formLayout = new FormLayout();
		formLayout.setCaption(applicationContext.getMessage("pj.action.label", new Object[]{listePj.size()}, UI.getCurrent().getLocale()));
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			Field<?> field = fieldGroup.buildAndBind(applicationContext.getMessage("pj.action." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);
		}
		layout.addComponent(formLayout);
		
		RequiredTextField tf = (RequiredTextField)fieldGroup.getField(PjCand_.commentPjCand.getName());
		RequiredComboBox<TypeStatutPiece> cb = (RequiredComboBox<TypeStatutPiece>)fieldGroup.getField(PjCand_.typeStatutPiece.getName());
		cb.setRequired(true);
		cb.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
		if (listePj.size()==1){						
			cb.setValue(new TypeStatutPiece(listePj.get(0).getCodStatut(),""));
			tf.setValue(listePj.get(0).getCommentaire());
		}
		
        
		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);
		
		btnClose = new OneClickButton(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnClose.addClickListener(e -> close());
		buttonsLayout.addComponent(btnClose);
		buttonsLayout.setComponentAlignment(btnClose, Alignment.MIDDLE_LEFT);

		btnValid = new OneClickButton(applicationContext.getMessage("btnValid", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnValid.addClickListener(e -> {
			try {
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la typeStatutPiece saisie */
				changeStatutPieceWindowListener.btnOkClick((TypeStatutPiece)cb.getValue(),tf.getValue());
				/* Ferme la fenêtre */
				close();
			} catch (CommitException ce) {
			}			
		});
		buttonsLayout.addComponent(btnValid);
		buttonsLayout.setComponentAlignment(btnValid, Alignment.MIDDLE_RIGHT);
		
		/* Centre la fenêtre */
		center();
	}
	
	/**
	 * Défini le 'ChangeStatutPieceWindowListener' utilisé
	 * @param changeStatutPieceWindowListener
	 */
	public void addChangeStatutPieceWindowListener(ChangeStatutPieceWindowListener changeStatutPieceWindowListener) {
		this.changeStatutPieceWindowListener = changeStatutPieceWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui.
	 */
	public interface ChangeStatutPieceWindowListener extends Serializable {

		/** Appelé lorsque Oui est cliqué.
		 * @param typeStatutPiece
		 * @param commentaire
		 */
		public void btnOkClick(TypeStatutPiece typeStatutPiece, String commentaire);

	}
}
