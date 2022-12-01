package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.List;

import lombok.Data;

@Data
public class Apprenant {

	private String code;
	private ApprenantEtatCivil etatCivil;
	private ApprenantNaissance naissance;
	private ApprenantBac bac;
	private List<ApprenantContact> contacts;
}
