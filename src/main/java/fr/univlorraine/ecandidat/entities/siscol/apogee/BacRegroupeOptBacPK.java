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
 * The persistent class for the BAC_REGROUPE_OPT_BAC database table.
 */
@Embeddable
@ToString(of = { "codBac", "codOptBac" })
@Data
@EqualsAndHashCode(of = { "codBac", "codOptBac" })
@SuppressWarnings("serial")
public class BacRegroupeOptBacPK implements Serializable {

	@Column(name = "cod_bac")
	private String codBac;

	@Column(name = "cod_opt_bac")
	private String codOptBac;

}
