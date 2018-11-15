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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the droit_profil_ind database table.
 */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "droit_profil_ind")
@Data
@EqualsAndHashCode(of = "idDroitProfilInd")
@SuppressWarnings("serial")
public class DroitProfilInd implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_droit_profil_ind", nullable = false)
	private Integer idDroitProfilInd;

	// bi-directional many-to-one association to Individu
	@ManyToOne
	@JoinColumn(name = "login_ind", nullable = false)
	private Individu individu;

	// bi-directional many-to-one association to DroitProfil
	@ManyToOne
	@JoinColumn(name = "id_profil", nullable = false)
	@NotNull
	private DroitProfil droitProfil;

	// bi-directional one-to-one association to CommissionMembre
	@OneToOne(mappedBy = "droitProfilInd", orphanRemoval = true, cascade = CascadeType.ALL)
	private CommissionMembre commissionMembre;

	// bi-directional one-to-one association to Gestionnaire
	@OneToOne(mappedBy = "droitProfilInd", orphanRemoval = true, cascade = CascadeType.ALL)
	private Gestionnaire gestionnaire;

	public DroitProfilInd() {
	}

	public DroitProfilInd(final Individu individu, final DroitProfil droitProfil) {
		super();
		this.individu = individu;
		this.droitProfil = droitProfil;
	}

}
