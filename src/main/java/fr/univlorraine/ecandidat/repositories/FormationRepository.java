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

import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeFormation;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Integer> {

	List<Formation> findByTesFormAndTemListCompFormAndTypSiScol(Boolean tesForm, Boolean temListCompForm, String typSiScol);

	List<Formation> findByCommissionCentreCandidatureIdCtrCandAndTypSiScol(Integer idCtrCand, String typSiScol);

	List<Formation> findByCommissionCentreCandidatureIdCtrCandAndTesFormAndTypSiScol(Integer idCtrCand, Boolean tes, String typSiScol);

	Formation findByCodFormAndTypSiScol(String cod, String typSiScol);

	Long countByTypeDecisionFav(TypeDecision typeDecision);

	Long countByTypeDecisionFavListComp(TypeDecision typeDecision);

	Long countByCommission(Commission commission);

	Long countByTypeFormation(TypeFormation typeFormation);

	/* Nombre de candidatures */
	@Query("select count(1)" + " from TypeDecisionCandidature td"
		+ " join td.candidature ca"
		+ " join ca.formation fo"
		+ " join fo.commission co"
		+ " join co.centreCandidature ce"
		+ " join ca.candidat cand"
		+ " join cand.compteMinima cpt"
		+ " join td.typeDecision t"
		+ " join t.typeAvis ty"
		+ " where fo.idForm = :idForm and cpt.campagne.idCamp = :idcamp"
		+ " and fo.typSiScol = :typSiScol"
		+ " and ca.datAnnulCand is null"
		+ " and td.temValidTypeDecCand = 1 and td.idTypeDecCand = (select max(td2.idTypeDecCand) from TypeDecisionCandidature td2 where ca.idCand = td2.candidature.idCand)"
		+ " and ty.codTypAvis = :codTypAvis"
		+ " group by fo.idForm, ty.codTypAvis, td.temValidTypeDecCand")
	Long findNbCandidatureAvisFavorable(@Param("idForm") Integer idForm, @Param("idcamp") Integer idcamp, @Param("codTypAvis") String codTypAvis, @Param("typSiScol") String typSiScol);

	/* Onglet Stat */
	@Query("select fo.idForm, count(1) from Candidature ca" + " join ca.formation fo"
		+ " join fo.commission co"
		+ " join co.centreCandidature ce"
		+ " join ca.candidat cand"
		+ " join cand.compteMinima cpt"
		+ " where ce.idCtrCand = :idCtrCand and cpt.campagne.idCamp = :idcamp"
		+ " and fo.typSiScol = :typSiScol"
		+ " and ca.datAnnulCand is null"
		+ " group by fo.idForm")
	List<Object[]> findStatNbCandidature(@Param("idCtrCand") Integer idCtrCand, @Param("idcamp") Integer idcamp, @Param("typSiScol") String typSiScol);

	@Query("select fo.idForm, count(1) from Candidature ca" + " join ca.formation fo"
		+ " join fo.commission co"
		+ " join co.centreCandidature ce"
		+ " join ca.candidat cand"
		+ " join cand.compteMinima cpt"
		+ " where ce.idCtrCand = :idCtrCand and cpt.campagne.idCamp = :idcamp"
		+ " and ca.datAnnulCand is not null"
		+ " and fo.typSiScol = :typSiScol"
		+ " group by fo.idForm")
	List<Object[]> findStatNbCandidatureCancel(@Param("idCtrCand") Integer idCtrCand, @Param("idcamp") Integer idcamp, @Param("typSiScol") String typSiScol);

	@Query("select fo.idForm, ca.typeStatut.codTypStatut, count(1) from Candidature ca" + " join ca.formation fo"
		+ " join fo.commission co"
		+ " join co.centreCandidature ce"
		+ " join ca.candidat cand"
		+ " join cand.compteMinima cpt"
		+ " where ce.idCtrCand = :idCtrCand and cpt.campagne.idCamp = :idcamp"
		+ " and ca.datAnnulCand is null"
		+ " and fo.typSiScol = :typSiScol"
		+ " group by fo.idForm, ca.typeStatut.codTypStatut")
	List<Object[]> findStatNbCandidatureByStatut(@Param("idCtrCand") Integer idCtrCand,
		@Param("idcamp") Integer idcamp,
		@Param("typSiScol") String typSiScol);

	@Query("select fo.idForm, ca.temAcceptCand, count(1) from Candidature ca" + " join ca.formation fo"
		+ " join fo.commission co"
		+ " join co.centreCandidature ce"
		+ " join ca.candidat cand"
		+ " join cand.compteMinima cpt"
		+ " where ce.idCtrCand = :idCtrCand and cpt.campagne.idCamp = :idcamp"
		+ " and ca.datAnnulCand is null"
		+ " and fo.typSiScol = :typSiScol"
		+ " and ca.temAcceptCand is not null"
		+ " group by fo.idForm, ca.temAcceptCand")
	List<Object[]> findStatNbCandidatureByConfirm(@Param("idCtrCand") Integer idCtrCand,
		@Param("idcamp") Integer idcamp,
		@Param("typSiScol") String typSiScol);

	@Query("select fo.idForm, ty.codTypAvis, td.temValidTypeDecCand, count(1)" + " from TypeDecisionCandidature td"
		+ " join td.candidature ca"
		+ " join ca.formation fo"
		+ " join fo.commission co"
		+ " join co.centreCandidature ce"
		+ " join ca.candidat cand"
		+ " join cand.compteMinima cpt"
		+ " join td.typeDecision t"
		+ " join t.typeAvis ty"
		+ " where ce.idCtrCand = :idCtrCand and cpt.campagne.idCamp = :idcamp"
		+ " and ca.datAnnulCand is null"
		+ " and fo.typSiScol = :typSiScol"
		+ " and td.idTypeDecCand in (select max(td2.idTypeDecCand) from TypeDecisionCandidature td2 where ca.idCand = td2.candidature.idCand)"
		+ " group by fo.idForm, ty.codTypAvis, td.temValidTypeDecCand")
	List<Object[]> findStatNbCandidatureByAvis(@Param("idCtrCand") Integer idCtrCand, @Param("idcamp") Integer idcamp, @Param("typSiScol") String typSiScol);
}
