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
package fr.univlorraine.ecandidat.vaadin.form.combo;

import java.util.List;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;

import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les Langues
 * @author Kevin Hergalant
 *
 */
public class ComboBoxLangue extends RequiredComboBox<Langue>{

	/**serialVersionUID**/
	private static final long serialVersionUID = 4302455271844387446L;
	
	private List<Langue> liste;
	
	@SuppressWarnings("unchecked")
	public ComboBoxLangue(List<Langue> liste, Boolean libelle){
		super(false);
		this.liste = liste;
		BeanItemContainer<Langue> container = new BeanItemContainer<Langue>(Langue.class, null);
		container.addNestedContainerProperty(ConstanteUtils.PROPERTY_FLAG);
		liste.forEach(e -> {container.addItem(e).getItemProperty(ConstanteUtils.PROPERTY_FLAG).setValue(new ThemeResource("images/flags/" + e.getCodLangue()+ ".png"));});
		
		setContainerDataSource(container);

		// Sets the combobox to show a certain property as the item caption
		if (libelle){
			setItemCaptionPropertyId(Langue_.libLangue.getName());
			setItemCaptionMode(ItemCaptionMode.PROPERTY);
		}else{
			setItemCaptionPropertyId(Langue_.codLangue.getName());
			setItemCaptionMode(ItemCaptionMode.ICON_ONLY);
		}	

		// Sets the icon to use with the items
		setItemIconPropertyId(ConstanteUtils.PROPERTY_FLAG);

		setImmediate(true);

		// Disallow null selections
		setNullSelectionAllowed(false);
		
		
	}
	
	/** Selectionne la langue désirée
	 * @param langue
	 */
	public void selectLangue(Langue langue){
		if (liste.size()>0){
			if (langue!=null){
				setValue(langue);
			}else{
				setValue(liste.get(0));
			}			
		}
	}
}
