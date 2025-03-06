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
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatureCtrCandController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.IndividuController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.MotivationAvis_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecisionCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision_;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

/**
 * Fenêtre de visu de l'histo des décisions d'une candidature
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class CtrCandShowHistoWindow extends Window {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient CandidatureCtrCandController candidatureCtrCandController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient DateTimeFormatter formatterDateTime;

	/* Le listener */
	private DeleteAvisWindowListener deleteAvisWindowListener;

	private final static String CHAMPS_CREATE = "create";
	private final static String CHAMPS_VALIDATE = "validate";
	private final static String CHAMPS_ACTION_DELETE = "delete";
	private final static String CHAMPS_PRESELECT = "preselect";

	public static String[] FIELDS_ORDER = {
		CHAMPS_CREATE,
		TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName(),
		TypeDecisionCandidature_.motivationAvis.getName() + "." + MotivationAvis_.libMotiv.getName(),
		CHAMPS_VALIDATE,
		TypeDecisionCandidature_.temAppelTypeDecCand.getName(),
		TypeDecisionCandidature_.commentTypeDecCand.getName(),
		TypeDecisionCandidature_.listCompRangTypDecCand.getName(),
		TypeDecisionCandidature_.listCompRangReelTypDecCand.getName(),
		CHAMPS_PRESELECT,
		CHAMPS_ACTION_DELETE };

	/* Composants */

	private OneClickButton btnClose;

	private final BeanItemContainer<TypeDecisionCandidature> container = new BeanItemContainer<>(TypeDecisionCandidature.class);
	private final TableFormating motivationAvisTable = new TableFormating(null, container);
	private Boolean hasRightToDelete = false;

	/**
	 * Crée une fenêtre de visu de l'histo des décisions d'une candidature
	 * @param candidature
	 * @param listeDroit
	 */
	public CtrCandShowHistoWindow(final Candidature candidature, final List<DroitFonctionnalite> listeDroit) {
		/* Droit de suppression */
		hasRightToDelete = droitProfilController.hasAccessToFonctionnalite(NomenclatureUtils.FONCTIONNALITE_VISU_HISTO_AVIS, listeDroit, false);
		/* Style */
		setModal(true);
		setWidth(100, Unit.PERCENTAGE);
		setResizable(true);
		setClosable(true);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("candidature.histoavis.window",
			new Object[]
			{
				candidatController.getLibelleTitle(candidature.getCandidat().getCompteMinima()) },
			UI.getCurrent().getLocale()));
		final Formation f = candidature.getFormation();
		final String msg = applicationContext.getMessage("candidature.histoavis.window.detail", new Object[] { f.getCommission().getLibComm(), f.getLibForm() }, UI.getCurrent().getLocale());
		final Label label = new Label(msg);
		label.addStyleName(StyleConstants.VIEW_SUBTITLE);
		layout.addComponent(label);

		container.addAll(candidature.getTypeDecisionCandidatures());
		container.addNestedContainerProperty(TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName());
		container.addNestedContainerProperty(TypeDecisionCandidature_.motivationAvis.getName() + "." + MotivationAvis_.libMotiv.getName());
		motivationAvisTable.addBooleanColumn(TypeDecisionCandidature_.temValidTypeDecCand.getName());
		motivationAvisTable.addBooleanColumn(TypeDecisionCandidature_.temAppelTypeDecCand.getName());
		motivationAvisTable.setSizeFull();
		motivationAvisTable.addGeneratedColumn(CHAMPS_CREATE, new ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final TypeDecisionCandidature typeDec = (TypeDecisionCandidature) itemId;
				return applicationContext.getMessage("candidature.histoavis.dateuser",
					new Object[]
					{
						formatterDateTime.format(typeDec.getDatCreTypeDecCand()),
						individuController.getLibIndividu(typeDec.getUserCreTypeDecCand()) },
					UI.getCurrent().getLocale());
			}
		});
		motivationAvisTable.addGeneratedColumn(CHAMPS_VALIDATE, new ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final TypeDecisionCandidature typeDec = (TypeDecisionCandidature) itemId;
				if (typeDec.getTemValidTypeDecCand() && typeDec.getDatValidTypeDecCand() != null) {
					return applicationContext.getMessage("candidature.histoavis.dateuser",
						new Object[]
						{
							formatterDateTime.format(typeDec.getDatValidTypeDecCand()),
							individuController.getLibIndividu(typeDec.getUserValidTypeDecCand()) },
						UI.getCurrent().getLocale());
				}
				return null;
			}
		});

		motivationAvisTable.addGeneratedColumn(CHAMPS_PRESELECT, new ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				return candidatureCtrCandController.getComplementPreselect((TypeDecisionCandidature) itemId);
			}
		});
		String[] fieldsOrderToUse = FIELDS_ORDER;
		/* Verification que l'utilisateur a le droit d'ecrire un postit */
		if (getOptDecToDelete().isPresent()) {
			motivationAvisTable.addGeneratedColumn(CHAMPS_ACTION_DELETE, new ColumnGenerator() {

				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final TypeDecisionCandidature typeDec = (TypeDecisionCandidature) itemId;
					if (typeDec != null && getOptDecToDelete().isPresent() && typeDec.equals(getOptDecToDelete().get())) {
						final OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("candidature.histoavis.delete.btn", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
						btnDelete.addStyleName(ValoTheme.BUTTON_TINY);
						btnDelete.addClickListener(e -> {
							final ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("candidature.histoavis.confirmDelete",
								new Object[]
								{
									typeDec.getTypeDecision().getLibTypDec() },
								UI.getCurrent().getLocale()),
								applicationContext.getMessage("candidature.histoavis.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
							confirmWindow.addBtnOuiListener(o -> {
								final Candidature cand = candidatureCtrCandController.deleteAvis(candidature, typeDec);
								if (cand != null) {
									motivationAvisTable.removeItem(typeDec);
									deleteAvisWindowListener.delete(candidature);
									if (!getOptDecToDelete().isPresent()) {
										motivationAvisTable.removeContainerProperty(CHAMPS_ACTION_DELETE);
									}
								}
							});
							UI.getCurrent().addWindow(confirmWindow);
						});
						return btnDelete;
					}
					return null;
				}
			});
		} else {
			fieldsOrderToUse = ArrayUtils.removeElement(FIELDS_ORDER, CHAMPS_ACTION_DELETE);
		}

		motivationAvisTable.setVisibleColumns((Object[]) fieldsOrderToUse);
		for (final String fieldName : fieldsOrderToUse) {
			motivationAvisTable.setColumnHeader(fieldName, applicationContext.getMessage("candidature.histoavis." + fieldName, null, UI.getCurrent().getLocale()));
		}
		motivationAvisTable.setSortContainerPropertyId(TypeDecisionCandidature_.idTypeDecCand.getName());
		motivationAvisTable.setSortAscending(false);
		motivationAvisTable.sort();
		motivationAvisTable.setColumnCollapsingAllowed(true);
		motivationAvisTable.setColumnReorderingAllowed(true);
		motivationAvisTable.setSortEnabled(false);
		motivationAvisTable.setSelectable(false);
		motivationAvisTable.setImmediate(true);
		layout.addComponent(motivationAvisTable);
		layout.setExpandRatio(motivationAvisTable, 1);

		/* Ajoute les boutons */
		final HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnClose = new OneClickButton(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnClose.addClickListener(e -> close());
		buttonsLayout.addComponent(btnClose);
		buttonsLayout.setComponentAlignment(btnClose, Alignment.MIDDLE_CENTER);

		/* Centre la fenêtre */
		center();
	}

	private Optional<TypeDecisionCandidature> getOptDecToDelete() {
		/* Optional delete */
		Optional<TypeDecisionCandidature> optDec = Optional.empty();
		if (!hasRightToDelete) {
			return optDec;
		}

		/* Liste */
		final List<TypeDecisionCandidature> liste = container.getItemIds();

		/* On cherche l'id max de la liste car on a le droit de ne supprimer que le dernier avis non validé */
		final OptionalInt maxId = liste.stream().mapToInt(TypeDecisionCandidature::getIdTypeDecCand).max();
		/* On récupère la decision correspondante */
		if (maxId.isPresent()) {
			optDec = liste.stream().filter(e -> e.getIdTypeDecCand().equals(maxId.getAsInt()) && !e.getTemValidTypeDecCand()).findFirst();
		}
		return optDec;
	}

	/**
	 * Défini le 'DeleteAvisWindowListener' utilisé
	 * @param deleteAvisWindowListener
	 */
	public void addDeleteAvisWindowListener(final DeleteAvisWindowListener deleteAvisWindowListener) {
		this.deleteAvisWindowListener = deleteAvisWindowListener;
	}

	/** Interface pour récupérer une action */
	public interface DeleteAvisWindowListener extends Serializable {
		/**
		 * Appelé lorsque une decision est supprimée
		 * @param candidature
		 */
		void delete(Candidature candidature);
	}
}
