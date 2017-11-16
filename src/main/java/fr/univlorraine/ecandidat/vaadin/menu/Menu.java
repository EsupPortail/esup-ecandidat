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

import java.io.Serializable;
import java.util.LinkedList;

import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import lombok.Data;
import lombok.ToString;

/**
 * Class permettant d'avoir un menu et un sous menu dans l'UI
 * @author Kevin Hergalant
 *
 */
@Data
@ToString(exclude="btn")
public class Menu implements Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = -444837676017360049L;
	
	/*** La vue */
	private String view;
	
	/*** La liste des sous menu */
	private LinkedList<SubMenu> subMenu;

	/*** Le bouton associé */
	private OneClickButton btn;
	
	public Menu(String view, LinkedList<SubMenu> subMenu, OneClickButton btn) {
		super();
		this.view = view;
		this.subMenu = subMenu;
		this.btn = btn;
	}
	
	/** Verification si le menu possède des sous menus
	 * @return true si le menu possède deja le sous menu
	 */
	public Boolean hasSubMenu(){
		if (this.getSubMenu()!=null && this.getSubMenu().size()>0){
			return true;
		}else{
			return false;
		}
	}
}
