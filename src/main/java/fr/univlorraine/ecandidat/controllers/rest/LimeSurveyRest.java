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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import fr.univlorraine.ecandidat.utils.NomenclatureUtils;

/**
 * La classe principale pour les appels WS LimeSurvey
 * @author Kevin Hergalant
 */
@Component
public class LimeSurveyRest {

	private final Logger logger = LoggerFactory.getLogger(LimeSurveyRest.class);

	/* Injections */
	@Value("${limesurvey.path}")
	private transient String URL;
	@Value("${limesurvey.user}")
	private transient String USER;
	@Value("${limesurvey.pass}")
	private transient String PWD;

	/**
	 * Execute un WS sur LimeSurvey
	 * @param  obj
	 * @return     la ResponseEntity
	 */
	private ResponseEntity<String> executeWS(final LimeSurveyRestObject obj) {
		try {
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String serialized;
			final ObjectMapper mapper = new ObjectMapper();
			serialized = mapper.writeValueAsString(obj);
			final HttpEntity<String> requestEntity = new HttpEntity<>(serialized, headers);
			final RestTemplate restTemplate = new RestTemplate();
			final ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, requestEntity, String.class);
			return response;
		} catch (final Exception e) {
			logger.error("Erreur d'appel du web service " + obj.getMethod() + " sur LimeSurvey", e);
			return null;
		}
	}

	/**
	 * Recupere la sessionKey pour chaque appel WS
	 * @return                      la sessionKey
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public LimeSurveyRestObjectRetour getSessionKey() throws JsonParseException, JsonMappingException, IOException {
		final LimeSurveyRestObject obj = new LimeSurveyRestObject("get_session_key");
		obj.addParameter("username", USER);
		obj.addParameter("password", PWD);
		final ResponseEntity<String> response = executeWS(obj);
		final ObjectMapper mapper = new ObjectMapper();
		if (response == null) {
			return null;
		}
		final LimeSurveyRestObjectRetour ret = mapper.readValue(response.getBody(), LimeSurveyRestObjectRetour.class);
		return ret;
	}

	/**
	 * Exporte les réponses à un questionnaire
	 * @param  idFormulaireLimeSurvey
	 * @param  codLangue
	 * @return                        les réponses à un questionnaire
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public List<SurveyReponse> exportResponse(final Integer idFormulaireLimeSurvey, final String codLangue) throws JsonParseException, JsonMappingException, IOException {
		if (URL == null || URL.equals("") || USER == null || USER.equals("") || PWD == null || PWD.equals("")) {
			return null;
		}
		logger.debug("Lancement du WebService LimeSurvey (idFormulaireLimeSurvey=" + idFormulaireLimeSurvey + ", codLangue=" + codLangue + ")");
		final LimeSurveyRestObjectRetour sessionKeyRest = getSessionKey();
		if (sessionKeyRest == null) {
			return null;
		}
		final String sessionKey = sessionKeyRest.getResult();
		if (sessionKey == null) {
			return null;
		}
		final LimeSurveyRestObject obj = new LimeSurveyRestObject("export_responses");
		obj.addParameter("sSessionKey", sessionKey);
		obj.addParameter("iSurveyID", idFormulaireLimeSurvey);
		obj.addParameter("sDocumentType", "json");
		obj.addParameter("sLanguageCode", codLangue);
		obj.addParameter("sCompletionStatus", "complete");
		obj.addParameter("sHeadingType", "code");
		obj.addParameter("sResponseType", "long");
		final List<String> listeChamps = new ArrayList<>();
		listeChamps.add("numDossier");
		listeChamps.add("idCandidature");
		obj.addParameter("aFields", listeChamps);

		final ResponseEntity<String> response = executeWS(obj);
		if (response == null) {
			return new ArrayList<>();
		}
		final ObjectMapper mapper = new ObjectMapper();
		try {
			final List<SurveyReponse> listToRet = new ArrayList<>();
			final LimeSurveyRestObjectRetour ret = mapper.readValue(response.getBody(), LimeSurveyRestObjectRetour.class);
			final String valueDecoded = new String(Base64.getDecoder().decode(ret.getResult().getBytes()));
			/* Ne fonctionne pas avec les versions 5 de LS */
//			Avant 5.x : {"responses": [{"2":{"id":"2","submitdate":"2021-12-14 16:32:12","lastpage":"1","startlanguage":"fr","startdate":"2021-12-14 16:32:04","datestamp":"2021-12-14 16:32:12","numDossier":"00CJWJIE","idCandidature":"493583","question":"Blabla yaaaaaaaaaataaaaaaaaaa"}},{"3":{"id":"3","submitdate":"2021-12-14 16:34:59","lastpage":"1","startlanguage":"fr","startdate":"2021-12-14 16:34:50","datestamp":"2021-12-14 16:34:59","numDossier":"00CJWJIE","idCandidature":"493583","question":"ceci est ma nouvelle r\u00e9ponse"}},{"4":{"id":"4","submitdate":"2021-12-14 16:35:51","lastpage":"1","startlanguage":"fr","startdate":"2021-12-14 16:35:41","datestamp":"2021-12-14 16:35:51","numDossier":"00CJWJIE","idCandidature":"493583","question":"Et allez, encore une!"}}]}
//			En 5.x : {"responses": [{"id":"1","submitdate":"2022-01-04 10:47:48","lastpage":"1","startlanguage":"fr","seed":"878465234","startdate":"2022-01-04 10:46:48","datestamp":"2022-01-04 10:47:48","numDossier":"00CJWJIE","Test":"Je suis une r\u00e9ponse"}]}
			final SurveyReponseRoot root = mapper.readValue(valueDecoded, SurveyReponseRoot.class);
			root.getResponses().forEach(e -> {
				final LinkedHashMap<String, SurveyReponse> hash = e;
				hash.forEach((k, v) -> {
					listToRet.add(v);
				});
			});
			return listToRet;
		} catch (final Exception e) {
			return new ArrayList<>();
		}
	}

	/**
	 * @return la version LS
	 */
	public String getVersionLimeSurvey() {
		if (URL == null || URL.equals("") || USER == null || USER.equals("") || PWD == null || PWD.equals("")) {
			return NomenclatureUtils.VERSION_NO_VERSION_VAL;
		}
		try {
			final LimeSurveyRestObjectRetour sessionKeyRest = getSessionKey();
			if (sessionKeyRest == null) {
				return null;
			}
			final String sessionKey = sessionKeyRest.getResult();
			if (sessionKey == null) {
				return null;
			}

			final String versionNumber = getSiteSetting(sessionKey, "versionnumber");
			if (versionNumber == null) {
				return NomenclatureUtils.VERSION_NO_VERSION_VAL;
			}
			final String buildNumber = getSiteSetting(sessionKey, "buildnumber");
			if (buildNumber == null) {
				return NomenclatureUtils.VERSION_NO_VERSION_VAL;
			}
			return versionNumber + "+" + buildNumber;
		} catch (final Exception e) {
		}
		return NomenclatureUtils.VERSION_NO_VERSION_VAL;
	}

	/**
	 * @param  sessionKey
	 * @param  settingName
	 * @return                      un site settings LimeSurvey
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private String getSiteSetting(final String sessionKey, final String settingName) throws JsonParseException, JsonMappingException, IOException {
		final LimeSurveyRestObject obj = new LimeSurveyRestObject("get_site_settings");
		obj.addParameter("sSessionKey", sessionKey);
		obj.addParameter("sSetttingName", settingName);

		final ResponseEntity<String> response = executeWS(obj);
		final ObjectMapper mapper = new ObjectMapper();
		final SimpleModule module = new SimpleModule();
		module.addDeserializer(LimeSurveyRestObjectRetour.class, new LimeSurveyRestObjectRetourDeserializer());
		mapper.registerModule(module);
		final LimeSurveyRestObjectRetour ret = mapper.readValue(response.getBody(), LimeSurveyRestObjectRetour.class);
		if (ret == null) {
			// logger.warn("Impossible de déterminer la version limesurvey (" + settingName + ")");
			return null;
		}
		if (ret.getError() != null) {
			// logger.warn("Impossible de déterminer la version limesurvey (" + settingName + "), error : " + ret.getError());
			return null;
		}
		return ret.getResult();
	}
}
