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
import javax.persistence.ManyToMany;
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
 * The persistent class for the formulaire database table.
 */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "formulaire")
@Data
@EqualsAndHashCode(of = "idFormulaire")
@ToString(of = {"idFormulaire", "codFormulaire", "libFormulaire", "idFormulaireLimesurvey", "tesFormulaire"})
@SuppressWarnings("serial")
public class Formulaire implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_formulaire", nullable = false)
	private Integer idFormulaire;

	@Column(name = "id_formulaire_limesurvey", unique = true, nullable = false)
	@NotNull
	private Integer idFormulaireLimesurvey;

	@Column(name = "cod_formulaire", unique = true, nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String codFormulaire;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_formulaire", nullable = false)
	@NotNull
	private LocalDateTime datCreFormulaire;

	@Column(name = "lib_formulaire", nullable = false, length = 50)
	@NotNull
	@Size(max = 50)
	private String libFormulaire;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_formulaire", nullable = false)
	@NotNull
	private LocalDateTime datModFormulaire;

	// bi-directional many-to-one association to CentreCandidature
	@ManyToOne
	@JoinColumn(name = "id_ctr_cand")
	private CentreCandidature centreCandidature;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_lib_formulaire", nullable = false)
	@NotNull
	private I18n i18nLibFormulaire;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_url_formulaire", nullable = false)
	@NotNull
	private I18n i18nUrlFormulaire;

	@Column(name = "tem_commun_formulaire", nullable = false)
	@NotNull
	private Boolean temCommunFormulaire;

	@Column(name = "tem_conditionnel_formulaire", nullable = false)
	@NotNull
	private Boolean temConditionnelFormulaire;

	@Column(name = "tes_formulaire", nullable = false)
	@NotNull
	private Boolean tesFormulaire;

	@Column(name = "user_cre_formulaire", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreFormulaire;

	@Column(name = "user_mod_formulaire", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModFormulaire;

	// bi-directional many-to-many association to Formation
	@ManyToMany(mappedBy = "formulaires")
	private List<Formation> formations;

	// bi-directional many-to-one association to FormulaireCand
	@OneToMany(mappedBy = "formulaire")
	private List<FormulaireCand> formulaireCands;

	@PrePersist
	private void onPrePersist() {
		this.datCreFormulaire = LocalDateTime.now();
		this.datModFormulaire = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		this.datModFormulaire = LocalDateTime.now();
	}

	public Formulaire() {
		super();
	}

	public Formulaire(final String user) {
		super();
		this.userCreFormulaire = user;
		this.userModFormulaire = user;
		this.tesFormulaire = false;
		this.temCommunFormulaire = false;
		this.temConditionnelFormulaire = false;
	}
}
