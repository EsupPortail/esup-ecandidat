package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import lombok.Data;

@Data
public class ApprenantNaissance {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstanteUtils.PEGASE_DAT_FORMAT)
	private LocalDate dateDeNaissance;
	private String paysDeNaissance;
	private String communeDeNaissance;
	private String communeDeNaissanceEtranger;
	private String nationalite;
	private String deuxiemeNationalite;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstanteUtils.PEGASE_DAT_FORMAT)
	private LocalDate dateDObtentionDeLaDeuxiemeNationalite;
}
