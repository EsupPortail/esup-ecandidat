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
package fr.univlorraine.ecandidat.services.security;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;

/**
 * La classe de commission d'un user
 * @author Kevin Hergalant
 *
 */
@Data
public class SecurityCommission implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 521681334340754635L;
	
	private Integer idComm;
	private String libComm;
	private List<DroitProfilFonc> listFonctionnalite;
	private Boolean isAdmin;

	public SecurityCommission(Commission comm, List<DroitProfilFonc> listFonctionnalite, Boolean isAdmin) {
		this.idComm = comm.getIdComm();
		this.libComm = comm.getLibComm();
		this.listFonctionnalite = listFonctionnalite;
		this.isAdmin = isAdmin;
	}
}
