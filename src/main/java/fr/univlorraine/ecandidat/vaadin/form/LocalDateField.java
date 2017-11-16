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

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/** Champs de LocalDate customisé
 * @author Kevin Hergalant
 *
 */
public class LocalDateField extends CustomField<LocalDate> implements IRequiredField{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -5051771040344068728L;
	
	private DateField dateField;
	private LocalDate timeValue;
	
	private boolean shouldHideError = true;

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
	 * @param context
	 */
	public void init (String context) {
    }
	
    /**
     * @param dataSource
     */
    public LocalDateField(Property<LocalDateTime> dataSource) {
    	setPropertyDataSource(dataSource);
    }
	
	public LocalDateField(Boolean tiny) {
		dateField = new DateField();
		if (tiny){
			dateField.addStyleName(ValoTheme.DATEFIELD_TINY);
		}
		dateField.setWidth(100,Unit.PERCENTAGE);
		dateField.addValueChangeListener(e->showOrHideErrorDateField());
		dateField.setImmediate(true);
	}
	
	/**
	 * 
	 */
	private void showOrHideErrorDateField(){
		fireValueChange(false);
		if (isRequired()){
			if (dateField.getValue()==null){
				dateField.addStyleName(StyleConstants.FIELD_ERROR);
			}else{
				dateField.removeStyleName(StyleConstants.FIELD_ERROR);
			}
		}		
	}
	
	/**
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		setDateValue(timeValue);		
		return dateField;
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return dateField.getValue()==null;
	}
	
	/**
	 * @return l'heure saisie
	 */
	/**
	 * @return
	 */
	private LocalDate getDateValue(){
		if (dateField.getValue()==null){
			return null;
		}else{
			return MethodUtils.convertDateToLocalDate(dateField.getValue());
		}
	}
	
	/**
	 * @param newFieldValue
	 */
	private void setDateValue(LocalDate newFieldValue){
		if (newFieldValue==null){
			dateField.setValue(null);
		}else{
			LocalDate localDate= LocalDate.of(newFieldValue.getYear(), newFieldValue.getMonth(), newFieldValue.getDayOfMonth());
			dateField.setValue(MethodUtils.convertLocalDateToDate(localDate));
		}
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#getValue()
	 */
	@Override
	public LocalDate getValue() {	
		return getDateValue();
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(LocalDate newFieldValue) throws ReadOnlyException,
			ConversionException {		
		super.setValue(newFieldValue);
		timeValue = newFieldValue;
		setDateValue(newFieldValue);
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<LocalDate> getType() {
		return LocalDate.class;
	}
	
	
	/**
	 * @see com.vaadin.ui.AbstractField#getConvertedValue()
	 */
	@Override
	public Object getConvertedValue() {
		return getDateValue();
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object)
	 */
	@Override
	public void setInternalValue(LocalDate newFieldValue){
		super.setInternalValue(newFieldValue);
		timeValue = newFieldValue;
		setDateValue(newFieldValue);
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#getInternalValue()
	 */
	@Override
	public LocalDate getInternalValue(){
		return getDateValue();		
	}
	

    /**
     * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#preCommit()
     */
    @Override
	public void preCommit() {
    	showOrHideErrorDateField();
    	shouldHideError = false;
		fireValueChange(false);
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#initField(java.lang.Boolean)
	 */
	@Override
	public void initField(Boolean immediate) {
		setImmediate(immediate);
	}

}
