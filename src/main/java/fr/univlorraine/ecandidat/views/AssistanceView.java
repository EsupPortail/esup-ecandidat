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
package fr.univlorraine.ecandidat.views;

import java.util.Locale;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.ConfigController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigEtab;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.views.windows.FaqWindow;

/**
 * Page d'assistance
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = AssistanceView.NAME)
public class AssistanceView extends VerticalLayout implements View {

	public static final String NAME = "assistanceView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient ConfigController configController;

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		final Boolean isPersonnel = userController.isPersonnel();
		final ConfigEtab configEtab = configController.getConfigEtab();

		/* Style */
		setMargin(true);
		setSpacing(true);

		/* Titre */
		final Label title = new Label(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
		title.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(title);

		final Panel panelContent = new Panel();
		panelContent.setWidth(100, Unit.PERCENTAGE);

		final VerticalLayout vlContent = new VerticalLayout();
		vlContent.setSizeUndefined();
		vlContent.setSpacing(true);
		vlContent.setMargin(true);
		panelContent.setContent(vlContent);
		addComponent(panelContent);

		Boolean find = false;

		/* Accès à la faq */
		if (cacheController.getFaq().size() > 0) {
			final OneClickButton docFaq = new OneClickButton(applicationContext.getMessage(NAME + ".btnFaq", null, UI.getCurrent().getLocale()), FontAwesome.QUESTION_CIRCLE);
			docFaq.addClickListener(e -> {
				UI.getCurrent().addWindow(new FaqWindow());
			});
			docFaq.addStyleName(ValoTheme.BUTTON_LINK);
			vlContent.addComponent(docFaq);
			find = true;
		}

		/* Accès à la documentation */
		String urlDoc = null;
		if (isPersonnel) {
			urlDoc = configEtab.getAssistDocUrl();
		} else {
			Boolean isEn = false;
			final Locale locale = UI.getCurrent().getLocale();
			if (locale != null) {
				final String cod = locale.getLanguage();
				if (StringUtils.isNotBlank(configEtab.getAssistDocUrlCandEn()) && cod != null && cod.equals("en")) {
					urlDoc = configEtab.getAssistDocUrlCandEn();
					isEn = true;
				}

			}
			if (!isEn) {
				urlDoc = configEtab.getAssistDocUrlCand();
			}
		}

		if (StringUtils.isNotBlank(urlDoc)) {
			vlContent.addComponent(getButton(applicationContext.getMessage(NAME + ".btnDoc", null, UI.getCurrent().getLocale()), urlDoc, FontAwesome.FILE_TEXT));
			find = true;
		}

		/* Envoyer un ticket */
		if (isPersonnel && StringUtils.isNotBlank(configEtab.getAssistHelpdeskUrl())) {
			vlContent.addComponent(getButton(applicationContext.getMessage(NAME + ".btnHelpdesk", null, UI.getCurrent().getLocale()), configEtab.getAssistHelpdeskUrl(), FontAwesome.AMBULANCE));
			find = true;
		}

		/* Envoyer un mail */
		if (StringUtils.isNotBlank(configEtab.getAssistContactMail())) {
			vlContent
				.addComponent(getButton(applicationContext.getMessage(NAME + ".btnContact", new Object[] { configEtab.getAssistContactMail() }, UI.getCurrent().getLocale()), "mailto: " + configEtab.getAssistContactMail(),
					FontAwesome.ENVELOPE));
			find = true;
		}

		/* Url de contact */
		if (StringUtils.isNotBlank(configEtab.getAssistContactUrl())) {
			vlContent.addComponent(getButton(applicationContext.getMessage(NAME + ".btnContactUrl", new Object[] { configEtab.getAssistContactUrl() }, UI.getCurrent().getLocale()), configEtab.getAssistContactUrl(),
				FontAwesome.EXTERNAL_LINK_SQUARE));
			find = true;
		}

		if (!find) {
			vlContent.addComponent(new Label(applicationContext.getMessage("assistanceView.noDoc", null, UI.getCurrent().getLocale()), ContentMode.HTML));
		}
	}

	/**
	 * @param  caption
	 * @param  bwo
	 * @param  icon
	 * @return         un bouton pour l'assistance
	 */
	private OneClickButton getButton(final String caption, final String bwo, final com.vaadin.server.Resource icon) {
		final BrowserWindowOpener browser = new BrowserWindowOpener(new ExternalResource(bwo));
		final OneClickButton btn = new OneClickButton(caption, icon);
		btn.addStyleName(ValoTheme.BUTTON_LINK);
		browser.extend(btn);
		return btn;
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
	}

}
