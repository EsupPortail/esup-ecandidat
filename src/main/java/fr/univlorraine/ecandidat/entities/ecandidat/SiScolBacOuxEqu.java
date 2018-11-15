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
 * The persistent class for the siScol_bac_oux_equ database table.
 */
@Entity
@Table(name = "siscol_bac_oux_equ")
@Data
@EqualsAndHashCode(of = "codBac")
@SuppressWarnings("serial")
public class SiScolBacOuxEqu implements Serializable {

	@Id
	@Column(name = "cod_bac", nullable = false, length = 4)
	@Size(max = 4)
	@NotNull
	private String codBac;

	@Column(name = "lib_bac", nullable = false, length = 40)
	@Size(max = 40)
	@NotNull
	private String libBac;

	@Column(name = "lic_bac", nullable = false, length = 10)
	@Size(max = 10)
	@NotNull
	private String licBac;

	@Column(name = "tem_en_sve_bac", nullable = false)
	@NotNull
	private Boolean temEnSveBac;

	@Column(name = "tem_nat_bac", nullable = false)
	@NotNull
	private Boolean temNatBac;

	@Column(name = "daa_deb_vld_bac", nullable = true, length = 4)
	@Size(max = 4)
	private String daaDebVldBac;

	@Column(name = "daa_fin_vld_bac", nullable = true, length = 4)
	@Size(max = 4)
	private String daaFinVldBac;

	@Column(name = "tem_ctrl_ine_bac", nullable = false)
	@NotNull
	private Boolean temCtrlIneBac;

	@Column(name = "ann_ctrl_ine_bac", nullable = true, length = 4)
	@Size(max = 4)
	private String annCtrlIneBac;

	// bi-directional many-to-one association to CandidatBacOuEqu
	@OneToMany(mappedBy = "siScolBacOuxEqu")
	private List<CandidatBacOuEqu> candidatBacOuEqus;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.libBac;
	}

	public SiScolBacOuxEqu() {
		super();
	}

	public SiScolBacOuxEqu(final String codBac, final String libBac, final String licBac,
			final Boolean temEnSveBac, final Boolean temNatBac, final String daaDebVldBac,
			final String daaFinVldBac, final Boolean temCtrlIneBac, final String annCtrlIneBac) {
		super();
		this.codBac = codBac;
		this.libBac = libBac;
		this.licBac = licBac;
		this.temEnSveBac = temEnSveBac;
		this.temNatBac = temNatBac;
		this.daaDebVldBac = daaDebVldBac;
		this.daaFinVldBac = daaFinVldBac;
		this.temCtrlIneBac = temCtrlIneBac;
		this.annCtrlIneBac = annCtrlIneBac;
	}
}
