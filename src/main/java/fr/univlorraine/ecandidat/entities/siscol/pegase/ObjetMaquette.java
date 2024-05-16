package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;

@Data
public class ObjetMaquette {

	private String id;
	private String typeObjetMaquette;
	private String code;
	private String espace;
	private DescripteurEnquete descripteursEnquete;

}
