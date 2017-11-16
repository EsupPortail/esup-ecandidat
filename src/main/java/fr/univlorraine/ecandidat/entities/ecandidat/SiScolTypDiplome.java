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
 * The persistent class for the siscol_typ_diplome database table.
 * 
 */
@Entity
@Table(name="siscol_typ_diplome")
@Data @EqualsAndHashCode(of="codTpdEtb")
public class SiScolTypDiplome implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="cod_tpd_etb", nullable=false, length=2)
	@Size(max = 2) 
	@NotNull
	private String codTpdEtb;

	@Column(name="lib_tpd", nullable=false, length=40)
	@Size(max =40) 
	@NotNull
	private String libTpd;

	@Column(name="lic_tpd", nullable=false, length=10)
	@Size(max = 10) 
	@NotNull
	private String licTpd;

	@Column(name="tem_en_sve_tpd", nullable=false)
	@NotNull
	private Boolean temEnSveTpd;

	//bi-directional many-to-one association to Formation
	@OneToMany(mappedBy="siScolTypDiplome")
	private List<Formation> formations;
	
	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.codTpdEtb+"/"+this.libTpd;
	}

	public SiScolTypDiplome() {
		super();
	}

	public SiScolTypDiplome(String codTpdEtb, String libTpd, String licTpd,
			Boolean temEnSveTpd) {
		super();
		this.codTpdEtb = codTpdEtb;
		this.libTpd = libTpd;
		this.licTpd = licTpd;
		this.temEnSveTpd = temEnSveTpd;
	}
}