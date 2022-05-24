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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.entities.ecandidat.PreferenceInd;
import fr.univlorraine.ecandidat.repositories.CentreCandidatureRepository;
import fr.univlorraine.ecandidat.repositories.CommissionRepository;
import fr.univlorraine.ecandidat.repositories.DroitFonctionnaliteRepository;
import fr.univlorraine.ecandidat.repositories.DroitProfilIndRepository;
import fr.univlorraine.ecandidat.repositories.DroitProfilRepository;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCommission;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleBeanPresentation;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.DroitProfilIndividuWindow;
import fr.univlorraine.ecandidat.views.windows.ScolDroitProfilWindow;
import fr.univlorraine.ecandidat.views.windows.ScolFindProfilWindow;
import fr.univlorraine.ecandidat.views.windows.ScolGestCandidatMasseWindow;

/**
 * Gestion des profils et droits
 * @author Kevin Hergalant
 */
@Component
public class DroitProfilController {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient DroitProfilRepository droitProfilRepository;
	@Resource
	private transient DroitFonctionnaliteRepository droitFonctionnaliteRepository;
	@Resource
	private transient DroitProfilIndRepository droitProfilIndRepository;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient CentreCandidatureRepository centreCandidatureRepository;
	@Resource
	private transient CommissionRepository commissionRepository;
	@Resource
	private transient CacheController cacheController;

	/* Variable d'envirronement */
	@Value("${admin.technique:}")
	private String adminTechnique;

	/** @return la lise des droit pas admin */
	public List<DroitProfil> getDroitProfilNotAdmin() {
		return droitProfilRepository.findByTypProfilNotIn(NomenclatureUtils.TYP_DROIT_PROFIL_ADM);
	}

	/** @return la liste de DroitFonctionnalite d'action de candidature */
	public List<DroitFonctionnalite> getListeDroitFonctionnaliteCandidatureToCache() {
		return droitFonctionnaliteRepository.findByTemActionCandFonc(true);
	}

	/** @return liste des getDroitUsersProfils */
	public List<DroitProfilInd> getDroitProfilInds(final Boolean adminMode) {
		final List<DroitProfilInd> liste = new ArrayList<>();
		if (adminMode) {
			final DroitProfil droitProfil =
				new DroitProfil(NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH, NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH, NomenclatureUtils.USER_NOMENCLATURE, NomenclatureUtils.USER_NOMENCLATURE,
					NomenclatureUtils.TYP_DROIT_PROFIL_ADM, false, true);
			final Individu ind = new Individu(adminTechnique, adminTechnique, null);
			liste.add(new DroitProfilInd(ind, droitProfil));
			liste.addAll(droitProfilIndRepository.findByDroitProfilTypProfil(NomenclatureUtils.TYP_DROIT_PROFIL_ADM));
		} else {
			liste.addAll(droitProfilIndRepository.findByDroitProfilTypProfil(NomenclatureUtils.TYP_DROIT_PROFIL_GEST_CANDIDAT));
			liste.addAll(droitProfilIndRepository.findByDroitProfilTypProfil(NomenclatureUtils.TYP_DROIT_PROFIL_GEST_CANDIDAT_LS));
		}
		return liste;
	}

	/** @return liste des droitFonctionnalite */
	public List<DroitFonctionnalite> getDroitFonctionnalitesByTypProfil(final String typProfil) {
		List<DroitFonctionnalite> listeFoncToRet;
		if (typProfil.equals(NomenclatureUtils.TYP_DROIT_PROFIL_GESTIONNAIRE)) {
			listeFoncToRet = droitFonctionnaliteRepository.findAll();
		} else {
			listeFoncToRet = droitFonctionnaliteRepository.findByTemOpenComFonc(true);
		}
		listeFoncToRet.sort((d1, d2) -> d1.getOrderFonc().compareTo(d2.getOrderFonc()));
		return listeFoncToRet;
	}

	/**
	 * Verifie que le code du profil n'existe pas
	 * @param  codProfil
	 * @return           true si le code existe
	 */
	public Boolean existCodeProfil(final String codProfil) {
		return droitProfilRepository.findByCodProfil(codProfil) != null;
	}

	/** Ouvre une fenêtre d'édition d'un droitProfil. */
	public void editNewDroitProfil() {
		UI.getCurrent().addWindow(new ScolDroitProfilWindow(new DroitProfil(userController.getCurrentUserLogin())));
	}

	/**
	 * Ouvre la recherche de recherche de profil
	 */
	public void findProfil() {
		UI.getCurrent().addWindow(new ScolFindProfilWindow());
	}

	/**
	 * Ouvre une fenêtre d'édition de droitProfil.
	 * @param droitProfil
	 */
	public void editDroitProfil(final DroitProfil droitProfil) {
		Assert.notNull(droitProfil, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(droitProfil, null)) {
			return;
		}

		final ScolDroitProfilWindow window = new ScolDroitProfilWindow(droitProfil);
		window.addCloseListener(e -> lockController.releaseLock(droitProfil));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Renvoie la liste proposee lors de l'ajout de profil
	 * @param  type
	 * @return      la liste des droits
	 */
	public List<DroitProfil> getListDroitProfilByType(final String type) {
		if (type.equals(NomenclatureUtils.DROIT_PROFIL_ADMIN)) {
			return droitProfilRepository.findByTypProfil(NomenclatureUtils.TYP_DROIT_PROFIL_ADM);
		} else if (type.equals(NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE)) {
			return droitProfilRepository.findByTypProfilAndTesProfil(NomenclatureUtils.TYP_DROIT_PROFIL_GESTIONNAIRE, true);
		} else if (type.equals(NomenclatureUtils.DROIT_PROFIL_COMMISSION)) {
			return droitProfilRepository.findByTypProfilAndTesProfil(NomenclatureUtils.TYP_DROIT_PROFIL_COMMISSION, true);
		} else if (type.equals(NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT)) {
			final List<DroitProfil> liste = new ArrayList<>();
			liste.addAll(droitProfilRepository.findByTypProfil(NomenclatureUtils.TYP_DROIT_PROFIL_GEST_CANDIDAT));
			liste.addAll(droitProfilRepository.findByTypProfil(NomenclatureUtils.TYP_DROIT_PROFIL_GEST_CANDIDAT_LS));
			return liste;
		}
		return null;
	}

	/** @return lq liste des profils possibles */
	public List<SimpleBeanPresentation> getListTypDroitProfil() {
		final List<SimpleBeanPresentation> liste = new ArrayList<>();
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.TYP_DROIT_PROFIL_GESTIONNAIRE, applicationContext.getMessage("nomenclature.droitProfil.centrecand", null, UI.getCurrent().getLocale())));
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.TYP_DROIT_PROFIL_COMMISSION, applicationContext.getMessage("nomenclature.droitProfil.commission", null, UI.getCurrent().getLocale())));
		return liste;
	}

	/**
	 * Enregistre profil d'individu
	 * @param  droit
	 * @param  ind
	 * @return       le profil rattaché a un individu
	 */
	public DroitProfilInd saveProfilInd(final Individu ind, final DroitProfil droit) {
		return saveProfilInd(new DroitProfilInd(ind, droit));
	}

	/**
	 * @param  dpi
	 * @return     le profil rattaché a un individu
	 */
	public DroitProfilInd saveProfilInd(final DroitProfilInd dpi) {
		return droitProfilIndRepository.saveAndFlush(dpi);
	}

	/**
	 * Renvoi les profil d'individu par commission et login
	 * @param  commission
	 * @param  individu
	 * @return            la liste des profil ind rattaché a une commission
	 */
	public List<DroitProfilInd> getProfilIndByCommissionAndLogin(final Commission commission, final Individu individu) {
		return droitProfilIndRepository.findByCommissionMembreCommissionIdCommAndIndividuLoginInd(commission.getIdComm(), individu.getLoginInd());
	}

	/**
	 * Renvoi les profil d'individu par ctrCand et login
	 * @param  ctrCand
	 * @param  individu
	 * @return          la liste des profil ind rattaché a un ctr
	 */
	public List<DroitProfilInd> getProfilIndByCentreCandidatureAndLogin(final CentreCandidature ctrCand, final Individu individu) {
		return droitProfilIndRepository.findByGestionnaireCentreCandidatureIdCtrCandAndIndividuLoginInd(ctrCand.getIdCtrCand(), individu.getLoginInd());
	}

	/** @return les profil d'individu ctrCand de l'individu courant */
	public List<DroitProfilInd> getProfilIndCtrCandCurrentUser() {
		return droitProfilIndRepository.findByIndividuLoginIndAndDroitProfilTypProfil(userController.getCurrentUserLogin(), NomenclatureUtils.TYP_DROIT_PROFIL_GESTIONNAIRE);
	}

	/**
	 * Enregistre un droit profil
	 * @param droitProfil
	 * @param fonctionnaliteMap
	 */
	public void saveDroitProfil(final DroitProfil droitProfil, final HashMap<DroitFonctionnalite, Boolean> fonctionnaliteMap) {
		Assert.notNull(droitProfil, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (droitProfil.getIdProfil() != null && !lockController.getLockOrNotify(droitProfil, null)) {
			return;
		}

		final DroitProfil droitProfilSaved = droitProfilRepository.saveAndFlush(droitProfil);
		droitProfilSaved.getDroitProfilFoncs().clear();
		fonctionnaliteMap.forEach((k, v) -> droitProfilSaved.addFonctionnalite(new DroitProfilFonc(k, droitProfilSaved, v)));
		/* on met a jour la date pour que l'entity soit push */
		droitProfilSaved.setDatModProfil(LocalDateTime.now());

		droitProfilRepository.saveAndFlush(droitProfilSaved);
		lockController.releaseLock(droitProfilSaved);
	}

	/**
	 * @param  droitProfil
	 * @return             true si le profil a des user
	 */
	private Boolean checkHasProfilInd(final DroitProfil droitProfil) {
		if (droitProfilIndRepository.countByDroitProfil(droitProfil) > 0) {
			Notification.show(applicationContext.getMessage("droitprofil.error.delete", new Object[] { DroitProfilInd.class.getSimpleName() }, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return true;
		}
		return false;
	}

	/**
	 * @param  droitProfil
	 * @return             true si on a d'autres profils du même type en service (on ne doit pas pouvoir désactiver ou supprimer tous les profils ctrcand et comm)
	 */
	public Boolean checkHasNoOtherTesDroitProfil(final DroitProfil droitProfil, final String codAction) {
		if (droitProfil.isDroitProfilCommission()) {
			if (droitProfilRepository.countByTypProfilAndIdProfilNotAndTesProfil(NomenclatureUtils.TYP_DROIT_PROFIL_COMMISSION, droitProfil.getIdProfil(), true).equals(0L)) {
				Notification
					.show(applicationContext.getMessage("droitprofil.error." + codAction + ".tes", new Object[]
					{ applicationContext.getMessage("nomenclature.droitProfil.commission", null, UI.getCurrent().getLocale()) }, UI.getCurrent().getLocale()),
						Type.WARNING_MESSAGE);
				return true;
			}
		} else if (droitProfil.isDroitProfilGestionnaireCtrCand()) {
			if (droitProfilRepository.countByTypProfilAndIdProfilNotAndTesProfil(NomenclatureUtils.TYP_DROIT_PROFIL_GESTIONNAIRE, droitProfil.getIdProfil(), true).equals(0L)) {
				Notification
					.show(applicationContext.getMessage("droitprofil.error." + codAction + ".tes", new Object[]
					{ applicationContext.getMessage("nomenclature.droitProfil.centrecand", null, UI.getCurrent().getLocale()) }, UI.getCurrent().getLocale()),
						Type.WARNING_MESSAGE);
				return true;
			}
		}
		return false;
	}

	/**
	 * Supprime un droitProfil
	 * @param droitProfil
	 */
	public void deleteDroitProfil(final DroitProfil droitProfil) {
		Assert.notNull(droitProfil, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Vérifie que le profil a des user */
		if (checkHasProfilInd(droitProfil)) {
			return;
		}

		/* Vérifie que le type de profil a d'autre profil de ce même type en service */
		if (checkHasProfilInd(droitProfil) || checkHasNoOtherTesDroitProfil(droitProfil, "delete")) {
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(droitProfil, null)) {
			return;
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("droitprofil.window.confirmDelete", new Object[] { droitProfil.getCodProfil() },
			UI.getCurrent().getLocale()), applicationContext.getMessage("droitprofil.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Vérifie que le profil a des user */
			if (checkHasProfilInd(droitProfil)) {
				/* Suppression du lock */
				lockController.releaseLock(droitProfil);
				return;
			}
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(droitProfil, null)) {
				droitProfilRepository.delete(droitProfil);
				/* Suppression du lock */
				lockController.releaseLock(droitProfil);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(droitProfil);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Ajoute un profil à un admin */
	public void addProfilToUser(final Boolean modeAdmin) {
		String typRole = NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT;
		if (modeAdmin) {
			typRole = NomenclatureUtils.DROIT_PROFIL_ADMIN;
		}
		final DroitProfilIndividuWindow window = new DroitProfilIndividuWindow(typRole);
		window.addDroitProfilIndividuListener((individu, droit) -> {
			final Individu ind = individuController.saveIndividu(individu);
			if (droitProfilIndRepository.findByDroitProfilCodProfilAndIndividuLoginInd(droit.getCodProfil(), individu.getLoginInd()).size() == 0) {
				droitProfilIndRepository.saveAndFlush(new DroitProfilInd(ind, droit));
			} else {
				Notification.show(applicationContext.getMessage("droitprofilind.allready", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
		});
		UI.getCurrent().addWindow(window);
	}

	/** Ajoute un profil à un admin */
	public void addProfilToUserEnMasse() {
		final ScolGestCandidatMasseWindow window = new ScolGestCandidatMasseWindow();
		window.addDroitProfilMasseListener((listGestionnaire, droitProfil) -> {
			final ConfirmWindow confirmWindow =
				new ConfirmWindow(applicationContext.getMessage("droitprofilind.gestcand.window.confirm", new Object[]
				{ droitProfil.getLibProfil(), listGestionnaire.size() },
					UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(e -> {
				listGestionnaire.forEach(gest -> {
					final Individu ind = gest.getDroitProfilInd().getIndividu();
					/* Recherche des droits
					 * Il faut supprimer un éventuel droit de gestionnaire de candidat */
					final List<DroitProfilInd> list = droitProfilIndRepository.findByIndividuLoginInd(ind.getLoginInd());
					final Optional<DroitProfilInd> droitGestCandLS =
						list.stream().filter(a -> a.getDroitProfil().getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT_LS)).findFirst();
					final Optional<DroitProfilInd> droitGestCand = list.stream().filter(b -> b.getDroitProfil().getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT)).findFirst();
					if (droitGestCandLS.isPresent() && !droitGestCandLS.get().getDroitProfil().getCodProfil().equals(droitProfil.getCodProfil())) {
						droitProfilIndRepository.delete(droitGestCandLS.get());
					}
					if (droitGestCand.isPresent() && !droitGestCand.get().getDroitProfil().getCodProfil().equals(droitProfil.getCodProfil())) {
						droitProfilIndRepository.delete(droitGestCand.get());
					}
					if ((!droitGestCandLS.isPresent() && droitProfil.getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT_LS))
						||
						(!droitGestCand.isPresent() && droitProfil.getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_GESTION_CANDIDAT))) {
						saveProfilInd(ind, droitProfil);
					}

				});
				window.close();
			});
			UI.getCurrent().addWindow(confirmWindow);
		});
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Ajoute le droit de profil a un utilisateur via son login, nom et adrMail-->Util pour la demo
	 * @param login
	 * @param displayName
	 * @param adrMail
	 */
	public void addDroitProfilIndForAdmin(final String login, final String displayName, final String adrMail) {
		final DroitProfil droit = droitProfilRepository.findByCodProfil(NomenclatureUtils.DROIT_PROFIL_ADMIN);
		final Individu individu = individuController.saveIndividu(new Individu(login, displayName, adrMail));
		if (droit == null || individu == null) {
			return;
		}
		if (droitProfilIndRepository.findByDroitProfilCodProfilAndIndividuLoginInd(droit.getCodProfil(), individu.getLoginInd()).size() == 0) {
			droitProfilIndRepository.saveAndFlush(new DroitProfilInd(individu, droit));
		}
	}

	/**
	 * SUpprime un droitProfilInd et supprime l'individu si il n'a pas d'autre role
	 * @param droitProfilInd
	 */
	public void deleteDroitProfilInd(final DroitProfilInd droitProfilInd) {
		droitProfilIndRepository.delete(droitProfilInd);
		final Individu individu = individuController.getIndividu(droitProfilInd.getIndividu().getLoginInd());
		if (individu != null) {
			final List<DroitProfilInd> liste = droitProfilIndRepository.findByIndividuLoginInd(individu.getLoginInd());
			if (liste != null && liste.size() == 0) {
				individuController.deleteIndividu(individu);
			}
		}
	}

	/**
	 * Supprime un droitProfil
	 * @param droitProfilInd
	 */
	public void deleteProfilToUser(final DroitProfilInd droitProfilInd) {
		Assert.notNull(droitProfilInd, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(droitProfilInd, null)) {
			return;
		}
		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("droitprofilind.window.confirmDelete",
			new Object[]
			{ droitProfilInd.getDroitProfil().getCodProfil(), droitProfilInd.getIndividu().getLoginInd() },
			UI.getCurrent().getLocale()), applicationContext.getMessage("droitprofilind.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(droitProfilInd, null) && droitProfilInd != null) {
				deleteDroitProfilInd(droitProfilInd);
				/* Suppression du lock */
				lockController.releaseLock(droitProfilInd);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(droitProfilInd);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Recupere le premier centre de candidature
	 * @param  pref
	 * @return      le premier centre de candidature pour un admin
	 */
	public CentreCandidature getCtrCandForAdmin(final PreferenceInd pref) {
		// on cherche le centre préféré
		if (pref != null && pref.getIdCtrCandPref() != null) {
			final CentreCandidature ctrCandPref = centreCandidatureRepository.findOne(pref.getIdCtrCandPref());
			if (ctrCandPref != null && ctrCandPref.getTesCtrCand()) {
				return ctrCandPref;
			}
		}
		// sinon on renvoit le premier
		return centreCandidatureRepository.findFirst1ByTesCtrCand(true);
	}

	/**
	 * Recupere la premiere commission
	 * @param  pref
	 * @return      la premiere commission pour un admin
	 */
	public Commission getCommissionForAdmin(final PreferenceInd pref) {
		// on cherche la commission préférée
		if (pref != null && pref.getIdCommPref() != null) {
			final Commission commPref = commissionRepository.findOne(pref.getIdCommPref());
			if (commPref != null && commPref.getTesComm() && commPref.getCentreCandidature().getTesCtrCand()) {
				return commPref;
			}
		}
		return commissionRepository.findFirst1ByTesCommAndCentreCandidatureTesCtrCand(true, true);
	}

	/**
	 * Renvoie les roles d'admin d'un individu
	 * @param  username
	 * @return          les roles d'admin d'un individu
	 */
	public List<DroitProfilInd> searchDroitAdminByLogin(final String username) {
		return droitProfilIndRepository.findByIndividuLoginIndAndDroitProfilCodProfil(username, NomenclatureUtils.DROIT_PROFIL_ADMIN);
	}

	/**
	 * Renvoie les roles de scol central d'un individu
	 * @param  username
	 * @return          les roles de scol central d'un individu
	 */
	public List<DroitProfilInd> searchDroitScolCentralByLogin(final String username) {
		return droitProfilIndRepository.findByIndividuLoginIndAndDroitProfilCodProfil(username, NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE);
	}

	/**
	 * Renvoie les roles d'un individu
	 * @param  username
	 * @return          les roles d'un individu
	 */
	public List<DroitProfilInd> searchDroitByLogin(final String username) {
		return droitProfilIndRepository.findByIndividuLoginInd(username);
	}

	/**
	 * @param  like
	 * @return      la liste des droits profil ind par like
	 */
	public List<DroitProfilInd> searchDroitByFilter(final String filter) {
		return droitProfilIndRepository.findByFilter("%" + filter + "%", new PageRequest(0, ConstanteUtils.NB_MAX_RECH_CPT_MIN));
	}

	/**
	 * Renvoie les role d'un gestionnaire par rapport à son login et son centre de candidature
	 * @param  idCtr
	 * @param  username
	 * @return          les role d'un gestionnaire par rapport à son login et son centre de candidature
	 */
	public List<DroitProfilInd> searchDroitByLoginAndIdCtrCand(final Integer idCtr, final String username) {
		return droitProfilIndRepository.findByGestionnaireCentreCandidatureIdCtrCandAndIndividuLoginInd(idCtr, username);
	}

	/**
	 * Renvoie les role d'un membre de commission par rapport à son login et sa commission
	 * @param  idComm
	 * @param  username
	 * @return          les role d'un membre de commission par rapport à son login et sa commission
	 */
	public List<DroitProfilInd> searchDroitByLoginAndIdComm(final Integer idComm, final String username) {
		return droitProfilIndRepository.findByCommissionMembreCommissionIdCommAndIndividuLoginInd(idComm, username);
	}

	/**
	 * Renvoie les role d'un gestionnaire par rapport à son login et qu'il a une commission
	 * @param  username
	 * @return          les role d'un gestionnaire par rapport à son login et qu'il ai une commissio
	 */
	public List<DroitProfilInd> searchDroitByLoginAndIsCommissionMember(final String username) {
		return droitProfilIndRepository.findByIndividuLoginIndAndCommissionMembreIsNotNull(username);
	}

	/**
	 * @param  fonctionnalite
	 * @param  listeDroit
	 * @param  readOnly
	 * @return                true si l'utilisateur a accès à la fonctionnalité
	 */
	public Boolean hasAccessToFonctionnalite(final String fonctionnalite, final List<DroitFonctionnalite> listeDroit, final Boolean readOnly) {
		if (listeDroit == null || listeDroit.size() == 0) {
			return false;
		}

		final Optional<DroitFonctionnalite> fonc = listeDroit.stream().filter(e -> e.getCodFonc().equals(fonctionnalite) && (readOnly || !e.getReadOnly())).findFirst();
		if (fonc.isPresent()) {
			return true;
		}
		return false;
	}

	/**
	 * @param  typGestionCandidature
	 * @param  candidature
	 * @return                       les fonctionnalites dont l'utilisateur a le droit pour une candidature
	 */
	public List<DroitFonctionnalite> getCandidatureFonctionnalite(final String typGestionCandidature, final Candidature candidature) {
		final Authentication auth = userController.getCurrentAuthentication();
		/* Ce sera la liste d'action a renvoyer */
		final List<DroitFonctionnalite> listeFoncToRet = new ArrayList<>();
		if ((userController.isGestionnaireCandidat(auth) || userController.isGestionnaireCandidatLS(auth))) {
			listeFoncToRet
				.add(new DroitFonctionnalite(NomenclatureUtils.FONCTIONNALITE_OPEN_CANDIDAT, applicationContext.getMessage("candidature.action.open", null, UI.getCurrent().getLocale()), 0, true));
		}

		/* Utilisateur ScolCentrale, on ajoute toutes les fonctionnalités */
		if (userController.isScolCentrale(auth)) {
			final List<DroitFonctionnalite> listeFonc = cacheController.getListeDroitFonctionnaliteCandidature();
			/* On place tout les éléments en ecriture */
			listeFonc.forEach(e -> listeFoncToRet.add(new DroitFonctionnalite(e, false)));
		} else {
			List<DroitProfilFonc> listFonctionnalite = new ArrayList<>();
			/* On provient du menu centre de candidature-->on a accès au droits du profil du centre de candidature */
			if (MethodUtils.isGestionCandidatureCtrCand(typGestionCandidature)) {
				final SecurityCentreCandidature scc = userController.getCentreCandidature(auth);
				if (scc != null) {
					listFonctionnalite = scc.getListFonctionnalite();
				}
			}
			/* On provient du menu commission-->on a accès au droits du profil de la commission */
			else if (MethodUtils.isGestionCandidatureCommission(typGestionCandidature)) {
				final SecurityCommission sc = userController.getCommission(auth);
				if (sc != null) {
					listFonctionnalite = sc.getListFonctionnalite();
				}
			}
			/* On provient du menu candidat-->on a accès au droits du profil de la commission et du centre de candidature */
			else if (MethodUtils.isGestionCandidatureCandidat(typGestionCandidature)) {
				final SecurityCentreCandidature scc = userController.getCentreCandidature(auth);
				if (scc != null && candidature != null
					&& (scc.getIsGestAllCommission() || MethodUtils.isIdInListId(candidature.getFormation().getCommission().getIdComm(), scc.getListeIdCommission()))) {
					listFonctionnalite.addAll(scc.getListFonctionnalite());
				}
				final SecurityCommission sc = userController.getCommission(auth);
				if (sc != null && candidature != null && sc.getIdComm().equals(candidature.getFormation().getCommission().getIdComm())) {

					listFonctionnalite.addAll(sc.getListFonctionnalite());
				}
				/* Il faut eviter les doublons et prendre les fonctionnalités en ecriture prioritairement-->on trie sur le temoin pour passer les readonly en dernier */
				listFonctionnalite.sort((d1, d2) -> d1.getTemReadOnly().compareTo(d2.getTemReadOnly()));
			}

			/* On traite la liste-->dédoublon + gestion du read only */
			listFonctionnalite.stream().filter(e -> e.getDroitFonctionnalite().getTemActionCandFonc()
				&& (!e.getTemReadOnly()
					|| (e.getTemReadOnly() && e.getDroitFonctionnalite().getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_POST_IT))
					|| (e.getTemReadOnly() && e.getDroitFonctionnalite().getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_VISU_HISTO_AVIS))))
				.forEach(f -> {
					listeFoncToRet.add(new DroitFonctionnalite(f.getDroitFonctionnalite(), f.getTemReadOnly()));
				});
		}
		/* On trie et on envoie */
		listeFoncToRet.sort((d1, d2) -> d1.getOrderFonc().compareTo(d2.getOrderFonc()));
		return listeFoncToRet.stream().distinct().collect(Collectors.toList());
	}
}
