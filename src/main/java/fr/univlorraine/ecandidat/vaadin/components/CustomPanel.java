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
package fr.univlorraine.ecandidat.vaadin.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Panel;

import fr.univlorraine.ecandidat.StyleConstants;

/**
 * Classe permettant d'intégrer un panel avec icone et message
 * @author Kevin
 *
 */
public class CustomPanel extends Panel{

	/**	serialVersionUID **/
	private static final long serialVersionUID = 6669280791113951889L;

	Label labelMessage = new Label("", ContentMode.HTML);
	private HorizontalLayout vlContentLabelMessage = new HorizontalLayout();
	
	
	/**
	 * Constructeur
	 */
	public CustomPanel(){
		labelMessage.addStyleName(StyleConstants.LABEL_SAUT_LIGNE);
		labelMessage.setSizeUndefined();
		vlContentLabelMessage.setSizeFull();		
		vlContentLabelMessage.addComponent(labelMessage);
		setContent(vlContentLabelMessage);
	}
	
	/** Constructeur étendu
	 * @param caption
	 * @param icon
	 */
	public CustomPanel(String caption, FontAwesome icon){
		this();		
		setCaption(caption);
		setIcon(icon);
	}
	
	/** Constructeur étendu
	 * @param caption
	 * @param message
	 * @param icon
	 */
	public CustomPanel(String caption, String message, FontAwesome icon){
		this();		
		setCaption(caption);
		setIcon(icon);
		setMessage(message);
	}
	
	/** Ajoute une marge
	 * @param margin
	 */
	public void setMargin(Boolean margin){
		vlContentLabelMessage.setMargin(true);
	}
	
	/** Modifie le message
	 * @param message
	 */
	public void setMessage(String message){
		labelMessage.setValue(message);
	}
	
	/**
	 * Met le panel a 100%
	 */
	public void setWidthMax(){
		setWidth(100, Unit.PERCENTAGE);
	}
	
	/** Ajuste ne pixel la largeur du panel
	 * @param pixel
	 */
	public void setWidthPixel(Integer pixel){
		setWidth(pixel, Unit.PIXELS);
	}

	/** Ajoute un style au label
	 * @param style
	 */
	public void addLabelStyleName(String style) {
		labelMessage.addStyleName(style);
	}
	
	/** CHange le composant principal
	 * @param c
	 */
	public void setComponent(Component c){
		vlContentLabelMessage.removeComponent(labelMessage);
		vlContentLabelMessage.addComponent(c);
	}
}
