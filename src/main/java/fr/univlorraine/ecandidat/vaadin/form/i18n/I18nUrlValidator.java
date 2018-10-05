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
package fr.univlorraine.ecandidat.vaadin.form.i18n;

import java.net.MalformedURLException;
import java.net.URL;

import com.vaadin.data.Validator;

import fr.univlorraine.ecandidat.entities.ecandidat.I18n;

/** Validateur de champs traduction pour URL
 * 
 * @author Kevin Hergalant */
@SuppressWarnings("serial")
public class I18nUrlValidator implements Validator {

	private String urlMalformedError = "";

	public I18nUrlValidator(final String urlMalformedError) {
		this.urlMalformedError = urlMalformedError;
	}

	/** @see com.vaadin.data.Validator#validate(java.lang.Object) */
	@Override
	public void validate(final Object value) throws InvalidValueException {
		/* Si la valeur est null donc nouvelle, on sort */
		if (value == null) {
			return;
		}
		I18n objet = (I18n) value;
		/* Parcourt de la liste de traductions */
		objet.getI18nTraductions().forEach(e -> {
			try {
				new URL(e.getValTrad());
			} catch (MalformedURLException m) {
				throw new InvalidValueException(urlMalformedError);
			}
		});
	}

}
