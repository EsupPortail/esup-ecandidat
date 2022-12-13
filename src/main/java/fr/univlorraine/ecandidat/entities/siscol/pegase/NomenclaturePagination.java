package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.List;

import lombok.Data;

@Data
public class NomenclaturePagination<T> {

	private List<T> nomenclatures;
	private Long nbTotalOccurences;
	private Long nbTotalPages;
	private Long nbOccurences;
	private Long numPage;

}
