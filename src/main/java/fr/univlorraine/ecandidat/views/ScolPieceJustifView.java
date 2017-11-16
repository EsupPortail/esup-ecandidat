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

import fr.univlorraine.ecandidat.controllers.PieceJustifController;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.template.PieceJustifViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des pièces justifs par la scolarité
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolPieceJustifView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolPieceJustifView extends PieceJustifViewTemplate implements View, EntityPushListener<PieceJustif>{

	/** serialVersionUID **/
	private static final long serialVersionUID = -6506576902045900646L;

	public static final String NAME = "scolPieceJustifView";
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient PieceJustifController pieceJustifController;
	@Resource
	private transient EntityPusher<PieceJustif> pieceJustifEntityPusher;

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		super.init();
				
		titleParam.setValue(applicationContext.getMessage("pieceJustif.title", null, UI.getCurrent().getLocale()));
		
		btnNew.addClickListener(e -> {
			pieceJustifController.editNewPieceJustif(null);
		});
		
		container.addAll(pieceJustifController.getPieceJustifsByCtrCand(null));
		sortContainer();
		
		pieceJustifTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				pieceJustifTable.select(e.getItemId());
				btnEdit.click();
			}
		});
		
		/* Inscrit la vue aux mises à jour de pieceJustif */
		pieceJustifEntityPusher.registerEntityPushListener(this);
	}
	
	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		/* Désinscrit la vue des mises à jour de pieceJustif */
		pieceJustifEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(PieceJustif entity) {
		if (entity.getCentreCandidature() == null){
			pieceJustifTable.removeItem(entity);
			pieceJustifTable.addItem(entity);
			//pieceJustifTable.sort();
			sortContainer();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(PieceJustif entity) {
		if (entity.getCentreCandidature() == null){
			pieceJustifTable.removeItem(entity);
			pieceJustifTable.addItem(entity);
			//pieceJustifTable.sort();
			sortContainer();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(PieceJustif entity) {
		if (entity.getCentreCandidature() == null){
			pieceJustifTable.removeItem(entity);
		}
	}
}
