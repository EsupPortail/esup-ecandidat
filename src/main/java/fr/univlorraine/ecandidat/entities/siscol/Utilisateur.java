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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * The persistent class for the UTILISATEUR database table.
 * 
 */
@Entity
@Data @EqualsAndHashCode(of="codUti")
public class Utilisateur implements Serializable {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = 6806478024550984011L;

	@Id
	@Column(name="COD_UTI", unique=true, nullable=false, length=30)
	@Size(max = 30) 
	@NotNull
	private String codUti;

	@Column(name="ADR_MAIL_UTI", length=200)
	@Size(max = 200) 
	private String adrMailUti;

	@Column(name="LIB_CMT_UTI", length=200)
	@Size(max = 200) 
	private String libCmtUti;

	@Column(name="TEM_EN_SVE_UTI", nullable=false, length=1)
	@Size(max = 1) 
	@NotNull
	private String temEnSveUti;

	//bi-directional many-to-one association to CentreGestion
	@ManyToOne
	@JoinColumn(name="COD_CGE")
	private CentreGestion centreGestion;
}