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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

/**
 * Désactive le log des événements dans une fourchette d'heure.
 * @author Adrien Colson
 */
public class TimeFilter extends Filter<ILoggingEvent> {

	/** Heure de début de la désactivation. */
	private transient LocalTime startDisabling;

	/** Heure de fin de la désactivation. */
	private transient LocalTime stopDisabling;

	/**
	 * @see ch.qos.logback.core.filter.Filter#decide(Object)
	 */
	@Override
	public FilterReply decide(final ILoggingEvent event) {
		final Instant eventInstant = Instant.ofEpochMilli(event.getTimeStamp());
		final LocalTime eventTime = LocalTime.from(eventInstant.atZone(ZoneId.systemDefault()));

		if (startDisabling instanceof LocalTime && eventTime.isBefore(startDisabling)) {
			return FilterReply.NEUTRAL;
		}

		if (stopDisabling instanceof LocalTime && eventTime.isAfter(stopDisabling)) {
			return FilterReply.NEUTRAL;
		}

		return FilterReply.DENY;
	}

	/**
	 * @param startDisablingSet heure de début de la désactivation
	 */
	public void setStartDisabling(final String startDisablingSet) {
		startDisabling = LocalTime.parse(startDisablingSet);
	}

	/**
	 * @param stopDisablingSet heure de fin de la désactivation
	 */
	public void setStopDisabling(final String stopDisablingSet) {
		stopDisabling = LocalTime.parse(stopDisablingSet);
	}

}
