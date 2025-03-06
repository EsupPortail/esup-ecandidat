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
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the pj_cand database table.
 */
@Data
@EqualsAndHashCode(of = {"idPj", "idCand"})
@Embeddable
@ToString(of = {"idPj", "idCand"})
@SuppressWarnings("serial")
public class PjCandPK implements Serializable {
	// default serial version id, required for serializable classes.

	@Column(name = "id_pj", nullable = false)
	@NotNull
	private Integer idPj;

	@Column(name = "id_cand", nullable = false)
	@NotNull
	private Integer idCand;

	public PjCandPK() {
	}

	public PjCandPK(final Integer idPj, final Integer idCand) {
		super();
		this.idPj = idPj;
		this.idCand = idCand;
	}
}
