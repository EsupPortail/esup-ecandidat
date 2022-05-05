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
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.I18n;
import fr.univlorraine.ecandidat.entities.ecandidat.Question;
import fr.univlorraine.ecandidat.entities.ecandidat.QuestionCand;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.repositories.QuestionCandRepository;
import fr.univlorraine.ecandidat.repositories.QuestionRepository;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;
import fr.univlorraine.ecandidat.views.windows.QuestionWindow;

/**
 * Gestion de l'entité question
 *
 * @author Matthieu Manginot
 */
@Component
public class QuestionController {
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
	private transient QuestionRepository questionRepository;
	@Resource
	private transient QuestionCandRepository questionCandRepository;

	/** @return liste des questions */
	public List<Question> getQuestions() {
		return questionRepository.findAll();
	}

	/**
	 * @param cand
	 * @return la liste des Questions à afficher pour une candidature
	 *         Toutes les communes de la scol + toutes les communes du ctr + toutes les questions de la formation + les questions effacées
	 */
	public List<Question> getQuestionForCandidature(final Candidature cand, final Boolean addDeletedQuestion) {
		Formation formation = cand.getFormation();
		List<Question> liste = new ArrayList<>();

		// On ajoute les Questions communes de la scole centrale-->déjà trié
		liste.addAll(getQuestionsByCtrCandEnService(null, true));

		// On ajoute les PJ communes du centre de candidature-->déjà trié
		liste.addAll(getQuestionsByCtrCandEnService(formation.getCommission().getCentreCandidature().getIdCtrCand(), true));

		// On ajoute les Questions distinctes de la formation
		List<Question> listeFormation = formation.getQuestions().stream().filter(e -> e.getTesQuestion()).collect(Collectors.toList());
		//Collections.sort(listeFormation);
		liste.addAll(listeFormation);

		// On ajoute les Questions qui seraient repassé hors service mais déjà renseignées par le candidat
		if (addDeletedQuestion) {
			List<Question> listeQuestionCand = new ArrayList<>();
			cand.getQuestionCandidatures().forEach(e -> {
				listeQuestionCand.add(e.getQuestion());
			});
			//Collections.sort(listeQuestionCand);
			liste.addAll(listeQuestionCand);
		}

		// on fait un distinct sur le tout
		return liste.stream().distinct().collect(Collectors.toList());
	}

	/** @return la liste complete des Questions */
	public List<Question> getAllQuestions() {
		return questionRepository.findAll();
	}

	/**
	 * @param idCtrCand
	 * @return a liste des Questions d'un ctr
	 */
	public List<Question> getQuestionsByCtrCand(final Integer idCtrCand) {
		return questionRepository.findByCentreCandidatureIdCtrCand(idCtrCand);
	}

	/**
	 * @param idCtrCand
	 * @return la liste des Questions en service d'un ctr
	 */
	public List<Question> getQuestionsByCtrCandEnService(final Integer idCtrCand, final Boolean commun) {
		List<Question> liste = questionRepository.findByCentreCandidatureIdCtrCandAndTesQuestionAndTemCommunQuestion(idCtrCand, true, commun);
		//Collections.sort(liste);
		return liste;
	}

	/** @return la liste des Questions communes de la scol */
	public List<Question> getQuestionsCommunScolEnService() {
		return questionRepository.findByCentreCandidatureIdCtrCandAndTesQuestionAndTemCommunQuestion(null, true, true);
	}

	/** @return la liste des Questions communes de la scol */
	public List<Question> getQuestionsCommunCtrCandEnService(final Integer idCtrCand) {
		List<Question> liste = new ArrayList<>();
		liste.addAll(questionRepository.findByCentreCandidatureIdCtrCandAndTesQuestionAndTemCommunQuestion(null, true, true));
		liste.addAll(questionRepository.findByCentreCandidatureIdCtrCandAndTesQuestionAndTemCommunQuestion(idCtrCand, true, true));
		return liste;
	}

	/**
	 * Renvoie la liste des Questions pour un ctrCand +
	 * scol
	 *
	 * @param idCtrCand
	 * @return la liste des Questions
	 */
	public List<Question> getQuestionsByCtrCandAndScolCentral(final Integer idCtrCand) {
		List<Question> liste = new ArrayList<>();
		liste.addAll(getQuestionsByCtrCandEnService(null, false));
		liste.addAll(getQuestionsByCtrCandEnService(idCtrCand, false));
		return liste;
	}

	/**
	 * Ouvre une fenêtre d'édition d'une nouvelle Question.
	 *
	 * @param ctrCand
	 */
	public void editNewQuestion(final CentreCandidature ctrCand) {
		Question question = new Question(userController.getCurrentUserLogin());
		question.setI18nLibQuestion(new I18n(i18nController.getTypeTraduction(NomenclatureUtils.TYP_TRAD_QUESTION_LIB)));
		question.setCentreCandidature(ctrCand);
		UI.getCurrent().addWindow(new QuestionWindow(question));
	}

	/**
	 * Ouvre une fenêtre d'édition de Question.
	 *
	 * @param Question
	 */
	public void editQuestion(final Question question) {
		Assert.notNull(question, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (!lockController.getLockOrNotify(question, null)) {
			return;
		}
		QuestionWindow window = new QuestionWindow(question);
		window.addCloseListener(e -> lockController.releaseLock(question));
		UI.getCurrent().addWindow(window);
	}

	/**
	 * Enregistre une Question
	 *
	 * @param Question
	 */
	public void saveQuestion(Question question) {
		Assert.notNull(question, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verrou */
		if (question.getIdQuestion() != null && !lockController.getLockOrNotify(question, null)) {
			return;
		}
		question.setUserModQuestion(userController.getCurrentUserLogin());
		question.setI18nLibQuestion(i18nController.saveI18n(question.getI18nLibQuestion()));
		question = questionRepository.saveAndFlush(question);

		lockController.releaseLock(question);
	}

	/**
	 * Supprime une Question
	 *
	 * @param Question
	 */
	public void deleteQuestion(final Question question) {
		Assert.notNull(question, applicationContext.getMessage("assert.notNull", null, UI.getCurrent().getLocale()));

		/* Verification que la Question n'est rattachée à rien */
		if (questionCandRepository.countByQuestion(question) > 0) {
			Notification.show(applicationContext.getMessage("question.error.delete", new Object[] {QuestionCand.class.getSimpleName()}, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		/* Verrou */
		if (!lockController.getLockOrNotify(question, null)) {
			return;
		}

		ConfirmWindow confirmWindow = new ConfirmWindow(applicationContext.getMessage("question.window.confirmDelete", new Object[] {
				question.getCodQuestion()}, UI.getCurrent().getLocale()), applicationContext.getMessage("question.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
		confirmWindow.addBtnOuiListener(e -> {
			/* On vérifie que la PJ est utilisée par des formation ou est commune, dans ce cas-->2eme confirmation */
			String confirm = null;
			if (questionRepository.findOne(question.getIdQuestion()).getFormations().size() > 0) {
				confirm = applicationContext.getMessage("question.window.confirmDelete.form", null, UI.getCurrent().getLocale());
			} else if (question.getTemCommunQuestion()) {
				confirm = applicationContext.getMessage("question.window.confirmDelete.commun", null, UI.getCurrent().getLocale());
			}

			if (confirm == null) {
				deleteQuestionDB(question);
			} else {
				/* Verrou */
				if (!lockController.getLockOrNotify(question, null)) {
					return;
				}
				ConfirmWindow confirmWindowQuestionUse = new ConfirmWindow(confirm, applicationContext.getMessage("pieceJustif.window.confirmDeleteTitle", null, UI.getCurrent().getLocale()));
				confirmWindowQuestionUse.addBtnOuiListener(y -> {
					deleteQuestionDB(question);
				});
				confirmWindowQuestionUse.addCloseListener(y -> {
					/* Suppression du lock */
					lockController.releaseLock(question);
				});
				UI.getCurrent().addWindow(confirmWindowQuestionUse);
			}
		});
		confirmWindow.addCloseListener(e -> {
			/* Suppression du lock */
			lockController.releaseLock(question);
		});
		UI.getCurrent().addWindow(confirmWindow);
	}

	/**
	 * Supprime une Question
	 *
	 * @param Question
	 */
	private void deleteQuestionDB(final Question question) {
		/* Contrôle que le client courant possède toujours le lock */
		if (lockController.getLockOrNotify(question, null)) {
			questionRepository.delete(question);
		}
	}

	/**
	 * Verifie l'unicité du code
	 *
	 * @param cod
	 * @param id
	 * @return true si le code est unique
	 */
	public Boolean isCodQuestionUnique(final String cod, final Integer id) {
		Question question = questionRepository.findByCodQuestion(cod);
		if (question == null) {
			return true;
		} else {
			if (question.getIdQuestion().equals(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retourne le type de traitement ALL
	 */
	public TypeTraitement getTypeTraitAll() {
		return new TypeTraitement(NomenclatureUtils.TYP_TRAIT_ALL, applicationContext.getMessage("typeTraitement.lib.all", null, UI.getCurrent().getLocale()));
	}
}
