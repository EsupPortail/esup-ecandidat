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
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import lombok.Getter;

/**
 * Fenêtre de saisie
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class InputWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -8745569299713953772L;

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	/* Composants */
	private AbstractTextField inputTextField;
	private OneClickButton btnOk = new OneClickButton();
	private OneClickButton btnCancel = new OneClickButton();

	/** Listeners */
	@Getter
	private Set<BtnOkListener> btnOkListeners = new LinkedHashSet<>();

	public void addBtnOkListener(BtnOkListener btnOkListener) {
		btnOkListeners.add(btnOkListener);
	}

	public void removeBtnOkListener(BtnOkListener btnOkListener) {
		btnOkListeners.remove(btnOkListener);
	}

	public void addBtnCancelListener(ClickListener clickListener) {
		btnCancel.addClickListener(clickListener);
	}

	public void removeBtnCancelListener(ClickListener clickListener) {
		btnCancel.removeClickListener(clickListener);
	}

	/**
	 * Crée une fenêtre de saisie avec un message et un titre par défaut
	 */
	public InputWindow() {
		this(null, null, false, 255);
	}

	/**
	 * Crée une fenêtre de saisie avec un titre par défaut
	 * @param message
	 */
	public InputWindow(String message) {
		this(message, null, false, 255);
	}

	/**
	 * Crée une fenêtre de saisie
	 * @param message
	 * @param titre
	 * @param maximize si on veut utiliser une TextArea
	 * @param maxLength
	 */
	public InputWindow(String message, String titre, Boolean maximize, Integer maxLength) {
		/* Style */
		setWidth(400, Unit.PIXELS);
		setModal(true);
		setResizable(false);
		setClosable(false);
		
		if (maximize){
			inputTextField = new TextArea();
		}else{
			inputTextField = new TextField();
		}
		inputTextField.setMaxLength(maxLength);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		if (titre == null) {
			titre = applicationContext.getMessage("inputWindow.defaultTitle", null, UI.getCurrent().getLocale());
		}
		setCaption(titre);

		/* Texte */
		if (message == null) {
			message = applicationContext.getMessage("inputWindow.defaultMessage", null, UI.getCurrent().getLocale());
		}
		Label textLabel = new Label(message);
		layout.addComponent(textLabel);

		/* Champ de saisie */
		inputTextField.setWidth(100, Unit.PERCENTAGE);
		inputTextField.addShortcutListener(new ShortcutListener(null, ShortcutAction.KeyCode.ENTER, null) {
			private static final long serialVersionUID = 6231790311427334925L;

			@Override
			public void handleAction(Object sender, Object target) {
				btnOk.click();
			}
		});
		layout.addComponent(inputTextField);

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnCancel.setCaption(applicationContext.getMessage("inputWindow.btnCancel", null, UI.getCurrent().getLocale()));
		btnCancel.addClickListener(e -> close());
		buttonsLayout.addComponent(btnCancel);
		buttonsLayout.setComponentAlignment(btnCancel, Alignment.MIDDLE_LEFT);

		btnOk.setCaption(applicationContext.getMessage("inputWindow.btnOk", null, UI.getCurrent().getLocale()));
		btnOk.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnOk.addClickListener(e -> {
			btnOkListeners.forEach(l -> l.btnOkClick(inputTextField.getValue()));
			close();
		});
		buttonsLayout.addComponent(btnOk);
		buttonsLayout.setComponentAlignment(btnOk, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
		/* Place le focus sur le champ de saisie */
		inputTextField.focus();
	}

	/**
	 * Interface pour les listeners du bouton ok.
	 */
	public interface BtnOkListener extends Serializable {

		/**
		 * Appelé lorsque Ok est cliqué.
		 */
		public void btnOkClick(String text);

	}

}
