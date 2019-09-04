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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;

@Repository
public interface OpiRepository extends JpaRepository<Opi, Integer> {

	List<Opi> findByCandidatureCandidatIdCandidat(Integer idCandidat);

	@Query("select count(o) from Opi o where o.datPassageOpi is null and o.candidature.candidat.compteMinima.campagne=:campagne")
	Long getNbOpiToPass(@Param("campagne") Campagne campagne);

	@Query("select count(o) from Opi o where o.datPassageOpi is not null and o.candidature.candidat.compteMinima.campagne=:campagne")
	Long getNbOpiPassed(@Param("campagne") Campagne campagne);

	@Modifying
	@Query("update Opi o set o.datPassageOpi = null, o.codOpi = null where o.candidature.candidat.compteMinima.campagne=:campagne")
	void reloadAllOpi(@Param("campagne") Campagne campagne);

}
