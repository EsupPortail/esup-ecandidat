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
package fr.univlorraine.ecandidat.utils;

import java.io.Serializable;
import java.util.List;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPro;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatStage;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.LockCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.PostIt;
import fr.univlorraine.ecandidat.utils.bean.presentation.FormulairePresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.PjPresentation;

/**
 * Class des listeners d'un candidat
 * @author Kevin Hergalant
 *
 */
public class ListenerUtils {

	/** Listener pour la mise a jour du candidat
	 * @author Kevin
	 *
	 */
	public interface InfoPersoListener {
		/** L'info perso a été modifié
		 * @param candidat
		 * @param langueChanged
		 */
		void infoPersoModified(Candidat candidat, Boolean langueChanged);
	}
	
	/** Listener pour la mise a jour de l'adresse
	 * @author Kevin
	 *
	 */
	public interface AdresseListener {
		/** L'adresse a été modifiée
		 * @param candidat
		 */
		void adresseModified(Candidat candidat);
	}
	
	/** Listener pour la mise a jour du cursus post bac
	 * @author Kevin
	 *
	 */
	public interface CandidatCursusExterneListener {
		/** Les cursus ont été modifié
		 * @param list
		 */
		void cursusModified(List<CandidatCursusPostBac> list);
	}

	/** Listener pour la mise a jour du parcours pro
	 * @author Kevin
	 *
	 */
	public interface CandidatProListener {
		/** Les cursus pro ont été modifié
		 * @param candidatCursusPros
		 */
		void cursusProModified(List<CandidatCursusPro> candidatCursusPros);
	}
	
	/** Listener pour la mise a jour du stage
	 * @author Kevin
	 *
	 */
	public interface CandidatStageListener {
		/** Les stages ont été modifié
		 * @param candidatStage
		 */
		void stageModified(List<CandidatStage> candidatStage);
	}
	
	/** Listener pour la mise a jour du bac
	 * @author Kevin
	 *
	 */
	public interface CandidatBacListener {
		/**Le bac a été modifié
		 * @param bac
		 */
		void bacModified(CandidatBacOuEqu bac);
	}
	
	/** Listener pour la mise a jour d'une formation pro
	 * @author Kevin
	 *
	 */
	public interface CandidatFormationProListener {
		/** Les formations pro ont été modifié
		 * @param candidatCursusPros
		 */
		void formationProModified(List<CandidatCursusPro> candidatCursusPros);
	}
	
	/**Listener pour la mise a jour d'une candidature
	 * @author Kevin Hergalant
	 *
	 */
	public interface CandidatureListener {
		/** Une pj a été modifié
		 * @param pieceJustif
		 * @param candidature
		 */
		void pjModified(PjPresentation pieceJustif, Candidature candidature);
		/** Un formulaire  a été modifié
		 * @param formulaire
		 * @param candidature
		 */
		void formulaireModified(FormulairePresentation formulaire, Candidature candidature);
		/** Candidature supprimée
		 * @param candidature
		 */
		void candidatureDeleted(Candidature candidature);
		/** Candidature annulée
		 * @param candidature
		 */
		void candidatureCanceled(Candidature candidature);
		/** Les pjs ont été modifiées
		 * @param listePj
		 * @param candidatureSave
		 */
		void pjsModified(List<PjPresentation> listePj, Candidature candidatureSave);
		
		/** Les pj sont en erreur
		 * @param listePj
		 */
		void reloadAllPiece(List<PjPresentation> listePj, Candidature candidatureLoad);
		
		/** Le statut a été modifié
		 * @param candidatureSave
		 */
		void infosCandidatureModified(Candidature candidatureSave);
		/**
		 * Le dossier candidat doit être ouvert
		 */
		void openCandidat();
		
		/**
		 * Un postIt a été ajouté
		 */
		void addPostIt(PostIt p);
		
		/** L'annulation a été annulée ;)
		 * @param candidatureSave
		 */
		void candidatureAnnulCanceled(Candidature candidatureSave);
		
		/** La candidature a été transmise
		 * @param candidatureSave
		 */
		void transmissionDossier(Candidature candidatureSave);
	}
	
	/**Listener pour la mise a jour d'une candidature
	 * @author Kevin Hergalant
	 *
	 */
	public interface CandidatureCandidatViewListener {
		/** Candidature annulée
		 * @param candidature
		 */
		void candidatureCanceled(Candidature candidature);
		
		/** Le statut du dossier a été modifié
		 * @param candidatureSave
		 */
		void statutDossierModified(Candidature candidatureSave);
	}
	
	/** Listener pour la mise a jour d'un candidat
	 * @author Kevin
	 *
	 */
	public interface CandidatAdminListener {
		/** Le compte a minima a été modifié
		 * @param cptMin
		 */
		void cptMinModified(CompteMinima cptMin);
	}
	
	/**
	 * Listener pour l'offre de formation
	 * @author Kevin Hergalant
	 *
	 */
	public interface OdfListener {
		/**
		 * L'offre de formation a été mdoifiée
		 */
		void updateOdf();
	}
	
	/**
	 * Interface pour le listener de changement de mode de maintenance
	 */
	public interface MaintenanceListener extends Serializable {

		/**
		 * Appelé lorsque le mode de maintenance est modifié
		 */
		public void changeModeMaintenance();

	}
	
	/**
	 * Interface pour le listener de changement de date SVA
	 */
	public interface DateSVAListener extends Serializable {

		/**
		 * Appelé lorsque le mode de date SVA est modifié
		 */
		public void changeModeParametreSVA();

	}
	
	/**
	 * Interface pour le listener de changement de gestionnaire Candidat 
	 */
	public interface GestionnaireCandidatListener extends Serializable {

		/**
		 * Appelé lorsque le mode de gestionnaire Candidat est modifié
		 */
		public void changeModeGestionnaireCandidat();

	}
	
	
	/**
	 * Interface pour le listener de lock Candidat 
	 */
	public interface LockCandidatListener extends Serializable {

		/**
		 * Appelé lorsque le lock candidat est supprimé
		 */
		public void lockCandidatDeleted(LockCandidat lock);

		/**
		 * Appelé lorsque les locks candidats ont été supprimés
		 */
		public void lockCandidatAllDeleted();

	}
}
