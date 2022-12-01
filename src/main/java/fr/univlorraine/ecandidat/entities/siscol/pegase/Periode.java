package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import lombok.Data;

@Data
public class Periode {

	private String code;

	private String libelle;

	private String libelleCourt;

	private String libelleLong;

	private String libelleAffichage;

	private Integer anneeUniversitaire;

	private Boolean active;

	private Boolean valideOuFutur;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstanteUtils.PEGASE_DAT_FORMAT)
	private LocalDate dateDebut;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstanteUtils.PEGASE_DAT_FORMAT)
	private LocalDate dateFin;
}
