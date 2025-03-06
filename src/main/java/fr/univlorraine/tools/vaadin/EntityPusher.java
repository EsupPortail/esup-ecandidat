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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Notifier des événements survenus sur les entités.
 * @author Adrien Colson
 * @param <ENTITY_TYPE> Type de l'entité suivie
 */
public class EntityPusher<ENTITY_TYPE> {

	/** Type de l'entité suivie. */
	private final Class<? super ENTITY_TYPE> entityType;

	/** Thread pool. */
	private transient ExecutorService executorService = Executors.newSingleThreadExecutor();

	/** Listeners. */
	private final List<EntityPushListener<? super ENTITY_TYPE>> entityPushListeners = new LinkedList<>();

	/**
	 * Initialise les champs transient.
	 * @see java.io.ObjectInputStream#defaultReadObject()
	 * @param inputStream deserializes primitive data and objects previously written using an ObjectOutputStream.
	 * @throws java.io.IOException if an I/O error occurs.
	 * @throws ClassNotFoundException if the class of a serialized object could not be found.
	 */
	private void readObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		inputStream.defaultReadObject();
		executorService = Executors.newSingleThreadExecutor();
	}

	/**
	 * Constructeur.
	 * @param entityTypeSet type de l'entité suivie
	 */
	public EntityPusher(final Class<? super ENTITY_TYPE> entityTypeSet) {
		this.entityType = entityTypeSet;
	}

	/**
	 * Accède au type de l'entité suivie.
	 * @return type de l'entité suivie
	 */
	public Class<? super ENTITY_TYPE> getEntityType() {
		return entityType;
	}

	/**
	 * Enregistre un listener auprès du notifier.
	 * @param entityPushListener listener concerné
	 */
	public void registerEntityPushListener(final EntityPushListener<? super ENTITY_TYPE> entityPushListener) {
		synchronized (this) {
			entityPushListeners.add(entityPushListener);
		}
	}

	/**
	 * @return the entityPushListeners
	 */
	public List<EntityPushListener<? super ENTITY_TYPE>> getEntityPushListeners() {
		return entityPushListeners;
	}

	/**
	 * Désenregistre un listener auprès du notifier.
	 * @param entityPushListener listener concerné
	 */
	public void unregisterEntityPushListener(final EntityPushListener<? super ENTITY_TYPE> entityPushListener) {
		synchronized (this) {
			entityPushListeners.remove(entityPushListener);
		}
	}

	/**
	 * Evénements pouvant arriver aux entités.
	 * @author Adrien Colson
	 */
	public enum EntityAction { PERSISTED, UPDATED, REMOVED }

	/**
	 * Notifie tous les listeners enregistrés.
	 * @param entityAction événement à notifier
	 * @param entity entité concernée
	 */
	public void notifyAll(final EntityAction entityAction, final ENTITY_TYPE entity) {
		synchronized (this) {
			entityPushListeners
				.stream().filter(entityPushListener -> entityPushListener.getUI() instanceof UI && entityPushListener.getUI().isAttached())
				.forEach(entityPushListener ->
					executorService.execute(() -> {
						final UI pusherUI = entityPushListener.getUI();
						if (pusherUI instanceof UI) {
							pusherUI.access(() -> {
								switch (entityAction) {
								case PERSISTED:
									entityPushListener.entityPersisted(entity);
									break;
								case UPDATED:
									entityPushListener.entityUpdated(entity);
									break;
								case REMOVED:
									entityPushListener.entityDeleted(entity);
									break;
								default:
								}
							});
						}
					})
				);
		}
	}

}
