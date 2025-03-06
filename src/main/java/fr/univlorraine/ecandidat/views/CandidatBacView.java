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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatParcoursController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatBacListener;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;

/**
 * Page de gestion du bac du candidat
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CandidatBacView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatBacView extends CandidatViewTemplate implements View, CandidatBacListener {

	public static final String NAME = "candidatBacView";

	public static final String[] FIELDS_ORDER_BAC = { SimpleTablePresentation.CHAMPS_TITLE, SimpleTablePresentation.CHAMPS_VALUE };

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;

	/* Composants */
	private final BeanItemContainer<SimpleTablePresentation> container = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	private final TableFormating table = new TableFormating(null, container);
	private Label noInfoLabel = new Label();

	/* Composants */

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		super.init();
		setNavigationButton(NAME);

		/* Edition des donneés */
		final OneClickButton btnEdit = new OneClickButton(applicationContext.getMessage("btnSaisir", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEdit.addClickListener(e -> {
			candidatParcoursController.editBac(candidat, this);
		});
		addGenericButton(btnEdit, Alignment.MIDDLE_LEFT);

		noInfoLabel = new Label(applicationContext.getMessage("infobac.noinfo", null, UI.getCurrent().getLocale()));
		addGenericComponent(noInfoLabel);

		/* Table de présentation */
		table.setSizeFull();
		table.setVisibleColumns((Object[]) FIELDS_ORDER_BAC);
		table.setColumnCollapsingAllowed(false);
		table.setColumnReorderingAllowed(false);
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table.setSelectable(false);
		table.setImmediate(true);
		table.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 250);
		table.setCellStyleGenerator((components, itemId, columnId) -> {
			if (columnId != null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)) {
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		addGenericComponent(table);
		setGenericExpandRatio(table);
	}

	/**
	 * Met a jour les composants
	 */
	private void majComponentsBac(final CandidatBacOuEqu bac) {
		if (bac == null) {
			container.removeAllItems();
			table.setVisible(false);
			noInfoLabel.setVisible(true);
			setGenericLayoutSizeFull(false);
		} else {
			container.removeAllItems();
			final List<SimpleTablePresentation> liste = candidatParcoursController.getInformationsBac(bac);
			container.addAll(liste);
			table.setVisible(true);
			noInfoLabel.setVisible(false);
			setGenericLayoutSizeFull(true);
			if (bac.getTemUpdatableBac() && !isLectureSeule && !isArchive) {
				setButtonVisible(true);
			} else {
				setButtonVisible(false);
			}
		}
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
		if (majView(applicationContext.getMessage("infobac.title", null, UI.getCurrent().getLocale()), true, ConstanteUtils.LOCK_BAC)) {
			majComponentsBac(candidat.getCandidatBacOuEqu());
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		candidatController.unlockCandidatRessource(cptMin, ConstanteUtils.LOCK_BAC);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatBacListener#bacModified(fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu)
	 */
	@Override
	public void bacModified(final CandidatBacOuEqu bac) {
		candidat.setCandidatBacOuEqu(bac);
		majComponentsBac(candidat.getCandidatBacOuEqu());
	}
}
