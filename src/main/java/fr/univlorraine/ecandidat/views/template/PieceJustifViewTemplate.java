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
package fr.univlorraine.ecandidat.views.template;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.DefaultItemSorter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.PieceJustifController;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileLayout;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.windows.ImageViewerWindow;

/**
 * Template de la vue des PieceJustif, utilisé par la scol et ctrCand
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class PieceJustifViewTemplate extends VerticalLayout {

	public static final String NAME = "scolPieceJustifView";

	String[] FIELDS_ORDER;
	String[] FIELDS_ORDER_FILE = {PieceJustif_.orderPj.getName(), PieceJustif_.codPj.getName(),
			PieceJustif_.libPj.getName(), PieceJustif_.tesPj.getName(), PieceJustif_.temCommunPj.getName(),
			PieceJustif_.temUnicitePj.getName(), PieceJustif_.temConditionnelPj.getName(),
			PieceJustif_.typeTraitement.getName(), PieceJustif_.codApoPj.getName(), PieceJustif_.fichier.getName()};
	String[] FIELDS_ORDER_NO_FILE = {PieceJustif_.orderPj.getName(), PieceJustif_.codPj.getName(),
			PieceJustif_.libPj.getName(), PieceJustif_.tesPj.getName(), PieceJustif_.temCommunPj.getName(),
			PieceJustif_.temUnicitePj.getName(), PieceJustif_.temConditionnelPj.getName(),
			PieceJustif_.typeTraitement.getName(), PieceJustif_.codApoPj.getName()};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient PieceJustifController pieceJustifController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient I18nController i18nController;

	// protected Boolean dematCtrCand0 = true;
	protected Boolean isVisuPjCommunMode = true;
	protected Boolean isReadOnly = false;
	private Boolean isOrderEnable = true;

	/* Composants */
	protected Label titleParam = new Label();
	protected OneClickButton btnNew = new OneClickButton(FontAwesome.PLUS);
	protected OneClickButton btnEdit = new OneClickButton(FontAwesome.PENCIL);
	protected HorizontalLayout buttonsLayout = new HorizontalLayout();
	protected BeanItemContainer<PieceJustif> container = new BeanItemContainer<>(PieceJustif.class);
	protected TableFormating pieceJustifTable = new TableFormating(null, container);

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
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);

		/* Boutons */
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		btnNew.setCaption(applicationContext.getMessage("pieceJustif.btnNouveau", null, UI.getCurrent().getLocale()));
		btnNew.setEnabled(true);
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);

		btnEdit.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (pieceJustifTable.getValue() instanceof PieceJustif) {
				pieceJustifController.editPieceJustif((PieceJustif) pieceJustifTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);

		OneClickButton btnDelete = new OneClickButton(
				applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (pieceJustifTable.getValue() instanceof PieceJustif) {
				pieceJustifController.deletePieceJustif((PieceJustif) pieceJustifTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);

		/* Table des pieceJustifs */
		pieceJustifTable.addBooleanColumn(PieceJustif_.tesPj.getName());
		pieceJustifTable.addBooleanColumn(PieceJustif_.temCommunPj.getName());
		pieceJustifTable.addBooleanColumn(PieceJustif_.temUnicitePj.getName());
		pieceJustifTable.addBooleanColumn(PieceJustif_.temConditionnelPj.getName());

		/* Type de traitement */
		pieceJustifTable.addGeneratedColumn(PieceJustif_.typeTraitement.getName(), new ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final PieceJustif pieceJustif = (PieceJustif) itemId;
				if (pieceJustif.getTypeTraitement() != null) {
					return pieceJustif.getTypeTraitement().getLibTypTrait();
				} else {
					return applicationContext.getMessage("typeTraitement.lib.all", null, UI.getCurrent().getLocale());
				}
			}

		});

		if (!fileController.getModeDematBackoffice().equals(ConstanteUtils.TYPE_FICHIER_STOCK_NONE)) {
			pieceJustifTable.addGeneratedColumn(PieceJustif_.fichier.getName(), new ColumnGenerator() {

				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final PieceJustif pieceJustif = (PieceJustif) itemId;
					if (pieceJustif.getFichier() == null) {
						if (isVisuPjCommunMode && !isReadOnly) {
							OneClickButton btnAdd = new OneClickButton(FontAwesome.PLUS);
							btnAdd.addStyleName(StyleConstants.ON_DEMAND_FILE_LAYOUT);
							btnAdd.setDescription(
									applicationContext.getMessage("file.btnAdd", null, UI.getCurrent().getLocale()));
							btnAdd.addClickListener(
									e -> pieceJustifController.addFileToPieceJustificative(pieceJustif));
							return btnAdd;
						}
						return null;
					} else {
						OnDemandFileLayout fileLayout = new OnDemandFileLayout(
								pieceJustif.getFichier().getNomFichier());

						/* Delete */
						if (isVisuPjCommunMode && !isReadOnly) {
							fileLayout.addBtnDelClickListener(
									e -> pieceJustifController.deleteFileToPieceJustificative(pieceJustif));
						}

						/* Viewer si JPG */
						if (MethodUtils.isImgFileName(pieceJustif.getFichier().getNomFichier())) {
							fileLayout.addBtnViewerClickListener(e -> {
								OnDemandFile file = new OnDemandFile(pieceJustif.getFichier().getNomFichier(),
										fileController.getInputStreamFromFichier(pieceJustif.getFichier()));
								ImageViewerWindow iv = new ImageViewerWindow(file, null);
								UI.getCurrent().addWindow(iv);
							});
							/* Opener si PDF */
						} else if (MethodUtils.isPdfFileName(pieceJustif.getFichier().getNomFichier())) {
							fileLayout.addBtnViewerPdfBrowserOpener(new OnDemandStreamFile() {
								@Override
								public OnDemandFile getOnDemandFile() {
									return new OnDemandFile(pieceJustif.getFichier().getNomFichier(),
											fileController.getInputStreamFromFichier(pieceJustif.getFichier()));
								}
							});
						}

						/* Download */
						fileLayout.addBtnDownloadFileDownloader(new OnDemandStreamFile() {
							@Override
							public OnDemandFile getOnDemandFile() {
								return new OnDemandFile(pieceJustif.getFichier().getNomFichier(),
										fileController.getInputStreamFromFichier(pieceJustif.getFichier()));
							}
						});

						return fileLayout;
					}
				}
			});
			FIELDS_ORDER = FIELDS_ORDER_FILE;
		} else {
			FIELDS_ORDER = FIELDS_ORDER_NO_FILE;
		}

		pieceJustifTable.setSizeFull();
		pieceJustifTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			pieceJustifTable.setColumnHeader(fieldName,
					applicationContext.getMessage("pieceJustif.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		container.setItemSorter(new DefaultItemSorter() {

			@Override
			public int compare(final Object itemId1, final Object itemId2) {
				return ((PieceJustif) itemId1).compareTo((PieceJustif) itemId2);
			}
		});
		pieceJustifTable.addHeaderClickListener(e -> {
			container.setItemSorter(new DefaultItemSorter());
			isOrderEnable = false;
		});
		pieceJustifTable.setColumnCollapsingAllowed(true);
		pieceJustifTable.setColumnReorderingAllowed(true);
		pieceJustifTable.setSelectable(true);
		pieceJustifTable.setImmediate(true);
		pieceJustifTable.setColumnWidth(PieceJustif_.orderPj.getName(), 60);
		pieceJustifTable.addItemSetChangeListener(e -> pieceJustifTable.sanitizeSelection());
		pieceJustifTable.addValueChangeListener(e -> {
			/*
			 * Les boutons d'édition et de suppression de pieceJustif sont actifs seulement
			 * si une pieceJustif est sélectionnée.
			 */
			boolean pieceJustifIsSelected = pieceJustifTable.getValue() instanceof PieceJustif;
			btnEdit.setEnabled(pieceJustifIsSelected);
			btnDelete.setEnabled(pieceJustifIsSelected);
		});
		addComponent(pieceJustifTable);
		setExpandRatio(pieceJustifTable, 1);
	}

	/**
	 * Trie le container
	 */
	protected void sortContainer() {
		if (isOrderEnable) {
			container.sort(new Object[] {PieceJustif_.orderPj.getName()}, new boolean[] {true});
		} else {
			pieceJustifTable.sort();
		}
	}
}
