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
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * The persistent class for the load_balancing_reload database table.
 */
@Entity
@Data
@Table(name = "load_balancing_reload")
@SuppressWarnings("serial")
public class LoadBalancingReload implements Serializable {

	@Id
	@Column(name = "cod_data_lb_reload", unique = true, nullable = false, length = 20)
	private String codDataLbReload;

	@Column(name = "dat_cre_lb_reload", nullable = false)
	@NotNull
	private LocalDateTime datCreLbReload;

	public LoadBalancingReload() {
		super();
	}

	public LoadBalancingReload(final String codDataLbReload, final LocalDateTime datCreLbReload) {
		super();
		this.codDataLbReload = codDataLbReload;
		this.datCreLbReload = datCreLbReload;
	}
}
