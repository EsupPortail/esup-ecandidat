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

import java.time.format.DateTimeFormatter;

import com.opencsv.bean.CsvBindByName;

import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import lombok.Data;

@Data
public class OpiCandidat {

	/* Données compte à minima */
	@CsvBindByName(column = "numero_candidat")
	private String numeroCandidat;

	/* Données candidat */
	@CsvBindByName(column = "sexe")
	private String sexe;

	@CsvBindByName(column = "nom_famille")
	private String nomFamille;

	@CsvBindByName(column = "nom_usuel")
	private String nomUsuel;

	@CsvBindByName(column = "prenom")
	private String prenom;

	@CsvBindByName(column = "prenom2")
	private String prenom2;

	@CsvBindByName(column = "prenom3")
	private String prenom3;

	@CsvBindByName(column = "date_naissance")
	private String dateNaissance;

	@CsvBindByName(column = "code_pays_naissance")
	private String codePaysNaissance;

	@CsvBindByName(column = "code_commune_naissance")
	private String codeCommuneNaissance;

	@CsvBindByName(column = "libelle_commune_naissance")
	private String libelleCommuneNaissance;

	@CsvBindByName(column = "code_nationalite")
	private String codeNationalite;

	@CsvBindByName(column = "ine")
	private String ine;

	/* Données adresse */
	@CsvBindByName(column = "adresse_code_pays")
	private String adresseCodePays;

	@CsvBindByName(column = "adresse_ligne1_etage")
	private String adresseLigne1Etage;

	@CsvBindByName(column = "adresse_ligne2_batiment")
	private String adresseLigne2Batiment;

	@CsvBindByName(column = "adresse_ligne3_voie")
	private String adresseLigne3Voie;

	@CsvBindByName(column = "adresse_ligne4_complement")
	private String adresseLigne4Complement;

	@CsvBindByName(column = "adresse_code_postal")
	private String adresseCodePostal;

	@CsvBindByName(column = "adresse_code_commune")
	private String adresseCodeCommune;

	@CsvBindByName(column = "adresse_cp_ville_etranger")
	private String adresseCpVilleEtranger;

	@CsvBindByName(column = "numero_telephone1")
	private String numeroTelephone1;

	@CsvBindByName(column = "numero_telephone2")
	private String numeroTelephone2;

	@CsvBindByName(column = "courriel")
	private String courriel;

	public OpiCandidat(final Candidat candidat, final DateTimeFormatter formatterDate) {
		super();
		this.numeroCandidat = candidat.getCompteMinima().getNumDossierOpiCptMin();
		this.sexe = candidat.getCivilite().getCodSexe();
		this.nomFamille = candidat.getNomPatCandidat();
		this.nomUsuel = candidat.getNomUsuCandidat();
		this.prenom = candidat.getPrenomCandidat();
		this.prenom2 = candidat.getAutrePrenCandidat();
		this.prenom3 = null;
		this.dateNaissance = formatterDate.format(candidat.getDatNaissCandidat());
		this.codePaysNaissance = candidat.getSiScolPaysNaiss() != null ? candidat.getSiScolPaysNaiss().getId().getCodPay() : null;
		this.codeCommuneNaissance = candidat.getSiScolCommuneNaiss() != null ? candidat.getSiScolCommuneNaiss().getId().getCodComNaiss() : null;
		this.libelleCommuneNaissance = candidat.getSiScolCommuneNaiss() != null ? candidat.getSiScolCommuneNaiss().getLibComNaiss() : candidat.getLibVilleNaissCandidat();
		this.codeNationalite = candidat.getSiScolPaysNat() != null ? candidat.getSiScolPaysNat().getId().getCodPay() : null;
		this.ine = (candidat.getIneCandidat() != null && candidat.getCleIneCandidat() != null) ? candidat.getIneCandidat() + candidat.getCleIneCandidat() : null;

		final Adresse adr = candidat.getAdresse();
		this.adresseCodePays = adr.getSiScolPays() != null ? adr.getSiScolPays().getId().getCodPay() : null;
		this.adresseLigne1Etage = adr.getAdr2Adr();
		this.adresseLigne2Batiment = adr.getAdr3Adr();
		this.adresseLigne3Voie = adr.getAdr1Adr();
		this.adresseLigne4Complement = null;
		this.adresseCodePostal = adr.getCodBdiAdr();
		this.adresseCodeCommune = adr.getSiScolCommune() != null ? adr.getSiScolCommune().getId().getCodCom() : null;
		this.adresseCpVilleEtranger = null;
		this.numeroTelephone1 = candidat.getTelCandidat();
		this.numeroTelephone2 = candidat.getTelPortCandidat();
		this.courriel = candidat.getCompteMinima().getMailPersoCptMin();
	}

}
