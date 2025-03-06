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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.MethodProperty.MethodException;
import com.vaadin.v7.ui.Table;

import jakarta.annotation.Resource;

/**
 * Table apportant un pattern aux format de date, de double, de boolean
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class TableFormating extends Table {

	@Resource
	private transient DateTimeFormatter formatterDate;
	@Resource
	private transient DateTimeFormatter formatterDateTime;
	private final NumberFormat integerFormatter = new DecimalFormat("#");

	public TableFormating(final String string, final Container dataSource, final DateTimeFormatter formatterDate, final DateTimeFormatter formatterDateTime) {
		super(string, dataSource);
		this.formatterDate = formatterDate;
		this.formatterDateTime = formatterDateTime;
	}

	public TableFormating(final String string, final Container dataSource) {
		super(string, dataSource);
	}

	public TableFormating(final Container dataSource) {
		super(null, dataSource);
	}

	public TableFormating() {
		super();
	}

	@Override
	protected String formatPropertyValue(final Object rowId, final Object colId, final Property<?> property) {
		Object v;
		try {
			v = property.getValue();
		} catch (final MethodException e) {
			return "";
		}
		if (v instanceof LocalDate) {
			final LocalDate dateValue = (LocalDate) v;
			return formatterDate.format(dateValue);
		} else if (v instanceof LocalDateTime) {
			final LocalDateTime dateValue = (LocalDateTime) v;
			return formatterDateTime.format(dateValue);
		} else if (v instanceof Integer) {
			return integerFormatter.format(v);
		} else if (v instanceof Boolean) {
			final Boolean boolValue = (Boolean) v;
			if (boolValue) {
				return "O";
			} else {
				return "N";
			}
		}
		return super.formatPropertyValue(rowId, colId, property);
	}

	public void addBooleanColumn(final String property) {
		addBooleanColumn(property, true);
	}

	/**
	 * Ajoute une case a cocher a la place de O et N
	 * @param property
	 */
	public void addBooleanColumn(final String property, final Boolean alignCenter) {
		addGeneratedColumn(property, new Table.ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				try {
					final Object value = PropertyUtils.getProperty(itemId, (String) columnId);
					if (value instanceof Boolean) {
						return new IconLabel((Boolean) value, alignCenter);
					} else {
						return value;
					}
				} catch (final Exception e) {
					return null;
				}
			}
		});
	}
}
