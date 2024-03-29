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

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.CommissionMembre;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;

/**
 * Fenêtre de recherche d'individu Ldap
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class DroitProfilMembreCommWindow extends DroitProfilIndividuWindow {

	@Resource
	private transient ApplicationContext applicationContext;

	/* Composants */
	private final CheckBox cbIsPresident;
	private final TextField tfCommentaire;

	/* Listener */
	private DroitProfilIndCommListener droitProfilIndCommListener;

	/**
	 * Constructeur de la fenêtre de profil pour membre de commission
	 */
	public DroitProfilMembreCommWindow() {
		super(NomenclatureUtils.DROIT_PROFIL_COMMISSION);

		/* CheckBox idPresident pour les commissions */
		cbIsPresident = new CheckBox(applicationContext.getMessage("droitprofilind.table.individu.isPres", null, UI.getCurrent().getLocale()));
		cbIsPresident.setValue(false);
		addOption(cbIsPresident);

		/* Commentaire */
		tfCommentaire = new TextField(applicationContext.getMessage("droitprofilind.table.individu.commentaire", null, UI.getCurrent().getLocale()), "");
		tfCommentaire.setNullRepresentation("");
		tfCommentaire.setMaxLength(500);
		tfCommentaire.setWidth(100, Unit.PERCENTAGE);
		addOption(tfCommentaire);

		setMinExpendRatio();
		setOptionLayoutWidth(200);
	}

	public DroitProfilMembreCommWindow(final CommissionMembre membre) {
		this();
		switchToModifMode(membre.getDroitProfilInd(), membre.getTemIsPresident(), membre.getCommentaire());
	}

	/**
	 * Vérifie les données et si c'est ok, fait l'action du listener
	 */
	@Override
	protected void performAction() {
		if (droitProfilIndCommListener != null && checkData()) {
			final Individu individu = getIndividu();
			final DroitProfil droit = getDroitProfil();
			if ((isModificationMode && droit != null) || (!isModificationMode && individu != null && droit != null)) {
				/* Commentaire */
				String comment = tfCommentaire.getValue();
				if (StringUtils.isBlank(comment)) {
					comment = null;
				}
				droitProfilIndCommListener.btnOkClick(individu, droit, cbIsPresident.getValue(), comment);
				close();
			}
		}
	}

	protected void switchToModifMode(final DroitProfilInd droitProfilInd, final Boolean isPres, final String commentaire) {
		super.switchToModifMode(droitProfilInd);
		setHeight(330, Unit.PIXELS);
		if (droitProfilInd.getCommissionMembre() != null) {
			cbIsPresident.setValue(isPres);
			tfCommentaire.setValue(commentaire);
		}
	}

	/**
	 * Défini le 'DroitProfilIndCommListener' utilisé
	 * @param droitProfilIndCommListener
	 */
	public void addDroitProfilIndCommListener(final DroitProfilIndCommListener droitProfilIndCommListener) {
		this.droitProfilIndCommListener = droitProfilIndCommListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui
	 */
	public interface DroitProfilIndCommListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param individu
		 * @param droit
		 * @param isPresident
		 */
		void btnOkClick(Individu individu, DroitProfil droit, Boolean isPresident, String commentaire);

	}

}
