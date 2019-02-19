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

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.ParametrePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredStringCheckBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextArea;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxPresentation;

/**
 * Fenêtre d'édition de parametre
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class ParametreWindow extends Window {

	public static final String[] FIELDS_ORDER_STRING = {Parametre_.codParam.getName(), Parametre_.libParam.getName(), ParametrePresentation.VAL_PARAM_STRING};
	public static final String[] FIELDS_ORDER_BOOLEAN = {Parametre_.codParam.getName(), Parametre_.libParam.getName(), ParametrePresentation.VAL_PARAM_BOOLEAN};
	public static final String[] FIELDS_ORDER_INTEGER = {Parametre_.codParam.getName(), Parametre_.libParam.getName(), ParametrePresentation.VAL_PARAM_INTEGER};
	public String[] FIELDS_ORDER;

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient ParametreController parametreController;

	/* Composants */
	private CustomBeanFieldGroup<ParametrePresentation> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de parametre
	 *
	 * @param parametre
	 *            la parametre à éditer
	 */
	public ParametreWindow(final Parametre parametre) {
		ParametrePresentation parametrePres = new ParametrePresentation(parametre);
		if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_BOOLEAN)) {
			FIELDS_ORDER = FIELDS_ORDER_BOOLEAN;
		} else if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_INTEGER)) {
			FIELDS_ORDER = FIELDS_ORDER_INTEGER;
		} else if (parametre.getTypParam().startsWith(NomenclatureUtils.TYP_PARAM_STRING)) {
			FIELDS_ORDER = FIELDS_ORDER_STRING;
		}
		/* Style */
		setModal(true);
		setWidth(500, Unit.PIXELS);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("parametre.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(ParametrePresentation.class);
		fieldGroup.setItemDataSource(parametrePres);
		FormLayout formLayout = new FormLayout();
		formLayout.setWidth(100, Unit.PERCENTAGE);
		formLayout.setSpacing(true);
		for (String fieldName : FIELDS_ORDER) {
			String caption = applicationContext.getMessage("parametre.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field = null;
			if (fieldName.equals(ParametrePresentation.VAL_PARAM_BOOLEAN)) {
				field = fieldGroup.buildAndBind(caption, fieldName, RequiredStringCheckBox.class);
			} else if (fieldName.equals(ParametrePresentation.VAL_PARAM_STRING) && parametrePres.getRegexParam() != null) {
				field = fieldGroup.buildAndBind(caption, fieldName, ComboBoxPresentation.class);
				ComboBoxPresentation cbPres = (ComboBoxPresentation) field;
				cbPres.setListe(parametreController.getListeRegex(parametrePres.getRegexParam()));
				cbPres.setCodeValue(parametre.getValParam());
			} else if (fieldName.equals(Parametre_.libParam.getName())) {
				field = fieldGroup.buildAndBind(caption, fieldName, RequiredTextArea.class);
				field.setWidth(100, Unit.PERCENTAGE);
			} else {
				field = fieldGroup.buildAndBind(caption, fieldName);
				field.setWidth(100, Unit.PERCENTAGE);
				if (fieldName.equals(ParametrePresentation.VAL_PARAM_STRING)) {
					((RequiredTextField) field).setNullRepresentation(null);
					Integer tailleMax = parametreController.getMaxLengthForString(parametre.getTypParam());
					field.addValidator(
							new StringLengthValidator(applicationContext.getMessage("parametre.taillemax.error", new Object[] {0, tailleMax}, UI.getCurrent().getLocale()), 0, tailleMax, true));
				} else if (fieldName.equals(ParametrePresentation.VAL_PARAM_INTEGER) && parametrePres.getCodParam().equals(NomenclatureUtils.COD_PARAM_FILE_MAX_SIZE)) {
					field.addValidator(value -> {
						if (value == null) {
							return;
						}
						Integer integerValue = null;
						try {
							integerValue = Integer.valueOf(value.toString());
						} catch (Exception e) {
							throw new InvalidValueException("");
						}
						Integer maxValue = ConstanteUtils.SIZE_MAX_PARAM_MAX_FILE_PJ;
						if (value != null && integerValue > maxValue) {
							throw new InvalidValueException(applicationContext.getMessage("parametre.taillemax.int.error", new Object[] {maxValue}, UI.getCurrent().getLocale()));
						}
					});
				}
			}
			formLayout.addComponent(field);
		}

		fieldGroup.getField(Parametre_.codParam.getName()).setReadOnly(true);
		fieldGroup.getField(Parametre_.libParam.getName()).setReadOnly(true);

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
				/* Enregistre le parametre saisie */
				parametreController.saveParametre(parametre, parametrePres);
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
