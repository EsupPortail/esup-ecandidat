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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the droit_profil_fonc database table.
 */
@Entity
@Table(name = "droit_profil_fonc")
@Data
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"droitFonctionnalite", "droitProfil"})
@SuppressWarnings("serial")
public class DroitProfilFonc implements Serializable {

	@EmbeddedId
	private DroitProfilFoncPK id;

	@Column(name = "tem_read_only", nullable = false)
	private Boolean temReadOnly;

	// bi-directional many-to-one association to DroitFonctionnalite
	@ManyToOne
	@JoinColumn(name = "cod_fonc", nullable = false, insertable = false, updatable = false)
	@NotNull
	private DroitFonctionnalite droitFonctionnalite;

	// bi-directional many-to-one association to DroitProfil
	@ManyToOne
	@JoinColumn(name = "id_profil", nullable = false, insertable = false, updatable = false)
	@NotNull
	private DroitProfil droitProfil;

	public DroitProfilFonc() {
		super();
	}

	public DroitProfilFonc(final DroitFonctionnalite droitFonctionnalite,
			final DroitProfil droitProfil, final Boolean temReadOnly) {
		super();
		this.id = new DroitProfilFoncPK(droitProfil.getIdProfil(), droitFonctionnalite.getCodFonc());
		this.droitFonctionnalite = droitFonctionnalite;
		this.droitProfil = droitProfil;
		this.temReadOnly = temReadOnly;
	}

}
