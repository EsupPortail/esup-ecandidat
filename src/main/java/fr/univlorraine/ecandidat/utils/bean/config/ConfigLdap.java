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
 * Objet contenant les infos de configuration du Ldap
 * @author Kevin Hergalant
 */
@Data
@SuppressWarnings("serial")
public class ConfigLdap implements Serializable {

	public final static String URL = "url";
	public final static String BASE = "base";
	public final static String USER = "user";
	public final static String PWD = "pwd";
	public final static String BRANCHE_PEOPLE = "branchePeople";
	public final static String FILTRE_PERSONNEL = "filtrePersonnel";
	public final static String CHAMPS_UID = "champsUid";
	public final static String CHAMPS_DISPLAYNAME = "champsDisplayName";
	public final static String CHAMPS_MAIL = "champsMail";
	public final static String CHAMPS_SN = "champsSn";
	public final static String CHAMPS_CN = "champsCn";
	public final static String CHAMPS_SUPANNCIVILITE = "champsSupannCivilite";
	public final static String CHAMPS_SUPANNETUID = "champsSupannEtuId";
	public final static String CHAMPS_GIVENNAME = "champsGivenName";

	private String url;
	private String base;
	private String user;
	private String pwd;
	private String branchePeople;
	private String filtrePersonnel;
	private String champsUid;
	private String champsDisplayName;
	private String champsMail;
	private String champsSn;
	private String champsCn;
	private String champsSupannCivilite;
	private String champsSupannEtuId;
	private String champsGivenName;

	public ConfigLdap() {
		super();
	}

}
