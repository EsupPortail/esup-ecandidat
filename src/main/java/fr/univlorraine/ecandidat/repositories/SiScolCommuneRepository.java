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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommunePK;

@Repository
public interface SiScolCommuneRepository extends JpaRepository<SiScolCommune, SiScolCommunePK> {

	@Query("select distinct c from SiScolCommune c, SiScolComBdi b where c.id.typSiScol = :typSiScol and c.id.codCom = b.id.codCom and b.id.codBdi=:codePostal  and b.id.typSiScol=:typSiScol")
	List<SiScolCommune> getCommuneByCodePostal(@Param("typSiScol") String typSiScol, @Param("codePostal") String codePostal);

	@Query("select distinct c from SiScolCommune c, SiScolEtablissement etab where etab.siScolDepartement.id.typSiScol = :typSiScol and etab.siScolDepartement.id.codDep = :codDep and etab.siScolCommune.id.typSiScol = :typSiScol and etab.siScolCommune.id.codCom = c.id.codCom ")
	List<SiScolCommune> getCommuneByDepartement(@Param("typSiScol") String typSiScol, @Param("codDep") String codDep);
}
