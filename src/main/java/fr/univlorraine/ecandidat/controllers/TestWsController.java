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
import fr.univlorraine.ecandidat.entities.siscol.WSAdresse;
import fr.univlorraine.ecandidat.entities.siscol.WSBac;
import fr.univlorraine.ecandidat.entities.siscol.WSCursusInterne;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.entities.siscol.WSPjInfo;
import fr.univlorraine.ecandidat.entities.siscol.apogee.IndOpi;
import fr.univlorraine.ecandidat.entities.siscol.pegase.FormationPegase;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.OpiRepository;
import fr.univlorraine.ecandidat.repositories.PjOpiRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.MethodUtils;

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
		logger.info("********** Début des Tests des Webservices Apogee **********");

		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
		final EntityManager em = emf.createEntityManager();
		final ResourceBundle bundle = ResourceBundle.getBundle("test-ws");
		final String codOpi = bundle.getString("apogee.opi.codOpi");
		try {
			logger.info("********** Vérifications OPI " + codOpi + " **********");
			final Candidature candOpi = candidatureRepository.findOne(Integer.valueOf(bundle.getString("apogee.opi.idCand")));
			if (countOpiData(em, "IND_OPI", codOpi) > 0) {
				throw new RuntimeException("Impossible de lancer les tests, nettoyez d'abord les OPI");
			}
			logger.info("********** Vérifications OPI terminée, lancement des tests **********");

			/* Checkine */
			logger.info("********** Vérifications Checkine **********");
			final Boolean isInes = siScolService.checkStudentINES(bundle.getString("apogee.checkine.ine"), bundle.getString("apogee.checkine.key"));
			if (!isInes) {
				throw new RuntimeException("Checkines ne fonctionne pas");
			} else {
				logger.info("Ok - " + bundle.getString("apogee.checkine.ine") + bundle.getString("apogee.checkine.key"));
			}

			/* Données individu */
			logger.info("********** Test Données individu **********");
			final String codEtu = bundle.getString("apogee.ind.codEtu");
			final WSIndividu ind = siScolService.getIndividu(codEtu, null, null);
			checkString(bundle, String.valueOf(ind.getCodEtu()), "apogee.ind.codEtu");
			checkString(bundle, String.valueOf(ind.getCodInd()), "apogee.ind.codInd");
			checkString(bundle, ind.getCodNneInd(), "apogee.ind.codNneInd");
			checkString(bundle, ind.getCodCleNneInd(), "apogee.ind.codCleNneInd");
			checkString(bundle, ind.getCodPayNai(), "apogee.ind.codPayNai");
			checkString(bundle, ind.getCodDepNai(), "apogee.ind.codDepNai");
			checkString(bundle, ind.getCodPayNat(), "apogee.ind.codPayNat");
			checkString(bundle, ind.getLibNomPatInd(), "apogee.ind.libNomPatInd");
			checkString(bundle, ind.getLibNomUsuInd(), "apogee.ind.libNomUsuInd");
			checkString(bundle, ind.getLibPr1Ind(), "apogee.ind.libPr1Ind");
			checkString(bundle, ind.getLibPr2Ind(), "apogee.ind.libPr2Ind");
			checkString(bundle, ind.getLibVilNaiEtu(), "apogee.ind.libVilNaiEtu");

			/* Données bac */
			logger.info("********** Test Données bac **********");
			final WSBac bac = ind.getBac();
			checkString(bundle, bac.getCodBac(), "apogee.bac.codBac");
			checkString(bundle, bac.getCodDep(), "apogee.bac.codDep");
			checkString(bundle, bac.getCodEtb(), "apogee.bac.codEtb");
			checkString(bundle, bac.getCodMnb(), "apogee.bac.codMnb");
			checkString(bundle, bac.getDaaObtBacIba(), "apogee.bac.daaObtBacIba");
			checkString(bundle, bac.getTemInsAdm(), "apogee.bac.temInsAdm");
			checkString(bundle, bac.getCodSpeBacPre(), "apogee.bac.codSpeBacPre");
			checkString(bundle, bac.getCodSpe1Bac(), "apogee.bac.codSpe1Bac");
			checkString(bundle, bac.getCodSpe2Bac(), "apogee.bac.codSpe2Bac");
			checkString(bundle, bac.getCodOpt1Bac(), "apogee.bac.codOpt1Bac");
			checkString(bundle, bac.getCodOpt2Bac(), "apogee.bac.codOpt2Bac");
			checkString(bundle, bac.getCodOpt3Bac(), "apogee.bac.codOpt3Bac");
			checkString(bundle, bac.getCodOpt4Bac(), "apogee.bac.codOpt4Bac");

			/* Données Adresse */
			logger.info("********** Test Données adresse **********");
			final WSAdresse adr = ind.getAdresse();
			checkString(bundle, adr.getCodBdi(), "apogee.adr.codBdi");
			checkString(bundle, adr.getCodCom(), "apogee.adr.codCom");
			checkString(bundle, adr.getCodPay(), "apogee.adr.codPay");
			checkString(bundle, adr.getLibAd1(), "apogee.adr.libAd1");
			checkString(bundle, adr.getLibAd2(), "apogee.adr.libAd2");
			checkString(bundle, adr.getLibAd3(), "apogee.adr.libAd3");
			checkString(bundle, adr.getLibAde(), "apogee.adr.libAde");
			checkString(bundle, adr.getNumTel(), "apogee.adr.numTel");
			checkString(bundle, adr.getNumTelPort(), "apogee.adr.numTelPort");

			/* Données Cursus (test de la taille de liste et de la premiere inscription) */
			logger.info("********** Test Données Cursus interne **********");
			final List<WSCursusInterne> listCursus = ind.getListCursusInterne();
			checkString(bundle, String.valueOf(listCursus.size()), "apogee.cursus.size");
			final WSCursusInterne cursus = listCursus.get(0);
			checkString(bundle, cursus.getCodVet(), "apogee.cursus.codVet");
			checkString(bundle, cursus.getLibVet(), "apogee.cursus.libVet");
			checkString(bundle, cursus.getCodAnu(), "apogee.cursus.codAnu");
			checkString(bundle, cursus.getCodMen(), "apogee.cursus.codMen");
			checkString(bundle, cursus.getCodTre(), "apogee.cursus.codTre");
			checkString(bundle, cursus.getNotVet(), "apogee.cursus.notVet");
			checkString(bundle, String.valueOf(cursus.getBarNotVet()), "apogee.cursus.barNotVet");

			/* Données PJ */
			logger.info("********** Test Données PJ **********");
			final WSPjInfo pjInfo = siScolService.getPjInfoFromApogee(bundle.getString("apogee.pj.codAnu"), bundle.getString("apogee.pj.codEtu"), bundle.getString("apogee.pj.codTpj"));
			checkString(bundle, pjInfo.getCodAnu(), "apogee.pj.codAnu");
			checkString(bundle, pjInfo.getCodTpj(), "apogee.pj.codTpj");
			checkString(bundle, pjInfo.getLibTpj(), "apogee.pj.libTpj");
			checkString(bundle, pjInfo.getNomFic(), "apogee.pj.nomFic");
			checkString(bundle, String.valueOf(pjInfo.getTemDemPJ()), "apogee.pj.temDemPJ");
			checkString(bundle, pjInfo.getStuPj(), "apogee.pj.stuPj");
			checkString(bundle, pjInfo.getMtfRefus(), "apogee.pj.mtfRefus");
			checkString(bundle, pjInfo.getCmtMtfRefus(), "apogee.pj.cmtMtfRefus");
			checkString(bundle, pjInfo.getDatDemPj(), "apogee.pj.datDemPj");
			checkString(bundle, pjInfo.getDatRecPj(), "apogee.pj.datRecPj");
			checkString(bundle, pjInfo.getDatRefus(), "apogee.pj.datRefus");
			checkString(bundle, pjInfo.getDatVal(), "apogee.pj.datVal");
			checkString(bundle, pjInfo.getDatExp(), "apogee.pj.datExp");
			checkString(bundle, pjInfo.getDaaPreTra(), "apogee.pj.daaPreTra");

			/* Données PJ */
			logger.info("********** Test Fichier PJ **********");
			final InputStream pjFichier = siScolService.getPjFichierFromApogee(bundle.getString("apogee.pj.codAnu"), bundle.getString("apogee.pj.codEtu"), bundle.getString("apogee.pj.codTpj"));
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			final byte[] bytes = new byte[1024];
			int count;
			while ((count = pjFichier.read(bytes)) > 0) {
				out.write(bytes, 0, count);
			}
			checkString(bundle, String.valueOf(out.size()), "apogee.filepj.size");

			logger.info("********** Test OPI **********");
			final Opi opi = opiRepository.findOne(candOpi.getIdCand());
			opi.setDatPassageOpi(null);
			opi.setCodOpi(null);
			opiRepository.save(opi);

			final PjOpiPK pk = new PjOpiPK(bundle.getString("apogee.opi.codOpi"), bundle.getString("apogee.opi.codTpj"));
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
			final Folder folder = (Folder) cmisSession.getObject(cmisSession.createObjectId(bundle.getString("apogee.opi.pj.candidatureId")));
			final String pathDoc = folder.getPath() + "/" + indOpi.getCodIndOpi() + "_OPI/PJ_" + bundle.getString("apogee.pj.codTpj") + "_" + indOpi.getCodIndOpi() + bundle.getString("apogee.opi.pj.ext");
			logger.info("Recherche par path : " + pathDoc);

			final Document d = (Document) cmisSession.getObjectByPath(pathDoc);
			checkString(bundle, String.valueOf(d.getContentStreamLength()), "apogee.opi.pj.size");

			logger.info("********** Fin des Tests des Webservices **********");

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
			parameter.put(SessionParameter.USER, bundle.getString("apogee.opi.pj.user"));
			parameter.put(SessionParameter.PASSWORD, bundle.getString("apogee.opi.pj.pwd"));

			// connection settings
			parameter.put(SessionParameter.ATOMPUB_URL, bundle.getString("apogee.opi.pj.url"));
			parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			parameter.put(SessionParameter.REPOSITORY_ID, bundle.getString("apogee.opi.pj.repository"));
			// create session
			return factory.createSession(parameter);
		} catch (final Exception e) {
			logger.error("Stockage de fichier - Impossible de se connecter au serveur de fichier CMIS", e);
			return null;
		}
	}

	/**
	 * Test les WS Pegase
	 */
	public void testWsPegase() {
		logger.info("********** Début des Tests des Webservices Pegase **********");
		final ResourceBundle bundle = ResourceBundle.getBundle("test-ws");

		/* Test des formations */
		logger.info("********** Vérification recherche formations par code **********");
		try {
			final String codForm = bundle.getString("pegase.formation.code");
			final List<FormationPegase> list = siScolService.getListFormationPegase(codForm, "");
			checkString(bundle, String.valueOf(list.size()), "pegase.formation.code.size");
		} catch (final Exception e) {
			e.printStackTrace();
		}
		logger.info("********** Vérification recherche formations par libelle **********");
		try {
			final String libForm = bundle.getString("pegase.formation.libelle");
			final List<FormationPegase> list = siScolService.getListFormationPegase("", libForm);
			checkString(bundle, String.valueOf(list.size()), "pegase.formation.libelle.size");
		} catch (final Exception e) {
			e.printStackTrace();
		}

		logger.info("********** Vérification apprenant **********");
		try {
			final String codApprenant = bundle.getString("pegase.apprenant.codApprenant");
			final WSIndividu ind = siScolService.getIndividu(codApprenant, null, null);
//			System.out.println(ind);
//			System.out.println(ind.getBac());
//			System.out.println(ind.getAdresse());
//			ind.getListCursusInterne().forEach(e -> {
//				System.out.println(e);
//			});
			checkString(bundle, String.valueOf(ind.getListCursusInterne().size()), "pegase.apprenant.cursus.size");
		} catch (final Exception e) {
			e.printStackTrace();
		}

		logger.info("********** Vérification Referentiel **********");
		final Boolean refEnable = Boolean.valueOf(bundle.getString("pegase.ref.enable"));
		if (!refEnable) {
			return;
		}

		/* Test des AnneeUni */
		try {
			siScolService.getListSiScolAnneeUni().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}
		/* Test des Bac */
		try {
			siScolService.getListSiScolBacOuxEqu().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}

		/* Test des CGE */
		try {
			siScolService.getListSiScolCentreGestion().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}

		/* Test des ComBDI */
		try {
			siScolService.getListSiScolComBdi().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}

		/* Test des Communes */
//		try {
//			siScolService.getListSiScolCommune().forEach(e -> {
//				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
//			});
//		} catch (final SiScolException e) {
//			e.printStackTrace();
//		}

		/* Test des Communes */
		try {
			siScolService.getListSiScolDepartement().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}

		/* Test des Dip */
		try {
			siScolService.getListSiScolDipAutCur().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}

		/* Test des Etabs */
//		try {
//			siScolService.getListSiScolEtablissement().forEach(e -> {
//				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
//			});
//		} catch (final SiScolException e) {
//			e.printStackTrace();
//		}

		/* Test des Mention */
		try {
			siScolService.getListSiScolMention().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}

		/* Test des Mention bac */
		try {
			siScolService.getListSiScolMentionNivBac().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}

		/* Test des pays */
		try {
			siScolService.getListSiScolPays().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}

		/* Test des typDiplome */
		try {
			siScolService.getListSiScolTypDiplome().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}

		/* Test des typRes */
		try {
			siScolService.getListSiScolTypResultat().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}

		/* Test des specialiteBac */
		try {
			siScolService.getListSiScolSpecialiteBac().forEach(e -> {
				MethodUtils.validateBean(e, LoggerFactory.getLogger(TestController.class), true);
			});
		} catch (final SiScolException e) {
			e.printStackTrace();
		}
	}
}
