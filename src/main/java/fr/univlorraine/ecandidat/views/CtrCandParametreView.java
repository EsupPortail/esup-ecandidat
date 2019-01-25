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
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des parametres du centre de candidature
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CtrCandParametreView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandParametreView extends VerticalLayout implements View, EntityPushListener<CentreCandidature> {

	public static final String NAME = "ctrCandParametreView";

	public static final String[] FIELDS_ORDER = {SimpleTablePresentation.CHAMPS_TITLE, SimpleTablePresentation.CHAMPS_VALUE};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient EntityPusher<CentreCandidature> centreCandidatureEntityPusher;

	/* Le droit sur la vue */
	private SecurityCtrCandFonc securityCtrCandFonc;

	/* Composants */
	private BeanItemContainer<SimpleTablePresentation> containerReadOnly = new BeanItemContainer<>(SimpleTablePresentation.class);
	private BeanItemContainer<SimpleTablePresentation> container = new BeanItemContainer<>(SimpleTablePresentation.class);

	/* Composants */

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		/* Récupération du centre de canidature en cours */
		securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_PARAM);
		if (securityCtrCandFonc.hasNoRight()) {
			return;
		}

		/* Titre */
		Label titleParam = new Label(applicationContext.getMessage("ctrCand.parametre.title", new Object[] {securityCtrCandFonc.getCtrCand().getLibCtrCand()}, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);

		/* Descriptif */

		Label titleParamDesc = new Label(applicationContext.getMessage("ctrCand.parametre.title.desc", null, UI.getCurrent().getLocale()));
		titleParamDesc.addStyleName(StyleConstants.VIEW_SUBTITLE);
		addComponent(titleParamDesc);

		containerReadOnly = new BeanItemContainer<>(SimpleTablePresentation.class);
		TableFormating paramReadOnlyTable = new TableFormating(null, containerReadOnly);
		paramReadOnlyTable.addBooleanColumn(SimpleTablePresentation.CHAMPS_VALUE, false);
		paramReadOnlyTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		paramReadOnlyTable.setColumnCollapsingAllowed(false);
		paramReadOnlyTable.setColumnReorderingAllowed(false);
		paramReadOnlyTable.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		paramReadOnlyTable.setSelectable(false);
		paramReadOnlyTable.setImmediate(true);
		paramReadOnlyTable.setPageLength(4);
		paramReadOnlyTable.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 300);
		paramReadOnlyTable.setCellStyleGenerator((components, itemId, columnId) -> {
			if (columnId != null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)) {
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		addComponent(paramReadOnlyTable);
		paramReadOnlyTable.setWidth(100, Unit.PERCENTAGE);

		/* Parametres */

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		Label titleParamParam = new Label(applicationContext.getMessage("ctrCand.parametre.title.param", null, UI.getCurrent().getLocale()));
		titleParamParam.setSizeUndefined();
		titleParamParam.addStyleName(StyleConstants.VIEW_SUBTITLE);
		buttonsLayout.addComponent(titleParamParam);
		buttonsLayout.setComponentAlignment(titleParamParam, Alignment.MIDDLE_CENTER);

		OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.addClickListener(e -> {
			centreCandidatureController.editCentreCandidature(securityCtrCandFonc.getCtrCand(), false);
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setExpandRatio(btnEdit, 1);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_LEFT);

		TableFormating paramTable = new TableFormating(null, container);
		paramTable.addBooleanColumn(SimpleTablePresentation.CHAMPS_VALUE, false);
		paramTable.setSizeFull();
		paramTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		paramTable.setColumnCollapsingAllowed(false);
		paramTable.setColumnReorderingAllowed(false);
		paramTable.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		paramTable.setSelectable(false);
		paramTable.setImmediate(true);
		paramTable.setPageLength(11);
		paramTable.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 300);
		paramTable.setCellStyleGenerator((components, itemId, columnId) -> {
			if (columnId != null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)) {
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		addComponent(paramTable);

		miseAJourContainer(securityCtrCandFonc.getCtrCand());
		setExpandRatio(paramTable, 1);

		/* Gestion du readOnly */
		if (securityCtrCandFonc.isWrite()) {
			buttonsLayout.setVisible(true);
		} else {
			buttonsLayout.setVisible(false);
		}

		/* Inscrit la vue aux mises à jour de centreCandidature */
		centreCandidatureEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * Met a jour le container
	 *
	 * @param ctrCand
	 */
	private void miseAJourContainer(final CentreCandidature ctrCand) {
		securityCtrCandFonc.setCtrCand(ctrCand);
		containerReadOnly.removeAllItems();
		container.removeAllItems();
		if (ctrCand != null) {
			containerReadOnly.addAll(centreCandidatureController.getListPresentation(ctrCand, true));
			container.addAll(centreCandidatureController.getListPresentation(ctrCand, false));
		}
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
		/* Désinscrit la vue des mises à jour de centreCandidature */
		centreCandidatureEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(final CentreCandidature entity) {

	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(final CentreCandidature entity) {
		if (securityCtrCandFonc.getCtrCand() != null && entity.getIdCtrCand().equals(securityCtrCandFonc.getCtrCand().getIdCtrCand())) {
			miseAJourContainer(entity);
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(final CentreCandidature entity) {
		miseAJourContainer(null);
	}
}
