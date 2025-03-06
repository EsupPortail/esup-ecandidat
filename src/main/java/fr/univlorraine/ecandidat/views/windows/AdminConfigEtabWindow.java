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

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.v7.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.ConfigController;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigEtab;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.EmailRFCValidator;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;
import fr.univlorraine.ecandidat.vaadin.form.UrlValidator;

/**
 * Fenêtre d'édition de langue
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class AdminConfigEtabWindow extends Window {

	public static final String[] FIELDS_ORDER = {
		ConfigEtab.ASSIST_DOC_URL,
		ConfigEtab.ASSIST_DOC_URL_CAND,
		ConfigEtab.ASSIST_DOC_URL_CAND_EN,
		ConfigEtab.ASSIST_HELPDESK_URL,
		ConfigEtab.ASSIST_CONTACT_MAIL,
		ConfigEtab.ASSIST_CONTACT_URL };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient ConfigController configController;

	/* Listener */
	private ConfigEtabListener configEtabListener;

	/* Composants */
	private final CustomBeanFieldGroup<ConfigEtab> fieldGroup;
	private final OneClickButton btnEnregistrer;
	private final OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de configuration PegaseAuth
	 * @param configLdap la configuration PegaseAuth à éditer
	 */
	public AdminConfigEtabWindow(final ConfigEtab configEtab) {
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
		setCaption(applicationContext.getMessage("config.etab.window", null, UI.getCurrent().getLocale()));

		/* FieldGroup */
		fieldGroup = new CustomBeanFieldGroup<>(ConfigEtab.class);
		fieldGroup.setItemDataSource(configEtab);

		/* Declaration du bouton d'enregistrement */
		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);

		/* Formulaire */
		final FormLayout formLayout = new FormLayout();
		formLayout.setSpacing(true);
		formLayout.setSizeFull();
		for (final String fieldName : FIELDS_ORDER) {
			final Field<?> field;
			final String caption = applicationContext.getMessage("config.etab.table." + fieldName, null, UI.getCurrent().getLocale());
			field = fieldGroup.buildAndBind(caption, fieldName);
			((RequiredTextField) field).setTextChangeEventMode(TextChangeEventMode.EAGER);
			if (fieldName.contains(ConfigEtab.ASSIST_MAIL)) {
				field.addValidator(new EmailRFCValidator(applicationContext.getMessage("validation.error.mail", null, UI.getCurrent().getLocale())));
			}
			if (fieldName.contains(ConfigEtab.ASSIST_URL)) {
				field.addValidator(new UrlValidator(applicationContext.getMessage("validation.url.malformed", null, UI.getCurrent().getLocale())));
			}
			field.setWidth(100, Unit.PERCENTAGE);
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

		/* Bouton enregistrer */
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la config saisie */
				configController.saveConfigEtab(configEtab);

				/* Ferme la fenêtre */
				close();

				/* Listener d'enregistrement */
				configEtabListener.btnSaveClick();
			} catch (final CommitException ce) {
			}
		});
		buttonsLayout.addComponents(btnAnnuler, btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		layout.addComponent(buttonsLayout);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * Défini le 'configEtabListener' utilisé
	 * @param formationListener
	 */
	public void addConfigEtabListener(final ConfigEtabListener configEtabListener) {
		this.configEtabListener = configEtabListener;
	}

	/** Interface pour récupérer un click sur Oui ou Non. */
	public interface ConfigEtabListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 */
		void btnSaveClick();

	}

}
