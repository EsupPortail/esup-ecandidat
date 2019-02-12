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
package fr.univlorraine.ecandidat.services.ldap;

/**
 * Class d'exception pour le ldap
 * 
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
public class LdapException extends Exception {

	/**
	 * Constructeur
	 */
	public LdapException() {
	}

	/**
	 * Constructeur avec message
	 * 
	 * @param message
	 */
	public LdapException(final String message) {
		super(message);
	}

	/**
	 * Constructeur avec cause
	 * 
	 * @param cause
	 */
	public LdapException(final Throwable cause) {
		super(cause);
	}

	/**
	 * Constructeur avec message et cause
	 * 
	 * @param message
	 * @param cause
	 */
	public LdapException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
