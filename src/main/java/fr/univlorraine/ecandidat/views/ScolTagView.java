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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TagController;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des alertes SVA par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolTagView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolTagView extends VerticalLayout implements View, EntityPushListener<Tag>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -129634359128603915L;

	public static final String NAME = "scolTagView";

	public static final String[] FIELDS_ORDER = {Tag_.libTag.getName(),Tag_.colorTag.getName(),Tag_.tesTag.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TagController tagController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient EntityPusher<Tag> tagEntityPusher;

	/* Composants */
	private TableFormating tagTable = new TableFormating();

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
		Label titleParam = new Label(applicationContext.getMessage("tag.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);


		OneClickButton btnNew = new OneClickButton(applicationContext.getMessage("tag.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			tagController.editNewTag();
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);


		OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (tagTable.getValue() instanceof Tag) {
				tagController.editTag((Tag) tagTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);
		
		OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (tagTable.getValue() instanceof Tag) {
				tagController.deleteTag((Tag) tagTable.getValue());
			}			
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);


		/* Table des tags */
		BeanItemContainer<Tag> container = new BeanItemContainer<Tag>(Tag.class, cacheController.getTags());
		tagTable.setContainerDataSource(container);		
		tagTable.addBooleanColumn(Tag_.tesTag.getName());
		tagTable.setSizeFull();
		tagTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			tagTable.setColumnHeader(fieldName, applicationContext.getMessage("tag.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		tagTable.addGeneratedColumn(Tag_.colorTag.getName(), new ColumnGenerator() {
			
			/**serialVersionUID**/
			private static final long serialVersionUID = -2562681984380111747L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				Tag tag = (Tag) itemId;
				HorizontalLayout hlColor = new HorizontalLayout();
				hlColor.setSpacing(true);
				Label labelColor = new Label("<div style='border:1px solid;width:20px;height:20px;background:"+tag.getColorTag()+";'></div>", ContentMode.HTML);
				Label labelTxt = new Label(tag.getColorTag());
				hlColor.addComponent(labelColor);
				hlColor.setComponentAlignment(labelColor, Alignment.MIDDLE_LEFT);
				hlColor.addComponent(labelTxt);
				hlColor.setComponentAlignment(labelTxt, Alignment.MIDDLE_LEFT);
				return hlColor;
			}
		});
		tagTable.setSortContainerPropertyId(Tag_.libTag.getName());
		tagTable.setColumnCollapsingAllowed(true);
		tagTable.setColumnReorderingAllowed(true);
		tagTable.setSelectable(true);
		tagTable.setImmediate(true);
		tagTable.addItemSetChangeListener(e -> tagTable.sanitizeSelection());
		tagTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de tag sont actifs seulement si une tag est sélectionnée. */
			boolean tagIsSelectedEdit = tagTable.getValue() instanceof Tag;
			
			btnEdit.setEnabled(tagIsSelectedEdit);
			btnDelete.setEnabled(tagIsSelectedEdit);
		});
		tagTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				tagTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(tagTable);
		setExpandRatio(tagTable, 1);
		
		/* Inscrit la vue aux mises à jour de alerteSva */
		tagEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de alerteSva */
		tagEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Tag entity) {
		tagTable.removeItem(entity);
		tagTable.addItem(entity);
		tagTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Tag entity) {
		tagTable.removeItem(entity);
		tagTable.addItem(entity);
		tagTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Tag entity) {
		tagTable.removeItem(entity);
	}
}
