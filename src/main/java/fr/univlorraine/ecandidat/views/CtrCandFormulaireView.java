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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.FormulaireController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.template.FormulaireViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des formulaires du centre de candidature
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CtrCandFormulaireView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandFormulaireView extends FormulaireViewTemplate implements View, EntityPushListener<Formulaire> {

	public static final String NAME = "ctrCandFormulaireView";

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormulaireController formulaireController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient EntityPusher<Formulaire> formulaireEntityPusher;

	/* Droit sur la vue */
	private SecurityCtrCandFonc securityCtrCandFonc;

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		/* Récupération du centre de candidature en cours */
		securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_FORMULAIRE);
		if (securityCtrCandFonc.hasNoRight()) {
			return;
		}
		super.init();

		titleParam.setValue(applicationContext.getMessage("formulaire.ctrCand.title", new Object[] {securityCtrCandFonc.getCtrCand().getLibCtrCand()}, UI.getCurrent().getLocale()));

		btnNew.addClickListener(e -> {
			formulaireController.editNewFormulaire(securityCtrCandFonc.getCtrCand());
		});

		container.addAll(formulaireController.getFormulairesByCtrCand(securityCtrCandFonc.getCtrCand().getIdCtrCand()));
		formulaireTable.sort();
		/* Gestion du readOnly */
		if (securityCtrCandFonc.isWrite()) {
			formulaireTable.addItemClickListener(e -> {
				if (e.isDoubleClick()) {
					formulaireTable.select(e.getItemId());
					btnEdit.click();
				}
			});
			buttonsLayout.setVisible(true);
		} else {
			buttonsLayout.setVisible(false);
		}

		/* Inscrit la vue aux mises à jour de formulaire */
		formulaireEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de formulaire */
		formulaireEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(final Formulaire entity) {
		if (securityCtrCandFonc.getCtrCand() != null && entity.getCentreCandidature() != null && entity.getCentreCandidature().getIdCtrCand().equals(securityCtrCandFonc.getCtrCand().getIdCtrCand())) {
			formulaireTable.removeItem(entity);
			formulaireTable.addItem(entity);
			formulaireTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(final Formulaire entity) {
		if (securityCtrCandFonc.getCtrCand() != null && entity.getCentreCandidature() != null && entity.getCentreCandidature().getIdCtrCand().equals(securityCtrCandFonc.getCtrCand().getIdCtrCand())) {
			formulaireTable.removeItem(entity);
			formulaireTable.addItem(entity);
			formulaireTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(final Formulaire entity) {
		if (securityCtrCandFonc.getCtrCand() != null && entity.getCentreCandidature() != null && entity.getCentreCandidature().getIdCtrCand().equals(securityCtrCandFonc.getCtrCand().getIdCtrCand())) {
			formulaireTable.removeItem(entity);
		}
	}
}
