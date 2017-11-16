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

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolUtilisateur;
import fr.univlorraine.ecandidat.repositories.IndividuRepository;
import fr.univlorraine.ecandidat.repositories.SiScolUtilisateurRepository;
import fr.univlorraine.ecandidat.utils.CustomException;


/**
 * Gestion des individus
 * @author Kevin Hergalant
 *
 */
@Component
public class IndividuController {
	
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	
	@Resource
	private transient LockController lockController;
	
	@Resource
	private transient IndividuRepository individuRepository;
	
	@Resource
	private transient SiScolUtilisateurRepository siScolUtilisateurRepository;
	
	
	/** Enregistre un individu
	 * @param individu
	 * @return l'individu
	 */
	public Individu saveIndividu(Individu individu){
		Individu ind = individuRepository.findOne(individu.getLoginInd());
		if (ind == null){
			return individuRepository.save(individu);
		}else{
			ind.setLibelleInd(individu.getLibelleInd());
			ind.setMailInd(individu.getMailInd());
			return individuRepository.save(ind);
		}
	}
	
	/** Retourne un individu
	 * @param login
	 * @return l'individu
	 */
	public Individu getIndividu(String login){
		return individuRepository.findOne(login);
	}
	
	/** Valide un bean d'individu
	 * @param ind
	 * @throws CustomException
	 */
	public void validateIndividuBean(Individu ind) throws CustomException{
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<Individu>> constraintViolations = validator.validate(ind);
		if (constraintViolations!=null && constraintViolations.size() > 0) {
			String erreur = ""; 
			for (ConstraintViolation<?> violation : constraintViolations) {
				erreur += (" *** "+ violation.getPropertyPath().toString() + " : " + violation.getMessage());
			  }
			throw new CustomException(applicationContext.getMessage("droitprofil.individu.error", null, UI.getCurrent().getLocale())+" : "+erreur);
		}
	}
	
	
	/** Supprime un individu
	 * @param individu
	 */
	public void deleteIndividu(Individu individu){
		individuRepository.delete(individu);
	}
	
	/**
	 * @param gest
	 * @param user 
	 * @return le code CGE d'un gestionnaire
	 */
	public String getCodCgeForGestionnaire(Gestionnaire gest, String user){
		if (gest != null && user!=null){
			if (gest.getSiScolCentreGestion()!=null){
				return gest.getSiScolCentreGestion().getCodCge();
			}
			if (gest.getLoginApoGest()!=null && !gest.getLoginApoGest().equals("")){
				return getCodCgeUserByLogin(gest.getLoginApoGest());
			}
			return getCodCgeUserByLogin(user);
		}
		return null;
	}
	
	/** Renvoi le cod cge pour un user
	 * @param userName
	 * @return le cod cge pour un user
	 */
	private String getCodCgeUserByLogin(String userName){
		List<SiScolUtilisateur> listeUser = siScolUtilisateurRepository.findByCodUtiAndTemEnSveUtiAndSiScolCentreGestionIsNotNull(userName, true);
		if (listeUser.size()>0){
			SiScolUtilisateur user = listeUser.get(0);
			if (user!=null && user.getSiScolCentreGestion()!=null){
				return user.getSiScolCentreGestion().getCodCge();
			}
		}
		return null;
	}
}
