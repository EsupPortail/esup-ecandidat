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
package fr.univlorraine.tools.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.net.SMTPAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.helpers.CyclicBuffer;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Matthieu Manginot
 * @author Adrien Colson
 */
public class TimeWindowSMTPAppender extends SMTPAppender {
	/** Time Window Duration in seconds. */
	private int timeWindowDurationSeconds = 1;
	/** Limite de mail à envoyer. */
	private int maxMessagesPerTimeWindow = 1;
	/** Compteur de mail envoyé. */
	private static int messagesCounter;
	/** Date de départ de la TimeWindow. */
	private static Date currentStartTimeWindow = new Date();
	/** Niveau de log traité par l'appender. */
	private Level logLevel = Level.ERROR;
	/** Niveau de log traité par le CyclicBuffer. */
	private Level cyclicBufferLogLevel = Level.TRACE;

	/**
	 * @see ch.qos.logback.core.net.SMTPAppenderBase#append(Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected final void append(final ILoggingEvent eventObject) {
		/* Si le niveau de log du message est supérieur ou égal
		 * au niveau de log positionné pour l'appender
		 * et si le max n'est pas atteint */
		if (eventObject.getLevel().isGreaterOrEqual(logLevel) && isForLogged()) {
			/* Log le message normalement */
			super.append(eventObject);
		} else if (eventObject.getLevel().isGreaterOrEqual(cyclicBufferLogLevel) && cbTracker != null) {
			/* Sinon simple ajout du message au cyclicBuffer si le niveau du message est supérieur ou égal
			 * au niveau de log positionné pour le cyclicBuffer */
			final String key = this.discriminator.getDiscriminatingValue(eventObject);
			final long now = System.currentTimeMillis();
			@SuppressWarnings("rawtypes")
			final CyclicBuffer cb = cbTracker.getOrCreate(key, now);
			subAppend(cb, eventObject);
		}
	}

	/**
	 * Retourne vrai si le message courant doit être loggé.
	 * @return boolean
	 */
	private boolean isForLogged() {
		synchronized (this) {
			boolean isForLogged = false;

			/* Calcul de la date de fin de la Time Window */
			final Calendar currentEndTimeWindow = Calendar.getInstance();
			currentEndTimeWindow.setTime(currentStartTimeWindow);
			currentEndTimeWindow.add(Calendar.SECOND, timeWindowDurationSeconds);
			/* Date courante */
			final Date currentTime = new Date();

			/* Si compteur inférieur à la limite et TimeWindow encore ouverte */
			if (messagesCounter < maxMessagesPerTimeWindow && currentTime.before(currentEndTimeWindow.getTime())) {
				isForLogged = true;
			} else if (currentTime.after(currentEndTimeWindow.getTime())) {
				/* Sinon si la TimeWindow est terminée */
				messagesCounter = 0;
				currentStartTimeWindow = new Date();
				isForLogged = true;
			} else {
				/* Sinon limite atteinte sur la TimeWindow courante */
				isForLogged = false;
			}
			/* Incrémentation du compteur de mail */
			messagesCounter++;

			return isForLogged;
		}
	}

	/**
	 * @return the timeWindowDurationSeconds
	 */
	public int getTimeWindowDurationSeconds() {
		return timeWindowDurationSeconds;
	}

	/**
	 * Défini la durée de la TimeWindow.
	 * @param timeWindowDurationSecondsSet durée en secondes
	 */
	public void setTimeWindowDurationSeconds(final int timeWindowDurationSecondsSet) {
		synchronized (this) {
			this.timeWindowDurationSeconds = timeWindowDurationSecondsSet;
		}
	}

	/**
	 * @return the maxMessagesPerTimeWindow
	 */
	public int getMaxMessagesPerTimeWindow() {
		return maxMessagesPerTimeWindow;
	}

	/**
	 * Défini le nombre maximum de messages par TimeWindow.
	 * @param maxMessagesPerTimeWindowSet nombre maximum de messages
	 */
	public void setMaxMessagesPerTimeWindow(final int maxMessagesPerTimeWindowSet) {
		synchronized (this) {
			this.maxMessagesPerTimeWindow = maxMessagesPerTimeWindowSet;
		}
	}

	/**
	 * @return the logLevel
	 */
	public Level getLogLevel() {
		return logLevel;
	}

	/**
	 * Défini le niveau de log.
	 * @param logLevelSet niveau de log
	 */
	public void setLogLevel(final String logLevelSet) {
		logLevel = Level.toLevel(logLevelSet);
	}

	/**
	 * @return the cyclicBufferLogLevel
	 */
	public Level getCyclicBufferLogLevel() {
		return cyclicBufferLogLevel;
	}

	/**
	 * Défini le niveau de log du buffer.
	 * @param cyclicBufferLogLevelSet niveau de log
	 */
	public void setCyclicBufferLogLevel(final String cyclicBufferLogLevelSet) {
		cyclicBufferLogLevel = Level.toLevel(cyclicBufferLogLevelSet);
	}
}
