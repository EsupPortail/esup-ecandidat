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

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;

@Repository
public interface PieceJustifRepository extends JpaRepository<PieceJustif, Integer> {
	
	public PieceJustif findByCodPj(String codPj);

	public List<PieceJustif> findByCentreCandidatureIdCtrCand(Integer idCtrCand);

	public List<PieceJustif> findByCentreCandidatureIdCtrCandAndTesPjAndTemCommunPj(Integer idCtrCand, Boolean enService, Boolean commun);
	
	public List<PieceJustif> findByTesPjAndTemCommunPj(Boolean enService, Boolean commun);
	
	public Long countByCentreCandidature(CentreCandidature centreCandidature);
	
}
