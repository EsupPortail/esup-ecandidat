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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfil;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.vaadin.form.RequiredComboBox;

/**
 * Fenêtre de recherche d'individu Ldap
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class DroitProfilGestionnaireWindow extends DroitProfilIndividuWindow {

	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;

	/* Composants */
	private final TextField tfLoginApogee;
	private final RequiredComboBox<SiScolCentreGestion> comboBoxCGE;
	private final CheckBox cbIsAllCommission;
	private final OptionGroup selectCommission;
	private final Panel panelCommissions;
	//private ListSelect0 selectCommission;

	/* Listener */
	private DroitProfilGestionnaireListener droitProfilGestionnaireListener;

	/**
	 * Constructeur de la fenêtre de profil pour gestionnaire
	 * @param gestionnaire
	 */
	public DroitProfilGestionnaireWindow(final Gestionnaire gestionnaire) {
		this(gestionnaire.getCentreCandidature());
		switchToModifMode(gestionnaire);
	}

	/**
	 * Constructeur de la fenêtre de profil pour gestionnaire
	 * @param ctrCand
	 */
	public DroitProfilGestionnaireWindow(final CentreCandidature ctrCand) {
		super(NomenclatureUtils.DROIT_PROFIL_CENTRE_CANDIDATURE);
		setHeight(650, Unit.PIXELS);

		/* Login Apogee pour les gestionnaires */
		tfLoginApogee = new TextField(applicationContext.getMessage("droitprofilind.table.individu.loginApoInd", null, UI.getCurrent().getLocale()), "");
		tfLoginApogee.setNullRepresentation("");
		tfLoginApogee.setMaxLength(20);
		tfLoginApogee.setWidth(100, Unit.PERCENTAGE);
		addOption(tfLoginApogee);

		/* Lise de CGE pour les gestionnaires */
		comboBoxCGE = new RequiredComboBox<>(cacheController.getListeCentreGestion(), SiScolCentreGestion.class);
		comboBoxCGE.setNullSelectionAllowed(true);
		comboBoxCGE.setCaption(applicationContext.getMessage("window.search.people.cge", null, UI.getCurrent().getLocale()));
		comboBoxCGE.setWidth(100, Unit.PERCENTAGE);
		addOption(comboBoxCGE);

		/* CheckBox isAllCommission pour les commissions */
		cbIsAllCommission = new CheckBox(applicationContext.getMessage("droitprofilind.table.individu.isAllComm", null, UI.getCurrent().getLocale()));
		cbIsAllCommission.setImmediate(true);
		addOption(cbIsAllCommission, Alignment.MIDDLE_LEFT, null);

		/* NativeSelect isAllCommission pour les commissions */
		panelCommissions = new Panel("Commissions");
		panelCommissions.setSizeFull();
		final VerticalLayout vlCommissions = new VerticalLayout();
		vlCommissions.setSizeUndefined();
		vlCommissions.setMargin(true);
		selectCommission = new OptionGroup();
		selectCommission.setSizeUndefined();
		selectCommission.setImmediate(true);
		selectCommission.setWidth(100, Unit.PERCENTAGE);
		selectCommission.setMultiSelect(true);
		selectCommission.setItemCaptionPropertyId(ConstanteUtils.GENERIC_LIBELLE);
		selectCommission.setContainerDataSource(new BeanItemContainer<>(Commission.class, ctrCand.getCommissions()));
		vlCommissions.addComponent(selectCommission);
		panelCommissions.setContent(vlCommissions);
		cbIsAllCommission.setValue(false);
		addOption(panelCommissions, Alignment.MIDDLE_RIGHT, 1f);
		setMaxExpendRatio();
		setOptionLayoutWidth(300);

		cbIsAllCommission.addValueChangeListener(e -> {
			selectCommission.setValue(null);
			final Boolean modeAllCommission = cbIsAllCommission.getValue();
			if (modeAllCommission) {
				panelCommissions.setVisible(false);
				setMinExpendRatio();
			} else {
				panelCommissions.setVisible(true);
				setMaxExpendRatio();
			}
		});
	}

	/**
	 * Vérifie les données et si c'est ok, fait l'action du listener
	 */
	@Override
	protected void performAction() {
		if (droitProfilGestionnaireListener != null && checkData()) {
			final Individu individu = getIndividu();
			final DroitProfil droit = getDroitProfil();
			if ((isModificationMode && droit != null) || (!isModificationMode && individu != null && droit != null)) {
				String loginApogee = tfLoginApogee.getValue();
				if (loginApogee != null && loginApogee.equals("")) {
					loginApogee = null;
				}
				final SiScolCentreGestion cge = (SiScolCentreGestion) comboBoxCGE.getValue();
				if (loginApogee != null && !loginApogee.equals("") && cge != null) {
					Notification.show(applicationContext.getMessage("window.search.people.login.cge", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
					return;
				}

				final Boolean isAllCommission = cbIsAllCommission.getValue();
				@SuppressWarnings("unchecked")
				final Set<Commission> setCommission = (Set<Commission>) selectCommission.getValue();
				if (!isAllCommission && setCommission.size() == 0) {
					Notification.show(applicationContext.getMessage("window.search.people.comm.allorone", null, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
					return;
				}

				droitProfilGestionnaireListener.btnOkClick(individu, droit, loginApogee, cge, isAllCommission, setCommission.stream().collect(Collectors.toList()));
				close();
			}
		}
	}

	/**
	 * Passe en mode modif
	 * @param gestionnaire
	 */
	protected void switchToModifMode(final Gestionnaire gestionnaire) {
		super.switchToModifMode(gestionnaire.getDroitProfilInd());
		if (gestionnaire != null) {
			tfLoginApogee.setValue(gestionnaire.getLoginApoGest());
			comboBoxCGE.setValue(gestionnaire.getSiScolCentreGestion());
			cbIsAllCommission.setValue(gestionnaire.getTemAllCommGest());
			selectCommission.setValue(gestionnaire.getCommissions().stream().collect(Collectors.toSet()));
		}
	}

	/**
	 * Défini le 'DroitProfilGestionnaireListener' utilisé
	 * @param droitProfilGestionnaireListener
	 */
	public void addDroitProfilGestionnaireListener(final DroitProfilGestionnaireListener droitProfilGestionnaireListener) {
		this.droitProfilGestionnaireListener = droitProfilGestionnaireListener;
	}

	/**
	 * Interface pour récupérer un click sur Oui
	 */
	public interface DroitProfilGestionnaireListener extends Serializable {

		/**
		 * Appelé lorsque Oui est cliqué.
		 * @param individu
		 * @param droit
		 * @param loginApo
		 * @param centreGestion
		 * @param isAllCommission
		 * @param listCommission
		 */
		void btnOkClick(Individu individu, DroitProfil droit, String loginApo, SiScolCentreGestion centreGestion, Boolean isAllCommission, List<Commission> listCommission);

	}

}
