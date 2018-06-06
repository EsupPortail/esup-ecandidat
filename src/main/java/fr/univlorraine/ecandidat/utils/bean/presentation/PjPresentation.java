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

import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.PieceJustif;
import fr.univlorraine.ecandidat.entities.ecandidat.PjCandidat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Ojet de PJ formatté
 *
 * @author Kevin Hergalant */
@Data
@EqualsAndHashCode(of = {"pieceJustif"})
public class PjPresentation implements Serializable {
	/** serialVersionUID **/
	private static final long serialVersionUID = 2189408161277446146L;

	public static String CHAMPS_ORDER = "order";
	public static String CHAMPS_CHECK = "check";
	public static String CHAMPS_ID_PJ = "pieceJustif";
	public static String CHAMPS_LIB_PJ = "libPj";
	public static String CHAMPS_FILE_PJ = "filePj";
	public static String CHAMPS_LIB_STATUT = "libStatut";
	public static String CHAMPS_COMMENTAIRE = "commentaire";
	public static String CHAMPS_CONDITIONNEL = "conditionnel";
	public static String CHAMPS_COMMUNE = "commune";
	public static String CHAMPS_USER_MOD = "userModStatut";

	private Integer order;
	private PieceJustif pieceJustif;
	private Boolean check;
	private String libPj;
	private Fichier filePj;
	private String codStatut;
	private String libStatut;
	private String commentaire;
	private Boolean pJConditionnel;
	private Boolean pJCommune;
	private PjCandidat pjCandidatFromApogee;
	private LocalDateTime datModification;
	private Integer idCandidature;
	private String userModStatut;

	public PjPresentation(final PieceJustif pieceJustif, final String libPj,
			final Fichier filePj, final String codStatut, final String libStatut,
			final String commentaire, final Boolean pJConditionnel, final Boolean pJCommune, final LocalDateTime datModification, final Integer idCandidature, final Integer order,
			final PjCandidat pjCandidatFromApogee, final String userModStatut) {
		super();
		this.pieceJustif = pieceJustif;
		this.libPj = libPj;
		this.filePj = filePj;
		this.codStatut = codStatut;
		this.libStatut = libStatut;
		this.commentaire = commentaire;
		this.check = false;
		this.pJConditionnel = pJConditionnel;
		this.pJCommune = pJCommune;
		this.datModification = datModification;
		this.idCandidature = idCandidature;
		this.order = order;
		this.pjCandidatFromApogee = pjCandidatFromApogee;
		this.userModStatut = userModStatut;
	}

	public PjPresentation() {
		super();
	}

}
