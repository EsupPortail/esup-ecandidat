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

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import fr.univlorraine.ecandidat.entities.ecandidat.PreferenceInd;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * La classe utilisateur gestionnaire de l'application
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityUserGestionnaire extends SecurityUser {

	private SecurityCentreCandidature centreCandidature;
	private SecurityCommission commission;
	private String displayNameCandidat;
	private String noDossierCandidat;

	/* Les preferences de l'individu */
	private PreferenceInd preferenceInd;

	public SecurityUserGestionnaire(final String username, final String displayName, final Collection<? extends GrantedAuthority> authorities,
			final SecurityCentreCandidature centreCandidature, final SecurityCommission commission, final PreferenceInd preferenceInd) {
		super(username, displayName, authorities);
		this.centreCandidature = centreCandidature;
		this.commission = commission;
		this.noDossierCandidat = null;
		this.displayNameCandidat = null;
		this.preferenceInd = preferenceInd;
	}
}
