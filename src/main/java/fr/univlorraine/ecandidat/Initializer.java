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
package fr.univlorraine.ecandidat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.atmosphere.cpr.SessionSupport;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import fr.univlorraine.ecandidat.config.SpringConfig;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.tools.logback.UserMdcServletFilter;

/**
 * Initialisation de l'application web
 * @author Adrien Colson
 */
public class Initializer implements WebApplicationInitializer {

	public final static String PROPERTY_FILE_PATH = "config.location";

	/**
	 * Profil Spring de debug
	 */
	public final static String DEBUG_PROFILE = "debug";
	public final static String TRACE_PROFILE = "trace";
	public final static String TRACE_FULL_PROFILE = "traceFull";

	/**
	 * Ajoute les paramètres de contexte aux propriétés Logback.
	 * @see                  https://logback.qos.ch/faq.html#sharedConfiguration
	 * @param servletContext the {@code ServletContext} to initialize
	 * @param productionMode production mode
	 */
	private void addContextParametersToLogbackConfig(final ServletContext servletContext) {
		final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		final JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(loggerContext);
		loggerContext.reset();

		final Enumeration<String> parameterNames = servletContext.getInitParameterNames();
		while (parameterNames.hasMoreElements()) {
			final String parameterName = parameterNames.nextElement();
			loggerContext.putProperty("context." + parameterName, servletContext.getInitParameter(parameterName));
		}

		try {
			final InputStream logbackConfig = getClass().getResourceAsStream("/logback.xml");
			jc.doConfigure(logbackConfig);
			logbackConfig.close();
		} catch (final JoranException | IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Ajoute les paramètres de contexte aux propriétés systèmes, de manière à les rendre accessibles dans logback.xml
	 * @param servletContext
	 */
	private void addContextParametersToSystemProperties(final ServletContext servletContext) {
		final Enumeration<String> e = servletContext.getInitParameterNames();
		while (e.hasMoreElements()) {
			final String parameterName = e.nextElement();
			System.setProperty("context." + parameterName, servletContext.getInitParameter(parameterName));
		}
		System.setProperty(ConstanteUtils.STARTUP_INIT_FLYWAY, "");
	}

	/**
	 * @see org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup(final ServletContext servletContext) throws ServletException {
		/* Si un fichier de properties est fourni, on charge toutes les propriétés dans le servletContext pour alimenter logback par la suite */
		final Properties properties = MethodUtils.loadPropertieFile();
		properties.forEach((k, v) -> servletContext.setInitParameter((String) k, (String) v));

		addContextParametersToSystemProperties(servletContext);
		addContextParametersToLogbackConfig(servletContext);

		/* Gestion des sessions dans Atmosphere (Push Vaadin) */
		servletContext.addListener(SessionSupport.class);

		/* Ajout du MaxInactiveInterval sur la httpSession */
		servletContext.addListener(new HttpSessionListener() {
			@Override
			public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
				httpSessionEvent.getSession().setMaxInactiveInterval(Integer.valueOf(ConstanteUtils.SESSION_MAX_INACTIVE_INTERVAL));
			}

			@Override
			public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
			}
		});

		/* Configure Spring */
		final AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
		final String logMode = servletContext.getInitParameter("logMode");
		if (logMode != null && logMode.startsWith(TRACE_PROFILE)) {
			springContext.getEnvironment().setActiveProfiles(logMode);
		}
		springContext.register(SpringConfig.class);
		servletContext.addListener(new ContextLoaderListener(springContext));

//		final String refreshRate = servletContext.getInitParameter("load.balancing.refresh.fixedRate");
//		System.out.println("refreshRate "+refreshRate);
//		if (refreshRate == null) {
//			//on place par défaut le refresh à 10min
//			servletContext.setInitParameter("load.balancing.refresh.fixedRate", "600000");
//		}

		/* String refreshRateFichier = servletContext.getInitParameter("fiabilisation.fichier.refresh.fixedRate");
		 * if (refreshRateFichier==null){
		 * //on place par défaut le refresh à 30min
		 * servletContext.setInitParameter("fiabilisation.fichier.refresh.fixedRate", "30000");//1800000
		 * } */

		/* Filtre Spring Security */
		final FilterRegistration.Dynamic springSecurityFilterChain = servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
		springSecurityFilterChain.addMappingForUrlPatterns(null, false, "/*");

		/* Filtre passant l'utilisateur courant à Logback */
		final FilterRegistration.Dynamic userMdcServletFilter = servletContext.addFilter("userMdcServletFilter", UserMdcServletFilter.class);
		userMdcServletFilter.addMappingForUrlPatterns(null, false, "/*");

		/* Servlet REST */
		final ServletRegistration.Dynamic restServlet = servletContext.addServlet("rest", new DispatcherServlet(springContext));
		restServlet.setLoadOnStartup(1);
		restServlet.addMapping("/rest", "/rest/*");
	}

}
