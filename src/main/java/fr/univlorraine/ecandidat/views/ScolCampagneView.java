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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CampagneController;
import fr.univlorraine.ecandidat.controllers.SiScolController;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des campagnes par la scolarité
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = ScolCampagneView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolCampagneView extends VerticalLayout implements View, EntityPushListener<Campagne> {

	public static final String NAME = "scolCampagneView";

	public static final String[] FIELDS_ORDER = { Campagne_.codCamp.getName(),
		Campagne_.libCamp.getName(),
		Campagne_.typSiScol.getName(),
		Campagne_.datDebCamp.getName(),
		Campagne_.datFinCamp.getName(),
		Campagne_.datFinCandidatCamp.getName(),
		Campagne_.tesCamp.getName(),
		Campagne_.datActivatPrevCamp.getName(),
		Campagne_.datActivatEffecCamp.getName(),
		Campagne_.datArchivCamp.getName(),
		"datDestructPrevCamp",
		Campagne_.datDestructEffecCamp.getName() };

	@Value("${hideSiScol:false}")
	private transient Boolean hideSiScol;

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient SiScolController siScolController;
	@Resource
	private transient EntityPusher<Campagne> campagneEntityPusher;
	@Resource
	private transient DateTimeFormatter formatterDateTime;
	/* Composants */
	private final TableFormating campagneTable = new TableFormating();

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		/* Suppression du type de siscol si on cache la colonne */
		String[] FIELDS_ORDER_USE;
		if (hideSiScol) {
			FIELDS_ORDER_USE = ArrayUtils.removeElement(FIELDS_ORDER, Campagne_.typSiScol.getName());
		} else {
			FIELDS_ORDER_USE = FIELDS_ORDER;
		}

		/* Titre */
		final Label titleParam = new Label(
			applicationContext.getMessage("campagne.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);

		/* Boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		final OneClickButton btnNew = new OneClickButton(
			applicationContext.getMessage("campagne.btnNouveau", null, UI.getCurrent().getLocale()),
			FontAwesome.PLUS);
		btnNew.setEnabled(true);
		btnNew.addClickListener(e -> {
			campagneController.editNewCampagne();
		});
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);

		final OneClickButton btnEdit = new OneClickButton(
			applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()),
			FontAwesome.PENCIL);
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (campagneTable.getValue() instanceof Campagne) {
				campagneController.editCampagne((Campagne) campagneTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);

		final OneClickButton btnDelete = new OneClickButton(
			applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()),
			FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (campagneTable.getValue() instanceof Campagne) {
				campagneController.deleteCampagne((Campagne) campagneTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);

		/* Table des campagnes */
		final BeanItemContainer<Campagne> container = new BeanItemContainer<>(Campagne.class,
			campagneController.getCampagnes());
		campagneTable.setContainerDataSource(container);
		campagneTable.addGeneratedColumn("datDestructPrevCamp", new ColumnGenerator() {
			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final LocalDateTime date = campagneController.getDateDestructionDossier((Campagne) itemId);
				if (date != null) {
					return formatterDateTime.format(date);
				}
				return null;
			}
		});
		campagneTable.addGeneratedColumn(Campagne_.typSiScol.getName(), new ColumnGenerator() {
			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				return siScolController.getLibSiScolByCod(((Campagne) itemId).getTypSiScol());
			}
		});

		campagneTable.addBooleanColumn(Campagne_.tesCamp.getName());
		campagneTable.setSizeFull();
		campagneTable.setVisibleColumns((Object[]) FIELDS_ORDER_USE);
		for (final String fieldName : FIELDS_ORDER_USE) {
			campagneTable.setColumnHeader(fieldName,
				applicationContext.getMessage("campagne.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		campagneTable.setSortContainerPropertyId(Campagne_.codCamp.getName());
		campagneTable.setSortAscending(false);
		campagneTable.setColumnCollapsingAllowed(true);
		campagneTable.setColumnReorderingAllowed(true);
		campagneTable.setSelectable(true);
		campagneTable.setImmediate(true);
		campagneTable.addItemSetChangeListener(e -> campagneTable.sanitizeSelection());
		campagneTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de campagne sont actifs seulement si
			 * une campagne est sélectionnée. */
			final boolean campagneIsSelected = campagneTable.getValue() instanceof Campagne;
			btnEdit.setEnabled(campagneIsSelected);
			btnDelete.setEnabled(campagneIsSelected);
		});
		campagneTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				campagneTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		addComponent(campagneTable);
		setExpandRatio(campagneTable, 1);

		/* Inscrit la vue aux mises à jour de campagne */
		campagneEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de campagne */
		campagneEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(final Campagne entity) {
		campagneTable.removeItem(entity);
		campagneTable.addItem(entity);
		campagneTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(final Campagne entity) {
		campagneTable.removeItem(entity);
		campagneTable.addItem(entity);
		campagneTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(final Campagne entity) {
		campagneTable.removeItem(entity);
	}
}
