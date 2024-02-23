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

import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatParcoursController;
import fr.univlorraine.ecandidat.controllers.ConfigController;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusInterne;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusInterne_;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;

/**
 * Page de gestion des cursus univ du candidat
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CandidatCursusInterneView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatCursusInterneView extends CandidatViewTemplate implements View {

	public static final String NAME = "candidatCursusInterneView";

	public static final String[] FIELDS_ORDER_INTERNE = {
		CandidatCursusInterne_.anneeUnivCursusInterne.getName(),
		CandidatCursusInterne_.codVetCursusInterne.getName(),
		CandidatCursusInterne_.libCursusInterne.getName(),
		CandidatCursusInterne_.siScolTypResultat.getName() + "." + SiScolTypResultat_.libTre.getName(),
		CandidatCursusInterne_.siScolMention.getName() + "." + SiScolMention_.libMen.getName(),
		CandidatCursusInterne_.notVetCursusInterne.getName(),
		CandidatCursusInterne_.barNotVetCursusInterne.getName()
	};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;
	@Resource
	private transient ConfigController configController;

	/* Composants */
	private final BeanItemContainer<CandidatCursusInterne> cursusInterneContainer = new BeanItemContainer<>(CandidatCursusInterne.class);
	private final TableFormating cursusInterneTable = new TableFormating(null, cursusInterneContainer);

	/** Initialise la vue */
	@Override
	@PostConstruct
	public void init() {
		super.init();
		setNavigationButton(NAME);

		setSubtitle(applicationContext.getMessage("cursusinterne.indication",
			new Object[] { configController.getConfigEtab(UI.getCurrent().getLocale()).getNom() },
			UI.getCurrent().getLocale()));

		cursusInterneContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolMention.getName() + "." + SiScolMention_.libMen.getName());
		cursusInterneContainer.addNestedContainerProperty(CandidatCursusInterne_.siScolTypResultat.getName() + "." + SiScolTypResultat_.libTre.getName());

		cursusInterneTable.setSizeFull();
		cursusInterneTable.setVisibleColumns((Object[]) FIELDS_ORDER_INTERNE);
		for (final String fieldName : FIELDS_ORDER_INTERNE) {
			cursusInterneTable.setColumnHeader(fieldName, applicationContext.getMessage("cursusinterne." + fieldName, null, UI.getCurrent().getLocale()));
		}
		cursusInterneTable.setColumnCollapsingAllowed(true);
		cursusInterneTable.setColumnReorderingAllowed(true);
		cursusInterneTable.setSelectable(false);
		cursusInterneTable.setImmediate(true);

		setButtonVisible(false);

		addGenericComponent(cursusInterneTable);
		setGenericExpandRatio(cursusInterneTable);
	}

	@Override
	public void enter(final ViewChangeEvent event) {
		if (majView(applicationContext.getMessage("cursusinterne.title", null, UI.getCurrent().getLocale()), true, null)) {
			cursusInterneContainer.removeAllItems();
			final List<CandidatCursusInterne> listeCursusInt = candidat.getCandidatCursusInternes();
			listeCursusInt.sort(Comparator.comparing(CandidatCursusInterne::getAnneeUnivCursusInterne));
			cursusInterneContainer.addAll(listeCursusInt);
			cursusInterneTable.setPageLength(listeCursusInt.size());
		}
	}
}
