package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;

@Data
public class IdentiteParcours {

	private Integer anneeObtentionBac;
	private IdentiteRef typeSerieBac;
	private IdentiteRef premiereSpecialiteBac;
	private IdentiteRef deuxiemeSpecialiteBac;
	private IdentiteRef mentionBac;
	private IdentiteRef paysBac;
	private IdentiteRef departementBac;
	private IdentiteRef etablissementBac;
	private String libelleEtablissementBacEtranger;
}
