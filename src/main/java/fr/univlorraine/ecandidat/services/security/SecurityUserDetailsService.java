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

import java.util.HashMap;

import javax.annotation.Resource;

import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.univlorraine.ecandidat.controllers.UserController;

/**
 * UserDetailsService perso
 * @author Kevin Hergalant
 */
@Service("userDetailsService")
public class SecurityUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken>, UserDetailsService {

	@Resource
	private transient UserController userController;

	@Override
	public UserDetails loadUserDetails(final CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
		return userController.getSecurityUser(token.getName(), token.getAssertion().getPrincipal().getAttributes());
	}

	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
		return userController.getSecurityUser(username, new HashMap<>());
	}
}
