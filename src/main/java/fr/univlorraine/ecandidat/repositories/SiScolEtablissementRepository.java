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

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissementPK;

@Repository
public interface SiScolEtablissementRepository extends JpaRepository<SiScolEtablissement, SiScolEtablissementPK> {

	@Query("select distinct etab from SiScolEtablissement etab where etab.id.typSiScol = :typSiScol and etab.siScolCommune.id.typSiScol = :typSiScol and etab.siScolCommune.id.codCom = :codCom and etab.temEnSveEtb = :temEnSveEtb order by etab.libEtb")
	List<SiScolEtablissement> getEtablissementByCommuneEnService(@Param("typSiScol") String typSiScol, @Param("codCom") String codCom, @Param("temEnSveEtb") Boolean temEnSveEtb);

}
