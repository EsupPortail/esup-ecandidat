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
