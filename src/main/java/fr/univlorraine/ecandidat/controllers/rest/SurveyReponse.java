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
package fr.univlorraine.ecandidat.controllers.rest;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Classe d'objet de reponse de LimeSurvey
 * @author Kevin Hergalant
 */

@Data
@EqualsAndHashCode(of = { "numDossier", "idCandidature" })
@ToString(of = { "numDossier", "startlanguage", "submitdate" })
public class SurveyReponse {
	private String id;
	private String submitdate;
	private String lastpage;
	private String startlanguage;
	private String startdate;
	private String datestamp;
	private String numDossier;
	private String idCandidature;
	private LinkedHashMap<String, Object> mapReponses;

	@JsonAnySetter
	public void handleUnknown(final String key, final Object value) {
		mapReponses.put(key, value);
	}

	public SurveyReponse() {
		super();
		mapReponses = new LinkedHashMap<>();
	}
}
