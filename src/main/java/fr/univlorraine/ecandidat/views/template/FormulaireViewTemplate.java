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
import fr.univlorraine.ecandidat.controllers.FormulaireController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire_;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;


/** Template de la vue des formulaires, utilisé par la scol et ctrCand
 * @author Kevin Hergalant
 *
 */
public class FormulaireViewTemplate extends VerticalLayout {

	/** serialVersionUID **/
	private static final long serialVersionUID = 5599982562122210684L;

	public static final String[] FIELDS_ORDER = {Formulaire_.idFormulaireLimesurvey.getName(),Formulaire_.codFormulaire.getName(),Formulaire_.libFormulaire.getName(),Formulaire_.tesFormulaire.getName(),Formulaire_.temCommunFormulaire.getName(),Formulaire_.temConditionnelFormulaire.getName()};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormulaireController formulaireController;
	@Resource
	private transient I18nController i18nController;
	
	/* Composants */	
	protected Label titleParam = new Label();
	protected OneClickButton btnNew = new OneClickButton(FontAwesome.PLUS);
	protected OneClickButton btnEdit = new OneClickButton(FontAwesome.PENCIL);
	protected HorizontalLayout buttonsLayout = new HorizontalLayout();
	protected BeanItemContainer<Formulaire> container = new BeanItemContainer<Formulaire>(Formulaire.class);
	protected TableFormating formulaireTable = new TableFormating(null, container);

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

		btnNew.setCaption(applicationContext.getMessage("formulaire.btnNouveau", null, UI.getCurrent().getLocale()));
		btnNew.setEnabled(true);		
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);


		btnEdit.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (formulaireTable.getValue() instanceof Formulaire) {
				formulaireController.editFormulaire((Formulaire) formulaireTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);
		
		OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (formulaireTable.getValue() instanceof Formulaire) {
				formulaireController.deleteFormulaire((Formulaire) formulaireTable.getValue());
			}			
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);


		/* Table des formulaires */
		
		
		formulaireTable.addBooleanColumn(Formulaire_.tesFormulaire.getName());
		formulaireTable.addBooleanColumn(Formulaire_.temCommunFormulaire.getName());
		formulaireTable.addBooleanColumn(Formulaire_.temConditionnelFormulaire.getName());
		formulaireTable.setSizeFull();
		formulaireTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (String fieldName : FIELDS_ORDER) {
			formulaireTable.setColumnHeader(fieldName, applicationContext.getMessage("formulaire.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		formulaireTable.setSortContainerPropertyId(Formulaire_.codFormulaire.getName());
		formulaireTable.setColumnCollapsingAllowed(true);
		formulaireTable.setColumnReorderingAllowed(true);
		formulaireTable.setSelectable(true);
		formulaireTable.setImmediate(true);
		formulaireTable.addItemSetChangeListener(e -> formulaireTable.sanitizeSelection());
		formulaireTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de formulaire sont actifs seulement si une formulaire est sélectionnée. */
			boolean formulaireIsSelected = formulaireTable.getValue() instanceof Formulaire;
			btnEdit.setEnabled(formulaireIsSelected);
			btnDelete.setEnabled(formulaireIsSelected);
		});
		
		addComponent(formulaireTable);
		setExpandRatio(formulaireTable, 1);
	}
	
}
