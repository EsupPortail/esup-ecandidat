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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The persistent class for the candidat_bac_ou_equ database table.
 */
@Entity
@Table(name = "candidat_bac_ou_equ")
@Data
@EqualsAndHashCode(of = "idCandidat")
@ToString(exclude = { "candidat" })
@SuppressWarnings("serial")
public class CandidatBacOuEqu implements Serializable {

	@Id
	@Column(name = "id_candidat", nullable = false)
	private Integer idCandidat;

	@Column(name = "annee_obt_bac")
	private Integer anneeObtBac;

	// bi-directional many-to-one association to SiScolBacOuxEqu
	@ManyToOne
	@JoinColumn(name = "cod_bac", nullable = false)
	@NotNull
	private SiScolBacOuxEqu siScolBacOuxEqu;

	// bi-directional many-to-one association to SiScolCommune
	@ManyToOne
	@JoinColumn(name = "cod_com")
	private SiScolCommune siScolCommune;

	// bi-directional many-to-one association to SiScolDepartement
	@ManyToOne
	@JoinColumn(name = "cod_dep")
	private SiScolDepartement siScolDepartement;

	// bi-directional many-to-one association to SiScolEtablissement
	@ManyToOne
	@JoinColumn(name = "cod_etb")
	private SiScolEtablissement siScolEtablissement;

	// bi-directional many-to-one association to SiScolMentionNivBac
	@ManyToOne
	@JoinColumn(name = "cod_mnb")
	private SiScolMentionNivBac siScolMentionNivBac;

	// bi-directional many-to-one association to SiScolPays
	@ManyToOne
	@JoinColumn(name = "cod_pay")
	private SiScolPays siScolPays;

	// bi-directional many-to-one association to SiScolSpecialiteBac
	@ManyToOne
	@JoinColumn(name = "cod_spe1_bac_ter")
	private SiScolSpecialiteBac siScolSpe1BacTer;

	// bi-directional many-to-one association to SiScolSpecialiteBac
	@ManyToOne
	@JoinColumn(name = "cod_spe2_bac_ter")
	private SiScolSpecialiteBac siScolSpe2BacTer;

	// bi-directional many-to-one association to SiScolSpecialiteBac
	@ManyToOne
	@JoinColumn(name = "cod_spe_bac_pre")
	private SiScolSpecialiteBac siScolSpeBacPre;

	// bi-directional many-to-one association to SiScolSpecialiteBac
	@ManyToOne
	@JoinColumn(name = "cod_opt1_bac")
	private SiScolOptionBac siScolOpt1Bac;

	@ManyToOne
	@JoinColumn(name = "cod_opt2_bac")
	private SiScolOptionBac siScolOpt2Bac;

	@ManyToOne
	@JoinColumn(name = "cod_opt3_bac")
	private SiScolOptionBac siScolOpt3Bac;

	@ManyToOne
	@JoinColumn(name = "cod_opt4_bac")
	private SiScolOptionBac siScolOpt4Bac;

	// bi-directional one-to-one association to Candidat
	@OneToOne
	@JoinColumn(name = "id_candidat", nullable = false, insertable = false, updatable = false)
	@NotNull
	private Candidat candidat;

	@Column(name = "tem_updatable_bac", nullable = false)
	@NotNull
	private Boolean temUpdatableBac;

	public CandidatBacOuEqu() {
		super();
	}

	public CandidatBacOuEqu(final Integer idCandidat,
		final Integer anneeObtBac,
		final SiScolBacOuxEqu siScolBacOuxEqu,
		final SiScolCommune siScolCommune,
		final SiScolDepartement siScolDepartement,
		final SiScolEtablissement siScolEtablissement,
		final SiScolMentionNivBac siScolMentionNivBac,
		final SiScolPays siScolPays,
		final Candidat candidat,
		final Boolean temUpdatableBac,
		final SiScolSpecialiteBac speBacPre,
		final SiScolSpecialiteBac spe1Bac,
		final SiScolSpecialiteBac spe2Bac,
		final SiScolOptionBac opt1Bac,
		final SiScolOptionBac opt2Bac,
		final SiScolOptionBac opt3Bac,
		final SiScolOptionBac opt4Bac) {
		super();
		this.idCandidat = idCandidat;
		this.anneeObtBac = anneeObtBac;
		this.siScolBacOuxEqu = siScolBacOuxEqu;
		this.siScolCommune = siScolCommune;
		this.siScolDepartement = siScolDepartement;
		this.siScolEtablissement = siScolEtablissement;
		this.siScolMentionNivBac = siScolMentionNivBac;
		this.siScolPays = siScolPays;
		this.candidat = candidat;
		this.temUpdatableBac = temUpdatableBac;
		this.siScolSpeBacPre = speBacPre;
		this.siScolSpe1BacTer = spe1Bac;
		this.siScolSpe2BacTer = spe2Bac;
		this.siScolOpt1Bac = opt1Bac;
		this.siScolOpt2Bac = opt2Bac;
		this.siScolOpt3Bac = opt3Bac;
		this.siScolOpt4Bac = opt4Bac;
	}

}
