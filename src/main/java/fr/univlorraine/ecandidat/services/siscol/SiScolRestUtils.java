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

/**
 * Class utilitaire des services rest de l'AMUE
 * @author Kevin Hergalant
 */
public class SiScolRestUtils {

	/**
	 * @param  path
	 * @param  service
	 * @param  mapGetParameter
	 * @return                 l'uri du service demandé
	 */
	public static URI getURIForService(final String path, final String service, final MultiValueMap<String, String> mapGetParameter) {
		return UriComponentsBuilder.fromUriString(path).path(service).queryParams(mapGetParameter).build().toUri();
	}

	/**
	 * @param  path
	 * @param  service
	 * @param  mapGetParameter
	 * @return                 l'uri du service demandé
	 */
	public static URI getURIForService(String path, final String suffixe, final String service, final MultiValueMap<String, String> mapGetParameter) {
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		path = path + suffixe;

		return UriComponentsBuilder.fromUriString(path).path(service).queryParams(mapGetParameter).build().toUri();
	}

	/**
	 * @param  service
	 * @param  subService
	 * @return            un sous service
	 */
	public static String getSubService(final String service, final String... subServices) {
		String path = service;
		for (final String subService : subServices) {
			path = path + "/" + subService;
		}
		return path + "/";
	}

	/**
	 * @param  path
	 * @param  service
	 * @param  mapGetParameter
	 * @return                 l'uri du service demandé
	 */
	public static URI getURIForService(String path, final String suffixe, final String service, final Long offset, final Long limit, final MultiValueMap<String, String> mapGetParameter) {
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		path = path + suffixe;
		final String myService = service + "/" + ConstanteUtils.PEGASE_OFFSET_PARAM + "/" + offset + "/" + ConstanteUtils.PEGASE_LIMIT_PARAM + "/" + limit;
		return UriComponentsBuilder.fromUriString(path).path(myService).queryParams(mapGetParameter).build().toUri();
	}

	/**
	 * @param  path
	 * @param  service
	 * @return         l'uri du service demandé
	 */
	public static URI getURIForPostService(final String path, final String service) {
		return UriComponentsBuilder.fromUriString(path).path(service).build().toUri();
	}

	/**
	 * @param  response
	 * @return          le charset du header
	 */
	private static Charset getCharset(final ClientHttpResponse response) {
		final HttpHeaders headers = response.getHeaders();
		final MediaType contentType = headers.getContentType();
		Charset charset = contentType != null ? contentType.getCharset() : null;
		if (charset == null) {
			charset = Charset.forName(ConstanteUtils.WS_APOGEE_DEFAULT_CHARSET);
		}
		return charset;
	}

	/**
	 * Class de deserialisation de boolean
	 * @author Kevin Hergalant
	 */
	public static class StringBooleanDeserializer extends JsonDeserializer<Boolean> {
		@Override
		public Boolean deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
			if (parser != null && parser.getText() != null) {
				return parser.getText().equals(ConstanteUtils.TYP_BOOLEAN_YES) ? true : false;
			}
			return false;
		}
	}

	/**
	 * Class de customisation d'erreur pour un appel au service rest de l'amue
	 * @author Kevin Hergalant
	 */
	public static class SiScolResponseErrorHandler implements ResponseErrorHandler {

		private final ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

		@Override
		public boolean hasError(final ClientHttpResponse response) throws IOException {
			return errorHandler.hasError(response);
		}

		@Override
		public void handleError(final ClientHttpResponse response) throws IOException {
			try {
				final String jsonInString = IOUtils.toString(response.getBody(), getCharset(response));
				final SiScolRestException erreur = new ObjectMapper().readValue(jsonInString, SiScolRestException.class);
				throw erreur;
			} catch (final SiScolRestException e) {
				throw e;
			} catch (final Exception ex) {
			}
			errorHandler.handleError(response);
		}
	}

	/**
	 * Class d'exception pour les appels rest SiScol
	 * @author Kevin Hergalant
	 */
	@Data
	@EqualsAndHashCode(callSuper = false)
	@SuppressWarnings("serial")
	public static class SiScolRestException extends RuntimeException {

		private String erreurMsg;
		private String erreurType;
		private String erreurDescription;

		/** Constructeur */
		@JsonCreator
		public SiScolRestException(@JsonProperty("erreurMsg") final String erreurMsg,
			@JsonProperty("erreurType") final String erreurType,
			@JsonProperty("erreurDescription") final String erreurDescription) {
			this.erreurMsg = erreurMsg;
			this.erreurType = erreurType;
			this.erreurDescription = erreurDescription;
		}
	}

}
