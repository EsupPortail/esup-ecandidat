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
 * The persistent class for the siScol_bac_oux_equ database table.
 */
@Data
@EqualsAndHashCode(of = { "codBac", "typSiScol" })
@Embeddable
@ToString(of = { "codBac", "typSiScol" })
@SuppressWarnings("serial")
public class SiScolBacOuxEquPK implements Serializable {

	@Column(name = "cod_bac", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String codBac;

	@Column(name = "typ_siscol", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String typSiScol;

	public SiScolBacOuxEquPK() {
		super();
	}

	public SiScolBacOuxEquPK(final String codBac, final String typSiScol) {
		super();
		this.codBac = codBac;
		this.typSiScol = typSiScol;
	}
}
