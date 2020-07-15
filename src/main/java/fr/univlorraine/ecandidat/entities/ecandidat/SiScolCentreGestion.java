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
 * The persistent class for the siScol_centre_gestion database table.
 */
@Entity
@Table(name = "siscol_centre_gestion")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolCentreGestion implements Serializable {

	@EmbeddedId
	private SiScolCentreGestionPK id;

	@Column(name = "lib_cge", nullable = false, length = 40)
	@Size(max = 40)
	@NotNull
	private String libCge;

	@Column(name = "lic_cge", nullable = false, length = 10)
	@Size(max = 10)
	@NotNull
	private String licCge;

	@Column(name = "tem_en_sve_cge", nullable = false)
	@NotNull
	private Boolean temEnSveCge;

	// bi-directional many-to-one association to ApoUtilisateur
	@OneToMany(mappedBy = "siScolCentreGestion")
	private List<SiScolUtilisateur> siScolUtilisateurs;

	// bi-directional many-to-one association to Formation
	@OneToMany(mappedBy = "siScolCentreGestion")
	private List<Formation> formations;

	// bi-directional many-to-one association to Formation
	@OneToMany(mappedBy = "siScolCentreGestion")
	private List<Gestionnaire> gestionnaires;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.id.getCodCge() + "/" + this.libCge;
	}

	public SiScolCentreGestion() {
		super();
	}

	public SiScolCentreGestion(final String codCge,
		final String typSiScol) {
		super();
		this.id = new SiScolCentreGestionPK(codCge, typSiScol);
	}

	public SiScolCentreGestion(final String codCge,
		final String libCge,
		final String licCge,
		final Boolean temEnSveCge,
		final String typSiScol) {
		super();
		this.id = new SiScolCentreGestionPK(codCge, typSiScol);
		this.libCge = libCge;
		this.licCge = licCge;
		this.temEnSveCge = temEnSveCge;
	}
}
