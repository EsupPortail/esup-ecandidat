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

import jakarta.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.repositories.LangueRepository;
import fr.univlorraine.ecandidat.views.windows.AdminLangueWindow;

/**
 * Gestion de l'entité langue
 * @author Kevin Hergalant
 *
 */
@Component
public class LangueController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient LangueRepository langueRepository;
	@Resource
	private transient CacheController cacheController;
	
	/**
	 * @return liste des langues
	 */
	public List<Langue> getLangues() {
		return langueRepository.findAll();
	}
	
	/**
	 * @return la liste des langues actives
	 */
	public List<Langue> getLanguesActivesWithoutDefaultToCache(){
		return langueRepository.findByTemDefautLangueAndTesLangue(false,true);
	}
	
	/**
	 * @return la langue par defaut
	 */
	public Langue getLangueDefaultToCache() {
		return langueRepository.findByTemDefautLangue(true);
	}

	/**
	 * Ouvre une fenêtre d'édition de langue.
	 * @param langue
	 */
	public void editLangue(Langue langue) {
		Assert.notNull(langue, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(langue, null)) {
			return;
		}

		AdminLangueWindow window = new AdminLangueWindow(langue);
		window.addCloseListener(e->lockController.releaseLock(langue));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un langue
	 * @param langue
	 */
	public void saveLangue(Langue langue) {
		Assert.notNull(langue, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(langue, null)) {
			return;
		}

		langueRepository.saveAndFlush(langue);
		cacheController.reloadLangues(true);
		lockController.releaseLock(langue);
	}


}
