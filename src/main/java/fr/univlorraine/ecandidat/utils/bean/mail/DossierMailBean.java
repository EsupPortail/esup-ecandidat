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

import java.math.BigDecimal;

import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class pour l'envoie de mail pour le dossier
 * @author Kevin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("serial")
public class DossierMailBean extends MailBean {

	private String dateReception;
	private String montantFraisIns;
	private String complementExo;

	public DossierMailBean(final String dateReception,
		final BigDecimal montantFraisIns,
		final String complementExo) {
		super();
		this.dateReception = dateReception;
		this.montantFraisIns = MethodUtils.parseBigDecimalAsString(montantFraisIns);
		this.complementExo = complementExo;
	}

	public DossierMailBean() {
		super();
	}

}
