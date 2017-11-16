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
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.template.PieceJustifViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des pieceJustifs du centre de candidature
 * @author Kevin Hergalant
 *
 */
@SpringView(name = CtrCandPieceJustifView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandPieceJustifView extends PieceJustifViewTemplate implements View, EntityPushListener<PieceJustif>{	

	/** serialVersionUID **/
	private static final long serialVersionUID = -5905988211512631154L;

	public static final String NAME = "ctrCandPieceJustifView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient PieceJustifController pieceJustifController;
	@Resource
	private transient EntityPusher<PieceJustif> pieceJustifEntityPusher;
	@Resource
	private transient UserController userController;

	/*Droit sur la vue*/
	private SecurityCtrCandFonc securityCtrCandFonc;

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/*Récupération du centre de canidature en cours*/
		securityCtrCandFonc = userController.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_PJ);
		if (securityCtrCandFonc.hasNoRight()){
			return;
		}
		isReadOnly = securityCtrCandFonc.isReadOnly();
		//dematCtrCand = ctrCand.getTemDematCtrCand();
		super.init();		
		
		titleParam.setValue(applicationContext.getMessage("pieceJustif.ctrCand.title", new Object[]{securityCtrCandFonc.getCtrCand().getLibCtrCand()}, UI.getCurrent().getLocale()));
		
		btnNew.addClickListener(e -> {
			pieceJustifController.editNewPieceJustif(securityCtrCandFonc.getCtrCand());
		});
		
		container.addAll(pieceJustifController.getPieceJustifsByCtrCand(securityCtrCandFonc.getCtrCand().getIdCtrCand()));
		sortContainer();
		/*Gestion du readOnly*/
		if (!isReadOnly){
			pieceJustifTable.addItemClickListener(e -> {
				if (e.isDoubleClick()) {
					pieceJustifTable.select(e.getItemId());
					btnEdit.click();
				}
			});
			buttonsLayout.setVisible(true);
		}else{
			buttonsLayout.setVisible(false);
		}
		
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
		if (securityCtrCandFonc.getCtrCand()!=null && entity.getCentreCandidature()!=null && entity.getCentreCandidature().getIdCtrCand().equals(securityCtrCandFonc.getCtrCand().getIdCtrCand())){
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
		if (securityCtrCandFonc.getCtrCand()!=null && entity.getCentreCandidature()!=null && entity.getCentreCandidature().getIdCtrCand().equals(securityCtrCandFonc.getCtrCand().getIdCtrCand())){
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
		if (securityCtrCandFonc.getCtrCand()!=null && entity.getCentreCandidature()!=null && entity.getCentreCandidature().getIdCtrCand().equals(securityCtrCandFonc.getCtrCand().getIdCtrCand())){
			pieceJustifTable.removeItem(entity);
		}
	}
}
