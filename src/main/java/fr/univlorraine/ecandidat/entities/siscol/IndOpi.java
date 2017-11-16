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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;


/**
 * The persistent class for the IND_OPI database table.
 * 
 */
@Entity
@Data
@Table(name="IND_OPI")
public class IndOpi implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="COD_IND_OPI", unique=true, nullable=false, precision=8)
	private long codIndOpi;

	@Column(name="ADR_MAIL_OPI", length=200)
	private String adrMailOpi;

	@Column(name="COD_CLE_NNE_IND_OPI", length=1)
	private String codCleNneIndOpi;

	@Column(name="COD_DAP", length=2)
	private String codDap;

	@Column(name="COD_DEP_PAY_ANN_PRE_OPI", length=3)
	private String codDepPayAnnPreOpi;

	@Column(name="COD_DEP_PAY_ANT_IAA_OPI", length=3)
	private String codDepPayAntIaaOpi;

	@Column(name="COD_DEP_PAY_DER_DIP", length=3)
	private String codDepPayDerDip;

	@Column(name="COD_DEP_PAY_NAI", length=3)
	private String codDepPayNai;

	@Column(name="COD_ETB", length=8)
	private String codEtb;

	@Column(name="COD_ETB_ANN_CRT", length=8)
	private String codEtbAnnCrt;

	@Column(name="COD_ETB_ANN_PRE_OPI", length=8)
	private String codEtbAnnPreOpi;

	@Column(name="COD_ETB_ANT_IAA", length=8)
	private String codEtbAntIaa;

	@Column(name="COD_ETB_DER_DIP", length=8)
	private String codEtbDerDip;

	@Column(name="COD_ETR_OPI", length=8)
	private String codEtrOpi;

	@Column(name="COD_ETU_OPI", precision=8)
	private BigDecimal codEtuOpi;

	@Column(name="COD_FAM", length=1)
	private String codFam;

	@Column(name="COD_IND", precision=8)
	private BigDecimal codInd;

	@Column(name="COD_NNE_IND_OPI", length=10)
	private String codNneIndOpi;

	@Column(name="COD_OPI_INT_EPO", nullable=false, length=10)
	private String codOpiIntEpo;

	@Column(name="COD_PAY_NAT", length=3)
	private String codPayNat;

	@Column(name="COD_PCS", length=2)
	private String codPcs;

	@Column(name="COD_PCS_AP", length=2)
	private String codPcsAp;

	@Column(name="COD_PRG_OPI", length=1)
	private String codPrgOpi;

	@Column(name="COD_RGI", length=1)
	private String codRgi;

	@Column(name="COD_SEX_ETU_OPI", length=1)
	private String codSexEtuOpi;

	@Column(name="COD_SIM", length=1)
	private String codSim;

	@Column(name="COD_SIS_ANN_PRE_OPI", length=1)
	private String codSisAnnPreOpi;

	@Column(name="COD_STU", length=2)
	private String codStu;

	@Column(name="COD_TDE_DER_DIP", length=3)
	private String codTdeDerDip;

	@Column(name="COD_TDS_OPI", length=1)
	private String codTdsOpi;

	@Column(name="COD_THB_OPI", length=1)
	private String codThbOpi;

	@Column(name="COD_THP_OPI", length=2)
	private String codThpOpi;

	@Column(name="COD_TPE_ANN_CRT", length=2)
	private String codTpeAnnCrt;

	@Column(name="COD_TPE_ANT_IAA", length=2)
	private String codTpeAntIaa;

	@Column(name="COD_TYP_DEP_PAY_ANN_PRE_OPI", length=1)
	private String codTypDepPayAnnPreOpi;

	@Column(name="COD_TYP_DEP_PAY_ANT_IAA_OPI", length=1)
	private String codTypDepPayAntIaaOpi;

	@Column(name="COD_TYP_DEP_PAY_DER_DIP", length=1)
	private String codTypDepPayDerDip;

	@Column(name="COD_TYP_DEP_PAY_NAI", length=1)
	private String codTypDepPayNai;

	@Column(name="DAA_ENS_SUP_OPI", length=4)
	private String daaEnsSupOpi;

	@Column(name="DAA_ENT_ETB_OPI", length=4)
	private String daaEntEtbOpi;

	@Column(name="DAA_ETB_ANT_IAA_OPI", length=9)
	private String daaEtbAntIaaOpi;

	@Column(name="DAA_ETB_DER_DIP", length=9)
	private String daaEtbDerDip;

	@Column(name="DAA_ETB_OPI", length=4)
	private String daaEtbOpi;

	@Column(name="DAA_ETR_SUP", length=9)
	private String daaEtrSup;

	@Column(name="DAA_LBT_IND_OPI", length=4)
	private String daaLbtIndOpi;

	@Temporal(TemporalType.DATE)
	@Column(name="DATE_NAI_IND_OPI", nullable=false)
	private Date dateNaiIndOpi;

	@Column(name="DMM_LBT_IND_OPI", length=2)
	private String dmmLbtIndOpi;

	@Column(name="DUR_EXP_PRO_EPO", precision=2)
	private BigDecimal durExpProEpo;

	@Column(name="LIB_NOM_PAT_IND_OPI", nullable=false, length=30)
	private String libNomPatIndOpi;

	@Column(name="LIB_NOM_USU_IND_OPI", length=30)
	private String libNomUsuIndOpi;

	@Column(name="LIB_PR1_IND_OPI", nullable=false, length=20)
	private String libPr1IndOpi;

	@Column(name="LIB_PR2_IND_OPI", length=20)
	private String libPr2IndOpi;

	@Column(name="LIB_PR3_IND_OPI", length=20)
	private String libPr3IndOpi;

	@Column(name="LIB_VIL_NAI_ETU_OPI", length=30)
	private String libVilNaiEtuOpi;

	@Column(name="NB_ENF_ETU_OPI", precision=1)
	private BigDecimal nbEnfEtuOpi;

	@Column(name="NUM_TEL_IND_OPI", length=15)
	private String numTelIndOpi;

	@Column(name="NUM_TEL_POR_OPI", length=15)
	private String numTelPorOpi;

	@Column(name="NUM_TEL_TEMP_EPO", length=15)
	private String numTelTempEpo;

	@Column(name="TEM_DATE_NAI_REL_OPI", nullable=false, length=1)
	private String temDateNaiRelOpi;

	@Column(name="TEM_EXP_PRO_EPO", length=1)
	private String temExpProEpo;

	@Column(name="TEM_INT_EPO", length=1)
	private String temIntEpo;

	@Column(name="TEM_MI_TPS_EPO", precision=2)
	private BigDecimal temMiTpsEpo;

	@Column(name="TEM_PRL_ANN_CRT", length=1)
	private String temPrlAnnCrt;

	@Column(name="TEM_SNS_PRG_OPI", length=1)
	private String temSnsPrgOpi;

	@Column(name="TEM_TRF_ANN_CRT", length=1)
	private String temTrfAnnCrt;
}