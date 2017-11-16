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
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des type de status de pièce par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolTypeStatutPieceView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolTypeStatutPieceView extends VerticalLayout implements View, EntityPushListener<TypeStatutPiece>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 6889285891430169967L;

	public static final String NAME = "scolTypeStatutPieceView";

	public static final String[] FIELDS_ORDER = {TypeStatutPiece_.codTypStatutPiece.getName(),TypeStatutPiece_.libTypStatutPiece.getName(),TypeStatutPiece_.i18nLibTypStatutPiece.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient NomenclatureTypeController typeStatutController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient EntityPusher<TypeStatutPiece> typeStatutPieceEntityPusher;

	/* Composants */
	private TableFormating typeStatutPieceTable = new TableFormating();

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
		Label titleParam = new Label(applicationContext.getMessage("typeStatutPiece.title", null, UI.getCurrent().getLocale()));
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
			if (typeStatutPieceTable.getValue() instanceof TypeStatutPiece) {
				typeStatutController.editTypeStatutPiece((TypeStatutPiece) typeStatutPieceTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);


		/* Table des typeStatutPieces */
		BeanItemContainer<TypeStatutPiece> container = new BeanItemContainer<TypeStatutPiece>(TypeStatutPiece.class, typeStatutController.getTypeStatutPieces());		
		typeStatutPieceTable.setContainerDataSource(container);
		typeStatutPieceTable.addGeneratedColumn(TypeStatutPiece_.i18nLibTypStatutPiece.getName(), new ColumnGenerator() {
			
			/*** serialVersionUID*/
			private static final long serialVersionUID = 2101119091378513475L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final TypeStatutPiece type = (TypeStatutPiece) itemId;
				return i18nController.getI18nTraductionLibelle(type.getI18nLibTypStatutPiece());
			}
		});
		typeStatutPieceTable.setSizeFull();
		typeStatutPieceTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			typeStatutPieceTable.setColumnHeader(fieldName, applicationContext.getMessage("typeStatutPiece.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		typeStatutPieceTable.setSortContainerPropertyId(TypeStatutPiece_.codTypStatutPiece.getName());
		typeStatutPieceTable.setColumnCollapsingAllowed(true);
		typeStatutPieceTable.setColumnReorderingAllowed(true);
		typeStatutPieceTable.setSelectable(true);
		typeStatutPieceTable.setImmediate(true);
		typeStatutPieceTable.addItemSetChangeListener(e -> typeStatutPieceTable.sanitizeSelection());
		typeStatutPieceTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de typeStatutPiece sont actifs seulement si une typeStatutPiece est sélectionnée. */
			boolean typeStatutPieceIsSelected = typeStatutPieceTable.getValue() instanceof TypeStatutPiece;
			btnEdit.setEnabled(typeStatutPieceIsSelected);
		});
		typeStatutPieceTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				typeStatutPieceTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(typeStatutPieceTable);
		setExpandRatio(typeStatutPieceTable, 1);
		
		/* Inscrit la vue aux mises à jour de typeStatutPiece */
		typeStatutPieceEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de typeStatutPiece */
		typeStatutPieceEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(TypeStatutPiece entity) {
		typeStatutPieceTable.removeItem(entity);
		typeStatutPieceTable.addItem(entity);
		typeStatutPieceTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(TypeStatutPiece entity) {
		typeStatutPieceTable.removeItem(entity);
		typeStatutPieceTable.addItem(entity);
		typeStatutPieceTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(TypeStatutPiece entity) {
		typeStatutPieceTable.removeItem(entity);
	}
}
