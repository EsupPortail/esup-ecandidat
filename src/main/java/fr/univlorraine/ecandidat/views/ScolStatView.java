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
package fr.univlorraine.ecandidat.views;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.StatController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.StatFormationPresentation;
import fr.univlorraine.ecandidat.views.template.StatViewTemplate;

/**
 * Page de gestion des parametres du centre de candidature
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@SpringView(name = ScolStatView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolStatView extends StatViewTemplate implements View {

	public static final String NAME = "scolStatView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient StatController statController;
	@Resource
	private transient UserController userController;

	/**
	 * Initialise la vue
	 */
	@PostConstruct
	public void init() {
		String libScolCentrale = applicationContext.getMessage("scolcentrale.mainmenu", null,
				UI.getCurrent().getLocale());
		String title = applicationContext.getMessage(NAME + ".title", null, UI.getCurrent().getLocale()) + " - "
				+ libScolCentrale;
		super.init(title, "SCOL", libScolCentrale,
				applicationContext.getMessage("stat.libHs.ctrCand", null, UI.getCurrent().getLocale()));
		removeColonnes(StatFormationPresentation.CHAMPS_LIB_SUPP, StatFormationPresentation.CHAMPS_CAPACITE_ACCUEIL);
		/* Mise a jour du container */
		majContainer();
	}

	/**
	 * Met a jour le container
	 */
	@Override
	protected void majContainer() {
		super.majContainer(statController.getStatCtrCand(getCampagne(), getDisplayHs()));
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
	}
}
