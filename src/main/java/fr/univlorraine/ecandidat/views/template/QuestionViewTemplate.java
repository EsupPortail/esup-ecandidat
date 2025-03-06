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

import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.DefaultItemSorter;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.QuestionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Question;
import fr.univlorraine.ecandidat.entities.ecandidat.Question_;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * Template de la vue des questions, utilisé par la scol et ctrCand
 * @author Matthieu Manginot
 */
@SuppressWarnings("serial")
public class QuestionViewTemplate extends VerticalLayout {

	public static final String NAME = "scolQuestionView";

	String[] FIELDS_ORDER = { Question_.codQuestion.getName(),
		Question_.libQuestion.getName(),
		Question_.tesQuestion.getName(),
		Question_.temCommunQuestion.getName(),
		Question_.temUniciteQuestion.getName(),
		Question_.temConditionnelQuestion.getName() };

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient QuestionController questionController;
	@Resource
	private transient I18nController i18nController;

	protected Boolean isVisuQuestionCommunMode = true;
	protected Boolean isReadOnly = false;

	/* Composants */
	protected Label titleParam = new Label();
	protected OneClickButton btnNew = new OneClickButton(FontAwesome.PLUS);
	protected OneClickButton btnEdit = new OneClickButton(FontAwesome.PENCIL);
	protected HorizontalLayout buttonsLayout = new HorizontalLayout();
	protected BeanItemContainer<Question> container = new BeanItemContainer<>(Question.class);
	protected TableFormating questionTable = new TableFormating(null, container);

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

		btnNew.setCaption(applicationContext.getMessage("question.btnNouveau", null, UI.getCurrent().getLocale()));
		btnNew.setEnabled(true);
		buttonsLayout.addComponent(btnNew);
		buttonsLayout.setComponentAlignment(btnNew, Alignment.MIDDLE_LEFT);

		btnEdit.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEdit.setEnabled(false);
		btnEdit.addClickListener(e -> {
			if (questionTable.getValue() instanceof Question) {
				questionController.editQuestion((Question) questionTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEdit);
		buttonsLayout.setComponentAlignment(btnEdit, Alignment.MIDDLE_CENTER);

		final OneClickButton btnDelete = new OneClickButton(
			applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDelete.setEnabled(false);
		btnDelete.addClickListener(e -> {
			if (questionTable.getValue() instanceof Question) {
				questionController.deleteQuestion((Question) questionTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnDelete);
		buttonsLayout.setComponentAlignment(btnDelete, Alignment.MIDDLE_RIGHT);

		/* Table des Questions */
		questionTable.addBooleanColumn(Question_.tesQuestion.getName());
		questionTable.addBooleanColumn(Question_.temCommunQuestion.getName());
		questionTable.addBooleanColumn(Question_.temUniciteQuestion.getName());
		questionTable.addBooleanColumn(Question_.temConditionnelQuestion.getName());

		questionTable.setSizeFull();
		questionTable.setVisibleColumns((Object[]) FIELDS_ORDER);
		for (final String fieldName : FIELDS_ORDER) {
			questionTable.setColumnHeader(fieldName,
				applicationContext.getMessage("question.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		questionTable.addHeaderClickListener(e -> {
			container.setItemSorter(new DefaultItemSorter());
		});
		questionTable.setColumnCollapsingAllowed(true);
		questionTable.setColumnReorderingAllowed(true);
		questionTable.setSelectable(true);
		questionTable.setImmediate(true);
		questionTable.addItemSetChangeListener(e -> questionTable.sanitizeSelection());
		questionTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de Question sont actifs seulement si
			 * une Question est sélectionnée. */
			final boolean questionIsSelected = questionTable.getValue() instanceof Question;
			btnEdit.setEnabled(questionIsSelected);
			btnDelete.setEnabled(questionIsSelected);
		});
		addComponent(questionTable);
		setExpandRatio(questionTable, 1);
	}

	/**
	 * Trie le container
	 */
	protected void sortContainer() {
		questionTable.sort();
	}
}
