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
package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

/**
 * The persistent class for the IND_BAC database table.
 */
@Entity
@Table(name = "IND_BAC")
@Data
@ToString(exclude = { "codInd" })
@SuppressWarnings("serial")
public class WSBac implements Serializable {

	@Id
	@Column(name = "COD_IND")
	private long codInd;

	@Column(name = "COD_BAC", length = 4)
	private String codBac;

	@Column(name = "COD_DEP", length = 3)
	private String codDep;

	@Column(name = "COD_ETB", length = 8)
	private String codEtb;

	@Column(name = "COD_MNB", length = 2)
	private String codMnb;

	@Column(name = "DAA_OBT_BAC_IBA", length = 4)
	private String daaObtBacIba;

	@Column(name = "TEM_INS_ADM", nullable = false, length = 1)
	private String temInsAdm;

	@Column(name = "COD_SPE_BAC_PRE")
	private String codSpeBacPre;

	@Column(name = "COD_SPE_1_BAC")
	private String codSpe1Bac;

	@Column(name = "COD_SPE_2_BAC")
	private String codSpe2Bac;

	@Column(name = "COD_OPT_1_BAC")
	private String codOpt1Bac;

	@Column(name = "COD_OPT_2_BAC")
	private String codOpt2Bac;

	@Column(name = "COD_OPT_3_BAC")
	private String codOpt3Bac;

	@Column(name = "COD_OPT_4_BAC")
	private String codOpt4Bac;

	public WSBac() {
		super();
	}

	public WSBac(final long codInd,
		final String codBac,
		final String codDep,
		final String codEtb,
		final String codMnb,
		final String daaObtBacIba,
		final String temInsAdm,
		final String codSpeBacPre,
		final String codSpe1Bac,
		final String codSpe2Bac,
		final String codOpt1Bac,
		final String codOpt2Bac,
		final String codOpt3Bac,
		final String codOpt4Bac) {
		super();
		this.codInd = codInd;
		this.codBac = codBac;
		this.codDep = codDep;
		this.codEtb = codEtb;
		this.codMnb = codMnb;
		this.daaObtBacIba = daaObtBacIba;
		this.temInsAdm = temInsAdm;
		this.codSpeBacPre = codSpeBacPre;
		this.codSpe1Bac = codSpe1Bac;
		this.codSpe2Bac = codSpe2Bac;
		this.codOpt1Bac = codOpt1Bac;
		this.codOpt2Bac = codOpt2Bac;
		this.codOpt3Bac = codOpt3Bac;
		this.codOpt4Bac = codOpt4Bac;
	}

}
