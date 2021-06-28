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
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.OpiController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.CustomPanel;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;

/**
 * Page de gestion des OPI
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = AdminOpiView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_ADMIN)
public class AdminOpiView extends VerticalLayout implements View {

	public static final String NAME = "adminOpiView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient OpiController opiController;
	@Resource
	private transient ParametreController parametreController;

	public static final String[] FIELDS_ORDER = { SimpleTablePresentation.CHAMPS_TITLE, SimpleTablePresentation.CHAMPS_VALUE };

	/* Composants */
	private final BeanItemContainer<SimpleTablePresentation> opiContainer = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating opiTable = new TableFormating(null, opiContainer);
	private final BeanItemContainer<SimpleTablePresentation> opiPjContainer = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating opiPjTable = new TableFormating(null, opiPjContainer);

	private static final Integer ELEMENT_WIDTH = 800;

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		//setSizeFull();
		setMargin(true);
		setSpacing(true);

		/* Titre */
		final Label titleNom = new Label(applicationContext.getMessage("opi.view.title", null, UI.getCurrent().getLocale()));
		titleNom.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleNom);

		/* Warning */
		final CustomPanel cp = new CustomPanel(applicationContext.getMessage("opi.panel.caption", null, UI.getCurrent().getLocale()), applicationContext.getMessage("opi.panel.label", null, UI.getCurrent().getLocale()), FontAwesome.WARNING);
		cp.setMargin(true);
		addComponent(cp);

		Boolean isUtiliseOPi = false;

		/* OPI */
		if (parametreController.getIsUtiliseOpi()) {
			isUtiliseOPi = true;
			final Label titleOpi = new Label(applicationContext.getMessage("opi.title", null, UI.getCurrent().getLocale()));
			titleOpi.addStyleName(StyleConstants.VIEW_SUBTITLE);

			final OneClickButton btnReloadOpi = new OneClickButton(applicationContext.getMessage("opi.btn.title", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
			btnReloadOpi.addClickListener(e -> {
				Object nbOpi = 0;
				try {
					nbOpi = opiContainer.getItemIds().get(1).getValue();
				} catch (final Exception ex) {
				}
				final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("opi.window.confirmReload", new Object[] { nbOpi }, UI.getCurrent().getLocale()),
					applicationContext.getMessage("opi.window.confirmReloadTitle", null, UI.getCurrent().getLocale()));
				confirmWindow.addBtnOuiListener(f -> {
					opiController.reloadOpi();
					reloadOpiContainer();
					Notification.show(applicationContext.getMessage("opi.reload.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
				});
				UI.getCurrent().addWindow(confirmWindow);

			});

			final OneClickButton btnCancelOpi = new OneClickButton(applicationContext.getMessage("opi.btn.cancel.title", null, UI.getCurrent().getLocale()), FontAwesome.CLOSE);
			btnCancelOpi.addClickListener(e -> {
				Object nbOpiAttente = 0;
				try {
					nbOpiAttente = opiContainer.getItemIds().get(0).getValue();
				} catch (final Exception ex) {
				}
				final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("opi.window.confirmCancel", new Object[] { nbOpiAttente }, UI.getCurrent().getLocale()),
					applicationContext.getMessage("opi.window.confirmCancelTitle", null, UI.getCurrent().getLocale()));
				confirmWindow.addBtnOuiListener(f -> {
					opiController.cancelOpi();
					reloadOpiContainer();
					Notification.show(applicationContext.getMessage("opi.cancel.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
				});
				UI.getCurrent().addWindow(confirmWindow);

			});

			final HorizontalLayout hlOpi = new HorizontalLayout(titleOpi, btnReloadOpi, btnCancelOpi);
			hlOpi.setWidth(ELEMENT_WIDTH, Unit.PIXELS);
			hlOpi.setSpacing(true);
			hlOpi.setComponentAlignment(btnReloadOpi, Alignment.MIDDLE_RIGHT);
			hlOpi.setComponentAlignment(btnCancelOpi, Alignment.MIDDLE_RIGHT);
			addComponent(hlOpi);

			opiTable.setWidth(ELEMENT_WIDTH, Unit.PIXELS);
			opiTable.setVisibleColumns((Object[]) FIELDS_ORDER);
			for (final String fieldName : FIELDS_ORDER) {
				opiTable.setColumnHeader(fieldName, applicationContext.getMessage("opi.table." + fieldName, null, UI.getCurrent().getLocale()));
			}
			opiTable.setSortContainerPropertyId(SimpleTablePresentation.CHAMPS_ORDER);
			opiTable.setColumnCollapsingAllowed(false);
			opiTable.setColumnReorderingAllowed(false);
			opiTable.setSelectable(false);
			opiTable.setImmediate(true);
			opiTable.setPageLength(2);
			opiTable.setCellStyleGenerator((components, itemId, columnId) -> {
				if (columnId != null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)) {
					return (ValoTheme.LABEL_BOLD);
				}
				return null;
			});
			addComponent(opiTable);
		}

		/* PJ OPI */
		if (parametreController.getIsUtiliseOpiPJ()) {
			if (isUtiliseOPi) {
				addComponent(new Label("<hr/>", ContentMode.HTML));
			}
			final Label titlePjOpi = new Label(applicationContext.getMessage("opi.pj.title", null, UI.getCurrent().getLocale()));
			titlePjOpi.addStyleName(StyleConstants.VIEW_SUBTITLE);

			final OneClickButton btnReloadPjOpi = new OneClickButton(applicationContext.getMessage("opi.pj.btn.title", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
			btnReloadPjOpi.addClickListener(e -> {
				Object nbOpiPj = 0;
				try {
					nbOpiPj = opiPjContainer.getItemIds().get(1).getValue();
				} catch (final Exception ex) {
				}
				final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("opi.pj.window.confirmReload", new Object[] { nbOpiPj }, UI.getCurrent().getLocale()),
					applicationContext.getMessage("opi.pj.window.confirmReloadTitle", null, UI.getCurrent().getLocale()));
				confirmWindow.addBtnOuiListener(f -> {
					opiController.reloadOpiPj();
					reloadOpiPjContainer();
					Notification.show(applicationContext.getMessage("opi.pj.reload.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
				});
				UI.getCurrent().addWindow(confirmWindow);
			});

			final OneClickButton btnCancelPjOpi = new OneClickButton(applicationContext.getMessage("opi.pj.btn.cancel.title", null, UI.getCurrent().getLocale()), FontAwesome.CLOSE);
			btnCancelPjOpi.addClickListener(e -> {
				Object nbOpiPjAttente = 0;
				try {
					nbOpiPjAttente = opiPjContainer.getItemIds().get(0).getValue();
				} catch (final Exception ex) {
				}
				final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("opi.pj.window.confirmCancel", new Object[] { nbOpiPjAttente }, UI.getCurrent().getLocale()),
					applicationContext.getMessage("opi.pj.window.confirmCancelTitle", null, UI.getCurrent().getLocale()));
				confirmWindow.addBtnOuiListener(f -> {
					opiController.cancelOpiPj();
					reloadOpiPjContainer();
					Notification.show(applicationContext.getMessage("opi.pj.cancel.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
				});
				UI.getCurrent().addWindow(confirmWindow);
			});

			final HorizontalLayout hlPjOpi = new HorizontalLayout(titlePjOpi, btnReloadPjOpi, btnCancelPjOpi);
			hlPjOpi.setSpacing(true);
			hlPjOpi.setWidth(ELEMENT_WIDTH, Unit.PIXELS);
			hlPjOpi.setSpacing(true);
			hlPjOpi.setComponentAlignment(btnReloadPjOpi, Alignment.MIDDLE_RIGHT);
			hlPjOpi.setComponentAlignment(btnCancelPjOpi, Alignment.MIDDLE_RIGHT);
			addComponent(hlPjOpi);

			opiPjTable.setVisibleColumns((Object[]) FIELDS_ORDER);
			opiPjTable.setWidth(ELEMENT_WIDTH, Unit.PIXELS);
			for (final String fieldName : FIELDS_ORDER) {
				opiPjTable.setColumnHeader(fieldName, applicationContext.getMessage("opi.pj.table." + fieldName, null, UI.getCurrent().getLocale()));
			}
			opiPjTable.setSortContainerPropertyId(SimpleTablePresentation.CHAMPS_ORDER);
			opiPjTable.setColumnCollapsingAllowed(false);
			opiPjTable.setColumnReorderingAllowed(false);
			opiPjTable.setSelectable(false);
			opiPjTable.setImmediate(true);
			opiPjTable.setPageLength(2);
			opiPjTable.setCellStyleGenerator((components, itemId, columnId) -> {
				if (columnId != null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)) {
					return (ValoTheme.LABEL_BOLD);
				}
				return null;
			});
			addComponent(opiPjTable);
		}
	}

	private void reloadOpiContainer() {
		opiContainer.removeAllItems();
		opiContainer.addAll(opiController.getAdminOpiPresentation());
		opiTable.sort();
	}

	private void reloadOpiPjContainer() {
		opiPjContainer.removeAllItems();
		opiPjContainer.addAll(opiController.getAdminOpiPjPresentation());
		opiPjTable.sort();
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
		reloadOpiContainer();
		reloadOpiPjContainer();
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
	}
}
