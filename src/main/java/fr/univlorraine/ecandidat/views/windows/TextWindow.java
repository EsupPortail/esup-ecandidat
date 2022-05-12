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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de saisie d'un texte
 *
 * @author Matthieu Manginot
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class TextWindow extends Window {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	/* Composants */
	private Label textLabel = new Label();
	private TextField textField = new TextField();
	private OneClickButton btnOui = new OneClickButton();
	private OneClickButton btnNon = new OneClickButton();

	/**
	 * Ajoute un listener sur le bouton oui
	 *
	 * @param clickListener
	 */
	public void addBtnOuiListener(final ClickListener clickListener) {
		btnOui.addClickListener(clickListener);
	}

	/**
	 * Supprime un listener sur le bouton oui
	 *
	 * @param clickListener
	 */
	public void removeBtnOuiListener(final ClickListener clickListener) {
		btnOui.removeClickListener(clickListener);
	}

	/**
	 * Ajoute un listener sur le bouton non
	 *
	 * @param clickListener
	 */
	public void addBtnNonListener(final ClickListener clickListener) {
		btnNon.addClickListener(clickListener);
	}

	/**
	 * Supprime un listener sur le bouton non
	 *
	 * @param clickListener
	 */
	public void removeBtnNonListener(final ClickListener clickListener) {
		btnNon.removeClickListener(clickListener);
	}

	/** Crée une fenêtre de confirmation avec un message et un titre par défaut */
	public TextWindow() {
		this(null, null, null);
	}

	/**
	 * Crée une fenêtre de confirmation avec un titre par défaut
	 *
	 * @param message
	 */
	public TextWindow(final String message) {
		this(message, null, null);
	}

	/**
	 * Modifie le titre
	 *
	 * @param titre
	 */
	public void setTitle(String titre) {
		if (titre == null) {
			titre = applicationContext.getMessage("confirmWindow.defaultTitle", null, UI.getCurrent().getLocale());
		}
		setCaption(titre);
	}

	public String getText() {
		return textField != null ? textField.getValue() : null;
	}

	/**
	 * Modifie le message
	 *
	 * @param message
	 */
	public void setMessage(String message) {
		if (message == null) {
			message = applicationContext.getMessage("confirmWindow.defaultQuestion", null, UI.getCurrent().getLocale());
		}
		textLabel.setValue(message);
	}

	/**
	 * Crée une fenêtre de confirmation
	 *
	 * @param message
	 * @param titre
	 */
	public TextWindow(final String message, final String titre, final String text) {
		/* Style */
		setWidth(450, Unit.PIXELS);
		setModal(true);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setTitle(titre);

		/* Label */
		textLabel.setContentMode(ContentMode.HTML);
		setMessage(message);
		layout.addComponent(textLabel);

		/* Texte */
		textField.setWidth(100, Unit.PERCENTAGE);
		textField.setMaxLength(1000);
		if (text != null)
			textField.setValue(text);
		layout.addComponent(textField);

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnNon.setCaption(applicationContext.getMessage("confirmWindow.btnNon", null, UI.getCurrent().getLocale()));
		btnNon.setIcon(FontAwesome.TIMES);
		btnNon.addClickListener(e -> close());
		buttonsLayout.addComponent(btnNon);
		buttonsLayout.setComponentAlignment(btnNon, Alignment.MIDDLE_LEFT);

		btnOui.setCaption(applicationContext.getMessage("confirmWindow.btnOui", null, UI.getCurrent().getLocale()));
		btnOui.setIcon(FontAwesome.CHECK);
		btnOui.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnOui.addClickListener(e -> close());
		buttonsLayout.addComponent(btnOui);
		buttonsLayout.setComponentAlignment(btnOui, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
		/* Focus */
		textField.focus();
	}

}
