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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.vaadin.server.ThemeResource;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.EntityPushEntityListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the langue database table.
 */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "langue")
@Data
@EqualsAndHashCode(of = "codLangue")
@ToString(of = {"codLangue", "libLangue", "tesLangue", "temDefautLangue"})
@SuppressWarnings("serial")
public class Langue implements Serializable {

	@Id
	@Column(name = "cod_langue", nullable = false, length = 5)
	@Size(max = 5)
	@NotNull
	private String codLangue;

	@Column(name = "lib_langue", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String libLangue;

	@Column(name = "tem_defaut_langue", nullable = false)
	@NotNull
	private Boolean temDefautLangue;

	@Column(name = "tes_langue", nullable = false)
	@NotNull
	private Boolean tesLangue;

	@Transient
	private ThemeResource flag;

	// bi-directional many-to-one association to Candidat
	@OneToMany(mappedBy = "langue")
	private List<Candidat> candidats;

	// bi-directional many-to-one association to I18nTraduction
	@OneToMany(mappedBy = "langue")
	private List<I18nTraduction> i18nTraductions;

	public Langue() {
		super();
	}

	public Langue(final String codLangue, final String libLangue, final Boolean temDefautLangue,
			final Boolean tesLangue) {
		super();
		this.codLangue = codLangue;
		this.libLangue = libLangue;
		this.temDefautLangue = temDefautLangue;
		this.tesLangue = tesLangue;
	}

	public Langue(final String codLangue) {
		super();
		this.codLangue = codLangue;
	}
}
