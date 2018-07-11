/** ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;

@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Integer> {

	List<Candidat> findByIneCandidatIgnoreCaseAndCleIneCandidatIgnoreCaseAndCompteMinimaCampagneCodCamp(String ineValue, String cleIneValue, String codCamp);

	@Query("select distinct c from Opi o " +
			"inner join o.candidature ca " +
			"inner join ca.candidat c " +
			"where c.compteMinima.campagne.idCamp = :idCamp and o.datPassageOpi is null order by o.datCreOpi")
	List<Candidat> findOpi(@Param("idCamp") Integer idCamp, Pageable pageable);
}
