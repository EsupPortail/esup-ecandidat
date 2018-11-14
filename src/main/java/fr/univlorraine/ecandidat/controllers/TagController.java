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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.StyleConstants;
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

	public List<Tag> getTagToCache() {
		return tagRepository.findAll();
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau tag.
	 */
	public void editNewTag() {
		UI.getCurrent().addWindow(new ScolTagWindow(new Tag()));
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
		cacheController.reloadTags();
		lockController.releaseLock(tag);
	}

	/**
	 * Supprime une tag
	 *
	 * @param tag
	 */
	public void deleteTag(final Tag tag) {
		Assert.notNull(tag, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		if (candidatureRepository.countByTag(tag) > 0) {
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
				cacheController.reloadTags();
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

	/**
	 * @return la liste des style css a renvoyer
	 */
	public List<String> getListTagCss() {
		List<String> liste = new ArrayList<>();
		List<Tag> listeTag = cacheController.getTagEnService();
		/* On ajoute les css colorisant les combobox pour les tags */
		listeTag.forEach(e -> {
			liste.add("." + StyleConstants.FILTER_SELECT + " ." + StyleConstants.GWT_MENU + "." + StyleConstants.FILTER_SELECT_ITEM + "-" + StyleConstants.TAG_COMBO_BOX + "-" + e.getIdTag()
					+ StyleConstants.CSS_BEFORE
					+ " { color: " + e.getColorTag() + ";"
					+ " content: '■';"
					+ " display: inline-block;"
					+ " font-size: 30px;"
					+ " margin-top: -5px;"
					+ " margin-right: 2px;"
					+ " text-shadow: #000000 1px 1px, #000000 -1px 1px, #000000 -1px -1px, #000000 1px -1px;"
					+ "}");
		});

		return liste;
	}
}
