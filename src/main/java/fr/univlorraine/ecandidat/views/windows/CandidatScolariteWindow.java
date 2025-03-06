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

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.ui.Field;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxCommune;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxDepartement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxEtablissement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxPays;

/**
 * Fenêtre d'édition de parcours scolaire (bac, cursus, etc..), utilisé pour factoriser la saisie d'adresse
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CandidatScolariteWindow extends Window {

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
	 * Crée une fenêtre d'édition de parcours scolaire
	 */
	public CandidatScolariteWindow() {
		/* Style */
		setModal(true);
		setWidth(550, Unit.PIXELS);
		setResizable(true);
		setClosable(true);
	}

	/**
	 * initialise le formulaire
	 * @param comboBoxPays
	 * @param comboBoxDepartement
	 * @param comboBoxCommune
	 * @param comboBoxEtablissement
	 * @param fieldAnneeObt
	 * @param pays
	 * @param departement
	 * @param commune
	 * @param etablissement
	 * @param anneeToCompare
	 */
	protected void initForm(final ComboBoxPays comboBoxPays,
		final ComboBoxDepartement comboBoxDepartement,
		final ComboBoxCommune comboBoxCommune,
		final ComboBoxEtablissement comboBoxEtablissement,
		final RequiredIntegerField fieldAnneeObt,
		final SiScolPays pays,
		final SiScolDepartement departement,
		final SiScolCommune commune,
		final SiScolEtablissement etablissement,
		final Integer anneeToCompare) {
		/* Champs d'année */
		//Integer anneeN1 = LocalDate.now().getYear()+1;
		final String conversionError = applicationContext.getMessage("validation.parse.annee", null, UI.getCurrent().getLocale());
		fieldAnneeObt.setConversionError(conversionError);
		fieldAnneeObt.removeAllValidators();
		fieldAnneeObt.addValidator(value -> {
			if (value == null) {
				return;
			}
			Integer integerValue = null;
			try {
				integerValue = Integer.valueOf(value.toString());
			} catch (final Exception e) {
				throw new InvalidValueException(conversionError);
			}
			if (value != null && (integerValue < 1900 || integerValue > anneeToCompare)) {
				throw new InvalidValueException(conversionError);
			}
		});
		fieldAnneeObt.setMaxLength(4);

		/* ajout des listeners */
		/* Champs pays */
		comboBoxPays.addValueChangeListener(e -> {
			final SiScolPays paysBox = (SiScolPays) e.getProperty().getValue();
			initPays(paysBox, comboBoxDepartement, comboBoxCommune, comboBoxEtablissement);
		});
		/* champs departement */
		comboBoxDepartement.addValueChangeListener(e -> {
			final SiScolDepartement departementBox = (SiScolDepartement) e.getProperty().getValue();
			initDepartement(departementBox, comboBoxCommune);
		});

		/* champs departement */
		comboBoxCommune.addValueChangeListener(e -> {
			final SiScolCommune communeBox = (SiScolCommune) e.getProperty().getValue();
			initCommune(communeBox, comboBoxEtablissement);
		});

		/* init pays */
		if (pays == null) {
			comboBoxPays.setValue(cacheController.getPaysFrance());
		} else {
			comboBoxPays.setValue(pays);
			initPays(pays, comboBoxDepartement, comboBoxCommune, comboBoxEtablissement);
			/* init dept */
			if (departement != null) {
				comboBoxDepartement.setValue(departement);
				initDepartement(departement, comboBoxCommune);
				/* init commune */
				if (commune != null) {
					comboBoxCommune.setValue(commune);
					initCommune(commune, comboBoxEtablissement);
					/* init etablissement */
					if (etablissement != null) {
						comboBoxEtablissement.setValue(etablissement);
					}
				}
			}
		}
	}

	/**
	 * Initialise les champs lors du changement de pays
	 * @param pays
	 * @param comboBoxDepartement
	 * @param comboBoxCommune
	 * @param comboBoxEtablissement
	 */
	private void initPays(final SiScolPays pays, final ComboBoxDepartement comboBoxDepartement, final ComboBoxCommune comboBoxCommune, final ComboBoxEtablissement comboBoxEtablissement) {
		if (pays != null && pays.getId().getCodPay().equals(siScolService.getCodPaysFrance())) {
			changeRequired(comboBoxDepartement, true);
			comboBoxDepartement.setVisible(true);
			comboBoxDepartement.setEnabled(true);

			changeRequired(comboBoxCommune, true);
			comboBoxCommune.setVisible(true);
			comboBoxCommune.setEnabled(false);

			changeRequired(comboBoxEtablissement, true);
			comboBoxEtablissement.setVisible(true);
			comboBoxEtablissement.setEnabled(false);
		} else {
			changeRequired(comboBoxDepartement, false);
			comboBoxDepartement.setVisible(false);
			comboBoxDepartement.setValue(null);

			changeRequired(comboBoxCommune, false);
			comboBoxCommune.setVisible(false);
			comboBoxCommune.setValue(null);

			changeRequired(comboBoxEtablissement, false);
			comboBoxEtablissement.setVisible(false);
			comboBoxCommune.setValue(null);
		}
	}

	/**
	 * Initialise les champs lors du changement de departement
	 * @param siScolDepartement
	 * @param communeField
	 */
	private void initDepartement(final SiScolDepartement siScolDepartement, final ComboBoxCommune communeField) {
		communeField.setValue(null);
		communeField.setListCommune(null);
		if (siScolDepartement == null) {
			communeField.setEnabled(false);
			return;
		}

		final List<SiScolCommune> listeCommune = tableRefController.listeCommuneByDepartement(siScolDepartement).stream().filter(e -> e.getTemEnSveCom()).collect(Collectors.toList());
		if (listeCommune.size() > 0) {
			communeField.setListCommune(listeCommune);
			communeField.setEnabled(true);
			if (listeCommune.size() == 1) {
				communeField.setValue(listeCommune.get(0));
			}
		} else {
			communeField.setEnabled(false);
		}
	}

	/**
	 * Initialise les champs lors du changement de commune
	 * @param commune
	 * @param comboBoxEtablissement
	 */
	private void initCommune(final SiScolCommune commune, final ComboBoxEtablissement comboBoxEtablissement) {
		comboBoxEtablissement.setValue(null);
		comboBoxEtablissement.setListEtablissement(null);
		if (commune == null) {
			comboBoxEtablissement.setEnabled(false);
			return;
		}

		final List<SiScolEtablissement> listeEtab = tableRefController.listeEtablissementByCommuneEnService(commune);
		if (listeEtab.size() > 0) {
			comboBoxEtablissement.setListEtablissement(listeEtab);
			comboBoxEtablissement.setEnabled(true);
			if (listeEtab.size() == 1) {
				comboBoxEtablissement.setValue(listeEtab.get(0));
			}
		} else {
			comboBoxEtablissement.setEnabled(false);
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
