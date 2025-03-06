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
 * Extension permettant d'intégrer Google Analytics.
 * @author Adrien Colson
 */
@JavaScript("ga_connector.js")
@SuppressWarnings("serial")
public class GoogleAnalyticsTracker extends AbstractJavaScriptExtension implements IAnalyticsTracker {

	private static final String TRACKEVENT_COMMAND = "_trackEvent";

	/**
	 * Constructeur.
	 * @param targetUi UI à laquelle est associée l'extension
	 * @param accountId identifiant du compte Google Analytics
	 */
	public GoogleAnalyticsTracker(final UI targetUi, final String accountId) {
		super();
		super.extend(targetUi);
		pushCommand("_setAccount", accountId);
	}

	/**
	 * Appelle une fonction Google Analytics.
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
	 * Précise le domaine du tracker Google Analytics.
	 * @param domainName le domaine du tracker
	 */
	public void setDomainName(final String domainName) {
		pushCommand("_setDomainName", domainName);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.IAnalyticsTracker#trackPageview(String)
	 */
	@Override
	public void trackPageview(final String name) {
		pushCommand("_trackPageview", name);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.IAnalyticsTracker#trackEvent(String, String)
	 */
	@Override
	public void trackEvent(final String category, final String action) {
		pushCommand(TRACKEVENT_COMMAND, category, action);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.IAnalyticsTracker#trackEvent(String, String, String)
	 */
	@Override
	public void trackEvent(final String category, final String action, final String optLabel) {
		pushCommand(TRACKEVENT_COMMAND, category, action, optLabel);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.IAnalyticsTracker#trackEvent(String, String, String, Integer)
	 */
	@Override
	public void trackEvent(final String category, final String action, final String optLabel, final Integer optValue) {
		pushCommand(TRACKEVENT_COMMAND, category, action, optLabel, optValue);
	}

	/**
	 * Suis un événement.
	 * @param category catégorie de l'événement suivi
	 * @param action nom de l'événement suivi
	 * @param optLabel description de l'événement suivi
	 * @param optValue une valeur associée à l'événement
	 * @param optNoninteraction false par défaut. Lorsque sa valeur est true, l'événement suivi n'entre pas en compte dans le calcul du taux de fréquentation d'un visiteur.
	 */
	public void trackEvent(final String category, final String action, final String optLabel, final Integer optValue, final Boolean optNoninteraction) {
		pushCommand(TRACKEVENT_COMMAND, category, action, optLabel, optValue, optNoninteraction);
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
