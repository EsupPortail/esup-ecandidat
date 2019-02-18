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
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.IconLabel;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Page de gestion des parametres
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class ParametreViewTemplate extends VerticalLayout {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient ParametreController parametreController;

	/* Composants */
	private OneClickButton btnEditParam = new OneClickButton(FontAwesome.PENCIL);
	protected CheckBox checkShowScolParam = new CheckBox();
	protected BeanItemContainer<Parametre> container = new BeanItemContainer<>(Parametre.class);
	protected TableFormating parametreTable = new TableFormating(null, container);

	/**
	 * Initialise la vue
	 *
	 * @param fieldsOrder
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);

		/* Titre */
		Label titleParam = new Label(applicationContext.getMessage("parametre.title", null, UI.getCurrent().getLocale()));
		titleParam.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(titleParam);

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		btnEditParam.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEditParam.setEnabled(false);
		btnEditParam.addClickListener(e -> {
			if (parametreTable.getValue() instanceof Parametre) {
				parametreController.editParametre((Parametre) parametreTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEditParam);
		buttonsLayout.setComponentAlignment(btnEditParam, Alignment.MIDDLE_LEFT);

		checkShowScolParam.setCaption(applicationContext.getMessage("parametre.show.scol", null, UI.getCurrent().getLocale()));
		checkShowScolParam.setVisible(false);
		buttonsLayout.addComponent(checkShowScolParam);
		buttonsLayout.setComponentAlignment(checkShowScolParam, Alignment.MIDDLE_LEFT);

		/* Table des parametres */
		parametreTable.setSizeFull();
		String[] fieldsOrder = getFieldsOrder();
		parametreTable.setVisibleColumns((Object[]) fieldsOrder);
		for (String fieldName : fieldsOrder) {
			parametreTable.setColumnHeader(fieldName, applicationContext.getMessage("parametre.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		parametreTable.addGeneratedColumn(Parametre_.libParam.getName(), new ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final Parametre parametre = (Parametre) itemId;
				String lib = parametre.getLibParam();
				if (lib.length() > 100) {
					lib = lib.substring(0, 100) + "....";
				}
				Label label = new Label(lib);
				label.setDescription(parametre.getLibParam());
				return label;
			}
		});
		parametreTable.addGeneratedColumn(Parametre_.valParam.getName(), new ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final Parametre parametre = (Parametre) itemId;
				if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_BOOLEAN)) {
					String val = parametre.getValParam();
					Boolean value = (val != null && val.equals(ConstanteUtils.TYP_BOOLEAN_YES)) ? true : (val != null && val.equals(ConstanteUtils.TYP_BOOLEAN_NO)) ? false : null;
					return new IconLabel(value, true);
				} else {
					String val = parametre.getValParam();
					if (parametre.getRegexParam() != null) {
						val = applicationContext.getMessage(parametre.getRegexParam().split(";")[0] + "." + val, null, UI.getCurrent().getLocale());
					}
					Label label = new Label(val);
					label.setSizeUndefined();
					HorizontalLayout hlLabel = new HorizontalLayout();
					hlLabel.setSizeFull();
					hlLabel.addComponent(label);
					hlLabel.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
					return hlLabel;
				}
			}
		});
		parametreTable.setSortContainerPropertyId(Parametre_.codParam.getName());
		parametreTable.setColumnCollapsingAllowed(true);
		parametreTable.setColumnReorderingAllowed(true);
		parametreTable.setSelectable(true);
		parametreTable.setImmediate(true);
		parametreTable.addItemSetChangeListener(e -> parametreTable.sanitizeSelection());
		parametreTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de parametre sont actifs seulement si un parametre est sélectionné. */
			boolean paramIsSelected = parametreTable.getValue() instanceof Parametre;
			btnEditParam.setEnabled(paramIsSelected);
		});
		parametreTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				parametreTable.select(e.getItemId());
				btnEditParam.click();
			}
		});
		addComponent(parametreTable);
		setExpandRatio(parametreTable, 1);
	}

	/**
	 * @return
	 */
	public String[] getFieldsOrder() {
		return new String[] {};
	}

}
