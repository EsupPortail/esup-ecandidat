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
import java.util.Optional;

import com.vaadin.v7.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les Commissions
 * @author Kevin Hergalant
 *
 */
public class ComboBoxCommission extends RequiredComboBox<Commission>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -6162803324051983163L;
	
	BeanItemContainer<Commission> container;
	
	
	public ComboBoxCommission() {
		super(true);
		container = new BeanItemContainer<Commission>(Commission.class);
		setContainerDataSource(container);
	}

	/**Filtre le container
	 * @param liste
	 */
	public void filterListValue(List<Commission> liste){
		container.removeAllItems();
		container.addAll(liste);	
	}

	public Boolean setCommissionValue(Integer idCommissionSelected) {
		if (container==null){
			return false;
		}
		Optional<Commission> commissionSelected = container.getItemIds().stream().filter(e->e.getIdComm().equals(idCommissionSelected)).findFirst();
		if (commissionSelected.isPresent()){
			setValue(commissionSelected.get());
			return true;
		}
		return false;
	}
}