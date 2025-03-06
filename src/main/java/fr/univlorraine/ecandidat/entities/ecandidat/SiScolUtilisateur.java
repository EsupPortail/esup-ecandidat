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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siscol_utilisateur database table.
 */
@Entity
@Table(name = "siscol_utilisateur")
@Data
@EqualsAndHashCode(of = "codUti")
@SuppressWarnings("serial")
public class SiScolUtilisateur implements Serializable {

	@Id
	@Column(name = "id_uti", nullable = false)
	@NotNull
	private Integer idUti;

	@Column(name = "cod_uti", nullable = false, length = 30)
	@Size(max = 30)
	@NotNull
	private String codUti;

	@Column(name = "adr_mail_uti", length = 200)
	@Size(max = 200)
	private String adrMailUti;

	@Column(name = "lib_cmt_uti", length = 200)
	@Size(max = 200)
	private String libCmtUti;

	@Column(name = "tem_en_sve_uti", nullable = false)
	@NotNull
	private Boolean temEnSveUti;

	@Column(name = "typ_siscol", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String typSiScol;

	// bi-directional many-to-one association to ApoCentreGestion
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_cge", referencedColumnName = "cod_cge"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolCentreGestion siScolCentreGestion;

	public SiScolUtilisateur() {
		super();
	}

	public SiScolUtilisateur(final String codUti,
		final String adrMailUti,
		final String libCmtUti,
		final Boolean temEnSveUti,
		final String typSiScol) {
		super();
		this.codUti = codUti;
		this.adrMailUti = adrMailUti;
		this.libCmtUti = libCmtUti;
		this.temEnSveUti = temEnSveUti;
		this.typSiScol = typSiScol;
	}

}
