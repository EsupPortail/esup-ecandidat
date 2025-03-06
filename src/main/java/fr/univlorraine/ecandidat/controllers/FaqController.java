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

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Faq;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.repositories.FaqRepository;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.ScolFaqWindow;
import jakarta.annotation.Resource;

/**
 * Gestion de l'entité faq
 * @author Kevin Hergalant
 */
@Component
public class FaqController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient FaqRepository faqRepository;

	/**
	 * @return liste des faq
	 */
	public List<Faq> getFaqToCache() {
		return faqRepository.findAllByOrderByOrderFaqAsc();
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau faq.
	 */
	public void editNewFaq() {
		final Faq faq = new Faq();
		faq.setI18nQuestion(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_FAQ_QUESTION)));
		faq.setI18nReponse(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_FAQ_REPONSE)));
		UI.getCurrent().addWindow(new ScolFaqWindow(faq));
	}

	/**
	 * Ouvre une fenêtre d'édition de faq.
	 * @param faq
	 */
	public void editFaq(final Faq faq) {
		Assert.notNull(faq, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(faq, null)) {
			return;
		}
		final ScolFaqWindow window = new ScolFaqWindow(faq);
		window.addCloseListener(e -> lockController.releaseLock(faq));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un faq
	 * @param faq
	 */
	public void saveFaq(Faq faq) {
		Assert.notNull(faq, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (faq.getIdFaq() != null && !lockController.getLockOrNotify(faq, null)) {
			return;
		}
		faq.setI18nQuestion(i18nController.saveI18n(faq.getI18nQuestion()));
		faq.setI18nReponse(i18nController.saveI18n(faq.getI18nReponse()));
		faq = faqRepository.saveAndFlush(faq);
		cacheController.reloadFaq(true);
		lockController.releaseLock(faq);
	}

	/**
	 * Supprime une faq
	 * @param faq
	 */
	public void deleteFaq(final Faq faq) {
		Assert.notNull(faq, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(faq, null)) {
			return;
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("faqAvis.window.confirmDelete", new Object[] { faq.getLibFaq() }, UI.getCurrent().getLocale()),
			applicationContext.getMessage("faqAvis.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(faq, null)) {
				faqRepository.delete(faq);
				cacheController.reloadFaq(true);
				/* Suppression du lock */
				lockController.releaseLock(faq);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(faq);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
}
