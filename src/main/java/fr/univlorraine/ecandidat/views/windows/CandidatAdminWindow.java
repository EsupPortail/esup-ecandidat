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
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima_;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;

/**
 * Fenêtre d'édition de compte a minima par un admin
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CandidatAdminWindow extends Window {

	public static final String[] FIELDS_ORDER = { CompteMinima_.nomCptMin.getName(),
		CompteMinima_.prenomCptMin.getName(),
		CompteMinima_.mailPersoCptMin.getName(),
		CompteMinima_.loginCptMin.getName(),
		CompteMinima_.supannEtuIdCptMin.getName(),
		CompteMinima_.temValidCptMin.getName(),
		CompteMinima_.temValidMailCptMin.getName(),
		CompteMinima_.temFcCptMin.getName() };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;

	/* Composants */
	private CustomBeanFieldGroup<CompteMinima> fieldGroup;
	private CandidatAdminWindowListener candidatAdminWindowListener;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de cptMin
	 * @param cptMin la cptMin à éditer
	 */
	public CandidatAdminWindow(final CompteMinima cptMin) {
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
		setCaption(applicationContext.getMessage("candidat.admin.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(CompteMinima.class);
		fieldGroup.setItemDataSource(cptMin);
		final FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (final String fieldName : FIELDS_ORDER) {
			final String caption = applicationContext.getMessage("compteMinima.table." + fieldName, null, UI.getCurrent().getLocale());
			final Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			if (fieldName.equals(CompteMinima_.mailPersoCptMin.getName())) {
				field.addValidator(new EmailValidator(applicationContext.getMessage("validation.error.mail", null, UI.getCurrent().getLocale())));
			}
			formLayout.addComponent(field);
		}

		final RequiredTextField loginField = (RequiredTextField) fieldGroup.getField(CompteMinima_.loginCptMin.getName());
		final RequiredTextField etuIdField = (RequiredTextField) fieldGroup.getField(CompteMinima_.supannEtuIdCptMin.getName());

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
				/* Si le code existe dejà --> erreur */
				if (candidatController.isLoginPresent(loginField.getValue(), cptMin) || candidatController.isSupannEtuIdPresent(etuIdField.getValue(), cptMin)) {
					return;
				}

				/* Valide la saisie */
				fieldGroup.commit();

				/* Enregistre la cptMin saisie */
				candidatAdminWindowListener.btnOkClick(cptMin);

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
	 * Défini le 'CandidatAdminWindowListener' utilisé
	 * @param candidatAdminWindowListener
	 */
	public void addCandidatAdminWindowListener(final CandidatAdminWindowListener candidatAdminWindowListener) {
		this.candidatAdminWindowListener = candidatAdminWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface CandidatAdminWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param cptMin
		 */
		void btnOkClick(CompteMinima cptMin);

	}
}
