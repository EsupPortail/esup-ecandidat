package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.util.List;

import lombok.Data;

@Data
public class IdentiteProfilApp {

	private List<IdentiteInes> listeINEs;
	private IdentiteEtatCivil etatCivil;
	private IdentiteDonneesContact donneesContact;
	private IdentiteParcours parcoursScolaireEtUniversitaire;
	private String codeApprenant;

	public String getIneConfirme() {
		return listeINEs.stream().filter(e -> e.getConfirme() && e.getMaitre()).findFirst().map(IdentiteInes::getIne).orElse(null);
	}
}
