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

import java.net.URL;

import com.vaadin.v7.data.Validator;

/**
 * Validateur de champs traduction pour URL
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class UrlValidator implements Validator {

	private String urlMalformedError = "";

	public UrlValidator(final String urlMalformedError) {
		this.urlMalformedError = urlMalformedError;
	}

	/** @see com.vaadin.data.Validator#validate(java.lang.Object) */
	@Override
	public void validate(final Object value) throws InvalidValueException {
		/* Si la valeur est null donc nouvelle, on sort */
		if (value == null) {
			return;
		}
		try {
			new URL((String) value);
		} catch (final Exception e) {
			throw new InvalidValueException(urlMalformedError);
		}

	}

}
