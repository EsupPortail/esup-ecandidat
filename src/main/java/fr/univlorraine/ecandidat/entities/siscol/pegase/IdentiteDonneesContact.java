package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;

@Data
public class IdentiteDonneesContact {

	private String adressePeriodeUPays;
	private String adressePeriodeULigne1Etage;
	private String adressePeriodeULigne2Batiment;
	private String adressePeriodeULigne3Voie;
	private String adressePeriodeULigne4Complement;
	private String adressePeriodeUCodePostal;
	private String adressePeriodeUCodeCommune;
	private String adressePeriodeULigne5Etranger;
	private IdentiteRef adresseFixePays;
	private String adresseFixeLigne1Etage;
	private String adresseFixeLigne2Batiment;
	private String adresseFixeLigne3Voie;
	private String adresseFixeLigne4Complement;
	private String adresseFixeCodePostal;
	private String adresseFixeCodeCommune;
	private String adresseFixeLigne5Etranger;
	private String adresseFixeProprietaire;
	private String telephoneContactUrgence;
	private String telephoneContactUrgenceProprietaire;
	private String adresseElectroniqueInstitutionnelle;
	private String adresseElectroniqueSecours;
	private String adresseElectroniqueSecoursProprietaire;

}
