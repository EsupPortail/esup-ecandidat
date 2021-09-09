package fr.univlorraine.ecandidat.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.tools.vaadin.EntityPusher.EntityAction;

public class EntityPusherCandidature {

	/** Thread pool. */
	private transient ExecutorService executorService = Executors.newSingleThreadExecutor();

	/** Listeners. */
	private final List<EntityPushListenerCandidature> entityPushListeners = new LinkedList<>();

	/**
	 * Initialise les champs transient.
	 * @see                           java.io.ObjectInputStream#defaultReadObject()
	 * @param  inputStream            deserializes primitive data and objects previously written using an ObjectOutputStream.
	 * @throws IOException            if an I/O error occurs.
	 * @throws ClassNotFoundException if the class of a serialized object could not be found.
	 */
	private void readObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		inputStream.defaultReadObject();
		executorService = Executors.newSingleThreadExecutor();
	}

	/**
	 * Constructeur.
	 * @param entityTypeSet type de l'entitÃ© suivie
	 */
	public EntityPusherCandidature() {
	}

	/**
	 * Enregistre un listener auprÃ¨s du notifier.
	 * @param entityPushListener listener concernÃ©
	 */
	public void registerEntityPushListener(final EntityPushListenerCandidature entityPushListener) {
		synchronized (this) {
			entityPushListeners.add(entityPushListener);
		}
	}

	/**
	 * @return the entityPushListeners
	 */
	public List<EntityPushListenerCandidature> getEntityPushListeners() {
		return entityPushListeners;
	}

	/**
	 * DÃ©senregistre un listener auprÃ¨s du notifier.
	 * @param entityPushListener listener concernÃ©
	 */
	public void unregisterEntityPushListener(final EntityPushListenerCandidature entityPushListener) {
		synchronized (this) {
			entityPushListeners.remove(entityPushListener);
		}
	}

	/**
	 * Notifie tous les listeners enregistrÃ©s.
	 * @param entityAction Ã©vÃ©nement Ã  notifier
	 * @param entity       entitÃ© concernÃ©e
	 */
	public void notifyAll(final EntityAction entityAction, final Candidature entity) {
		synchronized (this) {
			entityPushListeners
				.stream()
				.filter(entityPushListener -> entityPushListener.getUI() instanceof UI && entityPushListener.getUI().isAttached() && entityPushListener.getIdCommission().equals(entity.getFormation().getCommission().getIdComm()))
				.forEach(entityPushListener -> executorService.execute(() -> {
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
				}));
		}
	}
}
