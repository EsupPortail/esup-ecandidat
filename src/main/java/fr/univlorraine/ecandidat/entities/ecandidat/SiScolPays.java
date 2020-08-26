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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the siScol_pays database table.
 */
@Entity
@Table(name = "siscol_pays")
@Data
@EqualsAndHashCode(of = "id")
@SuppressWarnings("serial")
public class SiScolPays implements Serializable {

	@EmbeddedId
	private SiScolPaysPK id;

	@Column(name = "lib_nat", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libNat;

	@Column(name = "lib_pay", nullable = false, length = 500)
	@Size(max = 500)
	@NotNull
	private String libPay;

	@Column(name = "lic_pay", nullable = false, length = 200)
	@Size(max = 200)
	@NotNull
	private String licPay;

	@Column(name = "tem_en_sve_pay", nullable = false)
	@NotNull
	private Boolean temEnSvePay;

	// bi-directional many-to-one association to Adresse
	@OneToMany(mappedBy = "siScolPays")
	private List<Adresse> adresses;

	// bi-directional many-to-one association to Candidat
	@OneToMany(mappedBy = "siScolPaysNaiss")
	private List<Candidat> candidatsPaysNaiss;

	// bi-directional many-to-one association to Candidat
	@OneToMany(mappedBy = "siScolPaysNat")
	private List<Candidat> candidatsPaysNat;

	// bi-directional many-to-one association to CandidatBacOuEqu
	@OneToMany(mappedBy = "siScolPays")
	private List<CandidatBacOuEqu> candidatBacOuEqus;

	// bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy = "siScolPays")
	private List<CandidatCursusPostBac> candidatCursusPostBacs;

	public SiScolPays() {
		super();
	}

	public SiScolPays(final String codPay,
		final String libNat,
		final String libPay,
		final String licPay,
		final Boolean temEnSvePay,
		final String typSiScol) {
		super();
		this.id = new SiScolPaysPK(codPay, typSiScol);
		this.libNat = libNat;
		this.libPay = libPay;
		this.licPay = licPay;
		this.temEnSvePay = temEnSvePay;
	}
}
