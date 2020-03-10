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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.services.ldap.LdapException;
import fr.univlorraine.ecandidat.services.ldap.LdapGenericService;
import fr.univlorraine.ecandidat.services.ldap.PeopleLdap;

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

	@Value("${ldap.champs.cn}")
	private String champsCn;

	@Value("${ldap.champs.uid}")
	private String champsUid;

	@Value("${ldap.filtre.personnel}")
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

}
