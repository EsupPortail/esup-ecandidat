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

import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnGenerator;
import com.vaadin.v7.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.ListenerUtils.GestionnaireCandidatListener;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.ecandidat.views.template.UtilisateurViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion d'affectation des droitProfil
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolGestCandidatDroitProfilView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolGestCandidatDroitProfilView extends UtilisateurViewTemplate implements GestionnaireCandidatListener, View, EntityPushListener<DroitProfilInd>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 8799625552855771397L;

	public static final String NAME = "scolGestCandidatDroitProfilView";
	
	@Resource
	private transient ParametreController parametreController;
	
	public static final String[] FIELDS_ORDER_PARAM = {SimpleTablePresentation.CHAMPS_TITLE,SimpleTablePresentation.CHAMPS_VALUE,SimpleTablePresentation.CHAMPS_ACTION};
	
	@Resource
	private transient EntityPusher<DroitProfilInd> droitProfilIndEntityPusher;
	
	BeanItemContainer<SimpleTablePresentation> containerReadOnly = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
	

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {		
		super.init(false);
		
		/*Ajout des parametres globaux*/
		VerticalLayout vlComplement = new VerticalLayout();
		vlComplement.setSpacing(true);
		vlComplement.setWidth(100, Unit.PERCENTAGE);
		
		Label titleParamDesc = new Label(applicationContext.getMessage("droitprofilind.gestCandidat.param", null, UI.getCurrent().getLocale()));
		titleParamDesc.addStyleName(StyleConstants.VIEW_SUBTITLE);
		vlComplement.addComponent(titleParamDesc);
				
		TableFormating table = new TableFormating(null, containerReadOnly);
		GestionnaireCandidatListener listener = this;
		table.addGeneratedColumn(SimpleTablePresentation.CHAMPS_VALUE, new ColumnGenerator() {
			
			/**serialVersionUID**/
			private static final long serialVersionUID = 4125366493925127117L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final SimpleTablePresentation bean = (SimpleTablePresentation)itemId;
				return parametreController.getLibelleParametresGestionCandidat(bean.getValue().toString());
			}
		});
		table.addGeneratedColumn(SimpleTablePresentation.CHAMPS_ACTION, new ColumnGenerator() {
			
			/**serialVersionUID**/
			private static final long serialVersionUID = 5720531607995628916L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final SimpleTablePresentation bean = (SimpleTablePresentation)itemId;
				OneClickButton button = new OneClickButton(applicationContext.getMessage("btnModifier", null, UI.getCurrent().getLocale()), FontAwesome.REFRESH);
				button.addClickListener(e->{
					parametreController.changeParametreGestionCandidat(listener, bean.getCode(), bean.getValue().toString(), bean.getTitle());
				});
				return button;
			}
		});
				
		table.setVisibleColumns((Object[]) FIELDS_ORDER_PARAM);
		table.setColumnCollapsingAllowed(false);
		table.setColumnReorderingAllowed(false);
		table.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
		table.setSelectable(false);
		table.setImmediate(true);
		table.setPageLength(2);
		table.setWidth(100, Unit.PERCENTAGE);
		table.setColumnWidth(SimpleTablePresentation.CHAMPS_TITLE, 500);
		table.setColumnWidth(SimpleTablePresentation.CHAMPS_VALUE, 460);
		table.setCellStyleGenerator((components, itemId, columnId)->{
			if (columnId!=null && columnId.equals(SimpleTablePresentation.CHAMPS_TITLE)){
				return (ValoTheme.LABEL_BOLD);
			}
			return null;
		});
		
		vlComplement.addComponent(table);
		
		Label titleParamInd = new Label(applicationContext.getMessage("droitprofilind.gestCandidat.ind", null, UI.getCurrent().getLocale()));
		titleParamInd.addStyleName(StyleConstants.VIEW_SUBTITLE);
		vlComplement.addComponent(titleParamInd);
		
		addComplement(vlComplement);
		
		/* Inscrit la vue aux mises à jour de droitProfil */
		droitProfilIndEntityPusher.registerEntityPushListener(this);
	}
	
	/**
	 * Met a jour les parametres globaux
	 */
	private void majParamGlobaux(){
		containerReadOnly.removeAllItems();
		containerReadOnly.addAll(parametreController.getParametresGestionCandidat());
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		majContainer();
		majParamGlobaux();
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
		/* Desinscrit la vue aux mises à jour de droitProfil */
		droitProfilIndEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(DroitProfilInd entity) {
		removeEntity(entity);
		addEntity(entity);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(DroitProfilInd entity) {
		removeEntity(entity);
		addEntity(entity);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(DroitProfilInd entity) {
		removeEntity(entity);
	}

	@Override
	public void changeModeGestionnaireCandidat() {
		majParamGlobaux();
	}
}
