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

import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.controllers.TagController;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag;
import fr.univlorraine.ecandidat.entities.ecandidat.Tag_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.template.TagViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des alertes SVA par la scolarité
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = ScolTagView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolTagView extends TagViewTemplate implements View, EntityPushListener<Tag> {

	public static final String NAME = "scolTagView";

	public static final String[] FIELDS_ORDER = {Tag_.libTag.getName(), Tag_.colorTag.getName(), Tag_.tesTag.getName()};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TagController tagController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient EntityPusher<Tag> tagEntityPusher;

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		/* Init à partir du template */
		super.init();

		/* Titre */
		title.setValue(applicationContext.getMessage("tag.title", null, UI.getCurrent().getLocale()));

		/* Bouton new */
		btnNew.addClickListener(e -> {
			tagController.editNewTag(null);
		});

		/* Alimentation des données */
		container.addAll(tagController.getTagsByCtrCand(null));
		tagTable.sort();
		/* Click sur item de la table */
		tagTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				tagTable.select(e.getItemId());
				btnEdit.click();
			}
		});

		/* Inscrit la vue aux mises à jour des items */
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
		/* Désinscrit la vue des mises à jour de alerteSva */
		tagEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(final Tag entity) {
		if (entity.getCentreCandidature() == null) {
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
		if (entity.getCentreCandidature() == null) {
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
		if (entity.getCentreCandidature() == null) {
			tagTable.removeItem(entity);
		}
	}
}
