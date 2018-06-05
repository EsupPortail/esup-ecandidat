/** ESUP-Portail eCandidat - Copyright (c) 2016 ESUP-Portail consortium
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

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

/** Fenêtre de visu de l'histo des décisions d'une candidature
 *
 * @author Kevin Hergalant */
@Configurable(preConstruction = true)
public class CtrCandShowHistoWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = -7776558654950981770L;

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

	/* Le listener */
	private DeleteAvisWindowListener deleteAvisWindowListener;

	private final static String CHAMPS_ACTION_DELETE = "delete";

	public static String[] FIELDS_ORDER = {TypeDecisionCandidature_.datCreTypeDecCand.getName(),
			TypeDecisionCandidature_.userCreTypeDecCand.getName(),
			TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName(),
			TypeDecisionCandidature_.motivationAvis.getName() + "." + MotivationAvis_.libMotiv.getName(),
			TypeDecisionCandidature_.temValidTypeDecCand.getName(),
			TypeDecisionCandidature_.datValidTypeDecCand.getName(),
			TypeDecisionCandidature_.userValidTypeDecCand.getName(),
			TypeDecisionCandidature_.temAppelTypeDecCand.getName(),
			TypeDecisionCandidature_.commentTypeDecCand.getName(),
			TypeDecisionCandidature_.listCompRangTypDecCand.getName(),
			TypeDecisionCandidature_.preselectDateTypeDecCand.getName(),
			TypeDecisionCandidature_.preselectHeureTypeDecCand.getName(),
			TypeDecisionCandidature_.preselectLieuTypeDecCand.getName(), CHAMPS_ACTION_DELETE};

	/* Composants */

	private OneClickButton btnClose;

	/** Crée une fenêtre de visu de l'histo des décisions d'une candidature
	 *
	 * @param candidature
	 * @param listeDroit
	 */
	public CtrCandShowHistoWindow(final Candidature candidature, final List<DroitFonctionnalite> listeDroit) {
		/* Style */
		setModal(true);
		setWidth(100, Unit.PERCENTAGE);
		setResizable(true);
		setClosable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("candidature.histoavis.window", new Object[] {
				candidatController.getLibelleTitle(candidature.getCandidat().getCompteMinima())}, UI.getCurrent().getLocale()));
		Formation f = candidature.getFormation();
		String msg = applicationContext.getMessage("candidature.histoavis.window.detail", new Object[] {f.getCommission().getLibComm(), f.getLibForm()}, UI.getCurrent().getLocale());
		Label label = new Label(msg);
		label.addStyleName(StyleConstants.VIEW_SUBTITLE);
		layout.addComponent(label);

		BeanItemContainer<TypeDecisionCandidature> container = new BeanItemContainer<>(TypeDecisionCandidature.class, candidature.getTypeDecisionCandidatures());
		container.addNestedContainerProperty(TypeDecisionCandidature_.typeDecision.getName() + "." + TypeDecision_.libTypDec.getName());
		container.addNestedContainerProperty(TypeDecisionCandidature_.motivationAvis.getName() + "." + MotivationAvis_.libMotiv.getName());
		TableFormating motivationAvisTable = new TableFormating(null, container);
		motivationAvisTable.addBooleanColumn(TypeDecisionCandidature_.temValidTypeDecCand.getName());
		motivationAvisTable.addBooleanColumn(TypeDecisionCandidature_.temAppelTypeDecCand.getName());
		motivationAvisTable.setSizeFull();
		motivationAvisTable.setSortContainerPropertyId(TypeDecisionCandidature_.datCreTypeDecCand.getName());
		motivationAvisTable.addGeneratedColumn(TypeDecisionCandidature_.userCreTypeDecCand.getName(), new ColumnGenerator() {

			/*** serialVersionUID */
			private static final long serialVersionUID = 1368300795292841902L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final TypeDecisionCandidature typeDec = (TypeDecisionCandidature) itemId;
				String user = typeDec.getUserCreTypeDecCand();
				return individuController.getLibIndividu(user);
			}
		});
		motivationAvisTable.addGeneratedColumn(TypeDecisionCandidature_.userValidTypeDecCand.getName(), new ColumnGenerator() {
			/*** serialVersionUID */
			private static final long serialVersionUID = 5764883081589719005L;

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final TypeDecisionCandidature typeDec = (TypeDecisionCandidature) itemId;
				String user = typeDec.getUserValidTypeDecCand();
				return individuController.getLibIndividu(user);
			}
		});
		String[] fieldsOrderToUse = FIELDS_ORDER;
		/* Verification que l'utilisateur a le droit d'ecrire un postit */
		if (droitProfilController.hasAccessToFonctionnalite(NomenclatureUtils.FONCTIONNALITE_VISU_HISTO_AVIS, listeDroit, false)) {
			motivationAvisTable.addGeneratedColumn(CHAMPS_ACTION_DELETE, new ColumnGenerator() {

				/*** serialVersionUID */
				private static final long serialVersionUID = 5764883081589719005L;

				@Override
				public Object generateCell(final Table source, final Object itemId, final Object columnId) {
					final TypeDecisionCandidature typeDec = (TypeDecisionCandidature) itemId;
					if (typeDec != null && !typeDec.getTemValidTypeDecCand()) {
						OneClickButton btnDelete = new OneClickButton(applicationContext.getMessage("candidature.histoavis.delete.btn", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
						btnDelete.addClickListener(e -> {
							ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("candidature.histoavis.confirmDelete", new Object[] {
									typeDec.getTypeDecision().getLibTypDec()}, UI.getCurrent().getLocale()), applicationContext.getMessage("candidature.histoavis.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
							confirmWindow.addBtnOuiListener(o -> {
								Candidature cand = candidatureCtrCandController.deleteAvis(candidature, typeDec);
								if (cand != null) {
									motivationAvisTable.removeItem(typeDec);
									deleteAvisWindowListener.delete(candidature);
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
			fieldsOrderToUse = (String[]) ArrayUtils.removeElement(FIELDS_ORDER, CHAMPS_ACTION_DELETE);
		}

		motivationAvisTable.setVisibleColumns((Object[]) fieldsOrderToUse);
		for (String fieldName : fieldsOrderToUse) {
			motivationAvisTable.setColumnHeader(fieldName, applicationContext.getMessage("candidature.histoavis." + fieldName, null, UI.getCurrent().getLocale()));
		}

		motivationAvisTable.setSortAscending(false);
		motivationAvisTable.setColumnCollapsingAllowed(true);
		motivationAvisTable.setColumnReorderingAllowed(true);
		motivationAvisTable.setSelectable(false);
		motivationAvisTable.setImmediate(true);
		layout.addComponent(motivationAvisTable);
		layout.setExpandRatio(motivationAvisTable, 1);

		/* Ajoute les boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
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

	/** Défini le 'DeleteAvisWindowListener' utilisé
	 *
	 * @param deleteAvisWindowListener
	 */
	public void addDeleteAvisWindowListener(final DeleteAvisWindowListener deleteAvisWindowListener) {
		this.deleteAvisWindowListener = deleteAvisWindowListener;
	}

	/** Interface pour récupérer une action */
	public interface DeleteAvisWindowListener extends Serializable {
		/** Appelé lorsque une decision est supprimée
		 *
		 * @param candidature
		 */
		public void delete(Candidature candidature);
	}
}
