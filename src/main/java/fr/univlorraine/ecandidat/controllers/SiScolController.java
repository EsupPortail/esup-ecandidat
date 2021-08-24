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

import java.time.LocalDateTime;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOptBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacSpeBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolComBdi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolOptionBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolSpecialiteBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolUtilisateur;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.entities.siscol.WSPjInfo;
import fr.univlorraine.ecandidat.repositories.SiScolAnneeUniRepository;
import fr.univlorraine.ecandidat.repositories.SiScolBacOptBacRepository;
import fr.univlorraine.ecandidat.repositories.SiScolBacOuxEquRepository;
import fr.univlorraine.ecandidat.repositories.SiScolBacSpeBacRepository;
import fr.univlorraine.ecandidat.repositories.SiScolCentreGestionRepository;
import fr.univlorraine.ecandidat.repositories.SiScolComBdiRepository;
import fr.univlorraine.ecandidat.repositories.SiScolCommuneRepository;
import fr.univlorraine.ecandidat.repositories.SiScolDepartementRepository;
import fr.univlorraine.ecandidat.repositories.SiScolDipAutCurRepository;
import fr.univlorraine.ecandidat.repositories.SiScolEtablissementRepository;
import fr.univlorraine.ecandidat.repositories.SiScolMentionNivBacRepository;
import fr.univlorraine.ecandidat.repositories.SiScolMentionRepository;
import fr.univlorraine.ecandidat.repositories.SiScolOptionBacRepository;
import fr.univlorraine.ecandidat.repositories.SiScolPaysRepository;
import fr.univlorraine.ecandidat.repositories.SiScolSpecialiteBacRepository;
import fr.univlorraine.ecandidat.repositories.SiScolTypDiplomeRepository;
import fr.univlorraine.ecandidat.repositories.SiScolTypResultatRepository;
import fr.univlorraine.ecandidat.repositories.SiScolUtilisateurRepository;
import fr.univlorraine.ecandidat.repositories.VersionRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.views.windows.InfoWindow;
import fr.univlorraine.ecandidat.views.windows.InputWindow;

/**
 * Batch de synchro siScol
 * @author Kevin Hergalant
 */
@Component
public class SiScolController {

	private final Logger logger = LoggerFactory.getLogger(SiScolController.class);

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	@Resource
	private transient String urlWsPjApogee;

	@Resource
	private transient ApplicationContext applicationContext;

	@Resource
	private transient CacheController cacheController;

	@Resource
	private transient NomenclatureController nomenclatureController;

	@Resource
	private transient BatchController batchController;

	@Resource
	private transient DemoController demoController;

	/* Injection repository ecandidat */
	@Resource
	private transient SiScolUtilisateurRepository siScolUtilisateurRepository;
	@Resource
	private transient SiScolTypDiplomeRepository siScolTypDiplomeRepository;
//	@Resource
//	private transient SiScolCatExoExtRepository siScolCatExoExtRepository;
	@Resource
	private transient SiScolPaysRepository siScolPaysRepository;
	@Resource
	private transient SiScolMentionRepository siScolMentionRepository;
	@Resource
	private transient SiScolTypResultatRepository siScolTypResultatRepository;
	@Resource
	private transient SiScolMentionNivBacRepository siScolMentionNivBacRepository;
	@Resource
	private transient SiScolEtablissementRepository siScolEtablissementRepository;
	@Resource
	private transient SiScolDipAutCurRepository siScolDipAutCurRepository;
	@Resource
	private transient SiScolDepartementRepository siScolDepartementRepository;
	@Resource
	private transient SiScolCommuneRepository siScolCommuneRepository;
	@Resource
	private transient SiScolCentreGestionRepository siScolCentreGestionRepository;
	@Resource
	private transient SiScolBacOuxEquRepository siScolBacOuxEquRepository;
	@Resource
	private transient SiScolComBdiRepository siScolComBdiRepository;
	@Resource
	private transient VersionRepository versionRepository;
	@Resource
	private transient SiScolAnneeUniRepository siScolAnneeUniRepository;
	@Resource
	private transient SiScolOptionBacRepository siScolOptionBacRepository;
	@Resource
	private transient SiScolSpecialiteBacRepository siScolSpecialiteBacRepository;
	@Resource
	private transient SiScolBacOptBacRepository siScolBacOptBacRepository;
	@Resource
	private transient SiScolBacSpeBacRepository siScolBacSpeBacRepository;

	private static Boolean launchBatchWithListOption = true;

	/**
	 * Batch complet de synchro siScol
	 * @param  batchHisto
	 * @throws SiScolException
	 */
	public void syncSiScol(final BatchHisto batchHisto) throws SiScolException {
		batchController.addDescription(batchHisto, "Lancement du batch siScol");
		if (siScolService == null) {
			return;
		}
//		batchController.addDescription(batchHisto, "Lancement synchronisation CatExoExt");
//		syncCatExoExt();
		batchController.addDescription(batchHisto, "Lancement synchronisation BacOuEqu");
		syncBacOuEqu();
		batchController.addDescription(batchHisto, "Lancement synchronisation OptionBac");
		syncOptionBac();
		batchController.addDescription(batchHisto, "Lancement synchronisation BacOptBac");
		syncBacOptBac();
		batchController.addDescription(batchHisto, "Lancement synchronisation SpecialiteBac");
		syncSpecialiteBac();
		batchController.addDescription(batchHisto, "Lancement synchronisation BacSpeBac");
		syncBacSpeBac();
		batchController.addDescription(batchHisto, "Lancement synchronisation Mention");
		syncMention();
		batchController.addDescription(batchHisto, "Lancement synchronisation CGE");
		syncCGE();
		batchController.addDescription(batchHisto, "Lancement synchronisation Utilisateurs");
		syncUtilisateurs();
		batchController.addDescription(batchHisto, "Lancement synchronisation Departement");
		syncDepartement();
		batchController.addDescription(batchHisto, "Lancement synchronisation Commune");
		syncCommune();
		batchController.addDescription(batchHisto, "Lancement synchronisation DipAutCur");
		syncDipAutCur();
		batchController.addDescription(batchHisto, "Lancement synchronisation Pays");
		syncPays();
		batchController.addDescription(batchHisto, "Lancement synchronisation Etablissement");
		syncEtablissement();
		batchController.addDescription(batchHisto, "Lancement synchronisation TypDiplome");
		syncTypDiplome();
		batchController.addDescription(batchHisto, "Lancement synchronisation TypResultat");
		syncTypResultat();
		batchController.addDescription(batchHisto, "Lancement synchronisation MentionNivBac");
		syncMentionNivBac();
		batchController.addDescription(batchHisto, "Lancement synchronisation ComBdi");
		syncComBdi();
		batchController.addDescription(batchHisto, "Lancement synchronisation AnneeUni");
		syncAnneeUni();
		batchController.addDescription(batchHisto, "Lancement synchronisation Version");
		syncVersion();
		batchController.addDescription(batchHisto, "Fin du batch siScol");
	}

	/**
	 * Synchronise les BacOuEqu
	 * @throws SiScolException
	 */
	private void syncBacOuEqu() throws SiScolException {
		final List<SiScolBacOuxEqu> listeSiScol = siScolService.getListSiScolBacOuxEqu();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolBacOuxEquRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(bac -> siScolBacOuxEquRepository.saveAndFlush(bac));
		}
		cacheController.reloadListeBacOuxEqu(true);
	}

	/**
	 * Synchronise les centres de gestion
	 * @throws SiScolException
	 */
	private void syncCGE() throws SiScolException {
		final List<SiScolCentreGestion> listeSiScol = siScolService.getListSiScolCentreGestion();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolCentreGestionRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(cge -> siScolCentreGestionRepository.saveAndFlush(cge));
		}
		cacheController.reloadListeCentreGestion(true);
	}

	/**
	 * Synchronise les communes
	 * @throws SiScolException
	 */
	private void syncCommune() throws SiScolException {
		final List<SiScolCommune> listeSiScol = siScolService.getListSiScolCommune();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolCommuneRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(commune -> siScolCommuneRepository.saveAndFlush(commune));
		}
	}

	/**
	 * Synchronise les departements
	 * @throws SiScolException
	 */
	private void syncDepartement() throws SiScolException {
		final List<SiScolDepartement> listeSiScol = siScolService.getListSiScolDepartement();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolDepartementRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(departement -> siScolDepartementRepository.saveAndFlush(departement));
		}
		cacheController.reloadListeDepartement(true);
	}

	/**
	 * Synchronise les DipAutCur
	 * @throws SiScolException
	 */
	private void syncDipAutCur() throws SiScolException {
		final List<SiScolDipAutCur> listeSiScol = siScolService.getListSiScolDipAutCur();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolDipAutCurRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(dipAutCur -> siScolDipAutCurRepository.saveAndFlush(dipAutCur));
		}
		cacheController.reloadListeDipAutCur(true);
	}

	/**
	 * Synchronise les etablissements
	 * @throws SiScolException
	 */
	private void syncEtablissement() throws SiScolException {
		final List<SiScolEtablissement> listeSiScol = siScolService.getListSiScolEtablissement();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolEtablissementRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(etablissement -> siScolEtablissementRepository.saveAndFlush(etablissement));
		}
	}

	/**
	 * Synchronise les mentions
	 * @throws SiScolException
	 */
	private void syncMention() throws SiScolException {
		final List<SiScolMention> listeSiScol = siScolService.getListSiScolMention();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolMentionRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(mention -> siScolMentionRepository.saveAndFlush(mention));
		}
		cacheController.reloadListeMention(true);
	}

	/**
	 * Synchronise les typResultats
	 * @throws SiScolException
	 */
	private void syncTypResultat() throws SiScolException {
		final List<SiScolTypResultat> listeSiScol = siScolService.getListSiScolTypResultat();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolTypResultatRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(typResultats -> siScolTypResultatRepository.saveAndFlush(typResultats));
		}
		cacheController.reloadListeTypeResultat(true);
	}

	/**
	 * Synchronise les mentions niv bac
	 * @throws SiScolException
	 */
	private void syncMentionNivBac() throws SiScolException {
		final List<SiScolMentionNivBac> listeSiScol = siScolService.getListSiScolMentionNivBac();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolMentionNivBacRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(mentionNivBac -> siScolMentionNivBacRepository.saveAndFlush(mentionNivBac));
		}
		cacheController.reloadListeMentionNivBac(true);
	}

	/**
	 * Synchronise les pays
	 * @throws SiScolException
	 */
	private void syncPays() throws SiScolException {
		final List<SiScolPays> listeSiScol = siScolService.getListSiScolPays();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolPaysRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(pays -> siScolPaysRepository.saveAndFlush(pays));
		}
		cacheController.reloadListePays(true);
	}

	/**
	 * Synchronise les types de diplome
	 * @throws SiScolException
	 */
	private void syncTypDiplome() throws SiScolException {
		final List<SiScolTypDiplome> listeSiScol = siScolService.getListSiScolTypDiplome();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolTypDiplomeRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(typDiplome -> siScolTypDiplomeRepository.saveAndFlush(typDiplome));
		}
		cacheController.reloadListeTypDiplome(true);
	}

	/**
	 * Synchronise les catégories exonération/extracommunautaire
	 * @throws SiScolException
	 */
//	private void syncCatExoExt() throws SiScolException {
//		final List<SiScolCatExoExt> listeSiScol = siScolService.getListCatExoExt();
//		if (listeSiScol == null) {
//			return;
//		}
//		if (launchBatchWithListOption) {
//			siScolCatExoExtRepository.save(listeSiScol);
//		} else {
//			listeSiScol.forEach(catExoExt -> siScolCatExoExtRepository.saveAndFlush(catExoExt));
//		}
//		cacheController.reloadListeCatExoExt(true);
//	}

	/**
	 * Synchronise les utilisateurs
	 * @throws SiScolException
	 */
	private void syncUtilisateurs() throws SiScolException {
		final List<SiScolUtilisateur> listeSiScol = siScolService.getListSiScolUtilisateur();
		if (listeSiScol == null) {
			return;
		}
		siScolUtilisateurRepository.deleteAllInBatch();

		/* Erreur de duplicate entry a toulouse et rennes */
		Exception ex = null;
		Integer i = 1;
		for (final SiScolUtilisateur utilisateur : listeSiScol) {
			utilisateur.setIdUti(i);
			try {
				siScolUtilisateurRepository.saveAndFlush(utilisateur);
				i++;
			} catch (final Exception e) {
				ex = e;
			}
		}
		if (ex != null) {
			logger.error("Erreur a l'insertion des utilisateurs", ex);
		}
	}

	/**
	 * Synchronise les combdi
	 * @throws SiScolException
	 */
	private void syncComBdi() throws SiScolException {
		final List<SiScolComBdi> listeSiScol = siScolService.getListSiScolComBdi();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolComBdiRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(comBdi -> siScolComBdiRepository.saveAndFlush(comBdi));
		}
	}

	/**
	 * Synchronise les annees universitaires
	 * @throws SiScolException
	 */
	private void syncAnneeUni() throws SiScolException {
		final List<SiScolAnneeUni> listeSiScol = siScolService.getListSiScolAnneeUni();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolAnneeUniRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(anneUni -> siScolAnneeUniRepository.saveAndFlush(anneUni));
		}
		cacheController.reloadListeAnneeUni(true);
	}

	/**
	 * Synchronise les options du bac
	 * @throws SiScolException
	 */
	private void syncOptionBac() throws SiScolException {
		final List<SiScolOptionBac> listeSiScol = siScolService.getListSiScolOptionBac();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolOptionBacRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(opt -> siScolOptionBacRepository.saveAndFlush(opt));
		}
		cacheController.reloadListeOptionBac(true);
	}

	/**
	 * Synchronise les specialités du bac
	 * @throws SiScolException
	 */
	private void syncSpecialiteBac() throws SiScolException {
		final List<SiScolSpecialiteBac> listeSiScol = siScolService.getListSiScolSpecialiteBac();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolSpecialiteBacRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(opt -> siScolSpecialiteBacRepository.saveAndFlush(opt));
		}
		cacheController.reloadListeSpecialiteBac(true);
	}

	/**
	 * Synchronise les relations bac/options
	 * @throws SiScolException
	 */
	private void syncBacOptBac() throws SiScolException {
		final List<SiScolBacOptBac> listeSiScol = siScolService.getListSiScolBacOptBac();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolBacOptBacRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(opt -> siScolBacOptBacRepository.saveAndFlush(opt));
		}
		cacheController.reloadListeBacOptBac(true);
	}

	/**
	 * Synchronise les relations bac/spécialités
	 * @throws SiScolException
	 */
	private void syncBacSpeBac() throws SiScolException {
		final List<SiScolBacSpeBac> listeSiScol = siScolService.getListSiScolBacSpeBac();
		if (listeSiScol == null) {
			return;
		}
		if (launchBatchWithListOption) {
			siScolBacSpeBacRepository.save(listeSiScol);
		} else {
			listeSiScol.forEach(opt -> siScolBacSpeBacRepository.saveAndFlush(opt));
		}
		cacheController.reloadListeBacSpeBac(true);
	}

	/**
	 * Synchronise la version apogée
	 * @throws SiScolException
	 */
	private void syncVersion() throws SiScolException {
		Version version = siScolService.getVersion();
		if (version != null) {
			version.setCodVersion(NomenclatureUtils.VERSION_SI_SCOL_COD);
			version.setDatVersion(LocalDateTime.now());
			version = versionRepository.save(version);
		}
		nomenclatureController.loadElementVersion(NomenclatureUtils.VERSION_SI_SCOL_COD, version);
	}

	/** Test de la connexion */
	public void testSiScolConnnexion() {
		try {
			final Version v = siScolService.getVersion();
			if (v != null) {
				Notification.show(applicationContext.getMessage("parametre.siscol.check.ok", new Object[] { v.getValVersion() }, UI.getCurrent().getLocale()));
			} else {
				Notification.show(applicationContext.getMessage("parametre.siscol.check.disable", null, UI.getCurrent().getLocale()));
			}
		} catch (final Exception e) {
			Notification.show(applicationContext.getMessage("parametre.siscol.check.ko", null, UI.getCurrent().getLocale()));
		}
	}

	/** Teste la connexion au WS Apogée */
	public void testWSSiScolConnnexion() {
		final InputWindow inputWindow = new InputWindow(applicationContext.getMessage("version.ws.message", null, UI.getCurrent().getLocale()),
			applicationContext.getMessage("version.ws.title", null, UI.getCurrent().getLocale()),
			false,
			15);
		inputWindow.addBtnOkListener(text -> {
			if (text instanceof String && !text.isEmpty()) {
				if (text != null) {
					try {
						WSIndividu ind;
						if (demoController.getDemoMode()) {
							ind = demoController.recupInfoEtudiant("0000000000");
						} else {
							ind = siScolService.getIndividu(text, null, null);
						}
						String ret = "Pas d'info";
						if (ind != null) {
							ret = "<u>Individu</u> : <br>" + ind
								+ "<br><br><u>Adresse</u> : <br>"
								+ ind.getAdresse()
								+ "<br><br><u>Bac</u> : <br>"
								+ ind.getBac()
								+ "<br><br><u>Cursus interne</u> : <br>"
								+ ind.getListCursusInterne();
						}

						UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("version.ws.result", null, UI.getCurrent().getLocale()), ret, 500, 70));
					} catch (final Exception e) {
						Notification.show(applicationContext.getMessage("version.ws.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					}
				}
			}
		});
		UI.getCurrent().addWindow(inputWindow);
	}

	/**
	 * Teste du WS d'info de fichiers
	 * @param codEtu
	 * @param codTpj
	 */
	public void testWSPJSiScolInfo(final String codEtu, final String codTpj) {
		try {
			if (urlWsPjApogee == null || urlWsPjApogee.equals("")) {
				Notification.show(applicationContext.getMessage("version.ws.pj.noparam", new Object[] { ConstanteUtils.WS_APOGEE_PJ_URL_SERVICE + ConstanteUtils.WS_APOGEE_SERVICE_SUFFIXE }, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}
			final WSPjInfo info = siScolService.getPjInfoFromApogee(null, codEtu, codTpj);
			String ret = "Pas d'info";
			if (info != null) {
				ret = "<u>PJ Information</u> : <br>" + info;
			}

			UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("version.ws.result", null, UI.getCurrent().getLocale()), ret, 500, 70));
		} catch (final Exception e) {
			Notification.show(applicationContext.getMessage("version.ws.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		}
	}

	/**
	 * Test de téléchargement de fichiers
	 * @param  codEtu
	 * @param  codTpj
	 * @return        le fichier
	 */
	public OnDemandFile testWSPJSiScolFile(final String codEtu, final String codTpj) {
		try {
			if (urlWsPjApogee == null || urlWsPjApogee.equals("")) {
				Notification.show(applicationContext.getMessage("version.ws.pj.noparam", new Object[] { ConstanteUtils.WS_APOGEE_PJ_URL_SERVICE + ConstanteUtils.WS_APOGEE_SERVICE_SUFFIXE }, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return null;
			}
			final WSPjInfo info = siScolService.getPjInfoFromApogee(null, codEtu, codTpj);
			if (info == null) {
				return null;
			}
			return new OnDemandFile(info.getNomFic(), siScolService.getPjFichierFromApogee(info.getCodAnu(), codEtu, codTpj));
		} catch (final Exception e) {
			Notification.show(applicationContext.getMessage("version.ws.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return null;
		}

	}
}
