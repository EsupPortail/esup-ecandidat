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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.rest.LimeSurveyRest;
import fr.univlorraine.ecandidat.entities.ecandidat.Batch;
import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Civilite;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.I18nTraduction;
import fr.univlorraine.ecandidat.entities.ecandidat.I18nTraductionPK;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.Message;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.entities.ecandidat.SchemaVersion;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraduction;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;
import fr.univlorraine.ecandidat.repositories.BatchHistoRepository;
import fr.univlorraine.ecandidat.repositories.BatchRepository;
import fr.univlorraine.ecandidat.repositories.CampagneRepository;
import fr.univlorraine.ecandidat.repositories.CiviliteRepository;
import fr.univlorraine.ecandidat.repositories.CommissionRepository;
import fr.univlorraine.ecandidat.repositories.DroitFonctionnaliteRepository;
import fr.univlorraine.ecandidat.repositories.DroitProfilRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.repositories.I18nRepository;
import fr.univlorraine.ecandidat.repositories.I18nTraductionRepository;
import fr.univlorraine.ecandidat.repositories.LangueRepository;
import fr.univlorraine.ecandidat.repositories.LoadBalancingReloadRepository;
import fr.univlorraine.ecandidat.repositories.MailRepository;
import fr.univlorraine.ecandidat.repositories.MessageRepository;
import fr.univlorraine.ecandidat.repositories.ParametreRepository;
import fr.univlorraine.ecandidat.repositories.SchemaVersionRepository;
import fr.univlorraine.ecandidat.repositories.TypeAvisRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionRepository;
import fr.univlorraine.ecandidat.repositories.TypeStatutPieceRepository;
import fr.univlorraine.ecandidat.repositories.TypeStatutRepository;
import fr.univlorraine.ecandidat.repositories.TypeTraductionRepository;
import fr.univlorraine.ecandidat.repositories.TypeTraitementRepository;
import fr.univlorraine.ecandidat.repositories.VersionRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.utils.migration.RealeaseVersion;
import gouv.education.apogee.commun.client.ws.AdministratifMetier.AdministratifMetierServiceInterfaceService;

/**
 * Gestion des nomenclatures
 * @author Kevin Hergalant
 */
@Component
public class NomenclatureController {

	private final Logger logger = LoggerFactory.getLogger(NomenclatureController.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient EntityManagerFactory entityManagerFactoryEcandidat;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient SiScolController siScolController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient DemoController demoController;
	@Resource
	private transient TypeAvisRepository typeAvisRepository;
	@Resource
	private transient TypeStatutPieceRepository typeStatutPieceRepository;
	@Resource
	private transient TypeTraitementRepository typeTraitementRepository;
	@Resource
	private transient BatchRepository batchRepository;
	@Resource
	private transient BatchHistoRepository batchHistoRepository;
	@Resource
	private transient LangueRepository langueRepository;
	@Resource
	private transient TypeTraductionRepository typeTraductionRepository;
	@Resource
	private transient DroitProfilRepository droitProfilRepository;
	@Resource
	private transient MailRepository mailRepository;
	@Resource
	private transient I18nRepository i18nRepository;
	@Resource
	private transient I18nTraductionRepository i18nTraductionRepository;
	@Resource
	private transient CampagneRepository campagneRepository;
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient CommissionRepository commissionRepository;
	@Resource
	private transient TypeDecisionRepository typeDecisionRepository;
	@Resource
	private transient TypeStatutRepository typeStatutRepository;
	@Resource
	private transient DroitFonctionnaliteRepository droitFonctionnaliteRepository;
	@Resource
	private transient ParametreRepository parametreRepository;
	@Resource
	private transient VersionRepository versionRepository;
	@Resource
	private transient SchemaVersionRepository schemaVersionRepository;
	@Resource
	private transient CiviliteRepository civiliteRepository;
	@Resource
	private transient MessageRepository messageRepository;
	@Resource
	private transient LoadBalancingReloadRepository loadBalancingReloadRepository;
	@Resource
	private transient LimeSurveyRest limeSurveyRest;
	@Resource
	private transient CandidatController candidatController;

	private final ConcurrentHashMap<String, Version> mapVersion = new ConcurrentHashMap<>();

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/* Variable d'envirronement */
	@Value("${app.version:}")
	private String appVersion;

	@Value("${siscol.default:}")
	private transient String siScolDefault;

	/**
	 * La version de la nomenclature
	 * @return la version
	 */
	public Version getNomenclatureVersionDb() {
		final Version versionNomenclature = versionRepository.findOne(NomenclatureUtils.VERSION_NOMENCLATURE_COD);
		if (versionNomenclature == null) {
			return new Version(NomenclatureUtils.VERSION_NOMENCLATURE_COD, NomenclatureUtils.VERSION_NO_VERSION_VAL);
		}
		return versionNomenclature;
	}

	/**
	 * La version courante de nomenclature
	 * @return la version courante
	 */
	public Version getNomenclatureVersionCourante() {
		return new Version(NomenclatureUtils.VERSION_NOMENCLATURE_COD, NomenclatureUtils.VERSION_NOMENCLATURE_VAL);
	}

	/**
	 * Savoir si on doit recharger les paramètres
	 * @return true si la nomenclature doit etre rechargee
	 */
	public Boolean isNomenclatureToReload() {
		final Version versionNomenclature = getNomenclatureVersionDb();
		if (versionNomenclature == null
			|| !versionNomenclature.getValVersion().equals(NomenclatureUtils.VERSION_NOMENCLATURE_VAL)) {
			return true;
		}
		return false;
	}

	/**
	 * Retourne un numéro de version
	 * @param  codVersion
	 * @return            la version
	 */
	private Version getVersion(final String codVersion) {
		return versionRepository.findOne(codVersion);
	}

	/**
	 * Retourne un numéro de version de la db
	 * @param  codVersion
	 * @return            la version de la bd
	 */
	private Version getDbVersion(final String codVersion) {
		final List<SchemaVersion> list = schemaVersionRepository.findFirst1BySuccessOrderByInstalledRankDesc(true);
		final Version v = new Version(NomenclatureUtils.VERSION_DB, null);
		if (list.size() > 0) {
			final SchemaVersion s = list.get(0);
			v.setDatVersion(s.getInstalledOn());
			v.setValVersion(s.getVersion());
		}
		return v;
	}

	/** Met à jour la nomenclature de eCandidat */
	public void majNomenclature() {
		final Locale locale = new Locale("fr");

		/* Met à jours les civilités */
		nomenclatureCivilites(locale);

		/* Met à jours les langues */
		nomenclatureLangues(locale);

		/* Met à jours les batchs */
		nomenclatureBatchs(locale);

		/* Met à jours tous les droits */
		nomenclatureDroits(locale);

		/* Met à jours tous types de traduction */
		nomenclatureTypesTrad(locale);

		/* Met à jours tous les messages */
		nomenclatureMessage(locale);

		/* Met à jours tous les types (Type d'avis, Type de traitement, Type de statut
		 * de dossier, Type de statut de piece, Type de traduction) */
		nomenclatureTypes(locale);

		/* Met à jours tous les types de décision et les mails liés */
		nomenclatureTypeDecs(locale);

		/* Met à jours tous les paramètres */
		nomenclatureParametres(locale);

		/* Met à jours tous les mails */
		nomenclatureMails(locale);

		/* La version de la nomenclature pour finir */
		nomenclatureVersion(new Version(NomenclatureUtils.VERSION_NOMENCLATURE_COD, NomenclatureUtils.VERSION_NOMENCLATURE_VAL));
	}

	/**
	 * Met à jours les batchs
	 * @param locale
	 */
	private void nomenclatureCivilites(final Locale locale) {
		/* Civilite */
		majCivilite(new Civilite(NomenclatureUtils.CIVILITE_M,
			applicationContext.getMessage("nomenclature.civilite.monsieur.lib", null, locale),
			NomenclatureUtils.CIVILITE_SISCOL_M, NomenclatureUtils.CIVILITE_SEXE_M));
		majCivilite(new Civilite(NomenclatureUtils.CIVILITE_F,
			applicationContext.getMessage("nomenclature.civilite.mme.lib", null, locale),
			NomenclatureUtils.CIVILITE_SISCOL_F, NomenclatureUtils.CIVILITE_SEXE_F));

	}

	/**
	 * Met à jours les batchs
	 * @param locale
	 */
	private void nomenclatureLangues(final Locale locale) {
		/* Langue */
		majLangue(new Langue(NomenclatureUtils.LANGUE_FR,
			applicationContext.getMessage("nomenclature.langue.fr", null, locale), true, true));
		majLangue(new Langue(NomenclatureUtils.LANGUE_EN,
			applicationContext.getMessage("nomenclature.langue.en", null, locale), false, false));
		majLangue(new Langue(NomenclatureUtils.LANGUE_ES,
			applicationContext.getMessage("nomenclature.langue.es", null, locale), false, false));
		majLangue(new Langue(NomenclatureUtils.LANGUE_DE,
			applicationContext.getMessage("nomenclature.langue.de", null, locale), false, false));
	}

	/**
	 * Met à jours les batchs
	 * @param locale
	 */
	private void nomenclatureBatchs(final Locale locale) {
		/* Batch */
		majBatch(new Batch(NomenclatureUtils.BATCH_SI_SCOL,
			applicationContext.getMessage("nomenclature.batch.apo.libelle", null, locale), 23, 00));
		majBatch(new Batch(NomenclatureUtils.BATCH_APP_EN_MAINT,
			applicationContext.getMessage("nomenclature.batch.maintenance", null, locale), 22, 55));
		majBatch(new Batch(NomenclatureUtils.BATCH_APP_EN_SERVICE,
			applicationContext.getMessage("nomenclature.batch.enservice", null, locale), 23, 30));
		majBatch(new Batch(NomenclatureUtils.BATCH_NETTOYAGE_CPT,
			applicationContext.getMessage("nomenclature.batch.cptmin", null, locale), 22, 30));
		majBatch(new Batch(NomenclatureUtils.BATCH_NETTOYAGE,
			applicationContext.getMessage("nomenclature.batch.netoyage.libelle", null, locale), true, true, true,
			true, true, true, true, 22, 00));
		majBatch(new Batch(NomenclatureUtils.BATCH_ARCHIVAGE,
			applicationContext.getMessage("nomenclature.batch.archivage", null, locale), 22, 30));

		majBatch(new Batch(NomenclatureUtils.BATCH_SYNCHRO_LIMESURVEY,
			applicationContext.getMessage("nomenclature.batch.limesurvey", null, locale), 22, 30));
		majBatch(new Batch(NomenclatureUtils.BATCH_DESTRUCT_DOSSIER,
			applicationContext.getMessage("nomenclature.batch.destruct", null, locale), 22, 30));
		majBatch(new Batch(NomenclatureUtils.BATCH_ASYNC_OPI,
			applicationContext.getMessage("nomenclature.batch.async.opi", null, locale), 22, 30));
		majBatch(new Batch(NomenclatureUtils.BATCH_ASYNC_OPI_PJ,
			applicationContext.getMessage("nomenclature.batch.async.opi.pj", null, locale), 22, 45));
		majBatch(new Batch(NomenclatureUtils.BATCH_DESTRUCT_HISTO,
			applicationContext.getMessage("nomenclature.batch.keep.histo", null, locale), true, true, true, true,
			true, true, true, 23, 00));
		majBatch(new Batch(NomenclatureUtils.BATCH_DESIST_AUTO,
			applicationContext.getMessage("nomenclature.batch.desist.auto", null, locale), true, true, true, true,
			true, true, true, 23, 15));
		majBatch(new Batch(NomenclatureUtils.BATCH_RELANCE_FAVO,
			applicationContext.getMessage("nomenclature.batch.relance.favo", null, locale), true, true, true, true,
			true, true, true, 23, 30));
		majBatch(new Batch(NomenclatureUtils.BATCH_CALCUL_RANG_LC,
			applicationContext.getMessage("nomenclature.batch.calcul.rang.lc", null, locale), true, true, true,
			true, true, true, true, 23, 45));
		majBatch(new Batch(NomenclatureUtils.BATCH_MAJ_GESTIONNAIRE,
			applicationContext.getMessage("nomenclature.batch.maj.gest", null, locale), 21, 30));

		if (demoController.getDemoMode()) {
			majBatch(new Batch(NomenclatureUtils.BATCH_DEMO,
				applicationContext.getMessage("nomenclature.batch.demo.libelle", null, locale), true, true, true,
				true, true, true, true, 23, 55));
		}
	}

	/**
	 * Met à jours les droits
	 * @param locale
	 */
	private void nomenclatureDroits(final Locale locale) {
		/* DroitProfil */
		majDroitProfil(new DroitProfil(NomenclatureUtils.DROIT_PROFIL_ADMIN,
			applicationContext.getMessage("nomenclature.droitProfil.admin", null, locale),
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
			NomenclatureUtils.TYP_DROIT_PROFIL_ADM, false, true));
		majDroitProfil(new DroitProfil(NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE,
			applicationContext.getMessage("nomenclature.droitProfil.scolcentrale", null, locale),
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
			NomenclatureUtils.TYP_DROIT_PROFIL_ADM, false, true));
		final DroitProfil profilCtrCand = majDroitProfil(
			new DroitProfil(NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE,
				applicationContext.getMessage("nomenclature.droitProfil.centrecand", null, locale),
				NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
				NomenclatureUtils.TYP_DROIT_PROFIL_GESTIONNAIRE, true, true));
		final DroitProfil profilCommission = majDroitProfil(new DroitProfil(NomenclatureUtils.DROIT_PROFIL_COMMISSION,
			applicationContext.getMessage("nomenclature.droitProfil.commission", null, locale),
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
			NomenclatureUtils.TYP_DROIT_PROFIL_COMMISSION, true, true));
		majDroitProfil(new DroitProfil(NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT,
			applicationContext.getMessage("nomenclature.droitProfil.gestCand", null, locale),
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
			NomenclatureUtils.TYP_DROIT_PROFIL_GEST_CANDIDAT, false, true));
		majDroitProfil(new DroitProfil(NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT_LS,
			applicationContext.getMessage("nomenclature.droitProfil.gestCandLs", null, locale),
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
			NomenclatureUtils.TYP_DROIT_PROFIL_GEST_CANDIDAT_LS, false, true));

		/* Fonctionnalites */

		/* Accès aux menus */
		majDroitProfilFonc(profilCtrCand, profilCommission,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_PARAM,
				applicationContext.getMessage("nomenclature.fonctionnalite.param.lib", null, locale), true, 1,
				false));
		majDroitProfilFonc(profilCtrCand, null, new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_STATS,
			applicationContext.getMessage("nomenclature.fonctionnalite.stats.lib", null, locale), 2, false));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_COMMISSION,
				applicationContext.getMessage("nomenclature.fonctionnalite.gestCommission.lib", null, locale),
				3, false));
		majDroitProfilFonc(profilCtrCand, null, new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_PJ,
			applicationContext.getMessage("nomenclature.fonctionnalite.gestPj.lib", null, locale), 4, false));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FORMULAIRE,
				applicationContext.getMessage("nomenclature.fonctionnalite.gestFormulaire.lib", null, locale),
				5, false));
		majDroitProfilFonc(profilCtrCand, null, new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_QUESTION,
			applicationContext.getMessage("nomenclature.fonctionnalite.gestQuestion.lib", null, locale), 6, false));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FORMATION,
				applicationContext.getMessage("nomenclature.fonctionnalite.gestFormation.lib", null, locale), 7,
				false));
		majDroitProfilFonc(profilCtrCand, profilCommission,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE,
				applicationContext.getMessage("nomenclature.fonctionnalite.gestCand.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.gestCand.lib", null, locale), true,
				8, false));

		/* Accès aux menus surchargés Scol centrale */
		majDroitProfilFonc(profilCtrCand, null, new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_PARAM_CC,
			applicationContext.getMessage("nomenclature.fonctionnalite.gestParamDec.lib", null, locale), 9, false));

		/* Accès aux actions */
		majDroitProfilFonc(profilCtrCand, profilCommission,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FENETRE_CAND,
				applicationContext.getMessage("nomenclature.fonctionnalite.gestFenetreCand.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.gestFenetreCand.lib", null, locale),
				true, 10, true));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_POST_IT,
				applicationContext.getMessage("nomenclature.fonctionnalite.gestPostIt.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.gestPostIt.lic", null, locale), true,
				11, true));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_EDIT_TYPTRAIT,
				applicationContext.getMessage("nomenclature.fonctionnalite.editTypTrait.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.editTypTrait.lic", null, locale),
				true, 12, true));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_VALID_TYPTRAIT,
				applicationContext.getMessage("nomenclature.fonctionnalite.validTypTrait.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.validTypTrait.lic", null, locale),
				true, 13, true));
		majDroitProfilFonc(profilCtrCand, null, new DroitFonctionnalite(
			NomenclatureUtils.FONCTIONNALITE_EDIT_STATUT_DOSSIER,
			applicationContext.getMessage("nomenclature.fonctionnalite.editStatutDossier.lib", null, locale),
			applicationContext.getMessage("nomenclature.fonctionnalite.editStatutDossier.lic", null, locale), true,
			14, true));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_EDIT_AVIS,
				applicationContext.getMessage("nomenclature.fonctionnalite.editAvis.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.editAvis.lic", null, locale), true,
				15, true));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_VALID_AVIS,
				applicationContext.getMessage("nomenclature.fonctionnalite.validAvis.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.validAvis.lic", null, locale), true,
				16, true));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_VISU_HISTO_AVIS,
				applicationContext.getMessage("nomenclature.fonctionnalite.visuHistoAvis.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.visuHistoAvis.lic", null, locale),
				true, 17, true));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_NUM_OPI,
				applicationContext.getMessage("nomenclature.fonctionnalite.editCodOpi.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.editCodOpi.lic", null, locale), true,
				18, true));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_TAG,
				applicationContext.getMessage("nomenclature.fonctionnalite.editTag.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.editTag.lic", null, locale), true,
				19, true));
		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_DAT_CONFIRM,
				applicationContext.getMessage("nomenclature.fonctionnalite.editDatConfirm.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.editDatConfirm.lic", null, locale),
				true, 20, true));

		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_DAT_RETOUR,
				applicationContext.getMessage("nomenclature.fonctionnalite.editDatRetour.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.editDatRetour.lic", null, locale),
				true, 21, true));

		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_REGIME,
				applicationContext.getMessage("nomenclature.fonctionnalite.editRegime.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.editRegime.lic", null, locale),
				true, 22, true));

		majDroitProfilFonc(profilCtrCand, null,
			new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_MONTANT,
				applicationContext.getMessage("nomenclature.fonctionnalite.editMontant.lib", null, locale),
				applicationContext.getMessage("nomenclature.fonctionnalite.editMontant.lic", null, locale),
				true, 23, true));
	}

	/**
	 * Met à jours tous messages
	 * @param locale
	 */
	private void nomenclatureMessage(final Locale locale) {
		/* Messages */
		majMessage(
			new Message(NomenclatureUtils.COD_MSG_ACCUEIL,
				applicationContext.getMessage("nomenclature.message.accueil.lib", null, locale)),
			applicationContext.getMessage("nomenclature.message.accueil.default", null, locale));
		majMessage(
			new Message(NomenclatureUtils.COD_MSG_MAINTENANCE,
				applicationContext.getMessage("nomenclature.message.maintenance.lib", null, locale)),
			applicationContext.getMessage("nomenclature.message.maintenance.default", null, locale));
	}

	/**
	 * Met à jours tous les types de décision et les mails liés
	 * @param locale
	 */
	private void nomenclatureTypeDecs(final Locale locale) {
		/* Les mail de decision */
		final Mail mailDecisionFav = majMail(
			new Mail(NomenclatureUtils.MAIL_DEC_FAVORABLE,
				applicationContext.getMessage("nomenclature.mail.decision.favorable", null, locale), true, true,
				NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
				new TypeAvis(NomenclatureUtils.TYP_AVIS_FAV)),
			applicationContext.getMessage("nomenclature.mail.decision.favorable.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.decision.favorable.content", null, locale));
		final Mail mailDecisionDef = majMail(
			new Mail(NomenclatureUtils.MAIL_DEC_DEFAVORABLE,
				applicationContext.getMessage("nomenclature.mail.decision.defavorable", null, locale), true,
				true, NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
				new TypeAvis(NomenclatureUtils.TYP_AVIS_DEF)),
			applicationContext.getMessage("nomenclature.mail.decision.defavorable.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.decision.defavorable.content", null, locale));
		final Mail mailDecisionListAtt = majMail(
			new Mail(NomenclatureUtils.MAIL_DEC_LISTE_ATT,
				applicationContext.getMessage("nomenclature.mail.decision.listeAtt", null, locale), true, true,
				NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
				new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_ATTENTE)),
			applicationContext.getMessage("nomenclature.mail.decision.listeAtt.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.decision.listeAtt.content", null, locale));
		final Mail mailDecisionListeComp = majMail(
			new Mail(NomenclatureUtils.MAIL_DEC_LISTE_COMP,
				applicationContext.getMessage("nomenclature.mail.decision.listeComp", null, locale), true, true,
				NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
				new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_COMP)),
			applicationContext.getMessage("nomenclature.mail.decision.listeComp.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.decision.listeComp.content", null, locale));
		final Mail mailDecisionPres = majMail(
			new Mail(NomenclatureUtils.MAIL_DEC_PRESELECTION,
				applicationContext.getMessage("nomenclature.mail.decision.preselection", null, locale), true,
				true, NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
				new TypeAvis(NomenclatureUtils.TYP_AVIS_PRESELECTION)),
			applicationContext.getMessage("nomenclature.mail.decision.preselection.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.decision.preselection.content", null, locale));

		/* Type de decision */
		majTypeDec(new TypeDecision(NomenclatureUtils.TYP_DEC_FAVORABLE,
			applicationContext.getMessage("nomenclature.typDec.favorable", null, locale), true, true, true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
			new TypeAvis(NomenclatureUtils.TYP_AVIS_FAV), mailDecisionFav));
		majTypeDec(new TypeDecision(NomenclatureUtils.TYP_DEC_DEFAVORABLE,
			applicationContext.getMessage("nomenclature.typDec.defavorable", null, locale), true, true, false, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
			new TypeAvis(NomenclatureUtils.TYP_AVIS_DEF), mailDecisionDef));
		majTypeDec(new TypeDecision(NomenclatureUtils.TYP_DEC_LISTE_ATT,
			applicationContext.getMessage("nomenclature.typDec.listeAtt", null, locale), true, false, false, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
			new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_ATTENTE), mailDecisionListAtt));
		majTypeDec(new TypeDecision(NomenclatureUtils.TYP_DEC_LISTE_COMP,
			applicationContext.getMessage("nomenclature.typDec.listeComp", null, locale), true, false, false, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
			new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_COMP), mailDecisionListeComp));
		majTypeDec(new TypeDecision(NomenclatureUtils.TYP_DEC_PRESELECTION,
			applicationContext.getMessage("nomenclature.typDec.preselection", null, locale), true, false, false,
			true, NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
			new TypeAvis(NomenclatureUtils.TYP_AVIS_PRESELECTION), mailDecisionPres));
	}

	/**
	 * Met à jours tous les types (Type d'avis, Type de traitement, Type de statut
	 * de dossier, Type de statut de piece)
	 */
	private void nomenclatureTypes(final Locale locale) {
		/* TypeAvis */
		typeAvisRepository.saveAndFlush(new TypeAvis(NomenclatureUtils.TYP_AVIS_FAV,
			applicationContext.getMessage("nomenclature.typavis.fa", null, locale)));
		typeAvisRepository.saveAndFlush(new TypeAvis(NomenclatureUtils.TYP_AVIS_DEF,
			applicationContext.getMessage("nomenclature.typavis.de", null, locale)));
		typeAvisRepository.saveAndFlush(new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_COMP,
			applicationContext.getMessage("nomenclature.typavis.lc", null, locale)));
		typeAvisRepository.saveAndFlush(new TypeAvis(NomenclatureUtils.TYP_AVIS_LISTE_ATTENTE,
			applicationContext.getMessage("nomenclature.typavis.la", null, locale)));
		typeAvisRepository.saveAndFlush(new TypeAvis(NomenclatureUtils.TYP_AVIS_PRESELECTION,
			applicationContext.getMessage("nomenclature.typavis.pr", null, locale)));

		/* Type de traitement */
		majTypeTraitement(new TypeTraitement(NomenclatureUtils.TYP_TRAIT_AC,
			applicationContext.getMessage("nomenclature.typtrait.ac", null, locale), true));
		majTypeTraitement(new TypeTraitement(NomenclatureUtils.TYP_TRAIT_AD,
			applicationContext.getMessage("nomenclature.typtrait.ad", null, locale), true));
		majTypeTraitement(new TypeTraitement(NomenclatureUtils.TYP_TRAIT_AT,
			applicationContext.getMessage("nomenclature.typtrait.at", null, locale), false));

		/* Type de statut de dossier */
		majTypeStatut(new TypeStatut(NomenclatureUtils.TYPE_STATUT_ATT,
			applicationContext.getMessage("nomenclature.typeStatut.attente", null, locale)));
		majTypeStatut(new TypeStatut(NomenclatureUtils.TYPE_STATUT_REC,
			applicationContext.getMessage("nomenclature.typeStatut.recept", null, locale)));
		majTypeStatut(new TypeStatut(NomenclatureUtils.TYPE_STATUT_INC,
			applicationContext.getMessage("nomenclature.typeStatut.incomplet", null, locale)));
		majTypeStatut(new TypeStatut(NomenclatureUtils.TYPE_STATUT_COM,
			applicationContext.getMessage("nomenclature.typeStatut.complet", null, locale)));

		/* Type de statut de piece */
		majTypeStatutPiece(new TypeStatutPiece(NomenclatureUtils.TYP_STATUT_PIECE_REFUSE,
			applicationContext.getMessage("nomenclature.typstatutpiece.ref", null, locale)));
		majTypeStatutPiece(new TypeStatutPiece(NomenclatureUtils.TYP_STATUT_PIECE_TRANSMIS,
			applicationContext.getMessage("nomenclature.typstatutpiece.tra", null, locale)));
		majTypeStatutPiece(new TypeStatutPiece(NomenclatureUtils.TYP_STATUT_PIECE_VALIDE,
			applicationContext.getMessage("nomenclature.typstatutpiece.val", null, locale)));
		majTypeStatutPiece(new TypeStatutPiece(NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE,
			applicationContext.getMessage("nomenclature.typstatutpiece.ate", null, locale)));
		majTypeStatutPiece(new TypeStatutPiece(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE,
			applicationContext.getMessage("nomenclature.typstatutpiece.nonconc", null, locale)));
	}

	/**
	 * @param Met à jours les types de traduction
	 */
	private void nomenclatureTypesTrad(final Locale locale) {
		/* TypeTraduction */
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_FORM_LIB,
			applicationContext.getMessage("nomenclature.typtrad.formLib", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_FORM_URL,
			applicationContext.getMessage("nomenclature.typtrad.formUrl", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_MAIL_SUJET,
			applicationContext.getMessage("nomenclature.typtrad.mailSujet", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_MAIL_CORPS,
			applicationContext.getMessage("nomenclature.typtrad.mailCorps", null, locale), 5000));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_MOTIV_LIB,
			applicationContext.getMessage("nomenclature.typtrad.motivLib", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_PJ_LIB,
			applicationContext.getMessage("nomenclature.typtrad.pjLib", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_QUESTION_LIB,
			applicationContext.getMessage("nomenclature.typtrad.questionLib", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_TYP_DEC_LIB,
			applicationContext.getMessage("nomenclature.typtrad.typDecLib", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_TYP_TRAIT_LIB,
			applicationContext.getMessage("nomenclature.typtrad.typTraitLib", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_TYP_STATUT,
			applicationContext.getMessage("nomenclature.typtrad.typStatut", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_TYP_STATUT_PIECE,
			applicationContext.getMessage("nomenclature.typtrad.typStatutPiece", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_FAQ_QUESTION,
			applicationContext.getMessage("nomenclature.typtrad.faq.question", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_FAQ_REPONSE,
			applicationContext.getMessage("nomenclature.typtrad.faq.reponse", null, locale), 5000));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_MSG_VAL,
			applicationContext.getMessage("nomenclature.typtrad.msg.valeur", null, locale), 5000));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_CAMP_LIB,
			applicationContext.getMessage("nomenclature.typtrad.campLib", null, locale), 500));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_FORM_INFO_COMP,
			applicationContext.getMessage("nomenclature.typtrad.formInfoComp", null, locale), 5000));
		typeTraductionRepository.saveAndFlush(new TypeTraduction(NomenclatureUtils.TYP_TRAD_COMM_COMMENT_RETOUR,
			applicationContext.getMessage("nomenclature.typtrad.commCommentRetour", null, locale), 5000));
	}

	/**
	 * Met à jours tous les paramètres
	 */
	private void nomenclatureParametres(final Locale locale) {
		/* Paramètres candidature */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDATURE_NB_VOEUX_MAX,
			applicationContext.getMessage("parametrage.codParam.nbVoeuxMax", null, locale), "20",
			NomenclatureUtils.TYP_PARAM_INTEGER, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDATURE_NB_VOEUX_MAX_IS_ETAB,
			applicationContext.getMessage("parametrage.codParam.nbVoeuxMaxIsEtab", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDATURE_IS_BLOC_TRANS_FORM,
			applicationContext.getMessage("parametrage.codParam.isBlocTransForm", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_DOWNLOAD_IS_LETTRE_ADM_APRES_CONFIRM,
			applicationContext.getMessage("parametrage.codParam.downloadLettreAfterRep", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		/* Paramètres scol centrale */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SCOL_NB_JOUR_ARCHIVAGE,
			applicationContext.getMessage("parametrage.codParam.nbJourArchivage", null, locale), "365",
			NomenclatureUtils.TYP_PARAM_INTEGER, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SCOL_IS_COD_SISCOL_OBLI,
			applicationContext.getMessage("parametrage.codParam.formCodSiScolOblig", null, locale),
			ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SCOL_IS_APPEL,
			applicationContext.getMessage("parametrage.codParam.appel", null, locale),
			ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SCOL_SISCOL_COD_SANS_BAC,
			applicationContext.getMessage("parametrage.codParam.siScolCodSansBac", null, locale), "",
			NomenclatureUtils.TYP_PARAM_STRING + "(6)", true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SCOL_GESTION_CANDIDAT_COMM,
			applicationContext.getMessage("parametrage.codParam.gestionCandidatComm", null, locale),
			NomenclatureUtils.GEST_CANDIDATURE_READ, NomenclatureUtils.TYP_PARAM_STRING + "(1)", false, false));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SCOL_GESTION_CANDIDAT_CTR_CAND,
			applicationContext.getMessage("parametrage.codParam.gestionCandidatCtrCand", null, locale),
			NomenclatureUtils.GEST_CANDIDATURE_WRITE, NomenclatureUtils.TYP_PARAM_STRING + "(1)", false, false));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SCOL_IS_PARAM_CC_DECISION,
			applicationContext.getMessage("parametrage.codParam.utiliseParamCCDecision", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SCOL_NB_JOUR_RELANCE_FAVO,
			applicationContext.getMessage("parametrage.codParam.nbJourRelanceFavo", null, locale), "2",
			NomenclatureUtils.TYP_PARAM_INTEGER, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SCOL_IS_STATUT_ATT_WHEN_CHANGE_TT,
			applicationContext.getMessage("parametrage.codParam.isStatutAttWhenChangeTT", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		/* Paramètres candidat */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDAT_NB_JOUR_KEEP_CPT_MIN,
			applicationContext.getMessage("parametrage.codParam.nbJourKeepCptMin", null, locale), "5",
			NomenclatureUtils.TYP_PARAM_INTEGER, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDAT_PREFIXE_NUM_DOSS,
			applicationContext.getMessage("parametrage.codParam.prefixeNumDossCpt", null, locale), "",
			NomenclatureUtils.TYP_PARAM_STRING + "(2)", false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDAT_IS_INE_OBLI_FR,
			applicationContext.getMessage("parametrage.codParam.ineObligatoireFr", null, locale),
			ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDAT_IS_GET_CURSUS_INTERNE,
			applicationContext.getMessage("parametrage.codParam.isGetCursusInterne", null, locale),
			ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDAT_IS_UTILISE_SYNCHRO_INE,
			applicationContext.getMessage("parametrage.codParam.utiliseSynchroIne", null, locale),
			getIsEnableSyncByINEOld(), NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDAT_IS_GET_SISCOL_PJ,
			applicationContext.getMessage("parametrage.codParam.utiliseSiScolPj", null, locale),
			ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDAT_IS_MDP_CONNECT_CAS,
			applicationContext.getMessage("parametrage.codParam.isMdpConnectCas", null, locale),
			ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDAT_IS_COMPTE_EXTERNE_AUT,
			applicationContext.getMessage("parametrage.codParam.isCompteExterneAut", null, locale),
			ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_CANDIDAT_NB_HEURE_LIEN_MDP_VALID,
			applicationContext.getMessage("parametrage.codParam.nbHeureLienMdpValid", null, locale),
			"2", NomenclatureUtils.TYP_PARAM_INTEGER, false, true));

		/* Paramètres OPI */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_OPI_IS_UTILISE,
			applicationContext.getMessage("parametrage.codParam.utiliseOpi", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_OPI_IS_UTILISE_PJ,
			applicationContext.getMessage("parametrage.codParam.utiliseOpiPj", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_OPI_PREFIXE,
			applicationContext.getMessage("parametrage.codParam.prefixeOpi", null, locale), "EC",
			NomenclatureUtils.TYP_PARAM_STRING + "(2)", false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_OPI_IS_UTILISE_ADR,
			applicationContext.getMessage("parametrage.codParam.utiliseOpiAdr", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_OPI_IS_IMMEDIAT,
			applicationContext.getMessage("parametrage.codParam.opi.fil.eau", null, locale),
			ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_OPI_NB_BATCH_MAX,
			applicationContext.getMessage("parametrage.codParam.nbOpiBatch", null, locale), "0",
			NomenclatureUtils.TYP_PARAM_INTEGER, false, true));

		/* Paramètres Tech */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_TECH_IS_UTILISE_DEMAT,
			applicationContext.getMessage("parametrage.codParam.utiliseDemat", null, locale),
			ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_TECH_FILE_MAX_SIZE,
			applicationContext.getMessage("parametrage.codParam.file.maxsize", null, locale), "2",
			NomenclatureUtils.TYP_PARAM_INTEGER, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_TECH_IS_MAINTENANCE,
			applicationContext.getMessage("parametrage.codParam.maintenance", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, false));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_TECH_NB_JOUR_KEEP_HISTO_BATCH,
			applicationContext.getMessage("parametrage.codParam.nbJourKeepHistoBatch", null, locale), "30",
			NomenclatureUtils.TYP_PARAM_INTEGER, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_TECH_IS_DEMAT_MAINTENANCE,
			applicationContext.getMessage("parametrage.codParam.isDematMaintenance", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));

		/* Paramètres SVA */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SVA_ALERT_DAT,
			applicationContext.getMessage("parametrage.codParam.alertSvaDat", null, locale),
			NomenclatureUtils.CAND_DAT_NO_DAT, NomenclatureUtils.TYP_PARAM_STRING + "(3)", false, false));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SVA_ALERT_DEFINITIF,
			applicationContext.getMessage("parametrage.codParam.alertSvaDefinitif", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, false));

		/* Paramètres Gestionnaires */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_GEST_IS_UTILISE_BLOCAGE_MASSE,
			applicationContext.getMessage("parametrage.codParam.utiliseBlocageAvisMasse", null, locale),
			ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_GEST_IS_EXPORT_BLOC_NOTE,
			applicationContext.getMessage("parametrage.codParam.isExportBlocNote", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_GEST_IS_WARNING_CAND_SELECT,
			applicationContext.getMessage("parametrage.codParam.isWarningCandSelect", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		/* Paramètres Téléchargement */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_DOWNLOAD_MULTIPLE_NB_MAX,
			applicationContext.getMessage("parametrage.codParam.nbDossierDownloadMax", null, locale), "1",
			NomenclatureUtils.TYP_PARAM_INTEGER, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_DOWNLOAD_IS_ADD_SISCOL_PJ,
			applicationContext.getMessage("parametrage.codParam.isAddSiScolPjDossier", null, locale),
			getIsEnableAddPJApogeeDossierOld(), NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_DOWNLOAD_MULTIPLE_IS_ADD_PJ,
			applicationContext.getMessage("parametrage.codParam.isDownloadMultipleAddPj", null, locale),
			getIsDownloadMultipleAddPjOld(), NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_DOWNLOAD_MULTIPLE_MODE,
			applicationContext.getMessage("parametrage.codParam.modeDownloadMultiple", null, locale),
			getDownloadMultipleModeOld(), NomenclatureUtils.TYP_PARAM_STRING, false, true,
			NomenclatureUtils.PARAM_MODE_DOWNLOAD_MULTIPLE_REGEX));

		/* Paramètres Liste comp. */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_LC_IS_CALCUL_RANG_REEL,
			applicationContext.getMessage("parametrage.codParam.isCalculRangReelLc", null, locale),
			ConstanteUtils.TYP_BOOLEAN_YES, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));

		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_LC_MODE_AFFICHAGE_RANG,
			applicationContext.getMessage("parametrage.codParam.modeAffichageRangLc", null, locale),
			ConstanteUtils.PARAM_MODE_AFFICHAGE_RANG_SAISI, NomenclatureUtils.TYP_PARAM_STRING, true, true,
			NomenclatureUtils.PARAM_MODE_AFFICHAGE_RANG_REGEX));

		/* Type de formation */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_SCOL_MODE_TYPE_FORMATION,
			applicationContext.getMessage("parametrage.codParam.modeTypeFormation", null, locale),
			ConstanteUtils.PARAM_MODE_TYPE_FORMATION_TYPE_DIP, NomenclatureUtils.TYP_PARAM_STRING, false, true,
			NomenclatureUtils.PARAM_MODE_TYPE_FORMATION_REGEX));

		/* Bloque le téléchargement/envoie mail lettre au niveau établissement */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_TECH_IS_BLOC_LETTRE,
			applicationContext.getMessage("parametrage.codParam.isBlocLettre", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));

		/* Si la saisie du régime et du statut est disponible aux gestionnaires */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_GEST_IS_UTILISE_REG_STU,
			applicationContext.getMessage("parametrage.codParam.isUtiliseRegStu", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, true, true));

		/* Bloque le téléchargement/envoie mail lettre au niveau établissement */
		majParametre(new Parametre(NomenclatureUtils.COD_PARAM_TECH_IS_INSCRIPTION_USER,
			applicationContext.getMessage("parametrage.codParam.inscriptionUser", null, locale),
			ConstanteUtils.TYP_BOOLEAN_NO, NomenclatureUtils.TYP_PARAM_BOOLEAN, false, true));
	}

	/**
	 * Met à jours tous les mails
	 */
	private void nomenclatureMails(final Locale locale) {
		/* Les mail de statut de dossier */
		majMail(new Mail(NomenclatureUtils.MAIL_STATUT_AT,
			applicationContext.getMessage("nomenclature.mail.statut.attente", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.statut.attente.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.statut.attente.content", null, locale));
		majMail(new Mail(NomenclatureUtils.MAIL_STATUT_RE,
			applicationContext.getMessage("nomenclature.mail.statut.recept", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.statut.recept.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.statut.recept.content", null, locale));
		majMail(new Mail(NomenclatureUtils.MAIL_STATUT_IN,
			applicationContext.getMessage("nomenclature.mail.statut.incomplet", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.statut.incomplet.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.statut.incomplet.content", null, locale));
		majMail(new Mail(NomenclatureUtils.MAIL_STATUT_CO,
			applicationContext.getMessage("nomenclature.mail.statut.complet", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.statut.complet.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.statut.complet.content", null, locale));

		/* Mail compte a minima */
		majMail(new Mail(NomenclatureUtils.MAIL_CPT_MIN,
			applicationContext.getMessage("nomenclature.mail.cptMin", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.cptMin.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.cptMin.content", null, locale));

		/* Mail de modification du mot de passe */
		majMail(new Mail(NomenclatureUtils.MAIL_CPT_MIN_MDP_OUBLIE,
			applicationContext.getMessage("nomenclature.mail.pwdOublie", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.pwdOublie.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.pwdOublie.content", null, locale));

		/* Mail de modification du mot de passe */
		majMail(new Mail(NomenclatureUtils.MAIL_CPT_MIN_LIEN_VALID_OUBLIE,
			applicationContext.getMessage("nomenclature.mail.lienValidOublie", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.lienValidOublie.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.lienValidOublie.content", null, locale));

		/* Mail modif du mail du cptMin */
		majMail(new Mail(NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL,
			applicationContext.getMessage("nomenclature.mail.modifmail", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.modifmail.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.modifmail.content", null, locale));

		/* Mail de candidature */
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE,
			applicationContext.getMessage("nomenclature.mail.candidature", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.candidature.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.candidature.content", null, locale));

		/* Mail proposition */
		majMail(new Mail(NomenclatureUtils.MAIL_COMMISSION_ALERT_PROPOSITION,
			applicationContext.getMessage("nomenclature.mail.commission.prop.candidature", null, locale), true,
			true, NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.commission.prop.candidature.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.commission.prop.candidature.content", null, locale));

		/* Mail d'annulation de candidature */
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_ANNULATION,
			applicationContext.getMessage("nomenclature.mail.annul.candidature", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.annul.candidature.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.annul.candidature.content", null, locale));

		/* Mail d'annulation de candidature pour la commission */
		majMail(new Mail(NomenclatureUtils.MAIL_COMMISSION_ALERT_ANNULATION,
			applicationContext.getMessage("nomenclature.mail.commission.annul.candidature", null, locale), true,
			true, NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.commission.annul.candidature.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.commission.annul.candidature.content", null, locale));

		/* Mail de notification pour la commission lorsqu'un dossier a été transmis */
		majMail(new Mail(NomenclatureUtils.MAIL_COMMISSION_ALERT_TRANSMISSION,
			applicationContext.getMessage("nomenclature.mail.commission.trans.candidature", null, locale), true,
			true, NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.commission.trans.candidature.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.commission.trans.candidature.content", null, locale));

		/* Mail de confirmation de candidature */
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_CONFIRM,
			applicationContext.getMessage("nomenclature.mail.confirm.candidature", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.confirm.candidature.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.confirm.candidature.content", null, locale));

		/* Mail de desistement de candidature */
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_DESIST,
			applicationContext.getMessage("nomenclature.mail.desist.candidature", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.desist.candidature.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.desist.candidature.content", null, locale));

		/* Mail de desistement de candidature */
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_DESIST_AUTO,
			applicationContext.getMessage("nomenclature.mail.desistAuto.candidature", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.desistAuto.candidature.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.desistAuto.candidature.content", null, locale));

		/* Mail de modification de code OPI */
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_MODIF_COD_OPI,
			applicationContext.getMessage("nomenclature.mail.modif.opi", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.modif.opi.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.modif.opi.content", null, locale));

		/* Mail type de traitement AD */
		majMail(new Mail(NomenclatureUtils.MAIL_TYPE_TRAIT_AD,
			applicationContext.getMessage("nomenclature.mail.typetrait.ad", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.typetrait.ad.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.typetrait.ad.content", null, locale));

		/* Mail type de traitement AC */
		majMail(new Mail(NomenclatureUtils.MAIL_TYPE_TRAIT_AC,
			applicationContext.getMessage("nomenclature.mail.typetrait.ac", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.typetrait.ac.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.typetrait.ac.content", null, locale));

		/* Mail type de traitement AC */
		majMail(new Mail(NomenclatureUtils.MAIL_TYPE_TRAIT_ATT,
			applicationContext.getMessage("nomenclature.mail.typetrait.att", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.typetrait.att.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.typetrait.att.content", null, locale));

		/* Mail suppression compte */
		majMail(new Mail(NomenclatureUtils.MAIL_CPT_MIN_DELETE,
			applicationContext.getMessage("nomenclature.mail.cptMin.delete", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.cptMin.delete.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.cptMin.delete.content", null, locale));

		/* Mail relance formulaire */
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_RELANCE_FORMULAIRE,
			applicationContext.getMessage("nomenclature.mail.relance.form", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.relance.form.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.relance.form.content", null, locale));

		/* Mail relance formulaire */
		majMail(new Mail(NomenclatureUtils.MAIL_CANDIDATURE_RELANCE_FAVO,
			applicationContext.getMessage("nomenclature.mail.relance.favo", null, locale), true, true,
			NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.relance.favo.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.relance.favo.content", null, locale));

		/* Mail d'alerte de desistement de candidature pour la commission */
		majMail(new Mail(NomenclatureUtils.MAIL_COMMISSION_ALERT_DESISTEMENT,
			applicationContext.getMessage("nomenclature.mail.commission.desist.candidature", null, locale), true,
			true, NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.commission.desist.candidature.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.commission.desist.candidature.content", null, locale));

		/* Mail d'alerte de passage en liste principale pour la commission */
		majMail(new Mail(NomenclatureUtils.MAIL_COMMISSION_ALERT_LISTE_PRINC,
			applicationContext.getMessage("nomenclature.mail.commission.listprinc.candidature", null, locale), true,
			true, NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE, null),
			applicationContext.getMessage("nomenclature.mail.commission.listprinc.candidature.sujet", null, locale),
			applicationContext.getMessage("nomenclature.mail.commission.listprinc.candidature.content", null,
				locale));
	}

	/**
	 * Mise a jour de la version
	 * @param version
	 */
	private void nomenclatureVersion(final Version version) {
		final Version v = versionRepository.findOne(version.getCodVersion());
		if (v != null) {
			v.setValVersion(version.getValVersion());
			v.setDatVersion(LocalDateTime.now());
			versionRepository.save(v);
		} else {
			versionRepository.save(version);
		}

	}

	/**
	 * Met a jour un type de decision
	 * @param typeDec
	 */
	private void majParametre(final Parametre param) {
		// MethodUtils.validateBean(param, logger);
		final Parametre paramLoad = parametreRepository.findByCodParam(param.getCodParam());
		if (paramLoad == null) {
			parametreRepository.saveAndFlush(param);
		} else {
			paramLoad.setLibParam(param.getLibParam());
			paramLoad.setTemAffiche(param.getTemAffiche());
			paramLoad.setTypParam(param.getTypParam());
			parametreRepository.saveAndFlush(paramLoad);
		}
	}

	/**
	 * Met à jour la liste des fonctionnalités
	 * @param droitProfil
	 * @param droitFonctionnalite
	 */
	private void majDroitProfilFonc(final DroitProfil droitProfilCtrCand, final DroitProfil droitProfilCommission,
		DroitFonctionnalite droitFonctionnalite) {
		droitFonctionnalite = droitFonctionnaliteRepository.saveAndFlush(droitFonctionnalite);
		if (droitProfilCommission != null && droitFonctionnalite.getTemOpenComFonc()) {
			droitProfilCommission
				.addFonctionnalite(new DroitProfilFonc(droitFonctionnalite, droitProfilCommission, true));
			droitProfilRepository.saveAndFlush(droitProfilCommission);
		}
		droitProfilCtrCand.addFonctionnalite(new DroitProfilFonc(droitFonctionnalite, droitProfilCtrCand, false));
		droitProfilRepository.saveAndFlush(droitProfilCtrCand);
	}

	/**
	 * Met a jour un message
	 * @param typeStatut
	 */
	private void majMessage(final Message message, final String valDefautMsg) {
		final Message messageLoad = messageRepository.findByCodMsg(message.getCodMsg());
		if (messageLoad == null) {
			final TypeTraduction typeTrad = typeTraductionRepository.findOne(NomenclatureUtils.TYP_TRAD_MSG_VAL);
			final I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTrad));
			final I18nTraduction trad = new I18nTraduction(valDefautMsg, i18n, cacheController.getLangueDefault());
			i18nTraductionRepository.saveAndFlush(trad);
			message.setI18nValMessage(i18n);
			message.setDatModMsg(LocalDateTime.now());
			messageRepository.saveAndFlush(message);
		}
	}

	/**
	 * Met a jour un type de traitement
	 * @param typeStatut
	 */
	private void majTypeTraitement(final TypeTraitement typeTraitement) {
		final TypeTraitement typeTraitementLoad = typeTraitementRepository
			.findByCodTypTrait(typeTraitement.getCodTypTrait());
		if (typeTraitementLoad == null) {
			final TypeTraduction typeTrad = typeTraductionRepository.findOne(NomenclatureUtils.TYP_TRAD_TYP_TRAIT_LIB);
			final I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTrad));
			final I18nTraduction trad = new I18nTraduction(typeTraitement.getLibTypTrait(), i18n,
				cacheController.getLangueDefault());
			i18nTraductionRepository.saveAndFlush(trad);
			typeTraitement.setI18nLibTypTrait(i18n);

			typeTraitementRepository.saveAndFlush(typeTraitement);
		}
	}

	/**
	 * Met a jour un type de decision
	 * @param typeDec
	 */
	private void majTypeDec(final TypeDecision typeDec) {
		final TypeDecision typeDecLoad = typeDecisionRepository.findByCodTypDec(typeDec.getCodTypDec());
		if (typeDecLoad == null) {
			final TypeTraduction typeTrad = typeTraductionRepository.findOne(NomenclatureUtils.TYP_TRAD_TYP_DEC_LIB);
			final I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTrad));
			final I18nTraduction trad = new I18nTraduction(typeDec.getLibTypDec(), i18n,
				cacheController.getLangueDefault());
			i18nTraductionRepository.saveAndFlush(trad);
			typeDec.setI18nLibTypDec(i18n);

			typeDecisionRepository.saveAndFlush(typeDec);
		}
	}

	/**
	 * Met a jour un type de statut
	 * @param typeStatut
	 */
	private void majTypeStatut(final TypeStatut typeStatut) {
		final TypeStatut typeStatutLoad = typeStatutRepository.findByCodTypStatut(typeStatut.getCodTypStatut());
		if (typeStatutLoad == null) {
			final TypeTraduction typeTrad = typeTraductionRepository.findOne(NomenclatureUtils.TYP_TRAD_TYP_STATUT);
			final I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTrad));
			final I18nTraduction trad = new I18nTraduction(typeStatut.getLibTypStatut(), i18n,
				cacheController.getLangueDefault());
			i18nTraductionRepository.saveAndFlush(trad);
			typeStatut.setI18nLibTypStatut(i18n);

			typeStatutRepository.saveAndFlush(typeStatut);
		}
	}

	/**
	 * Met a jour un type de statut de piece
	 * @param typeStatutPiece
	 */
	private void majTypeStatutPiece(final TypeStatutPiece typeStatutPiece) {
		final TypeStatutPiece typeStatutPiceLoad = typeStatutPieceRepository
			.findByCodTypStatutPiece(typeStatutPiece.getCodTypStatutPiece());
		if (typeStatutPiceLoad == null) {
			final TypeTraduction typeTrad = typeTraductionRepository
				.findOne(NomenclatureUtils.TYP_TRAD_TYP_STATUT_PIECE);
			final I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTrad));
			final I18nTraduction trad = new I18nTraduction(typeStatutPiece.getLibTypStatutPiece(), i18n,
				cacheController.getLangueDefault());
			i18nTraductionRepository.saveAndFlush(trad);
			typeStatutPiece.setI18nLibTypStatutPiece(i18n);

			typeStatutPieceRepository.saveAndFlush(typeStatutPiece);
		}
	}

	/**
	 * Mise à jour d'un droit
	 * @param  droit
	 * @return       le droit profil maj
	 */
	private DroitProfil majDroitProfil(final DroitProfil droitProfil) {
		final DroitProfil droitProfilLoad = droitProfilRepository.findByCodProfil(droitProfil.getCodProfil());
		if (droitProfilLoad == null) {
			return droitProfilRepository.saveAndFlush(droitProfil);
		} else {
			droitProfilLoad.setTypProfil(droitProfil.getTypProfil());
			droitProfilLoad.setLibProfil(droitProfil.getLibProfil());
			return droitProfilRepository.saveAndFlush(droitProfilLoad);
		}
	}

	/**
	 * Mise à jour d'une civilite
	 * @param civilite
	 */
	private void majCivilite(final Civilite civilite) {
		final Civilite civiliteLoad = civiliteRepository.findOne(civilite.getCodCiv());
		if (civiliteLoad == null) {
			civiliteRepository.saveAndFlush(civilite);
		} else {
			civiliteLoad.setLibCiv(civilite.getLibCiv());
			civiliteLoad.setCodSiScol(civilite.getCodSiScol());
			civiliteLoad.setCodSexe(civilite.getCodSexe());
			civiliteRepository.saveAndFlush(civiliteLoad);
		}
	}

	/**
	 * Mise à jour d'une langue
	 * @param langue
	 */
	private void majLangue(final Langue langue) {
		final Langue langueLoad = langueRepository.findOne(langue.getCodLangue());
		if (langueLoad == null) {
			langueRepository.saveAndFlush(langue);
		} else {
			langueLoad.setLibLangue(langue.getLibLangue());
			langueRepository.saveAndFlush(langueLoad);
		}
	}

	/**
	 * Mise à jour d'un batch
	 * @param batch
	 */
	private void majBatch(Batch batch) {
		final Batch batchLoad = batchRepository.findOne(batch.getCodBatch());
		if (batchLoad == null) {
			batch = batchRepository.saveAndFlush(batch);
		} else {
			batchLoad.setLibBatch(batch.getLibBatch());
			batchRepository.saveAndFlush(batchLoad);
		}
	}

	/**
	 * Mise à jour d'un mail
	 * @param  mail
	 * @param  content
	 * @return         le mail maj
	 */
	private Mail majMail(Mail mail, String sujet, String content) {
		final Mail mailLoad = mailRepository.findByCodMail(mail.getCodMail());
		if (sujet == null || sujet.equals("")) {
			sujet = mail.getLibMail();
		}
		if (content == null || content.equals("")) {
			content = mail.getLibMail();
		}
		if (mailLoad == null) {
			final TypeTraduction typeTradSujet = typeTraductionRepository
				.findOne(NomenclatureUtils.TYP_TRAD_MAIL_SUJET);
			final I18n i18nSujetMail = i18nRepository.saveAndFlush(new I18n(typeTradSujet));
			i18nTraductionRepository
				.saveAndFlush(new I18nTraduction(sujet, i18nSujetMail, cacheController.getLangueDefault()));
			mail.setI18nSujetMail(i18nSujetMail);

			final TypeTraduction typeTradCorps = typeTraductionRepository
				.findOne(NomenclatureUtils.TYP_TRAD_MAIL_CORPS);
			final I18n i18nCorpsMail = i18nRepository.saveAndFlush(new I18n(typeTradCorps));
			i18nTraductionRepository
				.saveAndFlush(new I18nTraduction(content, i18nCorpsMail, cacheController.getLangueDefault()));
			mail.setI18nCorpsMail(i18nCorpsMail);
			mail = mailRepository.saveAndFlush(mail);
			return mail;
		} else if (mailLoad.getTemIsModeleMail() && mail.getTemIsModeleMail()
			&& !mailLoad.getLibMail().equals(mail.getLibMail())) {
			mailLoad.setLibMail(mail.getLibMail());
			return mailRepository.saveAndFlush(mailLoad);
		}
		return mailLoad;
	}

	/** Methode permettant de supprimer des élements déja insérés */
	public void cleanNomenclature() {
		final Version versionNomenclature = versionRepository.findOne(NomenclatureUtils.VERSION_NOMENCLATURE_COD);
		if (versionNomenclature == null) {
			return;
		}
		/* Definition locale */
		final String localFrString = "fr";
		final Locale localFr = new Locale(localFrString);

		final RealeaseVersion vNomenclature = new RealeaseVersion(versionNomenclature.getValVersion());

		// si inferieur a 2.2.0.6
		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_2_0_6))) {
			/* Suppression du mail de proposition */
			Mail mailLoad = mailRepository.findByCodMail("PROP_CANDIDATURE");
			if (mailLoad != null) {
				mailRepository.delete(mailLoad);
			}

			/* Suppression des codes par domaine : CPT_MIN_ et CANDIDATURE_ */
			mailLoad = mailRepository.findByCodMail("MOD_MAIL_CPT_MIN");
			if (mailLoad != null) {
				mailLoad.setCodMail(NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL);
				mailRepository.saveAndFlush(mailLoad);
			}
			mailLoad = mailRepository.findByCodMail("COMMISSION_PROP");
			if (mailLoad != null) {
				mailLoad.setCodMail("CANDIDATURE_COMMISSION_PROP");
				mailRepository.saveAndFlush(mailLoad);
			}
			mailLoad = mailRepository.findByCodMail("COMMISSION_ANNUL");
			if (mailLoad != null) {
				mailLoad.setCodMail("CANDIDATURE_COMMISSION_ANNUL");
				mailRepository.saveAndFlush(mailLoad);
			}

			mailLoad = mailRepository.findByCodMail("ANNUL_CANDIDATURE");
			if (mailLoad != null) {
				mailLoad.setCodMail(NomenclatureUtils.MAIL_CANDIDATURE_ANNULATION);
				mailRepository.saveAndFlush(mailLoad);
			}

			mailLoad = mailRepository.findByCodMail("CONFIRM_CANDIDATURE");
			if (mailLoad != null) {
				mailLoad.setCodMail(NomenclatureUtils.MAIL_CANDIDATURE_CONFIRM);
				mailRepository.saveAndFlush(mailLoad);
			}

			mailLoad = mailRepository.findByCodMail("DESIST_CANDIDATURE");
			if (mailLoad != null) {
				mailLoad.setCodMail(NomenclatureUtils.MAIL_CANDIDATURE_DESIST);
				mailRepository.saveAndFlush(mailLoad);
			}

			mailLoad = mailRepository.findByCodMail("CANDIDATURE_COMMISSION_PROP");
			if (mailLoad != null) {
				mailLoad.setCodMail(NomenclatureUtils.MAIL_COMMISSION_ALERT_PROPOSITION);
				mailRepository.saveAndFlush(mailLoad);
			}

			mailLoad = mailRepository.findByCodMail("CANDIDATURE_COMMISSION_ANNUL");
			if (mailLoad != null) {
				mailLoad.setCodMail(NomenclatureUtils.MAIL_COMMISSION_ALERT_ANNULATION);
				mailRepository.saveAndFlush(mailLoad);
			}

			mailLoad = mailRepository.findByCodMail("CANDIDATURE_COMMISSION_TRANS");
			if (mailLoad != null) {
				mailLoad.setCodMail(NomenclatureUtils.MAIL_COMMISSION_ALERT_TRANSMISSION);
				mailRepository.saveAndFlush(mailLoad);
			}

			final Parametre paramLoad = parametreRepository.findByCodParam("NB_VOEUX_CTR_MAX");
			if (paramLoad != null) {
				parametreRepository.delete(paramLoad);
			}

			/* Actualisation du code du batch LS */
			final Batch batchLS = batchRepository.findOne("BATCH_SYNC_LIMESURVEY");
			if (batchLS != null) {
				final List<BatchHisto> listeHisto = batchLS.getBatchHistos();
				batchLS.setCodBatch(NomenclatureUtils.BATCH_SYNCHRO_LIMESURVEY);
				final Batch batchLSNew = batchRepository.save(batchLS);
				listeHisto.forEach(e -> {
					final BatchHisto newBatchHisto = e.clone(batchLSNew);
					batchHistoRepository.saveAndFlush(newBatchHisto);
					batchHistoRepository.delete(e);
				});
				batchRepository.delete("BATCH_SYNC_LIMESURVEY");
			}

			/* Correction des accents sur preselection */
			final String oldLower = applicationContext.getMessage("nomenclature.correction.preselection.lower.old",
				null, localFr);
			final String newLower = applicationContext.getMessage("nomenclature.correction.preselection.lower.new",
				null, localFr);
			final String oldUpper = applicationContext.getMessage("nomenclature.correction.preselection.upper.old",
				null, localFr);
			final String newUpper = applicationContext.getMessage("nomenclature.correction.preselection.upper.new",
				null, localFr);

			// corrige les mails lib + traduction sujet et corps
			final List<Mail> listeMail = mailRepository.findAll();
			listeMail.forEach(mail -> {
				if (mail.getLibMail() != null) {
					Boolean find = false;
					if (mail.getLibMail().contains(oldLower)) {
						mail.setLibMail(mail.getLibMail().replaceAll(oldLower, newLower));
						find = true;
					}
					if (mail.getLibMail().contains(oldUpper)) {
						mail.setLibMail(mail.getLibMail().replaceAll(oldUpper, newUpper));
						find = true;
					}
					if (find) {
						mailRepository.save(mail);
					}
					correctionI18n(mail.getI18nCorpsMail(), localFrString, oldLower, newLower);
					correctionI18n(mail.getI18nCorpsMail(), localFrString, oldUpper, newUpper);
					correctionI18n(mail.getI18nSujetMail(), localFrString, oldLower, newLower);
					correctionI18n(mail.getI18nSujetMail(), localFrString, oldUpper, newUpper);
				}
			});

			// corrige les types de decision
			final List<TypeDecision> listeTypeDecision = typeDecisionRepository.findAll();
			listeTypeDecision.forEach(typeDec -> {
				Boolean find = false;
				if (typeDec.getLibTypDec().contains(oldLower)) {
					typeDec.setLibTypDec(typeDec.getLibTypDec().replaceAll(oldLower, newLower));
					find = true;
				}
				if (typeDec.getLibTypDec().contains(oldUpper)) {
					typeDec.setLibTypDec(typeDec.getLibTypDec().replaceAll(oldUpper, newUpper));
					find = true;
				}
				if (find) {
					typeDecisionRepository.save(typeDec);
				}
				correctionI18n(typeDec.getI18nLibTypDec(), localFrString, oldLower, newLower);
				correctionI18n(typeDec.getI18nLibTypDec(), localFrString, oldUpper, newUpper);
			});
		}

		/* Ajout du libellé de la campagne */
		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_2_0_13))) {
			final List<Campagne> listeCampagne = campagneRepository.findAll();
			listeCampagne.forEach(campagne -> {
				if (campagne.getI18nLibCamp() == null) {
					final TypeTraduction typeTrad = typeTraductionRepository.saveAndFlush(new TypeTraduction(
						NomenclatureUtils.TYP_TRAD_CAMP_LIB,
						applicationContext.getMessage("nomenclature.typtrad.campLib", null, new Locale("fr")),
						500));
					final I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTrad));
					final I18nTraduction trad = new I18nTraduction(campagne.getLibCamp(), i18n,
						cacheController.getLangueDefault());
					i18nTraductionRepository.saveAndFlush(trad);
					campagne.setI18nLibCamp(i18n);
					campagneRepository.saveAndFlush(campagne);
				}
			});
		}

		/* On supprime les entrées dans la table de reload car les codes ne sont plus
		 * bons */
		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_2_0_14))) {
			loadBalancingReloadRepository.deleteAllInBatch();
		}

		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_2_5_1))) {
			/* Ajout de l'info comp de la formation aux i18n */
			final TypeTraduction typeTradForm = typeTraductionRepository.saveAndFlush(new TypeTraduction(
				NomenclatureUtils.TYP_TRAD_FORM_INFO_COMP,
				applicationContext.getMessage("nomenclature.typtrad.formInfoComp", null, new Locale("fr")), 5000));
			final List<Formation> listeFormation = formationRepository.findAll();
			listeFormation.forEach(formation -> {
				if (formation.getI18nInfoCompForm() == null && formation.getInfoCompForm() != null) {
					final I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTradForm));
					final I18nTraduction trad = new I18nTraduction(formation.getInfoCompForm(), i18n,
						cacheController.getLangueDefault());
					i18nTraductionRepository.saveAndFlush(trad);
					formation.setI18nInfoCompForm(i18n);
					formationRepository.saveAndFlush(formation);
				}
			});

			/* Ajout du commentaire retour de la commission aux i18n */
			final TypeTraduction typeTradComm = typeTraductionRepository.saveAndFlush(new TypeTraduction(
				NomenclatureUtils.TYP_TRAD_COMM_COMMENT_RETOUR,
				applicationContext.getMessage("nomenclature.typtrad.commCommentRetour", null, new Locale("fr")),
				5000));
			final List<Commission> listeCommission = commissionRepository.findAll();
			listeCommission.forEach(commission -> {
				if (commission.getI18nCommentRetourComm() == null && commission.getCommentRetourComm() != null) {
					final I18n i18n = i18nRepository.saveAndFlush(new I18n(typeTradComm));
					final I18nTraduction trad = new I18nTraduction(commission.getCommentRetourComm(), i18n,
						cacheController.getLangueDefault());
					i18nTraductionRepository.saveAndFlush(trad);
					commission.setI18nCommentRetourComm(i18n);
					commissionRepository.saveAndFlush(commission);
				}
			});
		}

		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_2_9_1))) {
			// on renomme le parametre COD_SANS_BAC
			final Parametre paramCodSansBac = parametreRepository.findByCodParam("COD_SANS_BAC");
			if (paramCodSansBac != null) {
				final Parametre newParamCodSansBac = new Parametre(NomenclatureUtils.COD_PARAM_SCOL_SISCOL_COD_SANS_BAC,
					applicationContext.getMessage("parametrage.codParam.siScolCodSansBac", null, localFr),
					paramCodSansBac.getValParam(), NomenclatureUtils.TYP_PARAM_STRING + "(4)", true, true);
				parametreRepository.save(newParamCodSansBac);
				parametreRepository.delete(paramCodSansBac);
			}
		}

		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_3_0_0))) {
			// on renomme les codes des paramètres
			/* Paramètres OPI */
			renameCodParam("PREFIXE_OPI", NomenclatureUtils.COD_PARAM_OPI_PREFIXE);
			renameCodParam("IS_UTILISE_OPI", NomenclatureUtils.COD_PARAM_OPI_IS_UTILISE);
			renameCodParam("IS_UTILISE_OPI_ADR", NomenclatureUtils.COD_PARAM_OPI_IS_UTILISE_ADR);
			renameCodParam("NB_OPI_BATCH_MAX", NomenclatureUtils.COD_PARAM_OPI_NB_BATCH_MAX);
			renameCodParam("IS_UTILISE_OPI_PJ", NomenclatureUtils.COD_PARAM_OPI_IS_UTILISE_PJ);
			renameCodParam("IS_OPI_IMMEDIAT", NomenclatureUtils.COD_PARAM_OPI_IS_IMMEDIAT);

			/* Paramètres SVA */
			renameCodParam("ALERT_SVA_DAT", NomenclatureUtils.COD_PARAM_SVA_ALERT_DAT);
			renameCodParam("ALERT_SVA_DEFINITIF", NomenclatureUtils.COD_PARAM_SVA_ALERT_DEFINITIF);

			/* Paramètres Compte candidat */
			renameCodParam("PREFIXE_NUM_DOSS_CPT", NomenclatureUtils.COD_PARAM_CANDIDAT_PREFIXE_NUM_DOSS);
			renameCodParam("NB_JOUR_KEEP_CPT_MIN", NomenclatureUtils.COD_PARAM_CANDIDAT_NB_JOUR_KEEP_CPT_MIN);
			renameCodParam("IS_INE_OBLI_FR", NomenclatureUtils.COD_PARAM_CANDIDAT_IS_INE_OBLI_FR);
			renameCodParam("IS_GET_CURSUS_INTERNE", NomenclatureUtils.COD_PARAM_CANDIDAT_IS_GET_CURSUS_INTERNE);
			renameCodParam("IS_UTILISE_SYNCHRO_INE", NomenclatureUtils.COD_PARAM_CANDIDAT_IS_UTILISE_SYNCHRO_INE);

			/* Paramètres Candidature */
			renameCodParam("NB_VOEUX_MAX", NomenclatureUtils.COD_PARAM_CANDIDATURE_NB_VOEUX_MAX);
			renameCodParam("NB_VOEUX_MAX_IS_ETAB", NomenclatureUtils.COD_PARAM_CANDIDATURE_NB_VOEUX_MAX_IS_ETAB);

			/* Paramètres techniques */
			renameCodParam("FILE_MAX_SIZE", NomenclatureUtils.COD_PARAM_TECH_FILE_MAX_SIZE);
			renameCodParam("IS_MAINTENANCE", NomenclatureUtils.COD_PARAM_TECH_IS_MAINTENANCE);
			renameCodParam("IS_UTILISE_DEMAT", NomenclatureUtils.COD_PARAM_TECH_IS_UTILISE_DEMAT);
			renameCodParam("NB_JOUR_KEEP_HISTO_BATCH", NomenclatureUtils.COD_PARAM_TECH_NB_JOUR_KEEP_HISTO_BATCH);
			renameCodParam("IS_DEMAT_MAINTENANCE", NomenclatureUtils.COD_PARAM_TECH_IS_DEMAT_MAINTENANCE);

			/* Paramètres gestionnaire */
			renameCodParam("IS_UTILISE_BLOCAGE_AVIS_MASSE", NomenclatureUtils.COD_PARAM_GEST_IS_UTILISE_BLOCAGE_MASSE);
			renameCodParam("IS_LETTRE_ADM_APRES_ACCEPT", "GEST_IS_LETTRE_ADM_APRES_ACCEPT");

			/* Paramètres scol */
			renameCodParam("NB_JOUR_ARCHIVAGE", NomenclatureUtils.COD_PARAM_SCOL_NB_JOUR_ARCHIVAGE);
			renameCodParam("IS_APPEL", NomenclatureUtils.COD_PARAM_SCOL_IS_APPEL);
			renameCodParam("GESTION_CANDIDAT_COMM", NomenclatureUtils.COD_PARAM_SCOL_GESTION_CANDIDAT_COMM);
			renameCodParam("GESTION_CANDIDAT_CTR_CAND", NomenclatureUtils.COD_PARAM_SCOL_GESTION_CANDIDAT_CTR_CAND);
			renameCodParam("SISCOL_COD_SANS_BAC", NomenclatureUtils.COD_PARAM_SCOL_SISCOL_COD_SANS_BAC);
			renameCodParam("IS_FORM_COD_APO_OBLI", "SCOL_IS_COD_SISCOL_OBLI");

			/* Paramètres téléchargement multiple */
			renameCodParam("NB_DOSSIER_TELECHARGEMENT_MAX", NomenclatureUtils.COD_PARAM_DOWNLOAD_MULTIPLE_NB_MAX);
		}

		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_3_0_5))) {
			// on renomme les codes des paramètres
			renameCodParam("GEST_IS_LETTRE_ADM_APRES_ACCEPT",
				NomenclatureUtils.COD_PARAM_DOWNLOAD_IS_LETTRE_ADM_APRES_CONFIRM);
		}

		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_3_0_6))) {
			// on renomme les codes des paramètres
			renameCodParam("IS_STATUT_ATT_WHEN_CHANGE_TT",
				NomenclatureUtils.COD_PARAM_SCOL_IS_STATUT_ATT_WHEN_CHANGE_TT);
		}

		/* Modififcation du type de siscol */
		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_4_0_0))) {
			majTypSiScol("V2_4_00_00__majSiscol");
			renameCodAndLibParam("SCOL_IS_COD_APO_OBLI", NomenclatureUtils.COD_PARAM_SCOL_IS_COD_SISCOL_OBLI,
				applicationContext.getMessage("parametrage.codParam.formCodSiScolOblig", null, localFr));
			renameCodAndLibParam("DOWNLOAD_IS_ADD_APOGEE_PJ", NomenclatureUtils.COD_PARAM_DOWNLOAD_IS_ADD_SISCOL_PJ,
				applicationContext.getMessage("parametrage.codParam.isAddSiScolPjDossier", null, localFr));
			renameCodAndLibParam("CANDIDAT_IS_GET_APO_PJ", NomenclatureUtils.COD_PARAM_CANDIDAT_IS_GET_SISCOL_PJ,
				applicationContext.getMessage("parametrage.codParam.utiliseSiScolPj", null, localFr));
		}

		/* Modififcation du type de siscol reforme bac */
		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_4_0_3))) {
			majTypSiScol("V2_4_00_01__majSiscol");
		}

		/* Modififcation des droits profil de centre de candidatures */
		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_4_0_5))) {
			setTemUpdatableDroitProfil(NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE);
			setTemUpdatableDroitProfil(NomenclatureUtils.DROIT_PROFIL_COMMISSION);
		}

		/* Suppression mail CPT_MIN_ID_OUBLIE et ID_OUBLIE */
		if (vNomenclature.isLessThan(new RealeaseVersion(NomenclatureUtils.VERSION_NOMENCLATURE_MAJ_2_4_2_0))) {
			final Mail mailLoad = mailRepository.findByCodMail("CPT_MIN_ID_OUBLIE");
			if (mailLoad != null) {
				mailRepository.delete(mailLoad);
			}
			final Mail mailLoadId = mailRepository.findByCodMail("ID_OUBLIE");
			if (mailLoadId != null) {
				mailRepository.delete(mailLoadId);
			}

			/* Met à jour les clé de validation des dossiers */
			candidatController.majKeyValidation();
		}
	}

	/**
	 * Rend un profil updateable
	 */
	private void setTemUpdatableDroitProfil(final String codProfil) {
		final DroitProfil profil = droitProfilRepository.findByCodProfil(codProfil);
		if (profil != null) {
			profil.setTemUpdatable(true);
			droitProfilRepository.save(profil);
		}
	}

	/**
	 * Renommage des codes des parametres
	 * @param oldCodParam
	 * @param newCodParam
	 * @param local
	 */
	private void renameCodParam(final String oldCodParam, final String newCodParam) {
		final Parametre oldParam = parametreRepository.findByCodParam(oldCodParam);
		if (oldParam != null) {
			final Parametre newParam = new Parametre(newCodParam, oldParam);
			parametreRepository.save(newParam);
			parametreRepository.delete(oldParam);
		}
	}

	/**
	 * Renommage des codes des parametres
	 * @param oldCodParam
	 * @param newCodParam
	 * @param local
	 */
	private void renameCodAndLibParam(final String oldCodParam, final String newCodParam, final String newLib) {
		final Parametre oldParam = parametreRepository.findByCodParam(oldCodParam);
		if (oldParam != null) {
			final Parametre newParam = new Parametre(newCodParam, oldParam);
			newParam.setLibParam(newLib);
			parametreRepository.save(newParam);
			parametreRepository.delete(oldParam);
		}
	}

	/**
	 * Corrige une traduction de nomenclature erronée
	 * @param i18n
	 * @param locale
	 * @param oldValue
	 * @param newValue
	 */
	private void correctionI18n(final I18n i18n, final String locale, final String oldValue, final String newValue) {
		final I18nTraductionPK pk = new I18nTraductionPK(i18n.getIdI18n(), locale);
		final I18nTraduction traduction = i18nTraductionRepository.findOne(pk);
		if (traduction != null && traduction.getValTrad() != null && traduction.getValTrad().contains(oldValue)) {
			final String newVal = traduction.getValTrad().replaceAll(oldValue, newValue);
			traduction.setValTrad(newVal);
			i18nTraductionRepository.save(traduction);
		}
	}

	/**
	 * Charge un élément de version
	 * @param code
	 * @param version
	 */
	public void loadElementVersion(final String code, final Version version) {
		if (code != null && version != null) {
			mapVersion.put(code, version);
		}
	}

	/**
	 * Charge les versions
	 */
	public void loadMapVersion() {
		mapVersion.clear();
		/* App Version */
		loadElementVersion(NomenclatureUtils.VERSION_APPLICATION_COD,
			new Version(NomenclatureUtils.VERSION_APPLICATION_COD, appVersion));
		/* Db Version */
		loadElementVersion(NomenclatureUtils.VERSION_DB, getDbVersion(NomenclatureUtils.VERSION_DB));
		/* Nomenclature version */
		loadElementVersion(NomenclatureUtils.VERSION_NOMENCLATURE_COD,
			getVersion(NomenclatureUtils.VERSION_NOMENCLATURE_COD));
		/* Version Siscol */
		loadElementVersion(NomenclatureUtils.VERSION_SI_SCOL_COD, getVersion(NomenclatureUtils.VERSION_SI_SCOL_COD));
		if (siScolService.isImplementationApogee()) {
			/* Version WS */
			final String valVersionWS = MethodUtils.getClassVersion(AdministratifMetierServiceInterfaceService.class);
			loadElementVersion(NomenclatureUtils.VERSION_WS, new Version(NomenclatureUtils.VERSION_WS, valVersionWS));
			/* Version WS PJ */
			loadElementVersion(NomenclatureUtils.VERSION_WS_PJ,
				new Version(NomenclatureUtils.VERSION_WS, valVersionWS));
		}
		/* Démat */
		String libDemat = NomenclatureUtils.VERSION_NO_VERSION_VAL;
		if (fileController.getModeDemat().equals(ConstanteUtils.TYPE_FICHIER_STOCK_CMIS)) {
			libDemat = ConstanteUtils.TYPE_FICHIER_STOCK_CMIS_LIB;
		} else if (fileController.getModeDemat().equals(ConstanteUtils.TYPE_FICHIER_STOCK_FILE_SYSTEM)) {
			libDemat = ConstanteUtils.TYPE_FICHIER_STOCK_FILE_SYSTEM_LIB;
		}
		loadElementVersion(NomenclatureUtils.VERSION_DEMAT, new Version(NomenclatureUtils.VERSION_DEMAT, libDemat));
		/* LimeSurvey */
		loadElementVersion(NomenclatureUtils.VERSION_LS,
			new Version(NomenclatureUtils.VERSION_LS, limeSurveyRest.getVersionLimeSurvey()));
		/* Checkine */
		// pb dans certains etablissements ou le service est sur le même serveur
		// loadElementVersion(NomenclatureUtils.VERSION_INES, new
		// Version(NomenclatureUtils.VERSION_INES,
		// siScolService.getVersionWSCheckIne()));
	}

	/**
	 * @return true si l'activation de l'ajout des PJ en mode multiple est activé,
	 *         false sinon
	 */
	public String getIsDownloadMultipleAddPjOld() {
		try {
			final String downloadMultipleAddPj = applicationContext.getEnvironment()
				.getProperty("downloadMultipleAddPj");
			if (downloadMultipleAddPj != null && Boolean.valueOf(downloadMultipleAddPj)) {
				return ConstanteUtils.TYP_BOOLEAN_YES;
			}
		} catch (final Exception e) {
		}
		return ConstanteUtils.TYP_BOOLEAN_NO;
	}

	/** @return synchronisation par INE : par defaut true */
	public String getIsEnableSyncByINEOld() {
		try {
			final String enableSyncByINE = applicationContext.getEnvironment().getProperty("enableSyncByINE");
			if (enableSyncByINE != null && !Boolean.valueOf(enableSyncByINE)) {
				return ConstanteUtils.TYP_BOOLEAN_NO;
			}
		} catch (final Exception e) {
		}
		return ConstanteUtils.TYP_BOOLEAN_YES;

	}

	/** @return l'ajout des PJ Apogee dans le dossier : par defaut true */
	public String getIsEnableAddPJApogeeDossierOld() {
		try {
			final String enableAddPJApogeeDossier = applicationContext.getEnvironment()
				.getProperty("enableAddPJApogeeDossier");
			if (enableAddPJApogeeDossier != null && !Boolean.valueOf(enableAddPJApogeeDossier)) {
				return ConstanteUtils.TYP_BOOLEAN_NO;
			}
		} catch (final Exception e) {
		}
		return ConstanteUtils.TYP_BOOLEAN_YES;
	}

	/** @return le mode de download multiple par défaut zip */
	public String getDownloadMultipleModeOld() {
		try {
			final String downloadMultipleMode = applicationContext.getEnvironment().getProperty("downloadMultipleMode");
			if (downloadMultipleMode != null && downloadMultipleMode.equals("pdf")) {
				return ConstanteUtils.PARAM_MODE_DOWNLOAD_MULTIPLE_PDF;
			}
		} catch (final Exception e) {
		}
		return ConstanteUtils.PARAM_MODE_DOWNLOAD_MULTIPLE_ZIP;
	}

	/**
	 * Affiche les versions de l'application
	 */
	public void printVersions() {
		logger.debug("*****Versions de l'application*****");
		mapVersion.forEach((k, v) -> {
			logger.debug(k + " : " + v.getValVersion());
		});
	}

	/** @return la liste des versions */
	public List<SimpleTablePresentation> getVersions() {
		final List<SimpleTablePresentation> liste = new ArrayList<>();
		int i = 0;
		liste.add(getPresentationFromVersion(i++, NomenclatureUtils.VERSION_APPLICATION_COD, "app"));
		liste.add(getPresentationFromVersion(i++, NomenclatureUtils.VERSION_DB, "db"));
		liste.add(getPresentationFromVersion(i++, NomenclatureUtils.VERSION_NOMENCLATURE_COD, "nomenclature"));
		liste.add(getPresentationFromVersion(i++, NomenclatureUtils.VERSION_SI_SCOL_COD, "siScol"));
		if (siScolService.isImplementationApogee()) {
			liste.add(getPresentationFromVersion(i++, NomenclatureUtils.VERSION_WS, "ws"));
			liste.add(getPresentationFromVersion(i++, NomenclatureUtils.VERSION_WS_PJ, "ws.pj"));
		} else if (siScolService.isImplementationPegase()) {
			liste.add(getPresentationFromVersion(i++, NomenclatureUtils.VERSION_WS, "ws"));
		}

		liste.add(getPresentationFromVersion(i++, NomenclatureUtils.VERSION_DEMAT, "demat"));
		liste.add(getPresentationFromVersion(i++, NomenclatureUtils.VERSION_LS, "limesurvey"));
		// pb dans certains etablissements ou le service est sur le même serveur et
		// n'est pas encore démarré lorsque ecandidat est lancé
		// liste.add(getPresentationFromVersion(i++, NomenclatureUtils.VERSION_INES,
		// "ines"));
		if (siScolService.hasCheckStudentINES()) {
			liste.add(new SimpleTablePresentation(i++, NomenclatureUtils.VERSION_INES,
				applicationContext.getMessage("version.ines", null, UI.getCurrent().getLocale()),
				siScolService.getVersionWSCheckIne()));
		}
		return liste;
	}

	/**
	 * @param  order
	 * @param  code
	 * @param  msgCode
	 * @return         la liste de bean version
	 */
	public SimpleTablePresentation getPresentationFromVersion(final int order, final String code,
		final String msgCode) {
		String valVersion = NomenclatureUtils.VERSION_NO_VERSION_VAL;
		LocalDateTime datVersion = null;
		final Version version = mapVersion.get(code);
		if (version != null) {
			if (version.getValVersion() != null) {
				valVersion = version.getValVersion();
			}
			if (version.getDatVersion() != null) {
				datVersion = version.getDatVersion();
			}
		}
		return new SimpleTablePresentation(order, code,
			applicationContext.getMessage("version." + msgCode, null, UI.getCurrent().getLocale()), valVersion,
			datVersion);
	}

	/**
	 * Met à jour le typSiScol pour les tables de ref SiScol
	 * @throws Exception
	 */
	@Transactional
	private void majTypSiScol(final String script) {
		logger.debug("Mise a jour typeSiScol");

		/* Verfication type Siscol original */
		if (siScolDefault == null || siScolDefault.length() == 0) {
			throw new RuntimeException(
				"Erreur sur le paramètre siscol.default, il ne doit pas être null et etre de size = 1. Valeur actuelle = '"
					+ siScolDefault
					+ "'");
		}

		logger.debug("Valeur appliquée = '" + siScolDefault + "'");

		final EntityManager em = entityManagerFactoryEcandidat.createEntityManager();
		final EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			final InputStream inputStream = this.getClass().getResourceAsStream("/db/update/" + script + ".sql");
			final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			while (bufferedReader.ready()) {
				String line = bufferedReader.readLine();
				line = line.replaceAll("typSiscol", siScolDefault);
				logger.debug(line);
				final Query query = em.createNativeQuery(line);
				query.executeUpdate();
			}
			tx.commit();
			em.close();
		} catch (final Exception e) {
			tx.rollback();
			em.close();
			throw new RuntimeException(e);
		}
		logger.debug("Fin Mise a jour typeSiScol");
	}

}
