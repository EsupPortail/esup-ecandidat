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

/**Implementation du service Ldap de people
 * @author Kevin Hergalant
 *
 */
@Component(value="ldapPeopleServiceImpl")
public class LdapPeopleServiceImpl implements LdapGenericService<PeopleLdap> {

	/**serialVersionUID**/
	private static final long serialVersionUID = -5154026091229021611L;
	/**
	 * Logger
	 */
	private Logger logger = LoggerFactory.getLogger(LdapPeopleServiceImpl.class);
	
	/**
	 * le base DN pour les recherches ldap
	 */	
	@Value("${ldap.branche.people}")
	private String baseDn;
	@Value("${ldap.champs.uid}")
	private String champsUid;
	@Value("${ldap.champs.displayName}")
	private String champsDisplayName;
	@Value("${ldap.champs.mail}")
	private String champsMail;
	@Value("${ldap.champs.sn}")
	private String champsSn;
	@Value("${ldap.champs.cn}")
	private String champsCn;
	@Value("${ldap.champs.supannEtuId}")
	private String champsSupannEtuId;
	@Value("${ldap.champs.supannCivilite}")
	private String champsSupannCivilite;
	@Value("${ldap.champs.givenName}")
	private String champsGivenName;

	/**
	 * Ldap Template de lecture
	 */
	@Resource
	private LdapTemplate ldapTemplateRead;
	
	/**
	 * @see fr.univlorraine.ecandidat.services.ldap.LdapGenericService#findByPrimaryKey(java.lang.String)
	 */
	@Override
	public PeopleLdap findByPrimaryKey(String uid) {
		if (uid==null){
			return null;
		}		
		try{
			String filter = "("+champsUid+"="+uid+")";
			List<PeopleLdap> l = ldapTemplateRead.search(baseDn, filter,SearchControls.SUBTREE_SCOPE, getContextMapper());
			if(l!=null && l.size()>0){
				return (PeopleLdap) l.get(0);
			}
		}catch (NameNotFoundException e) {
			logger.error("ldap.search.namenotfound",e);
		}catch (TimeLimitExceededException e) {
			logger.error("ldap.search.timeexceeded",e);
		}
		return null;
	}
	
	/**
	 * @see fr.univlorraine.ecandidat.services.ldap.LdapGenericService#findEntitiesByFilter(java.lang.String)
	 */
	@Override
	public List<PeopleLdap> findEntitiesByFilter(String filter) throws LdapException {
		List<PeopleLdap> l = null;
		try{
			l = ldapTemplateRead.search(baseDn, filter,SearchControls.SUBTREE_SCOPE, getContextMapper());
			if (l.size()>ConstanteUtils.NB_MAX_RECH_PERS){
				throw new LdapException("ldap.search.toomuchresult");
			}else{
				return l;
			}
		}catch (NameNotFoundException e) {
			logger.error("ldap.search.namenotfound",e);
			throw new LdapException("ldap.search.namenotfound", e.getCause());
		}catch (TimeLimitExceededException e) {
			logger.error("ldap.search.timeexceeded",e);
			throw new LdapException("ldap.search.timeexceeded", e.getCause());
		}catch (SizeLimitExceededException e) {
			throw new LdapException("ldap.search.toomuchresult", e.getCause());
		}
	}

	/* (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.tools.ldap.LdapGenericService#getContextMapper()
	 */
	@Override
	public ContextMapper<PeopleLdap> getContextMapper() {
		return new PeopleContextMapper();
	}

	/** Le mapper de people
	 * @author Kevin
	 *
	 */
	private class PeopleContextMapper extends AbstractContextMapper<PeopleLdap> {
		public PeopleLdap doMapFromContext(DirContextOperations context) {
			PeopleLdap o = new PeopleLdap();
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
