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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.views.windows.FaqWindow;


/**
 * Page d'assistance
 * @author Kevin Hergalant
 *
 */
@SpringView(name = AssistanceView.NAME)
public class AssistanceView extends VerticalLayout implements View {

	/** serialVersionUID **/
	private static final long serialVersionUID = 4359194703029079044L;

	public static final String NAME = "assistanceView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CacheController cacheController;
	
	/* Variable d'envirronement */
	@Value("${assistance.documentation.url:}")
	private String assistanceDocumentationUrl;
	
	@Value("${assistance.documentation.url.candidat:}")
	private String assistanceDocumentationUrlCandidat;
	
	@Value("${assistance.documentation.url.candidat.en:}")
	private String assistanceDocumentationUrlCandidatEn;
	
	@Value("${assistance.helpdesk.url:}")
	private String assistanceHelpdeskUrl;
	
	@Value("${assistance.contact.mail:}")
	private String assistanceContactMail;
	
	
	
	
	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		Boolean isPersonnel = userController.isPersonnel();
		/* Style */
		setMargin(true);
		setSpacing(true);		

		/* Titre */
		Label title = new Label(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
		title.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(title);
		
		Panel panelContent = new Panel();
		panelContent.setWidth(100, Unit.PERCENTAGE);
		
		VerticalLayout vlContent = new VerticalLayout();
		vlContent.setSizeUndefined();
		vlContent.setSpacing(true);
		vlContent.setMargin(true);
		panelContent.setContent(vlContent);
		addComponent(panelContent);
		
		Boolean find = false;

		/* Accès à la faq */
		if (cacheController.getFaq().size()>0){
			OneClickButton docFaq = new OneClickButton(applicationContext.getMessage(NAME + ".btnFaq", null, UI.getCurrent().getLocale()), FontAwesome.QUESTION_CIRCLE);
			docFaq.addClickListener(e->{
				UI.getCurrent().addWindow(new FaqWindow());
			});
			docFaq.addStyleName(ValoTheme.BUTTON_LINK);
			vlContent.addComponent(docFaq);
			find = true;
		}		
		
		/* Accès à la documentation */
		String urlDoc = null;
		if (isPersonnel){
			urlDoc = assistanceDocumentationUrl;
		}else{
			Boolean isEn = false;
			Locale locale = UI.getCurrent().getLocale();
			if (locale != null){
				String cod = locale.getLanguage();
				if (assistanceDocumentationUrlCandidatEn!=null && !assistanceDocumentationUrlCandidatEn.equals("") && cod!=null && cod.equals("en")){
					urlDoc = assistanceDocumentationUrlCandidatEn;
					isEn = true;
				}
				
			}			
			if (!isEn){
				urlDoc = assistanceDocumentationUrlCandidat;
			}
		}
		
		if (urlDoc!=null && !urlDoc.equals("")){
			vlContent.addComponent(getButton(applicationContext.getMessage(NAME + ".btnDoc", null, UI.getCurrent().getLocale()), urlDoc ,FontAwesome.FILE_TEXT));
			find = true;
		}

		/* Envoyer un ticket */
		if (isPersonnel){
			if (assistanceHelpdeskUrl!=null && !assistanceHelpdeskUrl.equals("")){			
				vlContent.addComponent(getButton(applicationContext.getMessage(NAME + ".btnHelpdesk", null, UI.getCurrent().getLocale()), assistanceHelpdeskUrl ,FontAwesome.AMBULANCE));
				find = true;
			}
		}
		

		/* Envoyer un mail */
		if (assistanceContactMail!=null && !assistanceContactMail.equals("")){
			vlContent.addComponent(getButton(applicationContext.getMessage(NAME + ".btnContact", new Object[] {assistanceContactMail}, UI.getCurrent().getLocale()), "mailto: " + assistanceContactMail, FontAwesome.ENVELOPE));
			find = true;
		}
		
		if (!find){
			vlContent.addComponent(new Label(applicationContext.getMessage("assistanceView.noDoc", null, UI.getCurrent().getLocale()),ContentMode.HTML));
		}
	}

	/**
	 * @param caption
	 * @param bwo
	 * @param icon
	 * @return un bouton pour l'assistance
	 */
	private OneClickButton getButton(String caption, String bwo, com.vaadin.server.Resource icon){
		BrowserWindowOpener browser = new BrowserWindowOpener(new ExternalResource(bwo));
		OneClickButton btn = new OneClickButton(caption, icon);
		btn.addStyleName(ValoTheme.BUTTON_LINK);
		browser.extend(btn);
		return btn;
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
	}

}
