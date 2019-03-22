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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.DateField;
import com.vaadin.ui.UI;

/**
 * Champs de date field customisé
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class RequiredDateField extends DateField implements IRequiredField {

	private boolean shouldHideError = true;

	private String requieredError;

	/**
	 * @see com.vaadin.ui.AbstractComponent#setCaption(java.lang.String)
	 */
	@Override
	public void setCaption(String caption) {
		if (caption != null) {
			if (UI.getCurrent().getLocale() != null && UI.getCurrent().getLocale().getLanguage().equals("en")) {
				caption = caption + " (mm/dd/yy)";
			} else {
				caption = caption + " (jj/mm/aa)";
			}
		}

		super.setCaption(caption);
	}

	/**
	 * @see com.vaadin.ui.DateField#shouldHideErrors()
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

	/**
	 * Modifie la valeur
	 * @param localDate
	 */
	public void setLocalValue(final LocalDate localDate) {
		if (localDate == null) {
			setValue(null);
			return;
		}
		Instant instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		setValue(Date.from(instant));
	}

	/**
	 * @return la date en format LocalDate
	 */
	public LocalDate getLocalValue() {
		Date d = getValue();
		if (d == null) {
			return null;
		} else {
			try {
				LocalDate date = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				return date;
			} catch (Exception e) {
				return null;
			}
		}
	}

	/**
	 * Ajoute un validator pour être supérieur à la date du jour
	 * @param message message d'erreur
	 */
	public void mustBeAfterNow(final String message) {
		addValidator(value -> {
			if (value == null) {
				return;
			}
			if (((LocalDate) value).isBefore(LocalDate.now())) {
				throw new InvalidValueException(message);
			}
		});
	}
}