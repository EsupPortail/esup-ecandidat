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

import fr.univlorraine.ecandidat.entities.ecandidat.CandidatStage;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierStage implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 6371858823552443506L;

	private String annee;
	private String duree;
	private String organisme;
	private String descriptif;
	private String quotite;
	
	public ExportDossierStage() {
		super();
	}

	public ExportDossierStage(CandidatStage stage) {
		this.annee = String.valueOf(stage.getAnneeStage());
		this.duree = MethodUtils.formatToExport(stage.getDureeStage());
		this.organisme = MethodUtils.formatToExport(stage.getOrganismeStage());
		this.descriptif = MethodUtils.formatToExport(stage.getDescriptifStage());
		this.quotite = stage.getNbHSemStage()!=null?String.valueOf(stage.getNbHSemStage()):"";
	}
}
