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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des type de decisions par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolTypeDecisionView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolTypeDecisionView extends VerticalLayout implements View, EntityPushListener<TypeDecision>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -2638419918276829820L;

	public static final String NAME = "scolTypeDecisionView";

	public static final String[] FIELDS_ORDER = {TypeDecision_.codTypDec.getName(), TypeDecision_.libTypDec.getName(),TypeDecision_.typeAvis.getName()+"."+TypeAvis_.libelleTypAvis.getName(),TypeDecision_.mail.getName()+"."+Mail_.libMail.getName()
		,TypeDecision_.tesTypDec.getName(),TypeDecision_.temDeverseOpiTypDec.getName(),TypeDecision_.temDefinitifTypDec.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient EntityPusher<TypeDecision> typeDecisionEntityPusher;

	/* Composants */
	private TableFormating typeDecisionTable = new TableFormating();

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
		Label titleParam = new Label(applicationContext.getMessage("typeDec.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);


		OneClickButton btnNew = new OneClickButton(applicationContext.getMessage("typeDec.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			typeDecisionController.editNewTypeDecision();
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);


		OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (typeDecisionTable.getValue() instanceof TypeDecision) {
				typeDecisionController.editTypeDecision((TypeDecision) typeDecisionTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);
		
		OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (typeDecisionTable.getValue() instanceof TypeDecision) {
				typeDecisionController.deleteTypeDecision((TypeDecision) typeDecisionTable.getValue());
			}			
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);


		/* Table des typeDecisions */
		BeanItemContainer<TypeDecision> container = new BeanItemContainer<TypeDecision>(TypeDecision.class, typeDecisionController.getTypeDecisions());
		container.addNestedContainerProperty(TypeDecision_.typeAvis.getName()+"."+TypeAvis_.libelleTypAvis.getName());
		container.addNestedContainerProperty(TypeDecision_.mail.getName()+"."+Mail_.libMail.getName());
		typeDecisionTable.setContainerDataSource(container);		
		typeDecisionTable.addBooleanColumn(TypeDecision_.tesTypDec.getName());
		typeDecisionTable.addBooleanColumn(TypeDecision_.temDeverseOpiTypDec.getName());
		typeDecisionTable.addBooleanColumn(TypeDecision_.temDefinitifTypDec.getName());
		typeDecisionTable.setSizeFull();
		typeDecisionTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			typeDecisionTable.setColumnHeader(fieldName, applicationContext.getMessage("typeDec.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		typeDecisionTable.setSortContainerPropertyId(TypeDecision_.codTypDec.getName());
		typeDecisionTable.setColumnCollapsingAllowed(true);
		typeDecisionTable.setColumnReorderingAllowed(true);
		typeDecisionTable.setSelectable(true);
		typeDecisionTable.setImmediate(true);
		typeDecisionTable.addItemSetChangeListener(e -> typeDecisionTable.sanitizeSelection());
		typeDecisionTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de typeDecision sont actifs seulement si une typeDecision est sélectionnée. */
			boolean typeDecisionIsSelectedEdit = typeDecisionTable.getValue() instanceof TypeDecision;
			boolean typeDecisionIsSelectedDel = typeDecisionTable.getValue() instanceof TypeDecision && !((TypeDecision)typeDecisionTable.getValue()).getTemModelTypDec();
			
			btnEdit.setEnabled(typeDecisionIsSelectedEdit);
			btnDelete.setEnabled(typeDecisionIsSelectedDel);
		});
		typeDecisionTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				typeDecisionTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(typeDecisionTable);
		setExpandRatio(typeDecisionTable, 1);
		
		/* Inscrit la vue aux mises à jour de typeDecision */
		typeDecisionEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de typeDecision */
		typeDecisionEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(TypeDecision entity) {
		typeDecisionTable.removeItem(entity);
		typeDecisionTable.addItem(entity);
		typeDecisionTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(TypeDecision entity) {
		typeDecisionTable.removeItem(entity);
		typeDecisionTable.addItem(entity);
		typeDecisionTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(TypeDecision entity) {
		typeDecisionTable.removeItem(entity);
	}
}
