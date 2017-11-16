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
package fr.univlorraine.ecandidat.views.template;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fr.univlorraine.ecandidat.vaadin.components.CustomPanel;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de template de preference
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CtrCandPreferenceWindowTemplate extends Window {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = 190132500153558283L;

	@Resource
	private transient ApplicationContext applicationContext;
	
	private VerticalLayout layoutSpecifique = new VerticalLayout();
	private CustomPanel panelInfo = new CustomPanel();
	private OneClickButton btnReinit = new OneClickButton(FontAwesome.DATABASE);
	private OneClickButton btnRecordSession = new OneClickButton(FontAwesome.FLOPPY_O);
	private OneClickButton btnRecordDb = new OneClickButton(FontAwesome.DATABASE);
	
	/**
	 * Créé un template de préférence
	 */
	public CtrCandPreferenceWindowTemplate() {
		super();
		
		/* Style */
		setModal(true);
		setWidth(900,Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);
		
		/*Les messages d'information*/
		panelInfo.setCaption(applicationContext.getMessage("informations", null, UI.getCurrent().getLocale()));
		panelInfo.setIcon(FontAwesome.INFO_CIRCLE);
		panelInfo.setWidthMax();		
		layout.addComponent(panelInfo);
		
		/*On ajoute le layout specifique*/
		layoutSpecifique.setSpacing(true);
		layoutSpecifique.setSizeFull();
		layout.addComponent(layoutSpecifique);

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		OneClickButton btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);
		
		btnReinit.setCaption(applicationContext.getMessage("preference.btnInit", null, UI.getCurrent().getLocale()));
		buttonsLayout.addComponent(btnReinit);
		buttonsLayout.setComponentAlignment(btnReinit, Alignment.MIDDLE_CENTER);
		
		btnRecordSession.setCaption(applicationContext.getMessage("preference.btnRecordSession", null, UI.getCurrent().getLocale()));
		buttonsLayout.addComponent(btnRecordSession);
		buttonsLayout.setComponentAlignment(btnRecordSession, Alignment.MIDDLE_CENTER);
		
		btnRecordDb.setCaption(applicationContext.getMessage("preference.btnRecordDb", null, UI.getCurrent().getLocale()));
		buttonsLayout.addComponent(btnRecordDb);
		buttonsLayout.setComponentAlignment(btnRecordDb, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}
	
	
	/** Modifie le message d'info
	 * @param infoSpecifique
	 */
	protected void setInfoMessage(String infoSpecifique){
		panelInfo.setMessage(applicationContext.getMessage("preference.info.general", null, UI.getCurrent().getLocale())+infoSpecifique);
	}
	
	/** Ajoute le listener sur le bouton reinit
	 * @param listener
	 */
	protected void addReinitClickListener(ClickListener listener){
		btnReinit.addClickListener(listener);
	}
	
	/** Ajoute le listener sur le bouton save session
	 * @param listener
	 */
	protected void addRecordSessionClickListener(ClickListener listener){
		btnRecordSession.addClickListener(listener);
	}

	/** Ajoute le listener sur le bouton save db
	 * @param listener
	 */
	protected void addRecordDbClickListener(ClickListener listener){
		btnRecordDb.addClickListener(listener);
	}
	
	/** Ajoute un composant
	 * @param c
	 */
	protected void addComponentSpecifique(Component c){
		layoutSpecifique.addComponent(c);
	}
}
