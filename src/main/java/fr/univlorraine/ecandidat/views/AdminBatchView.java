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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.BatchController;
import fr.univlorraine.ecandidat.entities.ecandidat.Batch;
import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.Batch_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des batchs
 * @author Kevin Hergalant
 *
 */
@SpringView(name = AdminBatchView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_ADMIN)
public class AdminBatchView extends VerticalLayout implements View, EntityPushListener<Batch> {

	/** serialVersionUID **/
	private static final long serialVersionUID = 2473040540610218037L;

	public static final String NAME = "adminBatchView";

	public static final String[] BATCH_FIELDS_ORDER = {Batch_.codBatch.getName(), Batch_.libBatch.getName(), Batch_.tesBatch.getName(), Batch_.temIsLaunchImediaBatch.getName(),"prog","histo"};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient BatchController batchController;
	@Resource
	private transient EntityPusher<Batch> batchEntityPusher;

	/* Composants */
	private OneClickButton btnRefresh = new OneClickButton(FontAwesome.REFRESH);
	private OneClickButton btnEdit = new OneClickButton(FontAwesome.PENCIL);
	private OneClickButton btnHisto = new OneClickButton(FontAwesome.CLOCK_O);
	private OneClickButton btnLaunch = new OneClickButton(FontAwesome.ROCKET);
	private BeanItemContainer<Batch> container = new BeanItemContainer<Batch>(Batch.class);
	private TableFormating batchTable = new TableFormating(null,container);
	
	@Resource
	private transient DateTimeFormatter formatterDateTime;

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
		Label titleParam = new Label(applicationContext.getMessage("batch.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		HorizontalLayout leftButtonsLayout = new HorizontalLayout();
		leftButtonsLayout.setSpacing(true);
		buttonsLayout.addComponent(leftButtonsLayout);
		buttonsLayout.setComponentAlignment(leftButtonsLayout, Alignment.MIDDLE_LEFT);

		btnRefresh.setCaption(applicationContext.getMessage("btnRefresh", null, UI.getCurrent().getLocale()));
		btnRefresh.addClickListener(e -> {
			container.removeAllItems();
			container.addAll(batchController.getBatchs());
		});
		leftButtonsLayout.addComponent(btnRefresh);
		
		btnEdit.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (batchTable.getValue() instanceof Batch) {
				batchController.editBatch((Batch) batchTable.getValue());
			}
		});
		leftButtonsLayout.addComponent(btnEdit);

		btnHisto.setCaption(applicationContext.getMessage("batch.btnHisto", null, UI.getCurrent().getLocale()));
		btnHisto.setEnabled(false);
		btnHisto.addClickListener(e -> {
			if (batchTable.getValue() instanceof Batch) {
				batchController.showBatchHisto((Batch) batchTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnHisto);
		buttonsLayout.setComponentAlignment(btnHisto, Alignment.MIDDLE_CENTER);

		btnLaunch.setCaption(applicationContext.getMessage("batch.btnLaunch", null, UI.getCurrent().getLocale()));
		btnLaunch.setEnabled(false);
		btnLaunch.addClickListener(e -> {
			if (batchTable.getValue() instanceof Batch) {
				if (batchTable.getValue()!=null){
					if (((Batch)batchTable.getValue()).getTemIsLaunchImediaBatch()){
						batchController.cancelRunImmediatly((Batch) batchTable.getValue());
					}else{
						batchController.runImmediatly((Batch) batchTable.getValue());
					}
				}
			}
		});
		buttonsLayout.addComponent(btnLaunch);
		buttonsLayout.setComponentAlignment(btnLaunch, Alignment.MIDDLE_RIGHT);

		/* Table des batchs */
		batchTable.addBooleanColumn(Batch_.tesBatch.getName());
		batchTable.addBooleanColumn(Batch_.temIsLaunchImediaBatch.getName());
		batchTable.addGeneratedColumn("prog", new ColumnGenerator() {
			
			/*** serialVersionUID*/
			private static final long serialVersionUID = 7461290324017459118L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final Batch batch = (Batch) itemId;
				return new Label(getLabelProgramme(batch));
			}
		});
		batchTable.addGeneratedColumn("histo", new ColumnGenerator() {
			
			/*** serialVersionUID*/
			private static final long serialVersionUID = 7461290324017459118L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final Batch batch = (Batch) itemId;
				if (batch.getLastBatchHisto()!=null)
				{
					return new Label(getLabelBatchHisto(batch.getLastBatchHisto()));
				}
				return null;
			}
		});
		batchTable.setSizeFull();
		batchTable.setVisibleColumns((Object[]) BATCH_FIELDS_ORDER);
		for (String fieldName : BATCH_FIELDS_ORDER) {
			batchTable.setColumnHeader(fieldName, applicationContext.getMessage("batch.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		batchTable.setSortContainerPropertyId(Batch_.codBatch.getName());
		batchTable.setColumnCollapsingAllowed(true);
		batchTable.setColumnReorderingAllowed(true);
		batchTable.setSelectable(true);
		batchTable.setImmediate(true);
		batchTable.addItemSetChangeListener(e -> batchTable.sanitizeSelection());
		batchTable.addValueChangeListener(e -> {
			/* Les boutons d'édition, de programme et de lancement de batch sont actifs seulement si un batch est sélectionnée. */
			boolean batchIsSelected = batchTable.getValue() instanceof Batch;
			btnEdit.setEnabled(batchIsSelected);
			btnHisto.setEnabled(batchIsSelected);
			btnLaunch.setEnabled(batchIsSelected);
			if (batchTable.getValue()!=null){
				if (((Batch)batchTable.getValue()).getTemIsLaunchImediaBatch()){
					btnLaunch.setCaption(applicationContext.getMessage("batch.btnLaunch.cancel", null, UI.getCurrent().getLocale()));
				}else{
					btnLaunch.setCaption(applicationContext.getMessage("batch.btnLaunch", null, UI.getCurrent().getLocale()));
				}
			}
		});
		batchTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				batchTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(batchTable);
		setExpandRatio(batchTable, 1);

		/* Inscrit la vue aux mises à jour de batchs */
		batchEntityPusher.registerEntityPushListener(this);
	}
	
	/** Renvoie le label d'historique
	 * @param batchHisto
	 * @return
	 */
	private String getLabelBatchHisto(BatchHisto batchHisto){
		String txt = batchHisto.getStateBatchHisto()
				+" - "+applicationContext.getMessage("batch.histo.deb", new Object[]{batchHisto.getDateDebBatchHisto().format(formatterDateTime)}, UI.getCurrent().getLocale());
		if (batchHisto.getDateFinBatchHisto()!=null){
			LocalDateTime dateDeb = LocalDateTime.from(batchHisto.getDateDebBatchHisto());
			Long minutes = dateDeb.until(batchHisto.getDateFinBatchHisto(), ChronoUnit.MINUTES);
			dateDeb = dateDeb.plusMinutes(minutes);
			Long secondes = dateDeb.until(batchHisto.getDateFinBatchHisto(), ChronoUnit.SECONDS);
			txt += " - "+applicationContext.getMessage("batch.histo.fin", new Object[]{batchHisto.getDateFinBatchHisto().format(formatterDateTime)}, UI.getCurrent().getLocale());
			txt += " - "+applicationContext.getMessage("batch.histo.duree", new Object[]{getTimeFormated(minutes),getTimeFormated(secondes)}, UI.getCurrent().getLocale());
		}
		return txt;
	}
	
	/** Ajoute un zéro si la taille est de 1
	 * @return un timing formatte
	 */
	private String getTimeFormated(Object t){
		try{
			if (t == null){
				return "";
			}else{
				String retour = String.valueOf(t);
				if (retour.length()==1){
					retour = "0"+retour;
				}
				return retour;
			}
		}catch(Exception e){
			return "";
		}
		
	}
	
	/** Renvoie le label de schedul
	 * @param batch
	 * @return le label de programmation
	 */
	public String getLabelProgramme(Batch batch){
		String label = "";
		
		if (batch.getFixeYearBatch()!=null && batch.getFixeMonthBatch()!=null && batch.getFixeDayBatch()!=null){
			label += applicationContext.getMessage("batch.prog.jour", new Object[]{DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.of(batch.getFixeYearBatch(), batch.getFixeMonthBatch(), batch.getFixeDayBatch()))}, UI.getCurrent().getLocale());
		}else if (batch.getFixeMonthBatch()!=null && batch.getFixeDayBatch()!=null){
			label += applicationContext.getMessage("batch.prog.annuel", new Object[]{batch.getFixeDayBatch(),ConstanteUtils.NOM_MOIS_SHORT[batch.getFixeMonthBatch()-1]}, UI.getCurrent().getLocale());
		}else if (batch.getFixeDayBatch()!=null){
			label += applicationContext.getMessage("batch.prog.mensuel", new Object[]{batch.getFixeDayBatch()}, UI.getCurrent().getLocale());
		}
		/*else if (batch.getFixeDateBatch()!=null){
			label += applicationContext.getMessage("batch.prog.jour", new Object[]{DateTimeFormatter.ofPattern("dd/MM/yyyy").format(batch.getFixeDateBatch())}, UI.getCurrent().getLocale());
		}*/
		else{
			if (batch.getTemLundiBatch()){
				label = MethodUtils.constructStringEnum(label,ConstanteUtils.NOM_JOURS[0]);
			}
			if (batch.getTemMardiBatch()){
				label = MethodUtils.constructStringEnum(label,ConstanteUtils.NOM_JOURS[1]);
			}
			if (batch.getTemMercrBatch()){
				label = MethodUtils.constructStringEnum(label,ConstanteUtils.NOM_JOURS[2]);
			}
			if (batch.getTemJeudiBatch()){
				label = MethodUtils.constructStringEnum(label,ConstanteUtils.NOM_JOURS[3]);
			}
			if (batch.getTemVendrediBatch()){
				label = MethodUtils.constructStringEnum(label,ConstanteUtils.NOM_JOURS[4]);
			}
			if (batch.getTemSamediBatch()){
				label = MethodUtils.constructStringEnum(label,ConstanteUtils.NOM_JOURS[5]);
			}
			if (batch.getTemDimanBatch()){
				label = MethodUtils.constructStringEnum(label,ConstanteUtils.NOM_JOURS[6]);
			}
			if (label.equals("")){
				label = applicationContext.getMessage("batch.prog.noday", null, UI.getCurrent().getLocale());
			}else{
				label = applicationContext.getMessage("batch.prog.day.liste", new Object[]{label}, UI.getCurrent().getLocale());
			}
		}		
		label += " "+applicationContext.getMessage("batch.prog.hour", new Object[]{getTimeFormated(batch.getFixeHourBatch().getHour()),getTimeFormated(batch.getFixeHourBatch().getMinute())}, UI.getCurrent().getLocale());
		return label;
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		container.removeAllItems();
		container.addAll(batchController.getBatchs());
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		/* Inscrit la vue aux mises à jour de batchs */
		batchEntityPusher.unregisterEntityPushListener(this);

		super.detach();
	}
	
	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Batch entity) {
		batchTable.removeItem(entity);
		entity.setLastBatchHisto(batchController.getLastBatchHisto(entity));
		batchTable.addItem(entity);
		batchTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Batch entity) {
		batchTable.removeItem(entity);
		entity.setLastBatchHisto(batchController.getLastBatchHisto(entity));
		batchTable.addItem(entity);
		batchTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Batch entity) {
		
	}
}
