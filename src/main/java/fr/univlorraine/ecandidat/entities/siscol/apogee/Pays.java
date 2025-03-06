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
package fr.univlorraine.ecandidat.entities.siscol.apogee;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the PAYS database table.
 */
@Entity
@Table(name = "PAYS")
@Data
@EqualsAndHashCode(of = "codPay")
@SuppressWarnings("serial")
public class Pays implements Serializable {

	@Id
	@Column(name = "COD_PAY", unique = true, nullable = false, length = 3)
	@Size(max = 3)
	@NotNull
	private String codPay;

	@Column(name = "LIB_NAT", nullable = false, length = 40)
	@Size(max = 40)
	@NotNull
	private String libNat;

	@Column(name = "LIB_PAY", nullable = false, length = 40)
	@Size(max = 40)
	@NotNull
	private String libPay;

	@Column(name = "LIC_PAY", nullable = false, length = 10)
	@Size(max = 10)
	@NotNull
	private String licPay;

	@Column(name = "TEM_EN_SVE_PAY", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String temEnSvePay;
}
