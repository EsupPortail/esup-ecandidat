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
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.CommissionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse_;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission_;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune_;
import fr.univlorraine.ecandidat.vaadin.components.GridFormatting;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de recherche de commission
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class SearchCommissionWindow extends Window {
	
	/** serialVersionUID **/
	private static final long serialVersionUID = 3475563233611742318L;
		
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CommissionController commissionController;
	
	public static final String[] FIELDS_ORDER = {Commission_.codComm.getName(),Commission_.libComm.getName(),
		Commission_.adresse.getName()+"."+Adresse_.adr1Adr.getName(),
		 Commission_.adresse.getName()+"."+Adresse_.adr2Adr.getName(),
		 Commission_.adresse.getName()+"."+Adresse_.adr3Adr.getName(),
		 Commission_.adresse.getName()+"."+Adresse_.codBdiAdr.getName(),
		 Commission_.adresse.getName()+"."+Adresse_.siScolCommune.getName()+"."+SiScolCommune_.libCom.getName()};

	
	/* Composants */
	private GridFormatting<Commission> grid = new GridFormatting<Commission>(Commission.class);
	private OneClickButton btnValider;
	private OneClickButton btnAnnuler;

	/*Listener*/
	private CommissionListener commissionListener;


	/**Crée une fenêtre de recherche de commission
	 * @param ctrCand
	 */
	public SearchCommissionWindow(CentreCandidature ctrCand) {
		/* Style */
		setWidth(850, Unit.PIXELS);
		setHeight(480, Unit.PIXELS);
		setModal(true);
		setResizable(true);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		layout.setHeight(100, Unit.PERCENTAGE);
		layout.setMargin(true);
		layout.setSpacing(true);

		/* Titre */
		setCaption(applicationContext.getMessage("commission.window.search.title", null, Locale.getDefault()));
		
		/* Table de Resultat de recherche*/
		List<Commission> listeCommission;
		if (ctrCand != null){
			listeCommission = commissionController.getCommissionsByCtrCand(ctrCand);
		}else{
			listeCommission = commissionController.getCommissionsGestionnaire();
		}
		
		grid.addItems(listeCommission);
		grid.initColumn(FIELDS_ORDER, "commission.table.", Commission_.codComm.getName());
		grid.addSelectionListener(e->{
			// Le bouton d'enregistrement est actif seulement si une commission est sélectionnée.
			boolean isSelected = grid.getSelectedItem() instanceof Commission;
			btnValider.setEnabled(isSelected);
		});
		grid.addItemClickListener(e->{
			if (e.isDoubleClick()) {
				grid.select(e.getItemId());
				btnValider.click();				
			}
		});
		
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
	 * Vérifie les donnée et si c'est ok, fait l'action (renvoie le Commission)
	 */
	private void performAction(){
		if (commissionListener != null){
			Commission commission = grid.getSelectedItem();
			if (commission==null){
				Notification.show(applicationContext.getMessage("window.search.selectrow", null, Locale.getDefault()), Notification.Type.WARNING_MESSAGE);
				return;
			}else{				
				commissionListener.btnOkClick(commission);
				close();
			}					
		}
	}

	/**
	 * Défini le 'CommissionListener' utilisé
	 * @param commissionListener
	 */
	public void addCommissionListener(CommissionListener commissionListener) {
		this.commissionListener = commissionListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui ou Non.
	 */
	public interface CommissionListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param commission la Commission a renvoyer 
		 */
		public void btnOkClick(Commission commission);

	}

}
