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
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.AdresseListener;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;

/**
 * Vue pour l'adresse du candidat
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CandidatAdresseView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatAdresseView extends CandidatViewTemplate implements View, AdresseListener {

	public static final String NAME = "candidatAdresseView";

	public static final String[] FIELDS_ORDER = { SimpleTablePresentation.CHAMPS_TITLE, SimpleTablePresentation.CHAMPS_VALUE };

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;

	/* Composants */
	private final BeanItemContainer<SimpleTablePresentation> container = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	private final TableFormating table = new TableFormating(null, container);
	private final Label noInfoLabel = new Label();

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		super.init();
		setNavigationButton(NAME);

		/* Edition des donneés d'adresse */
		final OneClickButton btnEdit = new OneClickButton(FontAwesome.PENCIL);
		btnEdit.setCaption(applicationContext.getMessage("adresse.edit.btn", null, UI.getCurrent().getLocale()));
		btnEdit.addClickListener(e -> {
			candidatController.editAdresse(cptMin, this);
		});
		addGenericButton(btnEdit, Alignment.MIDDLE_LEFT);

		noInfoLabel.setValue(applicationContext.getMessage("adresse.noinfo", null, UI.getCurrent().getLocale()));
		addGenericComponent(noInfoLabel);

		/* L'adresse */
		table.setSizeFull();
		table.setVisibleColumns((Object[]) FIELDS_ORDER);
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
	 * Met a jour les composants adresse
	 */
	private void majComponentsAdresse(final Candidat candidat) {
		if (candidat == null) {
			setButtonVisible(false);
			table.setVisible(false);
			noInfoLabel.setVisible(false);
			return;
		} else {
			final Adresse adresse = candidat.getAdresse();
			if (adresse == null) {
				noInfoLabel.setVisible(true);
				table.setVisible(false);
				setGenericLayoutSizeFull(false);
			} else {
				noInfoLabel.setVisible(false);
				table.setVisible(true);
				final List<SimpleTablePresentation> liste = candidatController.getInformationsAdresse(adresse);
				container.removeAllItems();
				container.addAll(liste);
				table.setPageLength(liste.size());
				setGenericLayoutSizeFull(true);
			}
		}
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
		if (majView(applicationContext.getMessage("adresse.title", null, UI.getCurrent().getLocale()), true, ConstanteUtils.LOCK_ADRESSE)) {
			majComponentsAdresse(candidat);
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		candidatController.unlockCandidatRessource(cptMin, ConstanteUtils.LOCK_ADRESSE);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.AdresseListener#adresseModified(fr.univlorraine.ecandidat.entities.ecandidat.Candidat)
	 */
	@Override
	public void adresseModified(final Candidat candidat) {
		cptMin.setCandidat(candidat);
		majComponentsAdresse(candidat);
	}

}
