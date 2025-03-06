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

import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.DemoController;
import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.controllers.FormulaireController;
import fr.univlorraine.ecandidat.controllers.NomenclatureController;
import fr.univlorraine.ecandidat.controllers.SiScolController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.windows.AdminInesWindow;
import fr.univlorraine.ecandidat.views.windows.AdminWsPjWindow;

/**
 * Page de gestion des versions
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = AdminVersionView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_ADMIN)
public class AdminVersionView extends VerticalLayout implements View {

	public static final String NAME = "adminVersionView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient NomenclatureController nomenclatureController;
	@Resource
	private transient SiScolController siScolController;
	@Resource
	private transient FormulaireController formulaireController;
	@Resource
	private transient FileController fileController;
	@Resource
	private transient DemoController demoController;

	public static final String[] FIELDS_ORDER = { SimpleTablePresentation.CHAMPS_TITLE,
		SimpleTablePresentation.CHAMPS_VALUE,
		SimpleTablePresentation.CHAMPS_DATE,
		SimpleTablePresentation.CHAMPS_ACTION };

	/* Composants */
	private final BeanItemContainer<SimpleTablePresentation> container = new BeanItemContainer<>(SimpleTablePresentation.class);
	private final TableFormating versionTable = new TableFormating(null, container);

	/** Initialise la vue */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		/* Titre */
		final Label titleNom = new Label(applicationContext.getMessage("adminVersionView.title", null, UI.getCurrent().getLocale()));
		titleNom.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleNom);

		versionTable.addGeneratedColumn(SimpleTablePresentation.CHAMPS_ACTION, new ColumnGenerator() {
			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final SimpleTablePresentation bean = (SimpleTablePresentation) itemId;
				final OneClickButton btnCheck = new OneClickButton(applicationContext.getMessage("btnCheck", null, UI.getCurrent().getLocale()), FontAwesome.ROTATE_RIGHT);
				switch (bean.getCode()) {
				case NomenclatureUtils.VERSION_DEMAT:
					btnCheck.addClickListener(e -> {
						fileController.testDemat(true);
					});
					return btnCheck;
				case NomenclatureUtils.VERSION_SI_SCOL_COD:
					btnCheck.addClickListener(e -> {
						siScolController.testSiScolConnnexion();
					});
					return btnCheck;
				case NomenclatureUtils.VERSION_WS:
					btnCheck.addClickListener(e -> {
						siScolController.testWSSiScolConnnexion();
					});
					return btnCheck;
				case NomenclatureUtils.VERSION_WS_PJ:
					btnCheck.addClickListener(e -> {
						if (demoController.getDemoMode()) {
							return;
						}
						final AdminWsPjWindow window = new AdminWsPjWindow();
						UI.getCurrent().addWindow(window);
					});
					return btnCheck;
				case NomenclatureUtils.VERSION_LS:
					btnCheck.addClickListener(e -> {
						formulaireController.testConnexionLS();
					});
					return btnCheck;
				case NomenclatureUtils.VERSION_INES:
					btnCheck.addClickListener(e -> {
						final AdminInesWindow window = new AdminInesWindow();
						UI.getCurrent().addWindow(window);
					});
					return btnCheck;
				default:
					return null;
				}
			}
		});
		versionTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (final String fieldName : FIELDS_ORDER) {
			versionTable.setColumnHeader(fieldName, applicationContext.getMessage("version." + fieldName, null, UI.getCurrent().getLocale()));
		}
		versionTable.setSortContainerPropertyId(SimpleTablePresentation.CHAMPS_ORDER);
		versionTable.setColumnCollapsingAllowed(false);
		versionTable.setColumnReorderingAllowed(false);
		versionTable.setSelectable(false);
		versionTable.setImmediate(true);

		versionTable.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 300);
		versionTable.setCellStyleGenerator((components, itemId, columnId) -> {
			if (columnId != null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)) {
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		addComponent(versionTable);
		setExpandRatio(versionTable, 1);
	}

	/** @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent) */
	@Override
	public void enter(final ViewChangeEvent event) {
		final List<SimpleTablePresentation> liste = nomenclatureController.getVersions();
		container.removeAllItems();
		container.addAll(liste);
		versionTable.setPageLength(liste.size());
	}

	/** @see com.vaadin.ui.AbstractComponent#detach() */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de version */
		super.detach();
	}
}
