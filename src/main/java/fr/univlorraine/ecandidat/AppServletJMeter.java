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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.Constants;
import com.vaadin.server.CustomizedSystemMessages;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.spring.server.SpringVaadinServletService;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.UIDetachedException;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/** Servlet principale.
 *
 * @author Adrien Colson */
@WebServlet(value = ConstanteUtils.SERVLET_NO_MATCH, asyncSupported = true, initParams = {
		@WebInitParam(name = Constants.SERVLET_PARAMETER_HEARTBEAT_INTERVAL, value = ConstanteUtils.SERVLET_PARAMETER_HEARTBEAT_INTERVAL),
		@WebInitParam(name = ApplicationConfig.SESSION_MAX_INACTIVE_INTERVAL, value =  ConstanteUtils.SESSION_MAX_INACTIVE_INTERVAL),
		@WebInitParam(name = ApplicationConfig.WEBSOCKET_SUPPORT_SERVLET3, value = "true"),
		@WebInitParam(name = ApplicationConfig.ATMOSPHERE_INTERCEPTORS, value = "fr.univlorraine.tools.atmosphere.RecoverSecurityContextAtmosphereInterceptor"),
		@WebInitParam(name = "disable-xsrf-protection", value = "true"),
        @WebInitParam(name = "syncIdCheck", value = "false"),
        @WebInitParam(name = VaadinSession.UI_PARAMETER, value = "fr.univlorraine.ecandidat.MainUI")
		//@WebInitParam(name = VaadinServlet.PARAMETER_WIDGETSET, value = "fr.univlorraine.ecandidat.AppWidgetset")
})
public class AppServletJMeter extends SpringVaadinServlet implements Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = 6170593875497023254L;
	
	/** The logger. */
	private final Logger logger = LoggerFactory.getLogger(AppServletJMeter.class);

	/**
	 * Constructeur
	 */
	public AppServletJMeter() {
		System.setProperty(getPackageName() + "." + "disable-xsrf-protection", "true");
	}

	@Override
	protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {
		JMeterService service = new JMeterService(this, deploymentConfiguration);
		service.init();
		return service;
	}

    private String getPackageName() {
        String pkgName;
        final Package pkg = this.getClass().getPackage();
        if (pkg != null) {
            pkgName = pkg.getName();
        } else {
            final String className = this.getClass().getName();
            pkgName = new String(className.toCharArray(), 0,
                    className.lastIndexOf('.'));
        }
        return pkgName;
    }

	/** Servlet initialized.
	 *
	 * @throws ServletException
	 *             the servlet exception
	 * @see com.vaadin.spring.server.SpringVaadinServlet#servletInitialized() */
	@Override
	protected void servletInitialized() throws ServletException {
		logger.debug("JMeter Servlet Initialized");
		super.servletInitialized();
		/* Log les erreurs non gerees */
		getService().addSessionInitListener(sessionInitEvent -> {
			sessionInitEvent.getSession().setErrorHandler(e -> {
				Throwable cause = e.getThrowable();
				while (cause instanceof Throwable) {
					/* Gère les accès non autorisés */
					if (cause instanceof AccessDeniedException) {
						UI.getCurrent().getNavigator().navigateTo("accessDenied");
						return;
					}
					/* Gère les UIs détachées pour les utilisateurs déconnectés */
					if (cause instanceof AuthenticationCredentialsNotFoundException || cause instanceof UIDetachedException) {
						return;
					}
					cause = cause.getCause();
				}
				logger.error("Erreur non gérée", e.getThrowable());
			});
		});

		/* Traduit les messages syst�mes de Vaadin */
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
				/**serialVersionUID**/
				private static final long serialVersionUID = 7274300032260312467L;

				/** @see com.vaadin.server.BootstrapListener#modifyBootstrapPage(com.vaadin.server.BootstrapPageResponse) */
				@Override
				public void modifyBootstrapPage(final BootstrapPageResponse response) {
					final Element head = response.getDocument().head();
					head.appendElement("meta").attr("name", "viewport").attr("content", "width=device-width, initial-scale=1");
					head.appendElement("meta").attr("name", "apple-mobile-web-app-capable").attr("content", "yes");
					head.appendElement("meta").attr("name", "apple-mobile-web-app-status-bar-style").attr("content", "black");
				}

				/** @see com.vaadin.server.BootstrapListener#modifyBootstrapFragment(com.vaadin.server.BootstrapFragmentResponse) */
				@Override
				public void modifyBootstrapFragment(final BootstrapFragmentResponse response) {
				}
			});
		});
	}
	
	
	
    /**
     * Class JMeterService
     *
     */
    public static class JMeterService extends SpringVaadinServletService {
        private static final long serialVersionUID = -5874716650679865909L;
 
        public JMeterService(VaadinServlet servlet,
                DeploymentConfiguration deploymentConfiguration)
                throws ServiceException {
            super(servlet, deploymentConfiguration,null);
        }
 
        @Override
        protected VaadinSession createVaadinSession(VaadinRequest request)
                throws ServiceException {
            return new JMeterSession(this);
        }
    }
 
    /**
     * Class JMeterSession
     *
     */
    public static class JMeterSession extends VaadinSession {
        private static final long serialVersionUID = 4596901275146146127L;
 
        public JMeterSession(VaadinService service) {
            super(service);
        }
 
        @SuppressWarnings("deprecation")
		@Override
        public String createConnectorId(ClientConnector connector) {
            if (connector instanceof Component) {
                Component component = (Component) connector;
                return component.getId() == null ? super
                        .createConnectorId(connector) : component.getId();
            }
            return super.createConnectorId(connector);
        }
    }
}
