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

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.TextArea;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.utils.bean.presentation.QuestionPresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleBeanPresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxPresentation;
import fr.univlorraine.ecandidat.views.windows.InputWindow.BtnOkListener;
import jakarta.annotation.Resource;
import lombok.Getter;

/**
 * Fenêtre de saisie d'un texte
 * @author Matthieu Manginot
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CandidatQuestionWindow extends Window {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	/* Composants */
	private final Label textLabel = new Label();
	private final TextArea textArea = new TextArea();
	private final ComboBoxPresentation comboBox = new ComboBoxPresentation();
	private final OneClickButton btnOui = new OneClickButton();
	private final OneClickButton btnNon = new OneClickButton();

	/** Listeners */
	@Getter
	private final Set<BtnOkListener> btnOkListeners = new LinkedHashSet<>();

	/**
	 * Crée une fenêtre de confirmation
	 * @param message
	 * @param titre
	 */
	public CandidatQuestionWindow(final QuestionPresentation question, final String titre, final Boolean readOnly) {
		/* Style */
		setWidth(450, Unit.PIXELS);
		setModal(true);
		setResizable(false);
		setClosable(false);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(titre);

		/* Label */
		textLabel.setContentMode(ContentMode.HTML);
		textLabel.setValue(question.getLibQuestion());
		layout.addComponent(textLabel);

		/* Texte */
		textArea.setWidth(100, Unit.PERCENTAGE);
		textArea.setMaxLength(1000);
		if (question.getReponse() != null && question.isString()) {
			textArea.setValue(question.getReponse());
		}
		textArea.setRequired(true);
		textArea.setNullRepresentation("");
		textArea.setImmediate(true);
		textArea.setReadOnly(readOnly);
		textArea.setVisible(question.isString());
		layout.addComponent(textArea);

		/* Oui/Non */
		final String oui = applicationContext.getMessage("question.reponse.oui", null, UI.getCurrent().getLocale());
		final String non = applicationContext.getMessage("question.reponse.non", null, UI.getCurrent().getLocale());
		comboBox.setListe(Arrays.asList(new SimpleBeanPresentation(oui, oui), new SimpleBeanPresentation(non, non)));
		if (question.getReponse() != null && question.isBoolean()) {
			comboBox.setValue(question.getReponse());
		}
		comboBox.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
		comboBox.setRequired(true);
		comboBox.setImmediate(true);
		comboBox.setReadOnly(readOnly);
		comboBox.setVisible(question.isBoolean());
		layout.addComponent(comboBox);

		/* Boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnNon.setCaption(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()));
		btnNon.setIcon(FontAwesome.TIMES);
		btnNon.addClickListener(e -> close());
		buttonsLayout.addComponent(btnNon);
		buttonsLayout.setComponentAlignment(btnNon, Alignment.MIDDLE_LEFT);

		btnOui.setCaption(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()));
		btnOui.setIcon(FontAwesome.CHECK);
		btnOui.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnOui.addClickListener(e -> {
			if (question.isString() && !StringUtils.hasText(textArea.getValue())) {
				textArea.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
				textArea.commit();
				return;
			}
			comboBox.preCommit();
			//comboBox.commit();
			if (question.isBoolean() && comboBox.getValue() == null) {
				return;
			}
			btnOkListeners.forEach(l -> l.btnOkClick(question.isString() ? textArea.getValue() : comboBox.getValue()));
			close();
		});
		buttonsLayout.addComponent(btnOui);
		buttonsLayout.setComponentAlignment(btnOui, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();

		/* Focus */
		textArea.focus();
	}

	/**
	 * Interface pour les listeners du bouton ok.
	 */
	public interface BtnOuiListener extends Serializable {

		/**
		 * Appelé lorsque Ok est cliqué.
		 */
		void btnOuiClick(String text);
	}

	public void addBtnOuiListener(final BtnOkListener btnOkListener) {
		btnOkListeners.add(btnOkListener);
	}

	public void removeBtnOuiListener(final BtnOkListener btnOkListener) {
		btnOkListeners.remove(btnOkListener);
	}

}
