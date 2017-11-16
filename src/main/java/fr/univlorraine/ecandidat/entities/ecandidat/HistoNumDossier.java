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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.domain.Persistable;

import lombok.Data;


/**
 * The persistent class for the histo_num_dossier database table.
 * 
 */
@Entity @Data
@Table(name="histo_num_dossier")
public class HistoNumDossier implements Serializable, Persistable<String> {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="num_dossier", unique=true, nullable=false, updatable=true ,length=8)
	@Size(max = 8) 
	@NotNull
	private String numDossier;
	
	@Column(name="cod_camp", nullable=false, updatable=true, length=20)
	@Size(max = 20) 
	@NotNull
	private String codCamp;

	public HistoNumDossier() {
		super();
	}

	public HistoNumDossier(String numDossier, String codCamp) {
		super();
		this.numDossier = numDossier;
		this.codCamp = codCamp;
	}

	@Override
	public String getId() {
		return numDossier;
	}

	@Override
	public boolean isNew() {
		return true;
	}
}