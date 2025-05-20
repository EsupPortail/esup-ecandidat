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
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
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
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolOptionBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolSpecialiteBac;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.OpiRepository;
import fr.univlorraine.ecandidat.repositories.PostItRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionCandidatureRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
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

	private final Logger logger = LoggerFactory.getLogger(CandidatureCtrCandController.class);

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

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

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
		final Campagne campagneEnCours = campagneController.getCampagneActive();
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
		final Campagne campagneEnCours = campagneController.getCampagneActive();
		if (campagneEnCours == null) {
			return new ArrayList<>();
		}
		final List<Candidature> liste = candidatureRepository
			.findByFormationCommissionCentreCandidatureIdCtrCandAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNull(ctrCand.getIdCtrCand(),
				campagneEnCours.getCodCamp());
		traiteListe(liste);
		return liste;
	}

	/** @return les candidatures annulées par centre */
	public List<Candidature> getCandidatureByCommissionCanceled(final Commission commission) {
		final Campagne campagneEnCours = campagneController.getCampagneActive();
		if (campagneEnCours == null) {
			return new ArrayList<>();
		}
		final List<Candidature> liste =
			candidatureRepository.findByFormationCommissionIdCommAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNotNull(commission.getIdComm(),
				campagneEnCours.getCodCamp());
		traiteListe(liste);
		return liste;
	}

	/** @return les candidatures archivées par centre */
	public List<Candidature> getCandidatureByCommissionArchived(final Commission commission) {
		final List<Candidature> liste =
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
		for (final Candidature candidature : listeCandidature) {
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
		final Integer nb = listeCandidature.size();
		for (final Candidature candidature : listeCandidature) {
			if (nb > 1 && candidature.getLastTypeDecision() != null
				&& candidature.getLastTypeDecision().getTemValidTypeDecCand() != null
				&& candidature.getLastTypeDecision().getTemValidTypeDecCand()) {
				Notification.show(applicationContext.getMessage("candidature.editTypTrait.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return false;
			}
		}
		final String user = userController.getCurrentUserLogin();

		for (final Candidature e : listeCandidature) {
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
		final String user = userController.getCurrentUserLogin();

		for (final Candidature candidature : listeCandidature) {
			Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}
			if (!candidature.getTemValidTypTraitCand()) {
				candidature.setTemValidTypTraitCand(true);
				candidature.setUserModCand(user);
				candidature.setDatModCand(LocalDateTime.now());
				final TypeTraitement typeTraitement = candidature.getTypeTraitement();
				String typeMail = "";
				if (typeTraitement.equals(tableRefController.getTypeTraitementAccesDirect())) {
					typeMail = NomenclatureUtils.MAIL_TYPE_TRAIT_AD;
					final TypeDecisionCandidature tdc = saveTypeDecisionCandidature(candidature,
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
		final TypeDecisionCandidature typeDecisionCandidature = new TypeDecisionCandidature(candidature, typeDecision);
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
			final Integer nb = listeCandidature.size();
			for (final Candidature candidature : listeCandidature) {
				if (nb > 1 && candidature.getLastTypeDecision() != null
					&& candidature.getLastTypeDecision().getTemValidTypeDecCand() != null
					&& candidature.getLastTypeDecision().getTemValidTypeDecCand()) {
					Notification.show(applicationContext.getMessage("candidature.editAvis.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return false;
				}
			}
		}

		/* Si l'ancien avis donné est un avis LC validé, il faut recalculer le rang reel pour ces formations */
		final List<Formation> listeFormLC = new ArrayList<>();

		final String user = userController.getCurrentUserLogin();
		for (final Candidature e : listeCandidature) {
			Assert.notNull(e, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(e)) {
				continue;
			}

			/* Calcul du dernier avis */
			final TypeDecisionCandidature typeDecision = e.getLastTypeDecision();
			if (typeDecision != null && typeDecision.getTemValidTypeDecCand()
				&& typeDecision.getTypeDecision() != null
				&& typeDecision.getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisListComp())) {
				final Formation formLc = e.getFormation();
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
		for (final Candidature candidature : listeCandidature) {
			if (candidature.getLastTypeDecision() == null) {
				Notification.show(applicationContext.getMessage("candidature.validAvis.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return false;
			}
		}

		/* Bug sur la validation d'avis --> des candidatures ont recu le mail mais la candidature n'a pas été validée */
		/* On vérifie qu'il n'y a pas de candidature en double */
		final Long sizelistId = listeCandidature.stream().map(Candidature::getIdCand).distinct().count();
		if (listeCandidature.size() != sizelistId.intValue()) {
			logger.warn(applicationContext.getMessage("candidature.validAvis.doublon", null, UI.getCurrent().getLocale()) + " : erreur size list");
			Notification.show(applicationContext.getMessage("candidature.validAvis.doublon", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return false;
		}

		final String user = userController.getCurrentUserLogin();

		/* Bug sur la validation d'avis --> des candidatures ont recu le mail mais la candidature n'a pas été validée */
		final Map<Integer, Integer> mapIdCandTraite = new HashMap<>();
		int errorDoublon = 0;

		/* Si l'avis donné est un avis LC, il faut recalculer le rang reel pour chaque formation */
		final List<Formation> listeFormLC = new ArrayList<>();

		for (final Candidature candidature : listeCandidature) {
			Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}

			TypeDecisionCandidature typeDecision = candidature.getLastTypeDecision().cloneCompletTypeDecisionCandidature();
			if (typeDecision.getTypeDecision() != null && typeDecision.getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisListComp())) {
				final Formation formLc = candidature.getFormation();
				if (formLc.getTemListCompForm() && !listeFormLC.contains(formLc)) {
					listeFormLC.add(candidature.getFormation());
				}
			}

			typeDecision.setTemValidTypeDecCand(true);
			typeDecision.setDatValidTypeDecCand(LocalDateTime.now());
			typeDecision.setUserValidTypeDecCand(user);
			typeDecision.setCandidature(candidature);

			/* Bug sur la validation d'avis --> des candidatures ont recu le mail mais la candidature n'a pas été validée */
			/* On vérifie que la candidature de l'avis n'a pas déjà été enregistré avant enregistrement */
			if (mapIdCandTraite.get(typeDecision.getCandidature().getIdCand()) != null) {
				errorDoublon++;
				continue;
			} else {
				mapIdCandTraite.put(typeDecision.getCandidature().getIdCand(), typeDecision.getCandidature().getIdCand());
			}

			typeDecision = typeDecisionCandidatureRepository.save(typeDecision);

			final String localeCandidat = candidature.getCandidat().getLangue().getCodLangue();

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

			final AvisMailBean mailBean = new AvisMailBean(motif, commentaire, getComplementPreselectMail(typeDecision), complementAppel, rang);
			PdfAttachement attachement = null;
			final InputStream is = candidatureController.downloadLettre(candidature, ConstanteUtils.TYP_LETTRE_MAIL, localeCandidat, true);
			if (is != null) {
				try {
					attachement =
						new PdfAttachement(is, candidatureController.getNomFichierLettre(candidature, ConstanteUtils.TYP_LETTRE_MAIL, localeCandidat));
				} catch (final Exception e) {
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
			final List<TypeDecisionCandidature> listeTypDecRangReel = candidatureGestionController.calculRangReelListForm(listeFormLC);
			/* Pour chaque candidature recalculée, on ajouter le rang reel */
			listeTypDecRangReel.forEach(td -> {
				/* On cherche la candidature associée */
				final Optional<Candidature> optCand = listeCandidature.stream().filter(cand -> cand.equals(td.getCandidature())).findFirst();
				if (optCand.isPresent()) {
					optCand.get().setTypeDecision(td);
					optCand.get().setLastTypeDecision(td);
				}
			});
		}

		/* Bug sur la validation d'avis --> des candidatures ont recu le mail mais la candidature n'a pas été validée */
		if (errorDoublon > 0) {
			logger.warn(applicationContext.getMessage("candidature.validAvis.doublon", null, UI.getCurrent().getLocale()) + " : erreur map", Type.WARNING_MESSAGE);
			Notification.show(applicationContext.getMessage("candidature.validAvis.doublon", null, UI.getCurrent().getLocale()));
		} else {
			Notification.show(applicationContext.getMessage("candidature.validAvis.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		}
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

		final String user = userController.getCurrentUserLogin();
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
					new Object[] { formatterDate.format(typeDecision.getPreselectDateTypeDecCand()) },
					UI.getCurrent().getLocale()) + " ";
			}
			if (typeDecision.getPreselectHeureTypeDecCand() != null) {
				complementPreselect = complementPreselect + applicationContext.getMessage("candidature.mail.complement.preselect.heure",
					new Object[] { formatterTime.format(typeDecision.getPreselectHeureTypeDecCand()) },
					UI.getCurrent().getLocale()) + " ";
			}
			if (typeDecision.getPreselectLieuTypeDecCand() != null && !typeDecision.getPreselectLieuTypeDecCand().equals("")) {
				complementPreselect = complementPreselect
					+ applicationContext.getMessage("candidature.mail.complement.preselect.lieu", new Object[] { typeDecision.getPreselectLieuTypeDecCand() }, UI.getCurrent().getLocale());
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
		final String user = userController.getCurrentUserLogin();

		for (final Candidature e : listeCandidature) {
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
		final CtrCandShowHistoWindow showHistoWindow = new CtrCandShowHistoWindow(candidature, listeDroit);
		showHistoWindow.addDeleteAvisWindowListener(new DeleteAvisWindowListener() {

			@Override
			public void delete(final Candidature candidature) {
				if (changeCandidatureWindowListener != null) {
					final List<Candidature> listeCandidature = new ArrayList<>();
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
		final String user = userController.getCurrentUserLogin();

		for (Candidature candidature : listeCandidature) {
			Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}

			final String newCodeOpi = newOpi.getCodOpi();

			Opi opi = candidature.getOpi();
			if (opi == null) {
				opi = new Opi(candidature);
			}
			opi.setCodOpi(newCodeOpi);
			opi.setDatPassageOpi(LocalDateTime.now());
			final Opi opiSave = opiRepository.save(opi);

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
		final String user = userController.getCurrentUserLogin();

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
		final String user = userController.getCurrentUserLogin();

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
		final String user = userController.getCurrentUserLogin();

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
	 * @param  listeCandidature
	 * @param  bean
	 * @return                  modifie les infos de montant des droits
	 */
	public boolean editRegime(final List<Candidature> listeCandidature, final Candidature bean) {
		if (checkLockListCandidature(listeCandidature)) {
			return false;
		}
		final String user = userController.getCurrentUserLogin();

		for (Candidature candidature : listeCandidature) {
			Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}
			candidature.setSiScolRegime(bean.getSiScolRegime());
			candidature.setUserModCand(user);
			candidature = candidatureRepository.save(candidature);
		}
		Notification.show(applicationContext.getMessage("candidature.action.siScolRegime.notif", null, UI.getCurrent().getLocale()),
			Type.TRAY_NOTIFICATION);
		return true;
	}

	/**
	 * @param  listeCandidature
	 * @param  bean
	 * @return                  modifie les infos de montant des droits
	 */
	public boolean editMontant(final List<Candidature> listeCandidature, final Candidature bean) {
		if (checkLockListCandidature(listeCandidature)) {
			return false;
		}
		final String user = userController.getCurrentUserLogin();

		for (Candidature candidature : listeCandidature) {
			Assert.notNull(candidature, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
			/* Verrou */
			if (!lockCandidatController.getLockOrNotifyCandidature(candidature)) {
				continue;
			}
			candidature.setSiScolCatExoExt(bean.getSiScolCatExoExt());
			candidature.setCompExoExtCand(bean.getCompExoExtCand());
			candidature.setMntChargeCand(bean.getMntChargeCand());
			candidature.setUserModCand(user);
			candidature = candidatureRepository.save(candidature);
		}
		Notification.show(applicationContext.getMessage("candidature.action.montant.notif", null, UI.getCurrent().getLocale()),
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
		final CtrCandActionCandidatureWindow window = new CtrCandActionCandidatureWindow(listeCandidature, listeDroit, centreCandidature);
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
		final List<Candidature> liste = new ArrayList<>();
		liste.add(candidature);
		/* On vérifie les locks mais on ne l'enleve pas car on est dans la fenetre de
		 * candidature */
		if (checkLockListCandidature(liste)) {
			return;
		}
		final CtrCandActionCandidatureWindow window =
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
		final List<Candidature> liste = getCandidatureByCentreCandidature(ctrCand);
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

		try {
			final Locale locale = UI.getCurrent().getLocale();

			/* Traitement des entete */
			final List<String> listEnTete = new ArrayList<>();
			optionChecked.forEach(option -> {
				switch (option.getId()) {
				case "bacHide":
					listEnTete.add(applicationContext.getMessage("export.option.bac.anneeObtBac", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolPays", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolDepartement", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolCommune", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolEtablissement", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolBacOuxEqu", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolMentionNivBac", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolSpe1BacTer", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolSpe2BacTer", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolSpeBacPre", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolOpt1Bac", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolOpt2Bac", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolOpt3Bac", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.bac.siScolOpt4Bac", null, locale));
					break;
				case "adresseDiviseHide":
					listEnTete.add(applicationContext.getMessage("adresse.adr1Adr", null, locale));
					listEnTete.add(applicationContext.getMessage("adresse.adr2Adr.short", null, locale));
					listEnTete.add(applicationContext.getMessage("adresse.adr3Adr.short", null, locale));
					listEnTete.add(applicationContext.getMessage("adresse.codBdiAdr", null, locale));
					listEnTete.add(applicationContext.getMessage("adresse.siScolCommune", null, locale));
					listEnTete.add(applicationContext.getMessage("adresse.libComEtrAdr", null, locale));
					listEnTete.add(applicationContext.getMessage("adresse.siScolPays", null, locale));
					break;
				case "preselectionHide":
					listEnTete.add(applicationContext.getMessage("export.option.preselection", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.preselectDate", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.preselectHeure", null, locale));
					listEnTete.add(applicationContext.getMessage("export.option.preselectLieu", null, locale));
					break;
				default:
					listEnTete.add(option.getCaption());
					break;
				}

			});

			final List<List<String>> listCandidature = new ArrayList<List<String>>();

			/* Traitement des candidatures */
			liste.forEach(candidature -> {
				final List<String> listValeur = new ArrayList<String>();
				final Candidat candidat = candidature.getCandidat();
				final CompteMinima cptMin = candidat.getCompteMinima();
				final Formation formation = candidature.getFormation();
				final ExportListCandidatureAdresse adresse = generateAdresse(candidat.getAdresse());

				/* Calcul du dernier diplome obtenu */
				if (optionChecked.stream().filter(opt -> "etablissementHide".equals(opt.getId())
					|| "lastDipHide".equals(opt.getId())
					|| "lastLibDipHide".equals(opt.getId())).findAny().isPresent()) {
					String lastEtab = "";
					String lastDiplome = "";
					String lastLibelleDiplome = "";
					Integer annee = 0;
					for (final CandidatCursusInterne cursus : candidat.getCandidatCursusInternes()) {
						if (cursus.getAnneeUnivCursusInterne() > annee) {
							annee = cursus.getAnneeUnivCursusInterne();
							lastEtab = applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale()).toUpperCase();
							lastDiplome = cursus.getCodVetCursusInterne();
							lastLibelleDiplome = cursus.getLibCursusInterne();
						}
					}
					for (final CandidatCursusPostBac cursus : candidat.getCandidatCursusPostBacs()) {
						if (cursus.getAnneeUnivCursus() > annee) {
							annee = cursus.getAnneeUnivCursus();
							lastEtab = cursus.getSiScolEtablissement() != null ? cursus.getSiScolEtablissement().getLibEtb() : null;
							if (StringUtils.isBlank(lastEtab) && cursus.getSiScolPays() != null && !cursus.getSiScolPays().isCodePays(siScolService.getCodPaysFrance())) {
								lastEtab = applicationContext.getMessage("export.option.etablissementEtr", new Object[] { cursus.getSiScolPays().getLibPay() }, UI.getCurrent().getLocale()).toUpperCase();
							}
							lastDiplome = cursus.getSiScolDipAutCur() != null ? cursus.getSiScolDipAutCur().getLibDac() : null;
							lastLibelleDiplome = cursus.getLibCursus();
						}
					}
					candidat.setLastEtab(lastEtab);
					candidat.setLastDiplome(lastDiplome);
					candidat.setLastLibDiplome(lastLibelleDiplome);
				}

				/* Calcul de la dernière decision */
				final TypeDecisionCandidature lastTypeDec = candidature.getLastTypeDecision();

				optionChecked.forEach(option -> {
					switch (option.getId()) {
					case "numDossierHide":
						listValeur.add(MethodUtils.formatToExport(cptMin.getNumDossierOpiCptMin()));
						break;
					case "numEtuHide":
						listValeur.add(MethodUtils.formatToExport(cptMin.getSupannEtuIdCptMin()));
						break;
					case "civiliteHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getCivilite().getCodCiv()));
						break;
					case "nomPatHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getNomPatCandidat()));
						break;
					case "nomUsuHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getNomUsuCandidat()));
						break;
					case "prenomHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getPrenomCandidat()));
						break;
					case "dtNaissHide":
						listValeur.add(MethodUtils.formatDate(candidature.getCandidat().getDatNaissCandidat(), formatterDate));
						break;
					case "villeNaissHide":
						listValeur.add(candidature.getCandidat().getLibVilleNaissCandidat());
						break;
					case "nationaliteHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getSiScolPaysNat().getLicPay()));
						break;
					case "langueHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getLangue().getLibLangue()));
						break;
					case "etuIdHide":
						listValeur.add(MethodUtils.formatToExport(cptMin.getSupannEtuIdCptMin()));
						break;
					case "ineHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getIneCandidat()));
						break;
					case "cleIneHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getCleIneCandidat()));
						break;
					case "temFcHide":
						listValeur.add(MethodUtils.formatBoolToExport(cptMin.getTemFcCptMin()));
						break;
					case "telHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getTelCandidat()));
						break;
					case "telPortHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getTelPortCandidat()));
						break;
					case "mailHide":
						listValeur.add(MethodUtils.formatToExport(cptMin.getMailPersoCptMin()));
						break;
					case "bacHide":
						final CandidatBacOuEqu bac = candidat.getCandidatBacOuEqu();
						final SiScolPays bacPays = bac != null ? bac.getSiScolPays() : null;
						final SiScolDepartement bacDpt = bac != null ? bac.getSiScolDepartement() : null;
						final SiScolCommune bacComm = bac != null ? bac.getSiScolCommune() : null;
						final SiScolEtablissement bacEtab = bac != null ? bac.getSiScolEtablissement() : null;
						final SiScolBacOuxEqu bacOuEqu = bac != null ? bac.getSiScolBacOuxEqu() : null;
						final SiScolMentionNivBac bacMention = bac != null ? bac.getSiScolMentionNivBac() : null;
						final SiScolSpecialiteBac bacSpe1 = bac != null ? bac.getSiScolSpe1BacTer() : null;
						final SiScolSpecialiteBac bacSpe2 = bac != null ? bac.getSiScolSpe2BacTer() : null;
						final SiScolSpecialiteBac bacSpe3 = bac != null ? bac.getSiScolSpeBacPre() : null;
						final SiScolOptionBac bacOpt1 = bac != null ? bac.getSiScolOpt1Bac() : null;
						final SiScolOptionBac bacOpt2 = bac != null ? bac.getSiScolOpt2Bac() : null;
						final SiScolOptionBac bacOpt3 = bac != null ? bac.getSiScolOpt3Bac() : null;
						final SiScolOptionBac bacOpt4 = bac != null ? bac.getSiScolOpt4Bac() : null;

						listValeur.add(MethodUtils.formatIntToExport(bac != null ? bac.getAnneeObtBac() : null));
						listValeur.add(MethodUtils.formatToExport(bacPays != null ? bacPays.getLibPay() : null));
						listValeur.add(MethodUtils.formatToExport(bacDpt != null ? bacDpt.getLibDep() : null));
						listValeur.add(MethodUtils.formatToExport(bacComm != null ? bacComm.getLibCom() : null));
						listValeur.add(MethodUtils.formatToExport(bacEtab != null ? bacEtab.getLibEtb() : null));
						listValeur.add(MethodUtils.formatToExport(bacOuEqu != null ? bacOuEqu.getLibBac() : null));
						listValeur.add(MethodUtils.formatToExport(bacMention != null ? bacMention.getLibMnb() : null));
						listValeur.add(MethodUtils.formatToExport(bacSpe1 != null ? bacSpe1.getLibSpeBac() : null));
						listValeur.add(MethodUtils.formatToExport(bacSpe2 != null ? bacSpe2.getLibSpeBac() : null));
						listValeur.add(MethodUtils.formatToExport(bacSpe3 != null ? bacSpe3.getLibSpeBac() : null));
						listValeur.add(MethodUtils.formatToExport(bacOpt1 != null ? bacOpt1.getLibOptBac() : null));
						listValeur.add(MethodUtils.formatToExport(bacOpt2 != null ? bacOpt2.getLibOptBac() : null));
						listValeur.add(MethodUtils.formatToExport(bacOpt3 != null ? bacOpt3.getLibOptBac() : null));
						listValeur.add(MethodUtils.formatToExport(bacOpt4 != null ? bacOpt4.getLibOptBac() : null));
						break;
					case "adresseHide":
						listValeur.add(MethodUtils.formatToExport(adresse.getLibelle()));
						break;
					case "adresseDiviseHide":
						listValeur.add(MethodUtils.formatToExport(adresse.getAdr1()));
						listValeur.add(MethodUtils.formatToExport(adresse.getAdr2()));
						listValeur.add(MethodUtils.formatToExport(adresse.getAdr2()));
						listValeur.add(MethodUtils.formatToExport(adresse.getCodBdi()));
						listValeur.add(MethodUtils.formatToExport(adresse.getLibCommune()));
						listValeur.add(MethodUtils.formatToExport(adresse.getLibComEtr()));
						listValeur.add(MethodUtils.formatToExport(adresse.getLibPays()));
						break;
					case "etablissementHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getLastEtab()));
						break;
					case "lastDipHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getLastDiplome()));
						break;
					case "lastLibDipHide":
						listValeur.add(MethodUtils.formatToExport(candidat.getLastLibDiplome()));
						break;
					case "tagHide":
						listValeur.add(MethodUtils.formatToExport(formatLongCellSize(candidature.getTags().stream().map(e -> e.getLibTag()).collect(Collectors.joining(" / ")))));
						break;
					case "codFormHide":
						listValeur.add(MethodUtils.formatToExport(formation.getCodForm()));
						break;
					case "libFormHide":
						listValeur.add(MethodUtils.formatToExport(formation.getLibForm()));
						break;
					case "dateCandHide":
						listValeur.add(MethodUtils.formatToExport(candidature.getDatCreCand().format(formatterDateTime)));
						break;
					case "dateTransHide":
						listValeur.add(MethodUtils.formatToExport(MethodUtils.formatDate(candidature.getDatTransDossierCand(), formatterDateTime)));
						break;
					case "statutHide":
						listValeur.add(MethodUtils.formatToExport(candidature.getTypeStatut().getLibTypStatut()));
						break;
					case "dateModStatutHide":
						listValeur.add(MethodUtils.formatToExport(MethodUtils.formatDate(candidature.getDatModTypStatutCand(), formatterDateTime)));
						break;
					case "dateReceptHide":
						listValeur.add(MethodUtils.formatToExport(MethodUtils.formatDate(candidature.getDatReceptDossierCand(), formatterDate)));
						break;
					case "dateCompletHide":
						listValeur.add(MethodUtils.formatToExport(MethodUtils.formatDate(candidature.getDatCompletDossierCand(), formatterDate)));
						break;
					case "dateIncompletHide":
						listValeur.add(MethodUtils.formatToExport(MethodUtils.formatDate(candidature.getDatIncompletDossierCand(), formatterDate)));
						break;
					case "typeTraitHide":
						listValeur.add(MethodUtils.formatToExport(candidature.getTypeTraitement().getLibTypTrait()));
						break;
					case "typeTraitValidHide":
						listValeur.add(MethodUtils.formatBoolToExport(candidature.getTemValidTypTraitCand()));
						break;
					case "dateModPjHide":
						listValeur.add(MethodUtils.formatToExport(getDatModPjForm(candidature)));
						break;
					case "commissionHide":
						listValeur.add(MethodUtils.formatToExport(formation.getCommission().getLibComm()));
						break;
					case "avisCandHide":
						listValeur.add(MethodUtils.formatToExport(lastTypeDec != null ? lastTypeDec.getTypeDecision().getLibTypDec() : null));
						break;
					case "avisValidHide":
						listValeur.add(MethodUtils.formatToExport(lastTypeDec != null ? MethodUtils.formatBoolToExport(lastTypeDec.getTemValidTypeDecCand()) : ""));
						break;
					case "dateValidHide":
						listValeur.add(MethodUtils.formatToExport(lastTypeDec != null ? MethodUtils.formatDate(lastTypeDec.getDatValidTypeDecCand(), formatterDate) : null));
						break;
					case "motifHide":
						listValeur.add(MethodUtils.formatToExport((lastTypeDec != null && lastTypeDec.getMotivationAvis() != null) ? lastTypeDec.getMotivationAvis().getLibMotiv() : null));
						break;
					case "rangHide":
						listValeur.add(MethodUtils.formatIntToExport(lastTypeDec != null ? lastTypeDec.getListCompRangTypDecCand() : null));
						break;
					case "rangReelHide":
						listValeur.add(MethodUtils.formatIntToExport(lastTypeDec != null ? lastTypeDec.getListCompRangReelTypDecCand() : null));
						break;
					case "preselectionHide":
						listValeur.add(MethodUtils.formatToExport(lastTypeDec != null ? getComplementPreselectMail(lastTypeDec) : null));
						listValeur.add(MethodUtils.formatToExport(lastTypeDec != null ? MethodUtils.formatDate(lastTypeDec.getPreselectDateTypeDecCand(), formatterDate) : null));
						listValeur.add(MethodUtils.formatToExport(lastTypeDec != null ? MethodUtils.formatTime(lastTypeDec.getPreselectHeureTypeDecCand(), formatterTime) : null));
						listValeur.add(MethodUtils.formatToExport(lastTypeDec != null ? lastTypeDec.getPreselectLieuTypeDecCand() : null));
						break;
					case "commentaireHide":
						listValeur.add(MethodUtils.formatToExport(lastTypeDec != null ? lastTypeDec.getCommentTypeDecCand() : null));
						break;
					case "confirmHide":
						listValeur.add(MethodUtils.formatToExport(
							candidature.getTemAcceptCand() != null
								? (candidature.getTemAcceptCand() ? applicationContext.getMessage("export.option.confirm.confirm", null, UI.getCurrent().getLocale())
									: applicationContext.getMessage("export.option.confirm.desist", null, UI.getCurrent().getLocale()))
								: ""));
						break;
					case "datNewConfirmHide":
						listValeur.add(MethodUtils.formatToExport(MethodUtils.formatDate(candidature.getDatNewConfirmCand(), formatterDate)));
						break;
					case "datNewRetourHide":
						listValeur.add(MethodUtils.formatToExport(MethodUtils.formatDate(candidature.getDatNewRetourCand(), formatterDate)));
						break;
					case "regimeHide":
						listValeur.add(MethodUtils.formatToExport(candidature.getSiScolRegime() != null ? candidature.getSiScolRegime().getDisplayLibelle() : null));
						break;
					case "catExoHide":
						listValeur.add(MethodUtils.formatToExport(candidature.getSiScolCatExoExt() != null ? candidature.getSiScolCatExoExt().getDisplayLibelle() : null));
						break;
					case "compExoHide":
						listValeur.add(MethodUtils.formatToExport(candidature.getCompExoExtCand()));
						break;
					case "mntChargeHide":
						listValeur.add(MethodUtils.formatToExport(MethodUtils.parseBigDecimalAsString(candidature.getMntChargeCand())));
						break;
					case "datPassageOpiHide":
						listValeur.add(MethodUtils.formatToExport(candidature.getOpi() != null ? MethodUtils.formatDate(candidature.getOpi().getDatPassageOpi(), formatterDateTime) : null));
						break;
					case "codOpiHide":
						listValeur.add(MethodUtils.formatToExport(candidature.getOpi() != null ? candidature.getOpi().getCodOpi() : null));
						break;
					case "datAnnulHide":
						listValeur.add(MethodUtils.formatToExport(MethodUtils.formatDate(candidature.getDatAnnulCand(), formatterDateTime)));
						break;
					case "userAnnulHide":
						listValeur.add(MethodUtils.formatToExport(candidature.getUserAnnulCand()));
						break;
					case "questionReponseHide":
						listValeur.add(formatLongCellSize(candidature.getQuestionCands().stream().map(e -> e.getQuestion().getLibQuestion() + " : " + e.getReponseQuestionCand()).collect(Collectors.joining(" / "))));
						break;
					case "postItHide":
						listValeur.add(formatLongCellSize(getPostIt(candidature).stream().map(e -> e.getMessagePostIt()).collect(Collectors.joining(" / "))));
						break;
					default:
						listValeur.add("");
						break;
					}
				});
				listCandidature.add(listValeur);
			});

			/* Constituion du fichier */

			/* Objects exportés */
			final Map<String, Object> beans = new HashMap<>();
			beans.put("listEnTete", listEnTete);
			beans.put("listCandidature", listCandidature);
			/* Code à placer dans le nom du classeur */
			if (code != null) {
				beans.put("code", code);
			}

			/* Footer du fichier */
			if (temFooter) {
				beans.put("footer",
					applicationContext.getMessage("export.footer", new Object[] { libelle, liste.size(), formatterDateTime.format(LocalDateTime.now()) }, UI.getCurrent().getLocale()));
			} else {
				beans.put("footer", "");
			}

			/* Calcul du nom du fichier */
			final String libFile = applicationContext.getMessage("export.nom.fichier",
				new Object[] { libelle, DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now()) },
				UI.getCurrent().getLocale());

			return exportController.generateXlsxExport(beans, "candidatures_template", libFile, Arrays.asList(0));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException();
	}

	/**
	 * calcul la derniere modif de statut de PJ ou Formulaire
	 * @param  candidature
	 * @param  formatter
	 * @return             la date de derniere modif
	 */
	private String getDatModPjForm(final Candidature candidature) {
		LocalDateTime dateMod = null;
		final Optional<FormulaireCand> formOpt = candidature.getFormulaireCands()
			.stream()
			.filter(e -> e.getDatModFormulaireCand() != null)
			.sorted((e1, e2) -> (e2.getDatModFormulaireCand().compareTo(e1.getDatModFormulaireCand())))
			.findFirst();
		final Optional<FormulaireCandidat> formCandidatOpt = candidature.getCandidat()
			.getFormulaireCandidats()
			.stream()
			.filter(e -> e.getDatReponseFormulaireCandidat() != null)
			.sorted((e1, e2) -> (e2.getDatReponseFormulaireCandidat().compareTo(e1.getDatReponseFormulaireCandidat())))
			.findFirst();
		final Optional<PjCand> pjOpt = candidature.getPjCands()
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
			final FormulaireCandidat form = formCandidatOpt.get();
			if (dateMod == null) {
				dateMod = form.getDatReponseFormulaireCandidat();
			} else {
				dateMod = (form.getDatReponseFormulaireCandidat().isAfter(dateMod)) ? form.getDatReponseFormulaireCandidat() : dateMod;
			}
		}

		/* on compare avec la derniere réponse des PJ */
		if (pjOpt.isPresent()) {
			final PjCand pj = pjOpt.get();
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
		final ExportListCandidatureAdresse adresseBean = new ExportListCandidatureAdresse();
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
	 * Ouvre le dossier d'un candidat
	 * @param candidature
	 */
	public void openCandidat(final Candidature candidature) {
		final CompteMinima cpt = candidature.getCandidat().getCompteMinima();
		userController.setNoDossierNomCandidat(cpt);
		MainUI.getCurrent().buildMenuGestCand(false);
	}
}
