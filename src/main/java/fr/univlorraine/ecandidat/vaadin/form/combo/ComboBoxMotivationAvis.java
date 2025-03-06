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

import com.vaadin.v7.data.util.BeanItemContainer;

import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis_;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * ComboBox pour les MotivationAvis
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class ComboBoxMotivationAvis extends RequiredComboBox<MotivationAvis> {

	private BeanItemContainer<MotivationAvis> container = new BeanItemContainer<>(MotivationAvis.class);
	private String error;

	public ComboBoxMotivationAvis(final List<MotivationAvis> listeMotivation, final String error) {
		super(true);
		this.error = error;
		setContainerDataSource(container);
		setMotivationAvis(listeMotivation);
	}

	public ComboBoxMotivationAvis(final String error) {
		super(true);
		this.error = error;
		setContainerDataSource(container);
	}

	public void setMotivationAvis(final List<MotivationAvis> listeMotivation) {
		container.removeAllItems();
		container.addAll(listeMotivation);
		container.sort(new Object[] { MotivationAvis_.codMotiv.getName() }, new boolean[] { true });
	}

	/**
	 * SI la box n'est pas utilisé ou utilisé
	 * @param need
	 * @param motiv
	 */
	public void setBoxNeeded(final Boolean need, final MotivationAvis motiv) {
		if (need) {
			setVisible(true);
			setRequired(true);
			setRequiredError(error);
			setNullSelectionAllowed(false);
			if (motiv != null) {
				setValue(motiv);
			}
		} else {
			setVisible(false);
			setRequired(false);
			setRequiredError(null);
			setNullSelectionAllowed(true);
			this.setValue(null);
		}
	}
}
