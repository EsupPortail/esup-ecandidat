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

import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.template.ParametreViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des parametres
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = ScolParametreView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolParametreView extends ParametreViewTemplate implements View, EntityPushListener<Parametre> {

	public static final String NAME = "scolParametreView";
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient EntityPusher<Parametre> parametreEntityPusher;

	/** Initialise la vue */
	@Override
	@PostConstruct
	public void init() {
		super.init();

		container.addAll(parametreController.getScolParametres());
		parametreTable.sort();
		/* Inscrit la vue aux mises à jour de langue */
		parametreEntityPusher.registerEntityPushListener(this);
	}

	/** @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent) */
	@Override
	public void enter(final ViewChangeEvent event) {
	}

	/** @see com.vaadin.ui.AbstractComponent#detach() */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de langue */
		parametreEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/** @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object) */
	@Override
	public void entityPersisted(final Parametre entity) {
		if (entity.getTemAffiche() && entity.getTemScol()) {
			parametreTable.removeItem(entity);
			parametreTable.addItem(entity);
			parametreTable.sort();
		}
	}

	/** @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object) */
	@Override
	public void entityUpdated(final Parametre entity) {
		if (entity.getTemAffiche() && entity.getTemScol()) {
			parametreTable.removeItem(entity);
			parametreTable.addItem(entity);
			parametreTable.sort();
		}
	}

	/** @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object) */
	@Override
	public void entityDeleted(final Parametre entity) {

	}

}
