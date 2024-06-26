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

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.vaadin.components.CustomPanel;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.EmailRFCValidator;
import fr.univlorraine.ecandidat.vaadin.form.RequiredPasswordField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;

/**
 * Fenêtre d'édition de compte a minima
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CandidatCompteMinimaWindow extends Window {

	private static final String codeConfirmMailPerso = "confirmMailPersoCptMin";
	private static final String codeConfirmPwd = "confirmPwdCptMin";

	public static final String[] FIELDS_ORDER_WITHOUT_PWD = { CompteMinima_.nomCptMin.getName(), CompteMinima_.prenomCptMin.getName(), CompteMinima_.mailPersoCptMin.getName(), codeConfirmMailPerso };
	public static final String[] FIELDS_ORDER_WHITH_PWD =
		{ CompteMinima_.nomCptMin.getName(), CompteMinima_.prenomCptMin.getName(), CompteMinima_.mailPersoCptMin.getName(), codeConfirmMailPerso, CompteMinima_.pwdCptMin.getName(), codeConfirmPwd };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient ParametreController parametreController;

	/* Composants */
	private CompteMinimaWindowListener compteMinimaWindowListener;
	private CustomBeanFieldGroup<CompteMinima> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de compteMinima
	 * @param compteMinima la compteMinima à éditer
	 */
	public CandidatCompteMinimaWindow(final CompteMinima compteMinima, final Boolean createByGestionnaire) {
		final Boolean pwdAsked = (createByGestionnaire || (!parametreController.getIsMdpConnectCAS() && compteMinima.getLoginCptMin() != null)) ? false : true;

		/* Style */
		setModal(true);
		setWidth(550, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("compteMinima.window", null, UI.getCurrent().getLocale()));

		/* Ajout des informations de création de compte */
		layout.addComponent(new Label(applicationContext.getMessage("compteMinima.create.warning", null, UI.getCurrent().getLocale())));

		if (pwdAsked) {
			/* Panel d'infos mot de passe */
			final CustomPanel panelInfo =
				new CustomPanel(applicationContext.getMessage("compteMinima.info.pwd.title", null, UI.getCurrent().getLocale()), applicationContext.getMessage("compteMinima.info.pwd", null, UI.getCurrent().getLocale()),
					FontAwesome.INFO_CIRCLE);
			panelInfo.setWidthMax();
			panelInfo.setMargin(true);
			panelInfo.addLabelStyleName(ValoTheme.LABEL_TINY);
			layout.addComponent(panelInfo);
		}

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(CompteMinima.class);
		fieldGroup.setItemDataSource(compteMinima);
		final FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (final String fieldName : (pwdAsked ? FIELDS_ORDER_WHITH_PWD : FIELDS_ORDER_WITHOUT_PWD)) {
			final String caption = applicationContext.getMessage("compteMinima.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field;
			if (fieldName.equals(CompteMinima_.pwdCptMin.getName()) || fieldName.equals(codeConfirmPwd)) {
				field = fieldGroup.buildAndBind(caption, fieldName, RequiredPasswordField.class);
				field.setRequired(true);
				field.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
				final RequiredPasswordField pwdField = (RequiredPasswordField) field;
				pwdField.addPwdValidation();
				pwdField.setValue(null);
			} else {
				field = fieldGroup.buildAndBind(caption, fieldName);
			}
			field.setWidth(100, Unit.PERCENTAGE);
			if (fieldName.equals(CompteMinima_.mailPersoCptMin.getName()) || fieldName.equals(codeConfirmMailPerso)) {
				field.addValidator(new EmailRFCValidator(applicationContext.getMessage("validation.error.mail", null, UI.getCurrent().getLocale())));
				if (fieldName.equals(codeConfirmMailPerso)) {
					field.setRequired(true);
					field.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
				}
			}
			formLayout.addComponent(field);
		}

		final RequiredTextField eMailField = ((RequiredTextField) fieldGroup.getField(CompteMinima_.mailPersoCptMin.getName()));
		final RequiredTextField eMailConfirmField = ((RequiredTextField) fieldGroup.getField(codeConfirmMailPerso));

		final RequiredPasswordField pwdField = ((RequiredPasswordField) fieldGroup.getField(CompteMinima_.pwdCptMin.getName()));
		final RequiredPasswordField pwdConfirmField = ((RequiredPasswordField) fieldGroup.getField(codeConfirmPwd));

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

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {
				/* Verif la confirmation de mail est égale au mail */
				if (StringUtils.isNotBlank(eMailField.getValue())
					&&
					StringUtils.isNotBlank(eMailConfirmField.getValue())
					&&
					eMailField.isValid()
					&& eMailConfirmField.isValid()
					&&
					!eMailConfirmField.getValue().equals(eMailField.getValue())) {
					Notification.show(applicationContext.getMessage("compteMinima.mail.confirm.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				/* Verif de l'adresse mail */
				if (candidatController.searchCptMinByEMail(eMailField.getValue()) != null) {
					Notification.show(applicationContext.getMessage("compteMinima.mail.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				/* Verif la confirmation de mdp est égale au mdp */
				if (pwdAsked &&
					StringUtils.isNotBlank(pwdField.getValue())
					&& StringUtils.isNotBlank(pwdConfirmField.getValue())
					&&
					pwdField.isValid()
					&& pwdConfirmField.isValid()
					&&
					!pwdField.getValue().equals(pwdConfirmField.getValue())) {
					Notification.show(applicationContext.getMessage("compteMinima.pwd.confirm.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la compteMinima saisie */
				if (compteMinimaWindowListener != null) {
					compteMinimaWindowListener.btnOkClick(compteMinima);
				}

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

	/**
	 * Défini le 'CompteMinimaWindowListener' utilisé
	 * @param compteMinimaWindowListener
	 */
	public void addCompteMinimaWindowListener(final CompteMinimaWindowListener compteMinimaWindowListener) {
		this.compteMinimaWindowListener = compteMinimaWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface CompteMinimaWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param cptMin
		 */
		void btnOkClick(CompteMinima cptMin);

	}
}
