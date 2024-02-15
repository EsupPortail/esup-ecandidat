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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDatePersistenceConverter;
import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** The persistent class for the centre_candidature database table. */
@Entity
@Table(name = "centre_candidature")
@EntityListeners(EntityPushEntityListener.class)
@Data
@EqualsAndHashCode(of = "idCtrCand")
@ToString(of = { "idCtrCand", "codCtrCand", "libCtrCand", "tesCtrCand" })
@SuppressWarnings("serial")
public class CentreCandidature implements Serializable {

	public static final String TYP_SEND_MAIL_NONE = "N";
	public static final String TYP_SEND_MAIL_MAIL_CONTACT = "M";
	public static final String TYP_SEND_MAIL_LIST_GEST = "L";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_ctr_cand", nullable = false)
	private Integer idCtrCand;

	@Column(name = "cod_ctr_cand", unique = true, nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String codCtrCand;

	@Column(name = "mail_contact_ctr_cand", nullable = true, length = 80)
	@Size(max = 80)
	private String mailContactCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_analyse_ctr_cand")
	private LocalDate datAnalyseCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_confirm_ctr_cand")
	private LocalDate datConfirmCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_confirm_list_comp_ctr_cand")
	private LocalDate datConfirmListCompCtrCand;

	@Column(name = "delai_confirm_ctr_cand")
	private Integer delaiConfirmCtrCand;

	@Column(name = "delai_confirm_list_comp_ctr_cand")
	private Integer delaiConfirmListCompCtrCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_ctr_cand", nullable = false)
	@NotNull
	private LocalDateTime datCreCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_deb_depot_ctr_cand", nullable = false)
	@NotNull
	private LocalDate datDebDepotCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_fin_depot_ctr_cand", nullable = false)
	@NotNull
	private LocalDate datFinDepotCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_jury_ctr_cand")
	private LocalDate datJuryCtrCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_ctr_cand", nullable = false)
	@NotNull
	private LocalDateTime datModCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_publi_ctr_cand")
	private LocalDate datPubliCtrCand;

	@Convert(converter = LocalDatePersistenceConverter.class)
	@Column(name = "dat_retour_ctr_cand", nullable = false)
	@NotNull
	private LocalDate datRetourCtrCand;

	@Column(name = "tem_demat_ctr_cand", nullable = false)
	@NotNull
	private Boolean temDematCtrCand;

	@Column(name = "lib_ctr_cand", nullable = false, length = 200)
	@Size(max = 200)
	@NotNull
	private String libCtrCand;

	@Column(name = "tem_list_comp_ctr_cand", nullable = false)
	@NotNull
	private Boolean temListCompCtrCand;

	/* Type d'envoie des mails BCC du centre de candidature */
	/* N : pas d'alerte, M : utilisation du mail d'alerte, G : utilisation des mails de la liste des gestionnaires */
	@Column(name = "typ_send_mail_ctr_cand", length = 1, nullable = false)
	@Size(max = 1)
	@NotNull
	private String typSendMailCtrCand;

	@Column(name = "nb_max_voeux_ctr_cand", nullable = false)
	@NotNull
	private Integer nbMaxVoeuxCtrCand;

	@Column(name = "info_comp_ctr_cand", length = 500)
	@Size(max = 500)
	private String infoCompCtrCand;

	@Column(name = "tes_ctr_cand", nullable = false)
	@NotNull
	private Boolean tesCtrCand;

	@Column(name = "user_cre_ctr_cand", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreCtrCand;

	@Column(name = "user_mod_ctr_cand", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModCtrCand;

	@Column(name = "tem_param_ctr_cand", nullable = false)
	@NotNull
	private Boolean temParamCtrCand;

	// bi-directional many-to-one association to TypeDecision
	@ManyToOne
	@JoinColumn(name = "id_typ_dec_fav_list_comp", nullable = true)
	private TypeDecision typeDecisionFavListComp;

	// bi-directional many-to-one association to TypeDecision
	@ManyToOne
	@JoinColumn(name = "id_typ_dec_fav", nullable = false)
	@NotNull
	private TypeDecision typeDecisionFav;

	// bi-directional many-to-one association to Commission
	@OneToMany(mappedBy = "centreCandidature")
	private List<Commission> commissions;

	// bi-directional many-to-one association to PieceJustif
	@OneToMany(mappedBy = "centreCandidature")
	private List<PieceJustif> pieceJustifs;

	// bi-directional many-to-one association to PieceJustif
	@OneToMany(mappedBy = "centreCandidature")
	private List<Formulaire> formulaires;

	// bi-directional many-to-one association to PieceJustif
	@OneToMany(mappedBy = "centreCandidature")
	private List<MotivationAvis> motivationAvis;

	// bi-directional many-to-one association to PieceJustif
	@OneToMany(mappedBy = "centreCandidature")
	private List<Mail> mails;

	// bi-directional many-to-one association to PieceJustif
	@OneToMany(mappedBy = "centreCandidature")
	private List<TypeDecision> typeDecisions;

	// bi-directional many-to-one association to PieceJustif
	@OneToMany(mappedBy = "centreCandidature")
	private List<Tag> tags;

	// bi-directional many-to-one association to Gestionnaire
	@OneToMany(mappedBy = "centreCandidature", cascade = CascadeType.ALL)
	private List<Gestionnaire> gestionnaires;

	@PrePersist
	private void onPrePersist() {
		datCreCtrCand = LocalDateTime.now();
		datModCtrCand = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		datModCtrCand = LocalDateTime.now();
	}

	/** @return le libellé à afficher dans la listBox */
	public String getGenericLibelle() {
		return codCtrCand + "/" + libCtrCand;
	}

	/**
	 * @return les mails d'alerte du centre de candidature
	 */
	public String[] getMailBcc() {
		if (TYP_SEND_MAIL_MAIL_CONTACT.equals(typSendMailCtrCand) && mailContactCtrCand != null) {
			return new String[] { mailContactCtrCand };
		} else if (TYP_SEND_MAIL_LIST_GEST.equals(typSendMailCtrCand) && gestionnaires != null) {
			return gestionnaires.stream().map(e -> e.getDroitProfilInd().getIndividu().getMailInd()).toArray(String[]::new);
		}
		return new String[] {};
	}

	public CentreCandidature() {
		super();
	}

	public CentreCandidature(final String user, final TypeDecision typeDecDefault, final Integer nbVoeuxDefaut, final Boolean temDemat) {
		super();
		userCreCtrCand = user;
		userModCtrCand = user;
		typeDecisionFavListComp = null;
		typeDecisionFav = typeDecDefault;
		nbMaxVoeuxCtrCand = nbVoeuxDefaut;
		tesCtrCand = false;
		temListCompCtrCand = false;
		typSendMailCtrCand = TYP_SEND_MAIL_MAIL_CONTACT;
		temDematCtrCand = temDemat;
		datDebDepotCtrCand = LocalDate.now();
		datFinDepotCtrCand = LocalDate.now();
		datRetourCtrCand = LocalDate.now();

	}

}
