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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fr.univlorraine.ecandidat.controllers.ConfigController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigLdap;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre d'édition de langue
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class AdminTestConfigLdapWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient ConfigController configController;

	/* Composants */
	private final TextField testField = new TextField();
	private final OneClickButton btnTest;
	private final OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre de test de configuration Ldap
	 * @param configLdap la configuration Ldap à tester
	 */
	public AdminTestConfigLdapWindow(final ConfigLdap configLdap) {
		/* Style */
		setModal(true);
		setWidth(600, Unit.PIXELS);
		setResizable(false);
		setClosable(false);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("config.ldap.test.title", null, UI.getCurrent().getLocale()));

		/* Zone de texte */
		testField.setCaption(applicationContext.getMessage("config.ldap.test.label", null, UI.getCurrent().getLocale()));
		testField.setWidth(100, Unit.PERCENTAGE);
		testField.setRequired(true);

		/* Les boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());

		/* Ajoute les composants de test */
		btnTest = new OneClickButton(applicationContext.getMessage("config.ldap.test.btn", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnTest.addClickListener(e -> {
			/* Enregistre la config saisie */
			if (testField.isValid()) {
				if (StringUtils.isBlank(testField.getValue()) || testField.getValue().length() < ConstanteUtils.NB_MIN_CAR_PERS) {
					Notification.show(applicationContext.getMessage("window.search.morethan", new Object[] { ConstanteUtils.NB_MIN_CAR_PERS }, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
				} else {
					configController.testConfigLdap(configLdap, testField.getValue());
				}
			} else {
				testField.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
			}
		});

		buttonsLayout.addComponents(btnAnnuler, btnTest);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);
		buttonsLayout.setComponentAlignment(btnTest, Alignment.MIDDLE_RIGHT);

		layout.addComponents(testField, buttonsLayout);

		/* Centre la fenêtre */
		center();
	}

}
