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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatureCtrCandController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.PostIt;
import fr.univlorraine.ecandidat.entities.ecandidat.PostIt_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextArea;

/**
 * Fenêtre d'édition de notes
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CtrCandPostItAddWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 2000188096541940501L;

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatureCtrCandController candidatureCtrCandController;
	@Resource
	private transient TableRefController tableRefController;

	/* Composants */
	private CustomBeanFieldGroup<PostIt> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;
	private PostItWindowListener postItWindowListener;

	/**Crée une fenêtre d'édition de postit
	 * @param postIt
	 */
	public CtrCandPostItAddWindow(PostIt postIt) {		
		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(600, Unit.PIXELS);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("postit.add.window", null, UI.getCurrent().getLocale()));

		/*Layout adresse*/		
		fieldGroup = new CustomBeanFieldGroup<PostIt>(PostIt.class,ConstanteUtils.TYP_FORM_CANDIDAT);
		fieldGroup.setItemDataSource(postIt);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		Field<?> field = fieldGroup.buildAndBind(applicationContext.getMessage("postit.table." + PostIt_.messagePostIt.getName(), null, UI.getCurrent().getLocale()), PostIt_.messagePostIt.getName(), RequiredTextArea.class); 
		field.setWidth(100, Unit.PERCENTAGE);
		formLayout.addComponent(field);
		
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
				postItWindowListener.btnOkClick(candidatureCtrCandController.savePostIt(postIt));
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
	 * Défini le 'postItWindowListener' utilisé
	 * @param postItWindowListener
	 */
	public void addPostItWindowListener(PostItWindowListener postItWindowListener) {
		this.postItWindowListener = postItWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui.
	 */
	public interface PostItWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param postIt 
		 */
		public void btnOkClick(PostIt postIt);

	}
}
