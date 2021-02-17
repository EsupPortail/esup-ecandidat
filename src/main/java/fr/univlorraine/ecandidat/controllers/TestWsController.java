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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;
import fr.univlorraine.ecandidat.entities.ecandidat.PjOpi;
import fr.univlorraine.ecandidat.entities.ecandidat.PjOpiPK;
import fr.univlorraine.ecandidat.entities.siscol.IndOpi;
import fr.univlorraine.ecandidat.entities.siscol.WSAdresse;
import fr.univlorraine.ecandidat.entities.siscol.WSBac;
import fr.univlorraine.ecandidat.entities.siscol.WSCursusInterne;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.entities.siscol.WSPjInfo;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.OpiRepository;
import fr.univlorraine.ecandidat.repositories.PjOpiRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 */
@Component
public class TestWsController {
	private final Logger logger = LoggerFactory.getLogger(TestWsController.class);

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	@Resource
	private transient CandidatureRepository candidatureRepository;

	@Resource
	private transient OpiRepository opiRepository;

	@Resource
	private transient PjOpiRepository pjOpiRepository;

	private Session cmisSession;

	@SuppressWarnings("unchecked")
	public void testWs() throws IOException {
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
		final EntityManager em = emf.createEntityManager();
		final ResourceBundle bundle = ResourceBundle.getBundle("test-ws");
		final String codOpi = bundle.getString("opi.codOpi");
		try {
			logger.info("********** Vérifications OPI **********");
			final Candidature candOpi = candidatureRepository.findOne(Integer.valueOf(bundle.getString("opi.idCand")));
			if (countOpiData(em, "IND_OPI", codOpi) > 0) {
				throw new RuntimeException("Impossible de lancer les tests, nettoyez d'abord les OPI");
			}
			logger.info("********** Vérifications OPI terminée, lancement des tests **********");

			/* Checkine */
			logger.info("********** Vérifications Checkine **********");
			final Boolean isInes = siScolService.checkStudentINES(bundle.getString("checkine.ine"), bundle.getString("checkine.key"));
			if (!isInes) {
				throw new RuntimeException("Checkines ne fonctionne pas");
			} else {
				logger.info("Ok - " + bundle.getString("checkine.ine") + bundle.getString("checkine.key"));
			}

			/* Données individu */
			logger.info("********** Test Données individu **********");
			final String codEtu = bundle.getString("ind.codEtu");
			final WSIndividu ind = siScolService.getIndividu(codEtu, null, null);
			checkString(bundle, String.valueOf(ind.getCodEtu()), "ind.codEtu");
			checkString(bundle, String.valueOf(ind.getCodInd()), "ind.codInd");
			checkString(bundle, ind.getCodNneInd(), "ind.codNneInd");
			checkString(bundle, ind.getCodCleNneInd(), "ind.codCleNneInd");
			checkString(bundle, ind.getCodPayNai(), "ind.codPayNai");
			checkString(bundle, ind.getCodDepNai(), "ind.codDepNai");
			checkString(bundle, ind.getCodPayNat(), "ind.codPayNat");
			checkString(bundle, ind.getLibNomPatInd(), "ind.libNomPatInd");
			checkString(bundle, ind.getLibNomUsuInd(), "ind.libNomUsuInd");
			checkString(bundle, ind.getLibPr1Ind(), "ind.libPr1Ind");
			checkString(bundle, ind.getLibPr2Ind(), "ind.libPr2Ind");
			checkString(bundle, ind.getLibVilNaiEtu(), "ind.libVilNaiEtu");

			/* Données bac */
			logger.info("********** Test Données bac **********");
			final WSBac bac = ind.getBac();
			checkString(bundle, bac.getCodBac(), "bac.codBac");
			checkString(bundle, bac.getCodDep(), "bac.codDep");
			checkString(bundle, bac.getCodEtb(), "bac.codEtb");
			checkString(bundle, bac.getCodMnb(), "bac.codMnb");
			checkString(bundle, bac.getDaaObtBacIba(), "bac.daaObtBacIba");
			checkString(bundle, bac.getTemInsAdm(), "bac.temInsAdm");

			/* Données Adresse */
			logger.info("********** Test Données adresse **********");
			final WSAdresse adr = ind.getAdresse();
			checkString(bundle, adr.getCodBdi(), "adr.codBdi");
			checkString(bundle, adr.getCodCom(), "adr.codCom");
			checkString(bundle, adr.getCodPay(), "adr.codPay");
			checkString(bundle, adr.getLibAd1(), "adr.libAd1");
			checkString(bundle, adr.getLibAd2(), "adr.libAd2");
			checkString(bundle, adr.getLibAd3(), "adr.libAd3");
			checkString(bundle, adr.getLibAde(), "adr.libAde");
			checkString(bundle, adr.getNumTel(), "adr.numTel");
			checkString(bundle, adr.getNumTelPort(), "adr.numTelPort");

			/* Données Cursus (test de la taille de liste et de la premiere inscription) */
			logger.info("********** Test Données Cursus interne **********");
			final List<WSCursusInterne> listCursus = ind.getListCursusInterne();
			checkString(bundle, String.valueOf(listCursus.size()), "cursus.size");
			final WSCursusInterne cursus = listCursus.get(0);
			checkString(bundle, cursus.getCodVet(), "cursus.codVet");
			checkString(bundle, cursus.getLibVet(), "cursus.libVet");
			checkString(bundle, cursus.getCodAnu(), "cursus.codAnu");
			checkString(bundle, cursus.getCodMen(), "cursus.codMen");
			checkString(bundle, cursus.getCodTre(), "cursus.codTre");
			checkString(bundle, cursus.getNotVet(), "cursus.notVet");
			checkString(bundle, String.valueOf(cursus.getBarNotVet()), "cursus.barNotVet");

			/* Données PJ */
			logger.info("********** Test Données PJ **********");
			final WSPjInfo pjInfo = siScolService.getPjInfoFromApogee(bundle.getString("pj.codAnu"), bundle.getString("pj.codEtu"), bundle.getString("pj.codTpj"));
			checkString(bundle, pjInfo.getCodAnu(), "pj.codAnu");
			checkString(bundle, pjInfo.getCodTpj(), "pj.codTpj");
			checkString(bundle, pjInfo.getLibTpj(), "pj.libTpj");
			checkString(bundle, pjInfo.getNomFic(), "pj.nomFic");
			checkString(bundle, String.valueOf(pjInfo.getTemDemPJ()), "pj.temDemPJ");
			checkString(bundle, pjInfo.getStuPj(), "pj.stuPj");
			checkString(bundle, pjInfo.getMtfRefus(), "pj.mtfRefus");
			checkString(bundle, pjInfo.getCmtMtfRefus(), "pj.cmtMtfRefus");
			checkString(bundle, pjInfo.getDatDemPj(), "pj.datDemPj");
			checkString(bundle, pjInfo.getDatRecPj(), "pj.datRecPj");
			checkString(bundle, pjInfo.getDatRefus(), "pj.datRefus");
			checkString(bundle, pjInfo.getDatVal(), "pj.datVal");
			checkString(bundle, pjInfo.getDatExp(), "pj.datExp");
			checkString(bundle, pjInfo.getDaaPreTra(), "pj.daaPreTra");

			/* Données PJ */
			logger.info("********** Test Fichier PJ **********");
			final InputStream pjFichier = siScolService.getPjFichierFromApogee(bundle.getString("pj.codAnu"), bundle.getString("pj.codEtu"), bundle.getString("pj.codTpj"));
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final byte[] bytes = new byte[1024];
			int count;
			while ((count = pjFichier.read(bytes)) > 0) {
				out.write(bytes, 0, count);
			}
			checkString(bundle, String.valueOf(out.size()), "filepj.size");

			logger.info("********** Test OPI **********");
			final Opi opi = opiRepository.findOne(candOpi.getIdCand());
			opi.setDatPassageOpi(null);
			opi.setCodOpi(null);
			opiRepository.save(opi);

			final PjOpiPK pk = new PjOpiPK(bundle.getString("opi.codOpi"), bundle.getString("opi.codTpj"));
			final PjOpi pj = pjOpiRepository.findOne(pk);
			pj.setDatDeversement(null);
			pjOpiRepository.save(pj);

			siScolService.creerOpiViaWS(candOpi.getCandidat(), true);

			logger.info("********** Vérification OPI **********");
			checkOpiData(em, "IND_OPI", codOpi);
			checkOpiData(em, "OPI_BAC", codOpi);
			checkOpiData(em, "VOEUX_INS", codOpi);
			checkOpiData(em, "ADRESSE_OPI", codOpi);
			checkOpiData(em, "OPI_PJ", codOpi);

			logger.info("********** Vérification OPI PJ **********");
			final String requete = "Select a from IndOpi a where a.codOpiIntEpo='" + codOpi + "'";
			final Query query = em.createQuery(requete, IndOpi.class);
			final List<IndOpi> lindopi = query.getResultList();
			final IndOpi indOpi = lindopi.get(0);

			final Session cmisSession = getCmisSession(bundle);
			final Folder folder = (Folder) cmisSession.getObject(cmisSession.createObjectId(bundle.getString("opi.pj.candidatureId")));
			final String pathDoc = folder.getPath() + "/" + indOpi.getCodIndOpi() + "_OPI/PJ_" + bundle.getString("pj.codTpj") + "_" + indOpi.getCodIndOpi() + bundle.getString("opi.pj.ext");
			logger.info("Recherche par path : " + pathDoc);

			final Document d = (Document) cmisSession.getObjectByPath(pathDoc);
			checkString(bundle, String.valueOf(d.getContentStreamLength()), "opi.pj.size");

		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			em.close();
		}
	}

	/**
	 * Verifie le nombre d'une ligne d'une requete OPI
	 * @param em
	 * @param table
	 * @param codOpi
	 */
	private void checkOpiData(final EntityManager em, final String table, final String codOpi) {
		final Integer count = countOpiData(em, "IND_OPI", codOpi);
		final String log = "table = " + table + " - codOpi = " + codOpi + " - Nombre de lignes trouvées = " + count;
		if (count.equals(1)) {
			logger.info("Ok - " + log);
			return;
		}
		throw new RuntimeException("Erreur vérif OPI - " + log);
	}

	/**
	 * @param  em
	 * @param  table
	 * @param  codOpi
	 * @return        le nombre de lignes d'une table OPI
	 */
	private Integer countOpiData(final EntityManager em, final String table, final String codOpi) {
		final String requete = "select count(*) from APOGEE." + table + " where COD_IND_OPI = (select COD_IND_OPI from APOGEE.IND_OPI where COD_OPI_INT_EPO = '" + codOpi + "')";
		final Query query = em.createNativeQuery(requete);
		final int count = ((Number) query.getSingleResult()).intValue();
		return count;
	}

	/**
	 * Verifie le retour du WS avec le fichier properties
	 * @param bundle
	 * @param str
	 * @param codBundle
	 */
	private void checkString(final ResourceBundle bundle, final String str, final String codBundle) {
		final String value = bundle.getString(codBundle);
		final String log = "codeBundle = " + codBundle + ", valBundle = " + value + ", valeur = " + str;

		if (value.equals("null") && str == null) {
			logger.info("Ok - " + log);
			return;
		}

		if (Objects.equals(str, value)) {
			logger.info("Ok - " + log);
			return;
		}

		throw new RuntimeException("Erreur - " + log);
	}

	/** @return la session CMIS */
	public Session getCmisSession(final ResourceBundle bundle) {
		if (cmisSession == null) {
			cmisSession = cmisSession(bundle);
		}
		return cmisSession;
	}

	/** @return la session CMIS */
	private Session cmisSession(final ResourceBundle bundle) {
		try {
			// default factory implementation
			final SessionFactory factory = SessionFactoryImpl.newInstance();
			final Map<String, String> parameter = new HashMap<>();

			// user credentials
			parameter.put(SessionParameter.USER, bundle.getString("opi.pj.user"));
			parameter.put(SessionParameter.PASSWORD, bundle.getString("opi.pj.pwd"));

			// connection settings
			parameter.put(SessionParameter.ATOMPUB_URL, bundle.getString("opi.pj.url"));
			parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			parameter.put(SessionParameter.REPOSITORY_ID, bundle.getString("opi.pj.repository"));
			// create session
			return factory.createSession(parameter);
		} catch (final Exception e) {
			logger.error("Stockage de fichier - Impossible de se connecter au serveur de fichier CMIS", e);
			return null;
		}
	}
}
