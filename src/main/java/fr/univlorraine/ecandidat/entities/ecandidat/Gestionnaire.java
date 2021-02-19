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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the gestionnaire database table.
 */
@Entity
@Table(name = "gestionnaire")
@Data
@EqualsAndHashCode(of = "idDroitProfilInd")
@ToString(exclude = "droitProfilInd")
@SuppressWarnings("serial")
public class Gestionnaire implements Serializable {

	@Id
	@Column(name = "id_droit_profil_ind", nullable = false)
	private Integer idDroitProfilInd;

	@Column(name = "typ_siscol", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String typSiScol;

	// bi-directional many-to-one association to CentreCandidature
	@ManyToOne
	@JoinColumn(name = "id_ctr_cand", nullable = false)
	@NotNull
	private CentreCandidature centreCandidature;

	// bi-directional one-to-one association to DroitProfilInd
	@OneToOne
	@JoinColumn(name = "id_droit_profil_ind", nullable = false, insertable = false, updatable = false)
	@NotNull
	private DroitProfilInd droitProfilInd;

	@Column(name = "login_apo_gest", length = 20)
	@Size(max = 20)
	private String loginApoGest;

	// bi-directional many-to-one association to ApoCentreGestion
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_cge", referencedColumnName = "cod_cge"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolCentreGestion siScolCentreGestion;

	@Column(name = "tem_all_comm_gest", nullable = false)
	@NotNull
	private Boolean temAllCommGest;

	// bi-directional many-to-many association to Commission
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(name = "gestionnaire_commission", joinColumns = { @JoinColumn(name = "id_droit_profil_ind") }, inverseJoinColumns = { @JoinColumn(name = "id_comm") })
	private List<Commission> commissions;

	public Gestionnaire() {
		super();
	}

	public Gestionnaire(final String typSiScol,
		final CentreCandidature centreCandidature,
		final DroitProfilInd droitProfilInd,
		final String loginApoGest,
		final SiScolCentreGestion siScolCentreGestion,
		final Boolean isAllCommission,
		final List<Commission> listeCommission) {
		super();
		idDroitProfilInd = droitProfilInd.getIdDroitProfilInd();
		this.typSiScol = typSiScol;
		this.centreCandidature = centreCandidature;
		this.droitProfilInd = droitProfilInd;
		this.loginApoGest = loginApoGest;
		this.siScolCentreGestion = siScolCentreGestion;
		temAllCommGest = isAllCommission;
		commissions = listeCommission;
	}

}
