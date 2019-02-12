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

import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPro;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * 
 * @author Kevin Hergalant
 */
@Data
@SuppressWarnings("serial")
public class ExportDossierCursusPro implements Serializable {

	private String annee;
	private String duree;
	private String organisme;
	private String intitule;
	private String objectif;

	public ExportDossierCursusPro() {
		super();
	}

	public ExportDossierCursusPro(final CandidatCursusPro cursus) {
		this.annee = String.valueOf(cursus.getAnneeCursusPro());
		this.duree = MethodUtils.formatToExport(cursus.getDureeCursusPro());
		this.organisme = MethodUtils.formatToExport(cursus.getOrganismeCursusPro());
		this.intitule = MethodUtils.formatToExport(cursus.getIntituleCursusPro());
		this.objectif = MethodUtils.formatToExport(cursus.getObjectifCursusPro());
	}
}
