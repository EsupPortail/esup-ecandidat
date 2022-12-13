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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the TYP_RESULTAT database table.
 */
@Entity
@Table(name = "TYP_RESULTAT")
@Data
@EqualsAndHashCode(of = "codTre")
@SuppressWarnings("serial")
public class TypResultat implements Serializable {

	@Id
	@Column(name = "COD_TRE", unique = true, nullable = false, length = 4)
	@Size(max = 4)
	@NotNull
	private String codTre;

	@Column(name = "LIB_TRE", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String libTre;

	@Column(name = "LIC_TRE", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String licTre;

	@Column(name = "TEM_EN_SVE_TRE", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String temEnSveTre;

}
