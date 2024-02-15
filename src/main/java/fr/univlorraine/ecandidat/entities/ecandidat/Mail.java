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
 * The persistent class for the mail database table.
 */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "mail")
@Data
@EqualsAndHashCode(of = "idMail")
@ToString(of = {"idMail", "codMail", "libMail", "tesMail"})
@SuppressWarnings("serial")
public class Mail implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_mail", nullable = false)
	private Integer idMail;

	@Column(name = "cod_mail", unique = true, nullable = false, length = 30)
	@Size(max = 30)
	@NotNull
	private String codMail;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_mail", nullable = false)
	@NotNull
	private LocalDateTime datCreMail;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_mail", nullable = false)
	@NotNull
	private LocalDateTime datModMail;

	@Column(name = "lib_mail", nullable = false, length = 100)
	@Size(max = 100)
	@NotNull
	private String libMail;

	@Column(name = "tem_is_modele_mail", nullable = false)
	@NotNull
	private Boolean temIsModeleMail;

	@Column(name = "tes_mail", nullable = false)
	@NotNull
	private Boolean tesMail;

	@Column(name = "user_cre_mail", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreMail;

	@Column(name = "user_mod_mail", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModMail;

	// bi-directional many-to-one association to CentreCandidature
	@ManyToOne
	@JoinColumn(name = "id_ctr_cand")
	private CentreCandidature centreCandidature;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_corps_mail", nullable = false)
	@NotNull
	private I18n i18nCorpsMail;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_sujet_mail", nullable = false)
	@NotNull
	private I18n i18nSujetMail;

	// bi-directional many-to-one association to TypeAvis
	@ManyToOne
	@JoinColumn(name = "cod_typ_avis")
	private TypeAvis typeAvis;

	// bi-directional many-to-one association to TypeDecision
	@OneToMany(mappedBy = "mail")
	private List<TypeDecision> typeDecisions;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.codMail + "/" + this.libMail;
	}

	@PrePersist
	private void onPrePersist() {
		this.datCreMail = LocalDateTime.now();
		this.datModMail = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		this.datModMail = LocalDateTime.now();
	}

	public Mail() {
		super();
		this.temIsModeleMail = false;
		this.tesMail = false;
	}

	public Mail(final String user) {
		this();
		this.userCreMail = user;
		this.userModMail = user;
	}

	public Mail(final String codMail, final String libMail, final Boolean temIsModeleMail,
			final Boolean tesMail, final String userCreMail, final String userModMail, final TypeAvis typeAvis) {
		super();
		this.codMail = codMail;
		this.libMail = libMail;
		this.temIsModeleMail = temIsModeleMail;
		this.tesMail = tesMail;
		this.userCreMail = userCreMail;
		this.userModMail = userModMail;
		this.typeAvis = typeAvis;
	}

}
