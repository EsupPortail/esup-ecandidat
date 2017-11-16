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
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** La classe principale pour les appels WS LimeSurvey 
 * @author Kevin Hergalant
 *
 */
@Component
public class LimeSurveyRest {
	
	private Logger logger = LoggerFactory.getLogger(LimeSurveyRest.class);
	
	/* Injections */
	@Value("${limesurvey.path}")
	private transient String URL;
	@Value("${limesurvey.user}")
	private transient String USER;
	@Value("${limesurvey.pass}")
	private transient String PWD;
	
	/** Execute un WS sur LimeSurvey
	 * @param obj
	 * @return la ResponseEntity
	 */
	private ResponseEntity<String> executeWS(LimeSurveyRestObject obj){
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);						
			String serialized;
			ObjectMapper mapper = new ObjectMapper();
			serialized = mapper.writeValueAsString(obj);
			HttpEntity<String> requestEntity=new HttpEntity<String>(serialized,headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> response = restTemplate.exchange(URL,HttpMethod.POST,requestEntity,String.class);
			return response;
		} catch (Exception e) {
			logger.error("Erreur d'appel du web service "+obj.getMethod()+" sur LimeSurvey",e);
			return null;
		}
	}
	
	/** Recupere la sessionKey pour chaque appel WS
	 * @return la sessionKey
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public LimeSurveyRestObjectRetour getSessionKey() throws JsonParseException, JsonMappingException, IOException{
		LimeSurveyRestObject obj = new LimeSurveyRestObject("get_session_key");
		obj.addParameter("username", USER);
		obj.addParameter("password", PWD);
		ResponseEntity<String> response = executeWS(obj);
		ObjectMapper mapper = new ObjectMapper();
		if (response==null){
			return null;
		}
		LimeSurveyRestObjectRetour ret = mapper.readValue(response.getBody(), LimeSurveyRestObjectRetour.class);
		return ret;
	}
	
	/** Exporte les réponses à un questionnaire
	 * @param idFormulaireLimeSurvey
	 * @param codLangue
	 * @return les réponses à un questionnaire
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public List<SurveyReponse> exportResponse(Integer idFormulaireLimeSurvey, String codLangue) throws JsonParseException, JsonMappingException, IOException{
		if (URL == null || URL.equals("") || USER == null || USER.equals("") || PWD == null || PWD.equals("")){
			return null;
		}
		logger.debug("Lancement du WebService LimeSurvey (idFormulaireLimeSurvey="+idFormulaireLimeSurvey+", codLangue="+codLangue+")");
		LimeSurveyRestObjectRetour sessionKeyRest = getSessionKey();
		if (sessionKeyRest==null){
			return null;
		}
		String sessionKey = (String) sessionKeyRest.getResult();
		if (sessionKey==null){
			return null;
		}
		LimeSurveyRestObject obj = new LimeSurveyRestObject("export_responses");
		obj.addParameter("sSessionKey", sessionKey);
		obj.addParameter("iSurveyID", idFormulaireLimeSurvey);
		obj.addParameter("sDocumentType", "json");
		obj.addParameter("sLanguageCode", codLangue);
		obj.addParameter("sCompletionStatus", "complete");
		obj.addParameter("sHeadingType", "code");
		obj.addParameter("sResponseType", "long");
		List<String> listeChamps = new ArrayList<String>();
		listeChamps.add("numDossier");
		obj.addParameter("aFields", listeChamps);
		
		ResponseEntity<String> response = executeWS(obj);
		if (response==null){
			return new ArrayList<SurveyReponse>();
		}
		ObjectMapper mapper = new ObjectMapper();
		try{
			List<SurveyReponse> listToRet = new ArrayList<SurveyReponse>();
			LimeSurveyRestObjectRetour ret = mapper.readValue(response.getBody(), LimeSurveyRestObjectRetour.class);
			String valueDecoded = new String(Base64.decode(ret.getResult().getBytes()));
			SurveyReponseRoot root = mapper.readValue(valueDecoded, SurveyReponseRoot.class);	
			root.getResponses().forEach(e->{
				LinkedHashMap<String, SurveyReponse> hash = e;
				hash.forEach((k,v)->{
					listToRet.add(v);
				});
			});		
			return listToRet;
		}catch(Exception e){
			return new ArrayList<SurveyReponse>();
		}
		
	}
}
