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

import java.time.LocalTime;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;

import fr.univlorraine.ecandidat.utils.MethodUtils;

/** Champs de LocalTime customisé, 2 native select pour heure et minute
 *
 * @author Kevin Hergalant */
public class LocalTimeField extends CustomField<LocalTime> implements IRequiredField {

	/** serialVersionUID **/
	private static final long serialVersionUID = -2049478064097231638L;

	private NativeSelect hourNs;
	private NativeSelect minuteNs;
	private HorizontalLayout hlContent;
	private LocalTime timeValue;

	private boolean shouldHideError = true;

	/** @see com.vaadin.ui.AbstractField#shouldHideErrors() */
	@Override
	protected boolean shouldHideErrors() {
		Boolean hide = shouldHideError;
		shouldHideError = false;
		return hide;
	}

	/** @param context
	 */
	public void init(final String context) {
	}

	public LocalTimeField() {
		hourNs = new NativeSelect();
		minuteNs = new NativeSelect();
		hlContent = new HorizontalLayout();
		// hlContent.setSizeFull();
		hlContent.setSpacing(true);
		for (Integer i = 0; i < 24; i++) {
			hourNs.addItem(i);
			hourNs.setItemCaption(i, MethodUtils.getLabelMinuteHeure(i));
		}
		hourNs.setNullSelectionAllowed(false);
		hourNs.setImmediate(true);
		for (Integer i = 0; i < 60; i++) {
			minuteNs.addItem(i);
			minuteNs.setItemCaption(i, MethodUtils.getLabelMinuteHeure(i));
		}
		minuteNs.setNullSelectionAllowed(false);
		minuteNs.setImmediate(true);
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

	/** @see com.vaadin.ui.CustomField#initContent() */
	@Override
	protected Component initContent() {
		setDateTimeValue(timeValue);
		return hlContent;
	}

	/** @return l'heure saisie */
	private LocalTime getDateTimeValue() {
		if (hourNs.getValue() == null || minuteNs.getValue() == null) {
			return null;
		} else {
			return LocalTime.of((Integer) hourNs.getValue(), (Integer) minuteNs.getValue());
		}
	}

	/** @param newFieldValue
	 */
	private void setDateTimeValue(final LocalTime newFieldValue) {
		if (newFieldValue == null) {
			hourNs.setValue(null);
			minuteNs.setValue(null);
		} else {
			hourNs.setValue(newFieldValue.getHour());
			minuteNs.setValue(newFieldValue.getMinute());
		}
	}

	/** @see com.vaadin.ui.AbstractField#getValue() */
	@Override
	public LocalTime getValue() {
		return getDateTimeValue();
	}

	/** @see com.vaadin.ui.AbstractField#setValue(java.lang.Object) */
	@Override
	public void setValue(final LocalTime newFieldValue) throws ReadOnlyException,
			ConversionException {
		timeValue = newFieldValue;
		setDateTimeValue(newFieldValue);
	}

	/** @see com.vaadin.ui.AbstractField#getType() */
	@Override
	public Class<LocalTime> getType() {
		return LocalTime.class;
	}

	/** @see com.vaadin.ui.AbstractField#getConvertedValue() */
	@Override
	public Object getConvertedValue() {
		return getDateTimeValue();
	}

	/** @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object) */
	@Override
	public void setInternalValue(final LocalTime newFieldValue) {
		timeValue = newFieldValue;
		setDateTimeValue(newFieldValue);
	}

	/** @see com.vaadin.ui.AbstractField#getInternalValue() */
	@Override
	public LocalTime getInternalValue() {
		return getDateTimeValue();
	}

	/** @param dataSource
	 */
	public LocalTimeField(final Property<LocalTime> dataSource) {
		setPropertyDataSource(dataSource);
	}

	/** @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#preCommit() */
	@Override
	public void preCommit() {
		shouldHideError = false;
		fireValueChange(false);
	}

	/** @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#initField(java.lang.Boolean) */
	@Override
	public void initField(final Boolean immediate) {
		setImmediate(immediate);
	}

	/** @param height
	 */
	public void setHeightLayout(final float height) {
		hlContent.setHeight(height, Unit.PIXELS);
	}
}
