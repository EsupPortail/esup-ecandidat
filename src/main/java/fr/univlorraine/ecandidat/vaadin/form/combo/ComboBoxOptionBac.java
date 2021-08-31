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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOptBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolOptionBac;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * ComboBox pour les Bacs
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class ComboBoxOptionBac extends RequiredComboBox<SiScolOptionBac> {

	private final BeanItemContainer<SiScolOptionBac> container;

	private final List<SiScolOptionBac> listeSiScolOptionBac;
	private final List<SiScolBacOptBac> listBacOptBac;
	private final boolean hasFilterBacSpecialiteOption;

	public ComboBoxOptionBac(final List<SiScolOptionBac> listeSiScolOptionBac, final List<SiScolBacOptBac> listBacOptBac, final boolean hasFilterBacSpecialiteOption) {
		super(true);
		container = new BeanItemContainer<>(SiScolOptionBac.class, null);
		setContainerDataSource(container);
		setImmediate(true);
		setNullSelectionAllowed(true);
		listeSiScolOptionBac.sort(Comparator.comparing(SiScolOptionBac::getLibOptBac));
		this.listeSiScolOptionBac = listeSiScolOptionBac;
		this.listBacOptBac = listBacOptBac;
		this.hasFilterBacSpecialiteOption = hasFilterBacSpecialiteOption;
	}

	/**
	 * Filtre le container
	 * @param anneeStr
	 * @param bacNoBac
	 */
	public void filterListValue(final String anneeStr, final SiScolBacOuxEqu bac) {
		container.removeAllItems();
		if (anneeStr != null && bac != null) {
			final Integer annee = Integer.valueOf(anneeStr);
			final List<SiScolOptionBac> newList = listeSiScolOptionBac
				.stream()
				.filter(e -> ((e.getDaaFinValOptBac() == null || Integer.valueOf(e.getDaaFinValOptBac()) >= annee)
					&& (e.getDaaDebValOptBac() == null || Integer.valueOf(e.getDaaDebValOptBac()) <= annee)))
				.collect(Collectors.toList());

			if (hasFilterBacSpecialiteOption) {
				container.addAll(newList.stream()
					.filter(e -> listBacOptBac.stream().filter(bacOpt -> bacOpt.getCodBac().equals(bac.getId().getCodBac()) && bacOpt.getCodOptBac().equals(e.getId().getCodOptBac())).findAny().isPresent())
					.collect(Collectors.toList()));
			} else {
				container.addAll(newList);
			}
		}
		setVisible(container.getItemIds().size() > 0);
		if (container.getItemIds().size() == 0) {
			setValue(null);
		}
	}

	/**
	 * Si il y a des items dans la combo
	 * @return
	 */
	public boolean hasItems() {
		return container.getItemIds().size() > 0;
	}
}
