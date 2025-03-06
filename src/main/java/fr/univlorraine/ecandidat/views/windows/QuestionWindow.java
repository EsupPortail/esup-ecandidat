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
package fr.univlorraine.ecandidat.views.windows;

import java.util.Arrays;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.QuestionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Question;
import fr.univlorraine.ecandidat.entities.ecandidat.Question_;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleBeanPresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredCheckBox;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxPresentation;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nField;

/**
 * Fenêtre d'édition d'une Question
 *
 * @author Matthieu Manginot
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class QuestionWindow extends Window {

	public static final String[] FIELDS_ORDER = { Question_.codQuestion.getName(), Question_.typQuestion.getName(),
			Question_.libQuestion.getName(), Question_.tesQuestion.getName(), Question_.temCommunQuestion.getName(),
			Question_.temUniciteQuestion.getName(), Question_.temConditionnelQuestion.getName(),
			Question_.i18nLibQuestion.getName() };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient QuestionController questionController;

	/* Composants */
	private CustomBeanFieldGroup<Question> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition d'une Question
	 *
	 * @param Question la Question à éditer
	 */
	public QuestionWindow(final Question question) {
		/* Style */
		setModal(true);
		setWidth(550, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("question.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(Question.class);
		fieldGroup.setItemDataSource(question);
		final FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (final String fieldName : FIELDS_ORDER) {
			final String caption = applicationContext.getMessage("question.table." + fieldName, null,
					UI.getCurrent().getLocale());
			Field<?> field = null;
			if (fieldName.equals(Question_.typQuestion.getName())) {
				field = fieldGroup.buildAndBind(caption, fieldName, ComboBoxPresentation.class);
				final ComboBoxPresentation cbPres = (ComboBoxPresentation) field;
				cbPres.setListe(Arrays.asList(
						new SimpleBeanPresentation(Question.TYP_BOOLEAN,
								applicationContext.getMessage("question.type." + Question.TYP_BOOLEAN, null,
										UI.getCurrent().getLocale())),
						new SimpleBeanPresentation(Question.TYP_STRING, applicationContext.getMessage(
								"question.type." + Question.TYP_STRING, null, UI.getCurrent().getLocale()))));
				cbPres.setCodeValue(Question.TYP_BOOLEAN);
			} else {
				field = fieldGroup.buildAndBind(caption, fieldName);
			}
			if (field != null) {
				field.setWidth(100, Unit.PERCENTAGE);
				formLayout.addComponent(field);
			}
		}

		/* Description pour ceux qui ne comprennent pas les témoins */
		addFieldDescriptionCb(Question_.temCommunQuestion.getName());
		addFieldDescriptionCb(Question_.temUniciteQuestion.getName());

		/* Centre la fenetre avec le i18n */
		((I18nField) fieldGroup.getField(Question_.i18nLibQuestion.getName())).addCenterListener(e -> {
			if (e) {
				center();
			}
		});
		layout.addComponent(formLayout);

		/* Ajoute les boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()),
				FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()),
				FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {
				/* Si le code de profil existe dejà --> erreur */
				if (!questionController.isCodQuestionUnique(
						(String) fieldGroup.getField(Question_.codQuestion.getName()).getValue(),
						question.getIdQuestion())) {
					Notification.show(applicationContext.getMessage("window.error.cod.nonuniq", null,
							UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la pieceJustif saisie */
				questionController.saveQuestion(question);
				/* Ferme la fenêtre */
				close();
			} catch (final CommitException ce) {
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * @param name
	 */
	private void addFieldDescriptionCb(final String property) {
		final RequiredCheckBox rcb = (RequiredCheckBox) fieldGroup.getField(property);
		rcb.setDescription(
				applicationContext.getMessage("question.info." + property, null, UI.getCurrent().getLocale()));
		rcb.setIcon(FontAwesome.INFO_CIRCLE);
	}
}
