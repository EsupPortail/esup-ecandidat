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
package fr.univlorraine.ecandidat.views.template;

import jakarta.annotation.Resource;

import org.springframework.context.ApplicationContext;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd_;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil_;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu_;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;

public class UtilisateurViewTemplate extends VerticalLayout{

	/** serialVersionUID **/
	private static final long serialVersionUID = -9134270019307809812L;

	public static final String[] USER_PROFIL_FIELDS_ORDER = {DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName(),DroitProfilInd_.individu.getName()+"."+Individu_.libelleInd.getName(),DroitProfilInd_.droitProfil.getName()+"."+DroitProfil_.codProfil.getName()};

	/* Injections */
	@Resource
	protected transient ApplicationContext applicationContext;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient UserController userController;

	/* Composants */	
	private OneClickButton btnNouveauAdmin = new OneClickButton(FontAwesome.PLUS);
	//private OneClickButton btnNouveauAdminMasse = new OneClickButton(FontAwesome.USERS);
	private OneClickButton btnSupprimerAdmin = new OneClickButton(FontAwesome.TRASH_O);
	private BeanItemContainer<DroitProfilInd> containerAdmin = new BeanItemContainer<DroitProfilInd>(DroitProfilInd.class);
	private TableFormating adminTable = new TableFormating(null,containerAdmin);
	private Boolean adminMode = false;
	private HorizontalLayout hlComplement = new HorizontalLayout();
	
	/**
	 * Initialise la vue
	 */
	public void init(Boolean adminMode) {
		this.adminMode = adminMode;
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);	
		
		String titleView = applicationContext.getMessage("scolGestCandidatDroitProfilView.title", null, UI.getCurrent().getLocale());
		String labelBtnNew = applicationContext.getMessage("droitprofilind.btnNouveauGestCand", null, UI.getCurrent().getLocale());
		if (adminMode){
			titleView = applicationContext.getMessage("adminDroitProfilIndView.title", null, UI.getCurrent().getLocale());
			labelBtnNew =  applicationContext.getMessage("droitprofilind.btnNouveau", null, UI.getCurrent().getLocale());
		}
		
		Label title = new Label(titleView);
		title.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(title);
		
		hlComplement.setWidth(100, Unit.PERCENTAGE);
		hlComplement.setSpacing(true);
		hlComplement.setVisible(false);
		addComponent(hlComplement);

		/* Boutons */
		HorizontalLayout buttonsLayoutAdmin = new HorizontalLayout();
		buttonsLayoutAdmin.setWidth(100, Unit.PERCENTAGE);
		buttonsLayoutAdmin.setSpacing(true);
		addComponent(buttonsLayoutAdmin);
		
		btnNouveauAdmin.setCaption(labelBtnNew);
		btnNouveauAdmin.addClickListener(e -> droitProfilController.addProfilToUser(adminMode));
		buttonsLayoutAdmin.addComponent(btnNouveauAdmin);
		buttonsLayoutAdmin.setComponentAlignment(btnNouveauAdmin, Alignment.MIDDLE_LEFT);
		
		/*if (!adminMode){
			btnNouveauAdminMasse.setCaption(applicationContext.getMessage("droitprofilind.btnNouveauGestCandMasse", null, UI.getCurrent().getLocale()));
			btnNouveauAdminMasse.addClickListener(e -> droitProfilController.addProfilToUserEnMasse());
			buttonsLayoutAdmin.addComponent(btnNouveauAdminMasse);
			buttonsLayoutAdmin.setComponentAlignment(btnNouveauAdminMasse, Alignment.MIDDLE_CENTER);
		}*/
		

		btnSupprimerAdmin.setCaption(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()));
		btnSupprimerAdmin.setEnabled(false);
		btnSupprimerAdmin.addClickListener(e -> {
			if (adminTable.getValue() instanceof DroitProfilInd) {
				droitProfilController.deleteProfilToUser((DroitProfilInd) adminTable.getValue());
			}
		});
		buttonsLayoutAdmin.addComponent(btnSupprimerAdmin);
		buttonsLayoutAdmin.setComponentAlignment(btnSupprimerAdmin, Alignment.MIDDLE_RIGHT);

		/* Table des batchs */		
		containerAdmin.addNestedContainerProperty(DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName());
		containerAdmin.addNestedContainerProperty(DroitProfilInd_.individu.getName()+"."+Individu_.libelleInd.getName());
		containerAdmin.addNestedContainerProperty(DroitProfilInd_.droitProfil.getName()+"."+DroitProfil_.codProfil.getName());
		adminTable.setSizeFull();
		adminTable.setVisibleColumns((Object[]) USER_PROFIL_FIELDS_ORDER);
		for (String fieldName : USER_PROFIL_FIELDS_ORDER) {
			adminTable.setColumnHeader(fieldName, applicationContext.getMessage("droitprofilind.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		adminTable.setSortContainerPropertyId(DroitProfilInd_.individu.getName()+"."+Individu_.loginInd.getName());
		adminTable.setColumnCollapsingAllowed(true);
		adminTable.setColumnReorderingAllowed(true);
		adminTable.setSelectable(true);
		adminTable.setImmediate(true);
		adminTable.addItemSetChangeListener(e -> adminTable.sanitizeSelection());
		
		adminTable.addValueChangeListener(e -> {
			/* Les boutons d'édition, de programme et de lancement de batch sont actifs seulement si un droit est sélectionné. */
			boolean droitIsSelected = false;
			if (!(adminTable.getValue() instanceof DroitProfilInd)){
				droitIsSelected = false;
			}else if (((DroitProfilInd)adminTable.getValue()).getDroitProfil().getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH)){
				droitIsSelected = false;
			}else{
				droitIsSelected = true;
			}
			btnSupprimerAdmin.setEnabled(droitIsSelected);
		});
		addComponent(adminTable);
		setExpandRatio(adminTable, 1);
	}
	
	protected void addComplement(Component c){
		hlComplement.addComponent(c);
		hlComplement.setVisible(true);
	}
	
	
	/** Met à jour le container grace a la commission
	 */
	protected void majContainer(){	
		containerAdmin.removeAllItems();
		containerAdmin.addAll(droitProfilController.getDroitProfilInds(adminMode));
	}
	
	
	/** Supprime une entité de la table
	 * @param entity
	 */
	public void removeEntity(DroitProfilInd entity) {
		if ((adminMode && entity.getDroitProfil().isDroitProfilAdmin()) || (!adminMode && !entity.getDroitProfil().isDroitProfilAdmin())){
			adminTable.removeItem(entity);
		}		
	}
	
	/** Persisite une entité de la table
	 * @param entity
	 */
	public void addEntity(DroitProfilInd entity) {
		if ((adminMode && entity.getDroitProfil().isDroitProfilAdmin()) || (!adminMode && !entity.getDroitProfil().isDroitProfilAdmin())){
			adminTable.addItem(entity);
			adminTable.sort();
		}		
	}
}
