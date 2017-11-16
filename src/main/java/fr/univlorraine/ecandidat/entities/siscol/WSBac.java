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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;


/**
 * The persistent class for the IND_BAC database table.
 * 
 */
@Entity
@Table(name="IND_BAC")
@Data
@ToString(exclude={"codInd"})
public class WSBac implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="COD_IND")
	private long codInd;

	@Column(name="COD_BAC", length=4)
	private String codBac;

	@Column(name="COD_DEP", length=3)
	private String codDep;

	@Column(name="COD_ETB", length=8)
	private String codEtb;

	@Column(name="COD_MNB", length=2)
	private String codMnb;

	@Column(name="DAA_OBT_BAC_IBA", length=4)
	private String daaObtBacIba;
	
	@Column(name="TEM_INS_ADM", nullable=false, length=1)
	private String temInsAdm;

	public WSBac() {
		super();
	}

	public WSBac(long codInd, String codBac, String codDep, String codEtb,
			String codMnb, String daaObtBacIba, String temInsAdm) {
		super();
		this.codInd = codInd;
		this.codBac = codBac;
		this.codDep = codDep;
		this.codEtb = codEtb;
		this.codMnb = codMnb;
		this.daaObtBacIba = daaObtBacIba;
		this.temInsAdm = temInsAdm;
	}
}