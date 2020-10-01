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

	private String codEtb;

	private String codMnb;

	private String daaObtBacIba;

	private String temInsAdm;

	public WSBac() {
		super();
	}

	public WSBac(final long codInd,
		final String codBac,
		final String codDep,
		final String codEtb,
		final String codMnb,
		final String daaObtBacIba,
		final String temInsAdm) {
		super();
		this.codInd = codInd;
		this.codBac = codBac;
		this.codDep = codDep;
		this.codEtb = codEtb;
		this.codMnb = codMnb;
		this.daaObtBacIba = daaObtBacIba;
		this.temInsAdm = temInsAdm;
	}
}
