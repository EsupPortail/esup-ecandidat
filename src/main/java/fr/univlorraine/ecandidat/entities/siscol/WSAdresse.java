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
 * The persistent class for the ADRESSE database table.
 */
@Entity
@Table(name = "ADRESSE")
@Data
@ToString(exclude = {"codAdr"})
@SuppressWarnings("serial")
public class WSAdresse implements Serializable {

	@Id
	@Column(name = "COD_ADR", unique = true, nullable = false)
	private Integer codAdr;

	@Column(name = "COD_BDI", length = 5)
	private String codBdi;

	@Column(name = "COD_COM", length = 5)
	private String codCom;

	@Column(name = "COD_PAY", nullable = false, length = 3)
	private String codPay;

	@Column(name = "LIB_AD1", length = 32)
	private String libAd1;

	@Column(name = "LIB_AD2", length = 32)
	private String libAd2;

	@Column(name = "LIB_AD3", length = 32)
	private String libAd3;

	@Column(name = "LIB_ADE", length = 32)
	private String libAde;

	@Column(name = "NUM_TEL", length = 15)
	private String numTel;

	@Column(name = "NUM_TEL_PORT", length = 15)
	private String numTelPort;

	public WSAdresse() {
		super();
	}

	public WSAdresse(final Integer codAdr, final String codBdi, final String codCom,
			final String codPay, final String libAd1, final String libAd2, final String libAd3,
			final String numTel, final String numTelPort) {
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
