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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatParcoursController;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPro;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPro_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatFormationProListener;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;

/**
 * Page de gestion des parcours pro du candidat
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CandidatFormationProView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatFormationProView extends CandidatViewTemplate implements View, CandidatFormationProListener {

	public static final String NAME = "candidatFormationProView";

	public static final String[] FIELDS_ORDER_FORMATIONS = {
		CandidatCursusPro_.anneeCursusPro.getName(),
		CandidatCursusPro_.intituleCursusPro.getName(),
		CandidatCursusPro_.dureeCursusPro.getName(),
		CandidatCursusPro_.organismeCursusPro.getName(),
		CandidatCursusPro_.objectifCursusPro.getName()
	};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;

	/* FormationPros */
	private final BeanItemContainer<CandidatCursusPro> formationProContainer = new BeanItemContainer<CandidatCursusPro>(CandidatCursusPro.class);
	private final TableFormating formationProTable = new TableFormating(null, formationProContainer);

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		super.init();
		setNavigationButton(NAME);

		/* Indications pour les formations pro */
		setSubtitle(applicationContext.getMessage("formationpro.indication", null, UI.getCurrent().getLocale()));

		final OneClickButton btnNewFormationPro = new OneClickButton(applicationContext.getMessage("formationpro.btn.new", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNewFormationPro.setEnabled(true);
		btnNewFormationPro.addClickListener(e -> {
			candidatParcoursController.editFormationPro(candidat, null, this);
		});
		addGenericButton(btnNewFormationPro, Alignment.MIDDLE_LEFT);

		final OneClickButton btnEditFormationPro = new OneClickButton(applicationContext.getMessage("btnModifier", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEditFormationPro.setEnabled(false);
		btnEditFormationPro.addClickListener(e -> {
			if (formationProTable.getValue() instanceof CandidatCursusPro) {
				candidatParcoursController.editFormationPro(candidat, (CandidatCursusPro) formationProTable.getValue(), this);
			}
		});
		addGenericButton(btnEditFormationPro, Alignment.MIDDLE_CENTER);

		final OneClickButton btnDeleteFormationPro = new OneClickButton(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()), FontAwesome.TRASH_O);
		btnDeleteFormationPro.setEnabled(false);
		btnDeleteFormationPro.addClickListener(e -> {
			if (formationProTable.getValue() instanceof CandidatCursusPro) {
				candidatParcoursController.deleteFormationPro(candidat, (CandidatCursusPro) formationProTable.getValue(), this);
			}
		});
		addGenericButton(btnDeleteFormationPro, Alignment.MIDDLE_RIGHT);

		/* Table formationPro */
		formationProTable.setSizeFull();
		formationProTable.setVisibleColumns((Object[]) FIELDS_ORDER_FORMATIONS);
		for (final String fieldName : FIELDS_ORDER_FORMATIONS) {
			formationProTable.setColumnHeader(fieldName, applicationContext.getMessage("formationpro." + fieldName, null, UI.getCurrent().getLocale()));
		}
		formationProTable.setColumnCollapsingAllowed(true);
		formationProTable.setColumnReorderingAllowed(true);
		formationProTable.setSortContainerPropertyId(CandidatCursusPro_.anneeCursusPro.getName());
		formationProTable.setSelectable(true);
		formationProTable.setImmediate(true);
		formationProTable.addItemSetChangeListener(e -> formationProTable.sanitizeSelection());
		formationProTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de CandidatCursusPro sont actifs seulement si une CandidatCursusPro est sélectionnée. */
			final boolean formationProIsSelected = formationProTable.getValue() instanceof CandidatCursusPro;
			btnEditFormationPro.setEnabled(formationProIsSelected);
			btnDeleteFormationPro.setEnabled(formationProIsSelected);
		});
		formationProTable.addItemClickListener(e -> {
			if (e.isDoubleClick() && !isLectureSeule && !isArchive) {
				formationProTable.select(e.getItemId());
				btnEditFormationPro.click();
			}
		});
		addGenericComponent(formationProTable);
		setGenericExpandRatio(formationProTable);
	}

	/**
	 * Met a jour les composants
	 */
	private void majComponents() {
		formationProContainer.removeAllItems();
		formationProContainer.addAll(candidat.getCandidatCursusPros());
		formationProTable.sort();
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
		if (majView(applicationContext.getMessage("formationpro.title", null, UI.getCurrent().getLocale()), true, ConstanteUtils.LOCK_FORMATION_PRO)) {
			majComponents();
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		candidatController.unlockCandidatRessource(cptMin, ConstanteUtils.LOCK_FORMATION_PRO);
		super.detach();

	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatFormationProListener#formationProModified(java.util.List)
	 */
	@Override
	public void formationProModified(final List<CandidatCursusPro> candidatCursusPros) {
		candidat.setCandidatCursusPros(candidatCursusPros);
		majComponents();
	}

}