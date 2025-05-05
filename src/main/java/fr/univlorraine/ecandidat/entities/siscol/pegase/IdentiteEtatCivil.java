package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.time.LocalDate;

import lombok.Data;

@Data
public class IdentiteEtatCivil {

	private String sexe;
	private LocalDate dateNaissance;
	private String nomUsage;
	private String prenom2;
	private String prenom3;
	private IdentiteRef communeNaissance;
	private String libelleCommuneNaissanceEtranger;
	private IdentiteRef paysNaissance;
	private IdentiteRef nationalite;

}
