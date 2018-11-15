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

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the formulaire_cand database table.
 */
@Entity
@Table(name = "formulaire_cand")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class FormulaireCand implements Serializable {

	@EmbeddedId
	private FormulaireCandPK id;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_formulaire_cand", nullable = false)
	@NotNull
	private LocalDateTime datCreFormulaireCand;

	@Column(name = "user_cre_formulaire_cand", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreFormulaireCand;

	@Column(name = "user_mod_formulaire_cand", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModFormulaireCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_formulaire_cand", nullable = false)
	@NotNull
	private LocalDateTime datModFormulaireCand;

	// bi-directional many-to-one association to Candidature
	@ManyToOne
	@JoinColumn(name = "id_cand", nullable = false, insertable = false, updatable = false)
	@NotNull
	private Candidature candidature;

	// bi-directional many-to-one association to Formulaire
	@ManyToOne
	@JoinColumn(name = "id_formulaire", nullable = false, insertable = false, updatable = false)
	@NotNull
	private Formulaire formulaire;

	@PrePersist
	private void onPrePersist() {
		this.datCreFormulaireCand = LocalDateTime.now();
		this.datModFormulaireCand = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		this.datModFormulaireCand = LocalDateTime.now();
	}

	public FormulaireCand(final FormulaireCandPK id, final String userCreFormulaireCand, final Candidature candidature,
			final Formulaire formulaire) {
		super();
		this.id = id;
		this.userCreFormulaireCand = userCreFormulaireCand;
		this.candidature = candidature;
		this.formulaire = formulaire;
	}

	public FormulaireCand() {
		super();
	}
}
