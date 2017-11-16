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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the siScol_pays database table.
 * 
 */
@Entity
@Table(name="siscol_pays")
@Data @EqualsAndHashCode(of="codPay")
public class SiScolPays implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_pay", nullable=false, length=3)
	@Size(max = 3) 
	@NotNull
	private String codPay;

	@Column(name="lib_nat", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libNat;

	@Column(name="lib_pay", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libPay;

	@Column(name="lic_pay", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licPay;

	@Column(name="tem_en_sve_pay", nullable=false)
	@NotNull
	private Boolean temEnSvePay;

	//bi-directional many-to-one association to Adresse
	@OneToMany(mappedBy="siScolPays")
	private List<Adresse> adresses;

	//bi-directional many-to-one association to Candidat
	@OneToMany(mappedBy="siScolPaysNaiss")
	private List<Candidat> candidatsPaysNaiss;

	//bi-directional many-to-one association to Candidat
	@OneToMany(mappedBy="siScolPaysNat")
	private List<Candidat> candidatsPaysNat;

	//bi-directional many-to-one association to CandidatBacOuEqu
	@OneToMany(mappedBy="siScolPays")
	private List<CandidatBacOuEqu> candidatBacOuEqus;

	//bi-directional many-to-one association to CandidatCursusPostBac
	@OneToMany(mappedBy="siScolPays")
	private List<CandidatCursusPostBac> candidatCursusPostBacs;

	public SiScolPays() {
		super();
	}

	public SiScolPays(String codPay, String libNat, String libPay,
			String licPay, Boolean temEnSvePay) {
		super();
		this.codPay = codPay;
		this.libNat = libNat;
		this.libPay = libPay;
		this.licPay = licPay;
		this.temEnSvePay = temEnSvePay;
	}
}