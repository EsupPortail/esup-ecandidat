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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre_;
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
@SpringView(name = AdminParametreView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_ADMIN)
public class AdminParametreView extends ParametreViewTemplate implements View, EntityPushListener<Parametre> {

	public static final String[] FIELDS_ORDER = {Parametre_.codParam.getName(),
			Parametre_.libParam.getName(), Parametre_.valParam.getName(),
			Parametre_.typParam.getName(), Parametre_.temScol.getName()};

	public static final String NAME = "adminParametreView";
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
		parametreTable.addBooleanColumn(Parametre_.temScol.getName());
		parametreTable.setColumnWidth(Parametre_.temScol.getName(), 100);
		checkShowScolParam.setVisible(true);
		checkShowScolParam.addValueChangeListener(e -> {
			loadParams();
		});
		checkShowScolParam.setValue(true);

		/* Inscrit la vue aux mises à jour de langue */
		parametreEntityPusher.registerEntityPushListener(this);
	}

	@Override
	public String[] getFieldsOrder() {
		return new String[] {Parametre_.codParam.getName(),
				Parametre_.libParam.getName(), Parametre_.valParam.getName(),
				Parametre_.typParam.getName(), Parametre_.temScol.getName()};
	}

	/**
	 * Change le mode --> Affichage de scol ou non
	 */
	private void loadParams() {
		Boolean showScol = checkShowScolParam.getValue();
		List<Parametre> liste = parametreController.getParametres(showScol);
		container.removeAllItems();
		container.addAll(liste);
		parametreTable.sort();
	}

	@Override
	protected Boolean isAdmin() {
		return true;
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
		parametreTable.removeItem(entity);
		if (entity.getTemAffiche() && (!entity.getTemScol() || (entity.getTemScol() && checkShowScolParam.getValue()))) {
			parametreTable.addItem(entity);
			parametreTable.sort();
		}
	}

	/** @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object) */
	@Override
	public void entityUpdated(final Parametre entity) {
		parametreTable.removeItem(entity);
		if (entity.getTemAffiche() && (!entity.getTemScol() || (entity.getTemScol() && checkShowScolParam.getValue()))) {
			parametreTable.addItem(entity);
			parametreTable.sort();
		}
	}

	/** @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object) */
	@Override
	public void entityDeleted(final Parametre entity) {

	}

}
