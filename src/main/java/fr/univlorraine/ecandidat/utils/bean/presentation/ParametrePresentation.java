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

import javax.validation.constraints.NotNull;

import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Classe de presentation d'un parametre
 *
 * @author Kevin Hergalant
 */
@Data
@EqualsAndHashCode(of = "codParam")
@SuppressWarnings("serial")
public class ParametrePresentation implements Serializable {

	public static String VAL_PARAM_STRING = "valParamString";
	public static String VAL_PARAM_BOOLEAN = "valParamBoolean";
	public static String VAL_PARAM_INTEGER = "valParamInteger";

	@NotNull
	private String codParam;

	@NotNull
	private String libParam;

	@NotNull
	private Boolean temScol;

	private String regexParam;

	@NotNull
	private Integer valParamInteger;

	@NotNull
	private String valParamBoolean;

	private String valParamString;

	public ParametrePresentation() {
	}

	/**
	 * Créé un parametre de presentation pour un parametre
	 *
	 * @param parametre
	 */
	public ParametrePresentation(final Parametre parametre) {
		this.codParam = parametre.getCodParam();
		this.libParam = parametre.getLibParam();
		this.temScol = parametre.getTemScol();
		this.regexParam = parametre.getRegexParam();
		if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_BOOLEAN)) {
			this.valParamBoolean = parametre.getValParam();
		} else if (parametre.getTypParam().equals(NomenclatureUtils.TYP_PARAM_INTEGER)) {
			this.valParamInteger = Integer.valueOf(parametre.getValParam());
		} else if (parametre.getTypParam().startsWith(NomenclatureUtils.TYP_PARAM_STRING)) {
			this.valParamString = parametre.getValParam();
		}
	}
}
