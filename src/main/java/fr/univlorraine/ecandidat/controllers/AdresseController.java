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

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;

/**
 * Gestion de l'entité campagne
 * @author Kevin Hergalant
 *
 */
@Component
public class AdresseController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient I18nController i18nController;

	/**
	 * @param adresse
	 * @param delimiter
	 * @return le libelle d'une adresse
	 */
	public String getLibelleAdresse(Adresse adresse,String delimiter){
		String libAdr = "";
		if (adresse != null){
			if (adresse.getAdr1Adr()!=null){
				libAdr = libAdr + adresse.getAdr1Adr()+delimiter;
			}
			if (adresse.getAdr2Adr()!=null){
				libAdr = libAdr + adresse.getAdr2Adr()+delimiter;
			}
			if (adresse.getAdr3Adr()!=null){
				libAdr = libAdr + adresse.getAdr3Adr()+delimiter;
			}
			if (adresse.getCodBdiAdr()!=null && adresse.getCedexAdr()!=null && adresse.getSiScolCommune()!=null && adresse.getSiScolCommune().getLibCom()!=null){
				libAdr = libAdr + adresse.getCodBdiAdr()+" "+adresse.getSiScolCommune().getLibCom()+" "+ adresse.getCedexAdr()+delimiter;
			}else if (adresse.getCodBdiAdr()!=null && adresse.getSiScolCommune()!=null && adresse.getSiScolCommune().getLibCom()!=null){
				libAdr = libAdr + adresse.getCodBdiAdr()+" "+adresse.getSiScolCommune().getLibCom()+delimiter;
			}else{
				if (adresse.getCodBdiAdr()!=null){
					libAdr = libAdr + adresse.getCodBdiAdr()+delimiter;
				}
				if (adresse.getSiScolCommune()!=null && adresse.getSiScolCommune().getLibCom()!=null){
					libAdr = libAdr + adresse.getSiScolCommune().getLibCom()+delimiter;
				}
			}
			if (adresse.getLibComEtrAdr()!=null){
				libAdr = libAdr + adresse.getLibComEtrAdr()+delimiter;
			}
			if (adresse.getSiScolPays()!=null && !adresse.getSiScolPays().equals(cacheController.getPaysFrance())){
				libAdr = libAdr + adresse.getSiScolPays().getLibPay()+delimiter;
			}
		}
		if (libAdr!=null && !libAdr.equals("")){
			if (libAdr.substring(libAdr.length()-delimiter.length(), libAdr.length()).equals(delimiter)){
				libAdr = libAdr.substring(0, libAdr.length()-delimiter.length());
			}
		}
		return libAdr;
	}
	
	/**
	 * @param commission
	 * @param delimiter
	 * @return le libelle de l'adresse de la commission
	 */
	public String getLibelleAdresseCommission(Commission commission, String delimiter){
		String libAdr = getLibelleAdresse(commission.getAdresse(),delimiter);
		Boolean addDelimiter = false;
		if (commission.getTelComm()!=null){
			addDelimiter = true;
			libAdr = libAdr + delimiter + applicationContext.getMessage("candidature.adresse.tel", new Object[]{commission.getTelComm()}, UI.getCurrent().getLocale())+delimiter;
		}
		if (commission.getMailComm()!=null){
			if (!addDelimiter){
				addDelimiter = true;
				libAdr = libAdr + delimiter;
			}
			libAdr = libAdr + applicationContext.getMessage("candidature.adresse.mail", new Object[]{commission.getMailComm()}, UI.getCurrent().getLocale())+delimiter;
		}
		if (commission.getFaxComm()!=null){
			if (!addDelimiter){
				addDelimiter = true;
				libAdr = libAdr + delimiter;
			}
			libAdr = libAdr + applicationContext.getMessage("candidature.adresse.fax", new Object[]{commission.getFaxComm()}, UI.getCurrent().getLocale())+delimiter;
		}
		String commentRetour = i18nController.getI18nTraduction(commission.getI18nCommentRetourComm());
		if (commentRetour!=null && !commentRetour.equals("")){
			if (!addDelimiter){
				addDelimiter = true;
				libAdr = libAdr + delimiter;
			}
			libAdr = libAdr +delimiter+ commentRetour;
		}
		
		return libAdr;
	}
}
