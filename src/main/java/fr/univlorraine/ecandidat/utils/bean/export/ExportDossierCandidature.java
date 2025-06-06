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

import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import lombok.Data;

/**
 * Objet contenant les infos d'une candidature pour l'export
 * @author Kevin Hergalant
 */
@Data
@SuppressWarnings("serial")
public class ExportDossierCandidature implements Serializable {

	private String campagne;
	private String commission;
	private String adresseCommission;
	private String mailCommission;
	private String telCommission;
	private String urlCommission;
	private String commentaireRetour;
	private String libelleFormation;
	private String codeFormation;

	public ExportDossierCandidature() {
		super();
	}

	public ExportDossierCandidature(final String campagne, final String commission, final String adresseCommission, final String mailCommission, final String telCommission, final String urlCommission,
		final Formation formation, final String commentaireRetour) {
		super();
		this.campagne = campagne;
		this.commission = commission;
		this.adresseCommission = adresseCommission;
		this.mailCommission = mailCommission;
		this.telCommission = telCommission;
		this.urlCommission = urlCommission;
		this.codeFormation = formation.getCodForm();
		this.libelleFormation = formation.getLibForm();
		this.commentaireRetour = commentaireRetour;
	}
}
