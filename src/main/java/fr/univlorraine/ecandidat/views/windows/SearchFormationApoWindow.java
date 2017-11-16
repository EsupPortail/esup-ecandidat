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
package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.FormationController;
import fr.univlorraine.ecandidat.controllers.IndividuController;
import fr.univlorraine.ecandidat.entities.siscol.Vet;
import fr.univlorraine.ecandidat.services.siscol.SiScolException;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de recherche de formation apogee
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class SearchFormationApoWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = -1777247785495796621L;
		
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FormationController formationController;
	@Resource
	private transient IndividuController individuController;
	
	public static final String[] FIELDS_ORDER = {"id.codEtpVet","id.codVrsVet","libVet","id.codCge","libTypDip"};
	
	/* Composants */
	private GridFormatting<Vet> grid = new GridFormatting<Vet>(Vet.class);
	private TextField searchBox;
	private OneClickButton btnSearch;
	private OneClickButton btnValider;
	private OneClickButton btnAnnuler;

	/*Listener*/
	private VetListener vetListener;


	/**
	 * Crée une fenêtre de recherche de formaiton apogée
	 * @param idCtrCand
	 */
	public SearchFormationApoWindow(Integer idCtrCand) {
		/* Style */
		setWidth(900, Unit.PIXELS);
		setHeight(500, Unit.PIXELS);
		setModal(true);
		setResizable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);

		/* Titre */
		setCaption(applicationContext.getMessage("window.search.vet.title", null, Locale.getDefault()));
		
		/* Recherche */
		HorizontalLayout searchLayout = new HorizontalLayout();
		searchBox = new TextField();
		searchBox.addShortcutListener(new ShortcutListener("Shortcut Name", ShortcutAction.KeyCode.ENTER, null) {

			/** serialVersionUID **/
			private static final long serialVersionUID = 4119756957960484247L;

			@Override
		    public void handleAction(Object sender, Object target) {
		    	performSearch();
		    }
		});

		btnSearch = new OneClickButton(applicationContext.getMessage("window.search", null, Locale.getDefault()));
		btnSearch.addClickListener(e->performSearch());
		Label labelLimit = new Label(applicationContext.getMessage("formation.window.apo.limit", new Object[]{ConstanteUtils.NB_MAX_RECH_FORM}, Locale.getDefault()));
		
		searchLayout.setSpacing(true);
		searchLayout.addComponent(searchBox);
		searchLayout.setComponentAlignment(searchBox, Alignment.MIDDLE_LEFT);
		searchLayout.addComponent(btnSearch);
		searchLayout.setComponentAlignment(btnSearch, Alignment.MIDDLE_LEFT);
		searchLayout.addComponent(labelLimit);
		searchLayout.setComponentAlignment(labelLimit, Alignment.MIDDLE_LEFT);
		
		layout.addComponent(searchLayout);
		
		/* Table de Resultat de recherche*/
		grid.initColumn(FIELDS_ORDER, "vet.", "id.codEtpVet");
		grid.addSelectionListener(e->{
			// Le bouton d'enregistrement est actif seulement si une vet est sélectionnée.
			boolean isSelected = grid.getSelectedItem() instanceof Vet;
			btnValider.setEnabled(isSelected);
		});
		grid.addItemClickListener(e->{
			if (e.isDoubleClick()) {
				grid.select(e.getItemId());
				btnValider.click();				
			}
		});
		grid.setColumnWidth("id.codEtpVet", 120);
		grid.setColumnWidth("id.codVrsVet", 110);
		grid.setColumnWidth("libVet", 240);
		//grid.setExpendColumn("libVet");
		grid.setColumnWidth("id.codCge", 96);
		grid.setExpendColumn("libTypDip");
		//grid.setColumnWidth("libTypDip", 96);
		
		layout.addComponent(grid);
		layout.setExpandRatio(grid, 1.0f);

		/* Boutons */
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setWidth(100, Unit.PERCENTAGE);
		buttonsLayout.setSpacing(true);
		layout.addComponent(buttonsLayout);

		btnAnnuler = new OneClickButton(applicationContext.getMessage("btnAnnuler", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnAnnuler.addClickListener(e -> close());
		buttonsLayout.addComponent(btnAnnuler);
		buttonsLayout.setComponentAlignment(btnAnnuler, Alignment.MIDDLE_LEFT);
		
		btnValider = new OneClickButton(applicationContext.getMessage("btnValid", null, UI.getCurrent().getLocale()), FontAwesome.SAVE);
		btnValider.setEnabled(false);
		btnValider.addStyleName(ValoTheme.BUTTON_PRIMARY);
		btnValider.addClickListener(e -> {
			performAction();
		});
		buttonsLayout.addComponent(btnValider);
		buttonsLayout.setComponentAlignment(btnValider, Alignment.MIDDLE_RIGHT);
		

		/* Centre la fenêtre */
		center();
	}
	
	/**
	 * Vérifie els donnée et si c'est ok, fait l'action (renvoie le AnneeUni)
	 */
	private void performAction(){
		if (vetListener != null){
			Vet vet = grid.getSelectedItem();
			if (vet==null){
				Notification.show(applicationContext.getMessage("window.search.selectrow", null, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
				return;
			}else{
				vetListener.btnOkClick(vet);
				close();
			}					
		}
	}
	
	/**
	 * Effectue la recherche
	 * @param codCgeUserApo 
	 * @param codCgeUser 
	 */
	private void performSearch(){
		if (searchBox.getValue().equals(null) || searchBox.getValue().equals("") || searchBox.getValue().length()<ConstanteUtils.NB_MIN_CAR_FORM){
			Notification.show(applicationContext.getMessage("window.search.morethan", new Object[]{ConstanteUtils.NB_MIN_CAR_FORM}, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
		}else{
			grid.removeAll();
			try {
				grid.addItems(formationController.getVetByCGE(searchBox.getValue()));
			} catch (SiScolException e) {
				Notification.show(applicationContext.getMessage("siscol.connect.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				close();
			}
		}
	}

	/**
	 * Défini le 'VetListener' utilisé
	 * @param vetListener
	 */
	public void addVetListener(VetListener vetListener) {
		this.vetListener = vetListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface VetListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param vet la vet a renvoyer
		 */
		public void btnOkClick(Vet vet);

	}

}
