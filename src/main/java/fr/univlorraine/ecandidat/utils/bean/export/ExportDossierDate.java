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
package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les dates pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierDate implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 6786025518369323993L;

	private String dateRetour;
	private String dateConfirmation;
	private String dateJury;
	private String datePublication;
	
	public ExportDossierDate(String datRetour, String datConfirmation,
			String datJury, String datPubli) {
		super();
		this.dateRetour = MethodUtils.formatToExport(datRetour);
		this.dateConfirmation = MethodUtils.formatToExport(datConfirmation);
		this.dateJury = MethodUtils.formatToExport(datJury);
		this.datePublication = MethodUtils.formatToExport(datPubli);
	}

	public ExportDossierDate() {
		super();
	}	
	
}
