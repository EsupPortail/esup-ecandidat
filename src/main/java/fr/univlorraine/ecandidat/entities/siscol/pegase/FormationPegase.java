package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;

@Data
public class FormationPegase {

	public static final String FIELD_NAME_CODE = "code";
	public static final String FIELD_NAME_LIB = "libelle";
	public static final String FIELD_NAME_LIBL = "libelleLong";
	public static final String FIELD_NAME_ESPACEL = "espaceLibelle";

	private String id;

	private String code;

	private String libelle;

	private String libelleLong;

	private String espaceLibelle;

}
