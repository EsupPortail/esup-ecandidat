package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;

@Data
public class ApprenantContact {

	private ApprenantDemandeContact demandeDeContact;

	private String canalCommunication;
	private String proprietaire;
	private String pays;
	private String ligne1OuEtage;
	private String ligne2OuBatiment;
	private String ligne3OuVoie;
	private String ligne4OuComplement;
	private String ligne5Etranger;
	private String codePostal;
	private String commune;

	private String telephone;

	private String mail;
}
