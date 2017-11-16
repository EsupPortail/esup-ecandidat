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

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.UI;

/**
 * Navigateur rechargeant une vue même si c'est la même qui était déjà en cours
 * @author Kevin
 *
 */
public class ReloadViewNavigator extends Navigator{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7926898628703886727L;

	public ReloadViewNavigator(UI ui, CssLayout contentLayout) {
		super(ui, contentLayout);
	}
	
	public void changeCurrentView() {
		 ViewChangeEvent event = new ViewChangeEvent(this, null, null, null, null);
		 super.switchView(event);
	}
	
}
