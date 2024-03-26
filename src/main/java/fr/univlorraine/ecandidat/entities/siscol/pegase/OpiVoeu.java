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

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class OpiVoeu {

	@CsvBindByName(column = "numero_candidat")
	private String numeroCandidat;

	@CsvBindByName(column = "origine_admission")
	private String origine_admission;

	@CsvBindByName(column = "voieAdmission")
	private String voie_admission;

	@CsvBindByName(column = "annee_concours")
	private String anneeConcours;

	@CsvBindByName(column = "code_voeu")
	private String codeVoeu;

	@CsvBindByName(column = "code_periode")
	private String codePeriode;

	@CsvBindByName(column = "code_formation_psup")
	private String codeFormationPsup;

	@CsvBindByName(column = "code_sise")
	private String codeSise;

	@CsvBindByName(column = "code_etablissement_affectation")
	private String codeEtablissementAffectation;

}
