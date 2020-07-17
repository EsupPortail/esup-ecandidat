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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siScol_etablissement database table.
 */
@Entity
@Table(name = "siscol_etablissement")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolEtablissement implements Serializable {

	@EmbeddedId
	private SiScolEtablissementPK id;

	@Column(name = "cod_tpe_etb", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String codTpeEtb;

	@Column(name = "lib_etb", nullable = false, length = 40)
	@Size(max = 40)
	@NotNull
	private String libEtb;

	@Column(name = "lib_web_etb", length = 120)
	@Size(max = 120)
	private String libWebEtb;

	@Column(name = "lic_etb", nullable = false, length = 10)
	@Size(max = 10)
	@NotNull
	private String licEtb;

	@Column(name = "tem_en_sve_etb", nullable = false)
	@NotNull
	private Boolean temEnSveEtb;

	// bi-directional many-to-one association to ApoDepartement
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_dep", referencedColumnName = "cod_dep"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	@NotNull
	private SiScolDepartement siScolDepartement;

	// bi-directional many-to-one association to SiScolCommune
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_com", referencedColumnName = "cod_com"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolCommune siScolCommune;

	// bi-directional many-to-one association to CandidatBacOuEqu
	@OneToMany(mappedBy = "siScolEtablissement")
	private List<CandidatBacOuEqu> candidatBacOuEqus;

	// bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy = "siScolEtablissement")
	private List<CandidatCursusPostBac> candidatCursusPostBacs;

	public SiScolEtablissement() {
		super();
	}

	public SiScolEtablissement(final String codEtb,
		final String codTpeEtb,
		final String libEtb,
		final String libWebEtb,
		final String licEtb,
		final Boolean temEnSveEtb,
		final String typSiScol) {
		super();
		this.id = new SiScolEtablissementPK(codEtb, typSiScol);
		this.codTpeEtb = codTpeEtb;
		this.libEtb = libEtb;
		this.libWebEtb = libWebEtb;
		this.licEtb = licEtb;
		this.temEnSveEtb = temEnSveEtb;
	}

}
