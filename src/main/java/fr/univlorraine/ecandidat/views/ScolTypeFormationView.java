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
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TypeFormationController;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeFormation;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeFormation_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des alertes SVA par la scolarité
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = ScolTypeFormationView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolTypeFormationView extends VerticalLayout implements View, EntityPushListener<TypeFormation> {

	public static final String NAME = "scolTypeFormationView";

	public static final String[] FIELDS_ORDER = { TypeFormation_.codTypeForm.getName(), TypeFormation_.libTypeForm.getName(), TypeFormation_.tesTypeForm.getName() };

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TypeFormationController typeFormationController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient EntityPusher<TypeFormation> typeFormationEntityPusher;

	/* Composants */
	private final TableFormating typeFormationTable = new TableFormating();

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
		final Label titleParam = new Label(applicationContext.getMessage("typeForm.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);

		/* Boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		final OneClickButton btnNew = new OneClickButton(applicationContext.getMessage("typeForm.btnNouveau", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			typeFormationController.editNewTypeFormation();
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);

		final OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (typeFormationTable.getValue() instanceof TypeFormation) {
				typeFormationController.editTypeFormation((TypeFormation) typeFormationTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);

		final OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (typeFormationTable.getValue() instanceof TypeFormation) {
				typeFormationController.deleteTypeFormation((TypeFormation) typeFormationTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);

		/* Table des typeFormations */
		final BeanItemContainer<TypeFormation> container = new BeanItemContainer<>(TypeFormation.class, typeFormationController.getTypeFormation());
		typeFormationTable.setContainerDataSource(container);
		typeFormationTable.addBooleanColumn(TypeFormation_.tesTypeForm.getName());
		typeFormationTable.setSizeFull();
		typeFormationTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (final String fieldName : FIELDS_ORDER) {
			typeFormationTable.setColumnHeader(fieldName, applicationContext.getMessage("typeForm.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		typeFormationTable.setSortContainerPropertyId(TypeFormation_.codTypeForm.getName());
		typeFormationTable.setColumnCollapsingAllowed(true);
		typeFormationTable.setColumnReorderingAllowed(true);
		typeFormationTable.setSelectable(true);
		typeFormationTable.setImmediate(true);
		typeFormationTable.addItemSetChangeListener(e -> typeFormationTable.sanitizeSelection());
		typeFormationTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de typeFormation sont actifs seulement si une typeFormation est sélectionnée. */
			final boolean typeFormationIsSelectedEdit = typeFormationTable.getValue() instanceof TypeFormation;

			btnEdit.setEnabled(typeFormationIsSelectedEdit);
			btnDelete.setEnabled(typeFormationIsSelectedEdit);
		});
		typeFormationTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				typeFormationTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(typeFormationTable);
		setExpandRatio(typeFormationTable, 1);

		/* Inscrit la vue aux mises à jour de alerteSva */
		typeFormationEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {

	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de alerteSva */
		typeFormationEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(final TypeFormation entity) {
		typeFormationTable.removeItem(entity);
		typeFormationTable.addItem(entity);
		typeFormationTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(final TypeFormation entity) {
		typeFormationTable.removeItem(entity);
		typeFormationTable.addItem(entity);
		typeFormationTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(final TypeFormation entity) {
		typeFormationTable.removeItem(entity);
	}
}
