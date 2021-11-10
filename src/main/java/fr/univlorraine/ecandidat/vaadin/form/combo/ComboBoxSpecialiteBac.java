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

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacSpeBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolSpecialiteBac;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * ComboBox pour les Bacs
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class ComboBoxSpecialiteBac extends RequiredComboBox<SiScolSpecialiteBac> {

	private final BeanItemContainer<SiScolSpecialiteBac> container;

	private final List<SiScolSpecialiteBac> listeSiScolSpecialiteBac;
	private final List<SiScolBacSpeBac> listBacSpeBac;

	public ComboBoxSpecialiteBac(final List<SiScolSpecialiteBac> listeSiScolSpecialiteBac, final List<SiScolBacSpeBac> listBacSpeBac) {
		super(true);
		container = new BeanItemContainer<>(SiScolSpecialiteBac.class, null);
		setContainerDataSource(container);
		setImmediate(true);
		setNullSelectionAllowed(true);
		listeSiScolSpecialiteBac.sort(Comparator.comparing(SiScolSpecialiteBac::getLibSpeBac));
		this.listeSiScolSpecialiteBac = listeSiScolSpecialiteBac;
		this.listBacSpeBac = listBacSpeBac;
	}

	/**
	 * Filtre le container
	 * @param anneeStr
	 * @param bacNoBac
	 * @param isVisible
	 */
	public void filterListValue(final String anneeStr, final SiScolBacOuxEqu bac, final Boolean isVisible) {
		container.removeAllItems();
		if (anneeStr != null && bac != null) {
			final Integer annee = Integer.valueOf(anneeStr);
			/* On filtre la liste sur les années et si on a des correspondances bac/specialite, on filtre aussi sur ces correspondances */
			final List<SiScolSpecialiteBac> newList = listeSiScolSpecialiteBac
				.stream()
				.filter(e -> ((e.getDaaFinValSpeBac() == null || Integer.valueOf(e.getDaaFinValSpeBac()) >= annee)
					&& (e.getDaaDebValSpeBac() == null || Integer.valueOf(e.getDaaDebValSpeBac()) <= annee)
					&& (listBacSpeBac.isEmpty() || listBacSpeBac.stream().filter(bacSpe -> bacSpe.getCodBac().equals(bac.getCodBac()) && bacSpe.getCodSpeBac().equals(e.getCodSpeBac())).findAny().isPresent())))
				.collect(Collectors.toList());
			if (hasFilterBacSpecialiteOption) {
				container.addAll(newList.stream()
					.filter(e -> listBacSpeBac.stream().filter(bacSpe -> bacSpe.getCodBac().equals(bac.getId().getCodBac()) && bacSpe.getCodSpeBac().equals(e.getId().getCodSpeBac())).findAny().isPresent())
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
}
