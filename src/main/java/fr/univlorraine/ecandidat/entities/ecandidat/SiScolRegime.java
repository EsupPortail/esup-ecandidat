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
 * The persistent class for the siscol_regime database table.
 */
@Entity
@Table(name = "siscol_regime")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolRegime implements Serializable {

	public static String DISPLAY_LIB_FIELD = "displayLibelle";

	@EmbeddedId
	private SiScolRegimePK id;

	@Column(name = "lib_rgi", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libRgi;

	@Column(name = "lic_rgi", nullable = false, length = 200)
	@Size(max = 200)
	@NotNull
	private String licRgi;

	@Column(name = "tem_en_sve_rgi", nullable = false)
	@NotNull
	private Boolean temEnSveRgi;

	// bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy = "siScolRegime")
	private List<Candidat> candidats;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.id.getCodRgi() + "/" + this.libRgi;
	}

	/**
	 * @return le libellé à afficher dans la grid
	 */
	public String getDisplayLibelle() {
		return "[" + this.id.getCodRgi() + "] " + this.libRgi;
	}

	public SiScolRegime() {
		super();
	}

	public SiScolRegime(final String codRgi,
		final String libRgi,
		final String licRgi,
		final Boolean temEnSveRgi,
		final String typSiScol) {
		super();
		this.id = new SiScolRegimePK(codRgi, typSiScol);
		this.libRgi = libRgi;
		this.licRgi = licRgi;
		this.temEnSveRgi = temEnSveRgi;
	}
}
