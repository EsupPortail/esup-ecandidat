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
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the formulaire_candidat database table.
 */
@Data
@EqualsAndHashCode(of = { "idCand", "idFormulaireLimesurvey" })
@Embeddable
@ToString(of = { "idCand", "idFormulaireLimesurvey" })
@SuppressWarnings("serial")
public class FormulaireCandidaturePK implements Serializable {

	// default serial version id, required for serializable classes.

	@Column(name = "id_cand", nullable = false)
	@NotNull
	private Integer idCand;

	@Column(name = "id_formulaire_limesurvey", nullable = false)
	@NotNull
	private Integer idFormulaireLimesurvey;

	public FormulaireCandidaturePK() {
	}

	public FormulaireCandidaturePK(final Integer idCand, final Integer idFormulaireLimesurvey) {
		this.idFormulaireLimesurvey = idFormulaireLimesurvey;
		this.idCand = idCand;
	}
}
