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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.entities.ecandidat.AlertSva;
import fr.univlorraine.ecandidat.repositories.AlertSvaRepository;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleBeanPresentation;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.ScolAlertSvaWindow;

/**
 * Gestion de l'entité alertes SVA
 * @author Kevin Hergalant
 */
@Component
public class AlertSvaController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient AlertSvaRepository alertSvaRepository;
	
	public List<AlertSva> getAlertSvaToCache() {
		return alertSvaRepository.findAll();
	}
	
	/**
	 * @return liste des alertSva
	 */
	public List<AlertSva> getAlertSvaEnService() {
		return cacheController.getAlertesSva().stream().filter(e->e.getTesSva()).collect(Collectors.toList());
	}
	
	/**
	 * Ouvre une fenêtre d'édition d'un nouveau alertSva.
	 */
	public void editNewAlertSva() {
		UI.getCurrent().addWindow(new ScolAlertSvaWindow(new AlertSva()));
	}
	
	/**
	 * Ouvre une fenêtre d'édition de alertSva.
	 * @param alertSva
	 */
	public void editAlertSva(AlertSva alertSva) {
		Assert.notNull(alertSva, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(alertSva, null)) {
			return;
		}
		ScolAlertSvaWindow window = new ScolAlertSvaWindow(alertSva);
		window.addCloseListener(e->lockController.releaseLock(alertSva));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un alertSva
	 * @param alertSva
	 */
	public void saveAlertSva(AlertSva alertSva) {
		Assert.notNull(alertSva, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		/* Verrou */
		if (alertSva.getIdSva()!=null && !lockController.getLockOrNotify(alertSva, null)) {
			return;
		}
		alertSva = alertSvaRepository.saveAndFlush(alertSva);
		cacheController.reloadAlertesSva();
		lockController.releaseLock(alertSva);
	}

	/**
	 * Supprime une alertSva
	 * @param alertSva
	 */
	public void deleteAlertSva(AlertSva alertSva) {
		Assert.notNull(alertSva, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		
		/* Verrou */
		if (!lockController.getLockOrNotify(alertSva, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("alertSva.window.confirmDelete", new Object[]{alertSva.getNbJourSva()}, UI.getCurrent().getLocale()), applicationContext.getMessage("alertSva.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(alertSva, null)) {
				alertSvaRepository.delete(alertSva);
				cacheController.reloadAlertesSva();
				/* Suppression du lock */
				lockController.releaseLock(alertSva);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(alertSva);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	/** Verifie l'unicité du nombre de jours
	 * @param nbJoursTxt
	 * @param id
	 * @return true si le nombre de jours est unique
	 */
	public Boolean isNbJoursUnique(String nbJoursTxt, Integer id) {
		Integer nbJours;
		try{
			nbJours = Integer.valueOf(nbJoursTxt);
		}catch(Exception e){
			return true;
		}
		
		List<AlertSva> listeAlert = alertSvaRepository.findByNbJourSva(nbJours);
		if (listeAlert==null || listeAlert.size()==0){
			return true;
		}else{
			if (listeAlert.get(0).getIdSva().equals(id)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return la liste des style scc a renvoyer
	 */
	public List<String> getListAlertSvaCss(){
		List<String> liste = new ArrayList<String>();
		List<AlertSva> listeAlerteSva = getAlertSvaEnService();
		/*On ajoute les css colorisant les lignes pour sva*/
		for (AlertSva alert : listeAlerteSva){
			liste.add("."+StyleConstants.GRID+" ."+StyleConstants.GRID_ROW_SVA+"-"+alert.getNbJourSva()+" ."+StyleConstants.GRID_CELL+" { background-color: "+alert.getColorSva()+"; }");
		}
		return liste;
	}
	
	/**
	 * @param code
	 * @return le libellé de date SVA
	 */
	public String getLibelleDateSVA(String code){
		if (code == null){
			return null;
		}
		return applicationContext.getMessage("alertSva.choix.date."+code, null,  UI.getCurrent().getLocale());
	}
	
	/**
	 * @return la liste de type de date SVA
	 */
	public List<SimpleBeanPresentation> getListeDateSVA(){
		List<SimpleBeanPresentation> liste = new ArrayList<SimpleBeanPresentation>();
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.CAND_DAT_NO_DAT, getLibelleDateSVA(NomenclatureUtils.CAND_DAT_NO_DAT)));
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.CAND_DAT_ACCEPT, getLibelleDateSVA(NomenclatureUtils.CAND_DAT_ACCEPT)));
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.CAND_DAT_TRANS, getLibelleDateSVA(NomenclatureUtils.CAND_DAT_TRANS)));
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.CAND_DAT_RECEPT, getLibelleDateSVA(NomenclatureUtils.CAND_DAT_RECEPT)));
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.CAND_DAT_COMPLET, getLibelleDateSVA(NomenclatureUtils.CAND_DAT_COMPLET)));
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.CAND_DAT_INCOMPLET, getLibelleDateSVA(NomenclatureUtils.CAND_DAT_INCOMPLET)));
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.CAND_DAT_CRE, getLibelleDateSVA(NomenclatureUtils.CAND_DAT_CRE)));
		liste.add(new SimpleBeanPresentation(NomenclatureUtils.CAND_DAT_ANNUL, getLibelleDateSVA(NomenclatureUtils.CAND_DAT_ANNUL)));		
		return liste;
	}
}
