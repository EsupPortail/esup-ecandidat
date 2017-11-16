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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;


/**
 * The persistent class for the ANNEE_UNI database table.
 * 
 */
@Entity
@Table(name="ANNEE_UNI")
@Data
public class AnneeUni implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="COD_ANU")
	private String codAnu;

	@Column(name="ETA_ANU_IAE")
	private String etaAnuIae;

	@Column(name="LIB_ANU")
	private String libAnu;

	@Column(name="LIC_ANU")
	private String licAnu;
}