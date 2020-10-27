package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;

@Data
public class FormationPegase {

	public static final String FIELD_NAME_CODE = "code";
	public static final String FIELD_NAME_LIB = "libelleCourt";
	public static final String FIELD_NAME_LIC = "libelleLong";
	public static final String FIELD_NAME_COD_STR = "codeStructure";
	public static final String FIELD_NAME_LIB_STR = "libStructure";
	public static final String FIELD_NAME_COD_TYP_DIP = "codeTypeDiplome";
	public static final String FIELD_NAME_LIB_TYP_DIP = "libTypeDiplome";

	private String code;

	private String libelleCourt;

	private String libelleLong;

	private String codeStructure;

	private String libStructure;

	private String codeTypeDiplome;

	private String libTypeDiplome;
}
