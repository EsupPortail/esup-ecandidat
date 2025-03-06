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
 * The persistent class for the SPECIALITE_BAC database table.
 */
@Entity
@Table(name = "SPECIALITE_BAC")
@Data
@EqualsAndHashCode(of = "codSpeBac")
@SuppressWarnings("serial")
public class SpecialiteBac implements Serializable {

	@Id
	@Column(name = "COD_SPE_BAC", unique = true, nullable = false, length = 4)
	@Size(max = 4)
	@NotNull
	private String codSpeBac;

	@Column(name = "LIB_SPE_BAC", nullable = false, length = 40)
	@Size(max = 40)
	@NotNull
	private String libSpeBac;

	@Column(name = "LIC_SPE_BAC", nullable = false, length = 10)
	@Size(max = 10)
	@NotNull
	private String licSpeBac;

	@Column(name = "DAA_DEB_VAL_SPE_BAC", nullable = true)
	@Size(max = 4)
	private String daaDebValSpeBac;

	@Column(name = "DAA_FIN_VAL_SPE_BAC", nullable = true)
	@Size(max = 4)
	private String daaFinValSpeBac;

	@Column(name = "TEM_EN_SVE_SPE_BAC", length = 1)
	@Size(max = 1)
	@NotNull
	private String temEnSveSpeBac;
}
