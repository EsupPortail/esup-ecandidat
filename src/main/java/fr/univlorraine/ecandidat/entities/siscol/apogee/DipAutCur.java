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
 * The persistent class for the DIP_AUT_CUR database table.
 */
@Entity
@Table(name = "DIP_AUT_CUR")
@Data
@EqualsAndHashCode(of = "codDac")
@SuppressWarnings("serial")
public class DipAutCur implements Serializable {

	@Id
	@Column(name = "COD_DAC", unique = true, nullable = false, length = 7)
	@Size(max = 7)
	@NotNull
	private String codDac;

	@Column(name = "LIB_DAC", nullable = false, length = 60)
	@Size(max = 60)
	@NotNull
	private String libDac;

	@Column(name = "LIC_DAC", nullable = false, length = 10)
	@Size(max = 10)
	@NotNull
	private String licDac;

	@Column(name = "TEM_EN_SVE_DAC", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String temEnSveDac;

}
