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
package fr.univlorraine.ecandidat.services.siscol;

import java.io.InputStream;
import java.util.List;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.PjOpi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolComBdi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolUtilisateur;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;
import fr.univlorraine.ecandidat.entities.siscol.Vet;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.entities.siscol.WSPjInfo;
import gouv.education.apogee.commun.transverse.dto.opi.MAJEtatCivilDTO;

/** Interface d'acces aux données du SI Scol
 * @author Kevin Hergalant
 *
 */
public interface SiScolGenericService {
	
	/**
	 * @return true si on il s'agit de l'implémentation apogee
	 */
	default Boolean isImplementationApogee(){
		return false;		
	}
	
	/**
	 * @return la liste des BacOuxEqu
	 * @throws SiScolException 
	 */
	List<SiScolBacOuxEqu> getListSiScolBacOuxEqu() throws SiScolException;
	
	/**
	 * @return la liste des CentreGestion
	 */
	List<SiScolCentreGestion> getListSiScolCentreGestion() throws SiScolException;
	
	/**
	 * @return la liste des Commune
	 */
	List<SiScolCommune> getListSiScolCommune() throws SiScolException;
	
	/**
	 * @return la liste des Departements
	 */
	List<SiScolDepartement> getListSiScolDepartement() throws SiScolException;
	
	/**
	 * @return la liste des DipAutCur
	 */
	List<SiScolDipAutCur> getListSiScolDipAutCur() throws SiScolException;
	
	/**
	 * @return la liste des Etablissement
	 */
	List<SiScolEtablissement> getListSiScolEtablissement() throws SiScolException;
	
	/**
	 * @return la liste des Mention
	 */
	List<SiScolMention> getListSiScolMention() throws SiScolException;
	
	/**
	 * @return la liste des TypResultat
	 */
	List<SiScolTypResultat> getListSiScolTypResultat() throws SiScolException;
	
	/**
	 * @return la liste des MentionNivBac
	 */
	List<SiScolMentionNivBac> getListSiScolMentionNivBac() throws SiScolException;
	
	/**
	 * @return la liste des Pays
	 */
	List<SiScolPays> getListSiScolPays() throws SiScolException;
	
	/**
	 * @return la liste des TypDiplome
	 */
	List<SiScolTypDiplome> getListSiScolTypDiplome() throws SiScolException;
	
	/**
	 * @return la liste des Utilisateurs
	 */
	List<SiScolUtilisateur> getListSiScolUtilisateur() throws SiScolException;
	
	/**
	 * @return la liste des ComBdi
	 */
	List<SiScolComBdi> getListSiScolComBdi() throws SiScolException;
	
	/**
	 * @return la liste des AnneeUni
	 */
	List<SiScolAnneeUni> getListSiScolAnneeUni() throws SiScolException;
	
	/**
	 * @return la version du SI Scol
	 */
	Version getVersion() throws SiScolException;

	/** Renvoi la liste des formations apogée pour un utilisateur
	 * @param codCgeUser
	 * @param search
	 * @return la liste des formations
	 * @throws SiScolException
	 */
	default List<Vet> getListFormation(String codCgeUser, String search) throws SiScolException{
		return null;
	}

	/**
	 * @param codEtu
	 * @param ine
	 * @param cleIne
	 * @return un individu Apogee
	 * @throws SiScolException
	 */
	default WSIndividu getIndividu(String codEtu, String ine, String cleIne) throws SiScolException{
		return null;
	}
	
	/**
	 * Creation OPI par WS
	 */
	default void creerOpiViaWS(Candidat candidat){
	}
	
	/**
	 * Creation OPI PJ par WS
	 * @param is 
	 * @throws SiScolException 
	 */
	default void creerOpiPjViaWS(PjOpi opiPj, Fichier file, InputStream is) throws SiScolException{
	}
	
	/**
	 * @param candidat
	 * @return l'etat civil
	 */
	default MAJEtatCivilDTO getEtatCivil(Candidat candidat){
		return null;
	}
	
	/**
	 * Recupere une info de piece d'apogée
	 * @return l'info d'une PJ
	 * @throws SiScolException 
	 */
	default WSPjInfo getPjInfoFromApogee(String codAnu, String codEtu, String codPj) throws SiScolException{
		return null;
	}
	
	/**
	 * Recupere un fichier piece d'apogée
	 * @return le fichier de PJ
	 * @throws SiScolException 
	 */
	default InputStream getPjFichierFromApogee(String codAnu, String codEtu, String codPj) throws SiScolException{
		return null;
	}
}
