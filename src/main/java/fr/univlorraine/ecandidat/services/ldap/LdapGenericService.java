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

import java.io.Serializable;
import java.util.List;

import org.springframework.ldap.core.ContextMapper;

/**
 * Generic service Ldap
 * @author     Kevin Hergalant
 * @param  <T>
 */
public interface LdapGenericService<T> extends Serializable {

	/**
	 * @param  uid
	 * @return     un people Ldap
	 */
	T findByPrimaryKey(String uid);

	/**
	 * @param  uid
	 * @return     un people Ldap
	 */
	T findByPrimaryKeyWithException(String uid);

	/**
	 * @param  filter
	 * @return                      une liste d'entité
	 * @throws LdapException
	 * @throws LdapServiceException
	 */
	List<T> findEntitiesByFilter(String filter) throws LdapException;

	/**
	 * Modife les properties
	 * @param baseDn
	 * @param champsUid
	 * @param champsDisplayName
	 * @param champsMail
	 * @param champsSn
	 * @param champsCn
	 * @param champsSupannEtuId
	 * @param champsSupannCivilite
	 * @param champsGivenName
	 */
	void setProperties(final String ldapUrl, final String ldapBase, final String ldapUser, final String ldapPwd,
		final String baseDn, final String champsUid, final String champsDisplayName, final String champsMail, final String champsSn,
		final String champsCn, final String champsSupannEtuId, final String champsSupannCivilite, final String champsGivenName);

	/**
	 * Mapping l'entité LDAP vers l'objet
	 * @return le ContextMapper
	 */
	ContextMapper<?> getContextMapper();

}
