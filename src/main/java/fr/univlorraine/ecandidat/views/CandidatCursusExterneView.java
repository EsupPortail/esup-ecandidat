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
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CandidatController;
import fr.univlorraine.ecandidat.controllers.CandidatParcoursController;
import fr.univlorraine.ecandidat.controllers.ConfigController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatCursusPostBac_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatCursusExterneListener;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.CandidatViewTemplate;

/**
 * Page de gestion des cursus externes du candidat
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CandidatCursusExterneView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CANDIDAT)
public class CandidatCursusExterneView extends CandidatViewTemplate implements View, CandidatCursusExterneListener {

	public static final String NAME = "candidatCursusExterneView";

	public static final String[] FIELDS_ORDER_POST_BAC = {
		CandidatCursusPostBac_.anneeUnivCursus.getName(),
		CandidatCursusPostBac_.siScolPays.getName() + "." + SiScolPays_.libPay.getName(),
		CandidatCursusPostBac_.siScolDepartement.getName() + "." + SiScolDepartement_.libDep.getName(),
		CandidatCursusPostBac_.siScolCommune.getName() + "." + SiScolCommune_.libCom.getName(),
		CandidatCursusPostBac_.siScolEtablissement.getName() + "." + SiScolEtablissement_.libEtb.getName(),
		CandidatCursusPostBac_.siScolDipAutCur.getName() + "." + SiScolDipAutCur_.libDac.getName(),
		CandidatCursusPostBac_.libCursus.getName(),
		CandidatCursusPostBac_.obtenuCursus.getName(),
		CandidatCursusPostBac_.siScolMention.getName() + "." + SiScolMention_.libMen.getName()
	};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient ConfigController configController;
	@Resource
	private transient CandidatParcoursController candidatParcoursController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient ParametreController parametreController;

	/* Composants */
	private final BeanItemContainer<CandidatCursusPostBac> postBacContainer = new BeanItemContainer<CandidatCursusPostBac>(CandidatCursusPostBac.class);
	private final TableFormating postBacTable = new TableFormating(null, postBacContainer);

	/* Composants */

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		super.init();
		setNavigationButton(NAME);

		String indication;
		if (parametreController.getIsGetCursusInterne()) {
			indication = applicationContext.getMessage("cursusexterne.indication", new Object[] { applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale()) }, UI.getCurrent().getLocale());
		} else {
			indication = applicationContext.getMessage("cursusexterne.indication.withoutCursusInterne", new Object[] { applicationContext.getMessage("universite.title", null, UI.getCurrent().getLocale()) },
				UI.getCurrent().getLocale());
		}

		/* Indications pour le cursus */
		setSubtitle(indication);

		final OneClickButton btnNewPostBac = new OneClickButton(applicationContext.getMessage("cursusexterne.btn.new", null, UI.getCurrent().getLocale()), FontAwesome.PLUS);
		btnNewPostBac.addClickListener(e -> {
			candidatParcoursController.editCursusPostBac(candidat, null, this);
		});
		addGenericButton(btnNewPostBac, Alignment.MIDDLE_LEFT);

		final OneClickButton btnEditPostBac = new OneClickButton(applicationContext.getMessage("btnModifier", null, UI.getCurrent().getLocale()), FontAwesome.PENCIL);
		btnEditPostBac.setEnabled(false);
		btnEditPostBac.addClickListener(e -> {
			if (postBacTable.getValue() instanceof CandidatCursusPostBac) {
				candidatParcoursController.editCursusPostBac(candidat, (CandidatCursusPostBac) postBacTable.getValue(), this);
			}
		});
		addGenericButton(btnEditPostBac, Alignment.MIDDLE_CENTER);

		final OneClickButton btnDeletePostBac = new OneClickButton(FontAwesome.TRASH_O);
		btnDeletePostBac.setEnabled(false);
		btnDeletePostBac.setCaption(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()));
		btnDeletePostBac.addClickListener(e -> {
			if (postBacTable.getValue() instanceof CandidatCursusPostBac) {
				candidatParcoursController.deleteCursusPostBac(candidat, (CandidatCursusPostBac) postBacTable.getValue(), this);
			}
		});
		addGenericButton(btnDeletePostBac, Alignment.MIDDLE_RIGHT);

		/* Table post Bac */
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolMention.getName() + "." + SiScolMention_.libMen.getName());
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolEtablissement.getName() + "." + SiScolEtablissement_.libEtb.getName());
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolPays.getName() + "." + SiScolPays_.libPay.getName());
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolDepartement.getName() + "." + SiScolDepartement_.libDep.getName());
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolCommune.getName() + "." + SiScolCommune_.libCom.getName());
		postBacContainer.addNestedContainerProperty(CandidatCursusPostBac_.siScolDipAutCur.getName() + "." + SiScolDipAutCur_.libDac.getName());

		postBacTable.setSizeFull();
		postBacTable.setVisibleColumns((Object[]) FIELDS_ORDER_POST_BAC);
		for (final String fieldName : FIELDS_ORDER_POST_BAC) {
			postBacTable.setColumnHeader(fieldName, applicationContext.getMessage("cursusexterne." + fieldName, null, UI.getCurrent().getLocale()));
		}
		postBacTable.addGeneratedColumn(CandidatCursusPostBac_.obtenuCursus.getName(), new ColumnGenerator() {

			/** serialVersionUID **/
			private static final long serialVersionUID = -6382571666110400875L;

			@Override
			public Object generateCell(final Table source, final Object itemId,
				final Object columnId) {
				final CandidatCursusPostBac post = (CandidatCursusPostBac) itemId;
				return tableRefController.getLibelleObtenuCursusByCode(post.getObtenuCursus());
			}
		});
		postBacTable.setSortContainerPropertyId(CandidatCursusPostBac_.anneeUnivCursus.getName());
		postBacTable.setColumnCollapsingAllowed(true);
		postBacTable.setColumnReorderingAllowed(true);
		postBacTable.setSelectable(true);
		postBacTable.setImmediate(true);
		postBacTable.addItemSetChangeListener(e -> postBacTable.sanitizeSelection());
		postBacTable.addValueChangeListener(e -> {
			/* Les boutons d'édition et de suppression de CandidatCursusPostBac sont actifs seulement si une CandidatCursusPostBac est sélectionnée. */
			final boolean postBacIsSelected = postBacTable.getValue() instanceof CandidatCursusPostBac;
			btnEditPostBac.setEnabled(postBacIsSelected);
			btnDeletePostBac.setEnabled(postBacIsSelected);
		});
		postBacTable.addItemClickListener(e -> {
			if (e.isDoubleClick() && !isLectureSeule && !isArchive) {
				postBacTable.select(e.getItemId());
				btnEditPostBac.click();
			}
		});
		addGenericComponent(postBacTable);
		setGenericExpandRatio(postBacTable);
	}

	/**
	 * Met a jour les composants
	 */
	private void majComponentsPostBac(final List<CandidatCursusPostBac> listCursus) {
		postBacContainer.removeAllItems();
		postBacContainer.addAll(listCursus);
		postBacTable.sort();
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
		String title;
		if (parametreController.getIsGetCursusInterne()) {
			title = applicationContext.getMessage("cursusexterne.title", null, UI.getCurrent().getLocale());
		} else {
			title = applicationContext.getMessage("cursusexterne.title.withoutCursusInterne", null, UI.getCurrent().getLocale());
		}
		if (majView(title, true, ConstanteUtils.LOCK_CURSUS_EXTERNE)) {
			majComponentsPostBac(candidat.getCandidatCursusPostBacs());
		}
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		candidatController.unlockCandidatRessource(cptMin, ConstanteUtils.LOCK_CURSUS_EXTERNE);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.ecandidat.utils.ListenerUtils.CandidatCursusExterneListener#cursusModified(java.util.List)
	 */
	@Override
	public void cursusModified(final List<CandidatCursusPostBac> list) {
		candidat.setCandidatCursusPostBacs(list);
		majComponentsPostBac(candidat.getCandidatCursusPostBacs());
	}
}
