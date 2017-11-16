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

import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les MotivationAvis
 * @author Kevin Hergalant
 *
 */
public class ComboBoxMotivationAvis extends RequiredComboBox<MotivationAvis>{	
	/** serialVersionUID **/
	private static final long serialVersionUID = -3823108837648597992L;
	
	private BeanItemContainer<MotivationAvis> container;
	private String error;
	
	
	public ComboBoxMotivationAvis(List<MotivationAvis> listeMotivation,String error) {
		super(true);
		this.error = error;
		container = new BeanItemContainer<MotivationAvis>(MotivationAvis.class,listeMotivation);
		setContainerDataSource(container);
	}
	
	/** SI la box n'est pas utilisé ou utilisé
	 * @param need
	 * @param motiv 
	 */
	public void setBoxNeeded(Boolean need, MotivationAvis motiv){
		if (need){
			this.setVisible(true);
			this.setRequired(true);
			this.setRequiredError(error);
			this.setNullSelectionAllowed(false);
			if (motiv!=null){
				setValue(motiv);
			}
		}else{
			this.setVisible(false);
			this.setRequired(false);
			this.setRequiredError(null);
			this.setNullSelectionAllowed(true);
			this.setValue(null);
		}
	}
}