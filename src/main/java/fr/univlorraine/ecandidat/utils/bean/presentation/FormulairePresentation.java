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

import fr.univlorraine.ecandidat.entities.ecandidat.Formulaire;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** Ojet de formulaire formatté
 *
 * @author Kevin Hergalant */
@Data
@EqualsAndHashCode(of = {"formulaire"})
public class FormulairePresentation implements Serializable {

	/** serialVersionUID **/
	private static final long serialVersionUID = 3067467095838475483L;

	public static String CHAMPS_ID_FORM = "formulaire";
	public static String CHAMPS_LIB = "libFormulaire";
	public static String CHAMPS_URL = "urlFormulaire";
	public static String CHAMPS_LIB_STATUT = "libStatut";
	public static String CHAMPS_CONDITIONNEL = "conditionnel";
	public static String CHAMPS_REPONSES = "reponses";
	public static String CHAMPS_ACTION_RELANCE = "relance";

	private Formulaire formulaire;
	private String libFormulaire;
	private String urlFormulaire;
	private String codStatut;
	private String libStatut;
	private Boolean conditionnel;
	private String reponses;

	public FormulairePresentation() {
		super();
	}

	public FormulairePresentation(final Formulaire formulaire, final String libFormulaire, final String urlFormulaire,
			final String codStatut, final String libStatut, final Boolean conditionnel, final String reponses) {
		super();
		this.formulaire = formulaire;
		this.libFormulaire = libFormulaire;
		this.urlFormulaire = urlFormulaire;
		this.codStatut = codStatut;
		this.libStatut = libStatut;
		this.conditionnel = conditionnel;
		this.reponses = reponses;
	}

}
