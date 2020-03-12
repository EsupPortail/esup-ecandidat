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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.FormationController;
import fr.univlorraine.ecandidat.controllers.FormulaireController;
import fr.univlorraine.ecandidat.controllers.PieceJustifController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire_;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif_;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre d'ajout de pièces complémentaires a une formation
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CtrCandPieceComplementaireWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient PieceJustifController pieceJustifController;
	@Resource
	private transient FormulaireController formulaireController;
	@Resource
	private transient FormationController formationController;

	/* Composants */

	private final OneClickButton btnEnregistrer;
	private final OneClickButton btnAnnuler;

	/* PJ */
	private final ListSelect leftSelectPj;
	private final ListSelect rightSelectPj;
	private final ListSelect communSelectPj;
	private final BeanItemContainer<PieceJustif> containerLeftPj;
	private final BeanItemContainer<PieceJustif> containerRightPj;
	private final List<PieceJustif> listPj = new ArrayList<>();

	/* PJ */
	private final ListSelect leftSelectFormulaire;
	private final ListSelect rightSelectFormulaire;
	private final ListSelect communSelectFormulaire;
	private final BeanItemContainer<Formulaire> containerLeftFormulaire;
	private final BeanItemContainer<Formulaire> containerRightFormulaire;
	private final List<Formulaire> listFormulaire = new ArrayList<>();

	/**
	 * Crée une fenêtre d'ajout de pièces complémentaires a une ou plusieurs
	 * formation(s)
	 * @param formations
	 * @param ctrCand
	 * @param pieceJustifs
	 * @param formulaires
	 */
	@SuppressWarnings("unchecked")
	public CtrCandPieceComplementaireWindow(final List<Formation> formations,
		final CentreCandidature ctrCand,
		final List<PieceJustif> pieceJustifs,
		final List<Formulaire> formulaires) {
		/* Style */
		setModal(true);
		setWidth(850, Unit.PIXELS);
		setImmediate(true);
		setResizable(true);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setImmediate(true);
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("formation.piececomp.window", null, UI.getCurrent().getLocale()));

		/* Listes des PJ */
		final List<PieceJustif> listeRightPj = new ArrayList<>();
		final List<PieceJustif> listeLeftPj = new ArrayList<>();

		/* Construction des listes */
		if (pieceJustifs != null && pieceJustifs.size() != 0) {
			pieceJustifController.getPieceJustifsByCtrCandAndScolCentral(ctrCand.getIdCtrCand()).forEach(e -> {
				final Optional<PieceJustif> dp = new ArrayList<>(pieceJustifs).stream()
					.filter(pj -> pj.getCodPj().equals(e.getCodPj()))
					.findFirst();
				if (dp.isPresent()) {
					listeRightPj.add(e);
					listPj.add(e);
				} else {
					listeLeftPj.add(e);
				}
			});
		} else {
			listeLeftPj.addAll(pieceJustifController.getPieceJustifsByCtrCandAndScolCentral(ctrCand.getIdCtrCand()));
		}

		/* Containers */
		containerLeftPj = new BeanItemContainer<>(PieceJustif.class, listeLeftPj);
		containerRightPj = new BeanItemContainer<>(PieceJustif.class, listeRightPj);

		/* Listtes de gauche et droite de PJ */
		leftSelectPj = new ListSelect(
			applicationContext.getMessage("formation.piececomp.pj.dispo", null, UI.getCurrent().getLocale()));
		rightSelectPj = new ListSelect(
			applicationContext.getMessage("formation.piececomp.pj.select", null, UI.getCurrent().getLocale()));
		communSelectPj = new ListSelect(
			applicationContext.getMessage("formation.piececomp.pj.commun", null, UI.getCurrent().getLocale()));
		initListSelectPj(leftSelectPj, containerLeftPj);
		initListSelectPj(rightSelectPj, containerRightPj);
		initListSelectPj(communSelectPj,
			new BeanItemContainer<>(PieceJustif.class,
				pieceJustifController.getPieceJustifsCommunCtrCandEnService(ctrCand.getIdCtrCand())));
		communSelectPj.setEnabled(false);

		/* Layout bouton milieu PJ */
		final VerticalLayout layoutBtnPj = new VerticalLayout();
		// layoutBtnPj.setImmediate(true);
		layoutBtnPj.setHeight(100, Unit.PERCENTAGE);
		layoutBtnPj.setSpacing(true);
		final OneClickButton btnGoRightPj = new OneClickButton(FontAwesome.ARROW_CIRCLE_RIGHT);
		final OneClickButton btnGoLeftPj = new OneClickButton(FontAwesome.ARROW_CIRCLE_LEFT);
		layoutBtnPj.addComponent(btnGoRightPj);
		layoutBtnPj.setComponentAlignment(btnGoRightPj, Alignment.BOTTOM_CENTER);
		layoutBtnPj.addComponent(btnGoLeftPj);
		layoutBtnPj.setComponentAlignment(btnGoLeftPj, Alignment.TOP_CENTER);

		/* action du bouton mise à droite PJ */
		btnGoRightPj.addClickListener(e -> {
			final Set<PieceJustif> collectionLeft = (Set<PieceJustif>) leftSelectPj.getValue();
			collectionLeft.forEach(pj -> {
				containerLeftPj.removeItem(pj);
				containerRightPj.addBean(pj);
				leftSelectPj.setValue(null);
				rightSelectPj.setValue(null);
				listPj.add(pj);
			});
		});

		/* action du bouton mise à gauche PJ */
		btnGoLeftPj.addClickListener(e -> {
			final Set<PieceJustif> collectionRight = (Set<PieceJustif>) rightSelectPj.getValue();
			collectionRight.forEach(pj -> {
				containerRightPj.removeItem(pj);
				containerLeftPj.addBean(pj);
				leftSelectPj.setValue(null);
				rightSelectPj.setValue(null);
				listPj.remove(pj);
			});
		});

		/* Layout contenant les pj */
		final HorizontalLayout hlTwinSelectPj = new HorizontalLayout();
		hlTwinSelectPj.setImmediate(true);
		hlTwinSelectPj.setSpacing(true);
		hlTwinSelectPj.setWidth(100, Unit.PERCENTAGE);
		hlTwinSelectPj.addComponent(leftSelectPj);
		hlTwinSelectPj.setExpandRatio(leftSelectPj, 1);
		hlTwinSelectPj.addComponent(layoutBtnPj);
		hlTwinSelectPj.setExpandRatio(layoutBtnPj, 0.2f);
		hlTwinSelectPj.addComponent(rightSelectPj);
		hlTwinSelectPj.setExpandRatio(rightSelectPj, 1);
		hlTwinSelectPj.addComponent(communSelectPj);
		hlTwinSelectPj.setExpandRatio(communSelectPj, 1);

		/* Listes des Formulaires */
		final List<Formulaire> listeRightFormulaire = new ArrayList<>();
		final List<Formulaire> listeLeftFormulaire = new ArrayList<>();

		/* Construction des listes */
		if (formulaires != null && formulaires.size() != 0) {
			formulaireController.getFormulairesByCtrCandAndScolCentral(ctrCand.getIdCtrCand()).forEach(e -> {
				final Optional<Formulaire> dp = new ArrayList<>(formulaires).stream()
					.filter(formulaire -> formulaire.getCodFormulaire().equals(e.getCodFormulaire()))
					.findFirst();
				if (dp.isPresent()) {
					listeRightFormulaire.add(e);
					listFormulaire.add(e);
				} else {
					listeLeftFormulaire.add(e);
				}
			});
		} else {
			listeLeftFormulaire
				.addAll(formulaireController.getFormulairesByCtrCandAndScolCentral(ctrCand.getIdCtrCand()));
		}

		/* Containers */
		containerLeftFormulaire = new BeanItemContainer<>(Formulaire.class, listeLeftFormulaire);
		containerRightFormulaire = new BeanItemContainer<>(Formulaire.class, listeRightFormulaire);

		/* Listtes de gauche et droite de formulaire */
		leftSelectFormulaire = new ListSelect(applicationContext.getMessage("formation.piececomp.formulaire.dispo",
			null,
			UI.getCurrent().getLocale()));
		rightSelectFormulaire = new ListSelect(applicationContext.getMessage("formation.piececomp.formulaire.select",
			null,
			UI.getCurrent().getLocale()));
		communSelectFormulaire = new ListSelect(applicationContext.getMessage("formation.piececomp.formulaire.commun",
			null,
			UI.getCurrent().getLocale()));
		initListSelectFormulaire(leftSelectFormulaire, containerLeftFormulaire);
		initListSelectFormulaire(rightSelectFormulaire, containerRightFormulaire);
		initListSelectFormulaire(communSelectFormulaire,
			new BeanItemContainer<>(Formulaire.class,
				formulaireController.getFormulairesCommunCtrCandEnService(ctrCand.getIdCtrCand())));
		communSelectFormulaire.setEnabled(false);

		/* Layout bouton milieu formulaire */
		final VerticalLayout layoutBtnFormulaire = new VerticalLayout();
		layoutBtnFormulaire.setHeight(100, Unit.PERCENTAGE);
		layoutBtnFormulaire.setSpacing(true);
		final OneClickButton btnGoRightFormulaire = new OneClickButton(FontAwesome.ARROW_CIRCLE_RIGHT);
		final OneClickButton btnGoLeftFormulaire = new OneClickButton(FontAwesome.ARROW_CIRCLE_LEFT);
		layoutBtnFormulaire.addComponent(btnGoRightFormulaire);
		layoutBtnFormulaire.setComponentAlignment(btnGoRightFormulaire, Alignment.BOTTOM_CENTER);
		layoutBtnFormulaire.addComponent(btnGoLeftFormulaire);
		layoutBtnFormulaire.setComponentAlignment(btnGoLeftFormulaire, Alignment.TOP_CENTER);

		/* action du bouton mise à droite formulaire */
		btnGoRightFormulaire.addClickListener(e -> {
			final Set<Formulaire> collectionLeft = (Set<Formulaire>) leftSelectFormulaire.getValue();
			collectionLeft.forEach(formulaire -> {
				containerLeftFormulaire.removeItem(formulaire);
				containerRightFormulaire.addBean(formulaire);
				leftSelectFormulaire.setValue(null);
				rightSelectFormulaire.setValue(null);
				listFormulaire.add(formulaire);
			});
		});

		/* action du bouton mise à gauche formulaire */
		btnGoLeftFormulaire.addClickListener(e -> {
			final Set<Formulaire> collectionRight = (Set<Formulaire>) rightSelectFormulaire.getValue();
			collectionRight.forEach(formulaire -> {
				containerRightFormulaire.removeItem(formulaire);
				containerLeftFormulaire.addBean(formulaire);
				leftSelectFormulaire.setValue(null);
				rightSelectFormulaire.setValue(null);
				listFormulaire.remove(formulaire);
			});
		});

		/* Layout contenant les formulaire */
		final HorizontalLayout hlTwinSelectFormulaire = new HorizontalLayout();
		hlTwinSelectFormulaire.setImmediate(true);
		hlTwinSelectFormulaire.setSpacing(true);
		hlTwinSelectFormulaire.setWidth(100, Unit.PERCENTAGE);
		hlTwinSelectFormulaire.addComponent(leftSelectFormulaire);
		hlTwinSelectFormulaire.setExpandRatio(leftSelectFormulaire, 1);
		hlTwinSelectFormulaire.addComponent(layoutBtnFormulaire);
		hlTwinSelectFormulaire.setExpandRatio(layoutBtnFormulaire, 0.2f);
		hlTwinSelectFormulaire.addComponent(rightSelectFormulaire);
		hlTwinSelectFormulaire.setExpandRatio(rightSelectFormulaire, 1);
		hlTwinSelectFormulaire.addComponent(communSelectFormulaire);
		hlTwinSelectFormulaire.setExpandRatio(communSelectFormulaire, 1);

		/* Sheet */
		final TabSheet sheet = new TabSheet();
		sheet.setImmediate(true);
		sheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
		sheet.setSizeFull();
		sheet.addSelectedTabChangeListener(e -> center());
		layout.addComponent(sheet);
		layout.setExpandRatio(sheet, 1);

		/* Layout avec message d'info */
		final VerticalLayout vlPj = new VerticalLayout();
		vlPj.setSizeFull();
		vlPj.setMargin(true);
		vlPj.setSpacing(true);

		final VerticalLayout vlForm = new VerticalLayout();
		vlForm.setSizeFull();
		vlForm.setMargin(true);
		vlForm.setSpacing(true);

		/* Affichage du message si plus d'une formation selectionnées */
		if (formations.size() > 1) {
			final Label labelInfoPj = new Label(
				applicationContext.getMessage("formation.piececomp.info.pj", null, UI.getCurrent().getLocale()));
			labelInfoPj.addStyleName(ValoTheme.LABEL_TINY);
			labelInfoPj.addStyleName(StyleConstants.LABEL_ITALIC);
			vlPj.addComponent(labelInfoPj);

			final Label labelInfoForm = new Label(
				applicationContext.getMessage("formation.piececomp.info.form", null, UI.getCurrent().getLocale()));
			labelInfoForm.addStyleName(ValoTheme.LABEL_TINY);
			labelInfoForm.addStyleName(StyleConstants.LABEL_ITALIC);
			vlForm.addComponent(labelInfoForm);
		}

		/* Ajout des listes */
		vlPj.addComponent(hlTwinSelectPj);
		vlForm.addComponent(hlTwinSelectFormulaire);

		sheet.addTab(vlPj,
			applicationContext.getMessage("formation.piececomp.sheet.pj", null, UI.getCurrent().getLocale()));
		sheet.addTab(vlForm,
			applicationContext.getMessage("formation.piececomp.sheet.formulaire",
				null,
				UI.getCurrent().getLocale()));

		/* Ajoute les boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
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
			final ConfirmWindow confirmWindow = new ConfirmWindow(
				applicationContext.getMessage("formation.piececomp.window.confirm",
					new Object[]
					{ formations.size() },
					UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(c -> {
				/* Enregistre la langue saisie */
				formationController.savePiecesComplementaires(formations, listPj, listFormulaire);
				/* Ferme la fenêtre */
				close();
			});
			UI.getCurrent().addWindow(confirmWindow);
		});
		buttonsLayout.addComponent(btnEnregistrer);
		buttonsLayout.setComponentAlignment(btnEnregistrer, Alignment.MIDDLE_RIGHT);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * Initialise les listes de PJ
	 * @param listSelect
	 * @param container
	 */
	private void initListSelectPj(final ListSelect listSelect, final BeanItemContainer<PieceJustif> container) {
		listSelect.setWidth(100, Unit.PERCENTAGE);
		listSelect.setMultiSelect(true);
		listSelect.setNullSelectionAllowed(false);
		listSelect.setContainerDataSource(container);
		listSelect.setImmediate(true);
		listSelect.setItemCaptionPropertyId(PieceJustif_.libPj.getName());
	}

	/**
	 * Initialise les listes de PJ
	 * @param listSelect
	 * @param container
	 */
	private void initListSelectFormulaire(final ListSelect listSelect, final BeanItemContainer<Formulaire> container) {
		listSelect.setWidth(100, Unit.PERCENTAGE);
		listSelect.setMultiSelect(true);
		listSelect.setNullSelectionAllowed(false);
		listSelect.setContainerDataSource(container);
		listSelect.setImmediate(true);
		listSelect.setItemCaptionPropertyId(Formulaire_.libFormulaire.getName());
	}
}
