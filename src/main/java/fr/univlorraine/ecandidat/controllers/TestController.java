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
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
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

	public Boolean isTestMode() {
		if (enableTestMode == null) {
			return false;
		}
		return enableTestMode;
	}

	public void testMethode() {
		logger.debug("EnableTestMode : " + enableTestMode);
		logger.debug("Début des tests");
		try {
			//siScolController.syncCommuneNaiss();
			for (Integer i = 1; i < 100; i++) {
				String cpt = String.valueOf(i);
				if (cpt.length() == 1) {
					cpt = "0" + cpt;
				}

				final WSIndividu ind = siScolService.getIndividu("0000000" + cpt, null, null);
				if (ind != null && !"100".equals(ind.getCodPayNai()) && ind.getCodCommNai() != null) {
					System.out.println("XXXXXXXXXXX " + ind);
				}
			}

		} catch (final SiScolException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		logger.debug("Fin des tests");
	}

}
