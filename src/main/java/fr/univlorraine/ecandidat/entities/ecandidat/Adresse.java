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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the adresse database table.
 */
@Entity
@Table(name = "adresse")
@Data
@EqualsAndHashCode(of = "idAdr")
@SuppressWarnings("serial")
public class Adresse implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_adr", nullable = false)
	private Integer idAdr;

	@Column(name = "typ_siscol", nullable = false, length = 1)
	@Size(max = 1)
	@NotNull
	private String typSiScol;

	@Column(name = "adr1_adr", nullable = false, length = 32)
	@Size(max = 32)
	@NotNull
	private String adr1Adr;

	@Column(name = "adr2_adr", length = 32)
	@Size(max = 32)
	private String adr2Adr;

	@Column(name = "adr3_adr", length = 32)
	@Size(max = 32)
	private String adr3Adr;

	@Column(name = "cod_bdi_adr", length = 5)
	@Size(max = 5, min = 5)
	private String codBdiAdr;

	@Column(name = "cedex_adr", length = 50)
	@Size(max = 50)
	private String cedexAdr;

	@Column(name = "lib_com_etr_adr", length = 32)
	@Size(max = 32)
	private String libComEtrAdr;

	// bi-directional many-to-one association to SiScolCommune
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_com", referencedColumnName = "cod_com"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	private SiScolCommune siScolCommune;

	// bi-directional many-to-one association to SiScolPays
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "cod_pay", referencedColumnName = "cod_pay"),
		@JoinColumn(name = "typ_siscol", referencedColumnName = "typ_siscol", insertable = false, updatable = false)
	})
	@NotNull
	private SiScolPays siScolPays;

	// bi-directional many-to-one association to Candidat
	@OneToMany(mappedBy = "adresse")
	private List<Candidat> candidats;

	// bi-directional many-to-one association to Commission
	@OneToMany(mappedBy = "adresse")
	private List<Commission> commissions;

	public Adresse() {
		super();
	}

	public Adresse(final String typSiscol) {
		super();
		this.typSiScol = typSiscol;
	}

	public Adresse(final String adr1Adr,
		final String adr2Adr,
		final String adr3Adr,
		final String codBdiAdr,
		final String libComEtrAdr,
		final SiScolCommune siScolCommune,
		final SiScolPays siScolPays,
		final String typSiscol) {
		super();
		this.adr1Adr = adr1Adr;
		this.adr2Adr = adr2Adr;
		this.adr3Adr = adr3Adr;
		this.codBdiAdr = codBdiAdr;
		this.libComEtrAdr = libComEtrAdr;
		this.siScolCommune = siScolCommune;
		this.siScolPays = siScolPays;
		this.typSiScol = typSiscol;
	}

	/**
	 * Recupere les champs d'une autre adresse
	 * @param a
	 */
	public void duplicateAdresse(final Adresse a) {
		this.adr1Adr = a.getAdr1Adr();
		this.adr2Adr = a.getAdr2Adr();
		this.adr3Adr = a.getAdr3Adr();
		this.codBdiAdr = a.getCodBdiAdr();
		this.cedexAdr = a.getCedexAdr();
		this.libComEtrAdr = a.getLibComEtrAdr();
		this.siScolCommune = a.getSiScolCommune();
		this.siScolPays = a.getSiScolPays();
		this.typSiScol = a.getTypSiScol();
	}
}
