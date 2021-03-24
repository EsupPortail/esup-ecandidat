package fr.univlorraine.ecandidat.vaadin.form;

import com.vaadin.data.validator.RegexpValidator;

@SuppressWarnings("serial")
public class EmailRFCValidator extends RegexpValidator {

	/**
	 * Creates a validator for checking that a string is a syntactically valid
	 * e-mail address.
	 * https://howtodoinjava.com/java/regex/java-regex-validate-email-address/
	 * @param errorMessage
	 *                         the message to display in case the value does not validate.
	 */
	public EmailRFCValidator(final String errorMessage) {
		super("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", true, errorMessage);
	}
}