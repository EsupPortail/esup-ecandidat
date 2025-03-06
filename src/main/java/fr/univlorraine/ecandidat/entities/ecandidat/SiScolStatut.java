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

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siscol_statut database table.
 */
@Entity
@Table(name = "siscol_statut")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolStatut implements Serializable {

	@EmbeddedId
	private SiScolStatutPK id;

	@Column(name = "lib_stu", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libStu;

	@Column(name = "lic_stu", nullable = false, length = 200)
	@Size(max = 200)
	@NotNull
	private String licStu;

	@Column(name = "tem_en_sve_stu", nullable = false)
	@NotNull
	private Boolean temEnSveStu;

	// bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy = "siScolStatut")
	private List<Candidat> candidats;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.libStu;
	}

	public SiScolStatut() {
		super();
	}

	public SiScolStatut(final String codStu,
		final String libStu,
		final String licStu,
		final Boolean temEnSveStu,
		final String typSiScol) {
		super();
		this.id = new SiScolStatutPK(codStu, typSiScol);
		this.libStu = libStu;
		this.licStu = licStu;
		this.temEnSveStu = temEnSveStu;
	}
}
