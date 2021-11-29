package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FormationPegase {

	public static final String FIELD_NAME_CODE = "code";
	public static final String FIELD_NAME_LIB = "libelle";
	public static final String FIELD_NAME_LIBC = "libelleCourt";
	public static final String FIELD_NAME_LIBL = "libelleLong";
	public static final String FIELD_NAME_COD_STR = "codeStructure";
	public static final String FIELD_NAME_LIB_STR = "libStructure";
	public static final String FIELD_NAME_COD_TYP_DIP = "codeTypeDiplome";
	public static final String FIELD_NAME_LIB_TYP_DIP = "libTypeDiplome";
	public static final String FIELD_NAME_VERSION = "version";

	private String code;

	private String libelle;

	private String libelleLong;

	@JsonProperty("structure")
	private String codeStructure;

	private String codeTypeDiplome;

	private String libTypeDiplome;

	private Integer version;

	@SuppressWarnings("unchecked")
	@JsonProperty("detail")
	private void unpackNested(final Map<String, Object> detail) {
		final Map<String, Object> formation = (Map<String, Object>) detail.get("formation");
		if (formation != null) {
			version = (Integer) formation.get("version");
			codeTypeDiplome = (String) formation.get("typeDiplome");
		}
	}

}
