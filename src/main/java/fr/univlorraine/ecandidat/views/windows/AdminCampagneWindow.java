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

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CampagneController;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne_;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.LocalDateTimeField;
import fr.univlorraine.ecandidat.vaadin.form.SearchAnneeUnivApoField;

/**
 * Fenêtre d'édition de campagne
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class AdminCampagneWindow extends Window {

	public static final String[] FIELDS_ORDER = { Campagne_.codCamp.getName(),
		Campagne_.libCamp.getName(),
		Campagne_.i18nLibCamp.getName(),
		Campagne_.datDebCamp.getName(),
		Campagne_.datFinCamp.getName(),
		Campagne_.datFinCandidatCamp.getName(),
		Campagne_.datActivatPrevCamp.getName() };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CampagneController campagneController;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/* Composants */
	private CustomBeanFieldGroup<Campagne> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de campagne
	 * @param campagne
	 *                              la campagne à éditer
	 * @param campagneAArchiver
	 */
	public AdminCampagneWindow(final Campagne campagne, final Campagne campagneAArchiver) {
		String[] fieldsOrderToUse = FIELDS_ORDER;
		if (campagneAArchiver == null) {
			fieldsOrderToUse = ArrayUtils.removeElement(fieldsOrderToUse,
				Campagne_.datActivatPrevCamp.getName());
		}
		/* Style */
		setModal(true);
		setWidth(650, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("campagne.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(Campagne.class);
		fieldGroup.setItemDataSource(campagne);
		final FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);

		for (final String fieldName : fieldsOrderToUse) {
			final String caption = applicationContext.getMessage("campagne.table." + fieldName,
				null,
				UI.getCurrent().getLocale());
			Field<?> field;
			if (fieldName.equals(Campagne_.codCamp.getName())
				&& siScolService.hasSearchAnneeUni()) {
				field = fieldGroup.buildAndBind(caption, fieldName, SearchAnneeUnivApoField.class);
			} else {
				field = fieldGroup.buildAndBind(caption, fieldName);
			}
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);
		}

		layout.addComponent(formLayout);

		if (campagneAArchiver != null) {
			final LocalDateTimeField archivageField = (LocalDateTimeField) fieldGroup
				.getField(Campagne_.datActivatPrevCamp.getName());
			archivageField.setRequired(true);
			archivageField.setRequiredError(
				applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
			layout.addComponent(new Label(applicationContext.getMessage("campagne.to.archiv",
				new Object[]
				{ campagneAArchiver.getLibCamp() },
				UI.getCurrent().getLocale())));
		}

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
				if (!campagneController.isCodCampUnique(
					(String) fieldGroup.getField(Campagne_.codCamp.getName()).getValue(),
					campagne.getIdCamp())) {
					Notification.show(applicationContext.getMessage("window.error.cod.nonuniq",
						null,
						UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la campagne saisie */
				campagneController.saveCampagne(campagne, campagneAArchiver);
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
}
