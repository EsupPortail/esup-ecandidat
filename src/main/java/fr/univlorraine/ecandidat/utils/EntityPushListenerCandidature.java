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

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;

public interface EntityPushListenerCandidature {
	/**
	 * Accède à  l'UI concernée.
	 * @return UI concernée
	 */
	UI getUI();

	Integer getIdCommission();

	/**
	 * L'entité a été insérée en base.
	 * @param entity entité concernée
	 */
	void entityPersisted(Candidature entity);

	/**
	 * L'entité a été mise à  jour en base.
	 * @param entity entité concernée
	 */
	void entityUpdated(Candidature entity);

	/**
	 * L'entité a été supprimée de la base.
	 * @param entity entité concernée
	 */
	void entityDeleted(Candidature entity);
}
