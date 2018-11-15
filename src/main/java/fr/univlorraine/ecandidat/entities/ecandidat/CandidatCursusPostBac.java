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

/**
 * The persistent class for the candidat_cursus_post_bac database table.
 */
@Entity
@Table(name = "candidat_cursus_post_bac")
@Data
@EqualsAndHashCode(of = "idCursus")
@ToString(exclude = {"candidat"})
@SuppressWarnings("serial")
public class CandidatCursusPostBac implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cursus", nullable = false)
	private Integer idCursus;

	@Column(name = "annee_univ_cursus", nullable = false)
	@NotNull
	private Integer anneeUnivCursus;

	@Column(name = "lib_cursus", length = 255, nullable = false)
	@Size(max = 255)
	@NotNull
	private String libCursus;

	@Column(name = "obtenu_cursus", length = 1, nullable = false)
	@Size(max = 1)
	@NotNull
	private String obtenuCursus;

	// bi-directional many-to-one association to SiScolCommune
	@ManyToOne
	@JoinColumn(name = "cod_com")
	private SiScolCommune siScolCommune;

	// bi-directional many-to-one association to SiScolDepartement
	@ManyToOne
	@JoinColumn(name = "cod_dep")
	private SiScolDepartement siScolDepartement;

	// bi-directional many-to-one association to SiScolDipAutCur
	@ManyToOne
	@JoinColumn(name = "cod_dac", nullable = false)
	@NotNull
	private SiScolDipAutCur siScolDipAutCur;

	// bi-directional many-to-one association to SiScolEtablissement
	@ManyToOne
	@JoinColumn(name = "cod_etb")
	private SiScolEtablissement siScolEtablissement;

	// bi-directional many-to-one association to SiScolMention
	@ManyToOne
	@JoinColumn(name = "cod_men")
	private SiScolMention siScolMention;

	// bi-directional many-to-one association to SiScolPays
	@ManyToOne
	@JoinColumn(name = "cod_pay", nullable = false)
	@NotNull
	private SiScolPays siScolPays;

	// bi-directional many-to-one association to Candidat
	@ManyToOne
	@JoinColumn(name = "id_candidat", nullable = false)
	@NotNull
	private Candidat candidat;
}
