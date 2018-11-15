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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** The persistent class for the post-it database table. */
@Entity
@Table(name = "post_it")
@Data
@EqualsAndHashCode(of = "idPostIt")
@SuppressWarnings("serial")
public class PostIt implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_post_it", nullable = false)
	private Integer idPostIt;

	@Column(name = "user_cre_post_it", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCrePostIt;

	@Column(name = "message_post_it", nullable = false, length = 255)
	@Size(max = 255)
	@NotNull
	private String messagePostIt;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_post_it")
	private LocalDateTime datCrePostIt;

	// bi-directional many-to-one association to Candidature
	@ManyToOne
	@JoinColumn(name = "id_cand", nullable = false)
	@NotNull
	private Candidature candidature;

	@PrePersist
	private void onPrePersist() {
		this.datCrePostIt = LocalDateTime.now();
	}

	public PostIt() {
		super();
	}

	public PostIt(final String userCrePostIt, final Candidature candidature) {
		super();
		this.userCrePostIt = userCrePostIt;
		this.candidature = candidature;
	}

}
