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

import java.util.Enumeration;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.atmosphere.cpr.SessionSupport;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import fr.univlorraine.ecandidat.config.SpringConfig;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.tools.logback.UserMdcServletFilter;

/**
 * Initialisation de l'application web
 * 
 * @author Adrien Colson
 */
public class Initializer implements WebApplicationInitializer {

	/**
	 * Profil Spring de debug
	 */
	public final static String DEBUG_PROFILE = "debug";
	public final static String TRACE_PROFILE = "trace";
	public final static String TRACE_FULL_PROFILE = "traceFull";	

	/**
	 * Ajoute les paramètres de contexte aux propriétés systèmes, de manière à les rendre accessibles dans logback.xml
	 * @param servletContext
	 */
	private void addContextParametersToSystemProperties(ServletContext servletContext) {
		Enumeration<String> e = servletContext.getInitParameterNames();
		while (e.hasMoreElements()) {
			String parameterName = e.nextElement();
			System.setProperty("context." + parameterName, servletContext.getInitParameter(parameterName));
		}
		System.setProperty(ConstanteUtils.STARTUP_INIT_FLYWAY, "");
	}

	/**
	 * @see org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		addContextParametersToSystemProperties(servletContext);

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
		AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
		String logMode = servletContext.getInitParameter("logMode");
		if (logMode!=null && logMode.startsWith(TRACE_PROFILE)){
			springContext.getEnvironment().setActiveProfiles(logMode);
		}
		springContext.register(SpringConfig.class);
		servletContext.addListener(new ContextLoaderListener(springContext));
		
		String refreshRate = servletContext.getInitParameter("load.balancing.refresh.fixedRate");
		if (refreshRate==null){
			//on place par défaut le refresh à 10min
			servletContext.setInitParameter("load.balancing.refresh.fixedRate", "600000");
		}
		
		/*String refreshRateFichier = servletContext.getInitParameter("fiabilisation.fichier.refresh.fixedRate");
		if (refreshRateFichier==null){
			//on place par défaut le refresh à 30min
			servletContext.setInitParameter("fiabilisation.fichier.refresh.fixedRate", "30000");//1800000
		}*/

		/* Filtre Spring Security */
		FilterRegistration.Dynamic springSecurityFilterChain = servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
		springSecurityFilterChain.addMappingForUrlPatterns(null, false, "/*");

		/* Filtre passant l'utilisateur courant à Logback */
		FilterRegistration.Dynamic userMdcServletFilter = servletContext.addFilter("userMdcServletFilter", UserMdcServletFilter.class);
		userMdcServletFilter.addMappingForUrlPatterns(null, false, "/*");

		/* Servlet REST */
		ServletRegistration.Dynamic restServlet = servletContext.addServlet("rest", new DispatcherServlet(springContext));
		restServlet.setLoadOnStartup(1);
		restServlet.addMapping("/rest", "/rest/*");
	}

}
