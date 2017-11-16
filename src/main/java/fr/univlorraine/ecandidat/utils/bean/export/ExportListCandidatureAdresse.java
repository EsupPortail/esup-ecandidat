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
package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import lombok.Data;

/** 
 * Ojet servant a l'option d'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportListCandidatureAdresse implements Serializable {
	/**serialVersionUID**/
	private static final long serialVersionUID = 5495811611661986200L;

	private String adr1;
	private String adr2;
	private String adr3;
	private String codBdi;
	private String libComEtr;
	private String libCommune;
	private String libPays;
	private String libelle;
	
	public ExportListCandidatureAdresse() {
		super();
		this.adr1 = "";
		this.adr2 = "";
		this.adr3 = "";
		this.codBdi = "";
		this.libComEtr = "";
		this.libCommune = "";
		this.libPays = "";
		this.libelle = "";
	}
	
	
}
