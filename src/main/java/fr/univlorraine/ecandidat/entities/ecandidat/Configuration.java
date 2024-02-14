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

	public final static String COD_CONFIG_ETAB = "ETAB_";
	public final static String COD_CONFIG_ETAB_NOM = COD_CONFIG_ETAB + "NOM";
	public final static String COD_CONFIG_ETAB_CNIL = COD_CONFIG_ETAB + "CNIL";

	public final static String COD_CONFIG_PEGASE_AUTH = "PEGASE_AUTH_";
	public final static String COD_CONFIG_PEGASE_AUTH_URL = COD_CONFIG_PEGASE_AUTH + "URL";
	public final static String COD_CONFIG_PEGASE_AUTH_USER = COD_CONFIG_PEGASE_AUTH + "USER";
	public final static String COD_CONFIG_PEGASE_AUTH_PWD = COD_CONFIG_PEGASE_AUTH + "PWD";
	public final static String COD_CONFIG_PEGASE_AUTH_ETAB = COD_CONFIG_PEGASE_AUTH + "ETAB";

	public static final String COD_CONFIG_PEGASE_URL = "PEGASE_URL_";
	public static final String COD_CONFIG_PEGASE_URL_COC = COD_CONFIG_PEGASE_URL + "COC";
	public static final String COD_CONFIG_PEGASE_URL_COF = COD_CONFIG_PEGASE_URL + "COF";
	public static final String COD_CONFIG_PEGASE_URL_INS = COD_CONFIG_PEGASE_URL + "INS";
	public static final String COD_CONFIG_PEGASE_URL_MOF = COD_CONFIG_PEGASE_URL + "MOF";
	public static final String COD_CONFIG_PEGASE_URL_REF = COD_CONFIG_PEGASE_URL + "REF";
	public static final String COD_CONFIG_PEGASE_URL_ODF = COD_CONFIG_PEGASE_URL + "ODF";
	public static final String COD_CONFIG_PEGASE_URL_INS_EXT = COD_CONFIG_PEGASE_URL + "INS-EXT";

	public static final String COD_CONFIG_PEGASE_URL_PARAM_TEST_COD_ETU = COD_CONFIG_PEGASE_URL + "PARAM_TEST_COD_ETU";
	public static final String COD_CONFIG_PEGASE_URL_PARAM_TEST_COD_FORMATION = COD_CONFIG_PEGASE_URL + "PARAM_TEST_COD_FORMATION";

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
