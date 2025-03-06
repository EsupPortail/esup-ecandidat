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
package fr.univlorraine.tools.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.util.Enumeration;

/**
 * This ServletContextListener adds context init parameters to java system properties with prefix 'context.'.
 * @author Adrien Colson
 */
public class ContextParametersToSystemPropertiesListener implements ServletContextListener {

	/** 'context.' prefix. */
	private static final String CONTEXT_PREFIX = "context.";

	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		final ServletContext context = servletContextEvent.getServletContext();

		final Enumeration<String> parameterNamesEnum = context.getInitParameterNames();

		while (parameterNamesEnum.hasMoreElements()) {
			final String parameterName = parameterNamesEnum.nextElement();
			System.setProperty(CONTEXT_PREFIX + parameterName, context.getInitParameter(parameterName));
		}
	}

	@Override
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
	}

}
