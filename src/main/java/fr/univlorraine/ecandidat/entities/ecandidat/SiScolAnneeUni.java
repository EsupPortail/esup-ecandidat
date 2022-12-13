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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

/**
 * The persistent class for the ANNEE_UNI database table.
 */
@Entity
@Table(name = "siscol_annee_uni")
@Data
@SuppressWarnings("serial")
public class SiScolAnneeUni implements Serializable {

	public static final String ETAT_IAE_OPEN = "O";

	@EmbeddedId
	private SiScolAnneeUniPK id;

	@Column(name = "eta_anu_iae", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String etaAnuIae;

	@Column(name = "lib_anu", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libAnu;

	@Column(name = "lic_anu", nullable = false, length = 200)
	@Size(max = 200)
	@NotNull
	private String licAnu;

	public SiScolAnneeUni(final String codAnu,
		final String etaAnuIae,
		final String libAnu,
		final String licAnu,
		final String typSiScol) {
		super();
		this.id = new SiScolAnneeUniPK(codAnu, typSiScol);
		this.etaAnuIae = etaAnuIae;
		this.libAnu = libAnu;
		this.licAnu = licAnu;
	}

	public SiScolAnneeUni(final String codAnu,
		final String libAnu,
		final String licAnu,
		final String typSiScol) {
		super();
		this.id = new SiScolAnneeUniPK(codAnu, typSiScol);
		this.etaAnuIae = ETAT_IAE_OPEN;
		this.libAnu = libAnu;
		this.licAnu = licAnu;
	}

	public SiScolAnneeUni() {
		super();
	}
}
