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
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.MessageController;
import fr.univlorraine.ecandidat.entities.ecandidat.Message;
import fr.univlorraine.ecandidat.entities.ecandidat.Message_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion de la message par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolMessageView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolMessageView extends VerticalLayout implements View, EntityPushListener<Message>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 8432471097989849796L;

	public static final String NAME = "scolMessageView";

	public static final String[] FIELDS_ORDER = {Message_.codMsg.getName(),Message_.libMsg.getName(),Message_.tesMsg.getName(),Message_.i18nValMessage.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient MessageController messageController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient EntityPusher<Message> messageEntityPusher;

	/* Composants */
	private TableFormating messageTable = new TableFormating();

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
		Label titleParam = new Label(applicationContext.getMessage("message.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);


		OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (messageTable.getValue() instanceof Message) {
				messageController.editMessage((Message) messageTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);


		/* Table des messages */
		BeanItemContainer<Message> container = new BeanItemContainer<Message>(Message.class, cacheController.getMessages());
		messageTable.setContainerDataSource(container);		
		messageTable.setSizeFull();
		messageTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			messageTable.setColumnHeader(fieldName, applicationContext.getMessage("message.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		messageTable.addGeneratedColumn(Message_.i18nValMessage.getName(), new ColumnGenerator() {
			/*** serialVersionUID*/
			private static final long serialVersionUID = -8469925144843229389L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final Message message = (Message) itemId;
				return i18nController.getI18nTraductionLibelle(message.getI18nValMessage());
			}
		});
		messageTable.setSortContainerPropertyId(Message_.codMsg.getName());
		messageTable.addBooleanColumn(Message_.tesMsg.getName(), true);
		messageTable.setColumnCollapsingAllowed(true);
		messageTable.setColumnReorderingAllowed(true);
		messageTable.setSelectable(true);
		messageTable.setImmediate(true);
		messageTable.addItemSetChangeListener(e -> messageTable.sanitizeSelection());
		messageTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de message sont actifs seulement si une message est sélectionnée. */
			boolean messageIsSelected = messageTable.getValue() instanceof Message;
			btnEdit.setEnabled(messageIsSelected);
		});
		messageTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				messageTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(messageTable);
		setExpandRatio(messageTable, 1);
		
		/* Inscrit la vue aux mises à jour de message */
		messageEntityPusher.registerEntityPushListener(this);
	}
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de message */
		messageEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Message entity) {
		messageTable.removeItem(entity);
		messageTable.addItem(entity);
		messageTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Message entity) {
		messageTable.removeItem(entity);
		messageTable.addItem(entity);
		messageTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Message entity) {
		messageTable.removeItem(entity);
	}
}
