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
package fr.univlorraine.tools.vaadin;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * IAnalyticsTracker utilisant un logger.
 * @author Adrien Colson
 */
@SuppressWarnings("serial")
public class LogAnalyticsTracker implements IAnalyticsTracker {

	/**
	 * Logger de classe.
	 */
	private transient Logger logger = LoggerFactory.getLogger(LogAnalyticsTracker.class);

	/**
	 * Initialise les champs transient.
	 * @see java.io.ObjectInputStream#defaultReadObject()
	 * @param inputStream deserializes primitive data and objects previously written using an ObjectOutputStream.
	 * @throws java.io.IOException if an I/O error occurs.
	 * @throws ClassNotFoundException if the class of a serialized object could not be found.
	 */
	private void readObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		inputStream.defaultReadObject();
		logger = LoggerFactory.getLogger(LogAnalyticsTracker.class);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.IAnalyticsTracker#trackPageview(String)
	 */
	@Override
	public void trackPageview(final String name) {
		logger.debug("trackPageView({})", name);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.IAnalyticsTracker#trackEvent(String, String)
	 */
	@Override
	public void trackEvent(final String category, final String action) {
		logger.debug("trackEvent({}, {})", category, action);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.IAnalyticsTracker#trackEvent(String, String, String)
	 */
	@Override
	public void trackEvent(final String category, final String action, final String optLabel) {
		logger.debug("trackEvent({}, {}, {})", category, action, optLabel);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.IAnalyticsTracker#trackEvent(String, String, String, Integer)
	 */
	@Override
	public void trackEvent(final String category, final String action, final String optLabel, final Integer optValue) {
		logger.debug("trackEvent({}, {}, {}, {})", category, action, optLabel, optValue);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.IAnalyticsTracker#trackNavigator(com.vaadin.navigator.Navigator)
	 */
	@Override
	public void trackNavigator(final Navigator navigator) {
		navigator.addViewChangeListener(new ViewChangeListener() {
			/**
			 * @see com.vaadin.navigator.ViewChangeListener#beforeViewChange(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
			 */
			@Override
			public boolean beforeViewChange(final ViewChangeEvent event) {
				return true;
			}

			/**
			 * @see com.vaadin.navigator.ViewChangeListener#afterViewChange(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
			 */
			@Override
			public void afterViewChange(final ViewChangeEvent event) {
				trackPageview(event.getViewName());
			}
		});
	}

}
