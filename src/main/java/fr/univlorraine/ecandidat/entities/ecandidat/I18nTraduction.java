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

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the i18n_traduction database table.
 */
@Entity
@Table(name = "i18n_traduction")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "valTrad"})
@SuppressWarnings("serial")
public class I18nTraduction implements Serializable {

	@EmbeddedId
	private I18nTraductionPK id;

	@Lob
	@Column(name = "val_trad", nullable = false, columnDefinition = "TEXT")
	@NotNull
	private String valTrad;

	// bi-directional many-to-one association to I18n
	@ManyToOne
	@JoinColumn(name = "id_i18n", nullable = false, insertable = false, updatable = false)
	@NotNull
	private I18n i18n;

	// bi-directional many-to-one association to Langue
	@ManyToOne
	@JoinColumn(name = "cod_langue", nullable = false, insertable = false, updatable = false)
	@NotNull
	private Langue langue;

	public I18nTraduction() {
		super();
	}

	public I18nTraduction(final String valTrad, final I18n i18n, final Langue langue) {
		super();
		if (i18n != null) {
			this.id = new I18nTraductionPK(i18n.getIdI18n(), langue.getCodLangue());
			this.i18n = i18n;
		}
		this.valTrad = valTrad;
		this.langue = langue;
	}

	public I18nTraduction clone(final I18n i18n) {
		return new I18nTraduction(this.valTrad, i18n, this.langue);
	}
}
