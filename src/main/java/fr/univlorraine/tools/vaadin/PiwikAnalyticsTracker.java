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

import com.vaadin.annotations.JavaScript;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.UI;
import elemental.json.Json;
import elemental.json.JsonArray;

/**
 * Extension permettant d'intégrer Piwik.
 * @author Charlie Dubois
 * @author Adrien Colson
 */
@JavaScript("pwk_connector.js")
@SuppressWarnings("serial")
public class PiwikAnalyticsTracker extends AbstractJavaScriptExtension implements IAnalyticsTracker {

	/**
	 * Initialise l'extension.
	 * @param targetUi UI à laquelle est associée l'extension
	 * @param trackerUrl url du tracker Piwik
	 * @param siteId id du site Piwik
	 */
	public PiwikAnalyticsTracker(final UI targetUi, final String trackerUrl, final String siteId) {
		super();
		super.extend(targetUi);
		callFunction("setPiwikAccountCommand", trackerUrl, siteId);
	}

	/**
	 * Appelle une fonction Piwik.
	 * @param commandAndArguments nom de la commande suivi de ses arguments
	 */
	private void pushCommand(final Object... commandAndArguments) {
		final JsonArray ja = Json.createArray();
		for (int i = 0; i < commandAndArguments.length; i++) {
			ja.set(i, String.valueOf(commandAndArguments[i]));
		}
		callFunction("pushCommand", ja);
	}

	/**
	 * @see IAnalyticsTracker#trackPageview(String)
	 */
	@Override
	public void trackPageview(final String name) {
		pushCommand("trackPageView", name);
	}

	/**
	 * @see IAnalyticsTracker#trackEvent(String, String)
	 */
	@Override
	public void trackEvent(final String category, final String action) {
		pushCommand("trackEvent", category, action);
	}

	/**
	 * @see IAnalyticsTracker#trackEvent(String, String, String)
	 */
	@Override
	public void trackEvent(final String category, final String action, final String optLabel) {
		pushCommand("trackEvent", category, action, optLabel);
	}

	/**
	 * @see IAnalyticsTracker#trackEvent(String, String, String, Integer)
	 */
	@Override
	public void trackEvent(final String category, final String action, final String optLabel, final Integer optValue) {
		pushCommand("trackEvent", category, action, optLabel, optValue);
	}

	/**
	 * @see IAnalyticsTracker#trackNavigator(com.vaadin.navigator.Navigator)
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
