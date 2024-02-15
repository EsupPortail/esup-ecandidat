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
import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the type_decision database table.
 */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "type_decision")
@Data
@EqualsAndHashCode(of = "idTypDec")
@ToString(of = {"idTypDec", "codTypDec", "libTypDec", "tesTypDec"})
@SuppressWarnings("serial")
public class TypeDecision implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_typ_dec", nullable = false)
	private Integer idTypDec;

	@Column(name = "cod_typ_dec", unique = true, nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String codTypDec;

	@Column(name = "lib_typ_dec", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String libTypDec;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_typ_dec", nullable = false)
	@NotNull
	private LocalDateTime datCreTypDec;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_typ_dec", nullable = false)
	@NotNull
	private LocalDateTime datModTypDec;

	@Column(name = "tem_model_typ_dec", nullable = false)
	@NotNull
	private Boolean temModelTypDec;

	@Column(name = "tem_definitif_typ_dec", nullable = false)
	@NotNull
	private Boolean temDefinitifTypDec;

	@Column(name = "tem_deverse_opi_typ_dec", nullable = false)
	@NotNull
	private Boolean temDeverseOpiTypDec;

	@Column(name = "tem_aff_comment_typ_dec", nullable = false)
	@NotNull
	private Boolean temAffCommentTypDec;

	@Column(name = "tes_typ_dec", nullable = false)
	@NotNull
	private Boolean tesTypDec;

	@Column(name = "user_cre_typ_dec", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreTypDec;

	@Column(name = "user_mod_typ_dec", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModTypDec;

	// bi-directional many-to-one association to CentreCandidature
	@ManyToOne
	@JoinColumn(name = "id_ctr_cand")
	private CentreCandidature centreCandidature;

	// bi-directional many-to-one association to CentreCandidature
	@OneToMany(mappedBy = "typeDecisionFavListComp")
	private List<CentreCandidature> centreCandidaturesFavListComp;

	// bi-directional many-to-one association to CentreCandidature
	@OneToMany(mappedBy = "typeDecisionFav")
	private List<CentreCandidature> centreCandidaturesFav;

	// bi-directional many-to-one association to Formation
	@OneToMany(mappedBy = "typeDecisionFav")
	private List<Formation> formationsFav;

	// bi-directional many-to-one association to Formation
	@OneToMany(mappedBy = "typeDecisionFavListComp")
	private List<Formation> formationsFavListComp;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_lib_typ_dec", nullable = false)
	@NotNull
	private I18n i18nLibTypDec;

	// bi-directional many-to-one association to Mail
	@ManyToOne
	@JoinColumn(name = "id_mail", nullable = false)
	@NotNull
	private Mail mail;

	// bi-directional many-to-one association to TypeAvis
	@ManyToOne
	@JoinColumn(name = "cod_typ_avis", nullable = false)
	@NotNull
	private TypeAvis typeAvis;

	// bi-directional many-to-one association to TypeDecisionCandidature
	@OneToMany(mappedBy = "typeDecision")
	private List<TypeDecisionCandidature> typeDecisionCandidatures;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.codTypDec + "/" + this.libTypDec;
	}

	@PrePersist
	private void onPrePersist() {
		this.datCreTypDec = LocalDateTime.now();
		this.datModTypDec = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		this.datModTypDec = LocalDateTime.now();
	}

	public TypeDecision() {
		super();
		this.temModelTypDec = false;
		this.temDefinitifTypDec = false;
		this.temDeverseOpiTypDec = false;
		this.temAffCommentTypDec = true;
		this.tesTypDec = false;
	}

	public TypeDecision(final String user) {
		this();
		this.userCreTypDec = user;
		this.userModTypDec = user;
	}

	public TypeDecision(final String codTypDec, final String libTypDec, final Boolean temModelTypDec,
			final Boolean temDefinitifTypDec, final Boolean temDeverseOpiTypDec,
			final Boolean tesTypDec, final String userCreTypDec, final String userModTypDec,
			final TypeAvis typeAvis, final Mail mail) {
		super();
		this.temAffCommentTypDec = true;
		this.codTypDec = codTypDec;
		this.libTypDec = libTypDec;
		this.temModelTypDec = temModelTypDec;
		this.temDefinitifTypDec = temDefinitifTypDec;
		this.temDeverseOpiTypDec = temDeverseOpiTypDec;
		this.tesTypDec = tesTypDec;
		this.userCreTypDec = userCreTypDec;
		this.userModTypDec = userModTypDec;
		this.typeAvis = typeAvis;
		this.mail = mail;
	}

}
