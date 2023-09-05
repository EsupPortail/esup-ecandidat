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
package fr.univlorraine.ecandidat.entities.ecandidat;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the configuration database table.
 */
@Entity
@Table(name = "configuration")
@Data
@EqualsAndHashCode(of = "codConfig")
@SuppressWarnings("serial")
public class Configuration implements Serializable {

	public final static String COD_CONFIG_LDAP = "LDAP_";
	public final static String COD_CONFIG_LDAP_URL = "LDAP_URL";
	public final static String COD_CONFIG_LDAP_BASE = "LDAP_BASE";
	public final static String COD_CONFIG_LDAP_USER = "LDAP_USER";
	public final static String COD_CONFIG_LDAP_PWD = "LDAP_PWD";
	public final static String COD_CONFIG_LDAP_BRANCHE_PEOPLE = "LDAP_BRANCHE_PEOPLE";
	public final static String COD_CONFIG_LDAP_FILTRE_PERSONNEL = "LDAP_FILTRE_PERSONNEL";
	public final static String COD_CONFIG_LDAP_CHAMPS_UID = "LDAP_CHAMPS_UID";
	public final static String COD_CONFIG_LDAP_CHAMPS_DISPLAYNAME = "LDAP_CHAMPS_DISPLAYNAME";
	public final static String COD_CONFIG_LDAP_CHAMPS_MAIL = "LDAP_CHAMPS_MAIL";
	public final static String COD_CONFIG_LDAP_CHAMPS_SN = "LDAP_CHAMPS_SN";
	public final static String COD_CONFIG_LDAP_CHAMPS_CN = "LDAP_CHAMPS_CN";
	public final static String COD_CONFIG_LDAP_CHAMPS_SUPANN_CIVILITE = "LDAP_CHAMPS_SUPANN_CIVILITE";
	public final static String COD_CONFIG_LDAP_CHAMPS_SUPANN_ETU_ID = "LDAP_CHAMPS_SUPANN_ETU_ID";
	public final static String COD_CONFIG_LDAP_CHAMPS_GIVEN_NAME = "LDAP_CHAMPS_GIVENNAME";

	public final static String COD_CONFIG_PEGASE_AUTH = "PEGASE_AUTH_";
	public final static String COD_CONFIG_PEGASE_AUTH_URL = "PEGASE_AUTH_URL";
	public final static String COD_CONFIG_PEGASE_AUTH_USER = "PEGASE_AUTH_USER";
	public final static String COD_CONFIG_PEGASE_AUTH_PWD = "PEGASE_AUTH_PWD";

	public static final String COD_CONFIG_PEGASE_URL = "PEGASE_URL_";
	public static final String COD_CONFIG_PEGASE_URL_COC = "PEGASE_URL_COC";
	public static final String COD_CONFIG_PEGASE_URL_COF = "PEGASE_URL_COF";
	public static final String COD_CONFIG_PEGASE_URL_INS = "PEGASE_URL_INS";
	public static final String COD_CONFIG_PEGASE_URL_MOF = "PEGASE_URL_MOF";
	public static final String COD_CONFIG_PEGASE_URL_REF = "PEGASE_URL_REF";

	@Id
	@Column(name = "cod_config")
	private String codConfig;

	@Column(name = "val_config")
	private String valConfig;

	public Configuration() {
		super();
	}

	public Configuration(final String codConfig, final String valConfig) {
		super();
		this.codConfig = codConfig;
		this.valConfig = valConfig;
	}
}
