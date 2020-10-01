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
package fr.univlorraine.ecandidat.entities.siscol;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Data;

/**
 * The persistent class for the INDIVIDU database table.
 */
@Entity
@Data
@SuppressWarnings("serial")
public class WSIndividu implements Serializable {

	@Id
	@Column(name = "COD_IND", unique = true, nullable = false)
	private String codInd;

	@Column(name = "COD_CIV", length = 1)
	private String codCiv;

	@Column(name = "COD_DEP_PAY_NAI", length = 3)
	private String codDepPayNai;

	@Column(name = "COD_ETU", unique = true, precision = 8)
	private String codEtu;

	@Column(name = "COD_NNE_IND", length = 10)
	private String codNneInd;

	@Column(name = "COD_CLE_NNE_IND", length = 1)
	private String codCleNneInd;

	@Column(name = "COD_PAY_NAT", length = 3)
	private String codPayNat;

	@Column(name = "COD_TYP_DEP_PAY_NAI", length = 1)
	private String codTypDepPayNai;

	@Column(name = "DATE_NAI_IND")
	private LocalDate dateNaiInd;

	@Column(name = "LIB_NOM_PAT_IND", length = 30)
	private String libNomPatInd;

	@Column(name = "LIB_NOM_USU_IND", length = 30)
	private String libNomUsuInd;

	@Column(name = "LIB_PR1_IND", length = 20)
	private String libPr1Ind;

	@Column(name = "LIB_PR2_IND", length = 20)
	private String libPr2Ind;

	@Column(name = "LIB_VIL_NAI_ETU", length = 30)
	private String libVilNaiEtu;

	/* Données spéciales WS-->permet de ne pas recalculer le pays et le departement de naissance car le WS les ramene tel quel */
	@Transient
	private String codPayNai;
	@Transient
	private String codDepNai;
	@Transient
	private Boolean isWs;

	/**
	 * Bac de l'individu
	 */
	@Transient
	private WSBac bac;

	/**
	 * Adresse de l'individu
	 */
	@Transient
	private WSAdresse adresse;

	/**
	 * La liste des cursus interne de l'individu
	 */
	@Transient
	List<WSCursusInterne> listCursusInterne;

	public WSIndividu() {
		super();
	}

	public WSIndividu(final Integer codInd,
		final String codCiv,
		final String codDepPayNai,
		final BigDecimal codEtu,
		final String codNneInd,
		final String codCleNneInd,
		final String codTypDepPayNai,
		final LocalDate dateNaiInd,
		final String libNomPatInd,
		final String libNomUsuInd,
		final String libPr1Ind,
		final String libPr2Ind,
		final String libVilNaiEtu,
		final String codPayNat) {
		super();
		this.codInd = codInd != null ? String.valueOf(codInd) : null;
		this.codCiv = codCiv;
		this.codDepPayNai = codDepPayNai;
		this.codEtu = codEtu != null ? String.valueOf(codEtu) : null;
		this.codNneInd = codNneInd;
		this.codCleNneInd = codCleNneInd;
		this.codTypDepPayNai = codTypDepPayNai;
		this.dateNaiInd = dateNaiInd;
		this.libNomPatInd = libNomPatInd;
		this.libNomUsuInd = libNomUsuInd;
		this.libPr1Ind = libPr1Ind;
		this.libPr2Ind = libPr2Ind;
		this.libVilNaiEtu = libVilNaiEtu;
		this.codPayNat = codPayNat;
		this.isWs = false;
	}

	/* Constructeur spécial WS */
	public WSIndividu(final Integer codInd,
		final String codCiv,
		final BigDecimal codEtu,
		final String codNneInd,
		final String codCleNneInd,
		final LocalDate dateNaiInd,
		final String libNomPatInd,
		final String libNomUsuInd,
		final String libPr1Ind,
		final String libPr2Ind,
		final String libVilNaiEtu) {
		super();
		this.codInd = codInd != null ? String.valueOf(codInd) : null;
		this.codCiv = codCiv;
		this.codEtu = codEtu != null ? String.valueOf(codEtu) : null;
		this.codNneInd = codNneInd;
		this.codCleNneInd = codCleNneInd;
		this.dateNaiInd = dateNaiInd;
		this.libNomPatInd = libNomPatInd;
		this.libNomUsuInd = libNomUsuInd;
		this.libPr1Ind = libPr1Ind;
		this.libPr2Ind = libPr2Ind;
		this.libVilNaiEtu = libVilNaiEtu;
		this.isWs = true;
	}

	/* Constructeur spécial WS */
	public WSIndividu(final String code,
		final String codCiv,
		final LocalDate dateNaiInd,
		final String libNomPatInd,
		final String libNomUsuInd,
		final String libPr1Ind,
		final String libPr2Ind,
		final String libVilNaiEtu,
		final String codPayNai,
		final String codPayNat) {
		super();
		this.codInd = code;
		this.codCiv = codCiv;
		this.codEtu = code;
		this.dateNaiInd = dateNaiInd;
		this.libNomPatInd = libNomPatInd;
		this.libNomUsuInd = libNomUsuInd;
		this.libPr1Ind = libPr1Ind;
		this.libPr2Ind = libPr2Ind;
		this.libVilNaiEtu = libVilNaiEtu;
		this.codPayNai = codPayNai;
		this.codPayNat = codPayNat;
		this.isWs = true;
	}

	@Override
	public String toString() {
		return "WSIndividu(codInd=" + codInd
			+ ", codEtu="
			+ codEtu
			+ ", codCiv="
			+ codCiv
			+ ", codNneInd="
			+ codNneInd
			+ ", codCleNneInd="
			+ codCleNneInd
			+ ", dateNaiInd="
			+ dateNaiInd
			+ ", codPayNai="
			+ codPayNai
			+ ", codDepNai="
			+ codDepNai
			+ ", codPayNat="
			+ codPayNat
			+ ", libNomPatInd="
			+ libNomPatInd
			+ ", libNomUsuInd="
			+ libNomUsuInd
			+ ", libPr1Ind="
			+ libPr1Ind
			+ ", libPr2Ind="
			+ libPr2Ind
			+ ", libVilNaiEtu="
			+ libVilNaiEtu
			+ ")";
	}

}
