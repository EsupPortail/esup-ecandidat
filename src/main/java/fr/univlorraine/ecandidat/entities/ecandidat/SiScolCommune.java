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
 * The persistent class for the siScol_commune database table.
 */
@Entity
@Table(name = "siscol_commune")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolCommune implements Serializable {

	@EmbeddedId
	private SiScolCommunePK id;

	@Column(name = "lib_com", nullable = false, length = 32)
	@Size(max = 32)
	@NotNull
	private String libCom;

	@Column(name = "tem_en_sve_com", nullable = false)
	@NotNull
	private Boolean temEnSveCom;

	// bi-directional many-to-one association to Adresse
	@OneToMany(mappedBy = "siScolCommune")
	private List<Adresse> adresses;

	// bi-directional many-to-one association to ApoDepartement
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_dep", referencedColumnName = "cod_dep"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	@NotNull
	private SiScolDepartement siScolDepartement;

	// bi-directional many-to-one association to SiscolEtablissement
	@OneToMany(mappedBy = "siScolCommune")
	private List<SiScolEtablissement> siscolEtablissements;

	// bi-directional many-to-one association to CandidatBacOuEqu
	@OneToMany(mappedBy = "siScolCommune")
	private List<CandidatBacOuEqu> candidatBacOuEqus;

	// bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy = "siScolCommune")
	private List<CandidatCursusPostBac> candidatCursusPostBacs;

	public SiScolCommune() {
		super();
	}

	public SiScolCommune(final String codCom,
		final String libCom,
		final Boolean temEnSveCom,
		final String typSiScol) {
		super();
		this.id = new SiScolCommunePK(codCom, typSiScol);
		this.libCom = libCom;
		this.temEnSveCom = temEnSveCom;
	}

	public SiScolCommune(final String codCom, final String typSiScol) {
		super();
		this.id = new SiScolCommunePK(codCom, typSiScol);
	}
}
