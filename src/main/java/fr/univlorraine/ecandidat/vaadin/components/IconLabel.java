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
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

/**
 * Renvoi un label de Boolean sous forme d'icon check non-check 
 * @author Kevin Hergalant
 *
 */
public class IconLabel extends Label{

	/**serialVersionUID**/
	private static final long serialVersionUID = -2879012049228013806L;

	public IconLabel(Boolean value, Boolean alignCenter){
		super();
		if (value == null){
			return;
		}
		setContentMode(ContentMode.HTML);
		setValue(
				"<div style=width:100%;text-align:"+(alignCenter?"center":"left")+";>"+
				(value?FontAwesome.CHECK_SQUARE_O.getHtml():FontAwesome.SQUARE_O.getHtml())+
				"</div>"
				);
	}
}
