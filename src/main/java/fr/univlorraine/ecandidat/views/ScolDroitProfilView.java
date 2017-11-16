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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.DroitProfilController;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.TableFormating;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des droitProfil
 * @author Kevin Hergalant
 *
 */
@SpringView(name = ScolDroitProfilView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolDroitProfilView extends VerticalLayout implements View, EntityPushListener<DroitProfil>{

	/** serialVersionUID **/
	private static final long serialVersionUID = 5323335563718769860L;

	public static final String NAME = "scolDroitProfilView";

	public static final String[] DROIT_PROFIL_FIELDS_ORDER = {DroitProfil_.codProfil.getName(),DroitProfil_.libProfil.getName(),"fonctionnalite"};
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient DroitProfilController droitProfilController;

	/* Composants */
	private OneClickButton btnNouveauProfil = new OneClickButton(FontAwesome.PLUS);
	private OneClickButton btnEditProfil = new OneClickButton(FontAwesome.PENCIL);
	private OneClickButton btnSupprimerProfil = new OneClickButton(FontAwesome.TRASH_O);
	private BeanItemContainer<DroitProfil> containerProfil = new BeanItemContainer<DroitProfil>(DroitProfil.class);
	private TableFormating droitProfilTable = new TableFormating(null,containerProfil);
	
	@Resource
	private transient EntityPusher<DroitProfil> droitProfilEntityPusher;
	

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		/* Style */
		setSizeFull();
		setMargin(true);
		setSpacing(true);
		
		/* Titre */
		Label title = new Label(applicationContext.getMessage("droitprofil.title", null, UI.getCurrent().getLocale()));
		title.addStyleName(StyleConstants.VIEW_TITLE);
		addComponent(title);
		
		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		addComponent(buttonsLayout);

		HorizontalLayout leftButtonsLayout = new HorizontalLayout();
		leftButtonsLayout.setSpacing(true);
		buttonsLayout.addComponent(leftButtonsLayout);
		buttonsLayout.setComponentAlignment(leftButtonsLayout, Alignment.MIDDLE_LEFT);


		btnNouveauProfil.setCaption(applicationContext.getMessage("droitprofil.btnNouveau", null, UI.getCurrent().getLocale()));
		btnNouveauProfil.addClickListener(e -> droitProfilController.editNewDroitProfil());
		leftButtonsLayout.addComponent(btnNouveauProfil);

		btnEditProfil.setCaption(applicationContext.getMessage("btnEdit", null, UI.getCurrent().getLocale()));
		btnEditProfil.setEnabled(false);
		btnEditProfil.addClickListener(e -> {
			if (droitProfilTable.getValue() instanceof DroitProfil) {
				droitProfilController.editDroitProfil((DroitProfil) droitProfilTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnEditProfil);
		buttonsLayout.setComponentAlignment(btnEditProfil, Alignment.MIDDLE_CENTER);

		btnSupprimerProfil.setCaption(applicationContext.getMessage("btnDelete", null, UI.getCurrent().getLocale()));
		btnSupprimerProfil.setEnabled(false);
		btnSupprimerProfil.addClickListener(e -> {
			if (droitProfilTable.getValue() instanceof DroitProfil) {
				droitProfilController.deleteDroitProfil((DroitProfil) droitProfilTable.getValue());
			}
		});
		buttonsLayout.addComponent(btnSupprimerProfil);
		buttonsLayout.setComponentAlignment(btnSupprimerProfil, Alignment.MIDDLE_RIGHT);

		/* Table des batchs */		
		droitProfilTable.addGeneratedColumn("fonctionnalite", new ColumnGenerator() {
			
			/*** serialVersionUID*/
			private static final long serialVersionUID = 7461290324017459118L;

			@Override
			public Object generateCell(Table source, Object itemId, Object columnId) {
				final DroitProfil profil = (DroitProfil) itemId;
				if (profil.getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_ADMIN)){
					return applicationContext.getMessage("droitprofil.descriptif.admin", null, UI.getCurrent().getLocale());
				}else if (profil.getCodProfil().equals(NomenclatureUtils.DROIT_PROFIL_SCOL_CENTRALE)){
					return applicationContext.getMessage("droitprofil.descriptif.scol", null, UI.getCurrent().getLocale());
				}
				String fonc = "";
				for (DroitProfilFonc droit : profil.getDroitProfilFoncs()){
					fonc += droit.getDroitFonctionnalite().getLibFonc();
					if (droit.getTemReadOnly()){
						fonc += " (LS)";
					}
					fonc += ", ";
				}
				if (!fonc.equals("")){
					fonc = fonc.substring(0, fonc.length()-2);
				}
				return new Label(fonc);
			}
		});
		droitProfilTable.setSizeFull();
		droitProfilTable.setVisibleColumns((Object[]) DROIT_PROFIL_FIELDS_ORDER);
		for (String fieldName : DROIT_PROFIL_FIELDS_ORDER) {
			droitProfilTable.setColumnHeader(fieldName, applicationContext.getMessage("droitprofil.table." + fieldName, null, UI.getCurrent().getLocale()));
		}
		droitProfilTable.setSortContainerPropertyId(DroitProfil_.codProfil.getName());
		droitProfilTable.setColumnCollapsingAllowed(true);
		droitProfilTable.setColumnReorderingAllowed(true);
		droitProfilTable.setSelectable(true);
		droitProfilTable.setImmediate(true);
		droitProfilTable.addItemSetChangeListener(e -> droitProfilTable.sanitizeSelection());
		droitProfilTable.addValueChangeListener(e -> {
			/* Les boutons d'édition, de programme et de lancement de batch sont actifs seulement si un droit est sélectionné. */
			boolean droitIsSelected = droitProfilTable.getValue() instanceof DroitProfil && ((DroitProfil) droitProfilTable.getValue()).getTemUpdatable();
			btnEditProfil.setEnabled(droitIsSelected);
			btnSupprimerProfil.setEnabled(droitIsSelected);
		});
		droitProfilTable.addItemClickListener(e -> {
			if (e.isDoubleClick() && droitProfilTable.getValue() instanceof DroitProfil && ((DroitProfil) droitProfilTable.getValue()).getTemUpdatable()) {
				droitProfilTable.select(e.getItemId());
				btnEditProfil.click();
			}
		});
		addComponent(droitProfilTable);
		setExpandRatio(droitProfilTable, 1);
		
		/* Inscrit la vue aux mises à jour de droitProfil */
		droitProfilEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		containerProfil.removeAllItems();
		containerProfil.addAll(droitProfilController.getDroitProfilNotAdmin());		
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
		/* Desinscrit la vue aux mises à jour de droitProfil */
		droitProfilEntityPusher.registerEntityPushListener(this);
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(DroitProfil entity) {
		droitProfilTable.removeItem(entity);
		droitProfilTable.addItem(entity);
		droitProfilTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(DroitProfil entity) {
		droitProfilTable.removeItem(entity);
		droitProfilTable.addItem(entity);
		droitProfilTable.sort();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(DroitProfil entity) {
		droitProfilTable.removeItem(entity);
	}
}
