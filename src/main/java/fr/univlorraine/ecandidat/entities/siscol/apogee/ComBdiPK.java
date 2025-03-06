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
 * The primary key class for the apo_com_bdi database table.
 */
@Data
@EqualsAndHashCode(of = {"codCom", "codBdi"})
@Embeddable
@ToString(of = {"codCom", "codBdi"})
@SuppressWarnings("serial")
public class ComBdiPK implements Serializable {
	// default serial version id, required for serializable classes.

	@Column(name = "COD_COM")
	private String codCom;

	@Column(name = "COD_BDI")
	private String codBdi;
}
