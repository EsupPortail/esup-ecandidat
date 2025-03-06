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
package fr.univlorraine.ecandidat.views.windows;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fr.univlorraine.ecandidat.controllers.BatchController;
import fr.univlorraine.ecandidat.entities.ecandidat.Batch;
import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto_;
import fr.univlorraine.ecandidat.vaadin.components.IconLabel;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/** Fenêtre de visu de l'histo des batchs
 *
 * @author Kevin Hergalant */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class AdminBatchHistoWindow extends Window {

	private static final String COLONNE_DUREE = "duree";
	private static final String COLONNE_DETAIL = "detail";

	public static final String[] BATCH_HISTO_FIELDS_ORDER = {BatchHisto_.stateBatchHisto.getName(), BatchHisto_.dateDebBatchHisto.getName(), BatchHisto_.dateFinBatchHisto.getName(), COLONNE_DUREE,
			COLONNE_DETAIL};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient BatchController batchController;

	/* Composants */
	private TableFormating batchHistoTable;
	private BeanItemContainer<BatchHisto> container;
	private OneClickButton btnFermer;
	private OneClickButton btnRefresh;

	/** Crée une fenêtre de visu de l'histo d'un batch
	 *
	 * @param batch
	 *            le batch à visualiser */
	public AdminBatchHistoWindow(final Batch batch) {
		/* Style */
		setModal(true);
		setWidth(700, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("batchHisto.window", new Object[] {batch.getCodBatch()}, UI.getCurrent().getLocale()));

		/* Table */
		container = new BeanItemContainer<>(BatchHisto.class, batchController.getBatchHisto(batch));
		batchHistoTable = new TableFormating(null, container);
		batchHistoTable.addGeneratedColumn(COLONNE_DUREE, new ColumnGenerator() {
			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final BatchHisto batchHisto = (BatchHisto) itemId;
				if (batchHisto.getDateFinBatchHisto() != null) {
					LocalDateTime dateDeb = LocalDateTime.from(batchHisto.getDateDebBatchHisto());
					Long minutes = dateDeb.until(batchHisto.getDateFinBatchHisto(), ChronoUnit.MINUTES);
					dateDeb = dateDeb.plusMinutes(minutes);
					Long secondes = dateDeb.until(batchHisto.getDateFinBatchHisto(), ChronoUnit.SECONDS);
					return new Label(applicationContext.getMessage("batch.histo.duree", new Object[] {minutes, secondes}, UI.getCurrent().getLocale()));
				}
				return null;
			}
		});
		batchHistoTable.addGeneratedColumn(COLONNE_DETAIL, new ColumnGenerator() {
			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final BatchHisto batchHisto = (BatchHisto) itemId;
				if (batchHisto.getDetailBatchHisto() != null) {
					IconLabel label = new IconLabel(true, true);
					label.setDescription(applicationContext.getMessage("batchHisto.hasdetail", null, UI.getCurrent().getLocale()));
					return label;
				}
				IconLabel label = new IconLabel(false, true);
				label.setDescription(applicationContext.getMessage("batchHisto.nodetail", null, UI.getCurrent().getLocale()));
				return label;
			}
		});
		batchHistoTable.setSelectable(true);
		batchHistoTable.setSizeFull();
		batchHistoTable.setImmediate(true);
		batchHistoTable.addItemSetChangeListener(e -> batchHistoTable.sanitizeSelection());
		batchHistoTable.setVisibleColumns((Object[]) BATCH_HISTO_FIELDS_ORDER);
		for (String fieldName : BATCH_HISTO_FIELDS_ORDER) {
			batchHistoTable.setColumnHeader(fieldName, applicationContext.getMessage("batchHisto.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		layout.addComponent(batchHistoTable);

		/* Le détail */
		Label labelDetail = new Label();
		labelDetail.setSizeUndefined();
		labelDetail.setContentMode(ContentMode.HTML);

		VerticalLayout vlDetail = new VerticalLayout();
		vlDetail.setMargin(true);
		vlDetail.addComponent(labelDetail);

		Panel panel = new Panel(applicationContext.getMessage("batchHisto.detail.title", null, UI.getCurrent().getLocale()), vlDetail);
		panel.setWidth(100, Unit.PERCENTAGE);
		panel.setHeight(250, Unit.PIXELS);
		layout.addComponent(panel);
		panel.setVisible(false);

		batchHistoTable.addValueChangeListener(e -> {
			BatchHisto batchHisto = (BatchHisto) batchHistoTable.getValue();
			if (batchHisto == null || batchHisto.getDetailBatchHisto() == null) {
				labelDetail.setValue("");
				panel.setVisible(false);
				center();
			} else {
				labelDetail.setValue(batchHisto.getDetailBatchHisto());
				panel.setVisible(true);
				center();
			}
		});

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnFermer = new OneClickButton(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnFermer.addClickListener(e -> close());
		buttonsLayout.addComponent(btnFermer);
		buttonsLayout.setComponentAlignment(btnFermer, Alignment.MIDDLE_LEFT);

		btnRefresh = new OneClickButton(applicationContext.getMessage("btnRefresh", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
		btnRefresh.addClickListener(e -> {
			container.removeAllItems();
			container.addAll(batchController.getBatchHisto(batch));
		});
		buttonsLayout.addComponent(btnRefresh);
		buttonsLayout.setComponentAlignment(btnRefresh, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

}
