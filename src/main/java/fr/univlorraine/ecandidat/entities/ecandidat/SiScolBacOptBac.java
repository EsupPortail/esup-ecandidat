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
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siscol_bac_opt_bac database table.
 */
@Entity
@Table(name = "siscol_bac_opt_bac")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolBacOptBac implements Serializable {

	@EmbeddedId
	private SiScolBacOptBacPK id;

	@Column(name = "cod_bac", insertable = false, updatable = false)
	@NotNull
	private String codBac;

	@Column(name = "cod_opt_bac", insertable = false, updatable = false)
	@NotNull
	private String codOptBac;

	@Column(name = "typ_siscol", insertable = false, updatable = false)
	@NotNull
	private String typSiScol;

	public SiScolBacOptBac() {
		super();
	}

	public SiScolBacOptBac(final String codBac, final String codOptBac, final String typSiScol) {
		super();
		this.id = new SiScolBacOptBacPK(codBac, codOptBac, typSiScol);
		this.codBac = codBac;
		this.codOptBac = codOptBac;
		this.typSiScol = typSiScol;
	}
}
