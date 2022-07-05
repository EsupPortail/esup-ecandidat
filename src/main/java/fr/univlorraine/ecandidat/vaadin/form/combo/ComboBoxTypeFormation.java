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
import java.util.stream.Collectors;

import com.vaadin.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * ComboBox pour les Types de Traitement
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class ComboBoxTypeFormation extends RequiredComboBox<TypeTraitement> {

	private BeanItemContainer<TypeTraitement> container;

	private List<TypeTraitement> listeTypeTraitement;

	public ComboBoxTypeFormation(final List<TypeTraitement> listeTypeTraitement) {
		super(true);
		container = new BeanItemContainer<>(TypeTraitement.class, listeTypeTraitement);
		setContainerDataSource(container);
		this.listeTypeTraitement = listeTypeTraitement;
	}

	/**
	 * Filtre la combo
	 */
	public void filterFinal() {
		container.removeAllItems();
		List<TypeTraitement> newList = listeTypeTraitement.stream().filter(e -> e.getTemFinalTypTrait().equals(true)).collect(Collectors.toList());
		container.addAll(newList);
	}

	/**
	 * AJoute l'item 'Tous les types de traitement' --> Utilisé pour les justifs
	 */
	public void addTypTraitAll(final TypeTraitement oldValue, final TypeTraitement typTraitAll) {
		setItemCaptionMode(ItemCaptionMode.EXPLICIT);
		setNullSelectionAllowed(true);
		setNullSelectionItemId(typTraitAll);

		container.removeAllItems();
		container.addBean(typTraitAll);
		setItemCaption(typTraitAll, typTraitAll.getLibTypTrait());
		listeTypeTraitement.forEach(e -> {
			container.addBean(e);
			setItemCaption(e, e.getGenericLibelle());
		});
		setValue(oldValue);
	}
}
