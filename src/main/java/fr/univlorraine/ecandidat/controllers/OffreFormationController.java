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
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfCtrCand;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfDiplome;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfFormation;

/**Gestion de l'offre de formation
 * @author Kevin Hergalant
 */
@Component
public class OffreFormationController {
	/* Injections */
	@Resource
	private transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient CacheController cacheController;
	
	
	/** Recupere l'offre de formation
	 * @return la liste d'odf
	 */
	public List<OdfCtrCand> getOdfToCache() {
		List<OdfCtrCand> offreDeFormation = new ArrayList<OdfCtrCand>();
		centreCandidatureController.getCentreCandidaturesEnService().forEach(ctr->{
			addInternalCtrCand(ctr, offreDeFormation);				
		});
		return offreDeFormation;
	}
	
	/** Recupere un objet de type OdfCtrCand suivant son id
	 * @param ctrCand
	 * @return l'odf du ctrCand
	 */
	private OdfCtrCand getCtrCandFromOffre(CentreCandidature ctrCand, List<OdfCtrCand> offreDeFormation){
		Optional<OdfCtrCand> centreOpt = offreDeFormation.stream().filter(odfCtr->odfCtr.getIdCtrCand().equals(ctrCand.getIdCtrCand())).findFirst();
		if (centreOpt.isPresent()){
			return centreOpt.get();
		}
		return null;
	}
	
	/** Ajoute un centre de candidature à l'offre
	 * @param ctrCand
	 */
	public void addCtrCand(CentreCandidature ctrCand){		
		List<OdfCtrCand> offreDeFormation = cacheController.getOdf();
		addInternalCtrCand(ctrCand, offreDeFormation);
		cacheController.updateOdf(offreDeFormation);
	}
	
	/** Ajoute un centre de candidature à l'offre
	 * @param ctrCand
	 * @param offreDeFormation 
	 * @param initialLoading 
	 */
	private void addInternalCtrCand(CentreCandidature ctrCand, List<OdfCtrCand> offreDeFormation){		
		if (getCtrCandFromOffre(ctrCand, offreDeFormation)!=null){
			removeInternalCtrCand(ctrCand, offreDeFormation);
		}
		if (!ctrCand.getTesCtrCand()){
			removeInternalCtrCand(ctrCand, offreDeFormation);
			return;
		}
		List<Formation> formations = new ArrayList<Formation>();
		
		ctrCand.getCommissions().forEach(e->formations.addAll(e.getFormations()));
		
		
		if (formations.size()>0){
			OdfCtrCand ctrCandOffre = new OdfCtrCand(ctrCand.getIdCtrCand(),ctrCand.getLibCtrCand());
			ctrCandOffre.setListeDiplome(getDiplomesByCtrCand(ctrCand.getIdCtrCand(),formations));
			offreDeFormation.add(ctrCandOffre);			
		}
		offreDeFormation.sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
	}
	
	/** Supprime un centre de candidature de l'offre
	 * @param ctrCand
	 */
	public void removeCtrCand(CentreCandidature ctrCand){
		List<OdfCtrCand> offreDeFormation = cacheController.getOdf();
		removeInternalCtrCand(ctrCand, offreDeFormation);
		cacheController.updateOdf(offreDeFormation);
	}
	
	/** Supprime un centre de candidature de l'offre
	 * @param ctrCand
	 */
	private void removeInternalCtrCand(CentreCandidature ctrCand, List<OdfCtrCand> offreDeFormation){
		offreDeFormation.remove(new OdfCtrCand(ctrCand.getIdCtrCand(),ctrCand.getLibCtrCand()));		
	}
	
	/** Recupere un objet de type OdfDiplome suivant son code
	 * @param ctrCand
	 * @param siScolTypDiplome
	 * @return l'OdfDiplome
	 */
	private OdfDiplome getDiplomeFromOffre(OdfCtrCand ctrCand, SiScolTypDiplome siScolTypDiplome){
		if (ctrCand.getListeDiplome()==null || ctrCand.getListeDiplome().size()==0){
			return null;
		}
		
		Optional<OdfDiplome> dipOpt = ctrCand.getListeDiplome().stream().filter(dip->dip.getCodDip().equals(siScolTypDiplome.getCodTpdEtb())).findFirst();
		if (dipOpt.isPresent()){
			return dipOpt.get();
		}
		return null;
	}
	
	/** Renvoi la liste de diplome d'un centre de candidature
	 * @param formations
	 * @return la liste d'odf diplomes
	 */
	private List<OdfDiplome> getDiplomesByCtrCand(Integer idCtr, List<Formation> formations){
		List<OdfDiplome> diplomes = new ArrayList<OdfDiplome>();
		/*Parcourt des formations*/
		formations.forEach(formation->{
			if (formation.getTesForm()){
				SiScolTypDiplome diplome = formation.getSiScolTypDiplome();
				/*Verification que le diplome est deja présent dans la liste des diplomes*/
				Optional<OdfDiplome> dipOpt = diplomes.stream().filter(dip->dip.getCodDip().equals(diplome.getCodTpdEtb())).findAny();
				OdfDiplome leDiplome = null;
				/*Si deja présent-->on le recupere et on ajoute la formation à ce diplome*/
				if (dipOpt.isPresent()){
					leDiplome = dipOpt.get();		
				}
				/*Si pas present on en créé un nouveau*/
				else{
					leDiplome = new OdfDiplome(idCtr+"-"+diplome.getCodTpdEtb(),diplome.getCodTpdEtb(),diplome.getLibTpd());
					diplomes.add(leDiplome);
				}
				leDiplome.getListeFormation().add(new OdfFormation(formation.getLibForm(), formation.getIdForm(), formation.getMotCleForm(), formation.getDatDebDepotForm(),formation.getDatFinDepotForm(), formation.getTemDematForm()));
				leDiplome.getListeFormation().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));	
			}			
		});
		diplomes.sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
		return diplomes;
	}
	


	
	/** Recupere un objet de type OdfDiplome suivant son code
	 * @param ctrCand
	 * @param siScolTypDiplome
	 * @return l'odfFormation
	 */
	private OdfFormation getFormationFromOffre(OdfDiplome odfDiplome, Formation formation){
		if (odfDiplome.getListeFormation()==null || odfDiplome.getListeFormation().size()==0){
			return null;
		}
		
		Optional<OdfFormation> formOpt = odfDiplome.getListeFormation().stream().filter(form->form.getIdFormation().equals(formation.getIdForm())).findFirst();
		if (formOpt.isPresent()){
			return formOpt.get();
		}
		return null;
	}
	
	
	/** Ajoute une formation à l'offre
	 * @param formation
	 */
	public void addFormation(Formation formation){
		List<OdfCtrCand> offreDeFormation = cacheController.getOdf();
		CentreCandidature ctrCand = formation.getCommission().getCentreCandidature();
		if (formation.getTesForm()){
			OdfCtrCand odfCtrCand = getCtrCandFromOffre(ctrCand, offreDeFormation);
			if (odfCtrCand!=null){
				OdfDiplome odfDiplome = getDiplomeFromOffre(odfCtrCand,formation.getSiScolTypDiplome());
				if (odfDiplome==null){
					odfDiplome = new OdfDiplome(ctrCand.getIdCtrCand()+"-"+formation.getSiScolTypDiplome().getCodTpdEtb(),formation.getSiScolTypDiplome().getCodTpdEtb(),formation.getSiScolTypDiplome().getLibTpd());
					odfCtrCand.getListeDiplome().add(odfDiplome);
					odfCtrCand.getListeDiplome().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
				}
				OdfFormation odfFormation = getFormationFromOffre(odfDiplome, formation);
				if (odfFormation==null){
					odfDiplome.getListeFormation().add(new OdfFormation(formation.getLibForm(), formation.getIdForm(), formation.getMotCleForm(), formation.getDatDebDepotForm(),formation.getDatFinDepotForm(), formation.getTemDematForm()));
					odfDiplome.getListeFormation().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
				}else{
					odfFormation.setTitle(formation.getLibForm());
					odfFormation.setMotCle(formation.getMotCleForm());
					odfFormation.setDateDebut(formation.getDatDebDepotForm());
					odfFormation.setDateFin(formation.getDatFinDepotForm());
					odfFormation.setModeCandidature(formation.getTemDematForm());					
					odfDiplome.getListeFormation().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
				}
			}else{
				addInternalCtrCand(ctrCand, offreDeFormation);
			}
		}else{
			removeInternalFormation(formation, offreDeFormation);
		}	
		cacheController.updateOdf(offreDeFormation);
	}
	
	/** Supprime une formation à l'offre
	 * @param formation
	 */
	public void removeFormation(Formation formation){
		List<OdfCtrCand> offreDeFormation = cacheController.getOdf();
		removeInternalFormation(formation, offreDeFormation);
		cacheController.updateOdf(offreDeFormation);
	}
	
	/** Supprime une formation de l'offre
	 * @param formation
	 */
	private void removeInternalFormation(Formation formation, List<OdfCtrCand> offreDeFormation){
		OdfCtrCand odfCtrCand = getCtrCandFromOffre(formation.getCommission().getCentreCandidature(), offreDeFormation);
		if (odfCtrCand!=null){
			OdfDiplome odfDiplome = getDiplomeFromOffre(odfCtrCand,formation.getSiScolTypDiplome());
			if (odfDiplome!=null){
				odfDiplome.getListeFormation().remove(new OdfFormation(formation.getLibForm(), formation.getIdForm(), formation.getMotCleForm(), formation.getDatDebDepotForm(),formation.getDatFinDepotForm(), formation.getTemDematForm()));
				if (odfDiplome.getListeFormation().size()==0){
					odfCtrCand.getListeDiplome().remove(odfDiplome);
				}
				if (odfCtrCand.getListeDiplome().size()==0){
					offreDeFormation.remove(odfCtrCand);					
				}
			}
		}
	}
}
