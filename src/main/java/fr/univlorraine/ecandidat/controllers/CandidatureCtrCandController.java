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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusInterne;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCand;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.PostIt;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.OpiRepository;
import fr.univlorraine.ecandidat.repositories.PostItRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionCandidatureRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureListener;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureMasseListener;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.PdfAttachement;
import fr.univlorraine.ecandidat.utils.bean.export.ExportListCandidatureAdresse;
import fr.univlorraine.ecandidat.utils.bean.export.ExportListCandidatureOption;
import fr.univlorraine.ecandidat.utils.bean.mail.AvisMailBean;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.views.windows.CtrCandActionCandidatureWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandActionCandidatureWindow.ChangeCandidatureWindowListener;
import fr.univlorraine.ecandidat.views.windows.CtrCandPostItReadWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandShowHistoWindow;
import fr.univlorraine.ecandidat.views.windows.CtrCandShowHistoWindow.DeleteAvisWindowListener;

/**
 * Gestion des candidatures pour un gestionnaire
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Component
public class CandidatureCtrCandController {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatureRepository candidatureRepository;
	@Resource
	private transient TypeDecisionCandidatureRepository typeDecisionCandidatureRepository;
	@Resource
	private transient PostItRepository postItRepository;
	@Resource
	private transient OpiRepository opiRepository;
	@Resource
	private transient LockCandidatController lockCandidatController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient CandidatureGestionController candidatureGestionController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient OpiController opiController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient ExportController exportController;

	@Resource
	private transient DateTimeFormatter formatterDate;
	@Resource
	private transient DateTimeFormatter formatterDateTime;
	@Resource
	private transient DateTimeFormatter formatterTime;

	/**
	 * @param  commission
	 * @return            les candidatures par commission
	 */
	public List<Candidature> getCandidatureByCommission(final Commission commission, final List<TypeStatut> listeTypeStatut) {
		Campagne campagneEnCours = campagneController.getCampagneActive();
		List<Candidature> liste = new ArrayList<>();
		if (campagneEnCours == null) {
			return liste;
		}
		// si on vient de la commission, il faut restreindre aux type de statuts
		// visibles par les membres de commission
		if (listeTypeStatut != null) {
			liste = candidatureRepository.findByCommissionAndTypeStatut(commission.getIdComm(), campagneEnCours.getCodCamp(), listeTypeStatut);
		} else {
			liste = candidatureRepository.findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNull(commission.getIdComm(),
				campagneEnCours.getCodCamp());
		}
		traiteListe(liste);
		return liste;
	}

	/**
	 * @param  commission
	 * @return            les candidatures par commission
	 */
	/**
	 * @param  ctrCand
	 * @return         les candidatures par centre de candidature
	 */
	public List<Candidature> getCandidatureByCentreCandidature(final CentreCandidature ctrCand) {
		Campagne campagneEnCours = campagneController.getCampagneActive();
		if (campagneEnCours == null) {
			return new ArrayList<>();
		}
		List<Candidature> liste = candidatureRepository
			.findByFormationCommissionCentreCandidatureIdCtrCandAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNull(ctrCand.getIdCtrCand(),
				campagneEnCours.getCodCamp());
		traiteListe(liste);
		return liste;
	}

	/** @return les candidatures annulées par centre */
	public List<Candidature> getCandidatureByCommissionCanceled(final Commission commission) {
		Campagne campagneEnCours = campagneController.getCampagneActive();
		if (campagneEnCours == null) {
			return new ArrayList<>();
		}
		List<Candidature> liste =
			candidatureRepository.findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNotNull(commission.getIdComm(),
				campagneEnCours.getCodCamp());
		traiteListe(liste);
		return liste;
	}

	/** @return les candidatures archivées par centre */
	public List<Candidature> getCandidatureByCommissionArchived(final Commission commission) {
		List<Candidature> liste =
			candidatureRepository.findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneDatArchivCampIsNotNull(commission.getIdComm());
		traiteListe(liste);
		return liste;
	}

	/**
	 * Ajoute le dernier type de decision a toutes les candidatures
	 * @param liste
	 */
	private void traiteListe(final List<Candidature> liste) {
		liste.forEach(e -> {
			e.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(e));
		});
	}

	/**
	 * @param  listeCandidature
	 * @return                  true si la liste comporte un lock
	 */
	private Boolean checkLockListCandidature(final List<Candidature> listeCandidature) {
		for (Candidature candidature : listeCandidature) {
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Unlock une liste de candidature
	 * @param listeCandidature
	 */
	private void unlockListCandidature(final List<Candidature> listeCandidature) {
		listeCandidature.forEach(e -> lockCandidatController.releaseLockCandidature(e));
	}

	/**
	 * Edite les types de traitement de candidatures
	 * @param listeCandidature
	 * @param typeTraitement
	 */
	public Boolean editListCandidatureTypTrait(final List<Candidature> listeCandidature, final TypeTraitement typeTraitement) {
		if (checkLockListCandidature(listeCandidature)) {
			return false;
		}
		Integer nb = listeCandidature.size();
		for (Candidature candidature : listeCandidature) {
			if (nb > 1 && candidature.getLastTypeDecision() != null
				&& candidature.getLastTypeDecision().getTemValidTypeDecCand() != null
				&& candidature.getLastTypeDecision().getTemValidTypeDecCand()) {
				Notification.show(applicationContext.getMessage("candidature.editTypTrait.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return false;
			}
		}
		String user = userController.getCurrentUserLogin();

		for (Candidature e : listeCandidature) {
			Assert.notNull(e, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(e)) {
				continue;
			}
			if (!e.getTypeTraitement().equals(typeTraitement)) {
				e.setTypeTraitement(typeTraitement);
				Boolean sendMailTTAc = false;
				Boolean sendMailStatutAtt = false;
				if (typeTraitement.equals(tableRefController.getTypeTraitementAccesControle())) {
					e.setTemValidTypTraitCand(true);
					sendMailTTAc = true;
				} else {
					e.setTemValidTypTraitCand(false);
				}
				/* si un changement de type de traitement entraine le passage du statut de dossier à "En attente" */
				if (parametreController.getIsStatutAttWhenChangeTT()) {
					e.setTypeStatut(tableRefController.getTypeStatutEnAttente());
					sendMailStatutAtt = true;
				}

				e.setUserModCand(user);
				e.setDatModCand(LocalDateTime.now());
				candidatureRepository.save(e);

				/* Envoie des mails si besoin->on le fait après l'enregistrement au cas ou il y ai un pb de mail */
				if (sendMailTTAc) {
					mailController.sendMailByCod(e.getCandidat().getCompteMinima().getMailPersoCptMin(),
						NomenclatureUtils.MAIL_TYPE_TRAIT_AC,
						null,
						e,
						e.getCandidat().getLangue().getCodLangue());
				}
				if (sendMailStatutAtt) {
					mailController.sendMailByCod(e.getCandidat().getCompteMinima().getMailPersoCptMin(),
						NomenclatureUtils.MAIL_STATUT_AT,
						null,
						e,
						e.getCandidat().getLangue().getCodLangue());
				}
			}
		}

		Notification.show(applicationContext.getMessage("candidature.editTypTrait.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		return true;
	}

	/**
	 * Valide les types de traitement de candidatures
	 * @param listeCandidature
	 */
	public Boolean validTypTrait(final List<Candidature> listeCandidature) {
		if (checkLockListCandidature(listeCandidature)) {
			return false;
		}
		String user = userController.getCurrentUserLogin();

		for (Candidature candidature : listeCandidature) {
			Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}
			if (!candidature.getTemValidTypTraitCand()) {
				candidature.setTemValidTypTraitCand(true);
				candidature.setUserModCand(user);
				candidature.setDatModCand(LocalDateTime.now());
				TypeTraitement typeTraitement = candidature.getTypeTraitement();
				String typeMail = "";
				if (typeTraitement.equals(tableRefController.getTypeTraitementAccesDirect())) {
					typeMail = NomenclatureUtils.MAIL_TYPE_TRAIT_AD;
					TypeDecisionCandidature tdc = saveTypeDecisionCandidature(candidature,
						candidature.getFormation().getTypeDecisionFav(),
						true,
						user,
						ConstanteUtils.TYP_DEC_CAND_ACTION_AD);
					candidature.getTypeDecisionCandidatures().add(tdc);
					candidature.setLastTypeDecision(tdc);
				} else if (typeTraitement.equals(tableRefController.getTypeTraitementAccesControle())) {
					typeMail = NomenclatureUtils.MAIL_TYPE_TRAIT_AC;
				} else {
					typeMail = NomenclatureUtils.MAIL_TYPE_TRAIT_ATT;
				}
				candidatureRepository.save(candidature);
				mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),
					typeMail,
					null,
					candidature,
					candidature.getCandidat().getLangue().getCodLangue());
			}
		}

		Notification.show(applicationContext.getMessage("candidature.validTypTrait.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		return true;
	}

	/**
	 * Enregistre un type de decision pour une candidature
	 * @param  candidature
	 * @param  typeDecision
	 * @return              le TypeDecision appliqué
	 */
	public TypeDecisionCandidature saveTypeDecisionCandidature(final Candidature candidature,
		final TypeDecision typeDecision,
		final Boolean valid,
		final String user,
		final String codAction) {
		TypeDecisionCandidature typeDecisionCandidature = new TypeDecisionCandidature(candidature, typeDecision);
		typeDecisionCandidature.setTemValidTypeDecCand(valid);
		typeDecisionCandidature.setUserCreTypeDecCand(user);
		typeDecisionCandidature.setTemAppelTypeDecCand(false);

		if (valid) {
			typeDecisionCandidature.setDatValidTypeDecCand(LocalDateTime.now());
			typeDecisionCandidature.setUserValidTypeDecCand(user);
		}
		return typeDecisionCandidatureRepository.save(typeDecisionCandidature);
	}

	/**
	 * Edite les avis de candidatures
	 * @param  listeCandidature
	 * @param  typeDecisionCandidature
	 * @return                         true si tout s'est bien passé
	 */
	public Boolean editAvis(final List<Candidature> listeCandidature, final TypeDecisionCandidature typeDecisionCandidature) {
		if (checkLockListCandidature(listeCandidature)) {
			return false;
		}
		if (parametreController.getIsUtiliseBlocageAvisMasse()) {
			Integer nb = listeCandidature.size();
			for (Candidature candidature : listeCandidature) {
				if (nb > 1 && candidature.getLastTypeDecision() != null
					&& candidature.getLastTypeDecision().getTemValidTypeDecCand() != null
					&& candidature.getLastTypeDecision().getTemValidTypeDecCand()) {
					Notification.show(applicationContext.getMessage("candidature.editAvis.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return false;
				}
			}
		}

		/* Si l'ancien avis donné est un avis LC validé, il faut recalculer le rang reel pour ces formations */
		List<Formation> listeFormLC = new ArrayList<>();

		String user = userController.getCurrentUserLogin();
		for (Candidature e : listeCandidature) {
			Assert.notNull(e, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(e)) {
				continue;
			}

			/* Calcul du dernier avis */
			TypeDecisionCandidature typeDecision = e.getLastTypeDecision();
			if (typeDecision != null && typeDecision.getTemValidTypeDecCand()
				&& typeDecision.getTypeDecision() != null
				&& typeDecision.getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisListComp())) {
				Formation formLc = e.getFormation();
				if (formLc.getTemListCompForm() && !listeFormLC.contains(formLc)) {
					listeFormLC.add(e.getFormation());
				}
			}

			typeDecisionCandidature.setIdTypeDecCand(null);
			typeDecisionCandidature.setCandidature(e);
			typeDecisionCandidature.setDatCreTypeDecCand(LocalDateTime.now());
			typeDecisionCandidature.setTemValidTypeDecCand(false);
			typeDecisionCandidature.setUserCreTypeDecCand(user);

			typeDecisionCandidatureRepository.save(typeDecisionCandidature);
			e.setTemAcceptCand(null);
			e.setUserModCand(user);
			e.getTypeDecisionCandidatures().add(typeDecisionCandidature);
			e.setLastTypeDecision(typeDecisionCandidature);
			e.setDatModCand(LocalDateTime.now());
			candidatureRepository.save(e);
		}

		/* Recalcul des rang LC si besoin */
		if (parametreController.isCalculRangReelLc()) {
			candidatureGestionController.calculRangReelListForm(listeFormLC);
		}

		Notification.show(applicationContext.getMessage("candidature.editAvis.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		return true;
	}

	/**
	 * Valide les avis de candidatures
	 * @param  listeCandidature
	 * @return                  true si tout s'est bien passé
	 */
	public Boolean validAvis(final List<Candidature> listeCandidature) {
		if (checkLockListCandidature(listeCandidature)) {
			return false;
		}
		for (Candidature candidature : listeCandidature) {
			if (candidature.getLastTypeDecision() == null) {
				Notification.show(applicationContext.getMessage("candidature.validAvis.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return false;
			}
		}
		String user = userController.getCurrentUserLogin();

		/* Si l'avis donné est un avis LC, il faut recalculer le rang reel pour chaque formation */
		List<Formation> listeFormLC = new ArrayList<>();

		for (Candidature candidature : listeCandidature) {
			Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}
			TypeDecisionCandidature typeDecision = candidature.getLastTypeDecision().cloneCompletTypeDecisionCandidature();
			if (typeDecision.getTypeDecision() != null && typeDecision.getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisListComp())) {
				Formation formLc = candidature.getFormation();
				if (formLc.getTemListCompForm() && !listeFormLC.contains(formLc)) {
					listeFormLC.add(candidature.getFormation());
				}
			}

			typeDecision.setTemValidTypeDecCand(true);
			typeDecision.setDatValidTypeDecCand(LocalDateTime.now());
			typeDecision.setUserValidTypeDecCand(user);
			typeDecision = typeDecisionCandidatureRepository.save(typeDecision);

			String localeCandidat = candidature.getCandidat().getLangue().getCodLangue();

			candidature.setUserModCand(user);
			candidature.setDatModCand(LocalDateTime.now());
			candidature.setTypeDecision(typeDecision);
			candidature.setLastTypeDecision(typeDecision);
			candidatureRepository.save(candidature);
			String motif = "";
			if (typeDecision.getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisDefavorable()) && typeDecision.getMotivationAvis() != null) {
				motif = i18nController.getI18nTraduction(typeDecision.getMotivationAvis().getI18nLibMotiv(), localeCandidat);
			}

			String complementAppel = "";
			if (typeDecision.getTemAppelTypeDecCand()) {
				complementAppel = applicationContext.getMessage("candidature.mail.complement.appel", null, UI.getCurrent().getLocale());
			}
			String rang = "";
			if (typeDecision.getListCompRangTypDecCand() != null) {
				rang = String.valueOf(typeDecision.getListCompRangTypDecCand());
			}

			String commentaire = "";
			if (typeDecision.getTypeDecision().getTemAffCommentTypDec()) {
				commentaire = typeDecision.getCommentTypeDecCand();
			}

			AvisMailBean mailBean = new AvisMailBean(motif, commentaire, getComplementPreselectMail(typeDecision), complementAppel, rang);
			PdfAttachement attachement = null;
			InputStream is = candidatureController.downloadLettre(candidature, ConstanteUtils.TYP_LETTRE_MAIL, localeCandidat, true);
			if (is != null) {
				try {
					attachement =
						new PdfAttachement(is, candidatureController.getNomFichierLettre(candidature, ConstanteUtils.TYP_LETTRE_MAIL, localeCandidat));
				} catch (Exception e) {
					attachement = null;
				}
			}
			mailController.sendMail(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),
				typeDecision.getTypeDecision().getMail(),
				mailBean,
				candidature,
				localeCandidat,
				attachement);
		}

		/* Recalcul des rang LC si besoin */
		if (parametreController.isCalculRangReelLc()) {
			List<TypeDecisionCandidature> listeTypDecRangReel = candidatureGestionController.calculRangReelListForm(listeFormLC);
			/* Pour chaque candidature recalculée, on ajouter le rang reel */
			listeTypDecRangReel.forEach(td -> {
				/* On cherche la candidature associée */
				Optional<Candidature> optCand = listeCandidature.stream().filter(cand -> cand.equals(td.getCandidature())).findFirst();
				if (optCand.isPresent()) {
					optCand.get().setTypeDecision(td);
					optCand.get().setLastTypeDecision(td);
				}
			});
		}

		Notification.show(applicationContext.getMessage("candidature.validAvis.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		return true;
	}

	/**
	 * @param  candidature
	 * @param  typeDecision
	 * @return              supprime un avis
	 */
	public Candidature deleteAvis(Candidature candidature, final TypeDecisionCandidature typeDecision) {
		Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		/* Verrou */
		if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
			return null;
		}

		String user = userController.getCurrentUserLogin();
		typeDecisionCandidatureRepository.delete(typeDecision);
		candidature.removeTypeDecision(typeDecision);
		candidature.setUserModCand(user);
		candidature.setDatModCand(LocalDateTime.now());
		candidature = candidatureRepository.save(candidature);
		candidature.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(candidature));
		return candidature;
	}

	/**
	 * @param  typeDecision
	 * @return              un eventuel complément de préselection
	 */
	public String getComplementPreselectMail(final TypeDecisionCandidature typeDecision) {
		String complementPreselect = "";
		if (typeDecision.getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisPreselect())
			&& ((typeDecision.getPreselectDateTypeDecCand() != null)
				|| (typeDecision.getPreselectHeureTypeDecCand() != null)
				|| (typeDecision.getPreselectLieuTypeDecCand() != null && !typeDecision.getPreselectLieuTypeDecCand().equals("")))) {
			complementPreselect = applicationContext.getMessage("candidature.mail.complement.preselect", null, UI.getCurrent().getLocale()) + " ";
			complementPreselect = complementPreselect + getComplementPreselect(typeDecision);
			/* Suppression du dernier espace */
			if (complementPreselect != null && complementPreselect.length() != 0
				&& complementPreselect.substring(complementPreselect.length() - 1, complementPreselect.length()).equals(" ")) {
				complementPreselect = complementPreselect.substring(0, complementPreselect.length() - 1);
			}
		}
		return complementPreselect;
	}

	/**
	 * @param  typeDecision
	 * @return              un eventuel complément de préselection
	 */
	public String getComplementPreselect(final TypeDecisionCandidature typeDecision) {
		String complementPreselect = "";
		if (typeDecision.getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisPreselect())
			&& ((typeDecision.getPreselectDateTypeDecCand() != null)
				|| (typeDecision.getPreselectHeureTypeDecCand() != null)
				|| (typeDecision.getPreselectLieuTypeDecCand() != null && !typeDecision.getPreselectLieuTypeDecCand().equals("")))) {
			if (typeDecision.getPreselectDateTypeDecCand() != null) {
				complementPreselect = complementPreselect + applicationContext.getMessage("candidature.mail.complement.preselect.date",
					new Object[]
					{ formatterDate.format(typeDecision.getPreselectDateTypeDecCand()) },
					UI.getCurrent().getLocale()) + " ";
			}
			if (typeDecision.getPreselectHeureTypeDecCand() != null) {
				complementPreselect = complementPreselect + applicationContext.getMessage("candidature.mail.complement.preselect.heure",
					new Object[]
					{ formatterTime.format(typeDecision.getPreselectHeureTypeDecCand()) },
					UI.getCurrent().getLocale()) + " ";
			}
			if (typeDecision.getPreselectLieuTypeDecCand() != null && !typeDecision.getPreselectLieuTypeDecCand().equals("")) {
				complementPreselect = complementPreselect
					+ applicationContext.getMessage("candidature.mail.complement.preselect.lieu", new Object[]
					{ typeDecision.getPreselectLieuTypeDecCand() }, UI.getCurrent().getLocale());
			}
			/* Suppression du dernier espace */
			if (complementPreselect != null && complementPreselect.length() != 0
				&& complementPreselect.substring(complementPreselect.length() - 1, complementPreselect.length()).equals(" ")) {
				complementPreselect = complementPreselect.substring(0, complementPreselect.length() - 1);
			}
		}
		return complementPreselect;
	}

	/**
	 * Change le statut du dossier
	 * @param  listeCandidature
	 * @param  statut
	 * @return                  true si tout s'est bien passé
	 */
	public Boolean editListCandidatureTypStatut(final List<Candidature> listeCandidature, final TypeStatut statut, final LocalDate date) {
		if (checkLockListCandidature(listeCandidature)) {
			return false;
		}
		String user = userController.getCurrentUserLogin();

		for (Candidature e : listeCandidature) {
			Assert.notNull(e, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(e)) {
				continue;
			}
			if (statut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_REC)) {
				e.setDatReceptDossierCand(date);
			} else if (statut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_COM)) {
				e.setDatCompletDossierCand(date);
			} else if (statut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_INC)) {
				e.setDatIncompletDossierCand(date);
			}
			e.setTypeStatut(statut);
			e.setDatModTypStatutCand(LocalDateTime.now());
			e.setUserModCand(user);
			candidatureRepository.save(e);
			mailController.sendMailByCod(e.getCandidat().getCompteMinima().getMailPersoCptMin(),
				NomenclatureUtils.MAIL_STATUT_PREFIX + statut.getCodTypStatut(),
				null,
				e,
				e.getCandidat().getLangue().getCodLangue());
		}

		Notification.show(applicationContext.getMessage("candidature.editStatutDossier.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		return true;
	}

	/**
	 * Voir l'historique des avis d'une candidature
	 * @param candidature
	 * @param changeCandidatureWindowListener
	 */
	public void showHistoAvis(final Candidature candidature,
		final List<DroitFonctionnalite> listeDroit,
		final ChangeCandidatureWindowListener changeCandidatureWindowListener) {
		CtrCandShowHistoWindow showHistoWindow = new CtrCandShowHistoWindow(candidature, listeDroit);
		showHistoWindow.addDeleteAvisWindowListener(new DeleteAvisWindowListener() {

			@Override
			public void delete(final Candidature candidature) {
				if (changeCandidatureWindowListener != null) {
					List<Candidature> listeCandidature = new ArrayList<>();
					listeCandidature.add(candidature);
					changeCandidatureWindowListener.action(listeCandidature);
				}
			}
		});
		UI.getCurrent().addWindow(showHistoWindow);
	}

	/**
	 * Voir le bloc-notes
	 * @param candidature
	 * @param changeCandidatureWindowListener
	 */
	public void showPostIt(final Candidature candidature,
		final List<DroitFonctionnalite> listeDroit,
		final ChangeCandidatureWindowListener changeCandidatureWindowListener) {
		UI.getCurrent().addWindow(new CtrCandPostItReadWindow(candidature, listeDroit, changeCandidatureWindowListener));
	}

	/**
	 * Enregistre une note
	 * @param  postIt
	 * @return        le postit
	 */
	public PostIt savePostIt(final PostIt postIt) {
		return postItRepository.save(postIt);
	}

	/**
	 * supprime une note
	 * @param postIt
	 */
	public void deletePostIt(final PostIt postIt) {
		postItRepository.delete(postIt);
	}

	/**
	 * @param  candidature
	 * @return             les notes
	 */
	public List<PostIt> getPostIt(final Candidature candidature) {
		return postItRepository.findByCandidatureIdCand(candidature.getIdCand());
	}

	/**
	 * Modifie un numero opi
	 * @param  listeCandidature
	 * @param  newOpi
	 * @param  isSendMail
	 * @return                  true si tout s'est bien passé
	 */
	public Boolean editOpi(final List<Candidature> listeCandidature, final Opi newOpi, final Boolean isSendMail) {
		if (listeCandidature.size() > 1) {
			return false;
		}
		if (checkLockListCandidature(listeCandidature)) {
			return false;
		}
		String user = userController.getCurrentUserLogin();

		for (Candidature candidature : listeCandidature) {
			Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}

			String newCodeOpi = newOpi.getCodOpi();

			Opi opi = candidature.getOpi();
			if (opi == null) {
				opi = new Opi(candidature);
			}
			opi.setCodOpi(newCodeOpi);
			opi.setDatPassageOpi(LocalDateTime.now());
			Opi opiSave = opiRepository.save(opi);

			candidature.setOpi(opiSave);
			candidature.setUserModCand(user);
			candidature = candidatureRepository.save(candidature);
			if (isSendMail) {
				opiController.sendMailChangeCodeOpi(candidature.getCandidat(), newCodeOpi, "<ul><li>" + candidature.getFormation().getLibForm() + "</li></ul>");
			}
		}
		Notification.show(applicationContext.getMessage("candidature.action.opi.notif", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		return true;
	}

	/**
	 * @param  listeCandidature
	 * @param  listeTag
	 * @return                  modifie un tag
	 */
	public boolean editTag(final List<Candidature> listeCandidature, final List<Tag> listeTag) {
		if (checkLockListCandidature(listeCandidature)) {
			return false;
		}
		String user = userController.getCurrentUserLogin();

		for (Candidature candidature : listeCandidature) {
			Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}

			candidature.setTags(listeTag);
			candidature.setUserModCand(user);
			candidature = candidatureRepository.save(candidature);
		}
		Notification.show(applicationContext.getMessage("candidature.action.tags.notif", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		return true;
	}

	/**
	 * @param  listeCandidature
	 * @param  bean
	 * @return                  modifie une date de confirmation
	 */
	public boolean editDatConfirm(final List<Candidature> listeCandidature, final Candidature bean) {
		if (checkLockListCandidature(listeCandidature)) {
			return false;
		}
		String user = userController.getCurrentUserLogin();

		for (Candidature candidature : listeCandidature) {
			Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}
			candidature.setDatNewConfirmCand(bean.getDatNewConfirmCand());
			candidature.setUserModCand(user);
			candidature = candidatureRepository.save(candidature);
		}
		Notification.show(applicationContext.getMessage("candidature.action.datNewConfirmCand.notif", null, UI.getCurrent().getLocale()),
			Type.TRAY_NOTIFICATION);
		return true;
	}

	/**
	 * @param  listeCandidature
	 * @param  bean
	 * @return                  modifie une date de retour
	 */
	public boolean editDatRetour(final List<Candidature> listeCandidature, final Candidature bean) {
		if (checkLockListCandidature(listeCandidature)) {
			return false;
		}
		String user = userController.getCurrentUserLogin();

		for (Candidature candidature : listeCandidature) {
			Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}
			candidature.setDatNewRetourCand(bean.getDatNewRetourCand());
			candidature.setUserModCand(user);
			candidature = candidatureRepository.save(candidature);
		}
		Notification.show(applicationContext.getMessage("candidature.action.datNewRetourCand.notif", null, UI.getCurrent().getLocale()),
			Type.TRAY_NOTIFICATION);
		return true;
	}

	/**
	 * Edite les actions de candidatures en masse
	 * @param listeCandidature
	 * @param listeDroit
	 * @param centreCandidature
	 */
	public void editActionCandidatureMasse(final List<Candidature> listeCandidature,
		final List<DroitFonctionnalite> listeDroit,
		final CentreCandidature centreCandidature,
		final CandidatureMasseListener listener) {
		if (checkLockListCandidature(listeCandidature)) {
			unlockListCandidature(listeCandidature);
			return;
		}
		CtrCandActionCandidatureWindow window = new CtrCandActionCandidatureWindow(listeCandidature, listeDroit, centreCandidature);
		window.addCloseListener(e -> unlockListCandidature(listeCandidature));
		window.addChangeCandidatureWindowListener(new ChangeCandidatureWindowListener() {

			@Override
			public void action(final List<Candidature> listeCandidature) {
				if (listener != null) {
					listener.actionMasse();
				}
			}
		});
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Ouvre la fenetre du choix de l'action sur les candidatures selectionnées dans une candidature
	 * @param candidature
	 * @param listener
	 * @param listeDroit
	 */
	public void editActionCandidature(final Candidature candidature, final CandidatureListener listener, final List<DroitFonctionnalite> listeDroit) {
		List<Candidature> liste = new ArrayList<>();
		liste.add(candidature);
		/* On vérifie les locks mais on ne l'enleve pas car on est dans la fenetre de
		 * candidature */
		if (checkLockListCandidature(liste)) {
			return;
		}
		CtrCandActionCandidatureWindow window =
			new CtrCandActionCandidatureWindow(liste, listeDroit, candidature.getFormation().getCommission().getCentreCandidature());
		window.addChangeCandidatureWindowListener(new ChangeCandidatureWindowListener() {

			@Override
			public void openCandidature(final Candidature candidature) {
				if (candidature != null) {
					listener.openCandidat();
				}
			}

			@Override
			public void action(final List<Candidature> listeCandidature) {
				if (listeCandidature != null && listeCandidature.get(0) != null) {
					listener.infosCandidatureModified(listeCandidature.get(0));
				}
			}

			@Override
			public void removePostIt(final PostIt postIt) {
				listener.removePostIt(postIt);
			}

			@Override
			public void addPostIt(final PostIt postIt) {
				listener.addPostIt(postIt);
			}

			@Override
			public void updateTypTrait(final Candidature cand) {
				if (candidature != null) {
					listener.reloadAllPiece(candidaturePieceController.getPjCandidature(candidature), candidature);
				}
			}
		});
		UI.getCurrent().addWindow(window);

	}

	/**
	 * Génère un export de PJ
	 * @param  centreCandidature
	 * @param  listeCand
	 * @return                   l'InputStream du fichier d'export
	 */
	public OnDemandFile generateExportPj(final CentreCandidature centreCandidature, final List<Candidature> listeCand) {
		return null;
	}

	/**
	 * Exporte les candidatures
	 * @param  ctrCand
	 * @param  allOptions
	 * @param  optionChecked
	 * @param  temFooter
	 * @return               l'InputStream du fichier d'export
	 */
	public OnDemandFile generateExport(final CentreCandidature ctrCand,
		final LinkedHashSet<ExportListCandidatureOption> allOptions,
		final Set<ExportListCandidatureOption> optionChecked,
		final Boolean temFooter) {
		List<Candidature> liste = getCandidatureByCentreCandidature(ctrCand);
		return generateExport(ctrCand
			.getCodCtrCand(), ctrCand.getLibCtrCand() + " (" + ctrCand.getCodCtrCand() + ")", liste, allOptions, optionChecked, temFooter);
	}

	/**
	 * Exporte les candidatures
	 * @param  liste
	 * @param  allOptions
	 * @param  optionChecked
	 * @return               l'InputStream du fichier d'export
	 */
	public OnDemandFile generateExport(final Commission commission,
		final List<Candidature> liste,
		final LinkedHashSet<ExportListCandidatureOption> allOptions,
		final Set<ExportListCandidatureOption> optionChecked,
		final Boolean temFooter) {
		return generateExport(commission
			.getCodComm(), commission.getLibComm() + " (" + commission.getCodComm() + ")", liste, allOptions, optionChecked, temFooter);
	}

	/**
	 * @param  cellVal
	 * @return         une cellule tronquée avec sa valeur max
	 */
	private String formatLongCellSize(final String cellVal) {
		if (cellVal != null && cellVal.length() > ConstanteUtils.EXPORT_CELL_MAX_SIZE) {
			return cellVal.substring(0, ConstanteUtils.EXPORT_CELL_MAX_SIZE);
		}
		return cellVal;
	}

	/**
	 * Exporte les candidatures
	 * @param  liste
	 * @param  allOptions
	 * @param  optionChecked
	 * @return               le fichier
	 */
	private OnDemandFile generateExport(final String code,
		final String libelle,
		final List<Candidature> liste,
		final LinkedHashSet<ExportListCandidatureOption> allOptions,
		final Set<ExportListCandidatureOption> optionChecked,
		final Boolean temFooter) {
		if (liste == null || liste.size() == 0) {
			return null;
		}
		Map<String, Object> beans = new HashMap<>();

		/* Traitement des dates */
		liste.forEach(candidature -> {
			candidature.setDatCreCandStr(candidature.getDatCreCand().format(formatterDateTime));
			candidature.getCandidat().setAdresseCandidatExport(generateAdresse(candidature.getCandidat().getAdresse()));
			candidature.getCandidat().setDatNaissanceCandidatStr(MethodUtils.formatDate(candidature.getCandidat().getDatNaissCandidat(), formatterDate));

			if (candidature.getLastTypeDecision() != null) {
				candidature.getLastTypeDecision()
					.setDatValidTypeDecCandStr(MethodUtils.formatDate(candidature.getLastTypeDecision().getDatValidTypeDecCand(), formatterDate));
				candidature.getLastTypeDecision().setPreselectStr(getComplementPreselectMail(candidature.getLastTypeDecision()));
				candidature.getLastTypeDecision()
					.setPreselectDateTypeDecCandStr(MethodUtils.formatDate(candidature.getLastTypeDecision().getPreselectDateTypeDecCand(), formatterDate));
			}

			candidature.setDatModTypStatutCandStr(MethodUtils.formatDate(candidature.getDatModTypStatutCand(), formatterDateTime));
			candidature.setDatReceptDossierCandStr(MethodUtils.formatDate(candidature.getDatReceptDossierCand(), formatterDate));
			candidature.setDatTransDossierCandStr(MethodUtils.formatDate(candidature.getDatTransDossierCand(), formatterDateTime));
			candidature.setDatCompletDossierCandStr(MethodUtils.formatDate(candidature.getDatCompletDossierCand(), formatterDate));
			candidature.setDatAnnulCandStr(MethodUtils.formatDate(candidature.getDatAnnulCand(), formatterDateTime));
			candidature.setDatNewConfirmCandStr(MethodUtils.formatDate(candidature.getDatNewConfirmCand(), formatterDate));
			candidature.setDatNewRetourCandStr(MethodUtils.formatDate(candidature.getDatNewRetourCand(), formatterDate));
			candidature.setDatIncompletDossierCandStr(MethodUtils.formatDate(candidature.getDatIncompletDossierCand(), formatterDate));
			candidature.setDatModPjForm(getDatModPjForm(candidature));

			/* Tags */
			candidature.setTagsStr(formatLongCellSize(candidature.getTags().stream().map(e -> e.getLibTag()).collect(Collectors.joining(" / "))));

			/* Bloc note */
			if (parametreController.getIsExportBlocNote()) {
				candidature
					.setBlocNoteStr(formatLongCellSize(getPostIt(candidature).stream().map(e -> e.getMessagePostIt()).collect(Collectors.joining(" / "))));
			} else {
				allOptions.add(
					new ExportListCandidatureOption("postItHide", applicationContext.getMessage("export.option.postit", null, UI.getCurrent().getLocale())));
			}

			/* Definition du dernier etablissement frequenté */
			Candidat candidat = candidature.getCandidat();

			String lastEtab = "";
			String lastDiplome = "";
			String lastLibelleDiplome = "";
			Integer annee = 0;
			for (CandidatCursusInterne cursus : candidat.getCandidatCursusInternes()) {
				if (cursus.getAnneeUnivCursusInterne() > annee) {
					annee = cursus.getAnneeUnivCursusInterne();
					lastEtab = applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale());
					lastDiplome = cursus.getLibCursusInterne();
				}
			}
			for (CandidatCursusPostBac cursus : candidat.getCandidatCursusPostBacs()) {
				if (cursus.getAnneeUnivCursus() > annee && cursus.getSiScolEtablissement() != null) {
					annee = cursus.getAnneeUnivCursus();
					lastEtab = cursus.getSiScolEtablissement().getLibEtb();
					lastDiplome = cursus.getSiScolDipAutCur().getLibDac();
					lastLibelleDiplome = cursus.getLibCursus();
				}
			}
			candidat.setLastEtab(lastEtab);
			candidat.setLastDiplome(lastDiplome);
			candidat.setLastLibDiplome(lastLibelleDiplome);
		});
		if (code != null) {
			beans.put("code", code);
		}
		beans.put("candidatures", liste);
		allOptions.stream().forEach(exportOption -> {
			addExportOption(exportOption, optionChecked, beans);
		});
		if (temFooter) {
			beans.put("footer",
				applicationContext.getMessage("export.footer", new Object[]
				{ libelle, liste.size(), formatterDateTime.format(LocalDateTime.now()) }, UI.getCurrent().getLocale()));
		} else {
			beans.put("footer", "");
		}
		String libFile =
			applicationContext.getMessage("export.nom.fichier", new Object[]
			{ libelle, DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now()) },
				UI.getCurrent().getLocale());

		return exportController.generateXlsxExport(beans, "candidatures_template", libFile, Arrays.asList(0));
	}

	/**
	 * calcul la derniere modif de statut de PJ ou Formulaire
	 * @param  candidature
	 * @param  formatter
	 * @return             la date de derniere modif
	 */
	private String getDatModPjForm(final Candidature candidature) {
		LocalDateTime dateMod = null;
		Optional<FormulaireCand> formOpt = candidature.getFormulaireCands()
			.stream()
			.filter(e -> e.getDatModFormulaireCand() != null)
			.sorted((e1, e2) -> (e2.getDatModFormulaireCand().compareTo(e1.getDatModFormulaireCand())))
			.findFirst();
		Optional<FormulaireCandidat> formCandidatOpt = candidature.getCandidat()
			.getFormulaireCandidats()
			.stream()
			.filter(e -> e.getDatReponseFormulaireCandidat() != null)
			.sorted((e1, e2) -> (e2.getDatReponseFormulaireCandidat().compareTo(e1.getDatReponseFormulaireCandidat())))
			.findFirst();
		Optional<PjCand> pjOpt = candidature.getPjCands()
			.stream()
			.filter(e -> e.getDatModStatutPjCand() != null)
			.sorted((e1, e2) -> (e2.getDatModStatutPjCand().compareTo(e1.getDatModStatutPjCand())))
			.findFirst();
		/* On prend la derniere modif des formulaire_cand */
		if (formOpt.isPresent()) {
			dateMod = formOpt.get().getDatCreFormulaireCand();
		}
		/* on compare avec la derniere réponse FormulaireCandidat */
		if (formCandidatOpt.isPresent()) {
			FormulaireCandidat form = formCandidatOpt.get();
			if (dateMod == null) {
				dateMod = form.getDatReponseFormulaireCandidat();
			} else {
				dateMod = (form.getDatReponseFormulaireCandidat().isAfter(dateMod)) ? form.getDatReponseFormulaireCandidat() : dateMod;
			}
		}

		/* on compare avec la derniere réponse des PJ */
		if (pjOpt.isPresent()) {
			PjCand pj = pjOpt.get();
			if (dateMod == null) {
				dateMod = pj.getDatModStatutPjCand();
			} else {
				dateMod = (pj.getDatModStatutPjCand().isAfter(dateMod)) ? pj.getDatModStatutPjCand() : dateMod;
			}
		}
		if (dateMod == null) {
			return "";
		} else {
			return dateMod.format(formatterDate);
		}
	}

	/**
	 * @param  adresse
	 * @return         adresse formatée
	 */
	private ExportListCandidatureAdresse generateAdresse(final Adresse adresse) {
		ExportListCandidatureAdresse adresseBean = new ExportListCandidatureAdresse();
		String libAdr = "";
		if (adresse != null) {
			if (adresse.getAdr1Adr() != null) {
				libAdr = libAdr + adresse.getAdr1Adr() + " ";
				adresseBean.setAdr1(adresse.getAdr1Adr());
			}
			if (adresse.getAdr2Adr() != null) {
				libAdr = libAdr + adresse.getAdr2Adr() + " ";
				adresseBean.setAdr2(adresse.getAdr2Adr());
			}
			if (adresse.getAdr3Adr() != null) {
				libAdr = libAdr + adresse.getAdr3Adr() + " ";
				adresseBean.setAdr3(adresse.getAdr3Adr());
			}
			if (adresse.getCodBdiAdr() != null && adresse.getSiScolCommune() != null && adresse.getSiScolCommune().getLibCom() != null) {
				libAdr = libAdr + adresse.getCodBdiAdr() + " " + adresse.getSiScolCommune().getLibCom() + " ";
				adresseBean.setCodBdi(adresse.getCodBdiAdr());
				adresseBean.setLibCommune(adresse.getSiScolCommune().getLibCom());
			} else {
				if (adresse.getCodBdiAdr() != null) {
					libAdr = libAdr + adresse.getCodBdiAdr() + " ";
					adresseBean.setCodBdi(adresse.getCodBdiAdr());
				}
				if (adresse.getSiScolCommune() != null && adresse.getSiScolCommune().getLibCom() != null) {
					libAdr = libAdr + adresse.getSiScolCommune().getLibCom() + " ";
					adresseBean.setLibCommune(adresse.getSiScolCommune().getLibCom());
				}
			}
			if (adresse.getLibComEtrAdr() != null) {
				libAdr = libAdr + adresse.getLibComEtrAdr() + " ";
				adresseBean.setLibComEtr(adresse.getLibComEtrAdr());
			}
			if (adresse.getSiScolPays() != null && !adresse.getSiScolPays().equals(cacheController.getPaysFrance())) {
				libAdr = libAdr + adresse.getSiScolPays().getLibPay();
				adresseBean.setLibPays(adresse.getSiScolPays().getLibPay());
			}
		}
		adresseBean.setLibelle(libAdr);
		return adresseBean;
	}

	/**
	 * Doit-on cacher ou afficher les colonnes --> true cacher, false afficher
	 * @param exportOption
	 * @param optionChecked
	 * @param beans
	 */
	private void
		addExportOption(final ExportListCandidatureOption exportOption, final Set<ExportListCandidatureOption> optionChecked, final Map<String, Object> beans) {
		if (optionChecked.contains(exportOption)) {
			beans.put(exportOption.getId(), false);
		} else {
			beans.put(exportOption.getId(), true);
		}
	}

	/**
	 * Ouvre le dossier d'un candidat
	 * @param candidature
	 */
	public void openCandidat(final Candidature candidature) {
		CompteMinima cpt = candidature.getCandidat().getCompteMinima();
		userController.setNoDossierNomCandidat(cpt);
		MainUI.getCurrent().buildMenuGestCand(false);
	}
}
