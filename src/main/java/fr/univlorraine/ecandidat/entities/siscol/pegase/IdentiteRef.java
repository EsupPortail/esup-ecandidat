package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.time.LocalDate;

import lombok.Data;

@Data
public class IdentiteRef {

	private String type;
	private String code;
	private LocalDate dateConsommation;
}
