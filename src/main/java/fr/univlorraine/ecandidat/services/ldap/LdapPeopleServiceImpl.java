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

import java.util.List;

import javax.naming.directory.SearchControls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.SizeLimitExceededException;
import org.springframework.ldap.TimeLimitExceededException;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Implementation du service Ldap de people
 * @author Kevin Hergalant
 */
@Component(value = "ldapPeopleServiceImpl")
@SuppressWarnings("serial")
public class LdapPeopleServiceImpl implements LdapGenericService<PeopleLdap> {
	/**
	 * Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(LdapPeopleServiceImpl.class);

	/** Config Ldap template */
	@Value("${ldap.url:#{null}}")
	private transient String ldapUrl;

	@Value("${ldap.base:#{null}}")
	private transient String ldapBase;

	@Value("${ldap.user:#{null}}")
	private transient String ldapUser;

	@Value("${ldap.pwd:#{null}}")
	private transient String ldapPwd;

	/**
	 * le base DN pour les recherches ldap
	 */
	@Value("${ldap.branche.people:#{null}}")
	private String baseDn;
	@Value("${ldap.champs.uid:#{null}}")
	private String champsUid;
	@Value("${ldap.champs.displayName:#{null}}")
	private String champsDisplayName;
	@Value("${ldap.champs.mail:#{null}}")
	private String champsMail;
	@Value("${ldap.champs.sn:#{null}}")
	private String champsSn;
	@Value("${ldap.champs.cn:#{null}}")
	private String champsCn;
	@Value("${ldap.champs.supannEtuId:#{null}}")
	private String champsSupannEtuId;
	@Value("${ldap.champs.supannCivilite:#{null}}")
	private String champsSupannCivilite;
	@Value("${ldap.champs.givenName:#{null}}")
	private String champsGivenName;

	/**
	 * Ldap Template de lecture
	 */
	private LdapTemplate ldapTemplate;

	private LdapTemplate getLdapTemplate() {
		if (ldapTemplate == null) {
			final LdapContextSource ldapContextSource = new LdapContextSource();
			ldapContextSource.setUrl(ldapUrl);
			ldapContextSource.setBase(ldapBase);
			ldapContextSource.setUserDn(ldapUser);
			ldapContextSource.setPassword(ldapPwd);
			ldapContextSource.afterPropertiesSet();
			ldapTemplate = new LdapTemplate(ldapContextSource);
		}
		return ldapTemplate;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.ldap.LdapGenericService#findByPrimaryKey(java.lang.String)
	 */
	@Override
	public PeopleLdap findByPrimaryKey(final String uid) {
		if (uid == null) {
			return null;
		}
		try {
			final String filter = "(" + champsUid + "=" + uid + ")";
			final List<PeopleLdap> l = getLdapTemplate().search(baseDn, filter, SearchControls.SUBTREE_SCOPE, getContextMapper());
			if (l != null && l.size() > 0) {
				return l.get(0);
			}
		} catch (final NameNotFoundException e) {
			logger.error("ldap.search.namenotfound", e);
		} catch (final TimeLimitExceededException e) {
			logger.error("ldap.search.timeexceeded", e);
		}
		return null;
	}

	@Override
	public PeopleLdap findByPrimaryKeyWithException(final String uid) {
		if (uid == null) {
			return null;
		}
		final String filter = "(" + champsUid + "=" + uid + ")";
		final List<PeopleLdap> l = getLdapTemplate().search(baseDn, filter, SearchControls.SUBTREE_SCOPE, getContextMapper());
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.ldap.LdapGenericService#findEntitiesByFilter(java.lang.String)
	 */
	@Override
	public List<PeopleLdap> findEntitiesByFilter(final String filter) throws LdapException {
		List<PeopleLdap> l = null;
		try {
			l = getLdapTemplate().search(baseDn, filter, SearchControls.SUBTREE_SCOPE, getContextMapper());
			if (l.size() > ConstanteUtils.NB_MAX_RECH_PERS) {
				throw new LdapException("ldap.search.toomuchresult");
			} else {
				return l;
			}
		} catch (final NameNotFoundException e) {
			logger.error("ldap.search.namenotfound", e);
			throw new LdapException("ldap.search.namenotfound", e.getCause());
		} catch (final TimeLimitExceededException e) {
			logger.error("ldap.search.timeexceeded", e);
			throw new LdapException("ldap.search.timeexceeded", e.getCause());
		} catch (final SizeLimitExceededException e) {
			throw new LdapException("ldap.search.toomuchresult", e.getCause());
		}
	}

	/* (non-Javadoc)
	 *
	 * @see fr.univlorraine.ecandidat.tools.ldap.LdapGenericService#getContextMapper() */
	@Override
	public ContextMapper<PeopleLdap> getContextMapper() {
		return new PeopleContextMapper();
	}

	/**
	 * Modifie les properties
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
	@Override
	public void setProperties(final String ldapUrl, final String ldapBase, final String ldapUser, final String ldapPwd,
		final String baseDn, final String champsUid, final String champsDisplayName, final String champsMail, final String champsSn,
		final String champsCn, final String champsSupannEtuId, final String champsSupannCivilite, final String champsGivenName) {

		final LdapContextSource ldapContextSource = new LdapContextSource();
		ldapContextSource.setUrl(ldapUrl);
		ldapContextSource.setBase(ldapBase);
		ldapContextSource.setUserDn(ldapUser);
		ldapContextSource.setPassword(ldapPwd);
		ldapContextSource.afterPropertiesSet();
		ldapTemplate = new LdapTemplate(ldapContextSource);

		this.baseDn = baseDn;
		this.champsUid = champsUid;
		this.champsDisplayName = champsDisplayName;
		this.champsMail = champsMail;
		this.champsSn = champsSn;
		this.champsCn = champsCn;
		this.champsSupannEtuId = champsSupannEtuId;
		this.champsSupannCivilite = champsSupannCivilite;
		this.champsGivenName = champsGivenName;
	}

	/**
	 * Le mapper de people
	 * @author Kevin
	 */
	private class PeopleContextMapper extends AbstractContextMapper<PeopleLdap> {
		@Override
		public PeopleLdap doMapFromContext(final DirContextOperations context) {
			final PeopleLdap o = new PeopleLdap();
			o.setObjectClass(context.getStringAttributes("objectClass"));
			o.setUid(context.getStringAttribute(champsUid));
			o.setSn(context.getStringAttribute(champsSn));
			o.setCn(context.getStringAttribute(champsCn));
			o.setDisplayName(context.getStringAttribute(champsDisplayName));
			o.setMail(context.getStringAttribute(champsMail));
			o.setSupannEtuId(context.getStringAttribute(champsSupannEtuId));
			o.setGivenName(context.getStringAttribute(champsGivenName));
			o.setSupannCivilite(context.getStringAttribute(champsSupannCivilite));
			return o;
		}
	}

}
