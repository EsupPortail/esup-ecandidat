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

import java.util.List;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.filter.IsNull;

/**
 * Filtre de recherche dans une liste
 * @author Kevin Hergalant
 */
@SuppressWarnings({ "serial", "rawtypes" })
public final class ListContainsFilter implements Filter {

	private final Object propertyId;
	private final Object filterObject;
	private final Object nullValue;

	public ListContainsFilter(final Object propertyId, final Object filterObject, final Object nullValue) {
		this.propertyId = propertyId;
		this.filterObject = filterObject;
		this.nullValue = nullValue;
	}

	@Override
	public boolean passesFilter(final Object itemId, final Item item)
		throws UnsupportedOperationException {
		final Property<?> p = item.getItemProperty(getPropertyId());
		if (null == p) {
			return false;
		}

		final List listValue = (List) p.getValue();
		/* Recherche sur le nullObject --> on cherche sur liste vide ou null */
		if (nullValue != null && filterObject.equals(nullValue)) {
			return listValue == null || listValue.size() == 0;
		}

		/* On n'a pas passé de nullObject, si la liste est null, on sort */
		if (listValue == null) {
			return false;
		}

		/* Vérification si la liste contient la valeur */
		return listValue.contains(filterObject);
	}

	@Override
	public boolean appliesToProperty(final Object propertyId) {
		return getPropertyId().equals(propertyId);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}

		// Only objects of the same class can be equal
		if (!getClass().equals(obj.getClass())) {
			return false;
		}
		final IsNull o = (IsNull) obj;

		// Checks the properties one by one
		return (null != getPropertyId())
			? getPropertyId().equals(o.getPropertyId())
			: null == o.getPropertyId();
	}

	@Override
	public int hashCode() {
		return (null != getPropertyId() ? getPropertyId().hashCode() : 0);
	}

	/**
	 * Returns the property id of the property tested by the filter, not null
	 * for valid filters.
	 * @return property id (not null)
	 */
	public Object getPropertyId() {
		return propertyId;
	}

}
