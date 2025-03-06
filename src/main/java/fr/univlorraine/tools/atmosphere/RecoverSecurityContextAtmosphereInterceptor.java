/**
 *
 *  ESUP-Portail MONDOSSIERWEB - Copyright (c) 2016 ESUP-Portail consortium
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
 *
 */
package fr.univlorraine.tools.atmosphere;

import fr.univlorraine.tools.logback.UserMdcServletFilter;
import org.atmosphere.cpr.Action;
import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Interceptor Atmosphere permettant de restaurer le SecurityContext dans le SecurityContextHolder.
 * @see <a href="https://groups.google.com/forum/#!msg/atmosphere-framework/8yyOQALZEP8/ZCf4BHRgh_EJ">https://groups.google.com/forum/#!msg/atmosphere-framework/8yyOQALZEP8/ZCf4BHRgh_EJ</a>
 * @author Adrien Colson
 */
public class RecoverSecurityContextAtmosphereInterceptor implements AtmosphereInterceptor {

	/** Logger de classe. */
	private transient Logger logger = LoggerFactory.getLogger(RecoverSecurityContextAtmosphereInterceptor.class);

	/**
	 * Initialise les champs transient.
	 * @see java.io.ObjectInputStream#defaultReadObject()
	 * @param inputStream deserializes primitive data and objects previously written using an ObjectOutputStream.
	 * @throws java.io.IOException if an I/O error occurs.
	 * @throws ClassNotFoundException if the class of a serialized object could not be found.
	 */
	private void readObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		inputStream.defaultReadObject();
		logger = LoggerFactory.getLogger(RecoverSecurityContextAtmosphereInterceptor.class);
	}

	/**
	 * @see org.atmosphere.cpr.AtmosphereInterceptor#configure(org.atmosphere.cpr.AtmosphereConfig)
	 */
	@Override
	public void configure(final AtmosphereConfig atmosphereConfig) {
	}

	/**
	 * @see org.atmosphere.cpr.AtmosphereInterceptor#inspect(org.atmosphere.cpr.AtmosphereResource)
	 */
	@Override
	public Action inspect(final AtmosphereResource atmosphereResource) {
		logger.trace("Recover SecurityContext in SecurityContextHolder");
		final SecurityContext context = (SecurityContext) atmosphereResource.getRequest().getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		SecurityContextHolder.setContext(context);

		final Authentication auth = context.getAuthentication();
		if (auth instanceof Authentication) {
			MDC.put(UserMdcServletFilter.USER_KEY, auth.getName());
			logger.trace("Username set in MDC");
		}

		return Action.CONTINUE;
	}

	/**
	 * @see org.atmosphere.cpr.AtmosphereInterceptor#postInspect(org.atmosphere.cpr.AtmosphereResource)
	 */
	@Override
	public void postInspect(final AtmosphereResource atmosphereResource) {
		MDC.remove(UserMdcServletFilter.USER_KEY);
		logger.trace("Username removed from MDC");
	}

	@Override
	public void destroy() {
		MDC.remove(UserMdcServletFilter.USER_KEY);
		logger.trace("Username removed from MDC");
	}

}
