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
package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;

@Repository
public interface DroitProfilIndRepository extends JpaRepository<DroitProfilInd, Integer> {	
	public List<DroitProfilInd> findByIndividuLoginInd(String login);

	public List<DroitProfilInd> findByIndividuLoginIndAndDroitProfilCodProfil(String login, String codProfil);
	
	public List<DroitProfilInd> findByIndividuLoginIndAndDroitProfilTypProfil(String login, String typProfil);

	public List<DroitProfilInd> findByDroitProfilTypProfil(String typProfil);

	public List<DroitProfilInd> findByDroitProfilCodProfil(String droitProfilScolCentrale);

	public List<DroitProfilInd> findByDroitProfilCodProfilAndGestionnaireCentreCandidatureIdCtrCandAndIndividuLoginInd(String codProfil, Integer idCtrCand, String loginInd);
	
	public List<DroitProfilInd> findByGestionnaireCentreCandidatureIdCtrCandAndIndividuLoginInd(Integer idCtrCand, String loginInd);	
	
	public List<DroitProfilInd> findByDroitProfilCodProfilAndIndividuLoginInd(String codDroitProfil, String login);

	public List<DroitProfilInd> findByIndividuLoginIndAndCommissionMembreIsNotNull(String username);
	
	public List<DroitProfilInd> findByCommissionMembreCommissionIdCommAndIndividuLoginInd(Integer idComm, String loginInd);

	public Long countByDroitProfil(DroitProfil droitProfil);
}
