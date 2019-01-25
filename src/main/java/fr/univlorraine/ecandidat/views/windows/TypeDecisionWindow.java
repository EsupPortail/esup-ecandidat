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

import fr.univlorraine.ecandidat.controllers.MailController;
import fr.univlorraine.ecandidat.controllers.NomenclatureController;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision_;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxMail;

/**
 * Fenêtre d'édition de typeDecision
 *
 * @author Kevin Hergalant
 */
@Configurable(preConstruction = true)
@SuppressWarnings({"unchecked", "serial"})
public class TypeDecisionWindow extends Window {

	public static final String[] FIELDS_ORDER = {TypeDecision_.codTypDec.getName(), TypeDecision_.libTypDec.getName(), TypeDecision_.typeAvis.getName(), TypeDecision_.mail.getName(),
			TypeDecision_.tesTypDec.getName(), TypeDecision_.temDeverseOpiTypDec.getName(), TypeDecision_.temDefinitifTypDec.getName(), TypeDecision_.temAffCommentTypDec.getName(),
			TypeDecision_.i18nLibTypDec.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient NomenclatureController nomenclatureController;
	@Resource
	private transient MailController mailController;

	/* Composants */
	private CustomBeanFieldGroup<TypeDecision> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de typeDecision
	 *
	 * @param typeDecision
	 *            la typeDecision à éditer
	 */
	public TypeDecisionWindow(final TypeDecision typeDecision, final CentreCandidature ctrCand) {
		/* Style */
		setModal(true);
		setWidth(750, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("typeDec.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(TypeDecision.class);
		fieldGroup.setItemDataSource(typeDecision);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			String caption = applicationContext.getMessage("typeDec.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);
		}

		if (typeDecision.getTemModelTypDec()) {
			fieldGroup.getField(TypeDecision_.codTypDec.getName()).setEnabled(false);
			fieldGroup.getField(TypeDecision_.libTypDec.getName()).setEnabled(false);
			// fieldGroup.getField(TypeDecision_.tesTypDec.getName()).setEnabled(false);
			fieldGroup.getField(TypeDecision_.typeAvis.getName()).setEnabled(false);
		}

		RequiredComboBox<TypeAvis> cbAvis = (RequiredComboBox<TypeAvis>) fieldGroup.getField(TypeDecision_.typeAvis.getName());
		ComboBoxMail cbMail = (ComboBoxMail) fieldGroup.getField(TypeDecision_.mail.getName());
		cbMail.setListMail(mailController.getMailsTypeAvisEnServiceByCtrCand(ctrCand));
		cbMail.filterListValue((TypeAvis) cbAvis.getValue());
		cbAvis.addValueChangeListener(e -> {
			cbMail.filterListValue((TypeAvis) cbAvis.getValue());
		});

		/* Si la liste de mail est vide, on place le mail a null */
		if (typeDecision.getMail() != null && cbMail.hasMailDisplay()) {
			cbMail.setValue(typeDecision.getMail());
		} else {
			cbMail.setValue(null);
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

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {
				/* Si le code de profil existe dejà --> erreur */
				if (!typeDecisionController.isCodTypeDecUnique((String) fieldGroup.getField(TypeDecision_.codTypDec.getName()).getValue(), typeDecision.getIdTypDec())) {
					Notification.show(applicationContext.getMessage("window.error.cod.nonuniq", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}
				/* Si on desactive le dernier avis du meme typ d'avis */
				TypeAvis typ = (TypeAvis) cbAvis.getValue();
				if (typ == null) {
					fieldGroup.commit();
				}
				// Boolean tes = (Boolean) fieldGroup.getField(TypeDecision_.tesTypDec.getName()).getValue();
				// if (!typeDecisionController.checkDisableDecision(typ.getCodTypAvis(), typeDecision.getIdTypDec(), tes)) {
				// Notification.show(applicationContext.getMessage("typeDec.window.error.last", new Object[] {typ.getLibelleTypAvis()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				// return;
				// }

				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la typeDecision saisie */
				typeDecisionController.saveTypeDecision(typeDecision);
				/* Ferme la fenêtre */
				close();
			} catch (CommitException ce) {
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}
}
