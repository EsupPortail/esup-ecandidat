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
 * Objet contenant les infos d'un candidat pour l'export
 * 
 * @author Kevin Hergalant
 */
@Data
@SuppressWarnings("serial")
public class ExportDossierAvis implements Serializable {

	private String libelle;
	private String type;
	private Integer order;

	public ExportDossierAvis() {
		super();
	}

	public ExportDossierAvis(final String libelle, final String type) {
		this.libelle = libelle;
		this.type = type;
		if (type.equals("FA")) {
			this.order = 1;
		} else if (type.equals("PR")) {
			this.order = 2;
		} else if (type.equals("LA")) {
			this.order = 3;
		} else if (type.equals("LC")) {
			this.order = 4;
		} else if (type.equals("DE")) {
			this.order = 5;
		} else {
			this.order = 6;
		}
	}
}
