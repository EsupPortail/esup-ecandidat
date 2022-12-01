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
import javax.persistence.JoinColumns;
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
@ToString(exclude = { "candidat" })
@SuppressWarnings("serial")
public class CandidatCursusPostBac implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_cursus", nullable = false)
	private Integer idCursus;

	@Column(name = "typ_siscol", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String typSiScol;

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
	@JoinColumns({
		@JoinColumn(name = "cod_com", referencedColumnName = "cod_com"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolCommune siScolCommune;

	// bi-directional many-to-one association to SiScolDepartement
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_dep", referencedColumnName = "cod_dep"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolDepartement siScolDepartement;

	// bi-directional many-to-one association to SiScolDipAutCur
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_dac", referencedColumnName = "cod_dac"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	@NotNull
	private SiScolDipAutCur siScolDipAutCur;

	// bi-directional many-to-one association to SiScolEtablissement
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_etb", referencedColumnName = "cod_etb"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolEtablissement siScolEtablissement;

	// bi-directional many-to-one association to SiScolMention
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_men", referencedColumnName = "cod_men"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolMention siScolMention;

	// bi-directional many-to-one association to SiScolPays
	@ManyToOne
	@NotNull
	@JoinColumns({
		@JoinColumn(name = "cod_pay", referencedColumnName = "cod_pay"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolPays siScolPays;

	// bi-directional many-to-one association to Candidat
	@ManyToOne
	@JoinColumn(name = "id_candidat", nullable = false)
	@NotNull
	private Candidat candidat;
}
