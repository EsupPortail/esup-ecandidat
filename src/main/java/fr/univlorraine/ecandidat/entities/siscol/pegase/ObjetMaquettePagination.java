package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.List;

import lombok.Data;

@Data
public class ObjetMaquettePagination {

	private List<FormationPegase> items;
	private Long totalElements;
	private Long totalPages;
	private Long taille;
	private Long page;

}
