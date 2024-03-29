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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.repositories.AlertSvaRepository;
import fr.univlorraine.ecandidat.repositories.CentreCandidatureRepository;
import fr.univlorraine.ecandidat.repositories.CommissionRepository;
import fr.univlorraine.ecandidat.repositories.FormulaireRepository;
import fr.univlorraine.ecandidat.repositories.GestionnaireRepository;
import fr.univlorraine.ecandidat.repositories.MailRepository;
import fr.univlorraine.ecandidat.repositories.MotivationAvisRepository;
import fr.univlorraine.ecandidat.repositories.PieceJustifRepository;
import fr.univlorraine.ecandidat.repositories.TagRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionRepository;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.bean.export.ExportCtrcand;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleBeanPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.DroitProfilGestionnaireWindow;
import fr.univlorraine.ecandidat.views.windows.ScolCentreCandidatureWindow;

/**
 * Gestion de l'entité centreCandidature
 * @author Kevin Hergalant
 */
@Component
public class CentreCandidatureController {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient ExportController exportController;
	@Resource
	private transient CentreCandidatureRepository centreCandidatureRepository;
	@Resource
	private transient GestionnaireRepository gestionnaireRepository;
	@Resource
	private transient CommissionRepository commissionRepository;
	@Resource
	private transient FormulaireRepository formulaireRepository;
	@Resource
	private transient PieceJustifRepository pieceJustifRepository;
	@Resource
	private transient MotivationAvisRepository motivationAvisRepository;
	@Resource
	private transient MailRepository mailRepository;
	@Resource
	private transient TypeDecisionRepository typeDecisionRepository;
	@Resource
	private transient AlertSvaRepository alertSvaRepository;
	@Resource
	private transient TagRepository tagRepository;

	@Resource
	private transient OffreFormationController offreFormationController;
	@Resource
	private transient DateTimeFormatter formatterDate;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/**
	 * @return liste des centreCandidatures
	 */
	public List<CentreCandidature> getCentreCandidatures() {
		return centreCandidatureRepository.findAll();
	}

	/**
	 * @return liste des centreCandidatures
	 */
	public List<CentreCandidature> getCentreCandidaturesEnService() {
		return centreCandidatureRepository.findByTesCtrCand(true);
	}

	/**
	 * @return liste des centreCandidatures
	 */
	public CentreCandidature getCentreCandidature(final Integer id) {
		return centreCandidatureRepository.findOne(id);
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau centreCandidature.
	 */
	public void editNewCentreCandidature() {
		final CentreCandidature centreCandidature = new CentreCandidature(userController.getCurrentUserLogin(),
			typeDecisionController.getTypeDecisionFavDefault(),
			parametreController.getNbVoeuxMax(),
			false);
		final ScolCentreCandidatureWindow window = new ScolCentreCandidatureWindow(centreCandidature, true);
		window.addRecordCtrCandWindowListener(e -> {
			if (userController.getCentreCandidature() == null) {
				userController.setCentreCandidature(e);
				MainUI.getCurrent().buildMenuCtrCand();
			}
		});

		UI.getCurrent().addWindow(window);
	}

	/**
	 * Ouvre une fenêtre d'édition de centreCandidature.
	 * @param centreCandidature
	 */
	public void editCentreCandidature(final CentreCandidature centreCandidature, final Boolean isAdmin) {
		Assert.notNull(centreCandidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(centreCandidature, null)) {
			return;
		}

		final ScolCentreCandidatureWindow window = new ScolCentreCandidatureWindow(centreCandidature, isAdmin);
		window.addCloseListener(e -> lockController.releaseLock(centreCandidature));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un centreCandidature
	 * @param  centreCandidature
	 * @return                   le centreCandidature
	 */
	public CentreCandidature saveCentreCandidature(CentreCandidature centreCandidature) {
		Assert.notNull(centreCandidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (centreCandidature.getIdCtrCand() != null && !lockController.getLockOrNotify(centreCandidature, null)) {
			return null;
		}
		/* Enregistrement du centre de candidature */
		centreCandidature.setUserModCtrCand(userController.getCurrentUserLogin());
		centreCandidature = centreCandidatureRepository.saveAndFlush(centreCandidature);
		offreFormationController.addCtrCand(centreCandidature);

		/* on controle qu'on ne desactive pas le centre de candidature en cours */
		controlDisableOrDeleteCtrCandEnCours(centreCandidature, false);

		/* Si tes à non : Suppression dans l'offre */
		if (!centreCandidature.getTesCtrCand()) {
			offreFormationController.removeCtrCand(centreCandidature);
		}

		/* On recharge le menu du centre, si celui ci est le meme que celui en session car on a pu modifier le paramCC */
		final SecurityCentreCandidature ctrSession = userController.getCentreCandidature();
		if (ctrSession != null && ctrSession.getIdCtrCand() != null && ctrSession.getIdCtrCand().equals(centreCandidature.getIdCtrCand())) {
			MainUI.getCurrent().buildMenuCtrCand();
		}

		lockController.releaseLock(centreCandidature);
		return centreCandidature;
	}

	/**
	 * on controle qu'on ne desactive pas ou qu'on ne supprime pas le centre de candidature en cours
	 * @param centreCandidature
	 */
	private void controlDisableOrDeleteCtrCandEnCours(final CentreCandidature centreCandidature, final Boolean isDelete) {
		final SecurityCentreCandidature securityCentreCandidature = userController.getCentreCandidature();
		/* Si passage du temoin en service à non et que ce centre est celui en train d'être éditée */
		if ((!centreCandidature.getTesCtrCand() || isDelete) && securityCentreCandidature != null && securityCentreCandidature.getIdCtrCand().equals(centreCandidature.getIdCtrCand())) {
			userController.setCentreCandidature(null);
			MainUI.getCurrent().buildMenuCtrCand();
			Notification.show(applicationContext.getMessage("ctrCand.delete.or.disable.active", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
		}
	}

	/**
	 * Supprime une centreCandidature
	 * @param centreCandidature
	 */
	public void deleteCentreCandidature(final CentreCandidature centreCandidature) {
		Assert.notNull(centreCandidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		if (!isAutorizedToDelete(centreCandidature)) {
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(centreCandidature, null)) {
			return;
		}

		String txtDelete = applicationContext.getMessage("ctrCand.window.confirmDelete", new Object[] { centreCandidature.getCodCtrCand() }, UI.getCurrent().getLocale());
		if (gestionnaireRepository.countByCentreCandidature(centreCandidature) > 0) {
			txtDelete = txtDelete + " " + applicationContext.getMessage("ctrCand.window.delete.warning", null, UI.getCurrent().getLocale());
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(txtDelete, applicationContext.getMessage("ctrCand.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(centreCandidature, null)) {
				centreCandidature.getGestionnaires().forEach(gest -> {
					droitProfilController.deleteDroitProfilInd(gest.getDroitProfilInd());
				});
				centreCandidature.getGestionnaires().clear();
				centreCandidatureRepository.delete(centreCandidature);
				/* Suppression dans l'offre */
				offreFormationController.removeCtrCand(centreCandidature);
				/* on controle qu'on ne desactive pas le centre de candidature en cours */
				controlDisableOrDeleteCtrCandEnCours(centreCandidature, true);
				/* Suppression du lock */
				lockController.releaseLock(centreCandidature);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(centreCandidature);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Ajoute un profil à un gestionnaire
	 * @param ctrCand
	 */
	public void addProfilToGestionnaire(final CentreCandidature ctrCand) {
		/* Verrou */
		if (!lockController.getLockOrNotify(ctrCand, null)) {
			return;
		}

		final DroitProfilGestionnaireWindow window = new DroitProfilGestionnaireWindow(ctrCand);
		window.addDroitProfilGestionnaireListener((individu, droit, loginApo, commentaire, centreGestion, isAllCommission, listeCommission) -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(ctrCand, null)) {
				if (droitProfilController.getProfilIndByCentreCandidatureAndLogin(ctrCand, individu).size() == 0) {
					final Individu ind = individuController.saveIndividu(individu);
					final DroitProfilInd dpi = droitProfilController.saveProfilInd(ind, droit);
					final Gestionnaire gest = new Gestionnaire(siScolService.getTypSiscol(), ctrCand, dpi, loginApo, commentaire, centreGestion, isAllCommission, listeCommission);

					ctrCand.getGestionnaires().add(gest);
					centreCandidatureRepository.save(ctrCand);
				} else {
					Notification.show(applicationContext.getMessage("droitprofilind.gest.allready", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
			}
		});

		window.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(ctrCand);
		});
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Modifie un profil d'un gestionnaire
	 * @param gest
	 */
	public void updateProfilToGestionnaire(final Gestionnaire gest) {
		/* Verrou */
		if (!lockController.getLockOrNotify(gest.getCentreCandidature(), null)) {
			return;
		}

		Assert.notNull(gest, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		final DroitProfilGestionnaireWindow window = new DroitProfilGestionnaireWindow(gest);
		window.addDroitProfilGestionnaireListener((individu, droit, loginApo, commentaire, centreGestion, isAllCommission, listeCommission) -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(gest.getCentreCandidature(), null)) {
				gest.getDroitProfilInd().setDroitProfil(droit);
				droitProfilController.saveProfilInd(gest.getDroitProfilInd());
				gest.setLoginApoGest(loginApo);
				gest.setCommentaire(commentaire);
				gest.setSiScolCentreGestion(centreGestion);
				gest.setTemAllCommGest(isAllCommission);
				gest.setCommissions(listeCommission);
				gest.getCentreCandidature().setDatModCtrCand(LocalDateTime.now());
				centreCandidatureRepository.save(gest.getCentreCandidature());
			}
		});

		window.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(gest.getCentreCandidature());
		});
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Ajoute un profil à un gestionnaire
	 * @param gest
	 */
	public void deleteProfilToGestionnaire(final Gestionnaire gest) {
		/* Verrou */
		if (!lockController.getLockOrNotify(gest.getCentreCandidature(), null)) {
			return;
		}

		Assert.notNull(gest, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */

		final ConfirmWindow confirmWindow = new ConfirmWindow(
			applicationContext.getMessage("droitprofilind.window.confirmDelete",
				new Object[]
				{ gest.getDroitProfilInd().getDroitProfil().getCodProfil(), gest.getDroitProfilInd().getIndividu().getLoginInd() },
				UI.getCurrent().getLocale()),
			applicationContext.getMessage("droitprofilind.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(gest.getCentreCandidature(), null)) {
				gest.getCentreCandidature().getGestionnaires().remove(gest);
				centreCandidatureRepository.save(gest.getCentreCandidature());
				droitProfilController.deleteDroitProfilInd(gest.getDroitProfilInd());
				/* Suppression du lock */
				lockController.releaseLock(gest.getCentreCandidature());
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(gest.getCentreCandidature());
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Verifie qu'on a le droit de supprimer ce centre de candidature
	 * @param  typeDecision
	 * @return
	 */
	private Boolean isAutorizedToDelete(final CentreCandidature ctrCand) {
		if (pieceJustifRepository.countByCentreCandidature(ctrCand) > 0) {
			displayMsgErrorUnautorized(PieceJustif.class.getSimpleName());
			return false;
		}
		if (formulaireRepository.countByCentreCandidature(ctrCand) > 0) {
			displayMsgErrorUnautorized(Formulaire.class.getSimpleName());
			return false;
		}
		if (commissionRepository.countByCentreCandidature(ctrCand) > 0) {
			displayMsgErrorUnautorized(Commission.class.getSimpleName());
			return false;
		}

		if (motivationAvisRepository.countByCentreCandidature(ctrCand) > 0) {
			displayMsgErrorUnautorized(MotivationAvis.class.getSimpleName());
			return false;
		}

		if (mailRepository.countByCentreCandidature(ctrCand) > 0) {
			displayMsgErrorUnautorized(Mail.class.getSimpleName());
			return false;
		}

		if (typeDecisionRepository.countByCentreCandidature(ctrCand) > 0) {
			displayMsgErrorUnautorized(TypeDecision.class.getSimpleName());
			return false;
		}

		if (tagRepository.countByCentreCandidature(ctrCand) > 0) {
			displayMsgErrorUnautorized(Tag.class.getSimpleName());
			return false;
		}

		else {
			return true;
		}
	}

	/**
	 * Affiche le message d'erreur
	 * @param className
	 */
	private void displayMsgErrorUnautorized(final String className) {
		Notification.show(applicationContext.getMessage("ctrCand.error.delete", new Object[] { className }, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
	}

	/**
	 * Renvoi le centre de canidature actif pour l'utilisateur
	 * @return le centre de canidature actif
	 */
	public CentreCandidature getCentreCandidatureActif() {
		final Integer idCtr = userController.getCentreCandidature().getIdCtrCand();
		if (idCtr != null) {
			return getCentreCandidature(idCtr);
		}
		return null;
	}

	/**
	 * Verifie l'unicité du code
	 * @param  cod
	 * @param  id
	 * @return     true si le code est unique
	 */
	public Boolean isCodCtrCandUnique(final String cod, final Integer id) {
		final CentreCandidature ctrCand = centreCandidatureRepository.findByCodCtrCand(cod);
		if (ctrCand == null) {
			return true;
		} else {
			if (ctrCand.getIdCtrCand().equals(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retourne les centre de canidatures d'un individu
	 * @return la liste des centre de canidature actifs
	 */
	public List<CentreCandidature> getListCentreCandidature() {
		if (userController.isScolCentrale()) {
			return getCentreCandidaturesEnService();
		} else {
			final List<CentreCandidature> listeToRet = new ArrayList<>();
			for (final DroitProfilInd droitProfilInd : droitProfilController.getProfilIndCtrCandCurrentUser()) {
				final Gestionnaire gestionnaire = droitProfilInd.getGestionnaire();
				if (gestionnaire != null && gestionnaire.getCentreCandidature() != null && gestionnaire.getCentreCandidature().getTesCtrCand()) {
					listeToRet.add(gestionnaire.getCentreCandidature());
				}
			}
			return listeToRet;
		}
	}

	/**
	 * @return liste des centreCandidatures
	 */
	public Boolean getIsCtrCandParamCC(final Integer id) {
		return parametreController.getIsParamCC() && getCentreCandidature(id).getTemParamCtrCand();
	}

	/**
	 * Renvoie une liste pour visualiser les parametres d'un centre cand readonly
	 * @param  ctrCand
	 * @return         la liste d'affichage des parametres
	 */
	public List<SimpleTablePresentation> getListPresentationReadOnly(final CentreCandidature ctrCand) {
		final List<SimpleTablePresentation> liste = new ArrayList<>();
		int i = 1;
		liste.add(getItemPresentation(i++, CentreCandidature_.codCtrCand.getName(), ctrCand.getCodCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.libCtrCand.getName(), ctrCand.getLibCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.tesCtrCand.getName(), ctrCand.getTesCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.temParamCtrCand.getName(), ctrCand.getTemParamCtrCand() && parametreController.getIsParamCC()));
		return liste;
	}

	/**
	 * Renvoie une liste pour visualiser les parametres d'un centre cand non readOnly
	 * @param  ctrCand
	 * @return         la liste d'affichage des parametres
	 */
	public List<SimpleTablePresentation> getListPresentationWritable(final CentreCandidature ctrCand) {
		final List<SimpleTablePresentation> liste = new ArrayList<>();
		String completmentNbVoeuxMaxEtab = "";
		if (parametreController.getNbVoeuxMaxIsEtab()) {
			completmentNbVoeuxMaxEtab = " " + applicationContext.getMessage("ctrCand.table.nbMaxVoeuxCtrCand.notused", null, UI.getCurrent().getLocale());
		}
		int i = 1;
		liste.add(getItemPresentation(i++, CentreCandidature_.typSendMailCtrCand.getName(), applicationContext.getMessage("ctrCand.typSendMailCtrCand." + ctrCand.getTypSendMailCtrCand(), null, UI.getCurrent().getLocale())));
		if (ctrCand.getMailContactCtrCand() != null) {
			liste.add(getItemPresentation(i++, CentreCandidature_.mailContactCtrCand.getName(), ctrCand.getMailContactCtrCand()));
		}
		liste.add(getItemPresentation(i++, CentreCandidature_.typeDecisionFav.getName(), ctrCand.getTypeDecisionFav() != null ? ctrCand.getTypeDecisionFav().getLibTypDec() : ""));
		liste.add(getItemPresentation(i++, CentreCandidature_.temListCompCtrCand.getName(), ctrCand.getTemListCompCtrCand()));
		liste.add(getItemPresentation(i++,
			CentreCandidature_.typeDecisionFavListComp.getName(),
			ctrCand.getTypeDecisionFavListComp() != null ? ctrCand.getTypeDecisionFavListComp().getLibTypDec() : ""));
		liste.add(getItemPresentation(i++, CentreCandidature_.nbMaxVoeuxCtrCand.getName(), ctrCand.getNbMaxVoeuxCtrCand() + completmentNbVoeuxMaxEtab));
		liste.add(getItemPresentation(i++, CentreCandidature_.temDematCtrCand.getName(), ctrCand.getTemDematCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.datDebDepotCtrCand.getName(), ctrCand.getDatDebDepotCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.datFinDepotCtrCand.getName(), ctrCand.getDatFinDepotCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.datAnalyseCtrCand.getName(), ctrCand.getDatAnalyseCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.datRetourCtrCand.getName(), ctrCand.getDatRetourCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.datJuryCtrCand.getName(), ctrCand.getDatJuryCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.datPubliCtrCand.getName(), ctrCand.getDatPubliCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.datConfirmCtrCand.getName(), ctrCand.getDatConfirmCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.delaiConfirmCtrCand.getName(), ctrCand.getDelaiConfirmCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.datConfirmListCompCtrCand.getName(), ctrCand.getDatConfirmListCompCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.delaiConfirmListCompCtrCand.getName(), ctrCand.getDelaiConfirmListCompCtrCand()));
		liste.add(getItemPresentation(i++, CentreCandidature_.infoCompCtrCand.getName(), ctrCand.getInfoCompCtrCand()));

		return liste;
	}

	/**
	 * @param  order
	 * @param  property
	 * @param  value
	 * @return          un item de présentation
	 */
	private SimpleTablePresentation getItemPresentation(final int order, final String property, Object value) {
		if (value instanceof LocalDate) {
			value = formatterDate.format((LocalDate) value);
		}
		return new SimpleTablePresentation(order, property, applicationContext.getMessage("ctrCand.table." + property, null, UI.getCurrent().getLocale()), value);
	}

	/**
	 * @param  listeCtr
	 * @return          le fichier d'export
	 */
	public OnDemandFile generateExport(final List<CentreCandidature> listeCtr) {
		final List<ExportCtrcand> liste = new ArrayList<>();
		listeCtr.forEach(e -> {
			liste.add(new ExportCtrcand(e,
				formatterDate,
				applicationContext.getMessage("droitprofilind.table.individu.isAllComm", null, UI.getCurrent().getLocale()),
				applicationContext.getMessage("ctrCand.typSendMailCtrCand." + e.getTypSendMailCtrCand(), null, UI.getCurrent().getLocale())));
		});

		/* Alimentation */
		final Map<String, Object> beans = new HashMap<>();
		beans.put("ctrCands", liste);

		/* Libellé du fichier */
		final String libFile = applicationContext.getMessage("ctrCand.export.nom.fichier",
			new Object[]
			{ DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now()) },
			UI.getCurrent().getLocale());

		return exportController.generateXlsxExport(beans, "centres_candidature_template", libFile, Arrays.asList(3));
	}

	/**
	 * @return la liste des types d'envoie de mail pour les centres de candidature
	 */
	public List<SimpleBeanPresentation> getListeTypSendMail() {
		final List<SimpleBeanPresentation> liste = new ArrayList<>();
		liste.add(new SimpleBeanPresentation(CentreCandidature.TYP_SEND_MAIL_NONE, applicationContext.getMessage("ctrCand.typSendMailCtrCand." + CentreCandidature.TYP_SEND_MAIL_NONE, null, UI.getCurrent().getLocale())));
		liste.add(new SimpleBeanPresentation(CentreCandidature.TYP_SEND_MAIL_MAIL_CONTACT, applicationContext.getMessage("ctrCand.typSendMailCtrCand." + CentreCandidature.TYP_SEND_MAIL_MAIL_CONTACT, null, UI.getCurrent().getLocale())));
		liste.add(new SimpleBeanPresentation(CentreCandidature.TYP_SEND_MAIL_LIST_GEST, applicationContext.getMessage("ctrCand.typSendMailCtrCand." + CentreCandidature.TYP_SEND_MAIL_LIST_GEST, null, UI.getCurrent().getLocale())));
		return liste;
	}
}
