package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;

@Data
public class Periode {

	private String code;

	private String libelle;

	private String libelleLong;

	private String libelleAffichage;

	private Integer anneeUniversitaire;

	private Boolean active;
}
