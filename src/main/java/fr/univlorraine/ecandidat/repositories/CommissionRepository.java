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

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Integer> {
	
	Commission findByCodComm(String cod);
	
	List<Commission> findByCentreCandidatureIdCtrCandAndTesCommAndCentreCandidatureTesCtrCand(Integer idCtrCand, Boolean tesComm, Boolean tesCtrCand);
	
	List<Commission> findByCentreCandidatureIdCtrCandAndTesComm(Integer idCtrCand, Boolean tesComm);
	
	List<Commission> findByCentreCandidatureIdCtrCand(Integer idCtrCand);

	@Query("select c from Commission c where c.centreCandidature.idCtrCand = :idCtrCand and c.idComm in :listeIdCommission")
	List<Commission> findByCentreCandidatureIdCtrCandAndIdCommIn(@Param("idCtrCand") Integer idCtrCand, @Param("listeIdCommission") List<Integer> listeIdCommission);
	
	@Query("select c from Commission c where c.centreCandidature.idCtrCand = :idCtrCand and c.tesComm = :tesComm and c.idComm in :listeIdCommission")
	List<Commission> findByCentreCandidatureIdCtrCandAndTesCommAndIdCommIn(@Param("idCtrCand") Integer idCtrCand,@Param("tesComm") Boolean tesComm, @Param("listeIdCommission") List<Integer> listeIdCommission);

	List<Commission> findByTesCommAndCentreCandidatureTesCtrCand(Boolean tesComm, Boolean tesCtrCand);

	Commission findFirst1ByTesCommAndCentreCandidatureTesCtrCand(Boolean tesComm, Boolean tesCtrCand);
	
	public Long countByCentreCandidature(CentreCandidature centreCandidature);
	
	/* Onglet Stat */
	@Query("select co.idComm, count(1) from Candidature ca"
			+ " join ca.formation fo"
			+ " join fo.commission co"
			+ " join co.centreCandidature ce"
			+ " join ca.candidat cand"
			+ " join cand.compteMinima cpt"
			+ " where ce.idCtrCand = :idCtrCand and cpt.campagne.idCamp = :idcamp"
			+ " group by co.idComm")
	List<Object[]> findStatNbCandidature(@Param("idCtrCand") Integer idCtrCand, @Param("idcamp") Integer idcamp);
	
	@Query("select co.idComm, ca.typeStatut.codTypStatut, count(1) from Candidature ca"
			+ " join ca.formation fo"
			+ " join fo.commission co"
			+ " join co.centreCandidature ce"
			+ " join ca.candidat cand"
			+ " join cand.compteMinima cpt"
			+" where ce.idCtrCand = :idCtrCand and cpt.campagne.idCamp = :idcamp"
			+" group by co.idComm, ca.typeStatut.codTypStatut")
	List<Object[]> findStatNbCandidatureByStatut(@Param("idCtrCand") Integer idCtrCand, @Param("idcamp") Integer idcamp);
	
	@Query("select co.idComm, ca.temAcceptCand, count(1) from Candidature ca"
			+ " join ca.formation fo"
			+ " join fo.commission co"
			+ " join co.centreCandidature ce"
			+ " join ca.candidat cand"
			+ " join cand.compteMinima cpt"
			+" where ce.idCtrCand = :idCtrCand and cpt.campagne.idCamp = :idcamp"
			+" and ca.temAcceptCand is not null"
			+" group by co.idComm, ca.temAcceptCand")
	List<Object[]> findStatNbCandidatureByConfirm(@Param("idCtrCand") Integer idCtrCand, @Param("idcamp") Integer idcamp);
	
	@Query("select co.idComm, ty.codTypAvis, td.temValidTypeDecCand, count(1)"
			+ " from TypeDecisionCandidature td"
			+ " join td.candidature ca"
			+ " join ca.formation fo"
			+ " join fo.commission co"
			+ " join co.centreCandidature ce"
			+ " join ca.candidat cand"
			+ " join cand.compteMinima cpt"
			+ " join td.typeDecision t"
			+ " join t.typeAvis ty"
			+ " where ce.idCtrCand = :idCtrCand and cpt.campagne.idCamp = :idcamp"
			+ " and td.idTypeDecCand in (select max(td2.idTypeDecCand) from TypeDecisionCandidature td2 where ca.idCand = td2.candidature.idCand)"
			+ " group by co.idComm, ty.codTypAvis, td.temValidTypeDecCand")
	List<Object[]> findStatNbCandidatureByAvis(@Param("idCtrCand") Integer idCtrCand, @Param("idcamp") Integer idcamp);
}
