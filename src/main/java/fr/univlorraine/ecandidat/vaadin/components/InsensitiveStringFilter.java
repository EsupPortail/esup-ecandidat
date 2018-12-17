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
package fr.univlorraine.ecandidat.vaadin.components;

import java.text.Normalizer;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.SimpleStringFilter;

/**
 * Filtre non sensible à la casse ni aux accents
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public final class InsensitiveStringFilter implements Filter {

	final Object propertyId;
	final String filterString;

	public InsensitiveStringFilter(final Object propertyId, final String filterString) {
		this.propertyId = propertyId;
		this.filterString = stripAccents(filterString.toLowerCase());
	}

	/**
	 * Pass le filtre
	 *
	 * @param itemId
	 * @param item
	 * @return true si filtre ok
	 */
	@Override
	public boolean passesFilter(final Object itemId, final Item item) {
		final Property<?> p = item.getItemProperty(propertyId);
		if (p == null) {
			return false;
		}
		Object propertyValue = p.getValue();
		if (propertyValue == null) {
			return false;
		}

		final String value = stripAccents(propertyValue.toString());
		if (!value.contains(filterString)) {
			return false;
		}
		return true;
	}

	/**
	 * Supprime les accents et met en minuscule
	 *
	 * @param src
	 * @return chaine en minuscule, sans accent
	 */
	public String stripAccents(final String src) {
		return Normalizer.normalize(src.toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

	@Override
	public boolean appliesToProperty(final Object propertyId) {
		return this.propertyId.equals(propertyId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		// Only ones of the objects of the same class can be equal
		if (!(obj instanceof SimpleStringFilter)) {
			return false;
		}
		final SimpleStringFilter o = (SimpleStringFilter) obj;

		// Checks the properties one by one
		if (propertyId != o.getPropertyId() && o.getPropertyId() != null && !o.getPropertyId().equals(propertyId)) {
			return false;
		}
		if (filterString != o.getFilterString() && o.getFilterString() != null
				&& !o.getFilterString().equals(filterString)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return (propertyId != null ? propertyId.hashCode() : 0) ^ (filterString != null ? filterString.hashCode() : 0);
	}

	/**
	 * Returns the property identifier to which this filter applies.
	 *
	 * @return property id
	 */
	public Object getPropertyId() {
		return propertyId;
	}

	/**
	 * Returns the filter string.
	 * Note: this method is intended only for implementations of lazy string filters
	 * and may change in the future.
	 *
	 * @return filter string given to the constructor
	 */
	public String getFilterString() {
		return filterString;
	}
}
