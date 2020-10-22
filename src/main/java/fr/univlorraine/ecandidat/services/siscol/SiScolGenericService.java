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

import fr.univlorraine.apowsclient.opi.MAJEtatCivilDTO2;
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
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.entities.siscol.WSPjInfo;
import fr.univlorraine.ecandidat.entities.siscol.apogee.Diplome;
import fr.univlorraine.ecandidat.entities.siscol.apogee.Vet;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Formation;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;

/**
 * Interface d'acces aux données du SI Scol
 * @author Kevin Hergalant
 */
public interface SiScolGenericService {

	/** @return true si on il s'agit de l'implémentation apogee */
	default Boolean isImplementationApogee() {
		return false;
	}

	/** @return true si on il s'agit de l'implémentation pegase */
	default Boolean isImplementationPegase() {
		return false;
	}

	/**
	 * @return le code de SIscol
	 */
	String getTypSiscol();

	/**
	 * @return le code pays de la france
	 */
	String getCodPaysFrance();

	/**
	 * @return                 la liste des BacOuxEqu
	 * @throws SiScolException
	 */
	List<SiScolBacOuxEqu> getListSiScolBacOuxEqu() throws SiScolException;

	/** @return la liste des CentreGestion */
	List<SiScolCentreGestion> getListSiScolCentreGestion() throws SiScolException;

	/** @return la liste des Commune */
	List<SiScolCommune> getListSiScolCommune() throws SiScolException;

	/** @return la liste des Departements */
	List<SiScolDepartement> getListSiScolDepartement() throws SiScolException;

	/** @return la liste des DipAutCur */
	List<SiScolDipAutCur> getListSiScolDipAutCur() throws SiScolException;

	/** @return la liste des Etablissement */
	List<SiScolEtablissement> getListSiScolEtablissement() throws SiScolException;

	/** @return la liste des Mention */
	List<SiScolMention> getListSiScolMention() throws SiScolException;

	/** @return la liste des TypResultat */
	List<SiScolTypResultat> getListSiScolTypResultat() throws SiScolException;

	/** @return la liste des MentionNivBac */
	List<SiScolMentionNivBac> getListSiScolMentionNivBac() throws SiScolException;

	/** @return la liste des Pays */
	List<SiScolPays> getListSiScolPays() throws SiScolException;

	/** @return la liste des TypDiplome */
	List<SiScolTypDiplome> getListSiScolTypDiplome() throws SiScolException;

	/** @return la liste des Utilisateurs */
	List<SiScolUtilisateur> getListSiScolUtilisateur() throws SiScolException;

	/** @return la liste des ComBdi */
	List<SiScolComBdi> getListSiScolComBdi() throws SiScolException;

	/** @return la liste des AnneeUni */
	List<SiScolAnneeUni> getListSiScolAnneeUni() throws SiScolException;

	/** @return la liste des CatExoExt */
	//List<SiScolCatExoExt> getListCatExoExt() throws SiScolException;

	/** @return la version du SI Scol */
	Version getVersion() throws SiScolException;

	/**
	 * Renvoi la liste des formations apogée pour un utilisateur
	 * @param  codCgeUser
	 * @param  search
	 * @return                 la liste des formations
	 * @throws SiScolException
	 */
	default List<Vet> getListFormationApogee(final String codCgeUser, final String search) throws SiScolException {
		return null;
	}

	/**
	 * Renvoi la liste des formations pegase pour un utilisateur
	 * @param  codCgeUser
	 * @param  search
	 * @return                 la liste des formations
	 * @throws SiScolException
	 */
	default List<Formation> getListFormationPegase(final String search) throws SiScolException {
		return null;
	}

	/**
	 * Renvoi la liste des diplomes apogée pour une VET
	 * @param  codEtpVet
	 * @param  codVrsVet
	 * @return                 la liste des diplomes
	 * @throws SiScolException
	 */
	default List<Diplome> getListDiplome(final String codEtpVet, final String codVrsVet) throws SiScolException {
		return null;
	}

	/**
	 * @param  codEtu
	 * @param  ine
	 * @param  cleIne
	 * @return                 un individu Apogee
	 * @throws SiScolException
	 */
	default WSIndividu getIndividu(final String codEtu, final String ine, final String cleIne) throws SiScolException {
		return null;
	}

	/** Creation OPI par WS */
	default void creerOpiViaWS(final Candidat candidat, final Boolean isBatch) {
	}

	/**
	 * Creation OPI PJ par WS
	 * @param  is
	 * @throws SiScolException
	 */
	default void creerOpiPjViaWS(final PjOpi opiPj, final Fichier file, final InputStream is) throws SiScolException {
	}

	/**
	 * @param  candidat
	 * @return          l'etat civil
	 */
	default MAJEtatCivilDTO2 getEtatCivil(final Candidat candidat) {
		return null;
	}

	/**
	 * Recupere une info de piece d'apogée
	 * @return                 l'info d'une PJ
	 * @throws SiScolException
	 */
	default WSPjInfo getPjInfoFromApogee(final String codAnu, final String codEtu, final String codPj) throws SiScolException {
		return null;
	}

	/**
	 * Recupere un fichier piece d'apogée
	 * @return                 le fichier de PJ
	 * @throws SiScolException
	 */
	default InputStream getPjFichierFromApogee(final String codAnu, final String codEtu, final String codPj) throws SiScolException {
		return null;
	}

	/**
	 * @return                 true si l'INES est ok
	 * @throws SiScolException
	 */
	default Boolean checkStudentINES(final String ine, final String cle) throws SiScolException {
		return true;
	}

	/**
	 * @param  codIndOpi
	 * @param  codTpj
	 * @throws SiScolException
	 */
	default void deleteOpiPJ(final String codIndOpi, final String codTpj) throws SiScolException {
	}

	/**
	 * @return la version du WS checkine
	 */
	default String getVersionWSCheckIne() {
		return NomenclatureUtils.VERSION_NO_VERSION_VAL;
	}

	/**
	 * @return true si l'etudiant se synchronise avec le siscol
	 */
	default Boolean hasSyncEtudiant() {
		return false;
	}

	/**
	 * @return true si les PJ d'un etudiant se synchronisent avec le siscol
	 */
	default Boolean hasSyncEtudiantPJ() {
		return false;
	}

	/**
	 * @return true si on peut parametrer l'année universitaire
	 */
	default Boolean hasSearchAnneeUni() {
		return false;
	}

	/**
	 * @return true si on doit saisir le département de naissance
	 */
	default Boolean hasDepartementNaissance() {
		return true;
	}

}
