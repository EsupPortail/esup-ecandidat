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

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the Diplome database table.
 */
@Embeddable
@ToString(of = {"codDip", "codVrsVdi"})
@Data
@EqualsAndHashCode(of = {"codDip", "codVrsVdi"})
@SuppressWarnings("serial")
public class DiplomePK implements Serializable {

	@Column(name = "cod_dip")
	private String codDip;

	@Column(name = "cod_vrs_vdi")
	private String codVrsVdi;

}
