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

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Batch;
import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.BatchRun;
import fr.univlorraine.ecandidat.repositories.BatchHistoRepository;
import fr.univlorraine.ecandidat.repositories.BatchRepository;
import fr.univlorraine.ecandidat.repositories.BatchRunRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.AdminBatchHistoWindow;
import fr.univlorraine.ecandidat.views.windows.AdminBatchWindow;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;

/**
 * Gestion des batchs
 *
 * @author Kevin Hergalant
 */
@Component
public class BatchController {

	private Logger logger = LoggerFactory.getLogger(BatchController.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	@Resource
	private transient LockController lockController;

	@Resource
	private transient ParametreController parametreController;

	@Resource
	private transient CandidatController candidatController;

	@Resource
	private transient BatchRunRepository batchRunRepository;

	@Resource
	private transient BatchRepository batchRepository;

	@Resource
	private transient BatchHistoRepository batchHistoRepository;

	@Resource
	private transient SiScolController siScolController;

	@Resource
	private transient CandidatureGestionController candidatureGestionController;

	@Resource
	private transient DemoController demoController;

	@Resource
	private transient CampagneController campagneController;

	@Resource
	private transient FormulaireController formulaireController;

	@Resource
	private transient LoadBalancingController loadBalancingController;

	@Resource
	private transient DateTimeFormatter formatterDateTime;

	@Value("${batch.fixedRate}")
	private transient String batchFixedRate;

	/** @return liste des batchs */
	public List<Batch> getBatchs() {
		List<Batch> liste = batchRepository.findAll();
		liste.forEach(batch -> batch.setLastBatchHisto(getLastBatchHisto(batch)));
		return liste;
	}

	/** @return les informations de run des batchs */
	public String getInfoRun() {
		/* On cherche le dernier lancement */
		BatchRun run = batchRunRepository.findFirst1By();

		/* Calcul du batchFixedRate */
		Integer batchFixedRateInt = MethodUtils.getStringMillisecondeToInt(batchFixedRate);
		String batchFixedRateLabel = MethodUtils.getIntMillisecondeToString(batchFixedRateInt);

		/* Le dernier lancement */
		String datLastCheckRunLabel;
		if (run == null) {
			datLastCheckRunLabel = "-";
		} else {
			datLastCheckRunLabel = formatterDateTime.format(run.getDatLastCheckRun());
		}

		/* Le prochain lancement */
		String datNextCheckRunLabel;
		if (run == null) {
			datNextCheckRunLabel = "-";
		} else {
			datNextCheckRunLabel = formatterDateTime.format(run.getDatLastCheckRun().plus(new Long(batchFixedRateInt), ChronoUnit.MILLIS));
		}

		return applicationContext.getMessage("batch.info.run", new Object[] {batchFixedRateLabel, datLastCheckRunLabel, datNextCheckRunLabel}, UI.getCurrent().getLocale());
	}

	/**
	 * Renvoie la deniere execution
	 *
	 * @param batch
	 * @return la derniere execution de batch
	 */
	public BatchHisto getLastBatchHisto(final Batch batch) {
		return batchHistoRepository.findFirst1ByBatchCodBatchOrderByIdBatchHistoDesc(batch.getCodBatch());
	}

	/** @return liste des batchs */
	public List<BatchHisto> getBatchHisto(final Batch batch) {
		return batchHistoRepository.findFirst100ByBatchCodBatchOrderByIdBatchHistoDesc(batch.getCodBatch());
	}

	/**
	 * Ouvre une fenêtre d'historique du batch.
	 *
	 * @param batch
	 *            le batch
	 */
	public void showBatchHisto(final Batch batch) {
		Assert.notNull(batch, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		UI.getCurrent().addWindow(new AdminBatchHistoWindow(batch));
	}

	/**
	 * Ouvre une fenêtre d'édition de batch.
	 *
	 * @param batch
	 *            le batch a editer
	 */
	public void editBatch(final Batch batch) {
		Assert.notNull(batch, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(batch, null)) {
			return;
		}
		AdminBatchWindow bw = new AdminBatchWindow(batch);
		bw.addCloseListener(e -> lockController.releaseLock(batch));
		UI.getCurrent().addWindow(bw);
	}

	/**
	 * Enregistre un batch
	 *
	 * @param batch
	 *            le batch a enregistrer
	 */
	public void saveBatch(final Batch batch) {
		Assert.notNull(batch, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(batch, null)) {
			return;
		}

		batchRepository.saveAndFlush(batch);
		lockController.releaseLock(batch);
	}

	/**
	 * AJoute une descriptio nau batch
	 *
	 * @param batchHisto
	 * @param description
	 * @return l'historique enregistré
	 */
	public BatchHisto addDescription(final BatchHisto batchHisto, final String description) {
		if (description == null) {
			return batchHisto;
		}
		String descToInsert = "";
		if (batchHisto.getDetailBatchHisto() != null) {
			descToInsert = batchHisto.getDetailBatchHisto() + "<br>";
		}
		batchHisto.setDetailBatchHisto(descToInsert + formatterDateTime.format(LocalDateTime.now()) + " - " + description);
		logger.debug("Batch " + batchHisto.getBatch().getCodBatch() + " - " + description);
		return batchHistoRepository.save(batchHisto);
	}

	/**
	 * Lancement immediat du batch
	 *
	 * @param batch
	 */
	public void runImmediatly(final Batch batch) {
		ConfirmWindow win = new ConfirmWindow(applicationContext.getMessage("batch.immediat.ok", new Object[] {batch.getCodBatch()}, UI.getCurrent().getLocale()));
		win.addBtnOuiListener(e -> {
			BatchHisto histo = batchHistoRepository.findByBatchCodBatchAndStateBatchHisto(batch.getCodBatch(), ConstanteUtils.BATCH_RUNNING);
			if (histo == null) {
				batch.setTemIsLaunchImediaBatch(true);
				batchRepository.saveAndFlush(batch);
				Notification.show(applicationContext.getMessage("batch.immediat.launch", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			} else {
				Notification.show(applicationContext.getMessage("batch.immediat.nok", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
		});
		UI.getCurrent().addWindow(win);
	}

	/**
	 * Lancement immediat du batch
	 *
	 * @param batch
	 */
	public void cancelRunImmediatly(final Batch batch) {
		ConfirmWindow win = new ConfirmWindow(applicationContext.getMessage("batch.immediat.cancel", new Object[] {batch.getCodBatch()}, UI.getCurrent().getLocale()));
		win.addBtnOuiListener(e -> {
			BatchHisto histo = batchHistoRepository.findByBatchCodBatchAndStateBatchHisto(batch.getCodBatch(), ConstanteUtils.BATCH_RUNNING);
			if (histo == null) {
				batch.setTemIsLaunchImediaBatch(false);
				batchRepository.saveAndFlush(batch);
				Notification.show(applicationContext.getMessage("batch.immediat.cancel.ok", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			} else {
				Notification.show(applicationContext.getMessage("batch.immediat.cancel.nok", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
		});
		UI.getCurrent().addWindow(win);
	}

	/** Vérifie si un batch doit etre lancé depuis la dernière date de verification */
	@Scheduled(fixedDelayString = "${batch.fixedRate}")
	public void checkBatchRun() {
		if (!loadBalancingController.isLoadBalancingCandidatMode()) {
			List<BatchRun> liste = batchRunRepository.findAll();
			if (liste != null && liste.size() == 1) {
				BatchRun lastBatchRun = liste.get(0);
				List<Batch> listeBatch = batchRepository.findByTesBatch(true);
				logger.trace("Vérification lancement lastChek = " + lastBatchRun.getDatLastCheckRun() + " - Now = " + LocalDateTime.now());

				/* Suppression du dernier batch run */
				nettoyageBatchRun();
				listeBatch.forEach(batch -> {
					BatchHisto histo = batchHistoRepository.findByBatchCodBatchAndStateBatchHisto(batch.getCodBatch(), ConstanteUtils.BATCH_RUNNING);
					if (histo == null && isNeededToLaunch(batch, lastBatchRun.getDatLastCheckRun())) {
						logger.debug("Le batch " + batch.getCodBatch() + " doit être lancé");
						runJob(batch);
					}
				});
			} else {
				nettoyageBatchRun();
			}
		}
	}

	/** Nettoyage de la table BatchRun */
	private void nettoyageBatchRun() {
		batchRunRepository.deleteAll();
		batchRunRepository.saveAndFlush(new BatchRun(LocalDateTime.now()));
	}

	/**
	 * Vérifie si le batch doit être lancé grace a son schedul
	 *
	 * @param batch
	 * @param lastExec
	 * @return true si le batch doit etre lance ou non
	 */
	private Boolean isNeededToLaunch(final Batch batch, final LocalDateTime lastExec) {
		/* Vérification si le batch doit etre lancé immediatement */
		if (batch.getTemIsLaunchImediaBatch()) {
			batch.setTemIsLaunchImediaBatch(false);
			batchRepository.saveAndFlush(batch);
			return true;
		}
		LocalDateTime now = LocalDateTime.now();

		/* Vérification si le batch doit etre lancé à une date précise */
		if (batch.getFixeYearBatch() != null && batch.getFixeMonthBatch() != null && batch.getFixeDayBatch() != null) {
			if (now.getYear() != batch.getFixeYearBatch() || now.getMonth().getValue() != batch.getFixeMonthBatch()
					|| now.getDayOfMonth() != batch.getFixeDayBatch()) {
				return false;
			}
		}
		/*
		 * Vérification si le batch doit etre lancé annuelement avec un mois donné et un
		 * jour donné
		 */
		if (batch.getFixeMonthBatch() != null && batch.getFixeDayBatch() != null) {
			if (now.getMonth().getValue() != batch.getFixeMonthBatch()
					|| now.getDayOfMonth() != batch.getFixeDayBatch()) {
				return false;
			}
		}
		/* Vérification si le batch doit etre lancé mensuelement avec un jour donné */
		else if (batch.getFixeDayBatch() != null) {
			if (now.getDayOfMonth() != batch.getFixeDayBatch()) {
				return false;
			}
		}

		/*
		 * Sinon vérification si le batch doit etre lancé hebdo avec les jours précisés
		 */
		else {
			DayOfWeek today = now.getDayOfWeek();
			if (!batch.getTemLundiBatch() && today.getValue() == 1) {
				return false;
			} else if (!batch.getTemMardiBatch() && today.getValue() == 2) {
				return false;
			} else if (!batch.getTemMercrBatch() && today.getValue() == 3) {
				return false;
			} else if (!batch.getTemJeudiBatch() && today.getValue() == 4) {
				return false;
			} else if (!batch.getTemVendrediBatch() && today.getValue() == 5) {
				return false;
			} else if (!batch.getTemSamediBatch() && today.getValue() == 6) {
				return false;
			} else if (!batch.getTemDimanBatch() && today.getValue() == 7) {
				return false;
			}
		}

		logger.trace(batch.getCodBatch() + " - OK à lancer aujourd'hui");
		if (batch.getTemFrequenceBatch()) {
			Integer frequence = batch.getFrequenceBatch();
			if (frequence == null || frequence.compareTo(0) != 1) {
				return false;
			} else {
				LocalDateTime execBatch = batch.getLastDatExecutionBatch();
				if (execBatch == null) {
					return true;
				} else {
					execBatch = execBatch.plusMinutes(new Long(frequence));
					if (execBatch.isBefore(lastExec)) {
						logger.trace(batch.getCodBatch() + " - OK à lancer maintenant, frequence = " + frequence + ", lastExec = " + lastExec);
						return true;
					}
				}
			}
		} else {
			if ((batch.getFixeHourBatch().isAfter(lastExec.toLocalTime()))
					&& batch.getFixeHourBatch().isBefore(now.toLocalTime())) {
				logger.trace(batch.getCodBatch() + " - OK à lancer maintenant, heure lancement = " + batch.getFixeHourBatch() + ", lastExec = " + lastExec);
				return true;
			}
		}

		return false;
	}

	/**
	 * Lance un batch
	 *
	 * @param batch
	 */
	private void runJob(Batch batch) {
		logger.trace("Début du log");
		BatchHisto batchHisto = new BatchHisto();
		batchHisto.setDateDebBatchHisto(LocalDateTime.now());
		batchHisto.setBatch(batch);
		batchHisto.setStateBatchHisto(ConstanteUtils.BATCH_RUNNING);
		batchHisto = batchHistoRepository.saveAndFlush(batchHisto);
		batch.setLastDatExecutionBatch(LocalDateTime.now());
		batch = batchRepository.save(batch);
		try {
			if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_SI_SCOL)) {
				siScolController.syncSiScol(batchHisto);
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_NETTOYAGE)) {
				nettoyageBatch(4);
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_APP_EN_MAINT)) {
				parametreController.changeMaintenanceParam(true);
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_APP_EN_SERVICE)) {
				parametreController.changeMaintenanceParam(false);
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_NETTOYAGE_CPT)) {
				candidatController.nettoyageCptMinInvalides(batchHisto);
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_ARCHIVAGE)) {
				campagneController.archiveCampagne(batchHisto);
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_SYNCHRO_LIMESURVEY)) {
				formulaireController.launchBatchSyncLimeSurvey();
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_DESTRUCT_DOSSIER)) {
				candidatureGestionController.launchBatchDestructDossier(batchHisto);
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_ASYNC_OPI)) {
				candidatureGestionController.launchBatchAsyncOPI(batchHisto);
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_DESTRUCT_HISTO)) {
				cleanHistoBatch();
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_DEMO) && demoController.getDemoMode()) {
				demoController.launchDemoBatch();
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_ASYNC_OPI_PJ)) {
				candidatureGestionController.launchBatchAsyncOPIPj(batchHisto);
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_DESIST_AUTO)) {
				candidatureGestionController.desistAutoCandidature();
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_RELANCE_FAVO)) {
				candidatureGestionController.relanceFavorableNotConfirm(batchHisto);
			} else if (batch.getCodBatch().equals(NomenclatureUtils.BATCH_CALCUL_RANG_LC)) {
				candidatureGestionController.calculRangLcAllFormation(batchHisto);
			}

			batchHisto.setStateBatchHisto(ConstanteUtils.BATCH_FINISH);
		} catch (Exception e) {
			logger.error("Erreur de lancement du batch " + batch.getCodBatch(), e);
			batchHisto.setStateBatchHisto(ConstanteUtils.BATCH_ERROR);
		}
		batchHisto.setDateFinBatchHisto(LocalDateTime.now());
		batchHistoRepository.saveAndFlush(batchHisto);
		batch.setLastDatExecutionBatch(LocalDateTime.now());
		batch = batchRepository.save(batch);
		logger.trace("Fin du log");
	}

	/** Batch de nettoyage de l'historique des batchs */
	private void cleanHistoBatch() {
		List<BatchHisto> listHisto = batchHistoRepository.findByDateDebBatchHistoLessThan(LocalDateTime.now().minusDays(parametreController.getNbJourKeepHistoBatch()));
		if (listHisto != null && listHisto.size() > 0) {
			batchHistoRepository.deleteInBatch(listHisto);
		}
	}

	/** Batch de nettoyage des batchs */
	public void nettoyageBatch(final Integer plusHour) {
		List<BatchHisto> listHisto = batchHistoRepository.findByStateBatchHisto(ConstanteUtils.BATCH_RUNNING);
		listHisto.forEach(batchHisto -> {
			if (plusHour.equals(0)) {
				interruptBatch(batchHisto);
			} else {
				LocalDateTime histPlusHour = batchHisto.getDateDebBatchHisto().plusHours(new Long(plusHour));
				if (histPlusHour.isBefore(LocalDateTime.now())) {
					interruptBatch(batchHisto);
				}
			}
		});
	}

	/**
	 * Interrompt un batch
	 *
	 * @param batchHisto
	 */
	private void interruptBatch(final BatchHisto batchHisto) {
		batchHisto.setStateBatchHisto(ConstanteUtils.BATCH_INTERRUPT);
		batchHisto.setDateFinBatchHisto(LocalDateTime.now());
		batchHistoRepository.saveAndFlush(batchHisto);
	}
}
