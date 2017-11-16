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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.sort.SortOrder;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.template.CtrCandPreferenceWindowTemplate;

/**
 * Fenêtre de preference de vue candidature
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CtrCandPreferenceViewWindow extends CtrCandPreferenceWindowTemplate {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = 6528147988356073643L;

	@Resource
	private transient ApplicationContext applicationContext;
	
	private PreferenceViewListener preferenceViewListener;	
	private TextField tfFrozen = new TextField();
	

	/** Constructeur
	 * @param listeColonneView
	 * @param frozenCountView
	 * @param maxColumnView
	 * @param sortOrder 
	 */
	public CtrCandPreferenceViewWindow(List<Column> listeColonneView, Integer frozenCountView, Integer maxColumnView, List<SortOrder> sortOrder) {
		super();
		setCaption(applicationContext.getMessage("preference.window.view", null, UI.getCurrent().getLocale()));
		setInfoMessage(applicationContext.getMessage("preference.info.view", null, UI.getCurrent().getLocale()));
		
		/*Les colonnes*/
		String valeurColonneToReturn = "";
		String txtColonnesVisible = applicationContext.getMessage("preference.col.no", null, UI.getCurrent().getLocale());
		Boolean firstVisibleFind = false;
		String txtColonnesInvisible = applicationContext.getMessage("preference.col.no", null, UI.getCurrent().getLocale());
		Boolean firstInvisibleFind = false;
		String valeurColonneOrderToReturn = "";
		String txtColonnesOrder = applicationContext.getMessage("preference.col.no", null, UI.getCurrent().getLocale());
		Boolean firstOrderFind = false;
		/*Traitement des colonnes*/
		
		for (Column col : listeColonneView){
			if (col.isHidden()){
				if (firstInvisibleFind){
					txtColonnesInvisible = txtColonnesInvisible+" - ";
				}else{
					txtColonnesInvisible = "";
				}
				txtColonnesInvisible = txtColonnesInvisible + col.getHeaderCaption();
				firstInvisibleFind = true;
			}else{
				if (firstVisibleFind){
					txtColonnesVisible = txtColonnesVisible+" - ";
				}else{
					txtColonnesVisible = "";
				}
				txtColonnesVisible = txtColonnesVisible + col.getHeaderCaption();
				firstVisibleFind = true;
				valeurColonneToReturn = valeurColonneToReturn+col.getPropertyId()+";";
			}
			if (firstOrderFind){
				txtColonnesOrder = txtColonnesOrder+" - ";
			}else{
				txtColonnesOrder = "";
			}
			txtColonnesOrder = txtColonnesOrder + col.getHeaderCaption();
			firstOrderFind = true;			
			valeurColonneOrderToReturn = valeurColonneOrderToReturn+col.getPropertyId()+";";
		}
		final String valeurColonneVisibleToReturnFinal = valeurColonneToReturn;
		final String valeurColonneOrderToReturnFinal = valeurColonneOrderToReturn;
		
		String txtSort = applicationContext.getMessage("default.label", null, UI.getCurrent().getLocale());
		String sortColonne = null;
		String sortDirection = null;
		
		//Le tri
		if (sortOrder!=null && sortOrder.size()>0){
			SortOrder tri = sortOrder.get(0);
			if (!tri.getPropertyId().equals(Candidature_.idCand.getName())){
				try{
					sortColonne = (String) tri.getPropertyId();
					sortDirection = tri.getDirection().equals(SortDirection.ASCENDING)?ConstanteUtils.PREFERENCE_SORT_DIRECTION_ASCENDING:ConstanteUtils.PREFERENCE_SORT_DIRECTION_DESCENDING;
					txtSort = listeColonneView.stream().filter(col->col.getPropertyId().equals(tri.getPropertyId())).findFirst().get().getHeaderCaption()+ " (";
					txtSort = txtSort + (tri.getDirection().equals(SortDirection.ASCENDING)?applicationContext.getMessage("preference.col.sort.dir.asc", null, UI.getCurrent().getLocale()):applicationContext.getMessage("preference.col.sort.dir.desc", null, UI.getCurrent().getLocale()))+")";
				}catch(Exception e){}				
			}
		}
		final String sortColonneFinal = sortColonne;
		final String sortDirectionFinal = sortDirection;		
		
		/*Ajout des composants de colonnes*/
		addComponentSpecifique(new Label(applicationContext.getMessage("preference.col.visible", new Object[]{txtColonnesVisible}, UI.getCurrent().getLocale()), ContentMode.HTML));
		addComponentSpecifique(new Label(applicationContext.getMessage("preference.col.invisible", new Object[]{txtColonnesInvisible}, UI.getCurrent().getLocale()), ContentMode.HTML));
		addComponentSpecifique(new Label(applicationContext.getMessage("preference.col.order", new Object[]{txtColonnesOrder}, UI.getCurrent().getLocale()), ContentMode.HTML));
		addComponentSpecifique(new Label(applicationContext.getMessage("preference.col.sort", new Object[]{txtSort}, UI.getCurrent().getLocale()), ContentMode.HTML));
		
		/*Frozen*/
		HorizontalLayout hlFrozen = new HorizontalLayout();
		hlFrozen.setSpacing(true);
		Label labelFrozen = new Label(applicationContext.getMessage("preference.col.frozen", null, UI.getCurrent().getLocale()), ContentMode.HTML);
		hlFrozen.addComponent(labelFrozen);
		hlFrozen.setComponentAlignment(labelFrozen, Alignment.BOTTOM_LEFT);		
		tfFrozen.setMaxLength(2);
		tfFrozen.setColumns(3);
		tfFrozen.setValue(frozenCountView.toString());
		tfFrozen.addStyleName(ValoTheme.TEXTFIELD_TINY);
		hlFrozen.addComponent(tfFrozen);
		addComponentSpecifique(hlFrozen);
		
		
		/*Bouton de reinitialisation*/
		addReinitClickListener(e->{
			Integer frozen = getFrozen(maxColumnView);
			if (frozen!=null){
				ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("preference.confirm.init", null, UI.getCurrent().getLocale()));
				confirmWindow.addBtnOuiListener(c -> {
					if (preferenceViewListener!=null){
						preferenceViewListener.initPref();
					}					
					close();
				});
				UI.getCurrent().addWindow(confirmWindow);				
			}
		});
		
		/*Bouton d'enregistrement de session*/
		addRecordSessionClickListener(e->{
			Integer frozen = getFrozen(maxColumnView);
			if (frozen!=null){		
				if (preferenceViewListener!=null){
					preferenceViewListener.saveInSession(valeurColonneVisibleToReturnFinal, valeurColonneOrderToReturnFinal, frozen, sortColonneFinal, sortDirectionFinal);	
				}				
				close();
			}
		});
		
		/*Bouton d'enregistrement en base*/
		addRecordDbClickListener(e->{
			Integer frozen = getFrozen(maxColumnView);
			if (frozen!=null){		
				if (preferenceViewListener!=null){
					preferenceViewListener.saveInDb(valeurColonneVisibleToReturnFinal, valeurColonneOrderToReturnFinal, frozen, sortColonneFinal, sortDirectionFinal);
				}				
				close();
			}
		});

		/* Centre la fenêtre */
		center();
	}
	
	/**
	 * @param maxColumn
	 * @return le nombre de colonne gelees
	 */
	private Integer getFrozen(Integer maxColumn){
		if (tfFrozen.getValue()==null || tfFrozen.getValue().equals("")){
			Notification.show(applicationContext.getMessage("preference.col.frozen.error", new Object[]{0,maxColumn}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return null;
		}
		try{
			Integer frozen = Integer.valueOf(tfFrozen.getValue());
			if (frozen<0 || frozen>maxColumn){
				Notification.show(applicationContext.getMessage("preference.col.frozen.error", new Object[]{0,maxColumn}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
			return frozen;
		}catch (Exception ex){
			Notification.show(applicationContext.getMessage("preference.col.frozen.error", new Object[]{0,maxColumn}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return null;
		}		
	}
	
	/**
	 * Défini le 'preferenceViewListener' utilisé
	 * @param preferenceViewListener
	 */
	public void addPreferenceViewListener(PreferenceViewListener preferenceViewListener) {
		this.preferenceViewListener = preferenceViewListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui.
	 */
	public interface PreferenceViewListener extends Serializable {
		/**
		 * Initialise les preferences
		 */
		public void initPref();
		
		/**
		 * @param valeurColonneVisible 
		 * @param valeurColonneOrder 
		 * @param frozenCols
		 */
		public void saveInSession(String valeurColonneVisible, String valeurColonneOrder, Integer frozenCols, String sortColonne, String sortDirection);
		
		/**
		 * @param valeurColonneVisible 
		 * @param valeurColonneOrder 
		 * @param frozenCols
		 */
		public void saveInDb(String valeurColonneVisible, String valeurColonneOrder, Integer frozenCols, String sortColonne, String sortDirection);

	}
}
