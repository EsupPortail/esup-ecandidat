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
package fr.univlorraine.ecandidat.vaadin.menu;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;

/** Class d'accordeon pour les menus
 * @author Kevin Hergalant
 *
 */
public class AccordionMenu extends VerticalLayout{ 
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 4819725284839402315L;
	
	private List<AccordionItemMenu> listChildren;
	private String itemId;

	public AccordionMenu(){
		super();
		listChildren = new ArrayList<AccordionItemMenu>();
	}
	
	/** AJout d'un item de menu
	 * @param menuItem
	 * @param id
	 */
	public void addItemMenu(AccordionItemMenu menuItem, String id){
		setWidth(100, Unit.PERCENTAGE);
		menuItem.setData(id);
		menuItem.addStyleName(StyleConstants.VALO_MENUACCORDEON);
		listChildren.add(menuItem);
		this.addComponent(menuItem);		
	}

	/** Appelé lors du clic sur un item
	 * @param id
	 */
	public void changeItem(String id) {
		//on place l'id de l'item en cours sur l'id clique
		itemId = id;
		listChildren.forEach(e->{
			if (e.getData().equals(id)){
				e.setButtonVisible(true);
			}else{
				e.setButtonVisible(false);
			}
		});
	}
	
	/**
	 * A la premiere construction on ouvre le 1er item
	 */
	public void selectFirst(){
		if (listChildren!=null && listChildren.size()>0){
			changeItem((String) listChildren.get(0).getData());
		}
	}

	/** Retourne l'id de l'item selectionne en cours
	 * @return l'id de l'item
	 */
	public String getItemId() {
		return itemId;
	}
	
}
