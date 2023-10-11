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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the inscription_ind database table.
 */
@Entity
@Table(name = "inscription_ind")
@Data
@EqualsAndHashCode(of = "loginIns")
@ToString(of = { "loginIns", "libelleIns", "mailIns" })
@SuppressWarnings("serial")
public class InscriptionInd implements Serializable {

	@Id
	@Column(name = "login_ins", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String loginIns;

	@Column(name = "libelle_ins", nullable = false, length = 255)
	@NotNull
	@Size(max = 255)
	private String libelleIns;

	@Column(name = "mail_ins", length = 255)
	@Size(max = 255)
	private String mailIns;

	public InscriptionInd() {
	}

	public InscriptionInd(final String loginIns) {
		super();
		this.loginIns = loginIns;
	}

	public InscriptionInd(final String loginIns, final String libelleIns, final String mailIns) {
		super();
		this.loginIns = loginIns;
		this.libelleIns = libelleIns;
		this.mailIns = mailIns;
	}

}
