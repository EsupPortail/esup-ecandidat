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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Integer> {

	List<Candidature> findByCandidatCompteMinimaCampagne(Campagne campagne);

	List<Candidature> findByFormationIdFormAndCandidatIdCandidatAndDatAnnulCandIsNull(Integer idForm,
			Integer idCandidat);

	List<Candidature> findByFormationIdFormAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNull(Integer idForm,
			String codCamp);

	List<Candidature> findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNull(
			Integer idComm, String codCamp);

	// obligé de passer par une query car le IN est buggé -->
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=349477
	@Query("select ca from Candidature ca"
			+ " where ca.datAnnulCand is null and ca.formation.commission.idComm = :idComm"
			+ " and ca.candidat.compteMinima.campagne.codCamp = :codCamp " + " and ca.typeStatut in :listeTypeStatut")
	List<Candidature> findByCommissionAndTypeStatut(@Param("idComm") Integer idComm, @Param("codCamp") String codCamp,
			@Param("listeTypeStatut") List<TypeStatut> listeTypeStatut);

	List<Candidature> findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNotNull(
			Integer idComm, String codCamp);

	List<Candidature> findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneDatArchivCampIsNotNull(
			Integer idComm);

	List<Candidature> findByFormationCommissionCentreCandidatureIdCtrCandAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNull(
			Integer idCtrCand, String codCamp);

	@Query("select count(c) from Candidature c where c.formation.commission.centreCandidature.idCtrCand=:idCtrCand and c.datAnnulCand is null and c.candidat.idCandidat=:idCandidat")
	Long getNbCandByCtrCand(@Param("idCtrCand") Integer idCtrCand, @Param("idCandidat") Integer idCandidat);

	@Query("select count(c) from Candidature c where c.datAnnulCand is null and c.candidat.idCandidat=:idCandidat")
	Long getNbCandByEtab(@Param("idCandidat") Integer idCandidat);

	/* Count pour les erreurs de delete */
	public Long countByTag(Tag tag);

	public Long countByFormation(Formation formation);
}
