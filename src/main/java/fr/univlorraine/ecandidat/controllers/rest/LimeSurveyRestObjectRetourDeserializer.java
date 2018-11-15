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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Deserializer de la class GetImputationDataOut
 *
 * @author Kevin Hergalant
 */
public class LimeSurveyRestObjectRetourDeserializer extends JsonDeserializer<LimeSurveyRestObjectRetour> {

	@Override
	public LimeSurveyRestObjectRetour deserialize(final JsonParser jsonParser,
			final DeserializationContext deserializationContext)
			throws IOException, JsonProcessingException {
		try {
			ObjectCodec oc = jsonParser.getCodec();
			JsonNode node = oc.readTree(jsonParser);

			LimeSurveyRestObjectRetour out = new LimeSurveyRestObjectRetour();
			out.setId(node.get("id").intValue());
			out.setError(node.get("error").textValue());
			if (out.getError() != null) {
				return out;
			}
			JsonNode jsonNodeResult = node.get("result");
			if (jsonNodeResult.get("status") != null && jsonNodeResult.get("status").textValue() != null && !jsonNodeResult.get("status").textValue().equals("")) {
				out.setError(jsonNodeResult.get("status").textValue());
				return out;
			}
			out.setResult(jsonNodeResult.textValue());
			return out;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
