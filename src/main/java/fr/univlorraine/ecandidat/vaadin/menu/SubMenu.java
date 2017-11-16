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

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Label;

import lombok.Data;
import lombok.ToString;

/**
 * Class permettant d'avoir un menu et un sous menu dans l'UI
 * @author Kevin Hergalant
 *
 */
@Data
@ToString(exclude={"icon","label"})
public class SubMenu implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 156153180626476710L;
	
	/*** La vue */
	private String vue;
	
	/*** L'icone */
	private FontAwesome icon;
	
	/*** Le label du tabSheet */
	private Label label;
	
	public SubMenu(String vue, FontAwesome icon) {
		super();
		this.vue = vue;
		this.icon = icon;
		this.label = new Label();
		this.label.setData(this.vue);
	}
}
