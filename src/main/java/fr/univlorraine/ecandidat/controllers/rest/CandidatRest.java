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
package fr.univlorraine.ecandidat.controllers.rest;

import java.util.Base64;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.LoadBalancingController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.CandidatCompteMinimaView;

/**
 * Contrôleur REST pour la gestion de l'entité candidat
 */
@Controller
@RequestMapping("/candidat")
public class CandidatRest {

	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient UserController userController;

	/**
	 * valide le compte
	 */
	@RequestMapping(value = "/dossier/{validKeyCptMin}", method = RequestMethod.GET)
	public String valideDossier(@PathVariable final String validKeyCptMin) {
		String mode = "";
		String paramReinitPwd = "";
		final CompteMinima cptMin = candidatController.searchCptMinByValidKeyCptMin(validKeyCptMin);
		if (cptMin != null) {
			if (cptMin.getTemValidCptMin()) {
				mode = ConstanteUtils.REST_VALID_ALREADY_VALID;
			} else {
				cptMin.setTemValidCptMin(true);
				cptMin.setTemValidMailCptMin(true);
				candidatController.simpleSaveCptMin(cptMin);
				mode = ConstanteUtils.REST_VALID_SUCCESS;
				if (cptMin.getTemResetPwdCptMin() && cptMin.getInitPwdKeyCptMin() != null) {
					paramReinitPwd = "?" + ConstanteUtils.CPT_MIN_INIT_PWD_PARAM + "=" + cptMin.getInitPwdKeyCptMin();
				}
			}
		} else {
			mode = ConstanteUtils.REST_VALID_CPT_NULL;
		}
//		try {
//
//		} catch (final Exception e) {
//			mode = ConstanteUtils.REST_VALID_ERROR;
//		}

		final String path = loadBalancingController.getApplicationPath(true) + paramReinitPwd + "#!" + CandidatCompteMinimaView.NAME + "/" + mode;
		return "redirect:" + path;
	}

	/**
	 * valide le mail
	 */
	@RequestMapping(value = "/mail/{numDossierOpiEncode}", method = RequestMethod.GET)
	public String valideMail(@PathVariable final String numDossierOpiEncode) {
		String numDossierOpi = null;
		String mode = "";
		try {
			final byte[] numDossierOpiByte = Base64.getUrlDecoder().decode(numDossierOpiEncode);
			numDossierOpi = new String(numDossierOpiByte);

			if (numDossierOpi != null) {
				final CompteMinima cptMin = candidatController.searchCptMinByNumDossier(numDossierOpi);
				mode = "";
				if (cptMin != null) {
					if (cptMin.getTemValidMailCptMin()) {
						mode = ConstanteUtils.REST_VALID_ALREADY_VALID;
					} else {
						cptMin.setTemValidMailCptMin(true);
						candidatController.simpleSaveCptMin(cptMin);
						mode = ConstanteUtils.REST_VALID_SUCCESS;
						//userController.validSecurityUserMail(cptMin, true);
					}
				} else {
					mode = ConstanteUtils.REST_VALID_CPT_NULL;
				}
			}
		} catch (final Exception e) {
			mode = ConstanteUtils.REST_VALID_ERROR;
		}

		final String path = loadBalancingController.getApplicationPath(true) + "#!" + CandidatCompteMinimaView.NAME + "/" + mode;
		return "redirect:" + path;
	}
}
