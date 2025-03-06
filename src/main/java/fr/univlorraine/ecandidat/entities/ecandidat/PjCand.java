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
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the pj_cand database table.
 */
@Entity
@Table(name = "pj_cand")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class PjCand implements Serializable {

	@EmbeddedId
	private PjCandPK id;

	@Column(name = "comment_pj_cand", length = 500)
	@Size(max = 500)
	private String commentPjCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_pj_cand", nullable = false)
	@NotNull
	private LocalDateTime datCrePjCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_pj_cand", nullable = false)
	@NotNull
	private LocalDateTime datModPjCand;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_statut_pj_cand")
	private LocalDateTime datModStatutPjCand;

	@Column(name = "lib_file_pj_cand", length = 500)
	@Size(max = 500)
	private String libFilePjCand;

	@Column(name = "user_cre_pj_cand", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCrePjCand;

	@Column(name = "user_mod_pj_cand", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModPjCand;

	@Column(name = "user_mod_statut_pj_cand", length = 50)
	@Size(max = 50)
	private String userModStatutPjCand;

	/*
	 * @Column(name="tem_concern_pj_cand")
	 * private Boolean temConcernPjCand;
	 */

	// bi-directional many-to-one association to Candidature
	@ManyToOne
	@JoinColumn(name = "id_cand", nullable = false, insertable = false, updatable = false)
	@NotNull
	private Candidature candidature;

	// bi-directional many-to-one association to PieceJustif
	@ManyToOne
	@JoinColumn(name = "id_pj", nullable = false, insertable = false, updatable = false)
	@NotNull
	private PieceJustif pieceJustif;

	// bi-directional many-to-one association to Fichier
	@ManyToOne
	@JoinColumn(name = "id_fichier")
	private Fichier fichier;

	// bi-directional many-to-one association to TypeStatutPiece
	@ManyToOne
	@JoinColumn(name = "cod_typ_statut_piece", nullable = true)
	private TypeStatutPiece typeStatutPiece;

	@PrePersist
	private void onPrePersist() {
		this.datCrePjCand = LocalDateTime.now();
		this.datModPjCand = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		this.datModPjCand = LocalDateTime.now();
	}

	public PjCand(final PjCandPK id, final String userCrePjCand, final Candidature candidature,
			final PieceJustif pieceJustif) {
		super();
		this.id = id;
		this.userCrePjCand = userCrePjCand;
		this.candidature = candidature;
		this.pieceJustif = pieceJustif;
	}

	public PjCand() {
		super();
	}

}
