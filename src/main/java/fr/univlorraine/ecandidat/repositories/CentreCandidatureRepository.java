/**
 * ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package fr.univlorraine.ecandidat.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;

@Repository
public interface CentreCandidatureRepository extends JpaRepository<CentreCandidature, Integer> {
	public CentreCandidature findByCodCtrCand(String cod);

	public List<CentreCandidature> findByTesCtrCand(Boolean enService);

	public CentreCandidature findFirst1ByTesCtrCand(Boolean enService);

	public Long countByTypeDecisionFav(TypeDecision typeDecision);

	public Long countByTypeDecisionFavListComp(TypeDecision typeDecision);

	/* Onglet Stat */
	@Query("select ce.idCtrCand, count(1) from Candidature ca" + " join ca.formation fo" + " join fo.commission co"
			+ " join co.centreCandidature ce" + " join ca.candidat cand" + " join cand.compteMinima cpt"
			+ " where cpt.campagne.idCamp = :idcamp" + " and ca.datAnnulCand is null" + " group by ce.idCtrCand")
	List<Object[]> findStatNbCandidature(@Param("idcamp") Integer idcamp);

	@Query("select ce.idCtrCand, count(1) from Candidature ca" + " join ca.formation fo" + " join fo.commission co"
			+ " join co.centreCandidature ce" + " join ca.candidat cand" + " join cand.compteMinima cpt"
			+ " where cpt.campagne.idCamp = :idcamp" + " and ca.datAnnulCand is not null" + " group by ce.idCtrCand")
	List<Object[]> findStatNbCandidatureCancel(@Param("idcamp") Integer idcamp);

	@Query("select ce.idCtrCand, ca.typeStatut.codTypStatut, count(1) from Candidature ca" + " join ca.formation fo"
			+ " join fo.commission co" + " join co.centreCandidature ce" + " join ca.candidat cand"
			+ " join cand.compteMinima cpt" + " where cpt.campagne.idCamp = :idcamp" + " and ca.datAnnulCand is null"
			+ " group by ce.idCtrCand, ca.typeStatut.codTypStatut")
	List<Object[]> findStatNbCandidatureByStatut(@Param("idcamp") Integer idcamp);

	@Query("select ce.idCtrCand, ca.temAcceptCand, count(1) from Candidature ca" + " join ca.formation fo"
			+ " join fo.commission co" + " join co.centreCandidature ce" + " join ca.candidat cand"
			+ " join cand.compteMinima cpt" + " where cpt.campagne.idCamp = :idcamp" + " and ca.datAnnulCand is null"
			+ " and ca.temAcceptCand is not null" + " group by ce.idCtrCand, ca.temAcceptCand")
	List<Object[]> findStatNbCandidatureByConfirm(@Param("idcamp") Integer idcamp);

	@Query("select ce.idCtrCand, ty.codTypAvis, td.temValidTypeDecCand, count(1)" + " from TypeDecisionCandidature td"
			+ " join td.candidature ca" + " join ca.formation fo" + " join fo.commission co"
			+ " join co.centreCandidature ce" + " join ca.candidat cand" + " join cand.compteMinima cpt"
			+ " join td.typeDecision t" + " join t.typeAvis ty" + " where cpt.campagne.idCamp = :idcamp"
			+ " and ca.datAnnulCand is null"
			+ " and td.idTypeDecCand in (select max(td2.idTypeDecCand) from TypeDecisionCandidature td2 where ca.idCand = td2.candidature.idCand)"
			+ " group by ce.idCtrCand, ty.codTypAvis, td.temValidTypeDecCand")
	List<Object[]> findStatNbCandidatureByAvis(@Param("idcamp") Integer idcamp);
}
