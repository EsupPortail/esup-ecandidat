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

import com.vaadin.v7.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * ComboBox pour les Bacs
 * @author Kevin Hergalant
 */
public class ComboBoxBacOuEqu extends RequiredComboBox<SiScolBacOuxEqu> {

	/** serialVersionUID **/
	private static final long serialVersionUID = -1735636736386162950L;

	private final BeanItemContainer<SiScolBacOuxEqu> container;

	private final List<SiScolBacOuxEqu> listeSiScolBacOuxEqu;

	public ComboBoxBacOuEqu(final List<SiScolBacOuxEqu> listeSiScolBacOuxEqu) {
		super(true);
		container = new BeanItemContainer<>(SiScolBacOuxEqu.class, null);
		setContainerDataSource(container);
		this.listeSiScolBacOuxEqu = listeSiScolBacOuxEqu;
	}

	/**
	 * Filtre le container
	 * @param annee
	 * @param bacNoBac
	 */
	public void filterListValue(final Integer annee, final SiScolBacOuxEqu bacNoBac) {
		container.removeAllItems();
		if (annee != null) {
			final List<SiScolBacOuxEqu> newList = listeSiScolBacOuxEqu
				.stream()
				.filter(e -> (bacNoBac == null || (!bacNoBac.getId().getCodBac().equals(e.getId().getCodBac())))
					&& (e.getDaaFinVldBac() == null || Integer.valueOf(e.getDaaFinVldBac()) >= annee)
					&& (e.getDaaDebVldBac() == null || Integer.valueOf(e.getDaaDebVldBac()) <= annee))
				.collect(Collectors.toList());
			container.addAll(newList);
			if (newList.size() > 0) {
				setValue(newList.get(0));
			}
		}
	}

	/**
	 * Filtre le container et selectionne sans bac
	 * @param bac
	 */
	public void filterAndSelectNoBac(final SiScolBacOuxEqu bac) {
		container.removeAllItems();
		final List<SiScolBacOuxEqu> newList = listeSiScolBacOuxEqu.stream().filter(e -> e.getId().getCodBac().equals(bac.getId().getCodBac())).collect(Collectors.toList());
		container.addAll(newList);
		if (newList.size() > 0) {
			setValue(newList.get(0));
		}
	}
}
