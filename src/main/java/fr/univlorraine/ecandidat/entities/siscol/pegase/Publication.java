package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Publication {

	private String codeChemin;
	/* Objet feuille */
	private String codeFeuille;
	private String libCourtFeuille;
	private String libLongFeuille;

	private Integer bareme;
	private String noteSession1;
	private String codResSession1;
	private String noteSession2;
	private String codResSession2;

	@JsonProperty("objetFeuille")
	private void unpackNestedObjetFeuille(final Map<String, Object> objetFeuille) {
		if (objetFeuille == null) {
			return;
		}
		this.codeFeuille = (String) objetFeuille.get("code");
		this.libCourtFeuille = (String) objetFeuille.get("libelleCourt");
		this.libLongFeuille = (String) objetFeuille.get("libelleLong");
	}

	@JsonProperty("resultatSession1")
	private void unpackNestedSession1(final Map<String, Object> resultatSession1) {
		if (resultatSession1 == null) {
			return;
		}
		this.codResSession1 = (String) resultatSession1.get("code");
	}

	@JsonProperty("resultatSession2")
	private void unpackNestedSession2(final Map<String, Object> resultatSession2) {
		if (resultatSession2 == null) {
			return;
		}
		this.codResSession2 = (String) resultatSession2.get("code");
	}

	public String getCodRes() {
		if (getCodResSession1() != null) {
			return getCodResSession1();
		}
		return getCodResSession2();
	}

	public String getNote() {
		if (getNoteSession1() != null) {
			return getNoteSession1();
		}
		return getNoteSession2();
	}

	public Boolean hasResults() {
		if (getCodeChemin() == null) {
			return false;
		}
		return getCodeChemin().split(">").length == 3;
	}
}
