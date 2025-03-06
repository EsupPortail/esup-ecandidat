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

import java.io.Serializable;

/**
 * Interface d'observeurs d'événements permettant de tenir des statistiques de fréquentation.
 * @author Adrien Colson
 */
public interface IAnalyticsTracker extends Serializable {

	/**
	 * Suis l'affichage d'une vue.
	 * @param name le nom de la vue affichée
	 */
	void trackPageview(String name);

	/**
	 * Suis un événement.
	 * @param category catégorie de l'événement suivi
	 * @param action nom de l'événement suivi
	 */
	void trackEvent(String category, String action);

	/**
	 * Suis un événement.
	 * @param category catégorie de l'événement suivi
	 * @param action nom de l'événement suivi
	 * @param optLabel description de l'événement suivi
	 */
	void trackEvent(String category, String action, String optLabel);

	/**
	 * Suis un événement.
	 * @param category catégorie de l'événement suivi
	 * @param action nom de l'événement suivi
	 * @param optLabel description de l'événement suivi
	 * @param optValue une valeur associée à l'événement
	 */
	void trackEvent(String category, String action, String optLabel, Integer optValue);

	/**
	 * Active le suivi lors de changements de vues sur un navigator.
	 * @param navigator navigator à suivre
	 */
	void trackNavigator(Navigator navigator);

}
