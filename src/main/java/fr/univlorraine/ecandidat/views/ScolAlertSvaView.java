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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.AlertSvaController;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.AlertSva;
import fr.univlorraine.ecandidat.entities.ecandidat.AlertSva_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.DateSVAListener;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.CustomPanel;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des alertes SVA par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolAlertSvaView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolAlertSvaView extends VerticalLayout implements View, DateSVAListener, EntityPushListener<AlertSva>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -129634359128603915L;

	public static final String NAME = "scolAlertSvaView";

	public static final String[] FIELDS_ORDER = {AlertSva_.nbJourSva.getName(),AlertSva_.colorSva.getName(),AlertSva_.tesSva.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient AlertSvaController alertSvaController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient EntityPusher<AlertSva> alertSvaEntityPusher;

	/* Composants */
	private TableFormating alertSvaTable = new TableFormating();
	private BeanItemContainer<SimpleTablePresentation> parametreContainer = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	private String parametreDateSva;
	private Boolean parametreDefinitif;

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
		Label titleParam = new Label(applicationContext.getMessage("alertSva.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);
		
		CustomPanel panelMessage = new CustomPanel(applicationContext.getMessage("informations", null, UI.getCurrent().getLocale()), FontAwesome.INFO_CIRCLE);
		panelMessage.setMessage(applicationContext.getMessage("alertSva.info", null, UI.getCurrent().getLocale()));
		panelMessage.setWidthMax();
		panelMessage.setMargin(true);
		addComponent(panelMessage);
		
		/*Choix de la date*/
		HorizontalLayout paramLayout = new HorizontalLayout();
		paramLayout.setSpacing(true);
		addComponent(paramLayout);
		
		/*Table des parametres*/
		TableFormating table = new TableFormating(null, parametreContainer);
		table.addBooleanColumn(SimpleTablePresentation.CHAMPS_VALUE,false);
		String[] FIELDS_ORDER_PARAM = {SimpleTablePresentation.CHAMPS_TITLE,SimpleTablePresentation.CHAMPS_VALUE};
		table.setVisibleColumns((Object[]) FIELDS_ORDER_PARAM);
		table.setColumnCollapsingAllowed(false);
		table.setColumnReorderingAllowed(false);
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table.setSelectable(false);
		table.setImmediate(true);
		table.setPageLength(2);
		table.setWidth(100, Unit.PERCENTAGE);
		table.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 350);
		table.setColumnWidth(SimpleTablePresentation.CHAMPS_VALUE, 200);
		table.setCellStyleGenerator((components, itemId, columnId)->{
			if (columnId!=null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)){
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		paramLayout.addComponent(table);		
		paramLayout.setComponentAlignment(table, Alignment.MIDDLE_CENTER);

		
		OneClickButton buttonDate = new OneClickButton(applicationContext.getMessage("btnModifier", null, UI.getCurrent().getLocale()), FontAwesome.CALENDAR);
		buttonDate.addClickListener(e->{
			parametreController.changeSVAParametre(this, parametreDateSva, parametreDefinitif);
		});
		paramLayout.addComponent(buttonDate);		
		paramLayout.setComponentAlignment(buttonDate, Alignment.MIDDLE_CENTER);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);


		OneClickButton btnNew = new OneClickButton(applicationContext.getMessage("alertSva.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			alertSvaController.editNewAlertSva();
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);


		OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (alertSvaTable.getValue() instanceof AlertSva) {
				alertSvaController.editAlertSva((AlertSva) alertSvaTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);
		
		OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (alertSvaTable.getValue() instanceof AlertSva) {
				alertSvaController.deleteAlertSva((AlertSva) alertSvaTable.getValue());
			}			
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);


		/* Table des alertSvas */
		BeanItemContainer<AlertSva> container = new BeanItemContainer<AlertSva>(AlertSva.class, cacheController.getAlertesSva());
		alertSvaTable.setContainerDataSource(container);		
		alertSvaTable.addBooleanColumn(AlertSva_.tesSva.getName());
		alertSvaTable.setSizeFull();
		alertSvaTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			alertSvaTable.setColumnHeader(fieldName, applicationContext.getMessage("alertSva.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		alertSvaTable.addGeneratedColumn(AlertSva_.colorSva.getName(), new ColumnGenerator() {
			
			/**serialVersionUID**/
			private static final long serialVersionUID = -2562681984380111747L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				AlertSva alertSva = (AlertSva) itemId;
				HorizontalLayout hlColor = new HorizontalLayout();
				hlColor.setSpacing(true);
				Label labelColor = new Label("<div style='border:1px solid;width:20px;height:20px;background:"+alertSva.getColorSva()+";'></div>", ContentMode.HTML);
				Label labelTxt = new Label(alertSva.getColorSva());
				hlColor.addComponent(labelColor);
				hlColor.setComponentAlignment(labelColor, Alignment.MIDDLE_LEFT);
				hlColor.addComponent(labelTxt);
				hlColor.setComponentAlignment(labelTxt, Alignment.MIDDLE_LEFT);
				return hlColor;
			}
		});
		alertSvaTable.setSortContainerPropertyId(AlertSva_.nbJourSva.getName());
		alertSvaTable.setColumnCollapsingAllowed(true);
		alertSvaTable.setColumnReorderingAllowed(true);
		alertSvaTable.setSelectable(true);
		alertSvaTable.setImmediate(true);
		alertSvaTable.addItemSetChangeListener(e -> alertSvaTable.sanitizeSelection());
		alertSvaTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de alertSva sont actifs seulement si une alertSva est sélectionnée. */
			boolean alertSvaIsSelectedEdit = alertSvaTable.getValue() instanceof AlertSva;
			
			btnEdit.setEnabled(alertSvaIsSelectedEdit);
			btnDelete.setEnabled(alertSvaIsSelectedEdit);
		});
		alertSvaTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				alertSvaTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(alertSvaTable);
		setExpandRatio(alertSvaTable, 1);
		
		/* Inscrit la vue aux mises à jour de alerteSva */
		alertSvaEntityPusher.registerEntityPushListener(this);
	}
	
	private void majValueParametre(){
		parametreContainer.removeAllItems();
		parametreContainer.addBean(new SimpleTablePresentation("1",applicationContext.getMessage("alertSva.date.title", null, UI.getCurrent().getLocale()), alertSvaController.getLibelleDateSVA(parametreDateSva)));
		parametreContainer.addBean(new SimpleTablePresentation("2",applicationContext.getMessage("alertSva.definitif.title", null, UI.getCurrent().getLocale()), parametreDefinitif));
	}
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		parametreDateSva = parametreController.getAlertSvaDat();
		parametreDefinitif = parametreController.getAlertSvaDefinitif();
		majValueParametre();
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de alerteSva */
		alertSvaEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(AlertSva entity) {
		alertSvaTable.removeItem(entity);
		alertSvaTable.addItem(entity);
		alertSvaTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(AlertSva entity) {
		alertSvaTable.removeItem(entity);
		alertSvaTable.addItem(entity);
		alertSvaTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(AlertSva entity) {
		alertSvaTable.removeItem(entity);
	}

	@Override
	public void changeModeParametreSVA() {
		parametreDateSva = parametreController.getAlertSvaDat();
		parametreDefinitif = parametreController.getAlertSvaDefinitif();
		majValueParametre();
	}
}
