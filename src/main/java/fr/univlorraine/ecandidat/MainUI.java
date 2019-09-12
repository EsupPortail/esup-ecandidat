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
package fr.univlorraine.ecandidat;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.UploadException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.UIDetachedException;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.AlertSvaController;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.LoadBalancingController;
import fr.univlorraine.ecandidat.controllers.LockCandidatController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.TagController;
import fr.univlorraine.ecandidat.controllers.UiController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCommission;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.UIException;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.ReloadViewNavigator;
import fr.univlorraine.ecandidat.vaadin.menu.AccordionItemMenu;
import fr.univlorraine.ecandidat.vaadin.menu.AccordionMenu;
import fr.univlorraine.ecandidat.vaadin.menu.Menu;
import fr.univlorraine.ecandidat.vaadin.menu.SubMenu;
import fr.univlorraine.ecandidat.vaadin.menu.SubMenuBar;
import fr.univlorraine.ecandidat.views.AccueilView;
import fr.univlorraine.ecandidat.views.AdminBatchView;
import fr.univlorraine.ecandidat.views.AdminCacheView;
import fr.univlorraine.ecandidat.views.AdminDroitProfilIndView;
import fr.univlorraine.ecandidat.views.AdminLangueView;
import fr.univlorraine.ecandidat.views.AdminLockCandidatView;
import fr.univlorraine.ecandidat.views.AdminOpiView;
import fr.univlorraine.ecandidat.views.AdminParametreView;
import fr.univlorraine.ecandidat.views.AdminVersionView;
import fr.univlorraine.ecandidat.views.AdminView;
import fr.univlorraine.ecandidat.views.AssistanceView;
import fr.univlorraine.ecandidat.views.CandidatAdminView;
import fr.univlorraine.ecandidat.views.CandidatAdresseView;
import fr.univlorraine.ecandidat.views.CandidatBacView;
import fr.univlorraine.ecandidat.views.CandidatCandidaturesView;
import fr.univlorraine.ecandidat.views.CandidatCompteMinimaView;
import fr.univlorraine.ecandidat.views.CandidatCreerCompteView;
import fr.univlorraine.ecandidat.views.CandidatCursusExterneView;
import fr.univlorraine.ecandidat.views.CandidatCursusInterneView;
import fr.univlorraine.ecandidat.views.CandidatFormationProView;
import fr.univlorraine.ecandidat.views.CandidatInfoPersoView;
import fr.univlorraine.ecandidat.views.CandidatStageView;
import fr.univlorraine.ecandidat.views.CommissionCandidatureView;
import fr.univlorraine.ecandidat.views.CommissionParametreView;
import fr.univlorraine.ecandidat.views.CtrCandCandidatureArchivedView;
import fr.univlorraine.ecandidat.views.CtrCandCandidatureCanceledView;
import fr.univlorraine.ecandidat.views.CtrCandCandidatureView;
import fr.univlorraine.ecandidat.views.CtrCandCommissionView;
import fr.univlorraine.ecandidat.views.CtrCandFormationView;
import fr.univlorraine.ecandidat.views.CtrCandFormulaireCommunView;
import fr.univlorraine.ecandidat.views.CtrCandFormulaireView;
import fr.univlorraine.ecandidat.views.CtrCandMailTypeDecView;
import fr.univlorraine.ecandidat.views.CtrCandMotivAvisView;
import fr.univlorraine.ecandidat.views.CtrCandParametreView;
import fr.univlorraine.ecandidat.views.CtrCandPieceJustifCommunView;
import fr.univlorraine.ecandidat.views.CtrCandPieceJustifView;
import fr.univlorraine.ecandidat.views.CtrCandStatCommView;
import fr.univlorraine.ecandidat.views.CtrCandStatFormView;
import fr.univlorraine.ecandidat.views.CtrCandTagView;
import fr.univlorraine.ecandidat.views.CtrCandTypeDecisionView;
import fr.univlorraine.ecandidat.views.ErreurView;
import fr.univlorraine.ecandidat.views.MaintenanceView;
import fr.univlorraine.ecandidat.views.OffreFormationView;
import fr.univlorraine.ecandidat.views.ScolAlertSvaView;
import fr.univlorraine.ecandidat.views.ScolCampagneView;
import fr.univlorraine.ecandidat.views.ScolCentreCandidatureView;
import fr.univlorraine.ecandidat.views.ScolDroitProfilView;
import fr.univlorraine.ecandidat.views.ScolFaqView;
import fr.univlorraine.ecandidat.views.ScolFormulaireView;
import fr.univlorraine.ecandidat.views.ScolGestCandidatDroitProfilView;
import fr.univlorraine.ecandidat.views.ScolMailModelView;
import fr.univlorraine.ecandidat.views.ScolMailTypeDecView;
import fr.univlorraine.ecandidat.views.ScolMessageView;
import fr.univlorraine.ecandidat.views.ScolMotivAvisView;
import fr.univlorraine.ecandidat.views.ScolParametreView;
import fr.univlorraine.ecandidat.views.ScolPieceJustifView;
import fr.univlorraine.ecandidat.views.ScolTagView;
import fr.univlorraine.ecandidat.views.ScolTypeDecisionView;
import fr.univlorraine.ecandidat.views.ScolTypeStatutPieceView;
import fr.univlorraine.ecandidat.views.ScolTypeStatutView;
import fr.univlorraine.ecandidat.views.ScolTypeTraitementView;
import fr.univlorraine.ecandidat.views.windows.SearchCandidatWindow;
import fr.univlorraine.ecandidat.views.windows.SearchCommissionWindow;
import fr.univlorraine.ecandidat.views.windows.SearchCtrCandWindow;
import fr.univlorraine.tools.vaadin.IAnalyticsTracker;
import fr.univlorraine.tools.vaadin.LogAnalyticsTracker;
import fr.univlorraine.tools.vaadin.PiwikAnalyticsTracker;
import lombok.Getter;

/**
 * UI principale
 * @author Adrien Colson
 */
@SuppressWarnings("serial")
@Theme("valo-ul")
@SpringUI(path = "/*")
@Push(value = PushMode.AUTOMATIC)
public class MainUI extends UI {

	/** Nombre maximum de tentatives de reconnexion lors d'une déconnexion. */
	private static final int TENTATIVES_RECO = 3;

	/* Redirige java.util.logging vers SLF4j */
	static {
		SLF4JBridgeHandler.install();
	}

	private final Logger logger = LoggerFactory.getLogger(MainUI.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient UiController uiController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient AlertSvaController alertSvaController;
	@Resource
	private transient TagController tagController;
	@Resource
	private transient LockCandidatController lockCandidatController;

	/* Propriétés */
	@Value("${app.name}")
	private String appName;
	@Value("${app.version}")
	private String appVersion;
	@Value("${demoMode}")
	private String demoMode;

	@Value("${piwikAnalytics.trackerUrl:}")
	private transient String piwikAnalyticsTrackerUrl;
	@Value("${piwikAnalytics.siteId:}")
	private transient String piwikAnalyticsSiteId;

	@Value("${pushTransportMode:}")
	private transient String pushTransportMode;

	@Value("${sessionTimeOut:}")
	private transient String sessionTimeOut;

	/* Composants */
	private final CssLayout menu = new CssLayout();
	private final CssLayout menuLayout = new CssLayout(menu);
	private final CssLayout menuButtonLayout = new CssLayout();
	private final CssLayout contentLayout = new CssLayout();
	private final CssLayout layoutWithSheet = new CssLayout();
	private final HorizontalLayout layout = new HorizontalLayout(menuLayout, layoutWithSheet);
	private final SubMenuBar subBarMenu = new SubMenuBar();

	private OneClickButton lastButtonView;
	private AccordionMenu accordionMenu;
	private AccordionItemMenu itemMenuCtrCand;
	private AccordionItemMenu itemMenuCommission;
	private AccordionItemMenu itemMenuGestCandidat;
	private OneClickButton changeCandBtn;
	private OneClickButton createCandBtn;

	/** The view provider. */
	@Resource
	private SpringViewProvider viewProvider;
	@Getter
	private IAnalyticsTracker analyticsTracker;

	/** Gestionnaire de vues */
	private final ReloadViewNavigator navigator = new ReloadViewNavigator(this, contentLayout);

	/** Nom de la dernière vue visitée */
	private String currentViewName = null;

	/** Noms des vues et boutons du menu associés */
	private final Map<String, Menu> viewButtons = new HashMap<>();

	/** Noms des vues et numéro accordeon associé */
	private final Map<String, String> viewAccordion = new HashMap<>();

	/** Noms des vues et numéro accordeon associé */
	private final Map<String, String> viewAccordionCtrCand = new HashMap<>();

	/** Noms des vues et numéro accordeon associé */
	private final Map<String, String> viewAccordionCommission = new HashMap<>();

	/** Noms des vues et numéro accordeon associé */
	private final Map<String, String> viewAccordionGestCandidat = new HashMap<>();

	/** Temoin permettant de savoir si on a déjà ajouté les alertes SVA : à n'ajouter qu'une fois!! */
	private Boolean isSvaAlertDisplay = false;

	/** Les infos en cours d'edition */
	private Integer idCtrCandEnCours = null;
	private Integer idCommissionEnCours = null;
	private String noDossierCandidatEnCours = null;

	/** ID de l'UI pour les locks */
	private String uiId = null;

	private static final String SELECTED_ITEM = "selected";

	/**
	 * @see    com.vaadin.ui.UI#getCurrent()
	 * @return MainUI courante
	 */
	public static MainUI getCurrent() {
		return (MainUI) UI.getCurrent();
	}

	/** @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest) */
	@Override
	protected void init(final VaadinRequest vaadinRequest) {

		/* Configuration du timeout */
		configTimeOut();

		/* Configuration du push */
		configPush();

		/* Log les erreurs non gerees */
		configError();

		configUiId();

		configReconnectDialog();

		/* Affiche le nom de l'application dans l'onglet du navigateur */
		getPage().setTitle(appName);

		initLayout();

		initNavigator();

		initAnalyticsTracker();

		initLanguage();

		buildTitle();

		buildMenu();

		/* Enregistre l'UI pour la réception de notifications */
		uiController.registerUI(this);
	}

	/** Configure la gestion des erreurs */
	private void configError() {
		/* Log les erreurs non gerees */
		VaadinSession.getCurrent().setErrorHandler(e -> {
			Throwable cause = e.getThrowable();
			while (cause instanceof Throwable) {
				/* Gère les accès non autorisés */
				if (cause instanceof AccessDeniedException) {
					navigateToView(ErreurView.NAME);
					return;
				}
				/* Gère les UIs détachées pour les utilisateurs déconnectés */
				if (cause instanceof AuthenticationCredentialsNotFoundException || cause instanceof UIDetachedException
					|| cause instanceof UploadException
					|| cause instanceof IllegalStateException
					|| cause instanceof SocketTimeoutException
					|| MethodUtils.checkCause(cause, "SocketTimeoutException")
					|| MethodUtils.checkCause(cause, "ClientAbortException")
					|| cause instanceof EOFException
					|| cause instanceof URISyntaxException
					|| cause instanceof UIException) {
					sendError();
					return;
				}
				if (MethodUtils.checkCauseByStackTrace(cause, "FileUploadHandler", 0) || MethodUtils.checkCauseByStackTrace(cause, "OnDemandPdfBrowserOpener", 1)
					|| MethodUtils.checkCauseByStackTrace(cause, "DownloadStream", 3)
					|| MethodUtils.checkCauseByStackTrace(cause, "AtmosphereRequest", 7)
					|| MethodUtils.checkCauseByStackTrace(cause, "AbstractTextField", 0)
					|| MethodUtils.checkCauseByStackTrace(cause, "SocketChannelImpl", 4)
					|| (cause instanceof CmisRuntimeException && MethodUtils.checkCauseByMessage(cause, "Bad Gateway"))
					|| MethodUtils.checkCauseEmpty(cause)) {
					sendError();
					return;
				}
				cause = cause.getCause();
			}

			sendError();
			logger.error("Erreur inconnue", e.getThrowable());
		});
	}

	/** Envoi une notif d'erreur si possible */
	private void sendError() {
		try {
			if (Page.getCurrent() != null) {
				Notification.show("Une erreur est survenue");
			}
		} catch (final Exception e) {
		}
	}

	/** Configuration du timeOut */
	private void configTimeOut() {
		if (getSession() != null && getSession().getSession() != null && sessionTimeOut != null) {
			try {
				getSession().getSession().setMaxInactiveInterval(Integer.valueOf(sessionTimeOut));
			} catch (final Exception e) {
			}
		}
	}

	/** Configuration du push */
	private void configPush() {
		if (pushTransportMode != null && pushTransportMode.equals(Transport.LONG_POLLING.getIdentifier())) {
			getPushConfiguration().setTransport(Transport.LONG_POLLING);
		} else if (pushTransportMode != null && pushTransportMode.equals(Transport.WEBSOCKET_XHR.getIdentifier())) {
			getPushConfiguration().setTransport(Transport.WEBSOCKET_XHR);
		} else {
			getPushConfiguration().setTransport(Transport.WEBSOCKET);
		}
	}

	/** Genere l'id de l'ui */
	private void configUiId() {
		if (getSession() == null || getSession().getSession() == null || getSession().getSession().getId() == null) {
			return;
		} else {
			uiId = getSession().getSession().getId() + "-" + getUIId();
		}
	}

	/** @return l'id de l'UI */
	public String getUiId() {
		return uiId;
	}

	/** Initialise la langue */
	private void initLanguage() {
		i18nController.initLanguageUI(false);
	}

	/** Initialise le layout principal */
	private void initLayout() {
		layout.setSizeFull();
		setContent(layout);

		menuLayout.setPrimaryStyleName(ValoTheme.MENU_ROOT);

		layoutWithSheet.setPrimaryStyleName(StyleConstants.VALO_CONTENT);
		layoutWithSheet.addStyleName(StyleConstants.SCROLLABLE);
		layoutWithSheet.setSizeFull();

		final VerticalLayout vlAll = new VerticalLayout();
		vlAll.addStyleName(StyleConstants.SCROLLABLE);
		vlAll.setSizeFull();

		subBarMenu.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		subBarMenu.setVisible(false);
		vlAll.addComponent(subBarMenu);

		contentLayout.addStyleName(StyleConstants.SCROLLABLE);
		contentLayout.setSizeFull();
		vlAll.addComponent(contentLayout);
		vlAll.setExpandRatio(contentLayout, 1);

		layoutWithSheet.addComponent(vlAll);

		menuButtonLayout.addStyleName(StyleConstants.VALO_MY_MENU_MAX_WIDTH);
		layout.setExpandRatio(layoutWithSheet, 1);

		Responsive.makeResponsive(this);
		addStyleName(ValoTheme.UI_WITH_MENU);
	}

	/** Va à la vue */
	public void navigateToView(final String name) {
		if (name.equals(currentViewName)) {
			navigator.changeCurrentView();
		}
		navigator.navigateTo(name);
	}

	/** Retourne à l'accueil */
	public void navigateToAccueilView() {
		navigateToView(AccueilView.NAME);
	}

	/** Construit le titre de l'application */
	private void buildTitle() {
		final OneClickButton itemBtn = new OneClickButton(appName, new ThemeResource("logo.png"));
		String demo = "";
		if (demoMode != null && Boolean.valueOf(demoMode)) {
			demo = " - Version Demo";
		}
		itemBtn.setDescription(appVersion + demo);
		itemBtn.setPrimaryStyleName(ValoTheme.MENU_TITLE);
		itemBtn.addStyleName(ValoTheme.MENU_ITEM);
		itemBtn.addClickListener(e -> getNavigator().navigateTo(AccueilView.NAME));
		menu.addComponent(itemBtn);
	}

	/** Construit le menu */
	private void buildMenu() {
		menu.addStyleName(ValoTheme.MENU_PART);

		final OneClickButton showMenu = new OneClickButton(applicationContext.getMessage("mainUi.menu", null, getLocale()), FontAwesome.LIST);
		showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
		showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
		showMenu.addStyleName(StyleConstants.VALO_MENU_TOGGLE);
		showMenu.addClickListener(e -> {
			if (menu.getStyleName().contains(StyleConstants.VALO_MENU_VISIBLE)) {
				menu.removeStyleName(StyleConstants.VALO_MENU_VISIBLE);
			} else {
				menu.addStyleName(StyleConstants.VALO_MENU_VISIBLE);
			}

		});
		menu.addComponent(showMenu);

		menuButtonLayout.setPrimaryStyleName(StyleConstants.VALO_MENUITEMS);
		menu.addComponent(menuButtonLayout);

		constructMainMenu();
	}

	/** Reconstruit le menu apres une connexion */
	public void reconstructMainMenu() {
		constructMainMenu();
		navigateToAccueilView();
	}

	/** Construit tout les boutons */
	public void constructMainMenu() {
		/* On recupere l'authentification */
		final Authentication auth = userController.getCurrentAuthentication();

		menuButtonLayout.removeAllComponents();

		/* Titre: Username */
		final Label usernameLabel = new Label(userController.getCurrentUserName(auth));
		usernameLabel.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
		usernameLabel.setSizeUndefined();
		menuButtonLayout.addComponent(usernameLabel);

		/* Accueil */
		if (userController.isAnonymous(auth) && !loadBalancingController.isLoadBalancingGestionnaireMode()) {
			final LinkedList<SubMenu> subMenuAccueil = new LinkedList<>();
			subMenuAccueil.add(new SubMenu(AccueilView.NAME, FontAwesome.POWER_OFF));
			subMenuAccueil.add(new SubMenu(CandidatCreerCompteView.NAME, FontAwesome.MAGIC));
			addItemMenu(applicationContext.getMessage("main.menu.accueil.title", null, getLocale()), null, FontAwesome.HOME, subMenuAccueil, null);
		} else {
			addItemMenu(applicationContext.getMessage("main.menu.accueil.title", null, getLocale()), AccueilView.NAME, FontAwesome.HOME, null, null);
		}

		/* Assistance */
		addItemMenu(applicationContext.getMessage(AssistanceView.NAME + ".title", null, getLocale()), AssistanceView.NAME, FontAwesome.AMBULANCE, null, null);

		/* Accueil */
		addItemMenu(applicationContext.getMessage(OffreFormationView.NAME + ".title", null, getLocale()), OffreFormationView.NAME, FontAwesome.BOOKMARK, null, null);

		/* Bouton de connexion */
		if (userController.isAnonymous(auth)) {
			final OneClickButton itemBtn = new OneClickButton(applicationContext.getMessage("btnConnect", null, getLocale()), FontAwesome.POWER_OFF);
			itemBtn.addClickListener(e -> userController.connectCAS());
			itemBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
			menuButtonLayout.addComponent(itemBtn);
		} else {
			final OneClickButton itemBtn = new OneClickButton(applicationContext.getMessage("btnDisconnect", null, getCurrent().getLocale()), FontAwesome.POWER_OFF);
			itemBtn.addClickListener(e -> {
				userController.deconnect();
			});
			itemBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
			menuButtonLayout.addComponent(itemBtn);
		}

		/* Bouton permettant de rétablir l'utilisateur ayant changé de rôle */
		if (userController.isUserSwitched()) {
			final OneClickButton btnSwitchUserBack = new OneClickButton(applicationContext.getMessage("admin.switchUser.btnSwitchUserBack", null, getLocale()), FontAwesome.UNDO);
			btnSwitchUserBack.setPrimaryStyleName(ValoTheme.MENU_ITEM);
			btnSwitchUserBack.addClickListener(e -> userController.switchBackToPreviousUser());
			menuButtonLayout.addComponent(btnSwitchUserBack);
		}

		accordionMenu = new AccordionMenu();
		menuButtonLayout.addComponent(accordionMenu);

		final Boolean isCandidatMode = loadBalancingController.isLoadBalancingCandidatMode();

		if (!isCandidatMode) {
			/* Bouton vers la vue Admin */
			if (userController.canCurrentUserAccessView(AdminView.class, auth)) {
				final AccordionItemMenu itemMenuAdmin = new AccordionItemMenu(applicationContext.getMessage("admin.mainmenu", null, getLocale()), accordionMenu);
				accordionMenu.addItemMenu(itemMenuAdmin, ConstanteUtils.UI_MENU_ADMIN);

				final LinkedList<SubMenu> subMenuParametrage = new LinkedList<>();
				subMenuParametrage.add(new SubMenu(AdminParametreView.NAME, FontAwesome.COGS));
				subMenuParametrage.add(new SubMenu(AdminLangueView.NAME, FontAwesome.FLAG));
				subMenuParametrage.add(new SubMenu(AdminVersionView.NAME, FontAwesome.COG));
				subMenuParametrage.add(new SubMenu(AdminCacheView.NAME, FontAwesome.DATABASE));
				addItemMenu(applicationContext.getMessage(AdminParametreView.NAME + ".title", null, getLocale()), null, FontAwesome.COGS, subMenuParametrage, itemMenuAdmin);
				addItemMenu(applicationContext.getMessage(AdminBatchView.NAME + ".title", null, getLocale()), AdminBatchView.NAME, FontAwesome.ROCKET, null, itemMenuAdmin);

				final LinkedList<SubMenu> subMenuSession = new LinkedList<>();
				subMenuSession.add(new SubMenu(AdminView.NAME, FontAwesome.WRENCH));
				subMenuSession.add(new SubMenu(AdminLockCandidatView.NAME, FontAwesome.LOCK));

				addItemMenu(applicationContext.getMessage(AdminView.NAME + ".title", null, getLocale()), null, FontAwesome.WRENCH, subMenuSession, itemMenuAdmin);
				addItemMenu(applicationContext.getMessage(ScolCampagneView.NAME + ".title", null, getLocale()), ScolCampagneView.NAME, FontAwesome.STAR, null, itemMenuAdmin);
				addItemMenu(applicationContext.getMessage(AdminDroitProfilIndView.NAME + ".title", null, getLocale()), AdminDroitProfilIndView.NAME, FontAwesome.SHIELD, null, itemMenuAdmin);

				/* Opi */
				if (parametreController.getIsUtiliseOpi() || parametreController.getIsUtiliseOpiPJ()) {
					addItemMenu(applicationContext.getMessage(AdminOpiView.NAME + ".title", null, getLocale()), AdminOpiView.NAME, FontAwesome.GRADUATION_CAP, null, itemMenuAdmin);
				}
			}

			/* Bouton vers la vue Scol centrale */
			if (userController.canCurrentUserAccessView(ScolMailModelView.class, auth)) {
				/* Menu maitre Scol */
				final AccordionItemMenu itemMenuScol = new AccordionItemMenu(applicationContext.getMessage("scolcentrale.mainmenu", null, getLocale()), accordionMenu);
				accordionMenu.addItemMenu(itemMenuScol, ConstanteUtils.UI_MENU_SCOL);

				/* Menu parametres */
				addItemMenu(applicationContext.getMessage(ScolParametreView.NAME + ".title", null, getLocale()), ScolParametreView.NAME, FontAwesome.COGS, null, itemMenuScol);

				/* Menu droit/profil */
				final LinkedList<SubMenu> subMenuDroits = new LinkedList<>();
				subMenuDroits.add(new SubMenu(ScolDroitProfilView.NAME, FontAwesome.USER));
				subMenuDroits.add(new SubMenu(ScolGestCandidatDroitProfilView.NAME, FontAwesome.USERS));
				addItemMenu(applicationContext.getMessage("scolDroitProfilMenu.title", null, getLocale()), null, FontAwesome.USER, subMenuDroits, itemMenuScol);

				/* Menu mails */
				addItemMenu(applicationContext.getMessage(ScolMailModelView.NAME + ".title", null, getLocale()), ScolMailModelView.NAME, FontAwesome.ENVELOPE_O, null, itemMenuScol);

				/* Paramétrages décisions */
				final LinkedList<SubMenu> subMenuParamDecision = new LinkedList<>();
				subMenuParamDecision.add(new SubMenu(ScolMailTypeDecView.NAME, FontAwesome.ENVELOPE));
				subMenuParamDecision.add(new SubMenu(ScolTypeDecisionView.NAME, FontAwesome.GAVEL));
				subMenuParamDecision.add(new SubMenu(ScolMotivAvisView.NAME, FontAwesome.BALANCE_SCALE));
				addItemMenu(applicationContext.getMessage("paramDecision.menus.title", null, getLocale()), null, FontAwesome.GAVEL, subMenuParamDecision, itemMenuScol);

				/* Menu Centre Candidature */
				addItemMenu(applicationContext.getMessage(ScolCentreCandidatureView.NAME + ".title", null, getLocale()), ScolCentreCandidatureView.NAME, FontAwesome.BANK, null, itemMenuScol);

				/* Menu pj */
				addItemMenu(applicationContext.getMessage(ScolPieceJustifView.NAME + ".title", null, getLocale()), ScolPieceJustifView.NAME, FontAwesome.FILE_TEXT_O, null, itemMenuScol);

				/* Menu formulaires */
				addItemMenu(applicationContext.getMessage(ScolFormulaireView.NAME + ".title", null, getLocale()), ScolFormulaireView.NAME, FontAwesome.FILE_ZIP_O, null, itemMenuScol);

				/* Menu alertes */
				final LinkedList<SubMenu> subMenuAlert = new LinkedList<>();
				subMenuAlert.add(new SubMenu(ScolAlertSvaView.NAME, FontAwesome.BELL));
				subMenuAlert.add(new SubMenu(ScolTagView.NAME, FontAwesome.TAGS));
				addItemMenu(applicationContext.getMessage("scolAlert.title", null, getLocale()), null, FontAwesome.BELL, subMenuAlert, itemMenuScol);

				/* Menu message */
				addItemMenu(applicationContext.getMessage(ScolMessageView.NAME + ".title", null, getLocale()), ScolMessageView.NAME, FontAwesome.ENVELOPE, null, itemMenuScol);

				/* Menu nomenclature */
				final LinkedList<SubMenu> subMenuTypDec = new LinkedList<>();
				subMenuTypDec.add(new SubMenu(ScolTypeTraitementView.NAME, FontAwesome.BATTERY_QUARTER));
				subMenuTypDec.add(new SubMenu(ScolTypeStatutView.NAME, FontAwesome.BATTERY_HALF));
				subMenuTypDec.add(new SubMenu(ScolTypeStatutPieceView.NAME, FontAwesome.BATTERY_THREE_QUARTERS));
				subMenuTypDec.add(new SubMenu(ScolFaqView.NAME, FontAwesome.QUESTION_CIRCLE));
				addItemMenu(applicationContext.getMessage("scolNomenclature.title", null, getLocale()), null, FontAwesome.BATTERY_FULL, subMenuTypDec, itemMenuScol);

				/* Si on veut ajouter les stats globales, decommenter ci dessous. COmmenté car trop grosses requetes */
				// addItemMenu(applicationContext.getMessage("stat.menu.title", null, getLocale()), ScolStatView.NAME, FontAwesome.LINE_CHART, null, itemMenuScol);
			}

			/* Bouton vers la vue Centre de candidature */
			if (userController.canCurrentUserAccessView(CtrCandParametreView.class, auth)) {
				itemMenuCtrCand = new AccordionItemMenu(applicationContext.getMessage("ctrcand.mainmenu", null, getLocale()), accordionMenu);
				accordionMenu.addItemMenu(itemMenuCtrCand, ConstanteUtils.UI_MENU_CTR);
				buildMenuCtrCand(auth);
			}

			/* Bouton vers la vue Commission */
			if (userController.canCurrentUserAccessView(CommissionCandidatureView.class, auth)) {
				itemMenuCommission = new AccordionItemMenu(applicationContext.getMessage("commission.mainmenu", null, getLocale()), accordionMenu);
				accordionMenu.addItemMenu(itemMenuCommission, ConstanteUtils.UI_MENU_COMM);
				buildMenuCommission(auth);
			}

			/* Bouton vers la vue de gestion du candidat */
			final Boolean isGestionnaireCandidat = userController.isGestionnaireCandidat(auth);
			if (isGestionnaireCandidat || userController.isGestionnaireCandidatLS(auth)) {
				itemMenuGestCandidat = new AccordionItemMenu(applicationContext.getMessage("gestcand.mainmenu", null, getLocale()), accordionMenu);
				accordionMenu.addItemMenu(itemMenuGestCandidat, ConstanteUtils.UI_MENU_GEST_CAND);

				if (isGestionnaireCandidat) {
					createCandBtn = new OneClickButton(applicationContext.getMessage("btn.create.candidat", null, getLocale()), FontAwesome.PENCIL);
					createCandBtn.setDescription(applicationContext.getMessage("btn.create.candidat", null, getLocale()));
					createCandBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
					createCandBtn.addClickListener(e -> {
						candidatController.createCompteMinima(true);
					});
					itemMenuGestCandidat.addButton(createCandBtn);
				}

				/* Changement de candidat */
				changeCandBtn = new OneClickButton(applicationContext.getMessage("btn.find.candidat", null, getLocale()));
				changeCandBtn.setDescription(applicationContext.getMessage("btn.find.candidat", null, getLocale()));
				changeCandBtn.setIcon(FontAwesome.SEARCH);
				changeCandBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
				changeCandBtn.addClickListener(e -> {
					final SearchCandidatWindow win = new SearchCandidatWindow();
					win.addCompteMinimaListener(compteMinima -> {
						if (compteMinima != null) {
							noDossierCandidatEnCours = compteMinima.getNumDossierOpiCptMin();
							userController.setNoDossierNomCandidat(compteMinima);
							buildMenuGestCand(false);
						}
					});

					getCurrent().addWindow(win);
				});
				itemMenuGestCandidat.addButton(changeCandBtn);
				buildMenuGestCand(false, auth);
			}
		} else {
			/* Accès uniquement aux admins */
			if (userController.canCurrentUserAccessView(AdminView.class, auth)) {
				final AccordionItemMenu itemMenuAdmin = new AccordionItemMenu(applicationContext.getMessage("admin.mainmenu", null, getLocale()), accordionMenu);
				accordionMenu.addItemMenu(itemMenuAdmin, ConstanteUtils.UI_MENU_ADMIN);

				addItemMenu(applicationContext.getMessage(AdminVersionView.NAME + ".title", null, getLocale()), AdminVersionView.NAME, FontAwesome.COG, null, itemMenuAdmin);
				addItemMenu(applicationContext.getMessage(AdminView.NAME + ".title", null, getLocale()), AdminView.NAME, FontAwesome.WRENCH, null, itemMenuAdmin);
			}
		}

		accordionMenu.selectFirst();

		/* Gestion de candidature */
		if (userController.canCurrentUserAccessView(CandidatInfoPersoView.class, auth) && userController.isCandidatValid(auth)) {
			final AccordionItemMenu itemMenuCandidat = new AccordionItemMenu(applicationContext.getMessage("compte.main.menu", null, getLocale()), accordionMenu, false);
			accordionMenu.addItemMenu(itemMenuCandidat, ConstanteUtils.UI_MENU_CAND);
			buildMenuCandidat(itemMenuCandidat);
		}

		focusCurrentMenu(currentViewName);
		focusCurrentAccordion(currentViewName);
		reloadSubMenuBar();
	}

	/**
	 * Verifie la concordance du candidat en cours d'édition avec les menus
	 * @param  noDossierCandidat
	 * @return                   true si ok, false si nok
	 */
	public Boolean checkConcordanceCandidat(final String noDossierCandidat) {
		if (noDossierCandidatEnCours != null && noDossierCandidat != null && !noDossierCandidatEnCours.equals(noDossierCandidat)) {
			Notification.show(applicationContext.getMessage("cptMin.change.error", null, getLocale()));
			// constructMainMenu();
			buildMenuGestCand(true);
			return false;
		}
		return true;
	}

	/**
	 * Contruit le menu candidat
	 * @param itemMenu
	 *                     l'item de menu du candidat
	 */
	private void buildMenuCandidat(final AccordionItemMenu itemMenu) {
		final Boolean getCursusInterne = parametreController.getIsGetCursusInterne();
		addItemMenu(applicationContext.getMessage("candidatInfoPersoView.title.short", null, getLocale()), CandidatInfoPersoView.NAME, FontAwesome.PENCIL, null, itemMenu);
		addItemMenu(applicationContext.getMessage(CandidatAdresseView.NAME + ".title", null, getLocale()), CandidatAdresseView.NAME, FontAwesome.HOME, null, itemMenu);
		addItemMenu(applicationContext.getMessage(CandidatBacView.NAME + ".title", null, getLocale()), CandidatBacView.NAME, FontAwesome.BOOK, null, itemMenu);
		String txtCursusExterne;
		if (getCursusInterne) {
			addItemMenu(applicationContext.getMessage(CandidatCursusInterneView.NAME + ".title", null, getLocale()), CandidatCursusInterneView.NAME, FontAwesome.UNIVERSITY, null, itemMenu);
			txtCursusExterne = applicationContext.getMessage(CandidatCursusExterneView.NAME + ".title", null, getLocale());
		} else {
			txtCursusExterne = applicationContext.getMessage(CandidatCursusExterneView.NAME + ".title.withoutCursusInterne", null, getLocale());
		}
		addItemMenu(txtCursusExterne, CandidatCursusExterneView.NAME, FontAwesome.GRADUATION_CAP, null, itemMenu);
		addItemMenu(applicationContext.getMessage(CandidatStageView.NAME + ".title", null, getLocale()), CandidatStageView.NAME, FontAwesome.CUBE, null, itemMenu);
		addItemMenu(applicationContext.getMessage("candidatFormationProView.title.short", null, getLocale()), CandidatFormationProView.NAME, FontAwesome.CUBES, null, itemMenu);

		addItemMenu(applicationContext.getMessage("main.menu.candidature.title", null, getLocale()), CandidatCandidaturesView.NAME, FontAwesome.ASTERISK, null, itemMenu);

		/* On recupere l'authentification */
		final Authentication auth = userController.getCurrentAuthentication();
		final Boolean isGestionnaireCandidat = userController.isGestionnaireCandidat(auth);
		if (isGestionnaireCandidat || userController.isGestionnaireCandidatLS(auth)) {
			if (isGestionnaireCandidat) {
				addItemMenu(applicationContext.getMessage("gestcand.adminmenu", null, getLocale()), CandidatAdminView.NAME, FontAwesome.FLASH, null, itemMenu);
				viewAccordionGestCandidat.put(CandidatAdminView.NAME, (String) itemMenu.getData());
			}
			viewAccordionGestCandidat.put(CandidatInfoPersoView.NAME, (String) itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatAdresseView.NAME, (String) itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatBacView.NAME, (String) itemMenu.getData());
			if (getCursusInterne) {
				viewAccordionGestCandidat.put(CandidatCursusInterneView.NAME, (String) itemMenu.getData());
			}
			viewAccordionGestCandidat.put(CandidatCursusExterneView.NAME, (String) itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatStageView.NAME, (String) itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatFormationProView.NAME, (String) itemMenu.getData());
			viewAccordionGestCandidat.put(CandidatCandidaturesView.NAME, (String) itemMenu.getData());
		}
	}

	/**
	 * Construit le menu de gestion de candidature
	 * @param reloadConcordance
	 *                              si c'est un reload suite a la nonn concordance de candidat
	 */
	public void buildMenuGestCand(final Boolean reloadConcordance) {
		buildMenuGestCand(reloadConcordance, userController.getCurrentAuthentication());
	}

	/**
	 * Construit le menu de gestion de candidature
	 * @param reloadConcordance
	 *                              si c'est un reload suite a la nonn concordance de candidat
	 */
	private void buildMenuGestCand(final Boolean reloadConcordance, final Authentication auth) {
		final UserDetails details = userController.getCurrentUser(auth);
		final String noDossier = userController.getNoDossierCandidat(details);
		String name = userController.getDisplayNameCandidat(details);
		if (name == null || name.equals("")) {
			name = noDossier;
		}
		if (name != null && !name.equals("")) {
			noDossierCandidatEnCours = noDossier;
			changeCandBtn.setCaption(name);
			changeCandBtn.setIcon(null);
			if (itemMenuGestCandidat.getNbButton() <= 2) {
				buildMenuCandidat(itemMenuGestCandidat);
			}
			if (!reloadConcordance) {
				navigateToView(CandidatInfoPersoView.NAME);
			}
		} else {
			itemMenuGestCandidat.removeAllButtons(changeCandBtn, createCandBtn);
			viewAccordionGestCandidat.forEach((key, value) -> {
				viewButtons.remove(key);
				viewAccordion.remove(key);
			});
			viewAccordionGestCandidat.clear();
			changeCandBtn.setCaption(applicationContext.getMessage("btn.find.candidat", null, getLocale()));
			changeCandBtn.setIcon(FontAwesome.SEARCH);
			changeCandBtn.setVisible(true);
			navigateToView(AccueilView.NAME);
		}
	}

	/** Construit le menu centre de candidature */
	public void buildMenuCtrCand() {
		buildMenuCtrCand(userController.getCurrentAuthentication());
	}

	/** Construit le menu centre de candidature */
	private void buildMenuCtrCand(final Authentication auth) {
		itemMenuCtrCand.removeAllButtons();
		viewAccordionCtrCand.forEach((key, value) -> {
			viewButtons.remove(key);
			viewAccordion.remove(key);
		});
		viewAccordionCtrCand.clear();

		final SecurityCentreCandidature centreCandidature = userController.getCentreCandidature(auth);
		if (centreCandidature != null) {
			idCtrCandEnCours = centreCandidature.getIdCtrCand();
			final OneClickButton ctrCandBtn = constructCtrCandChangeBtn(centreCandidature.getLibCtrCand());
			ctrCandBtn.setDescription(applicationContext.getMessage("ctrCand.window.change", new Object[] { centreCandidature.getLibCtrCand() }, getLocale()));
			itemMenuCtrCand.addButton(ctrCandBtn);

			final Boolean isScolCentrale = userController.isScolCentrale(auth);

			final List<DroitProfilFonc> listFonctionnalite = centreCandidature.getListFonctionnalite();
			if (hasAccessToFonctionnalite(isScolCentrale, listFonctionnalite, NomenclatureUtils.FONCTIONNALITE_PARAM)) {
				final LinkedList<SubMenu> subMenuParam = new LinkedList<>();
				subMenuParam.add(new SubMenu(CtrCandParametreView.NAME, FontAwesome.COG));
				subMenuParam.add(new SubMenu(CtrCandTagView.NAME, FontAwesome.TAGS));

				addItemMenu(applicationContext.getMessage("param.menus.title", null, getLocale()), CtrCandStatFormView.NAME, FontAwesome.COGS, subMenuParam, itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandParametreView.NAME, (String) itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandTagView.NAME, (String) itemMenuCtrCand.getData());
			}

			/* Stats */
			if (hasAccessToFonctionnalite(isScolCentrale, listFonctionnalite, NomenclatureUtils.FONCTIONNALITE_STATS)) {
				final LinkedList<SubMenu> subMenuStats = new LinkedList<>();
				subMenuStats.add(new SubMenu(CtrCandStatFormView.NAME, FontAwesome.BAR_CHART));
				subMenuStats.add(new SubMenu(CtrCandStatCommView.NAME, FontAwesome.PIE_CHART));

				addItemMenu(applicationContext.getMessage("stat.menus.title", null, getLocale()), CtrCandStatFormView.NAME, FontAwesome.LINE_CHART, subMenuStats, itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandStatFormView.NAME, (String) itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandStatCommView.NAME, (String) itemMenuCtrCand.getData());
			}

			/* Paramétrage CC */
			if (userController.isMenuParamCCOpen(idCtrCandEnCours) && hasAccessToFonctionnalite(isScolCentrale, listFonctionnalite, NomenclatureUtils.FONCTIONNALITE_GEST_PARAM_CC)) {

				final LinkedList<SubMenu> subMenuParamCC = new LinkedList<>();
				subMenuParamCC.add(new SubMenu(CtrCandMailTypeDecView.NAME, FontAwesome.ENVELOPE));
				subMenuParamCC.add(new SubMenu(CtrCandTypeDecisionView.NAME, FontAwesome.GAVEL));
				subMenuParamCC.add(new SubMenu(CtrCandMotivAvisView.NAME, FontAwesome.BALANCE_SCALE));

				addItemMenu(applicationContext.getMessage("paramDecision.menus.title", null, getLocale()), CtrCandTypeDecisionView.NAME, FontAwesome.GAVEL, subMenuParamCC, itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandMailTypeDecView.NAME, (String) itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandTypeDecisionView.NAME, (String) itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandMotivAvisView.NAME, (String) itemMenuCtrCand.getData());
			}

			/* Commission */
			if (hasAccessToFonctionnalite(isScolCentrale, listFonctionnalite, NomenclatureUtils.FONCTIONNALITE_GEST_COMMISSION)) {
				addItemMenu(applicationContext.getMessage(CtrCandCommissionView.NAME + ".title", null, getLocale()), CtrCandCommissionView.NAME, FontAwesome.CALENDAR, null, itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandCommissionView.NAME, (String) itemMenuCtrCand.getData());
			}

			/* PJ */
			if (hasAccessToFonctionnalite(isScolCentrale, listFonctionnalite, NomenclatureUtils.FONCTIONNALITE_GEST_PJ)) {
				final LinkedList<SubMenu> subMenuPj = new LinkedList<>();
				subMenuPj.add(new SubMenu(CtrCandPieceJustifView.NAME, FontAwesome.FILE_TEXT_O));
				subMenuPj.add(new SubMenu(CtrCandPieceJustifCommunView.NAME, FontAwesome.FILES_O));

				addItemMenu(applicationContext.getMessage(CtrCandPieceJustifView.NAME + ".title", null, getLocale()), CtrCandPieceJustifView.NAME, FontAwesome.FILE_TEXT_O, subMenuPj, itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandPieceJustifView.NAME, (String) itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandPieceJustifCommunView.NAME, (String) itemMenuCtrCand.getData());
			}

			/* Formulaires */
			if (hasAccessToFonctionnalite(isScolCentrale, listFonctionnalite, NomenclatureUtils.FONCTIONNALITE_GEST_FORMULAIRE)) {
				final LinkedList<SubMenu> subMenuForm = new LinkedList<>();
				subMenuForm.add(new SubMenu(CtrCandFormulaireView.NAME, FontAwesome.FILE_ZIP_O));
				subMenuForm.add(new SubMenu(CtrCandFormulaireCommunView.NAME, FontAwesome.FILES_O));

				addItemMenu(applicationContext.getMessage(CtrCandFormulaireView.NAME + ".title", null, getLocale()), CtrCandFormulaireView.NAME, FontAwesome.FILE_ZIP_O, subMenuForm, itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandFormulaireView.NAME, (String) itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandFormulaireCommunView.NAME, (String) itemMenuCtrCand.getData());
			}

			/* Formation */
			if (hasAccessToFonctionnalite(isScolCentrale, listFonctionnalite, NomenclatureUtils.FONCTIONNALITE_GEST_FORMATION)) {
				addItemMenu(applicationContext.getMessage(CtrCandFormationView.NAME + ".title", null, getLocale()), CtrCandFormationView.NAME, FontAwesome.LEAF, null, itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandFormationView.NAME, (String) itemMenuCtrCand.getData());
			}

			/* Candidatures */
			if (hasAccessToFonctionnalite(isScolCentrale, listFonctionnalite, NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE)) {
				final LinkedList<SubMenu> subMenuCandidatures = new LinkedList<>();
				subMenuCandidatures.add(new SubMenu(CtrCandCandidatureView.NAME, FontAwesome.BRIEFCASE));
				subMenuCandidatures.add(new SubMenu(CtrCandCandidatureCanceledView.NAME, FontAwesome.WARNING));
				subMenuCandidatures.add(new SubMenu(CtrCandCandidatureArchivedView.NAME, FontAwesome.FOLDER_OPEN));

				addItemMenu(applicationContext.getMessage(CtrCandCandidatureView.NAME + ".title", null, getLocale()),
					CtrCandCandidatureView.NAME,
					FontAwesome.BRIEFCASE,
					subMenuCandidatures,
					itemMenuCtrCand);
				viewAccordionCtrCand.put(CtrCandCandidatureView.NAME, (String) itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandCandidatureCanceledView.NAME, (String) itemMenuCtrCand.getData());
				viewAccordionCtrCand.put(CtrCandCandidatureArchivedView.NAME, (String) itemMenuCtrCand.getData());

				/* L'utilisateur a accès aux ecran de candidature-->on ajoute les css */
				initAlertSva();
			}
		} else {
			final OneClickButton ctrCandBtn = constructCtrCandChangeBtn(applicationContext.getMessage("ctrCand.window.change.default", null, getLocale()));
			itemMenuCtrCand.addButton(ctrCandBtn);
		}
	}

	/**
	 * Construit le bouton de recherche de centre
	 * @param  libelle
	 *                     le libelle du bouton
	 * @return         le bouton de recherche
	 */
	private OneClickButton constructCtrCandChangeBtn(final String libelle) {
		final OneClickButton ctrCandBtn = new OneClickButton(libelle);
		ctrCandBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
		ctrCandBtn.addClickListener(e -> {
			final SearchCtrCandWindow win = new SearchCtrCandWindow();
			win.addCentreCandidatureListener(centre -> {
				userController.setCentreCandidature(centre);
				buildMenuCtrCand();
				navigateToView(AccueilView.NAME);
				idCtrCandEnCours = centre.getIdCtrCand();
			});
			getCurrent().addWindow(win);
		});
		return ctrCandBtn;
	}

	/**
	 * Vérifie que le centre de candidature en cours d'edition est le même que celui dans la session
	 * @param  ctrCand
	 * @return         true si ok
	 */
	public Boolean checkConcordanceCentreCandidature(final CentreCandidature ctrCand) {
		if (idCtrCandEnCours != null && (ctrCand == null || (ctrCand != null && !idCtrCandEnCours.equals(ctrCand.getIdCtrCand())))) {
			Notification.show(applicationContext.getMessage("ctrCand.change.error", null, getLocale()));
			buildMenuCtrCand();
			return false;
		}
		return true;
	}

	/** Construit le menu de commission */
	public void buildMenuCommission() {
		buildMenuCommission(userController.getCurrentAuthentication());
	}

	/** Construit le menu de commission */
	private void buildMenuCommission(final Authentication auth) {
		itemMenuCommission.removeAllButtons();
		viewAccordionCommission.forEach((key, value) -> {
			viewButtons.remove(key);
			viewAccordion.remove(key);
		});
		viewAccordionCommission.clear();

		final SecurityCommission commission = userController.getCommission(auth);
		if (commission != null) {
			final Boolean isScolCentrale = userController.isScolCentrale(auth);
			final List<DroitProfilFonc> listFonctionnalite = commission.getListFonctionnalite();
			idCommissionEnCours = commission.getIdComm();

			final OneClickButton commissionBtn = constructCommissionChangeBtn(commission.getLibComm());
			commissionBtn.setDescription(applicationContext.getMessage("commission.window.change", new Object[] { commission.getLibComm() }, getLocale()));
			itemMenuCommission.addButton(commissionBtn);

			if (hasAccessToFonctionnalite(isScolCentrale, listFonctionnalite, NomenclatureUtils.FONCTIONNALITE_PARAM)) {
				addItemMenu(applicationContext.getMessage(CommissionParametreView.NAME + ".title", null, getLocale()), CommissionParametreView.NAME, FontAwesome.COG, null, itemMenuCommission);
				viewAccordionCommission.put(CommissionParametreView.NAME, (String) itemMenuCommission.getData());
			}

			if (hasAccessToFonctionnalite(isScolCentrale, commission.getListFonctionnalite(), NomenclatureUtils.FONCTIONNALITE_GEST_CANDIDATURE)) {
				addItemMenu(applicationContext.getMessage(CommissionCandidatureView.NAME + ".title", null, getLocale()),
					CommissionCandidatureView.NAME,
					FontAwesome.BRIEFCASE,
					null,
					itemMenuCommission);
				viewAccordionCommission.put(CommissionCandidatureView.NAME, (String) itemMenuCommission.getData());
				/* L'utilisateur a accès aux ecran de candidature-->on ajoute les alertes SVA */
				initAlertSva();
			}
		} else {
			final OneClickButton commissionBtn = constructCommissionChangeBtn(applicationContext.getMessage("commission.window.change.default", null, getLocale()));
			itemMenuCommission.addButton(commissionBtn);
		}
	}

	/**
	 * Construit le bouton de recherche de commission
	 * @param  libelle
	 *                     le libellé du bouton
	 * @return         le bouton de recherche
	 */
	private OneClickButton constructCommissionChangeBtn(final String libelle) {
		final OneClickButton commissionBtn = new OneClickButton(libelle);
		commissionBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
		commissionBtn.addClickListener(e -> {
			final SearchCommissionWindow win = new SearchCommissionWindow(null);
			win.addCommissionListener(comm -> {
				userController.setCommission(comm);
				buildMenuCommission();
				navigateToView(AccueilView.NAME);
				idCommissionEnCours = comm.getIdComm();
			});
			getCurrent().addWindow(win);
		});
		return commissionBtn;
	}

	/**
	 * Verifie la concordance de la commission en cours d'édition avec la commission en session
	 * @param  commission
	 * @return            true si la concordance est ok
	 */
	public Boolean checkConcordanceCommission(final Commission commission) {
		if (idCommissionEnCours != null && (commission == null || (commission != null && !idCommissionEnCours.equals(commission.getIdComm())))) {
			Notification.show(applicationContext.getMessage("commission.change.error", null, getLocale()));
			buildMenuCommission();
			return false;
		}
		return true;
	}

	/**
	 * Verifie si l'utilisateur a le droit d'accéder à la fonctionnalite
	 * @param  isAdmin
	 *                                est-il admin
	 * @param  listFonctionnalite
	 *                                la liste des fonctionnalite du gestionnaire
	 * @param  codFonc
	 *                                le code de la fonctionnalite a tester
	 * @return                    true si il a acces, false sinon
	 */
	private Boolean hasAccessToFonctionnalite(final Boolean isScolCentrale, final List<DroitProfilFonc> listFonctionnalite, final String codFonc) {
		if (isScolCentrale) {
			return true;
		}
		if (listFonctionnalite != null && listFonctionnalite.stream().filter(e -> e.getDroitFonctionnalite().getCodFonc().equals(codFonc)).findFirst().isPresent()) {
			return true;
		}
		return false;
	}

	/**
	 * Ajout d'un menu d'item avec ou sans sous menu
	 * @param caption
	 *                       le libelle
	 * @param viewName
	 *                       la vue rattachee
	 * @param icon
	 *                       l'icon du menu
	 * @param itemMenu
	 *                       l'item menu rattache
	 * @param mapSubMenu
	 *                       un eventuel sous-menu
	 */
	private void addItemMenu(final String caption, final String viewName, final com.vaadin.server.Resource icon, final LinkedList<SubMenu> subMenus, final AccordionItemMenu itemMenu) {
		final OneClickButton itemBtn = new OneClickButton(caption, icon);
		final Menu menu = new Menu(viewName, subMenus, itemBtn);
		itemBtn.setPrimaryStyleName(ValoTheme.MENU_ITEM);
		/* Pas de sous menu */
		if (subMenus == null) {
			itemBtn.addClickListener(e -> {
				navigateToView(viewName);
			});
			viewButtons.put(viewName, menu);
			if (itemMenu != null) {
				viewAccordion.put(viewName, (String) itemMenu.getData());
			}
		}
		/* Des sous menu, on associe le bouton du menu à chaque vue de sous menu */
		else {
			subMenus.forEach(e -> {
				viewButtons.put(e.getVue(), menu);
				if (itemMenu != null) {
					viewAccordion.put(e.getVue(), (String) itemMenu.getData());
				}
			});
			itemBtn.addClickListener(e -> {
				navigateToView(subMenus.getFirst().getVue());
			});

		}
		if (itemMenu == null) {
			menuButtonLayout.addComponent(itemBtn);
		} else {
			itemMenu.addButton(itemBtn);
		}

	}

	/**
	 * Construction du sous-menu
	 * @param menu
	 *                 le menu
	 * @param vue
	 *                 la vue rattachee
	 */
	private void contructSubMenu(final Menu menu, final String vue) {
		if (menu.hasSubMenu()) {
			/* Si le menu n'a pas déjà été créé lors de la dernière action */
			if (lastButtonView == null || !lastButtonView.equals(menu.getBtn())) {
				subBarMenu.constructMenuBar(menu, navigator, vue);
			} else {
				// on bouge vers la vue
				subBarMenu.selectSubMenuSheet(menu, vue, navigator, true);
			}
			subBarMenu.setVisible(true);
		} else {
			subBarMenu.setVisible(false);
		}
		/* On stocke le dernier bouton cliqué pour ne pas avoir à reconstruire le menu à chaque fois */
		lastButtonView = menu.getBtn();
	}

	/** Configure la reconnexion en cas de déconnexion. */
	private void configReconnectDialog() {
		getReconnectDialogConfiguration().setDialogModal(true);
		getReconnectDialogConfiguration().setReconnectAttempts(TENTATIVES_RECO);
		configReconnectDialogMessages();
	}

	/** Modifie les messages de reconnexion */
	public void configReconnectDialogMessages() {
		getReconnectDialogConfiguration().setDialogText(applicationContext.getMessage("vaadin.reconnectDialog.text", null, getLocale()));
		getReconnectDialogConfiguration().setDialogTextGaveUp(applicationContext.getMessage("vaadin.reconnectDialog.textGaveUp", null, getLocale()));
	}

	/** Initialise le gestionnaire de vues */
	private void initNavigator() {
		navigator.addProvider(viewProvider);
		navigator.setErrorProvider(new ViewProvider() {

			@Override
			public String getViewName(final String viewAndParameters) {
				return ErreurView.NAME;
			}

			@Override
			public View getView(final String viewName) {
				return viewProvider.getView(ErreurView.NAME);
			}
		});
		navigator.addViewChangeListener(new ViewChangeListener() {

			@Override
			public boolean beforeViewChange(final ViewChangeEvent event) {
				if (!event.getViewName().equals(AccueilView.NAME) && !event.getViewName().equals(ErreurView.NAME)
					&& !event.getViewName().equals(CandidatCompteMinimaView.NAME)
					&& !event.getViewName().equals(MaintenanceView.NAME)
					&& !viewButtons.containsKey(event.getViewName())) {
					navigateToView(ErreurView.NAME);
					return false;
				}
				viewButtons.values().forEach(menu -> menu.getBtn().removeStyleName(SELECTED_ITEM));
				if (uiController.redirectToMaintenanceView(event.getViewName())) {
					navigateToView(MaintenanceView.NAME);
					return false;
				}
				return true;
			}

			@Override
			public void afterViewChange(final ViewChangeEvent event) {
				focusCurrentMenu(event.getViewName());
				final Menu menuItem = viewButtons.get(event.getViewName());
				if (menuItem != null && menuItem.getBtn() instanceof OneClickButton) {
					contructSubMenu(menuItem, event.getViewName());
				}
				focusCurrentAccordion(event.getViewName());
				currentViewName = event.getViewName();
				menu.removeStyleName(StyleConstants.VALO_MENU_VISIBLE);
			}
		});

		/* Résout la vue à afficher */
		final String fragment = Page.getCurrent().getUriFragment();
		if (fragment == null || fragment.isEmpty()) {
			navigateToView(AccueilView.NAME);
		}
	}

	/** Recharge la bar de submenu lors d'un changement de langue */
	private void reloadSubMenuBar() {
		if (currentViewName == null) {
			return;
		}
		final Menu menu = viewButtons.get(currentViewName);
		if (menu != null) {
			contructSubMenu(menu, currentViewName);
		}
	}

	/**
	 * Focus le menu courant
	 * @param viewName
	 */
	private void focusCurrentMenu(final String viewName) {
		if (viewName != null) {
			final Menu menu = viewButtons.get(viewName);
			if (menu != null && menu.getBtn() instanceof OneClickButton) {
				menu.getBtn().addStyleName(SELECTED_ITEM);
				menu.getBtn().focus();
			}
		}
	}

	/**
	 * Focus l'accordéon courant
	 * @param viewName
	 */
	private void focusCurrentAccordion(final String viewName) {
		final String idAccordion = viewAccordion.get(viewName);
		if (idAccordion != null && !idAccordion.equals(accordionMenu.getItemId())) {
			accordionMenu.changeItem(idAccordion);
		}
	}

	/** Ajoute le css des alertes SVA */
	private void initAlertSva() {
		if (isSvaAlertDisplay) {
			return;
		}
		final List<String> listeAlerteSvaCss = alertSvaController.getListAlertSvaCss();
		/* On ajoute les css colorisant les lignes pour sva */
		for (final String alertCss : listeAlerteSvaCss) {
			Page.getCurrent().getStyles().add(alertCss);
		}
		isSvaAlertDisplay = true;
	}

	/** Initialise le tracker d'activité. */
	private void initAnalyticsTracker() {
		if (piwikAnalyticsTrackerUrl instanceof String && piwikAnalyticsTrackerUrl != null
			&& !piwikAnalyticsTrackerUrl.equals("")
			&& piwikAnalyticsSiteId instanceof String
			&& piwikAnalyticsSiteId != null
			&& !piwikAnalyticsSiteId.equals("")) {
			analyticsTracker = new PiwikAnalyticsTracker(this, piwikAnalyticsTrackerUrl, piwikAnalyticsSiteId);
		} else {
			analyticsTracker = new LogAnalyticsTracker();
		}
		analyticsTracker.trackNavigator(navigator);
	}

	/** @see com.vaadin.ui.UI#detach() */
	@Override
	public void detach() {
		lockCandidatController.removeAllLockUI(uiId);
		/* Se désinscrit de la réception de notifications */
		uiController.unregisterUI(this);

		super.detach();
	}
}
