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

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.ConfigController;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseAuth;
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
public class AdminConfigPegaseAuthWindow extends Window {

	public static final String[] FIELDS_ORDER = { ConfigPegaseAuth.URL, ConfigPegaseAuth.USER, ConfigPegaseAuth.PWD };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient ConfigController configController;

	/* Listener */
	private ConfigPegaseAuthListener configPegaseAuthListener;

	/* Composants */
	private final CustomBeanFieldGroup<ConfigPegaseAuth> fieldGroup;
	private final OneClickButton btnEnregistrer;
	private final OneClickButton btnTest;
	private final OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de configuration PegaseAuth
	 * @param configLdap la configuration PegaseAuth à éditer
	 */
	public AdminConfigPegaseAuthWindow(final ConfigPegaseAuth configPegaseAuth) {
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
		setCaption(applicationContext.getMessage("config.pegaseAuth.window", null, UI.getCurrent().getLocale()));

		/* FieldGroup */
		fieldGroup = new CustomBeanFieldGroup<>(ConfigPegaseAuth.class);
		fieldGroup.setItemDataSource(configPegaseAuth);

		/* Declaration du bouton d'enregistrement */
		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);

		/* Formulaire */
		final FormLayout formLayout = new FormLayout();
		formLayout.setSpacing(true);
		formLayout.setSizeFull();
		for (final String fieldName : FIELDS_ORDER) {
			final Field<?> field;
			final String caption = applicationContext.getMessage("config.pegaseAuth.table." + fieldName, null, UI.getCurrent().getLocale());
			if (fieldName.equals(ConfigPegaseAuth.PWD)) {
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

		/* Les boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);

		/* Bouton annuler */
		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());

		/* Bouton de test */
		btnTest = new OneClickButton(applicationContext.getMessage("config.pegaseAuth.test.btn", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnTest.addClickListener(e -> {

			try {
				/* Valide la saisie */
				fieldGroup.commit();

				if (configController.testConfigPegaseAuth(configPegaseAuth)) {
					btnEnregistrer.setEnabled(true);
					return;
				}

				btnEnregistrer.setEnabled(false);
			} catch (final CommitException ce) {
				btnEnregistrer.setEnabled(false);
			}

		});

		/* Bouton enregistrer */
		btnEnregistrer.setEnabled(false);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la config saisie */
				configController.saveConfigPegaseAuth(configPegaseAuth);

				/* Ferme la fenêtre */
				close();

				/* Listener d'enregistrement */
				configPegaseAuthListener.btnSaveClick();
			} catch (final CommitException ce) {
			}
		});

		buttonsLayout.addComponents(btnAnnuler, btnTest, btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);
		buttonsLayout.setComponentAlignment(btnTest, Alignment.MIDDLE_CENTER);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		layout.addComponent(buttonsLayout);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * Défini le 'configLdapListener' utilisé
	 * @param formationListener
	 */
	public void addConfigPegaseAuthListener(final ConfigPegaseAuthListener configPegaseAuthListener) {
		this.configPegaseAuthListener = configPegaseAuthListener;
	}

	/** Interface pour récupérer un click sur Oui ou Non. */
	public interface ConfigPegaseAuthListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 */
		void btnSaveClick();

	}

}
