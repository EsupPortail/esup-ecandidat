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
package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InscriptionIns {

	/* Données inscription */
	private String code;
	private String libelleCourt;
	private String libelleLong;

	/* Données formation */
	private String codeFormation;
	private String libelleCourtFormation;
	private String libelleLongFormation;

	/* Données periode */
	private String codePeriode;
	private Integer anneeUnivPeriode;

	@SuppressWarnings("unchecked")
	@JsonProperty("cible")
	private void unpackNested(final Map<String, Object> cible) {
		if (cible == null) {
			return;
		}
		this.code = (String) cible.get("code");
		this.libelleCourt = (String) cible.get("libelleCourt");
		this.libelleLong = (String) cible.get("libelleLong");
		/* Formation */
		final Map<String, Object> formation = (Map<String, Object>) cible.get("formation");
		if (formation != null) {
			this.codeFormation = (String) formation.get("code");
			this.libelleCourtFormation = (String) formation.get("libelleCourt");
			this.libelleLongFormation = (String) formation.get("libelleLong");
		}
		/* Période */
		final Map<String, Object> periode = (Map<String, Object>) cible.get("periode");
		if (periode != null) {
			this.codePeriode = (String) periode.get("code");
			this.anneeUnivPeriode = (Integer) periode.get("anneeUniversitaire");
		}
	}

	public String getAnneeUniv() {
		if (getAnneeUnivPeriode() != null) {
			return String.valueOf(getAnneeUnivPeriode());
		}
		return null;
	}

	/**
	 * @return le code de chemin
	 */
	public String getCodeChemin() {
		if (codeFormation != null) {
			return codeFormation + ">" + code;
		} else {
			return code;
		}
	}
}
