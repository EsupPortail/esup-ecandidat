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

import java.text.DecimalFormat;
import java.time.LocalDate;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/** ComboBox pour les jours ou mois ou année
 *
 * @author Kevin Hergalant */
public class ComboBoxJourMoisAnnee extends RequiredComboBox<String> {

	/** serialVersionUID **/
	private static final long serialVersionUID = 8150654585870434557L;

	public ComboBoxJourMoisAnnee() {
		super(false);
		setNullSelectionAllowed(true);
	}

	/** Modifie le type de nativeselect soit jour soit mois soit année
	 *
	 * @param type
	 */
	public void changeTypeNativeSelect(final Integer type) {
		if (type == ConstanteUtils.TYPE_JOUR) {
			for (Integer i = 1; i < 32; i++) {
				addItem(i);
			}
		} else if (type == ConstanteUtils.TYPE_MOIS) {
			for (Integer i = 0; i < 12; i++) {
				addItem(i + 1);
				setItemCaption(i + 1, ConstanteUtils.NOM_MOIS_LONG[i]);
			}
		} else if (type == ConstanteUtils.TYPE_ANNEE) {
			DecimalFormat myFormatter = new DecimalFormat("####");
			for (Integer i = 0; i < 3; i++) {
				int year = LocalDate.now().getYear() + i;
				addItem(year);
				setItemCaption(year, myFormatter.format(year));
			}
		}
	}
}
