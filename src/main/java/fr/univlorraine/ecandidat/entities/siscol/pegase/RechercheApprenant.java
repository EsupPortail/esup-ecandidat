package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class RechercheApprenant {

	private String id;
	private String identifiantApprenantPegase;
	private String nomNaissance;
	private String nomUsage;
	private String prenom;
	private String prenom2;
	private String prenom3;
	private String email;
	private String sexe;
	private String codeApprenant;
	private LocalDate dateNaissance;
	private RechercheApprenantIneMaitre ineMaitre;
	private RechercheApprenantRef communeNaissance;
	private RechercheApprenantRef paysNaissance;
	private RechercheApprenantRef nationalite;
	private List<String> inesSupplementaires;
	private Boolean actif;
}
