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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.vaadin.server.UserError;
import com.vaadin.ui.TabSheet;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.ui.Field;

import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.IRequiredField;

/**
 * Classe de TabSheet customisée
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class CustomTabSheet extends TabSheet {

	private final Map<Integer, String[]> mapFieldOrder;
	private final CustomBeanFieldGroup<?> fieldGroup;
	private final String errorMessage;

	public CustomTabSheet(final CustomBeanFieldGroup<?> fieldGroup, final String errorMessage) {
		super();
		this.fieldGroup = fieldGroup;
		mapFieldOrder = new HashMap<>();
		this.errorMessage = errorMessage;
	}

	public void addGroupField(final Integer key, final String[] value) {
		final String[] oldValue = mapFieldOrder.get(key);
		if (oldValue != null) {
			mapFieldOrder.put(key, ArrayUtils.addAll(oldValue, value));
		} else {
			mapFieldOrder.put(key, value);
		}
	}

	/**
	 * Supprime les eventuelles erreur des onglets
	 */
	public void effaceErrorSheet() {
		mapFieldOrder.forEach((k, v) -> {
			displayErrorSheet(false, k);
		});
	}

	/**
	 * Affiche les erreurs pour tout le tableau
	 * @param fieldError
	 */
	public void getSheetOnError(final Map<Field<?>, InvalidValueException> fieldError) {
		if (fieldError == null) {
			return;
		}
		mapFieldOrder.forEach((k, v) -> {
			sheetHasError(fieldError, v, k);
		});
	}

	/**
	 * @return la liste des champs en erreur
	 */
	public Map<Field<?>, InvalidValueException> getFieldError() {
		final Map<Field<?>, InvalidValueException> map = new HashMap<>();
		for (final Field<?> field : fieldGroup.getFields()) {
			try {
				field.validate();
			} catch (final InvalidValueException e) {
				map.put(field, e);
			}
		}
		return map;
	}

	/**
	 * @return la liste des champs en erreur
	 */
	public Map<Field<?>, InvalidValueException> getFieldsError(final String[] fields) {
		final Map<Field<?>, InvalidValueException> map = new HashMap<>();
		for (final String fieldName : fields) {
			final Field<?> field = fieldGroup.getField(fieldName);
			try {
				final IRequiredField reqField = (IRequiredField) field;
				reqField.preCommit();
				field.validate();
			} catch (final InvalidValueException e) {
				map.put(field, e);
			}
		}
		return map;
	}

	/**
	 * Valide les sheets
	 */
	public void validateSheet() {
		getSheetOnError(getFieldError());
	}

	/**
	 * Verifie qu'il y a une erreur dans un sheet
	 * @param fieldError
	 * @param FIELDS_ORDER
	 * @param tabOrder
	 */
	private void sheetHasError(final Map<Field<?>, InvalidValueException> fieldError, final String[] FIELDS_ORDER, final Integer tabOrder) {
		Boolean findError = false;
		for (int i = 0; i < FIELDS_ORDER.length; i++) {
			if (fieldError.get(fieldGroup.getField(FIELDS_ORDER[i])) != null) {
				findError = true;
			}
		}
		displayErrorSheet(findError, tabOrder);
	}

	/**
	 * Affiche les erreur d'un sheet avec un point exclam en logo
	 * @param findError
	 * @param tabOrder
	 */
	public void displayErrorSheet(final Boolean findError, final Integer tabOrder) {
		if (findError) {
			this.getTab(tabOrder).setComponentError(new UserError(errorMessage));
		} else {
			this.getTab(tabOrder).setComponentError(null);
		}
	}

}
