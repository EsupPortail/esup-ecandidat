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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.FichierFiabilisation;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCand;
import fr.univlorraine.ecandidat.repositories.FichierFiabilisationRepository;
import fr.univlorraine.ecandidat.repositories.PieceJustifRepository;
import fr.univlorraine.ecandidat.repositories.PjCandRepository;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.PieceJustifWindow;
import fr.univlorraine.ecandidat.views.windows.UploadWindow;

/**
 * Gestion de l'entité pieceJustif
 * @author Kevin Hergalant
 */
@Component
public class PieceJustifController {
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
	private transient PieceJustifRepository pieceJustifRepository;
	@Resource
	private transient PjCandRepository pjCandRepository;
	@Resource
	private transient FichierFiabilisationRepository fichierFiabilisationRepository;
	@Resource
	private transient FileController fileController;
	
	/**
	 * @return liste des pieceJustifs
	 */
	public List<PieceJustif> getPieceJustifs() {
		return pieceJustifRepository.findAll();
	}
	
	/**
	 * @param cand
	 * @return la liste des PJ à afficher pour une candidature
	 * Toute les commune de la scol + toute les commune du ctr + toutes les pieces de la formation + les pièces effacées
	 */
	public List<PieceJustif> getPjForCandidature(Candidature cand, Boolean addDeletedPj){
		Formation formation = cand.getFormation();
		List<PieceJustif> liste = new ArrayList<PieceJustif>();
		
		//On ajoute les PJ communes de la scole centrale-->déjà trié
		liste.addAll(getPieceJustifsByCtrCandEnService(null, true));
		
		//On ajoute les PJ communes du centre de candidature-->déjà trié
		liste.addAll(getPieceJustifsByCtrCandEnService(formation.getCommission().getCentreCandidature().getIdCtrCand(), true));
		
		//On ajoute les PJ distinctes de la formation
		List<PieceJustif> listeFormation = formation.getPieceJustifs().stream().filter(e->e.getTesPj()).collect(Collectors.toList());
		Collections.sort(listeFormation);
		liste.addAll(listeFormation);
		
		//On ajoute les PJ qui seraient repassé hors service mais déjà renseignées par le candidat
		if (addDeletedPj){
			List<PieceJustif> listePjCand = new ArrayList<PieceJustif>();
			cand.getPjCands().forEach(e->{
				listePjCand.add(e.getPieceJustif());
			});
			Collections.sort(listePjCand);
			liste.addAll(listePjCand);
		}
		
		//on fait un distinct sur le tout
		return liste.stream().distinct().collect(Collectors.toList());
	}
	
	/**
	 * @return la liste complete des PJ
	 */
	public List<PieceJustif> getAllPieceJustifs() {
		return pieceJustifRepository.findAll();
	}

	/**
	 * @param idCtrCand
	 * @return a liste des PJ d'un ctr
	 */
	public List<PieceJustif> getPieceJustifsByCtrCand(Integer idCtrCand) {
		return pieceJustifRepository.findByCentreCandidatureIdCtrCand(idCtrCand);
	}
	
	/**
	 * @param idCtrCand
	 * @return la liste des PJ en service d'un ctr
	 */
	private List<PieceJustif> getPieceJustifsByCtrCandEnService(Integer idCtrCand, Boolean commun) {
		List<PieceJustif> liste = pieceJustifRepository.findByCentreCandidatureIdCtrCandAndTesPjAndTemCommunPj(idCtrCand,true,commun);
		Collections.sort(liste);
		return liste;
	}
	
	/**
	 * @return la liste des PJ communes de la scol
	 */
	public List<PieceJustif> getPieceJustifsCommunScolEnService() {
		return pieceJustifRepository.findByCentreCandidatureIdCtrCandAndTesPjAndTemCommunPj(null,true,true);
	}
	
	/**
	 * @return la liste des PJ communes de la scol
	 */
	public List<PieceJustif> getPieceJustifsCommunCtrCandEnService(Integer idCtrCand) {
		List<PieceJustif> liste = new ArrayList<PieceJustif>();
		liste.addAll(pieceJustifRepository.findByCentreCandidatureIdCtrCandAndTesPjAndTemCommunPj(null,true,true));
		liste.addAll(pieceJustifRepository.findByCentreCandidatureIdCtrCandAndTesPjAndTemCommunPj(idCtrCand,true,true));
		return liste;
	}
	
	/** Renvoie la liste des pj pour un ctrCand +
	 *  scol
	 * @param idCtrCand
	 * @return la liste des PJ
	 */
	public List<PieceJustif> getPieceJustifsByCtrCandAndScolCentral(Integer idCtrCand) {
		List<PieceJustif> liste = new ArrayList<PieceJustif>();
		liste.addAll(getPieceJustifsByCtrCandEnService(null, false));
		liste.addAll(getPieceJustifsByCtrCandEnService(idCtrCand, false));
		return liste;
	}
	
	/**
	 * Ouvre une fenêtre d'édition d'un nouveau pieceJustif.
	 * @param ctrCand 
	 */
	public void editNewPieceJustif(CentreCandidature ctrCand) {
		PieceJustif pj = new PieceJustif(userController.getCurrentUserLogin());
		pj.setI18nLibPj(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_PJ_LIB)));
		pj.setCentreCandidature(ctrCand);
		UI.getCurrent().addWindow(new PieceJustifWindow(pj));
	}
	
	/**
	 * Ouvre une fenêtre d'édition de pieceJustif.
	 * @param pieceJustif
	 */
	public void editPieceJustif(PieceJustif pieceJustif) {
		Assert.notNull(pieceJustif, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(pieceJustif, null)) {
			return;
		}
		PieceJustifWindow window = new PieceJustifWindow(pieceJustif);
		window.addCloseListener(e->lockController.releaseLock(pieceJustif));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre un pieceJustif
	 * @param pieceJustif
	 */
	public void savePieceJustif(PieceJustif pieceJustif) {
		Assert.notNull(pieceJustif, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (pieceJustif.getIdPj()!=null && !lockController.getLockOrNotify(pieceJustif, null)) {
			return;
		}
		pieceJustif.setUserModPj(userController.getCurrentUserLogin());
		pieceJustif.setI18nLibPj(i18nController.saveI18n(pieceJustif.getI18nLibPj()));
		pieceJustif = pieceJustifRepository.saveAndFlush(pieceJustif);
		
		lockController.releaseLock(pieceJustif);
	}

	/**
	 * Supprime une pieceJustif
	 * @param pieceJustif
	 */
	public void deletePieceJustif(PieceJustif pieceJustif) {
		Assert.notNull(pieceJustif, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/*Verification que le mode de fichier et celui de l'application sont identiques*/
		if (!fileController.isModeFileStockageOk(pieceJustif.getFichier(),true)){
			Notification.show(applicationContext.getMessage("file.error.mode", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		/*Verification que la pice n'est rattachée à rien*/		
		if (pjCandRepository.countByPieceJustif(pieceJustif)>0){
			Notification.show(applicationContext.getMessage("pieceJustif.error.delete", new Object[]{PjCand.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}
		
		/* Verrou */
		if (!lockController.getLockOrNotify(pieceJustif, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("pieceJustif.window.confirmDelete", new Object[]{pieceJustif.getCodPj()}, UI.getCurrent().getLocale()), applicationContext.getMessage("pieceJustif.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/*On vérifie que la PJ est utilisée par des formation ou est commune, dans ce cas-->2eme confirmation*/
			String question = null;			
			if (pieceJustifRepository.findOne(pieceJustif.getIdPj()).getFormations().size()>0){
				question = applicationContext.getMessage("pieceJustif.window.confirmDelete.form", null, UI.getCurrent().getLocale());
			}else if (pieceJustif.getTemCommunPj()){
				question = applicationContext.getMessage("pieceJustif.window.confirmDelete.commun", null, UI.getCurrent().getLocale()); 
			}
			
			if (question==null){
				deletePj(pieceJustif);
			}else{
				/* Verrou */
				if (!lockController.getLockOrNotify(pieceJustif, null)) {
					return;
				}
				ConfirmWindow confirmWindowPJUse = new ConfirmWindow(question, applicationContext.getMessage("pieceJustif.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
				confirmWindowPJUse.addBtnOuiListener(y -> {
					deletePj(pieceJustif);
				});
				confirmWindowPJUse.addCloseListener(y -> {
					/* Suppression du lock */
					lockController.releaseLock(pieceJustif);			
				});
				UI.getCurrent().addWindow(confirmWindowPJUse);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(pieceJustif);			
		});
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	/** SUpprime une pièce justificative
	 * @param pieceJustif
	 */
	private void deletePj(PieceJustif pieceJustif){
		/* Contrôle que le client courant possède toujours le lock */
		if (lockController.getLockOrNotify(pieceJustif, null)) {				
			try{
				deletePieceJustifDbAndFile(pieceJustif);
				/* Suppression du lock */
				lockController.releaseLock(pieceJustif);
			}catch(Exception ex){
				Notification.show(applicationContext.getMessage("file.error.delete", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			}
		}
	}
	
	/** Supprime une PJ
	 * @param pieceJustif
	 * @throws FileException
	 */
	/*@Transactional(rollbackFor=FileException.class)
	private void deletePjDbAndFile(PieceJustif pieceJustif) throws FileException{
		Fichier fichier = pieceJustif.getFichier();		
		pieceJustifRepository.delete(pieceJustif);
		if (fichier != null){
			fileController.deleteFichier(fichier,true);
		}
	}*/
	
	/** Supprime une PJ
	 * @param pieceJustif
	 */
	private void deletePieceJustifDbAndFile(PieceJustif pieceJustif){
		Fichier fichier = pieceJustif.getFichier();
		Integer id = pieceJustif.getIdPj();
		pieceJustifRepository.delete(pieceJustif);
		if (fichier != null){
			FichierFiabilisation fichierFiabilisation = new FichierFiabilisation(fichier);
			fichierFiabilisation.setIdPj(id);
			fichierFiabilisation = fichierFiabilisationRepository.save(fichierFiabilisation);
			try {
				fileController.deleteFichier(fichier);
				fichierFiabilisationRepository.delete(fichierFiabilisation);
			} catch (FileException e) {}
		}
	}
	
	/** AJoute un fichier à une pièce justif
	 * @param pieceJustif
	 */
	public void addFileToPieceJustificative(PieceJustif pieceJustif) {
		/* Verrou */
		if (!lockController.getLockOrNotify(pieceJustif, null)) {
			return;
		}
		String user = userController.getCurrentUserLogin();
		String cod = ConstanteUtils.TYPE_FICHIER_PJ_GEST+"_"+pieceJustif.getIdPj();
		UploadWindow uw = new UploadWindow(cod,ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE, null, false, false);
		uw.addUploadWindowListener(file->{
			if (file == null){
				return;
			}
			Fichier fichier = fileController.createFile(file,user,ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE);
			pieceJustif.setFichier(fichier);
			pieceJustifRepository.save(pieceJustif);
			Notification.show(applicationContext.getMessage("window.upload.success", new Object[]{file.getFileName()}, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
			uw.close();
		});
		uw.addCloseListener(e->lockController.releaseLock(pieceJustif));
		UI.getCurrent().addWindow(uw);
	}
	
	/** Supprime un fichier d'une pieceJustif
	 * @param pieceJustif
	 */
	public void deleteFileToPieceJustificative(PieceJustif pieceJustif) {
		/* Verrou */
		if (!lockController.getLockOrNotify(pieceJustif, null)) {
			return;
		}
		if (!fileController.isModeFileStockageOk(pieceJustif.getFichier(),true)){
			return;
		}
		Fichier fichier = pieceJustif.getFichier();		
		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("file.window.confirmDelete", new Object[]{fichier.getNomFichier()}, UI.getCurrent().getLocale()), applicationContext.getMessage("file.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(file -> {
			removeFileToPieceJustif(pieceJustif,fichier);
			
		});
		confirmWindow.addCloseListener(e->lockController.releaseLock(pieceJustif));
		UI.getCurrent().addWindow(confirmWindow);
	}
	
	/** Supprime un fichier d'une PJ
	 * @param pieceJustif
	 * @param fichier
	 * @throws FileException
	 */
	/*@Transactional(rollbackFor=FileException.class)
	private void removeFileToPj(PieceJustif pieceJustif, Fichier fichier) throws FileException{
		pieceJustif.setFichier(null);
		pieceJustifRepository.save(pieceJustif);
		fileController.deleteFichier(fichier,true);			
	}*/
	
	/**
	 * @param pieceJustif
	 * @param fichier
	 * @throws FileException
	 */
	private void removeFileToPieceJustif(PieceJustif pieceJustif, Fichier fichier){
		pieceJustif.setFichier(null);
		pieceJustif = pieceJustifRepository.save(pieceJustif);
		if (fichier != null){
			FichierFiabilisation fichierFiabilisation = new FichierFiabilisation(fichier);
			fichierFiabilisation.setIdPj(pieceJustif.getIdPj());
			fichierFiabilisation = fichierFiabilisationRepository.save(fichierFiabilisation);
			try {
				fileController.deleteFichier(fichier);
				fichierFiabilisationRepository.delete(fichierFiabilisation);
			} catch (FileException e) {}
		}
	}
	
	/** Verifie l'unicité du code
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodPjUnique(String cod, Integer id) {
		PieceJustif pieceJustif = pieceJustifRepository.findByCodPj(cod);
		if (pieceJustif==null){
			return true;
		}else{
			if (pieceJustif.getIdPj().equals(id)){
				return true;
			}
		}
		return false;
	}
}
