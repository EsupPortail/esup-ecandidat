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
package fr.univlorraine.ecandidat.services.security;

import java.io.Serializable;

import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * La classe de fonctionnalité de la commission d'un user
 * 
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = false)
public class SecurityCommissionFonc extends SecurityFonc implements Serializable {

	private Commission commission;

	public SecurityCommissionFonc(final Commission commission, final Droit droit) {
		super(droit);
		this.commission = commission;
	}

	public SecurityCommissionFonc(final Droit droit) {
		super(droit);
	}
}
