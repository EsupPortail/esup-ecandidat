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
import java.util.LinkedHashSet;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.utils.bean.export.ExportListCandidatureOption;
import fr.univlorraine.ecandidat.views.template.CtrCandPreferenceWindowTemplate;

/**
 * Fenêtre de preference d'export
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class CtrCandPreferenceExportWindow extends CtrCandPreferenceWindowTemplate {
	
	/*** serialVersionUID */
	private static final long serialVersionUID = 6528147988356073643L;

	@Resource
	private transient ApplicationContext applicationContext;

	private PreferenceExportListener preferenceExportListener;

	/** Crée une fenêtre pour enregistrer ses préférences d'export
	 * @param listeColonneExport
	 * @param tempFooter
	 */
	public CtrCandPreferenceExportWindow(LinkedHashSet<ExportListCandidatureOption> listeColonneExport, Boolean tempFooter) {
		super();
		setCaption(applicationContext.getMessage("preference.window.export", null, UI.getCurrent().getLocale()));
		setInfoMessage(applicationContext.getMessage("preference.info.export", null, UI.getCurrent().getLocale()));
		
		/*Les colonnes*/
		String valeurColonneToReturn = "";
		String txtColonnesVisible = applicationContext.getMessage("preference.col.no", null, UI.getCurrent().getLocale());
		Boolean firstVisibleFind = false;
		for (ExportListCandidatureOption exportItem : listeColonneExport){
			if (firstVisibleFind){
				txtColonnesVisible = txtColonnesVisible+" - ";
			}else{
				txtColonnesVisible = "";
			}
			txtColonnesVisible = txtColonnesVisible + exportItem.getCaption();
			firstVisibleFind = true;
			valeurColonneToReturn = valeurColonneToReturn+exportItem.getId()+";";
		}
		final String valeurColonneToReturnFinal = valeurColonneToReturn;
		
		/*Ajout du composant de colonnes*/
		addComponentSpecifique(new Label(applicationContext.getMessage("preference.col.visible", new Object[]{txtColonnesVisible}, UI.getCurrent().getLocale()), ContentMode.HTML));
		
		/*Ajout du composant de footer*/
		String txtFooter = applicationContext.getMessage("oui.label", null, UI.getCurrent().getLocale());
		if (tempFooter!=null && !tempFooter){
			txtFooter = applicationContext.getMessage("non.label", null, UI.getCurrent().getLocale());
		}
		addComponentSpecifique(new Label(applicationContext.getMessage("preference.col.temFooter", new Object[]{txtFooter}, UI.getCurrent().getLocale()), ContentMode.HTML));
		
		

		/* Ajoute les boutons */
		
		/*Bouton de reinitialisation*/
		addReinitClickListener(e->{
			ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("preference.confirm.init", null, UI.getCurrent().getLocale()));
			confirmWindow.addBtnOuiListener(c -> {
				if (preferenceExportListener!=null){
					preferenceExportListener.initPref();
				}									
				close();
			});
			UI.getCurrent().addWindow(confirmWindow);
		});
		
		/*Bouton d'enregistrement de session*/
		addRecordSessionClickListener(e->{
			if (preferenceExportListener!=null){
				preferenceExportListener.saveInSession(valeurColonneToReturnFinal, tempFooter);	
			}					
			close();
		});
		
		/*Bouton d'enregistrement en base*/
		addRecordDbClickListener(e->{
			if (preferenceExportListener!=null){
				preferenceExportListener.saveInDb(valeurColonneToReturnFinal, tempFooter);
			}					
			close();
		});

		/* Centre la fenêtre */
		center();
	}
	
	/**
	 * Défini le 'preferenceExportListener' utilisé
	 * @param preferenceExportListener
	 */
	public void addPreferenceExportListener(PreferenceExportListener preferenceExportListener) {
		this.preferenceExportListener = preferenceExportListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui.
	 */
	public interface PreferenceExportListener extends Serializable {

		/**
		 * Initialise les preeferences
		 */
		public void initPref();
		
		/** Enregistre en session
		 * @param valeurColonneCoche
		 * @param tempFooter
		 */
		public void saveInSession(String valeurColonneCoche, Boolean tempFooter);
		
		/** Enregistre en base
		 * @param valeurColonneCoche
		 * @param tempFooter
		 */
		public void saveInDb(String valeurColonneCoche, Boolean tempFooter);

	}
}
