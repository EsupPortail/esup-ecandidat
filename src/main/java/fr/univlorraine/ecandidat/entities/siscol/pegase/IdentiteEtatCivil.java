package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.time.LocalDate;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import lombok.Data;

@Data
public class IdentiteEtatCivil {

	private String sexe;
	private LocalDate dateNaissance;
	private String nomUsage;
	private String prenom2;
	private String prenom3;
	private IdentiteRef communeNaissance;
	private String libelleCommuneNaissanceEtranger;
	private IdentiteRef paysNaissance;
	private IdentiteRef nationalite;

	public IdentiteEtatCivil() {
		super();
	}

	public IdentiteEtatCivil(Candidat candidat, String codPaysFrance) {
		super();
		this.sexe = candidat.getCivilite().getCodSexe();
		this.dateNaissance = candidat.getDatNaissCandidat();
		this.nomUsage = candidat.getNomUsuCandidat();
		this.prenom2 = candidat.getAutrePrenCandidat();
		this.libelleCommuneNaissanceEtranger = (!candidat.getSiScolPaysNaiss().isCodePays(codPaysFrance)) ? candidat.getLibVilleNaissCandidat() : null;
		this.communeNaissance = communeNaissance;
		this.paysNaissance = paysNaissance;
		this.nationalite = nationalite;
	}

}
