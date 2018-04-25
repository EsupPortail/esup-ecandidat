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
package fr.univlorraine.ecandidat.views;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.CampagneController;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/** Page de creation de compte du candidat
 *
 * @author Kevin Hergalant */
@SpringView(name = CandidatCreerCompteView.NAME)
public class CandidatCreerCompteView extends VerticalLayout implements View {

	/** serialVersionUID **/
	private static final long serialVersionUID = -1892026915407604201L;

	public static final String NAME = "candidatCreerCompteView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient I18nController i18nController;

	private Label labelTitle = new Label();
	private Label labelAccueil = new Label();
	private HorizontalLayout hlConnectedCreateCompte = new HorizontalLayout();
	private Panel panelIsStudent = new Panel();
	private Panel panelNotStudent = new Panel();
	private Panel panelCreateCompte = new Panel();
	private VerticalLayout vlConnexionIsStudent = new VerticalLayout();
	private VerticalLayout vlConnexionNotStudent = new VerticalLayout();
	private OneClickButton logBtnNoCompte = new OneClickButton(FontAwesome.SIGN_OUT);

	/** Initialise la vue */
	@PostConstruct
	public void init() {
		/* Style */
		setMargin(true);
		setSpacing(true);
		setSizeFull();

		/* Titre */
		HorizontalLayout hlLangue = new HorizontalLayout();
		hlLangue.setWidth(100, Unit.PERCENTAGE);
		hlLangue.setSpacing(true);

		/* Le titre */
		labelTitle.setValue(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
		labelTitle.addStyleName(StyleConstants.VIEW_TITLE);
		hlLangue.addComponent(labelTitle);
		hlLangue.setExpandRatio(labelTitle, 1);
		hlLangue.setComponentAlignment(labelTitle, Alignment.MIDDLE_LEFT);

		if (cacheController.getLangueEnServiceWithoutDefault().size() > 0) {
			Langue langueDef = cacheController.getLangueDefault();
			Image flagDef = new Image(null, new ThemeResource("images/flags/" + langueDef.getCodLangue() + ".png"));
			flagDef.addClickListener(e -> changeLangue(langueDef));
			flagDef.addStyleName(StyleConstants.CLICKABLE);
			hlLangue.addComponent(flagDef);
			hlLangue.setComponentAlignment(flagDef, Alignment.MIDDLE_CENTER);
			cacheController.getLangueEnServiceWithoutDefault().forEach(langue -> {
				Image flag = new Image(null, new ThemeResource("images/flags/" + langue.getCodLangue() + ".png"));
				flag.addClickListener(e -> changeLangue(langue));
				flag.addStyleName(StyleConstants.CLICKABLE);
				hlLangue.addComponent(flag);
				hlLangue.setComponentAlignment(flag, Alignment.MIDDLE_CENTER);

			});
		}

		addComponent(hlLangue);

		/* Panel scrollable de contenu */
		Panel panelContent = new Panel();
		panelContent.setSizeFull();
		panelContent.addStyleName(ValoTheme.PANEL_BORDERLESS);
		addComponent(panelContent);
		setExpandRatio(panelContent, 1);

		VerticalLayout vlContent = new VerticalLayout();
		vlContent.setSpacing(true);
		panelContent.setContent(vlContent);

		/* Texte */
		labelAccueil.setValue("");
		labelAccueil.setContentMode(ContentMode.HTML);

		vlContent.addComponent(labelAccueil);

		if (!campagneController.isCampagneActiveCandidat(campagneController.getCampagneActive())) {
			vlContent.addComponent(new Label(applicationContext.getMessage("accueilView.nocampagne", null, UI.getCurrent().getLocale())));
			return;
		}

		/* Connexion CAS */
		panelIsStudent.setCaption(applicationContext.getMessage("accueilView.title.etu", new Object[] {
				applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale())}, UI.getCurrent().getLocale()));
		panelIsStudent.addStyleName(StyleConstants.ACCUEIL_COMPTE_PANEL);
		panelIsStudent.addStyleName(StyleConstants.PANEL_COLORED);
		vlConnexionIsStudent.setSpacing(true);
		vlConnexionIsStudent.setMargin(true);
		panelIsStudent.setContent(vlConnexionIsStudent);
		vlContent.addComponent(panelIsStudent);

		/* Creation sans compte cas */
		panelNotStudent.setCaption(applicationContext.getMessage("accueilView.title.nonetu", new Object[] {
				applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale())}, UI.getCurrent().getLocale()));
		panelNotStudent.addStyleName(StyleConstants.ACCUEIL_COMPTE_PANEL);
		panelNotStudent.addStyleName(StyleConstants.PANEL_COLORED);
		vlConnexionNotStudent.setSpacing(true);
		vlConnexionNotStudent.setMargin(true);
		panelNotStudent.setContent(vlConnexionNotStudent);
		vlContent.addComponent(panelNotStudent);

		/* Connecté mais sans compte candidat */
		hlConnectedCreateCompte.addStyleName(StyleConstants.ACCUEIL_COMPTE_PANEL);
		vlContent.addComponent(hlConnectedCreateCompte);

		panelCreateCompte.setCaption(applicationContext.getMessage("accueilView.title.nocompte", null, UI.getCurrent().getLocale()));
		panelCreateCompte.addStyleName(StyleConstants.PANEL_COLORED);
		VerticalLayout vlCreateCompte = new VerticalLayout();
		vlCreateCompte.setSpacing(true);
		vlCreateCompte.setMargin(true);
		panelCreateCompte.setContent(vlCreateCompte);
		hlConnectedCreateCompte.addComponent(panelCreateCompte);

		logBtnNoCompte.setCaption(applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()));
		logBtnNoCompte.addClickListener(e -> {
			candidatController.createCompteMinima(false);
		});
		vlCreateCompte.addComponent(logBtnNoCompte);
	}

	/** Change la langue de l'utilisateur et rafraichi les infos
	 *
	 * @param langue
	 */
	private void changeLangue(final Langue langue) {
		i18nController.changeLangue(langue);
		labelTitle.setValue(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
		panelCreateCompte.setCaption(applicationContext.getMessage("accueilView.title.nocompte", null, UI.getCurrent().getLocale()));
		logBtnNoCompte.setCaption(applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()));
		Authentication auth = userController.getCurrentAuthentication();
		setTxtMessageAccueil(auth);
		refreshLayoutConnexion(auth);
	}

	/** Rafrachi le layout de connexion
	 *
	 * @param auth
	 */
	private void refreshLayoutConnexion(final Authentication auth) {
		if (!userController.isAnonymous(auth)) {
			panelIsStudent.setVisible(false);
			panelNotStudent.setVisible(false);
			if (!userController.isPersonnel(auth) && !userController.isCandidat(auth)) {
				hlConnectedCreateCompte.setVisible(true);
			} else {
				hlConnectedCreateCompte.setVisible(false);
			}
			return;
		} else {
			hlConnectedCreateCompte.setVisible(false);
			panelNotStudent.setVisible(true);
			panelIsStudent.setVisible(true);
		}
		refreshConnexionPanelStudent();
		refreshConnexionPanelNotStudent();
	}

	/** Rafraichi le panel de connexion sans compte */
	private void refreshConnexionPanelStudent() {
		vlConnexionIsStudent.removeAllComponents();

		OneClickButton logBtn = new OneClickButton(applicationContext.getMessage("btnConnect.candidat", null, UI.getCurrent().getLocale()), FontAwesome.SIGN_OUT);
		logBtn.addClickListener(e -> {
			userController.connectCAS();
		});

		HorizontalLayout hlConnect = new HorizontalLayout();
		hlConnect.setSpacing(true);
		Label labelConnect = new Label(applicationContext.getMessage("accueilView.connect.cas", null, UI.getCurrent().getLocale()));
		hlConnect.addComponent(labelConnect);
		hlConnect.setComponentAlignment(labelConnect, Alignment.MIDDLE_LEFT);
		hlConnect.addComponent(logBtn);
		hlConnect.setComponentAlignment(logBtn, Alignment.MIDDLE_CENTER);

		vlConnexionIsStudent.addComponent(hlConnect);
	}

	/** Rafraichi le panel de connexion sans compte */
	private void refreshConnexionPanelNotStudent() {
		vlConnexionNotStudent.removeAllComponents();

		OneClickButton logBtnNoCompte = new OneClickButton(applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()), FontAwesome.SIGN_OUT);
		logBtnNoCompte.addClickListener(e -> {
			candidatController.createCompteMinima(false);
		});
		vlConnexionNotStudent.addComponent(logBtnNoCompte);
	}

	/** @param auth
	 * @return le texte de message d'accueil */
	private String setTxtMessageAccueil(final Authentication auth) {
		String txt = "";
		if (!userController.isAnonymous(auth)) {
			txt += applicationContext.getMessage("accueilView.welcome", null, UI.getCurrent().getLocale());
			txt += applicationContext.getMessage("accueilView.connected", new Object[] {userController.getCurrentUserName(auth)}, UI.getCurrent().getLocale());
			if (userController.isPersonnel(auth)) {
				txt += applicationContext.getMessage("accueilView.role", new Object[] {auth.getAuthorities()}, UI.getCurrent().getLocale());
			} else if (userController.isCandidat(auth)) {
				txt += applicationContext.getMessage("accueilView.cand.connected", null, UI.getCurrent().getLocale());
			}
		}
		if (txt != null && !txt.equals("")) {
			labelAccueil.setValue(txt);
			labelAccueil.setVisible(true);
		} else {
			labelAccueil.setVisible(false);
		}

		return txt;
	}

	/** @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent) */
	@Override
	public void enter(final ViewChangeEvent event) {
		Authentication auth = userController.getCurrentAuthentication();
		setTxtMessageAccueil(auth);
		refreshLayoutConnexion(auth);
	}

}
