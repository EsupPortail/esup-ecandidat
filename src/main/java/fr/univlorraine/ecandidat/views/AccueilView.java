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
import fr.univlorraine.ecandidat.controllers.LoadBalancingController;
import fr.univlorraine.ecandidat.controllers.MessageController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.TestController;
import fr.univlorraine.ecandidat.controllers.TestWsController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.ConnexionLayout;
import fr.univlorraine.ecandidat.vaadin.components.CustomPanel;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.views.windows.CandidatIdOublieWindow;

/**
 * Page d'accueil
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = AccueilView.NAME)
public class AccueilView extends VerticalLayout implements View {

	public static final String NAME = "accueilView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient MessageController messageController;
	@Resource
	private transient CacheController cacheController;

	private final Label labelTitle = new Label();
	private final CustomPanel panelMessage = new CustomPanel();
	private final Label labelAccueil = new Label("", ContentMode.HTML);
	private final HorizontalLayout hlConnectedCreateCompte = new HorizontalLayout();
	private final VerticalLayout vlConnexionWithCompte = new VerticalLayout();
	private final Panel panelCreateCompte = new Panel();
	private final OneClickButton logBtnNoCompte = new OneClickButton(FontAwesome.SIGN_OUT);
	private final ConnexionLayout connexionLayout = new ConnexionLayout();

	/* CNIL */
	private final Panel panelCnil = new Panel();
	private final Label labelCnil = new Label();

	private String title;

	/** TODO a retirer-->Test */
	@Resource
	private transient TestController testController;
	@Resource
	private transient TestWsController testWsController;

	/** TODO fin a retirer-->Test */

	/** Initialise la vue */
	@PostConstruct
	public void init() {
		/* Style */
		setMargin(true);
		setSpacing(true);
		setSizeFull();

		if (testController.isTestMode()) {
			final HorizontalLayout hlTest = new HorizontalLayout();
			hlTest.setSpacing(true);
			addComponent(hlTest);

			final OneClickButton btnTest = new OneClickButton("Test");
			hlTest.addComponent(btnTest);
			btnTest.addClickListener(e -> {
				testController.testMethode();
			});

			final OneClickButton btnTestWs = new OneClickButton("Test Ws Apogee");
			hlTest.addComponent(btnTestWs);
			btnTestWs.addClickListener(e -> {
				try {
					testWsController.testWs();
				} catch (final Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			});

			final OneClickButton btnTestWsPegase = new OneClickButton("Test Ws Pegase");
			hlTest.addComponent(btnTestWsPegase);
			btnTestWsPegase.addClickListener(e -> {
				try {
					testWsController.testWsPegase();
				} catch (final Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			});

		}

		/* Titre */
		final HorizontalLayout hlLangue = new HorizontalLayout();
		hlLangue.setWidth(100, Unit.PERCENTAGE);
		hlLangue.setSpacing(true);

		/* Le titre */
		labelTitle.addStyleName(StyleConstants.VIEW_TITLE);
		hlLangue.addComponent(labelTitle);
		hlLangue.setExpandRatio(labelTitle, 1);
		hlLangue.setComponentAlignment(labelTitle, Alignment.MIDDLE_LEFT);

		if (cacheController.getLangueEnServiceWithoutDefault().size() > 0) {
			final Langue langueDef = cacheController.getLangueDefault();
			final Image flagDef = new Image(null, new ThemeResource("images/flags/" + langueDef.getCodLangue() + ".png"));
			flagDef.addClickListener(e -> changeLangue(langueDef));
			flagDef.addStyleName(StyleConstants.CLICKABLE);
			hlLangue.addComponent(flagDef);
			hlLangue.setComponentAlignment(flagDef, Alignment.MIDDLE_CENTER);
			cacheController.getLangueEnServiceWithoutDefault().forEach(langue -> {
				final Image flag = new Image(null, new ThemeResource("images/flags/" + langue.getCodLangue() + ".png"));
				flag.addClickListener(e -> changeLangue(langue));
				flag.addStyleName(StyleConstants.CLICKABLE);
				hlLangue.addComponent(flag);
				hlLangue.setComponentAlignment(flag, Alignment.MIDDLE_CENTER);

			});
		}

		addComponent(hlLangue);

		/* Panel scrollable de contenu */
		final Panel panelContent = new Panel();
		panelContent.setSizeFull();
		panelContent.addStyleName(ValoTheme.PANEL_BORDERLESS);
		addComponent(panelContent);
		setExpandRatio(panelContent, 1);

		final VerticalLayout vlContent = new VerticalLayout();
		vlContent.setSpacing(true);
		panelContent.setContent(vlContent);

		/* Les messages de l'accueil */
		panelMessage.setCaption(applicationContext.getMessage("informations", null, UI.getCurrent().getLocale()));
		panelMessage.setIcon(FontAwesome.INFO_CIRCLE);
		// panelMessage.setWidthPixel(500);
		panelMessage.setMargin(true);
		vlContent.addComponent(panelMessage);
		panelMessage.setWidth(100, Unit.PERCENTAGE);
		panelMessage.addStyleName(StyleConstants.ACCUEIL_MESSAGE_PANEL);

		/* Texte */
		vlContent.addComponent(labelAccueil);

		if (!campagneController.isCampagneActiveCandidat(campagneController.getCampagneActive())) {
			vlContent.addComponent(new Label(applicationContext.getMessage("accueilView.nocampagne", null, UI.getCurrent().getLocale())));
			addMentionCnil();
			return;
		}

		/* Connexion */
		/* Avec compte */
		vlConnexionWithCompte.setWidth(100, Unit.PERCENTAGE);
		vlConnexionWithCompte.addStyleName(StyleConstants.ACCUEIL_COMPTE_PANEL);
		vlContent.addComponent(vlConnexionWithCompte);

		connexionLayout.addCasListener(() -> userController.connectCAS());
		connexionLayout.addStudentListener((user, pwd) -> userController.connectCandidatInterne(user, pwd));
		connexionLayout.addForgotPasswordListener(() -> {
			UI.getCurrent().addWindow(new CandidatIdOublieWindow(ConstanteUtils.FORGOT_MODE_ID_OUBLIE));
		});
		connexionLayout.addCreateCompteListener(() -> candidatController.createCompteMinima(false));
		connexionLayout.addForgotCodeActivationListener(() -> {
			UI.getCurrent().addWindow(new CandidatIdOublieWindow(ConstanteUtils.FORGOT_MODE_CODE_ACTIVATION));
		});
		vlConnexionWithCompte.addComponent(connexionLayout);

		/* Connecté mais sans compte candidat */
		hlConnectedCreateCompte.setWidth(100, Unit.PERCENTAGE);
		hlConnectedCreateCompte.addStyleName(StyleConstants.ACCUEIL_COMPTE_PANEL);
		vlContent.addComponent(hlConnectedCreateCompte);

		panelCreateCompte.setCaption(applicationContext.getMessage("accueilView.title.nocompte", null, UI.getCurrent().getLocale()));
		panelCreateCompte.addStyleName(StyleConstants.PANEL_COLORED);
		final VerticalLayout vlCreateCompte = new VerticalLayout();
		vlCreateCompte.setSpacing(true);
		vlCreateCompte.setMargin(true);
		panelCreateCompte.setContent(vlCreateCompte);
		hlConnectedCreateCompte.addComponent(panelCreateCompte);

		logBtnNoCompte.setCaption(applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()));
		logBtnNoCompte.addClickListener(e -> {
			candidatController.createCompteMinima(false);
		});
		vlCreateCompte.addComponent(logBtnNoCompte);

		/* Mention cnil */
		addMentionCnil();
	}

	/** ajoute la mention CNIL */
	private void addMentionCnil() {
		panelCnil.setWidth(100, Unit.PERCENTAGE);
		panelCnil.setHeight(100, Unit.PIXELS);
		addComponent(panelCnil);
		setComponentAlignment(panelCnil, Alignment.BOTTOM_LEFT);

		final VerticalLayout vlContentLabelCnil = new VerticalLayout();
		vlContentLabelCnil.setSizeUndefined();
		vlContentLabelCnil.setWidth(100, Unit.PERCENTAGE);
		vlContentLabelCnil.setMargin(true);

		labelCnil.setContentMode(ContentMode.HTML);
		labelCnil.addStyleName(ValoTheme.LABEL_TINY);
		labelCnil.addStyleName(StyleConstants.LABEL_JUSTIFY);
		labelCnil.addStyleName(StyleConstants.LABEL_SAUT_LIGNE);
		vlContentLabelCnil.addComponent(labelCnil);

		panelCnil.setContent(vlContentLabelCnil);
	}

	/** Met a jour la mention CNIL */
	private void updateMentionCnil() {
		final String mentionCnil = applicationContext.getMessage("cnil.mention", null, UI.getCurrent().getLocale());
		if (mentionCnil != null && !mentionCnil.equals("")) {
			labelCnil.setValue(mentionCnil);
			panelCnil.setVisible(true);
		} else {
			labelCnil.setValue("");
			panelCnil.setVisible(false);
		}
	}

	/**
	 * Change la langue de l'utilisateur et rafraichi les infos
	 * @param langue
	 */
	private void changeLangue(final Langue langue) {
		i18nController.changeLangue(langue);
		labelTitle.setValue(title);
		panelCreateCompte.setCaption(applicationContext.getMessage("accueilView.title.nocompte", null, UI.getCurrent().getLocale()));
		logBtnNoCompte.setCaption(applicationContext.getMessage("accueilView.createaccount", null, UI.getCurrent().getLocale()));
		final Authentication auth = userController.getCurrentAuthentication();
		setTxtMessageAccueil(auth);
		refreshLayoutConnexion(auth);
		updateMentionCnil();
	}

	/**
	 * Rafrachi le layout de connexion
	 * @param auth
	 */
	private void refreshLayoutConnexion(final Authentication auth) {
		if (loadBalancingController.isLoadBalancingGestionnaireMode()) {
			vlConnexionWithCompte.setVisible(false);
			connexionLayout.setClickShortcut(false);
			hlConnectedCreateCompte.setVisible(false);
			return;
		}
		if (!userController.isAnonymous(auth)) {
			vlConnexionWithCompte.setVisible(false);
			connexionLayout.setClickShortcut(false);
			if (!userController.isPersonnel(auth) && !userController.isCandidat(auth)) {
				hlConnectedCreateCompte.setVisible(true);
			} else {
				hlConnectedCreateCompte.setVisible(false);
			}
			return;
		} else {
			hlConnectedCreateCompte.setVisible(false);
			vlConnexionWithCompte.setVisible(true);
			connexionLayout.setClickShortcut(true);
		}
		refreshConnexionPanelWithCompte();
	}

	/** Rafraichi le panel de connexion avec un compte */
	private void refreshConnexionPanelWithCompte() {
		connexionLayout.updateLibelle();
	}

	/**
	 * @param  auth
	 * @return      le texte de message d'accueil
	 */
	private String setTxtMessageAccueil(final Authentication auth) {
		/* On cherche un eventuel message d'accueil */
		final String msg = messageController.getMessage(NomenclatureUtils.COD_MSG_ACCUEIL);
		if (msg != null) {
			panelMessage.setMessage(msg);
			panelMessage.setVisible(true);
		} else {
			panelMessage.setMessage("");
			panelMessage.setVisible(false);
		}

		String txt = "";
		if (!userController.isAnonymous(auth)) {
			txt += applicationContext.getMessage("accueilView.welcome", new Object[] { userController.getCurrentUserName(auth) }, UI.getCurrent().getLocale());

			if (userController.isPersonnel(auth)) {
				txt += applicationContext.getMessage("accueilView.connected", new Object[] { userController.getCurrentUserLogin(auth) }, UI.getCurrent().getLocale());
				txt += applicationContext.getMessage("accueilView.role", new Object[] { auth.getAuthorities() }, UI.getCurrent().getLocale());
			} else if (userController.isCandidat(auth)) {
				txt += applicationContext.getMessage("accueilView.connected", new Object[] { userController.getCurrentNoDossierCptMinOrLogin(auth) }, UI.getCurrent().getLocale());
				if (userController.isCandidatValid(auth)) {
					txt += applicationContext.getMessage("accueilView.cand.connected", null, UI.getCurrent().getLocale());
				} else {
					txt += applicationContext.getMessage("compteMinima.connect.valid.error", null, UI.getCurrent().getLocale());
				}

			}
		} else if (loadBalancingController.isLoadBalancingGestionnaireMode()) {
			txt = applicationContext.getMessage("accueilView.app.gest", null, UI.getCurrent().getLocale());
		}
		if (txt != null && !txt.equals("")) {
			labelAccueil.setValue(txt);
			labelAccueil.setVisible(true);
		} else {
			labelAccueil.setVisible(false);
		}

		return txt;
	}

	/** Rafraichi la vue */
	private void refreshView() {
		final Authentication auth = userController.getCurrentAuthentication();
		if (userController.isAnonymous(auth)) {
			title = applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale());
		} else {
			title = applicationContext.getMessage("main.menu.accueil.title", null, UI.getCurrent().getLocale());
		}
		labelTitle.setValue(title);
		setTxtMessageAccueil(auth);
		refreshLayoutConnexion(auth);
		updateMentionCnil();
	}

	/** @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent) */
	@Override
	public void enter(final ViewChangeEvent event) {
		refreshView();
	}

}
