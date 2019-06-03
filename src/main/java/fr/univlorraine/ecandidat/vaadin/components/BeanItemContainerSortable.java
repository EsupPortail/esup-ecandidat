/**
 *
 */
package fr.univlorraine.ecandidat.vaadin.components;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.DefaultItemSorter;

/**
 * BeanItemContainer permettant d'ajouter une colonne non sortable aux tris.
 * Permet dee trier cette colonne sur une autre property
 * @author Kevin
 *
 */
@SuppressWarnings("serial")
public class BeanItemContainerSortable<T> extends BeanItemContainer<T> {

	private Map<String, String> mapSortCorres = new HashMap<>();

	/**
	 * @param type
	 * @throws IllegalArgumentException
	 */
	public BeanItemContainerSortable(final Class<? super T> type, final Map<String, String> mapSortCorres) throws IllegalArgumentException {
		super(type);
		if (mapSortCorres != null) {
			this.mapSortCorres = mapSortCorres;
			setItemSorter(new DefaultItemSorter() {
				@Override
				protected int compareProperty(final Object propertyId, final boolean sortDirection, final com.vaadin.data.Item item1, final com.vaadin.data.Item item2) {
					String propSortable = mapSortCorres.get(propertyId);
					if (propSortable != null) {
						String tag1 = (String) item1.getItemProperty(propSortable).getValue();
						String tag2 = (String) item2.getItemProperty(propSortable).getValue();
						int c = tag1.compareTo(tag2);
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
		Set<Object> result = new HashSet<>(super.getSortableContainerPropertyIds());
		if (mapSortCorres != null) {
			mapSortCorres.forEach((k, v) -> result.add(k));
		}
		return result;
	}
}
