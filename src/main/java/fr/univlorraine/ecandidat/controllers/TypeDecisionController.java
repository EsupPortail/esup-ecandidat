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
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.repositories.CentreCandidatureRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionCandidatureRepository;
import fr.univlorraine.ecandidat.repositories.TypeDecisionRepository;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.ScolTypeDecisionWindow;

/** Gestion de l'entité typeDecision
 * 
 * @author Kevin Hergalant */

@Component
public class TypeDecisionController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient TypeDecisionRepository typeDecisionRepository;
	@Resource
	private transient CentreCandidatureRepository centreCandidatureRepository;
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient TypeDecisionCandidatureRepository typeDecisionCandidatureRepository;

	/** @return le type de decision favorable par defaut */
	public TypeDecision getTypeDecisionFavDefault() {
		return typeDecisionRepository.findByCodTypDec(NomenclatureUtils.TYP_DEC_FAVORABLE);
	}

	/** @return liste des typeDecisions */
	public List<TypeDecision> getTypeDecisions() {
		return typeDecisionRepository.findAll();
	}

	/** @return liste des typeDecisions */
	public List<TypeDecision> getTypeDecisionsEnService() {
		return typeDecisionRepository.findByTesTypDec(true);
	}

	/** @return liste des typeDecisions */
	public List<TypeDecision> getTypeDecisionsFavorableEnService() {
		return typeDecisionRepository.findByTesTypDecAndTypeAvisCodTypAvis(true, NomenclatureUtils.TYP_AVIS_FAV);
	}

	/** Ouvre une fenêtre d'édition d'un nouveau typeDecision. */
	public void editNewTypeDecision() {
		TypeDecision typ = new TypeDecision(userController.getCurrentUserLogin());
		typ.setI18nLibTypDec(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_TYP_DEC_LIB)));
		UI.getCurrent().addWindow(new ScolTypeDecisionWindow(typ));
	}

	/** Ouvre une fenêtre d'édition de typeDecision.
	 * 
	 * @param typeDecision
	 */
	public void editTypeDecision(final TypeDecision typeDecision) {
		Assert.notNull(typeDecision, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(typeDecision, null)) {
			return;
		}
		ScolTypeDecisionWindow window = new ScolTypeDecisionWindow(typeDecision);
		window.addCloseListener(e -> lockController.releaseLock(typeDecision));
		UI.getCurrent().addWindow(window);
	}

	/** Enregistre un typeDecision
	 * 
	 * @param typeDecision
	 */
	public void saveTypeDecision(TypeDecision typeDecision) {
		Assert.notNull(typeDecision, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (typeDecision.getIdTypDec() != null && !lockController.getLockOrNotify(typeDecision, null)) {
			return;
		}
		typeDecision.setUserModTypDec(userController.getCurrentUserLogin());
		typeDecision.setI18nLibTypDec(i18nController.saveI18n(typeDecision.getI18nLibTypDec()));
		typeDecision = typeDecisionRepository.saveAndFlush(typeDecision);

		lockController.releaseLock(typeDecision);
	}

	/** Supprime une typeDecision
	 * 
	 * @param typeDecision
	 */
	public void deleteTypeDecision(final TypeDecision typeDecision) {
		Assert.notNull(typeDecision, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		if (!isAutorizedToDelete(typeDecision)) {
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(typeDecision, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("typeDec.window.confirmDelete", new Object[] {
				typeDecision.getCodTypDec()}, UI.getCurrent().getLocale()), applicationContext.getMessage("typeDec.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(typeDecision, null)) {
				typeDecisionRepository.delete(typeDecision);
				/* Suppression du lock */
				lockController.releaseLock(typeDecision);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(typeDecision);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Verifie qu'on a le droit de supprimer ce type de decision
	 * 
	 * @param typeDecision
	 * @return true si on a le droit de supprimer ce type de decision */
	private Boolean isAutorizedToDelete(final TypeDecision typeDecision) {
		if (centreCandidatureRepository.countByTypeDecisionFav(typeDecision) > 0 || centreCandidatureRepository.countByTypeDecisionFavListComp(typeDecision) > 0) {
			displayMsgErrorUnautorized(CentreCandidature.class.getSimpleName());
			return false;
		}

		if (formationRepository.countByTypeDecisionFav(typeDecision) > 0 || formationRepository.countByTypeDecisionFavListComp(typeDecision) > 0) {
			displayMsgErrorUnautorized(Formation.class.getSimpleName());
			return false;
		}
		if (typeDecisionCandidatureRepository.countByTypeDecision(typeDecision) > 0) {
			displayMsgErrorUnautorized(TypeDecisionCandidature.class.getSimpleName());
			return false;
		}
		return true;
	}

	/** Affiche le message d'erreur
	 * 
	 * @param className
	 */
	private void displayMsgErrorUnautorized(final String className) {
		Notification.show(applicationContext.getMessage("typeDec.error.delete", new Object[] {className}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
	}

	/** Verifie l'unicité du code
	 * 
	 * @param cod
	 * @param id
	 * @return true si le code est unique */
	public Boolean isCodTypeDecUnique(final String cod, final Integer id) {
		TypeDecision typeDecision = typeDecisionRepository.findByCodTypDec(cod);
		if (typeDecision == null) {
			return true;
		} else {
			if (typeDecision.getIdTypDec().equals(id)) {
				return true;
			}
		}
		return false;
	}

	/** Verifie que l'on ne desactive pas le dernier avis d'un type
	 * Exemple : on ne peut pas avoir aucun avis en service FAVORABLE
	 * 
	 * @param typ
	 * @param id
	 * @param tes
	 * @return true si on peut enregistrer */
	public Boolean checkDisableDecision(final String typ, final Integer id, final Boolean tes) {
		if (typ == null) {
			return false;
		}
		/* Si l'avis est actif ou si c'est un nouvel avis, pas de verif */
		if (tes || id == null) {
			return true;
		}
		/* Les types d'avis autre que fav et defav sont possible HS */
		else if (typ.equals(NomenclatureUtils.TYP_AVIS_LISTE_ATTENTE) || typ.equals(NomenclatureUtils.TYP_AVIS_LISTE_COMP) || typ.equals(NomenclatureUtils.TYP_AVIS_PRESELECTION)) {
			return true;
		} else {
			List<TypeDecision> listeAvisDuMemeType = typeDecisionRepository.findByTesTypDecAndTypeAvisCodTypAvis(true, typ);
			if (listeAvisDuMemeType.stream().filter(e -> !e.getIdTypDec().equals(id)).count() == 0) {
				return false;
			}
		}
		return true;
	}
}
