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
package fr.univlorraine.ecandidat.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.services.ldap.LdapException;
import fr.univlorraine.ecandidat.services.ldap.LdapGenericService;
import fr.univlorraine.ecandidat.services.ldap.PeopleLdap;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigLdap;

/**
 * Controller gérant les appels Ldap
 * @author Kevin Hergalant
 */
@Component
public class LdapController {

	/* applicationContext pour les messages */
	@Resource
	private transient ApplicationContext applicationContext;

	/* Les services Ldap */
	@Resource(name = "ldapPeopleServiceImpl")
	private LdapGenericService<PeopleLdap> ldapPeopleService;

	@Resource
	private transient ConfigController configController;

	/** Config Ldap template */
	@Value("${ldap.url:#{null}}")
	private transient String ldapUrl;
	@Value("${ldap.base:#{null}}")
	private transient String ldapBase;
	@Value("${ldap.user:#{null}}")
	private transient String ldapUser;
	@Value("${ldap.pwd:#{null}}")
	private transient String ldapPwd;
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
	@Value("${ldap.filtre.personnel:#{null}}")
	private String filtrePersonnel;

	/**
	 * Rafraichi le container de recherche de people Ldap
	 * @param txt le filtre a appliquer
	 */
	public List<PeopleLdap> getPeopleByFilter(final String txt) {
		String filtreTotal = "(|(" + champsUid + "=" + txt + ")(" + champsCn + "=*" + txt + "*))";
		if (filtrePersonnel != null && !filtrePersonnel.equals("")) {
			filtreTotal = "(&" + filtreTotal + filtrePersonnel + ")";
		}
		try {
			final List<PeopleLdap> l = ldapPeopleService.findEntitiesByFilter(filtreTotal);
			if (l == null || l.size() == 0) {
				Notification.show(applicationContext.getMessage("ldap.search.noresult", null, UI.getCurrent().getLocale()), Notification.Type.TRAY_NOTIFICATION);
				return new ArrayList<>();
			} else {
				return l.stream().filter(e -> e.getUid() != null).collect(Collectors.toList());
			}
		} catch (final LdapException e) {
			Notification.show(applicationContext.getMessage(e.getMessage(), null, UI.getCurrent().getLocale()), Notification.Type.TRAY_NOTIFICATION);
			return new ArrayList<>();
		}
	}

	/**
	 * @param  uid
	 * @return     Retourne un people par son uid
	 */
	public PeopleLdap findByPrimaryKey(final String uid) {
		return ldapPeopleService.findByPrimaryKey(uid);
	}

	/**
	 * @param  uid
	 * @return     Retourne un people par son uid
	 */
	public PeopleLdap findByPrimaryKeyWithException(final String uid) {
		return ldapPeopleService.findByPrimaryKeyWithException(uid);
	}

	/**
	 * Initialise les propriétés du Ldap
	 */
	public void initProperties() {
		final ConfigLdap config = configController.getConfigLdap();
		if (StringUtils.isNoneBlank(config.getUrl(), config.getBase(), config.getUser(), config.getPwd(), config.getBranchePeople())) {
			setProperties(config);
			return;
		} else if (StringUtils.isNoneBlank(ldapUrl, ldapBase, ldapUser, ldapPwd, baseDn)) {
			setProperties(ldapUrl, ldapBase, ldapUser, ldapPwd, baseDn, champsUid, champsDisplayName, champsMail, champsSn, champsCn, champsSupannEtuId, champsSupannCivilite, champsGivenName, filtrePersonnel);
			return;
		}
		throw new RuntimeException("Erreur à la configuration du ldap");
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
	 * @param filtrePersonnel
	 */
	public void setProperties(final String ldapUrl, final String ldapBase, final String ldapUser, final String ldapPwd, final String baseDn,
		final String champsUid, final String champsDisplayName, final String champsMail, final String champsSn,
		final String champsCn, final String champsSupannEtuId, final String champsSupannCivilite, final String champsGivenName, final String filtrePersonnel) {
		this.champsCn = champsCn;
		this.champsUid = champsUid;
		this.filtrePersonnel = filtrePersonnel;
		ldapPeopleService.setProperties(ldapUrl, ldapBase, ldapUser, ldapPwd, baseDn,
			champsUid, champsDisplayName, champsMail, champsSn, champsCn, champsSupannEtuId, champsSupannCivilite, champsGivenName);
	}

	/**
	 * @param configLdap
	 */
	public void setProperties(final ConfigLdap configLdap) {
		this.champsCn = configLdap.getChampsCn();
		this.champsUid = configLdap.getChampsUid();
		this.filtrePersonnel = configLdap.getFiltrePersonnel();
		setProperties(configLdap.getUrl(), configLdap.getBase(), configLdap.getUser(), configLdap.getPwd(), configLdap.getBranchePeople(),
			configLdap.getChampsUid(), configLdap.getChampsDisplayName(), configLdap.getChampsMail(), configLdap.getChampsSn(), configLdap.getChampsCn(),
			configLdap.getChampsSupannEtuId(), configLdap.getChampsSupannCivilite(), configLdap.getChampsGivenName(), configLdap.getFiltrePersonnel());
	}

}
