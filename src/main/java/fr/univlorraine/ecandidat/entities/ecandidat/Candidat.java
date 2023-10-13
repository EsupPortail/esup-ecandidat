/**
 *  ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.LocalDatePersistenceConverter;
import fr.univlorraine.ecandidat.utils.bean.export.ExportListCandidatureAdresse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** The persistent class for the candidat database table. */
@Entity
@Table(name = "candidat")
@Data
@EqualsAndHashCode(of = "idCandidat")
@ToString(exclude = { "compteMinima" })
@SuppressWarnings("serial")
public class Candidat implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_candidat", nullable = false)
	private Integer idCandidat;

	@Column(name = "typ_siscol", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String typSiScol;

	@Column(name = "autre_pren_candidat", length = 20)
	@Size(max = 20)
	private String autrePrenCandidat;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_naiss_candidat", nullable = false)
	@NotNull
	private LocalDate datNaissCandidat;

	@Column(name = "ine_candidat", length = 10)
	@Size(min = 9, max = 10)
	private String ineCandidat;

	@Column(name = "cle_ine_candidat", length = 2)
	@Size(min = 1, max = 2)
	private String cleIneCandidat;

	@Column(name = "lib_ville_naiss_candidat", nullable = false, length = 30)
	@Size(max = 30)
	@NotNull
	private String libVilleNaissCandidat;

	@Column(name = "nom_pat_candidat", nullable = false, length = 30)
	@Size(max = 30)
	@NotNull
	private String nomPatCandidat;

	@Column(name = "nom_usu_candidat", length = 30)
	@Size(max = 30)
	private String nomUsuCandidat;

	@Column(name = "prenom_candidat", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String prenomCandidat;

	@Column(name = "tel_candidat", length = 20)
	@Size(max = 20)
	private String telCandidat;

	@Column(name = "tel_port_candidat", length = 20)
	@Size(max = 20)
	private String telPortCandidat;

	// bi-directional many-to-one association to Civilite
	@ManyToOne
	@NotNull
	@JoinColumn(name = "cod_civ", nullable = false)
	private Civilite civilite;

	// bi-directional many-to-one association to Adresse
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_adr")
	private Adresse adresse;

	// bi-directional many-to-one association to SiScolPays
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_pay_naiss", referencedColumnName = "cod_pay"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	@NotNull
	private SiScolPays siScolPaysNaiss;

	// bi-directional many-to-one association to SiScolDepartement
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_dep_naiss_candidat", referencedColumnName = "cod_dep"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolDepartement siScolDepartement;

	// bi-directional many-to-one association to SiScolCommune
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_com_naiss_candidat", referencedColumnName = "cod_com"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolCommune siScolCommune;

	// bi-directional many-to-one association to SiScolPays
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_pay_nat", referencedColumnName = "cod_pay"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	@NotNull
	private SiScolPays siScolPaysNat;

	// bi-directional many-to-one association to SiScolRegime
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_rgi", referencedColumnName = "cod_rgi"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolRegime siScolRegime;

	// bi-directional many-to-one association to SiScolStatut
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_stu", referencedColumnName = "cod_stu"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolStatut siScolStatut;

	// bi-directional many-to-one association to Langue
	@ManyToOne
	@JoinColumn(name = "cod_langue", nullable = false)
	@NotNull
	private Langue langue;

	// bi-directional one-to-one association to Candidat
	@OneToOne
	@JoinColumn(name = "id_cpt_min", nullable = false)
	@NotNull
	private CompteMinima compteMinima;

	// bi-directional one-to-one association to CandidatBacOuEqu
	@OneToOne(mappedBy = "candidat", cascade = CascadeType.REMOVE)
	private CandidatBacOuEqu candidatBacOuEqu;

	// bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy = "candidat", cascade = CascadeType.REMOVE)
	private List<CandidatCursusPostBac> candidatCursusPostBacs;

	// bi-directional many-to-one association to CandidatCursusInterne
	@OneToMany(mappedBy = "candidat", cascade = CascadeType.REMOVE)
	private List<CandidatCursusInterne> candidatCursusInternes;

	// bi-directional many-to-one association to CandidatCursusPro
	@OneToMany(mappedBy = "candidat", cascade = CascadeType.REMOVE)
	private List<CandidatCursusPro> candidatCursusPros;

	// bi-directional many-to-one association to CandidatCursusPro
	@OneToMany(mappedBy = "candidat", cascade = CascadeType.REMOVE)
	private List<CandidatStage> candidatStage;

	// bi-directional many-to-one association to Candidature
	@OneToMany(mappedBy = "candidat", cascade = CascadeType.REMOVE)
	private List<Candidature> candidatures;

	// bi-directional many-to-one association to FormulaireCandidat
	@OneToMany(mappedBy = "candidat", cascade = CascadeType.REMOVE)
	private List<FormulaireCandidat> formulaireCandidats;

	// bi-directional many-to-one association to PjCandidat
	@OneToMany(mappedBy = "candidat", cascade = CascadeType.REMOVE)
	private List<PjCandidat> pjCandidats;

	// bi-directional many-to-one association to PjOpi
	@OneToMany(mappedBy = "candidat", cascade = CascadeType.REMOVE)
	private List<PjOpi> pjOpis;

	@Column(name = "tem_updatable_candidat", nullable = false)
	@NotNull
	private Boolean temUpdatableCandidat;

	@Transient
	private String numDossierOpiCandidat;

	@Transient
	private ExportListCandidatureAdresse adresseCandidatExport;

	@Transient
	private String datNaissanceCandidatStr;

	@Transient
	private String lastEtab;

	@Transient
	private String lastDiplome;

	@Transient
	private String lastLibDiplome;

	@Transient
	@Size(min = 11, max = 11)
	private String ineAndKey;

	public Candidat() {
		super();
	}

	public Candidat(final CompteMinima cptMin, final Langue langue, final String typSiScol) {
		this.compteMinima = cptMin;
		this.nomPatCandidat = cptMin.getNomCptMin();
		this.prenomCandidat = cptMin.getPrenomCptMin();
		this.langue = langue;
		this.typSiScol = typSiScol;
		this.temUpdatableCandidat = true;
	}

	/**
	 * Ajoute un cursus
	 * @param e
	 */
	public void addCursusPostBac(final CandidatCursusPostBac e) {
		if (getCandidatCursusPostBacs().contains(e)) {
			getCandidatCursusPostBacs().remove(e);
		}
		getCandidatCursusPostBacs().add(e);
	}

	/**
	 * Ajoute un cursus pro
	 * @param e
	 */
	public void addCursusPro(final CandidatCursusPro e) {
		if (getCandidatCursusPros().contains(e)) {
			getCandidatCursusPros().remove(e);
		}
		getCandidatCursusPros().add(e);
	}

	/**
	 * Ajoute un stage
	 * @param e
	 */
	public void addStage(final CandidatStage e) {
		if (getCandidatStage().contains(e)) {
			getCandidatStage().remove(e);
		}
		getCandidatStage().add(e);
	}
}
