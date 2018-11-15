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
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the load_balancing_reload_run database table.
 */
@Data
@EqualsAndHashCode(of = {"datLastCheckLbReloadRun", "instanceIdLbReloadRun"})
@Embeddable
@ToString(of = {"datLastCheckLbReloadRun", "instanceIdLbReloadRun"})
@SuppressWarnings("serial")
public class LoadBalancingReloadRunPK implements Serializable {
	// default serial version id, required for serializable classes.

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_last_check_lb_reload_run", unique = true, nullable = false)
	@NotNull
	private LocalDateTime datLastCheckLbReloadRun;

	@Column(name = "instance_id_lb_reload_run", unique = true, nullable = false, length = 20)
	@NotNull
	private String instanceIdLbReloadRun;

	public LoadBalancingReloadRunPK() {
		super();
	}

	public LoadBalancingReloadRunPK(final LocalDateTime datLastCheckLbReloadRun, final String instanceIdLbReloadRun) {
		super();
		this.datLastCheckLbReloadRun = datLastCheckLbReloadRun;
		this.instanceIdLbReloadRun = instanceIdLbReloadRun;
	}
}
