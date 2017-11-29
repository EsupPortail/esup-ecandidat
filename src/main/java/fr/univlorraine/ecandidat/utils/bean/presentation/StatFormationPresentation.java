/**
 * ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package fr.univlorraine.ecandidat.utils.bean.presentation;

import java.io.Serializable;

import javax.persistence.Id;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Objet de StatFormation
 *
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(of = "id")
public class StatFormationPresentation implements Serializable {

	/** serialVersionUID **/
	private static final long serialVersionUID = 3067467095838475483L;

	public static String CHAMPS_ID = "id";
	public static String CHAMPS_COD = "cod";
	public static String CHAMPS_LIB = "lib";
	public static String CHAMPS_LIB_SUPP = "libSupp";
	/*Nombre de candidature total*/
	public static String CHAMPS_NB_CANDIDATURE_TOTAL = "nbCandidatureTotal";
	/*Nombre de candidature cancel*/
	public static String CHAMPS_NB_CANDIDATURE_CANCEL = "nbCandidatureCancel";
	/*Les statuts de dossier*/
	public static String CHAMPS_NB_STATUT_ATTENTE = "nbStatutAttente";
	public static String CHAMPS_NB_STATUT_COMPLET = "nbStatutComplet";
	public static String CHAMPS_NB_STATUT_INCOMPLET = "nbStatutIncomplet";
	public static String CHAMPS_NB_STATUT_RECEPTIONNE = "nbStatutReceptionne";
	/*Les avis*/
	public static String CHAMPS_NB_AVIS_FAVORABLE = "nbAvisFavorable";
	public static String CHAMPS_NB_AVIS_DEFAVORABLE = "nbAvisDefavorable";
	public static String CHAMPS_NB_AVIS_LISTEATTENTE = "nbAvisListeAttente";
	public static String CHAMPS_NB_AVIS_LISTECOMP = "nbAvisListeComp";
	public static String CHAMPS_NB_AVIS_PRESELECTION = "nbAvisPreselection";
	/*Total des avis*/
	public static String CHAMPS_NB_AVIS_TOTAL = "nbAvisTotal";
	public static String CHAMPS_NB_AVIS_TOTAL_VALIDE = "nbAvisTotalValide";
	public static String CHAMPS_NB_AVIS_TOTAL_NON_VALIDE = "nbAvisTotalNonValide";
	/*Les confirmations*/
	public static String CHAMPS_NB_CONFIRM = "nbConfirm";
	public static String CHAMPS_NB_DESIST = "nbDesist";

	@Id
	private Integer id;
	private String cod;
	private String lib;
	private String libSupp;
	private Boolean tes;

	/*Nombre de candidature total*/
	private Long nbCandidatureTotal;
	/*Nombre de candidature cancel*/
	private Long nbCandidatureCancel;
	/*Les statuts de dossier*/
	private Long nbStatutAttente;
	private Long nbStatutComplet;
	private Long nbStatutIncomplet;
	private Long nbStatutReceptionne;
	/*Les avis*/
	private Long nbAvisFavorable;
	private Long nbAvisDefavorable;
	private Long nbAvisListeAttente;
	private Long nbAvisListeComp;
	private Long nbAvisPreselection;
	/*Total des avis*/
	private Long nbAvisTotal;
	private Long nbAvisTotalValide;
	private Long nbAvisTotalNonValide;
	/*Les confirmations*/
	private Long nbConfirm;
	private Long nbDesist;

	public StatFormationPresentation(final Formation f) {
		super();
		this.id = f.getIdForm();
		this.cod = f.getCodForm();
		this.lib = f.getLibForm();
		this.libSupp = f.getCommission().getLibComm();
		this.tes = f.getTesForm();
	}

	public StatFormationPresentation() {
		super();
	}

	public StatFormationPresentation(final Commission c) {
		super();
		this.id = c.getIdComm();
		this.cod = c.getCodComm();
		this.lib = c.getLibComm();
		this.libSupp = c.getLibComm();
		this.tes = c.getTesComm();
	}

	public StatFormationPresentation(final CentreCandidature c) {
		super();
		this.id = c.getIdCtrCand();
		this.cod = c.getCodCtrCand();
		this.lib = c.getLibCtrCand();
		this.libSupp = c.getLibCtrCand();
		this.tes = c.getTesCtrCand();
	}

	public void setFooter() {
		this.nbCandidatureTotal = new Long(0);
		this.nbCandidatureCancel = new Long(0);
		this.nbStatutAttente = new Long(0);
		this.nbStatutComplet = new Long(0);
		this.nbStatutIncomplet = new Long(0);
		this.nbStatutReceptionne = new Long(0);
		this.nbAvisFavorable = new Long(0);
		this.nbAvisDefavorable = new Long(0);
		this.nbAvisListeAttente = new Long(0);
		this.nbAvisListeComp = new Long(0);
		this.nbAvisPreselection = new Long(0);
		this.nbAvisTotal = new Long(0);
		this.nbAvisTotalValide = new Long(0);
		this.nbAvisTotalNonValide = new Long(0);
		this.nbConfirm = new Long(0);
		this.nbDesist = new Long(0);
	}
}
