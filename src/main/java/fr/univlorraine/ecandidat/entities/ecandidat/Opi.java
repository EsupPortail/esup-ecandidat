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

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the opi_attente database table.
 */
@Entity
@Table(name = "opi")
@Data
@EqualsAndHashCode(of = "idCand")
@SuppressWarnings("serial")
public class Opi implements Serializable {

	@Id
	@Column(name = "id_cand", nullable = false)
	private Integer idCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_opi", nullable = false)
	@NotNull
	private LocalDateTime datCreOpi;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_passage_opi")
	private LocalDateTime datPassageOpi;

	@Column(name = "cod_opi", nullable = true, length = 10)
	@Size(max = 10)
	private String codOpi;

	// bi-directional one-to-one association to Candidature
	@OneToOne
	@JoinColumn(name = "id_cand", insertable = false, updatable = false)
	private Candidature candidature;

	@PrePersist
	private void onPrePersist() {
		datCreOpi = LocalDateTime.now();
	}

	public Opi(final Candidature candidature) {
		this.candidature = candidature;
		idCand = candidature.getIdCand();
	}

	public Opi() {
		super();
	}
}
