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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalDateTimePersistenceConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** The persistent class for the pj_opi database table. */
@Entity
@Table(name = "pj_opi")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "idFichier", "datDeversement", "candidat", "codIndOpi"})
@SuppressWarnings("serial")
public class PjOpi implements Serializable {

	@EmbeddedId
	private PjOpiPK id;

	@Convert(converter = LocalDateTimePersistenceConverter.class)
	@Column(name = "dat_deversement")
	private LocalDateTime datDeversement;

	@Column(name = "cod_ind_opi")
	private String codIndOpi;

	@Column(name = "id_fichier")
	@NotNull
	private Integer idFichier;

	// bi-directional many-to-one association to Campagne
	@ManyToOne
	@JoinColumn(name = "id_candidat")
	private Candidat candidat;
}
