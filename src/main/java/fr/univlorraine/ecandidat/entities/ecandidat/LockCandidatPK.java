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
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The primary key class for the lock_candidat database table.
 * 
 */
@Data @EqualsAndHashCode(of={"numDossierOpiCptMin","ressourceLock"})
@Embeddable
@ToString(of={"numDossierOpiCptMin","ressourceLock"})
public class LockCandidatPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="num_dossier_opi_cpt_min", nullable=false, length=8)
	@Size(max = 8) 
	@NotNull
	private String numDossierOpiCptMin;

	@Column(name="ressource_lock", nullable=false, length=40)
	@Size(max = 40)
	@NotNull
	private String ressourceLock;

	public LockCandidatPK() {
	}
	
	public LockCandidatPK(String numDossierOpiCptMin, String ressourceLock) {
		super();
		this.numDossierOpiCptMin = numDossierOpiCptMin;
		this.ressourceLock = ressourceLock;
	}
}