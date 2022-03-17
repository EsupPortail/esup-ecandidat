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

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the question database table.
 */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "question")
@Data
@EqualsAndHashCode(of = "idQuestion")
@ToString(of = { "idQuestion", "codQuestion", "libQuestion", "tesQuestion" })
@SuppressWarnings("serial")
public class Question implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_question", nullable = false)
	private Integer idQuestion;

	@Column(name = "typ_question", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String typQuestion;

	@Column(name = "cod_question", unique = true, nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String codQuestion;

	@Column(name = "lib_question", nullable = false, length = 50)
	@NotNull
	@Size(max = 50)
	private String libQuestion;

	@Column(name = "tes_question", nullable = false)
	@NotNull
	private Boolean tesQuestion;

	@Column(name = "tem_commun_question", nullable = false)
	@NotNull
	private Boolean temCommunQuestion;

	@Column(name = "tem_conditionnel_question", nullable = false)
	@NotNull
	private Boolean temConditionnelQuestion;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_question", nullable = false)
	@NotNull
	private LocalDateTime datCreQuestion;

	@Column(name = "user_cre_question", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreQuestion;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_question", nullable = false)
	@NotNull
	private LocalDateTime datModQuestion;

	@Column(name = "user_mod_question", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModQuestion;

	// bi-directional many-to-one association to CentreCandidature
	@ManyToOne
	@JoinColumn(name = "id_ctr_cand")
	private CentreCandidature centreCandidature;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_lib_question", nullable = false)
	@NotNull
	private I18n i18nLibQuestion;

	// bi-directional many-to-many association to Formation
	@ManyToMany(mappedBy = "questions")
	private List<Formation> formations;

	// bi-directional many-to-one association to QuestionCand
	@OneToMany(mappedBy = "question")
	private List<QuestionCand> questionCands;

	@PrePersist
	private void onPrePersist() {
		this.datCreQuestion = LocalDateTime.now();
		this.datModQuestion = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		this.datModQuestion = LocalDateTime.now();
	}

	public Question() {
		super();
	}

	public Question(final String user) {
		super();
		this.userCreQuestion = user;
		this.userModQuestion = user;
		this.tesQuestion = false;
		this.temCommunQuestion = false;
		this.temConditionnelQuestion = false;
	}
}
