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
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the siScol_dip_aut_cur database table.
 */

@Data
@EqualsAndHashCode(of = { "codDac", "typSiScol" })
@Embeddable
@ToString(of = { "codDac", "typSiScol" })
@SuppressWarnings("serial")
public class SiScolDipAutCurPK implements Serializable {

	@Column(name = "cod_dac", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String codDac;

	@Column(name = "typ_siscol", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String typSiScol;

	public SiScolDipAutCurPK() {
		super();
	}

	public SiScolDipAutCurPK(final String codDac, final String typSiScol) {
		super();
		this.codDac = codDac;
		this.typSiScol = typSiScol;
	}
}
