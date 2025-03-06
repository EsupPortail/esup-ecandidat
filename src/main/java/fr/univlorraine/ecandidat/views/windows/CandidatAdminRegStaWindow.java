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
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolRegime;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolStatut;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * Fenêtre d'édition de compte a minima par un admin
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CandidatAdminRegStaWindow extends Window {

	public static final String[] FIELDS_ORDER = { Candidat_.siScolRegime.getName(), Candidat_.siScolStatut.getName() };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;

	/* Composants */
	private final CustomBeanFieldGroup<Candidat> fieldGroup;
	private CandidatAdminRegStaWindowListener candidatAdminRegStaWindowListener;
	private final OneClickButton btnEnregistrer;
	private final OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de candidat pour éditer ses statuts
	 * @param cptMin la cptMin à éditer
	 */
	@SuppressWarnings("unchecked")
	public CandidatAdminRegStaWindow(final Candidat candidat) {
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
		fieldGroup = new CustomBeanFieldGroup<>(Candidat.class);
		fieldGroup.setItemDataSource(candidat);
		final FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (final String fieldName : FIELDS_ORDER) {
			final String caption = applicationContext.getMessage("candidat.admin.regStu." + fieldName, null, UI.getCurrent().getLocale());
			final Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			if (fieldName.equals(Candidat_.siScolRegime.getName())) {
				((RequiredComboBox<SiScolRegime>) field).setNullSelectionAllowed(true);
			} else if (fieldName.equals(Candidat_.siScolStatut.getName())) {
				((RequiredComboBox<SiScolStatut>) field).setNullSelectionAllowed(true);
			}
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

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {
				/* Valide la saisie */
				fieldGroup.commit();

				/* Enregistre la cptMin saisie */
				candidatAdminRegStaWindowListener.btnOkClick(candidat);

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
	 * Défini le 'CandidatAdminRegStaWindowListener' utilisé
	 * @param candidatAdminRegStaWindowListener
	 */
	public void addCandidatAdminRegStaWindowListener(final CandidatAdminRegStaWindowListener candidatAdminRegStaWindowListener) {
		this.candidatAdminRegStaWindowListener = candidatAdminRegStaWindowListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface CandidatAdminRegStaWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param cptMin
		 */
		void btnOkClick(Candidat candidat);

	}
}
