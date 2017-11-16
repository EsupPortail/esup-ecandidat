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


/**
 * The persistent class for the ANNEE_UNI database table.
 * 
 */
@Entity
@Table(name="siscol_annee_uni")
@Data
public class SiScolAnneeUni implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_anu", nullable=false, length=4)
	@Size(max = 4) 
	@NotNull
	private String codAnu;

	@Column(name="eta_anu_iae", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String etaAnuIae;

	@Column(name="lib_anu", nullable=false, length=40)
	@Size(max = 40) 
	@NotNull
	private String libAnu;

	@Column(name="lic_anu", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licAnu;

	public SiScolAnneeUni(String codAnu, String etaAnuIae, String libAnu,
			String licAnu) {
		super();
		this.codAnu = codAnu;
		this.etaAnuIae = etaAnuIae;
		this.libAnu = libAnu;
		this.licAnu = licAnu;
	}

	public SiScolAnneeUni() {
		super();
	}
}