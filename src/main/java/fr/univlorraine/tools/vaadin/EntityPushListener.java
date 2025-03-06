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

import com.vaadin.ui.UI;

/**
 * Listener des événements sur les entités.
 * @author Adrien Colson
 * @param <ENTITY_TYPE> Type de l'entité suivie
 */
public interface EntityPushListener<ENTITY_TYPE> {

	/**
	 * Accède à l'UI concernée.
	 * @return UI concernée
	 */
	UI getUI();

	/**
	 * L'entité a été insérée en base.
	 * @param entity entité concernée
	 */
	void entityPersisted(ENTITY_TYPE entity);

	/**
	 * L'entité a été mise à jour en base.
	 * @param entity entité concernée
	 */
	void entityUpdated(ENTITY_TYPE entity);

	/**
	 * L'entité a été supprimée de la base.
	 * @param entity entité concernée
	 */
	void entityDeleted(ENTITY_TYPE entity);

}
