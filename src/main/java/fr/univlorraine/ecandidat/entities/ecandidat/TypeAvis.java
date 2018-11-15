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
 * The persistent class for the type_avis database table.
 */
@Entity
@Table(name = "type_avis")
@Data
@EqualsAndHashCode(of = "codTypAvis")
@ToString(of = {"codTypAvis", "libelleTypAvis"})
@SuppressWarnings("serial")
public class TypeAvis implements Serializable {

	@Id
	@Column(name = "cod_typ_avis", nullable = false, length = 2)
	@Size(max = 2)
	@NotNull
	private String codTypAvis;

	@Column(name = "libelle_typ_avis", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String libelleTypAvis;

	// bi-directional many-to-one association to Mail
	@OneToMany(mappedBy = "typeAvis")
	private List<Mail> mails;

	// bi-directional many-to-one association to TypeDecision
	@OneToMany(mappedBy = "typeAvis")
	private List<TypeDecision> typeDecisions;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.codTypAvis + "/" + this.libelleTypAvis;
	}

	public TypeAvis(final String codTypAvis, final String libelleTypAvis) {
		super();
		this.codTypAvis = codTypAvis;
		this.libelleTypAvis = libelleTypAvis;
	}

	public TypeAvis(final String codTypAvis) {
		super();
		this.codTypAvis = codTypAvis;
	}

	public TypeAvis() {
		super();
	}

}
