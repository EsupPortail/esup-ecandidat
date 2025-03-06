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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.validator.RegexpValidator;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.DemoController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat_;
import fr.univlorraine.ecandidat.entities.ecandidat.Civilite;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommuneNaiss;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.siscol.WSAdresse;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.IRequiredField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredDateField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxCommuneNaiss;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxDepartement;
import fr.univlorraine.ecandidat.vaadin.form.siscol.ComboBoxPays;
import jakarta.annotation.Resource;

/**
 * Fenêtre d'édition d'info perso
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CandidatInfoPersoWindow extends Window {

	private static final String CHAMPS_INE_AND_FIELD = "ineAndKey";

	public static final String[] FIELDS_ORDER_1 = { Candidat_.siScolPaysNat.getName(), CHAMPS_INE_AND_FIELD };

	public static final String[] FIELDS_ORDER_2 = { Candidat_.civilite.getName(),
		Candidat_.nomPatCandidat.getName(),
		Candidat_.nomUsuCandidat.getName(),
		Candidat_.prenomCandidat.getName(),
		Candidat_.autrePrenCandidat.getName(),
		Candidat_.datNaissCandidat.getName(),
		Candidat_.siScolPaysNaiss.getName(),
		Candidat_.siScolDepartement.getName(),
		Candidat_.siScolCommuneNaiss.getName(),
		Candidat_.libVilleNaissCandidat.getName(),
		Candidat_.langue.getName(),
		Candidat_.telCandidat.getName(),
		Candidat_.telPortCandidat.getName() };

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

	private final DateTimeFormatter simpleDateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private CandidatWindowListener candidatWindowListener;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

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
	private ComboBoxCommuneNaiss commField;
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
	private WSIndividu individuSiScol;
	private Boolean needToDeleteDataSiScol = false;

	/**
	 * Crée une fenêtre d'édition de candidat
	 * @param candidat
	 *                    le candidat à éditer
	 */
	public CandidatInfoPersoWindow(final Candidat candidat) {
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

		for (final String fieldName : FIELDS_ORDER_1) {
			final String caption = applicationContext.getMessage("infoperso.table." + fieldName, null, UI.getCurrent().getLocale());
			final Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			layoutParamINE.addComponent(field);
		}

		for (final String fieldName : FIELDS_ORDER_2) {
			/* Affichage ou non commune naissance */
//			if (fieldName.equals(Candidat_.siScolCommuneNaiss.getName()) && !siScolService.hasCommuneNaissance()) {
//				continue;
//			}

			if (fieldName.equals(Candidat_.langue.getName()) && cacheController.getLangueEnServiceWithoutDefault().size() == 0) {
				continue;
			}
			final String caption = applicationContext.getMessage("infoperso.table." + fieldName, null, UI.getCurrent().getLocale());
			final Field<?> field = fieldGroup.buildAndBind(caption, fieldName);
			field.setWidth(100, Unit.PERCENTAGE);
			layoutParamGen.addComponent(field);
		}

		initForm();

		/* Ajoute les boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
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

				candidatWindowListener.btnOkClick(candidat, individuSiScol, needToDeleteDataSiScol);

				/* Ferme la fenêtre */
				close();
			} catch (final CommitException ce) {

			}
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * CLique sur le bouton next
	 * @param ineField
	 * @param natField
	 */
	private void nextIne(final Candidat candidat) {
		/* Verif que le champs INE est valide */
		final Boolean valid = validateIneField(candidat);
		/* Si champs INE valide */
		if (valid) {
			try {

				/* On vérifie que :
				 * On est en mode Apogée
				 * Que le champs INE n'est pas vide
				 * Que le champs INE a changé
				 * Que le champs clé INE a changé */
				String ineAndKeyCandidat = null;
				if (candidat.getIneCandidat() != null && candidat.getCleIneCandidat() != null) {
					ineAndKeyCandidat = candidat.getIneCandidat() + candidat.getCleIneCandidat();
				}

				if (siScolService.hasSyncEtudiant()
					&& (!(ineAndKeyField.getValue() == null) && !ineAndKeyField.getValue().equals("") && !ineAndKeyField.getValue().equals(ineAndKeyCandidat))) {

					/* Vérification qu'il n'y a pas de lock sur le canddiat + adresse + info perso */
					if (candidatController.isLockedForImportApo(candidat.getCompteMinima())) {
						btnNext.setEnabled(true);
						return;
					}

					/* Récupération des infos d'apogée */
					/* individuSiScol peut ne pas être nul si le supannEtuId etait renseigné dans le compte à minima, on a déjà été cherché sont compte */
					if (individuSiScol == null) {
						individuSiScol = candidatController.recupInfoCandidat(candidat.getCompteMinima().getSupannEtuIdCptMin(), ineAndKeyField.getValue());
					}

					/* Si tout est ok-->on récupère les nouvelles infos apogée et on traite ces données */
					if (individuSiScol != null) {
						final String prenom = individuSiScol.getLibPr1Ind();
						final String nom = individuSiScol.getLibNomPatInd() == null ? individuSiScol.getLibNomUsuInd() : individuSiScol.getLibNomPatInd();
						final String date = individuSiScol.getDateNaiInd() == null ? null : simpleDateFormat.format(individuSiScol.getDateNaiInd());

						final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("infoperso.confirm.apogee",
							new Object[] { prenom,
								nom,
								date },
							UI.getCurrent().getLocale()), applicationContext.getMessage("infoperso.confirm.apogeeTitle", null, UI.getCurrent().getLocale()));
						confirmWindow.addBtnOuiListener(e -> {
							fieldGroup.getItemDataSource().getBean().setTemUpdatableCandidat(false);
							initSecondLayout();
							initDataSiScol();
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
							cleanDataSiScol(candidat);
						} else {
							final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("infoperso.confirm.not.apogee", null, UI.getCurrent().getLocale()),
								applicationContext.getMessage("infoperso.confirm.changeto.not.apogeeTitle", null, UI.getCurrent().getLocale()));
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
					disableChampsSiScol();
					initSecondLayout();
				} else {
					fieldGroup.getItemDataSource().getBean().setTemUpdatableCandidat(true);
					initSecondLayout();
				}
				center();
			} catch (final SiScolException e) {
				Notification.show(applicationContext.getMessage("siscol.connect.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
		} else {
			btnNext.setEnabled(true);
		}
	}

	/**
	 * Nettoie les donénes apogée ramenées précédement
	 * @param candidat
	 */
	private void cleanDataSiScol(final Candidat candidat) {
		final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("infoperso.confirm.changeto.not.apogee", null, UI.getCurrent().getLocale()),
			applicationContext.getMessage("infoperso.confirm.changeto.not.apogeeTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			needToDeleteDataSiScol = true;
			if (dptField != null) {
				dptField.setValue(null);
			}
			if (commField != null) {
				commField.setValue(null);
			}
			paysField.setValue(cacheController.getPaysFrance());
			nomPatCandidatField.setValue(null);
			nomUsuCandidatField.setValue(null);
			prenomCandidatField.setValue(null);
			autrePrenCandidatField.setValue(null);
			if (libVilleNaissCandidatField != null) {
				libVilleNaissCandidatField.setValue(null);
			}
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
	private void disableChampsSiScol() {
		datNaissCandidatField.setEnabled(false);
		civiliteField.setEnabled(false);
		if (libVilleNaissCandidatField != null) {
			libVilleNaissCandidatField.setEnabled(false);
		}
		autrePrenCandidatField.setEnabled(false);
		prenomCandidatField.setEnabled(false);
		nomUsuCandidatField.setEnabled(false);
		nomPatCandidatField.setEnabled(false);
		dptField.setEnabled(false);
		commField.setEnabled(false);
		paysField.setEnabled(false);
	}

	/** Initialise les données avec les valuer apogée */
	private void initDataSiScol() {
		if (individuSiScol == null) {
			return;
		}
		if (individuSiScol.getIsWs()) {
			/* Champs pays naissance */
			paysField.setValue(tableRefController.getPaysByCode(individuSiScol.getCodPayNai()));

			/* Champs dpt naissance */
			dptField.setValue(tableRefController.getDepartementByCode(individuSiScol.getCodDepNai()));

			/* Champs commune naissance */
			commField.setValue(tableRefController.getCommuneNaissanceByCode(individuSiScol.getCodCommNai()));
		} else {
			/* Champs pays naissance */
			paysField.setValue(candidatController.getPaysNaissance(individuSiScol.getCodTypDepPayNai(), individuSiScol.getCodDepPayNai()));

			/* Champs dpt naissance */
			dptField.setValue(candidatController.getDepartementNaissance(individuSiScol.getCodTypDepPayNai(), individuSiScol.getCodDepPayNai()));

			/* Champs commune naissance */
			commField.setValue(tableRefController.getCommuneNaissanceByCode(individuSiScol.getCodCommNai()));
		}

		/* Champs nomPatCandidat */
		nomPatCandidatField.setValue(individuSiScol.getLibNomPatInd());

		/* Champs nomUsuCandidat */
		nomUsuCandidatField.setValue(individuSiScol.getLibNomUsuInd());

		/* Champs nomUsuCandidat */
		prenomCandidatField.setValue(individuSiScol.getLibPr1Ind());

		/* Champs autrePrenCandidat */
		autrePrenCandidatField.setValue(individuSiScol.getLibPr2Ind());

		/* Champs libVilleNaissCandidat */
		libVilleNaissCandidatField.setValue(individuSiScol.getLibVilNaiEtu());

		final WSAdresse adr = individuSiScol.getAdresse();
		if (adr != null) {
			/* Champs telCandidat */
			telCandidatField.setValue(adr.getNumTel());

			/* Champs telPortCandidat */
			telPortCandidatField.setValue(adr.getNumTelPort());
		}

		/* Champs civilite */
		civiliteField.setValue(candidatController.getCiviliteByCodeSiScol(individuSiScol.getCodCiv()));
		/* Champs civilite */
		datNaissCandidatField.setLocalValue(individuSiScol.getDateNaiInd());
		disableChampsSiScol();
	}

	/**
	 * Valide l'ine
	 * @param  natField
	 * @param  ineField
	 * @param  candidat
	 * @param  candidat
	 * @return          true si l'ine est ok
	 */
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
			} catch (final Exception e) {
				Notification.show(applicationContext.getMessage("infoperso.ine.verif.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return false;
			}

			if (!demoController.getDemoMode() && candidatController.isINEPresent(ineAndKeyField.getValue(), candidat)) {
				return false;
			}
		} catch (final InvalidValueException e) {
			return false;
		}
		return true;
	}

	/**
	 * Valide un champs
	 * @param  field
	 * @throws InvalidValueException
	 */
	private void validateField(final Field<?> field) throws InvalidValueException {
		try {
			final IRequiredField reqField = (IRequiredField) field;
			reqField.preCommit();
			field.validate();
		} catch (final InvalidValueException e) {
			throw e;
		}
	}

	/**
	 * Passe le champs en majuscule
	 * @param field
	 */
	private void toUpperCase(final RequiredTextField field) {
		if (field != null && field.getValue() != null && !field.getValue().equals("")) {
			field.setValue(MethodUtils.cleanForSiScol(field.getValue()));
		}
	}

	/** Initialise le formulaire */
	@SuppressWarnings("unchecked")
	private void initForm() {
		final Candidat candidat = fieldGroup.getItemDataSource().getBean();

		/* Initialisation des champs */
		paysField = (ComboBoxPays) fieldGroup.getField(Candidat_.siScolPaysNaiss.getName());
		dptField = (ComboBoxDepartement) fieldGroup.getField(Candidat_.siScolDepartement.getName());
		commField = (ComboBoxCommuneNaiss) fieldGroup.getField(Candidat_.siScolCommuneNaiss.getName());
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
		final RegexpValidator telValidator = new RegexpValidator(ConstanteUtils.REGEX_TEL, applicationContext.getMessage("validation.error.tel", null, UI.getCurrent().getLocale()));
		telCandidatField.addValidator(telValidator);
		telPortCandidatField.addValidator(telValidator);

		// si le candidat à un INE null mais un supannEtuId, on va charger le candidat depuis apogee. Si on le trouve, on value l'INE et on bloque la saisie de l'INE
		if (candidat.getIneCandidat() == null && candidat.getCompteMinima().getSupannEtuIdCptMin() != null && !candidat.getCompteMinima().getSupannEtuIdCptMin().equals("")) {
			try {
				individuSiScol = candidatController.recupInfoCandidat(candidat.getCompteMinima().getSupannEtuIdCptMin(), null, null);
				if (individuSiScol != null && individuSiScol.getCodNneInd() != null && individuSiScol.getCodCleNneInd() != null) {
					ineAndKeyField.setValue(individuSiScol.getCodNneInd() + individuSiScol.getCodCleNneInd());
					ineAndKeyField.setEnabled(false);
				}
			} catch (final SiScolException e1) {
				Notification.show(applicationContext.getMessage("siscol.connect.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				close();
			}
			// si le candidat à un INE et supannEtuId valué, on bloque la saisie de l'INE
		} else if (candidat.getIneCandidat() != null && !candidat.getIneCandidat().equals("")
			&& candidat.getCompteMinima().getSupannEtuIdCptMin() != null
			&& !candidat.getCompteMinima().getSupannEtuIdCptMin().equals("")) {
			ineAndKeyField.setEnabled(false);
		}

		/* ajout des listeners */
		/* Champs nationalité */
		natField.setToNationalite(applicationContext.getMessage("infoperso.table.siScolPaysNat.suggest", null, UI.getCurrent().getLocale()));
		/* natField.addValueChangeListener(e->{
		 * if (e.getProperty().getValue() instanceof SiScolPays){
		 * SiScolPays nationaliteSelected = (SiScolPays)e.getProperty().getValue() ;
		 * initNationalite(nationaliteSelected);
		 * }
		 * }); */
		/* Champs pays */
		paysField.addValueChangeListener(e -> {
			if (e.getProperty().getValue() instanceof SiScolPays) {
				final SiScolPays paysSelected = (SiScolPays) e.getProperty().getValue();
				//initPays(paysSelected, dptField, null, commField, libVilleNaissCandidatField);
				initFields(paysSelected, null, null, null);
			}
		});

		/* champs departement */
		dptField.addValueChangeListener(e -> {
			final SiScolDepartement departementSelected = (SiScolDepartement) e.getProperty().getValue();
			/* Hack car lancé au moment du commit */
			if (dptField.isVisible()) {
				initFields((SiScolPays) paysField.getValue(), departementSelected, null, null);
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
			initFields(candidat.getSiScolPaysNaiss(), candidat.getSiScolDepartement(), candidat.getSiScolCommuneNaiss(), candidat.getLibVilleNaissCandidat());
		}
	}

	private void initFields(final SiScolPays pays, final SiScolDepartement siScolDepartement, final SiScolCommuneNaiss siScolCommNaiss, final String libVilleNaiss) {
		if (pays != null && pays.isCodePays(siScolService.getCodPaysFrance())) {
			/* Gestion des Departements */
			changeRequired(dptField, true);
			dptField.setVisible(true);
			dptField.setValue(siScolDepartement);

			/* Chargement des communes */
			List<SiScolCommuneNaiss> listeCommune = new ArrayList<>();
			if (siScolDepartement != null && siScolService.hasCommuneNaissance()) {
				listeCommune = tableRefController.listeCommuneNaissByDepartement(siScolDepartement).stream().filter(e -> e.getTemEnSveComNaiss()).collect(Collectors.toList());
			}

			/* Gestion des communes */
			changeRequired(commField, siScolService.hasCommuneNaissance());
			commField.setListCommune(listeCommune);
			commField.setVisible(siScolService.hasCommuneNaissance());
			/* Calcul de la commune de naissance */
			SiScolCommuneNaiss siScolCommNaissCalc = null;
			if (siScolDepartement != null && listeCommune.size() > 0) {
				if (siScolCommNaiss != null) {
					siScolCommNaissCalc = siScolCommNaiss;
				} else if (listeCommune.size() == 1) {
					siScolCommNaissCalc = listeCommune.get(0);
				}
			}
			commField.setValue(siScolCommNaissCalc);
			commField.setEnabled(siScolDepartement != null && listeCommune.size() > 0);

			/* Gestion de la ville de naissance */
			changeRequired(libVilleNaissCandidatField, !siScolService.hasCommuneNaissance());
			libVilleNaissCandidatField.setVisible(!siScolService.hasCommuneNaissance());
			libVilleNaissCandidatField.setValue(siScolDepartement != null ? libVilleNaiss : null);
			libVilleNaissCandidatField.setEnabled(siScolDepartement != null);

		} else {
			/* Gestion des Departements */
			changeRequired(dptField, false);
			dptField.setVisible(false);
			dptField.setValue(null);

			/* Gestion des communes de naissance */
			changeRequired(commField, false);
			commField.setVisible(false);
			commField.setValue(null);

			/* Gestion de la ville de naissance */
			changeRequired(libVilleNaissCandidatField, true);
			libVilleNaissCandidatField.setVisible(true);
			libVilleNaissCandidatField.setEnabled(true);
			libVilleNaissCandidatField.setValue(libVilleNaiss);
		}

	}

	/**
	 * Change l'etat obligatoire d'un champs
	 * @param field
	 * @param isRequired
	 */
	private void changeRequired(final Field<?> field, final Boolean isRequired) {
		if (field == null) {
			return;
		}
		field.setRequired(isRequired);
		if (isRequired) {
			field.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
		} else {
			field.setRequiredError(null);
		}
	}

	/**
	 * Défini le 'CandidatWindowListener' utilisé
	 * @param candidatWindowListener
	 */
	public void addCandidatWindowListener(final CandidatWindowListener candidatWindowListener) {
		this.candidatWindowListener = candidatWindowListener;
	}

	/** Interface pour récupérer un click sur Oui ou Non. */
	public interface CandidatWindowListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param candidat
		 * @param individu
		 * @param needToDeleteDataSiScol
		 */
		void btnOkClick(Candidat candidat, WSIndividu individu, Boolean needToDeleteDataSiScol);

	}
}
