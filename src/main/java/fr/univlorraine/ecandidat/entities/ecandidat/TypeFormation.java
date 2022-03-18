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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the typeForm database table.
 */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "type_formation")
@Data
@EqualsAndHashCode(of = "idTypeForm")
@ToString(of = { "idTypeForm", "codTypeForm", "libTypeForm", "tesTypeForm" })
@SuppressWarnings("serial")
public class TypeFormation implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_typ_form", nullable = false)
	private Integer idTypeForm;

	@Column(name = "cod_typ_form", unique = true, nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String codTypeForm;

	@Column(name = "lib_typ_form", nullable = false, length = 50)
	@NotNull
	@Size(max = 50)
	private String libTypeForm;

	@Column(name = "tes_typ_form", nullable = false)
	@NotNull
	private Boolean tesTypeForm;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_cre_typ_form", nullable = false)
	@NotNull
	private LocalDateTime datCreTypeForm;

	@Column(name = "user_cre_typ_form", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userCreTypeForm;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_typ_form", nullable = false)
	@NotNull
	private LocalDateTime datModTypeForm;

	@Column(name = "user_mod_typ_form", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String userModTypeForm;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_lib_typ_form", nullable = false)
	@NotNull
	private I18n i18nLibTypeForm;

	// bi-directional many-to-one association to Formation
	@OneToMany(mappedBy = "typeFormation")
	private List<Formation> formations;

	@PrePersist
	private void onPrePersist() {
		this.datCreTypeForm = LocalDateTime.now();
		this.datModTypeForm = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		this.datModTypeForm = LocalDateTime.now();
	}

	public TypeFormation() {
		super();
	}

	public TypeFormation(final String user) {
		super();
		this.userCreTypeForm = user;
		this.userModTypeForm = user;
		this.tesTypeForm = true;
	}
}
