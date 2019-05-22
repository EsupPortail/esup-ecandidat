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
package fr.univlorraine.ecandidat.vaadin.form.i18n;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.I18nTraduction;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraduction;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.IRequiredField;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxLangue;

/**
 * Champs complex de traduction
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class I18nField extends CustomField<I18n> implements IRequiredField {

	/* Variable pour le champs et les msg d'erreur */
	private boolean shouldHideError = true;
	private String requieredError;
	private String infoMouseOverRichText;

	/* La langue par défaut a afficher */
	private Langue langueParDefaut;

	/* Les langues en service */
	private List<Langue> listeLangueEnService;

	private VerticalLayout layoutComplet;
	private VerticalLayout layoutLangue;
	private OneClickButton btnAddLangue;
	private I18n valeur;
	private List<I18nTraduction> listeTraduction;
	private List<HorizontalLayout> listLayoutTraductions;
	private TypeTraduction typeTraduction;

	/* Listener pour recentrer la fenetre */
	private CenterListener centerListener;

	/**
	 * Constructeur, initialisation du champs
	 *
	 * @param listeLangueEnService
	 * @param langueParDefaut
	 * @param libelleBtnPlus
	 */
	public I18nField(final Langue langueParDefaut, final List<Langue> listeLangueEnService, final String libelleBtnPlus, final String infoMouseOverRichText) {
		super();
		setRequired(false);
		this.langueParDefaut = langueParDefaut;
		this.listeLangueEnService = listeLangueEnService;
		this.infoMouseOverRichText = infoMouseOverRichText;

		listLayoutTraductions = new ArrayList<>();
		listeTraduction = new ArrayList<>();
		layoutComplet = new VerticalLayout();
		layoutComplet.setSpacing(true);
		layoutLangue = new VerticalLayout();
		layoutLangue.setSpacing(true);
		layoutComplet.addComponent(layoutLangue);

		btnAddLangue = new OneClickButton(libelleBtnPlus, FontAwesome.PLUS_SQUARE_O);
		btnAddLangue.setVisible(false);
		btnAddLangue.addStyleName(ValoTheme.BUTTON_TINY);
		layoutComplet.addComponent(btnAddLangue);
		btnAddLangue.addClickListener(e -> {
			layoutLangue.addComponent(getLangueLayout(null));
			checkVisibleAddLangue();
			centerWindow();
		});
	}

	/** Listener appelé pour le centrage */
	private void centerWindow() {
		if (centerListener != null) {
			centerListener.centerWindow(true);
		}
	}

	/** @see com.vaadin.ui.CustomField#initContent() */
	@Override
	protected Component initContent() {

		/* Ajout de la langue par defaut */
		HorizontalLayout hlLangueDef = new HorizontalLayout();
		listLayoutTraductions.add(hlLangueDef);
		hlLangueDef.setSpacing(true);
		hlLangueDef.setWidth(100, Unit.PERCENTAGE);
		layoutLangue.addComponent(hlLangueDef);

		/* Si il y a d'autres langue en service on met le drapeau */
		if (listeLangueEnService.size() > 0 || listeTraduction.size() > 1) {
			Image flag = new Image(null, new ThemeResource("images/flags/" + langueParDefaut.getCodLangue() + ".png"));
			HorizontalLayout hlFlag = new HorizontalLayout();
			hlFlag.setWidth(75, Unit.PIXELS);
			hlFlag.addComponent(flag);
			hlFlag.setComponentAlignment(flag, Alignment.MIDDLE_CENTER);
			hlLangueDef.addComponent(hlFlag);
		}

		/* La valeur de la traduction */
		AbstractField<String> tfVal = getNewValueComponent();
		tfVal.setId(langueParDefaut.getCodLangue());
		tfVal.setWidth(100, Unit.PERCENTAGE);

		/* Recuperation de la valeur de la traduction par defaut dans la liste des traductions */
		if (listeTraduction.size() != 0) {
			Optional<I18nTraduction> opt = listeTraduction.stream().filter(l -> l.getLangue().getCodLangue().equals(langueParDefaut.getCodLangue())).findFirst();
			if (opt.isPresent()) {
				tfVal.setValue(opt.get().getValTrad());
			}
		}

		hlLangueDef.addComponent(tfVal);
		hlLangueDef.setExpandRatio(tfVal, 1);

		listeTraduction.stream().filter(l -> !l.getLangue().getCodLangue().equals(langueParDefaut.getCodLangue())).forEach(traductionOther -> {
			/* Ajout d'une langue inactive si elle existe */
			Optional<Langue> opt = listeLangueEnService.stream().filter(langueEnService -> langueEnService.getCodLangue().equals(traductionOther.getLangue().getCodLangue())).findFirst();
			if (opt.isPresent()) {
				/* Ajout des autres langues si elles n'existent pas déjà */
				layoutLangue.addComponent(getLangueLayout(traductionOther));
			} else {
				layoutLangue.addComponent(getLangueLayoutInactive(traductionOther));
			}

		});

		checkVisibleAddLangue();

		return layoutComplet;
	}

	/**
	 * Soit on affiche un textField, soit un richtextarea
	 * On ajoute un listener pour
	 *
	 * @return le field a afficher
	 */
	private AbstractField<String> getNewValueComponent() {
		AbstractField<String> retour;
		if (typeTraduction.getLengthTypTrad() < 1000) {
			retour = new TextField();
		} else {
			retour = new RichTextArea();
			retour.setDescription(infoMouseOverRichText);
		}
		retour.addValueChangeListener(e -> {
			fireValueChange(false);
		});
		retour.addValidator(e -> {
			if (!shouldHideError && getRequiredError() != null && !isValid()) {
				retour.addStyleName(StyleConstants.FIELD_ERROR_COMPLETE);
			} else {
				retour.removeStyleName(StyleConstants.FIELD_ERROR_COMPLETE);
			}
		});

		return retour;
	}

	/** @see com.vaadin.ui.AbstractField#getType() */
	@Override
	public Class<I18n> getType() {
		return I18n.class;
	}

	/** @see com.vaadin.ui.AbstractField#setInternalValue(java.lang.Object) */
	@Override
	protected void setInternalValue(final I18n newFieldValue) {
		super.setInternalValue(newFieldValue);
		valeur = newFieldValue.clone();
		typeTraduction = valeur.getTypeTraduction();
		listeTraduction = new ArrayList<>(valeur.getI18nTraductions());
	}

	/** @see com.vaadin.ui.AbstractField#getValue() */
	@Override
	public I18n getValue() {
		return getI18nValue();
	}

	/** @see com.vaadin.ui.AbstractField#getInternalValue() */
	@Override
	protected I18n getInternalValue() {
		return getI18nValue();
	}

	/** @see com.vaadin.ui.AbstractField#shouldHideErrors() */
	@Override
	protected boolean shouldHideErrors() {
		Boolean hide = shouldHideError;
		shouldHideError = false;
		return hide;
	}

	/**
	 * Verifie que les traductions ne sont pas toutes vides
	 *
	 * @see com.vaadin.ui.AbstractField#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		if (getI18nValue() == null || getI18nValue().getI18nTraductions() == null || getI18nValue().getI18nTraductions().size() == 0) {
			return true;
		} else {
			Boolean allBlank = true;
			for (I18nTraduction trad : getI18nValue().getI18nTraductions()) {
				if (trad.getValTrad() != null && !trad.getValTrad().trim().equals("")) {
					allBlank = false;
				}
			}
			return allBlank;
		}
	}

	/** @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#preCommit() */
	@Override
	public void preCommit() {
		shouldHideError = false;
		super.setRequiredError(requieredError);
		if (isEmpty()) {
			fireValueChange(false);
		}
	}

	/** @see fr.univlorraine.ecandidat.vaadin.form.IRequiredField#initField(java.lang.Boolean) */
	@Override
	public void initField(final Boolean immediate) {
		setImmediate(immediate);
		super.setRequiredError(null);
	}

	/** @see com.vaadin.ui.AbstractField#setRequiredError(java.lang.String) */
	@Override
	public void setRequiredError(final String requiredMessage) {
		requieredError = requiredMessage;
	}

	/**
	 * Renvoie un layout contenant un choix de langue et une traduction
	 *
	 * @param traductionInactive
	 * @return le layout
	 */
	private HorizontalLayout getLangueLayoutInactive(final I18nTraduction traductionInactive) {
		Langue langueInactive = traductionInactive.getLangue();
		/* Ajout de la langue par defaut */
		HorizontalLayout hlLangueInactive = new HorizontalLayout();
		listLayoutTraductions.add(hlLangueInactive);
		hlLangueInactive.setSpacing(true);
		hlLangueInactive.setWidth(100, Unit.PERCENTAGE);
		layoutLangue.addComponent(hlLangueInactive);

		Image flag = new Image(null, new ThemeResource("images/flags/" + langueInactive.getCodLangue() + ".png"));
		HorizontalLayout hlFlag = new HorizontalLayout();
		hlFlag.setWidth(75, Unit.PIXELS);
		hlFlag.addComponent(flag);
		hlFlag.setComponentAlignment(flag, Alignment.MIDDLE_CENTER);
		hlLangueInactive.addComponent(hlFlag);

		/* La valeur de la traduction */
		AbstractField<String> tfVal = getNewValueComponent();
		tfVal.setId(langueInactive.getCodLangue());
		tfVal.setWidth(100, Unit.PERCENTAGE);

		/* Recuperation de la valeur de la traduction par defaut dans la liste des traductions */
		if (listeTraduction.size() != 0) {
			Optional<I18nTraduction> opt = listeTraduction.stream().filter(l -> l.getLangue().getCodLangue().equals(langueInactive.getCodLangue())).findFirst();
			if (opt.isPresent()) {
				tfVal.setValue(opt.get().getValTrad());
			}
		}

		hlLangueInactive.addComponent(tfVal);
		hlLangueInactive.setExpandRatio(tfVal, 1);

		/* Le bouton de suppression de la langue */
		OneClickButton removeLangue = new OneClickButton(FontAwesome.MINUS_SQUARE_O);
		removeLangue.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		removeLangue.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		removeLangue.addClickListener(e -> {
			layoutLangue.removeComponent(hlLangueInactive);
			listLayoutTraductions.remove(hlLangueInactive);
			checkVisibleAddLangue();
			centerWindow();
		});
		hlLangueInactive.addComponent(removeLangue);

		return hlLangueInactive;
	}

	/**
	 * Renvoie un layout contenant un choix de langue et une traduction
	 *
	 * @param traductionOther
	 * @return le layout
	 */
	private HorizontalLayout getLangueLayout(final I18nTraduction traductionOther) {
		/* Le layout renvoyé */
		HorizontalLayout hlLangueOther = new HorizontalLayout();
		listLayoutTraductions.add(hlLangueOther);
		hlLangueOther.setSpacing(true);
		hlLangueOther.setWidth(100, Unit.PERCENTAGE);

		/* La combobox avec les icones de drapeaux */
		ComboBoxLangue cbLangue = new ComboBoxLangue(listeLangueEnService, false);
		cbLangue.selectLangue((traductionOther == null ? null : traductionOther.getLangue()));
		cbLangue.setWidth(75, Unit.PIXELS);
		hlLangueOther.addComponent(cbLangue);

		/* Le textField... ou */
		AbstractField<String> tfValOther = getNewValueComponent();
		tfValOther.setWidth(100, Unit.PERCENTAGE);
		if (traductionOther != null) {
			tfValOther.setValue(traductionOther.getValTrad());
		}
		hlLangueOther.addComponent(tfValOther);
		hlLangueOther.setExpandRatio(tfValOther, 1);

		/* Le bouton de suppression de la langue */
		OneClickButton removeLangue = new OneClickButton(FontAwesome.MINUS_SQUARE_O);
		removeLangue.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		removeLangue.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		removeLangue.addClickListener(e -> {
			layoutLangue.removeComponent(hlLangueOther);
			listLayoutTraductions.remove(hlLangueOther);
			checkVisibleAddLangue();
			centerWindow();
		});
		hlLangueOther.addComponent(removeLangue);
		return hlLangueOther;
	}

	/** Modifie la visibilité du bouton d'ajout de langue */
	private void checkVisibleAddLangue() {
		if (listeLangueEnService.size() == 0) {
			btnAddLangue.setVisible(false);
		} else {
			List<I18nTraduction> i18nTraductions = getValue().getI18nTraductions();
			for (Langue langue : listeLangueEnService) {
				Optional<I18nTraduction> opt = i18nTraductions.stream().filter(l -> l.getLangue().getCodLangue().equals(langue.getCodLangue())).findFirst();
				if (!opt.isPresent()) {
					btnAddLangue.setVisible(true);
					return;
				}
			}
			btnAddLangue.setVisible(false);
		}
	}

	/**
	 * Recupere la valeur du champs contenant la liste de traduction
	 *
	 * @param i18n
	 * @return la liste des traductions
	 */
	@SuppressWarnings("unchecked")
	private List<I18nTraduction> getValueField(final I18n i18n) {
		List<I18nTraduction> listeToRet = new ArrayList<>();
		listLayoutTraductions.forEach(e -> {
			if (e.getComponentCount() == 0) {
				return;
			}

			// langue par défaut
			if (e.getComponent(0) instanceof TextField || e.getComponent(0) instanceof RichTextArea) {
				AbstractField<String> tf = (AbstractField<String>) e.getComponent(0);
				listeToRet.add(new I18nTraduction(MethodUtils.cleanHtmlValue(tf.getValue()), i18n, langueParDefaut));
			} else if (e.getComponent(0) instanceof HorizontalLayout) {
				AbstractField<String> tf = (AbstractField<String>) e.getComponent(1);
				listeToRet.add(new I18nTraduction(MethodUtils.cleanHtmlValue(tf.getValue()), i18n, new Langue(tf.getId())));
			} else {
				ComboBox cbLangue = (ComboBox) e.getComponent(0);
				Langue langue = (Langue) cbLangue.getValue();
				AbstractField<String> tf = (AbstractField<String>) e.getComponent(1);
				listeToRet.add(new I18nTraduction(tf.getValue(), i18n, langue));
			}
		});
		return listeToRet;

	}

	/**
	 * Modifie un i18n ou créé un nouveau
	 *
	 * @return l'i18n complété
	 */
	private I18n getI18nValue() {
		I18n objet;
		if (valeur != null) {
			objet = valeur;
		} else {
			objet = new I18n();
		}
		objet.setI18nTraductions(getValueField(objet));
		return objet;
	}

	/** @see com.vaadin.ui.AbstractField#isValid() */
	@Override
	public boolean isValid() {
		try {
			validate();
			validateFields(true);
			return true;
		} catch (InvalidValueException e) {
			validateFields(false);
			return false;
		}
	}

	/**
	 * Colore les champs en rouge si erreur
	 *
	 * @param validate
	 */
	@SuppressWarnings("unchecked")
	private void validateFields(final Boolean validate) {
		listLayoutTraductions.forEach(e -> {
			AbstractField<String> tf;
			if (e.getComponent(0) instanceof TextField || e.getComponent(0) instanceof RichTextArea) {
				tf = (AbstractField<String>) e.getComponent(0);
			} else if (e.getComponent(0) instanceof HorizontalLayout) {
				tf = (AbstractField<String>) e.getComponent(1);
			} else {
				tf = (AbstractField<String>) e.getComponent(1);
			}
			/* Ajout du style */
			if (validate) {
				tf.removeStyleName(StyleConstants.FIELD_ERROR_COMPLETE);
			} else {
				tf.addStyleName(StyleConstants.FIELD_ERROR_COMPLETE);
			}
		});
	}

	/** Disable ce champs */
	public void setNoRequierd() {
		setRequired(false);
		setRequiredError(null);
		getValidators().forEach(e -> {
			if (e instanceof I18nValidator) {
				removeValidator(e);
			}
		});
	}

	/**
	 * Défini le 'CenterListener' utilisé
	 *
	 * @param centerListener
	 */
	public void addCenterListener(final CenterListener centerListener) {
		this.centerListener = centerListener;
	}

	/** Interface pour récupérer si on ajoute ou supprime une langue-->Center a nouveau */
	public interface CenterListener extends Serializable {

		void centerWindow(Boolean center);

	}
}
