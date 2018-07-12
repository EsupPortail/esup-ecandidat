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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** The persistent class for the candidat_cursus_interne database table. */
@Entity
@Table(name = "candidat_cursus_interne")
@Data
@EqualsAndHashCode(of = "idCursusInterne")
@ToString(exclude = {"candidat"})
public class CandidatCursusInterne implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cursus_interne", unique = true, nullable = false)
	private Integer idCursusInterne;

	@Column(name = "annee_univ_cursus_interne")
	private Integer anneeUnivCursusInterne;

	@Column(name = "cod_vet_cursus_interne", length = 100)
	@Size(max = 100)
	private String codVetCursusInterne;

	@Column(name = "lib_cursus_interne", length = 255)
	@Size(max = 255)
	private String libCursusInterne;

	@Column(name = "not_vet_cursus_interne", length = 20)
	@Size(max = 20)
	private String notVetCursusInterne;

	// bi-directional many-to-one association to SiScolMention
	@ManyToOne
	@JoinColumn(name = "cod_men_cursus_interne")
	private SiScolMention siScolMention;

	// bi-directional many-to-one association to SiScolMention
	@ManyToOne
	@JoinColumn(name = "cod_tre_cursus_interne")
	private SiScolTypResultat siScolTypResultat;

	// bi-directional many-to-one association to Candidat
	@ManyToOne
	@JoinColumn(name = "id_candidat", nullable = false)
	@NotNull
	private Candidat candidat;

	public CandidatCursusInterne() {
		super();
	}

	public CandidatCursusInterne(final Integer anneeUnivCursusInterne,
			final String codVetCursusInterne, final String libCursusInterne,
			final SiScolTypResultat siScolTypResultat, final SiScolMention siScolMention,
			final Candidat candidat, final String notVetCursusInterne) {
		super();
		this.anneeUnivCursusInterne = anneeUnivCursusInterne;
		this.codVetCursusInterne = codVetCursusInterne;
		this.libCursusInterne = libCursusInterne;
		this.siScolTypResultat = siScolTypResultat;
		this.siScolMention = siScolMention;
		this.candidat = candidat;
		this.notVetCursusInterne = notVetCursusInterne;
	}
}
