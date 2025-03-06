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
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.QuestionController;
import fr.univlorraine.ecandidat.entities.ecandidat.Question;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.views.template.QuestionViewTemplate;
import fr.univlorraine.tools.vaadin.EntityPushListener;
import fr.univlorraine.tools.vaadin.EntityPusher;

/**
 * Page de gestion des questions par la scolarité
 *
 * @author Matthieu Manginot
 */
@SuppressWarnings("serial")
@SpringView(name = ScolQuestionView.NAME)
@PreAuthorize(ConstanteUtils.PRE_AUTH_SCOL_CENTRALE)
public class ScolQuestionView extends QuestionViewTemplate implements View, EntityPushListener<Question> {

	public static final String NAME = "scolQuestionView";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient QuestionController questionController;
	@Resource
	private transient EntityPusher<Question> questionEntityPusher;

	/**
	 * Initialise la vue
	 */
	@Override
	@PostConstruct
	public void init() {
		super.init();

		titleParam.setValue(applicationContext.getMessage("question.title", null, UI.getCurrent().getLocale()));

		btnNew.addClickListener(e -> {
			questionController.editNewQuestion(null);
		});

		container.addAll(questionController.getQuestionsByCtrCand(null));
		sortContainer();

		questionTable.addItemClickListener(e -> {
			if (e.isDoubleClick()) {
				questionTable.select(e.getItemId());
				btnEdit.click();
			}
		});

		/* Inscrit la vue aux mises à jour de Question */
		questionEntityPusher.registerEntityPushListener(this);
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
		/* Désinscrit la vue des mises à jour de Question */
		questionEntityPusher.unregisterEntityPushListener(this);
		super.detach();
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityPersisted(java.lang.Object)
	 */
	@Override
	public void entityPersisted(final Question entity) {
		if (entity.getCentreCandidature() == null) {
			questionTable.removeItem(entity);
			questionTable.addItem(entity);
			// questionTable.sort();
			sortContainer();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityUpdated(java.lang.Object)
	 */
	@Override
	public void entityUpdated(final Question entity) {
		if (entity.getCentreCandidature() == null) {
			questionTable.removeItem(entity);
			questionTable.addItem(entity);
			// questionTable.sort();
			sortContainer();
		}
	}

	/**
	 * @see fr.univlorraine.tools.vaadin.EntityPushListener#entityDeleted(java.lang.Object)
	 */
	@Override
	public void entityDeleted(final Question entity) {
		if (entity.getCentreCandidature() == null) {
			questionTable.removeItem(entity);
		}
	}
}
