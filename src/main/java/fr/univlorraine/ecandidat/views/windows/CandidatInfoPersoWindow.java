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
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.DemoController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat_;
import fr.univlorraine.ecandidat.entities.ecandidat.Civilite;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.siscol.WSAdresse;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.IRequiredField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredDateField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxDepartement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxPays;

/** Fenêtre d'édition d'info perso
 *
 * @author Kevin Hergalant */
@Configurable(preConstruction = true)
public class CandidatInfoPersoWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 7131633614265914808L;

	private static final String CHAMPS_INE_AND_FIELD = "ineAndKey";

	public static final String[] FIELDS_ORDER_1 = {Candidat_.siScolPaysNat.getName(), CHAMPS_INE_AND_FIELD};

	public static final String[] FIELDS_ORDER_2 = {Candidat_.civilite.getName(), Candidat_.nomPatCandidat.getName(), Candidat_.nomUsuCandidat.getName(), Candidat_.prenomCandidat.getName(),
			Candidat_.autrePrenCandidat.getName(), Candidat_.datNaissCandidat.getName(), Candidat_.siScolPaysNaiss.getName(), Candidat_.siScolDepartement.getName(),
			Candidat_.libVilleNaissCandidat.getName(), Candidat_.langue.getName(), Candidat_.telCandidat.getName(), Candidat_.telPortCandidat.getName()};

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient DemoController demoController;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	private CandidatWindowListener candidatWindowListener;

	/* Composants */
	private CustomBeanFieldGroup<Candidat> fieldGroup;
	private OneClickButton btnEnregistrer;
	private Button btnNext;
	private Button btnAnnuler;

	/* Formulaire */
	private VerticalLayout layoutVParamINE;
	private FormLayout layoutParamINE;
	private FormLayout layoutParamGen;

	/* Champs */
	private ComboBoxPays paysField;
	private ComboBoxDepartement dptField;
	private RequiredTextField nomPatCandidatField;
	private RequiredTextField nomUsuCandidatField;
	private RequiredTextField prenomCandidatField;
	private RequiredTextField autrePrenCandidatField;
	private RequiredTextField libVilleNaissCandidatField;
	private RequiredTextField telCandidatField;
	private RequiredTextField telPortCandidatField;
	private RequiredComboBox<Civilite> civiliteField;
	private RequiredDateField datNaissCandidatField;
	private RequiredTextField ineAndKeyField;
	private ComboBoxPays natField;

	/* Les données apogée */
	private WSIndividu individuApogee;
	private Boolean needToDeleteDataApogee = false;

	/** Crée une fenêtre d'édition de candidat
	 *
	 * @param candidat
	 *            le candidat à éditer */
	public CandidatInfoPersoWindow(final Candidat candidat) {
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
		setCaption(applicationContext.getMessage("infoperso.window", null, UI.getCurrent().getLocale()));

		/* Formulaire */
		fieldGroup = new CustomBeanFieldGroup<>(Candidat.class, ConstanteUtils.TYP_FORM_CANDIDAT);
		fieldGroup.setItemDataSource(candidat);

		/* Tabsheet */

		/* Layout des param généraux */
		layoutParamINE = new FormLayout();
		layoutParamINE.setSizeFull();
		layoutParamINE.setSpacing(true);
		layoutParamINE.setMargin(true);
		layoutParamINE.setVisible(true);
		layoutVParamINE = new VerticalLayout();
		layoutVParamINE.setSizeFull();
		layoutVParamINE.addComponent(new Label(applicationContext.getMessage("infoperso.ine.info", null, UI.getCurrent().getLocale())));
		layoutVParamINE.addComponent(layoutParamINE);
		layout.addComponent(layoutVParamINE);

		/* Layout des param généraux */
		layoutParamGen = new FormLayout();
		layoutParamGen.setSizeFull();
		layoutParamGen.setSpacing(true);
		layoutParamGen.setMargin(true);
		layoutParamGen.setVisible(false);
		layout.addComponent(layoutParamGen);

		/* Formulaire */

		for (String fieldName : FIELDS_ORDER_1) {
			String caption = applicationContext.getMessage("infoperso.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			layoutParamINE.addComponent(field);
		}

		for (String fieldName : FIELDS_ORDER_2) {
			if (fieldName.equals(Candidat_.langue.getName()) && cacheController.getLangueEnServiceWithoutDefault().size() == 0) {
				continue;
			}
			String caption = applicationContext.getMessage("infoperso.table." + fieldName, null, UI.getCurrent().getLocale());
			Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			layoutParamGen.addComponent(field);
		}

		initForm();

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new Button(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);

		btnNext = new Button(applicationContext.getMessage("btnNext", null, UI.getCurrent().getLocale()), FontAwesome.ARROW_CIRCLE_O_RIGHT);
		btnNext.setDisableOnClick(true);
		btnNext.addClickListener(e -> {
			nextIne(candidat);
		});
		buttonsLayout.addComponent(btnNext);
		buttonsLayout.setComponentAlignment(btnNext, Alignment.MIDDLE_RIGHT);

		btnEnregistrer = new OneClickButton(applicationContext.getMessage("btnSave", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnEnregistrer.setVisible(false);
		btnEnregistrer.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnEnregistrer.addClickListener(e -> {
			try {
				if (candidatController.isINEPresent(ineAndKeyField.getValue(), candidat)) {
					return;
				}

				if (!candidatController.checkDateNaissance(datNaissCandidatField.getLocalValue())) {
					return;
				}

				toUpperCase(nomPatCandidatField);
				toUpperCase(nomUsuCandidatField);
				toUpperCase(prenomCandidatField);
				toUpperCase(autrePrenCandidatField);
				toUpperCase(libVilleNaissCandidatField);

				/* Valide la saisie */
				fieldGroup.commit();

				candidatWindowListener.btnOkClick(candidat, individuApogee, needToDeleteDataApogee);

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

	/** CLique sur le bouton next
	 *
	 * @param ineField
	 * @param natField
	 */
	private void nextIne(final Candidat candidat) {
		/* Verif que le champs INE est valide */
		Boolean valid = validateIneField(candidat);
		/* Si champs INE valide */
		if (valid) {
			try {

				/*
				 * On vérifie que :
				 * On est en mode Apogée
				 * Que le champs INE n'est pas vide
				 * Que le champs INE a changé
				 * Que le champs clé INE a changé
				 */
				String ineAndKeyCandidat = null;
				if (candidat.getIneCandidat() != null && candidat.getCleIneCandidat() != null) {
					ineAndKeyCandidat = candidat.getIneCandidat() + candidat.getCleIneCandidat();
				}

				if (parametreController.getSiScolMode().equals(ConstanteUtils.SI_SCOL_APOGEE)
						&& (!(ineAndKeyField.getValue() == null) && !ineAndKeyField.getValue().equals("") && !ineAndKeyField.getValue().equals(ineAndKeyCandidat))) {

					/* Vérification qu'il n'y a pas de lock sur le canddiat + adresse + info perso */
					if (candidatController.isLockedForImportApo(candidat.getCompteMinima())) {
						btnNext.setEnabled(true);
						return;
					}

					/* Récupération des infos d'apogée */
					/* individuApogee peut ne pas être nul si le supannEtuId etait renseigné dans le compte à minima, on a déjà été cherché sont compte */
					if (individuApogee == null) {
						individuApogee = candidatController.recupInfoCandidat(candidat.getCompteMinima().getSupannEtuIdCptMin(), ineAndKeyField.getValue());
					}

					/* Si tout est ok-->on récupère les nouvelles infos apogée et on traite ces données */
					if (individuApogee != null) {
						String prenom = individuApogee.getLibPr1Ind();
						String nom = individuApogee.getLibNomPatInd() == null ? individuApogee.getLibNomUsuInd() : individuApogee.getLibNomPatInd();
						String date = individuApogee.getDateNaiInd() == null ? null : simpleDateFormat.format(individuApogee.getDateNaiInd());

						ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("infoperso.confirm.apogee", new Object[] {prenom, nom,
								date}, UI.getCurrent().getLocale()), applicationContext.getMessage("infoperso.confirm.apogeeTitle", null, UI.getCurrent().getLocale()));
						confirmWindow.addBtnOuiListener(e -> {
							fieldGroup.getItemDataSource().getBean().setTemUpdatableCandidat(false);
							initSecondLayout();
							initDataApogee();
						});
						confirmWindow.addBtnNonListener(e -> {
							btnNext.setEnabled(true);
						});
						UI.getCurrent().addWindow(confirmWindow);
					}
					/* Tout n'est pas OK : les données apogée n'ont pas été trouvées ***/
					else {
						/* Si précédemment, le candidat provenait d'apogée, on doit effacer les données saisies */
						if (!candidat.getTemUpdatableCandidat()) {
							cleanDataApogee(candidat);
						} else {
							ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("infoperso.confirm.not.apogee", null, UI.getCurrent().getLocale()), applicationContext.getMessage("infoperso.confirm.changeto.not.apogeeTitle", null, UI.getCurrent().getLocale()));
							confirmWindow.addBtnOuiListener(e -> {
								fieldGroup.getItemDataSource().getBean().setTemUpdatableCandidat(true);
								initSecondLayout();
							});
							confirmWindow.addBtnNonListener(e -> {
								btnNext.setEnabled(true);
							});
							UI.getCurrent().addWindow(confirmWindow);
						}
					}
				} else if (!candidat.getTemUpdatableCandidat()) {
					disableChampsApogee();
					initSecondLayout();
				} else {
					fieldGroup.getItemDataSource().getBean().setTemUpdatableCandidat(true);
					initSecondLayout();
				}
				center();
			} catch (SiScolException e) {
				Notification.show(applicationContext.getMessage("siscol.connect.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
		} else {
			btnNext.setEnabled(true);
		}
	}

	/** Nettoie les donénes apogée ramenées précédement
	 *
	 * @param candidat
	 */
	private void cleanDataApogee(final Candidat candidat) {
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("infoperso.confirm.changeto.not.apogee", null, UI.getCurrent().getLocale()), applicationContext.getMessage("infoperso.confirm.changeto.not.apogeeTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			needToDeleteDataApogee = true;
			dptField.setValue(null);
			paysField.setValue(cacheController.getPaysFrance());
			nomPatCandidatField.setValue(null);
			nomUsuCandidatField.setValue(null);
			prenomCandidatField.setValue(null);
			autrePrenCandidatField.setValue(null);
			libVilleNaissCandidatField.setValue(null);
			telCandidatField.setValue(null);
			telPortCandidatField.setValue(null);
			civiliteField.setValue(null);
			datNaissCandidatField.setValue(null);
			fieldGroup.getItemDataSource().getBean().setTemUpdatableCandidat(true);
			initSecondLayout();
		});
		confirmWindow.addBtnNonListener(e -> {
			btnNext.setEnabled(true);
		});
		UI.getCurrent().addWindow(confirmWindow);

	}

	/** Initialise le second layout */
	private void initSecondLayout() {
		layoutVParamINE.setVisible(false);
		layoutParamGen.setVisible(true);
		btnNext.setEnabled(true);
		btnNext.setVisible(false);
		btnEnregistrer.setVisible(true);
	}

	/** Disable les champs lorsqu'on es ten mode apogée */
	private void disableChampsApogee() {
		datNaissCandidatField.setEnabled(false);
		civiliteField.setEnabled(false);
		libVilleNaissCandidatField.setEnabled(false);
		autrePrenCandidatField.setEnabled(false);
		prenomCandidatField.setEnabled(false);
		nomUsuCandidatField.setEnabled(false);
		nomPatCandidatField.setEnabled(false);
		dptField.setEnabled(false);
		paysField.setEnabled(false);
	}

	/** Initialise les données avec les valuer apogée */
	private void initDataApogee() {
		if (individuApogee == null) {
			return;
		}

		if (individuApogee.getIsWs()) {
			/* Champs pays naissance */
			paysField.setValue(tableRefController.getPaysByCode(individuApogee.getCodPayNai()));

			/* Champs dpt naissance */
			dptField.setValue(tableRefController.getDepartementByCode(individuApogee.getCodDepNai()));
		} else {
			/* Champs pays naissance */
			paysField.setValue(candidatController.getPaysNaissance(individuApogee.getCodTypDepPayNai(), individuApogee.getCodDepPayNai()));

			/* Champs dpt naissance */
			dptField.setValue(candidatController.getDepartementNaissance(individuApogee.getCodTypDepPayNai(), individuApogee.getCodDepPayNai()));
		}

		/* Champs nomPatCandidat */
		nomPatCandidatField.setValue(individuApogee.getLibNomPatInd());

		/* Champs nomUsuCandidat */
		nomUsuCandidatField.setValue(individuApogee.getLibNomUsuInd());

		/* Champs nomUsuCandidat */
		prenomCandidatField.setValue(individuApogee.getLibPr1Ind());

		/* Champs autrePrenCandidat */
		autrePrenCandidatField.setValue(individuApogee.getLibPr2Ind());

		/* Champs libVilleNaissCandidat */
		libVilleNaissCandidatField.setValue(individuApogee.getLibVilNaiEtu());

		WSAdresse adr = individuApogee.getAdresse();
		if (adr != null) {
			/* Champs telCandidat */
			telCandidatField.setValue(adr.getNumTel());

			/* Champs telPortCandidat */
			telPortCandidatField.setValue(adr.getNumTelPort());
		}

		/* Champs civilite */
		civiliteField.setValue(candidatController.getCiviliteByCodeApo(individuApogee.getCodCiv()));
		/* Champs civilite */
		datNaissCandidatField.setValue(individuApogee.getDateNaiInd());
		disableChampsApogee();
	}

	/** Valide l'ine
	 *
	 * @param natField
	 * @param ineField
	 * @param candidat
	 * @param candidat
	 * @return true si l'ine est ok */
	private Boolean validateIneField(final Candidat candidat) {
		try {
			validateField(natField);
			validateField(ineAndKeyField);

			/* Passage des champs en maj */
			toUpperCase(ineAndKeyField);

			try {
				if (!demoController.getDemoMode() && !candidatController.checkStudentINE(ineAndKeyField.getValue())) {
					Notification.show(applicationContext.getMessage("infoperso.ine.not.conform", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Notification.show(applicationContext.getMessage("infoperso.ine.verif.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return false;
			}

			if (!demoController.getDemoMode() && candidatController.isINEPresent(ineAndKeyField.getValue(), candidat)) {
				return false;
			}
		} catch (InvalidValueException e) {
			return false;
		}
		return true;
	}

	/** Valide un champs
	 *
	 * @param field
	 * @throws InvalidValueException
	 */
	private void validateField(final Field<?> field) throws InvalidValueException {
		try {
			IRequiredField reqField = (IRequiredField) field;
			reqField.preCommit();
			field.validate();
		} catch (InvalidValueException e) {
			throw e;
		}
	}

	/** Passe le champs en majuscule
	 *
	 * @param field
	 */
	private void toUpperCase(final RequiredTextField field) {
		if (field != null && field.getValue() != null && !field.getValue().equals("")) {
			field.setValue(MethodUtils.cleanForApogee(field.getValue()));
		}
	}

	/** Initialise le formulaire */
	@SuppressWarnings("unchecked")
	private void initForm() {
		Candidat candidat = fieldGroup.getItemDataSource().getBean();

		/* Initialisation des champs */
		paysField = (ComboBoxPays) fieldGroup.getField(Candidat_.siScolPaysNaiss.getName());
		dptField = (ComboBoxDepartement) fieldGroup.getField(Candidat_.siScolDepartement.getName());
		nomPatCandidatField = (RequiredTextField) fieldGroup.getField(Candidat_.nomPatCandidat.getName());
		nomUsuCandidatField = (RequiredTextField) fieldGroup.getField(Candidat_.nomUsuCandidat.getName());
		prenomCandidatField = (RequiredTextField) fieldGroup.getField(Candidat_.prenomCandidat.getName());
		autrePrenCandidatField = (RequiredTextField) fieldGroup.getField(Candidat_.autrePrenCandidat.getName());
		libVilleNaissCandidatField = (RequiredTextField) fieldGroup.getField(Candidat_.libVilleNaissCandidat.getName());
		telCandidatField = (RequiredTextField) fieldGroup.getField(Candidat_.telCandidat.getName());
		telPortCandidatField = (RequiredTextField) fieldGroup.getField(Candidat_.telPortCandidat.getName());
		civiliteField = (RequiredComboBox<Civilite>) fieldGroup.getField(Candidat_.civilite.getName());
		datNaissCandidatField = (RequiredDateField) fieldGroup.getField(Candidat_.datNaissCandidat.getName());
		ineAndKeyField = (RequiredTextField) fieldGroup.getField(CHAMPS_INE_AND_FIELD);
		if (candidat.getIneCandidat() != null && candidat.getCleIneCandidat() != null) {
			ineAndKeyField.setValue(candidat.getIneCandidat() + candidat.getCleIneCandidat());
		}
		ineAndKeyField.setMaxLength(11);
		// cleIneField.setWidthUndefined();
		// ineField.setValue("1204014627");
		natField = (ComboBoxPays) fieldGroup.getField(Candidat_.siScolPaysNat.getName());

		/* No tel expression reguliere */
		RegexpValidator telValidator = new RegexpValidator(ConstanteUtils.regExNoTel, applicationContext.getMessage("validation.error.tel", null, Locale.getDefault()));
		telCandidatField.addValidator(telValidator);
		telPortCandidatField.addValidator(telValidator);

		// si le candidat à un INE null mais un supannEtuId, on va charger le candidat depuis apogee. Si on le trouve, on value l'INE et on bloque la saisie de l'INE
		if (candidat.getIneCandidat() == null && candidat.getCompteMinima().getSupannEtuIdCptMin() != null && !candidat.getCompteMinima().getSupannEtuIdCptMin().equals("")) {
			try {
				individuApogee = candidatController.recupInfoCandidat(candidat.getCompteMinima().getSupannEtuIdCptMin(), null, null);
				if (individuApogee != null && individuApogee.getCodNneInd() != null && individuApogee.getCodCleNneInd() != null) {
					ineAndKeyField.setValue(individuApogee.getCodNneInd() + individuApogee.getCodCleNneInd());
					ineAndKeyField.setEnabled(false);
				}
			} catch (SiScolException e1) {
				Notification.show(applicationContext.getMessage("siscol.connect.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				close();
			}
			// si le candidat à un INE et supannEtuId valué, on bloque la saisie de l'INE
		} else if (candidat.getIneCandidat() != null && !candidat.getIneCandidat().equals("") && candidat.getCompteMinima().getSupannEtuIdCptMin() != null
				&& !candidat.getCompteMinima().getSupannEtuIdCptMin().equals("")) {
			ineAndKeyField.setEnabled(false);
		}

		/* ajout des listeners */
		/* Champs nationalité */
		natField.setToNationalite(applicationContext.getMessage("infoperso.table.siScolPaysNat.suggest", null, UI.getCurrent().getLocale()));
		/*
		 * natField.addValueChangeListener(e->{
		 * if (e.getProperty().getValue() instanceof SiScolPays){
		 * SiScolPays nationaliteSelected = (SiScolPays)e.getProperty().getValue() ;
		 * initNationalite(nationaliteSelected);
		 * }
		 * });
		 */
		/* Champs pays */
		paysField.addValueChangeListener(e -> {
			if (e.getProperty().getValue() instanceof SiScolPays) {
				SiScolPays paysSelected = (SiScolPays) e.getProperty().getValue();
				initPays(paysSelected, dptField, null);
			}
		});

		/* Valeur defaut */
		/* Champs nationalité */
		if (candidat.getSiScolPaysNat() == null) {
			natField.setValue(cacheController.getPaysFrance());
		} else {
			natField.setValue(candidat.getSiScolPaysNat());
			// initNationalite(candidat.getSiScolPaysNat());
		}
		/* Champs pays */
		if (candidat.getSiScolPaysNaiss() == null) {
			paysField.setValue(cacheController.getPaysFrance());
		} else {
			paysField.setValue(candidat.getSiScolPaysNaiss());
			initPays(candidat.getSiScolPaysNaiss(), dptField, candidat.getSiScolDepartement());
		}
	}

	/** Initialise la nationalité
	 *
	 * @param nationalite
	 * @param INEField
	 * @param cleIneField
	 */
	/*
	 * private void initNationalite(SiScolPays nationalite){
	 * changeRequired(ineField,candidatController.getINEObligatoire(nationalite));
	 * changeRequired(cleIneField,candidatController.getINEObligatoire(nationalite));
	 * }
	 */

	/** Initialise la combo pays
	 *
	 * @param pays
	 * @param dptField
	 * @param siScolDepartement
	 */
	private void initPays(final SiScolPays pays, final ComboBoxDepartement dptField, final SiScolDepartement siScolDepartement) {
		if (pays != null && pays.getCodPay().equals(ConstanteUtils.PAYS_CODE_FRANCE)) {
			changeRequired(dptField, true);
			dptField.setVisible(true);
			if (siScolDepartement != null) {
				dptField.setValue(siScolDepartement);
			} else {
				dptField.setValue(null);
			}
		} else {
			changeRequired(dptField, false);
			dptField.setVisible(false);
			dptField.setValue(null);
		}
	}

	/** Change l'etat obligatoire d'un champs
	 *
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

	/** Défini le 'CandidatWindowListener' utilisé
	 *
	 * @param candidatWindowListener
	 */
	public void addCandidatWindowListener(final CandidatWindowListener candidatWindowListener) {
		this.candidatWindowListener = candidatWindowListener;
	}

	/** Interface pour récupérer un click sur Oui ou Non. */
	public interface CandidatWindowListener extends Serializable {

		/** Appelé lorsque Oui est cliqué.
		 *
		 * @param candidat
		 * @param individu
		 * @param needToDeleteDataApogee
		 */
		public void btnOkClick(Candidat candidat, WSIndividu individu, Boolean needToDeleteDataApogee);

	}
}
