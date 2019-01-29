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

import fr.univlorraine.ecandidat.controllers.TypeDecisionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Mail_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis_;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeDecision_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.template.TypeDecisionViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des type de decisions par la scolarité
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = ScolTypeDecisionView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolTypeDecisionView extends TypeDecisionViewTemplate implements View, EntityPushListener<TypeDecision> {

	public static final String NAME = "scolTypeDecisionView";

	public static final String[] FIELDS_ORDER = {TypeDecision_.codTypDec.getName(), TypeDecision_.libTypDec.getName(), TypeDecision_.typeAvis.getName() + "." + TypeAvis_.libelleTypAvis.getName(),
			TypeDecision_.mail.getName() + "." + Mail_.libMail.getName(), TypeDecision_.tesTypDec.getName(), TypeDecision_.temDeverseOpiTypDec.getName(), TypeDecision_.temDefinitifTypDec.getName()};

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient TypeDecisionController typeDecisionController;
	@Resource
	private transient EntityPusher<TypeDecision> typeDecisionEntityPusher;

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		/* Init à partir du template */
		super.init();

		/* Titre */
		titleParam.setValue(applicationContext.getMessage("typeDec.title", null, UI.getCurrent().getLocale()));

		/* Bouton New */
		btnNew.addClickListener(e -> {
			typeDecisionController.editNewTypeDecision(null);
		});

		/* Bouton edit */
		btnEdit.addClickListener(e -> {
			if (typeDecisionTable.getValue() instanceof TypeDecision) {
				typeDecisionController.editTypeDecision((TypeDecision) typeDecisionTable.getValue(), null);
			}
		});

		container.addAll(typeDecisionController.getTypeDecisionsByCtrCand(null));
		typeDecisionTable.sort();
		typeDecisionTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				typeDecisionTable.select(e.getItemId());
				btnEdit.click();
			}
		});

		/* Inscrit la vue aux mises à jour de typeDecision */
		typeDecisionEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de typeDecision */
		typeDecisionEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(final TypeDecision entity) {
		if (entity.getCentreCandidature() == null) {
			typeDecisionTable.removeItem(entity);
			typeDecisionTable.addItem(entity);
			typeDecisionTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(final TypeDecision entity) {
		if (entity.getCentreCandidature() == null) {
			typeDecisionTable.removeItem(entity);
			typeDecisionTable.addItem(entity);
			typeDecisionTable.sort();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(final TypeDecision entity) {
		if (entity.getCentreCandidature() == null) {
			typeDecisionTable.removeItem(entity);
		}
	}
}
