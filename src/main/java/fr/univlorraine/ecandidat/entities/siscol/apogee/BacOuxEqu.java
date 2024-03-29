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
 * The persistent class for the BAC_OUX_EQU database table.
 */
@Entity
@Table(name = "BAC_OUX_EQU")
@Data
@EqualsAndHashCode(of = "codBac")
@SuppressWarnings("serial")
public class BacOuxEqu implements Serializable {

	@Id
	@Column(name = "COD_BAC", unique = true, nullable = false, length = 4)
	@Size(max = 4)
	@NotNull
	private String codBac;

	@Column(name = "LIB_BAC", nullable = false, length = 40)
	@Size(max = 40)
	@NotNull
	private String libBac;

	@Column(name = "LIC_BAC", nullable = false, length = 10)
	@Size(max = 10)
	@NotNull
	private String licBac;

	@Column(name = "DAA_DEB_VLD_BAC", nullable = true)
	@Size(max = 4)
	private String daaDebVldBac;

	@Column(name = "DAA_FIN_VLD_BAC", nullable = true)
	@Size(max = 4)
	private String daaFinVldBac;

	@Column(name = "TEM_EN_SVE_BAC", length = 1)
	@Size(max = 1)
	@NotNull
	private String temEnSveBac;

	@Column(name = "TEM_NAT_BAC", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String temNatBac;

	@Column(name = "TEM_CTRL_INE", length = 1)
	@Size(max = 1)
	@NotNull
	private String temCtrlIne;

	@Column(name = "ANN_CTRL_INE", nullable = true, length = 4)
	@Size(max = 4)
	private String annCtrlIne;
}
