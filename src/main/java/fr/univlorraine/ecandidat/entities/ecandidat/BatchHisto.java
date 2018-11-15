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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fr.univlorraine.ecandidat.entities.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** The persistent class for the batch_histo database table. */
@Entity
@Table(name = "batch_histo")
@Data
@EqualsAndHashCode(of = "idBatchHisto")
@SuppressWarnings("serial")
public class BatchHisto implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_batch_histo", nullable = false)
	private Integer idBatchHisto;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "date_deb_batch_histo")
	private LocalDateTime dateDebBatchHisto;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "date_fin_batch_histo")
	private LocalDateTime dateFinBatchHisto;

	@Column(name = "state_batch_histo", length = 10)
	@Size(max = 10)
	private String stateBatchHisto;

	@Lob
	@Column(name = "detail_batch_histo", nullable = true, columnDefinition = "TEXT")
	private String detailBatchHisto;

	// bi-directional many-to-one association to Batch
	@ManyToOne
	@JoinColumn(name = "cod_batch")
	@NotNull
	private Batch batch;

	public BatchHisto clone(final Batch batch) {
		BatchHisto bh = new BatchHisto();
		bh.setDateDebBatchHisto(this.dateDebBatchHisto);
		bh.setDateFinBatchHisto(this.dateFinBatchHisto);
		bh.setStateBatchHisto(this.stateBatchHisto);
		bh.setDetailBatchHisto(this.detailBatchHisto);
		bh.setBatch(batch);
		return bh;
	}
}
