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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the pj_candidat database table.
 */
@Entity
@Table(name = "pj_candidat")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"candidat"})
@SuppressWarnings("serial")
public class PjCandidat implements Serializable {

	@EmbeddedId
	@NotNull
	private PjCandidatPK id;

	@Column(name = "nom_fic_pj_candidat", nullable = false)
	@Size(max = 30)
	@NotNull
	private String nomFicPjCandidat;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_exp_pj_candidat")
	private LocalDateTime datExpPjCandidat;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_pj_candidat")
	private LocalDateTime datCrePjCandidat;

	// bi-directional many-to-one association to Candidat
	@ManyToOne
	@JoinColumn(name = "id_candidat", nullable = false, insertable = false, updatable = false)
	@NotNull
	private Candidat candidat;

	@PrePersist
	private void onPrePersist() {
		this.datCrePjCandidat = LocalDateTime.now();
	}
}
