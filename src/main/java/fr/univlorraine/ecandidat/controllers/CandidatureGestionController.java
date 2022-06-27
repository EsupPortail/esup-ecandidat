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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat_;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision_;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.CompteMinimaRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionCandidatureRepository;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.PdfAttachement;

/**
 * Traitement des candidatures (opi, etc..)
 * @author Kevin Hergalant
 */
@Component
public class CandidatureGestionController {
	private final Logger logger = LoggerFactory.getLogger(CandidatureGestionController.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient CandidatureCtrCandController ctrCandCandidatureController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient BatchController batchController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient CandidatureRepository candidatureRepository;
	@Resource
	private transient CompteMinimaRepository compteMinimaRepository;
	@Resource
	private transient TypeDecisionCandidatureRepository typeDecisionCandidatureRepository;

	@Resource
	private transient DateTimeFormatter formatterDate;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/* Utilisé pour le batch de destruction, si trop volumineux, on supprime à la main les fichiers
	 * Plus aucun contrôle n'est fait sur la suppression des fichiers */
	@Value("${enableDeleteFileManuallyBatchDestruct:}")
	private transient Boolean enableDeleteFileManuallyBatchDestruct;

	/* Utilisé pour le batch de destruction, permet de supprimer le folder root */
	@Value("${enableDeleteRootFolderManuallyBatchDestruct:}")
	private transient Boolean enableDeleteRootFolderManuallyBatchDestruct;

	/**
	 * @param  formation
	 * @param  campagne
	 * @return           la liste des type decision LC classé par rang puis par Id
	 */
	public List<TypeDecisionCandidature> findTypDecLc(final Formation formation, final Campagne campagne) {
		final Specification<TypeDecisionCandidature> spec = new Specification<TypeDecisionCandidature>() {

			@Override
			public Predicate toPredicate(final Root<TypeDecisionCandidature> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {

				/* Creation de la subquery pour récupérer le max des id de type decision pour chaque candidature de la formation */
				final Subquery<Integer> subquery = query.subquery(Integer.class);
				final Root<TypeDecisionCandidature> rootSq = subquery.from(TypeDecisionCandidature.class);

				/* On fait la jointure sur la candidature */
				final Join<TypeDecisionCandidature, Candidature> joinCandSq = rootSq.join(TypeDecisionCandidature_.candidature);
				/* Select du max */
				subquery.select(cb.max(rootSq.get(TypeDecisionCandidature_.idTypeDecCand)));
				/* Group by sur idCand */
				subquery.groupBy(rootSq.get(TypeDecisionCandidature_.candidature).get(Candidature_.idCand));

				/* Ajout des clauses where : formation, campagne et datAnnul null */
				final Predicate predicateFormation = cb.equal(joinCandSq.get(Candidature_.formation), formation);
				final Predicate predicateCampagne =
					cb.equal(joinCandSq.join(Candidature_.candidat).join(Candidat_.compteMinima).get(CompteMinima_.campagne), campagne);
				final Predicate predicateDtAnnul = cb.isNull(joinCandSq.get(Candidature_.datAnnulCand));

				/* Finalisation de la subquery */
				subquery.where(predicateFormation, predicateCampagne, predicateDtAnnul);

				/* Selection des typeDecisionCandidature, creation des clauses where :
				 * L'avis doit etre validé, avec un rang non null, un avis LC et contenu dans la subquery */
				final Predicate predicateValid = cb.equal(root.get(TypeDecisionCandidature_.temValidTypeDecCand), true);
				final Predicate predicateRang = cb.isNotNull(root.get(TypeDecisionCandidature_.listCompRangTypDecCand));
				final Predicate predicateAvis =
					cb.equal(root.join(TypeDecisionCandidature_.typeDecision).get(TypeDecision_.typeAvis), tableRefController.getTypeAvisListComp());
				final Predicate predicateSqMaxIds = cb.in(root.get(TypeDecisionCandidature_.idTypeDecCand)).value(subquery);

				/* Recherche avec ces clauses */
				return cb.and(predicateValid, predicateRang, predicateAvis, predicateSqMaxIds);
			}
		};

		/* On sort sur le rang en premier puis si même rang, sur l'id --> l'id le plus bas sera classé premier */
		final Order orderRang = new Order(Direction.ASC, TypeDecisionCandidature_.listCompRangTypDecCand.getName());
		final Order orderId = new Order(Direction.ASC, TypeDecisionCandidature_.idTypeDecCand.getName());

		return typeDecisionCandidatureRepository.findAll(spec, new Sort(orderRang, orderId));
	}

	/**
	 * @param batchHisto
	 */
	public void calculRangLcAllFormation(final BatchHisto batchHisto) {
		final Campagne camp = campagneController.getCampagneActive();
		if (camp == null) {
			return;
		}
		final List<Formation> listForm = formationRepository.findByTesFormAndTemListCompForm(true, true);
		batchController.addDescription(batchHisto, "Lancement batch de recalcul des rangs LC pour " + listForm.size() + " formations");
		final int[] cpt = { 0 };
		listForm.forEach(formation -> {
			final List<TypeDecisionCandidature> listeTdc = findTypDecLc(formation, camp);
			final int size = listeTdc.size();
			cpt[0] = cpt[0] + size;
			batchController.addDescription(batchHisto, "Batch calcul rang : formation '" + formation.getCodForm() + "', " + size + " décisions à recalculer");
			calculRangReel(listeTdc);
		});
		batchController.addDescription(batchHisto, "Fin batch de recalcul des rangs LC, recalcul terminé pour " + cpt[0] + " décisions");
	}

	/**
	 * Recalcul le pour une liste de formation
	 * @param  liste
	 *                   de formation
	 * @return       la liste des TypeDecisionCandidature
	 */
	public List<TypeDecisionCandidature> calculRangReelListForm(final List<Formation> liste) {
		final Campagne camp = campagneController.getCampagneActive();
		final List<TypeDecisionCandidature> listeTypDecRangReel = new ArrayList<>();
		if (camp == null) {
			return listeTypDecRangReel;
		}
		for (final Formation formation : liste) {
			if (formation == null || !formation.getTemListCompForm()) {
				continue;
			}
			listeTypDecRangReel.addAll(calculRangReel(findTypDecLc(formation, camp)));
		}
		return listeTypDecRangReel;
	}

	/**
	 * Recalcul le rang reel des avis en LC
	 * @param  liste
	 * @return       la liste des TypeDecisionCandidature
	 */
	public List<TypeDecisionCandidature> calculRangReel(final List<TypeDecisionCandidature> liste) {
		final List<TypeDecisionCandidature> listeTypDecRangReel = new ArrayList<>();
		int i = 1;
		for (final TypeDecisionCandidature td : liste) {
			if (td.getListCompRangReelTypDecCand() == null || !td.getListCompRangReelTypDecCand().equals(i)) {
				td.setListCompRangReelTypDecCand(i);
				final TypeDecisionCandidature tdSave = typeDecisionCandidatureRepository.save(td);
				listeTypDecRangReel.add(tdSave);
				Candidature candidature = td.getCandidature();
				candidature.setUserModCand(ConstanteUtils.AUTO_LISTE_COMP);
				candidature.setDatModCand(LocalDateTime.now());
				candidature = candidatureRepository.save(candidature);
				logger.debug("Recalcul du rang pour " + td);
			}
			i++;
		}
		return listeTypDecRangReel;
	}

	/**
	 * Si un candidat rejette une candidature, le premier de la liste comp est pris
	 * @param formation
	 */
	public void candidatFirstCandidatureListComp(Formation formation) {
		formation = formationRepository.findOne(formation.getIdForm());
		final Campagne camp = campagneController.getCampagneActive();
		if (formation == null || !formation.getTemListCompForm() || formation.getTypeDecisionFavListComp() == null || camp == null) {
			return;
		}

		final List<TypeDecisionCandidature> listTypDecLc = findTypDecLc(formation, camp);
		final Optional<TypeDecisionCandidature> optTypDec = listTypDecLc.stream().findFirst();

		if (optTypDec.isPresent()) {
			final TypeDecisionCandidature td = optTypDec.get();
			Candidature candidature = td.getCandidature();

			logger.debug("Traitement liste comp. : " + candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin());
			ctrCandCandidatureController.saveTypeDecisionCandidature(candidature,
				formation.getTypeDecisionFavListComp(),
				true,
				ConstanteUtils.AUTO_LISTE_COMP,
				ConstanteUtils.TYP_DEC_CAND_ACTION_LC);
			// on la recharge
			candidature = candidatureController.loadCandidature(candidature.getIdCand());

			/* On affecte une nouvelle date de confirmation si besoin */
			final LocalDate newDateConfirm = candidatureController.getDateConfirmCandidat(formation.getDatConfirmListCompForm(),
				formation.getDelaiConfirmListCompForm(),
				null,
				candidatureController.getLastTypeDecisionCandidature(candidature));
			if (newDateConfirm != null) {
				candidature.setDatNewConfirmCand(newDateConfirm);
			}

			candidature.setUserModCand(ConstanteUtils.AUTO_LISTE_COMP);
			candidature.setDatModCand(LocalDateTime.now());
			candidature.setTemAcceptCand(null);
			candidature = candidatureRepository.save(candidature);

			/* On recupere le dernier avis */
			candidature.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(candidature));

			PdfAttachement attachement = null;
			final InputStream is =
				candidatureController.downloadLettre(candidature, ConstanteUtils.TYP_LETTRE_MAIL, candidature.getCandidat().getLangue().getCodLangue(), false);
			if (is != null) {
				try {
					attachement = new PdfAttachement(is,
						candidatureController
							.getNomFichierLettre(candidature, ConstanteUtils.TYP_LETTRE_MAIL, candidature.getCandidat().getLangue().getCodLangue()));
				} catch (final Exception e) {
					attachement = null;
				}
			}
			mailController.sendMail(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),
				formation.getTypeDecisionFavListComp().getMail(),
				null,
				candidature,
				candidature.getCandidat().getLangue().getCodLangue(),
				attachement);
			/* envoi du mail à la commission */
			if (candidature.getFormation().getCommission().getTemAlertListePrincComm()) {
				mailController.sendMailByCod(candidature.getFormation().getCommission().getMailAlertComm(),
					NomenclatureUtils.MAIL_COMMISSION_ALERT_LISTE_PRINC,
					null,
					candidature,
					null);
			}

			/* On retire l'element ayant eu un avis favorable */
			listTypDecLc.remove(td);

			/* On recalcul les rang */
			if (parametreController.isCalculRangReelLc()) {
				calculRangReel(listTypDecLc);
			}
		}
	}

	/** Lance le batch de destruction des dossiers */
	public void launchBatchDestructDossier(final BatchHisto batchHisto) throws FileException {
		final Boolean deleteFileManualy = enableDeleteFileManuallyBatchDestruct != null && enableDeleteFileManuallyBatchDestruct;
		final Boolean deleteRootManualy = enableDeleteRootFolderManuallyBatchDestruct != null && enableDeleteRootFolderManuallyBatchDestruct;
		final List<Campagne> listeCamp = campagneController.getCampagnes()
			.stream()
			.filter(e -> (e.getDatDestructEffecCamp() == null && e.getDatArchivCamp() != null))
			.collect(Collectors.toList());
		batchController.addDescription(batchHisto, "Lancement batch de destruction");
		batchController.addDescription(batchHisto, "Batch de destruction, option enableDeleteFileManuallyBatchDestruct=" + deleteFileManualy);
		batchController.addDescription(batchHisto, "Batch de destruction, option enableDeleteRootFolderManuallyBatchDestruct=" + deleteRootManualy);
		for (final Campagne campagne : listeCamp) {
			if (campagneController.getDateDestructionDossier(campagne).isBefore(LocalDateTime.now())) {
				batchController.addDescription(batchHisto,
					"Batch de destruction, destruction dossiers campagne : " + campagne.getCodCamp()
						+ " - "
						+ campagne.getCompteMinimas().size()
						+ " comptes à supprimer");
				Integer i = 0;
				Integer cpt = 0;
				for (final CompteMinima cptMin : campagne.getCompteMinimas()) {
					if (cptMin.getCandidat() != null) {
						for (final Candidature candidature : cptMin.getCandidat().getCandidatures()) {
							for (final PjCand pjCand : candidature.getPjCands()) {
								if (deleteFileManualy) {
									candidaturePieceController.removeFileToPjManually(pjCand);
								} else {
									candidaturePieceController.removeFileToPj(pjCand);
								}
							}
						}
					}
					compteMinimaRepository.delete(cptMin);
					i++;
					cpt++;
					if (i.equals(ConstanteUtils.BATCH_LOG_NB_LONG)) {
						batchController.addDescription(batchHisto, "Batch de destruction, destruction de " + cpt + " comptes ok");
						i = 0;
					}
				}
				/* Lancement du batch de fiabilisation des fichiers */
				fileController.launchFiabilisationFichier(campagne.getDatArchivCamp());

				/* Destruction du dossier de la campagne et les sous-repertoire */
				if (!deleteRootManualy) {
					batchController.addDescription(batchHisto, "Batch de destruction, destruction dossier root campagne : " + campagne.getCodCamp());
					fileController.deleteCampagneFolder(campagne.getCodCamp());
				}
				campagneController.saveDateDestructionCampagne(campagne);

				/* Enregistre la date de suppression */
				batchController.addDescription(batchHisto,
					"Batch de destruction, fin destruction campagne : " + campagne.getCodCamp() + ", " + cpt + " comptes supprimés");
			}
			batchController.addDescription(batchHisto, "Fin batch de destruction");
		}
	}

	/**
	 * @param  campagne
	 * @param  isCandidatureRelance
	 * @return                      la liste des type decision favorable non confirmée
	 */
	public List<TypeDecisionCandidature> findTypDecFavoNotAccept(final Campagne campagne, final Boolean isCandidatureRelance) {
		final Specification<TypeDecisionCandidature> spec = new Specification<TypeDecisionCandidature>() {

			@Override
			public Predicate toPredicate(final Root<TypeDecisionCandidature> root, final CriteriaQuery<?> query, final CriteriaBuilder cb) {

				/* Creation de la subquery pour récupérer le max des id de type decision pour chaque candidature de la formation */
				final Subquery<Integer> subquery = query.subquery(Integer.class);
				final Root<TypeDecisionCandidature> rootSq = subquery.from(TypeDecisionCandidature.class);

				/* On fait la jointure sur la candidature */
				final Join<TypeDecisionCandidature, Candidature> joinCandSq = rootSq.join(TypeDecisionCandidature_.candidature);
				/* Select du max */
				subquery.select(cb.max(rootSq.get(TypeDecisionCandidature_.idTypeDecCand)));
				/* Group by sur idCand */
				subquery.groupBy(rootSq.get(TypeDecisionCandidature_.candidature).get(Candidature_.idCand));

				/* Ajout des clauses where : campagne , datAnnul null, temAccept null */
				final Predicate predicateCampagne = cb.equal(joinCandSq.get(Candidature_.candidat).get(Candidat_.compteMinima).get(CompteMinima_.campagne), campagne);
				final Predicate predicateDtAnnul = cb.isNull(joinCandSq.get(Candidature_.datAnnulCand));
				final Predicate predicateNotAccept = cb.isNull(joinCandSq.get(Candidature_.temAcceptCand));
				/* Si isCandidatureRelance à false, on ne prend pas les relancés, sinon on prend tout le monde */
				if (!isCandidatureRelance) {
					final Predicate predicateIsNotRelance = cb.equal(joinCandSq.get(Candidature_.temRelanceCand), false);
					/* Finalisation de la subquery */
					subquery.where(predicateCampagne, predicateDtAnnul, predicateNotAccept, predicateIsNotRelance);
				} else {
					/* Finalisation de la subquery */
					subquery.where(predicateCampagne, predicateDtAnnul, predicateNotAccept);
				}

				/* Selection des typeDecisionCandidature, creation des clauses where :
				 * L'avis doit etre validé, un avis Favo et contenu dans la subquery */
				final Predicate predicateValid = cb.equal(root.get(TypeDecisionCandidature_.temValidTypeDecCand), true);
				final Predicate predicateAvis =
					cb.equal(root.get(TypeDecisionCandidature_.typeDecision).get(TypeDecision_.typeAvis), tableRefController.getTypeAvisFavorable());
				final Predicate predicateSqMaxIds = cb.in(root.get(TypeDecisionCandidature_.idTypeDecCand)).value(subquery);

				/* Recherche avec ces clauses */
				return cb.and(predicateValid, predicateAvis, predicateSqMaxIds);
			}
		};

		return typeDecisionCandidatureRepository.findAll(spec);
	}

	/**
	 * Batch de desistement automatique des candidatures ayant dépassé la date de confirmation
	 * @param batchHisto
	 */
	public void desistAutoCandidature(final BatchHisto batchHisto) {
		logger.debug("Lancement batch BATCH_DESIST_AUTO");
		final Campagne campagne = campagneController.getCampagneActive();
		if (campagne == null) {
			return;
		}
		/* Recuperation des candidatures a traiter */
		// List<TypeDecisionCandidature> listeTyDec = typeDecisionCandidatureRepository.findListFavoNotConfirmToDesist(campagne, true, tableRefController.getTypeAvisFavorable());
		final List<TypeDecisionCandidature> listeTyDec = findTypDecFavoNotAccept(campagne, true);

		batchController.addDescription(batchHisto, "Lancement batch BATCH_DESIST_AUTO, " + listeTyDec.size() + " candidatures à analyser");
		Integer i = 0;
		Integer cpt = 0;
		for (final TypeDecisionCandidature td : listeTyDec) {
			/* Rechargement de la candidature (suite bug?) */
			final Candidature candidature = candidatureController.loadCandidature(td.getCandidature().getIdCand());
			if (candidature == null) {
				logger.debug("Candidature inconnue IdCand = " + td.getCandidature().getIdCand());
				continue;
			}
			final LocalDate dateConfirm = candidatureController.getDateConfirmCandidat(candidature);
			/* Vérification date non null et date avant aujourd'hui */
			if (dateConfirm == null || dateConfirm.equals(LocalDate.now()) || dateConfirm.isAfter(LocalDate.now())) {
				logger.debug("Date de confirmation null ou future pour candidature IdCand = " + td.getCandidature().getIdCand() + ", dateConfirm=" + dateConfirm);
				continue;
			}
			/* Vérification que le candidat n'a pas déjà confirmé */
			if (candidature.getTemAcceptCand() != null) {
				logger.warn("Desistement annulé car le candidat a déjà confirmé sa candidature = " + candidature);
				continue;
			}

			logger.debug("Desistement de la candidature = " + candidature);

			candidature.setTemAcceptCand(false);
			candidature.setDatAcceptCand(LocalDateTime.now());
			candidature.setUserAcceptCand(ConstanteUtils.AUTO_DESIST);
			candidatureRepository.save(candidature);
			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),
				NomenclatureUtils.MAIL_CANDIDATURE_DESIST_AUTO,
				null,
				candidature,
				candidature.getCandidat().getLangue().getCodLangue());
			candidatFirstCandidatureListComp(candidature.getFormation());
			i++;
			cpt++;
			if (i.equals(ConstanteUtils.BATCH_LOG_NB_SHORT)) {
				batchController.addDescription(batchHisto, "Batch de destruction, desistement de " + cpt + " candidatures ok");
				i = 0;
			}
		}
		batchController.addDescription(batchHisto, "Fin batch BATCH_DESIST_AUTO : " + cpt + " candidatures désistées automatiquement");
	}

	/**
	 * @param batchHisto
	 */
	public void relanceFavorableNotConfirm(final BatchHisto batchHisto) {
		final Campagne campagne = campagneController.getCampagneActive();
		if (campagne == null) {
			return;
		}
		final LocalDate dateConfirmCalc = LocalDate.now().plusDays(parametreController.getNbJourRelanceFavo());
		if (dateConfirmCalc == null) {
			return;
		}
		batchController.addDescription(batchHisto, "Lancement batch BATCH_RELANCE_FAVO");
		/* On recherche toutes les candidatures qui ont :
		 * - Le bon codeCamp
		 * - La date de confirmation est non null et égale à la date calculée
		 * - n'ont jamais été relancées
		 * - leur dernier avis est non null
		 * - leur dernier avis est validé
		 * - leur dernier avis est favorable **/
		// List<TypeDecisionCandidature> listeTyDec = typeDecisionCandidatureRepository.findListFavoNotConfirmToRelance(campagne, true, false, tableRefController.getTypeAvisFavorable());
		final List<TypeDecisionCandidature> listeTyDec = findTypDecFavoNotAccept(campagne, false);
		batchController.addDescription(batchHisto, "Batch de relance, chargement des décisions, ok");
		batchController.addDescription(batchHisto, "Batch de relance, " + listeTyDec.size() + " candidatures à analyser");
		Integer i = 0;
		Integer cpt = 0;
		for (final TypeDecisionCandidature td : listeTyDec) {
			final Candidature candidature = td.getCandidature();
			final LocalDate dateConfirmCandidat = candidatureController.getDateConfirmCandidat(candidature);

			/* Vérification date non null et date égale à date calculée */
			if (dateConfirmCandidat == null || !dateConfirmCandidat.equals(dateConfirmCalc)) {
				continue;
			}
			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(),
				NomenclatureUtils.MAIL_CANDIDATURE_RELANCE_FAVO,
				null,
				candidature,
				candidature.getCandidat().getLangue().getCodLangue());
			candidature.setTemRelanceCand(true);
			candidatureRepository.save(candidature);
			i++;
			cpt++;
			if (i.equals(ConstanteUtils.BATCH_LOG_NB_SHORT)) {
				batchController.addDescription(batchHisto, "Batch de relance, relance de " + cpt + " candidatures ok");
				i = 0;
			}
		}
		batchController.addDescription(batchHisto, "Fin batch BATCH_RELANCE_FAVO : " + cpt + " candidatures relancées automatiquement");
	}

}
