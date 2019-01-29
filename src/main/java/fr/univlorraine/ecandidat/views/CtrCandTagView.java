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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.CentreCandidatureController;
import fr.univlorraine.ecandidat.controllers.TagController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.template.TagViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des motivation d'avis par la scolarité
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CtrCandTagView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandTagView extends TagViewTemplate implements View, EntityPushListener<Tag> {

	public static final String NAME = "ctrCandTagView";

	/* Injections */
	@Resource
	private transient UserController userController;
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TagController tagController;
	@Resource
	private transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient EntityPusher<Tag> tagEntityPusher;

	/* Droit sur la vue */
	private SecurityCtrCandFonc securityCtrCandFonc;

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		/* Récupération du centre de canidature en cours */
		securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_PARAM_CC);
		if (securityCtrCandFonc.hasNoRight()) {
			return;
		}
		super.init();

		/* Titre */
		title.setValue(applicationContext.getMessage("tag.ctrCand.title", new Object[] {securityCtrCandFonc.getCtrCand().getLibCtrCand()}, UI.getCurrent().getLocale()));

		/* Bouton new */
		btnNew.addClickListener(e -> {
			tagController.editNewTag(securityCtrCandFonc.getCtrCand());
		});

		container.addAll(tagController.getTagsByCtrCand(securityCtrCandFonc.getCtrCand().getIdCtrCand()));
		tagTable.sort();
		/* Gestion du readOnly */
		if (centreCandidatureController.getIsCtrCandParamCC(securityCtrCandFonc.getCtrCand().getIdCtrCand()) && securityCtrCandFonc.isWrite()) {
			tagTable.addItemClickListener(e -> {
				if (e.isDoubleClick()) {
					tagTable.select(e.getItemId());
					btnEdit.click();
				}
			});
			buttonsLayout.setVisible(true);
		} else {
			buttonsLayout.setVisible(false);
		}

		/* Inscrit la vue aux mises à jour de formulaire */
		tagEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de tag */
		tagEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @param entity
	 * @return true si l'entité doit etre updaté dans cette table car elle provient du ctrCand
	 */
	private Boolean isEntityFromCtrCand(final Tag entity) {
		return securityCtrCandFonc.getCtrCand() != null && entity.getCentreCandidature() != null
				&& entity.getCentreCandidature().getIdCtrCand().equals(securityCtrCandFonc.getCtrCand().getIdCtrCand());
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(final Tag entity) {
		if (isEntityFromCtrCand(entity)) {
			tagTable.removeItem(entity);
			tagTable.addItem(entity);
			tagTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(final Tag entity) {
		if (isEntityFromCtrCand(entity)) {
			tagTable.removeItem(entity);
			tagTable.addItem(entity);
			tagTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(final Tag entity) {
		if (isEntityFromCtrCand(entity)) {
			tagTable.removeItem(entity);
		}
	}
}
