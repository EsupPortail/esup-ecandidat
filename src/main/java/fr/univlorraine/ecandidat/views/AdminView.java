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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.HierarchicalContainer;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextField;
import com.vaadin.v7.ui.TreeTable;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.LoadBalancingController;
import fr.univlorraine.ecandidat.controllers.LockController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.UiController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.MaintenanceListener;
import fr.univlorraine.ecandidat.utils.bean.presentation.SessionPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.SessionPresentation.SessionType;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * Informations sur les clients actifs
 * <br/>
 * Sessions / Verrous
 * @author Matthieu MANGINOT
 * @author Adrien Colson
 * @author Kevin Hergalant
 */
@SpringView(name = AdminView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_ADMIN)
@SuppressWarnings("unchecked")
public class AdminView extends VerticalLayout implements View, MaintenanceListener {

	/** serialVersionUID **/
	private static final long serialVersionUID = 7969257300291771236L;

	public static final String NAME = "adminView";

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient UiController uiController;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient LoadBalancingController loadBalancingController;

	/* Data */
	private Boolean isMaintenance;

	/* Composants */
	private final TextField switchUserTextField = new TextField();
	private OneClickButton btnSwitchUser;
	private final Label clientTreeTableTitle = new Label();
	private OneClickButton btnSendMessage;
	private final OneClickButton btnMaintenance = new OneClickButton();
	private OneClickButton btnKill;
	private OneClickButton btnRemoveLock;
	private final HierarchicalContainer uisContainer = new HierarchicalContainer();
	private final TreeTable uisTreeTable = new TreeTable(null, uisContainer);

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		/* Titre */
		final HorizontalLayout titleLayout = new HorizontalLayout();
		titleLayout.setWidth(100, Unit.PERCENTAGE);
		titleLayout.setSpacing(true);
		addComponent(titleLayout);

		/* Label de titre */
		final Label title = new Label(applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()));
		title.addStyleName(StyleConstants.VIEW_TITLE);
		titleLayout.addComponent(title);
		titleLayout.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

		/* Bouton d'arret */
		final Boolean isLoadBalancingCandidatMode = loadBalancingController.isLoadBalancingCandidatMode();

		if (!isLoadBalancingCandidatMode) {
			btnMaintenance.setIcon(FontAwesome.POWER_OFF);
			final MaintenanceListener listener = this;
			btnMaintenance.addClickListener(e -> parametreController.changeMaintenanceStatut(!isMaintenance, listener));
			titleLayout.addComponent(btnMaintenance);
			titleLayout.setComponentAlignment(btnMaintenance, Alignment.MIDDLE_RIGHT);
		}

		if (!isLoadBalancingCandidatMode) {
			/* Changement de rôle */
			final Label switchUserTitle = new Label(applicationContext.getMessage("admin.switchUser.title", null, UI.getCurrent().getLocale()));
			switchUserTitle.addStyleName(StyleConstants.VIEW_TITLE);
			addComponent(switchUserTitle);

			final HorizontalLayout switchUserLayout = new HorizontalLayout();
			switchUserLayout.setSpacing(true);
			addComponent(switchUserLayout);

			final Label switchUserLabel = new Label(applicationContext.getMessage("admin.switchUser.label", null, UI.getCurrent().getLocale()));
			switchUserLayout.addComponent(switchUserLabel);
			switchUserLayout.setComponentAlignment(switchUserLabel, Alignment.MIDDLE_CENTER);

			switchUserTextField.setMaxLength(20);
			switchUserTextField.setImmediate(true);
			switchUserTextField.addTextChangeListener(e -> {
				switchUserTextField.setValue(e.getText());
				/* Le bouton de changement de rôle est actif si un login est entré. */
				btnSwitchUser.setEnabled(e.getText() instanceof String && !e.getText().trim().isEmpty());
			});
			switchUserTextField.addShortcutListener(new ShortcutListener(null, ShortcutAction.KeyCode.ENTER, null) {
				private static final long serialVersionUID = 6654068159607621464L;

				@Override
				public void handleAction(final Object sender, final Object target) {
					final String e = switchUserTextField.getValue();
					if (e != null && !e.trim().isEmpty()) {
						userController.switchToUser(switchUserTextField.getValue());
					}
				}
			});
			switchUserLayout.addComponent(switchUserTextField);

			btnSwitchUser = new OneClickButton(applicationContext.getMessage("admin.switchUser.btnSwitchUser", null, UI.getCurrent().getLocale()), FontAwesome.SIGN_IN);
			btnSwitchUser.setEnabled(false);
			btnSwitchUser.addClickListener(e -> userController.switchToUser(switchUserTextField.getValue()));
			switchUserLayout.addComponent(btnSwitchUser);
		}

		/* Titre et boutons liste des clients actifs */
		final HorizontalLayout clientTreeTableButtonsLayout = new HorizontalLayout();
		clientTreeTableButtonsLayout.setWidth(100, Unit.PERCENTAGE);
		clientTreeTableButtonsLayout.setSpacing(true);
		addComponent(clientTreeTableButtonsLayout);

		clientTreeTableTitle.addStyleName(StyleConstants.VIEW_TITLE);
		clientTreeTableButtonsLayout.addComponent(clientTreeTableTitle);
		clientTreeTableButtonsLayout.setComponentAlignment(clientTreeTableTitle, Alignment.MIDDLE_LEFT);

		final OneClickButton refreshBtn = new OneClickButton(applicationContext.getMessage("admin.uiList.refresh", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
		clientTreeTableButtonsLayout.addComponent(refreshBtn);
		clientTreeTableButtonsLayout.setComponentAlignment(refreshBtn, Alignment.MIDDLE_CENTER);

		refreshBtn.addClickListener(e -> {
			refreshListUi();
		});

		if (!isLoadBalancingCandidatMode) {
			btnSendMessage = new OneClickButton(applicationContext.getMessage("admin.sendMessage.title", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
			btnSendMessage.setEnabled(UI.getCurrent().getPushConfiguration().getPushMode().isEnabled());
			btnSendMessage.addClickListener(e -> uiController.sendMessage());
			clientTreeTableButtonsLayout.addComponent(btnSendMessage);
			clientTreeTableButtonsLayout.setComponentAlignment(btnSendMessage, Alignment.MIDDLE_CENTER);
		}

		final HorizontalLayout clientTreeTableRightButtonsLayout = new HorizontalLayout();
		clientTreeTableRightButtonsLayout.setSpacing(true);
		clientTreeTableButtonsLayout.addComponent(clientTreeTableRightButtonsLayout);
		clientTreeTableButtonsLayout.setComponentAlignment(clientTreeTableRightButtonsLayout, Alignment.MIDDLE_RIGHT);

		btnRemoveLock = new OneClickButton(applicationContext.getMessage("admin.uiList.btnRemoveLock", null, UI.getCurrent().getLocale()), FontAwesome.UNLOCK);
		btnRemoveLock.setEnabled(false);
		btnRemoveLock.addClickListener(e -> confirmRemoveLock((SessionPresentation) uisTreeTable.getValue()));
		clientTreeTableRightButtonsLayout.addComponent(btnRemoveLock);

		btnKill = new OneClickButton(applicationContext.getMessage("admin.uiList.btnkill", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnKill.setEnabled(false);
		btnKill.addClickListener(e -> {
			final SessionPresentation obj = (SessionPresentation) uisTreeTable.getValue();
			if (obj.getType().equals(SessionType.USER)) {
				confirmKillUser(obj);
			}
			if (obj.getType().equals(SessionType.SESSION)) {
				confirmKillSession(obj);
			} else if (obj.getType().equals(SessionType.UI)) {
				confirmKillUI(obj);
			}
		});
		clientTreeTableRightButtonsLayout.addComponent(btnKill);

		/* Les propriétés */
		final String idUi = MainUI.getCurrent().getUiId();
		final String idSession = MainUI.getCurrent().getSession().getSession().getId();
		final String idUser = userController.getCurrentUserLogin();

		/* TreeTable des clients actifs */
		uisContainer.addContainerProperty(ConstanteUtils.SESSION_PROPERTY_INFO, String.class, null);
		uisContainer.addContainerProperty(ConstanteUtils.SESSION_PROPERTY_TITLE, String.class, null);
		uisContainer.addContainerProperty(ConstanteUtils.SESSION_PROPERTY_ID, String.class, null);
		uisContainer.addContainerProperty(ConstanteUtils.SESSION_PROPERTY_ICON, com.vaadin.server.Resource.class, null);

		uisContainer.sort(new Object[] { ConstanteUtils.SESSION_PROPERTY_ID }, new boolean[] { true });
		uisTreeTable.addStyleName(StyleConstants.CUSTOM_TREE);
		//uisTreeTable.setContainerDataSource(uisContainer);
		uisTreeTable.setVisibleColumns(new Object[] { ConstanteUtils.SESSION_PROPERTY_TITLE, ConstanteUtils.SESSION_PROPERTY_INFO });
		uisTreeTable.setColumnHeader(ConstanteUtils.SESSION_PROPERTY_TITLE, applicationContext.getMessage("admin.uiList.column." + ConstanteUtils.SESSION_PROPERTY_TITLE, null, UI.getCurrent().getLocale()));
		uisTreeTable.setColumnHeader(ConstanteUtils.SESSION_PROPERTY_INFO, applicationContext.getMessage("admin.uiList.column." + ConstanteUtils.SESSION_PROPERTY_INFO, null, UI.getCurrent().getLocale()));
		uisTreeTable.setColumnExpandRatio(ConstanteUtils.SESSION_PROPERTY_INFO, 1);
		uisTreeTable.setSizeFull();
		uisTreeTable.setImmediate(true);
		uisTreeTable.setSelectable(true);
		uisTreeTable.setItemIconPropertyId(ConstanteUtils.SESSION_PROPERTY_ICON);
		uisTreeTable.setCellStyleGenerator((source, itemId, propertyId) -> {
			if (propertyId != null && propertyId.equals(ConstanteUtils.SESSION_PROPERTY_TITLE)) {
				final SessionPresentation item = (SessionPresentation) itemId;
				if ((item.getType().equals(SessionType.USER) && item.getId().equals(idUser))
					|| (item.getType().equals(SessionType.UI) && item.getId().equals(idUi))
					|| (item.getType().equals(SessionType.SESSION) && item.getId().equals(idSession))) {

					return StyleConstants.SESSION_TITLE;
				}
			}
			return null;
		});
		uisTreeTable.addValueChangeListener(e -> {
			final SessionPresentation selectedObject = (SessionPresentation) uisTreeTable.getValue();
			if (selectedObject == null) {
				btnKill.setEnabled(false);
				btnRemoveLock.setEnabled(false);
				return;
			}
			if ((selectedObject.getType().equals(SessionType.USER) && selectedObject.getId().equals(idUser))
				|| (selectedObject.getType().equals(SessionType.UI) && selectedObject.getId().equals(idUi))
				|| (selectedObject.getType().equals(SessionType.SESSION) && selectedObject.getId().equals(idSession))) {
				btnKill.setEnabled(false);
				btnRemoveLock.setEnabled(false);
				return;
			}

			final boolean selectedObjectIsUserOrSessionOrUI =
				((selectedObject.getType().equals(SessionType.USER) && !(selectedObject.getId().equals(applicationContext.getMessage("user.notconnected", null, UI.getCurrent().getLocale()))))
					|| selectedObject.getType().equals(SessionType.SESSION)
					|| selectedObject.getType().equals(SessionType.UI));

			/* Le bouton qui permet de supprimer une session est actif si une session est sélectionnée */
			btnKill.setEnabled(selectedObjectIsUserOrSessionOrUI);

			final boolean selectedLock = selectedObject.getType().equals(SessionType.LOCK);

			/* Le bouton qui permet de supprimer un verrou est actif si un verrou est sélectionné */
			btnRemoveLock.setEnabled(selectedLock);
		});

		addComponent(uisTreeTable);
		setExpandRatio(uisTreeTable, 1);

		/* Informations sur le comportement en cas de suppression */
		addComponent(new Label(FontAwesome.WARNING.getHtml() + " " + applicationContext.getMessage("admin.uiList.warning", null, UI.getCurrent().getLocale()), ContentMode.HTML));
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
		updateBtnMaintenance();
		refreshListUi();
	}

	/**
	 * Met a jour le bouton de maintenance
	 */
	private void updateBtnMaintenance() {
		isMaintenance = parametreController.getIsMaintenance();
		if (isMaintenance) {
			btnMaintenance.removeStyleName(ValoTheme.BUTTON_DANGER);
			btnMaintenance.addStyleName(ValoTheme.BUTTON_FRIENDLY);
			btnMaintenance.setCaption(applicationContext.getMessage("admin.maintenance.btn.wakeup", null, UI.getCurrent().getLocale()));
		} else {
			btnMaintenance.removeStyleName(ValoTheme.BUTTON_FRIENDLY);
			btnMaintenance.addStyleName(ValoTheme.BUTTON_DANGER);
			btnMaintenance.setCaption(applicationContext.getMessage("admin.maintenance.btn.shutdown", null, UI.getCurrent().getLocale()));
		}
	}

	/**
	 * Rafraichi la liste des UI
	 */
	private void refreshListUi() {
		majContainer();
		majNombreClient();
	}

	/**
	 * Met a jour le container
	 */
	private void majContainer() {
		uisContainer.removeAllItems();

		final List<SessionPresentation> listeUis = uiController.getListSessionToDisplay();

		/* Ajout des users */
		listeUis.stream().filter(e -> e.getType().equals(SessionType.USER)).forEach(user -> {
			final Item item = addItem(user);
			item.getItemProperty(ConstanteUtils.SESSION_PROPERTY_ICON).setValue(FontAwesome.USER);
			uisTreeTable.setCollapsed(user, false);

			/* Ajout des sessions */
			listeUis.stream().filter(session -> session.getType().equals(SessionType.SESSION)
				&& session.getTypeParent().equals(SessionType.USER)
				&& session.getIdParent().equals(user.getId())).forEach(session -> {
					addItem(session);
					uisContainer.setParent(session, user);
					uisTreeTable.setCollapsed(session, false);

					/* Ajout des UI */
					listeUis.stream().filter(ui -> ui.getType().equals(SessionType.UI)
						&& ui.getTypeParent().equals(SessionType.SESSION)
						&& ui.getIdParent().equals(session.getId())).forEach(ui -> {
							addItem(ui);
							uisContainer.setParent(ui, session);
							uisTreeTable.setCollapsed(ui, true);

							/* Ajout des locks */
							final List<SessionPresentation> listeLocks = listeUis.stream().filter(lock -> lock.getType().equals(SessionType.LOCK)
								&& lock.getTypeParent().equals(SessionType.UI)
								&& lock.getIdParent().equals(ui.getId())).collect(Collectors.toList());
							if (listeLocks.size() == 0) {
								uisContainer.setChildrenAllowed(ui, false);
							} else {
								listeLocks.forEach(lock -> {
									addItem(lock);
									uisContainer.setParent(lock, ui);
									uisContainer.setChildrenAllowed(lock, false);
								});
							}
						});
				});
		});
		uisTreeTable.sanitizeSelection();
	}

	/**
	 * Ajoute un item
	 * @param  sessionItem
	 * @return
	 */
	private Item addItem(final SessionPresentation sessionItem) {
		final Item item = uisContainer.addItem(sessionItem);
		item.getItemProperty(ConstanteUtils.SESSION_PROPERTY_ID).setValue(sessionItem.getId());
		item.getItemProperty(ConstanteUtils.SESSION_PROPERTY_TITLE).setValue(sessionItem.getTitle());
		item.getItemProperty(ConstanteUtils.SESSION_PROPERTY_INFO).setValue(sessionItem.getInfo());
		return item;
	}

	/**
	 * Met à jour le nombre de clients
	 */
	private void majNombreClient() {
		/* Affiche le nombre d'UIs connectées */
		clientTreeTableTitle.setValue(applicationContext.getMessage("admin.uiList.title",
			new Object[] { ((List<SessionPresentation>) uisContainer.getItemIds()).stream().filter(e -> e.getType().equals(SessionType.UI)).count() }, UI.getCurrent().getLocale()));
	}

	/**
	 * SUpprime un elmeent de la liste
	 * @param item
	 */
	private void removeElement(final SessionPresentation item) {
		SessionPresentation parent = null;
		if (uisContainer.getParent(item) != null) {
			parent = (SessionPresentation) uisContainer.getParent(item);
		}
		//suppression de l'item
		uisContainer.removeItemRecursively(item);
		if (parent != null) {
			//si le parent est une session, on verifie que celle ci a encore des enfants. Sinon, on la supprime aussi
			if (parent.getType().equals(SessionType.SESSION)) {
				SessionPresentation parentUser = null;
				if (uisContainer.getParent(parent) != null) {
					parentUser = (SessionPresentation) uisContainer.getParent(parent);
				}
				if (uisContainer.getChildren(parent) == null || uisContainer.getChildren(parent).size() == 0) {
					uisContainer.removeItemRecursively(parent);
					//on verifie aussi que le user a d'autre session, sinon, on supprime
					if (parentUser != null && (uisContainer.getChildren(parentUser) == null || uisContainer.getChildren(parentUser).size() == 0)) {
						uisContainer.removeItemRecursively(parentUser);
					}
				}
			} else if (parent.getType().equals(SessionType.USER)) {
				//on verifie aussi que le user a d'autre session, sinon, on supprime
				if (uisContainer.getChildren(parent) == null || uisContainer.getChildren(parent).size() == 0) {
					uisContainer.removeItemRecursively(parent);
				}
			}
		}

		uisTreeTable.sanitizeSelection();
		majNombreClient();
	}

	/**
	 * Supprime un verrou
	 * @param lockItem le verrou a supprimer
	 */
	public void confirmRemoveLock(final SessionPresentation lockItem) {
		final Item item = uisContainer.getItem(lockItem);
		String textLock = lockItem.getId();
		if (item != null && lockItem.getInfo() != null) {
			textLock = lockItem.getInfo();
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("admin.uiList.confirmRemoveLock", new Object[] { textLock }, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			final Object lock = lockController.getLockBySessionItem(lockItem);
			if (lock != null) {
				lockController.removeLock(lock);
			} else {
				Notification.show(applicationContext.getMessage("admin.uiList.confirmKillUI.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			majContainer();
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Confirme la fermeture d'une UI
	 * @param uiItem l'UI a kill
	 */
	public void confirmKillUI(final SessionPresentation uiItem) {
		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("admin.uiList.confirmKillUI", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			final MainUI ui = uiController.getUI(uiItem);
			if (ui != null) {
				uiController.killUI(ui);
			} else {
				Notification.show(applicationContext.getMessage("admin.uiList.confirmKillUI.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			removeElement(uiItem);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Confirme la fermeture de toutes les sessions associées à un utilisateur
	 * @param user
	 */
	public void confirmKillUser(final SessionPresentation user) {
		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("admin.uiList.confirmKillUser", new Object[] { user.getId() }, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			final UserDetails details = uiController.getUser(user);
			if (details != null) {
				uiController.killUser(details);
			} else {
				Notification.show(applicationContext.getMessage("admin.uiList.confirmKillUser.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			removeElement(user);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Confirme la fermeture d'une session
	 * @param session la session a kill
	 */
	public void confirmKillSession(final SessionPresentation session) {
		final SessionPresentation user = (SessionPresentation) uisContainer.getParent(session);
		String userName = applicationContext.getMessage("user.notconnected", null, UI.getCurrent().getLocale());
		if (user != null) {
			userName = user.getId();
		}

		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("admin.uiList.confirmKillSession", new Object[] { session.getId(), userName }, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			final VaadinSession vaadinSession = uiController.getSession(session);
			Collection<SessionPresentation> listeUI = null;
			if (uisContainer.getChildren(session) != null) {
				listeUI = (Collection<SessionPresentation>) uisContainer.getChildren(session);
			}
			if (vaadinSession != null) {
				uiController.killSession(vaadinSession, listeUI);
			} else {
				Notification.show(applicationContext.getMessage("admin.uiList.confirmKillSession.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			removeElement(session);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.MaintenanceListener#changeModeMaintenance()
	 */
	@Override
	public void changeModeMaintenance() {
		updateBtnMaintenance();
	}
}
