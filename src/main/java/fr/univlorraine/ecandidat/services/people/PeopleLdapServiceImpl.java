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
package fr.univlorraine.ecandidat.services.people;

import java.util.List;

import javax.annotation.Resource;
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
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Implementation du service Ldap de people
 * @author Kevin Hergalant
 */
@Component(value = "ldapPeopleServiceImpl")
@SuppressWarnings("serial")
public class PeopleLdapServiceImpl implements PeopleGenericService<People> {
	/**
	 * Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(PeopleLdapServiceImpl.class);

	/** Config Ldap template */
	@Value("${ldap.url:#{null}}")
	private transient String ldapUrl;

	@Value("${ldap.base:#{null}}")
	private transient String ldapBase;

	@Value("${ldap.user:#{null}}")
	private transient String ldapUser;

	@Value("${ldap.pwd:#{null}}")
	private transient String ldapPwd;

	@Value("${ldap.filtre.personnel}")
	private String filtrePersonnel;

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
	@Value("${ldap.champs.supannEtuId:#{null}}")
	private String champsSupannEtuId;
	@Value("${ldap.champs.givenName:#{null}}")
	private String champsGivenName;

	/**
	 * Ldap Template de lecture
	 */
	@Resource
	private LdapTemplate ldapTemplateRead;

	/**
	 * @see fr.univlorraine.ecandidat.services.people.PeopleGenericService#findByPrimaryKey(java.lang.String)
	 */
	@Override
	public People findByPrimaryKey(final String uid) {
		if (uid == null) {
			return null;
		}
		try {
			final String filter = "(" + champsUid + "=" + uid + ")";
			final List<People> l = ldapTemplateRead.search(baseDn, filter, SearchControls.SUBTREE_SCOPE, getContextMapper());
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
	public People findByPrimaryKeyWithException(final String uid) {
		if (uid == null) {
			return null;
		}
		final String filter = "(" + champsUid + "=" + uid + ")";
		final List<People> l = ldapTemplateRead.search(baseDn, filter, SearchControls.SUBTREE_SCOPE, getContextMapper());
		if (l != null && l.size() > 0) {
			return l.get(0);
		}
		return null;
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.people.PeopleGenericService#findEntitiesByFilter(java.lang.String)
	 */
	@Override
	public List<People> findByFilter(final String filter) throws PeopleException {
		List<People> l = null;
		try {
			String filtreTotal = "(|(" + champsUid + "=" + filter + ")(" + champsSn + "=*" + filter + "*))";
			if (filtrePersonnel != null && !filtrePersonnel.equals("")) {
				filtreTotal = "(&" + filtreTotal + filtrePersonnel + ")";
			}
			l = ldapTemplateRead.search(baseDn, filtreTotal, SearchControls.SUBTREE_SCOPE, getContextMapper());
			if (l.size() > ConstanteUtils.NB_MAX_RECH_PERS) {
				throw new PeopleException("ldap.search.toomuchresult");
			} else {
				return l;
			}
		} catch (final NameNotFoundException e) {
			logger.error("ldap.search.namenotfound", e);
			throw new PeopleException("ldap.search.namenotfound", e.getCause());
		} catch (final TimeLimitExceededException e) {
			logger.error("ldap.search.timeexceeded", e);
			throw new PeopleException("ldap.search.timeexceeded", e.getCause());
		} catch (final SizeLimitExceededException e) {
			throw new PeopleException("ldap.search.toomuchresult", e.getCause());
		}
	}

	/* (non-Javadoc)
	 *
	 * @see fr.univlorraine.ecandidat.tools.ldap.LdapGenericService#getContextMapper() */
	public ContextMapper<People> getContextMapper() {
		return new PeopleContextMapper();
	}

	/**
	 * Le mapper de people
	 * @author Kevin
	 */
	private class PeopleContextMapper extends AbstractContextMapper<People> {
		@Override
		public People doMapFromContext(final DirContextOperations context) {
			final People o = new People();
			o.setObjectClass(context.getStringAttributes("objectClass"));
			o.setUid(context.getStringAttribute(champsUid));
			o.setSn(context.getStringAttribute(champsSn));
			o.setDisplayName(context.getStringAttribute(champsDisplayName));
			o.setMail(context.getStringAttribute(champsMail));
			o.setSupannEtuId(context.getStringAttribute(champsSupannEtuId));
			o.setGivenName(context.getStringAttribute(champsGivenName));
			return o;
		}
	}
}
