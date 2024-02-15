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

import java.io.Serializable;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import org.atmosphere.cpr.ApplicationConfig;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.Constants;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.spring.server.SpringVaadinServlet;

import fr.univlorraine.ecandidat.controllers.ConfigController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Servlet principale.
 * @author Adrien Colson
 */
@SuppressWarnings("serial")
@WebServlet(value = ConstanteUtils.SERVLET_ALL_MATCH,
	asyncSupported = true,
	initParams = {
		@WebInitParam(name = Constants.SERVLET_PARAMETER_HEARTBEAT_INTERVAL, value = ConstanteUtils.SERVLET_PARAMETER_HEARTBEAT_INTERVAL),
		@WebInitParam(name = ApplicationConfig.SESSION_MAX_INACTIVE_INTERVAL, value = ConstanteUtils.SESSION_MAX_INACTIVE_INTERVAL),
		@WebInitParam(name = Constants.SERVLET_PARAMETER_CLOSE_IDLE_SESSIONS, value = "true"),
		@WebInitParam(name = ApplicationConfig.WEBSOCKET_SUPPORT_SERVLET3, value = "true"),
		@WebInitParam(name = Constants.SERVLET_PARAMETER_PRODUCTION_MODE, value = "true"),
		@WebInitParam(name = ApplicationConfig.ATMOSPHERE_INTERCEPTORS, value = "fr.univlorraine.tools.atmosphere.RecoverSecurityContextAtmosphereInterceptor"),
	})
public class AppServlet extends SpringVaadinServlet implements Serializable {

	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(AppServlet.class);

	/**
	 * Servlet initialized.
	 * @throws ServletException
	 *                             the servlet exception
	 * @see                     com.vaadin.spring.server.SpringVaadinServlet#servletInitialized()
	 */
	@Override
	protected void servletInitialized() throws ServletException {
		logger.debug("Standard Servlet Initialized");
		super.servletInitialized();

		/* Traduit les messages systemes de Vaadin */
		final ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
		getService().setSystemMessagesProvider(smi -> {
			final Locale locale = smi.getLocale();
			final CustomizedSystemMessages customizedSystemMessages = new CustomizedSystemMessages();
			customizedSystemMessages.setSessionExpiredCaption(applicationContext.getMessage("vaadin.sessionExpired.caption", null, locale));
			customizedSystemMessages.setSessionExpiredMessage(applicationContext.getMessage("vaadin.sessionExpired.message", null, locale));
			customizedSystemMessages.setCommunicationErrorCaption(applicationContext.getMessage("vaadin.communicationError.caption", null, locale));
			customizedSystemMessages.setCommunicationErrorMessage(applicationContext.getMessage("vaadin.communicationError.message", null, locale));
			customizedSystemMessages.setAuthenticationErrorCaption(applicationContext.getMessage("vaadin.authenticationError.caption", null, locale));
			customizedSystemMessages.setAuthenticationErrorMessage(applicationContext.getMessage("vaadin.authenticationError.message", null, locale));
			customizedSystemMessages.setInternalErrorCaption(applicationContext.getMessage("vaadin.internalError.caption", null, locale));
			customizedSystemMessages.setInternalErrorMessage(applicationContext.getMessage("vaadin.internalError.message", null, locale));
			customizedSystemMessages.setCookiesDisabledCaption(applicationContext.getMessage("vaadin.cookiesDisabled.caption", null, locale));
			customizedSystemMessages.setCookiesDisabledMessage(applicationContext.getMessage("vaadin.cookiesDisabled.message", null, locale));
			return customizedSystemMessages;
		});

		/* Met en place la responsivite */
		getService().addSessionInitListener(event -> {
			event.getSession().addBootstrapListener(new BootstrapListener() {

				/** @see com.vaadin.server.BootstrapListener#modifyBootstrapPage(com.vaadin.server.BootstrapPageResponse) */
				@Override
				public void modifyBootstrapPage(final BootstrapPageResponse response) {
					final Element head = response.getDocument().head();
					head.appendElement("meta").attr("name", "viewport").attr("content", "width=device-width, initial-scale=1");
					head.appendElement("meta").attr("name", "apple-mobile-web-app-capable").attr("content", "yes");
					head.appendElement("meta").attr("name", "apple-mobile-web-app-status-bar-style").attr("content", "black");
					try {
						final ConfigController configController = (ConfigController) applicationContext.getBean("configController");
						final String faviconBase64 = configController.getFaviconBase64();
						if (faviconBase64 != null) {
							head.getElementsByAttributeValue("rel", "icon").attr("href", "data:image/x-icon;base64," + faviconBase64);
						}
					} catch (final Exception e) {
						e.printStackTrace();
					}

//					final File fileExternal =
//						MethodUtils.getExternalResource(applicationContext.getEnvironment().getProperty("external.ressource"), ConstanteUtils.EXTERNAL_RESSOURCE_IMG_FOLDER, ConstanteUtils.EXTERNAL_RESSOURCE_IMG_FAV_FILE);
//					if (fileExternal != null) {
//						try {
//							final byte[] fileContent = FileUtils.readFileToByteArray(fileExternal);
//							final String encodedString = Base64.getEncoder().encodeToString(fileContent);
//
//						} catch (final Exception e) {
//						}
//					}
				}

				/** @see com.vaadin.server.BootstrapListener#modifyBootstrapFragment(com.vaadin.server.BootstrapFragmentResponse) */
				@Override
				public void modifyBootstrapFragment(final BootstrapFragmentResponse response) {
				}
			});
		});
	}

}
