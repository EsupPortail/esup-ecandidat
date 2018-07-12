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
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.BatchController;
import fr.univlorraine.ecandidat.entities.ecandidat.Batch;
import fr.univlorraine.ecandidat.entities.ecandidat.Batch_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.LocalTimeField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredCheckBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxJourMoisAnnee;

/** Fenêtre d'édition de batch
 *
 * @author Kevin Hergalant */
@Configurable(preConstruction = true)
public class AdminBatchWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -8212886557264076581L;

	public static final String[] BATCH_FIELDS_ORDER = {Batch_.codBatch.getName(), Batch_.tesBatch.getName(), Batch_.temFrequenceBatch.getName(), Batch_.frequenceBatch.getName(),
			Batch_.fixeHourBatch.getName(), Batch_.fixeDayBatch.getName(), Batch_.fixeMonthBatch.getName(), Batch_.fixeYearBatch.getName(),
			Batch_.temLundiBatch.getName(), Batch_.temMardiBatch.getName(), Batch_.temMercrBatch.getName(), Batch_.temJeudiBatch.getName(), Batch_.temVendrediBatch.getName(),
			Batch_.temSamediBatch.getName(), Batch_.temDimanBatch.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient BatchController batchController;

	/* Composants */
	private CustomBeanFieldGroup<Batch> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/* composant frequence */
	RequiredCheckBox rcbTemFrequence;
	RequiredIntegerField rifFrequence;
	LocalTimeField ltfHour;

	/** Crée une fenêtre d'édition de batch
	 *
	 * @param batch
	 *            le batch à éditer */
	public AdminBatchWindow(final Batch batch) {
		/* Style */
		setModal(true);
		setWidth(600, Unit.PIXELS);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("batch.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(Batch.class);
		fieldGroup.setItemDataSource(batch);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : BATCH_FIELDS_ORDER) {
			String caption = applicationContext.getMessage("batch.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field;
			if (fieldName.equals(Batch_.fixeMonthBatch.getName())) {
				field = fieldGroup.buildAndBind(caption, fieldName, ComboBoxJourMoisAnnee.class);
				((ComboBoxJourMoisAnnee) field).changeTypeNativeSelect(ConstanteUtils.TYPE_MOIS);
			} else if (fieldName.equals(Batch_.fixeDayBatch.getName())) {
				field = fieldGroup.buildAndBind(caption, fieldName, ComboBoxJourMoisAnnee.class);
				((ComboBoxJourMoisAnnee) field).changeTypeNativeSelect(ConstanteUtils.TYPE_JOUR);
			} else if (fieldName.equals(Batch_.fixeYearBatch.getName())) {
				field = fieldGroup.buildAndBind(caption, fieldName, ComboBoxJourMoisAnnee.class);
				((ComboBoxJourMoisAnnee) field).changeTypeNativeSelect(ConstanteUtils.TYPE_ANNEE);
			} else {
				field = fieldGroup.buildAndBind(caption, fieldName);
			}

			if (fieldName.equals(Batch_.fixeHourBatch.getName())) {
				field.setSizeUndefined();

			} else if (fieldName.equals(Batch_.frequenceBatch.getName())) {
				field.setWidth(70, Unit.PIXELS);

			} else {
				field.setWidth(100, Unit.PERCENTAGE);
			}
			formLayout.addComponent(field);
		}

		fieldGroup.getField(Batch_.codBatch.getName()).setReadOnly(true);

		/* Mise a jour des champs frequence */
		rcbTemFrequence = (RequiredCheckBox) fieldGroup.getField(Batch_.temFrequenceBatch.getName());
		rifFrequence = (RequiredIntegerField) fieldGroup.getField(Batch_.frequenceBatch.getName());
		ltfHour = (LocalTimeField) fieldGroup.getField(Batch_.fixeHourBatch.getName());
		ltfHour.setHeightLayout(35);
		rcbTemFrequence.addValueChangeListener(e -> {
			majFrequence();
		});
		majFrequence();

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
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre le batch saisi */
				batchController.saveBatch(batch);
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

	/** Mise a jour champs frequence */
	private void majFrequence() {
		if (rcbTemFrequence.getValue()) {
			rifFrequence.setVisible(true);
			ltfHour.setVisible(false);
		} else {
			try {
				rifFrequence.validate();
			} catch (Exception e) {
				rifFrequence.setValue("0");
			}
			rifFrequence.setVisible(false);
			ltfHour.setVisible(true);
		}
	}

}
