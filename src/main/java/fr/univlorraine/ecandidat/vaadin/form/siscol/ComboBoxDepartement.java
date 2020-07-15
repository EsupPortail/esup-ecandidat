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
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.combobox.FilteringMode;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartementPK_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement_;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * ComboBox pour les departements
 * @author Kevin Hergalant
 */
public class ComboBoxDepartement extends RequiredComboBox<SiScolDepartement> {

	/** serialVersionUID **/
	private static final long serialVersionUID = -6228803739439963326L;

	private final BeanItemContainer<SiScolDepartement> container;

	public ComboBoxDepartement(final List<SiScolDepartement> listeSiScolDepartement, final String suggest) {
		super(true);
		container = new BeanItemContainer<>(SiScolDepartement.class, listeSiScolDepartement);
		setContainerDataSource(container);
		setTextInputAllowed(true);
		setImmediate(true);
		setInputPrompt(suggest);
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
		final SimpleStringFilter libFilter = new SimpleStringFilter(SiScolDepartement_.libDep.getName(), filterString, true, false);
		final SimpleStringFilter codFilter = new SimpleStringFilter(SiScolDepartement_.id.getName() + "." + SiScolDepartementPK_.codDep.getName(), filterString, true, false);
		container.addContainerFilter(new Or(libFilter, codFilter));
		return null;
	}
}