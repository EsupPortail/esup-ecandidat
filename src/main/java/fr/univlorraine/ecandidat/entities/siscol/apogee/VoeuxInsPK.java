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
package fr.univlorraine.ecandidat.entities.siscol.apogee;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the VOEUX_INS database table.
 */
@ToString(of = {"codIndOpi", "codCge", "codEtp", "codVrsVet"})
@Embeddable
@Data
@EqualsAndHashCode(of = {"codIndOpi", "codCge", "codEtp", "codVrsVet"})
@SuppressWarnings("serial")
public class VoeuxInsPK implements Serializable {
	// default serial version id, required for serializable classes.

	@Column(name = "COD_IND_OPI", nullable = false, precision = 8)
	private long codIndOpi;

	@Column(name = "COD_CGE", nullable = false, length = 3)
	private String codCge;

	@Column(name = "COD_ETP", nullable = false, length = 6)
	private String codEtp;

	@Column(name = "COD_VRS_VET", nullable = false, precision = 3)
	private long codVrsVet;
}
