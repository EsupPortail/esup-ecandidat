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

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.AbstractField;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.controllers.FormationController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeFormation;
import fr.univlorraine.ecandidat.entities.siscol.apogee.Diplome;
import fr.univlorraine.ecandidat.entities.siscol.apogee.TypDiplome;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.CustomTabSheet;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.RequiredCheckBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredDateField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextArea;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;
import fr.univlorraine.ecandidat.vaadin.form.UrlValidator;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxCommission;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxTypeDecision;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxTypeTraitement;
import fr.univlorraine.ecandidat.vaadin.form.i18n.I18nField;
import jakarta.annotation.Resource;

/**
 * Fenêtre d'édition de formation
 * @author Kevin Hergalant
 */
@Configurable(preConstruction = true)
@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
public class CtrCandFormationWindow extends Window {

	public static final String[] FIELDS_ORDER_1_APO = { Formation_.codEtpVetApoForm.getName(),
		Formation_.codVrsVetApoForm.getName(),
		Formation_.libApoForm.getName() };
	public static final String[] FIELDS_ORDER_1_DIP_APO = { Formation_.codDipApoForm.getName(),
		Formation_.codVrsVdiApoForm.getName(),
		Formation_.libDipApoForm.getName() };

	public static final String[] FIELDS_ORDER_1_PEGASE = { Formation_.codPegaseForm.getName(),
		Formation_.libPegaseForm.getName() };

	public static final String[] FIELDS_ORDER_2 = { Formation_.codForm.getName(),
		Formation_.libForm.getName(),
		Formation_.tesForm.getName(),
		Formation_.commission.getName(),
		Formation_.typeTraitement.getName(),
		Formation_.siScolCentreGestion.getName(),
		Formation_.typeDecisionFav.getName(),
		Formation_.temDematForm.getName(),
		Formation_.temListCompForm.getName(),
		Formation_.typeDecisionFavListComp.getName(),
		Formation_.siScolTypDiplome.getName(),
		Formation_.typeFormation.getName(),
		Formation_.urlForm.getName(),
		Formation_.motCleForm.getName(),
		Formation_.capaciteForm.getName() };
	public static final String[] FIELDS_ORDER_3 = { Formation_.datDebDepotForm.getName(),
		Formation_.datFinDepotForm.getName(),
		Formation_.datAnalyseForm.getName(),
		Formation_.datRetourForm.getName(),
		Formation_.datJuryForm.getName(),
		Formation_.datPubliForm.getName(),
		Formation_.datConfirmForm.getName(),
		Formation_.delaiConfirmForm.getName(),
		Formation_.datConfirmListCompForm.getName(),
		Formation_.delaiConfirmListCompForm.getName() };
	public static final String[] FIELDS_ORDER_4 = { Formation_.preselectLieuForm.getName(),
		Formation_.preselectDateForm.getName(),
		Formation_.preselectHeureForm.getName() };
	public static final String[] FIELDS_ORDER_5 = { Formation_.i18nInfoCompForm.getName() };

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

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	@Value("${hideSiScol:false}")
	private transient Boolean hideSiScol;

	/* Composants */
	private CustomBeanFieldGroup<Formation> fieldGroup;
	private OneClickButton btnEnregistrer;
	private OneClickButton btnAnnuler;
	private CustomTabSheet sheet;
	private final Label labelErrorDate = new Label();
	private OneClickButton btnApoAssociateDip;
	private OneClickButton btnApoDissociateDip;

	/**
	 * Crée une fenêtre d'édition de formation
	 * @param formation
	 *                     la formation à éditer
	 */
	public CtrCandFormationWindow(final Formation formation, final SecurityCtrCandFonc securityCtrCand) {
		final CentreCandidature ctrCand = securityCtrCand.getCtrCand();

		/* Style */
		setModal(true);
		setWidth(700, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
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
		// sheet.addStyleName(StyleConstants.RESIZE_MAX_WIDTH);
		sheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		sheet.setSizeFull();
		sheet.addSelectedTabChangeListener(e -> center());
		layout.addComponent(sheet);

		if (!hideSiScol && siScolService.isImplementationApogee()) {
			sheet.addGroupField(0, FIELDS_ORDER_1_APO);
			sheet.addGroupField(0, FIELDS_ORDER_1_DIP_APO);
			sheet.addGroupField(1, FIELDS_ORDER_2);
			sheet.addGroupField(2, FIELDS_ORDER_3);
			sheet.addGroupField(3, FIELDS_ORDER_4);
			sheet.addGroupField(4, FIELDS_ORDER_5);
			/* Layout apogee */
			final FormLayout layoutParamApo = new FormLayout();
			layoutParamApo.setSizeFull();
			layoutParamApo.setSpacing(true);
			layoutParamApo.setMargin(true);
			sheet.addTab(layoutParamApo, applicationContext.getMessage("formation.window.sheet.apo", null, UI.getCurrent().getLocale()));

			/* Ajout VET */
			for (final String fieldName : FIELDS_ORDER_1_APO) {
				layoutParamApo.addComponent(getField(fieldName));
			}
			/* Bouton importation */
			final OneClickButton btnApo = new OneClickButton(applicationContext.getMessage("formation.window.btn.apo", null, UI.getCurrent().getLocale()));
			layoutParamApo.addComponent(btnApo);

			/* Ajout Diplome */
			for (final String fieldName : FIELDS_ORDER_1_DIP_APO) {
				layoutParamApo.addComponent(getField(fieldName));
			}

			/* Actions sur les boutons apogee */
			final RequiredTextField rtfCodEtpVetApo = (RequiredTextField) fieldGroup.getField(Formation_.codEtpVetApoForm.getName());
			final RequiredTextField rtfCodVrsVetApo = (RequiredTextField) fieldGroup.getField(Formation_.codVrsVetApoForm.getName());
			final RequiredTextField rtfLibApo = (RequiredTextField) fieldGroup.getField(Formation_.libApoForm.getName());

			/* Fields diplome */
			final RequiredTextField rtfCodDipApo = (RequiredTextField) fieldGroup.getField(Formation_.codDipApoForm.getName());
			final RequiredTextField rtfCodVrsDdiApo = (RequiredTextField) fieldGroup.getField(Formation_.codVrsVdiApoForm.getName());
			final RequiredTextField rtfLibDipApoo = (RequiredTextField) fieldGroup.getField(Formation_.libDipApoForm.getName());

			btnApo.addClickListener(e -> {
				final SearchFormationApoWindow window = new SearchFormationApoWindow(ctrCand.getIdCtrCand());
				window.addVetListener((vet, useCode, useLibelle) -> {
					if (vet.getId() != null && vet.getId().getCodEtpVet() != null && vet.getId().getCodVrsVet() != null) {
						rtfCodEtpVetApo.setValue(vet.getId().getCodEtpVet());
						rtfCodVrsVetApo.setValue(vet.getId().getCodVrsVet());
						if (useCode) {
							final RequiredTextField rtfCodForm = (RequiredTextField) fieldGroup.getField(Formation_.codForm.getName());
							rtfCodForm.setValue(vet.getId().getCodEtpVet() + "-" + vet.getId().getCodVrsVet());
						}

						/* Initialisation du diplome */
						rtfCodDipApo.setValue(null);
						rtfCodVrsDdiApo.setValue(null);
						rtfLibDipApoo.setValue(null);
					}
					if (vet.getLibVet() != null) {
						rtfLibApo.setValue(vet.getLibVet());
						if (useLibelle) {
							final RequiredTextField rtfLibForm = (RequiredTextField) fieldGroup.getField(Formation_.libForm.getName());
							rtfLibForm.setValue(vet.getLibVet());
						}
					}

					if (vet.getId().getCodCge() != null) {
						final RequiredComboBox<SiScolCentreGestion> comboBoxCGE = (RequiredComboBox<SiScolCentreGestion>) fieldGroup.getField(Formation_.siScolCentreGestion.getName());
						comboBoxCGE.setValue(tableRefController.getSiScolCentreGestionByCode(vet.getId().getCodCge()));
						comboBoxCGE.setEnabled(false);
					}
					if (vet.getId().getCodTpd() != null) {
						final RequiredComboBox<TypDiplome> comboBoxTd = (RequiredComboBox<TypDiplome>) fieldGroup.getField(Formation_.siScolTypDiplome.getName());
						comboBoxTd.setValue(tableRefController.getSiScolTypDiplomeByCode(vet.getId().getCodTpd()));
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
					final List<Diplome> liste = formationController.getDiplomeByVETs(rtfCodEtpVetApo.getValue(), rtfCodVrsVetApo.getValue());
					if (liste != null) {
						if (liste.size() == 1) {
							final Diplome dip = liste.get(0);
							if (dip != null && dip.getId() != null) {
								rtfCodDipApo.setValue(dip.getId().getCodDip());
								rtfCodVrsDdiApo.setValue(dip.getId().getCodVrsVdi());
								rtfLibDipApoo.setValue(dip.getLibDip());
								majFieldDip();
								Notification.show(applicationContext.getMessage("formation.window.apodip.one", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
							}
						} else if (liste.size() > 1) {
							final SearchDiplomeApoWindow dipWindow = new SearchDiplomeApoWindow(liste);
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
				} catch (final SiScolException ex) {
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

		} else if (!hideSiScol && siScolService.isImplementationPegase()) {
			sheet.addGroupField(0, FIELDS_ORDER_1_PEGASE);
			sheet.addGroupField(1, FIELDS_ORDER_2);
			sheet.addGroupField(2, FIELDS_ORDER_3);
			sheet.addGroupField(3, FIELDS_ORDER_4);
			sheet.addGroupField(4, FIELDS_ORDER_5);
			/* Layout apogee */
			final FormLayout layoutParamPegase = new FormLayout();
			layoutParamPegase.setSizeFull();
			layoutParamPegase.setSpacing(true);
			layoutParamPegase.setMargin(true);
			sheet.addTab(layoutParamPegase, applicationContext.getMessage("formation.window.sheet.pegase", null, UI.getCurrent().getLocale()));

			/* Ajout Formation */
			for (final String fieldName : FIELDS_ORDER_1_PEGASE) {
				layoutParamPegase.addComponent(getField(fieldName));
			}

			/* Bouton importation */
			final OneClickButton btnPegase = new OneClickButton(applicationContext.getMessage("formation.window.btn.pegase", null, UI.getCurrent().getLocale()));
			layoutParamPegase.addComponent(btnPegase);

			/* Actions sur les boutons Pegase */
			final RequiredTextField rtfCodFormPegase = (RequiredTextField) fieldGroup.getField(Formation_.codPegaseForm.getName());
			final RequiredTextField rtfLibFormPegase = (RequiredTextField) fieldGroup.getField(Formation_.libPegaseForm.getName());

			btnPegase.addClickListener(e -> {
				final SearchFormationPegaseWindow window = new SearchFormationPegaseWindow();
				window.addFormationListener((form, useCode, useLibelle) -> {
					if (form.getCode() != null) {
						rtfCodFormPegase.setValue(form.getCode());
						if (useCode) {
							final RequiredTextField rtfCodForm = (RequiredTextField) fieldGroup.getField(Formation_.codForm.getName());
							rtfCodForm.setValue(form.getCode());
						}
					}
					if (form.getLibelleLong() != null) {
						rtfLibFormPegase.setValue(form.getLibelleLong());
						if (useLibelle) {
							final RequiredTextField rtfLibForm = (RequiredTextField) fieldGroup.getField(Formation_.libForm.getName());
							rtfLibForm.setValue(form.getLibelleLong());
						}
					}

//					if (form.getCodeStructure() != null) {
//						final RequiredComboBox<SiScolCentreGestion> comboBoxCGE = (RequiredComboBox<SiScolCentreGestion>) fieldGroup.getField(Formation_.siScolCentreGestion.getName());
//						comboBoxCGE.setValue(tableRefController.getSiScolCentreGestionByCode(form.getCodeStructure()));
//						comboBoxCGE.setEnabled(false);
//					}

					/* Recherche du type de diplome */
					try {
						final String codTypDiplome = siScolService.getTypDiplomeByFormation(form);
						if (codTypDiplome != null) {
							final RequiredComboBox<TypDiplome> comboBoxTd = (RequiredComboBox<TypDiplome>) fieldGroup.getField(Formation_.siScolTypDiplome.getName());
							comboBoxTd.setValue(tableRefController.getSiScolTypDiplomeByCode(codTypDiplome));
						}
					} catch (final SiScolException ex) {
					}
//					if (form.getCodeTypeDiplome() != null) {
//						final RequiredComboBox<TypDiplome> comboBoxTd = (RequiredComboBox<TypDiplome>) fieldGroup.getField(Formation_.siScolTypDiplome.getName());
//						comboBoxTd.setValue(tableRefController.getSiScolTypDiplomeByCode(form.getCodeTypeDiplome()));
//						comboBoxTd.setEnabled(false);
//					}
				});
				UI.getCurrent().addWindow(window);
			});

		} else {
			sheet.addGroupField(0, FIELDS_ORDER_2);
			sheet.addGroupField(1, FIELDS_ORDER_3);
			sheet.addGroupField(2, FIELDS_ORDER_4);
			sheet.addGroupField(3, FIELDS_ORDER_5);
		}

		/* Layout des param généraux */
		final FormLayout layoutParamGen = new FormLayout();
		layoutParamGen.setSizeFull();
		layoutParamGen.setSpacing(true);
		layoutParamGen.setMargin(true);
		sheet.addTab(layoutParamGen, applicationContext.getMessage("formation.window.sheet.gen", null, UI.getCurrent().getLocale()));

		/* Layout des dates */
		final VerticalLayout vlDate = new VerticalLayout();
		vlDate.setMargin(true);
		labelErrorDate.addStyleName(ValoTheme.LABEL_FAILURE);
		labelErrorDate.setContentMode(ContentMode.HTML);
		labelErrorDate.setVisible(false);
		vlDate.addComponent(labelErrorDate);
		final FormLayout layoutParamDate = new FormLayout();
		layoutParamDate.setSizeFull();
		layoutParamDate.setSpacing(true);
		layoutParamDate.setMargin(true);
		vlDate.addComponent(layoutParamDate);
		sheet.addTab(vlDate, applicationContext.getMessage("formation.window.sheet.date", null, UI.getCurrent().getLocale()));

		/* Layout des param epreuves */
		final FormLayout layoutParamEpreuve = new FormLayout();
		layoutParamEpreuve.setSizeFull();
		layoutParamEpreuve.setSpacing(true);
		layoutParamEpreuve.setMargin(true);
		sheet.addTab(layoutParamEpreuve, applicationContext.getMessage("formation.window.sheet.epreuve", null, UI.getCurrent().getLocale()));

		/* Layout des param info Comp */
		final FormLayout layoutInfoComp = new FormLayout();
		layoutInfoComp.setSizeFull();
		layoutInfoComp.setSpacing(true);
		layoutInfoComp.setMargin(true);
		sheet.addTab(layoutInfoComp, applicationContext.getMessage("formation.window.sheet.infocomp", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		for (final String fieldName : FIELDS_ORDER_2) {
			layoutParamGen.addComponent(getField(fieldName));
		}

		for (final String fieldName : FIELDS_ORDER_3) {
			layoutParamDate.addComponent(getField(fieldName));
		}

		for (final String fieldName : FIELDS_ORDER_4) {
			layoutParamEpreuve.addComponent(getField(fieldName));
		}

		for (final String fieldName : FIELDS_ORDER_5) {
			layoutInfoComp.addComponent(getField(fieldName));
		}

		/* Box CGE */
		final RequiredComboBox<SiScolCentreGestion> comboBoxCGE = (RequiredComboBox<SiScolCentreGestion>) fieldGroup.getField(Formation_.siScolCentreGestion.getName());
		comboBoxCGE.setVisible(siScolService.hasCge() && !hideSiScol);

		/* Les box de liste complémentaire */
		final ComboBoxTypeDecision cbTypeDecisionFav = (ComboBoxTypeDecision) fieldGroup.getField(Formation_.typeDecisionFav.getName());
		final ComboBoxTypeDecision cbTypeDecisionFavListComp = (ComboBoxTypeDecision) fieldGroup.getField(Formation_.typeDecisionFavListComp.getName());

		/* Alimentation des listes */
		final List<TypeDecision> listeTypDec = typeDecisionController.getTypeDecisionsFavorableEnServiceByCtrCand(securityCtrCand.getCtrCand());
		cbTypeDecisionFav.setTypeDecisions(listeTypDec);
		cbTypeDecisionFavListComp.setTypeDecisions(listeTypDec);

		final RequiredCheckBox checkBoxListComp = (RequiredCheckBox) fieldGroup.getField(Formation_.temListCompForm.getName());

		/* On cherche le type de decision a placer par défaut */
		final TypeDecision typeDecisionDefault = getTypeDecisionByDefault(formation, ctrCand);

		/* Ajout du listener pour action sur la checkbox des liste comp-->activation ou
		 * desactivation des decisions fav list comp */
		checkBoxListComp.addValueChangeListener(e -> {
			cbTypeDecisionFavListComp.setBoxNeeded(checkBoxListComp.getValue(), typeDecisionDefault);
		});

		final ComboBoxCommission cbCommission = (ComboBoxCommission) fieldGroup.getField(Formation_.commission.getName());
		if (securityCtrCand.getIsGestAllCommission()) {
			cbCommission.filterListValue(ctrCand.getCommissions());
		} else {
			cbCommission.filterListValue(
				ctrCand.getCommissions().stream().filter(commission -> MethodUtils.isIdInListId(commission.getIdComm(), securityCtrCand.getListeIdCommission())).collect(Collectors.toList()));
		}

		/* Condition sur le type de formation --> Aucun, typeDiplome ou typeFormation */
		final RequiredComboBox<TypDiplome> cbTypeDip = (RequiredComboBox) fieldGroup.getField(Formation_.siScolTypDiplome.getName());
		final RequiredComboBox<TypeFormation> cbTypeForm = (RequiredComboBox) fieldGroup.getField(Formation_.typeFormation.getName());
		final String modeTypForm = parametreController.getModeTypeFormation();
		final Boolean isTypDip = ConstanteUtils.PARAM_MODE_TYPE_FORMATION_TYPE_DIP.equals(modeTypForm);
		final Boolean isTypForm = ConstanteUtils.PARAM_MODE_TYPE_FORMATION_NOMENCLATURE.equals(modeTypForm);
		cbTypeDip.setVisible(isTypDip);
		cbTypeDip.setRequired(isTypDip);
		cbTypeDip.setRequiredError(isTypDip ? applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()) : null);
		cbTypeForm.setVisible(isTypForm);
		cbTypeForm.setRequired(isTypForm);
		cbTypeForm.setRequiredError(isTypForm ? applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()) : null);

		/* Listener pour centrer la fenetre après ajout de langue */
		final I18nField i18nField = ((I18nField) fieldGroup.getField(Formation_.i18nInfoCompForm.getName()));
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
				comboBoxCGE.setEnabled(false);
				cbTypeDip.setEnabled(false);
			} else if (formation.getCodPegaseForm() != null) {
				/* On n'a plus le type de diplome dans la recherche d'objets maquette, mise à jour à la main */
				//cbTypeDip.setEnabled(false);
			}
		}

		cbTypeDecisionFavListComp.setBoxNeeded(checkBoxListComp.getValue(), typeDecisionDefault);

		/* Le delai ou la date de confirmation */
		final RequiredDateField datConfirm = (RequiredDateField) fieldGroup.getField(Formation_.datConfirmForm.getName());
		final RequiredIntegerField delaiConfirm = (RequiredIntegerField) fieldGroup.getField(Formation_.delaiConfirmForm.getName());
		final RequiredDateField datLCConfirm = (RequiredDateField) fieldGroup.getField(Formation_.datConfirmListCompForm.getName());
		final RequiredIntegerField delaiLCConfirm = (RequiredIntegerField) fieldGroup.getField(Formation_.delaiConfirmListCompForm.getName());

		datConfirm.addValueChangeListener(e -> disableFieldDelaiOrDateConfirm(datConfirm.getValue(), delaiConfirm));
		delaiConfirm.addValueChangeListener(e -> disableFieldDelaiOrDateConfirm(delaiConfirm.getValue(), datConfirm));
		datLCConfirm.addValueChangeListener(e -> disableFieldDelaiOrDateConfirm(datLCConfirm.getValue(), delaiLCConfirm));
		delaiLCConfirm.addValueChangeListener(e -> disableFieldDelaiOrDateConfirm(delaiLCConfirm.getValue(), datLCConfirm));

		disableFieldDelaiOrDateConfirm(datConfirm.getValue(), delaiConfirm);
		disableFieldDelaiOrDateConfirm(delaiConfirm.getValue(), datConfirm);
		disableFieldDelaiOrDateConfirm(datLCConfirm.getValue(), delaiLCConfirm);
		disableFieldDelaiOrDateConfirm(delaiLCConfirm.getValue(), datLCConfirm);

		/* Filtre des types de decisions */
		final ComboBoxTypeTraitement cbTypeTraitement = (ComboBoxTypeTraitement) fieldGroup.getField(Formation_.typeTraitement.getName());
		cbTypeTraitement.filterFinal();

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
				/* Efface les erreurs des onglets */
				sheet.effaceErrorSheet();
				labelErrorDate.setVisible(false);

				/* Si le code de formation existe dejà --> erreur */
				if (!formationController.isCodFormUnique((String) fieldGroup.getField(Formation_.codForm.getName()).getValue(), formation.getIdForm())) {
					Notification.show(applicationContext.getMessage("window.error.cod.nonuniq", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return;
				}

				fieldGroup.preCommit();

				if (!fieldGroup.isValid()) {
					sheet.validateSheet();
					return;
				}

				final String validationDate = getErrorMessageDate();
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
			} catch (final CommitException ce) {
				sheet.getSheetOnError(ce.getInvalidFields());
			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * Desactive les champs de confirmation
	 * @param value
	 * @param field
	 */
	private void disableFieldDelaiOrDateConfirm(final Object value, final AbstractField field) {
		if (value != null) {
			field.setValue(null);
			field.setEnabled(false);
		} else {
			field.setEnabled(true);
		}
	}

	/**
	 * Mise a jour des champs de diplome
	 */
	private void majFieldDip() {
		final RequiredTextField rtfCodEtpVetApo = (RequiredTextField) fieldGroup.getField(Formation_.codEtpVetApoForm.getName());
		final RequiredTextField rtfCodVrsVetApo = (RequiredTextField) fieldGroup.getField(Formation_.codVrsVetApoForm.getName());
		final RequiredTextField rtfCodDipApo = (RequiredTextField) fieldGroup.getField(Formation_.codDipApoForm.getName());
		final RequiredTextField rtfCodVrsDdiApo = (RequiredTextField) fieldGroup.getField(Formation_.codVrsVdiApoForm.getName());
		final RequiredTextField rtfLibDipApoo = (RequiredTextField) fieldGroup.getField(Formation_.libDipApoForm.getName());
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
	 * @param  formation
	 * @param  ctrCand
	 * @return           la decision par défaut du ctr cand
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
	 * @param  fieldName
	 * @return
	 */
	private Field<?> getField(final String fieldName) {
		final String caption = applicationContext.getMessage("formation.table." + fieldName, null, UI.getCurrent().getLocale());
		Field<?> field;
		if (fieldName.equals(Formation_.motCleForm.getName())) {
			field = fieldGroup.buildAndBind(caption, fieldName, RequiredTextArea.class);
		}

		else if (fieldName.equals(Formation_.codEtpVetApoForm.getName())
			|| fieldName.equals(Formation_.codVrsVetApoForm.getName())
			|| fieldName.equals(Formation_.libApoForm.getName())) {
			if (parametreController.getIsFormCodSiScolOblig()) {
				field = fieldGroup.buildAndBind(caption, fieldName, true);
			} else {
				field = fieldGroup.buildAndBind(caption, fieldName);
			}
			field.setEnabled(false);
		} else if (fieldName.equals(Formation_.codPegaseForm.getName())
			|| fieldName.equals(Formation_.libPegaseForm.getName())) {
			if (parametreController.getIsFormCodSiScolOblig()) {
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
			} else if (fieldName.equals(Formation_.urlForm.getName())) {
				field.addValidator(new UrlValidator(applicationContext.getMessage("validation.url.malformed", null, UI.getCurrent().getLocale())));
			}
		}

		field.setWidth(100, Unit.PERCENTAGE);
		return field;
	}

	/** @return true si les dates sont valides */
	private String getErrorMessageDate() {
		final RequiredDateField fieldDatConfirm = ((RequiredDateField) fieldGroup.getField(Formation_.datConfirmForm.getName()));
		final RequiredDateField fieldDatConfirmListComp = ((RequiredDateField) fieldGroup.getField(Formation_.datConfirmListCompForm.getName()));
		final RequiredDateField fieldDatDebDepot = ((RequiredDateField) fieldGroup.getField(Formation_.datDebDepotForm.getName()));
		final RequiredDateField fieldDatAnalyse = ((RequiredDateField) fieldGroup.getField(Formation_.datAnalyseForm.getName()));
		final RequiredDateField fieldDatFinDepo = ((RequiredDateField) fieldGroup.getField(Formation_.datFinDepotForm.getName()));
		final RequiredDateField fieldDatJury = ((RequiredDateField) fieldGroup.getField(Formation_.datJuryForm.getName()));
		final RequiredDateField fieldDatPubli = ((RequiredDateField) fieldGroup.getField(Formation_.datPubliForm.getName()));
		final RequiredDateField fieldDatRetour = ((RequiredDateField) fieldGroup.getField(Formation_.datRetourForm.getName()));

		return formationController.getTxtErrorEditDate(fieldDatConfirm.getValue(),
			fieldDatConfirmListComp.getValue(),
			fieldDatDebDepot.getValue(),
			fieldDatAnalyse.getValue(),
			fieldDatFinDepo.getValue(),
			fieldDatJury.getValue(),
			fieldDatPubli.getValue(),
			fieldDatRetour.getValue());
	}
}
