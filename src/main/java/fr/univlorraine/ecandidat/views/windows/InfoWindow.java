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

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de confirmation
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class InfoWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 3632242925880712176L;

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	/* Composants */
	private OneClickButton btnAnnuler = new OneClickButton();

	/**
	 * Crée une fenêtre d'information
	 * @param message
	 * @param titre
	 */
	public InfoWindow(String titre, String message, Integer width, Integer percentageHeight) {
		/* Style */
		if (width==null){
			width = 400;
		}
		setWidth(width, Unit.PIXELS);
		if (percentageHeight!=null){
			setHeight(percentageHeight,Unit.PERCENTAGE);
		}
		setModal(true);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(titre);

		/* Texte */
		Label textLabel = new Label(message,ContentMode.HTML);
		
		if (percentageHeight!=null){
			layout.setSizeFull();
			/* Titre */
			VerticalLayout layoutItem = new VerticalLayout();
			layoutItem.setMargin(true);
			layoutItem.setWidth(100, Unit.PERCENTAGE);
			layoutItem.setSpacing(true);
			layoutItem.addComponent(textLabel);
			/* Panel */
			Panel panel = new Panel();
			panel.setSizeFull();
			panel.setContent(layoutItem);
			
			layout.addComponent(panel);
			layout.setExpandRatio(panel, 1);
		}else{
			layout.addComponent(textLabel);
		}
		
		

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_CENTER);

		/* Centre la fenêtre */
		center();
	}

}
