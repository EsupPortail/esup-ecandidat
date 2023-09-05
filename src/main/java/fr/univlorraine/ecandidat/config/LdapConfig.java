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
package fr.univlorraine.ecandidat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Configuration Ldap
 * @author Kevin Hergalant
 */
@Configuration
public class LdapConfig {

	@Value("${ldap.url:#{null}}")
	private transient String ldapUrl;

	@Value("${ldap.base:#{null}}")
	private transient String ldapBase;

	@Value("${ldap.user:#{null}}")
	private transient String ldapUser;

	@Value("${ldap.pwd:#{null}}")
	private transient String ldapPwd;

	/**
	 * LdapContextSource
	 * @return le context ldap
	 */
	@Bean
	public LdapContextSource contextSourceLdap() {
		final LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setUrl(ldapUrl);
		ldapContextSource.setBase(ldapBase);
		ldapContextSource.setUserDn(ldapUser);
		ldapContextSource.setPassword(ldapPwd);
		return ldapContextSource;
	}

	/**
	 * LdapTemplate
	 * @return le template de lecture
	 */
	@Bean
	public LdapTemplate ldapTemplateRead() {
		final LdapTemplate ldapTemplateRead = new LdapTemplate();
		ldapTemplateRead.setContextSource(contextSourceLdap());
		return ldapTemplateRead;
	}
}
