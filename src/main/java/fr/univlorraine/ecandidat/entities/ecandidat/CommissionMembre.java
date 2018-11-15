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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the commission_membre database table.
 */
@Entity
@Table(name = "commission_membre")
@Data
@EqualsAndHashCode(of = "idDroitProfilInd")
@ToString(exclude = "droitProfilInd")
@SuppressWarnings("serial")
public class CommissionMembre implements Serializable {

	@Id
	@Column(name = "id_droit_profil_ind", nullable = false)
	private int idDroitProfilInd;

	@Column(name = "tem_is_president", nullable = false)
	@NotNull
	private Boolean temIsPresident;

	// bi-directional many-to-one association to Commission
	@ManyToOne
	@JoinColumn(name = "id_comm", nullable = false)
	@NotNull
	private Commission commission;

	// bi-directional one-to-one association to DroitProfilInd
	@OneToOne
	@JoinColumn(name = "id_droit_profil_ind", nullable = false, insertable = false, updatable = false)
	@NotNull
	private DroitProfilInd droitProfilInd;

	public CommissionMembre() {
		super();
	}

	public CommissionMembre(final Commission commission, final DroitProfilInd droitProfilInd, final Boolean temIsPresident) {
		super();
		this.idDroitProfilInd = droitProfilInd.getIdDroitProfilInd();
		this.commission = commission;
		this.droitProfilInd = droitProfilInd;
		this.temIsPresident = temIsPresident;
	}
}
