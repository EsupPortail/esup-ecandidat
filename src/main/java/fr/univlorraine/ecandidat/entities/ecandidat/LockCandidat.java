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

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the lock_candidat database table.
 */
@Entity
@Table(name = "lock_candidat")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class LockCandidat implements Serializable {

	@EmbeddedId
	private LockCandidatPK id;

	@Column(name = "instance_id_lock", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String instanceIdLock;

	@Column(name = "ui_id_lock", nullable = false)
	@NotNull
	private String uiIdLock;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_lock", nullable = false)
	@NotNull
	private LocalDateTime datLock;

	public LockCandidat(final LockCandidatPK id, final String instanceIdLock, final String uiIdLock) {
		super();
		this.id = id;
		this.instanceIdLock = instanceIdLock;
		this.uiIdLock = uiIdLock;
		this.datLock = LocalDateTime.now();
	}

	public LockCandidat() {
		super();
	}
}
