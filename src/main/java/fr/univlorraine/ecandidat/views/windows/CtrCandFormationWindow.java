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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.FormationController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.siscol.Diplome;
import fr.univlorraine.ecandidat.entities.siscol.TypDiplome;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.CustomTabSheet;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredCheckBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredDateField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextArea;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxCommission;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxTypeDecision;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxTypeTraitement;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nField;

/**
 * Fenêtre d'édition de formation
 *
 * @author Kevin Hergalant
 */
@Configurable(preConstruction = true)
@SuppressWarnings("unchecked")
public class CtrCandFormationWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -1967836926575353048L;

	public static final String[] FIELDS_ORDER_1 = {Formation_.codEtpVetApoForm.getName(),
			Formation_.codVrsVetApoForm.getName(), Formation_.libApoForm.getName()};
	public static final String[] FIELDS_ORDER_1_DIP = {Formation_.codDipApoForm.getName(),
			Formation_.codVrsVdiApoForm.getName(), Formation_.libDipApoForm.getName()};
	public static final String[] FIELDS_ORDER_2 = {Formation_.codForm.getName(), Formation_.libForm.getName(),
			Formation_.tesForm.getName(), Formation_.commission.getName(), Formation_.typeTraitement.getName(),
			Formation_.siScolCentreGestion.getName(), Formation_.typeDecisionFav.getName(),
			Formation_.temDematForm.getName(), Formation_.temListCompForm.getName(),
			Formation_.typeDecisionFavListComp.getName(), Formation_.siScolTypDiplome.getName(),
			Formation_.motCleForm.getName(), Formation_.capaciteForm.getName()};
	public static final String[] FIELDS_ORDER_3 = {Formation_.datDebDepotForm.getName(),
			Formation_.datFinDepotForm.getName(), Formation_.datAnalyseForm.getName(),
			Formation_.datRetourForm.getName(), Formation_.datJuryForm.getName(), Formation_.datPubliForm.getName(),
			Formation_.datConfirmForm.getName(), Formation_.datConfirmListCompForm.getName()};
	public static final String[] FIELDS_ORDER_4 = {Formation_.preselectLieuForm.getName(),
			Formation_.preselectDateForm.getName(), Formation_.preselectHeureForm.getName()};
	public static final String[] FIELDS_ORDER_5 = {Formation_.i18nInfoCompForm.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormationController formationController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient TypeDecisionController typeDecisionController;

	/* Composants */
	private CustomBeanFieldGroup<Formation> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;
	private CustomTabSheet sheet;
	private Label labelErrorDate = new Label();
	private OneClickButton btnApoAssociateDip;
	private OneClickButton btnApoDissociateDip;

	/**
	 * Crée une fenêtre d'édition de formation
	 *
	 * @param formation
	 *            la formation à éditer
	 */
	public CtrCandFormationWindow(final Formation formation, final SecurityCtrCandFonc securityCtrCand) {
		CentreCandidature ctrCand = securityCtrCand.getCtrCand();

		/* Style */
		setModal(true);
		setWidth(700, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		// layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("formation.window", null, UI.getCurrent().getLocale()));

		/* FieldGroup */
		fieldGroup = new CustomBeanFieldGroup<>(Formation.class);

		/* Mise a jour du bean */
		fieldGroup.setItemDataSource(formation);

		/* Tabsheet */
		sheet = new CustomTabSheet(fieldGroup, applicationContext.getMessage("validation.tabsheet", null, UI.getCurrent().getLocale()));
		sheet.setImmediate(true);
		// sheet.addStyleName(StyleConstants.RESIZE_MAX_WIDTH);
		sheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		sheet.setSizeFull();
		sheet.addSelectedTabChangeListener(e -> center());
		layout.addComponent(sheet);

		if (parametreController.getSiScolMode().equals(ConstanteUtils.SI_SCOL_APOGEE)) {
			sheet.addGroupField(0, FIELDS_ORDER_1);
			sheet.addGroupField(0, FIELDS_ORDER_1_DIP);
			sheet.addGroupField(1, FIELDS_ORDER_2);
			sheet.addGroupField(2, FIELDS_ORDER_3);
			sheet.addGroupField(3, FIELDS_ORDER_4);
			sheet.addGroupField(4, FIELDS_ORDER_5);
			/* Layout apogee */
			FormLayout layoutParamApo = new FormLayout();
			layoutParamApo.setSizeFull();
			layoutParamApo.setSpacing(true);
			layoutParamApo.setMargin(true);
			sheet.addTab(layoutParamApo, applicationContext.getMessage("formation.window.sheet.apo", null, UI.getCurrent().getLocale()));

			/* Ajout VET */
			for (String fieldName : FIELDS_ORDER_1) {
				layoutParamApo.addComponent(getField(fieldName));
			}
			/* Bouton importation */
			OneClickButton btnApo = new OneClickButton(applicationContext.getMessage("formation.window.btn.apo", null, UI.getCurrent().getLocale()));
			layoutParamApo.addComponent(btnApo);

			/* Ajout Diplome */
			for (String fieldName : FIELDS_ORDER_1_DIP) {
				layoutParamApo.addComponent(getField(fieldName));
			}

			/* Actions sur les boutons apogee */
			RequiredTextField rtfCodEtpVetApo = (RequiredTextField) fieldGroup.getField(Formation_.codEtpVetApoForm.getName());
			RequiredTextField rtfCodVrsVetApo = (RequiredTextField) fieldGroup.getField(Formation_.codVrsVetApoForm.getName());
			RequiredTextField rtfLibApo = (RequiredTextField) fieldGroup.getField(Formation_.libApoForm.getName());

			/* Fields diplome */
			RequiredTextField rtfCodDipApo = (RequiredTextField) fieldGroup.getField(Formation_.codDipApoForm.getName());
			RequiredTextField rtfCodVrsDdiApo = (RequiredTextField) fieldGroup.getField(Formation_.codVrsVdiApoForm.getName());
			RequiredTextField rtfLibDipApoo = (RequiredTextField) fieldGroup.getField(Formation_.libDipApoForm.getName());

			btnApo.addClickListener(e -> {
				SearchFormationApoWindow window = new SearchFormationApoWindow(ctrCand.getIdCtrCand());
				window.addVetListener(v -> {
					if (v.getId() != null && v.getId().getCodEtpVet() != null && v.getId().getCodVrsVet() != null) {
						rtfCodEtpVetApo.setValue(v.getId().getCodEtpVet());
						rtfCodVrsVetApo.setValue(v.getId().getCodVrsVet());
						RequiredTextField rtfCodForm = (RequiredTextField) fieldGroup.getField(Formation_.codForm.getName());
						rtfCodForm.setValue(v.getId().getCodEtpVet() + "-" + v.getId().getCodVrsVet());

						/* Initialisation du diplome */
						rtfCodDipApo.setValue(null);
						rtfCodVrsDdiApo.setValue(null);
						rtfLibDipApoo.setValue(null);
					}
					if (v.getLibVet() != null) {
						rtfLibApo.setValue(v.getLibVet());
						RequiredTextField rtfLibForm = (RequiredTextField) fieldGroup.getField(Formation_.libForm.getName());
						rtfLibForm.setValue(v.getLibVet());
					}

					if (v.getId().getCodCge() != null) {
						RequiredComboBox<SiScolCentreGestion> comboBoxCGE = (RequiredComboBox<SiScolCentreGestion>) fieldGroup.getField(Formation_.siScolCentreGestion.getName());
						comboBoxCGE.setValue(tableRefController.getSiScolCentreGestionByCode(v.getId().getCodCge()));
						comboBoxCGE.setEnabled(false);
					}
					if (v.getId().getCodTpd() != null) {
						RequiredComboBox<TypDiplome> comboBoxTd = (RequiredComboBox<TypDiplome>) fieldGroup.getField(Formation_.siScolTypDiplome.getName());
						comboBoxTd.setValue(tableRefController.getSiScolTypDiplomeByCode(v.getId().getCodTpd()));
						comboBoxTd.setEnabled(false);
					}
					majFieldDip();
				});
				UI.getCurrent().addWindow(window);
			});

			/* Bouton importation diplome */
			btnApoAssociateDip = new OneClickButton(applicationContext.getMessage("formation.window.btn.apodipA", null, UI.getCurrent().getLocale()));
			btnApoAssociateDip.addClickListener(e -> {
				try {
					List<Diplome> liste = formationController.getDiplomeByVETs(rtfCodEtpVetApo.getValue(), rtfCodVrsVetApo.getValue());
					if (liste != null) {
						if (liste.size() == 1) {
							Diplome dip = liste.get(0);
							if (dip != null && dip.getId() != null) {
								rtfCodDipApo.setValue(dip.getId().getCodDip());
								rtfCodVrsDdiApo.setValue(dip.getId().getCodVrsVdi());
								rtfLibDipApoo.setValue(dip.getLibDip());
								majFieldDip();
								Notification.show(applicationContext.getMessage("formation.window.apodip.one", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
							}
						} else if (liste.size() > 1) {
							SearchDiplomeApoWindow dipWindow = new SearchDiplomeApoWindow(liste);
							dipWindow.addDiplomeListener(dip -> {
								if (dip != null && dip.getId() != null) {
									rtfCodDipApo.setValue(dip.getId().getCodDip());
									rtfCodVrsDdiApo.setValue(dip.getId().getCodVrsVdi());
									rtfLibDipApoo.setValue(dip.getLibDip());
									majFieldDip();
								}
							});
							UI.getCurrent().addWindow(dipWindow);
						} else {
							Notification.show(applicationContext.getMessage("formation.window.apodip.no", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
						}
					}
				} catch (SiScolException ex) {
					Notification.show(applicationContext.getMessage("siscol.connect.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					close();
				}
			});
			layoutParamApo.addComponent(btnApoAssociateDip);

			/* Dissociation de diplome */
			btnApoDissociateDip = new OneClickButton(applicationContext.getMessage("formation.window.btn.apodipD", null, UI.getCurrent().getLocale()));
			btnApoDissociateDip.addClickListener(e -> {
				rtfCodDipApo.setValue(null);
				rtfCodVrsDdiApo.setValue(null);
				rtfLibDipApoo.setValue(null);
				majFieldDip();
			});
			layoutParamApo.addComponent(btnApoDissociateDip);

			majFieldDip();

		} else {
			sheet.addGroupField(0, FIELDS_ORDER_2);
			sheet.addGroupField(1, FIELDS_ORDER_3);
			sheet.addGroupField(2, FIELDS_ORDER_4);
			sheet.addGroupField(3, FIELDS_ORDER_5);
		}

		/* Layout des param généraux */
		FormLayout layoutParamGen = new FormLayout();
		layoutParamGen.setSizeFull();
		layoutParamGen.setSpacing(true);
		layoutParamGen.setMargin(true);
		sheet.addTab(layoutParamGen, applicationContext.getMessage("formation.window.sheet.gen", null, UI.getCurrent().getLocale()));

		/* Layout des dates */
		VerticalLayout vlDate = new VerticalLayout();
		vlDate.setMargin(true);
		labelErrorDate.addStyleName(ValoTheme.LABEL_FAILURE);
		labelErrorDate.setContentMode(ContentMode.HTML);
		labelErrorDate.setVisible(false);
		vlDate.addComponent(labelErrorDate);
		FormLayout layoutParamDate = new FormLayout();
		layoutParamDate.setSizeFull();
		layoutParamDate.setSpacing(true);
		layoutParamDate.setMargin(true);
		vlDate.addComponent(layoutParamDate);
		sheet.addTab(vlDate, applicationContext.getMessage("formation.window.sheet.date", null, UI.getCurrent().getLocale()));

		/* Layout des param epreuves */
		FormLayout layoutParamEpreuve = new FormLayout();
		layoutParamEpreuve.setSizeFull();
		layoutParamEpreuve.setSpacing(true);
		layoutParamEpreuve.setMargin(true);
		sheet.addTab(layoutParamEpreuve, applicationContext.getMessage("formation.window.sheet.epreuve", null, UI.getCurrent().getLocale()));

		/* Layout des param info Comp */
		FormLayout layoutInfoComp = new FormLayout();
		layoutInfoComp.setSizeFull();
		layoutInfoComp.setSpacing(true);
		layoutInfoComp.setMargin(true);
		sheet.addTab(layoutInfoComp, applicationContext.getMessage("formation.window.sheet.infocomp", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		for (String fieldName : FIELDS_ORDER_2) {
			layoutParamGen.addComponent(getField(fieldName));
		}

		for (String fieldName : FIELDS_ORDER_3) {
			layoutParamDate.addComponent(getField(fieldName));
		}

		for (String fieldName : FIELDS_ORDER_4) {
			layoutParamEpreuve.addComponent(getField(fieldName));
		}

		for (String fieldName : FIELDS_ORDER_5) {
			layoutInfoComp.addComponent(getField(fieldName));
		}

		/* Les box de liste complémentaire */
		// ComboBoxTypeDecision cbTypeDecisionFav =
		// (ComboBoxTypeDecision)fieldGroup.getField(Formation_.typeDecisionFav.getName());
		ComboBoxTypeDecision cbTypeDecisionFav = (ComboBoxTypeDecision) fieldGroup.getField(Formation_.typeDecisionFav.getName());
		ComboBoxTypeDecision cbTypeDecisionFavListComp = (ComboBoxTypeDecision) fieldGroup.getField(Formation_.typeDecisionFavListComp.getName());

		/* Alimentation des listes */
		cbTypeDecisionFav.setTypeDecisions(typeDecisionController.getTypeDecisionsFavorableEnServiceByCtrCand(securityCtrCand.getCtrCand()));
		cbTypeDecisionFavListComp.setTypeDecisions(typeDecisionController.getTypeDecisionsFavorableEnServiceByCtrCand(securityCtrCand.getCtrCand()));

		RequiredCheckBox checkBoxListComp = (RequiredCheckBox) fieldGroup.getField(Formation_.temListCompForm.getName());

		/* On cherche le type de decision a placer par défaut */
		TypeDecision typeDecisionDefault = getTypeDecisionByDefault(formation, ctrCand);

		/*
		 * Ajout du listener pour action sur la checkbox des liste comp-->activation ou
		 * desactivation des decisions fav list comp
		 */
		checkBoxListComp.addValueChangeListener(e -> {
			cbTypeDecisionFavListComp.setBoxNeeded(checkBoxListComp.getValue(), typeDecisionDefault);
		});

		ComboBoxCommission cbCommission = (ComboBoxCommission) fieldGroup.getField(Formation_.commission.getName());
		if (securityCtrCand.getIsGestAllCommission()) {
			cbCommission.filterListValue(ctrCand.getCommissions());
		} else {
			cbCommission.filterListValue(
					ctrCand.getCommissions().stream().filter(commission -> MethodUtils.isIdInListId(commission.getIdComm(), securityCtrCand.getListeIdCommission())).collect(Collectors.toList()));
		}

		/* Listener pour centrer la fenetre après ajout de langue */
		I18nField i18nField = ((I18nField) fieldGroup.getField(Formation_.i18nInfoCompForm.getName()));
		i18nField.addCenterListener(e -> {
			if (e) {
				center();
			}
		});
		i18nField.setNoRequierd();

		/* Si la formation est nouvelle on colle les valeurs par defaut du ctrCand */
		if (formation.getIdForm() == null) {
			cbTypeDecisionFavListComp.setValue(typeDecisionDefault);
			/* Obligé d'alimenter les box, car elles sont vides au départ */
			cbTypeDecisionFav.setValue(formation.getTypeDecisionFav());
		} else {
			/* Obligé d'alimenter les box, car elles sont vides au départ */
			cbTypeDecisionFav.setValue(formation.getTypeDecisionFav());
			cbTypeDecisionFavListComp.setValue(formation.getTypeDecisionFavListComp());
			if (formation.getCodEtpVetApoForm() != null) {
				RequiredComboBox<SiScolCentreGestion> comboBoxCGE = (RequiredComboBox<SiScolCentreGestion>) fieldGroup.getField(Formation_.siScolCentreGestion.getName());
				comboBoxCGE.setEnabled(false);
				RequiredComboBox<TypDiplome> comboBoxTd = (RequiredComboBox<TypDiplome>) fieldGroup.getField(Formation_.siScolTypDiplome.getName());
				comboBoxTd.setEnabled(false);
			}
		}

		cbTypeDecisionFavListComp.setBoxNeeded(checkBoxListComp.getValue(), typeDecisionDefault);

		/* Filtre des types de decisions */
		ComboBoxTypeTraitement cbTypeTraitement = (ComboBoxTypeTraitement) fieldGroup.getField(Formation_.typeTraitement.getName());
		cbTypeTraitement.filterFinal();

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
				/* Efface les erreurs des onglets */
				sheet.effaceErrorSheet();
				labelErrorDate.setVisible(false);

				/* Si le code de profil existe dejà --> erreur */
				if (!formationController.isCodFormUnique((String) fieldGroup.getField(Formation_.codForm.getName()).getValue(), formation.getIdForm())) {
					Notification.show(applicationContext.getMessage("window.error.cod.nonuniq", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				fieldGroup.preCommit();

				if (!fieldGroup.isValid()) {
					sheet.validateSheet();
					return;
				}

				String validationDate = getErrorMessageDate();
				if (validationDate != null && !validationDate.equals("")) {
					labelErrorDate.setValue(validationDate);
					labelErrorDate.setVisible(true);
					sheet.displayErrorSheet(true, 2);
					return;
				}

				/* Valide la saisie */
				fieldGroup.commit();

				formationController.saveFormation(formation);
				/* Ferme la fenêtre */
				close();
			} catch (CommitException ce) {
				sheet.getSheetOnError(ce.getInvalidFields());
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * Mise a jour des champs de diplome
	 */
	private void majFieldDip() {
		RequiredTextField rtfCodEtpVetApo = (RequiredTextField) fieldGroup.getField(Formation_.codEtpVetApoForm.getName());
		RequiredTextField rtfCodVrsVetApo = (RequiredTextField) fieldGroup.getField(Formation_.codVrsVetApoForm.getName());
		RequiredTextField rtfCodDipApo = (RequiredTextField) fieldGroup.getField(Formation_.codDipApoForm.getName());
		RequiredTextField rtfCodVrsDdiApo = (RequiredTextField) fieldGroup.getField(Formation_.codVrsVdiApoForm.getName());
		RequiredTextField rtfLibDipApoo = (RequiredTextField) fieldGroup.getField(Formation_.libDipApoForm.getName());
		if (rtfCodDipApo == null || rtfCodVrsDdiApo == null || rtfLibDipApoo == null || btnApoAssociateDip == null) {
			return;
		}
		if (rtfCodEtpVetApo.getValue() != null && rtfCodVrsVetApo.getValue() != null) {
			rtfCodDipApo.setVisible(true);
			rtfCodVrsDdiApo.setVisible(true);
			rtfLibDipApoo.setVisible(true);
			if (rtfCodDipApo.getValue() != null && rtfCodVrsDdiApo.getValue() != null) {
				btnApoAssociateDip.setVisible(false);
				btnApoDissociateDip.setVisible(true);
			} else {
				btnApoAssociateDip.setVisible(true);
				btnApoDissociateDip.setVisible(false);
			}

		} else {
			rtfCodVrsDdiApo.setVisible(false);
			rtfLibDipApoo.setVisible(false);
			rtfCodDipApo.setVisible(false);
			rtfCodVrsDdiApo.setVisible(false);
			rtfLibDipApoo.setVisible(false);
			btnApoAssociateDip.setVisible(false);
			btnApoDissociateDip.setVisible(false);
		}
	}

	/**
	 * @param formation
	 * @param ctrCand
	 * @return la decision par défaut du ctr cand
	 */
	private TypeDecision getTypeDecisionByDefault(final Formation formation, final CentreCandidature ctrCand) {
		TypeDecision typeDecisionDefault = null;
		if (formation.getTypeDecisionFavListComp() != null) {
			typeDecisionDefault = formation.getTypeDecisionFavListComp();
		} else {
			typeDecisionDefault = ctrCand.getTypeDecisionFavListComp();
		}
		return typeDecisionDefault;
	}

	/**
	 * Renvoie le field construit
	 *
	 * @param fieldName
	 * @return
	 */
	private Field<?> getField(final String fieldName) {
		String caption = applicationContext.getMessage("formation.table." + fieldName, null, UI.getCurrent().getLocale());
		Field<?> field;
		if (fieldName.equals(Formation_.motCleForm.getName())) {
			field = fieldGroup.buildAndBind(caption, fieldName, RequiredTextArea.class);
		} else if (fieldName.equals(Formation_.codEtpVetApoForm.getName())
				|| fieldName.equals(Formation_.codVrsVetApoForm.getName())
				|| fieldName.equals(Formation_.libApoForm.getName())) {
			if (parametreController.getIsFormCodApoOblig()) {
				field = fieldGroup.buildAndBind(caption, fieldName, true);
			} else {
				field = fieldGroup.buildAndBind(caption, fieldName);
			}
			field.setEnabled(false);
		} else {
			field = fieldGroup.buildAndBind(caption, fieldName);
			if (fieldName.equals(Formation_.codDipApoForm.getName())
					|| fieldName.equals(Formation_.codVrsVdiApoForm.getName())
					|| fieldName.equals(Formation_.libDipApoForm.getName())) {
				field.setEnabled(false);
			}
		}

		field.setWidth(100, Unit.PERCENTAGE);
		return field;
	}

	/** @return true si les dates sont valides */
	private String getErrorMessageDate() {
		RequiredDateField fieldDatConfirm = ((RequiredDateField) fieldGroup.getField(Formation_.datConfirmForm.getName()));
		RequiredDateField fieldDatConfirmListComp = ((RequiredDateField) fieldGroup.getField(Formation_.datConfirmListCompForm.getName()));
		RequiredDateField fieldDatDebDepot = ((RequiredDateField) fieldGroup.getField(Formation_.datDebDepotForm.getName()));
		RequiredDateField fieldDatAnalyse = ((RequiredDateField) fieldGroup.getField(Formation_.datAnalyseForm.getName()));
		RequiredDateField fieldDatFinDepo = ((RequiredDateField) fieldGroup.getField(Formation_.datFinDepotForm.getName()));
		RequiredDateField fieldDatJury = ((RequiredDateField) fieldGroup.getField(Formation_.datJuryForm.getName()));
		RequiredDateField fieldDatPubli = ((RequiredDateField) fieldGroup.getField(Formation_.datPubliForm.getName()));
		RequiredDateField fieldDatRetour = ((RequiredDateField) fieldGroup.getField(Formation_.datRetourForm.getName()));

		return formationController.getTxtErrorEditDate(fieldDatConfirm.getValue(), fieldDatConfirmListComp.getValue(), fieldDatDebDepot.getValue(), fieldDatAnalyse.getValue(),
				fieldDatFinDepo.getValue(), fieldDatJury.getValue(), fieldDatPubli.getValue(), fieldDatRetour.getValue());
	}
}
