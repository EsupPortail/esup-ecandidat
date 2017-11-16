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

import com.vaadin.server.FontAwesome;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Classe de presentation d'un boolean
 * @author Kevin Hergalant
 *
 */
@Data @EqualsAndHashCode(of="valeur")
public class BooleanPresentation implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	public enum BooleanValue { ALL, TRUE, FALSE, NULL};
	
	public static String CHAMPS_VALEUR = "valeur";
	public static String CHAMPS_LIBELLE = "libelle";
	public static String CHAMPS_ICONE = "icone";

	private BooleanValue valeur;
	private String libelle;
	private FontAwesome icone;
	
	public BooleanPresentation() {
		super();
	}
	
	public BooleanPresentation(BooleanValue valeur, String libelle, FontAwesome icone) {
		super();
		this.valeur = valeur;
		this.libelle = libelle;
		this.icone = icone;
	}

	public BooleanPresentation(String libelle) {
		super();
		this.libelle = libelle;
	}
}