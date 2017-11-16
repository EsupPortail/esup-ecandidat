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

import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

/** Champs de CheckBox customisé, modifie la position du label natif de vaadin
 * @author Kevin Hergalant
 *
 */
public class RequiredCheckBox extends CustomField<Boolean> implements IRequiredField{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -4721685836823109589L;
	
	private boolean shouldHideError = true;
	private String requieredError;
	
	private CheckBox field;
	
	protected Boolean value;

	public RequiredCheckBox() {
		field = new CheckBox();
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
	 * @see com.vaadin.ui.AbstractField#addValueChangeListener(com.vaadin.data.Property.ValueChangeListener)
	 */
	@Override
	public void addValueChangeListener(ValueChangeListener listener) {
		field.addValueChangeListener(listener);
	}


	/**
	 * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Boolean newFieldValue) throws ReadOnlyException,
			ConversionException {
		if (newFieldValue==null){
			newFieldValue = false;
		}
		value = newFieldValue;		
		super.setValue(newFieldValue);
	}

	/**
	 * @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object)
	 */
	@Override
	protected void setInternalValue(Boolean newFieldValue){
		if (newFieldValue==null){
			newFieldValue = false;
		}
		value = newFieldValue;
		super.setInternalValue(newFieldValue);	
	}

	/**
	 * @see com.vaadin.ui.AbstractField#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getValue() == null;
	}


	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#preCommit()
	 */
	@Override
	public void preCommit() {
		shouldHideError = false;
		super.setRequiredError(this.requieredError);
		if (isEmpty()){
			fireValueChange(false);
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#initField(java.lang.Boolean)
	 */
	@Override
	public void initField(Boolean immediate) {
		setImmediate(immediate);
		super.setRequiredError(null);
	}
	
	
	
	/**
	 * ON MET LA CASE A COCHER SANS ETOILE
	 * @see com.vaadin.ui.AbstractField#setRequired(boolean)
	 */
	@Override
	public void setRequired(boolean required) {
		super.setRequired(false);
	}

	/**
	 * @see com.vaadin.ui.AbstractField#setRequiredError(java.lang.String)
	 */
	@Override
	public void setRequiredError(String requiredMessage) {
		//this.requieredError = requiredMessage;
		this.requieredError = null;
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getValue()
	 */
	@Override
	public Boolean getValue() {
		return field.getValue();
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getInternalValue()
	 */
	@Override
	protected Boolean getInternalValue() {
		return field.getValue();
	}
	
	/**
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		field.setValue(value);
		return field;
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}

}