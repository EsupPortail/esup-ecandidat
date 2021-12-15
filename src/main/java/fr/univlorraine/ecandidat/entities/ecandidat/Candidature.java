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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDatePersistenceConverter;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** The persistent class for the candidature database table. */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "candidature")
@Data
@EqualsAndHashCode(of = "idCand")
@ToString(exclude = { "candidat", "pjCands", "formulaireCands", "formulaireCandidatures", "lastTypeDecision", "formation", "opi" })
@SuppressWarnings("serial")
public class Candidature implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cand", nullable = false)
	private Integer idCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_accept_cand")
	private LocalDateTime datAcceptCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_annul_cand")
	private LocalDateTime datAnnulCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_cand", nullable = false)
	@NotNull
	private LocalDateTime datCreCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_cand", nullable = false)
	@NotNull
	private LocalDateTime datModCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_typ_statut_cand")
	private LocalDateTime datModTypStatutCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_trans_dossier_cand")
	private LocalDateTime datTransDossierCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_recept_dossier_cand")
	private LocalDate datReceptDossierCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_complet_dossier_cand")
	private LocalDate datCompletDossierCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_incomplet_dossier_cand")
	private LocalDate datIncompletDossierCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_new_confirm_cand")
	private LocalDate datNewConfirmCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_new_retour_cand")
	private LocalDate datNewRetourCand;

	@Column(name = "tem_proposition_cand", nullable = false)
	@NotNull
	private Boolean temPropositionCand;

	@Column(name = "tem_valid_typ_trait_cand", nullable = false)
	@NotNull
	private Boolean temValidTypTraitCand;

	@Column(name = "tem_accept_cand")
	private Boolean temAcceptCand;

	@Column(name = "tem_relance_cand", nullable = false)
	@NotNull
	private Boolean temRelanceCand;

	@Column(name = "user_accept_cand", length = 50)
	@Size(max = 50)
	private String userAcceptCand;

	@Column(name = "user_annul_cand", length = 50)
	@Size(max = 50)
	private String userAnnulCand;

	@Column(name = "user_cre_cand", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreCand;

	@Column(name = "user_mod_cand", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModCand;

	// bi-directional many-to-one association to Candidat
	@ManyToOne
	@JoinColumn(name = "id_candidat", nullable = false)
	@NotNull
	private Candidat candidat;

	// bi-directional many-to-one association to Formation
	@ManyToOne
	@JoinColumn(name = "id_form", nullable = false)
	@NotNull
	private Formation formation;

	// bi-directional one-to-one association to OpiAttente
	@OneToOne(mappedBy = "candidature", cascade = CascadeType.REMOVE)
	private Opi opi;

	// bi-directional many-to-one association to TypeStatut
	@ManyToOne
	@JoinColumn(name = "cod_typ_statut", nullable = false)
	@NotNull
	private TypeStatut typeStatut;

	// bi-directional many-to-one association to SiScolCatExoExt
	@ManyToOne
	@JoinColumn(name = "cod_cat_exo_ext", nullable = true)
	private SiScolCatExoExt siScolCatExoExt;

	@Column(name = "comp_exo_ext_cand", length = 200, nullable = true)
	@Size(max = 200)
	private String compExoExtCand;

	@Column(name = "mnt_charge_cand", nullable = true)
	@Digits(integer = 10, fraction = 2)
	private BigDecimal mntChargeCand;

	// bi-directional many-to-one association to TypeTraitement
	@ManyToOne
	@JoinColumn(name = "cod_typ_trait", nullable = false)
	@NotNull
	private TypeTraitement typeTraitement;

	/* Date de la formation lors de l'archivage */
	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_analyse_form")
	private LocalDate datAnalyseForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_confirm_form")
	private LocalDate datConfirmForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_deb_depot_form")
	private LocalDate datDebDepotForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_fin_depot_form")
	private LocalDate datFinDepotForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_jury_form")
	private LocalDate datJuryForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_publi_form")
	private LocalDate datPubliForm;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_retour_form")
	private LocalDate datRetourForm;

	@Column(name = "delai_confirm_form")
	private Integer delaiConfirmForm;

	// bi-directional many-to-one association to FormulaireCand
	@OneToMany(mappedBy = "candidature", cascade = CascadeType.REMOVE)
	private List<FormulaireCand> formulaireCands;

	// bi-directional many-to-one association to PjCand
	@OneToMany(mappedBy = "candidature")
	private List<PjCand> pjCands;

	// bi-directional many-to-one association to PostIt
	@OneToMany(mappedBy = "candidature", cascade = CascadeType.REMOVE)
	private List<PostIt> postIts;

	// bi-directional many-to-one association to FormulaireCand
	@OneToMany(mappedBy = "candidature", cascade = CascadeType.REMOVE)
	private List<FormulaireCandidature> formulaireCandidatures;

	// bi-directional many-to-one association to TypeDecisionCandidature
	@OneToMany(mappedBy = "candidature", cascade = CascadeType.REMOVE)
	private List<TypeDecisionCandidature> typeDecisionCandidatures;

	// bi-directional many-to-many association to Tag
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "tag_candidature", joinColumns = { @JoinColumn(name = "id_cand") }, inverseJoinColumns = { @JoinColumn(name = "id_tag") })
	private List<Tag> tags;

	/* Attributs Transient */
	@Transient
	private TypeDecisionCandidature lastTypeDecision;
	@Transient
	private String datCreCandStr;
	@Transient
	private String datModTypStatutCandStr;
	@Transient
	private String datReceptDossierCandStr;
	@Transient
	private String datTransDossierCandStr;
	@Transient
	private String datCompletDossierCandStr;
	@Transient
	private String datIncompletDossierCandStr;
	@Transient
	private String datAnnulCandStr;
	@Transient
	private String datModPjForm;
	@Transient
	private String datNewConfirmCandStr;
	@Transient
	private String datNewRetourCandStr;
	@Transient
	private String tagsStr;
	@Transient
	private String tagsSortable;
	@Transient
	private String blocNoteStr;
	@Transient
	private String catExoStr;
	@Transient
	private String mntChargeStr;
	@Transient
	private String codOpiStr;
	@Transient
	private String datPassageOpiStr;

	@PrePersist
	private void onPrePersist() {
		datCreCand = LocalDateTime.now();
		datModCand = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		datModCand = LocalDateTime.now();
	}

	public Candidature(final String user, final Candidat candidat, final Formation formation, final TypeTraitement typeTraitement, final TypeStatut statut, final Boolean temPropositionCand, final Boolean temValidTypTraitCand) {
		super();
		temRelanceCand = false;
		this.temPropositionCand = temPropositionCand;
		this.temValidTypTraitCand = temValidTypTraitCand;
		userCreCand = user;
		userModCand = user;
		this.typeTraitement = typeTraitement;
		this.candidat = candidat;
		typeStatut = statut;
		this.formation = formation;
	}

	public Candidature() {
		super();
	}

	/**
	 * Modifie la liste des PJ
	 * @param pjCand
	 */
	public void updatePjCand(final PjCand pjCand) {
		removePjCand(pjCand);
		getPjCands().add(pjCand);
	}

	/**
	 * Modifie la liste des Form
	 * @param formulaireCand
	 */
	public void updateFormulaireCand(final FormulaireCand formulaireCand) {
		removeFormulaireCand(formulaireCand);
		getFormulaireCands().add(formulaireCand);
	}

	/**
	 * @param pjCand
	 */
	public void removePjCand(final PjCand pjCand) {
		if (getPjCands().contains(pjCand)) {
			getPjCands().remove(pjCand);
		}
	}

	/**
	 * @param formulaireCand
	 */
	public void removeFormulaireCand(final FormulaireCand formulaireCand) {
		if (getFormulaireCands().contains(formulaireCand)) {
			getFormulaireCands().remove(formulaireCand);
		}
	}

	/**
	 * Modifie une decision
	 * @param typeDecision
	 */
	public void setTypeDecision(final TypeDecisionCandidature typeDecision) {
		getTypeDecisionCandidatures().remove(typeDecision);
		getTypeDecisionCandidatures().add(typeDecision);
	}

	/**
	 * Supprime une decision
	 * @param typeDecision
	 */
	public void removeTypeDecision(final TypeDecisionCandidature typeDecision) {
		getTypeDecisionCandidatures().remove(typeDecision);
	}

	/**
	 * @return les tags en service
	 */
	public List<Tag> getTags() {
		if (tags == null) {
			return null;
		}
		return tags.stream().filter(e -> e.getTesTag()).collect(Collectors.toList());
	}

	/**
	 * @return le tag sous forme de string pour qu'il soit sortable (la list de tag n'implementant pas comparable)
	 */
	public String getTagsSortable() {
		if (tags == null || tags.size() == 0) {
			// pour que les tags vide apparaissent en dernier
			return ConstanteUtils.BIGGER_STRING_TO_SORT;
		}
		return tags.stream().map(e -> e.getLibTag()).collect(Collectors.joining(" "));
	}
}
