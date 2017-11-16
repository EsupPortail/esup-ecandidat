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
package fr.univlorraine.ecandidat.controllers;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.entities.ecandidat.PreferenceInd;
import fr.univlorraine.ecandidat.repositories.PreferenceIndRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Gestion des preferences
 * @author Kevin Hergalant
 */
@Component
public class PreferenceController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient PreferenceIndRepository preferenceIndRepository;
	

	/**
	 * @param login
	 * @return charge les preference d'un login
	 */
	public PreferenceInd getPreferenceIndividu(String login){
		return preferenceIndRepository.findByLoginInd(login);
	}
	
	/** prepare les preferences d'un individu à être enregsitré, si null, c'est que l'individu n'existe pas
	 * @return les preferences d'un individu
	 */
	public PreferenceInd preparePreferenceToSaveInDb(){
		String login = userController.getCurrentUserLogin();
		Individu individu = individuController.getIndividu(login);
		if (individu == null){
			return null;
		}
		PreferenceInd pref = getPreferenceIndividu(login);
		if (pref == null){
			pref = new PreferenceInd(individu);
		}
		return pref;
	}
	
	/**
	 * Initialise les preference de la vue
	 */
	public void initPrefCand(){
		savePrefCandInSession(null, null, null, null, null, false);
	}
	
	/** Modifie les preferences de vue dans la session
	 * @param listeColonne
	 * @param listColonneOrder
	 * @param frozen
	 * @param sortColonne
	 * @param sortDirection
	 * @param log
	 */
	public void savePrefCandInSession(String listeColonne, String listColonneOrder, Integer frozen, String sortColonne, String sortDirection, Boolean log){		
		PreferenceInd pref = userController.getPreferenceIndividu();	
		pref.setCandColVisiblePref(listeColonne);
		pref.setCandColOrderPref(listColonneOrder);
		pref.setCandColFrozenPref(frozen);
		pref.setCandColSortPref(sortColonne);
		pref.setCandColSortDirectionPref(sortDirection);
		userController.setPreferenceIndividu(pref);
		if (log){
			Notification.show(applicationContext.getMessage("preference.notif.session.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		}		
	}
	
	/** Modifie les preferences de vue dans la session
	 * @param listeColonne
	 * @param listColonneOrder 
	 * @param frozen
	 */
	public void savePrefCandInDb(String listeColonne, String listColonneOrder, Integer frozen, String sortColonne, String sortDirection){
		PreferenceInd pref = preparePreferenceToSaveInDb();
		if (pref == null){
			return;
		}
		pref.setCandColVisiblePref(listeColonne);
		pref.setCandColOrderPref(listColonneOrder);
		pref.setCandColFrozenPref(frozen);
		pref.setCandColSortPref(sortColonne);
		pref.setCandColSortDirectionPref(sortDirection);
		preferenceIndRepository.save(pref);
		savePrefCandInSession(listeColonne, listColonneOrder, frozen, sortColonne, sortDirection, false);
		Notification.show(applicationContext.getMessage("preference.notif.db.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
	}
	
	/**
	 * @param defaultValue
	 * @return les colonnes de la vue
	 */
	public String[] getPrefCandColonnesVisible(String[] defaultValue){
		PreferenceInd pref = userController.getPreferenceIndividu();
		if (pref.getCandColVisiblePref()!=null){			
			return pref.getCandColVisiblePref().split(";");
		}else{
			return defaultValue;
		}
	}
	
	/**
	 * @param defaultValue
	 * @return les colonnes de la vue
	 */
	public String[] getPrefCandColonnesOrder(String[] defaultValue){
		PreferenceInd pref = userController.getPreferenceIndividu();
		if (pref.getCandColOrderPref()!=null){			
			return pref.getCandColOrderPref().split(";");
		}else{
			return defaultValue;
		}
	}
	
	/**
	 * @return le nombre de colonne gelees
	 */
	public Integer getPrefCandFrozenColonne(Integer defaultValue){
		PreferenceInd pref = userController.getPreferenceIndividu();
		if (pref.getCandColVisiblePref()!=null){			
			return pref.getCandColFrozenPref();
		}else{
			return defaultValue;
		}
	}
	
	/**
	 * @return la colonne de trie
	 */
	public String getPrefCandSortColonne(String defaultSortColonne) {
		String sortColonne = userController.getPreferenceIndividu().getCandColSortPref();
		if (sortColonne == null){
			return defaultSortColonne;
		}
		return sortColonne;
	}

	/**
	 * @return la direction du trie
	 */
	public SortDirection getPrefCandSortDirection(SortDirection defaultSortDirection) {
		PreferenceInd pref = userController.getPreferenceIndividu();
		if (pref.getCandColSortDirectionPref()!=null && pref.getCandColSortDirectionPref().equals(ConstanteUtils.PREFERENCE_SORT_DIRECTION_ASCENDING)){
			return SortDirection.ASCENDING;
		}else if (pref.getCandColSortDirectionPref()!=null && pref.getCandColSortDirectionPref().equals(ConstanteUtils.PREFERENCE_SORT_DIRECTION_DESCENDING)){
			return SortDirection.DESCENDING;
		}
		return defaultSortDirection;
	}
	
	/**
	 * @return la commission favorite
	 */
	public Integer getPrefCandIdComm(){
		return userController.getPreferenceIndividu().getCandIdCommPref();
	}
	
	/** Modifie la commission favorite
	 * @param commission
	 */
	public void setPrefCandIdComm(Commission commission){
		/*On le modifie en session..*/
		PreferenceInd prefInSession = userController.getPreferenceIndividu();
		if (commission == null){
			prefInSession.setCandIdCommPref(null);
			return;
		}else{
			prefInSession.setCandIdCommPref(commission.getIdComm());
		}
		/*.. et en base*/
		PreferenceInd pref = preparePreferenceToSaveInDb();
		if (pref == null){
			return;
		}
		pref.setCandIdCommPref(commission.getIdComm());
		preferenceIndRepository.save(pref);
	}
	
	/**
	 * Initialise les preference de l'export
	 */
	public void initPrefExport() {
		savePrefExportInSession(null, true, false);
	}

	/** Enregistre les preference d'export en session
	 * @param valeurColonneCoche
	 * @param temFooter
	 * @param log
	 */
	public void savePrefExportInSession(String valeurColonneCoche, Boolean temFooter, Boolean log) {
		PreferenceInd pref = userController.getPreferenceIndividu();
		pref.setExportColPref(valeurColonneCoche);
		pref.setExportTemFooterPref(temFooter);
		userController.setPreferenceIndividu(pref);
		if (log){
			Notification.show(applicationContext.getMessage("preference.notif.session.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		}		
	}

	/** Enregistre les preference d'export en base
	 * @param valeurColonneCoche
	 * @param temFooter
	 */
	public void savePrefExportInDb(String valeurColonneCoche, Boolean temFooter) {
		PreferenceInd pref = preparePreferenceToSaveInDb();
		if (pref == null){
			return;
		}
		pref.setExportColPref(valeurColonneCoche);
		pref.setExportTemFooterPref(temFooter);
		preferenceIndRepository.save(pref);
		savePrefExportInSession(valeurColonneCoche, temFooter, false);		
		Notification.show(applicationContext.getMessage("preference.notif.db.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
	}
	
	
	/**
	 * @return les colonnes de l'export
	 */
	public String[] getPrefExportColonnes(){
		PreferenceInd pref = userController.getPreferenceIndividu();
		if (pref.getExportColPref()!=null){			
			return pref.getExportColPref().split(";");
		}
		return null;
	}
	
	/**
	 * @return lee footer de l'export
	 */
	public Boolean getPrefExportFooter(){
		PreferenceInd pref = userController.getPreferenceIndividu();
		if (pref.getExportTemFooterPref()!=null && !pref.getExportTemFooterPref()){			
			return false;
		}
		return true;
	}
	
	/** Modifie la commission favorite en session et en base
	 * @param commission
	 */
	public void setPrefCommission(Commission commission){
		/*On le modifie en session..*/
		PreferenceInd prefInSession = userController.getPreferenceIndividu();
		if (commission == null){
			return;
		}else{
			prefInSession.setIdCommPref(commission.getIdComm());
		}
		/*.. et en base*/
		PreferenceInd pref = preparePreferenceToSaveInDb();
		if (pref == null){
			return;
		}
		pref.setIdCommPref(commission.getIdComm());
		preferenceIndRepository.save(pref);
	}
	
	/** Modifie le centre de candidature favorit en session et en base
	 * @param ctrCand
	 */
	public void setPrefCentreCandidature(CentreCandidature ctrCand){
		/*On le modifie en session..*/
		PreferenceInd prefInSession = userController.getPreferenceIndividu();
		if (ctrCand == null){
			return;
		}else{
			prefInSession.setIdCtrCandPref(ctrCand.getIdCtrCand());
		}
		/*.. et en base*/
		PreferenceInd pref = preparePreferenceToSaveInDb();
		if (pref == null){
			return;
		}
		pref.setIdCtrCandPref(ctrCand.getIdCtrCand());
		preferenceIndRepository.save(pref);
	}
}
