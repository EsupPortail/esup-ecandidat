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

import com.vaadin.data.Validator;

import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/**
 * Validateur de champs traduction
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class I18nLenghtValidator implements Validator {

	private String tooLongError = "";

	public I18nLenghtValidator(final String tooLongError) {
		this.tooLongError = tooLongError;
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
			/* Verif de la taille d'une traduc */
			String valTrad = MethodUtils.cleanHtmlValue(e.getValTrad());
			if (valTrad != null && valTrad.length() > objet.getTypeTraduction().getLengthTypTrad()) {
				String msg = tooLongError.replace(ConstanteUtils.I18N_MSG_SIZE, objet.getTypeTraduction().getLengthTypTrad().toString());
				msg = msg.replace(ConstanteUtils.I18N_MSG_SIZE_ACTUAL, String.valueOf(valTrad.length()));
				if (e.getLangue() != null && e.getLangue().getCodLangue() != null) {
					msg = msg.replace(ConstanteUtils.I18N_MSG_LANGUE, e.getLangue().getCodLangue());
				}
				throw new InvalidValueException(msg);
			}
		});
	}

}
