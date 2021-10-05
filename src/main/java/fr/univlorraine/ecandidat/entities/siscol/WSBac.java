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

import lombok.Data;
import lombok.ToString;

/**
 * Mapper pour le bac provenant du WS
 */
@Data
@ToString(exclude = { "codInd" })
@SuppressWarnings("serial")
public class WSBac implements Serializable {

	private long codInd;

	private String codBac;

	private String codDep;

	private String codCom;

	private String codPays;

	private String codEtb;

	private String codMnb;

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
