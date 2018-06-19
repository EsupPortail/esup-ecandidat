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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/** Fenêtre de confirmation
 *
 * @author Kevin Hergalant */
@Configurable(preConstruction = true)
public class CandidatAdminDeleteWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -6274038192734633032L;

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	/* Composants */
	private Label textLabel = new Label();
	private OneClickButton btnOui = new OneClickButton();
	private CheckBox checkBoxSendMail = new CheckBox();
	private OneClickButton btnNon = new OneClickButton();

	private DeleteCandidatWindowListener deleteCandidatWindowListener;

	/** Crée une fenêtre de confirmation avec un message et un titre par défaut */
	public CandidatAdminDeleteWindow() {
		this(null);
	}

	/** Modifie le message
	 *
	 * @param message
	 */
	public void setMessage(String message) {
		if (message == null) {
			message = applicationContext.getMessage("confirmWindow.defaultQuestion", null, UI.getCurrent().getLocale());
		}
		textLabel.setValue(message);
	}

	/** Crée une fenêtre de confirmation
	 *
	 * @param message
	 */
	public CandidatAdminDeleteWindow(final String message) {
		/* Style */
		setWidth(400, Unit.PIXELS);
		setModal(true);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("confirmWindow.defaultTitle", null, UI.getCurrent().getLocale()));

		/* Texte */
		setMessage(message);
		textLabel.setContentMode(ContentMode.HTML);
		layout.addComponent(textLabel);

		/* Coche envoi mail */
		checkBoxSendMail.setCaption(applicationContext.getMessage("candidat.delete.mail", null, UI.getCurrent().getLocale()));
		checkBoxSendMail.setValue(true);
		layout.addComponent(checkBoxSendMail);

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
		btnOui.addClickListener(e -> {
			deleteCandidatWindowListener.btnOkClick(checkBoxSendMail.getValue());
			close();
		});
		buttonsLayout.addComponent(btnOui);
		buttonsLayout.setComponentAlignment(btnOui, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	/** Défini le 'DeleteCandidatWindowListener' utilisé
	 *
	 * @param deleteCandidatWindowListener
	 */
	public void addDeleteCandidatWindowListener(final DeleteCandidatWindowListener deleteCandidatWindowListener) {
		this.deleteCandidatWindowListener = deleteCandidatWindowListener;
	}

	/** Interface pour récupérer un click sur Oui. */
	public interface DeleteCandidatWindowListener extends Serializable {

		/** Appelé lorsque Oui est cliqué.
		 *
		 * @param sendMail
		 */
		public void btnOkClick(Boolean sendMail);

	}
}
