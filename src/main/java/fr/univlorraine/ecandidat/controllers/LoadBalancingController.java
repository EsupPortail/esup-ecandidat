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
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.LoadBalancingReload;
import fr.univlorraine.ecandidat.entities.ecandidat.LoadBalancingReloadRun;
import fr.univlorraine.ecandidat.repositories.LoadBalancingReloadRepository;
import fr.univlorraine.ecandidat.repositories.LoadBalancingReloadRunRepository;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 */
@Component
public class LoadBalancingController {

	/* Injections */
	private final Logger logger = LoggerFactory.getLogger(LoadBalancingController.class);

	@Resource
	private transient CacheController cacheController;

	@Resource
	@Lazy
	private transient LockCandidatController lockCandidatController;

	@Resource
	private transient LoadBalancingReloadRepository loadBalancingReloadRepository;

	@Resource
	private transient LoadBalancingReloadRunRepository loadBalancingReloadRunRepository;

	@Value("${load.balancing.gestionnaire.mode:}")
	private transient Boolean loadBalancingGestionnaireMode;

	@Value("${load.balancing.candidat.url:}")
	private transient String loadBalancingCandidatUrl;

	@Value("${load.balancing.candidat.id.instance:}")
	private transient String loadBalancingIdInstance;

	@Value("${app.url}")
	private transient String appUrl;

	/* LoadBalancing */

	public Boolean isLoadBalancingGestionnaireMode() {
		if (loadBalancingGestionnaireMode != null && loadBalancingGestionnaireMode) {
			logger.trace("GestionnaireMode : true");
			return true;
		}
		logger.trace("GestionnaireMode : false");
		return false;
	}

	public Boolean isLoadBalancingCandidatMode() {
		if (loadBalancingGestionnaireMode != null && !loadBalancingGestionnaireMode) {
			logger.trace("CandidateMode : true");
			return true;
		}
		logger.trace("CandidateMode : false");
		return false;
	}

	/**
	 * @return l'id d'instance de l'application
	 */
	String getIdInstance() {
		if (loadBalancingIdInstance != null && !loadBalancingIdInstance.equals("")) {
			return loadBalancingIdInstance;
		}
		return "1";
	}

	/**
	 * @return l'url de l'application (ajoute un / a la fin)
	 */
	public String getApplicationPath(final Boolean addSlash) {
		if (addSlash) {
			return MethodUtils.formatUrlApplication(appUrl);
		}
		return appUrl;
	}

	/**
	 * @return l'url de l'application candidat pour le loadbalancing (ajoute un / a la fin)
	 */
	public String getApplicationPathForCandidat() {
		if (isLoadBalancingGestionnaireMode() && loadBalancingCandidatUrl != null) {
			return MethodUtils.formatUrlApplication(loadBalancingCandidatUrl);
		} else {
			return getApplicationPath(true);
		}
	}

	/**
	 * Vérifie si on doit recharger les caches depuis la dernière date de verification
	 * Sert aussi de hearthbeat d'instance
	 */
	@Scheduled(fixedRate = 2 * 60 * 1000)
	@Async
	public void checkBatchLBRun() {
		/* Permet de recharger les caches des instances candidat */
		if (isLoadBalancingCandidatMode()) {
			final String instance = getIdInstance();
			final LoadBalancingReloadRun loadBalancingReloadRun = loadBalancingReloadRunRepository.findOne(instance);
			if (loadBalancingReloadRun != null) {
				final LocalDateTime lastCheck = loadBalancingReloadRun.getDatLastCheckLbReloadRun();
				logger.trace("Vérification des données pour l'instance " + instance + " avant la " + lastCheck);
				final List<LoadBalancingReload> listeToReload = loadBalancingReloadRepository.findByDatCreLbReloadAfterOrDatCreLbReload(lastCheck, lastCheck);
				listeToReload.forEach(e -> {
					final String code = e.getCodDataLbReload();
					logger.trace("Rechargement des données pour l'instance " + instance + " : code=" + code);
					cacheController.reloadData(code, false);
					logger.trace("Fin rechargement des données pour l'instance " + instance + " : code=" + code);
				});
				loadBalancingReloadRunRepository.delete(loadBalancingReloadRun);
			}
			loadBalancingReloadRunRepository.saveAndFlush(new LoadBalancingReloadRun(instance));
		}
		/* Permet d'ajouter un hearthbeat pour n'importe quelle instance */
		if (isLoadBalancingGestionnaireMode()) {
			final String instance = getIdInstance();
			final LoadBalancingReloadRun loadBalancingReloadRun = loadBalancingReloadRunRepository.findOne(instance);
			if (loadBalancingReloadRun != null) {
				loadBalancingReloadRunRepository.delete(loadBalancingReloadRun);
			}
			loadBalancingReloadRunRepository.saveAndFlush(new LoadBalancingReloadRun(instance));
		}
	}

	/**
	 * Supprime les locks par instance si celle ci ne tourne plus
	 */
	@Scheduled(fixedRate = 30 * 60 * 1000)
	@Async
	public void hearthbeatInstanceCleanLock() {
		if (isLoadBalancingGestionnaireMode()) {
			final LocalDateTime dtSeuil = LocalDateTime.now().minusMinutes(30);
			/* On recherche toutes les instances qui n'ont plus donné signe de vie depuis 30min */
			final List<LoadBalancingReloadRun> listLbReload = loadBalancingReloadRunRepository.findByDatLastCheckLbReloadRunBefore(dtSeuil);
			listLbReload.forEach(e -> {
				lockCandidatController.cleanAllLockCandidatForInstance(e.getInstanceIdLbReloadRun());
				loadBalancingReloadRunRepository.delete(e);
			});
		}
	}

	/**
	 * Recharge toutes les données en cache au départ de l'appli
	 */
	public void reloadAllData() {
		final String instance = getIdInstance();
		cacheController.loadAllCaches();
		if (isLoadBalancingCandidatMode()) {
			final LoadBalancingReloadRun loadBalancingReloadRun = loadBalancingReloadRunRepository.findOne(instance);
			if (loadBalancingReloadRun != null) {
				loadBalancingReloadRunRepository.delete(loadBalancingReloadRun);
			}
			loadBalancingReloadRunRepository.saveAndFlush(new LoadBalancingReloadRun(instance));
		}
	}

	/**
	 * Demande aux autres instances de recharger la data
	 * @param code
	 * @param needToPushToCandidat
	 */
	public void askToReloadData(final String code, final Boolean needToPushToCandidat) {
		if (needToPushToCandidat && isLoadBalancingGestionnaireMode()) {
			final LocalDateTime now = LocalDateTime.now();
			LoadBalancingReload loadBalancingReload = loadBalancingReloadRepository.findOne(code);
			if (loadBalancingReload != null) {
				loadBalancingReload.setDatCreLbReload(now);
			} else {
				loadBalancingReload = new LoadBalancingReload(code, now);
			}
			loadBalancingReloadRepository.save(loadBalancingReload);
		}
	}
}
