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
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des type de Traitements par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolTypeTraitementView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolTypeTraitementView extends VerticalLayout implements View, EntityPushListener<TypeTraitement>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -7882171354725594421L;


	public static final String NAME = "scolTypeTraitementView";

	public static final String[] FIELDS_ORDER = {TypeTraitement_.codTypTrait.getName(),TypeTraitement_.libTypTrait.getName(),TypeTraitement_.i18nLibTypTrait.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient NomenclatureTypeController nomenclatureTypeController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient EntityPusher<TypeTraitement> typeTraitementEntityPusher;

	/* Composants */
	private TableFormating typeTraitementTable = new TableFormating();

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
		Label titleParam = new Label(applicationContext.getMessage("typeTraitement.title", null, UI.getCurrent().getLocale()));
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
			if (typeTraitementTable.getValue() instanceof TypeTraitement) {
				nomenclatureTypeController.editTypeTraitement((TypeTraitement) typeTraitementTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);


		/* Table des typeTraitements */		
		typeTraitementTable.setContainerDataSource(new BeanItemContainer<TypeTraitement>(TypeTraitement.class, nomenclatureTypeController.getTypeTraitements()));
		typeTraitementTable.addGeneratedColumn(TypeTraitement_.i18nLibTypTrait.getName(), new ColumnGenerator() {
	
			/*** serialVersionUID*/
			private static final long serialVersionUID = 2101119091378513475L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final TypeTraitement typeTraitement = (TypeTraitement) itemId;
				return i18nController.getI18nTraductionLibelle(typeTraitement.getI18nLibTypTrait());
			}
		});
		typeTraitementTable.setSizeFull();
		typeTraitementTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			typeTraitementTable.setColumnHeader(fieldName, applicationContext.getMessage("typeTraitement.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		typeTraitementTable.setSortContainerPropertyId(TypeTraitement_.codTypTrait.getName());
		typeTraitementTable.setColumnCollapsingAllowed(true);
		typeTraitementTable.setColumnReorderingAllowed(true);
		typeTraitementTable.setSelectable(true);
		typeTraitementTable.setImmediate(true);
		typeTraitementTable.addItemSetChangeListener(e -> typeTraitementTable.sanitizeSelection());
		typeTraitementTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de typeTraitement sont actifs seulement si une typeTraitement est sélectionnée. */
			boolean typeTraitementIsSelected = typeTraitementTable.getValue() instanceof TypeTraitement;
			btnEdit.setEnabled(typeTraitementIsSelected);
		});
		typeTraitementTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				typeTraitementTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(typeTraitementTable);
		setExpandRatio(typeTraitementTable, 1);
		
		/* Inscrit la vue aux mises à jour de typeTraitement */
		typeTraitementEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de typeTraitement */
		typeTraitementEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(TypeTraitement entity) {
		typeTraitementTable.removeItem(entity);
		typeTraitementTable.addItem(entity);
		typeTraitementTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(TypeTraitement entity) {
		typeTraitementTable.removeItem(entity);
		typeTraitementTable.addItem(entity);
		typeTraitementTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(TypeTraitement entity) {
		typeTraitementTable.removeItem(entity);
	}
}
