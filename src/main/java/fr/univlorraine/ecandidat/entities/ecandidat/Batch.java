/** ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.EntityPushEntityListener;
import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import fr.univlorraine.ecandidat.entities.tools.LocalTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** The persistent class for the batch database table. */
@Entity
@EntityListeners(EntityPushEntityListener.class)
@Table(name = "batch")
@Data
@EqualsAndHashCode(of = "codBatch")
@ToString(exclude = {"batchHistos", "lastBatchHisto"})
public class Batch implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "cod_batch", nullable = false, length = 30)
	@Size(max = 30)
	@NotNull
	private String codBatch;

	@Column(name = "lib_batch", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String libBatch;

	@Column(name = "tem_is_launch_imedia_batch", nullable = false)
	@NotNull
	private Boolean temIsLaunchImediaBatch;

	@Column(name = "tes_batch", nullable = false)
	@NotNull
	private Boolean tesBatch;

	@Column(name = "fixe_hour_batch", nullable = false)
	@Convert(converter = LocalTimePersistenceConverter.class)
	@NotNull
	private LocalTime fixeHourBatch;

	@Column(name = "fixe_day_batch")
	private Integer fixeDayBatch;

	@Column(name = "fixe_month_batch")
	private Integer fixeMonthBatch;

	@Column(name = "fixe_year_batch")
	private Integer fixeYearBatch;

	@Column(name = "frequence_batch", nullable = false)
	@NotNull
	private Integer frequenceBatch;

	@Column(name = "tem_diman_batch", nullable = false)
	@NotNull
	private Boolean temDimanBatch;

	@Column(name = "tem_jeudi_batch", nullable = false)
	@NotNull
	private Boolean temJeudiBatch;

	@Column(name = "tem_lundi_batch", nullable = false)
	@NotNull
	private Boolean temLundiBatch;

	@Column(name = "tem_mardi_batch", nullable = false)
	@NotNull
	private Boolean temMardiBatch;

	@Column(name = "tem_mercr_batch", nullable = false)
	@NotNull
	private Boolean temMercrBatch;

	@Column(name = "tem_samedi_batch", nullable = false)
	@NotNull
	private Boolean temSamediBatch;

	@Column(name = "tem_vendredi_batch", nullable = false)
	@NotNull
	private Boolean temVendrediBatch;

	@Column(name = "tem_frequence_batch", nullable = false)
	@NotNull
	private Boolean temFrequenceBatch;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "last_dat_execution_batch")
	private LocalDateTime lastDatExecutionBatch;

	@Transient
	private BatchHisto lastBatchHisto;

	// bi-directional many-to-one association to BatchHisto
	@OneToMany(mappedBy = "batch")
	private List<BatchHisto> batchHistos;

	public Batch() {
		super();
	}

	public Batch(final String codBatch, final String libBatch,
			final Boolean temIsLaunchImediaBatch, final Boolean tesBatch, final Integer hour, final Integer min) {
		super();
		this.codBatch = codBatch;
		this.libBatch = libBatch;
		this.temIsLaunchImediaBatch = temIsLaunchImediaBatch;
		this.tesBatch = tesBatch;
		this.temLundiBatch = false;
		this.temMardiBatch = false;
		this.temMercrBatch = false;
		this.temJeudiBatch = false;
		this.temVendrediBatch = false;
		this.temSamediBatch = false;
		this.temDimanBatch = false;
		this.temFrequenceBatch = false;
		this.frequenceBatch = 0;
		this.fixeHourBatch = LocalTime.of(hour, min);
	}

	public Batch(final String codBatch, final String libBatch,
			final Boolean temIsLaunchImediaBatch, final Boolean tesBatch,
			final Boolean temLundiBatch, final Boolean temMardiBatch, final Boolean temMercrBatch, final Boolean temJeudiBatch,
			final Boolean temVendrediBatch, final Boolean temSamediBatch, final Boolean temDimanBatch, final Integer hour, final Integer min) {
		super();
		this.codBatch = codBatch;
		this.libBatch = libBatch;
		this.temIsLaunchImediaBatch = temIsLaunchImediaBatch;
		this.tesBatch = tesBatch;
		this.temLundiBatch = temLundiBatch;
		this.temMardiBatch = temMardiBatch;
		this.temMercrBatch = temMercrBatch;
		this.temJeudiBatch = temJeudiBatch;
		this.temVendrediBatch = temVendrediBatch;
		this.temSamediBatch = temSamediBatch;
		this.temDimanBatch = temDimanBatch;
		this.temFrequenceBatch = false;
		this.frequenceBatch = 0;
		this.fixeHourBatch = LocalTime.of(hour, min);
	}
}
