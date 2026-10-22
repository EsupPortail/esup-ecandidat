package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.Arrays;
import java.util.List;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import lombok.Data;

@Data
public class IdentiteProfilApp {

	private List<IdentiteInes> listeINEs;
	private IdentiteEtatCivil etatCivil;
	private IdentiteDonneesContact donneesContact;
	private IdentiteParcours parcoursScolaireEtUniversitaire;
	private String codeApprenant;
	private String ineMaitre;

	public String getIneConfirme() {
		return listeINEs.stream().filter(e -> e.getConfirme() && e.getMaitre()).findFirst().map(IdentiteInes::getIne).orElse(null);
	}

	public IdentiteProfilApp() {
		super();
	}

	public IdentiteProfilApp(Candidat candidat) {
		super();
	}

	public IdentiteProfilApp(Candidat candidat, String codPaysFrance) {
		super();
		if (candidat.getInes() != null) {
			this.ineMaitre = candidat.getInes();
			this.listeINEs = Arrays.asList(new IdentiteInes[] { new IdentiteInes(candidat.getInes()) });
		}
		this.etatCivil = new IdentiteEtatCivil(candidat, codPaysFrance);
		//this.listeINEs = Arrays.asList(new String[]{candidat.getIneAndKey()});
	}
}
