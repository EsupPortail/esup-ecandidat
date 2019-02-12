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

import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusInterne;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 *
 * @author Kevin Hergalant
 */
@Data
@SuppressWarnings("serial")
public class ExportDossierCursusInterne implements Serializable {

	private String annee;
	private String code;
	private String formation;
	private String resultat;
	private String mention;

	public ExportDossierCursusInterne() {
		super();
	}

	public ExportDossierCursusInterne(final CandidatCursusInterne cursus) {
		if (cursus != null) {
			if (cursus.getAnneeUnivCursusInterne() != null) {
				this.annee = String.valueOf(cursus.getAnneeUnivCursusInterne());
			} else {
				this.annee = "";
			}
			this.code = MethodUtils.formatToExport(cursus.getCodVetCursusInterne());
			this.formation = MethodUtils.formatToExport(cursus.getLibCursusInterne());
			if (cursus.getSiScolTypResultat() != null) {
				this.resultat = cursus.getSiScolTypResultat().getLibTre();
			} else {
				this.resultat = "";
			}
			if (cursus.getSiScolMention() != null) {
				this.mention = cursus.getSiScolMention().getLibMen();
			} else {
				this.mention = "";
			}
		} else {
			this.annee = "";
			this.code = "";
			this.formation = "";
			this.resultat = "";
			this.mention = "";
		}
	}
}
