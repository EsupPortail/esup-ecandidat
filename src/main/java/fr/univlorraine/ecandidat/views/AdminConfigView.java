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
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigEtab;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseAuthEtab;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseUrl;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.windows.AdminConfigEtabWindow;
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
	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	public static final String[] FIELDS_ORDER = { SimpleTablePresentation.CHAMPS_TITLE, SimpleTablePresentation.CHAMPS_VALUE };

	/* Composants Etab */
	private final OneClickButton btnEditConfigEtab = new OneClickButton(FontAwesome.PENCIL);
	private final BeanItemContainer<SimpleTablePresentation> containerConfigEtab = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating tableConfigEtab = new TableFormating(null, containerConfigEtab);

	/* Composants Auth Pégase */
	private final OneClickButton btnEditConfigPegaseAuthEtab = new OneClickButton(FontAwesome.PENCIL);
	private final OneClickButton btnTestConfigPegaseAuthEtab = new OneClickButton(FontAwesome.REFRESH);
	private final BeanItemContainer<SimpleTablePresentation> containerConfigPegaseAuthEtab = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating tableConfigPegaseAuthEtab = new TableFormating(null, containerConfigPegaseAuthEtab);

	/* Composants Url Pégase */
	private final OneClickButton btnEditConfigPegaseUrl = new OneClickButton(FontAwesome.PENCIL);
	private final OneClickButton btnTestConfigPegaseUrl = new OneClickButton(FontAwesome.REFRESH);
	private final BeanItemContainer<SimpleTablePresentation> containerConfigPegaseUrl = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating tableConfigPegaseUrl = new TableFormating(null, containerConfigPegaseUrl);

	/** Initialise la vue */
	@PostConstruct
	public void init() {
		/* Style */
		setWidth(100, Unit.PERCENTAGE);
		setMargin(true);
		setSpacing(true);

		/* Configuration Etablissement */
		/* Titre */
		final Label titleConfigEtab = new Label(applicationContext.getMessage("config.etab.title", null, UI.getCurrent().getLocale()));
		titleConfigEtab.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleConfigEtab);

		/* Boutons Auth Pégase */
		final HorizontalLayout configEtabButtonsLayout = new HorizontalLayout();
		configEtabButtonsLayout.setWidth(100, Unit.PERCENTAGE);
		configEtabButtonsLayout.setSpacing(true);
		addComponent(configEtabButtonsLayout);

		btnEditConfigEtab.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEditConfigEtab.addClickListener(e -> {
			final ConfigEtab config = configController.loadConfigEtab();
			final AdminConfigEtabWindow window = new AdminConfigEtabWindow(config);
			window.addConfigEtabListener(() -> refreshTableEtab());
			UI.getCurrent().addWindow(window);
		});

		configEtabButtonsLayout.addComponents(btnEditConfigEtab);
		configEtabButtonsLayout.setComponentAlignment(btnEditConfigEtab, Alignment.MIDDLE_LEFT);

		tableConfigEtab.setWidth(100, Unit.PERCENTAGE);
		tableConfigEtab.setVisibleColumns((Object[]) FIELDS_ORDER);
		tableConfigEtab.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		tableConfigEtab.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 250);
		tableConfigEtab.setColumnCollapsingAllowed(false);
		tableConfigEtab.setColumnReorderingAllowed(false);
		tableConfigEtab.setSelectable(false);
		tableConfigEtab.setImmediate(true);

		addComponent(tableConfigEtab);

		/* La suite est du full Pégase */
		if (!siScolService.isImplementationPegase()) {
			return;
		}

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
			final ConfigPegaseAuthEtab config = configController.loadConfigPegaseAuthEtabWithoutPwd();
			final AdminConfigPegaseAuthEtabWindow window = new AdminConfigPegaseAuthEtabWindow(config);
			window.addConfigPegaseAuthListener(() -> refreshTablePegaseAuth());
			UI.getCurrent().addWindow(window);
		});

		btnTestConfigPegaseAuthEtab.setCaption(applicationContext.getMessage("config.pegaseAuth.test.title", null, UI.getCurrent().getLocale()));
		btnTestConfigPegaseAuthEtab.addClickListener(e -> {
			configController.testConfigPegaseAuth(configController.loadConfigPegaseAuthEtab());
		});

		authPegaseButtonsLayout.addComponents(btnEditConfigPegaseAuthEtab, btnTestConfigPegaseAuthEtab);
		authPegaseButtonsLayout.setComponentAlignment(btnEditConfigPegaseAuthEtab, Alignment.MIDDLE_LEFT);
		authPegaseButtonsLayout.setComponentAlignment(btnTestConfigPegaseAuthEtab, Alignment.MIDDLE_RIGHT);

		tableConfigPegaseAuthEtab.setWidth(100, Unit.PERCENTAGE);
		tableConfigPegaseAuthEtab.setVisibleColumns((Object[]) FIELDS_ORDER);
		tableConfigPegaseAuthEtab.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		tableConfigPegaseAuthEtab.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 250);
		tableConfigPegaseAuthEtab.setColumnCollapsingAllowed(false);
		tableConfigPegaseAuthEtab.setColumnReorderingAllowed(false);
		tableConfigPegaseAuthEtab.setSelectable(false);
		tableConfigPegaseAuthEtab.setImmediate(true);

		addComponent(tableConfigPegaseAuthEtab);

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
			final ConfigPegaseUrl config = configController.loadConfigPegaseUrl();
			final AdminConfigPegaseUrlWindow window = new AdminConfigPegaseUrlWindow(config);
			window.addConfigPegaseUrlListener(() -> refreshTablePegaseUrl());
			UI.getCurrent().addWindow(window);
		});

		btnTestConfigPegaseUrl.setCaption(applicationContext.getMessage("config.pegaseUrl.test.title", null, UI.getCurrent().getLocale()));
		btnTestConfigPegaseUrl.addClickListener(e -> {
			configController.testConfigPegaseUrl(configController.loadConfigPegaseUrl());
		});

		urlPegaseButtonsLayout.addComponents(btnEditConfigPegaseUrl, btnTestConfigPegaseUrl);
		urlPegaseButtonsLayout.setComponentAlignment(btnEditConfigPegaseUrl, Alignment.MIDDLE_LEFT);
		urlPegaseButtonsLayout.setComponentAlignment(btnTestConfigPegaseUrl, Alignment.MIDDLE_RIGHT);

		tableConfigPegaseUrl.setWidth(100, Unit.PERCENTAGE);
		tableConfigPegaseUrl.setVisibleColumns((Object[]) FIELDS_ORDER);
		tableConfigPegaseUrl.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		tableConfigPegaseUrl.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 250);
		tableConfigPegaseUrl.setColumnCollapsingAllowed(false);
		tableConfigPegaseUrl.setColumnReorderingAllowed(false);
		tableConfigPegaseUrl.setSelectable(false);
		tableConfigPegaseUrl.setImmediate(true);

		addComponent(tableConfigPegaseUrl);
	}

	private void refreshTableEtab() {
		final List<SimpleTablePresentation> liste = configController.getConfigEtabPresentation();
		containerConfigEtab.removeAllItems();
		containerConfigEtab.addAll(liste);
		tableConfigEtab.setPageLength(liste.size());
	}

	private void refreshTablePegaseAuth() {
		final List<SimpleTablePresentation> liste = configController.getConfigPegaseAuthEtabPresentation();
		containerConfigPegaseAuthEtab.removeAllItems();
		containerConfigPegaseAuthEtab.addAll(liste);
		tableConfigPegaseAuthEtab.setPageLength(liste.size());
	}

	private void refreshTablePegaseUrl() {
		final List<SimpleTablePresentation> liste = configController.getConfigPegaseUrlPresentation();
		containerConfigPegaseUrl.removeAllItems();
		containerConfigPegaseUrl.addAll(liste);
		tableConfigPegaseUrl.setPageLength(liste.size());
	}

	/** @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent) */
	@Override
	public void enter(final ViewChangeEvent event) {
		refreshTableEtab();
		if (siScolService.isImplementationPegase()) {
			refreshTablePegaseAuth();
			refreshTablePegaseUrl();
		}
	}

	/** @see com.vaadin.ui.AbstractComponent#detach() */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de version */
		super.detach();
	}
}
