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
package fr.univlorraine.ecandidat.config;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vaadin.spring.annotation.EnableVaadin;

import fr.univlorraine.ecandidat.Initializer;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.KeyValue;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/**
 * Configuration Spring
 * @author Adrien Colson
 */
@Configuration
@EnableSpringConfigured
@ComponentScan(basePackageClasses = Initializer.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableVaadin
@EnableScheduling
// @EnableVaadinNavigation
@PropertySource("classpath:/app.properties")
public class SpringConfig {

	/** @return PropertySourcesPlaceholderConfigurer qui ajoute les paramètres de contexte aux propriétés Spring */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	/** @return ResourceBundleMessageSource pour les messages de l'application */
	@Bean
	public ResourceBundleMessageSource messageSource() {
		final ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
		resourceBundleMessageSource.setBasenames("i18n/messages", "i18n/backoffice/backoffice-messages", "i18n/backoffice/nomenclature-messages", "i18n/candidat/candidat-messages");
		resourceBundleMessageSource.setFallbackToSystemLocale(false);
		return resourceBundleMessageSource;
	}

	/** @return un formatter de date */
	@Bean
	public static DateTimeFormatter formatterDate() {
		return DateTimeFormatter.ofPattern("dd/MM/yyyy");
	}

	/** @return un formatter de dateTime pour apogee */
	@Bean
	public static DateTimeFormatter formatterDateFile() {
		return DateTimeFormatter.ofPattern("yyyyMMdd");
	}

	/** @return un formatter de dateTime */
	@Bean
	public static DateTimeFormatter formatterDateTime() {
		return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	}

	/** @return un formatter de dateTime */
	@Bean
	public static DateTimeFormatter formatterDateTimeFile() {
		return DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
	}

	/** @return un formatter de temps */
	@Bean
	public static DateTimeFormatter formatterTime() {
		return DateTimeFormatter.ofPattern("HH'h'mm");
	}

	/** @return un formatter de dateTime pour les WS */
	@Bean
	public static DateTimeFormatter formatterDateTimeWS() {
		return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	}

	/** @return un formatter de dateTime pour apogee */
	@Bean
	public static DateTimeFormatter formatterDateTimeApo() {
		return DateTimeFormatter.ofPattern("ddMMyyyy");
	}

	/** @return un formatter de dateTime pour apogee WS des PJ */
	@Bean
	public static DateTimeFormatter formatterDateTimeApoWsPj() {
		return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	}

	/** @return l'url du WS des PJ Apogée */
	@Bean
	static String urlWsPjApogee() {
		return MethodUtils.getUrlWSApogee(ConstanteUtils.WS_APOGEE_PJ_URL_SERVICE);
	}

	/** @return l'url du WS des PJ Apogée */
	@Bean
	static KeyValue headerWsPjApogee() {
		return MethodUtils.getHeaderWSApogee(ConstanteUtils.WS_APOGEE_PJ_URL_SERVICE);
	}

	/** @return l'url du WS de verification de l'INES */
	@Bean
	static String urlWsCheckInes() {
		return MethodUtils.getUrlWSApogee(ConstanteUtils.WS_INES_CHECK_URL_SERVICE);
	}

	/** @return l'url du WS des PJ Apogée */
	@Bean
	static KeyValue headerWsCheckInes() {
		return MethodUtils.getHeaderWSApogee(ConstanteUtils.WS_INES_CHECK_URL_SERVICE);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("serial")
	@Bean
	public RestTemplate wsPegaseRestTemplate() {
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
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
		converter.getObjectMapper().registerModule(moduleEmptyStringAsNull);

		return new RestTemplateBuilder()
			.additionalMessageConverters(converter)
			.build();
	}

}
