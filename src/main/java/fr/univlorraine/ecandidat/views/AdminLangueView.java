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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.LangueController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des langues
 * @author Kevin Hergalant
 *
 */
@SpringView(name = AdminLangueView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_ADMIN)
public class AdminLangueView extends VerticalLayout implements View, EntityPushListener<Langue>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 6118429225941087757L;

	public static final String NAME = "adminLangueView";

	public static final String[] LANGUE_FIELDS_ORDER = {"flagLangue",Langue_.codLangue.getName(),Langue_.libLangue.getName(),Langue_.tesLangue.getName(),Langue_.temDefautLangue.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LangueController langueController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient EntityPusher<Langue> langueEntityPusher;

	/* Composants */
	private OneClickButton btnEdit = new OneClickButton(FontAwesome.PENCIL);
	private BeanItemContainer<Langue> container = new BeanItemContainer<Langue>(Langue.class);
	private TableFormating langueTable = new TableFormating(null, container);

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
		Label titleParam = new Label(applicationContext.getMessage("langue.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);


		btnEdit.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (langueTable.getValue() instanceof Langue) {
				langueController.editLangue((Langue) langueTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);


		/* Table des langues */
		langueTable.addBooleanColumn(Langue_.tesLangue.getName());
		langueTable.addBooleanColumn(Langue_.temDefautLangue.getName());
		langueTable.addGeneratedColumn("flagLangue", new ColumnGenerator() {
			
			/*** serialVersionUID*/
			private static final long serialVersionUID = 7461290324017459118L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final Langue langue = (Langue) itemId;
				Image flag = new Image(null, new ThemeResource("images/flags/"+langue.getCodLangue()+".png"));
				return flag;
			}
		});
		langueTable.setSizeFull();
		langueTable.setVisibleColumns((Object[]) LANGUE_FIELDS_ORDER);
		for (String fieldName : LANGUE_FIELDS_ORDER) {
			langueTable.setColumnHeader(fieldName, applicationContext.getMessage("langue.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		langueTable.setSortContainerPropertyId(Langue_.codLangue.getName());
		langueTable.setColumnCollapsingAllowed(true);
		langueTable.setColumnReorderingAllowed(true);
		langueTable.setSelectable(true);
		langueTable.setImmediate(true);
		langueTable.addItemSetChangeListener(e -> langueTable.sanitizeSelection());
		langueTable.addValueChangeListener(e -> {
			if (langueTable.getValue()==null){
				btnEdit.setEnabled(false);
			}else{
				Langue langue = (Langue) langueTable.getValue();
				if (!langue.getCodLangue().equals(NomenclatureUtils.LANGUE_FR)){
					btnEdit.setEnabled(true);
				}else{
					btnEdit.setEnabled(false);
				}
			}
			
		});
		langueTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				langueTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(langueTable);
		setExpandRatio(langueTable, 1);
		
		/* Inscrit la vue aux mises à jour de langue */
		langueEntityPusher.registerEntityPushListener(this);
	}
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		container.removeAllItems();
		container.addAll(langueController.getLangues());
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de langue */
		langueEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(Langue entity) {
		langueTable.removeItem(entity);
		langueTable.addItem(entity);
		langueTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(Langue entity) {
		langueTable.removeItem(entity);
		langueTable.addItem(entity);
		langueTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(Langue entity) {
		
	}
}
