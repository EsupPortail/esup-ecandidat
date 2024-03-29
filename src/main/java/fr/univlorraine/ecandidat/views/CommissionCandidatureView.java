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

import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.EntityPushListenerCandidature;
import fr.univlorraine.ecandidat.utils.EntityPusherCandidature;
import fr.univlorraine.ecandidat.views.template.CandidatureViewTemplate;

/**
 * Page de gestion des candidatures pour la commission
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = CommissionCandidatureView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_COMMISSION)
public class CommissionCandidatureView extends CandidatureViewTemplate implements View, EntityPushListenerCandidature {

	public static final String NAME = "commissionCandidatureView";

	/* Injections */
	@Resource
	private transient EntityPusherCandidature candidatureEntityPusher;

	@Resource
	private transient CacheController cacheController;

	/* Le liste de type de statut dont l'affichage est restreint --> on n'affiche pas ces candidatures */
	private List<TypeStatut> listeCodTypeStatut = null;

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		super.init(true, ConstanteUtils.TYP_GESTION_CANDIDATURE_COMMISSION, false, false);
		setTitle(null);
		listeCodTypeStatut = cacheController.getListeCodTypeStatutVisibleToCommission();

		/* Inscrit la vue aux mises à jour de candidature */
		candidatureEntityPusher.registerEntityPushListener(this);
	}

	@Override
	protected List<Candidature> getListeCandidature(final Commission commission) {
		return candidatureCtrCandController.getCandidatureByCommission(commission, listeCodTypeStatut);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
		majContainer();
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detachView();
		/* Désinscrit la vue des mises à jour de candidature */
		candidatureEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(final Candidature entity) {
		removeEntity(entity);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(final Candidature entity) {
		if (entity.getDatAnnulCand() != null) {
			return;
		}
		removeEntity(entity);
		if (!listeCodTypeStatut.contains(entity.getTypeStatut())) {
			return;
		}
		addEntity(entity);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(final Candidature entity) {
		if (!isEntityApartientCommission(entity)) {
			return;
		}
		removeEntity(entity);
		if (entity.getDatAnnulCand() != null) {
			return;
		}
		if (!listeCodTypeStatut.contains(entity.getTypeStatut())) {
			return;
		}
		entity.setLastTypeDecision(candidatureController.getLastTypeDecisionCandidature(entity));
		addEntity(entity);
	}

	@Override
	public Integer getIdCommission() {
		return getCommission().getIdComm();
	}

}
