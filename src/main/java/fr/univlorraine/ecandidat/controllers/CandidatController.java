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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse_;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat_;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Civilite;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.entities.ecandidat.HistoNumDossier;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.siscol.WSAdresse;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.repositories.CandidatRepository;
import fr.univlorraine.ecandidat.repositories.CompteMinimaRepository;
import fr.univlorraine.ecandidat.repositories.HistoNumDossierRepository;
import fr.univlorraine.ecandidat.services.ldap.PeopleLdap;
import fr.univlorraine.ecandidat.services.security.PasswordHashService;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.CustomException;
import fr.univlorraine.ecandidat.utils.ListenerUtils.AdresseListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatAdminListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.InfoPersoListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.mail.CptMinMailBean;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.views.windows.CandidatAdminDeleteWindow;
import fr.univlorraine.ecandidat.views.windows.CandidatAdminWindow;
import fr.univlorraine.ecandidat.views.windows.CandidatAdresseWindow;
import fr.univlorraine.ecandidat.views.windows.CandidatCompteMinimaWindow;
import fr.univlorraine.ecandidat.views.windows.CandidatInfoPersoWindow;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;

/** Gestion de l'entité candidat
 *
 * @author Kevin Hergalant */
@Component
public class CandidatController {
	/* Injections */
	private Logger logger = LoggerFactory.getLogger(CandidatController.class);
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockCandidatController lockCandidatController;
	@Resource
	private transient LdapController ldapController;
	@Resource
	private transient CompteMinimaRepository compteMinimaRepository;
	@Resource
	private transient HistoNumDossierRepository histoNumDossierRepository;
	@Resource
	private transient CandidatRepository candidatRepository;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient DemoController demoController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient CandidatPieceController candidatPieceController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient DateTimeFormatter formatterDate;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	@Value("${enableSyncByINE:}")
	private transient Boolean enableSyncByINE;

	/** @param cptMin
	 * @return le libelle d'un candidat présent dans les fenetres */
	public Object getLibelleTitle(final CompteMinima cptMin) {
		if (cptMin.getCandidat() == null) {
			return cptMin.getNomCptMin() + " " + cptMin.getPrenomCptMin() + " (" + cptMin.getNumDossierOpiCptMin() + ")";
		} else {
			Candidat candidat = cptMin.getCandidat();
			return candidat.getNomPatCandidat() + " " + candidat.getPrenomCandidat() + " (" + cptMin.getNumDossierOpiCptMin() + ")";
		}
	}

	/** Creation d'un compte a minima */
	public void createCompteMinima(final Boolean createByGest) {
		CompteMinima cptMin = new CompteMinima();
		if (!createByGest) {
			String login = userController.getCurrentNoDossierCptMinOrLogin();
			if (login != null && !login.equals("")) {
				cptMin.setLoginCptMin(login);
				PeopleLdap p = ldapController.findByPrimaryKey(login);
				if (p != null) {
					if (p.getSupannEtuId() != null && !p.getSupannEtuId().equals("")) {
						cptMin.setSupannEtuIdCptMin(p.getSupannEtuId());
					}
					if (p.getSn() != null && !p.getSn().equals("")) {
						cptMin.setNomCptMin(p.getSn());
					}
					if (p.getGivenName() != null && !p.getGivenName().equals("")) {
						cptMin.setPrenomCptMin(p.getGivenName());
					}
					if (p.getMail() != null && !p.getMail().equals("")) {
						cptMin.setMailPersoCptMin(p.getMail());
					}
				}
			}
		}
		CandidatCompteMinimaWindow cptMinWin = new CandidatCompteMinimaWindow(cptMin, false, createByGest);
		cptMinWin.addCompteMinimaWindowListener(compteMinima -> {
			CompteMinima cpt = saveCompteMinima(compteMinima);
			if (cpt != null && !createByGest) {
				userController.alimenteSecurityUserCptMin(cpt);
				MainUI.getCurrent().reconstructMainMenu();
			}
		});
		UI.getCurrent().addWindow(cptMinWin);
	}

	/** Enregistre un compte à minima
	 *
	 * @param cptMin
	 */
	public void simpleSaveCptMin(final CompteMinima cptMin) {
		compteMinimaRepository.save(cptMin);
	}

	/** @return le compte à minima d'un utilisateur */
	public CompteMinima getCompteMinima() {
		String noDossier = userController.getNoDossierOPI();
		if (noDossier != null) {
			return searchCptMinByNumDossier(noDossier);
		}
		return null;
	}

	/** @return le compte a minima sur toutes les campagnes */
	public CompteMinima getCompteMinimaForAllCampagne() {
		String noDossier = userController.getNoDossierOPI();
		if (noDossier != null) {
			return compteMinimaRepository.findByNumDossierOpiCptMin(noDossier);
		}
		return null;
	}

	/** Renvoi un eventuel message d'erreur en cas de cptMin à null
	 *
	 * @param cptMin
	 * @return message d'erreur en cas de cptMin à null ou null */
	public String getErrorView(final CompteMinima cptMin) {
		if (cptMin == null) {
			return applicationContext.getMessage("erreurView.title", null, UI.getCurrent().getLocale());
		}
		if (cptMin.getTemValidCptMin() == false || cptMin.getTemValidMailCptMin() == false) {
			return applicationContext.getMessage("compteMinima.connect.valid.error", null, UI.getCurrent().getLocale());
		}
		return null;
	}

	/** Renvoi un eventuel message d'erreur en cas de cptMin à null
	 *
	 * @param cptMin
	 * @return message d'erreur en cas de cptMin à null ou null */
	public String getErrorViewForAdmin(final CompteMinima cptMin) {
		if (cptMin == null) {
			return applicationContext.getMessage("erreurView.title", null, UI.getCurrent().getLocale());
		}
		return null;
	}

	/** Renvoi un eventuel message d'erreur en cas de candidat à null
	 *
	 * @param candidat
	 * @return message d'erreur en cas de cptMin à null ou null */
	public String getErrorCandidat(final Candidat candidat) {
		if (candidat == null) {
			return applicationContext.getMessage("candidat.error", null, UI.getCurrent().getLocale());
		}

		return null;
	}

	/** @param cptMin
	 * @param ressource
	 * @return un message d'erreur si la ressource est lockée */
	public String getLockError(final CompteMinima cptMin, final String ressource) {
		/* Verrou */
		if (!lockCandidatController.getLock(cptMin, ressource)) {
			return applicationContext.getMessage("lock.message.candidat", null, UI.getCurrent().getLocale());
		}
		return null;
	}

	/** @param cptMin
	 * @return un message d'erreur si les ressources candidat sont lockées */
	public String getLockErrorFull(final CompteMinima cptMin) {
		if (getLockError(cptMin, ConstanteUtils.LOCK_INFOS_PERSO) != null || getLockError(cptMin, ConstanteUtils.LOCK_ADRESSE) != null || getLockError(cptMin, ConstanteUtils.LOCK_BAC) != null
				|| getLockError(cptMin, ConstanteUtils.LOCK_CURSUS_EXTERNE) != null || getLockError(cptMin, ConstanteUtils.LOCK_FORMATION_PRO) != null
				|| getLockError(cptMin, ConstanteUtils.LOCK_STAGE) != null
		// || getLockError(cptMin,ConstanteUtils.LOCK_CANDIDATURE)!=null
		) {
			unlockCandidatFull(cptMin);
			return applicationContext.getMessage("lock.message.candidat", null, UI.getCurrent().getLocale());
		}
		return null;
	}

	/** Verifie qu'une ressource est lockée
	 *
	 * @return true si oui, false sinon */
	private Boolean isRessourceLocked(final CompteMinima cptMin, final String ressource) {
		return lockCandidatController.checkLock(cptMin, ressource);
	}

	/** Lock d'une ressource pour un compte
	 *
	 * @param cptMin
	 */
	public void unlockCandidatRessource(final CompteMinima cptMin, final String ressource) {
		lockCandidatController.releaseLock(cptMin, ressource);
	}

	/** UnLock d'une ressource pour un compte
	 *
	 * @param cptMin
	 */
	public void unlockCandidatFull(final CompteMinima cptMin) {
		lockCandidatController.releaseLock(cptMin, ConstanteUtils.LOCK_INFOS_PERSO);
		lockCandidatController.releaseLock(cptMin, ConstanteUtils.LOCK_ADRESSE);
		lockCandidatController.releaseLock(cptMin, ConstanteUtils.LOCK_BAC);
		lockCandidatController.releaseLock(cptMin, ConstanteUtils.LOCK_CURSUS_EXTERNE);
		lockCandidatController.releaseLock(cptMin, ConstanteUtils.LOCK_FORMATION_PRO);
		lockCandidatController.releaseLock(cptMin, ConstanteUtils.LOCK_STAGE);
		// lockController.releaseLock(new LockCandidat(cptMin,
		// ConstanteUtils.LOCK_CANDIDATURE));
	}

	/** Enregistre un compte à minima
	 *
	 * @param cptMin
	 * @return le compte enregistré */
	private CompteMinima saveCompteMinima(CompteMinima cptMin) {
		// Generateur de mot de passe
		PasswordHashService passwordHashUtils = PasswordHashService.getCurrentImplementation();

		Campagne campagne = campagneController.getCampagneActive();
		if (campagne == null) {
			Notification.show(applicationContext.getMessage("compteMinima.camp.error", null, UI.getCurrent().getLocale()), Type.ERROR_MESSAGE);
			return null;
		}
		cptMin.setCampagne(campagne);
		String prefix = parametreController.getPrefixeNumDossCpt();
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
		try {
			cptMin.setPwdCptMin(passwordHashUtils.createHash(pwd));
			cptMin.setTypGenCptMin(passwordHashUtils.getType());
		} catch (CustomException e) {
			Notification.show(applicationContext.getMessage("compteMinima.pwd.error", null, UI.getCurrent().getLocale()), Type.ERROR_MESSAGE);
			return null;
		}

		/* La date avant destruction */
		LocalDateTime datValid = LocalDateTime.now();
		Integer nbJourToKeep = parametreController.getNbJourKeepCptMin();
		datValid = datValid.plusDays(nbJourToKeep);
		datValid = LocalDateTime.of(datValid.getYear(), datValid.getMonth(), datValid.getDayOfMonth(), 23, 0, 0);

		cptMin.setDatFinValidCptMin(datValid);

		try {
			cptMin = saveBaseCompteMinima(cptMin, campagne);
		} catch (Exception ex) {
			logger.error(applicationContext.getMessage("compteMinima.numdossier.error", null, UI.getCurrent().getLocale()) + " numDossier=" + numDossierGenere, ex);
			Notification.show(applicationContext.getMessage("compteMinima.numdossier.error", null, UI.getCurrent().getLocale()), Type.ERROR_MESSAGE);
			return null;
		}

		CptMinMailBean mailBean = new CptMinMailBean(cptMin.getPrenomCptMin(), cptMin.getNomCptMin(), cptMin.getNumDossierOpiCptMin(), pwd, getLienValidation(numDossierGenere), campagneController.getLibelleCampagne(cptMin.getCampagne(), getCodLangueCptMin(cptMin)), formatterDate.format(cptMin.getDatFinValidCptMin()));
		mailController.sendMailByCod(cptMin.getMailPersoCptMin(), NomenclatureUtils.MAIL_CPT_MIN, mailBean, null, getCodLangueCptMin(cptMin));
		Notification.show(applicationContext.getMessage("compteMinima.create.success", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		return cptMin;
	}

	private String getLienValidation(final String numDossier) {
		String base64encodedString = Base64.getUrlEncoder().withoutPadding().encodeToString(numDossier.getBytes());
		return loadBalancingController.getApplicationPathForCandidat() + "rest/candidat/dossier/" + base64encodedString;
	}

	/** @param cptMin
	 * @param campagne
	 * @return le compte a minima enregistre
	 * @throws Exception
	 */
	@Transactional
	private CompteMinima saveBaseCompteMinima(CompteMinima cptMin, final Campagne campagne) throws Exception {
		String numDossier = cptMin.getNumDossierOpiCptMin();
		if (numDossier == null || numDossier.equals("")) {
			throw new Exception("numdossier null");
		}
		/* Passage des noms prénoms en capital */
		cptMin.setNomCptMin(MethodUtils.cleanForApogee(cptMin.getNomCptMin()));
		cptMin.setPrenomCptMin(MethodUtils.cleanForApogee(cptMin.getPrenomCptMin()));
		/* Enregistrement de l'historique */
		histoNumDossierRepository.saveAndFlush(new HistoNumDossier(numDossier, campagne.getCodCamp()));
		/* Enregistrement du compte */
		cptMin = compteMinimaRepository.saveAndFlush(cptMin);
		return cptMin;
	}

	/** Vérifie qu'un dossier existe
	 *
	 * @param numDossier
	 * @return true si le numDossier existe deja */
	private Boolean isNumDossierExist(final String numDossier) {
		CompteMinima cptMin = compteMinimaRepository.findByNumDossierOpiCptMin(numDossier);
		if (cptMin != null || histoNumDossierRepository.exists(numDossier)) {
			return true;
		}
		return false;
	}

	/** Verif qu'un dossier avec l'adresse mail existe
	 *
	 * @param eMail
	 * @return le compte a minima */
	public CompteMinima searchCptMinByEMail(final String eMail) {
		Campagne campagneEnCours = campagneController.getCampagneActive();
		if (campagneEnCours == null) {
			return null;
		}
		return compteMinimaRepository.findByMailPersoCptMinIgnoreCaseAndCampagneCodCamp(eMail, campagneEnCours.getCodCamp());
	}

	/** Cherche un cptMin par le login CAS
	 *
	 * @param username
	 * @return le compte a minima */
	public CompteMinima searchCptMinByLogin(final String username) {
		Campagne campagneEnCours = campagneController.getCampagneActive();
		if (campagneEnCours == null) {
			return null;
		}
		return compteMinimaRepository.findByLoginCptMinIgnoreCaseAndCampagneCodCamp(username, campagneEnCours.getCodCamp());
	}

	/** Cherche un cptMin par son numero de dossier
	 *
	 * @param numDossier
	 * @return le compte a minima */
	public CompteMinima searchCptMinByNumDossier(final String numDossier) {
		Campagne campagneEnCours = campagneController.getCampagneActive();
		if (campagneEnCours == null) {
			return null;
		}
		return compteMinimaRepository.findByNumDossierOpiCptMinAndCampagneCodCamp(numDossier, campagneEnCours.getCodCamp());
	}

	/** Initialise le pwd du compte
	 *
	 * @param eMail
	 * @return true si tout se passe bien */
	public Boolean initPasswordOrActivationCode(final String eMail, final String mode) {
		// Generateur de mot de passe
		PasswordHashService passwordHashUtils = PasswordHashService.getCurrentImplementation();

		CompteMinima cptMin = searchCptMinByEMail(eMail);
		if (cptMin == null) {
			Notification.show(applicationContext.getMessage("compteMinima.id.oublie.mail.err", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return false;
		}
		String pwd = passwordHashUtils.generateRandomPassword(ConstanteUtils.GEN_SIZE, ConstanteUtils.GEN_PWD);
		try {
			cptMin.setPwdCptMin(passwordHashUtils.createHash(pwd));
			cptMin.setTypGenCptMin(passwordHashUtils.getType());
		} catch (CustomException e) {
			Notification.show(applicationContext.getMessage("compteMinima.pwd.error", null, UI.getCurrent().getLocale()), Type.ERROR_MESSAGE);
			return false;
		}
		compteMinimaRepository.save(cptMin);

		if (mode.equals(ConstanteUtils.FORGOT_MODE_ID_OUBLIE)) {
			CptMinMailBean mailBean = new CptMinMailBean(cptMin.getPrenomCptMin(), cptMin.getNomCptMin(), cptMin.getNumDossierOpiCptMin(), pwd, null, campagneController.getLibelleCampagne(cptMin.getCampagne(), getCodLangueCptMin(cptMin)), null);
			mailController.sendMailByCod(cptMin.getMailPersoCptMin(), NomenclatureUtils.MAIL_CPT_MIN_ID_OUBLIE, mailBean, null, getCodLangueCptMin(cptMin));
			Notification.show(applicationContext.getMessage("compteMinima.id.oublie.success", null, UI.getCurrent().getLocale()), Type.HUMANIZED_MESSAGE);
		} else {
			CptMinMailBean mailBean = new CptMinMailBean(cptMin.getPrenomCptMin(), cptMin.getNomCptMin(), cptMin.getNumDossierOpiCptMin(), pwd, getLienValidation(cptMin.getNumDossierOpiCptMin()), campagneController.getLibelleCampagne(cptMin.getCampagne(), getCodLangueCptMin(cptMin)), formatterDate.format(cptMin.getDatFinValidCptMin()));
			mailController.sendMailByCod(cptMin.getMailPersoCptMin(), NomenclatureUtils.MAIL_CPT_MIN, mailBean, null, getCodLangueCptMin(cptMin));
			Notification.show(applicationContext.getMessage("compteMinima.code.oublie.success", null, UI.getCurrent().getLocale()), Type.HUMANIZED_MESSAGE);
		}

		return true;
	}

	/** @return une liste de données perso à afficher */
	public List<SimpleTablePresentation> getInformationsPerso(final Candidat candidat) {
		List<SimpleTablePresentation> liste = new ArrayList<>();
		liste.add(new SimpleTablePresentation(1, Candidat_.siScolPaysNat.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.siScolPaysNat.getName(), null, UI.getCurrent().getLocale()), candidat.getSiScolPaysNat().getLibNat()));
		liste.add(new SimpleTablePresentation(2, Candidat_.civilite.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.civilite.getName(), null, UI.getCurrent().getLocale()), candidat.getCivilite().getCodCiv()));
		liste.add(new SimpleTablePresentation(3, Candidat_.nomPatCandidat.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.nomPatCandidat.getName(), null, UI.getCurrent().getLocale()), candidat.getNomPatCandidat()));
		liste.add(new SimpleTablePresentation(4, Candidat_.nomUsuCandidat.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.nomUsuCandidat.getName(), null, UI.getCurrent().getLocale()), candidat.getNomUsuCandidat()));
		liste.add(new SimpleTablePresentation(5, Candidat_.prenomCandidat.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.prenomCandidat.getName(), null, UI.getCurrent().getLocale()), candidat.getPrenomCandidat()));
		liste.add(new SimpleTablePresentation(6, Candidat_.autrePrenCandidat.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.autrePrenCandidat.getName(), null, UI.getCurrent().getLocale()), candidat.getAutrePrenCandidat()));
		liste.add(new SimpleTablePresentation(7, Candidat_.ineCandidat.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.ineCandidat.getName(), null, UI.getCurrent().getLocale()), candidat.getIneCandidat()));
		liste.add(new SimpleTablePresentation(7, Candidat_.cleIneCandidat.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.cleIneCandidat.getName(), null, UI.getCurrent().getLocale()), candidat.getCleIneCandidat()));
		liste.add(new SimpleTablePresentation(8, Candidat_.telCandidat.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.telCandidat.getName(), null, UI.getCurrent().getLocale()), candidat.getTelCandidat()));
		liste.add(new SimpleTablePresentation(9, Candidat_.telPortCandidat.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.telPortCandidat.getName(), null, UI.getCurrent().getLocale()), candidat.getTelPortCandidat()));
		liste.add(new SimpleTablePresentation(10, Candidat_.datNaissCandidat.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.datNaissCandidat.getName(), null, UI.getCurrent().getLocale()), formatterDate.format(candidat.getDatNaissCandidat())));
		liste.add(new SimpleTablePresentation(11, Candidat_.siScolPaysNaiss.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.siScolPaysNaiss.getName(), null, UI.getCurrent().getLocale()), candidat.getSiScolPaysNaiss().getLibPay()));
		liste.add(new SimpleTablePresentation(12, Candidat_.siScolDepartement.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.siScolDepartement.getName(), null, UI.getCurrent().getLocale()), candidat.getSiScolDepartement() == null ? null : candidat.getSiScolDepartement().getGenericLibelle()));
		liste.add(new SimpleTablePresentation(13, Candidat_.libVilleNaissCandidat.getName(), applicationContext.getMessage("infoperso.table."
				+ Candidat_.libVilleNaissCandidat.getName(), null, UI.getCurrent().getLocale()), candidat.getLibVilleNaissCandidat()));
		if (cacheController.getLangueEnServiceWithoutDefault().size() > 0) {
			liste.add(new SimpleTablePresentation(14, Candidat_.langue.getName(), applicationContext.getMessage("infoperso.table."
					+ Candidat_.langue.getName(), null, UI.getCurrent().getLocale()), candidat.getLangue()));
		}
		return liste;
	}

	/** @return une liste de données adresse à afficher */
	public List<SimpleTablePresentation> getInformationsAdresse(final Adresse adresse) {
		List<SimpleTablePresentation> liste = new ArrayList<>();
		if (adresse == null) {
			return liste;
		} else {
			liste.add(new SimpleTablePresentation(1, Adresse_.siScolPays.getName(), applicationContext.getMessage("adresse."
					+ Adresse_.siScolPays.getName(), null, UI.getCurrent().getLocale()), adresse.getSiScolPays().getLibPay()));
			if (adresse.getSiScolCommune() == null) {
				liste.add(new SimpleTablePresentation(2, Adresse_.libComEtrAdr.getName(), applicationContext.getMessage("adresse."
						+ Adresse_.libComEtrAdr.getName(), null, UI.getCurrent().getLocale()), adresse.getLibComEtrAdr()));
			} else {
				liste.add(new SimpleTablePresentation(2, Adresse_.codBdiAdr.getName(), applicationContext.getMessage("adresse."
						+ Adresse_.codBdiAdr.getName(), null, UI.getCurrent().getLocale()), adresse.getCodBdiAdr()));
				liste.add(new SimpleTablePresentation(3, Adresse_.siScolCommune.getName(), applicationContext.getMessage("adresse."
						+ Adresse_.siScolCommune.getName(), null, UI.getCurrent().getLocale()), adresse.getSiScolCommune().getLibCom()));
			}
			liste.add(new SimpleTablePresentation(4, Adresse_.adr1Adr.getName(), applicationContext.getMessage("adresse."
					+ Adresse_.adr1Adr.getName(), null, UI.getCurrent().getLocale()), adresse.getAdr1Adr()));
			liste.add(new SimpleTablePresentation(5, Adresse_.adr2Adr.getName(), applicationContext.getMessage("adresse."
					+ Adresse_.adr2Adr.getName(), null, UI.getCurrent().getLocale()), adresse.getAdr2Adr()));
			liste.add(new SimpleTablePresentation(6, Adresse_.adr3Adr.getName(), applicationContext.getMessage("adresse."
					+ Adresse_.adr3Adr.getName(), null, UI.getCurrent().getLocale()), adresse.getAdr3Adr()));
		}
		return liste;
	}

	/** Batch pour le nettoyage des comptes a minima */
	public void nettoyageCptMinInvalides() {
		List<CompteMinima> listeToDelete = compteMinimaRepository.findByTemValidCptMinAndDatFinValidCptMinBefore(false, LocalDateTime.now());
		listeToDelete.forEach(e -> {
			nettoyageCptMinInvalide(e);
		});
	}

	/** Supprime un compte à minima
	 *
	 * @param cptMin
	 */
	@Transactional
	private void nettoyageCptMinInvalide(final CompteMinima cptMin) {
		if (cptMin.getCandidat() == null) {
			deleteCandidatBase(cptMin);
		}
	}

	/** Enregistre un candidat
	 *
	 * @param candidat
	 * @param individu
	 * @param listener
	 */
	private Candidat saveCandidat(Candidat candidat, final WSIndividu individu, final CandidatAdminListener listener) {
		candidat = candidatRepository.save(candidat);
		CompteMinima cptMin = candidat.getCompteMinima();
		if (individu != null && individu.getCodEtu() != null && !String.valueOf(individu.getCodEtu()).equals(cptMin.getSupannEtuIdCptMin())) {
			cptMin.setSupannEtuIdCptMin(String.valueOf(individu.getCodEtu()));
			cptMin = compteMinimaRepository.save(cptMin);
			candidat.setCompteMinima(cptMin);
			if (listener != null) {
				listener.cptMinModified(compteMinimaRepository.save(cptMin));
			}
		}
		return candidat;
	}

	/** Edition d'un candidat
	 *
	 * @param cptMin
	 */
	public void editCandidat(final CompteMinima cptMin, final InfoPersoListener listener) {
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		String lockError = getLockError(cptMin, ConstanteUtils.LOCK_INFOS_PERSO);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}
		Candidat candidat = cptMin.getCandidat();
		if (candidat == null) {
			candidat = new Candidat(cptMin, cacheController.getLangueDefault());
		}

		CandidatInfoPersoWindow window = new CandidatInfoPersoWindow(candidat);
		window.addCandidatWindowListener((cand, individuApogee, needToDeleteDataApogee) -> {
			if (isLockedForImportApo(cand.getCompteMinima())) {
				return;
			}
			Boolean langueChanged = false;
			if (userController.isCandidat()) {
				langueChanged = i18nController.changeLangue(cand.getLangue());
			}
			if (individuApogee != null && individuApogee.getAdresse() != null) {
				Adresse adresse = getAdresseByApogeeData(individuApogee.getAdresse());
				Adresse lastAdresse = cand.getAdresse();
				if (lastAdresse != null) {
					adresse.setIdAdr(lastAdresse.getIdAdr());
				}
				cand.setAdresse(adresse);
			} else if (needToDeleteDataApogee) {
				cand.setAdresse(null);
			}

			/* Calcul de l'INE et clé */
			String ine = MethodUtils.getIne(cand.getIneAndKey());
			String cle = MethodUtils.getCleIne(cand.getIneAndKey());

			/* On passe tout en capitale */
			cand.setNomPatCandidat(MethodUtils.cleanForApogee(cand.getNomPatCandidat()));
			cand.setNomUsuCandidat(MethodUtils.cleanForApogee(cand.getNomUsuCandidat()));
			cand.setPrenomCandidat(MethodUtils.cleanForApogee(cand.getPrenomCandidat()));
			cand.setAutrePrenCandidat(MethodUtils.cleanForApogee(cand.getAutrePrenCandidat()));
			cand.setIneCandidat(MethodUtils.cleanForApogee(ine));
			cand.setCleIneCandidat(MethodUtils.cleanForApogee(cle));
			cand.setLibVilleNaissCandidat(MethodUtils.cleanForApogee(cand.getLibVilleNaissCandidat()));

			Candidat candidatSave = saveCandidat(cand, individuApogee, null);
			candidatSave.setCandidatBacOuEqu(candidatParcoursController.getBacByApogeeData((individuApogee != null) ? individuApogee.getBac() : null, candidatSave, needToDeleteDataApogee));
			if (parametreController.getIsGetCursusInterne()) {
				candidatSave.setCandidatCursusInternes(candidatParcoursController.getCursusInterne((individuApogee != null) ? individuApogee.getListCursusInterne()
						: null, candidatSave, needToDeleteDataApogee));
			}

			/* Synchro des pieces */
			if (individuApogee != null) {
				/* Individu Apogée non null on synchronise tout */
				try {
					candidatPieceController.synchronizePJCandidat(candidatSave);
				} catch (Exception e) {
				}
			} else if (needToDeleteDataApogee) {
				/* On supprime les pièces */
				candidatPieceController.deletePJCandidat(candidatSave);
			}

			listener.infoPersoModified(candidatSave, langueChanged);
		});
		UI.getCurrent().addWindow(window);
	}

	/** @param adresse
	 * @return une adresse suivant les données apo */
	private Adresse getAdresseByApogeeData(final WSAdresse adr) {
		SiScolPays pays = tableRefController.getPaysByCode(adr.getCodPay());
		SiScolCommune commune = tableRefController.getCommuneByCodePostalAndCodeCom(adr.getCodBdi(), adr.getCodCom());
		return new Adresse(adr.getLibAd1(), adr.getLibAd2(), adr.getLibAd3(), adr.getCodBdi(), adr.getLibAde(), commune, pays);
	}

	/** Edite le mail du candidat
	 *
	 * @param cptMin
	 */
	public void editMail(final CompteMinima cptMin) {
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		String lockError = getLockError(cptMin, ConstanteUtils.LOCK_INFOS_PERSO);
		if (lockError != null) {
			Notification.show(lockError, Type.WARNING_MESSAGE);
			return;
		}
		CandidatCompteMinimaWindow cptMinWin = new CandidatCompteMinimaWindow(cptMin, true, false);
		cptMinWin.addCompteMinimaWindowListener(e -> {
			cptMin.setTemValidMailCptMin(false);
			compteMinimaRepository.save(cptMin);
			String base64encodedString = Base64.getUrlEncoder().withoutPadding().encodeToString(cptMin.getNumDossierOpiCptMin().getBytes());
			String path = loadBalancingController.getApplicationPathForCandidat() + "rest/candidat/mail/" + base64encodedString;
			CptMinMailBean mailBean = new CptMinMailBean(cptMin.getPrenomCptMin(), cptMin.getNomCptMin(), cptMin.getNumDossierOpiCptMin(), null, path, campagneController.getLibelleCampagne(cptMin.getCampagne(), getCodLangueCptMin(cptMin)), null);

			mailController.sendMailByCod(cptMin.getMailPersoCptMin(), NomenclatureUtils.MAIL_CPT_MIN_MOD_MAIL, mailBean, null, getCodLangueCptMin(cptMin));
			Notification.show(applicationContext.getMessage("compteMinima.editmail.notif", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			MainUI.getCurrent().reconstructMainMenu();
		});
		UI.getCurrent().addWindow(cptMinWin);
	}

	/** @param cptMin
	 * @return la langue d'un compte a minima */
	private String getCodLangueCptMin(final CompteMinima cptMin) {
		if (cptMin.getCandidat() != null) {
			return cptMin.getCandidat().getLangue().getCodLangue();
		} else {
			return UI.getCurrent().getLocale().getLanguage();
		}
	}

	/** Edite l'adresse d'un candidat
	 *
	 * @param cptMin
	 * @param listener
	 */
	public void editAdresse(final CompteMinima cptMin, final AdresseListener listener) {
		/* Verrou --> normalement le lock est géré en amont mais on vérifie qd même */
		String lockError = getLockError(cptMin, ConstanteUtils.LOCK_ADRESSE);
		if (lockError != null) {
			Notification.show(lockError, Type.ERROR_MESSAGE);
			return;
		}
		Candidat candidat = cptMin.getCandidat();
		Adresse adresse = candidat.getAdresse();
		if (adresse == null) {
			adresse = new Adresse();
		}

		CandidatAdresseWindow window = new CandidatAdresseWindow(adresse);
		window.addAdresseWindowListener(e -> {
			listener.adresseModified(saveAdresse(candidat, e));
		});
		UI.getCurrent().addWindow(window);
	}

	/** Enregistre l'adresse
	 *
	 * @param candidat
	 * @param adresse
	 * @return le candidat avec son adresse */
	private Candidat saveAdresse(final Candidat candidat, final Adresse adresse) {
		candidat.setAdresse(adresse);
		return candidatRepository.save(candidat);
	}

	/** @param cptMin
	 * @return false si la ressource est lockée */
	public Boolean isLockedForImportApo(final CompteMinima cptMin) {
		Boolean isLock = isRessourceLocked(cptMin, ConstanteUtils.LOCK_INFOS_PERSO);
		if (!isLock) {
			isLock = isRessourceLocked(cptMin, ConstanteUtils.LOCK_BAC);
		}
		if (!isLock) {
			isLock = isRessourceLocked(cptMin, ConstanteUtils.LOCK_ADRESSE);
		}
		if (isLock) {
			Notification.show(applicationContext.getMessage("lock.message.candidat", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return true;
		}
		return false;
	}

	/** Verifie si l'INE est obligatoire suivant la nationalité
	 *
	 * @param nationalite
	 * @return true si c'est obligatoire */
	public Boolean getINEObligatoire(final SiScolPays nationalite) {
		if (parametreController.getIsIneObligatoireFr() && nationalite != null && nationalite.getCodPay().equals(ConstanteUtils.PAYS_CODE_FRANCE)) {
			return true;
		}
		return false;
	}

	/** @param codTypDepPayNai
	 * @param codDepPayNai
	 * @return le pays de naissance */
	public SiScolPays getPaysNaissance(final String codTypDepPayNai, final String codDepPayNai) {
		if (codTypDepPayNai != null && codTypDepPayNai.equals(ConstanteUtils.COD_TYP_PAY_DPT_DEPARTEMENT)) {
			return cacheController.getPaysFrance();
		} else if (codTypDepPayNai != null && codTypDepPayNai.equals(ConstanteUtils.COD_TYP_PAY_DPT_PAYS)) {
			return tableRefController.getPaysByCode(codDepPayNai);
		} else {
			return cacheController.getPaysFrance();
		}
	}

	/** @param codTypDepPayNai
	 * @param codDepPayNai
	 * @return le departement de naissance */
	public SiScolDepartement getDepartementNaissance(final String codTypDepPayNai, final String codDepPayNai) {
		if (codTypDepPayNai != null && codTypDepPayNai.equals(ConstanteUtils.COD_TYP_PAY_DPT_DEPARTEMENT)) {
			return tableRefController.getDepartementByCode(codDepPayNai);
		} else {
			return null;
		}
	}

	/** @param codCiv
	 * @return la civilite */
	public Civilite getCiviliteByCodeApo(final String codCiv) {
		if (codCiv != null) {
			return tableRefController.getCiviliteByCodeApo(codCiv);
		} else {
			return null;
		}
	}

	/** Recupere les infos du candidat
	 *
	 * @param codEtu
	 * @param ine
	 * @param cleIne
	 * @return l'individu apogee chargé
	 * @throws SiScolException
	 */
	public WSIndividu recupInfoCandidat(final String codEtu, final String ine, final String cleIne) throws SiScolException {
		if (demoController.getDemoMode()) {
			return demoController.recupInfoEtudiant(ine);
		}
		WSIndividu ind = null;
		/* En priorité on utilise le codEtu */
		if (codEtu != null) {
			ind = siScolService.getIndividu(codEtu, null, null);
		} else if (ine != null && parametreController.getIsUtiliseSyncIne()) {
			ind = siScolService.getIndividu(null, ine, cleIne);
		}
		return ind;
	}

	public WSIndividu recupInfoCandidat(final String codEtu, final String ineAndKeyFieldValue) throws SiScolException {
		if (demoController.getDemoMode()) {
			return demoController.recupInfoEtudiant(ineAndKeyFieldValue);
		}
		WSIndividu ind = null;
		/* En priorité on utilise le codEtu */
		if (codEtu != null) {
			ind = siScolService.getIndividu(codEtu, null, null);
		} else if (ineAndKeyFieldValue != null && parametreController.getIsUtiliseSyncIne()) {
			String ine = MethodUtils.getIne(ineAndKeyFieldValue);
			String cleIne = MethodUtils.getCleIne(ineAndKeyFieldValue);
			ind = siScolService.getIndividu(null, ine, cleIne);
		}
		return ind;
	}

	/** fonction assurant la vérification d'un numéro INE passé en paramètre
	 *
	 * @param theStudentINEAndKey
	 * @return boolean true si l'ine est ok
	 * @throws Exception
	 */
	public boolean checkStudentINE(String theStudentINEAndKey) throws Exception {
		try {
			if (theStudentINEAndKey == null || theStudentINEAndKey.length() == 0) {
				return true;
			}

			if (theStudentINEAndKey != null) {
				theStudentINEAndKey = theStudentINEAndKey.replaceAll(" ", "");
			}

			boolean isIneCorrect = true;

			//
			if (MethodUtils.isINES(theStudentINEAndKey)) {
				String ine = MethodUtils.getIne(theStudentINEAndKey);
				String cleIne = MethodUtils.getCleIne(theStudentINEAndKey);
				isIneCorrect = siScolService.checkStudentINES(ine, cleIne);
			} else if (MethodUtils.checkBEA23(theStudentINEAndKey) && (!MethodUtils.checkNNE36(theStudentINEAndKey))) {
				isIneCorrect = true;
			} else if ((!MethodUtils.checkBEA23(theStudentINEAndKey)) && (MethodUtils.checkNNE36(theStudentINEAndKey))) {
				isIneCorrect = true;
			} else if ((!MethodUtils.checkBEA23(theStudentINEAndKey)) && (!MethodUtils.checkNNE36(theStudentINEAndKey))) {
				isIneCorrect = false;
			}

			return (isIneCorrect);
		} catch (Exception e) {
			throw e;
		}
	}

	public Boolean isINEPresent(final String ineAndKeyFieldValue, final Candidat candidat) {
		String ine = MethodUtils.getIne(ineAndKeyFieldValue);
		String cleIne = MethodUtils.getCleIne(ineAndKeyFieldValue);
		return isINEPresent(ine, cleIne, candidat);
	}

	/** Verifie que l'INE saisi n'est pas déjà existant
	 *
	 * @param ineValue
	 * @param candidat
	 * @return true si présent, false sinon */
	public Boolean isINEPresent(final String ineValue, final String cleIneValue, final Candidat candidat) {
		if (ineValue == null || ineValue.equals("")) {
			return false;
		} else {
			Campagne campagneEnCours = campagneController.getCampagneActive();
			if (campagneEnCours == null) {
				return false;
			}
			List<Candidat> liste = candidatRepository.findByIneCandidatIgnoreCaseAndCleIneCandidatIgnoreCaseAndCompteMinimaCampagneCodCamp(ineValue, cleIneValue, campagneEnCours.getCodCamp());
			if (liste.size() > 0) {
				if (candidat.getIdCandidat() == null) {
					Notification.show(applicationContext.getMessage("infoperso.ine.allready.present", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return true;
				}
				Optional<Candidat> candOpt = liste.stream().filter(e -> !e.getIdCandidat().equals(candidat.getIdCandidat())).findAny();
				if (candOpt.isPresent()) {
					Notification.show(applicationContext.getMessage("infoperso.ine.allready.present", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return true;
				}
			}
		}
		return false;
	}

	/** Verification de la date de naissance
	 *
	 * @param dateNaissance
	 * @return true si la date de naissance est correcte */
	public Boolean checkDateNaissance(final LocalDate dateNaissance) {
		if (dateNaissance == null) {
			return true;
		}
		LocalDate dateDeb = LocalDate.now().minusYears(100);
		LocalDate dateFin = LocalDate.now().minusYears(10).minusDays(1);
		if (dateNaissance.isBefore(dateDeb) || dateNaissance.isAfter(dateFin)) {
			Notification.show(applicationContext.getMessage("infoperso.datnaiss.invalid", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	/** Verifie que le supannEtuId saisi n'est pas déjà existant
	 *
	 * @param supannEtuId
	 * @param cptMin
	 * @return true si pas présent, false sinon */
	public Boolean isSupannEtuIdPresent(final String supannEtuId, final CompteMinima cptMin) {
		if (supannEtuId == null || supannEtuId.equals("")) {
			return false;
		} else {
			Campagne campagneEnCours = campagneController.getCampagneActive();
			if (campagneEnCours == null) {
				return false;
			}
			List<CompteMinima> liste = compteMinimaRepository.findBySupannEtuIdCptMinAndIdCptMinNotAndCampagneCodCamp(supannEtuId, cptMin.getIdCptMin(), campagneEnCours.getCodCamp());
			if (liste.size() > 0) {
				Notification.show(applicationContext.getMessage("candidat.etuid.allready.present", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return true;
			}
		}
		return false;
	}

	/** Verifie que le login saisi n'est pas déjà existant
	 *
	 * @param login
	 * @param cptMin
	 * @return true si pas présent, false sinon */
	public Boolean isLoginPresent(final String login, final CompteMinima cptMin) {
		if (login == null || login.equals("")) {
			return false;
		} else {
			Campagne campagneEnCours = campagneController.getCampagneActive();
			if (campagneEnCours == null) {
				return false;
			}
			List<CompteMinima> liste = compteMinimaRepository.findByLoginCptMinIgnoreCaseAndIdCptMinNotAndCampagneCodCamp(login, cptMin.getIdCptMin(), campagneEnCours.getCodCamp());
			if (liste.size() > 0) {
				Notification.show(applicationContext.getMessage("candidat.login.allready.present", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return true;
			}
		}
		return false;
	}

	/** @param value
	 * @param inclureDossierOtherYears
	 * @param exactSearch
	 * @return la liste des compte grace a un filtre pour la recherche de compte candidat */
	public List<CompteMinima> getCptMinByFilter(String value, final Boolean inclureDossierOtherYears, final Boolean exactSearch) {
		if (!exactSearch) {
			value = "%" + value + "%";
		}
		if (inclureDossierOtherYears) {
			return compteMinimaRepository.findByFilterAllYears(value, new PageRequest(0, ConstanteUtils.NB_MAX_RECH_CPT_MIN));
		} else {
			Campagne campagne = campagneController.getCampagneActive();
			if (value == null || campagne == null) {
				return new ArrayList<>();
			}
			return compteMinimaRepository.findByFilter(campagne.getCodCamp(), value, new PageRequest(0, ConstanteUtils.NB_MAX_RECH_CPT_MIN));
		}
	}

	/** @param cptMin
	 * @return les infos du compte a minima */
	public List<SimpleTablePresentation> getInfoForAdmin(final CompteMinima cptMin) {
		List<SimpleTablePresentation> liste = new ArrayList<>();
		liste.add(new SimpleTablePresentation(1, CompteMinima_.nomCptMin.getName(), applicationContext.getMessage("compteMinima.table."
				+ CompteMinima_.nomCptMin.getName(), null, UI.getCurrent().getLocale()), cptMin.getNomCptMin()));
		liste.add(new SimpleTablePresentation(2, CompteMinima_.prenomCptMin.getName(), applicationContext.getMessage("compteMinima.table."
				+ CompteMinima_.prenomCptMin.getName(), null, UI.getCurrent().getLocale()), cptMin.getPrenomCptMin()));
		liste.add(new SimpleTablePresentation(3, CompteMinima_.mailPersoCptMin.getName(), applicationContext.getMessage("compteMinima.table."
				+ CompteMinima_.mailPersoCptMin.getName(), null, UI.getCurrent().getLocale()), cptMin.getMailPersoCptMin()));
		liste.add(new SimpleTablePresentation(4, CompteMinima_.numDossierOpiCptMin.getName(), applicationContext.getMessage("compteMinima.table."
				+ CompteMinima_.numDossierOpiCptMin.getName(), null, UI.getCurrent().getLocale()), cptMin.getNumDossierOpiCptMin()));
		liste.add(new SimpleTablePresentation(5, CompteMinima_.loginCptMin.getName(), applicationContext.getMessage("compteMinima.table."
				+ CompteMinima_.loginCptMin.getName(), null, UI.getCurrent().getLocale()), cptMin.getLoginCptMin()));
		liste.add(new SimpleTablePresentation(6, CompteMinima_.supannEtuIdCptMin.getName(), applicationContext.getMessage("compteMinima.table."
				+ CompteMinima_.supannEtuIdCptMin.getName(), null, UI.getCurrent().getLocale()), cptMin.getSupannEtuIdCptMin()));
		liste.add(new SimpleTablePresentation(7, CompteMinima_.datCreCptMin.getName(), applicationContext.getMessage("compteMinima.table."
				+ CompteMinima_.datCreCptMin.getName(), null, UI.getCurrent().getLocale()), (cptMin.getDatCreCptMin() != null ? formatterDate.format(cptMin.getDatCreCptMin()) : null)));
		liste.add(new SimpleTablePresentation(8, CompteMinima_.datFinValidCptMin.getName(), applicationContext.getMessage("compteMinima.table."
				+ CompteMinima_.datFinValidCptMin.getName(), null, UI.getCurrent().getLocale()), (cptMin.getDatFinValidCptMin() != null ? formatterDate.format(cptMin.getDatFinValidCptMin()) : null)));
		liste.add(new SimpleTablePresentation(9, CompteMinima_.temValidCptMin.getName(), applicationContext.getMessage("compteMinima.table."
				+ CompteMinima_.temValidCptMin.getName(), null, UI.getCurrent().getLocale()), cptMin.getTemValidCptMin()));
		liste.add(new SimpleTablePresentation(10, CompteMinima_.temValidMailCptMin.getName(), applicationContext.getMessage("compteMinima.table."
				+ CompteMinima_.temValidMailCptMin.getName(), null, UI.getCurrent().getLocale()), cptMin.getTemValidMailCptMin()));
		liste.add(new SimpleTablePresentation(10, CompteMinima_.temFcCptMin.getName(), applicationContext.getMessage("compteMinima.table."
				+ CompteMinima_.temFcCptMin.getName(), null, UI.getCurrent().getLocale()), cptMin.getTemFcCptMin()));
		return liste;
	}

	/** Edite le compte a minima
	 *
	 * @param cptMin
	 * @param listener
	 */
	public void editAdminCptMin(final CompteMinima cptMin, final CandidatAdminListener listener) {
		CandidatAdminWindow win = new CandidatAdminWindow(cptMin);
		win.addCandidatAdminWindowListener(e -> {
			e.setNomCptMin(MethodUtils.cleanForApogee(e.getNomCptMin()));
			e.setPrenomCptMin(MethodUtils.cleanForApogee(e.getPrenomCptMin()));
			listener.cptMinModified(compteMinimaRepository.save(e));
		});
		UI.getCurrent().addWindow(win);
	}

	/** Synchronise un compte candidat avec apogée
	 *
	 * @param cptMin
	 */
	public void synchronizeCandidat(final CompteMinima cptMin, final CandidatAdminListener listener) {
		if (!parametreController.getSiScolMode().equals(ConstanteUtils.SI_SCOL_APOGEE)) {
			return;
		}
		String lockError = getLockErrorFull(cptMin);
		if (lockError != null) {
			Notification.show(lockError, Type.WARNING_MESSAGE);
			return;
		}
		ConfirmWindow win = new ConfirmWindow(applicationContext.getMessage("candidat.sync.apo.window", null, UI.getCurrent().getLocale()));
		win.addBtnOuiListener(e -> {

			Candidat candidat = cptMin.getCandidat();
			if (candidat == null) {
				candidat = new Candidat(cptMin, cacheController.getLangueDefault());
			}
			try {
				WSIndividu individu = null;
				if (cptMin != null && cptMin.getSupannEtuIdCptMin() != null && !cptMin.getSupannEtuIdCptMin().equals("")) {
					individu = recupInfoCandidat(cptMin.getSupannEtuIdCptMin(), null, null);
					if (individu != null && isINEPresent(individu.getCodNneInd(), individu.getCodCleNneInd(), candidat)) {
						Notification.show(applicationContext.getMessage("infoperso.ine.allready.present", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
						return;
					}
				} else if (candidat != null && candidat.getIneCandidat() != null && !candidat.getIneCandidat().equals("") && candidat.getCleIneCandidat() != null
						&& !candidat.getCleIneCandidat().equals("")) {
					String ine = candidat.getIneCandidat();
					String cleIne = candidat.getCleIneCandidat();
					if (ine == null || ine.equals("") || cleIne == null || cleIne.equals("")) {
						Notification.show(applicationContext.getMessage("candidat.sync.apo.ine.absent", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
						return;
					}
					if (isINEPresent(ine, cleIne, candidat)) {
						Notification.show(applicationContext.getMessage("infoperso.ine.allready.present", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
						return;
					}
					individu = recupInfoCandidat(null, ine, cleIne);
				}

				if (individu != null) {
					candidat = getCandidatByApogeeData(individu, candidat);
					if (MethodUtils.validateBean(candidat, logger)) {
						if (individu.getAdresse() != null) {
							Adresse adresse = getAdresseByApogeeData(individu.getAdresse());
							if (MethodUtils.validateBean(adresse, logger)) {
								Adresse lastAdresse = candidat.getAdresse();
								if (lastAdresse != null) {
									adresse.setIdAdr(lastAdresse.getIdAdr());
								}
								candidat.setAdresse(adresse);
							}
						} else {
							candidat.setAdresse(null);
						}
						candidat = saveCandidat(candidat, individu, listener);
						candidatParcoursController.getBacByApogeeData(individu.getBac(), candidat, true);
						if (parametreController.getIsGetCursusInterne()) {
							candidatParcoursController.getCursusInterne(individu.getListCursusInterne(), candidat, true);
						}
					} else {
						Notification.show(applicationContext.getMessage("candidat.sync.apo.etu.nok", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
						return;
					}
				} else {
					Notification.show(applicationContext.getMessage("candidat.sync.apo.etu.absent", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

			} catch (SiScolException f) {
				logger.error(applicationContext.getMessage("siscol.connect.error", null, UI.getCurrent().getLocale()), f);
				Notification.show(applicationContext.getMessage("siscol.connect.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			Notification.show(applicationContext.getMessage("candidat.sync.apo.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		});
		UI.getCurrent().addWindow(win);
	}

	/** @param dataApogee
	 * @param candidat
	 * @return un candidat construit avec les données apogée */
	private Candidat getCandidatByApogeeData(final WSIndividu individuApogee, final Candidat candidat) {
		if (individuApogee == null) {
			return null;
		}

		if (individuApogee.getIsWs()) {
			/* Champs pays naissance */
			candidat.setSiScolPaysNaiss(tableRefController.getPaysByCode(individuApogee.getCodPayNai()));

			/* Champs dpt naissance */
			candidat.setSiScolDepartement(tableRefController.getDepartementByCode(individuApogee.getCodDepNai()));
		} else {
			/* Champs pays naissance */
			candidat.setSiScolPaysNaiss(getPaysNaissance(individuApogee.getCodTypDepPayNai(), individuApogee.getCodDepPayNai()));

			/* Champs dpt naissance */
			candidat.setSiScolDepartement(getDepartementNaissance(individuApogee.getCodTypDepPayNai(), individuApogee.getCodDepPayNai()));
		}

		/* Champs pays nationalite */
		candidat.setSiScolPaysNat(tableRefController.getPaysByCode(individuApogee.getCodPayNat()));

		/* Champs nomPatCandidat */
		candidat.setNomPatCandidat(individuApogee.getLibNomPatInd());

		/* Champs nomUsuCandidat */
		candidat.setNomUsuCandidat(individuApogee.getLibNomUsuInd());

		/* Champs nomUsuCandidat */
		candidat.setPrenomCandidat(individuApogee.getLibPr1Ind());

		/* Champs autrePrenCandidat */
		candidat.setAutrePrenCandidat(individuApogee.getLibPr2Ind());

		/* Champs libVilleNaissCandidat */
		candidat.setLibVilleNaissCandidat(individuApogee.getLibVilNaiEtu());

		WSAdresse adr = individuApogee.getAdresse();
		if (adr != null) {
			/* Champs telCandidat */
			candidat.setTelCandidat(adr.getNumTel());

			/* Champs telPortCandidat */
			candidat.setTelPortCandidat(adr.getNumTelPort());
		}

		candidat.setIneCandidat(individuApogee.getCodNneInd());
		candidat.setCleIneCandidat(individuApogee.getCodCleNneInd());

		/* Champs civilite */
		candidat.setCivilite(getCiviliteByCodeApo(individuApogee.getCodCiv()));
		/* Champs civilite */
		candidat.setDatNaissCandidat(individuApogee.getDateNaiInd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

		candidat.setTemUpdatableCandidat(false);

		return candidat;
	}

	/** Supprime un candidat
	 *
	 * @param cptMin
	 * @param listener
	 */
	public void deleteCandidat(final CompteMinima cptMin, final CandidatAdminListener listener) {
		if (cptMin.getCandidat() != null && cptMin.getCandidat().getCandidatures().size() > 0) {
			Notification.show(applicationContext.getMessage("candidat.delete.has.candidature", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		CandidatAdminDeleteWindow win = new CandidatAdminDeleteWindow(applicationContext.getMessage("candidat.delete.window", null, UI.getCurrent().getLocale()));
		win.addDeleteCandidatWindowListener(sendMail -> {
			try {
				deleteCandidatBase(cptMin);
				if (sendMail) {
					CptMinMailBean mailBean = new CptMinMailBean(cptMin.getPrenomCptMin(), cptMin.getNomCptMin(), cptMin.getNumDossierOpiCptMin(), null, null, campagneController.getLibelleCampagne(cptMin.getCampagne(), getCodLangueCptMin(cptMin)), null);
					mailController.sendMailByCod(cptMin.getMailPersoCptMin(), NomenclatureUtils.MAIL_CPT_MIN_DELETE, mailBean, null, getCodLangueCptMin(cptMin));
				}
				userController.setNoDossierNomCandidat(null);
				Notification.show(applicationContext.getMessage("candidat.delete.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
				MainUI.getCurrent().buildMenuGestCand(false);
			} catch (Exception ex) {
				logger.error(applicationContext.getMessage("candidat.delete.error", null, UI.getCurrent().getLocale()), ex);
				Notification.show(applicationContext.getMessage("candidat.delete.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}
		});
		UI.getCurrent().addWindow(win);
	}

	/** Supprime un candidat
	 *
	 * @param cptMin
	 * @param listener
	 */
	public void deleteCandidatCnil(final CompteMinima cptMin, final CandidatAdminListener listener) {
		CandidatAdminDeleteWindow win = new CandidatAdminDeleteWindow();
		win.setWidth(850, Unit.PIXELS);
		String message = applicationContext.getMessage("candidat.delete.window", null, UI.getCurrent().getLocale());
		if (cptMin.getCandidat() != null) {
			message = message + "<br/><br/>";
			if (cptMin.getCandidat().getCandidatures().size() > 0) {
				message = message + applicationContext.getMessage("candidat.delete.cnil.hascand", null, UI.getCurrent().getLocale());
				message = message + "<ul>";
				for (Candidature cand : cptMin.getCandidat().getCandidatures()) {
					// définition du statut
					String statut = cand.getTypeStatut().getLibTypStatut();
					if (cand.getDatAnnulCand() != null) {
						statut = applicationContext.getMessage("cancel.label", null, UI.getCurrent().getLocale());
					}
					// definition du libellé opi
					String opi = applicationContext.getMessage("candidat.delete.cnil.cand.noopi", null, UI.getCurrent().getLocale());
					if (cand.getOpi() != null && cand.getOpi().getDatPassageOpi() != null) {
						opi = applicationContext.getMessage("candidat.delete.cnil.cand.hasopi", new Object[] {formatterDate.format(cand.getOpi().getDatPassageOpi())}, UI.getCurrent().getLocale());
					}
					message = message + applicationContext.getMessage("candidat.delete.cnil.cand", new Object[] {cand.getFormation().getLibForm(), statut, opi}, UI.getCurrent().getLocale());
				}
				message = message + "</ul>";
			} else {
				message = message + applicationContext.getMessage("candidat.delete.cnil.nocand", null, UI.getCurrent().getLocale());
			}
		}
		win.setMessage(message);
		win.addDeleteCandidatWindowListener(sendMail -> {
			try {
				if (cptMin.getCandidat() != null) {
					for (Candidature candidature : cptMin.getCandidat().getCandidatures()) {
						for (PjCand pjCand : candidature.getPjCands()) {
							candidaturePieceController.removeFileToPj(pjCand);
						}
					}
				}
				compteMinimaRepository.delete(cptMin);
				if (sendMail) {
					CptMinMailBean mailBean = new CptMinMailBean(cptMin.getPrenomCptMin(), cptMin.getNomCptMin(), cptMin.getNumDossierOpiCptMin(), null, null, campagneController.getLibelleCampagne(cptMin.getCampagne(), getCodLangueCptMin(cptMin)), null);
					mailController.sendMailByCod(cptMin.getMailPersoCptMin(), NomenclatureUtils.MAIL_CPT_MIN_DELETE, mailBean, null, getCodLangueCptMin(cptMin));
				}
				userController.setNoDossierNomCandidat(null);
				Notification.show(applicationContext.getMessage("candidat.delete.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
				MainUI.getCurrent().buildMenuGestCand(false);
			} catch (Exception ex) {
				logger.error(applicationContext.getMessage("candidat.delete.error", null, UI.getCurrent().getLocale()), ex);
				Notification.show(applicationContext.getMessage("candidat.delete.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}
		});
		UI.getCurrent().addWindow(win);
	}

	/** Supprime un candidat en base
	 *
	 * @param cptMin
	 */
	@Transactional
	private void deleteCandidatBase(final CompteMinima cptMin) {
		histoNumDossierRepository.delete(cptMin.getNumDossierOpiCptMin());
		compteMinimaRepository.delete(cptMin);
	}

	/** @param candidat
	 * @return true si le candidat est un candidat et qu'il a déjà au moins une candidature */
	public Boolean isCandidatAndHaveCandidature(final Candidat candidat) {
		if (candidat == null || candidat.getCandidatures() == null || candidat.getCandidatures().size() == 0) {
			return false;
		}
		if (userController.isCandidat() && candidat.getCandidatures().size() > 0) {
			return true;
		}
		return false;
	}
}
