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

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.FormationController;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredDateField;

/**
 * Fenêtre d'édition de formation
 * 
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction = true)
public class CtrCandFormationDatesWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -1967836926575353048L;

	public static final String[] FIELDS_ORDER = { Formation_.tesForm.getName(), Formation_.datDebDepotForm.getName(),
			Formation_.datFinDepotForm.getName(), Formation_.datAnalyseForm.getName(),
			Formation_.datRetourForm.getName(), Formation_.datJuryForm.getName(), Formation_.datPubliForm.getName(),
			Formation_.datConfirmForm.getName() };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormationController formationController;

	/* Composants */
	private CustomBeanFieldGroup<Formation> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;
	private Label labelErrorDate = new Label();

	/**
	 * Crée une fenêtre d'édition de formation
	 * 
	 * @param formation
	 *            la formation à éditer
	 * @param formations
	 */
	public CtrCandFormationDatesWindow(Formation formation, List<Formation> formations) {

		/* Style */
		setModal(true);
		setWidth(700, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(new MarginInfo(false, true, true, true));
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("formation.dates.window", null, UI.getCurrent().getLocale()));

		/* FieldGroup */
		fieldGroup = new CustomBeanFieldGroup<>(Formation.class);
		fieldGroup.setItemDataSource(formation);

		/* Layout des dates */
		VerticalLayout vlDate = new VerticalLayout();
		if (formations.size() > 1) {
			Label labelInfo = new Label(
					applicationContext.getMessage("formation.dates.info", null, UI.getCurrent().getLocale()));
			labelInfo.addStyleName(ValoTheme.LABEL_TINY);
			labelInfo.addStyleName(StyleConstants.LABEL_ITALIC);
			vlDate.addComponent(labelInfo);
		}

		labelErrorDate.addStyleName(ValoTheme.LABEL_FAILURE);
		labelErrorDate.setContentMode(ContentMode.HTML);
		labelErrorDate.setVisible(false);
		vlDate.addComponent(labelErrorDate);

		FormLayout layoutParamDate = new FormLayout();
		layoutParamDate.setSizeFull();
		layoutParamDate.setSpacing(true);
		vlDate.addComponent(layoutParamDate);
		layout.addComponent(vlDate);

		for (String fieldName : FIELDS_ORDER) {
			String caption = applicationContext.getMessage("formation.table." + fieldName, null,
					UI.getCurrent().getLocale());
			Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			layoutParamDate.addComponent(field);
		}

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()),
				FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()),
				FontAwesome.SAVE);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage(
					"formation.dates.window.confirm", new Object[] { formations.size() }, UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(c -> {
				try {
					fieldGroup.preCommit();
					if (!fieldGroup.isValid()) {
						return;
					}
					String validationDate = getErrorMessageDate();
					if (validationDate != null && !validationDate.equals("")) {
						labelErrorDate.setValue(validationDate);
						labelErrorDate.setVisible(true);
						return;
					}
					/* Valide la saisie */
					fieldGroup.commit();
					/* Enregistre la mail saisie */
					formationController.saveDatesFormation(formation, formations);
					/* Ferme la fenêtre */
					close();
				} catch (CommitException ce) {
				}
			});
			UI.getCurrent().addWindow(confirmWindow);
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * @return true si les dates sont valides
	 */
	private String getErrorMessageDate() {
		RequiredDateField fieldDatConfirm = ((RequiredDateField) fieldGroup
				.getField(Formation_.datConfirmForm.getName()));
		RequiredDateField fieldDatDebDepot = ((RequiredDateField) fieldGroup
				.getField(Formation_.datDebDepotForm.getName()));
		RequiredDateField fieldDatAnalyse = ((RequiredDateField) fieldGroup
				.getField(Formation_.datAnalyseForm.getName()));
		RequiredDateField fieldDatFinDepo = ((RequiredDateField) fieldGroup
				.getField(Formation_.datFinDepotForm.getName()));
		RequiredDateField fieldDatJury = ((RequiredDateField) fieldGroup.getField(Formation_.datJuryForm.getName()));
		RequiredDateField fieldDatPubli = ((RequiredDateField) fieldGroup.getField(Formation_.datPubliForm.getName()));
		RequiredDateField fieldDatRetour = ((RequiredDateField) fieldGroup
				.getField(Formation_.datRetourForm.getName()));

		Date datConfirm = fieldDatConfirm.getValue();
		Date datDebDepot = fieldDatDebDepot.getValue();
		Date datAnalyse = fieldDatAnalyse.getValue();
		Date datFinDepo = fieldDatFinDepo.getValue();
		Date datJury = fieldDatJury.getValue();
		Date datPubli = fieldDatPubli.getValue();
		Date datRetour = fieldDatRetour.getValue();

		String txtError = "";
		/* Date de fin de dépôt des voeux >= Date de début de dépôt des voeux */
		if (datFinDepo.before(datDebDepot)) {
			txtError = txtError + getErrorMessageDate(txtError, Formation_.datFinDepotForm.getName(),
					Formation_.datDebDepotForm.getName());
		}

		/* Date préanalyse >= Date de fin de dépôt des voeux */
		if (datAnalyse != null && datAnalyse.before(datFinDepo)) {
			txtError = txtError + getErrorMessageDate(txtError, Formation_.datAnalyseForm.getName(),
					Formation_.datFinDepotForm.getName());
		}

		/* Date limite de retour de dossier >= Date de fin de dépôt des voeux */
		if (datRetour.before(datFinDepo)) {
			txtError = txtError + getErrorMessageDate(txtError, Formation_.datRetourForm.getName(),
					Formation_.datFinDepotForm.getName());
		}

		/* Date de jury >= Date limite de retour de dossier */
		if (datJury != null && datJury.before(datRetour)) {
			txtError = txtError + getErrorMessageDate(txtError, Formation_.datJuryForm.getName(),
					Formation_.datRetourForm.getName());
		}

		/* Date de publication des résultats >= Date de jury */
		if (datPubli != null) {
			if (datJury != null) {
				if (datPubli.before(datJury)) {
					txtError = txtError + getErrorMessageDate(txtError, Formation_.datPubliForm.getName(),
							Formation_.datJuryForm.getName());
				}
			} else {
				/*
				 * Date de jury a null-->on verifie qu'elle est superieur a la date de retour
				 * Date de publication des résultats >= Date limite de retour de dossier
				 */
				if (datPubli.before(datRetour)) {
					txtError = txtError + getErrorMessageDate(txtError, Formation_.datPubliForm.getName(),
							Formation_.datRetourForm.getName());
				}
			}
		}

		/* Date limite de confirmation >= Date de publication des résultats */
		if (datConfirm != null) {
			if (datPubli != null) {
				if (datConfirm.before(datPubli)) {
					txtError = txtError + getErrorMessageDate(txtError, Formation_.datConfirmForm.getName(),
							Formation_.datPubliForm.getName());
				}
			}
			/*
			 * Date de publi a null-->on verifie qu'elle est superieur a la date de jury
			 * Date limite de confirmation >= Date de jury
			 **/
			else if (datJury != null) {
				if (datConfirm.before(datJury)) {
					txtError = txtError + getErrorMessageDate(txtError, Formation_.datConfirmForm.getName(),
							Formation_.datJuryForm.getName());
				}
			} else {
				/*
				 * Date de publi a null et -->on verifie qu'elle est superieur a la date de
				 * retour Date limite de confirmation >= Date de publication des résultats
				 **/
				if (datConfirm.before(datRetour)) {
					txtError = txtError + getErrorMessageDate(txtError, Formation_.datConfirmForm.getName(),
							Formation_.datRetourForm.getName());
				}
			}
		}

		return txtError;
	}

	/**
	 * @param txt
	 * @param libDate
	 * @param libDateToCompare
	 * @return
	 */
	private String getErrorMessageDate(String txt, String libDate, String libDateToCompare) {
		String txtRet = "";
		if (txt != null && !txt.equals("")) {
			txtRet = "<br>";
		}

		return txtRet + applicationContext.getMessage("formation.table.dat.error",
				new Object[] {
						applicationContext.getMessage("formation.table." + libDate, null, UI.getCurrent().getLocale()),
						applicationContext.getMessage("formation.table." + libDateToCompare, null,
								UI.getCurrent().getLocale()) },
				UI.getCurrent().getLocale());
	}
}
