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
package fr.univlorraine.ecandidat.entities.tools;

import javax.annotation.Resource;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import fr.univlorraine.tools.vaadin.EntityPusher;
import fr.univlorraine.tools.vaadin.EntityPusher.EntityAction;

/**
 * Appelle les méthodes des EntityPusher correspondants lors de l'insertion, la modification ou la suppression d'une entité.
 * @author Adrien Colson
 */
@Configurable
public class EntityPushEntityListener {

	@Resource
	private transient ApplicationContext applicationContext;

	@PostPersist
	public void postPersist(Object entity) {
		notifyEntityPushers(EntityAction.PERSISTED, entity);
	}

	@PostUpdate
	public void postUpdate(Object entity) {
		notifyEntityPushers(EntityAction.UPDATED, entity);
	}

	@PostRemove
	public void postRemove(Object entity) {
		notifyEntityPushers(EntityAction.REMOVED, entity);
	}

	@SuppressWarnings("unchecked")
	private void notifyEntityPushers(EntityAction entityAction, Object entity) {
		if (applicationContext==null){
			return;
		}
		applicationContext.getBeansOfType(EntityPusher.class).values()
			.stream().filter(entityPusher -> entityPusher.getEntityType().isInstance(entity))
			.forEach(entityPusher -> entityPusher.notifyAll(entityAction, entity));
	}

}
