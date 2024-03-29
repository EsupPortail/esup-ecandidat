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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.EmailRFCValidator;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;

/**
 * Fenêtre de demande d'envoie d'identifiant
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CandidatIdOublieWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;

	/* Composants */
	private final OneClickButton btnEnregistrer;
	private final OneClickButton btnAnnuler;

	/* Encodage par défaut */
	@Value("${charset.default:}")
	private String defaultCharset;

	/**
	 * Crée une fenêtre de demande d'envoie d'identifiant ou de code d'activation
	 */
	public CandidatIdOublieWindow(final String mode) {
		/* Style */
		setModal(true);
		setWidth(600, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		if (mode.equals(ConstanteUtils.FORGOT_MODE_ID_OUBLIE)) {
			setCaption(applicationContext.getMessage("compteMinima.pwd.oublie.title", null, UI.getCurrent().getLocale()));
			layout.addComponent(new Label(applicationContext.getMessage("compteMinima.pwd.oublie", null, UI.getCurrent().getLocale())));
		} else {
			setCaption(applicationContext.getMessage("compteMinima.code.oublie.title", null, UI.getCurrent().getLocale()));
			layout.addComponent(new Label(applicationContext.getMessage("compteMinima.code.oublie", null, UI.getCurrent().getLocale())));
		}

		/* Formulaire */
		final FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);

		final RequiredTextField rtf = new RequiredTextField(defaultCharset);
		rtf.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
		rtf.setNullRepresentation("");
		rtf.setRequired(true);
		rtf.setCaption(applicationContext.getMessage("compteMinima.table.mailPersoCptMin", null, UI.getCurrent().getLocale()));
		rtf.addValidator(new EmailRFCValidator(applicationContext.getMessage("validation.error.mail", null, UI.getCurrent().getLocale())));
		rtf.setWidth(100, Unit.PERCENTAGE);
		formLayout.addComponent(rtf);
		layout.addComponent(formLayout);

		/* Ajoute les boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSend", null, UI.getCurrent().getLocale()), FontAwesome.SEND);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			rtf.preCommit();
			if (rtf.isValid()) {
				if (candidatController.initPasswordOrActivationCode(rtf.getValue(), mode)) {
					close();
				}
			}

		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}
}
