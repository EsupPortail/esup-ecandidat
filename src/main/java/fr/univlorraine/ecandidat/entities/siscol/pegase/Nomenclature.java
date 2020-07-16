package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import lombok.Data;

@Data
public class Nomenclature {

	private String code;

	private String libelleCourt;

	private String libelleLong;

	private String libelleAffichage;

	private String prioriteAffichage;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstanteUtils.PEGASE_DAT_FORMAT)
	private LocalDate dateDebutValidite;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstanteUtils.PEGASE_DAT_FORMAT)
	private LocalDate dateFinValidite;

	private Boolean temoinVisible;

	private Boolean temoinLivre;

}
