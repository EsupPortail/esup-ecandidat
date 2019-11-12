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
package fr.univlorraine.ecandidat.utils.bean.mail;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class pour l'envoie de mail pour les compteMinima
 * @author Kevin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class CandidatMailBean extends MailBean {

	private String civilite;
	private String numDossierOpi;
	private String nomPat;
	private String nomUsu;
	private String prenom;
	private String autrePrenom;
	private String ine;
	private String cleIne;
	private String datNaiss;
	private String libVilleNaiss;
	private String libLangue;
	private String tel;
	private String telPort;

	public CandidatMailBean(final String civilite,
		final String numDossierOpi,
		final String nomPat,
		final String nomUsu,
		final String prenom,
		final String autrePrenom,
		final String ine,
		final String cleIne,
		final String datNaiss,
		final String libVilleNaiss,
		final String libLangue,
		final String tel,
		final String telPort,
		final String idInscription) {
		super();
		this.numDossierOpi = numDossierOpi;
		this.nomPat = nomPat;
		this.nomUsu = nomUsu;
		this.prenom = prenom;
		this.autrePrenom = autrePrenom;
		this.ine = ine;
		this.cleIne = cleIne;
		this.datNaiss = datNaiss;
		this.libVilleNaiss = libVilleNaiss;
		this.libLangue = libLangue;
		this.tel = tel;
		this.telPort = telPort;
	}

	public CandidatMailBean() {
		super();
	}

}
