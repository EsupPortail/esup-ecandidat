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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.PjOpi;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.repositories.CandidatRepository;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.CompteMinimaRepository;
import fr.univlorraine.ecandidat.repositories.FichierRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.repositories.OpiRepository;
import fr.univlorraine.ecandidat.repositories.PjOpiRepository;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.PdfAttachement;

/** Traitement des candidatures (opi, etc..)
 *
 * @author Kevin Hergalant */
@Component
public class CandidatureGestionController {
	private Logger logger = LoggerFactory.getLogger(CandidatureGestionController.class);

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
	private transient DemoController demoController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient CandidatureRepository candidatureRepository;
	@Resource
	private transient CandidatRepository candidatRepository;
	@Resource
	private transient OpiRepository opiRepository;
	@Resource
	private transient PjOpiRepository pjOpiRepository;
	@Resource
	private transient FichierRepository fichierRepository;
	@Resource
	private transient CompteMinimaRepository compteMinimaRepository;

	@Resource
	private transient DateTimeFormatter formatterDate;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/*
	 * Utilisé pour le batch de destruction, si trop volumineux, on supprime à la main les fichiers
	 * Plus aucun contrôle n'est fait sur la suppression des fichiers
	 */
	@Value("${enableDeleteFileManuallyBatchDestruct:}")
	private transient Boolean enableDeleteFileManuallyBatchDestruct;

	/*
	 * Utilisé pour le batch de destruction, permet de supprimer le folder root
	 */
	@Value("${enableDeleteRootFolderManuallyBatchDestruct:}")
	private transient Boolean enableDeleteRootFolderManuallyBatchDestruct;

	private static final Integer NB_OPI_LOG = 300;
	private static final Integer NB_OPIPJ_LOG = 300;
	private static final Integer NB_DELETE_COMPTE_LOG = 500;

	/** Genere un opi si besoin
	 *
	 * @param candidature
	 * @param confirm
	 */
	public void generateOpi(final Candidature candidature, final Boolean confirm) {
		logger.debug("Generation de l'opi");
		if (candidature == null) {
			return;
		}
		TypeDecisionCandidature lastTypeDecision = candidatureController.getLastTypeDecisionCandidature(candidature);
		if (parametreController.getIsUtiliseOpi() && lastTypeDecision.getTypeDecision().getTemDeverseOpiTypDec() && !demoController.getDemoMode()) {
			Opi opi = opiRepository.findOne(candidature.getIdCand());
			// cas de la confirmation
			if (opi == null && confirm) {
				opi = opiRepository.save(new Opi(candidature));
				candidature.setOpi(opi);
				if (parametreController.getIsOpiImmediat()) {
					logger.debug("Lancement OPI immédiat après confirmation");
					siScolService.creerOpiViaWS(candidature.getCandidat(), false);
				}
			} else if (opi != null && !confirm) {
				if (opi.getDatPassageOpi() == null) {
					opiRepository.delete(opi);
				} else {
					opi.setDatPassageOpi(null);
					opiRepository.save(opi);
					candidature.setOpi(opi);
					if (parametreController.getIsOpiImmediat()) {
						logger.debug("Lancement OPI immédiat après desistement");
						siScolService.creerOpiViaWS(candidature.getCandidat(), false);
					}
				}
			}
		}
	}

	/** Si un candidat rejette une candidature, le premier de la liste comp est pris
	 *
	 * @param formation
	 */
	public void candidatFirstCandidatureListComp(Formation formation) {
		formation = formationRepository.findOne(formation.getIdForm());
		Campagne camp = campagneController.getCampagneActive();
		if (formation == null || !formation.getTemListCompForm() || formation.getTypeDecisionFavListComp() == null || camp == null) {
			return;
		}

		// recherche des candidatures de la campagne en cours
		List<Candidature> listeCand = candidatureRepository.findByFormationIdFormAndCandidatCompteMinimaCampagneCodCampAndDatAnnulCandIsNull(formation.getIdForm(), camp.getCodCamp());

		// mise a jour des avis
		listeCand.stream().forEach(e -> e.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(e)));

		// recuperation des liste comp avec le plus petit rang
		Optional<Candidature> optCand = listeCand.stream().filter(e -> e.getLastTypeDecision() != null && e.getLastTypeDecision().getTemValidTypeDecCand()
				&& e.getLastTypeDecision().getTypeDecision().getTypeAvis().equals(tableRefController.getTypeAvisListComp()) && e.getLastTypeDecision().getListCompRangTypDecCand() != null).sorted((e1,
						e2) -> (e1.getLastTypeDecision().getListCompRangTypDecCand().compareTo(e2.getLastTypeDecision().getListCompRangTypDecCand())))
				.findFirst();
		if (optCand.isPresent()) {
			Candidature candidature = optCand.get();
			logger.debug("Traitement liste comp. : " + candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin());
			ctrCandCandidatureController.saveTypeDecisionCandidature(optCand.get(), formation.getTypeDecisionFavListComp(), true, ConstanteUtils.AUTO_LISTE_COMP);
			// on la recharge
			candidature = candidatureController.loadCandidature(candidature.getIdCand());

			/* On affecte une nouvelle date de confirmation si besoin */
			if (formation.getDatConfirmForm() != null && formation.getDatConfirmListCompForm() != null
					&& (formation.getDatConfirmListCompForm().isAfter(formation.getDatConfirmForm()) || formation.getDatConfirmListCompForm().isEqual(formation.getDatConfirmForm()))) {
				candidature.setDatNewConfirmCand(formation.getDatConfirmListCompForm());
			}
			candidature.setUserModCand(ConstanteUtils.AUTO_LISTE_COMP);
			candidature.setDatModCand(LocalDateTime.now());
			candidature.setTemAcceptCand(null);
			candidature = candidatureRepository.save(candidature);

			/* On recupere le dernier avis */
			candidature.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(candidature));

			PdfAttachement attachement = null;
			if (ConstanteUtils.ADD_LETTRE_TO_MAIL) {
				InputStream is = candidatureController.downloadLettre(candidature, ConstanteUtils.TYP_LETTRE_MAIL, candidature.getCandidat().getLangue().getCodLangue(), false);
				if (is != null) {
					try {
						attachement =
								new PdfAttachement(is, candidatureController.getNomFichierLettre(candidature, ConstanteUtils.TYP_LETTRE_MAIL, candidature.getCandidat().getLangue().getCodLangue()));
					} catch (Exception e) {
						attachement = null;
					}
				}
			}
			mailController.sendMail(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(), formation.getTypeDecisionFavListComp().getMail(), null, candidature,
					candidature.getCandidat().getLangue().getCodLangue(), attachement);
		}
	}

	/** Lance le batch de destruction des dossiers */
	public void launchBatchDestructDossier(final BatchHisto batchHisto) throws FileException {
		Boolean deleteFileManualy = enableDeleteFileManuallyBatchDestruct != null && enableDeleteFileManuallyBatchDestruct;
		Boolean deleteRootManualy = enableDeleteRootFolderManuallyBatchDestruct != null && enableDeleteRootFolderManuallyBatchDestruct;
		List<Campagne> listeCamp = campagneController.getCampagnes().stream().filter(e -> (e.getDatDestructEffecCamp() == null && e.getDatArchivCamp() != null)).collect(Collectors.toList());
		batchController.addDescription(batchHisto, "Lancement batch de destruction");
		batchController.addDescription(batchHisto, "Batch de destruction, option enableDeleteFileManuallyBatchDestruct=" + deleteFileManualy);
		batchController.addDescription(batchHisto, "Batch de destruction, option enableDeleteRootFolderManuallyBatchDestruct=" + deleteRootManualy);
		for (Campagne campagne : listeCamp) {
			if (campagneController.getDateDestructionDossier(campagne).isBefore(LocalDateTime.now())) {
				batchController.addDescription(batchHisto, "Batch de destruction, destruction dossiers campagne : " + campagne.getCodCamp() + " - " + campagne.getCompteMinimas().size()
						+ " comptes à supprimer");
				Integer i = 0;
				Integer cpt = 0;
				for (CompteMinima cptMin : campagne.getCompteMinimas()) {
					if (cptMin.getCandidat() != null) {
						for (Candidature candidature : cptMin.getCandidat().getCandidatures()) {
							for (PjCand pjCand : candidature.getPjCands()) {
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
					if (i.equals(NB_DELETE_COMPTE_LOG)) {
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

				/* Enregistre la date de suppression */
				campagneController.saveDateDestructionCampagne(campagne);
				batchController.addDescription(batchHisto, "Batch de destruction, fin destruction campagne : " + campagne.getCodCamp() + ", " + cpt + " comptes supprimés");
			}
			batchController.addDescription(batchHisto, "Fin batch de destruction");
		}
	}

	/** Lance le batch de creation d'OPI asynchrone
	 *
	 * @param batchHisto
	 */
	public void launchBatchAsyncOPI(final BatchHisto batchHisto) {
		Campagne campagne = campagneController.getCampagneActive();
		if (campagne == null) {
			return;
		}
		Integer nbOpi = parametreController.getNbOpiBatch();
		if (nbOpi == null || nbOpi.equals(0)) {
			nbOpi = Integer.MAX_VALUE;
		}
		List<Candidat> listeCandidat = candidatRepository.findOpi(campagne.getIdCamp(), new PageRequest(0, nbOpi));
		batchController.addDescription(batchHisto, "Lancement batch, deversement de " + listeCandidat.size() + " OPI");
		Integer i = 0;
		Integer cpt = 0;
		for (Candidat e : listeCandidat) {
			siScolService.creerOpiViaWS(e, true);
			i++;
			cpt++;
			if (i.equals(NB_OPI_LOG)) {
				batchController.addDescription(batchHisto, "Deversement de " + cpt + " OPI");
				i = 0;
			}
		}
		batchController.addDescription(batchHisto, "Fin batch, deversement de " + cpt + " OPI");
	}

	/** Lance le batch de creation de PJ OPI asynchrone
	 *
	 * @param batchHisto
	 */
	public void launchBatchAsyncOPIPj(final BatchHisto batchHisto) {
		Campagne campagne = campagneController.getCampagneActive();
		if (campagne == null) {
			return;
		}
		List<PjOpi> listePjOpi = pjOpiRepository.findByCandidatCompteMinimaCampagneIdCampAndDatDeversementIsNull(campagne.getIdCamp());
		batchController.addDescription(batchHisto, "Lancement batch, deversement de " + listePjOpi.size() + " PJOPI");
		Integer i = 0;
		Integer cpt = 0;
		for (PjOpi pjOpi : listePjOpi) {
			deversePjOpi(pjOpi);
			i++;
			cpt++;
			if (i.equals(NB_OPIPJ_LOG)) {
				batchController.addDescription(batchHisto, "Deversement de " + cpt + " PJOPI");
				i = 0;
			}
		}
		batchController.addDescription(batchHisto, "Fin batch, deversement de " + cpt + " PJOPI");
	}

	/** Deverse une Opi PJ
	 *
	 * @param pjOpi
	 */
	public void deversePjOpi(final PjOpi pjOpi) {
		/* On nettoie les PjOPI dont le fichier n'existe plus */
		Fichier file = fichierRepository.findOne(pjOpi.getIdFichier());
		if (file == null) {
			pjOpiRepository.delete(pjOpi);
			return;
		}
		/* On récupere le fichier, si celui-ci n'existe plus, on efface, les autres exceptions, on ignore */
		InputStream is = fileController.getInputStreamFromFichier(file, false);
		try {
			if (is == null && !fileController.existFile(file)) {
				pjOpiRepository.delete(pjOpi);
				return;
			}
		} catch (FileException e) {
		}

		if (is != null) {
			try {
				// lancement WS
				siScolService.creerOpiPjViaWS(pjOpi, file, is);

				// Verification si la PJ est présente sur le serveur
				String complementLog = " - Parametres : codOpi=" + pjOpi.getId().getCodOpi() + ", codApoPj=" + pjOpi.getId().getCodApoPj() + ", idCandidat="
						+ pjOpi.getCandidat().getIdCandidat();
				String suffixeLog = "Vérification OPI_PJ : ";
				try {
					Boolean isFileCandidatureOpiExist = fileController.isFileCandidatureOpiExist(pjOpi, file, complementLog);
					if (isFileCandidatureOpiExist == null) {
						logger.debug(suffixeLog + "Pas de verification" + complementLog);
					} else if (!isFileCandidatureOpiExist) {
						logger.info(suffixeLog + "La pièce n'existe pas sur le serveur" + complementLog);
						deleteOpiPJApo(pjOpi, suffixeLog, complementLog);
						return;
					} else {
						logger.debug(suffixeLog + "OK" + complementLog);
					}
				} catch (FileException e) {
					deleteOpiPJApo(pjOpi, suffixeLog, complementLog);
					logger.info(suffixeLog + "Impossible de vérifier si la pièce existe sur le serveur" + complementLog, e);
					return;
				}

				// si tout se passe bien, on enregistre la date du deversement
				pjOpi.setDatDeversement(LocalDateTime.now());
				pjOpiRepository.save(pjOpi);
			} catch (SiScolException e) {
				logger.error(e.getMessage(), e);
			} finally {
				MethodUtils.closeRessource(is);
			}
		}
	}

	/** Supprime la ligne OPI_PJ correspondante
	 *
	 * @param pjOpi
	 * @param suffixeLog
	 * @param complementLog
	 */
	private void deleteOpiPJApo(final PjOpi pjOpi, final String suffixeLog, final String complementLog) {
		try {
			siScolService.deleteOpiPJ(pjOpi.getCodIndOpi(), pjOpi.getId().getCodApoPj());
		} catch (SiScolException e) {
		}
	}

	/** Batch de desistement automatique des candidatures ayant dépassé la date de confirmation */
	public void desistAutoCandidature() {
		logger.debug("Lancement batch BATCH_DESIST_AUTO");
		Campagne campagne = campagneController.getCampagneActive();
		if (campagne == null) {
			return;
		}
		/* Recuperation des candidature a traiter */
		List<Candidature> liste =
				candidatureRepository.findByCandidatCompteMinimaCampagneCodCampAndTemAcceptCandIsNullAndDatAnnulCandIsNullAndFormationDatConfirmFormIsNotNullAndFormationDatConfirmFormBefore(
						campagne.getCodCamp(), LocalDate.now());
		logger.debug("Batch BATCH_DESIST_AUTO : " + liste.size() + " candidatures à analyser");
		logger.debug("Batch BATCH_DESIST_AUTO : Mise à jour des avis");
		/* mise a jour des avis->Sinon les auto list comp recoivent un avis favorable */
		liste.stream().forEach(e -> e.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(e)));
		logger.debug("Batch BATCH_DESIST_AUTO : Fin de mise à jour des avis, lancement du traitement");

		Integer i = 0;
		for (Candidature candidature : liste) {
			LocalDate dateConfirm = candidatureController.getDateConfirmCandidat(candidature);
			TypeDecisionCandidature decision = candidature.getLastTypeDecision();
			if (dateConfirm == null || dateConfirm.equals(LocalDate.now()) || dateConfirm.isAfter(LocalDate.now()) || decision == null
					|| !decision.getTypeDecision().getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_FAV) || !decision.getTemValidTypeDecCand()) {
				continue;
			}
			candidature.setTemAcceptCand(false);
			candidature.setDatAcceptCand(LocalDateTime.now());
			candidature.setUserAcceptCand(ConstanteUtils.AUTO_DESIST);
			candidatureRepository.save(candidature);
			mailController.sendMailByCod(candidature.getCandidat().getCompteMinima().getMailPersoCptMin(), NomenclatureUtils.MAIL_CANDIDATURE_DESIST, null, candidature,
					candidature.getCandidat().getLangue().getCodLangue());
			candidatFirstCandidatureListComp(candidature.getFormation());
			i++;
		}
		logger.debug("Fin batch BATCH_DESIST_AUTO : " + i + " candidatures désistées automatiquement");
	}
}
