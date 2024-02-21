package fr.univlorraine.ecandidat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.core.Context;

public class LoggerStartupListener extends LevelChangePropagator {

	String[] LOG_MODE_POSSIBLE = new String[] { "traceFull", "trace", "debug", "info" };

	public final static String PROPERTY_FILE_PATH = "config.location";
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

		/* Si un fichier de properties est fourni, on récupère la valeur de logMode */
		final String systemFilePropertiesPath = System.getProperty(PROPERTY_FILE_PATH);
		if (StringUtils.isNotBlank(systemFilePropertiesPath)) {
			try {
				final Properties properties = new Properties();
				final File fileConfig = new File(systemFilePropertiesPath);
				if (fileConfig.exists() && fileConfig.isFile()) {
					try (FileInputStream file = new FileInputStream(fileConfig)) {
						properties.load(file);
						final String logModeProp = properties.getProperty(PROPERTY_LOG_MODE);
						if (isValidLogMode(logModeProp)) {
							logMode = logModeProp;
						}
					}
				}
			} catch (final Exception e) {

			}
		}
		context.putProperty("LOG_MODE", logMode);

		super.start();
	}

	private Boolean isValidLogMode(final String logMode) {
		return logMode != null && Arrays.stream(LOG_MODE_POSSIBLE).anyMatch(logMode::equals);
	}
}