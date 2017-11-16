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
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.ui.CheckBox;

import fr.univlorraine.ecandidat.entities.ecandidat.CommissionMembre;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;

/**
 * Fenêtre de recherche d'individu Ldap
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class DroitProfilMembreCommWindow extends DroitProfilIndividuWindow {

	
	/** serialVersionUID **/
	private static final long serialVersionUID = 927755644223279264L;
	
	@Resource
	private transient ApplicationContext applicationContext;

	/* Composants */
	private CheckBox cbIsPresident;

	/*Listener*/
	private DroitProfilIndCommListener droitProfilIndCommListener;


	/**
	 * Constructeur de la fenêtre de profil pour membre de commission
	 */
	public DroitProfilMembreCommWindow() {
		super(NomenclatureUtils.DROIT_PROFIL_COMMISSION);
		
		/*CheckBox idPresident pour les commissions*/
		cbIsPresident = new CheckBox(applicationContext.getMessage("droitprofilind.table.individu.isPres", null, Locale.getDefault()));
		cbIsPresident.setValue(false);
		addOption(cbIsPresident);
		setMinExpendRatio();
		setOptionLayoutWidth(150);
	}
	
	public DroitProfilMembreCommWindow(CommissionMembre membre) {
		this();
		switchToModifMode(membre.getDroitProfilInd(),membre.getTemIsPresident());
	}

	/**
	 * Vérifie les données et si c'est ok, fait l'action du listener
	 */
	@Override
	protected void performAction(){
		if (droitProfilIndCommListener != null && checkData()){
			Individu individu = getIndividu();
			DroitProfil droit = getDroitProfil();
			if ((isModificationMode &&  droit!=null) || (!isModificationMode && individu!=null && droit!=null)){
				droitProfilIndCommListener.btnOkClick(individu,droit,cbIsPresident.getValue());
				close();
			}				
		}
	}
	
	
	protected void switchToModifMode(DroitProfilInd droitProfilInd, Boolean isPres) {
		super.switchToModifMode(droitProfilInd);
		setHeight(280, Unit.PIXELS);
		if (droitProfilInd.getCommissionMembre()!=null){
			cbIsPresident.setValue(isPres);
		}
	}

	/**
	 * Défini le 'DroitProfilIndCommListener' utilisé
	 * @param droitProfilIndCommListener
	 */
	public void addDroitProfilIndCommListener(DroitProfilIndCommListener droitProfilIndCommListener) {
		this.droitProfilIndCommListener = droitProfilIndCommListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui
	 */
	public interface DroitProfilIndCommListener extends Serializable {
		
		/** Appelé lorsque Oui est cliqué.		
		 * @param individu
		 * @param droit
		 * @param isPresident
		 */
		public void btnOkClick(Individu individu, DroitProfil droit, Boolean isPresident);

	}

}
