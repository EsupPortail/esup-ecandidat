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
package fr.univlorraine.ecandidat.utils;

import java.util.Arrays;
import java.util.Properties;

import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.core.Context;

public class LoggerStartupListener extends LevelChangePropagator {

	String[] LOG_MODE_POSSIBLE = new String[] { "traceFull", "trace", "debug", "info" };

	private static final String PROPERTY_LOG_MODE = "logMode";
	private static final String DEFAULT_LOG_MODE = "info";

	String contextLogMode = null;

	public void setContextLogMode(final String contextLogMode) {
		this.contextLogMode = contextLogMode;
	}

	@Override
	public void start() {
		if (super.isStarted())
			return;

		final Context context = getContext();
		/* Definition du logMode */
		String logMode = DEFAULT_LOG_MODE;

		/* Si présent dans les variables de context on l'applique */
		if (isValidLogMode(contextLogMode)) {
			logMode = contextLogMode;
		}

		System.out.println("LoggerStartupListener  contextLogMode: " + contextLogMode);
		System.out.println("LoggerStartupListener  logMode: " + logMode);

		/* Si un fichier de properties est fourni, on récupère la valeur de logMode */
		final Properties properties = MethodUtils.loadPropertieFile();
		final String logModeProp = properties.getProperty(PROPERTY_LOG_MODE);

		System.out.println("LoggerStartupListener  properties: " + properties);
		System.out.println("LoggerStartupListener  logModeProp: " + logModeProp);

		if (isValidLogMode(logModeProp)) {
			logMode = logModeProp;
		}
		context.putProperty("LOG_MODE", logMode);
		System.out.println("LoggerStartupListener  logMode: " + logMode);

		super.start();
	}

	private Boolean isValidLogMode(final String logMode) {
		return logMode != null && Arrays.stream(LOG_MODE_POSSIBLE).anyMatch(logMode::equals);
	}
}