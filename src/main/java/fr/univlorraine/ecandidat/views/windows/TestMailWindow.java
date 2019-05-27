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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.MailController;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/** Fenêtre de test de mail
 *
 * @author Kevin Hergalant */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class TestMailWindow extends Window {
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient MailController mailController;

	private List<TextField> listTf = new ArrayList<>();

	private OneClickButton btnTest;
	private OneClickButton btnAnnuler;

	private HorizontalLayout generateHlTf(final String property) {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth(100, Unit.PERCENTAGE);
		hl.setSpacing(true);
		TextField tfLabel = new TextField();
		tfLabel.setValue("${" + property + "}");
		tfLabel.setReadOnly(true);
		hl.addComponent(tfLabel);
		hl.setExpandRatio(tfLabel, 1);
		TextField tf = new TextField();
		tf.setId(property);
		tf.setWidth(100, Unit.PERCENTAGE);
		tf.setValue("test" + property.replaceAll("\\.", ""));
		hl.addComponent(tf);
		hl.setExpandRatio(tf, 1.5f);

		listTf.add(tf);
		return hl;
	}

	/** Crée une fenêtre d'édition de mail
	 *
	 * @param mail
	 *            la mail à éditer */
	public TestMailWindow(final Mail mail) {
		/* Style */
		setModal(true);
		setWidth(700, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("mail.test.window", new Object[] {mail.getCodMail(), mail.getLibMail()}, UI.getCurrent().getLocale()));

		/*Layout de variables*/
		VerticalLayout layoutVar = new VerticalLayout();
		layoutVar.setMargin(true);
		layoutVar.setSpacing(true);
		layoutVar.setWidth(100, Unit.PERCENTAGE);
		layoutVar.setHeightUndefined();
		String varMailGen = mailController.getVarMailCandidature(mail.getCodMail());
		String varMailSpecifiques = mailController.getVarMail(mail);

		if (varMailGen != null) {
			for (String property : varMailGen.split(";")) {
				layoutVar.addComponent(generateHlTf(property));
			}
		}

		if (varMailSpecifiques != null) {
			for (String property : varMailSpecifiques.split(";")) {
				layoutVar.addComponent(generateHlTf(property));
			}
		}

		/*Panel de variables*/
		Panel pan = new Panel();
		pan.setHeight(600, Unit.PIXELS);
		pan.setWidth(100, Unit.PERCENTAGE);
		pan.setContent(layoutVar);

		layout.addComponent(pan);

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnTest = new OneClickButton(applicationContext.getMessage("mail.test.button", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnTest.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnTest.addClickListener(e -> {
			// try {
			// String ret = mailController.testMail(mail, listTf.stream().collect(Collectors.toMap(TextField::getId, TextField::getValue)));
			// UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("mail.test.result", null, UI.getCurrent().getLocale()), ret, 500, 70));
			// } catch (ParseException ex) {
			// Notification.show(applicationContext.getMessage("mail.test.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			// }

			close();
		});
		buttonsLayout.addComponent(btnTest);
		buttonsLayout.setComponentAlignment(btnTest, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

}
