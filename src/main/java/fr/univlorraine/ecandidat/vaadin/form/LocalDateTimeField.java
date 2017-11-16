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
import java.time.LocalTime;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/** Champs de LocalDateTime customisé, 2 native select pour heure et minute + dateField
 * @author Kevin Hergalant
 *
 */
public class LocalDateTimeField extends CustomField<LocalDateTime> implements IRequiredField{

	/** serialVersionUID **/
	private static final long serialVersionUID = -2049478064097231638L;
	
	private DateField dateField;
	private NativeSelect hourNs;
	private NativeSelect minuteNs;
	private HorizontalLayout hlContent;
	private LocalDateTime timeValue;
	
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
    public LocalDateTimeField(Property<LocalDateTime> dataSource) {
    	setPropertyDataSource(dataSource);
    }
	
	public LocalDateTimeField() {
		dateField = new DateField();
		dateField.addValueChangeListener(e->showOrHideErrorDateField());
		hourNs = new NativeSelect();
		hourNs.addValueChangeListener(e->fireValueChange(false));
		minuteNs = new NativeSelect();
		minuteNs.addValueChangeListener(e->fireValueChange(false));
		hlContent = new HorizontalLayout();
		//hlContent.setSizeFull();
		hlContent.setSpacing(true);
		
		dateField.setImmediate(true);
		hlContent.addComponent(dateField);
		
		for (Integer i = 0; i<24; i++){
			hourNs.addItem(i);
			hourNs.setItemCaption(i, MethodUtils.getLabelMinuteHeure(i));
		}
		hourNs.setNullSelectionAllowed(false);
		hourNs.setImmediate(true);
		hourNs.setValue(0);
		for (Integer i = 0; i<60; i++){
			minuteNs.addItem(i);
			minuteNs.setItemCaption(i, MethodUtils.getLabelMinuteHeure(i));
		}
		minuteNs.setNullSelectionAllowed(false);
		minuteNs.setImmediate(true);
		minuteNs.setValue(0);
		hlContent.addComponent(hourNs);
		hlContent.setComponentAlignment(hourNs, Alignment.MIDDLE_LEFT);
		Label label1 = new Label(":");
		hlContent.addComponent(label1);
		hlContent.setComponentAlignment(label1, Alignment.MIDDLE_LEFT);
		hlContent.addComponent(minuteNs);
		hlContent.setComponentAlignment(minuteNs, Alignment.MIDDLE_LEFT);
		Label label2 = new Label("(HH:MM)");
		hlContent.addComponent(label2);
		hlContent.setComponentAlignment(label2, Alignment.MIDDLE_LEFT);
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
		setDateTimeValue(timeValue);		
		return hlContent;
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return hourNs.getValue()==null || minuteNs.getValue()==null || dateField.getValue()==null;
	}
	
	/**
	 * @return l'heure saisie
	 */
	/**
	 * @return
	 */
	private LocalDateTime getDateTimeValue(){
		if (hourNs.getValue()==null || minuteNs.getValue()==null || dateField.getValue()==null){
			return null;
		}else{
			LocalDate date = MethodUtils.convertDateToLocalDate(dateField.getValue());
			LocalTime time = LocalTime.of((Integer)hourNs.getValue(), (Integer)minuteNs.getValue());
			return LocalDateTime.of(date, time);
		}
	}
	
	/**
	 * @param newFieldValue
	 */
	private void setDateTimeValue(LocalDateTime newFieldValue){
		if (newFieldValue==null){
			hourNs.setValue(0);
			minuteNs.setValue(0);
			dateField.setValue(null);
		}else{
			hourNs.setValue(newFieldValue.getHour());
			minuteNs.setValue(newFieldValue.getMinute());
			LocalDate localDate= LocalDate.of(newFieldValue.getYear(), newFieldValue.getMonth(), newFieldValue.getDayOfMonth());
			dateField.setValue(MethodUtils.convertLocalDateToDate(localDate));
		}
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#getValue()
	 */
	@Override
	public LocalDateTime getValue() {	
		return getDateTimeValue();
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(LocalDateTime newFieldValue) throws ReadOnlyException,
			ConversionException {		
		super.setValue(newFieldValue);
		timeValue = newFieldValue;
		setDateTimeValue(newFieldValue);
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<LocalDateTime> getType() {
		return LocalDateTime.class;
	}
	
	
	/**
	 * @see com.vaadin.ui.AbstractField#getConvertedValue()
	 */
	@Override
	public Object getConvertedValue() {
		return getDateTimeValue();
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object)
	 */
	@Override
	public void setInternalValue(LocalDateTime newFieldValue){
		super.setInternalValue(newFieldValue);
		timeValue = newFieldValue;
		setDateTimeValue(newFieldValue);
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#getInternalValue()
	 */
	@Override
	public LocalDateTime getInternalValue(){
		return getDateTimeValue();		
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
