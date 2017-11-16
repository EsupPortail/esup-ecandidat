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
package fr.univlorraine.ecandidat.services.siscol;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Class utilitaire des services rest de l'AMUE	
 * @author Kevin Hergalant
 *
 */
public class SiScolRestUtils {
	
	/**
	 * @param property
	 * @return le path du service dans le fichier property de l'amue
	 * @throws SiScolException
	 */
	/*public static String getProperty(String property) throws SiScolException {
		String filename = ConstanteUtils.WS_APOGEE_PROP_FILE;
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = SiScolRestUtils.class.getResourceAsStream(filename);
			if (input == null) {
				throw new SiScolException("Erreur à la lecture du fichier " + filename);
			}
			prop.load(input);
			String path = prop.getProperty(property);
    		if (!path.endsWith("/")){
    			path = path + "/";
    		}
			return path;
		} catch (IOException ex) {
			throw new SiScolException("Erreur à la lecture du fichier " + filename);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					throw new SiScolException("Erreur à la fermeture du fichier " + filename);
				}
			}
		}
	}*/
	

	
	/**
	 * @param path
	 * @param service
	 * @param mapGetParameter
	 * @return l'uri du service demandé
	 */
	public static URI getURIForService(String path, String service, MultiValueMap<String, String> mapGetParameter){
		return UriComponentsBuilder.fromUriString(path)
			    .path(service)
			    .queryParams(mapGetParameter)
			    .build()
			    .toUri();		
	}	
	
	/**
	 * @param response
	 * @return le charset du header
	 */
	private static Charset getCharset(ClientHttpResponse response) {
		HttpHeaders headers = response.getHeaders();
		MediaType contentType = headers.getContentType();
		Charset charset =  contentType != null ? contentType.getCharset() : null;
		if (charset == null){
			charset = Charset.forName(ConstanteUtils.WS_APOGEE_DEFAULT_CHARSET);
		}
		return charset;
	}
	
	/**Class de deserialisation de boolean
	 * @author Kevin Hergalant
	 *
	 */
	public static class StringBooleanDeserializer extends JsonDeserializer<Boolean> {
	    @Override
	    public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
	    	if (parser != null && parser.getText() != null){
	    		return parser.getText().equals(ConstanteUtils.TYP_BOOLEAN_YES)?true:false;
	    	}
	        return false;
	    }       
	}
	
	/** Class de customisation d'erreur pour un appel au service rest de l'amue
	 * @author Kevin Hergalant
	 *
	 */
	public static class SiScolResponseErrorHandler implements ResponseErrorHandler{
		
		private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
		
		@Override
		public boolean hasError(ClientHttpResponse response) throws IOException {
			return errorHandler.hasError(response);				
		}
		
		@Override
		public void handleError(ClientHttpResponse response) throws IOException {
			try{
				String jsonInString = IOUtils.toString(response.getBody(), getCharset(response));
				SiScolRestException erreur = new ObjectMapper().readValue(jsonInString, SiScolRestException.class);				
				throw erreur;
			}catch(SiScolRestException e){
				throw e;
			}catch (Exception ex){}
			errorHandler.handleError(response);
		}
	}

	/** Class d'exception pour les appels rest SiScol
	 * @author Kevin Hergalant
	 *
	 */
	@Data
	@EqualsAndHashCode(callSuper=false)
	public static class SiScolRestException extends RuntimeException {
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 5565823427943349016L;

		private String erreurMsg;
		private String erreurType;
		private String erreurDescription;

		/**
		 * Constructeur
		 */
		@JsonCreator
		public SiScolRestException(@JsonProperty("erreurMsg") String erreurMsg,
				@JsonProperty("erreurType") String erreurType,
				@JsonProperty("erreurDescription") String erreurDescription) {
			this.erreurMsg = erreurMsg;
			this.erreurType = erreurType;
			this.erreurDescription = erreurDescription;
		}
	}
	
}
