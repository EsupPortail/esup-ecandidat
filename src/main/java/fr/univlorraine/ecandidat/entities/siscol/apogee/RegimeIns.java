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
 * The persistent class for the REGIME_INS database table.
 */
@Entity
@Table(name = "REGIME_INS")
@Data
@EqualsAndHashCode(of = "codRgi")
@SuppressWarnings("serial")
public class RegimeIns implements Serializable {

	@Id
	@Column(name = "COD_RGI", unique = true, nullable = false, length = 2)
	@Size(max = 2)
	@NotNull
	private String codRgi;

	@Column(name = "LIB_RGI", nullable = false, length = 65)
	@Size(max = 65)
	@NotNull
	private String libRgi;

	@Column(name = "LIC_RGI", nullable = false, length = 15)
	@Size(max = 15)
	@NotNull
	private String licRgi;

	@Column(name = "TEM_EN_SVE_RGI", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String temEnSveRgi;

}
