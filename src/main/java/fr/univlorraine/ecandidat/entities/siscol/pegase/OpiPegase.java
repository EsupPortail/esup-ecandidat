package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.time.format.DateTimeFormatter;

import com.opencsv.bean.CsvBindByName;

import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import lombok.Data;

@Data
public class OpiPegase {

	public OpiPegase(final Candidat candidat, final DateTimeFormatter formatterDate, final String codpaysFrance) {
		super();
		this.nomNaissance = candidat.getNomPatCandidat();
		this.nomUsage = candidat.getNomUsuCandidat();
		this.prenom = candidat.getPrenomCandidat();
		this.prenom2 = candidat.getAutrePrenCandidat();
		this.prenom3 = null;
		this.sexe = candidat.getCivilite().getCodSexe();
		this.dateNaissance = formatterDate.format(candidat.getDatNaissCandidat());
		this.codePaysNaissance = candidat.getSiScolPaysNaiss() != null ? candidat.getSiScolPaysNaiss().getId().getCodPay() : null;
		this.codeCommuneNaissance = candidat.getSiScolCommuneNaiss() != null ? candidat.getSiScolCommuneNaiss().getId().getCodComNaiss() : null;
		if (candidat.getSiScolPaysNaiss() != null && !codpaysFrance.equals(candidat.getSiScolPaysNaiss().getId().getCodPay())) {
			this.libelleCommuneNaissanceEtranger = candidat.getLibVilleNaissCandidat();
		}
		this.codeNationalite = candidat.getSiScolPaysNat() != null ? candidat.getSiScolPaysNat().getId().getCodPay() : null;
		this.ine = candidat.getIneAndKey();
		final Adresse adr = candidat.getAdresse();
		this.adresseCodePays = adr.getSiScolPays() != null ? adr.getSiScolPays().getId().getCodPay() : null;
		this.adresseLigne1Etage = adr.getAdr2Adr();
		this.adresseLigne2Batiment = adr.getAdr3Adr();
		this.adresseLigne3Voie = adr.getAdr1Adr();
		this.adresseLigne4Complement = null;
		this.adresseLigne5Etranger = null;
		this.adresseCodePostal = adr.getCodBdiAdr();
		this.adresseCodeCommune = adr.getSiScolCommune() != null ? adr.getSiScolCommune().getId().getCodCom() : null;
		this.numeroTelephone1 = candidat.getTelCandidat();
		this.numeroTelephone2 = candidat.getTelPortCandidat();
		this.mail = candidat.getCompteMinima().getMailPersoCptMin();
	}

	@CsvBindByName(column = "nom_naissance")
	private String nomNaissance;

	@CsvBindByName(column = "nom_usage")
	private String nomUsage;

	@CsvBindByName(column = "prenom")
	private String prenom;

	@CsvBindByName(column = "prenom2")
	private String prenom2;

	@CsvBindByName(column = "prenom3")
	private String prenom3;

	@CsvBindByName(column = "sexe")
	private String sexe;

	@CsvBindByName(column = "date_naissance")
	private String dateNaissance;

	@CsvBindByName(column = "code_pays_naissance")
	private String codePaysNaissance;

	@CsvBindByName(column = "code_commune_naissance")
	private String codeCommuneNaissance;

	@CsvBindByName(column = "libelle_commune_naissance_etranger")
	private String libelleCommuneNaissanceEtranger;

	@CsvBindByName(column = "code_nationalite")
	private String codeNationalite;

	@CsvBindByName(column = "ine")
	private String ine;

	@CsvBindByName(column = "adresse_code_pays")
	private String adresseCodePays;

	@CsvBindByName(column = "adresse_ligne1_etage")
	private String adresseLigne1Etage;

	@CsvBindByName(column = "adresse_ligne2_batiment")
	private String adresseLigne2Batiment;

	@CsvBindByName(column = "adresse_ligne3_voie")
	private String adresseLigne3Voie;

	@CsvBindByName(column = "adresse_ligne4_complement")
	private String adresseLigne4Complement;

	@CsvBindByName(column = "adresse_ligne5_etranger")
	private String adresseLigne5Etranger;

	@CsvBindByName(column = "adresse_code_postal")
	private String adresseCodePostal;

	@CsvBindByName(column = "adresse_code_commune")
	private String adresseCodeCommune;

	@CsvBindByName(column = "numero_telephone1")
	private String numeroTelephone1;

	@CsvBindByName(column = "numero_telephone2")
	private String numeroTelephone2;

	@CsvBindByName(column = "mail")
	private String mail;

	@CsvBindByName(column = "origine_admission1")
	private String origineAdmission1;

	@CsvBindByName(column = "annee_concours1")
	private String anneeConcours1;

	@CsvBindByName(column = "code_voeu1")
	private String codeVoeu1;

	@CsvBindByName(column = "code_periode1")
	private String codePeriode1;

	@CsvBindByName(column = "origine_admission2")
	private String origineAdmission2;

	@CsvBindByName(column = "annee_concours2")
	private String anneeConcours2;

	@CsvBindByName(column = "code_voeu2")
	private String codeVoeu2;

	@CsvBindByName(column = "code_periode2")
	private String codePeriode2;

	@CsvBindByName(column = "origine_admission3")
	private String origineAdmission3;

	@CsvBindByName(column = "annee_concours3")
	private String anneeConcours3;

	@CsvBindByName(column = "code_voeu3")
	private String codeVoeu3;

	@CsvBindByName(column = "code_periode3")
	private String codePeriode3;

	@CsvBindByName(column = "origine_admission4")
	private String origineAdmission4;

	@CsvBindByName(column = "annee_concours4")
	private String anneeConcours4;

	@CsvBindByName(column = "code_voeu4")
	private String codeVoeu4;

	@CsvBindByName(column = "code_periode4")
	private String codePeriode4;

	@CsvBindByName(column = "origine_admission5")
	private String origineAdmission5;

	@CsvBindByName(column = "annee_concours5")
	private String anneeConcours5;

	@CsvBindByName(column = "code_voeu5")
	private String codeVoeu5;

	@CsvBindByName(column = "code_periode5")
	private String codePeriode5;

	public void setVoeu1(final OpiVoeu voeu) {
		setOrigineAdmission1(voeu.getOrigineAdmission());
		setAnneeConcours1(voeu.getAnneeConcours());
		setCodeVoeu1(voeu.getCodeVoeu());
		setCodePeriode1(voeu.getCodePeriode());
	}

	public void setVoeu2(final OpiVoeu voeu) {
		setOrigineAdmission2(voeu.getOrigineAdmission());
		setAnneeConcours2(voeu.getAnneeConcours());
		setCodeVoeu2(voeu.getCodeVoeu());
		setCodePeriode2(voeu.getCodePeriode());
	}

	public void setVoeu3(final OpiVoeu voeu) {
		setOrigineAdmission3(voeu.getOrigineAdmission());
		setAnneeConcours3(voeu.getAnneeConcours());
		setCodeVoeu3(voeu.getCodeVoeu());
		setCodePeriode3(voeu.getCodePeriode());
	}

	public void setVoeu4(final OpiVoeu voeu) {
		setOrigineAdmission4(voeu.getOrigineAdmission());
		setAnneeConcours4(voeu.getAnneeConcours());
		setCodeVoeu4(voeu.getCodeVoeu());
		setCodePeriode4(voeu.getCodePeriode());
	}

	public void setVoeu5(final OpiVoeu voeu) {
		setOrigineAdmission5(voeu.getOrigineAdmission());
		setAnneeConcours5(voeu.getAnneeConcours());
		setCodeVoeu5(voeu.getCodeVoeu());
		setCodePeriode5(voeu.getCodePeriode());
	}

}
