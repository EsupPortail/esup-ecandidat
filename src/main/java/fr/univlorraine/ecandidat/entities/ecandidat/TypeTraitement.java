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
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the type_traitement database table.
 */

@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "type_traitement")
@Data
@EqualsAndHashCode(of = "codTypTrait")
@ToString(of = {"codTypTrait", "libTypTrait", "temFinalTypTrait"})
@SuppressWarnings("serial")
public class TypeTraitement implements Serializable {

	@Id
	@Column(name = "cod_typ_trait", nullable = false, length = 2)
	@Size(max = 2)
	@NotNull
	private String codTypTrait;

	@Column(name = "lib_typ_trait", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String libTypTrait;

	@Column(name = "tem_final_typ_trait", nullable = false)
	@NotNull
	private Boolean temFinalTypTrait;

	// bi-directional many-to-one association to Candidature
	@OneToMany(mappedBy = "typeTraitement")
	private List<Candidature> candidatures;

	// bi-directional many-to-one association to Formation
	@OneToMany(mappedBy = "typeTraitement")
	private List<Formation> formations;

	// bi-directional many-to-one association to PieceJustif
	@OneToMany(mappedBy = "typeTraitement")
	private List<PieceJustif> pieceJustifs;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_lib_typ_trait", nullable = false)
	@NotNull
	private I18n i18nLibTypTrait;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_typ_trait", nullable = true)
	private LocalDateTime datModTypTrait;

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle() {
		return this.codTypTrait + "/" + this.libTypTrait;
	}

	public TypeTraitement() {
		super();
	}

	public TypeTraitement(final String codTypTrait, final String libTypTrait, final Boolean temFinalTypTrait) {
		super();
		this.codTypTrait = codTypTrait;
		this.libTypTrait = libTypTrait;
		this.temFinalTypTrait = temFinalTypTrait;
	}
}
