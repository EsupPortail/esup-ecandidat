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

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * The persistent class for the siScol_com_bdi database table.
 */
@Entity
@Table(name = "siscol_com_bdi")
@Data
@SuppressWarnings("serial")
public class SiScolComBdi implements Serializable {

	@EmbeddedId
	private SiScolComBdiPK id;

	public SiScolComBdi() {
		super();
	}

	public SiScolComBdi(final String codCom,
		final String codBdi,
		final String typSiScol) {
		super();
		this.id = new SiScolComBdiPK(codCom, codBdi, typSiScol);
	}
}
