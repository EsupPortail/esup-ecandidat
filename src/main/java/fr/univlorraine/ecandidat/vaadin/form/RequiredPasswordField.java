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

import com.vaadin.server.FontAwesome;
import com.vaadin.v7.ui.PasswordField;

/**
 * Champs de password
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class RequiredPasswordField extends PasswordField implements IRequiredField {

	private boolean shouldHideError = true;

	private String requieredError;
	private final String validationError;
	private final String info;

	public RequiredPasswordField(final String requieredError, final String validationError, final String info) {
		super();
		this.requieredError = requieredError;
		this.validationError = validationError;
		this.info = info;
	}

	/**
	 * Ajoute une validation
	 */
	public void addPwdValidation() {
		addValidator(new PasswordValidator(validationError));
		setDescription(info);
		setIcon(FontAwesome.INFO_CIRCLE);
	}

	/**
	 * @see com.vaadin.ui.AbstractField#shouldHideErrors()
	 */
	@Override
	protected boolean shouldHideErrors() {
		final Boolean hide = shouldHideError;
		shouldHideError = false;
		return hide;
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#preCommit()
	 */
	@Override
	public void preCommit() {
		shouldHideError = false;
		super.setRequiredError(this.requieredError);
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
		this.requieredError = requiredMessage;
	}
}