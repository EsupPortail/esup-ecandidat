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

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;

@Repository
public interface DroitProfilIndRepository extends JpaRepository<DroitProfilInd, Integer> {
	List<DroitProfilInd> findByIndividuLoginInd(String login);

	List<DroitProfilInd> findByIndividuLoginIndAndDroitProfilCodProfil(String login, String codProfil);

	List<DroitProfilInd> findByIndividuLoginIndAndDroitProfilTypProfil(String login, String typProfil);

	List<DroitProfilInd> findByDroitProfilTypProfil(String typProfil);

	List<DroitProfilInd> findByDroitProfilCodProfil(String droitProfilScolCentrale);

	List<DroitProfilInd> findByDroitProfilCodProfilAndGestionnaireCentreCandidatureIdCtrCandAndIndividuLoginInd(String codProfil, Integer idCtrCand, String loginInd);

	List<DroitProfilInd> findByGestionnaireCentreCandidatureIdCtrCandAndIndividuLoginInd(Integer idCtrCand, String loginInd);

	List<DroitProfilInd> findByDroitProfilCodProfilAndIndividuLoginInd(String codDroitProfil, String login);

	List<DroitProfilInd> findByIndividuLoginIndAndCommissionMembreIsNotNull(String username);

	List<DroitProfilInd> findByCommissionMembreCommissionIdCommAndIndividuLoginInd(Integer idComm, String loginInd);

	Long countByDroitProfil(DroitProfil droitProfil);

	@Query("select droit from DroitProfilInd droit inner join droit.individu ind " + "where "
		+ "LOWER(ind.loginInd) like LOWER(:filter) "
		+ "or LOWER(ind.libelleInd) like LOWER(:filter) "
		+ "order by ind.libelleInd")
	List<DroitProfilInd> findByFilter(@Param("filter") String filter, Pageable pageable);
}
