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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDatePersistenceConverter;
import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** The persistent class for the formation database table. */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "formation")
@Data
@EqualsAndHashCode(of = "idForm")
@ToString(of =
{ "idForm", "codForm", "libForm", "tesForm" })
@SuppressWarnings("serial")
public class Formation implements Serializable {

	public static final String FLAG_COLUMN_NAME = "flagEtat";
	public static final String DAT_VOEUX_COLUMN_NAME = "dateVoeux";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_form", nullable = false)
	private Integer idForm;

	@Column(name = "typ_siscol", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String typSiScol;

	@Column(name = "cod_etp_vet_apo_form", length = 20)
	@Size(max = 20)
	private String codEtpVetApoForm;

	@Column(name = "cod_vrs_vet_apo_form", length = 20)
	@Size(max = 20)
	private String codVrsVetApoForm;

	@Column(name = "lib_apo_form", length = 120)
	@Size(max = 120)
	private String libApoForm;

	@Column(name = "cod_dip_apo_form", length = 20)
	@Size(max = 20)
	private String codDipApoForm;

	@Column(name = "cod_vrs_vdi_apo_form", length = 20)
	@Size(max = 20)
	private String codVrsVdiApoForm;

	@Column(name = "lib_dip_apo_form", length = 120)
	@Size(max = 120)
	private String libDipApoForm;

	@Column(name = "cod_pegase_form", length = 50)
	@Size(max = 50)
	private String codPegaseForm;

	@Column(name = "lib_pegase_form", length = 500)
	@Size(max = 500)
	private String libPegaseForm;

	@Column(name = "cod_form", unique = true, nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String codForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_analyse_form")
	private LocalDate datAnalyseForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_confirm_form")
	private LocalDate datConfirmForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_confirm_list_comp_form")
	private LocalDate datConfirmListCompForm;

	@Column(name = "delai_confirm_form")
	private Integer delaiConfirmForm;

	@Column(name = "delai_confirm_list_comp_form")
	private Integer delaiConfirmListCompForm;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_form", nullable = false)
	@NotNull
	private LocalDateTime datCreForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_deb_depot_form", nullable = false)
	@NotNull
	private LocalDate datDebDepotForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_fin_depot_form", nullable = false)
	@NotNull
	private LocalDate datFinDepotForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_jury_form")
	private LocalDate datJuryForm;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_form", nullable = false)
	@NotNull
	private LocalDateTime datModForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_publi_form")
	private LocalDate datPubliForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_retour_form", nullable = false)
	@NotNull
	private LocalDate datRetourForm;

	@Column(name = "lib_form", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libForm;

	@Column(name = "tem_list_comp_form", nullable = false)
	@NotNull
	private Boolean temListCompForm;

	@Column(name = "mot_cle_form", length = 500)
	@Size(max = 500)
	private String motCleForm;

	@Column(name = "url_form", length = 500)
	@Size(max = 500)
	private String urlForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "preselect_date_form")
	private LocalDate preselectDateForm;

	@Column(name = "preselect_heure_form")
	@Convert(converter = LocalTimePersistenceConverter.class)
	private LocalTime preselectHeureForm;

	@Column(name = "preselect_lieu_form", length = 100)
	@Size(max = 100)
	private String preselectLieuForm;

	@Column(name = "tem_demat_form", nullable = false)
	@NotNull
	private Boolean temDematForm;

	@Column(name = "tes_form", nullable = false)
	@NotNull
	private Boolean tesForm;

	@Column(name = "info_comp_form", length = 500)
	@Size(max = 500)
	private String infoCompForm;

	@Column(name = "capacite_form", nullable = true)
	private Integer capaciteForm;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_info_comp_form", nullable = false)
	private I18n i18nInfoCompForm;

	@Column(name = "user_cre_form", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreForm;

	@Column(name = "user_mod_form", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModForm;

	// bi-directional many-to-one association to Candidature
	@OneToMany(mappedBy = "formation")
	private List<Candidature> candidatures;

	// bi-directional many-to-one association to ApoCentreGestion
	@ManyToOne
	@JoinColumns(
	{
		@JoinColumn(name = "cod_cge", referencedColumnName = "cod_cge"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolCentreGestion siScolCentreGestion;

	// bi-directional many-to-one association to SiScolTypDiplome
	@ManyToOne
	@JoinColumns(
	{
		@JoinColumn(name = "cod_tpd_etb", referencedColumnName = "cod_tpd_etb"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolTypDiplome siScolTypDiplome;

	// bi-directional many-to-one association to TypeFormation
	@ManyToOne
	@JoinColumn(name = "id_typ_form", nullable = true)
	private TypeFormation typeFormation;

	// bi-directional many-to-one association to Commission
	@ManyToOne
	@NotNull
	@JoinColumn(name = "id_comm")
	private Commission commission;

	// bi-directional many-to-many association to Formulaire
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "formulaire_form", joinColumns =
	{ @JoinColumn(name = "id_form", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "id_formulaire", nullable = false) })
	private List<Formulaire> formulaires;

	// bi-directional many-to-many association to PieceJustif
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "pj_form", joinColumns =
	{ @JoinColumn(name = "id_form", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "id_pj", nullable = false) })
	private List<PieceJustif> pieceJustifs;

	// bi-directional many-to-many association to Formulaire
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "question_form", joinColumns =
	{ @JoinColumn(name = "id_form", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "id_question", nullable = false) })
	private List<Question> questions;

	// bi-directional many-to-one association to TypeDecision
	@ManyToOne
	@JoinColumn(name = "id_typ_dec_fav", nullable = false)
	@NotNull
	private TypeDecision typeDecisionFav;

	// bi-directional many-to-one association to TypeDecision
	@ManyToOne
	@JoinColumn(name = "id_typ_dec_fav_list_comp", nullable = true)
	private TypeDecision typeDecisionFavListComp;

	// bi-directional many-to-one association to TypeTraitement
	@ManyToOne
	@JoinColumn(name = "cod_typ_trait", nullable = false)
	@NotNull
	private TypeTraitement typeTraitement;

	@Transient
	private String flagEtat;
	@Transient
	private String dateVoeux;

	/* Pour l'export */
	@Transient
	private String infoCompFormStr;
	@Transient
	private String preselectDateFormStr;
	@Transient
	private String datAnalyseFormStr;
	@Transient
	private String datConfirmFormStr;
	@Transient
	private String datConfirmListCompFormStr;
	@Transient
	private String datCreFormStr;
	@Transient
	private String datDebDepotFormStr;
	@Transient
	private String datFinDepotFormStr;
	@Transient
	private String datJuryFormStr;
	@Transient
	private String datModFormStr;
	@Transient
	private String datPubliFormStr;
	@Transient
	private String datRetourFormStr;

	@PrePersist
	private void onPrePersist() {
		datCreForm = LocalDateTime.now();
		datModForm = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		datModForm = LocalDateTime.now();
	}

	public Formation(final String user, final String typSiScol) {
		this(typSiScol);
		this.userCreForm = user;
		this.userModForm = user;
		this.temListCompForm = false;
		this.tesForm = false;

	}

	public Formation(final String typSiScol) {
		this();
		this.typSiScol = typSiScol;
	}

	public Formation() {
		super();
	}
}
