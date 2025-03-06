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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.v7.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * ComboBox pour les Types de Decision
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class ComboBoxTypeDecision extends RequiredComboBox<TypeDecision> {

	private BeanItemContainer<TypeDecision> container = new BeanItemContainer<>(TypeDecision.class);
	private List<TypeDecision> listeTypDec = new ArrayList<>();
	private String error;

	public ComboBoxTypeDecision(final List<TypeDecision> listeTypDec, final String error) {
		super(true);
		this.error = error;
		setContainerDataSource(container);
		setTypeDecisions(listeTypDec);
	}

	public ComboBoxTypeDecision(final String error) {
		super(true);
		this.error = error;
		setContainerDataSource(container);
	}

	public void setTypeDecisions(final List<TypeDecision> listeTypDec) {
		this.listeTypDec = listeTypDec;
		container.removeAllItems();
		container.addAll(listeTypDec);
	}

	/**
	 * Filtre le container
	 *
	 * @param typeAvis
	 */
	public void filterListValue(final TypeAvis typeAvis) {
		container.removeAllItems();
		List<TypeDecision> newList = listeTypDec.stream().filter(e -> e.getTypeAvis().equals(typeAvis)).collect(Collectors.toList());
		container.addAll(newList);
	}

	/*
	 * (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object)
	 */
	@Override
	public void setInternalValue(final Object newValue) throws ReadOnlyException {
		if (newValue != null && newValue instanceof TypeDecision) {
			TypeDecision td = (TypeDecision) newValue;
			if (td.getTesTypDec()) {
				super.setInternalValue(newValue);
			} else {
				super.setInternalValue(null);
			}
		} else {
			super.setInternalValue(newValue);
		}
	}

	/**
	 * SI la box n'est pas utilisé ou utilisé
	 *
	 * @param need
	 * @param typeDecision
	 */
	public void setBoxNeeded(final Boolean need, final TypeDecision typeDecision) {
		if (need) {
			this.setVisible(true);
			this.setRequired(true);
			this.setRequiredError(error);
			this.setNullSelectionAllowed(false);
			if (typeDecision != null) {
				setValue(typeDecision);
			}
		} else {
			this.setVisible(false);
			this.setRequired(false);
			this.setRequiredError(null);
			this.setNullSelectionAllowed(true);
			this.setValue(null);
		}
	}
}
