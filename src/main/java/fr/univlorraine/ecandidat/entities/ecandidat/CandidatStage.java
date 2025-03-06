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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the candidat_stage database table.
 */
@Entity
@Table(name = "candidat_stage")
@Data
@EqualsAndHashCode(of = "idStage")
@ToString(exclude = {"candidat"})
@SuppressWarnings("serial")
public class CandidatStage implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_stage", nullable = false)
	private Integer idStage;

	@Column(name = "descriptif_stage", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String descriptifStage;

	@Column(name = "duree_stage", nullable = false, length = 20)
	@Size(max = 20)
	@NotNull
	private String dureeStage;

	@Column(name = "nb_h_sem_stage")
	private Integer nbHSemStage;

	@Column(name = "organisme_stage", nullable = false, length = 50)
	@Size(max = 50)
	@NotNull
	private String organismeStage;

	@Column(name = "annee_stage", nullable = false)
	@NotNull
	private Integer anneeStage;

	// bi-directional many-to-one association to Candidat
	@ManyToOne
	@JoinColumn(name = "id_candidat", nullable = false)
	@NotNull
	private Candidat candidat;
}
