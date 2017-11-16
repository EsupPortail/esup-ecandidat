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
import com.vaadin.server.Page;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;


/**
 * Champs color picker
 * @author Kevin Hergalant
 *
 */
public class RequiredColorPickerField extends CustomField<String> implements IRequiredField{
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 3425366805659728823L;
	
	/*Variable pour le champs et les msg d'erreur*/
	private boolean shouldHideError = true;
	private String requieredError;
	
	private HorizontalLayout layout;
	private TextField colorTextField;
	private ColorPicker btnColor;
	//private String color;
	
	/**
	 * Constructeur, initialisation du champs
	 */
	public RequiredColorPickerField(String caption) {
		super();
		layout = new HorizontalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setSpacing(true);
		colorTextField = new TextField();
		colorTextField.addValueChangeListener(e->showOrHideError());
		colorTextField.setNullRepresentation("");
		colorTextField.addStyleName(ValoTheme.TEXTFIELD_BORDERLESS);
		colorTextField.setReadOnly(true);

		btnColor = new ColorPicker("Couleur de l'alerte");
		btnColor.addColorChangeListener(e->{
			changeFieldValue(e.getColor().getCSS());
		});
		btnColor.setPosition(Page.getCurrent().getBrowserWindowWidth() / 2 - 246/2,
                Page.getCurrent().getBrowserWindowHeight() / 2 - 507/2);
		btnColor.setSwatchesVisibility(true);
		btnColor.setHistoryVisibility(false);
		btnColor.setTextfieldVisibility(true);
		btnColor.setHSVVisibility(false);
		layout.addComponent(btnColor);
		layout.addComponent(colorTextField);
		layout.setExpandRatio(colorTextField, 1);
		
	}	
	

	/**
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {		
		return layout;
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#getType()
	 */
	@Override
	public Class<String> getType() {
		return String.class;
	}
	
	/** Change la valeur
	 * @param value
	 */
	private void changeFieldValue(String value){
		colorTextField.setReadOnly(false);
		//color = value;
		if (value!=null){
			colorTextField.setValue(value);
		}else{
			colorTextField.setValue("");
		}
		
		colorTextField.setReadOnly(true);
	}
	
	public void changeFieldColor(String colorStr) {
		Color color = new Color(Integer.valueOf(colorStr.substring(1, 3), 16),
				Integer.valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr.substring(5, 7), 16));
		btnColor.setColor(color);
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object)
	 */
	@Override
	protected void setInternalValue(String newFieldValue){
		super.setInternalValue(newFieldValue);
		changeFieldValue(newFieldValue);
	}
	
	/**
	 * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(String newFieldValue) throws ReadOnlyException,
			ConversionException {
		super.setInternalValue(newFieldValue);
		changeFieldValue(newFieldValue);
	}
	
	/**
	 * Montre ou cache l'erreur
	 */
	private void showOrHideError(){
		fireValueChange(false);
		if (isRequired()){
			if (colorTextField.getValue()==null || colorTextField.getValue().equals("")){
				colorTextField.addStyleName(StyleConstants.FIELD_ERROR);
				btnColor.addStyleName(StyleConstants.FIELD_ERROR);
			}else{
				colorTextField.removeStyleName(StyleConstants.FIELD_ERROR);
				btnColor.removeStyleName(StyleConstants.FIELD_ERROR);
			}
		}		
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		return colorTextField.getValue();
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getInternalValue()
	 */
	@Override
	protected String getInternalValue() {
		return colorTextField.getValue();
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
	 * @see com.vaadin.ui.AbstractField#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return colorTextField.getValue()==null || colorTextField.getValue().equals("");
	}

	/**
	 * @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#preCommit()
	 */
	@Override
	public void preCommit() {
		showOrHideError();
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
	 * @see com.vaadin.ui.AbstractField#setRequiredError(java.lang.String)
	 */
	@Override
	public void setRequiredError(String requiredMessage) {
		this.requieredError = requiredMessage;
	}


	@Override
	public void setReadOnly(boolean readOnly) {
		super.setReadOnly(readOnly);
		if (readOnly){
			btnColor.setEnabled(false);
		}else{
			btnColor.setEnabled(true);
		}
	}
	
}
