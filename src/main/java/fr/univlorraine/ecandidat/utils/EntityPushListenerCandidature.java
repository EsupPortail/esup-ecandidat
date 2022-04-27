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
