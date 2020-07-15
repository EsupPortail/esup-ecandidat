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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siscol_typ_diplome database table.
 */
@Entity
@Table(name = "siscol_typ_resultat")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolTypResultat implements Serializable {

	@EmbeddedId
	private SiScolTypResultatPK id;

	@Column(name = "lib_tre", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String libTre;

	@Column(name = "lic_tre", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String licTre;

	@Column(name = "tem_en_sve_tre", nullable = false)
	@NotNull
	private Boolean temEnSveTre;

	// bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy = "siScolTypResultat")
	private List<CandidatCursusInterne> candidatCursusInternes;

	public SiScolTypResultat() {
		super();
	}

	public SiScolTypResultat(final String codTre,
		final String libTre,
		final String licTre,
		final Boolean temEnSveTre,
		final String typSiScol) {
		super();
		this.id = new SiScolTypResultatPK(codTre, typSiScol);
		this.libTre = libTre;
		this.licTre = licTre;
		this.temEnSveTre = temEnSveTre;
	}
}
