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

/** Class pour l'envoie de mail pôur les compteMinima
 * @author Kevin
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class CandidatMailBean extends MailBean{
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 6927400477810349771L;
	
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
	
	public CandidatMailBean(String civilite, String numDossierOpi, String nomPat, String nomUsu,
			String prenom, String autrePrenom, String ine, String cleIne,
			String datNaiss, String libVilleNaiss, String libLangue,
			String tel, String telPort) {
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
