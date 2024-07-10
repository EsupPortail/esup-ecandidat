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
package fr.univlorraine.ecandidat.utils.bean.config;

import java.io.Serializable;

import lombok.Data;

/**
 * Objet contenant les infos de configuration auth Pegase
 * @author Kevin Hergalant
 */
@Data
@SuppressWarnings("serial")
public class ConfigEtab implements Serializable {

	public final static String NOM = "nom";
	public final static String CNIL = "cnil";
	public final static String ASSIST_DOC_URL_CAND = "assistDocUrlCand";
	public final static String ASSIST_DOC_URL_CAND_EN = "assistDocUrlCandEn";
	public final static String ASSIST_DOC_URL = "assistDocUrl";
	public final static String ASSIST_HELPDESK_URL = "assistHelpdeskUrl";
	public final static String ASSIST_CONTACT_MAIL = "assistContactMail";
	public final static String ASSIST_CONTACT_URL = "assistContactUrl";

	public final static String ASSIST_URL = "Url";
	public final static String ASSIST_MAIL = "Mail";

	private String nom;
	private String cnil;
	private String assistDocUrlCand;
	private String assistDocUrlCandEn;
	private String assistDocUrl;
	private String assistHelpdeskUrl;
	private String assistContactMail;
	private String assistContactUrl;

	public ConfigEtab() {
		super();
	}
}
