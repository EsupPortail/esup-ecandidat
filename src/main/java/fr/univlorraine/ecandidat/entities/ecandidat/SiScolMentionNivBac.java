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
 * The persistent class for the siScol_mention_niv_bac database table.
 */
@Entity
@Table(name = "siscol_mention_niv_bac")
@Data
@EqualsAndHashCode(of = "codMnb")
@SuppressWarnings("serial")
public class SiScolMentionNivBac implements Serializable {

	@Id
	@Column(name = "cod_mnb", nullable = false, length = 2)
	@Size(max = 2)
	@NotNull
	private String codMnb;

	@Column(name = "lib_mnb", nullable = false, length = 40)
	@Size(max = 40)
	@NotNull
	private String libMnb;

	@Column(name = "lic_mnb", nullable = false, length = 10)
	@Size(max = 10)
	@NotNull
	private String licMnb;

	@Column(name = "tem_en_sve_mnb", nullable = false)
	@NotNull
	private Boolean temEnSveMnb;

	// bi-directional many-to-one association to CandidatBacOuEqu
	@OneToMany(mappedBy = "siScolMentionNivBac")
	private List<CandidatBacOuEqu> candidatBacOuEqus;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.libMnb;
	}

	public SiScolMentionNivBac() {
		super();
	}

	public SiScolMentionNivBac(final String codMnb, final String libMnb, final String licMnb,
			final Boolean temEnSveMnb) {
		super();
		this.codMnb = codMnb;
		this.libMnb = libMnb;
		this.licMnb = licMnb;
		this.temEnSveMnb = temEnSveMnb;
	}
}
