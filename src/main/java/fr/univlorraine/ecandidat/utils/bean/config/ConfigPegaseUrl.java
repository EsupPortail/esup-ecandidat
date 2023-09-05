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
public class ConfigPegaseUrl implements Serializable {

	public static final String COD_CONFIG_PEGASE_URL = "PEGASE_URL_";
	public static final String COD_CONFIG_PEGASE_URL_COC = "PEGASE_URL_COC";
	public static final String COD_CONFIG_PEGASE_URL_COF = "PEGASE_URL_COF";
	public static final String COD_CONFIG_PEGASE_URL_INS = "PEGASE_URL_INS";
	public static final String COD_CONFIG_PEGASE_URL_MOF = "PEGASE_URL_MOF";
	public static final String COD_CONFIG_PEGASE_URL_REF = "PEGASE_URL_REF";

	public final static String COC = "coc";
	public final static String COF = "cof";
	public final static String INS = "ins";
	public final static String MOF = "mof";
	public final static String REF = "ref";

	private String coc;
	private String cof;
	private String ins;
	private String mof;
	private String ref;

	public ConfigPegaseUrl() {
		super();
	}

}
