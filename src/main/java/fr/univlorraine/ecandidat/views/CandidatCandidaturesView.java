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
package fr.univlorraine.ecandidat.views;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatureController;
import fr.univlorraine.ecandidat.controllers.CandidaturePieceController;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.I18nController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission_;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement_;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCommission;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureCandidatViewListener;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;

/**
 * Page de gestion des candidatures du candidat
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CandidatCandidaturesView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatCandidaturesView extends CandidatViewTemplate implements View, CandidatureCandidatViewListener {

	public static final String NAME = "candidatCandidaturesView";

	public static final String[] FIELDS_ORDER_CANDIDAT = {
		Candidature_.formation.getName() + "." + Formation_.libForm.getName(),
		Candidature_.formation.getName() + "." + Formation_.datRetourForm.getName(),
		ConstanteUtils.CANDIDATURE_LIB_STATUT,
		ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION
	};

	public static final String[] FIELDS_ORDER_GEST = {
		Candidature_.formation.getName() + "." + Formation_.libForm.getName(),
		Candidature_.formation.getName() + "." + Formation_.datRetourForm.getName(),
		Candidature_.typeTraitement.getName() + "." + TypeTraitement_.libTypTrait.getName(),
		Candidature_.temValidTypTraitCand.getName(),
		ConstanteUtils.CANDIDATURE_LIB_STATUT,
		ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION,
		Candidature_.formation.getName() + "." + Formation_.commission.getName() + "." + Commission_.centreCandidature.getName() + "." + CentreCandidature_.libCtrCand.getName()
	};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient CandidaturePieceController candidaturePieceController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient DateTimeFormatter formatterDate;

	/* Composants */
	private BeanItemContainer<Candidature> candidatureContainer = new BeanItemContainer<>(Candidature.class);
	private TableFormating candidatureTable = new TableFormating(null, candidatureContainer);
	private OneClickButton btnNewCandidature;

	/**/

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		super.init();
		setNavigationButton(CandidatFormationProView.NAME, null);
		String[] fieldsOrderToUse = FIELDS_ORDER_CANDIDAT;

		btnNewCandidature = new OneClickButton(applicationContext.getMessage("candidature.btn.new", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNewCandidature.setEnabled(true);
		btnNewCandidature.addClickListener(e -> {
			candidatureController.editNewCandidature();
		});
		addGenericButton(btnNewCandidature, Alignment.MIDDLE_LEFT);

		/* L'authentification */
		Authentication auth = userController.getCurrentAuthentication();

		/* Gestionnaire? */
		Boolean isGestionnaire = userController.isGestionnaireCandidat(auth);

		/* On a besoin de savoir si un gestionnaire est sur cet ecran et si il a les droits pour ouvrir la candidature du candidat */
		SecurityCentreCandidature scc = userController.getCentreCandidature(auth);
		SecurityCommission sc = userController.getCommission(auth);

		/* Table candidatures */
		if (isGestionnaire) {
			fieldsOrderToUse = FIELDS_ORDER_GEST;
			btnNewCandidature.setCaption(applicationContext.getMessage("candidature.btn.proposition", null, UI.getCurrent().getLocale()));
			candidatureContainer.addNestedContainerProperty(Candidature_.formation.getName() + "." + Formation_.commission.getName() + "." + Commission_.centreCandidature.getName() + "." + CentreCandidature_.libCtrCand.getName());
			candidatureContainer.addNestedContainerProperty(Candidature_.typeTraitement.getName() + "." + TypeTraitement_.libTypTrait.getName());
			candidatureTable.addBooleanColumn(Candidature_.temValidTypTraitCand.getName());
		}
		candidatureContainer.addNestedContainerProperty(Candidature_.formation.getName() + "." + Formation_.libForm.getName());
		candidatureContainer.addNestedContainerProperty(Candidature_.formation.getName() + "." + Formation_.datRetourForm.getName());

		candidatureTable.addGeneratedColumn(Candidature_.formation.getName() + "." + Formation_.datRetourForm.getName(), new ColumnGenerator() {
			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				LocalDate dateRetourCandidat = candidatureController.getDateRetourCandidat((Candidature) itemId);
				return dateRetourCandidat != null ? formatterDate.format(dateRetourCandidat) : "";
			}
		});

		candidatureTable.addGeneratedColumn(ConstanteUtils.CANDIDATURE_LIB_STATUT, new ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final Candidature candidature = (Candidature) itemId;
				return new Label(i18nController.getI18nTraduction(candidature.getTypeStatut().getI18nLibTypStatut()));
			}
		});
		candidatureTable.addGeneratedColumn(ConstanteUtils.CANDIDATURE_LIB_LAST_DECISION, new ColumnGenerator() {

			@Override
			public Object generateCell(final Table source, final Object itemId, final Object columnId) {
				final Candidature candidature = (Candidature) itemId;
				return candidatureController.getLibLastTypeDecisionCandidature(candidature.getLastTypeDecision(), !isGestionnaire);
			}
		});

		candidatureTable.setSizeFull();
		candidatureTable.setVisibleColumns((Object[]) fieldsOrderToUse);
		for (String fieldName : fieldsOrderToUse) {
			candidatureTable.setColumnHeader(fieldName, applicationContext.getMessage("candidature." + fieldName, null, UI.getCurrent().getLocale()));
		}
		candidatureTable.setSortContainerPropertyId(Candidature_.idCand.getName());
		candidatureTable.setColumnCollapsingAllowed(true);
		candidatureTable.setColumnReorderingAllowed(true);
		candidatureTable.setSelectable(true);
		candidatureTable.setImmediate(true);
		candidatureTable.addItemSetChangeListener(e -> candidatureTable.sanitizeSelection());

		OneClickButton btnOpenCandidature = new OneClickButton(applicationContext.getMessage("btnOpen", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnOpenCandidature.setEnabled(false);
		btnOpenCandidature.addClickListener(e -> {
			if (candidatureTable.getValue() instanceof Candidature) {
				Candidature candidature = (Candidature) candidatureTable.getValue();
				candidatureController.openCandidatureCandidat(candidature, isArchive, this);
			}
		});
		addGenericButton(btnOpenCandidature, Alignment.MIDDLE_RIGHT);
		candidatureTable.addValueChangeListener(e -> {
			if (!(candidatureTable.getValue() instanceof Candidature)) {
				/* Les boutons d'édition et de suppression de Candidature sont actifs seulement si une Candidature est sélectionnée. */
				btnOpenCandidature.setEnabled(false);
				return;
			}
			if (userController.isScolCentrale(auth)) {
				btnOpenCandidature.setEnabled(true);
				return;
			}

			/* Verification que l'utilisateur a le droit d'ouvrir la candidature */
			Candidature candidature = (Candidature) candidatureTable.getValue();
			if (userController.isCandidat(auth) && candidatureController.isCandidatOfCandidature(candidature)) {
				btnOpenCandidature.setEnabled(true);
				return;
			}

			if (candidatureController.hasRightToOpenCandidature(candidature, scc, sc)) {
				btnOpenCandidature.setEnabled(true);
			} else {
				btnOpenCandidature.setEnabled(false);
			}

		});
		candidatureTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				candidatureTable.select(e.getItemId());
				btnOpenCandidature.click();
			}
		});

		addGenericComponent(candidatureTable);
		setGenericExpandRatio(candidatureTable);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
		if (majView(applicationContext.getMessage("candidatures.title", null, UI.getCurrent().getLocale()), true, null)) {
			candidatureContainer.removeAllItems();
			candidatureContainer.addAll(candidatureController.getCandidatures(candidat));
		}

		String param = event.getParameters();
		if (param != null && !param.equals("")) {
			try {
				Integer id = Integer.parseInt(param);
				Candidature candidature = candidatureController.loadCandidature(id);
				if (candidature != null) {
					candidatureController.openCandidatureCandidat(candidature, isArchive, this);
				}
			} catch (NumberFormatException nfe) {
			}
		}
		setButtonVisible(true);
		Authentication auth = userController.getCurrentAuthentication();
		if (!userController.isGestionnaireCandidat(auth) && !userController.isCandidat(auth)) {
			btnNewCandidature.setVisible(false);
		} else if (isArchive) {
			btnNewCandidature.setVisible(false);
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();

	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureCandidatViewListener#candidatureCanceled(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void candidatureCanceled(final Candidature candidature) {
		candidatureTable.removeItem(candidature);
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatureCandidatViewListener#statutDossierModified(fr.univlorraine.ecandidat.entities.ecandidat.Candidature)
	 */
	@Override
	public void statutDossierModified(final Candidature candidature) {
		candidatureTable.removeItem(candidature);
		candidatureTable.addItem(candidature);
		candidatureTable.sort();

	}
}