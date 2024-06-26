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
package fr.univlorraine.ecandidat.vaadin.form.siscol;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.combobox.FilteringMode;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommuneNaiss;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommuneNaiss_;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * ComboBox pour les communes de naissance
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class ComboBoxCommuneNaiss extends RequiredComboBox<SiScolCommuneNaiss> {

	private final BeanItemContainer<SiScolCommuneNaiss> container;

	public ComboBoxCommuneNaiss(final String suggest) {
		super(false);
		container = new BeanItemContainer<SiScolCommuneNaiss>(SiScolCommuneNaiss.class, null);
		setContainerDataSource(container);
		setTextInputAllowed(true);
		setImmediate(true);
		setInputPrompt(suggest);
		setItemCaptionPropertyId(SiScolCommuneNaiss_.libComNaiss.getName());
		setItemCaptionMode(ItemCaptionMode.PROPERTY);
		setPageLength(10);
	}

	/**
	 * @see com.vaadin.ui.ComboBox#buildFilter(java.lang.String, com.vaadin.shared.ui.combobox.FilteringMode)
	 */
	@Override
	protected Filter buildFilter(final String filterString, final FilteringMode filteringMode) {
		container.removeAllContainerFilters();
		container.addContainerFilter(new SimpleStringFilter(SiScolCommuneNaiss_.libComNaiss.getName(), filterString, true, false));
		return null;
	}

	/**
	 * Met a jour la liste des communes
	 * @param liste
	 */
	public void setListCommune(final List<SiScolCommuneNaiss> liste) {
		container.removeAllItems();
		if (liste != null) {
			container.addAll(liste);
		}
	}
}