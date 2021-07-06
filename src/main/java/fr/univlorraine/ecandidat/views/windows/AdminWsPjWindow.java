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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.SiScolController;
import fr.univlorraine.ecandidat.controllers.TestController;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileDownloader;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import lombok.Data;

/**
 * Fenêtre de test du WS PJ
 * @author Kevin Hergalant
 */
@Configurable(preConstruction = true)
public class AdminWsPjWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 2100803133162621729L;

	public String[] FIELDS_ORDER = { "codEtu", "codTpj" };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient SiScolController siScolController;
	@Resource
	private transient TestController testController;

	/* Composants */
	private CustomBeanFieldGroup<WSPjForm> fieldGroup;
	private OneClickButton btnTestInfo;
	private OneClickButton btnTestFile;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre de test du WS PJ
	 */
	public AdminWsPjWindow() {
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
		setCaption(applicationContext.getMessage("version.ws.pj.window", null, UI.getCurrent().getLocale()));

		final WSPjForm pjForm = new WSPjForm();
		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(WSPjForm.class);
		fieldGroup.setItemDataSource(pjForm);
		final FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (final String fieldName : FIELDS_ORDER) {
			final Field<?> field = fieldGroup.buildAndBind(applicationContext.getMessage("version.ws.pj." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);
		}

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

		btnTestInfo = new OneClickButton(applicationContext.getMessage("version.ws.pj.testInfo.btn", null, UI.getCurrent().getLocale()), FontAwesome.INFO_CIRCLE);
		btnTestInfo.addStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonsLayout.addComponent(btnTestInfo);
		buttonsLayout.setComponentAlignment(btnTestInfo, Alignment.MIDDLE_CENTER);
		btnTestInfo.addClickListener(e -> {
			try {
				/* Valide la saisie */
				fieldGroup.commit();
				/* Test les données saisies */
				final WSPjForm data = fieldGroup.getItemDataSource().getBean();
				siScolController.testWSPJSiScolInfo(data.getCodEtu(), data.getCodTpj());
				/* Ferme la fenêtre */
				// close();
			} catch (final CommitException ce) {
			}
		});

		btnTestFile = new OneClickButton(applicationContext.getMessage("version.ws.pj.testFile.btn", null, UI.getCurrent().getLocale()), FontAwesome.CLOUD);
		btnTestFile.addStyleName(ValoTheme.BUTTON_PRIMARY);
		buttonsLayout.addComponent(btnTestFile);
		buttonsLayout.setComponentAlignment(btnTestFile, Alignment.MIDDLE_RIGHT);
		new OnDemandFileDownloader(new OnDemandStreamFile() {

			@Override
			public OnDemandFile getOnDemandFile() {
				try {
					/* Valide la saisie */
					fieldGroup.commit();
					/* Test les données saisies */
					final WSPjForm data = fieldGroup.getItemDataSource().getBean();
					return siScolController.testWSPJSiScolFile(data.getCodEtu(), data.getCodTpj());
				} catch (final CommitException ce) {
					return null;
				}
			}
		}, btnTestFile);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * @author Kevin Hergalant
	 */
	@Data
	public class WSPjForm {
		@NotNull
		@Size(max = 8)
		private String codEtu;
		@NotNull
		@Size(max = 5)
		private String codTpj;

		public WSPjForm(final String codEtu, final String codTpj) {
			super();
			this.codEtu = codEtu;
			this.codTpj = codTpj;
		}

		public WSPjForm() {
			super();
		}
	}
}
