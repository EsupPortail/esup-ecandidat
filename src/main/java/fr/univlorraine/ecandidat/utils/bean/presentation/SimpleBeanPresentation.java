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

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Classe de presentation d'un parametre
 * @author Kevin Hergalant
 *
 */
@Data @EqualsAndHashCode(of="code")
public class SimpleBeanPresentation implements Serializable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String code;
	private String valeur;
	
	public SimpleBeanPresentation() {
		super();
	}
	
	public SimpleBeanPresentation(String code, String valeur) {
		super();
		this.code = code;
		this.valeur = valeur;
	}
	
	public SimpleBeanPresentation(String code) {
		super();
		this.code = code;
	}

	/**
	 * @return le libellé à afficher dans la listBox
	 */
	public String getGenericLibelle(){
		return this.valeur;
	}
}