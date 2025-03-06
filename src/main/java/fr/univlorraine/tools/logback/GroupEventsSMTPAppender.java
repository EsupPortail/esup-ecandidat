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

import ch.qos.logback.classic.net.SMTPAppender;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.helpers.CyclicBuffer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Appender qui regroupe les événements à mailer, dans une limite de temps et de nombre d'événements.
 * @author Adrien Colson
 */
public class GroupEventsSMTPAppender extends SMTPAppender {

	/** Message indiquant une demande d'envoi de mail. */
	public static final String SEND_MAIL_MESSAGE = "sendMail!";

	/** Délai des mail en secondes. */
	private int mailDelaySeconds = 1;

	/** Timer entre chaque envoi de mails. */
	private transient Timer timer = new Timer();

	/** Tâche timer courante. */
	private transient TimerTask currentTimerTask;

	/** Dernier événement de log. */
	private transient ILoggingEvent lastEventObject;

	/**
	 * Initialise les champs transient.
	 * @see java.io.ObjectInputStream#defaultReadObject()
	 * @param inputStream deserializes primitive data and objects previously written using an ObjectOutputStream.
	 * @throws java.io.IOException if an I/O error occurs.
	 * @throws ClassNotFoundException if the class of a serialized object could not be found.
	 */
	private void readObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		inputStream.defaultReadObject();
		timer = new Timer();
	}

	/**
	 * @return the mailDelaySeconds
	 */
	public int getMailDelaySeconds() {
		return mailDelaySeconds;
	}

	/**
	 * Défini le délai d'envoi de mail.
	 * @param mailDelaySecondsSet délai en secondes
	 */
	public void setMailDelaySeconds(final int mailDelaySecondsSet) {
		synchronized (this) {
			mailDelaySeconds = mailDelaySecondsSet;
		}
	}

	/**
	 * @see ch.qos.logback.core.net.SMTPAppenderBase#append(Object)
	 */
	@Override
	protected final void append(final ILoggingEvent eventObject) {
		/* Vérifie si l'événement est une demande d'envoi de mail */
		if (SEND_MAIL_MESSAGE.equals(eventObject.getMessage())) {
			sendMail();
			return;
		}

		/* Vérifie si l'événement est à traiter */
		boolean isEventToProcess;
		try {
			isEventToProcess = eventEvaluator.evaluate(eventObject);
		} catch (final EvaluationException e) {
			isEventToProcess = false;
		}

		if (isEventToProcess) {
			if (cbTracker == null) {
				/* S'il n'y a pas de CyclicBuffer on envoie un mail */
				super.append(eventObject);
			} else {
				/* Sinon on délaie l'envoi de mail */
				processEvent(eventObject);
			}
		}
	}

	/**
	 * Délaie l'envoi d'email.
	 * @param eventObject événement traité
	 */
	private void processEvent(final ILoggingEvent eventObject) {
		synchronized (this) {
			final String key = discriminator.getDiscriminatingValue(eventObject);
			final CyclicBuffer<ILoggingEvent> cb = cbTracker.getOrCreate(key, System.currentTimeMillis());

			/* S'il y avait déjà un dernier événement, on le place dans le CycliBuffer */
			if (lastEventObject != null) {
				subAppend(cb, lastEventObject);
			}
			lastEventObject = eventObject;

			if (cb.length() >= cb.getMaxSize()) {
				/* Si le CyclicBuffer a atteint sa capacité maximale, on annule le timer et on envoie le mail */
				if (currentTimerTask instanceof TimerTask) {
					currentTimerTask.cancel();
					currentTimerTask = null;
				}
				sendMail();
			} else if (currentTimerTask == null) {
				/* Sinon si un timer n'est pas programmé, on en programme un */
				currentTimerTask = new TimerTask() {
					/**
					 * @see java.util.TimerTask#run()
					 */
					@Override
					public void run() {
						sendMail();
						currentTimerTask = null;
					}
				};
				timer.schedule(currentTimerTask, TimeUnit.MILLISECONDS.convert(mailDelaySeconds, TimeUnit.SECONDS));
			}
		}
	}

	/**
	 * Envoie le mail.
	 */
	private void sendMail() {
		if (lastEventObject != null) {
			super.append(lastEventObject);
			lastEventObject = null;
		}
	}

	/**
	 * @see ch.qos.logback.core.net.SMTPAppenderBase#stop()
	 */
	@Override
	public void stop() {
		synchronized (this) {
			sendMail();
			super.stop();
		}
	}

}
