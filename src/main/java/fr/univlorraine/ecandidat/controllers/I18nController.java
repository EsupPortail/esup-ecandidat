/** ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package fr.univlorraine.ecandidat.controllers;

import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.I18nTraduction;
import fr.univlorraine.ecandidat.entities.ecandidat.I18nTraductionPK;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraduction;
import fr.univlorraine.ecandidat.repositories.I18nRepository;
import fr.univlorraine.ecandidat.repositories.TypeTraductionRepository;
import fr.univlorraine.ecandidat.services.security.SecurityUserCandidat;

/** Gestion de tout ce qui est internationalisation
 *
 * @author Kevin Hergalant */
@Component
public class I18nController {
	/* Injections */
	@Resource
	private transient I18nRepository i18nRepository;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient TypeTraductionRepository typeTraductionRepository;

	/** Enregistre une entité i18n
	 *
	 * @param i18nMaybeIncomplet
	 * @return l'entite I18n créé */
	public I18n saveI18n(final I18n i18nMaybeIncomplet) {
		if (i18nMaybeIncomplet.getIdI18n() == null) {
			I18n i18n = i18nRepository.save(new I18n(i18nMaybeIncomplet.getTypeTraduction()));
			i18nMaybeIncomplet.getI18nTraductions().forEach(e -> {
				e.setI18n(i18n);
				e.setId(new I18nTraductionPK(i18n.getIdI18n(), e.getLangue().getCodLangue()));
				i18n.getI18nTraductions().add(e);
			});
			return i18nRepository.save(i18n);
		} else {
			return i18nRepository.save(i18nMaybeIncomplet);
		}
	}

	/** Enregistre une entité i18n nullable
	 *
	 * @param i18n
	 * @return l'entite I18n créé */
	public I18n saveI18nNullable(final I18n i18n) {
		if (i18n == null) {
			return null;
		}
		/** On supprime d'abord les traductions qui sont à vide */
		Iterator<I18nTraduction> iter = i18n.getI18nTraductions().iterator();
		while (iter.hasNext()) {
			I18nTraduction trad = iter.next();
			if (trad.getValTrad() == null || trad.getValTrad().equals("")) {
				iter.remove();
			}
		}
		if (i18n.getI18nTraductions().isEmpty()) {
			return null;
		}
		return saveI18n(i18n);
	}

	/** Retourne un id i18n nullable
	 *
	 * @param i18n
	 * @return l'id de l'objet i18n */
	public Integer getIdI18nNullable(final I18n i18n) {
		return (i18n != null) ? i18n.getIdI18n() : null;
	}

	/** Supprime un i18n nullable
	 *
	 * @param id
	 */
	public void deleteI18nNullable(final Integer id) {
		i18nRepository.delete(id);
	}

	/** @param typeTraduction
	 * @return le type de traduction */
	public TypeTraduction getTypeTraduction(final String typeTraduction) {
		return typeTraductionRepository.findOne(typeTraduction);
	}

	/** Renvoi la valeur d'une traduction (langue default si plus d'une traduction)
	 *
	 * @param i18n
	 * @return la valeur d'une traduction */
	public String getI18nTraduction(final I18n i18n) {
		return getI18nTraduction(i18n, getLangueCandidat());
	}

	/** @param i18n
	 * @return les traductions sous forme de libellé */
	public String getI18nTraductionLibelle(final I18n i18n) {
		StringBuilder ret = new StringBuilder("");
		i18n.getI18nTraductions().forEach(e -> {
			ret.append(e.getValTrad() + "; ");
		});
		return ret.toString();
	}

	/** Renvoi la valeur d'un traduction (langue default si plus d'une traduction)
	 *
	 * @param i18n
	 * @param codLangueCand
	 * @return la valeur d'un traduction par une langue */
	public String getI18nTraduction(final I18n i18n, final String codLangueCand) {
		if (i18n == null || i18n.getI18nTraductions().size() == 0) {
			return null;
		} else if (i18n.getI18nTraductions().size() == 1) {
			return i18n.getI18nTraductions().get(0).getValTrad();
		} else {
			if (codLangueCand != null) {
				Optional<I18nTraduction> i18nTraductionPref = i18n.getI18nTraductions().stream().filter(t -> t.getLangue().getTesLangue()
						&& t.getLangue().getCodLangue().equals(codLangueCand)).findFirst();
				if (i18nTraductionPref.isPresent()) {
					return i18nTraductionPref.get().getValTrad();
				}
			} else {
				Optional<I18nTraduction> i18nTraductionDefault = i18n.getI18nTraductions().stream().filter(t -> t.getLangue().equals(cacheController.getLangueDefault())).findFirst();
				if (i18nTraductionDefault.isPresent()) {
					return i18nTraductionDefault.get().getValTrad();
				}
			}
		}
		return null;
	}

	/** Change la langue de l'utilisateur-->verifie qu'elle existe d'abord et est
	 * active
	 *
	 * @return true si la langue a été changée */
	public Boolean changeLangue(final Langue langue) {
		return changeLangueUI(getCodeLangueActive(langue.getCodLangue()), false);
	}

	/** @param langue
	 * @return la langue active */
	private String getCodeLangueActive(final String codLangue) {
		String codLangueDefault = cacheController.getLangueDefault().getCodLangue();
		if (!codLangue.equals(codLangueDefault)) {
			Optional<Langue> langueFilter = cacheController.getLangueEnServiceWithoutDefault().stream().filter(e -> e.getCodLangue().equals(codLangue)).findAny();
			if (langueFilter.isPresent()) {
				return codLangue;
			}
		}
		return codLangueDefault;
	}

	/** @return la langue de l'interface */
	public String getLangueUI() {
		try {
			if (UI.getCurrent() != null && UI.getCurrent().getLocale() != null) {
				return UI.getCurrent().getLocale().getLanguage();
			}
		} catch (Exception e) {
		}
		return cacheController.getLangueDefault().getCodLangue();
	}

	/** @return la langue préférée du candidat */
	public String getLangueCandidat() {
		SecurityUserCandidat user = userController.getSecurityUserCandidat();
		if (user != null) {
			return user.getCodLangue();
		} else {
			return getLangueUI();
		}
	}

	/** Initialise la langue lors de l'arrivée sur l'UI */
	public void initLanguageUI(final Boolean forceToReloadMenu) {

		/* Mise a jour de la langue */
		String langue = cacheController.getLangueDefault().getCodLangue();
		SecurityUserCandidat user = userController.getSecurityUserCandidat();
		if (user != null && user.getCodLangue() != null) {
			langue = getCodeLangueActive(user.getCodLangue());
		}

		if (langue != null) {
			changeLangueUI(langue, forceToReloadMenu);
		} else {
			Langue langueDefault = cacheController.getLangueDefault();
			if (langueDefault != null) {
				changeLangueUI(langueDefault.getCodLangue(), forceToReloadMenu);
			}
		}
	}

	/** Change la langue de l'UI
	 *
	 * @param codeLangue
	 *            le code langage de la locale
	 * @param forceToReloadMenu
	 *            si le menu doit être forcé à être rechargé-->cas du candidat en
	 *            connexion interne
	 * @return true si la langue a été& changée */
	private Boolean changeLangueUI(final String codeLangue, final Boolean forceToReloadMenu) {
		if (codeLangue == null || UI.getCurrent() == null) {
			return false;
		}
		Locale locale = UI.getCurrent().getLocale();

		if (forceToReloadMenu || locale == null || locale.getLanguage() == null
				|| (codeLangue != null && !codeLangue.equals(locale.getLanguage()))) {
			SecurityUserCandidat user = userController.getSecurityUserCandidat();
			if (user != null) {
				user.setCodLangue(codeLangue);
			}
			((MainUI) UI.getCurrent()).setLocale(new Locale(codeLangue));
			((MainUI) UI.getCurrent()).configReconnectDialogMessages();
			((MainUI) UI.getCurrent()).constructMainMenu();

			return true;
		}
		return false;
	}
}
