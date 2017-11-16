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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.rest.LimeSurveyRest;
import fr.univlorraine.ecandidat.controllers.rest.SurveyReponse;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCand;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCandidat;
import fr.univlorraine.ecandidat.entities.ecandidat.FormulaireCandidatPK;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.repositories.FormulaireCandRepository;
import fr.univlorraine.ecandidat.repositories.FormulaireCandidatRepository;
import fr.univlorraine.ecandidat.repositories.FormulaireRepository;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.FormulaireWindow;
import fr.univlorraine.ecandidat.views.windows.InfoWindow;
import fr.univlorraine.ecandidat.views.windows.InputWindow;

/**
 * Gestion de l'entité formulaire
 * @author Kevin Hergalant
 *
 */
@Component
public class FormulaireController {
	private Logger logger = LoggerFactory.getLogger(FormulaireController.class);
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LockController lockController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient UserController userController;
	@Resource
	private transient FormulaireRepository formulaireRepository;
	@Resource
	private transient LimeSurveyRest limeSurveyRest;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient FormulaireCandRepository formulaireCandRepository;
	@Resource
	private transient FormulaireCandidatRepository formulaireCandidatRepository;
	@Resource
	private transient DateTimeFormatter formatterDateTimeWS;
	@Resource
	private transient CacheController cacheController;
	
	/**
	 * @return liste des formulaires
	 */
	public List<Formulaire> getFormulaires() {
		return formulaireRepository.findAll();
	}
	
	/**
	 * @return liste des formulaires
	 */
	public List<Formulaire> getFormulairesEnService() {
		return formulaireRepository.findByTesFormulaire(true);
	}
	
	/**
	 * @param cand
	 * @return la liste des formulaires à afficher pour une candidature
	 * Tout les commune de la scol + tout les commune du ctr + tout les formulaires de la formation + les formulaires effacées
	 */
	public List<Formulaire> getFormulaireForCandidature(Candidature cand){
		Formation formation = cand.getFormation();
		List<Formulaire> liste = new ArrayList<Formulaire>();
		liste.addAll(getFormulairesByCtrCandEnService(null, true));
		liste.addAll(getFormulairesByCtrCandEnService(formation.getCommission().getCentreCandidature().getIdCtrCand(), true));
		liste.addAll(formation.getFormulaires().stream().filter(e->e.getTesFormulaire()).collect(Collectors.toList()));
		cand.getFormulaireCands().forEach(e->{
			liste.add(e.getFormulaire());
		});
		return liste.stream().distinct().collect(Collectors.toList());
	}
	
	/** Recherche les formulaires d'un centre de candidatures
	 * @param idCtrCand
	 * @return les formulaires d'un centre de candidatures
	 */
	public List<Formulaire> getFormulairesByCtrCand(
			Integer idCtrCand) {
		return formulaireRepository.findByCentreCandidatureIdCtrCand(idCtrCand);
	}
	
	/**
	 * @param idCtrCand
	 * @param commun
	 * @return les formulaires d'un centre de candidatures en service et commun ou non
	 */
	private List<Formulaire> getFormulairesByCtrCandEnService(Integer idCtrCand, Boolean commun) {
		return formulaireRepository.findByCentreCandidatureIdCtrCandAndTesFormulaireAndTemCommunFormulaire(idCtrCand,true,commun);
	}
	
	/**
	 * @return la liste des formulaires communs a tout l'etablissement
	 */
	public List<Formulaire> getFormulairesCommunScolEnService() {
		return formulaireRepository.findByCentreCandidatureIdCtrCandAndTesFormulaireAndTemCommunFormulaire(null,true,true);
	}
	
	/** Renvoie la liste des formulaires pour un ctrCand +
	 *  scol
	 * @param idCtrCand
	 * @return les formulaires et propre au ctr et commun a tout l'etablissement
	 */
	public List<Formulaire> getFormulairesByCtrCandAndScolCentral(Integer idCtrCand) {
		List<Formulaire> liste = new ArrayList<Formulaire>();
		liste.addAll(getFormulairesByCtrCandEnService(null, false));
		liste.addAll(getFormulairesByCtrCandEnService(idCtrCand, false));
		return liste;
	}
	
	/**
	 * @param idCtrCand
	 * @return la liste des formulaires en service et commun
	 */
	public List<Formulaire> getFormulairesCommunCtrCandEnService(Integer idCtrCand) {
		List<Formulaire> liste = new ArrayList<Formulaire>();
		liste.addAll(formulaireRepository.findByCentreCandidatureIdCtrCandAndTesFormulaireAndTemCommunFormulaire(null,true,true));
		liste.addAll(formulaireRepository.findByCentreCandidatureIdCtrCandAndTesFormulaireAndTemCommunFormulaire(idCtrCand,true,true));
		return liste;
	}

	/**
	 * Ouvre une fenêtre d'édition d'un nouveau formulaire.
	 * @param ctrCand 
	 */
	public void editNewFormulaire(CentreCandidature ctrCand) {
		Formulaire formulaire = new Formulaire(userController.getCurrentUserLogin());
		formulaire.setI18nLibFormulaire(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_FORM_LIB)));
		formulaire.setI18nUrlFormulaire(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_FORM_URL)));
		formulaire.setCentreCandidature(ctrCand);
		UI.getCurrent().addWindow(new FormulaireWindow(formulaire));
	}
	
	/**
	 * Ouvre une fenêtre d'édition de formulaire.
	 * @param formulaire
	 */
	public void editFormulaire(Formulaire formulaire) {
		Assert.notNull(formulaire, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(formulaire, null)) {
			return;
		}

		FormulaireWindow window = new FormulaireWindow(formulaire);
		window.addCloseListener(e->lockController.releaseLock(formulaire));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un formulaire
	 * @param formulaire
	 */	
	public void saveFormulaire(Formulaire formulaire) {
		Assert.notNull(formulaire, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		

		/* Verrou */
		if (formulaire.getIdFormulaire()!=null && !lockController.getLockOrNotify(formulaire, null)) {
			return;
		}
		formulaire.setUserModFormulaire(userController.getCurrentUserLogin());
		formulaire.setI18nLibFormulaire(i18nController.saveI18n(formulaire.getI18nLibFormulaire()));
		formulaire.setI18nUrlFormulaire(i18nController.saveI18n(formulaire.getI18nUrlFormulaire()));
		formulaire = formulaireRepository.saveAndFlush(formulaire);
		
		lockController.releaseLock(formulaire);
	}

	/**
	 * Supprime une formulaire
	 * @param formulaire
	 */
	public void deleteFormulaire(Formulaire formulaire) {
		Assert.notNull(formulaire, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));
		
		if (formulaireCandRepository.countByFormulaire(formulaire)>0){
			Notification.show(applicationContext.getMessage("formulaire.error.delete", new Object[]{FormulaireCand.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		/* Verrou */
		if (!lockController.getLockOrNotify(formulaire, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("motivAvis.window.confirmDelete", new Object[]{formulaire.getCodFormulaire()}, UI.getCurrent().getLocale()), applicationContext.getMessage("motivAvis.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* Contrôle que le client courant possède toujours le lock */
			if (lockController.getLockOrNotify(formulaire, null)) {
				formulaireRepository.delete(formulaire);
				/* Suppression du lock */
				lockController.releaseLock(formulaire);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(formulaire);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/** Verifie l'unicité du code
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodFormUnique(String cod, String idLimeSurveyStr, Integer id) {
		Integer idLimeSurvey = -1;
		try{
			idLimeSurvey = Integer.valueOf(idLimeSurveyStr);
		}catch(NumberFormatException e){}
		
		List<Formulaire> form = formulaireRepository.findByCodFormulaireOrIdFormulaireLimesurvey(cod,idLimeSurvey);
		if (form.size()==0){
			return true;
		}else if (form.size()==1 && form.get(0).getIdFormulaire().equals(id)){
			return true;
		}
		return false;
	}
	
	/**
	 * Lance le batch de synchro LimeSurvey
	 */
	public void launchBatchSyncLimeSurvey(){		
		List<Langue> listeLangue = new ArrayList<Langue>(cacheController.getLangueEnServiceWithoutDefault());
		listeLangue.add(cacheController.getLangueDefault());
		
		//on parcourt la liste des idLimeSurvey distinct et on exporte les réponse pour chaque langue en service
		getFormulairesEnService()
			.stream()
			.map(Formulaire::getIdFormulaireLimesurvey)
			.distinct()
			.collect(Collectors.toList())
			.forEach(idFormulaireLimeSurvey->{
				//listeLangue.forEach(langue->{
					//String codLangue = langue.getCodLangue();	
					String codLangue = null;
					try {
						/*On recherche les réponses du formulaire que l'on dedoublonne par rapport à la date de reponse*/
						for (SurveyReponse reponse : getListeReponseDedoublonne(limeSurveyRest.exportResponse(idFormulaireLimeSurvey, codLangue))){
							if (reponse.getNumDossier()==null){
								continue;
							}
							/*Recup des info du candidat*/
							CompteMinima cptMin = candidatController.searchCptMinByNumDossier(reponse.getNumDossier());
							if (cptMin==null || cptMin.getCandidat()==null){
								continue;
							}
							Candidat candidat = cptMin.getCandidat();
							FormulaireCandidatPK pk = new FormulaireCandidatPK(candidat.getIdCandidat(), idFormulaireLimeSurvey);
							LocalDateTime timeReponse;
							try{
								timeReponse = LocalDateTime.parse(reponse.getSubmitdate(), formatterDateTimeWS);
							}catch(Exception e){
								timeReponse = LocalDateTime.now();
							}
							
							/*Consitution de la réponse*/
							FormulaireCandidat formulaireCandidat = formulaireCandidatRepository.findOne(pk);							
							if (formulaireCandidat == null){
								formulaireCandidat = new FormulaireCandidat();
								formulaireCandidat.setId(pk);
								formulaireCandidat.setCandidat(candidat);
								formulaireCandidat.setReponsesFormulaireCandidat(getTextReponseSurvey(reponse.getMapReponses()));
								formulaireCandidat.setDatReponseFormulaireCandidat(timeReponse);
								formulaireCandidatRepository.save(formulaireCandidat);
							}else if (timeReponse.isAfter(formulaireCandidat.getDatReponseFormulaireCandidat())){
								formulaireCandidat.setReponsesFormulaireCandidat(getTextReponseSurvey(reponse.getMapReponses()));
								formulaireCandidat.setDatReponseFormulaireCandidat(timeReponse);
								formulaireCandidatRepository.save(formulaireCandidat);
							}
						}				
					} catch (Exception e) {
						logger.error("Erreur WebService LimeSurvey (idFormulaireLimeSurvey="+idFormulaireLimeSurvey+")",e);
						return;
					}
				//});			
		});
	}
	
	/**
	 * @param listeReponse
	 * @return la liste dedoublonne de réponse avec la réponse max
	 */
	private List<SurveyReponse> getListeReponseDedoublonne(List<SurveyReponse> listeReponse){
		if (listeReponse==null){
			return new ArrayList<SurveyReponse>();
		}
		Map<String,SurveyReponse> mapReponse = new HashMap<String,SurveyReponse>();
		listeReponse.forEach(e->{
			if (e.getNumDossier()==null || e.getSubmitdate()==null){
				return;
			}
			SurveyReponse rep = mapReponse.get(e.getNumDossier());
			if (rep==null || e.getSubmitdate().compareTo(rep.getSubmitdate())>0){
				mapReponse.put(e.getNumDossier(), e);
			}
		});
		return mapReponse.entrySet().stream().map(e -> e.getValue()).collect(Collectors.toList());
	}
	
	
	/**
	 * @param mapReponses
	 * @return les réponses formatées
	 */
	private String getTextReponseSurvey(Map<String,Object> mapReponses){
		String txtReponse = null;
		if (mapReponses==null || mapReponses.size()==0){
			return txtReponse;
		}
		
		for(Entry<String, Object> entry : mapReponses.entrySet()) {
		    if (txtReponse==null){
				txtReponse = "";
			}
			txtReponse = txtReponse+entry.getKey()+" : "+entry.getValue()+"{;}";
		}
		return txtReponse;
	}
	
	/**
	 * Teste la connexion à LimeSurvey
	 */
	public void testConnexionLS(){
		InputWindow inputWindow = new InputWindow(applicationContext.getMessage("version.ls.message", null, UI.getCurrent().getLocale()), applicationContext.getMessage("version.ls.title", null, UI.getCurrent().getLocale()), false, 15);
		inputWindow.addBtnOkListener(text -> {
			if (text instanceof String && !text.isEmpty()) {
				if (text!=null){
					try {
						Integer idForm = Integer.valueOf(text);
						List<SurveyReponse> listeReponse = getListeReponseDedoublonne(limeSurveyRest.exportResponse(idForm, "fr"));
						String nbRep = applicationContext.getMessage("version.ls.resultTxt", new Object[]{listeReponse.size()}, UI.getCurrent().getLocale());
						UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("version.ls.result", null, UI.getCurrent().getLocale()),nbRep , 400, 30));
					} catch (Exception e) {
						Notification.show(applicationContext.getMessage("version.ls.error", null, UI.getCurrent().getLocale()),Type.WARNING_MESSAGE);
					}					
				}
			}
		});
		UI.getCurrent().addWindow(inputWindow);
	}
}
