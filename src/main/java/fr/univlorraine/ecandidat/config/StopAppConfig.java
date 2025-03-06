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
package fr.univlorraine.ecandidat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.controllers.LockCandidatController;
import jakarta.annotation.Resource;

/**
 * Configuration du lancement de l'appli
 * @author Kevin Hergalant
 */
@Component
public class StopAppConfig implements ApplicationListener<ContextStoppedEvent> {

	private final Logger logger = LoggerFactory.getLogger(StopAppConfig.class);

	@Resource
	private transient LockCandidatController lockCandidatController;

	@Override
	public void onApplicationEvent(final ContextStoppedEvent event) {
		preprocessCleanLock();
	}

	/** Au stop de l'appli, on supprime tout les locks */
	private void preprocessCleanLock() {
		logger.info("Nettoyage des locks");
		lockCandidatController.cleanAllLockCandidatForInstance();
	}
}
