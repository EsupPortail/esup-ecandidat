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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.ConfigController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigLdap;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredPasswordField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;

/**
 * Fenêtre d'édition de langue
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class AdminConfigLdapWindow extends Window {

	public static final String[] FIELDS_ORDER = { ConfigLdap.URL,
		ConfigLdap.BASE,
		ConfigLdap.USER,
		ConfigLdap.PWD,
		ConfigLdap.BRANCHE_PEOPLE,
		ConfigLdap.FILTRE_PERSONNEL,
		ConfigLdap.CHAMPS_UID,
		ConfigLdap.CHAMPS_DISPLAYNAME,
		ConfigLdap.CHAMPS_MAIL,
		ConfigLdap.CHAMPS_SN,
		ConfigLdap.CHAMPS_CN,
		ConfigLdap.CHAMPS_SUPANNCIVILITE,
		ConfigLdap.CHAMPS_SUPANNETUID,
		ConfigLdap.CHAMPS_GIVENNAME };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient ConfigController configController;

	/* Listener */
	private ConfigLdapListener configLdapListener;

	/* Composants */
	private final TextField testField = new TextField();
	private final OneClickButton btnTest;

	private final CustomBeanFieldGroup<ConfigLdap> fieldGroup;
	private final OneClickButton btnEnregistrer;
	private final OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de configuration Ldap
	 * @param configLdap la configuration Ldap à éditer
	 */
	public AdminConfigLdapWindow(final ConfigLdap configLdap) {
		/* Style */
		setModal(true);
		setWidth(700, Unit.PIXELS);
		setResizable(false);
		setClosable(false);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("config.ldap.window", null, UI.getCurrent().getLocale()));

		/* FieldGroup */
		fieldGroup = new CustomBeanFieldGroup<>(ConfigLdap.class);
		fieldGroup.setItemDataSource(configLdap);

		/* Les boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.setEnabled(false);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la config saisie */
				configController.saveConfigLdap(configLdap);

				/* Ferme la fenêtre */
				close();

				/* Listener d'enregistrement */
				configLdapListener.btnSaveClick();
			} catch (final CommitException ce) {
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Formulaire */
		final FormLayout formLayout = new FormLayout();
		formLayout.setSpacing(true);
		formLayout.setSizeFull();
		for (final String fieldName : FIELDS_ORDER) {
			final Field<?> field;
			final String caption = applicationContext.getMessage("config.ldap.table." + fieldName, null, UI.getCurrent().getLocale());
			if (fieldName.equals(ConfigLdap.PWD)) {
				field = fieldGroup.buildAndBind(caption, fieldName, RequiredPasswordField.class);
				((RequiredPasswordField) field).setTextChangeEventMode(TextChangeEventMode.EAGER);
			} else {
				field = fieldGroup.buildAndBind(caption, fieldName);
				((RequiredTextField) field).setTextChangeEventMode(TextChangeEventMode.EAGER);
			}
			field.setWidth(100, Unit.PERCENTAGE);
			field.setRequired(true);
			field.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
			field.addValueChangeListener(e -> btnEnregistrer.setEnabled(false));
			formLayout.addComponent(field);
		}
		layout.addComponent(formLayout);

		/* Ajoute les composants de test */
		btnTest = new OneClickButton(applicationContext.getMessage("config.ldap.test.btn", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnTest.addClickListener(e -> {

			try {
				/* Valide la saisie */
				fieldGroup.commit();

				/* Enregistre la config saisie */
				if (testField.isValid()) {
					if (StringUtils.isBlank(testField.getValue()) || testField.getValue().length() < ConstanteUtils.NB_MIN_CAR_PERS) {
						Notification.show(applicationContext.getMessage("window.search.morethan", new Object[] { ConstanteUtils.NB_MIN_CAR_PERS }, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
					} else {
						if (configController.testConfigLdap(configLdap, testField.getValue())) {
							btnEnregistrer.setEnabled(true);
							return;
						}
					}
				} else {
					testField.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
				}
				btnEnregistrer.setEnabled(false);
			} catch (final CommitException ce) {
				btnEnregistrer.setEnabled(false);
			}

		});

		testField.setWidth(100, Unit.PERCENTAGE);
		testField.setRequired(true);

		final Panel panelTest = new Panel();
		panelTest.setWidth(100, Unit.PERCENTAGE);

		final HorizontalLayout testLayout = new HorizontalLayout();
		testLayout.setWidth(100, Unit.PERCENTAGE);
		testLayout.setSpacing(true);
		testLayout.setMargin(true);
		testLayout.addComponents(testField, btnTest);
		testLayout.setComponentAlignment(testField, Alignment.MIDDLE_LEFT);
		testLayout.setComponentAlignment(btnTest, Alignment.BOTTOM_RIGHT);
		testLayout.setExpandRatio(testField, 1);

		panelTest.setContent(testLayout);
		panelTest.setCaption(applicationContext.getMessage("config.ldap.test.caption", null, UI.getCurrent().getLocale()));

		layout.addComponents(panelTest, buttonsLayout);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * Défini le 'configLdapListener' utilisé
	 * @param formationListener
	 */
	public void addConfigLdapListener(final ConfigLdapListener configLdapListener) {
		this.configLdapListener = configLdapListener;
	}

	/** Interface pour récupérer un click sur Oui ou Non. */
	public interface ConfigLdapListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 */
		void btnSaveClick();

	}

}
