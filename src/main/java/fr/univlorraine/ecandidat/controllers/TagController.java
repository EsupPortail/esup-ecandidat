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
package fr.univlorraine.ecandidat.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.repositories.CandidatureRepository;
import fr.univlorraine.ecandidat.repositories.TagRepository;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.ScolTagWindow;

/**
 * Gestion de l'entité Tag
 *
 * @author Kevin Hergalant
 */
@Component
public class TagController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient TagRepository tagRepository;
	@Resource
	private transient CandidatureRepository candidatureRepository;

	/**
	 * @return liste des motivationAvis
	 */
	public List<Tag> getTagEnServiceByCtrCand(final CentreCandidature ctrCand) {
		// tags de la scol centrale
		List<Tag> liste = tagRepository.findByTesTagAndCentreCandidatureIdCtrCand(true, null);
		// tags pour les ctrCand
		if (ctrCand != null) {
			liste.addAll(tagRepository.findByTesTagAndCentreCandidatureIdCtrCand(true, ctrCand.getIdCtrCand()));
		}
		liste.sort((h1, h2) -> h1.getLibTag().compareTo(h2.getLibTag()));
		return liste;
	}

	/**
	 * @param idCtrCand
	 * @return la liste des Tags par centre de candidature
	 */
	public List<Tag> getTagsByCtrCand(
			final Integer idCtrCand) {
		return tagRepository.findByCentreCandidatureIdCtrCand(idCtrCand);
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau tag.
	 */
	public void editNewTag(final CentreCandidature ctrCand) {
		Tag tag = new Tag();
		tag.setTesTag(true);
		tag.setCentreCandidature(ctrCand);
		UI.getCurrent().addWindow(new ScolTagWindow(tag));
	}

	/**
	 * Ouvre une fenêtre d'édition de tag.
	 *
	 * @param tag
	 */
	public void editTag(final Tag tag) {
		Assert.notNull(tag, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(tag, null)) {
			return;
		}
		ScolTagWindow window = new ScolTagWindow(tag);
		window.addCloseListener(e -> lockController.releaseLock(tag));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un tag
	 *
	 * @param tag
	 */
	public void saveTag(Tag tag) {
		Assert.notNull(tag, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		/* Verrou */
		if (tag.getIdTag() != null && !lockController.getLockOrNotify(tag, null)) {
			return;
		}
		tag = tagRepository.saveAndFlush(tag);
		lockController.releaseLock(tag);
	}

	/**
	 * Supprime une tag
	 *
	 * @param tag
	 */
	public void deleteTag(final Tag tag) {
		Assert.notNull(tag, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		/* Vérification que le tag n'est pas utilisé */
		if (candidatureRepository.countByTags(tag) > 0) {
			Notification.show(applicationContext.getMessage("tag.error.delete", new Object[] {TypeDecisionCandidature.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(tag, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("tag.window.confirmDelete", new Object[] {tag.getLibTag()}, UI.getCurrent().getLocale()),
				applicationContext.getMessage("tag.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(tag, null)) {
				tagRepository.delete(tag);
				/* Suppression du lock */
				lockController.releaseLock(tag);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(tag);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
}
