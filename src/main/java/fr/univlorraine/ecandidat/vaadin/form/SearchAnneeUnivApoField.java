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

import com.vaadin.v7.data.util.converter.Converter.ConversionException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.TextField;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.views.windows.SearchAnneeUnivApoWindow;


/**
 * Champs de recherche d'annee univ
 * @author Kevin Hergalant
 *
 */
public class SearchAnneeUnivApoField extends CustomField<String> implements IRequiredField{

	/** serialVersionUID **/
	private static final long serialVersionUID = 3994791458997281136L;
	
	/*Variable pour le champs et les msg d'erreur*/
	private boolean shouldHideError = true;
	private String requieredError;
	
	private HorizontalLayout layout;
	private TextField anneeField;
	private OneClickButton btnSearch;
	
	/**
	 * Constructeur, initialisation du champs
	 * @param libelleBtnFind 
	 */
	public SearchAnneeUnivApoField(String libelleBtnFind) {
		super();
		layout = new HorizontalLayout();
		layout.setSpacing(true);
		anneeField = new TextField();
		anneeField.addValueChangeListener(e->showOrHideError());
		anneeField.setNullRepresentation("");
		anneeField.setReadOnly(true);
		btnSearch = new OneClickButton(libelleBtnFind,FontAwesome.SEARCH);
		btnSearch.addClickListener(e->{
			SearchAnneeUnivApoWindow window = new SearchAnneeUnivApoWindow();
			window.addAnneeUniListener(a->changeFieldValue(a));
			UI.getCurrent().addWindow(window);
		});
		layout.addComponent(anneeField);
		layout.addComponent(btnSearch);
	}	
	
	

	/* (non-Javadoc)
	 * @see com.vaadin.ui.AbstractComponent#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		btnSearch.setVisible(enabled);
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
		anneeField.setReadOnly(false);
		anneeField.setValue(value);
		anneeField.setReadOnly(true);
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
			if (anneeField.getValue()==null){
				anneeField.addStyleName(StyleConstants.FIELD_ERROR);
			}else{
				anneeField.removeStyleName(StyleConstants.FIELD_ERROR);
			}
		}		
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		return anneeField.getValue();
	}

	/**
	 * @see com.vaadin.ui.AbstractField#getInternalValue()
	 */
	@Override
	protected String getInternalValue() {
		return anneeField.getValue();
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
		return anneeField.getValue()==null || anneeField.getValue().equals("");
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
	
}
