package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InscriptionIns {

	/* Données inscription */
	private String code;
	private String libelleCourt;
	private String libelleLong;

	/* Données formation */
	private String codeFormation;
	private String libelleCourtFormation;
	private String libelleLongFormation;

	/* Données periode */
	private String codePeriode;
	private Integer anneeUnivPeriode;

	@SuppressWarnings("unchecked")
	@JsonProperty("cible")
	private void unpackNested(final Map<String, Object> cible) {
		if (cible == null) {
			return;
		}
		this.code = (String) cible.get("code");
		this.libelleCourt = (String) cible.get("libelleCourt");
		this.libelleLong = (String) cible.get("libelleLong");
		/* Formation */
		final Map<String, Object> formation = (Map<String, Object>) cible.get("formation");
		if (formation != null) {
			this.codeFormation = (String) formation.get("code");
			this.libelleCourtFormation = (String) formation.get("libelleCourt");
			this.libelleLongFormation = (String) formation.get("libelleLong");
		}
		/* Période */
		final Map<String, Object> periode = (Map<String, Object>) cible.get("periode");
		if (periode != null) {
			this.codePeriode = (String) periode.get("code");
			this.anneeUnivPeriode = (Integer) periode.get("anneeUniversitaire");
		}
	}

	public String getAnneeUniv() {
		if (getAnneeUnivPeriode() != null) {
			return String.valueOf(getAnneeUnivPeriode());
		}
		return null;
	}

	/**
	 * @return le code de chemin
	 */
	public String getCodeChemin() {
		if (codeFormation != null) {
			return codeFormation + ">" + code;
		} else {
			return code;
		}
	}
}
