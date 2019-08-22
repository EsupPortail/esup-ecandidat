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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.entities.ecandidat.PjOpi;
import fr.univlorraine.ecandidat.entities.ecandidat.PjOpiPK;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.repositories.CandidatRepository;
import fr.univlorraine.ecandidat.repositories.FichierRepository;
import fr.univlorraine.ecandidat.repositories.OpiRepository;
import fr.univlorraine.ecandidat.repositories.PjOpiRepository;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.mail.CandidatMailBean;
import fr.univlorraine.ecandidat.utils.bean.mail.ChangeCodOpiMailBean;

/**
 * Gestion des batchs
 * @author Kevin Hergalant
 */
@Component
public class OpiController {

	private final Logger logger = LoggerFactory.getLogger(OpiController.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient BatchController batchController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient PieceJustifController pieceJustifController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient MailController mailController;
	@Resource
	private transient DemoController demoController;
	@Resource
	private transient OpiRepository opiRepository;
	@Resource
	private transient PjOpiRepository pjOpiRepository;
	@Resource
	private transient CandidatRepository candidatRepository;
	@Resource
	private transient FichierRepository fichierRepository;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/**
	 * Genere un opi si besoin
	 * @param candidature
	 * @param confirm
	 */
	public void generateOpi(final Candidature candidature, final Boolean confirm) {
		logger.debug("Generation de l'opi");
		if (candidature == null) {
			return;
		}
		final TypeDecisionCandidature lastTypeDecision = candidatureController.getLastTypeDecisionCandidature(candidature);
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

	/**
	 * Envoi un mail de changement de code OPI
	 * @param candidat
	 * @param newCode
	 * @param libFormationImpactee
	 */
	public void sendMailChangeCodeOpi(final Candidat candidat, final String newCode, final String libFormationImpactee) {
		final String locale = candidat.getLangue().getCodLangue();
		final CandidatMailBean candidatMailBean = mailController.getCandidatMailBean(candidat, locale);
		final ChangeCodOpiMailBean mailBean = new ChangeCodOpiMailBean(newCode, libFormationImpactee, candidatMailBean);
		mailController.sendMailByCod(candidat.getCompteMinima()
			.getMailPersoCptMin(), NomenclatureUtils.MAIL_CANDIDATURE_MODIF_COD_OPI, mailBean, null, candidat.getLangue().getCodLangue());
	}

	/**
	 * @param  candidat
	 * @return          la liste des opi d'un candidat
	 */
	public List<Opi> getListOpiByCandidat(final Candidat candidat, final Boolean isBatch) {
		/* Si OPI immediate :
		 * On prend :
		 * OPI sans OPIPJ
		 * OPI avec une date de déversement deja passée
		 * OPI desisté
		 * Si BATCH, on prend tous les OPI */
		final List<Opi> listOpi = opiRepository.findByCandidatureCandidatIdCandidat(candidat.getIdCandidat());
		if (isBatch) {
			return listOpi;
		}

		/* Mode synchrone */
		final List<Opi> listOpiToRet = new ArrayList<>();
		listOpi.forEach(opi -> {
			/* OPI avec avec une date de déversement deja passée
			 * OPI sans OPIPJ
			 * OPI desisté */
			if (opi.getDatPassageOpi() != null || (opi.getCandidature().getTemAcceptCand() != null && !opi.getCandidature().getTemAcceptCand())
				|| getPJToDeverse(opi.getCandidature()).size() == 0) {
				listOpiToRet.add(opi);
			}
		});
		return listOpiToRet;
	}

	/**
	 * Traite la liste des OPI
	 * @param candidat
	 * @param listeOpi
	 * @param isCodOpiIntEpoFromEcandidat
	 * @param codOpiIntEpo
	 */
	public void traiteListOpiCandidat(final Candidat candidat,
		final List<Opi> listeOpi,
		final Boolean isCodOpiIntEpoFromEcandidat,
		final String codOpiIntEpo,
		final String logComp) {
		logger.debug("traiteListOpiCandidat " + codOpiIntEpo + " fromEcv2 = " + isCodOpiIntEpoFromEcandidat + logComp + " - " + listeOpi.size() + " opi");
		String libFormation = "";
		for (final Opi opi : listeOpi) {
			/* On enregistre la date de passage */
			opi.setDatPassageOpi(LocalDateTime.now());
			opi.setCodOpi(codOpiIntEpo);
			opiRepository.save(opi);
			if (!isCodOpiIntEpoFromEcandidat) {
				libFormation = libFormation + "<li>" + opi.getCandidature().getFormation().getLibForm() + "</li>";
			}
		}
		/* Si le code OPI est different de celui de eCandidat, on envoi un mail au
		 * candidat */
		if (!isCodOpiIntEpoFromEcandidat && libFormation != null && !libFormation.equals("")) {
			logger.debug("Envoi du mail de modification" + logComp);
			sendMailChangeCodeOpi(candidat, codOpiIntEpo, "<ul>" + libFormation + "</ul>");
		}
	}

	/**
	 * Traite la liste des OPI desistement
	 * @param candidat
	 * @param listeOpiDesistementATraiter
	 * @param logComp
	 */
	public void traiteListOpiDesistCandidat(final Candidat candidat, final List<Opi> listeOpiDesistementATraiter, final String logComp) {
		logger.debug("traiteListOpiDesistCandidat " + logComp + " - " + listeOpiDesistementATraiter.size() + " opi");
		for (final Opi opi : listeOpiDesistementATraiter) {
			/* On enregistre la date de passage */
			opi.setDatPassageOpi(LocalDateTime.now());
			opiRepository.save(opi);
		}
	}

	/**
	 * Lance le batch de creation d'OPI asynchrone
	 * @param batchHisto
	 */
	public void launchBatchAsyncOPI(final BatchHisto batchHisto) {
		final Campagne campagne = campagneController.getCampagneActive();
		if (campagne == null) {
			return;
		}
		Integer nbOpi = parametreController.getNbOpiBatch();
		if (nbOpi == null || nbOpi.equals(0)) {
			nbOpi = Integer.MAX_VALUE;
		}
		final List<Candidat> listeCandidat = candidatRepository.findOpi(campagne.getIdCamp(), new PageRequest(0, nbOpi));
		batchController.addDescription(batchHisto, "Lancement batch, deversement de " + listeCandidat.size() + " OPI");
		Integer i = 0;
		Integer cpt = 0;
		for (final Candidat e : listeCandidat) {
			siScolService.creerOpiViaWS(e, true);
			i++;
			cpt++;
			if (i.equals(ConstanteUtils.BATCH_LOG_NB_SHORT)) {
				batchController.addDescription(batchHisto, "Deversement de " + cpt + " OPI");
				i = 0;
			}
		}
		batchController.addDescription(batchHisto, "Fin batch, deversement de " + cpt + " OPI");
	}

	/**
	 * Traite les PJ
	 * @param listeOpi
	 * @param codOpiIntEpo
	 * @param logComp
	 * @param isBatch
	 */
	public void
		traiteListOpiPjCandidat(final List<Opi> listeOpi, final String codOpiIntEpo, final long codIndOpi, final String logComp, final Boolean isBatch) {
		logger.debug("traiteListOpiPjCandidat " + codOpiIntEpo + logComp + " - " + listeOpi.size() + " opi");
		for (final Opi opi : listeOpi) {
			/* Traitement des PJ OPI si dématerialisation */
			if (candidatureController.isCandidatureDematerialise(opi.getCandidature()) && parametreController.getIsUtiliseOpiPJ()) {
				logger.debug("Deversement PJ OPI dans table eCandidat" + logComp);
				deversePjOpi(opi, codOpiIntEpo, codIndOpi);
			}
		}
		if (isBatch) {
			/* Deversement dans Apogée */
			final List<PjOpi> listePjOpi = pjOpiRepository.findByIdCodOpiAndDatDeversementIsNull(codOpiIntEpo);
			logger.debug("Tentative deversement PJ OPI WS Apogée " + codOpiIntEpo + logComp + " - " + listePjOpi.size() + " pjOPI");
			listePjOpi.forEach(pjOpi -> {
				try {
					deversePjOpi(pjOpi);
				} catch (final SiScolException e) {
					// si erreur on ne log rien, on est dans le batch OPI
				}
			});
		}
	}

	/**
	 * Deverse les PJ dans la table des PJ OPI
	 * @param opi
	 * @param codOpiIntEpo
	 * @param codIndOpi
	 */
	public void deversePjOpi(final Opi opi, final String codOpiIntEpo, final Long codIndOpi) {
		if (opi == null || opi.getDatPassageOpi() == null || opi.getCodOpi() == null) {
			return;
		}
		final Candidature candidature = opi.getCandidature();
		final List<PjCand> listPjOpiToDeverse = getPJToDeverse(candidature);
		logger.debug("deversement PJ OPI dans eCandidat " + codOpiIntEpo + " Nombre de PJ : " + listPjOpiToDeverse.size());
		listPjOpiToDeverse.forEach(pjCand -> {
			logger.debug("deversement PJ OPI dans eCandidat " + codOpiIntEpo + " PJ : " + pjCand.getPieceJustif());
			/* On créé la clé primaire */
			final PjOpiPK pk = new PjOpiPK(codOpiIntEpo, pjCand.getPieceJustif().getCodApoPj());

			/* On charge une eventuelle piece */
			PjOpi pjOpi = pjOpiRepository.findOne(pk);

			/* Dans le cas ou il y a deja une PJ Opi */
			if (pjOpi != null) {
				/* on va vérifier que la pièce n'a pas été déversée et que le fichier existe
				 * encore */
				if (pjOpi.getDatDeversement() == null && fichierRepository.findOne(pjOpi.getIdFichier()) == null) {
					// dans ce cas, on supprime
					pjOpiRepository.delete(pjOpi);
					pjOpi = null;
				}
			}

			/* On l'insert */
			if (pjOpi == null) {
				pjOpi = new PjOpi();
				pjOpi.setId(pk);
				pjOpi.setCandidat(candidature.getCandidat());
				if (codIndOpi != null) {
					try {
						pjOpi.setCodIndOpi(String.valueOf(codIndOpi));
					} catch (final Exception e) {
					}
				}
				pjOpi.setDatDeversement(null);
				pjOpi.setIdFichier(pjCand.getFichier().getIdFichier());
				pjOpi = pjOpiRepository.save(pjOpi);
				logger.debug("Ajout PJ OPI dans eCandidat " + pjOpi);
			}
		});
	}

	/**
	 * @param  candidature
	 * @return             la liste des PJ à déverser
	 */
	public List<PjCand> getPJToDeverse(final Candidature candidature) {
		final List<PjCand> listPjCand = new ArrayList<>();
		if (!candidatureController.isCandidatureDematerialise(candidature) || !parametreController.getIsUtiliseOpiPJ()) {
			return listPjCand;
		}
		final List<PieceJustif> listPiece = pieceJustifController.getPjForCandidature(candidature, false);
		listPiece.stream().filter(e -> e.getCodApoPj() != null).forEach(pj -> {
			final PjCand pjCand = candidaturePieceController.getPjCandFromList(pj, candidature, true);
			if (pjCand != null && pjCand.getFichier() != null
				&& pjCand.getTypeStatutPiece() != null
				&& pjCand.getTypeStatutPiece().equals(tableRefController.getTypeStatutPieceValide())) {
				listPjCand.add(pjCand);
			}
		});
		return listPjCand;
	}

	/**
	 * Lance le batch de creation de PJ OPI asynchrone
	 * @param batchHisto
	 */
	public void launchBatchAsyncOPIPj(final BatchHisto batchHisto) {
		final Campagne campagne = campagneController.getCampagneActive();
		if (campagne == null) {
			return;
		}
		final List<PjOpi> listePjOpi = pjOpiRepository.findByCandidatCompteMinimaCampagneIdCampAndDatDeversementIsNull(campagne.getIdCamp());
		batchController.addDescription(batchHisto, "Lancement batch, deversement de " + listePjOpi.size() + " PJOPI");
		Integer i = 0;
		Integer cpt = 0;
		Integer nbError = 0;
		for (final PjOpi pjOpi : listePjOpi) {
			try {
				deversePjOpi(pjOpi);
			} catch (final SiScolException e) {
				nbError++;
			}
			i++;
			cpt++;
			if (i.equals(ConstanteUtils.BATCH_LOG_NB_SHORT)) {
				batchController.addDescription(batchHisto, "Deversement de " + cpt + " PJOPI, dont " + nbError + " erreur(s)");
				i = 0;
			}
		}
		batchController.addDescription(batchHisto, "Fin batch, deversement de " + cpt + " PJOPI, dont " + nbError + " erreur(s)");
	}

	/**
	 * Deverse une Opi PJ
	 * @param  pjOpi
	 * @throws SiScolException
	 */
	public void deversePjOpi(final PjOpi pjOpi) throws SiScolException {
		/* On nettoie les PjOPI dont le fichier n'existe plus */
		final Fichier file = fichierRepository.findOne(pjOpi.getIdFichier());
		if (file == null) {
			pjOpiRepository.delete(pjOpi);
			return;
		}
		/* On récupere le fichier, si celui-ci n'existe plus, on efface, les autres exceptions, on ignore */
		final InputStream is = fileController.getInputStreamFromFichier(file, false);
		try {
			if (is == null && !fileController.existFile(file)) {
				pjOpiRepository.delete(pjOpi);
				return;
			}
		} catch (final FileException e) {
		}

		if (is != null) {
			try {
				// lancement WS
				siScolService.creerOpiPjViaWS(pjOpi, file, is);

				// Verification si la PJ est présente sur le serveur
				final String complementLog = " - Parametres : codOpi=" + pjOpi.getId()
					.getCodOpi() + ", codApoPj=" + pjOpi.getId().getCodApoPj() + ", idCandidat=" + pjOpi.getCandidat().getIdCandidat();
				final String suffixeLog = "Vérification OPI_PJ : ";
				try {
					final Boolean isFileCandidatureOpiExist = fileController.isFileCandidatureOpiExist(pjOpi, file, complementLog);
					if (isFileCandidatureOpiExist == null) {
						logger.debug(suffixeLog + "Pas de verification" + complementLog);
					} else if (!isFileCandidatureOpiExist) {
						logger.info(suffixeLog + "La pièce n'existe pas sur le serveur" + complementLog);
						deleteOpiPJApo(pjOpi, suffixeLog, complementLog);
						return;
					} else {
						logger.debug(suffixeLog + "OK" + complementLog);
					}
				} catch (final FileException e) {
					deleteOpiPJApo(pjOpi, suffixeLog, complementLog);
					logger.info(suffixeLog + "Impossible de vérifier si la pièce existe sur le serveur" + complementLog, e);
					return;
				}

				// si tout se passe bien, on enregistre la date du deversement
				pjOpi.setDatDeversement(LocalDateTime.now());
				pjOpiRepository.save(pjOpi);
			} catch (final SiScolException e) {
				logger.error(e.getMessage(), e);
				throw e;
			} finally {
				MethodUtils.closeRessource(is);
			}
		}
	}

	/**
	 * Supprime la ligne OPI_PJ correspondante
	 * @param pjOpi
	 * @param suffixeLog
	 * @param complementLog
	 */
	private void deleteOpiPJApo(final PjOpi pjOpi, final String suffixeLog, final String complementLog) {
		try {
			siScolService.deleteOpiPJ(pjOpi.getCodIndOpi(), pjOpi.getId().getCodApoPj());
		} catch (final SiScolException e) {
		}
	}
}
