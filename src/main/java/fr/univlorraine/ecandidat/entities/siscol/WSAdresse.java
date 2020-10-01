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
 * The persistent class for the ADRESSE database table.
 */
@Data
@ToString(exclude = { "codAdr" })
@SuppressWarnings("serial")
public class WSAdresse implements Serializable {

	private Integer codAdr;

	private String codBdi;

	private String codCom;

	private String codPay;

	private String libAd1;

	private String libAd2;

	private String libAd3;

	private String libAde;

	private String numTel;

	private String numTelPort;

	public WSAdresse() {
		super();
	}

	public WSAdresse(final Integer codAdr,
		final String codBdi,
		final String codCom,
		final String codPay,
		final String libAd1,
		final String libAd2,
		final String libAd3,
		final String numTel,
		final String numTelPort) {
		super();
		this.codAdr = codAdr;
		this.codBdi = codBdi;
		this.codCom = codCom;
		this.codPay = codPay;
		this.libAd1 = libAd1;
		this.libAd2 = libAd2;
		this.libAd3 = libAd3;
		this.numTel = numTel;
		this.numTelPort = numTelPort;
	}
}
