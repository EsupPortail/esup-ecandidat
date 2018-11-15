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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siScol_mention database table.
 */
@Entity
@Table(name = "siscol_mention")
@Data
@EqualsAndHashCode(of = "codMen")
@SuppressWarnings("serial")
public class SiScolMention implements Serializable {

	@Id
	@Column(name = "cod_men", nullable = false, length = 2)
	@Size(max = 2)
	@NotNull
	private String codMen;

	@Column(name = "lib_men", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String libMen;

	@Column(name = "lic_men", nullable = false, length = 10)
	@Size(max = 10)
	@NotNull
	private String licMen;

	@Column(name = "tem_en_sve_men", nullable = false)
	@NotNull
	private Boolean temEnSveMen;

	// bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy = "siScolMention")
	private List<CandidatCursusPostBac> candidatCursusPostBacs;

	// bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy = "siScolMention")
	private List<CandidatCursusInterne> candidatCursusInternes;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.libMen;
	}

	public SiScolMention() {
		super();
	}

	public SiScolMention(final String codMen, final String libMen, final String licMen,
			final Boolean temEnSveMen) {
		super();
		this.codMen = codMen;
		this.libMen = libMen;
		this.licMen = licMen;
		this.temEnSveMen = temEnSveMen;
	}
}
