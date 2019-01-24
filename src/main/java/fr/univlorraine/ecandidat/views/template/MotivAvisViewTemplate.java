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
package fr.univlorraine.ecandidat.views.template;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.MotivationAvisController;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis_;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Template de la vue des motivation d'avis, utilisé par la scol et ctrCand
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class MotivAvisViewTemplate extends VerticalLayout {

	public static final String[] FIELDS_ORDER = {MotivationAvis_.codMotiv.getName(), MotivationAvis_.libMotiv.getName(), MotivationAvis_.tesMotiv.getName()};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient MotivationAvisController motivationAvisController;
	@Resource
	private transient EntityPusher<MotivationAvis> motivationAvisEntityPusher;

	/* Composants */
	protected Label titleParam = new Label();
	protected BeanItemContainer<MotivationAvis> container = new BeanItemContainer<>(MotivationAvis.class);
	protected TableFormating motivationAvisTable = new TableFormating(null, container);
	protected OneClickButton btnNew = new OneClickButton(FontAwesome.PLUS);
	protected OneClickButton btnEdit = new OneClickButton(FontAwesome.PENCIL);
	protected HorizontalLayout buttonsLayout = new HorizontalLayout();

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
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);

		/* Boutons */
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		btnNew.setCaption(applicationContext.getMessage("motivAvis.btnNouveau", null, UI.getCurrent().getLocale()));
		btnNew.setEnabled(true);
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);

		btnEdit.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (motivationAvisTable.getValue() instanceof MotivationAvis) {
				motivationAvisController.editMotivationAvis((MotivationAvis) motivationAvisTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);

		OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (motivationAvisTable.getValue() instanceof MotivationAvis) {
				motivationAvisController.deleteMotivationAvis((MotivationAvis) motivationAvisTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);

		/* Table des motivationAviss */
		motivationAvisTable.setContainerDataSource(container);
		motivationAvisTable.addBooleanColumn(MotivationAvis_.tesMotiv.getName());
		motivationAvisTable.setSizeFull();
		motivationAvisTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			motivationAvisTable.setColumnHeader(fieldName, applicationContext.getMessage("motivAvis.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		motivationAvisTable.setSortContainerPropertyId(MotivationAvis_.codMotiv.getName());
		motivationAvisTable.setColumnCollapsingAllowed(true);
		motivationAvisTable.setColumnReorderingAllowed(true);
		motivationAvisTable.setSelectable(true);
		motivationAvisTable.setImmediate(true);
		motivationAvisTable.addItemSetChangeListener(e -> motivationAvisTable.sanitizeSelection());
		motivationAvisTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de motivationAvis sont actifs seulement si une motivationAvis est sélectionnée. */
			boolean motivationAvisIsSelected = motivationAvisTable.getValue() instanceof MotivationAvis;
			btnEdit.setEnabled(motivationAvisIsSelected);
			btnDelete.setEnabled(motivationAvisIsSelected);
		});
		motivationAvisTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				motivationAvisTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(motivationAvisTable);
		setExpandRatio(motivationAvisTable, 1);
	}
}
