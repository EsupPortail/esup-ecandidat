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

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.OpiRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 */
@Component
public class TestController {
	private final Logger logger = LoggerFactory.getLogger(TestController.class);

	@Value("${enableTestMode:}")
	private transient Boolean enableTestMode;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	@Resource
	private transient SiScolController siScolController;

	@Resource
	private transient CandidatureRepository candidatureRepository;

	@Resource
	private transient MessageController messageController;

	@Resource
	private transient CacheController cacheController;

	@Resource
	private transient ApplicationContext applicationContext;

	@Resource
	private transient ReloadableResourceBundleMessageSource messageSource;

	@Resource
	private transient OpiRepository opiRepository;

	public Boolean isTestMode() {
		if (enableTestMode == null) {
			return false;
		}
		return enableTestMode;
	}

	public void testMethode() {
		logger.debug("EnableTestMode : " + enableTestMode);
		logger.debug("Début des tests");

		//messageSource.clearCache();

		//cacheController.invalidConfCache(true);

		//messageSource.clearCacheIncludingAncestors();
//		try {
//			logger.info("********** Test OPI **********");
//			final Candidature candidature = candidatureRepository.findOne(721565);
//
//			siScolService.testOpiViaWS(candidature.getCandidat(), Arrays.asList(candidature));
//
//		} catch (final SiScolException ex) {
//			// TODO Auto-generated catch block
//			ex.printStackTrace();
//		}
		//logger.info("********** Test OPI **********");
//		final Candidature candidature = candidatureRepository.findOne(721565);
//
//		siScolService.testOpiViaWS(candidature.getCandidat(), Arrays.asList(candidature));
//		try {
//			siScolService.getListSiScolAnneeUni().forEach(e -> {
//				System.out.println(e);
//			});
//		} catch (final SiScolException ex) {
//			// TODO Auto-generated catch block
//			ex.printStackTrace();
//		}
	}

}
