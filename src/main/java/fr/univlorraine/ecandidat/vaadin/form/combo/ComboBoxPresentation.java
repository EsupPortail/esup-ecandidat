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
package fr.univlorraine.ecandidat.vaadin.form.combo;

import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleBeanPresentation;
import fr.univlorraine.ecandidat.vaadin.form.IRequiredField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les String simples
 * @author Kevin Hergalant
 *
 */
public class ComboBoxPresentation extends CustomField<String> implements IRequiredField{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -8961942083430898321L;
	
	private boolean shouldHideError = true;
	private String requieredError;
	
	protected RequiredComboBox<String> field;
	
	protected String value;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ComboBoxPresentation() {
		field = new RequiredComboBox(true);
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
	public void setValue(String newFieldValue) throws ReadOnlyException,
			ConversionException {
		
		if (newFieldValue!=null){
			field.setValue(new SimpleBeanPresentation(newFieldValue));
		}
		value = newFieldValue;		
		super.setValue(newFieldValue);
	}

	/**
	 * @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object)
	 */
	@Override
	protected void setInternalValue(String newFieldValue){
		if (newFieldValue!=null){
			field.setValue(new SimpleBeanPresentation(newFieldValue));
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
		field.preCommit();
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
	 * @see com.vaadin.ui.AbstractField#setRequiredError(java.lang.String)
	 */
	@Override
	public void setRequiredError(String requiredMessage) {
		this.requieredError = requiredMessage;
		field.setRequiredError(requiredMessage);
		
	}
	
	@Override
    public void setRequired(boolean required) {
		field.setRequired(required);
		super.setRequired(required);
    }

	/**
	 * @see com.vaadin.ui.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		if (field.getValue()==null){
			return null;
		}			
		return ((SimpleBeanPresentation)field.getValue()).getCode();
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getInternalValue()
	 */
	@Override
	protected String getInternalValue() {
		if (field.getValue()==null){
			return null;
		}			
		return ((SimpleBeanPresentation)field.getValue()).getCode();
	}
	
	/**
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		return field;
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<String> getType() {
		return String.class;
	}

	public void setListe(List<SimpleBeanPresentation> liste) {
		field.setContainerDataSource(new BeanItemContainer<SimpleBeanPresentation>(SimpleBeanPresentation.class,liste));
		
	}

}