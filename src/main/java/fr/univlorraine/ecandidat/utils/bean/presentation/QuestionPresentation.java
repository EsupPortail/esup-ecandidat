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
package fr.univlorraine.ecandidat.utils.bean.presentation;

import java.io.Serializable;
import java.time.LocalDateTime;

import fr.univlorraine.ecandidat.entities.ecandidat.Question;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Objet de Question formatté
 *
 * @author Kevin Hergalant
 */
@Data
@EqualsAndHashCode(of = { "question" })
@SuppressWarnings("serial")
public class QuestionPresentation implements Serializable {

	public static String CHAMPS_ORDER = "order";
	public static String CHAMPS_CHECK = "check";
	public static String CHAMPS_ID_QUESTION = "question";
	public static String CHAMPS_LIB_QUESTION = "libQuestion";
	public static String CHAMPS_LIB_STATUT = "libStatut";
	public static String CHAMPS_REPONSE = "reponse";
	public static String CHAMPS_CONDITIONNEL = "conditionnel";
	public static String CHAMPS_COMMUNE = "commune";
	public static String CHAMPS_USER_MOD = "userModStatut";

	private Integer order;
	private Question question;
	private Boolean check;
	private String libQuestion;
	private String codStatut;
	private String libStatut;
	private String reponse;
	private Boolean questionConditionnel;
	private Boolean questionCommune;
	private LocalDateTime datModification;
	private Integer idCandidature;
	private String userModStatut;

	public QuestionPresentation(final Question question, final String libQuestion, final String codStatut,
			final String libStatut, final String reponse, final Boolean questionConditionnel,
			final Boolean questionCommune, final LocalDateTime datModification, final Integer idCandidature,
			final Integer order, final String userModStatut) {
		super();
		this.question = question;
		this.libQuestion = libQuestion;
		this.codStatut = codStatut;
		this.libStatut = libStatut;
		this.reponse = reponse;
		this.check = false;
		this.questionConditionnel = questionConditionnel;
		this.questionCommune = questionCommune;
		this.datModification = datModification;
		this.idCandidature = idCandidature;
		this.order = order;
		this.userModStatut = userModStatut;
	}

	public QuestionPresentation() {
		super();
	}

}
