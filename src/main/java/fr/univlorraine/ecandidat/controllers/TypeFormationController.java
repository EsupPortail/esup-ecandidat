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

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeFormation;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.repositories.TypeFormationRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.ScolTypeFormationWindow;
import jakarta.annotation.Resource;

/**
 * Gestion de l'entité alertes SVA
 * @author Kevin Hergalant
 */
@Component
public class TypeFormationController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient TypeFormationRepository typeFormationRepository;
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient ParametreController parametreController;

	public List<TypeFormation> getTypeFormation() {
		return typeFormationRepository.findAll();
	}

	/**
	 * @return liste des typeFormation
	 */
	public List<TypeFormation> getTypeFormationEnService() {
		return typeFormationRepository.findByTesTypeForm(true);
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau typeFormation.
	 */
	public void editNewTypeFormation() {
		UI.getCurrent().addWindow(new ScolTypeFormationWindow(new TypeFormation(userController.getCurrentUserLogin())));
	}

	/**
	 * Ouvre une fenêtre d'édition de typeFormation.
	 * @param typeFormation
	 */
	public void editTypeFormation(final TypeFormation typeFormation) {
		Assert.notNull(typeFormation, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(typeFormation, null)) {
			return;
		}
		final ScolTypeFormationWindow window = new ScolTypeFormationWindow(typeFormation);
		window.addCloseListener(e -> lockController.releaseLock(typeFormation));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un typeFormation
	 * @param typeFormation
	 */
	public void saveTypeFormation(TypeFormation typeFormation) {
		Assert.notNull(typeFormation, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		/* Verrou */
		if (typeFormation.getIdTypeForm() != null && !lockController.getLockOrNotify(typeFormation, null)) {
			return;
		}
		typeFormation.setUserModTypeForm(userController.getCurrentUserLogin());

		typeFormation = typeFormationRepository.saveAndFlush(typeFormation);
		cacheController.reloadListeTypeFormation(true);
		/* Besoin de recharger l'offre de formation si enregsitrement */
		final String modeTypForm = parametreController.getModeTypeFormation();
		if (ConstanteUtils.PARAM_MODE_TYPE_FORMATION_NOMENCLATURE.equals(modeTypForm)) {
			cacheController.reloadOdf(true);
		}

		lockController.releaseLock(typeFormation);
	}

	/**
	 * Supprime une typeFormation
	 * @param typeFormation
	 */
	public void deleteTypeFormation(final TypeFormation typeFormation) {
		Assert.notNull(typeFormation, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		if (formationRepository.countByTypeFormation(typeFormation) > 0) {
			Notification.show(applicationContext.getMessage("typeForm.error.delete", new Object[] { TypeDecisionCandidature.class.getSimpleName() }, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(typeFormation, null)) {
			return;
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("typeForm.window.confirmDelete", new Object[] { typeFormation.getLibTypeForm() }, UI.getCurrent().getLocale()),
			applicationContext.getMessage("typeForm.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(typeFormation, null)) {
				typeFormationRepository.delete(typeFormation);
				cacheController.reloadListeTypeFormation(true);
				/* Suppression du lock */
				lockController.releaseLock(typeFormation);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(typeFormation);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Verifie l'unicité du code
	 * @param  cod
	 * @param  id
	 * @return     true si le code est unique
	 */
	public Boolean isCodTypFormUnique(final String cod, final Integer id) {
		final TypeFormation typeFormation = typeFormationRepository.findByCodTypeForm(cod);
		if (typeFormation == null) {
			return true;
		} else {
			if (typeFormation.getIdTypeForm().equals(id)) {
				return true;
			}
		}
		return false;
	}
}
