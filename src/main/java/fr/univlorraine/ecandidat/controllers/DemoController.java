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
package fr.univlorraine.ecandidat.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import fr.univlorraine.ecandidat.entities.siscol.WSAdresse;
import fr.univlorraine.ecandidat.entities.siscol.WSBac;
import fr.univlorraine.ecandidat.entities.siscol.WSCursusInterne;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.services.file.FileManager;
import fr.univlorraine.ecandidat.services.ldap.PeopleLdap;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Gestion de la version de démo de l'application
 * @author Kevin Hergalant
 */
@Component
public class DemoController {

	private final Logger logger = LoggerFactory.getLogger(DemoController.class);

	/* Injections */
	@Resource
	private transient EntityManagerFactory entityManagerFactoryEcandidat;

	@Resource
	private transient FileManager fileManager;

	@Resource
	private transient NomenclatureController nomenclatureController;

	@Resource
	private transient DroitProfilController droitProfilController;

	@Value("${demoMode:}")
	private String demoMode;

	@Value("${file.filesystem.candidat.path:}")
	private String folderCandidat;

	@Value("${file.filesystem.gestionnaire.path:}")
	private String folderGestionnaire;

	private Boolean isDemoMode = null;

	/** @return true si on est en mode demo */
	public Boolean getDemoMode() {
		if (isDemoMode == null) {
			isDemoMode = Boolean.valueOf(demoMode);
			if (isDemoMode == null) {
				isDemoMode = false;
			}
		}
		return isDemoMode;
	}

	/**
	 * Le batch de démo
	 * @throws SiScolException
	 */
	public void launchDemoBatch() throws SiScolException {
		if (getDemoMode()) {
			logger.debug("Lancement du batch demo");
			cleanData();
			nomenclatureController.majNomenclature();
			cleanFolderFiles();
			addAdmins();
			populateData();
			logger.debug("Fin du batch demo");
		}
	}

	/** Lance le script pour effacer les données */
	@Transactional
	private void cleanData() {
		logger.debug("Demo : debut clean des datas");
		launchSqlScript("cleanData.sql");
		logger.debug("Demo : fin clean des datas");
	}

	/** Lance le script pour ajouter les données */
	@Transactional
	private void populateData() {
		logger.debug("Demo : debut clean des datas");
		launchSqlScript("populateData.sql");
		logger.debug("Demo : fin clean des datas");
	}

	/** Ajoute les administrateurs */
	private void addAdmins() {
		droitProfilController.addDroitProfilIndForAdmin("ziller5", "Olivier Ziller", "olivier.ziller@univ-lorraine.fr");
		droitProfilController.addDroitProfilIndForAdmin("champmar5", "Cedric Champmartin", "cedric.champmartin@univ-lorraine.fr");
		droitProfilController.addDroitProfilIndForAdmin("eCandidat", "eCandidat", "kevin.hergalant@univ-lorraine.fr");
		droitProfilController.addDroitProfilIndForAdmin("andre7", "Severine Klipfel", "severine.klipfel@univ-lorraine.fr");
	}

	/** Vide les repertoires */
	private void cleanFolderFiles() {
		if (fileManager.getType().equals(ConstanteUtils.TYPE_FICHIER_STOCK_FILE_SYSTEM)) {
			if (folderCandidat != null) {
				cleanFiles(folderCandidat);
			}
			if (folderGestionnaire != null) {
				cleanFiles(folderGestionnaire);
			}
		}
	}

	/** Vide les fichiers d'un repertoire */
	private void cleanFiles(final String folderPath) {
		if (folderPath == null) {
			return;
		}
		final File folder = new File(folderPath);
		if (!folder.isDirectory()) {
			return;
		}
		for (final File file : folder.listFiles()) {
			file.delete();
		}
	}

	/**
	 * Lance un script sql
	 * @param script
	 */
	@Transactional
	private void launchSqlScript(final String script) {
		final EntityManager em = entityManagerFactoryEcandidat.createEntityManager();
		em.getTransaction().begin();
		try {
			final InputStream inputStream = this.getClass().getResourceAsStream("/db/demo/" + script);
			final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			while (bufferedReader.ready()) {
				final Query query = em.createNativeQuery(bufferedReader.readLine());
				query.executeUpdate();
			}

		} catch (final Exception e) {
			em.getTransaction().rollback();
			em.close();
		}
		em.getTransaction().commit();
		em.close();
	}

	/** @return une liste de peopleLdap anonyme pour la recherche Ldap */
	public List<PeopleLdap> findListIndividuLdapDemo() {
		final List<PeopleLdap> liste = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			final String login = RandomStringUtils.randomAlphabetic(8).toLowerCase() + RandomStringUtils.randomNumeric(1);
			final PeopleLdap people = new PeopleLdap(login, "displayName-" + login, "sn-" + login, "cn-" + login, "mail-" + login, null, "M.", "givenName-" + login);
			liste.add(people);
		}
		return liste;
	}

	/**
	 * @param  ine
	 * @return     un individu Apogee anonyme
	 */
	public WSIndividu recupInfoEtudiant(final String ine) {
		WSIndividu ind = null;
		if (ine != null && ine.equals("0000000000") || ine.equals("1111111111")
			|| ine.equals("2222222222")
			|| ine.equals("3333333333")
			|| ine.equals("4444444444")
			||
			ine.equals("5555555555")
			|| ine.equals("6666666666")
			|| ine.equals("7777777777")
			|| ine.equals("8888888888")
			|| ine.equals("9999999999")) {
			final String cpt = ine.substring(0, 1);
			ind = new WSIndividu(1, "1", "057", new BigDecimal(ine), ine, "A", "D", LocalDate.of(1992, 2, 12), "NomPat-" + cpt, "NomUsu-" + cpt, "Prenom1-" + cpt, "Prenom2-" + cpt, "Metz", "100");
			ind.setAdresse(new WSAdresse("1", "57000", "57463", "100", "15 rue de Nancy", "Etage 1", "Porte droite", "0383542120", "0612356421"));
			ind.setBac(new WSBac(Long.valueOf(ine), "S", "057", "0573227Y", null, "2009"));
			final List<WSCursusInterne> listCursusInterne = new ArrayList<>();
			listCursusInterne.add(new WSCursusInterne("VET001-001", "License 1 - Droit", "2010", "AB", "1", "10", 1));
			listCursusInterne.add(new WSCursusInterne("VET001-002", "License 2 - Droit", "2011", "P", "1", "11", 1));
			listCursusInterne.add(new WSCursusInterne("VET001-003", "License 2 - Droit", "2012", "P", "1", "12", 1));
			listCursusInterne.add(new WSCursusInterne("VET001-004", "Master 1 - Droit", "2013", "B", "1", "13", 1));
			listCursusInterne.add(new WSCursusInterne("VET001-005", "Master 2 - Droit", "2014", "P", "1", "14", 1));
			ind.setListCursusInterne(listCursusInterne);
		}
		return ind;
	}
}
