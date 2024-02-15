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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the formulaire_cand database table.
 */
@Entity
@Table(name = "formulaire_candidature")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "candidature" })
@SuppressWarnings("serial")
public class FormulaireCandidature implements Serializable {

	@EmbeddedId
	@NotNull
	private FormulaireCandidaturePK id;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_formulaire_cand", nullable = false)
	@NotNull
	private LocalDateTime datCreFormulaireCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_formulaire_cand", nullable = false)
	@NotNull
	private LocalDateTime datModFormulaireCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_reponse_formulaire_cand", nullable = false)
	@NotNull
	private LocalDateTime datReponseFormulaireCand;

	@Lob
	@Column(name = "reponses_formulaire_cand", nullable = true, columnDefinition = "TEXT")
	private String reponsesFormulaireCand;

	// bi-directional many-to-one association to Candidature
	@ManyToOne
	@JoinColumn(name = "id_cand", nullable = false, insertable = false, updatable = false)
	@NotNull
	private Candidature candidature;

	@PrePersist
	private void onPrePersist() {
		this.datCreFormulaireCand = LocalDateTime.now();
		this.datModFormulaireCand = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		this.datModFormulaireCand = LocalDateTime.now();
	}
}
