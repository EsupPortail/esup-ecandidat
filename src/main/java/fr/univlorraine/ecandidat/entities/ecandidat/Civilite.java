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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the civilite database table.
 */
@Entity
@Table(name = "civilite")
@Data
@EqualsAndHashCode(of = "codCiv")
@SuppressWarnings("serial")
public class Civilite implements Serializable {

	@Id
	@Column(name = "cod_civ")
	private String codCiv;

	@Column(name = "cod_siscol")
	private String codSiScol;

	@Column(name = "lib_civ")
	private String libCiv;

	@Column(name = "cod_sexe")
	private String codSexe;

	// bi-directional many-to-one association to Candidat
	@OneToMany(mappedBy = "civilite")
	private List<Candidat> candidats;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.codCiv;
	}

	public Civilite() {
		super();
	}

	public Civilite(final String codCiv, final String libCiv, final String codSiScol, final String codSexe) {
		super();
		this.codCiv = codCiv;
		this.codSiScol = codSiScol;
		this.libCiv = libCiv;
		this.codSexe = codSexe;
	}
}
