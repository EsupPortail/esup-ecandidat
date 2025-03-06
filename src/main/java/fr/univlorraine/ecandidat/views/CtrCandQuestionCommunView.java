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

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.QuestionController;
import fr.univlorraine.ecandidat.controllers.UserController;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.template.QuestionViewTemplate;

/**
 * Page de visu des questions communes du centre de candidature
 *
 * @author Matthieu Manginot
 */
@SuppressWarnings("serial")
@SpringView(name = CtrCandQuestionCommunView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_CTR_CAND)
public class CtrCandQuestionCommunView extends QuestionViewTemplate implements View {

	public static final String NAME = "ctrCandQuestionCommunView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient QuestionController questionController;
	@Resource
	private transient UserController userController;

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		/* Récupération du centre de canidature en cours */
		SecurityCtrCandFonc securityCtrCandFonc = userController
				.getCtrCandFonctionnalite(NomenclatureUtils.FONCTIONNALITE_GEST_QUESTION);
		if (securityCtrCandFonc.hasNoRight()) {
			setSizeFull();
			setMargin(true);
			setSpacing(true);
			addComponent(
					new Label(applicationContext.getMessage("erreurView.title", null, UI.getCurrent().getLocale())));
			return;
		}
		isVisuQuestionCommunMode = false;
		super.init();

		titleParam.setValue(applicationContext.getMessage("question.commun.title", null, UI.getCurrent().getLocale()));

		container.addAll(questionController.getQuestionsCommunScolEnService());
		sortContainer();
		buttonsLayout.setVisible(false);
	}

	/**
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(final ViewChangeEvent event) {
	}

	/**
	 * @see com.vaadin.ui.AbstractComponent#detach()
	 */
	@Override
	public void detach() {
		super.detach();
	}
}
