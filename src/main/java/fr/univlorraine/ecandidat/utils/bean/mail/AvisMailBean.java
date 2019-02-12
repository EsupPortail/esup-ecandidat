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
 * Class pour l'envoie de mail pôur les compteMinima
 * 
 * @author Kevin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class AvisMailBean extends MailBean {

	private String motif;
	private String commentaire;
	private String complementPreselect;
	private String complementAppel;
	private String rang;

	public AvisMailBean(final String motif, final String commentaire, final String complementPreselect, final String complementAppel, final String rang) {
		super();
		this.motif = motif;
		this.commentaire = commentaire;
		this.complementPreselect = complementPreselect;
		this.complementAppel = complementAppel;
		this.rang = rang;
	}
}
