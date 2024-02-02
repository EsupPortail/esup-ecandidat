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
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseAuthEtab;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseUrl;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.windows.AdminConfigPegaseAuthEtabWindow;
import fr.univlorraine.ecandidat.views.windows.AdminConfigPegaseUrlWindow;

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

	/* Composants Auth Pégase */
	private final OneClickButton btnEditConfigPegaseAuthEtab = new OneClickButton(FontAwesome.PENCIL);
	private final OneClickButton btnTestConfigPegaseAuthEtab = new OneClickButton(FontAwesome.REFRESH);
	private final BeanItemContainer<SimpleTablePresentation> containerPegaseAuthEtab = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating configPegaseAuthEtabTable = new TableFormating(null, containerPegaseAuthEtab);

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

		/* Configuration Pegase */
		/* Titre */
		final Label titleConfigPegase = new Label(applicationContext.getMessage("config.pegase.title", null, UI.getCurrent().getLocale()));
		titleConfigPegase.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleConfigPegase);

		/* Authentification pégase */
		final Label titleConfigPegaseAuthEtab = new Label(applicationContext.getMessage("config.pegaseAuthEtab.title", null, UI.getCurrent().getLocale()));
		titleConfigPegaseAuthEtab.addStyleName(StyleConstants.VIEW_SUBTITLE);
		addComponents(titleConfigPegase, titleConfigPegaseAuthEtab);

		/* Boutons Auth Pégase */
		final HorizontalLayout authPegaseButtonsLayout = new HorizontalLayout();
		authPegaseButtonsLayout.setWidth(100, Unit.PERCENTAGE);
		authPegaseButtonsLayout.setSpacing(true);
		addComponent(authPegaseButtonsLayout);

		btnEditConfigPegaseAuthEtab.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEditConfigPegaseAuthEtab.addClickListener(e -> {
			final ConfigPegaseAuthEtab config = configController.getConfigPegaseAuthEtabWithoutPwd();
			final AdminConfigPegaseAuthEtabWindow window = new AdminConfigPegaseAuthEtabWindow(config);
			window.addConfigPegaseAuthListener(() -> refreshTablePegaseAuth());
			UI.getCurrent().addWindow(window);
		});

		btnTestConfigPegaseAuthEtab.setCaption(applicationContext.getMessage("config.pegaseAuth.test.title", null, UI.getCurrent().getLocale()));
		btnTestConfigPegaseAuthEtab.addClickListener(e -> {
			configController.testConfigPegaseAuth(configController.getConfigPegaseAuthEtab());
		});

		authPegaseButtonsLayout.addComponents(btnEditConfigPegaseAuthEtab, btnTestConfigPegaseAuthEtab);
		authPegaseButtonsLayout.setComponentAlignment(btnEditConfigPegaseAuthEtab, Alignment.MIDDLE_LEFT);
		authPegaseButtonsLayout.setComponentAlignment(btnTestConfigPegaseAuthEtab, Alignment.MIDDLE_RIGHT);

		configPegaseAuthEtabTable.setWidth(100, Unit.PERCENTAGE);
		configPegaseAuthEtabTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		configPegaseAuthEtabTable.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		configPegaseAuthEtabTable.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 250);
		configPegaseAuthEtabTable.setColumnCollapsingAllowed(false);
		configPegaseAuthEtabTable.setColumnReorderingAllowed(false);
		configPegaseAuthEtabTable.setSelectable(false);
		configPegaseAuthEtabTable.setImmediate(true);

		addComponent(configPegaseAuthEtabTable);

		/* Url pégase */
		final Label titleConfigPegaseUrl = new Label(applicationContext.getMessage("config.pegaseUrl.title", null, UI.getCurrent().getLocale()));
		titleConfigPegaseUrl.addStyleName(StyleConstants.VIEW_SUBTITLE);
		addComponents(titleConfigPegaseUrl);

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

	private void refreshTablePegaseAuth() {
		final List<SimpleTablePresentation> liste = configController.getConfigPegaseAuthEtabPresentation();
		containerPegaseAuthEtab.removeAllItems();
		containerPegaseAuthEtab.addAll(liste);
		configPegaseAuthEtabTable.setPageLength(liste.size());
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
