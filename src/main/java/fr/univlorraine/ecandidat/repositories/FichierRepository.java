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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;

@Repository
public interface FichierRepository extends JpaRepository<Fichier, Integer> {

	List<Fichier> findByFileFichier(String fileFichier);
	
	List<Fichier> findByFileFichierAndIdFichierNot(String fileFichier, Integer idFichier);
	
	@Query("select f from Fichier f where "
			+ "not exists (select c from Commission c where f.idFichier = c.fichier.idFichier) "
			+ "and not exists (select p from PjCand p where f.idFichier = p.fichier.idFichier) "
			+ "and not exists (select pj from PieceJustif pj where f.idFichier = pj.fichier.idFichier) "
			+ "and f.datCreFichier<:dateCompare")
	List<Fichier> findFichierOrphelin(@Param("dateCompare") LocalDateTime dateCompare);
}
