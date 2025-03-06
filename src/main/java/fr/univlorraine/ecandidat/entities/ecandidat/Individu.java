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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import fr.univlorraine.ecandidat.services.people.People;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the individu database table.
 */
@Entity
@Table(name = "individu")
@Data
@EqualsAndHashCode(of = "loginInd")
@ToString(of = { "loginInd", "libelleInd", "mailInd" })
@SuppressWarnings("serial")
public class Individu implements Serializable {

	@Id
	@Column(name = "login_ind", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String loginInd;

	@Column(name = "libelle_ind", nullable = false, length = 255)
	@NotNull
	@Size(max = 255)
	private String libelleInd;

	@Column(name = "mail_ind", length = 255)
	@Size(max = 255)
	private String mailInd;

	@Column(name = "tes_ind", nullable = false)
	@NotNull
	private Boolean tesInd;

	// bi-directional many-to-one association to DroitProfilInd
	@OneToMany(mappedBy = "individu")
	private List<DroitProfilInd> droitProfilInds;

	@OneToOne(mappedBy = "individu", cascade = CascadeType.REMOVE)
	private PreferenceInd preferenceInd;

	public Individu() {
	}

	public Individu(final String loginInd, final String libelleInd, final String mailInd) {
		super();
		this.loginInd = loginInd;
		this.libelleInd = libelleInd;
		this.mailInd = mailInd;
		this.tesInd = true;
	}

	public Individu(final People people) {
		this(people.getUid(), people.getDisplayName(), people.getMail());
	}

}
