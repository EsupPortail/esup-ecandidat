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
package fr.univlorraine.ecandidat.vaadin.form;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.MailController;
import fr.univlorraine.ecandidat.controllers.MotivationAvisController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxCommission;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxJourMoisAnnee;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxMail;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxMotivationAvis;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxPresentation;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxTypeDecision;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxTypeTraitement;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nField;

/**
 * FieldGroupFactory utilisé dans l'application
 * Permet d'utiliser le bon composant pour le bon type
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CustomFieldGroupFieldFactory extends DefaultFieldGroupFieldFactory {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient MotivationAvisController motivationAvisController;
	@Resource
	private transient MailController mailController;

	@SuppressWarnings("rawtypes")
	@Override
	public <T extends Field> T createField(final Class<?> dataType, final Class<T> fieldType) {

		/* Le type du champs est un NativeSelectJourMoisAnnee */
		if (fieldType == ComboBoxJourMoisAnnee.class) {
			return fieldType.cast(new ComboBoxJourMoisAnnee());
		}

		/* Le type du champs est un SearchAnneeUnivApoField */
		else if (fieldType == SearchAnneeUnivApoField.class) {
			return fieldType.cast(new SearchAnneeUnivApoField(applicationContext.getMessage("btnFind", null, UI.getCurrent().getLocale())));
		}

		/* Le type du champs est un RequiredColorPickerField */
		else if (fieldType == RequiredColorPickerField.class) {
			return fieldType.cast(new RequiredColorPickerField(applicationContext.getMessage("alertSva.caption.value", null, UI.getCurrent().getLocale())));
		}

		/* Le type du champs est un ComboBoxPresentation */
		else if (fieldType == ComboBoxPresentation.class) {
			return fieldType.cast(new ComboBoxPresentation());
		}

		/* Le type du champs est un TextArea */
		else if (fieldType == RequiredTextArea.class) {
			return fieldType.cast(new RequiredTextArea());
		}

		/* Le type du champs est un ComboBoxTypeDecision-->utilise pour afficher tout les types de decsion et pas uniquement les favorables */
		else if (fieldType == ComboBoxTypeDecision.class) {
			return fieldType
					.cast(new ComboBoxTypeDecision(typeDecisionController.getTypeDecisionsEnService(), applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale())));
		}

		/*
		 * La valeur est i18n
		 * else if (fieldType==I18nField.class){
		 * return fieldType.cast(new I18nField(nomenclatureController.getLangueDefault()));
		 * }
		 */

		/* Le type du champs est un Boolean (param) */
		else if (fieldType == RequiredStringCheckBox.class) {
			return fieldType.cast(new RequiredStringCheckBox());
		}

		/* La valeur du champs est un LocalDate */
		else if (dataType == LocalDate.class) {
			final RequiredDateField field = new RequiredDateField();
			field.setImmediate(true);
			field.setConverter(new LocalDateToDateConverter());
			return fieldType.cast(field);
		}

		/* La valeur du champs est un LocalTime */
		else if (dataType == LocalTime.class) {
			return fieldType.cast(new LocalTimeField());
		}

		/* La valeur du champs est un LocalDateTime */
		else if (dataType == LocalDateTime.class) {
			return fieldType.cast(new LocalDateTimeField());
		}

		/* La valeur du champs est un Integer */
		else if (dataType == Integer.class) {
			return fieldType.cast(new RequiredIntegerField());
		}

		/* La valeur du champs est une date */
		else if (dataType == Date.class) {
			return fieldType.cast(new RequiredDateField());
		}

		/* La valeur du champs est un Boolean */
		else if (dataType == Boolean.class) {
			return fieldType.cast(new RequiredCheckBox());
		}

		/* La valeur est i18n */
		else if (dataType == I18n.class) {
			return fieldType.cast(new I18nField(cacheController.getLangueDefault(), cacheController.getLangueEnServiceWithoutDefault(),
					applicationContext.getMessage("btnI18nLng", null, UI.getCurrent().getLocale())));
		}
		/* La valeur est un type d'avis */
		else if (dataType == TypeAvis.class) {
			return fieldType.cast(new RequiredComboBox<>(cacheController.getListeTypeAvis(), TypeAvis.class));
		}
		/* La valeur est un mail */
		else if (dataType == Mail.class) {
			return fieldType.cast(new ComboBoxMail(mailController.getMailsTypeAvis()));
		}
		/* La valeur est un type de decision */
		else if (dataType == TypeDecision.class) {
			return fieldType.cast(
					new ComboBoxTypeDecision(typeDecisionController.getTypeDecisionsFavorableEnService(), applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale())));
		}
		/* La valeur est une Motivation d'Avis */
		else if (dataType == MotivationAvis.class) {
			return fieldType
					.cast(new ComboBoxMotivationAvis(motivationAvisController.getMotivationAvisEnService(), applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale())));
		}
		/* La valeur est un type de diplome */
		else if (dataType == SiScolTypDiplome.class) {
			return fieldType.cast(new RequiredComboBox<>(cacheController.getListeTypDiplome(), SiScolTypDiplome.class));
		}
		/* La valeur est un type de traitement */
		else if (dataType == TypeTraitement.class) {
			return fieldType.cast(new ComboBoxTypeTraitement(cacheController.getListeTypeTraitement()));
		}
		/* La valeur est un CGE */
		else if (dataType == SiScolCentreGestion.class) {
			return fieldType.cast(new RequiredComboBox<>(cacheController.getListeCentreGestion(), SiScolCentreGestion.class));
		}
		/* La valeur est un type de traitement */
		else if (dataType == Commission.class) {
			return fieldType.cast(new ComboBoxCommission());
		}
		/* La valeur est un type de statut de pj */
		else if (dataType == TypeStatutPiece.class) {
			return fieldType.cast(new RequiredComboBox<>(tableRefController.getListeTypeStatutPieceActif(), TypeStatutPiece.class));
		}
		/* La valeur est un type de statut de dossier */
		else if (dataType == TypeStatut.class) {
			return fieldType.cast(new RequiredComboBox<>(cacheController.getListeTypeStatut(), TypeStatut.class));
		}
		/* Sinon, le champs est un simple TextField */
		else {
			return fieldType.cast(new RequiredTextField());
		}
	}

}
