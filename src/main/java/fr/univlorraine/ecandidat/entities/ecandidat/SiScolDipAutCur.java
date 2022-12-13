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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siScol_dip_aut_cur database table.
 */

@Entity
@Table(name = "siscol_dip_aut_cur")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolDipAutCur implements Serializable {

	@EmbeddedId
	private SiScolDipAutCurPK id;

	@Column(name = "lib_dac", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libDac;

	@Column(name = "lic_dac", nullable = false, length = 200)
	@Size(max = 200)
	@NotNull
	private String licDac;

	@Column(name = "tem_en_sve_dac", nullable = false)
	@NotNull
	private Boolean temEnSveDac;

	// bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy = "siScolDipAutCur")
	private List<CandidatCursusPostBac> candidatCursusPostBacs;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.libDac;
	}

	public SiScolDipAutCur() {
		super();
	}

	public SiScolDipAutCur(final String codDac,
		final String libDac,
		final String licDac,
		final Boolean temEnSveDac,
		final String typSiScol) {
		super();
		this.id = new SiScolDipAutCurPK(codDac, typSiScol);
		this.libDac = libDac.toUpperCase();
		this.licDac = licDac.toUpperCase();
		this.temEnSveDac = temEnSveDac;
	}
}
