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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.ConfigController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigLdap;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseAuth;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseUrl;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.windows.AdminConfigLdapWindow;
import fr.univlorraine.ecandidat.views.windows.AdminConfigPegaseAuthWindow;
import fr.univlorraine.ecandidat.views.windows.AdminConfigPegaseUrlWindow;
import fr.univlorraine.ecandidat.views.windows.AdminTestConfigLdapWindow;

/**
 * Page de gestion des versions
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = AdminConfigView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_ADMIN)
public class AdminConfigView extends VerticalLayout implements View {

	public static final String NAME = "adminConfigView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient ConfigController configController;

	public static final String[] FIELDS_ORDER = { SimpleTablePresentation.CHAMPS_TITLE, SimpleTablePresentation.CHAMPS_VALUE };

	/* Composants Ldap */
	private final OneClickButton btnEditConfigLdap = new OneClickButton(FontAwesome.PENCIL);
	private final OneClickButton btnTestConfigLdap = new OneClickButton(FontAwesome.REFRESH);
	private final BeanItemContainer<SimpleTablePresentation> containerConfigLdap = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating configLdapTable = new TableFormating(null, containerConfigLdap);

	/* Composants Auth Pégase */
	private final OneClickButton btnEditConfigPegaseAuth = new OneClickButton(FontAwesome.PENCIL);
	private final OneClickButton btnTestConfigPegaseAuth = new OneClickButton(FontAwesome.REFRESH);
	private final BeanItemContainer<SimpleTablePresentation> containerPegaseAuth = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating configPegaseAuthTable = new TableFormating(null, containerPegaseAuth);

	/* Composants Url Pégase */
	private final OneClickButton btnEditConfigPegaseUrl = new OneClickButton(FontAwesome.PENCIL);
	private final OneClickButton btnTestConfigPegaseUrl = new OneClickButton(FontAwesome.REFRESH);
	private final BeanItemContainer<SimpleTablePresentation> containerPegaseUrl = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating configPegaseUrlTable = new TableFormating(null, containerPegaseUrl);

	/** Initialise la vue */
	@PostConstruct
	public void init() {
		/* Style */
		setWidth(100, Unit.PERCENTAGE);
		setMargin(true);
		setSpacing(true);

		/* Titre */
		final Label titleConfigLdap = new Label(applicationContext.getMessage("config.ldap.title", null, UI.getCurrent().getLocale()));
		titleConfigLdap.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleConfigLdap);

		/* Boutons Ldap */
		final HorizontalLayout ldapButtonsLayout = new HorizontalLayout();
		ldapButtonsLayout.setWidth(100, Unit.PERCENTAGE);
		ldapButtonsLayout.setSpacing(true);
		addComponent(ldapButtonsLayout);

		btnEditConfigLdap.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEditConfigLdap.addClickListener(e -> {
			final ConfigLdap config = configController.getConfigLdapWithoutPwd();
			final AdminConfigLdapWindow window = new AdminConfigLdapWindow(config);
			window.addConfigLdapListener(() -> refreshTableConfigLdap());
			UI.getCurrent().addWindow(window);
		});

		btnTestConfigLdap.setCaption(applicationContext.getMessage("config.ldap.test.title", null, UI.getCurrent().getLocale()));
		btnTestConfigLdap.addClickListener(e -> {
			UI.getCurrent().addWindow(new AdminTestConfigLdapWindow(configController.getConfigLdap()));
		});

		ldapButtonsLayout.addComponents(btnEditConfigLdap, btnTestConfigLdap);
		ldapButtonsLayout.setComponentAlignment(btnEditConfigLdap, Alignment.MIDDLE_LEFT);
		ldapButtonsLayout.setComponentAlignment(btnTestConfigLdap, Alignment.MIDDLE_RIGHT);
		configLdapTable.setWidth(100, Unit.PERCENTAGE);
		configLdapTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		configLdapTable.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 250);
		configLdapTable.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		configLdapTable.setColumnCollapsingAllowed(false);
		configLdapTable.setColumnReorderingAllowed(false);
		configLdapTable.setSelectable(false);
		configLdapTable.setImmediate(true);

		addComponent(configLdapTable);
		//setExpandRatio(configLdapTable, 1);

		/* Configuration Pegase */
		/* Titre */
		final Label titleConfigPegase = new Label(applicationContext.getMessage("config.pegase.title", null, UI.getCurrent().getLocale()));
		titleConfigPegase.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleConfigPegase);

		/* Authentification pégase */
		final Label titleConfigPegaseAuth = new Label(applicationContext.getMessage("config.pegaseAuth.title", null, UI.getCurrent().getLocale()));
		titleConfigPegaseAuth.addStyleName(StyleConstants.VIEW_SUBTITLE);
		addComponents(titleConfigPegase, titleConfigPegaseAuth);

		/* Boutons Auth Pégase */
		final HorizontalLayout authPegaseButtonsLayout = new HorizontalLayout();
		authPegaseButtonsLayout.setWidth(100, Unit.PERCENTAGE);
		authPegaseButtonsLayout.setSpacing(true);
		addComponent(authPegaseButtonsLayout);

		btnEditConfigPegaseAuth.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEditConfigPegaseAuth.addClickListener(e -> {
			final ConfigPegaseAuth config = configController.getConfigPegaseAuthWithoutPwd();
			final AdminConfigPegaseAuthWindow window = new AdminConfigPegaseAuthWindow(config);
			window.addConfigPegaseAuthListener(() -> refreshTablePegaseAuth());
			UI.getCurrent().addWindow(window);
		});

		btnTestConfigPegaseAuth.setCaption(applicationContext.getMessage("config.pegaseAuth.test.title", null, UI.getCurrent().getLocale()));
		btnTestConfigPegaseAuth.addClickListener(e -> {
			configController.testConfigPegaseAuth(configController.getConfigPegaseAuth());
		});

		authPegaseButtonsLayout.addComponents(btnEditConfigPegaseAuth, btnTestConfigPegaseAuth);
		authPegaseButtonsLayout.setComponentAlignment(btnEditConfigPegaseAuth, Alignment.MIDDLE_LEFT);
		authPegaseButtonsLayout.setComponentAlignment(btnTestConfigPegaseAuth, Alignment.MIDDLE_RIGHT);

		configPegaseAuthTable.setWidth(100, Unit.PERCENTAGE);
		configPegaseAuthTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		configPegaseAuthTable.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		configPegaseAuthTable.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 250);
		configPegaseAuthTable.setColumnCollapsingAllowed(false);
		configPegaseAuthTable.setColumnReorderingAllowed(false);
		configPegaseAuthTable.setSelectable(false);
		configPegaseAuthTable.setImmediate(true);

		addComponent(configPegaseAuthTable);

		/* Url pégase */
		final Label titleConfigPegaseUrl = new Label(applicationContext.getMessage("config.pegaseUrl.title", null, UI.getCurrent().getLocale()));
		titleConfigPegaseUrl.addStyleName(StyleConstants.VIEW_SUBTITLE);
		addComponents(titleConfigPegase, titleConfigPegaseUrl);

		/* Boutons Url Pégase */
		final HorizontalLayout urlPegaseButtonsLayout = new HorizontalLayout();
		urlPegaseButtonsLayout.setWidth(100, Unit.PERCENTAGE);
		urlPegaseButtonsLayout.setSpacing(true);
		addComponent(urlPegaseButtonsLayout);

		btnEditConfigPegaseUrl.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEditConfigPegaseUrl.addClickListener(e -> {
			final ConfigPegaseUrl config = configController.getConfigPegaseUrl();
			final AdminConfigPegaseUrlWindow window = new AdminConfigPegaseUrlWindow(config);
			window.addConfigPegaseUrlListener(() -> refreshTablePegaseUrl());
			UI.getCurrent().addWindow(window);
		});

		btnTestConfigPegaseUrl.setCaption(applicationContext.getMessage("config.pegaseUrl.test.title", null, UI.getCurrent().getLocale()));
		btnTestConfigPegaseUrl.addClickListener(e -> {
			configController.testConfigPegaseUrl(configController.getConfigPegaseUrl());
		});

		urlPegaseButtonsLayout.addComponents(btnEditConfigPegaseUrl, btnTestConfigPegaseUrl);
		urlPegaseButtonsLayout.setComponentAlignment(btnEditConfigPegaseUrl, Alignment.MIDDLE_LEFT);
		urlPegaseButtonsLayout.setComponentAlignment(btnTestConfigPegaseUrl, Alignment.MIDDLE_RIGHT);

		configPegaseUrlTable.setWidth(100, Unit.PERCENTAGE);
		configPegaseUrlTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		configPegaseUrlTable.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		configPegaseUrlTable.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 250);
		configPegaseUrlTable.setColumnCollapsingAllowed(false);
		configPegaseUrlTable.setColumnReorderingAllowed(false);
		configPegaseUrlTable.setSelectable(false);
		configPegaseUrlTable.setImmediate(true);

		addComponent(configPegaseUrlTable);
	}

	private void refreshTableConfigLdap() {
		final List<SimpleTablePresentation> liste = configController.getConfigLdapPresentation();
		containerConfigLdap.removeAllItems();
		containerConfigLdap.addAll(liste);
		configLdapTable.setPageLength(liste.size());
	}

	private void refreshTablePegaseAuth() {
		final List<SimpleTablePresentation> liste = configController.getConfigPegaseAuthPresentation();
		containerPegaseAuth.removeAllItems();
		containerPegaseAuth.addAll(liste);
		configPegaseAuthTable.setPageLength(liste.size());
	}

	private void refreshTablePegaseUrl() {
		final List<SimpleTablePresentation> liste = configController.getConfigPegaseUrlPresentation();
		containerPegaseUrl.removeAllItems();
		containerPegaseUrl.addAll(liste);
		configPegaseUrlTable.setPageLength(liste.size());
	}

	/** @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent) */
	@Override
	public void enter(final ViewChangeEvent event) {
		refreshTableConfigLdap();
		refreshTablePegaseAuth();
		refreshTablePegaseUrl();
	}

	/** @see com.vaadin.ui.AbstractComponent#detach() */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de version */
		super.detach();
	}
}
