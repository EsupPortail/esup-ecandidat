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

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.sort.SortOrder;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.Renderer;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.utils.CustomException;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.UIException;
import fr.univlorraine.ecandidat.utils.bean.presentation.BooleanPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.BooleanPresentation.BooleanValue;
import fr.univlorraine.ecandidat.utils.bean.presentation.ComboBoxFilterPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.ComboBoxFilterPresentation.TypeFilter;
import fr.univlorraine.ecandidat.vaadin.components.GridConverter.LocalDateTimeToStringConverter;
import fr.univlorraine.ecandidat.vaadin.components.GridConverter.LocalDateToStringConverter;
import fr.univlorraine.ecandidat.vaadin.form.LocalDateField;

/**
 * Grid perso formatée
 * @author     Kevin Hergalant
 * @param  <T>
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class GridFormatting<T> extends Grid {

	@Resource
	private transient DateTimeFormatter formatterDate;
	@Resource
	private transient DateTimeFormatter formatterDateTime;
	@Resource
	protected transient ApplicationContext applicationContext;
	/* Listener de filtres */
	private FilterListener filterListener;

	/* Composants utilisés */
	private BeanItemContainerSortable<T> container;
	private final List<SortOrder> listSortOrder = new ArrayList<>();
	private HeaderRow filterRow;
	private HeaderRow headerRow;

	/** Constructeur */
	public GridFormatting(final Class<T> clazz) {
		this(clazz, null);
	}

	/** Constructeur */
	public GridFormatting(final Class<T> clazz, final Map<String, String> mapSortCorres) {
		super();
		setSizeFull();
		setImmediate(true);
		setResponsive(true);
		setColumnReorderingAllowed(true);
		addStyleName(StyleConstants.GRID_POINTER);
		setSelectionMode(SelectionMode.SINGLE);
		container = new BeanItemContainerSortable<>(clazz, mapSortCorres);
		setContainerDataSource(container);
		addSortListener(e -> {
			if (e.getSortOrder().size() > 0) {
				listSortOrder.clear();
				listSortOrder.addAll(e.getSortOrder());
			}
		});
	}

	/**
	 * Initialisation des colonnes par défaut
	 * @param fieldsOrder
	 * @param prefixeProperty
	 * @param sortProperty
	 */
	public void initColumn(final String[] fieldsOrder, final String prefixeProperty, final String sortProperty) {
		initColumn(fieldsOrder, prefixeProperty, sortProperty, SortDirection.ASCENDING);
	}

	/**
	 * Initialisation des colonnes par défaut
	 * @param fieldsOrder
	 * @param prefixeProperty
	 * @param sortProperty
	 */
	public void initColumn(final String[] fieldsOrder, final String prefixeProperty, final String sortProperty, final SortDirection sortDirection) {
		final List<SortOrder> listSortOrder = new ArrayList<>();
		listSortOrder.add(new SortOrder(sortProperty, sortDirection));
		initColumn(fieldsOrder, fieldsOrder, fieldsOrder, prefixeProperty, listSortOrder, null);
	}

	/**
	 * Ajoute les NestedContainerProperty Nettoie les colonnes non visibles Cache
	 * automatiquement les colonnes Trie les colonnes
	 * @param fields
	 * @param fieldsVisible
	 * @param fieldsOrder
	 * @param prefixeProperty
	 * @param sortColonne
	 * @param listeCbFilter
	 */
	public void initColumn(final String[] fields,
		final String[] fieldsVisible,
		final String[] fieldsOrder,
		final String prefixeProperty,
		final List<SortOrder> sortColonne,
		final List<ComboBoxFilterPresentation> listeCbFilter) {
		/* On ajoute les nested property */
		final BeanItemContainerSortable<?> container = (BeanItemContainerSortable<?>) getContainerDataSource();

		/* On traite les colonnes-->ajout des nested et ajout des header */
		Arrays.stream(fields)
			.forEach(e -> {
				/* On ajoute les nested property */
				if (e.contains(".")) {
					container.addNestedContainerProperty(e);
				}
			});
		/* header de colonnes */
		final HeaderRow headerRow = getDefaultHeaderRow();

		/* On supprime les colonnes des champs non présentes dans la liste */
		final List<String> listFields = Arrays.asList(fields);
		/* Parcours et formatage des colonnes */
		getColumns().forEach(e -> {
			if (!listFields.contains(e.getPropertyId())) {
				removeColumn(e.getPropertyId());
			} else {
				final String prop = (String) e.getPropertyId();
				final Class<?> clazz = MethodUtils.getClassProperty(container.getBeanType(), prop);
				if (clazz != null) {
					if (clazz == String.class || clazz == BigDecimal.class || clazz == Long.class || clazz == Double.class) {
						addStringFilter(prop);
					} else if (clazz == Boolean.class) {
						addBooleanColumns(prop);
						addBooleanFilter(prop, MethodUtils.getIsNotNull(container.getBeanType(), prop));
					} else if (clazz == LocalDate.class) {
						addLocalDateFormatingColumn(prop);
						addDateFilter(prop);
					} else if (clazz == LocalDateTime.class) {
						addLocalDateTimeFormatingColumn(prop);
						addDateFilter(prop);
					}
				}
				e.setHeaderCaption(applicationContext.getMessage(prefixeProperty + e.getPropertyId(), null, UI.getCurrent().getLocale()));
				/* On ajoute les tooltip des colonnes */
				headerRow.getCell(prop)
					.setHtml("<div title='" + applicationContext.getMessage(prefixeProperty + prop, null, UI.getCurrent().getLocale())
						+ "'>"
						+ applicationContext.getMessage(prefixeProperty + prop, null, UI.getCurrent().getLocale())
						+ "</div>");

				e.setHidable(true);
				if (!(Arrays.stream(fieldsVisible).filter(f -> e.getPropertyId().equals(f)).findAny().isPresent())) {
					e.setHidden(true);
				} else {
					e.setHidden(false);
				}
			}
		});

		/* On met l'ordre des colonnes */
		setColumnOrder((Object[]) (fieldsOrder));
		listSortOrder.clear();
		listSortOrder.addAll(sortColonne);

		sort();

		/* Initialisation des ComboBox */
		if (listeCbFilter != null) {
			listeCbFilter.forEach(e -> addComboBoxFilter(e));
		}
	}

	/**
	 * Modifie le Modele de selection, ajoute un style si le modele n'est pas à NONE
	 * @return le Modele de selection
	 */
	@Override
	public SelectionModel setSelectionMode(final SelectionMode selectionMode) {
		if (selectionMode != null && selectionMode.equals(SelectionMode.NONE)) {
			removeStyleName(StyleConstants.GRID_POINTER);
		} else {
			addStyleName(StyleConstants.GRID_POINTER);
		}
		return super.setSelectionMode(selectionMode);
	}

	/**
	 * @return                       le bean selectionné
	 * @throws CustomException
	 * @throws UIException
	 * @throws IllegalStateException
	 */
	public T getSelectedItem() {
		try {
			final Object itemId = super.getSelectedRow();
			if (itemId == null) {
				return null;
			}
			return container.getItem(itemId).getBean();
		} catch (final Exception e) {
			throw new UIException();
		}
	}

	/**
	 * @param  itemId
	 * @return        un bean pour son item
	 */
	public T getItem(final Object itemId) {
		try {
			if (itemId == null) {
				return null;
			}
			return container.getItem(itemId).getBean();
		} catch (final Exception e) {
			throw new UIException();
		}
	}

	/** @return la liste des items visibles */
	public List<T> getItems() {
		return container.getItemIds();
	}

	/**
	 * Ajoute tous les éléments au container
	 * @param list
	 */
	public void addItems(final Collection<? extends T> list) {
		container.addAll(list);
		sort();
		recalculateColumnWidths();
	}

	/**
	 * Ajoute un item
	 * @param bean
	 */
	public void addItem(final Object bean) {
		container.addItem(bean);
		sort();
	}

	/**
	 * Supprime un item
	 * @param bean
	 */
	public void removeItem(final Object bean) {
		container.removeItem(bean);
	}

	/**
	 * Modifie un item
	 * @param bean
	 */
	public void updateItem(final Object bean) {
		removeItem(bean);
		addItem(bean);
	}

	/**
	 * Modifie un item
	 * @param oldBean
	 * @param newBean
	 */
	public void updateItem(final Object oldBean, final Object newBean) {
		removeItem(oldBean);
		addItem(newBean);
	}

	/** Supprime tous les éléments du container */
	public void removeAll() {
		container.removeAllItems();
	}

	/**
	 * Supprime tous les éléments du container et Ajoute tous les éléments au
	 * container
	 * @param list
	 */
	public void removeAndAddAll(final Collection<? extends T> list) {
		removeAll();
		addItems(list);
	}

	/** Trie la table */
	public void sort(final List<SortOrder> listSortOrderS) {
		this.listSortOrder.clear();
		this.listSortOrder.addAll(listSortOrderS);
		sort();
	}

	/** Trie la table */
	public void sort() {
		setSortOrder(listSortOrder);
	}

	/** Trie la table */
	public void sortAndDeselect() {
		sort();
		deselectAll();
	}

	/** Desactive le trie */
	public void disableSorting() {
		getColumns().forEach(e -> e.setSortable(false));
	}

	/**
	 * Change la largeur d'une colonne
	 * @param property
	 * @param width
	 */
	public void setColumnWidth(final String property, final Integer width) {
		final Column col = getColumn(property);
		if (col == null) {
			return;
		}
		col.setWidth(width);
	}

	/**
	 * Change la largeur de plusieurs colonnes colonne
	 * @param width
	 * @param propertys
	 */
	public void setColumnsWidth(final Integer width, final String... propertys) {
		for (final String property : propertys) {
			setColumnWidth(property, width);
		}
	}

	/** Expend une colonne */
	public void setExpendColumn(final String property) {
		final Column col = getColumn(property);
		if (col == null) {
			return;
		}
		col.setExpandRatio(1);
	}

	/** Expend toutes les colonnes */
	public void setExpendAllColumns() {
		getColumns().forEach(e -> e.setExpandRatio(1));
	}

	/**
	 * Supprime la largeur défini
	 * @param propertys
	 */
	public void setColumnWidthUndefined(final String... propertys) {
		for (final String property : propertys) {
			getColumn(property).setWidthUndefined();
		}
	}

	/** @return la ligne de header */
	public HeaderRow getHeaderRow() {
		if (headerRow == null) {
			headerRow = prependHeaderRow();
			headerRow.setStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
		}
		return headerRow;
	}

	/**
	 * Ajoute un cellule collspan sur des cellules
	 * @param propertys
	 */
	public void addJoin(final Object... propertys) {
		getHeaderRow().join(propertys);
	}

	/**
	 * Ajoute un cellule collspan et un header
	 * @param txt
	 * @param propertys
	 */
	public void addJoinHeader(final String txt, final Object... propertys) {
		getHeaderRow().join(propertys).setText(txt);
	}

	/**
	 * Ajoute un header sur une cellule
	 * @param txt
	 * @param property
	 */
	public void addCellHeader(final String txt, final Object property) {
		getHeaderRow().getCell(property).setText(txt);
	}

	/**
	 * Ajoute un converter à une colonne
	 * @param propertyId
	 * @param converter
	 */
	public void setColumnConverter(final String propertyId, final Converter<?, ?> converter) {
		final Column col = getColumn(propertyId);
		if (col == null) {
			return;
		}
		col.setConverter(converter);
	}

	/**
	 * AJoute un renderer à une colonne
	 * @param propertyId
	 * @param renderer
	 */
	public void setColumnRenderer(final String propertyId, final Renderer<?> renderer) {
		final Column col = getColumn(propertyId);
		if (col == null) {
			return;
		}
		col.setRenderer(renderer);
	}

	/**
	 * Formate les colonnes en boolean : Ajoute une case a cocher a la place de O et
	 * N
	 * @param propertys
	 */
	private void addBooleanColumns(final String... propertys) {
		for (final String property : propertys) {
			final Column col = getColumn(property).setRenderer(new HtmlRenderer(),
				new StringToBooleanConverter("<div style=width:100%;text-align:center>" + FontAwesome.CHECK_SQUARE_O.getHtml() + "</div>",
					"<div style=width:100%;text-align:center>" + FontAwesome.SQUARE_O.getHtml() + "</div>"));
			col.setWidth(119);
		}
	}

	/**
	 * Formate une colonne en LocalDateTime
	 * @param property
	 */
	private void addLocalDateTimeFormatingColumn(final String property) {
		final Column col = getColumn(property).setConverter(new LocalDateTimeToStringConverter(formatterDateTime, formatterDate));
		col.setWidth(190);
	}

	/**
	 * Formate une colonne en LocalDate
	 * @param property
	 */
	private void addLocalDateFormatingColumn(final String property) {
		final Column col = getColumn(property).setConverter(new LocalDateToStringConverter(formatterDate));
		col.setWidth(135);
	}

	/**
	 * Renvoi une cellule de filtre
	 * @return
	 */
	private HeaderCell getFilterCell(final String property) {
		if (filterRow == null) {
			filterRow = appendHeaderRow();
		}
		return filterRow.getCell(property);
	}

	/**
	 * Supprime des cellule de filtre
	 * @param propertys
	 */
	public void removeFilterCells(final String... propertys) {
		if (filterRow == null) {
			return;
		}
		for (final String property : propertys) {
			filterRow.getCell(property).setComponent(new Label());
		}
	}

	/** Supprime la ligne de filter */
	public void removeFilterRow() {
		if (filterRow != null) {
			removeHeaderRow(filterRow);
		}
	}

	/**
	 * Ajoute un filtre en TextField sur une colonne
	 * @param filterRow
	 * @param container
	 * @param propertys
	 */
	private void addStringFilter(final String property) {
		final HeaderCell cell = getFilterCell(property);
		final TextField filterField = new TextField();
		filterField.setImmediate(true);
		filterField.setWidth(100, Unit.PERCENTAGE);
		filterField.addStyleName(ValoTheme.TEXTFIELD_TINY);
		filterField.setInputPrompt(applicationContext.getMessage("filter.all", null, UI.getCurrent().getLocale()));
		filterField.addTextChangeListener(change -> {
			// Can't modify filters so need to replace
			container.removeContainerFilters(property);
			// (Re)create the filter if necessary
			if (!change.getText().isEmpty()) {
				container.addContainerFilter(new InsensitiveStringFilter(property, change.getText()));
			}
			fireFilterListener();
		});
		cell.setComponent(filterField);
	}

	/**
	 * Ajoute un filtre de Boolean sur une liste de colonnes
	 * @param filterRow
	 * @param container
	 * @param property
	 * @param isNotNull
	 * @param labelTrue
	 * @param labelFalse
	 * @param labelNull
	 */
	private void addBooleanFilter(final String property, final Boolean isNotNull) {
		final HeaderCell cell = getFilterCell(property);
		final ComboBox cbOuiNon = new ComboBox();
		cbOuiNon.setTextInputAllowed(false);

		final List<BooleanPresentation> liste = new ArrayList<>();
		final BooleanPresentation allObject = new BooleanPresentation(BooleanValue.ALL, applicationContext.getMessage("filter.all", null, UI.getCurrent().getLocale()), null);
		liste.add(allObject);
		if (!isNotNull) {
			liste.add(new BooleanPresentation(BooleanValue.NULL, applicationContext.getMessage("filter.null", null, UI.getCurrent().getLocale()), null));
		}
		liste.add(new BooleanPresentation(BooleanValue.TRUE, applicationContext.getMessage("oui.label", null, UI.getCurrent().getLocale()), FontAwesome.CHECK_SQUARE_O));
		liste.add(new BooleanPresentation(BooleanValue.FALSE, applicationContext.getMessage("non.label", null, UI.getCurrent().getLocale()), FontAwesome.SQUARE_O));

		final BeanItemContainer<BooleanPresentation> containerOuiNon = new BeanItemContainer<>(BooleanPresentation.class, liste);
		cbOuiNon.setNullSelectionItemId(allObject);
		cbOuiNon.setImmediate(true);
		cbOuiNon.setContainerDataSource(containerOuiNon);
		cbOuiNon.setItemCaptionPropertyId(BooleanPresentation.CHAMPS_LIBELLE);
		cbOuiNon.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbOuiNon.setItemIconPropertyId(BooleanPresentation.CHAMPS_ICONE);
		cbOuiNon.setWidth(100, Unit.PERCENTAGE);
		cbOuiNon.addStyleName(ValoTheme.COMBOBOX_TINY);

		cbOuiNon.addValueChangeListener(change -> {
			container.removeContainerFilters(property);
			if (cbOuiNon.getValue() != null) {
				final BooleanPresentation value = (BooleanPresentation) cbOuiNon.getValue();
				if (value != null) {
					final BooleanValue booleanValue = value.getValeur();
					switch (booleanValue) {
					case TRUE:
						container.addContainerFilter(new Equal(property, true));
						break;
					case FALSE:
						container.addContainerFilter(new Equal(property, false));
						break;
					case NULL:
						container.addContainerFilter(new Equal(property, null));
						break;
					default:
						break;
					}
				}
				fireFilterListener();
			}
		});
		cell.setComponent(cbOuiNon);
	}

	/**
	 * Ajoute un filtre en DateField sur une liste de colonnes
	 * @param filterRow
	 * @param container
	 * @param propertys
	 */
	private void addDateFilter(final String... propertys) {
		for (final String property : propertys) {
			final HeaderCell cell = getFilterCell(property);
			final LocalDateField filterField = new LocalDateField(true);
			filterField.setImmediate(true);
			filterField.setWidth(100, Unit.PERCENTAGE);
			filterField.addValueChangeListener(change -> {
				final LocalDate value = filterField.getValue();
				// Can't modify filters so need to replace
				container.removeContainerFilters(property);
				// (Re)create the filter if necessary
				if (value != null) {
					container.addContainerFilter(new SimpleStringFilter(property, value.toString(), true, false));
				}
				fireFilterListener();
			});
			cell.setComponent(filterField);
		}
	}

	/**
	 * Ajoute un filtre en combobox String sur une colonne
	 * @param cbFilter
	 *                     le filtre
	 */
	private void addComboBoxFilter(final ComboBoxFilterPresentation cbFilter) {
		final String property = cbFilter.getProperty();
		final ComboBox cb = cbFilter.getCb();

		final HeaderCell cell = getFilterCell(property);
		cb.addValueChangeListener(e -> {
			container.removeContainerFilters(property);
			/* Vérification que la combo contient une valeur */
			if (cb.getValue() != null) {
				/* Cas majoritaire --> property EQUALS, on passe un filtre equals a la property */
				if (cbFilter.getTypeFilter().equals(TypeFilter.EQUALS)) {
					/* vérification que la combo ne contient pas la valeur "Aucun" --> Défini par un objet passé dans le ComboBoxFilterPresentation */
					if (cbFilter.getNullObject() != null && cb.getValue().equals(cbFilter.getNullObject())) {
						/* Dans ce cas, on cherche sur un isNull */
						container.addContainerFilter(new IsNull(property));
					}
					/* vérification que la combo ne contient pas la valeur "Tous sauf..." --> Défini par un objet passé dans le ComboBoxFilterPresentation */
					else if (cbFilter.getExcludeObject() != null && cbFilter.getExcludeLibelle() != null && cb.getValue().equals(cbFilter.getExcludeLibelle())) {
						/* Dans ce cas, on cherche sur un Not Equals */
						container.addContainerFilter(new Not(new Compare.Equal(property, cbFilter.getExcludeObject())));
					} else {
						/* Sinon sur un equals */
						container.addContainerFilter(new Compare.Equal(property, cb.getValue()));
					}
				}
				/* Cas particulier --> property LIST_CONTAINS, on vérifie qu'une property de type list contient une valeur ou est vide ou null */
				else if (cbFilter.getTypeFilter().equals(TypeFilter.LIST_CONTAINS)) {
					container.addContainerFilter(new ListContainsFilter(property, cb.getValue(), cbFilter.getNullObject()));
				}

			}
			/* On déclanche le filtre */
			fireFilterListener();
		});
		cb.setImmediate(true);
		cb.setWidth(100, Unit.PERCENTAGE);
		cb.addStyleName(ValoTheme.COMBOBOX_TINY);
		cell.setComponent(cb);
	}

	/** Lance le listener de filtre */
	private void fireFilterListener() {
		if (filterListener != null) {
			filterListener.filter();
		}
	}

	/**
	 * Défini le 'FilterListener' utilisé
	 * @param filterListener
	 */
	public void addFilterListener(final FilterListener filterListener) {
		this.filterListener = filterListener;
	}

	/** Interface pour récupérer un lancement de filtre. */
	public interface FilterListener extends Serializable {

		/** Appelé lorsqu'un filtre est activé ou désactivé. */
		void filter();

	}
}
