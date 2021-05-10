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

	@CsvBindByName(column = "annee_universitaire")
	private String anneeUniversitaire;

	@CsvBindByName(column = "code_formation_psup")
	private String codeFormationPsup;

	@CsvBindByName(column = "code_sise")
	private String codeSise;

}
