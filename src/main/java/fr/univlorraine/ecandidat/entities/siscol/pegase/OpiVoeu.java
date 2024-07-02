package fr.univlorraine.ecandidat.entities.siscol.pegase;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class OpiVoeu {

	@CsvBindByName(column = "numero_candidat")
	private String numeroCandidat;

	@CsvBindByName(column = "origine_admission")
	private String origineAdmission;

	@CsvBindByName(column = "voie_admission")
	private String voieAdmission;

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
