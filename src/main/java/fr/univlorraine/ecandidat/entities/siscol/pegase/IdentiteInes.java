package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;

@Data
public class IdentiteInes {

	private String ine;
	private Boolean confirme;
	private Boolean maitre;

	public IdentiteInes() {
		super();
	}

	public IdentiteInes(String ine) {
		super();
		this.ine = ine;
		this.confirme = true;
		this.maitre = true;
	}
}
