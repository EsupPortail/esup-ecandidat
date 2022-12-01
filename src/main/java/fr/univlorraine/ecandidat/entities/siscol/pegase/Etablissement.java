package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;

@Data
public class Etablissement {

	private String numeroUai;
	private String patronymeUai;
	private String libelleAffichage;
	private String prioriteAffichage;
	private Boolean temoinVisible;
	private Boolean temoinLivre;
	private String commune;
	private TypeUai typeUai;
	private Departement departement;
}
