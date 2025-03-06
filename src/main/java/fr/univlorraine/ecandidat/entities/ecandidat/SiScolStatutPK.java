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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the PK of siScol_statut database table.
 */
@Data
@EqualsAndHashCode(of = { "codStu", "typSiScol" })
@Embeddable
@ToString(of = { "codStu", "typSiScol" })
@SuppressWarnings("serial")
public class SiScolStatutPK implements Serializable {

	@Column(name = "cod_stu", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String codStu;

	@Column(name = "typ_siscol", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String typSiScol;

	public SiScolStatutPK() {
		super();
	}

	public SiScolStatutPK(final String codStu, final String typSiScol) {
		super();
		this.codStu = codStu;
		this.typSiScol = typSiScol;
	}
}
