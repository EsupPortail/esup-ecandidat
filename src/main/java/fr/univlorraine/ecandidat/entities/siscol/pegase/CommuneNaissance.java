package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CommuneNaissance extends Nomenclature {

	private String departement;
}
