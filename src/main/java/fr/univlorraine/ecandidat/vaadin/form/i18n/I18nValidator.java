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

import com.vaadin.v7.data.Validator;

import fr.univlorraine.ecandidat.entities.ecandidat.I18n;

/**
 * Validateur de champs traduction
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class I18nValidator implements Validator {

	private String errorOneMissing = "";
	private String errorSameLang = "";

	public I18nValidator(final String errorOneMissing, final String errorSameLang) {
		this.errorOneMissing = errorOneMissing;
		this.errorSameLang = errorSameLang;
	}

	/** @see com.vaadin.data.Validator#validate(java.lang.Object) */
	@Override
	public void validate(final Object value) throws InvalidValueException {
		/* Si la valeur est null donc nouvelle, on sort */
		if (value == null) {
			return;
		}

		final I18n objet = (I18n) value;

		/* Parcourt de la liste de traductions */
		objet.getI18nTraductions().forEach(e -> {
			/* Verif qu'il ne manque pas une traduc */
			if (e.getValTrad() == null || e.getValTrad().trim().equals("")) {
				throw new InvalidValueException(errorOneMissing);
			}
			/* Verif qu'une langue n'est pas appelée deux fois */
			if (objet.getI18nTraductions().stream().filter(x -> x.getLangue().getCodLangue().equals(e.getLangue().getCodLangue())).count() > 1) {
				throw new InvalidValueException(errorSameLang + "(" + e.getLangue().getCodLangue() + ")");
			}
		});
	}

}
