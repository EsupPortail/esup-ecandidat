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
package fr.univlorraine.ecandidat.vaadin.form;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.ui.TextField;

/**
 * Champs de text field d'integer customisé
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class RequiredIntegerField extends TextField implements IRequiredField {

	private boolean shouldHideError = true;

	protected String requieredError;

	/**
	 * Constructeur
	 */
	public RequiredIntegerField() {
		super();
		setConverter(new StringToIntegerConverter() {

			@Override
			protected NumberFormat getFormat(final Locale locale) {
				return new DecimalFormat("#");
			}
		});
		addValidator(value -> {
			if (value == null) {
				return;
			}
			Integer integerValue = null;
			try {
				integerValue = Integer.valueOf(value.toString());
			} catch (Exception e) {
				throw new InvalidValueException(getConversionError());
			}
			if (value != null && integerValue < 0) {
				throw new InvalidValueException(getConversionError());
			}
		});
	}

	/**
	 * @see com.vaadin.ui.AbstractField#shouldHideErrors()
	 */
	@Override
	protected boolean shouldHideErrors() {
		Boolean hide = shouldHideError;
		shouldHideError = false;
		return hide;
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#preCommit()
	 */
	@Override
	public void preCommit() {
		shouldHideError = false;
		super.setRequiredError(requieredError);
		if (isEmpty()) {
			fireValueChange(false);
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#initField(java.lang.Boolean)
	 */
	@Override
	public void initField(final Boolean immediate) {
		setImmediate(immediate);
		super.setRequiredError(null);
	}

	/**
	 * @see com.vaadin.ui.AbstractField#setRequiredError(java.lang.String)
	 */
	@Override
	public void setRequiredError(final String requiredMessage) {
		requieredError = requiredMessage;
	}
}