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
/**
 *
 */
package fr.univlorraine.ecandidat.vaadin.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.DefaultItemSorter;

/**
 * BeanItemContainer permettant d'ajouter une colonne non sortable aux tris.
 * Permet dee trier cette colonne sur une autre property
 * @author Kevin
 */
@SuppressWarnings("serial")
public class BeanItemContainerSortable<T> extends BeanItemContainer<T> {

	private Map<String, String> mapSortCorres = new HashMap<>();

	/**
	 * @param  type
	 * @throws IllegalArgumentException
	 */
	public BeanItemContainerSortable(final Class<? super T> type, final Map<String, String> mapSortCorres) throws IllegalArgumentException {
		super(type);
		if (mapSortCorres != null) {
			this.mapSortCorres = mapSortCorres;
			setItemSorter(new DefaultItemSorter() {
				@Override
				protected int compareProperty(final Object propertyId, final boolean sortDirection, final com.vaadin.v7.data.Item item1, final com.vaadin.v7.data.Item item2) {
					final String propSortable = mapSortCorres.get(propertyId);
					if (propSortable != null) {
						final String tag1 = (String) item1.getItemProperty(propSortable).getValue();
						final String tag2 = (String) item2.getItemProperty(propSortable).getValue();
						final int c = tag1.compareTo(tag2);
						return sortDirection ? c : -(c);
					} else {
						return super.compareProperty(propertyId, sortDirection, item1, item2);
					}
				}
			});
		}
	}

	@Override
	public Collection<?> getSortableContainerPropertyIds() {
		final Set<Object> result = new HashSet<>(super.getSortableContainerPropertyIds());
		if (mapSortCorres != null) {
			mapSortCorres.forEach((k, v) -> result.add(k));
		}
		return result;
	}
}
