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

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.QueryStatement;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.controllers.rest.LimeSurveyRest;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Civilite;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.HistoNumDossier;
import fr.univlorraine.ecandidat.entities.siscol.OpiPj;
import fr.univlorraine.ecandidat.entities.siscol.WSPjInfo;
import fr.univlorraine.ecandidat.repositories.AlertSvaRepository;
import fr.univlorraine.ecandidat.repositories.BatchRepository;
import fr.univlorraine.ecandidat.repositories.CandidatRepository;
import fr.univlorraine.ecandidat.repositories.CompteMinimaRepository;
import fr.univlorraine.ecandidat.repositories.FichierRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.repositories.HistoNumDossierRepository;
import fr.univlorraine.ecandidat.repositories.I18nTraductionRepository;
import fr.univlorraine.ecandidat.repositories.PieceJustifRepository;
import fr.univlorraine.ecandidat.repositories.PjCandRepository;
import fr.univlorraine.ecandidat.repositories.PjOpiRepository;
import fr.univlorraine.ecandidat.repositories.SiScolBacOuxEquRepository;
import fr.univlorraine.ecandidat.repositories.SiScolCommuneRepository;
import fr.univlorraine.ecandidat.repositories.SiScolDepartementRepository;
import fr.univlorraine.ecandidat.repositories.SiScolEtablissementRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionCandidatureRepository;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.services.file.FileManager;
import fr.univlorraine.ecandidat.services.security.PasswordHashService;
import fr.univlorraine.ecandidat.services.security.SecurityUserCandidat;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.CustomException;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.export.ExportLettreCandidat;
import fr.univlorraine.ecandidat.utils.bean.mail.CptMinMailBean;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.views.windows.CandidatureWindow;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 */
@Component
@SuppressWarnings("unchecked")
public class TestController {
	private final Logger logger = LoggerFactory.getLogger(TestController.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient UiController uiController;
	@Resource
	private transient AdresseController adresseController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient CommissionController commissionController;
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient CompteMinimaRepository compteMinimaRepository;
	@Resource
	private transient SiScolCommuneRepository siScolCommuneRepository;
	@Resource
	private transient SiScolDepartementRepository siScolDepartementRepository;
	@Resource
	private transient SiScolEtablissementRepository siScolEtablissementRepository;
	@Resource
	private transient SiScolBacOuxEquRepository siScolBacOuxEquRepository;
	@Resource
	private transient PjCandRepository pjCandRepository;
	@Resource
	private transient PjOpiRepository pjOpiRepository;
	@Resource
	private transient PieceJustifRepository pieceJustifRepository;
	@Resource
	private transient HistoNumDossierRepository histoNumDossierRepository;
	@Resource
	private transient UserController userController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient StatController statController;
	@Resource
	private transient FileManager fileManager;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient FichierRepository fichierRepository;
	@Resource
	private transient CandidatRepository candidatRepository;
	@Resource
	private transient AlertSvaRepository alertSvaRepository;
	@Resource
	private transient BatchRepository batchRepository;
	@Resource
	private transient CandidatureGestionController candidatureGestionController;
	@Resource
	private transient BatchController batchController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient TagController tagController;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient LimeSurveyRest limeSurveyRest;
	@Resource
	private transient DateTimeFormatter formatterDate;
	@Resource
	private transient I18nTraductionRepository i18nTraductionRepository;
	@Resource
	private transient TypeDecisionCandidatureRepository typeDecisionCandidatureRepository;

	@Value("${enableTestMode:}")
	private transient Boolean enableTestMode;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	private Session cmisSession;

	/* Les informations de context */
	@Value("${file.cmis.user:}")
	private transient String userCmis;

	@Value("${file.cmis.pwd:}")
	private transient String passwordCmis;

	@Value("${file.cmis.atompub.url:}")
	private transient String urlCmis;

	@Value("${file.cmis.repository:}")
	private transient String repositoryCmis;

	@Value("${file.cmis.gestionnaire.id:}")
	private transient String folderGestionnaireCmis;

	@Value("${file.cmis.candidat.id:}")
	private transient String folderCandidatCmis;

	@Value("${file.cmis.apocandidature.id:}")
	private transient String folderApoCandidatureCmis;

	public Boolean isTestMode() {
		if (enableTestMode == null) {
			return false;
		}
		return enableTestMode;
	}

	public void testMethode() {
//		try {
//			siScolService.getCursusInterne("310140877777").forEach(System.out::println);
//		} catch (final SiScolException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		}
//		final Session cmisSession = getCmisSession();
//
//		final OperationContext operationContext = getCmisSession().createOperationContext();
//		operationContext.setCacheEnabled(false);
//
//		final ItemIterable<CmisObject> resultsFolder = cmisSession.queryObjects("cmis:folder", "IN_FOLDER('23a3fe93-566b-4efa-9553-b3f6aa9a351c')", true, operationContext);
//		System.out.println("Nombre de folders : " + resultsFolder.getTotalNumItems());
//		resultsFolder.forEach(e -> {
////			final Folder folder = (Folder) e;
////			final List<String> liste = folder.deleteTree(true, UnfileObject.DELETE, true);
//			System.out.println("Folder : " + e.getName());
//		});

//		final ItemIterable<CmisObject> resultsDoc = cmisSession.queryObjects("cmis:document", "IN_TREE('" + folderCandidatCmis + "')", true, operationContext);
//		final ItemIterable<CmisObject> resultsFolder = cmisSession.queryObjects("cmis:folder", "IN_TREE('" + folderCandidatCmis + "')", true, operationContext);
//
//		System.out.println("Nombre de docs : " + resultsDoc.getTotalNumItems());
//		System.out.println("Nombre de folders : " + resultsFolder.getTotalNumItems());
//
//		resultsDoc.forEach(e -> {
//			System.out.println("Doc : " + e.getName());
//		});
//
//		resultsFolder.forEach(e -> {
//			System.out.println("Folder : " + e.getName());
//		});

//		final String queryFolder = "SELECT * FROM cmis:folder WHERE IN_TREE('" + folderCandidatCmis + "')";
//
//		/* Requete CMIS pour rechercher le fichier */
//		final QueryStatement qsF = getCmisSession().createQueryStatement(queryDoc);
//
//		System.out.println(qsF.query(true).getTotalNumItems());

		//siScolService.creerOpiViaWS(candidatRepository.findOne(62433), true);

//		final Campagne campagne = campagneController.getCampagneActive();
//
//		System.out.println("Etape 0 : " + LocalDateTime.now());
//		// System.out.println(typeDecisionCandidatureRepository.findListFavoNotConfirmToRelance(campagne, true, false, tableRefController.getTypeAvisFavorable()).size());
//		System.out.println("Etape 1 : " + LocalDateTime.now());
//		System.out.println(candidatureGestionController.findTypDecFavoNotAccept(campagne, true).size());
//		System.out.println("Etape 2 : " + LocalDateTime.now());

		/* System.out.println(typeDecisionCandidatureRepository.findListFavoNotConfirmToRelance(campagne, true, false,
		 * tableRefController.getTypeAvisFavorable()).size());
		 * System.out.println(typeDecisionCandidatureRepository.findListFavoNotConfirmToDesist(campagne, true, tableRefController.getTypeAvisFavorable()).size());
		 *
		 * System.out.println("Etape 0 : " + LocalDateTime.now());
		 * List<Formation> listForm = formationRepository.findByTesFormAndTemListCompForm(true, true);
		 * int[] cpt1 = {0};
		 *
		 * System.out.println("Etape 1 : " + LocalDateTime.now());
		 *
		 * listForm.forEach(formation -> {
		 * List<TypeDecisionCandidature> listeTdc = candidatureGestionController.findTypDecLc(formation, campagne);
		 * cpt1[0] = cpt1[0] + listeTdc.size();
		 * });
		 *
		 * System.out.println("Etape 1 nb: " + cpt1[0]);
		 *
		 * System.out.println("Etape 2 : " + LocalDateTime.now());
		 *
		 * int[] cpt2 = {0};
		 * listForm.forEach(formation -> {
		 * List<TypeDecisionCandidature> listeTdc2 = typeDecisionCandidatureRepository.findListCompByFormation(campagne, true, tableRefController.getTypeAvisListComp(),
		 * formation);
		 * cpt2[0] = cpt2[0] + listeTdc2.size();
		 * });
		 * System.out.println("Etape 2 nb: " + cpt2[0]);
		 *
		 * System.out.println("Etape 3 : " + LocalDateTime.now()); */

		// String html = "<b>Consultez <span>l'offre </span>de formation et <font color=\"#ff0000\">créez</font> votre compte. <span><i><u>Bonjour</u></i></span>, je <span><font size=\"7\"
		// face=\"Georgia\">suis</font></span> du <span>texte</span></b> ";
		//
		// Whitelist whitelist = Whitelist.relaxed();
		// whitelist.addTags("font");
		// whitelist.addAttributes("font", "color", "face", "size");
		// whitelist.addAttributes("p", "style");
		// whitelist.addAttributes("span", "style");
		// whitelist.addAttributes("div", "style");
		//
		// System.out.println("1 : " + html);
		// System.out.println("2 : " + Jsoup.clean(html, whitelist));
		// System.out.println("3 : " + Parser.unescapeEntities(Jsoup.clean(html, whitelist), true));

		// I18nTraductionPK pk = new I18nTraductionPK(6, "fr");
		// I18nTraduction trad = i18nTraductionRepository.findOne(pk);
		// System.out.println(transformI18nVelocity(trad));

		// mailController.getMailsByCtrCand(false, null).forEach(e -> {
		//
		// e.getI18nCorpsMail().getI18nTraductions().forEach(trad -> {
		// System.out.println();
		// System.out.println("AVANT");
		// System.out.println(trad.getValTrad());
		//
		// System.out.println("APRES");
		// System.out.println(transformI18nVelocity(trad.getValTrad()));
		// });
		// });

		// String velocity = "Bonjour $prenom $candidat.nomPat";
		//
		// velocity = "#if($!car.fuel != '')testIf#end";

		// I18nTraductionPK pk = new I18nTraductionPK(3585, "fr");
		// I18nTraduction trad = i18nTraductionRepository.findOne(pk);
		// //
		// // CandidatMailBean cand = new CandidatMailBean();
		// // cand.setNomPat("TestNomPat");
		// // cand.setPrenom("TestPrenom");
		// //
		// try {
		// RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
		// StringReader reader = new StringReader(trad.getValTrad());
		// Template template = new Template();
		// template.setRuntimeServices(runtimeServices);
		// template.setData(runtimeServices.parse(reader, "Template name"));
		// template.initDocument();
		//
		// VelocityContext vc = new VelocityContext();
		// vc.put("StringUtils", StringUtils.class);
		// vc.put("candidat.prenom", "TestNomPat");
		// vc.put("candidat.nomPat", "TestPrenom");
		//
		// StringWriter sw = new StringWriter();
		// template.merge(vc, sw);
		//
		// System.out.println(sw.toString());
		// } catch (Exception e) {
		//
		// }

		// candidatureGestionController.findTypDecLc(formationRepository.findOne(2101), campagneController.getCampagneActive()).forEach(e -> {
		// System.out.println(e);
		// });
		// System.out.println("XXXXXXXXXX");
		// candidatureGestionController.candidatFirstCandidatureListComp(formationRepository.findOne(2101));
		// candidatureGestionController.findTypDecLc(formationRepository.findOne(2101), campagneController.getCampagneActive()).forEach(e -> {
		// System.out.println(e);
		// });
		// System.out.println(limeSurveyRest.getVersionLimeSurvey());
		// PjOpiPK pk = new PjOpiPK("EC00I0553R", "DIDEN");
		// PjOpi pjopi = pjOpiRepository.findOne(pk);
		// // System.out.println(pjopi);
		// try {
		// candidatureGestionController.deversePjOpi(pjopi);
		// } catch (SiScolException e) {
		// e.printStackTrace();
		// }

		// checkPJOPI();

		// try {
		// System.out.println(isFileCandidatureOpiExist("PJ_DIDEN_175322.pdf"));
		// } catch (FileException e) {
		// e.printStackTrace();
		// }

		// getIdsCmis();
		// try {
		// siScolService.deleteOpiPJ("171045", "DIDEN");
		// } catch (SiScolException e) {
		// e.printStackTrace();
		// }
		// String toto = null;
		// toto.length();

		// candidaturePieceController.deversePjOpi(candidatureController.loadCandidature(93713).getOpi(),"ECWN41JN3A");
		// candidatureGestionController.launchBatchAsyncOPIPj();
		// fileManager.deleteCampagneFolder("2010");
		// try {
		// siScolService.checkStudentINES("223456789", "HE");
		// } catch (SiScolException e) {
		// e.printStackTrace();
		// }
		// candidatureGestionController.launchBatchAsyncOPIPj();
		// candidatureController.archiveCandidatureDateFormation(campagneController.getCampagneActive());

		/* statController.getStatFormation(43).forEach(e->{ System.out.println(e); }); */

		/* PjPresentation pieceJustif = new PjPresentation(); Fichier file =
		 * fichierRepository.findOne(331); pieceJustif.setFilePj(file);
		 * InputStream is =
		 * fileController.getInputStreamFromPjPresentation(pieceJustif); if (is !=
		 * null){ ImageViewerWindow iv = new ImageViewerWindow(new
		 * OnDemandFile(file.getNomFichier(), is), null); UI.getCurrent().addWindow(iv);
		 * } */

		/* System.out.println(fichierRepository.findFichierOrphelin(LocalDateTime.now().
		 * minusDays(1))); String totot = null; totot.length(); */
		/* try { candidatureGestionController.launchBatchDestructDossier(); } catch
		 * (FileException e) { // TODO Auto-generated catch block e.printStackTrace(); } */
		/* try { //logger.debug(siScolService.getPjInfoFromApogee("2016", "31600488",
		 * "DSEC0")); siScolService.getPjFichierFromApogee("2016", "31600249", "DVITA");
		 * //fileContr. } catch (SiScolException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } */
		// fileManager.deleteCampagneFolder("2014");
		/* Candidat cand = candidatRepository.findOne(21); for (int i=0;i<2000;i++){
		 * candidatureController.testCandidature(cand, 1); } */

		/* CompteMinima cpt = compteMinimaRepository.findOne(480);
		 * cpt.setDatCreCptMin(LocalDateTime.now()); compteMinimaRepository.save(cpt);
		 * logger.debug(LocalDateTime.now()); */
		// logger.debug("Test lancé");
		/* cacheController.getMapParametre(); cacheController.reloadMapParametre(false); */
		/* Candidat candidat = candidatRepository.findOne(3273); MAJEtatCivilDTO
		 * etatCivil = siScolService.getEtatCivil(candidat);
		 * etatCivil.setCodNneIndOpi(null); */
		/* EC1IWCWV74 */
		/* 31525894 */

		// logger.debug(siScolService.findNneIndOpiByCodOpiIntEpo("toto", null,
		// etatCivil, candidat.getDatNaissCandidat()));
	}

	public void getIdsCmis() {
		try {
			final Session session = getCmisSession();
			final CmisObject object = session.getObject(session.createObjectId(folderApoCandidatureCmis));
			final Folder folder = (Folder) object;
			final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss");
			int i = 1;
			for (final CmisObject e : folder.getChildren()) {
				try {
					final Folder folderCand = (Folder) e;
					for (final CmisObject file : folderCand.getChildren()) {
						System.out.println(i + ";"
							+ file.getId()
							+ ";"
							+ file.getName()
							+ ";"
							+ file.getCreatedBy()
							+ ";"
							+ fmt.format(file.getCreationDate().getTime())
							+ ";"
							+ file.getLastModifiedBy()
							+ ";"
							+ fmt.format(file.getLastModificationDate().getTime()));
						i++;
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			// return false;
		} catch (final Exception e) {
			e.printStackTrace();

		}
	}

	public void checkPJOPI() {
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
		final EntityManager em = emf.createEntityManager();
		// String requete = "select distinct IND_OPI.COD_IND_OPI, OPI_PJ.COD_TPJ, IND_OPI.COD_OPI_INT_EPO, OPI_PJ.NOM_FIC, INDIVIDU.COD_ETU from OPI_PJ, IND_OPI LEFT OUTER JOIN INDIVIDU ON
		// INDIVIDU.COD_IND_OPI = IND_OPI.COD_IND_OPI where OPI_PJ.COD_IND_OPI = IND_OPI.COD_IND_OPI";
		final String requete = "select distinct IND_OPI.COD_IND_OPI, OPI_PJ.COD_TPJ, IND_OPI.COD_OPI_INT_EPO, OPI_PJ.NOM_FIC, INDIVIDU.COD_ETU "
			+ "from TELEM_IAA_TPJ, INDIVIDU, OPI_PJ, TELEM_PIECE_JUSTIF, IND_OPI "
			+ "where OPI_PJ.COD_IND_OPI = INDIVIDU.COD_IND_OPI "
			+ "and IND_OPI.COD_IND_OPI = INDIVIDU.COD_IND_OPI "
			+ "and OPI_PJ.COD_TPJ = TELEM_IAA_TPJ.COD_TPJ "
			+ "and INDIVIDU.COD_IND = TELEM_IAA_TPJ.COD_IND "
			+ "and TELEM_PIECE_JUSTIF.COD_TPJ = TELEM_IAA_TPJ.COD_TPJ "
			+ "and TEM_PJ_CAND = 'O' and TELEM_IAA_TPJ.NOM_FIC is null "
			+ "and TEM_DEMAT_PJ = 'O' and DATE_RECEP_PJ is null "
			+ "and TELEM_IAA_TPJ.COD_ANU = (select COD_ANU from ANNEE_UNI WHERE ETA_ANU_IAE = 'O')";

		final Query query = em.createNativeQuery(requete, OpiPj.class);
		final List<OpiPj> listeOpiPJ = query.getResultList();
		Integer i = 0;
		Integer cpt = 0;
		System.out.println(LocalDateTime.now() + " lancement de " + listeOpiPJ.size());
		for (final OpiPj e : listeOpiPJ) {
			try {
				Boolean present = isFileCandidatureOpiExist(e.getNomFic());
				// System.out.println("Duréé recherche opi : " + Duration.between(cptTime, LocalDateTime.now()).toMillis());
				if (!present) {
					if (e.getCodEtu() != null) {
						final String newName = "PJ_" + e.getId().getCodTpj() + "_" + e.getCodEtu() + ".";
						present = isFileDefinitifExist(newName);
						// System.out.println("Duréé recherche definitive : " + Duration.between(cptTime, LocalDateTime.now()).toMillis());
						if (!present) {
							System.out.println("Recherche définitive : " + e.getNomFic());
							System.out.println("D;" + e.getId().getCodIndOpi() + ";" + e.getId().getCodTpj() + ";" + e.getCodOpiIndEpo() + ";" + e.getNomFic() + ";" + e.getCodEtu());
							System.out.println("update pj_opi p set dat_deversement = null where p.cod_opi = '" + e.getCodOpiIndEpo() + "' and p.cod_apo_pj = '" + e.getId().getCodTpj() + "';");
							System.out.println("update batch b set tem_is_launch_imedia_batch = 1 where cod_batch = 'BATCH_ASYNC_OPI_PJ';");
						}
					} else {
						System.out.println("C;" + e.getId().getCodIndOpi() + ";" + e.getId().getCodTpj() + ";" + e.getCodOpiIndEpo() + ";" + e.getNomFic() + ";" + e.getCodEtu());
					}
				}
				i++;
				cpt++;
				if (i.equals(300)) {
					System.out.println(LocalDateTime.now() + ": Verification de " + cpt + " OPIPJ");
					i = 0;
				}
			} catch (final FileException e1) {
				e1.printStackTrace();
			}
		}
		em.close();
	}

	/** @return la session CMIS */
	public Session getCmisSession() {
		if (cmisSession == null) {
			cmisSession = cmisSession();
		}
		return cmisSession;
	}

	/** @return la session CMIS */
	private Session cmisSession() {
		try {
			// default factory implementation
			final SessionFactory factory = SessionFactoryImpl.newInstance();
			final Map<String, String> parameter = new HashMap<>();

			// user credentials
			parameter.put(SessionParameter.USER, userCmis);
			parameter.put(SessionParameter.PASSWORD, passwordCmis);

			// connection settings
			parameter.put(SessionParameter.ATOMPUB_URL, urlCmis);
			parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			parameter.put(SessionParameter.REPOSITORY_ID, repositoryCmis);
			// create session
			return factory.createSession(parameter);
		} catch (final Exception e) {
			logger.error("Stockage de fichier - Impossible de se connecter au serveur de fichier CMIS", e);
			return null;
		}
	}

	public Boolean isFileCandidatureOpiExist(final String name) throws FileException {
		final Session session = getCmisSession();
		try {
			/* Requete CMIS pour rechercher le fichier */
			final QueryStatement qs = session.createQueryStatement("SELECT * FROM cmis:document WHERE cmis:name = ?");
			qs.setString(1, name);

			/* True si la requete ramene plus de 0 resultats */
			return qs.query(true).getTotalNumItems() > 0;
			// return false;
		} catch (final Exception e) {
			throw new FileException(e);
		}
	}

	public Boolean isFileDefinitifExist(final String name) throws FileException {
		final Session session = getCmisSession();
		try {
			/* Requete CMIS pour rechercher le fichier */
			final QueryStatement qs = session.createQueryStatement("SELECT * FROM cmis:document WHERE cmis:name LIKE ?");
			qs.setString(1, name + "%");

			/* True si la requete ramene plus de 0 resultats */
			return qs.query(true).getTotalNumItems() > 0;
			// return false;
		} catch (final Exception e) {
			throw new FileException(e);
		}
	}

	/** @return le fichier */
	public OnDemandFile testLettreAdm() {

		final String templateLettreAdm = ConstanteUtils.TEMPLATE_LETTRE_REFUS;
		final String fileName = applicationContext.getMessage("candidature.lettre.file.ref", new Object[] { "AXQDF1P8_Martinpat_Jean", "CODFORM" }, UI.getCurrent().getLocale());
		final Commission commission = commissionController.getCommissionById(1);
		final Adresse adrComm = commission.getAdresse();
		final Adresse adrTest = new Adresse("15 rue des plantes", null, null, adrComm.getCodBdiAdr(), null, adrComm.getSiScolCommune(), adrComm.getSiScolPays());

		final String adresseCandidat = adresseController.getLibelleAdresse(adrTest, "\n");
		final String adresseCommission = adresseController.getLibelleAdresse(commission.getAdresse(), "\n");

		final ExportLettreCandidat data = new ExportLettreCandidat("AXQDF1P8",
			"Monsieur",
			"Martin",
			"Martinpat",
			"Jean",
			"10/10/1985",
			adresseCandidat,
			"Campagne 2015",
			commission.getLibComm(),
			adresseCommission,
			"AX-BJ156",
			"L1 informatique",
			commission.getSignataireComm(),
			"Libellé de la décision",
			"Commentaire de la décision",
			"Diplome requis manquant",
			"16/08/2016",
			"10/06/2016",
			"17/08/2016",
			false,
			new BigDecimal("1200.21"),
			"Commentaire d'exo");

		InputStream fichierSignature = null;
		if (commission.getFichier() != null) {
			fichierSignature = fileController.getInputStreamFromFichier(commission.getFichier());
		}
		final InputStream template = MethodUtils.getXDocReportTemplate(templateLettreAdm, "fr", cacheController.getLangueDefault().getCodLangue());
		return new OnDemandFile(fileName, candidatureController.generateLettre(template, data, fichierSignature, "fr", true));
	}

	public OnDemandFile testFichier() {
		try {
			final WSPjInfo info = siScolService.getPjInfoFromApogee("2016", "31600249", "DVITA");
			return new OnDemandFile(info.getNomFic(), siScolService.getPjFichierFromApogee("2016", "31600249", "DVITA"));
		} catch (final SiScolException e) {
			return null;
		}
	}

	/* public void testMethod(){ logger.debug(candidatRepository.
	 * findByIneCandidatIgnoreCaseAndCleIneCandidatIgnoreCaseAndCompteMinimaCampagneCodCamp
	 * ("toto", "b", "2016"));
	 * logger.debug(compteMinimaRepository.findByNumDossierOpiCptMin("1QJ5A59F"));
	 * logger.debug(compteMinimaRepository.
	 * findByTemValidCptMinAndDatFinValidCptMinBefore(true, LocalDateTime.now()));
	 * logger.debug(compteMinimaRepository.
	 * findBySupannEtuIdCptMinAndIdCptMinNotAndCampagneCodCamp("39811490", 1,
	 * "2015")); logger.debug(compteMinimaRepository.
	 * findByLoginCptMinIgnoreCaseAndIdCptMinNotAndCampagneCodCamp("hergalan6", 1,
	 * "2015")); logger.debug(compteMinimaRepository.
	 * findByNumDossierOpiCptMinAndCampagneCodCamp("1QJ5A59F", "2015"));
	 * logger.debug(compteMinimaRepository.
	 * findByLoginCptMinIgnoreCaseAndCampagneCodCamp("hergalan6", "2015"));
	 * logger.debug(compteMinimaRepository.
	 * findByMailPersoCptMinIgnoreCaseAndCampagneCodCamp("sTeLlaNce@hotMAil.fr",
	 * "2015")); } */

	/* public void afficheFichierPerdu(){
	 * logger.debug(LocalDateTime.now()+" : Verif de fichiers"); List<Fichier> liste
	 * = fichierRepository.findAll(); int i = 0; for (Fichier e : liste){ try{
	 * if(e.getTypFichier().equals("C")){ i++;
	 * fileManager.getInputStreamFromFile(e,false); } }catch(Exception ex){
	 * logger.debug(e.getIdFichier()); } }
	 * logger.debug(LocalDateTime.now()+" : Verif de "+i+" fichiers"); } */

	public CompteMinima createCompteMinima() {
		logger.debug("Creation du compte");
		// Generateur de mot de passe
		final PasswordHashService passwordHashUtils = PasswordHashService.getCurrentImplementation();

		final Campagne campagne = campagneController.getCampagneActive();
		CompteMinima cptMin = new CompteMinima();
		cptMin.setCampagne(campagne);
		cptMin.setMailPersoCptMin("kevin.hergalant@univ-lorraine.fr");
		cptMin.setNomCptMin("TEST-LB-NOM");
		cptMin.setPrenomCptMin("TEST-LB-PRENOM");
		cptMin.setTemValidCptMin(true);
		cptMin.setTemValidMailCptMin(true);
		cptMin.setTemFcCptMin(false);
		LocalDateTime datValid = LocalDateTime.now();
		final Integer nbJourToKeep = parametreController.getNbJourKeepCptMin();
		datValid = datValid.plusDays(nbJourToKeep);
		datValid = LocalDateTime.of(datValid.getYear(), datValid.getMonth(), datValid.getDayOfMonth(), 23, 0, 0);
		cptMin.setDatFinValidCptMin(datValid);

		final String prefix = parametreController.getPrefixeNumDossCpt();
		Integer sizeNumDossier = ConstanteUtils.GEN_SIZE;
		if (prefix != null) {
			sizeNumDossier = sizeNumDossier - prefix.length();
		}

		String numDossierGenere = passwordHashUtils.generateRandomPassword(sizeNumDossier, ConstanteUtils.GEN_NUM_DOSS);

		while (isNumDossierExist(numDossierGenere)) {
			numDossierGenere = passwordHashUtils.generateRandomPassword(sizeNumDossier, ConstanteUtils.GEN_NUM_DOSS);
		}

		if (prefix != null) {
			numDossierGenere = prefix + numDossierGenere;
		}
		cptMin.setNumDossierOpiCptMin(numDossierGenere);

		String pwd = passwordHashUtils.generateRandomPassword(ConstanteUtils.GEN_SIZE, ConstanteUtils.GEN_PWD);
		pwd = "123";
		try {
			cptMin.setPwdCptMin(passwordHashUtils.createHash(pwd));
			cptMin.setTypGenCptMin(passwordHashUtils.getType());
		} catch (final CustomException e) {
			return null;
		}
		final String codLangue = "fr";
		try {
			logger.debug("Creation compte NoDossier = " + cptMin.getNumDossierOpiCptMin());
			/* Enregistrement de l'historique */
			histoNumDossierRepository.saveAndFlush(new HistoNumDossier(cptMin.getNumDossierOpiCptMin(), campagne.getCodCamp()));
			/* Enregistrement du compte */
			cptMin = compteMinimaRepository.saveAndFlush(cptMin);
			final CptMinMailBean mailBean = new CptMinMailBean(cptMin.getPrenomCptMin(),
				cptMin.getNomCptMin(),
				cptMin.getNumDossierOpiCptMin(),
				pwd,
				"http://lien-validation-" + numDossierGenere,
				campagneController.getLibelleCampagne(cptMin.getCampagne(), codLangue),
				formatterDate.format(cptMin.getDatFinValidCptMin()));
			mailController.sendMailByCod(cptMin.getMailPersoCptMin(), NomenclatureUtils.MAIL_CPT_MIN, mailBean, null, codLangue);
			return cptMin;
		} catch (final Exception ex) {
			logger.error(applicationContext.getMessage("compteMinima.numdossier.error", null, UI.getCurrent().getLocale()) + " numDossier=" + numDossierGenere, ex);
			return null;
		}
	}

	/**
	 * Vérifie qu'un dossier existe
	 * @param  numDossier
	 * @return            true si le numDossier existe deja
	 */
	private Boolean isNumDossierExist(final String numDossier) {
		final CompteMinima cptMin = compteMinimaRepository.findByNumDossierOpiCptMin(numDossier);
		if (cptMin != null || histoNumDossierRepository.exists(numDossier)) {
			return true;
		}
		return false;
	}

	public void allInOne() {
		candidatToFormation();
		openCandidature();
		downloadDossier();
		deleteCandidat();
		finish();
	}

	public void completeDossier() {
		final SecurityUserCandidat cand = userController.getSecurityUserCandidat();
		if (cand != null) {
			final CompteMinima cpt = compteMinimaRepository.findOne(cand.getIdCptMin());
			final Adresse adresse = new Adresse();
			adresse.setAdr1Adr("Test");
			adresse.setAdr2Adr("Test");
			adresse.setAdr3Adr("Test");
			adresse.setCodBdiAdr("57045");
			adresse.setLibComEtrAdr("Test");
			adresse.setSiScolPays(cacheController.getPaysFrance());
			adresse.setSiScolCommune(siScolCommuneRepository.findOne("57463"));

			/* CandidatBacOuEqu bac = new CandidatBacOuEqu(); bac.setAnneeObtBac(2000);
			 * bac.set */

			Candidat candidat = new Candidat();
			candidat.setCompteMinima(cpt);
			candidat.setCivilite(new Civilite("M.", "Monsieur", "1"));
			candidat.setAdresse(adresse);
			candidat.setNomPatCandidat(cpt.getNomCptMin());
			candidat.setNomUsuCandidat(cpt.getNomCptMin());
			candidat.setPrenomCandidat(cpt.getPrenomCptMin());
			candidat.setDatNaissCandidat(LocalDate.now().minusYears(Long.valueOf(20)));
			candidat.setSiScolPaysNaiss(cacheController.getPaysFrance());
			candidat.setSiScolPaysNat(cacheController.getPaysFrance());
			candidat.setSiScolDepartement(siScolDepartementRepository.findOne("057"));
			candidat.setLibVilleNaissCandidat("Metz");
			candidat.setLangue(cacheController.getLangueDefault());
			candidat.setTemUpdatableCandidat(true);
			candidat = candidatRepository.save(candidat);

			final CandidatBacOuEqu bac = new CandidatBacOuEqu();
			bac.setAnneeObtBac(2000);
			bac.setSiScolPays(cacheController.getPaysFrance());
			bac.setSiScolDepartement(siScolDepartementRepository.findOne("057"));
			bac.setSiScolCommune(siScolCommuneRepository.findOne("57463"));
			bac.setSiScolEtablissement(siScolEtablissementRepository.findOne("0540041B"));
			bac.setSiScolBacOuxEqu(siScolBacOuxEquRepository.findOne("S"));
			bac.setTemUpdatableBac(true);
			bac.setCandidat(candidat);
			bac.setIdCandidat(candidat.getIdCandidat());
			candidat.setCandidatBacOuEqu(bac);
			candidatRepository.save(candidat);

			logger.debug("Dossier complet");
		}
	}

	public void candidatToFormation() {
		logger.debug("Candidature");
		candidatureController.candidatToFormation(4, null, true);
	}

	/* public void candidatToFormationWithPJ(){ SecurityUserCandidat cand =
	 * userController.getSecurityUserCandidat(); if (cand!=null){ CompteMinima cpt =
	 * compteMinimaRepository.findOne(cand.getIdCptMin()); if (cpt!=null &&
	 * cpt.getCandidat()!=null && cpt.getCandidat().getCandidatures().size()>0){
	 * logger.debug("Ajout d'une PJ"); Candidature candidature =
	 * cpt.getCandidat().getCandidatures().get(0); PieceJustif piece =
	 * pieceJustifRepository.findOne(2); PjCandPK pk = new PjCandPK(2,
	 * candidature.getIdCand()); PjCand pj = new PjCand(pk, "test", candidature,
	 * piece); Fichier file = fichierRepository.findOne(46841); pj.setFichier(file);
	 * pj.setTypeStatutPiece(tableRefController.getTypeStatutPieceTransmis());
	 * pj.setUserModPjCand("test"); pjCandRepository.save(pj); } } } */

	public void openCandidature() {
		final SecurityUserCandidat cand = userController.getSecurityUserCandidat();
		if (cand != null) {
			final CompteMinima cpt = compteMinimaRepository.findOne(cand.getIdCptMin());
			if (cpt != null && cpt.getCandidat() != null && cpt.getCandidat().getCandidatures().size() > 0) {
				logger.debug("openCandidature : " + cpt.getNumDossierOpiCptMin());
				final Candidature candidature = cpt.getCandidat().getCandidatures().get(0);
				final CandidatureWindow cw = new CandidatureWindow(candidature, true, false, false, null);
				UI.getCurrent().addWindow(cw);
				cw.close();
			}
		}
	}

	public void downloadDossier() {
		final SecurityUserCandidat cand = userController.getSecurityUserCandidat();
		if (cand != null) {
			final CompteMinima cpt = compteMinimaRepository.findOne(cand.getIdCptMin());
			logger.debug("Download dossier : " + cpt.getNumDossierOpiCptMin() + " " + cpt.getCandidat());
			if (cpt != null && cpt.getCandidat() != null && cpt.getCandidat().getCandidatures().size() > 0) {
				logger.debug("Download dossier candidat : " + cpt.getNumDossierOpiCptMin());
				final Candidature candidature = cpt.getCandidat().getCandidatures().get(0);
				candidatureController.downloadDossier(candidature,
					candidatureController.getInformationsCandidature(candidature, false),
					candidatureController.getInformationsDateCandidature(candidature, false),
					candidaturePieceController.getPjCandidature(candidature),
					candidaturePieceController.getFormulaireCandidature(candidature),
					true);
			}
		}
	}

	public void deleteCandidat() {
		final SecurityUserCandidat cand = userController.getSecurityUserCandidat();
		if (cand != null) {
			final CompteMinima cpt = compteMinimaRepository.findOne(cand.getIdCptMin());
			if (cpt != null) {
				logger.debug("Delete compte NoDossier = " + cpt.getNumDossierOpiCptMin());
				compteMinimaRepository.delete(cpt);
				uiController.unregisterUiCandidat(MainUI.getCurrent());
				final SecurityContext context = SecurityContextHolder.createEmptyContext();
				SecurityContextHolder.setContext(context);
				UI.getCurrent().getSession().getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
				final MainUI current = (MainUI) UI.getCurrent();
				uiController.registerUiCandidat(current);
				current.navigateToAccueilView();
			}
		}
	}

	public void finish() {
		logger.debug("Finish");
	}

	/* public void createMultipleCptMin(){ for (int i = 0;i<200;i++){
	 * createCompteMinima(); } } */

	/* public CompteMinima getRandomCptMin(List<CompteMinima> liste){ Random
	 * randomGenerator = new Random(); List<CompteMinima> listeWithout = new
	 * ArrayList<CompteMinima>(); liste.forEach(e->{ if (e.getIdCptMin()>18){
	 * listeWithout.add(e); } }); int index =
	 * randomGenerator.nextInt(listeWithout.size()); return listeWithout.get(index);
	 * } */

	/* public CompteMinima createCompteMinima(){ PasswordHashService
	 * passwordHashUtils = PasswordHashService.getCurrentImplementation();
	 * CompteMinima cptMin =
	 * compteMinimaRepository.findByNumDossierOpiCptMin("1QJ5A59F"); Campagne
	 * campagne = campagneController.getCampagneActive(); cptMin.setIdCptMin(null);
	 * cptMin.setSupannEtuIdCptMin(null); cptMin.setLoginCptMin(null);
	 * cptMin.setCampagne(campagne); String prefix =
	 * parametreController.getPrefixeNumDossCpt(); Integer sizeNumDossier =
	 * ConstanteUtils.GEN_SIZE; if (prefix!=null){ sizeNumDossier =
	 * sizeNumDossier-prefix.length(); }
	 * String numDossierGenere =
	 * passwordHashUtils.generateRandomPassword(sizeNumDossier,ConstanteUtils.
	 * GEN_NUM_DOSS);
	 * while(isNumDossierExist(numDossierGenere)){ numDossierGenere =
	 * passwordHashUtils.generateRandomPassword(sizeNumDossier,ConstanteUtils.
	 * GEN_NUM_DOSS); }
	 * if (prefix!=null){ numDossierGenere = prefix+numDossierGenere; }
	 * cptMin.setNumDossierOpiCptMin(numDossierGenere); try {
	 * cptMin.setPwdCptMin(passwordHashUtils.createHash("123"));
	 * cptMin.setTypGenCptMin(passwordHashUtils.getType()); } catch (CustomException
	 * e) { e.printStackTrace(); }
	 * LocalDateTime datValid = LocalDateTime.now(); Integer nbJourToKeep =
	 * parametreController.getNbJourKeepCptMin(); datValid =
	 * datValid.plusDays(nbJourToKeep); datValid =
	 * LocalDateTime.of(datValid.getYear(), datValid.getMonth(),
	 * datValid.getDayOfMonth(), 23, 0,0); cptMin.setDatFinValidCptMin(datValid);
	 * String numDossier = cptMin.getNumDossierOpiCptMin(); if (numDossier==null ||
	 * numDossier.equals("")){ return null; } cptMin =
	 * compteMinimaRepository.saveAndFlush(cptMin);
	 * histoNumDossierRepository.saveAndFlush(new HistoNumDossier(numDossier,
	 * campagne.getCodCamp()));
	 * logger.debug("Creation compte NoDossier = "+cptMin.getNumDossierOpiCptMin());
	 * return cptMin; } */

	/* public void createCandidats(){ CompteMinima cptMin =
	 * compteMinimaRepository.findByNumDossierOpiCptMin("1QJ5A59F"); Candidat
	 * candidat = cptMin.getCandidat(); candidat.setIneCandidat(null);
	 * candidat.setCleIneCandidat(null); candidat.setIdCandidat(null);
	 * candidat.setTemUpdatableCandidat(true);
	 * Adresse adresse = candidat.getAdresse();
	 * List<CompteMinima> liste = compteMinimaRepository.findAll();
	 * liste.forEach(e->{ if (e.getIdCptMin()>17){ candidat.setCompteMinima(e);
	 * adresse.setIdAdr(null); candidat.setAdresse(adresse);
	 * candidatRepository.save(candidat); } }); } */
}
