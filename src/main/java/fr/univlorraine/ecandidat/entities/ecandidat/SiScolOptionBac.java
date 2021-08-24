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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siscol_option_bac database table.
 */
@Entity
@Table(name = "siscol_option_bac")
@Data
@EqualsAndHashCode(of = "codOptBac")
@SuppressWarnings("serial")
public class SiScolOptionBac implements Serializable {

	@Id
	@Column(name = "cod_opt_bac", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String codOptBac;

	@Column(name = "lib_opt_bac", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libOptBac;

	@Column(name = "lic_opt_bac", nullable = false, length = 200)
	@Size(max = 200)
	@NotNull
	private String licOptBac;

	@Column(name = "tem_en_sve_opt_bac", nullable = false)
	@NotNull
	private Boolean temEnSveOptBac;

	@Column(name = "daa_deb_val_opt_bac", nullable = true, length = 4)
	@Size(max = 4)
	private String daaDebValOptBac;

	@Column(name = "daa_fin_val_opt_bac", nullable = true, length = 4)
	@Size(max = 4)
	private String daaFinValOptBac;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.libOptBac;
	}

	public SiScolOptionBac() {
		super();
	}

	public SiScolOptionBac(@Size(max = 4) @NotNull final String codOptBac,
		@Size(max = 40) @NotNull final String libOptBac,
		@Size(max = 10) @NotNull final String licOptBac,
		@NotNull final Boolean temEnSveOptBac,
		@Size(max = 4) final String daaDebValOptBac,
		@Size(max = 4) final String daaFinValOptBac) {
		super();
		this.codOptBac = codOptBac;
		this.libOptBac = libOptBac;
		this.licOptBac = licOptBac;
		this.temEnSveOptBac = temEnSveOptBac;
		this.daaDebValOptBac = daaDebValOptBac;
		this.daaFinValOptBac = daaFinValOptBac;
	}

}
