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

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;

@Repository
public interface CompteMinimaRepository extends JpaRepository<CompteMinima, Integer> {

	CompteMinima findByNumDossierOpiCptMin(String numDossier);

	List<CompteMinima> findByTemValidCptMinAndDatFinValidCptMinBefore(Boolean enService, LocalDateTime now);

	List<CompteMinima> findBySupannEtuIdCptMinAndIdCptMinNotAndCampagneCodCamp(String supannEtuId, Integer idCptMin,
			String codCamp);

	List<CompteMinima> findByLoginCptMinIgnoreCaseAndIdCptMinNotAndCampagneCodCamp(String login, Integer idCptMin,
			String codCamp);

	CompteMinima findByNumDossierOpiCptMinAndCampagneCodCamp(String numDossier, String codCamp);

	CompteMinima findByLoginCptMinIgnoreCaseAndCampagneCodCamp(String username, String codCamp);

	CompteMinima findByMailPersoCptMinIgnoreCaseAndCampagneCodCamp(String eMail, String codCamp);

	List<CompteMinima> findByLoginCptMinLikeIgnoreCaseOrNomCptMinLikeIgnoreCaseOrNumDossierOpiCptMinLikeIgnoreCaseOrSupannEtuIdCptMinLikeIgnoreCase(
			String login, String nom, String noDossier, String supannEtuId);

	@Query("select cpt from CompteMinima cpt left outer join cpt.candidat cand "
			+ "where cpt.campagne.codCamp=:codCamp " + "and (" + "LOWER(cpt.loginCptMin) like LOWER(:filter) "
			+ "or LOWER(cpt.nomCptMin) like LOWER(:filter) " + "or LOWER(cpt.numDossierOpiCptMin) like LOWER(:filter) "
			+ "or LOWER(cpt.supannEtuIdCptMin) like LOWER(:filter) "
			+ "or LOWER(cand.nomPatCandidat) like LOWER(:filter) " + ") order by cpt.nomCptMin")
	List<CompteMinima> findByFilter(@Param("codCamp") String codCamp, @Param("filter") String filter,
			Pageable pageable);

	@Query("select cpt from CompteMinima cpt left outer join cpt.candidat cand " + "where "
			+ "LOWER(cpt.loginCptMin) like LOWER(:filter) " + "or LOWER(cpt.nomCptMin) like LOWER(:filter) "
			+ "or LOWER(cpt.numDossierOpiCptMin) like LOWER(:filter) "
			+ "or LOWER(cpt.supannEtuIdCptMin) like LOWER(:filter) "
			+ "or LOWER(cand.nomPatCandidat) like LOWER(:filter) order by cpt.nomCptMin")
	List<CompteMinima> findByFilterAllYears(@Param("filter") String filter, Pageable pageable);

	public Long countByCampagne(Campagne campagne);
}
