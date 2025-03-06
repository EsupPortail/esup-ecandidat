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

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siScol_departement database table.
 */
@Entity
@Table(name = "siscol_departement")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolDepartement implements Serializable {

	@EmbeddedId
	private SiScolDepartementPK id;

	@Column(name = "cod_dep", length = 50, insertable = false, updatable = false)
	@Size(max = 50)
	private String codDep;

	@Column(name = "lib_dep", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libDep;

	@Column(name = "lic_dep", nullable = false, length = 200)
	@Size(max = 200)
	@NotNull
	private String licDep;

	@Column(name = "tem_en_sve_dep", nullable = false)
	@NotNull
	private Boolean temEnSveDep;

	// bi-directional many-to-one association to Candidat
	@OneToMany(mappedBy = "siScolDepartement")
	private List<Candidat> candidats;

	// bi-directional many-to-one association to ApoEtablissement
	@OneToMany(mappedBy = "siScolDepartement")
	private List<SiScolEtablissement> siScolEtablissements;

	// bi-directional many-to-one association to CandidatBacOuEqu
	@OneToMany(mappedBy = "siScolDepartement")
	private List<CandidatBacOuEqu> candidatBacOuEqus;

	// bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy = "siScolDepartement")
	private List<CandidatCursusPostBac> candidatCursusPostBacs;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.id.getCodDep() + "/" + this.libDep;
	}

	public SiScolDepartement() {
		super();
	}

	public SiScolDepartement(final String codDep, final String typSiScol) {
		super();
		this.id = new SiScolDepartementPK(codDep, typSiScol);
	}

	public SiScolDepartement(final String codDep,
		final String libDep,
		final String licDep,
		final Boolean temEnSveDep,
		final String typSiScol) {
		super();
		this.id = new SiScolDepartementPK(codDep, typSiScol);
		this.libDep = libDep.toUpperCase();
		this.licDep = licDep.toUpperCase();
		this.temEnSveDep = temEnSveDep;
	}
}
