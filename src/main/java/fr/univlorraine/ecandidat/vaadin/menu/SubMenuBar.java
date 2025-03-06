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

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.navigator.Navigator;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;

/**
 * Class Sous menu
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class SubMenuBar extends TabSheet{

	/***serialVersionUID*/
	private static final long serialVersionUID = 7553358799501168128L;
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	
	/** Construit la bar de sous menu
	 * @param menu
	 * @param navigator
	 * @param vue
	 */
	public void constructMenuBar(Menu menu, Navigator navigator, String vue){
		/*Suppression des listeners du tabSheet-->Sinon lorsqu'on ajoute un élément, il navigue donc boucle*/
		cleanTabSheetListener();
		/*Suppression puis ajout des composant du menu*/
		this.removeAllComponents();
		menu.getSubMenu().forEach(s -> {
			this.addTab(s.getLabel(), applicationContext.getMessage(s.getVue() + ".title", null, UI.getCurrent().getLocale()),s.getIcon());
		});
		/*On selectionne l'onglet qui va bien, utile lors du back navigateur ou clavier*/
		selectSubMenuSheet(menu, vue, navigator,false);
		/*Ajout du listener*/
		addTabSheetListener(navigator);
	}
	
	/** Selection d'un element du sous menu
	 * @param menu
	 * @param vue
	 */
	public void selectSubMenuSheet(Menu menu, String vue,Navigator navigator, Boolean cleanListener){
		if (cleanListener){
			/*Suppression des listeners du tabSheet-->Sinon lorsqu'on ajoute un élément, il navigue donc boucle*/
			cleanTabSheetListener();
		}
		/*Suppression des listeners du tabSheet-->Sinon lorsqu'on ajoute un élément, il navigue donc boucle*/
		cleanTabSheetListener();
		menu.getSubMenu().forEach(s -> {
			if (s.getVue().equals(vue)){
				this.setSelectedTab(s.getLabel());
			}
		});
		/*Ajout du listener*/
		if (cleanListener){
			addTabSheetListener(navigator);
		}
	}
	
	/**
	 * Suppression des listener du menu
	 */
	private void cleanTabSheetListener(){
		for(Object listener : this.getListeners(SelectedTabChangeEvent.class)){
			this.removeListener(SelectedTabChangeEvent.class, listener);
		}
	}
	
	/**
	 * Ajout du listener du sous menu
	 */
	private void addTabSheetListener(Navigator navigator){
		this.addSelectedTabChangeListener(e -> {
			if (e.getTabSheet().getSelectedTab()!=null){
				navigator.navigateTo((String) ((Label)e.getTabSheet().getSelectedTab()).getData());
			}
		});
	}

}
