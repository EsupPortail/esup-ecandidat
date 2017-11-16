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
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.NomenclatureTypeController;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des type de Status par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolTypeStatutView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolTypeStatutView extends VerticalLayout implements View, EntityPushListener<TypeStatut>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 8354904491798693098L;

	public static final String NAME = "scolTypeStatutView";

	public static final String[] FIELDS_ORDER = {TypeStatut_.codTypStatut.getName(),TypeStatut_.libTypStatut.getName(),TypeStatut_.temCommVisible.getName(),TypeStatut_.i18nLibTypStatut.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient NomenclatureTypeController nomenclatureTypeController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient EntityPusher<TypeStatut> typeStatutEntityPusher;

	/* Composants */
	private TableFormating typeStatutTable = new TableFormating();

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
		Label titleParam = new Label(applicationContext.getMessage("typeStatut.title", null, UI.getCurrent().getLocale()));
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
			if (typeStatutTable.getValue() instanceof TypeStatut) {
				nomenclatureTypeController.editTypeStatut((TypeStatut) typeStatutTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);


		/* Table des typeStatuts */		
		typeStatutTable.setContainerDataSource(new BeanItemContainer<TypeStatut>(TypeStatut.class, nomenclatureTypeController.getTypeStatuts()));
		typeStatutTable.addGeneratedColumn(TypeStatut_.i18nLibTypStatut.getName(), new ColumnGenerator() {
			
			/*** serialVersionUID*/
			private static final long serialVersionUID = 2101119091378513475L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final TypeStatut type = (TypeStatut) itemId;
				return i18nController.getI18nTraductionLibelle(type.getI18nLibTypStatut());
			}
		});
		typeStatutTable.setSizeFull();
		typeStatutTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			typeStatutTable.setColumnHeader(fieldName, applicationContext.getMessage("typeStatut.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		typeStatutTable.setSortContainerPropertyId(TypeStatut_.codTypStatut.getName());
		typeStatutTable.setColumnCollapsingAllowed(true);
		typeStatutTable.setColumnReorderingAllowed(true);
		typeStatutTable.setSelectable(true);
		typeStatutTable.setImmediate(true);
		typeStatutTable.addBooleanColumn(TypeStatut_.temCommVisible.getName());
		typeStatutTable.setColumnWidth(TypeStatut_.temCommVisible, 250);
		typeStatutTable.addItemSetChangeListener(e -> typeStatutTable.sanitizeSelection());
		typeStatutTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de typeStatut sont actifs seulement si une typeStatut est sélectionnée. */
			boolean typeStatutIsSelected = typeStatutTable.getValue() instanceof TypeStatut;
			btnEdit.setEnabled(typeStatutIsSelected);
		});
		typeStatutTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				typeStatutTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(typeStatutTable);
		setExpandRatio(typeStatutTable, 1);
		
		/* Inscrit la vue aux mises à jour de typeStatut */
		typeStatutEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de typeStatut */
		typeStatutEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(TypeStatut entity) {
		typeStatutTable.removeItem(entity);
		typeStatutTable.addItem(entity);
		typeStatutTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(TypeStatut entity) {
		typeStatutTable.removeItem(entity);
		typeStatutTable.addItem(entity);
		typeStatutTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(TypeStatut entity) {
		typeStatutTable.removeItem(entity);
	}
}
