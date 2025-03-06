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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.vaadin.components.CustomPanel;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredPasswordField;

/**
 * Fenêtre d'édition de compte a minima
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CandidatCptMinPwdWindow extends Window {

	private static final String codeConfirmPwd = "confirmPwdCptMin";
	private static final String codeOldPwdCptMin = "oldPwdCptMin";

	public String[] FIELDS_ORDER = { codeOldPwdCptMin, CompteMinima_.pwdCptMin.getName(), codeConfirmPwd };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;

	/* Composants */
	private CompteMinimaWindowListener compteMinimaWindowListener;
	private CustomBeanFieldGroup<CompteMinima> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de compteMinima
	 * @param compteMinima la compteMinima à éditer
	 */
	public CandidatCptMinPwdWindow(final CompteMinima compteMinima) {
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
		setCaption(applicationContext.getMessage("compteMinima.editpwd.title", null, UI.getCurrent().getLocale()));

		/* Panel d'infos mot de passe */
		final CustomPanel panelInfo =
			new CustomPanel(applicationContext.getMessage("compteMinima.info.pwd.title", null, UI.getCurrent().getLocale()), applicationContext.getMessage("compteMinima.info.pwd", null, UI.getCurrent().getLocale()),
				FontAwesome.INFO_CIRCLE);
		panelInfo.setWidthMax();
		panelInfo.setMargin(true);
		panelInfo.addLabelStyleName(ValoTheme.LABEL_TINY);
		layout.addComponent(panelInfo);

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(CompteMinima.class);
		fieldGroup.setItemDataSource(compteMinima);
		final FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (final String fieldName : FIELDS_ORDER) {
			final String caption = applicationContext.getMessage("compteMinima.table." + fieldName, null, UI.getCurrent().getLocale());
			final Field<?> field = fieldGroup.buildAndBind(caption, fieldName, RequiredPasswordField.class);
			field.setRequired(true);
			field.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
			field.setWidth(100, Unit.PERCENTAGE);
			formLayout.addComponent(field);
		}

		/* Recupération de l'ancien pwd */
		final String oldPwd = compteMinima.getPwdCptMin();

		final RequiredPasswordField pwdOldField = ((RequiredPasswordField) fieldGroup.getField(codeOldPwdCptMin));
		final RequiredPasswordField pwdField = ((RequiredPasswordField) fieldGroup.getField(CompteMinima_.pwdCptMin.getName()));
		if (pwdField != null) {
			pwdField.addPwdValidation();
			pwdField.setValue(null);
		}

		final RequiredPasswordField pwdConfirmField = ((RequiredPasswordField) fieldGroup.getField(codeConfirmPwd));
		if (pwdConfirmField != null) {
			pwdConfirmField.addPwdValidation();
			pwdConfirmField.setValue(null);
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

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {
				/* Vérification password vide */
				if (StringUtils.isBlank(pwdOldField.getValue()) || StringUtils.isBlank(pwdField.getValue()) || StringUtils.isBlank(pwdConfirmField.getValue())) {
					Notification.show(applicationContext.getMessage("compteMinima.pwd.empty", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				/* Verif de l'ancien mot de passe */
				if (!candidatController.verifMdp(oldPwd, pwdOldField.getValue())) {
					Notification.show(applicationContext.getMessage("compteMinima.pwd.oldNotEqual", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				/* Verif la confirmation de mdp est égale au mdp */
				if (pwdField.isValid()
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
