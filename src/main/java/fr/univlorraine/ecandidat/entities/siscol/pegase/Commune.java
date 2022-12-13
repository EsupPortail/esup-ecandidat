package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class Commune extends Nomenclature {

	private String codePostal;
	private String codeInsee;
	private String codeInseeAncien;
	private String libelleAcheminement;
}
