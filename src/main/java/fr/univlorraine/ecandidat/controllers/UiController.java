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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import com.vaadin.server.VaadinSession;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SessionPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.SessionPresentation.SessionType;
import fr.univlorraine.ecandidat.views.MaintenanceView;
import fr.univlorraine.ecandidat.views.windows.InputWindow;

/**
 * Gestion des sessions
 * 
 * @author Kevin Hergalant
 */

@Component
@SuppressWarnings("serial")
public class UiController implements Serializable {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient LockController lockController;

	/** Thread pool */
	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	/* Envoi de messages aux clients connectés */

	/** UIs connectées */
	private LinkedList<MainUI> uis = new LinkedList<>();

	/**
	 * @return les UIs
	 */
	@SuppressWarnings("unchecked")
	public synchronized LinkedList<MainUI> getUis() {
		return (LinkedList<MainUI>) uis.clone();
	}

	/**
	 * Doit-on rediriger vers la page de maintenance
	 * 
	 * @param viewDemande
	 * @return true si on doit rediriger
	 */
	public Boolean redirectToMaintenanceView(final String viewDemande) {
		if (parametreController.getIsMaintenance() && !viewDemande.equals(MaintenanceView.NAME) && !userController.isAdmin()) {
			return true;
		}
		return false;
	}

	/**
	 * Ajoute une UI à la liste des UIs connectées
	 * 
	 * @param ui
	 *            l'UI a ajouter
	 */
	public synchronized void registerUI(final MainUI ui) {
		VaadinSession session = ui.getSession();
		if (session == null || session.getSession() == null) {
			return;
		}
		SecurityContext securityContext = (SecurityContext) session.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

		if (securityContext == null || securityContext.getAuthentication() == null) {
			return;
		}
		uis.add(ui);
	}

	/**
	 * Enlève une UI de la liste des UIs connectées
	 * 
	 * @param ui
	 *            l'UI a enlever
	 */
	public synchronized void unregisterUI(final MainUI ui) {
		uis.remove(ui);
	}

	/**
	 * Envoie une notification à tous les clients connectés
	 * 
	 * @param notification
	 */
	private synchronized void sendNotification(final Notification notification) {
		uis.forEach(ui -> executorService.execute(() -> ui.access(() -> notification.show(ui.getPage()))));
	}

	/**
	 * Permet la saisie et l'envoi d'un message à tous les clients connectés
	 */
	public void sendMessage() {
		InputWindow inputWindow = new InputWindow(applicationContext.getMessage("admin.sendMessage.message", null, UI.getCurrent().getLocale()),
				applicationContext.getMessage("admin.sendMessage.title", null, UI.getCurrent().getLocale()), true, 255);
		inputWindow.addBtnOkListener(text -> {
			if (text instanceof String && !text.isEmpty()) {
				Notification notification =
						new Notification(applicationContext.getMessage("admin.sendMessage.notificationCaption", new Object[] {text}, UI.getCurrent().getLocale()), null, Type.TRAY_NOTIFICATION, true);
				notification.setDelayMsec(-1);
				notification.setDescription("\n" + applicationContext.getMessage("admin.sendMessage.notificationDescription", null, UI.getCurrent().getLocale()));
				notification.setPosition(Position.TOP_CENTER);
				sendNotification(notification);
			}
		});
		UI.getCurrent().addWindow(inputWindow);
	}

	/**
	 * Vérifie si une UI est toujours active
	 * 
	 * @param ui
	 *            l'UI a vérifier
	 * @return true si l'UI est active
	 */
	public synchronized boolean isUIStillActive(final UI ui) {
		return uis.contains(ui);
	}

	/* Construit la liste d'affichage dans l'arbre des UI */
	public List<SessionPresentation> getListSessionToDisplay() {
		List<SessionPresentation> liste = new ArrayList<>();
		getUis().forEach(ui -> {
			VaadinSession session = ui.getSession();
			if (session == null || session.getSession() == null) {
				return;
			}
			SecurityContext securityContext = (SecurityContext) session.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

			UserDetails user;
			if (securityContext == null || securityContext.getAuthentication() == null || securityContext.getAuthentication().getPrincipal() == null) {
				return;
			} else {
				user = (UserDetails) securityContext.getAuthentication().getPrincipal();
			}

			List<Object> uiLocks = lockController.getUILocks(ui);

			/* User item */
			SessionPresentation adminSessionUser = new SessionPresentation(user.getUsername(), SessionType.USER);
			if (!liste.contains(adminSessionUser)) {
				adminSessionUser.setTitle(
						applicationContext.getMessage("admin.uiList.item.user." + ConstanteUtils.SESSION_PROPERTY_TITLE, new Object[] {adminSessionUser.getId()}, UI.getCurrent().getLocale()));
				adminSessionUser.setInfo(applicationContext.getMessage("admin.uiList.item.user." + ConstanteUtils.SESSION_PROPERTY_INFO, new Object[] {String.valueOf(user.getAuthorities())},
						UI.getCurrent().getLocale()));
				liste.add(adminSessionUser);
			}

			/* Session item */
			SessionPresentation adminSessionSession = new SessionPresentation(session.getSession().getId(), SessionType.SESSION);
			if (!liste.contains(adminSessionSession)) {
				adminSessionSession.setIdParent(adminSessionUser.getId());
				adminSessionSession.setTypeParent(adminSessionUser.getType());
				WebBrowser browser = ui.getPage().getWebBrowser();
				String ipAddress = browser.getAddress();
				String browserInfo = browser.getBrowserApplication() + " v" + browser.getBrowserMajorVersion() + "." + browser.getBrowserMinorVersion();
				adminSessionSession.setTitle(
						applicationContext.getMessage("admin.uiList.item.session." + ConstanteUtils.SESSION_PROPERTY_TITLE, new Object[] {adminSessionSession.getId()}, UI.getCurrent().getLocale()));
				adminSessionSession.setInfo(
						applicationContext.getMessage("admin.uiList.item.session." + ConstanteUtils.SESSION_PROPERTY_INFO, new Object[] {ipAddress, browserInfo}, UI.getCurrent().getLocale()));
				liste.add(adminSessionSession);
			}

			/* UI item */
			SessionPresentation adminSessionUI = new SessionPresentation(ui.getUiId(), SessionType.UI);
			if (!liste.contains(adminSessionUI)) {
				adminSessionUI.setIdParent(adminSessionSession.getId());
				adminSessionUI.setTypeParent(adminSessionSession.getType());
				adminSessionUI.setTitle(applicationContext.getMessage("admin.uiList.item.ui." + ConstanteUtils.SESSION_PROPERTY_TITLE, new Object[] {ui.getUIId()}, UI.getCurrent().getLocale()));
				adminSessionUI.setInfo(applicationContext.getMessage("admin.uiList.item.ui." + ConstanteUtils.SESSION_PROPERTY_INFO, new Object[] {uiLocks.size(), ui.getNavigator().getState()},
						UI.getCurrent().getLocale()));
				liste.add(adminSessionUI);
			}

			/* Lock items */
			uiLocks.forEach(lock -> {
				SessionPresentation adminSessionLock = new SessionPresentation(String.valueOf(System.identityHashCode(lock)), SessionType.LOCK);
				if (!liste.contains(adminSessionLock)) {
					adminSessionLock.setIdParent(adminSessionUI.getId());
					adminSessionLock.setTypeParent(adminSessionUI.getType());
					adminSessionLock.setTitle(
							applicationContext.getMessage("admin.uiList.item.lock." + ConstanteUtils.SESSION_PROPERTY_TITLE, new Object[] {lock.getClass().getName()}, UI.getCurrent().getLocale()));
					adminSessionLock
							.setInfo(applicationContext.getMessage("admin.uiList.item.lock." + ConstanteUtils.SESSION_PROPERTY_INFO, new Object[] {lock.toString()}, UI.getCurrent().getLocale()));
					liste.add(adminSessionLock);
				}
			});
		});
		return liste;
	}

	/* Tuer des UIs, sessions et utilisateurs */
	/**
	 * @param user
	 * @return un user
	 */
	public UserDetails getUser(final SessionPresentation user) {
		for (MainUI ui : getUis()) {
			try {
				VaadinSession session = ui.getSession();
				if (session == null || session.getSession() == null) {
					return null;
				}
				SecurityContext securityContext = (SecurityContext) session.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

				if (securityContext == null || securityContext.getAuthentication() == null) {
					return null;
				} else {
					UserDetails details = (UserDetails) securityContext.getAuthentication().getPrincipal();
					if (details != null && details.getUsername().equals(user.getId())) {
						return details;
					}
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * Ferme toutes les sessions associées à un utilisateur
	 * 
	 * @param user
	 *            le user a kill
	 */
	public synchronized void killUser(final UserDetails user) {
		for (MainUI mainUI : uis) {
			SecurityContext securityContext = (SecurityContext) mainUI.getSession().getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
			if (user.getUsername().equals(securityContext.getAuthentication().getName())) {
				mainUI.close();
			}
		}
	}

	/**
	 * @param sessionItem
	 * @return la session
	 */
	public VaadinSession getSession(final SessionPresentation sessionItem) {
		for (MainUI ui : getUis()) {
			try {
				VaadinSession session = ui.getSession();
				if (session == null || session.getSession() == null || session.getSession().getId() == null) {
					return null;
				} else if (sessionItem.getId().equals(session.getSession().getId())) {
					return session;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * Ferme une session
	 * 
	 * @param session
	 *            la session a kill
	 * @param listeUI
	 */
	public void killSession(final VaadinSession session, final Collection<SessionPresentation> listeUI) {
		if (listeUI != null) {
			listeUI.forEach(e -> killUI(getUI(e)));
		}
		session.close();
	}

	/**
	 * @param uiItem
	 * @return une UI
	 */
	public MainUI getUI(final SessionPresentation uiItem) {
		for (MainUI ui : getUis()) {
			try {
				if (ui.getUiId().equals(uiItem.getId())) {
					return ui;
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * Ferme une UI
	 * 
	 * @param ui
	 *            lUI a fermer
	 */
	public void killUI(final UI ui) {
		ui.close();
	}

	/**
	 * Connecte un candidat
	 * Registre l'ui de connexion, ferme les autres appartenant à la session
	 * 
	 * @param ui
	 */
	public void registerUiCandidat(final MainUI ui) {
		registerUI(ui);
	}

	/**
	 * Deonnecte un candidat
	 * 
	 * @param ui
	 */
	public void unregisterUiCandidat(final MainUI ui) {
		unregisterUI(ui);
	}

	/**
	 * Navigue à une vue
	 * 
	 * @param view
	 */
	public void navigateTo(final String view) {
		MainUI ui = MainUI.getCurrent();
		if (ui != null) {
			ui.navigateToView(view);
		}
	}
}
