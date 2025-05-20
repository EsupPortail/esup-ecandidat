package fr.univlorraine.ecandidat.entities.siscol.pegase;

import lombok.Data;

@Data
public class Identite {

	private String id;
	private String identifiantApprenantPegase;
	private String nomNaissance;
	private String prenom;
	private String email;
	private String telephonePortablePersonnel;
	private IdentiteProfilApp profilApprenant;
}
