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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Extension;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid.MultiSelectionModel;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.OpiController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.FileOpi;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.CustomPanel;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
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

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	public static final String[] FIELDS_ORDER = { SimpleTablePresentation.CHAMPS_TITLE, SimpleTablePresentation.CHAMPS_VALUE };
	public static final String[] FIELDS_ORDER_FILE = { FileOpi.CHAMPS_DATE, FileOpi.CHAMPS_CANDIDAT, FileOpi.CHAMPS_VOEUX, FileOpi.CHAMPS_BOTH };

	/* Composants */
	private final BeanItemContainer<SimpleTablePresentation> opiContainer = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating opiTable = new TableFormating(null, opiContainer);
	private final BeanItemContainer<SimpleTablePresentation> opiPjContainer = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating opiPjTable = new TableFormating(null, opiPjContainer);
	private final GridFormatting<FileOpi> fileOpiGrid = new GridFormatting<>(FileOpi.class);

	private static final Integer ELEMENT_WIDTH = 600;

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

		Boolean addHr = false;

		/* OPI */
		if (parametreController.getIsUtiliseOpi()) {
			addHr = true;
			final Label titleOpi = new Label(applicationContext.getMessage("opi.title", null, UI.getCurrent().getLocale()));
			titleOpi.addStyleName(StyleConstants.VIEW_SUBTITLE);

			final OneClickButton btnReloadOpi = new OneClickButton(applicationContext.getMessage("opi.btn.title", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
			btnReloadOpi.addClickListener(e -> {
				final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("opi.window.confirmReload", null, UI.getCurrent().getLocale()),
					applicationContext.getMessage("opi.window.confirmReloadTitle", null, UI.getCurrent().getLocale()));
				confirmWindow.addBtnOuiListener(f -> {
					opiController.reloadOpi();
					reloadOpiContainer();
					Notification.show(applicationContext.getMessage("opi.reload.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
				});
				UI.getCurrent().addWindow(confirmWindow);

			});

			final HorizontalLayout hlOpi = new HorizontalLayout(titleOpi, btnReloadOpi);
			hlOpi.setWidth(ELEMENT_WIDTH, Unit.PIXELS);
			hlOpi.setSpacing(true);
			hlOpi.setComponentAlignment(btnReloadOpi, Alignment.MIDDLE_RIGHT);
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
			setSizeFull();
			if (addHr) {
				addComponent(new Label("<hr/>", ContentMode.HTML));
			}
			addHr = true;
			final Label titlePjOpi = new Label(applicationContext.getMessage("opi.pj.title", null, UI.getCurrent().getLocale()));
			titlePjOpi.addStyleName(StyleConstants.VIEW_SUBTITLE);

			final OneClickButton btnReloadPjOpi = new OneClickButton(applicationContext.getMessage("opi.pj.btn.title", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
			btnReloadPjOpi.addClickListener(e -> {
				final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("opi.pj.window.confirmReload", null, UI.getCurrent().getLocale()),
					applicationContext.getMessage("opi.pj.window.confirmReloadTitle", null, UI.getCurrent().getLocale()));
				confirmWindow.addBtnOuiListener(f -> {
					opiController.reloadOpiPj();
					reloadOpiPjContainer();
					Notification.show(applicationContext.getMessage("opi.pj.reload.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
				});
				UI.getCurrent().addWindow(confirmWindow);
			});

			final HorizontalLayout hlPjOpi = new HorizontalLayout(titlePjOpi, btnReloadPjOpi);
			hlPjOpi.setSpacing(true);
			hlPjOpi.setWidth(ELEMENT_WIDTH, Unit.PIXELS);
			hlPjOpi.setSpacing(true);
			hlPjOpi.setComponentAlignment(btnReloadPjOpi, Alignment.MIDDLE_RIGHT);
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

		final List<FileOpi> files = siScolService.getFilesOpi();
		if (files.size() > 0) {
			if (addHr) {
				addComponent(new Label("<hr/>", ContentMode.HTML));
			}

			final Label titlePjOpi = new Label(applicationContext.getMessage("opi.file.download.title", null, UI.getCurrent().getLocale()));
			titlePjOpi.addStyleName(StyleConstants.VIEW_SUBTITLE);
			addComponent(titlePjOpi);

			final Button tempDownloadBtn = new Button();
			tempDownloadBtn.setId("tempdownloadbtn");
			tempDownloadBtn.addStyleName(StyleConstants.HIDDEN);
			addComponent(tempDownloadBtn);
			final List<Extension> listExt = new ArrayList<>();

			final Button deleteBtn = new Button(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()));
			deleteBtn.addClickListener(e -> {
				final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("opi.file.window.confirmDelete", null, UI.getCurrent().getLocale()),
					applicationContext.getMessage("opi.file.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
				confirmWindow.addBtnOuiListener(o -> {
					siScolService.deleteFileOpi(fileOpiGrid.getSelectedRows().stream().map(opi -> (FileOpi) opi).collect(Collectors.toList()));
					reloadOpiFileContainer();
					fileOpiGrid.deselectAll();
				});
				UI.getCurrent().addWindow(confirmWindow);
			});
			deleteBtn.setEnabled(false);

			final HorizontalLayout hlPjOpi = new HorizontalLayout(titlePjOpi, tempDownloadBtn, deleteBtn);
			hlPjOpi.setSpacing(true);
			hlPjOpi.setWidth(100, Unit.PERCENTAGE);
			hlPjOpi.setComponentAlignment(deleteBtn, Alignment.MIDDLE_RIGHT);
			addComponent(hlPjOpi);

			/* Table des formations */
			fileOpiGrid.initColumn(FIELDS_ORDER_FILE, "opi.file.download.grid.", FileOpi.CHAMPS_DATE, SortDirection.DESCENDING);
			fileOpiGrid.setSelectionMode(SelectionMode.MULTI);
			fileOpiGrid.addSelectionListener(e -> {
				/* Les boutons d'édition et de suppression de fichiers sont actifs seulement si
				 * une ligne est sélectionnée. */
				final Integer nb = fileOpiGrid.getSelectedRows().size();
				deleteBtn.setEnabled(nb >= 1);
			});

			/* Téléchargement fichier candidat */
			fileOpiGrid.getColumn(FileOpi.CHAMPS_CANDIDAT).setRenderer(new ButtonRenderer(new RendererClickListener() {
				@Override
				public void click(final RendererClickEvent event) {
					listExt.forEach(e -> tempDownloadBtn.removeExtension(e));
					listExt.clear();
					final FileDownloader fileDownloader = new OnDemandFileDownloader(new OnDemandStreamFile() {
						@Override
						public OnDemandFile getOnDemandFile() {
							try {
								final FileOpi fileOpi = (FileOpi) event.getItemId();
								final InputStream targetStream = new FileInputStream(new File(fileOpi.getPathToCandidat()));
								final OnDemandFile file = new OnDemandFile(fileOpi.getLibFileCandidat(), targetStream);
								tempDownloadBtn.setEnabled(true);
								return file;
							} catch (final Exception ex) {
								return null;
							}

						}
					}, tempDownloadBtn);
					fileDownloader.extend(tempDownloadBtn);
					listExt.add(fileDownloader);
					Page.getCurrent().getJavaScript().execute("document.getElementById('tempdownloadbtn').click();");

				}
			}, null));

			/* Téléchargement fichier candidature */
			fileOpiGrid.getColumn(FileOpi.CHAMPS_VOEUX).setRenderer(new ButtonRenderer(new RendererClickListener() {
				@Override
				public void click(final RendererClickEvent event) {
					listExt.forEach(e -> tempDownloadBtn.removeExtension(e));
					listExt.clear();
					final FileDownloader fileDownloader = new OnDemandFileDownloader(new OnDemandStreamFile() {
						@Override
						public OnDemandFile getOnDemandFile() {
							try {
								final FileOpi fileOpi = (FileOpi) event.getItemId();
								final InputStream targetStream = new FileInputStream(new File(fileOpi.getPathToVoeux()));
								final OnDemandFile file = new OnDemandFile(fileOpi.getLibFileVoeux(), targetStream);
								tempDownloadBtn.setEnabled(true);
								return file;
							} catch (final Exception ex) {
								return null;
							}

						}
					}, tempDownloadBtn);
					fileDownloader.extend(tempDownloadBtn);
					listExt.add(fileDownloader);
					Page.getCurrent().getJavaScript().execute("document.getElementById('tempdownloadbtn').click();");

				}
			}, ""));

			/* Téléchargement fichier candidature */
			fileOpiGrid.getColumn(FileOpi.CHAMPS_BOTH).setRenderer(new ButtonRenderer(new RendererClickListener() {

				@Override
				public void click(final RendererClickEvent event) {
					listExt.forEach(e -> tempDownloadBtn.removeExtension(e));
					listExt.clear();
					final FileDownloader fileDownloader = new OnDemandFileDownloader(new OnDemandStreamFile() {
						@Override
						public OnDemandFile getOnDemandFile() {
							final OnDemandFile file = opiController.getZipOpi((FileOpi) event.getItemId());
							if (file != null) {
								tempDownloadBtn.setEnabled(true);
								return file;
							}
							tempDownloadBtn.setEnabled(true);
							return null;
						}
					}, tempDownloadBtn);

					listExt.add(fileDownloader);
					Page.getCurrent().getJavaScript().execute("document.getElementById('tempdownloadbtn').click();");
				}
			}, ""));

			/* Selection */
			fileOpiGrid.addItemClickListener(e -> {
				/* Suivant le mode de slection de la grid on fait un traitement */
				final MultiSelectionModel selection = (MultiSelectionModel) fileOpiGrid.getSelectionModel();
				selection.deselectAll();
				try {
					selection.select(e.getItemId());
				} catch (final Exception e1) {
					return;
				}
			});

			/* Styles */
			fileOpiGrid.setStyleName(StyleConstants.GRID_BTN);
			fileOpiGrid.setCellStyleGenerator(cell -> {
				final FileOpi fileOpi = (FileOpi) cell.getItemId();
				if (FileOpi.CHAMPS_CANDIDAT.equals(cell.getPropertyId()) && fileOpi.getPathToCandidat() == null) {
					return StyleConstants.HIDDEN;
				}
				if (FileOpi.CHAMPS_VOEUX.equals(cell.getPropertyId()) && fileOpi.getPathToVoeux() == null) {
					return StyleConstants.HIDDEN;
				}
				if (FileOpi.CHAMPS_CANDIDAT.equals(cell.getPropertyId()) || FileOpi.CHAMPS_VOEUX.equals(cell.getPropertyId()) || FileOpi.CHAMPS_BOTH.equals(cell.getPropertyId())) {
					return StyleConstants.CENTER;
				}
				return null;
			});

			addComponent(fileOpiGrid);
			setExpandRatio(fileOpiGrid, 1);
			fileOpiGrid.setSizeFull();
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

	private void reloadOpiFileContainer() {
		fileOpiGrid.removeAll();
		fileOpiGrid.addItems(siScolService.getFilesOpi());
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
		reloadOpiContainer();
		reloadOpiPjContainer();
		reloadOpiFileContainer();
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
	}
}
