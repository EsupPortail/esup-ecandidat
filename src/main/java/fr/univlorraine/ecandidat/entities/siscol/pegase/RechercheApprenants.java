package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.List;

import lombok.Data;

@Data
public class RechercheApprenants {

	private List<RechercheApprenant> items;
	int totalElements;
}
