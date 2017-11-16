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
package fr.univlorraine.ecandidat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.univlorraine.ecandidat.entities.ecandidat.AlertSva;
import fr.univlorraine.ecandidat.entities.ecandidat.Batch;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Faq;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.Message;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Configuration Entity Push
 * 
 * @author Adrien Colson
 */
@Configuration
public class EntityPushConfig {

	@Bean
	public EntityPusher<Batch> batchEntityPusher() {
		return new EntityPusher<>(Batch.class);
	}
	
	@Bean
	public EntityPusher<Langue> langueEntityPusher() {
		return new EntityPusher<>(Langue.class);
	}
	
	@Bean
	public EntityPusher<Parametre> parametreEntityPusher() {
		return new EntityPusher<>(Parametre.class);
	}
	
	@Bean
	public EntityPusher<Mail> mailEntityPusher() {
		return new EntityPusher<>(Mail.class);
	}
	
	@Bean
	public EntityPusher<DroitProfilInd> droitProfilIndEntityPusher() {
		return new EntityPusher<>(DroitProfilInd.class);
	}
	
	@Bean
	public EntityPusher<DroitProfil> droitProfilEntityPusher() {
		return new EntityPusher<>(DroitProfil.class);
	}
	
	@Bean
	public EntityPusher<TypeDecision> typeDecisionEntityPusher() {
		return new EntityPusher<>(TypeDecision.class);
	}
	
	@Bean
	public EntityPusher<MotivationAvis> motivationAvisEntityPusher() {
		return new EntityPusher<>(MotivationAvis.class);
	}
	
	@Bean
	public EntityPusher<CentreCandidature> centreCandidatureEntityPusher() {
		return new EntityPusher<>(CentreCandidature.class);
	}
	
	@Bean
	public EntityPusher<Campagne> campagneEntityPusher() {
		return new EntityPusher<>(Campagne.class);
	}
	
	@Bean
	public EntityPusher<PieceJustif> pieceJustifEntityPusher() {
		return new EntityPusher<>(PieceJustif.class);
	}
	
	@Bean
	public EntityPusher<Formulaire> formulaireEntityPusher() {
		return new EntityPusher<>(Formulaire.class);
	}
	
	@Bean
	public EntityPusher<Commission> commissionEntityPusher() {
		return new EntityPusher<>(Commission.class);
	}
	
	@Bean
	public EntityPusher<Formation> formationEntityPusher() {
		return new EntityPusher<>(Formation.class);
	}
	
	@Bean
	public EntityPusher<Version> versionEntityPusher() {
		return new EntityPusher<>(Version.class);
	}
	
	@Bean
	public EntityPusher<TypeTraitement> typeTraitementEntityPusher() {
		return new EntityPusher<>(TypeTraitement.class);
	}
	
	@Bean
	public EntityPusher<TypeStatut> typeStatutEntityPusher() {
		return new EntityPusher<>(TypeStatut.class);
	}
	
	@Bean
	public EntityPusher<TypeStatutPiece> typeStatutPieceEntityPusher() {
		return new EntityPusher<>(TypeStatutPiece.class);
	}
	
	@Bean
	public EntityPusher<Faq> faqEntityPusher() {
		return new EntityPusher<>(Faq.class);
	}
	
	@Bean
	public EntityPusher<Candidature> candidatureEntityPusher() {
		return new EntityPusher<>(Candidature.class);
	}
	
	@Bean
	public EntityPusher<AlertSva> alertSvaEntityPusher() {
		return new EntityPusher<>(AlertSva.class);
	}
	
	@Bean
	public EntityPusher<Message> messageEntityPusher() {
		return new EntityPusher<>(Message.class);
	}
	
	@Bean
	public EntityPusher<Tag> tagEntityPusher() {
		return new EntityPusher<>(Tag.class);
	}	
}
