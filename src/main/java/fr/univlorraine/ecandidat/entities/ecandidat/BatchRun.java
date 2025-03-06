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

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** The persistent class for the batch_run database table. */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "batch_run")
@Data
@EqualsAndHashCode(of = "codRun")
@SuppressWarnings("serial")
public class BatchRun implements Serializable {

	public static final String COD_RUN_BATCH = "BATCH";

	@Id
	@Column(name = "cod_run", nullable = false)
	@NotNull
	private String codRun;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_last_check_run", nullable = false)
	@NotNull
	private LocalDateTime datLastCheckRun;

	public BatchRun() {
		super();
	}

	public BatchRun(final String codRun, final LocalDateTime datLastCheckRun) {
		super();
		this.codRun = codRun;
		this.datLastCheckRun = datLastCheckRun;
	}
}
