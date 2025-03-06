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
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the version database table.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "version")
@Data
@EqualsAndHashCode(of = "codVersion")
public class Version implements Serializable {

	@Id
	@Column(name = "cod_version", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String codVersion;

	@Column(name = "val_version", nullable = false, length = 100)
	@Size(max = 100)
	@NotNull
	private String valVersion;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_version", nullable = false)
	@NotNull
	private LocalDateTime datVersion;

	@PrePersist
	private void onPrePersist() {
		this.datVersion = LocalDateTime.now();
	}

	@PreUpdate
	private void onPreUpdate() {
		this.datVersion = LocalDateTime.now();
	}

	public Version() {
		super();
	}

	public Version(final String valVersion) {
		super();
		this.valVersion = valVersion;
	}

	public Version(final String codVersion, final String valVersion) {
		super();
		this.codVersion = codVersion;
		this.valVersion = valVersion;
	}
}
