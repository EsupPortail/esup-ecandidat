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
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CommissionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.CustomPanel;
import fr.univlorraine.ecandidat.vaadin.components.CustomTabSheet;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.EmailRFCValidator;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextArea;
import fr.univlorraine.ecandidat.vaadin.form.UrlValidator;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nField;
import fr.univlorraine.ecandidat.vaadin.form.siscol.AdresseForm;

/**
 * Fenêtre d'édition de commission
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CtrCandCommissionWindow extends Window {

	public static final String[] FIELDS_ORDER_GEN = { Commission_.codComm.getName(),
		Commission_.libComm.getName(),
		Commission_.tesComm.getName(),
		Commission_.mailComm.getName(),
		Commission_.urlComm.getName(),
		Commission_.telComm.getName(),
		Commission_.faxComm.getName(),
		Commission_.i18nCommentRetourComm.getName() };

	public static final String[] FIELDS_ORDER_ALERT = {
		Commission_.mailAlertComm.getName(),
		Commission_.temAlertPropComm.getName(),
		Commission_.temAlertAnnulComm.getName(),
		Commission_.temAlertTransComm.getName(),
		Commission_.temAlertDesistComm.getName(),
		Commission_.temAlertListePrincComm.getName() };
	public static final String[] FIELDS_ORDER_SIGN = { Commission_.temEditLettreComm.getName(), Commission_.temMailLettreComm.getName(), Commission_.signataireComm.getName() };

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CommissionController commissionController;

	/* Composants */
	private CustomTabSheet sheet;
	private CustomBeanFieldGroup<Commission> fieldGroup;
	private CustomBeanFieldGroup<Adresse> fieldGroupAdresse;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;

	/**
	 * Crée une fenêtre d'édition de commission
	 * @param commission
	 *                       la commission à éditer
	 * @param isAdmin
	 */
	public CtrCandCommissionWindow(final Commission commission, final Boolean isAdmin) {
		/* Style */
		setModal(true);
		setWidth(700, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("commission.window", null, UI.getCurrent().getLocale()));

		/* FiledGroup de commission */
		fieldGroup = new CustomBeanFieldGroup<>(Commission.class);
		fieldGroup.setItemDataSource(commission);

		/* Tabsheet */
		sheet = new CustomTabSheet(fieldGroup, applicationContext.getMessage("validation.tabsheet", null, UI.getCurrent().getLocale()));
		sheet.setImmediate(true);
		sheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		// sheet.addStyleName(StyleConstants.RESIZE_MAX_WIDTH);
		sheet.setSizeFull();
		sheet.addGroupField(0, FIELDS_ORDER_GEN);
		sheet.addGroupField(2, FIELDS_ORDER_ALERT);
		sheet.addGroupField(3, FIELDS_ORDER_SIGN);
		sheet.addSelectedTabChangeListener(e -> center());
		layout.addComponent(sheet);

		/* Formulaire général */
		final FormLayout layoutParamGen = new FormLayout();
		layoutParamGen.setSizeFull();
		layoutParamGen.setSpacing(true);
		layoutParamGen.setMargin(true);
		sheet.addTab(layoutParamGen, applicationContext.getMessage("commission.window.sheet.gen", null, UI.getCurrent().getLocale()));

		/* Layout adresse */
		final VerticalLayout vlAdresse = new VerticalLayout();
		vlAdresse.setSpacing(true);
		vlAdresse.setMargin(true);

		final OneClickButton btnImport = new OneClickButton(applicationContext.getMessage("commission.window.import.adr", null, UI.getCurrent().getLocale()));
		if (!isAdmin) {
			btnImport.setEnabled(false);
		}
		btnImport.addClickListener(e -> {
			final SearchCommissionWindow scw = new SearchCommissionWindow(commission.getCentreCandidature());
			scw.addCommissionListener(comm -> {
				if (fieldGroupAdresse.getItemDataSource() == null || fieldGroupAdresse.getItemDataSource().getBean() == null) {
					return;
				}
				final Adresse adr = fieldGroupAdresse.getItemDataSource().getBean();
				adr.duplicateAdresse(comm.getAdresse());
				fieldGroupAdresse.setItemDataSource(adr);
			});
			UI.getCurrent().addWindow(scw);
		});
		vlAdresse.addComponent(btnImport);
		vlAdresse.setComponentAlignment(btnImport, Alignment.MIDDLE_CENTER);

		/* Formulaire d'adresse */
		fieldGroupAdresse = new CustomBeanFieldGroup<>(Adresse.class, ConstanteUtils.TYP_FORM_ADR);
		fieldGroupAdresse.setItemDataSource(commission.getAdresse());
		final AdresseForm adresseForm = new AdresseForm(fieldGroupAdresse, true);
		vlAdresse.addComponent(adresseForm);
		vlAdresse.setExpandRatio(adresseForm, 1);
		sheet.addTab(vlAdresse, applicationContext.getMessage("commission.window.sheet.adr", null, UI.getCurrent().getLocale()));

		/* Layout des propriétés d'alerte */
		final FormLayout layoutAlert = new FormLayout();
		layoutAlert.setSizeFull();
		layoutAlert.setSpacing(true);
		layoutAlert.setMargin(true);
		sheet.addTab(layoutAlert, applicationContext.getMessage("commission.window.sheet.alert", null, UI.getCurrent().getLocale()));

		/* Layout pour le signataire */
		final VerticalLayout vlSignataire = new VerticalLayout();
		vlSignataire.setSpacing(true);
		vlSignataire.setMargin(true);

		/* Infos signataire */
		String txtInfo = applicationContext.getMessage("commission.window.signataire.info", null, UI.getCurrent().getLocale());
		if (isAdmin) {
			txtInfo = txtInfo + applicationContext.getMessage("commission.window.signataire.info.admin", null, UI.getCurrent().getLocale());
		} else {
			txtInfo = txtInfo + applicationContext.getMessage("commission.window.signataire.info.param", null, UI.getCurrent().getLocale());
		}
		final CustomPanel panelInfo = new CustomPanel(applicationContext.getMessage("informations", null, UI.getCurrent().getLocale()), txtInfo, FontAwesome.INFO_CIRCLE);
		panelInfo.setWidthMax();
		vlSignataire.addComponent(panelInfo);

		/* Formulaire signataire */
		final FormLayout layoutSignataire = new FormLayout();
		layoutSignataire.setSizeFull();
		layoutSignataire.setSpacing(true);
		vlSignataire.addComponent(layoutSignataire);
		sheet.addTab(vlSignataire, applicationContext.getMessage("commission.window.sheet.signataire", null, UI.getCurrent().getLocale()));

		/* Ajout des propriétés générales */
		for (final String fieldName : FIELDS_ORDER_GEN) {
			final String caption = applicationContext.getMessage("commission.table." + fieldName, null, UI.getCurrent().getLocale());
			final Field<?> field = fieldGroup.buildAndBind(caption, fieldName);

			/* On interdit les modifs des infos sensibles pour les membres de la commission */
			if (!isAdmin && (fieldName.equals(Commission_.codComm.getName()) || fieldName.equals(Commission_.libComm.getName()) || fieldName.equals(Commission_.tesComm.getName()))) {
				field.setEnabled(false);
			}

			field.setWidth(100, Unit.PERCENTAGE);
			if (fieldName.equals(Commission_.mailComm.getName())) {
				field.addValidator(new EmailRFCValidator(applicationContext.getMessage("validation.error.mail", null, UI.getCurrent().getLocale())));
			}
			if (fieldName.equals(Commission_.urlComm.getName())) {
				field.addValidator(new UrlValidator(applicationContext.getMessage("validation.url.malformed", null, UI.getCurrent().getLocale())));
			}
			if (fieldName.equals(Commission_.telComm.getName()) || fieldName.equals(Commission_.faxComm.getName())) {
				field.addValidator(new RegexpValidator(ConstanteUtils.regExNoTel, applicationContext.getMessage("validation.error.tel", null, UI.getCurrent().getLocale())));
			}
			layoutParamGen.addComponent(field);
		}

		/* I18n */
		/* Listener pour centrer la fenetre après ajout de langue */
		final I18nField i18nField = ((I18nField) fieldGroup.getField(Commission_.i18nCommentRetourComm.getName()));
		i18nField.addCenterListener(e -> {
			if (e) {
				center();
			}
		});
		i18nField.setNoRequierd();

		/* Pour les alertes */
		for (final String fieldName : FIELDS_ORDER_ALERT) {
			final Field<?> field = fieldGroup.buildAndBind(applicationContext.getMessage("commission.table." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
			if (fieldName.equals(Commission_.mailAlertComm.getName())) {
				field.setWidth(100, Unit.PERCENTAGE);
				field.addValidator(new EmailRFCValidator(applicationContext.getMessage("validation.error.mail", null, UI.getCurrent().getLocale())));
			}
			layoutAlert.addComponent(field);
		}

		/* Pour le signataire */
		for (final String fieldName : FIELDS_ORDER_SIGN) {
			final String caption = applicationContext.getMessage("commission.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field;
			if (fieldName.equals(Commission_.signataireComm.getName())) {
				field = fieldGroup.buildAndBind(caption, fieldName, RequiredTextArea.class);
			} else {
				field = fieldGroup.buildAndBind(caption, fieldName);
			}
			field.setWidth(100, Unit.PERCENTAGE);
			layoutSignataire.addComponent(field);
		}

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
			/* Efface les erreurs des onglets */
			sheet.effaceErrorSheet();
			displayErrorSheet(false, 1);

			/* Si le code existe dejà --> erreur */
			if (!commissionController.isCodCommUnique((String) fieldGroup.getField(Commission_.codComm.getName()).getValue(), commission.getIdComm())) {
				Notification.show(applicationContext.getMessage("window.error.cod.nonuniq", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}

			fieldGroupAdresse.preCommit();
			fieldGroup.preCommit();

			if (!fieldGroup.isValid()) {
				sheet.validateSheet();
			}

			if (!fieldGroupAdresse.isValid()) {
				displayErrorSheet(true, 1);
			}

			try {
				/* Valide la saisie de l'adresse */
				fieldGroupAdresse.commit();
			} catch (final CommitException ce) {
				displayErrorSheet(true, 1);
				return;
			}
			try {
				/* Valide la saisie */
				fieldGroup.commit();
				/* Enregistre la commission saisie */
				commissionController.saveCommission(commission, fieldGroupAdresse.getItemDataSource().getBean());
				/* Ferme la fenêtre */
				close();
			} catch (final CommitException ce) {
				sheet.getSheetOnError(ce.getInvalidFields());
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Hack car problème de hauteur de fenetre */
		sheet.setSelectedTab(1);
		sheet.setSelectedTab(0);
		/* Centre la fenêtre */
		center();
	}

	/**
	 * Affiche les erreur d'un sheet avec un point exclam en logo
	 * @param findError
	 * @param tabOrder
	 */
	private void displayErrorSheet(final Boolean findError, final Integer tabOrder) {
		if (findError) {
			sheet.getTab(tabOrder).setIcon(FontAwesome.EXCLAMATION);
		} else {
			sheet.getTab(tabOrder).setIcon(null);
		}
	}
}
