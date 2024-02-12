package fr.univlorraine.ecandidat.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void configureMessageConverters(final List<HttpMessageConverter<?>> messageConverters) {
		messageConverters.add(0, jacksonMessageConverter());
	}

	@SuppressWarnings("serial")
	@Bean
	public MappingJackson2HttpMessageConverter jacksonMessageConverter() {
		final SimpleModule moduleEmptyStringAsNull = new SimpleModule();
		moduleEmptyStringAsNull.addDeserializer(String.class, new StdDeserializer<String>(String.class) {

			@Override
			public String deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
				final String result = StringDeserializer.instance.deserialize(p, ctxt);
				if (StringUtils.isEmpty(result)) {
					return null;
				}
				return result;
			}
		});

		final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

		final List<MediaType> supportedMediaTypes = new ArrayList<>();
		supportedMediaTypes.addAll(converter.getSupportedMediaTypes());
		supportedMediaTypes.add(MediaType.ALL);
		converter.setSupportedMediaTypes(supportedMediaTypes);
		converter.getObjectMapper().registerModule(moduleEmptyStringAsNull);
		return converter;
	}
}