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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the message database table.
 */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "message")
@Data
@EqualsAndHashCode(of = "codMsg")
@ToString(of = {"codMsg", "libMsg", "tesMsg"})
@SuppressWarnings("serial")
public class Message implements Serializable {

	@Id
	@Column(name = "cod_msg", nullable = false, length = 30)
	@Size(max = 30)
	@NotNull
	private String codMsg;

	@Column(name = "lib_msg", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String libMsg;

	// bi-directional many-to-one association to I18n
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "id_i18n_val_msg", nullable = false)
	@NotNull
	private I18n i18nValMessage;

	@Column(name = "tes_msg", nullable = false)
	@NotNull
	private Boolean tesMsg;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_mod_msg", nullable = false)
	@NotNull
	private LocalDateTime datModMsg;

	public Message() {
		super();
	}

	public Message(final String codMsg, final String libMsg) {
		super();
		this.codMsg = codMsg;
		this.libMsg = libMsg;
		this.tesMsg = false;
	}
}
