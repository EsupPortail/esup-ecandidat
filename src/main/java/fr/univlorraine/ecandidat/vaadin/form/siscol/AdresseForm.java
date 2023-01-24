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
package fr.univlorraine.ecandidat.vaadin.form.siscol;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;

/**
 * Layout de formulaire d'adresse
 * @author Kevin
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class AdresseForm extends VerticalLayout {

	public static final String[] FIELDS_ORDER = { Adresse_.siScolPays
		.getName(),
		Adresse_.codBdiAdr.getName(),
		Adresse_.siScolCommune.getName(),
		Adresse_.libComEtrAdr.getName(),
		Adresse_.adr1Adr.getName(),
		Adresse_.adr2Adr.getName(),
		Adresse_.adr3Adr.getName(),
		Adresse_.cedexAdr.getName() };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CacheController cacheController;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/**
	 * Crée une fenêtre d'édition d'adresse
	 * @param fieldGroupAdresse l'adresse à éditer
	 */
	public AdresseForm(final CustomBeanFieldGroup<Adresse> fieldGroupAdresse, final Boolean withCedex) {
		setSpacing(true);
		setSizeFull();

		final FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (final String fieldName : FIELDS_ORDER) {
			if (withCedex || (!withCedex && !fieldName.equals(Adresse_.cedexAdr.getName()))) {
				final String caption = applicationContext.getMessage("adresse." + fieldName, null, UI.getCurrent().getLocale());
				Field<?> field;
				if (fieldName.equals(Adresse_.codBdiAdr.getName())) {
					field = fieldGroupAdresse.buildAndBind(caption, fieldName, RequiredIntegerField.class);
					((RequiredIntegerField) field).setNullRepresentation("");
					((RequiredIntegerField) field).setMaxLength(5);
				} else {
					field = fieldGroupAdresse.buildAndBind(caption, fieldName);
				}
				field.setWidth(100, Unit.PERCENTAGE);
				formLayout.addComponent(field);
			}

		}

		initForm(fieldGroupAdresse);

		addComponent(formLayout);
	}

	/**
	 * Initialise le formulaire
	 * @param fieldGroupAdresse
	 */
	private void initForm(final CustomBeanFieldGroup<Adresse> fieldGroupAdresse) {
		final Adresse adresse = fieldGroupAdresse.getItemDataSource().getBean();

		/* Champs commune etrangere */
		final RequiredTextField adr1Adrfield = (RequiredTextField) fieldGroupAdresse.getField(Adresse_.adr1Adr.getName());
		changeRequired(adr1Adrfield, true);

		/* Champs commune */
		final ComboBoxCommune communeField = (ComboBoxCommune) fieldGroupAdresse.getField(Adresse_.siScolCommune.getName());
		/* Champs code postal */
		final RequiredIntegerField bdiField = (RequiredIntegerField) fieldGroupAdresse.getField(Adresse_.codBdiAdr.getName());
		/* Champs commune etrangere */
		final RequiredTextField communeEtrfield = (RequiredTextField) fieldGroupAdresse.getField(Adresse_.libComEtrAdr.getName());
		/* Champs pays */
		final ComboBoxPays paysField = (ComboBoxPays) fieldGroupAdresse.getField(Adresse_.siScolPays.getName());

		/* ajout des listeners */
		/* Champs code postal */
		bdiField.addValueChangeListener(event -> {
			final String val = (String) event.getProperty().getValue();
			initBdi(val, communeField, null);
		});
		/* Champs pays */
		paysField.addValueChangeListener(e -> {
			final SiScolPays pays = (SiScolPays) e.getProperty().getValue();
			initPays(pays, communeField, bdiField, communeEtrfield);
		});

		if (adresse.getIdAdr() == null || adresse.getSiScolPays() == null) {
			paysField.setValue(cacheController.getPaysFrance());
		} else {
			paysField.setValue(adresse.getSiScolPays());
			initPays(adresse.getSiScolPays(), communeField, bdiField, communeEtrfield);
		}

		if (adresse.getIdAdr() != null && adresse.getCodBdiAdr() != null) {
			initBdi(adresse.getCodBdiAdr(), communeField, adresse.getSiScolCommune() != null ? adresse.getSiScolCommune() : null);
		} else {
			initBdi(null, communeField, null);
		}
	}

	/**
	 * Initialise les champs lors du changement de code postal
	 * @param valBdi
	 * @param communeField
	 * @param commune
	 */
	private void initBdi(final String valBdi, final ComboBoxCommune communeField, final SiScolCommune commune) {
		try {
			communeField.setValue(null);
			communeField.setListCommune(null);

			Integer.valueOf(valBdi);
			if (valBdi.length() == 5) {
				final List<SiScolCommune> listeCommune = tableRefController.listeCommuneByCodePostal(valBdi).stream().filter(e -> e.getTemEnSveCom()).collect(Collectors.toList());
				if (listeCommune.size() > 0) {
					communeField.setListCommune(listeCommune);
					communeField.setEnabled(true);
					if (listeCommune.size() == 1) {
						communeField.setValue(listeCommune.get(0));
					}
					if (commune != null) {
						communeField.setValue(commune);
					}
				} else {
					communeField.setEnabled(false);
				}
			} else {
				communeField.setEnabled(false);
			}
		} catch (final Exception e) {
			communeField.setEnabled(false);
		}
	}

	/**
	 * Initialise les champs lors du changement de pays
	 * @param pays
	 * @param communeField
	 * @param bdiField
	 * @param communeEtrfield
	 */
	private void initPays(final SiScolPays pays, final ComboBoxCommune communeField, final RequiredIntegerField bdiField, final RequiredTextField communeEtrfield) {
		if (pays != null && pays.getId().getCodPay().equals(siScolService.getCodPaysFrance())) {
			changeRequired(bdiField, true);
			bdiField.setVisible(true);
			bdiField.setConversionError(applicationContext.getMessage("validation.codpostal", null, UI.getCurrent().getLocale()));
			changeRequired(communeField, true);
			communeField.setVisible(true);
			communeEtrfield.setValue(null);
			changeRequired(communeEtrfield, false);
			communeEtrfield.setVisible(false);
		} else {
			changeRequired(bdiField, false);
			bdiField.setVisible(false);
			bdiField.setValue(null);
			bdiField.setConversionError(null);
			changeRequired(communeField, false);
			communeField.setValue(null);
			communeField.setListCommune(null);
			communeField.setVisible(false);
			changeRequired(communeEtrfield, true);
			communeEtrfield.setVisible(true);
		}
	}

	/**
	 * Change l'etat obligatoire d'un champs
	 * @param field
	 * @param isRequired
	 */
	private void changeRequired(final Field<?> field, final Boolean isRequired) {
		field.setRequired(isRequired);
		if (isRequired) {
			field.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
		} else {
			field.setRequiredError(null);
		}
	}
}
