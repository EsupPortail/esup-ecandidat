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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the type_traduction database table.
 */
@Entity
@Table(name = "type_traduction")
@Data
@EqualsAndHashCode(of = "codTypTrad")
@ToString(of = {"codTypTrad", "libTypTrad"})
@SuppressWarnings("serial")
public class TypeTraduction implements Serializable {

	@Id
	@Column(name = "cod_typ_trad", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String codTypTrad;

	@Column(name = "length_typ_trad", nullable = false)
	@NotNull
	private Integer lengthTypTrad;

	@Column(name = "lib_typ_trad", nullable = false, length = 30)
	@Size(max = 30)
	@NotNull
	private String libTypTrad;

	// bi-directional many-to-one association to I18n
	@OneToMany(mappedBy = "typeTraduction")
	private List<I18n> i18ns;

	public TypeTraduction() {
		super();
	}

	public TypeTraduction(final String codTypTrad,
			final String libTypTrad, final Integer lengthTypTrad) {
		super();
		this.lengthTypTrad = lengthTypTrad;
		this.libTypTrad = libTypTrad;
		this.codTypTrad = codTypTrad;
	}

}
