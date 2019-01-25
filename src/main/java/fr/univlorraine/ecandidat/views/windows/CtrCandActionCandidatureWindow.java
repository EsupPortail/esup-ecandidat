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
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.CandidatureCtrCandController;
import fr.univlorraine.ecandidat.controllers.MotivationAvisController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite_;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi_;
import fr.univlorraine.ecandidat.entities.ecandidat.PostIt;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.form.CustomBeanFieldGroup;
import fr.univlorraine.ecandidat.vaadin.form.LocalTimeField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredCheckBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;
import fr.univlorraine.ecandidat.vaadin.form.RequiredDateField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredIntegerField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTagsField;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextArea;
import fr.univlorraine.ecandidat.vaadin.form.RequiredTextField;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxMotivationAvis;
import fr.univlorraine.ecandidat.vaadin.form.combo.ComboBoxTypeDecision;

/**
 * Fenêtre d'action sur une ou plusieurs candidatures
 *
 * @author Kevin Hergalant
 */

@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CtrCandActionCandidatureWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient MotivationAvisController motivationAvisController;
	@Resource
	private transient CandidatureCtrCandController ctrCandCandidatureController;

	public static final String[] FIELDS_ORDER_DECISION = {TypeDecisionCandidature_.typeDecision.getName(),
			TypeDecisionCandidature_.motivationAvis.getName(),
			TypeDecisionCandidature_.listCompRangTypDecCand.getName(),
			TypeDecisionCandidature_.preselectLieuTypeDecCand.getName(),
			TypeDecisionCandidature_.preselectDateTypeDecCand.getName(),
			TypeDecisionCandidature_.preselectHeureTypeDecCand.getName(),
			TypeDecisionCandidature_.temAppelTypeDecCand.getName(),
			TypeDecisionCandidature_.commentTypeDecCand.getName()};

	public static final String[] FIELDS_ORDER_TYPE_STATUT = {Candidature_.typeStatut.getName(), Candidature_.datReceptDossierCand.getName()};

	public static final String[] FIELDS_ORDER_TYPE_TRAIT = {Candidature_.typeTraitement.getName()};

	public static final String[] FIELDS_ORDER_OPI = {Opi_.codOpi.getName()};

	/* Composants */
	private OptionGroup optionGroupAction;
	private CustomBeanFieldGroup<TypeDecisionCandidature> fieldGroupDecision;
	private VerticalLayout layoutDecision;
	private CustomBeanFieldGroup<Candidature> fieldGroupTypeStatut;
	private FormLayout formLayoutTypeStatut;
	private CustomBeanFieldGroup<Candidature> fieldGroupTypeTrait;
	private FormLayout formLayoutTypeTrait;
	private CustomBeanFieldGroup<Opi> fieldGroupOpi;
	private FormLayout formLayoutOpi;
	private RequiredCheckBox cbMailOpi = new RequiredCheckBox();
	private RequiredTagsField rtf = new RequiredTagsField();
	private HorizontalLayout hlTags = new HorizontalLayout();
	private CustomBeanFieldGroup<Candidature> fieldGroupDatConfirm;
	private FormLayout formLayoutDatConfirm;
	private CustomBeanFieldGroup<Candidature> fieldGroupDatRetour;
	private FormLayout formLayoutDatRetour;

	/* cas de modif d'une seule candidature */
	private Candidature candidature;

	/* Le centre de candidature lié */
	private CentreCandidature centreCandidature;

	private Button btnValid;
	private Button btnClose;

	/* Le listener */
	private ChangeCandidatureWindowListener changeCandidatureWindowListener;

	/**
	 * Crée une fenêtre d'action sur une ou plusieurs candidatures
	 *
	 * @param listeCandidature
	 *            la liste de candidature a manipuler
	 */
	public CtrCandActionCandidatureWindow(final List<Candidature> listeCandidature, final List<DroitFonctionnalite> listeDroits) {
		/* Style */
		setModal(true);
		setWidth(550, Unit.PIXELS);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* On vérifie si on traite un seul candidat */
		if (listeCandidature.size() > 0 && listeCandidature.size() == 1) {
			candidature = listeCandidature.get(0);
			centreCandidature = candidature.getFormation().getCommission().getCentreCandidature();
		} else if (listeCandidature.size() > 0) {
			centreCandidature = listeCandidature.get(0).getFormation().getCommission().getCentreCandidature();
		}

		/* Titre */
		setCaption(applicationContext.getMessage("candidature.action.window", null, UI.getCurrent().getLocale()));
		/* Le container d'options */
		BeanItemContainer<DroitFonctionnalite> container = new BeanItemContainer<>(DroitFonctionnalite.class);
		listeDroits.forEach(e -> {
			if (!e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_FENETRE_CAND) &&
					(listeCandidature.size() == 1 || (listeCandidature.size() > 1
							&& !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_VISU_HISTO_AVIS)
							&& !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_POST_IT)
							&& !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_NUM_OPI)
							&& !e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_OPEN_CANDIDAT)))
					&&
			/* Soit le code n'est pas action sur Tag, soit c'est celui ci mais la liste des tags est > 0 */
					(!e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_TAG)
							|| (e.getCodFonc().equals(NomenclatureUtils.FONCTIONNALITE_GEST_TAG) && cacheController.getTagEnService().size() > 0))) {
				container.addItem(e);
			}
		});

		if (container.size() == 0) {
			layout.addComponent(new Label(applicationContext.getMessage("candidature.action.noAction", null, UI.getCurrent().getLocale())));
		} else {
			container.sort(new Object[] {DroitFonctionnalite_.orderFonc.getName()}, new boolean[] {true});
			/* Les options */
			optionGroupAction = new OptionGroup(applicationContext.getMessage("candidature.action.label", new Object[] {listeCandidature.size()}, UI.getCurrent().getLocale()), container);
			optionGroupAction.setItemCaptionPropertyId(DroitFonctionnalite_.licFonc.getName());
			optionGroupAction.setItemCaptionMode(ItemCaptionMode.PROPERTY);
			layout.addComponent(optionGroupAction);

			optionGroupAction.addValueChangeListener(e -> majComponents());

			/* Layout de decision */
			layoutDecision = new VerticalLayout();
			layoutDecision.setCaption(applicationContext.getMessage("candidature.action.select.decision", null, UI.getCurrent().getLocale()));
			layoutDecision.setWidth(100, Unit.PERCENTAGE);

			/* Le field group pour la decision */
			/* Si une seule candidature, on recupere le dernier avis */
			if (candidature != null && candidature.getLastTypeDecision() != null) {
				TypeDecisionCandidature typeDecisionCandidature = candidature.getLastTypeDecision().cloneTypeDecisionCandidature();
				OneClickButton duplicateAvisBtn = new OneClickButton(applicationContext.getMessage("candidature.action.decision.duplicate.btn", null, UI.getCurrent().getLocale()), FontAwesome.COPY);
				duplicateAvisBtn.addStyleName(ValoTheme.BUTTON_TINY);
				duplicateAvisBtn.addClickListener(e -> {
					fieldGroupDecision.setItemDataSource(typeDecisionCandidature);
					Notification.show(applicationContext.getMessage("candidature.action.decision.duplicate", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
				});
				layoutDecision.addComponent(duplicateAvisBtn);
				layoutDecision.setComponentAlignment(duplicateAvisBtn, Alignment.MIDDLE_CENTER);
			}

			fieldGroupDecision = new CustomBeanFieldGroup<>(TypeDecisionCandidature.class);
			fieldGroupDecision.setItemDataSource(new TypeDecisionCandidature());

			/* Formulaire pour la decision */
			FormLayout formLayoutDecision = new FormLayout();
			formLayoutDecision.setWidth(100, Unit.PERCENTAGE);
			formLayoutDecision.setSpacing(true);
			for (String fieldName : FIELDS_ORDER_DECISION) {
				Field<?> field;
				if (fieldName.equals(TypeDecisionCandidature_.typeDecision.getName())) {
					field = fieldGroupDecision.buildAndBind(applicationContext.getMessage("action.decision." + fieldName, null, UI.getCurrent().getLocale()), fieldName, ComboBoxTypeDecision.class);
				} else if (fieldName.equals(TypeDecisionCandidature_.commentTypeDecCand.getName())) {
					field = fieldGroupDecision.buildAndBind(applicationContext.getMessage("action.decision." + fieldName, null, UI.getCurrent().getLocale()), fieldName, RequiredTextArea.class);
				} else {
					field = fieldGroupDecision.buildAndBind(applicationContext.getMessage("action.decision." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
				}

				if (!fieldName.equals(TypeDecisionCandidature_.preselectHeureTypeDecCand.getName())) {
					field.setWidth(100, Unit.PERCENTAGE);
				} else {
					field.setSizeUndefined();
				}

				formLayoutDecision.addComponent(field);
			}
			layoutDecision.addComponent(formLayoutDecision);
			layout.addComponent(layoutDecision);

			/* Pour le type de statut */
			fieldGroupTypeStatut = new CustomBeanFieldGroup<>(Candidature.class);
			fieldGroupTypeStatut.setItemDataSource(new Candidature());
			formLayoutTypeStatut = new FormLayout();
			formLayoutTypeStatut.setCaption(applicationContext.getMessage("candidature.action.select.statut", null, UI.getCurrent().getLocale()));
			formLayoutTypeStatut.setWidth(100, Unit.PERCENTAGE);
			formLayoutTypeStatut.setSpacing(true);
			for (String fieldName : FIELDS_ORDER_TYPE_STATUT) {
				Field<?> field = fieldGroupTypeStatut.buildAndBind(applicationContext.getMessage("candidature.action." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
				field.setWidth(100, Unit.PERCENTAGE);
				formLayoutTypeStatut.addComponent(field);
			}
			layout.addComponent(formLayoutTypeStatut);

			@SuppressWarnings("unchecked")
			RequiredComboBox<TypeStatut> cbTypeStatut = (RequiredComboBox<TypeStatut>) fieldGroupTypeStatut.getField(Candidature_.typeStatut.getName());
			cbTypeStatut.addValueChangeListener(e -> majStatutDossierComponent());

			ComboBoxTypeDecision cbTypeDecision = (ComboBoxTypeDecision) fieldGroupDecision.getField(TypeDecisionCandidature_.typeDecision.getName());
			ComboBoxMotivationAvis cbMotivation = (ComboBoxMotivationAvis) fieldGroupDecision.getField(TypeDecisionCandidature_.motivationAvis.getName());
			if (centreCandidature != null) {
				cbTypeDecision.setTypeDecisions(typeDecisionController.getTypeDecisionsEnServiceByCtrCand(centreCandidature));
				cbMotivation.setMotivationAvis(motivationAvisController.getMotivationAvisEnServiceByCtrCand(centreCandidature));
			}
			cbTypeDecision.addValueChangeListener(e -> majAvisComponent());

			/* Les type de traitement */
			fieldGroupTypeTrait = new CustomBeanFieldGroup<>(Candidature.class);
			fieldGroupTypeTrait.setItemDataSource(new Candidature());
			if (candidature != null) {
				fieldGroupTypeTrait.getItemDataSource().getBean().setTypeTraitement(candidature.getTypeTraitement());
			}
			formLayoutTypeTrait = new FormLayout();
			formLayoutTypeTrait.setCaption(applicationContext.getMessage("candidature.action.select.typTrait", null, UI.getCurrent().getLocale()));
			formLayoutTypeTrait.setWidth(100, Unit.PERCENTAGE);
			formLayoutTypeTrait.setSpacing(true);
			for (String fieldName : FIELDS_ORDER_TYPE_TRAIT) {
				Field<?> field = fieldGroupTypeTrait.buildAndBind(applicationContext.getMessage("candidature.action." + fieldName, null, UI.getCurrent().getLocale()), fieldName);
				formLayoutTypeTrait.addComponent(field);
			}
			layout.addComponent(formLayoutTypeTrait);

			/* Le code OPI */
			fieldGroupOpi = new CustomBeanFieldGroup<>(Opi.class);
			fieldGroupOpi.setItemDataSource(new Opi());
			formLayoutOpi = new FormLayout();
			formLayoutOpi.setCaption(applicationContext.getMessage("candidature.action.select.opi", null, UI.getCurrent().getLocale()));
			formLayoutOpi.setWidth(100, Unit.PERCENTAGE);
			formLayoutOpi.setSpacing(true);
			for (String fieldName : FIELDS_ORDER_OPI) {
				String caption = applicationContext.getMessage("candidature.action.opi." + fieldName, null, UI.getCurrent().getLocale());
				Field<?> field = fieldGroupOpi.buildAndBind(caption, fieldName);
				field.setWidth(100, Unit.PERCENTAGE);
				formLayoutOpi.addComponent(field);
			}
			HorizontalLayout hlCb = new HorizontalLayout();
			hlCb.setSpacing(true);
			layout.addComponent(hlCb);
			hlCb.addComponent(new Label(applicationContext.getMessage("candidature.action.opi.sendMail", null, UI.getCurrent().getLocale())));

			cbMailOpi.setValue(true);
			hlCb.addComponent(cbMailOpi);
			formLayoutOpi.addComponent(hlCb);
			layout.addComponent(formLayoutOpi);

			/* Les tags */

			hlTags.setCaption(applicationContext.getMessage("candidature.action.select.tags", null, UI.getCurrent().getLocale()));
			hlTags.setWidth(100, Unit.PERCENTAGE);

			/* cas particulier du tag, on a une liste, on ne peut pas passer par un field */
			if (candidature != null) {
				rtf.setTags(candidature.getTags());
			}
			rtf.setWidth(100, Unit.PERCENTAGE);
			rtf.setHeight(280, Unit.PIXELS);
			hlTags.addComponent(rtf);

			layout.addComponent(hlTags);

			/* La date de confirmation */
			fieldGroupDatConfirm = new CustomBeanFieldGroup<>(Candidature.class);
			fieldGroupDatConfirm.setItemDataSource(new Candidature());
			if (candidature != null) {
				fieldGroupDatConfirm.getItemDataSource().getBean().setDatNewConfirmCand(candidature.getDatNewConfirmCand());
			}
			formLayoutDatConfirm = new FormLayout();
			formLayoutDatConfirm.setCaption(applicationContext.getMessage("candidature.action.select.datConfirm", null, UI.getCurrent().getLocale()));
			formLayoutDatConfirm.setWidth(100, Unit.PERCENTAGE);
			formLayoutDatConfirm.setSpacing(true);
			RequiredDateField rdfDatConfirm = (RequiredDateField) fieldGroupDatConfirm.buildAndBind(applicationContext.getMessage("candidature.action."
					+ Candidature_.datNewConfirmCand.getName(), null, UI.getCurrent().getLocale()), Candidature_.datNewConfirmCand.getName());
			rdfDatConfirm.setWidth(100, Unit.PERCENTAGE);
			formLayoutDatConfirm.addComponent(rdfDatConfirm);
			layout.addComponent(formLayoutDatConfirm);

			/* La date de retour */
			fieldGroupDatRetour = new CustomBeanFieldGroup<>(Candidature.class);
			fieldGroupDatRetour.setItemDataSource(new Candidature());
			if (candidature != null) {
				fieldGroupDatRetour.getItemDataSource().getBean().setDatNewConfirmCand(candidature.getDatNewConfirmCand());
			}
			formLayoutDatRetour = new FormLayout();
			formLayoutDatRetour.setCaption(applicationContext.getMessage("candidature.action.select.datRetour", null, UI.getCurrent().getLocale()));
			formLayoutDatRetour.setWidth(100, Unit.PERCENTAGE);
			formLayoutDatRetour.setSpacing(true);
			RequiredDateField rdfDatRetour = (RequiredDateField) fieldGroupDatRetour.buildAndBind(applicationContext.getMessage("candidature.action."
					+ Candidature_.datNewRetourCand.getName(), null, UI.getCurrent().getLocale()), Candidature_.datNewRetourCand.getName());
			rdfDatConfirm.setWidth(100, Unit.PERCENTAGE);
			formLayoutDatRetour.addComponent(rdfDatRetour);
			layout.addComponent(formLayoutDatRetour);
		}

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnClose = new Button(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnClose.addClickListener(e -> close());
		buttonsLayout.addComponent(btnClose);
		buttonsLayout.setComponentAlignment(btnClose, Alignment.MIDDLE_LEFT);

		btnValid = new Button(applicationContext.getMessage("btnValid", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnValid.setDisableOnClick(true);
		btnValid.addClickListener(e -> {
			DroitFonctionnalite fonc = (DroitFonctionnalite) optionGroupAction.getValue();
			if (fonc == null) {
				close();
			} else {
				String codFonc = fonc.getCodFonc();
				if (codFonc == null) {
					close();
				}
				if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_EDIT_STATUT_DOSSIER)) {
					try {
						/* Verification date superieur à date du jour */
						RequiredDateField field = (RequiredDateField) fieldGroupTypeStatut.getField(Candidature_.datReceptDossierCand.getName());
						if (field.getValue() != null && field.getValue().after(new java.util.Date())) {
							Notification.show(applicationContext.getMessage("candidature.action.datReceptDossierCand.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
							return;
						}

						/* Valide la saisie */
						fieldGroupTypeStatut.commit();
						/* Enregistre la typeStatutPiece saisie */
						if (ctrCandCandidatureController.editListCandidatureTypStatut(listeCandidature, fieldGroupTypeStatut.getItemDataSource().getBean().getTypeStatut(),
								fieldGroupTypeStatut.getItemDataSource().getBean().getDatReceptDossierCand())) {
							if (changeCandidatureWindowListener != null) {
								changeCandidatureWindowListener.action(listeCandidature);
							}
							/* Ferme la fenêtre */
							close();
						}

					} catch (CommitException ce) {
					} finally {
						btnValid.setEnabled(true);
					}
				} else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_EDIT_TYPTRAIT)) {
					try {
						/* Valide la saisie */
						fieldGroupTypeTrait.commit();
						/* Enregistre la typeStatutPiece saisie */
						if (ctrCandCandidatureController.editListCandidatureTypTrait(listeCandidature, fieldGroupTypeTrait.getItemDataSource().getBean().getTypeTraitement())) {
							if (changeCandidatureWindowListener != null) {
								changeCandidatureWindowListener.action(listeCandidature);
							}
							/* Ferme la fenêtre */
							close();
						}

					} catch (CommitException ce) {
					} finally {
						btnValid.setEnabled(true);
					}
				} else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_EDIT_AVIS)) {
					try {
						/* Valide la saisie */
						fieldGroupDecision.commit();
						/* Enregistre la typeStatutPiece saisie */
						if (ctrCandCandidatureController.editAvis(listeCandidature, fieldGroupDecision.getItemDataSource().getBean())) {
							if (changeCandidatureWindowListener != null) {
								changeCandidatureWindowListener.action(listeCandidature);
							}
							/* Ferme la fenêtre */
							close();
						}

					} catch (CommitException ce) {
					} finally {
						btnValid.setEnabled(true);
					}
				}
				/* Gestion du code OPI */
				else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_NUM_OPI)) {
					try {
						/* Valide la saisie */
						fieldGroupOpi.commit();

						if (ctrCandCandidatureController.editOpi(listeCandidature, fieldGroupOpi.getItemDataSource().getBean(), cbMailOpi.getValue())) {
							if (changeCandidatureWindowListener != null) {
								changeCandidatureWindowListener.action(listeCandidature);
							}
							/* Ferme la fenêtre */
							close();
						}
					} catch (CommitException ce) {
					} finally {
						btnValid.setEnabled(true);
					}
				}
				/* Gestion des tags */
				else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_TAG)) {
					try {
						if (ctrCandCandidatureController.editTag(listeCandidature, rtf.getTags())) {
							if (changeCandidatureWindowListener != null) {
								changeCandidatureWindowListener.action(listeCandidature);
							}
							/* Ferme la fenêtre */
							close();
						}
					} catch (Exception ce) {
					} finally {
						btnValid.setEnabled(true);
					}
				}
				/* Gestion des date de confirmation */
				else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_DAT_CONFIRM)) {
					try {
						/* Valide la saisie */
						fieldGroupDatConfirm.commit();

						if (ctrCandCandidatureController.editDatConfirm(listeCandidature, fieldGroupDatConfirm.getItemDataSource().getBean())) {
							if (changeCandidatureWindowListener != null) {
								changeCandidatureWindowListener.action(listeCandidature);
							}
							/* Ferme la fenêtre */
							close();
						}
					} catch (CommitException ce) {
					} finally {
						btnValid.setEnabled(true);
					}
				}
				/* Gestion des date de retour */
				else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_DAT_RETOUR)) {
					try {
						/* Valide la saisie */
						fieldGroupDatRetour.commit();

						if (ctrCandCandidatureController.editDatRetour(listeCandidature, fieldGroupDatRetour.getItemDataSource().getBean())) {
							if (changeCandidatureWindowListener != null) {
								changeCandidatureWindowListener.action(listeCandidature);
							}
							/* Ferme la fenêtre */
							close();
						}
					} catch (CommitException ce) {
					} finally {
						btnValid.setEnabled(true);
					}
				}
				/* Gestion des avis */
				else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_VALID_AVIS)) {
					if (ctrCandCandidatureController.validAvis(listeCandidature)) {
						if (changeCandidatureWindowListener != null) {
							changeCandidatureWindowListener.action(listeCandidature);
						}
					}
					close();
				} else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_VALID_TYPTRAIT)) {
					if (ctrCandCandidatureController.validTypTrait(listeCandidature)) {
						if (changeCandidatureWindowListener != null) {
							changeCandidatureWindowListener.action(listeCandidature);
						}
					}
					close();
				} else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_OPEN_CANDIDAT)) {
					ctrCandCandidatureController.openCandidat(candidature);
					if (changeCandidatureWindowListener != null && candidature != null) {
						changeCandidatureWindowListener.openCandidature(candidature);
					}
					close();
				} else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_VISU_HISTO_AVIS)) {
					ctrCandCandidatureController.showHistoAvis(candidature, listeDroits, changeCandidatureWindowListener);
					btnValid.setEnabled(true);
					// close();
				} else if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_POST_IT)) {
					ctrCandCandidatureController.showPostIt(candidature, listeDroits, changeCandidatureWindowListener);
					btnValid.setEnabled(true);
					// close();
				}
			}
		});
		buttonsLayout.addComponent(btnValid);
		buttonsLayout.setComponentAlignment(btnValid, Alignment.MIDDLE_RIGHT);

		if (container.size() == 0) {
			btnValid.setEnabled(false);
		} else {
			/* Met a jour lers composants */
			majComponents();
		}

		/* Centre la fenêtre */
		center();
	}

	/**
	 * Met a jour les composants
	 *
	 * @param listeCandidature
	 */
	private void majComponents() {
		DroitFonctionnalite fonc = (DroitFonctionnalite) optionGroupAction.getValue();
		if (fonc == null) {
			formLayoutTypeTrait.setVisible(false);
			formLayoutTypeStatut.setVisible(false);
			layoutDecision.setVisible(false);
			formLayoutOpi.setVisible(false);
			hlTags.setVisible(false);
			formLayoutDatConfirm.setVisible(false);
			formLayoutDatRetour.setVisible(false);
		} else {
			String codFonc = fonc.getCodFonc();
			if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_EDIT_TYPTRAIT)) {
				formLayoutTypeTrait.setVisible(true);
			} else {
				formLayoutTypeTrait.setVisible(false);

			}
			if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_EDIT_STATUT_DOSSIER)) {
				formLayoutTypeStatut.setVisible(true);
				@SuppressWarnings("unchecked")
				RequiredComboBox<TypeStatut> cbTypeStatut = (RequiredComboBox<TypeStatut>) fieldGroupTypeStatut.getField(Candidature_.typeStatut.getName());
				cbTypeStatut.setValue(tableRefController.getTypeStatutReceptionne());
				majStatutDossierComponent();
			} else {
				formLayoutTypeStatut.setVisible(false);
			}
			if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_EDIT_AVIS)) {
				layoutDecision.setVisible(true);
				majAvisComponent();
			} else {
				layoutDecision.setVisible(false);
			}

			if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_NUM_OPI)) {
				formLayoutOpi.setVisible(true);
			} else {
				formLayoutOpi.setVisible(false);
			}

			if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_TAG)) {
				hlTags.setVisible(true);
			} else {
				hlTags.setVisible(false);
			}

			if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_DAT_CONFIRM)) {
				formLayoutDatConfirm.setVisible(true);
			} else {
				formLayoutDatConfirm.setVisible(false);
			}

			if (codFonc.equals(NomenclatureUtils.FONCTIONNALITE_GEST_DAT_RETOUR)) {
				formLayoutDatRetour.setVisible(true);
			} else {
				formLayoutDatRetour.setVisible(false);
			}

		}
		center();
	}

	/** Mise à jour des composants pour les StatutDossier */
	@SuppressWarnings("unchecked")
	private void majStatutDossierComponent() {
		RequiredComboBox<TypeStatut> cbTypeStatut = (RequiredComboBox<TypeStatut>) fieldGroupTypeStatut.getField(Candidature_.typeStatut.getName());
		RequiredDateField fieldDateRecept = (RequiredDateField) fieldGroupTypeStatut.getField(Candidature_.datReceptDossierCand.getName());

		if (cbTypeStatut.getValue() != null) {
			TypeStatut typeStatut = (TypeStatut) cbTypeStatut.getValue();
			if (typeStatut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_ATT)) {
				fieldDateRecept.setVisible(false);
				fieldDateRecept.setRequired(false);
				fieldDateRecept.setRequiredError(null);
				fieldDateRecept.setValue(null);
			} else {
				fieldDateRecept.setVisible(true);
				fieldDateRecept.setRequired(true);
				fieldDateRecept.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
				if (typeStatut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_REC)) {
					fieldDateRecept.setCaption(applicationContext.getMessage("candidature.action." + Candidature_.datReceptDossierCand.getName(), null, UI.getCurrent().getLocale()));
				} else if (typeStatut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_COM)) {
					fieldDateRecept.setCaption(applicationContext.getMessage("candidature.action." + Candidature_.datCompletDossierCand.getName(), null, UI.getCurrent().getLocale()));
				} else if (typeStatut.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_INC)) {
					fieldDateRecept.setCaption(applicationContext.getMessage("candidature.action." + Candidature_.datIncompletDossierCand.getName(), null, UI.getCurrent().getLocale()));
				}
			}
		}
		fieldDateRecept.setLocalValue(LocalDate.now());
	}

	/** Mise à jour des composants pour les avis */
	private void majAvisComponent() {
		ComboBoxTypeDecision cbTypeDecision = (ComboBoxTypeDecision) fieldGroupDecision.getField(TypeDecisionCandidature_.typeDecision.getName());
		ComboBoxMotivationAvis cbMotivation = (ComboBoxMotivationAvis) fieldGroupDecision.getField(TypeDecisionCandidature_.motivationAvis.getName());
		RequiredIntegerField fieldRang = (RequiredIntegerField) fieldGroupDecision.getField(TypeDecisionCandidature_.listCompRangTypDecCand.getName());
		RequiredTextField fieldLieuPreselect = (RequiredTextField) fieldGroupDecision.getField(TypeDecisionCandidature_.preselectLieuTypeDecCand.getName());
		RequiredDateField fieldDatePreselect = (RequiredDateField) fieldGroupDecision.getField(TypeDecisionCandidature_.preselectDateTypeDecCand.getName());
		RequiredCheckBox fieldAppel = (RequiredCheckBox) fieldGroupDecision.getField(TypeDecisionCandidature_.temAppelTypeDecCand.getName());
		LocalTimeField fieldHeurePreselect = (LocalTimeField) fieldGroupDecision.getField(TypeDecisionCandidature_.preselectHeureTypeDecCand.getName());
		RequiredTextArea fieldComment = (RequiredTextArea) fieldGroupDecision.getField(TypeDecisionCandidature_.commentTypeDecCand.getName());

		if (cbTypeDecision.getValue() != null) {
			if (candidature != null && candidature.getLastTypeDecision() != null &&
					candidature.getLastTypeDecision().getTemValidTypeDecCand() &&
					candidature.getLastTypeDecision().getTypeDecision().getTemDefinitifTypDec() &&
					parametreController.getIsAppel()) {
				fieldAppel.setVisible(true);
			} else {
				fieldAppel.setVisible(false);
				fieldAppel.setValue(false);
			}

			fieldComment.setVisible(true);

			TypeDecision typeDecision = (TypeDecision) cbTypeDecision.getValue();
			if (typeDecision.getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_DEF)) {
				cbMotivation.setBoxNeeded(true, null);
			} else {
				cbMotivation.setBoxNeeded(false, null);
			}
			if (typeDecision.getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_LISTE_COMP)) {
				fieldRang.setVisible(true);
				fieldRang.setRequired(true);
				fieldRang.setRequiredError(applicationContext.getMessage("validation.obigatoire", null, UI.getCurrent().getLocale()));
			} else {
				fieldRang.setVisible(false);
				fieldRang.setRequired(false);
				fieldRang.setRequiredError(null);
				fieldRang.setValue(null);
			}
			if (typeDecision.getTypeAvis().getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_PRESELECTION)) {
				fieldLieuPreselect.setVisible(true);
				fieldDatePreselect.setVisible(true);
				fieldHeurePreselect.setVisible(true);
				if (candidature != null) {
					fieldLieuPreselect.setValue(candidature.getFormation().getPreselectLieuForm());
					fieldDatePreselect.setValue((candidature.getFormation().getPreselectDateForm() != null) ? Date.valueOf(candidature.getFormation().getPreselectDateForm()) : null);
					fieldHeurePreselect.setValue(candidature.getFormation().getPreselectHeureForm());
				} else {
					fieldLieuPreselect.setValue(null);
					fieldDatePreselect.setValue(null);
					fieldHeurePreselect.setValue(null);
				}
			} else {
				fieldLieuPreselect.setVisible(false);
				fieldDatePreselect.setVisible(false);
				fieldHeurePreselect.setVisible(false);
			}
		} else {
			cbMotivation.setBoxNeeded(false, null);
			fieldRang.setVisible(false);
			fieldRang.setRequired(false);
			fieldRang.setRequiredError(null);
			fieldRang.setValue(null);
			fieldLieuPreselect.setVisible(false);
			fieldDatePreselect.setVisible(false);
			fieldHeurePreselect.setVisible(false);
			fieldLieuPreselect.setValue(null);
			fieldDatePreselect.setValue(null);
			fieldHeurePreselect.setValue(null);
			fieldAppel.setVisible(false);
			fieldAppel.setValue(false);
			fieldComment.setValue(null);
			fieldComment.setVisible(false);
		}
		center();
	}

	/**
	 * Défini le 'ChangeCandidatureWindowListener' utilisé
	 *
	 * @param changeCandidatureWindowListener
	 */
	public void addChangeCandidatureWindowListener(final ChangeCandidatureWindowListener changeCandidatureWindowListener) {
		this.changeCandidatureWindowListener = changeCandidatureWindowListener;
	}

	/** Interface pour récupérer une action */
	public interface ChangeCandidatureWindowListener extends Serializable {
		/**
		 * Appelé si un postIt est ajouté-->maj de la liste des postIt
		 *
		 * @param postIt
		 */
		void addPostIt(PostIt postIt);

		/**
		 * Appelé si open est selectionné
		 *
		 * @param cand
		 */
		void openCandidature(Candidature cand);

		/**
		 * Appelé lorsque tout autre action est envoyé --> mise a jour de la liste presentation de la window
		 *
		 * @param listeCandidature
		 */
		void action(List<Candidature> listeCandidature);
	}
}
