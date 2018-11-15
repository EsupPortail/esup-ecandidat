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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the ETABLISSEMENT database table.
 */
@Entity
@Data
@EqualsAndHashCode(of = "codEtb")
@SuppressWarnings("serial")
public class Etablissement implements Serializable {

	@Id
	@Column(name = "COD_ETB", unique = true, nullable = false, length = 8)
	@Size(max = 8)
	@NotNull
	private String codEtb;

	@Column(name = "COD_TPE", nullable = false, length = 2)
	@Size(max = 2)
	@NotNull
	private String codTpe;

	@Column(name = "LIB_ETB", nullable = false, length = 40)
	@Size(max = 40)
	@NotNull
	private String libEtb;

	@Column(name = "LIB_WEB_ETB", length = 120)
	@Size(max = 120)
	private String libWebEtb;

	@Column(name = "LIC_ETB", nullable = false, length = 10)
	@Size(max = 10)
	@NotNull
	private String licEtb;

	@Column(name = "TEM_EN_SVE_ETB", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String temEnSveEtb;

	// bi-directional many-to-one association to Departement
	@ManyToOne
	@JoinColumn(name = "COD_DEP")
	private Departement departement;

	// bi-directional many-to-one association to Departement
	@ManyToOne
	@JoinColumn(name = "COD_COM_ADR_ETB")
	private Commune commune;
}
