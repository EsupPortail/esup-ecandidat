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

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 * Objet contenant les infos de configuration auth Pegase
 * @author Kevin Hergalant
 */
@Data
@SuppressWarnings("serial")
public class ConfigPegaseUrl implements Serializable {

	public final static String COC = "coc";
	public final static String COF = "cof";
	public final static String INS = "ins";
	public final static String INS_EXT = "insExt";
	public final static String MOF = "mof";
	public final static String ODF = "odf";
	public final static String REF = "ref";
	public final static String PARAM_TEST_COD_ETU = "paramTestCodEtu";
	public final static String PARAM_TEST_COD_FORMATION = "paramTestCodFormation";

	private String coc;
	private String cof;
	private String ins;
	private String insExt;
	private String mof;
	private String odf;
	private String ref;

	private String paramTestCodEtu;
	private String paramTestCodFormation;

	public ConfigPegaseUrl() {
		super();
	}

	public Boolean isValid() {
		return StringUtils.isNotBlank(getCoc())
			&& StringUtils.isNotBlank(getCof())
			&& StringUtils.isNotBlank(getIns())
			&& StringUtils.isNotBlank(getInsExt())
			&& StringUtils.isNotBlank(getMof())
			//&& StringUtils.isNotBlank(getOdf())
			&& StringUtils.isNotBlank(getRef());
	}
}
