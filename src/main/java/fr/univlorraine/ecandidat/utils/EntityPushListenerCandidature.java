package fr.univlorraine.ecandidat.utils;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;

public interface EntityPushListenerCandidature {
	/**
	 * AccÃ¨de Ã  l'UI concernÃ©e.
	 * @return UI concernÃ©e
	 */
	UI getUI();

	Integer getIdCommission();

	/**
	 * L'entitÃ© a Ã©tÃ© insÃ©rÃ©e en base.
	 * @param entity entitÃ© concernÃ©e
	 */
	void entityPersisted(Candidature entity);

	/**
	 * L'entitÃ© a Ã©tÃ© mise Ã  jour en base.
	 * @param entity entitÃ© concernÃ©e
	 */
	void entityUpdated(Candidature entity);

	/**
	 * L'entitÃ© a Ã©tÃ© supprimÃ©e de la base.
	 * @param entity entitÃ© concernÃ©e
	 */
	void entityDeleted(Candidature entity);
}
