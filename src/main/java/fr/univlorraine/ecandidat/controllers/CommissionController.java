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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CommissionMembre;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.FichierFiabilisation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.repositories.CommissionRepository;
import fr.univlorraine.ecandidat.repositories.FichierFiabilisationRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.services.security.SecurityCommission;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.export.ExportCommission;
import fr.univlorraine.ecandidat.utils.bean.export.ExportLettreCandidat;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandCommissionWindow;
import fr.univlorraine.ecandidat.views.windows.DroitProfilMembreCommWindow;
import fr.univlorraine.ecandidat.views.windows.UploadWindow;

/**
 * Gestion de l'entité commission
 *
 * @author Kevin Hergalant
 */
@Component
public class CommissionController {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient ExportController exportController;
	@Resource
	private transient CommissionRepository commissionRepository;
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient FichierFiabilisationRepository fichierFiabilisationRepository;
	@Resource
	private transient AdresseController adresseController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient I18nController i18nController;

	@Resource
	private transient DateTimeFormatter formatterDateTime;

	/** @return liste des commissions */
	public List<Commission> getCommissionsByCtrCand(final CentreCandidature ctrCand) {
		return commissionRepository.findByCentreCandidatureIdCtrCandAndTesCommAndCentreCandidatureTesCtrCand(ctrCand.getIdCtrCand(), true, true);
	}

	/**
	 * @param idComm
	 * @return une commission
	 */
	public Commission getCommissionById(final Integer idComm) {
		return commissionRepository.findOne(idComm);
	}

	/**
	 * @param ctrCand
	 * @param isGestAllCommission
	 * @param listeIdCommission
	 * @return les commissions d'un centre de candidature
	 */
	public List<Commission> getCommissionsByCtrCand(final CentreCandidature ctrCand, final Boolean isGestAllCommission,
			final List<Integer> listeIdCommission) {
		if (isGestAllCommission != null && isGestAllCommission == true) {
			return commissionRepository.findByCentreCandidatureIdCtrCand(ctrCand.getIdCtrCand());
		} else if (listeIdCommission != null && listeIdCommission.size() > 0) {
			return commissionRepository.findByCentreCandidatureIdCtrCandAndIdCommIn(ctrCand.getIdCtrCand(), listeIdCommission);
		} else {
			return new ArrayList<>();
		}
	}

	/**
	 * @param ctrCand
	 * @param isGestAllCommission
	 * @param listeIdCommission
	 * @param isArchivedView
	 * @return les commissions en service d'un centre de candidature
	 */
	public List<Commission> getCommissionsEnServiceByCtrCand(final CentreCandidature ctrCand,
			final Boolean isGestAllCommission, final List<Integer> listeIdCommission, final Boolean isArchivedView) {
		if (isGestAllCommission != null && isGestAllCommission == true) {
			if (isArchivedView) {
				return commissionRepository.findByCentreCandidatureIdCtrCand(ctrCand.getIdCtrCand());
			} else {
				return commissionRepository.findByCentreCandidatureIdCtrCandAndTesComm(ctrCand.getIdCtrCand(), true);
			}
		} else if (listeIdCommission != null && listeIdCommission.size() > 0) {
			if (isArchivedView) {
				return commissionRepository.findByCentreCandidatureIdCtrCandAndIdCommIn(ctrCand.getIdCtrCand(), listeIdCommission);
			} else {
				return commissionRepository.findByCentreCandidatureIdCtrCandAndTesCommAndIdCommIn(ctrCand.getIdCtrCand(), true, listeIdCommission);
			}
		} else {
			return new ArrayList<>();
		}
	}

	/** @return la liste des commission dont le user est membre */
	public List<Commission> getCommissionsGestionnaire() {
		Authentication auth = userController.getCurrentAuthentication();
		if (userController.isScolCentrale(auth)) {
			return commissionRepository.findByTesCommAndCentreCandidatureTesCtrCand(true, true);
		}

		List<DroitProfilInd> listeProfil = droitProfilController.searchDroitByLoginAndIsCommissionMember(userController.getCurrentUserLogin(auth));
		return listeProfil.stream().map(e -> e.getCommissionMembre().getCommission()).filter(c -> c != null
				&& c.getCentreCandidature() != null && c.getCentreCandidature().getTesCtrCand() && c.getTesComm()).collect(Collectors.toList());
	}

	/** Ouvre une fenêtre d'édition d'un nouveau commission. */
	public void editNewCommission(final CentreCandidature ctrCand) {
		Commission commission = new Commission(ctrCand, userController.getCurrentUserLogin());
		commission.setI18nCommentRetourComm(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_COMM_COMMENT_RETOUR)));
		UI.getCurrent().addWindow(new CtrCandCommissionWindow(commission, true));
	}

	/**
	 * Ouvre une fenêtre d'édition de commission.
	 *
	 * @param commission
	 */
	public void editCommission(final Commission commission, final Boolean isAdmin) {
		Assert.notNull(commission, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(commission, null)) {
			return;
		}
		if (commission.getI18nCommentRetourComm() == null) {
			commission.setI18nCommentRetourComm(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_COMM_COMMENT_RETOUR)));
		}
		CtrCandCommissionWindow window = new CtrCandCommissionWindow(commission, isAdmin);
		window.addCloseListener(e -> lockController.releaseLock(commission));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un commission
	 *
	 * @param commission
	 * @param adresse
	 */
	public void saveCommission(Commission commission, final Adresse adresse) {
		Assert.notNull(commission, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		Assert.notNull(adresse, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (commission.getIdComm() != null && !lockController.getLockOrNotify(commission, null)) {
			return;
		}
		commission.setDatModComm(LocalDateTime.now());
		commission.setUserModComm(userController.getCurrentUserLogin());
		commission.setAdresse(adresse);

		/* Pour les i18n nullable, attention a les supprimer si besoin */
		Integer idI18n = i18nController.getIdI18nNullable(commission.getI18nCommentRetourComm());
		commission.setI18nCommentRetourComm(i18nController.saveI18nNullable(commission.getI18nCommentRetourComm()));
		commission = commissionRepository.saveAndFlush(commission);
		if (commission.getI18nCommentRetourComm() == null && idI18n != null) {
			i18nController.deleteI18nNullable(idI18n);
		}

		/* on controle qu'on ne desactive pas la commission en cours */
		controlDisableOrDeleteCommissionEnCours(commission, false);

		lockController.releaseLock(commission);
	}

	/**
	 * on controle qu'on ne desactive pas ou qu'on ne supprime pas le centre de candidature en cours
	 *
	 * @param centreCandidature
	 */
	private void controlDisableOrDeleteCommissionEnCours(final Commission commission, final Boolean isDelete) {
		SecurityCommission securityCommission = userController.getCommission();
		/*
		 * Si passage du temoin en service à non et que ce centre est celui en train
		 * d'être éditée
		 */
		if ((!commission.getTesComm() || isDelete) && securityCommission != null
				&& securityCommission.getIdComm().equals(commission.getIdComm())) {
			userController.setCommission(null);
			MainUI.getCurrent().buildMenuCommission();
			Notification.show(applicationContext.getMessage("commission.delete.or.disable.active", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		}
	}

	/**
	 * Supprime une commission
	 *
	 * @param commission
	 */
	public void deleteCommission(final Commission commission) {
		Assert.notNull(commission, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		if (formationRepository.countByCommission(commission) > 0) {
			Notification.show(applicationContext.getMessage("commission.error.delete", new Object[] {Formation.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(commission, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("commission.window.confirmDelete", new Object[] {
				commission.getCodComm()}, UI.getCurrent().getLocale()), applicationContext.getMessage("commission.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(commission, null)) {
				commission.getCommissionMembres().forEach(gest -> {
					droitProfilController.deleteDroitProfilInd(gest.getDroitProfilInd());
				});
				commission.getCommissionMembres().clear();
				Commission commissionSave = commissionRepository.save(commission);
				try {
					deleteCommissionDbAndFile(commissionSave);

					/* on controle qu'on ne supprime pas la commission en cours */
					controlDisableOrDeleteCommissionEnCours(commission, true);

					/* Suppression du lock */
					lockController.releaseLock(commissionSave);
				} catch (Exception ex) {
					Notification.show(applicationContext.getMessage("file.error.delete", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(commission);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Supprime une PJ
	 *
	 * @param pieceJustif
	 * @throws FileException
	 */
	private void deleteCommissionDbAndFile(final Commission commission) {
		Fichier fichier = commission.getFichier();
		Integer idComm = commission.getIdComm();
		commissionRepository.delete(commission);
		if (fichier != null) {
			FichierFiabilisation fichierFiabilisation = new FichierFiabilisation(fichier);
			fichierFiabilisation.setIdComm(idComm);
			fichierFiabilisation = fichierFiabilisationRepository.save(fichierFiabilisation);
			try {
				fileController.deleteFichier(fichier);
				fichierFiabilisationRepository.delete(fichierFiabilisation);
			} catch (FileException e) {
			}
		}
	}

	/**
	 * Verifie l'unicité du code
	 *
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodCommUnique(final String cod, final Integer id) {
		Commission motiv = commissionRepository.findByCodComm(cod);
		if (motiv == null) {
			return true;
		} else {
			if (motiv.getIdComm().equals(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Ajoute un profil à un membre
	 *
	 * @param commission
	 */
	public void addProfilToMembre(final Commission commission) {
		/* Verrou */
		if (!lockController.getLockOrNotify(commission, null)) {
			return;
		}
		DroitProfilMembreCommWindow window = new DroitProfilMembreCommWindow();
		window.addDroitProfilIndCommListener((individu, droit, isPresident) -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(commission, null)) {
				if (droitProfilController.getProfilIndByCommissionAndLogin(commission, individu).size() == 0) {
					Individu ind = individuController.saveIndividu(individu);
					DroitProfilInd dpi = droitProfilController.saveProfilInd(ind, droit);
					commission.getCommissionMembres().add(new CommissionMembre(commission, dpi, isPresident));
					commissionRepository.save(commission);
				} else {
					Notification.show(applicationContext.getMessage("droitprofilind.gest.allready", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
			}
		});

		window.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(commission);
		});
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Modifie le profil d'un membre
	 *
	 * @param membre
	 */
	public void updateProfilToMembre(final CommissionMembre membre) {
		/* Verrou */
		if (!lockController.getLockOrNotify(membre.getCommission(), null)) {
			return;
		}

		Assert.notNull(membre, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		DroitProfilMembreCommWindow window = new DroitProfilMembreCommWindow(membre);
		window.addDroitProfilIndCommListener((individu, droit, isPresident) -> {
			membre.getDroitProfilInd().setDroitProfil(droit);
			droitProfilController.saveProfilInd(membre.getDroitProfilInd());
			membre.setTemIsPresident(isPresident);
			membre.getCommission().setDatModComm(LocalDateTime.now());
			commissionRepository.save(membre.getCommission());
		});

		window.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(membre.getCommission());
		});
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Ajoute un profil à un membre
	 *
	 * @param membre
	 */
	public void deleteProfilToMembre(final CommissionMembre membre) {
		/* Verrou */
		if (!lockController.getLockOrNotify(membre.getCommission(), null)) {
			return;
		}

		Assert.notNull(membre, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("droitprofilind.window.confirmDelete", new Object[] {membre.getDroitProfilInd().getDroitProfil().getCodProfil(),
				membre.getDroitProfilInd().getIndividu().getLoginInd()},
				UI.getCurrent().getLocale()), applicationContext.getMessage("droitprofilind.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(membre.getCommission(), null)) {
				membre.getCommission().getCommissionMembres().remove(membre);
				commissionRepository.save(membre.getCommission());
				droitProfilController.deleteDroitProfilInd(membre.getDroitProfilInd());
				/* Suppression du lock */
				lockController.releaseLock(membre.getCommission());
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(membre.getCommission());
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * @param listeCommission
	 * @param ctrCand
	 * @return le fichier d'export
	 */
	public OnDemandFile generateExport(final List<Commission> listeCommission, final SecurityCtrCandFonc ctrCand) {
		List<ExportCommission> liste = new ArrayList<>();
		listeCommission.forEach(e -> {
			ExportCommission exp = new ExportCommission(e, formatterDateTime);
			exp.setAdresse(adresseController.getLibelleAdresse(e.getAdresse(), " "));
			liste.add(exp);
		});

		Map<String, Object> beans = new HashMap<>();
		beans.put("commissions", liste);

		String libelle = "";
		if (ctrCand != null) {
			libelle = ctrCand.getCtrCand().getLibCtrCand() + " (" + ctrCand.getCtrCand().getCodCtrCand() + ")";
		}

		String libFile = applicationContext.getMessage("commission.export.nom.fichier", new Object[] {libelle,
				DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now())}, UI.getCurrent().getLocale());

		return exportController.generateXlsxExport(beans, "commissions_template", libFile);
	}

	/**
	 * Renvoie une liste pour visualiser les parametres d'une commission lettre
	 *
	 * @param commission
	 * @return la liste d'affichage des parametres
	 */
	public List<SimpleTablePresentation> getListPresentationLettre(final Commission commission) {
		List<SimpleTablePresentation> liste = new ArrayList<>();
		int i = 1;
		liste.add(getItemPresentation(i++, Commission_.temEditLettreComm.getName(), commission.getTemEditLettreComm()));
		liste.add(getItemPresentation(i++, Commission_.temMailLettreComm.getName(), commission.getTemMailLettreComm()));
		return liste;
	}

	/**
	 * Renvoie une liste pour visualiser les parametres d'une commission readonly
	 *
	 * @param commission
	 * @return la liste d'affichage des parametres
	 */
	public List<SimpleTablePresentation> getListPresentationReadonly(final Commission commission) {
		List<SimpleTablePresentation> liste = new ArrayList<>();
		int i = 1;
		liste.add(getItemPresentation(i++, Commission_.codComm.getName(), commission.getCodComm()));
		liste.add(getItemPresentation(i++, Commission_.libComm.getName(), commission.getLibComm()));
		liste.add(getItemPresentation(i++, Commission_.tesComm.getName(), commission.getTesComm()));
		return liste;
	}

	/**
	 * Renvoie une liste pour visualiser les parametres d'une commission writable
	 *
	 * @param commission
	 * @return la liste d'affichage des parametres
	 */
	public List<SimpleTablePresentation> getListPresentationWritable(final Commission commission) {
		List<SimpleTablePresentation> liste = new ArrayList<>();
		int i = 1;
		liste.add(getItemPresentation(i++, Commission_.mailComm.getName(), commission.getMailComm()));
		liste.add(getItemPresentation(i++, Commission_.telComm.getName(), commission.getTelComm()));
		liste.add(getItemPresentation(i++, Commission_.faxComm.getName(), commission.getFaxComm()));
		liste.add(getItemPresentation(i++, Commission_.i18nCommentRetourComm.getName(), i18nController.getI18nTraduction(commission.getI18nCommentRetourComm())));
		liste.add(getItemPresentation(i++, Commission_.adresse.getName(), (commission.getAdresse() != null) ? adresseController.getLibelleAdresse(commission.getAdresse(), " ") : ""));
		liste.add(getItemPresentation(i++, Commission_.temAlertPropComm.getName(), commission.getTemAlertPropComm()));
		liste.add(getItemPresentation(i++, Commission_.temAlertAnnulComm.getName(), commission.getTemAlertAnnulComm()));
		liste.add(getItemPresentation(i++, Commission_.temAlertTransComm.getName(), commission.getTemAlertTransComm()));
		liste.add(getItemPresentation(i++, Commission_.temAlertDesistComm.getName(), commission.getTemAlertDesistComm()));
		liste.add(getItemPresentation(i++, Commission_.temAlertListePrincComm.getName(), commission.getTemAlertListePrincComm()));
		return liste;
	}

	/**
	 * @param order
	 * @param property
	 * @param value
	 * @return un item de présentation
	 */
	private SimpleTablePresentation getItemPresentation(final int order, final String property, final Object value) {
		return new SimpleTablePresentation(order, property, applicationContext.getMessage("commission.table." + property, null, UI.getCurrent().getLocale()), value);
	}

	/**
	 * AJoute un fichier à la commission
	 *
	 * @param commission
	 */
	public void addFileToSignataire(final Commission commission) {
		/* Verification que le service n'est pas en maintenance */
		if (fileController.isFileServiceMaintenance(true)) {
			return;
		}
		/* Verrou */
		if (!lockController.getLockOrNotify(commission, null)) {
			return;
		}
		String user = userController.getCurrentUserLogin();
		String cod = ConstanteUtils.TYPE_FICHIER_SIGN_COMM + "_" + commission.getIdComm();
		UploadWindow uw = new UploadWindow(cod, ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE, null, false, true);
		uw.addUploadWindowListener(file -> {
			if (file == null) {
				return;
			}
			Fichier fichier = fileController.createFile(file, user, ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE);
			commission.setFichier(fichier);
			commissionRepository.save(commission);
			Notification.show(applicationContext.getMessage("window.upload.success", new Object[] {file.getFileName()}, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
			uw.close();
		});
		uw.addCloseListener(e -> lockController.releaseLock(commission));
		UI.getCurrent().addWindow(uw);
	}

	/**
	 * Supprime un fichier d'une commission
	 *
	 * @param commission
	 */
	public void deleteFileToSignataire(final Commission commission) {
		/* Vérifie si le service de fichier est en maintenance */
		if (fileController.isFileServiceMaintenance(true)) {
			return;
		}
		/* Verrou */
		if (!lockController.getLockOrNotify(commission, null)) {
			return;
		}
		if (!fileController.isModeFileStockageOk(commission.getFichier(), true)) {
			return;
		}
		Fichier fichier = commission.getFichier();
		if (fichier == null) {
			Notification.show(applicationContext.getMessage("file.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			lockController.releaseLock(commission);
			return;
		}
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("file.window.confirmDelete", new Object[] {
				fichier.getNomFichier()}, UI.getCurrent().getLocale()), applicationContext.getMessage("file.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(file -> {
			removeFileToCommission(commission, fichier);
		});
		confirmWindow.addCloseListener(e -> lockController.releaseLock(commission));
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Supprime un fichier pour la commission
	 *
	 * @param commission
	 * @param fichier
	 * @throws FileException
	 */
	private void removeFileToCommission(final Commission commission, final Fichier fichier) {
		commission.setFichier(null);
		commissionRepository.save(commission);
		Integer idComm = commission.getIdComm();
		if (fichier != null) {
			FichierFiabilisation fichierFiabilisation = new FichierFiabilisation(fichier);
			fichierFiabilisation.setIdComm(idComm);
			fichierFiabilisation = fichierFiabilisationRepository.save(fichierFiabilisation);
			try {
				fileController.deleteFichier(fichier);
				fichierFiabilisationRepository.delete(fichierFiabilisation);
			} catch (FileException e) {
			}
		}
	}

	/**
	 * @param commission
	 * @param templateLettreAdm
	 * @return l'inputStream de la lettre
	 */
	public OnDemandFile testLettreAdm(final Commission commission, final String templateLettreAdm,
			final String fileName) {
		Adresse adrComm = commission.getAdresse();
		Adresse adrTest = new Adresse("15 rue des plantes", null, null, adrComm.getCodBdiAdr(), null, adrComm.getSiScolCommune(), adrComm.getSiScolPays());

		String adresseCandidat = adresseController.getLibelleAdresse(adrTest, "\n");
		String adresseCommission = adresseController.getLibelleAdresse(commission.getAdresse(), "\n");

		ExportLettreCandidat data = new ExportLettreCandidat("AXQDF1P8", "Monsieur", "Martin", "Martinpat", "Jean", "10/10/1985", adresseCandidat, "Campagne 2015", commission
				.getLibComm(), adresseCommission, "AX-BJ156", "L1 informatique",
				commission
						.getSignataireComm(),
				"Libellé de la décision", "Commentaire de la décision", "Diplome requis manquant", "16/08/2016", "10/06/2016", "17/08/2016");

		InputStream fichierSignature = null;
		if (commission.getFichier() != null) {
			fichierSignature = fileController.getInputStreamFromFichier(commission.getFichier());
		}

		/* Template */
		InputStream template = MethodUtils.getXDocReportTemplate(templateLettreAdm, i18nController.getLangueUI(), cacheController.getLangueDefault().getCodLangue());

		/* Generation du fichier */
		return new OnDemandFile(fileName, candidatureController.generateLettre(template, data, fichierSignature, i18nController.getLangueUI(), true));
	}
}
