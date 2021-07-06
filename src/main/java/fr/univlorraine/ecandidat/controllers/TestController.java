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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 */
@Component
public class TestController {
	private final Logger logger = LoggerFactory.getLogger(TestController.class);

	@Value("${enableTestMode:}")
	private transient Boolean enableTestMode;

	public Boolean isTestMode() {
		if (enableTestMode == null) {
			return false;
		}
		return enableTestMode;
	}

	public void testMethode() {
		logger.debug("EnableTestMode : " + enableTestMode);
	}

}
