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

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * @author Kevin Hergalant
 */
@Data
@SuppressWarnings("serial")
public class ExportDossierBac implements Serializable {

	private String annee;
	private String serie;
	private String mention;
	private String pays;
	private String departement;
	private String commune;
	private String etablissement;
	/* Specialité / Options */
	private String specialiteTer1;
	private String specialiteTer2;
	private String specialitePre;
	private String option1;
	private String option2;
	private String option3;
	private String option4;

	private Boolean affichageSpeOpt = false;

	public ExportDossierBac() {
		super();
	}

	public ExportDossierBac(final Candidat candidat) {
		final CandidatBacOuEqu bac = candidat.getCandidatBacOuEqu();
		if (bac != null) {
			if (bac.getAnneeObtBac() != null) {
				this.annee = String.valueOf(bac.getAnneeObtBac());
			} else {
				this.annee = "";
			}
			if (bac.getSiScolBacOuxEqu() != null) {
				this.serie = bac.getSiScolBacOuxEqu().getLibBac();
			} else {
				this.serie = "";
			}
			if (bac.getSiScolMentionNivBac() != null) {
				this.mention = bac.getSiScolMentionNivBac().getLibMnb();
			} else {
				this.mention = "";
			}
			if (bac.getSiScolPays() != null) {
				this.pays = bac.getSiScolPays().getLibPay();
			} else {
				this.pays = "";
			}
			if (bac.getSiScolDepartement() != null) {
				this.departement = bac.getSiScolDepartement().getLibDep();
			} else {
				this.departement = "";
			}
			if (bac.getSiScolCommune() != null) {
				this.commune = bac.getSiScolCommune().getLibCom();
			} else {
				this.commune = "";
			}
			if (bac.getSiScolEtablissement() != null) {
				this.etablissement = bac.getSiScolEtablissement().getLibEtb();
			} else {
				this.etablissement = "";
			}

			if (bac.getSiScolSpe1BacTer() != null) {
				this.specialiteTer1 = bac.getSiScolSpe1BacTer().getLibSpeBac();
				affichageSpeOpt = true;
			} else {
				this.specialiteTer1 = "";
			}
			if (bac.getSiScolSpe2BacTer() != null) {
				this.specialiteTer2 = bac.getSiScolSpe2BacTer().getLibSpeBac();
				affichageSpeOpt = true;
			} else {
				this.specialiteTer2 = "";
			}
			if (bac.getSiScolSpeBacPre() != null) {
				this.specialitePre = bac.getSiScolSpeBacPre().getLibSpeBac();
				affichageSpeOpt = true;
			} else {
				this.specialitePre = "";
			}

			if (bac.getSiScolOpt1Bac() != null) {
				this.option1 = bac.getSiScolOpt1Bac().getLibOptBac();
				affichageSpeOpt = true;
			} else {
				this.option1 = "";
			}
			if (bac.getSiScolOpt2Bac() != null) {
				this.option2 = bac.getSiScolOpt2Bac().getLibOptBac();
				affichageSpeOpt = true;
			} else {
				this.option2 = "";
			}
			if (bac.getSiScolOpt3Bac() != null) {
				this.option3 = bac.getSiScolOpt3Bac().getLibOptBac();
				affichageSpeOpt = true;
			} else {
				this.option3 = "";
			}
			if (bac.getSiScolOpt4Bac() != null) {
				this.option4 = bac.getSiScolOpt4Bac().getLibOptBac();
				affichageSpeOpt = true;
			} else {
				this.option4 = "";
			}
		} else {
			this.annee = "";
			this.serie = "";
			this.mention = "";
			this.pays = "";
			this.departement = "";
			this.commune = "";
			this.etablissement = "";
			this.specialiteTer1 = "";
			this.specialiteTer2 = "";
			this.specialitePre = "";
			this.option1 = "";
			this.option2 = "";
			this.option3 = "";
			this.option4 = "";
		}
	}
}
