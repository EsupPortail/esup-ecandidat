package fr.univlorraine.ecandidat.entities.siscol.pegase;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
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

	public Identite() {
		super();
	}

	public Identite(Candidat candidat, String codPaysFrance) {
		super();
		this.nomNaissance = candidat.getNomPatCandidat();
		this.prenom = candidat.getPrenomCandidat();
		this.email = candidat.getCompteMinima().getMailPersoCptMin();
		this.telephonePortablePersonnel = candidat.getTelPortCandidat();
		this.profilApprenant = new IdentiteProfilApp(candidat, codPaysFrance);
	}

}
