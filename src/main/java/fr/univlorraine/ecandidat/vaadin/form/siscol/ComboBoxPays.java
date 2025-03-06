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

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.filter.SimpleStringFilter;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays_;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * ComboBox pour les pays
 * @author Kevin Hergalant
 */
public class ComboBoxPays extends RequiredComboBox<SiScolPays> {

	/** serialVersionUID **/
	private static final long serialVersionUID = 299220968012576093L;

	private final BeanItemContainer<SiScolPays> container;

	public ComboBoxPays(final List<SiScolPays> listeSiScolPays, final String suggest) {
		super(false);
		container = new BeanItemContainer<SiScolPays>(SiScolPays.class, listeSiScolPays);
		setContainerDataSource(container);
		setTextInputAllowed(true);
		setImmediate(true);
		setInputPrompt(suggest);
		setItemCaptionPropertyId(SiScolPays_.libPay.getName());
		setItemCaptionMode(ItemCaptionMode.PROPERTY);
		setPageLength(10);
	}

	/**
	 * @see com.vaadin.ui.ComboBox#buildFilter(java.lang.String, com.vaadin.shared.ui.combobox.FilteringMode)
	 */
	@Override
	protected Filter buildFilter(final String filterString,
		final FilteringMode filteringMode) {
		container.removeAllContainerFilters();
		container.addContainerFilter(new SimpleStringFilter(SiScolPays_.libPay.getName(), filterString, true, false));
		return null;
	}

	/**
	 * Change le libellé affiché et le suggest en nationalité
	 * @param suggest
	 */
	public void setToNationalite(final String suggest) {
		setItemCaptionPropertyId(SiScolPays_.libNat.getName());
		setInputPrompt(suggest);
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox#preCommit()
	 */
	@Override
	public void preCommit() {
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox#initField(java.lang.Boolean)
	 */
	@Override
	public void initField(final Boolean immediate) {

	}
}