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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siscol_bac_spe_bac database table.
 */
@Entity
@Table(name = "siscol_bac_spe_bac")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolBacSpeBac implements Serializable {

	@EmbeddedId
	private SiScolBacSpeBacPK id;

	@Column(name = "cod_bac", insertable = false, updatable = false)
	@NotNull
	private String codBac;

	@Column(name = "cod_spe_bac", insertable = false, updatable = false)
	@NotNull
	private String codSpeBac;

	@Column(name = "typ_siscol", insertable = false, updatable = false)
	@NotNull
	private String typSiScol;

	public SiScolBacSpeBac() {
		super();
	}

	public SiScolBacSpeBac(final String codBac, final String codSpeBac, final String typSiScol) {
		super();
		this.id = new SiScolBacSpeBacPK(codBac, codSpeBac, typSiScol);
		this.codBac = codBac;
		this.codSpeBac = codSpeBac;
		this.typSiScol = typSiScol;
	}

}
