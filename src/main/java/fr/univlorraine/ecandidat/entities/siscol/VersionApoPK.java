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
package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the VERSION_APO database table.
 */
@Data
@EqualsAndHashCode(of = {"codVer", "codPatch", "codPerso"})
@Embeddable
@ToString(of = {"codVer", "codPatch", "codPerso"})
@SuppressWarnings("serial")
public class VersionApoPK implements Serializable {
	// default serial version id, required for serializable classes.

	@Column(name = "COD_VER")
	private String codVer;

	@Column(name = "COD_PATCH")
	private long codPatch;

	@Column(name = "COD_PERSO")
	private long codPerso;
}
