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
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siscol_specialite_bac database table.
 */
@Entity
@Table(name = "siscol_specialite_bac")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolSpecialiteBac implements Serializable {

	@EmbeddedId
	private SiScolSpecialiteBacPK id;

	@Column(name = "lib_spe_bac", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libSpeBac;

	@Column(name = "lic_spe_bac", nullable = false, length = 200)
	@Size(max = 200)
	@NotNull
	private String licSpeBac;

	@Column(name = "tem_en_sve_spe_bac", nullable = false)
	@NotNull
	private Boolean temEnSveSpeBac;

	@Column(name = "daa_deb_val_spe_bac", nullable = true, length = 4)
	@Size(max = 4)
	private String daaDebValSpeBac;

	@Column(name = "daa_fin_val_spe_bac", nullable = true, length = 4)
	@Size(max = 4)
	private String daaFinValSpeBac;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.libSpeBac;
	}

	public SiScolSpecialiteBac() {
		super();
	}

	public SiScolSpecialiteBac(final String codSpeBac,
		final String libSpeBac,
		final String licSpeBac,
		final Boolean temEnSveSpeBac,
		final String daaDebValSpeBac,
		final String daaFinValSpeBac,
		final String typSiScol) {
		super();
		this.id = new SiScolSpecialiteBacPK(codSpeBac, typSiScol);
		this.libSpeBac = libSpeBac;
		this.licSpeBac = licSpeBac;
		this.temEnSveSpeBac = temEnSveSpeBac;
		this.daaDebValSpeBac = daaDebValSpeBac;
		this.daaFinValSpeBac = daaFinValSpeBac;
	}

}
