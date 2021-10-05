package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;

@Data
public class ApprenantBac {

	private String serie;
	private String anneeObtention;
	private String mention;
	private String etablissement;
	private String ine;
	private String pays;
	private String premiereSpecialiteBac;
	private String deuxiemeSpecialiteBac;
}
