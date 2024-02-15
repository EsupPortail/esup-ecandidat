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
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.EntityPushEntityListener;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the parametre database table.
 */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "parametre")
@Data
@EqualsAndHashCode(of = "codParam")
@SuppressWarnings("serial")
public class Parametre implements Serializable {

	@Id
	@Column(name = "cod_param", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String codParam;

	@Column(name = "lib_param", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libParam;

	@Column(name = "val_param", nullable = false, length = 100)
	@Size(max = 100)
	@NotNull
	private String valParam;

	@Column(name = "typ_param", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String typParam;

	@Column(name = "regex_param", nullable = true, length = 100)
	@Size(max = 100)
	private String regexParam;

	@Column(name = "tem_scol", nullable = false)
	@NotNull
	private Boolean temScol;

	@Column(name = "tem_affiche", nullable = false)
	@NotNull
	private Boolean temAffiche;

	public Parametre() {
		super();
	}

	public Parametre(final String codParam, final String libParam, final String valParam,
			final String typParam, final Boolean temScol, final Boolean temAffiche) {
		super();
		this.codParam = codParam;
		this.libParam = libParam;
		this.valParam = valParam;
		this.typParam = typParam;
		this.temScol = temScol;
		this.temAffiche = temAffiche;
	}

	public Parametre(final String codParam, final String libParam, final String valParam,
			final String typParam, final Boolean temScol, final Boolean temAffiche, final String regex) {
		this(codParam, libParam, valParam, typParam, temScol, temAffiche);
		this.regexParam = regex;
	}

	/**
	 * @param newCodParam
	 * @param oldParam
	 */
	public Parametre(final String newCodParam, final Parametre oldParam) {
		super();
		this.codParam = newCodParam;
		this.libParam = oldParam.getLibParam();
		this.valParam = oldParam.getValParam();
		this.typParam = oldParam.getTypParam();
		this.temScol = oldParam.getTemScol();
		this.temAffiche = oldParam.getTemAffiche();
	}
}
