package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DescripteurSise {

	private String codTypDiplome;

	@JsonProperty("typeDiplome")
	private void unpackNested(final Map<String, Object> typeDiplome) {
		if (typeDiplome == null) {
			return;
		}
		this.codTypDiplome = (String) typeDiplome.get("code");
	}

}
