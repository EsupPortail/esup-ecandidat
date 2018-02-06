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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.TestController;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import lombok.Data;

/** Fenêtre de test de l'INE
 *
 * @author Kevin Hergalant */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class AdminInesWindow extends Window {

	public String[] FIELDS_ORDER = {"ines"};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TestController testController;
	@Resource
	private transient CandidatController candidatController;

	@Resource
	private transient String urlWsCheckInes;

	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/* Composants */
	private CustomBeanFieldGroup<Ines> fieldGroup;
	private OneClickButton btnTest;
	private OneClickButton btnAnnuler;

	/** Crée une fenêtre de test du WS PJ */
	public AdminInesWindow() {
		/* Style */
		setModal(true);
		setWidth(700, Unit.PIXELS);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("version.ines.window", null, UI.getCurrent().getLocale()));

		Ines ineForm = new Ines();
		if (testController.isTestMode()) {
			ineForm.setInes("223456789HE");
		}

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(Ines.class);
		fieldGroup.setItemDataSource(ineForm);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			Field<?> field = fieldGroup.buildAndBind(applicationContext.getMessage("version.ines." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);
		}

		layout.addComponent(formLayout);

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnTest = new OneClickButton(applicationContext.getMessage("version.ines.btn", null, UI.getCurrent().getLocale()), FontAwesome.INFO_CIRCLE);
		btnTest.addStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonsLayout.addComponent(btnTest);
		buttonsLayout.setComponentAlignment(btnTest, Alignment.MIDDLE_RIGHT);
		btnTest.addClickListener(e -> {
			try {
				/* Valide la saisie */
				fieldGroup.commit();
				/* Test les données saisies */
				Ines data = fieldGroup.getItemDataSource().getBean();

				if (urlWsCheckInes == null || urlWsCheckInes.equals("")) {
					Notification.show(applicationContext.getMessage("version.ines.ws.no", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				if (!MethodUtils.isINES(data.getInes())) {
					Notification.show(applicationContext.getMessage("version.ines.malforme", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				try {
					String ine = MethodUtils.getIne(data.getInes());
					String cleIne = MethodUtils.getCleIne(data.getInes());
					if (!siScolService.checkStudentINES(ine, cleIne)) {
						Notification.show(applicationContext.getMessage("version.ines.nok", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					} else {
						Notification.show(applicationContext.getMessage("version.ines.ok", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					}
				} catch (Exception ex) {
					Notification.show(applicationContext.getMessage("version.ines.ws.nok", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				}
			} catch (CommitException ce) {
			}
		});

		/* Centre la fenêtre */
		center();
	}

	/** @author Kevin Hergalant */
	@Data
	public class Ines {
		@NotNull
		@Size(min = 11, max = 11)
		private String ines;

		public Ines(final String ines) {
			super();
			this.ines = ines;
		}

		public Ines() {
			super();
		}
	}
}
