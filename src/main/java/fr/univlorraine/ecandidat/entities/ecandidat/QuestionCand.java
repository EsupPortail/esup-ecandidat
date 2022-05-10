/**
 *  ESUP-Portail eCand - Copyright (c) 2016 ESUP-Portail consortium
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
import lombok.ToString;

/**
 * The persistent class for the question_cand database table.
 */
@Entity
@Table(name = "question_cand")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = { "candidature" })
@SuppressWarnings("serial")
public class QuestionCand implements Serializable {

	@EmbeddedId
	@NotNull
	private QuestionCandPK id;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_question_cand", nullable = false)
	@NotNull
	private LocalDateTime datCreQuestionCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_question_cand", nullable = false)
	@NotNull
	private LocalDateTime datModQuestionCand;

	@Column(name = "reponse_question_cand", nullable = true, length = 1000)
	@Size(max = 1000)
	private String reponseQuestionCand;

	@Column(name = "user_cre_question_cand", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreQuestionCand;

	@Column(name = "user_mod_question_cand", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModQuestionCand;

	@Column(name = "user_mod_statut_question_cand", length = 50)
	@Size(max = 50)
	private String userModStatutQuestionCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_statut_question_cand")
	private LocalDateTime datModStatutQuestionCand;

	// bi-directional many-to-one association to Candidature
	@ManyToOne
	@JoinColumn(name = "id_cand", nullable = false, insertable = false, updatable = false)
	@NotNull
	private Candidature candidature;

	// bi-directional many-to-one association to Question
	@ManyToOne
	@JoinColumn(name = "id_question", nullable = false, insertable = false, updatable = false)
	@NotNull
	private Question question;

	// bi-directional many-to-one association to TypeStatutPiece
	@ManyToOne
	@JoinColumn(name = "cod_typ_statut_piece", nullable = true)
	private TypeStatutPiece typeStatutPiece;

	@PrePersist
	private void onPrePersist() {
		this.datCreQuestionCand = LocalDateTime.now();
		this.datModQuestionCand = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		this.datModQuestionCand = LocalDateTime.now();
	}

	public QuestionCand(final QuestionCandPK id, final String userCreQuestionCand, final Candidature candidature,
			final Question question) {
		super();
		this.id = id;
		this.userCreQuestionCand = userCreQuestionCand;
		this.candidature = candidature;
		this.question = question;
	}

	public QuestionCand() {
		super();
	}
}
