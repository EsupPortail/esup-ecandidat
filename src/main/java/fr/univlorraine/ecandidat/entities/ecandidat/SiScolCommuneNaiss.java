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
package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siscol_commune_naiss database table.
 */
@Entity
@Table(name = "siscol_commune_naiss")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolCommuneNaiss implements Serializable {

	@EmbeddedId
	private SiScolCommuneNaissPK id;

	@Column(name = "lib_com_naiss", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libComNaiss;

	@Column(name = "tem_en_sve_com_naiss", nullable = false)
	@NotNull
	private Boolean temEnSveComNaiss;

	// bi-directional many-to-one association to ApoDepartement
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_dep", referencedColumnName = "cod_dep"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	@NotNull
	private SiScolDepartement siScolDepartement;

	public SiScolCommuneNaiss() {
		super();
	}

	public SiScolCommuneNaiss(final String codComNaiss,
		final String libComNaiss,
		final Boolean temEnSveComNaiss,
		final SiScolDepartement siScolDepartement,
		final String typSiScol) {
		super();
		this.id = new SiScolCommuneNaissPK(codComNaiss, typSiScol);
		this.libComNaiss = libComNaiss.toUpperCase();
		this.temEnSveComNaiss = temEnSveComNaiss;
		this.siScolDepartement = siScolDepartement;
	}

	public SiScolCommuneNaiss(final String codComNaiss, final String typSiScol) {
		super();
		this.id = new SiScolCommuneNaissPK(codComNaiss, typSiScol);
	}
}
