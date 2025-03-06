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

import com.vaadin.v7.data.validator.RegexpValidator;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;

@SuppressWarnings("serial")
public class PasswordValidator extends RegexpValidator {

	/**
	 * Creates a validator for checking that a string is a syntactically valid
	 * e-mail address.
	 * https://howtodoinjava.com/java/regex/java-regex-validate-email-address/
	 * @param errorMessage
	 *                        the message to display in case the value does not validate.
	 */
	public PasswordValidator(final String errorMessage) {
		super(ConstanteUtils.REGEX_PWD, true, errorMessage);
	}
}