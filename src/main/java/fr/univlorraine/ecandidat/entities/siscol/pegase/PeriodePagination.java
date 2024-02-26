package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.List;

import lombok.Data;

@Data
public class PeriodePagination {

	private List<Periode> items;
	private Long totalElements;
	private Long totalPages;
	private Long taille;
	private Long page;

}
