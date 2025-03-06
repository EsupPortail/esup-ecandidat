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
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import fr.univlorraine.ecandidat.utils.migration.RealeaseVersion;
import lombok.Data;

/**
 * The persistent class for the schema_version database table.
 */
@Entity
@Table(name = "schema_version")
@Data
@SuppressWarnings("serial")
public class SchemaVersion implements Serializable {

	@Id
	@Column(name = "installed_rank")
	private Integer installedRank;

	@Column
	private String description;

	@Column
	private String version;

	@Column
	private String script;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "installed_on")
	private LocalDateTime installedOn;

	@Column
	private Boolean success;

	@Transient
	private RealeaseVersion releaseVersion;
}
