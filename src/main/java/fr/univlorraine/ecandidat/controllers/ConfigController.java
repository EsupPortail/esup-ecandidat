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

import javax.annotation.Resource;
import javax.naming.directory.SearchControls;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Configuration;
import fr.univlorraine.ecandidat.repositories.ConfigurationRepository;
import fr.univlorraine.ecandidat.services.ldap.PeopleLdap;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.CryptoUtils;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigLdap;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseAuth;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseUrl;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.views.windows.InfoWindow;

/**
 * Gestion des nomenclatures
 * @author Kevin Hergalant
 */
@Component
public class ConfigController {

	private final Logger logger = LoggerFactory.getLogger(ConfigController.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient LdapController ldapController;
	@Resource
	private transient ConfigurationRepository configurationRepository;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	@Value("config.crypt.secret")
	private transient String cryptoSecret;

	@Value("config.crypt.salt")
	private transient String cryptoSalt;

	public String getConfigurationByCod(final List<Configuration> list, final String code) {
		return list.stream().filter(e -> e.getCodConfig().equals(code)).findFirst().map(Configuration::getValConfig).orElse(null);
	}

	public ConfigLdap getConfigLdap() {
		final List<Configuration> list = configurationRepository.findByCodConfigStartsWith(Configuration.COD_CONFIG_LDAP);
		final ConfigLdap config = new ConfigLdap();
		config.setUrl(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_URL));
		config.setBase(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_BASE));
		config.setUser(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_USER));
		final String pwd = getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_PWD);
		config.setPwd(pwd != null ? CryptoUtils.decrypt(pwd, cryptoSecret, cryptoSalt) : null);
		config.setBranchePeople(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_BRANCHE_PEOPLE));
		config.setFiltrePersonnel(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_FILTRE_PERSONNEL));
		config.setChampsUid(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_CHAMPS_UID));
		config.setChampsDisplayName(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_CHAMPS_DISPLAYNAME));
		config.setChampsMail(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_CHAMPS_MAIL));
		config.setChampsSn(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_CHAMPS_SN));
		config.setChampsCn(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_CHAMPS_CN));
		config.setChampsSupannCivilite(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_CHAMPS_SUPANN_CIVILITE));
		config.setChampsSupannEtuId(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_CHAMPS_SUPANN_ETU_ID));
		config.setChampsGivenName(getConfigurationByCod(list, Configuration.COD_CONFIG_LDAP_CHAMPS_GIVEN_NAME));
		return config;
	}

	public ConfigLdap getConfigLdapWithoutPwd() {
		final ConfigLdap config = getConfigLdap();
		config.setPwd(null);
		return config;
	}

	/**
	 * @param  order
	 * @param  code
	 * @param  msgCode
	 * @return         la liste de bean version
	 */
	public List<SimpleTablePresentation> getConfigLdapPresentation() {
		final List<SimpleTablePresentation> list = new ArrayList<>();
		final ConfigLdap config = getConfigLdap();
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_URL, applicationContext.getMessage("config.ldap.table.url", null, UI.getCurrent().getLocale()), config.getUrl()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_BASE, applicationContext.getMessage("config.ldap.table.base", null, UI.getCurrent().getLocale()), config.getBase()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_USER, applicationContext.getMessage("config.ldap.table.user", null, UI.getCurrent().getLocale()), config.getUser()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_PWD, applicationContext.getMessage("config.ldap.table.pwd", null, UI.getCurrent().getLocale()), config.getPwd() != null ? "******" : null));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_BRANCHE_PEOPLE, applicationContext.getMessage("config.ldap.table.branchePeople", null, UI.getCurrent().getLocale()), config.getBranchePeople()));
		list.add(
			new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_FILTRE_PERSONNEL, applicationContext.getMessage("config.ldap.table.filtrePersonnel", null, UI.getCurrent().getLocale()), config.getFiltrePersonnel()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_CHAMPS_UID, applicationContext.getMessage("config.ldap.table.champsUid", null, UI.getCurrent().getLocale()), config.getChampsUid()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_CHAMPS_DISPLAYNAME, applicationContext.getMessage("config.ldap.table.champsDisplayName", null, UI.getCurrent().getLocale()),
			config.getChampsDisplayName()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_CHAMPS_MAIL, applicationContext.getMessage("config.ldap.table.champsMail", null, UI.getCurrent().getLocale()), config.getChampsMail()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_CHAMPS_SN, applicationContext.getMessage("config.ldap.table.champsSn", null, UI.getCurrent().getLocale()), config.getChampsSn()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_CHAMPS_CN, applicationContext.getMessage("config.ldap.table.champsCn", null, UI.getCurrent().getLocale()), config.getChampsCn()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_CHAMPS_SUPANN_CIVILITE, applicationContext.getMessage("config.ldap.table.champsSupannCivilite", null, UI.getCurrent().getLocale()),
			config.getChampsSupannCivilite()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_CHAMPS_SUPANN_ETU_ID, applicationContext.getMessage("config.ldap.table.champsSupannEtuId", null, UI.getCurrent().getLocale()),
			config.getChampsSupannEtuId()));
		list.add(
			new SimpleTablePresentation(Configuration.COD_CONFIG_LDAP_CHAMPS_GIVEN_NAME, applicationContext.getMessage("config.ldap.table.champsGivenName", null, UI.getCurrent().getLocale()), config.getChampsGivenName()));
		return list;
	}

	public void saveConfigLdap(final ConfigLdap configLdap) {
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_URL, configLdap.getUrl()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_BASE, configLdap.getBase()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_USER, configLdap.getUser()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_PWD, CryptoUtils.encrypt(configLdap.getPwd(), cryptoSecret, cryptoSalt)));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_BRANCHE_PEOPLE, configLdap.getBranchePeople()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_FILTRE_PERSONNEL, configLdap.getFiltrePersonnel()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_CHAMPS_UID, configLdap.getChampsUid()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_CHAMPS_DISPLAYNAME, configLdap.getChampsDisplayName()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_CHAMPS_MAIL, configLdap.getChampsMail()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_CHAMPS_SN, configLdap.getChampsSn()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_CHAMPS_CN, configLdap.getChampsCn()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_CHAMPS_SUPANN_CIVILITE, configLdap.getChampsSupannCivilite()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_CHAMPS_SUPANN_ETU_ID, configLdap.getChampsSupannEtuId()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_LDAP_CHAMPS_GIVEN_NAME, configLdap.getChampsGivenName()));
		ldapController.setProperties(configLdap);
		Notification.show(applicationContext.getMessage("config.save", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
	}

	public boolean testConfigLdap(final ConfigLdap configLdap, final String txt) {
		String filtreTotal = "(|(" + configLdap.getChampsUid() + "=" + txt + ")(" + configLdap.getChampsCn() + "=*" + txt + "*))";
		if (StringUtils.isNotBlank(configLdap.getFiltrePersonnel())) {
			filtreTotal = "(&" + filtreTotal + configLdap.getFiltrePersonnel() + ")";
		}
		try {
			final LdapContextSource ldapContextSource = new LdapContextSource();
			ldapContextSource.setUrl(configLdap.getUrl());
			ldapContextSource.setBase(configLdap.getBase());
			ldapContextSource.setUserDn(configLdap.getUser());
			ldapContextSource.setPassword(configLdap.getPwd());
			ldapContextSource.afterPropertiesSet();
			final LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource);

			final List<PeopleLdap> list = ldapTemplate.search(configLdap.getBranchePeople(), filtreTotal, SearchControls.SUBTREE_SCOPE, new PeopleContextMapper(configLdap));
			if (list.size() > ConstanteUtils.NB_MAX_RECH_PERS) {
				Notification.show(applicationContext.getMessage("ldap.search.toomuchresult", null, UI.getCurrent().getLocale()), Notification.Type.TRAY_NOTIFICATION);
				return false;
			} else if (list.size() == 0) {
				Notification.show(applicationContext.getMessage("ldap.search.noresult", null, UI.getCurrent().getLocale()), Notification.Type.TRAY_NOTIFICATION);
				return false;
			} else {
				final StringBuilder ret = new StringBuilder();
				list.forEach(e -> ret.append(e + "<br/><br/>"));
				UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("config.ldap.test.result", null, UI.getCurrent().getLocale()), ret.toString(), 500, 70));
				return true;
			}
		} catch (final Exception e) {
			UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("config.ldap.test.result", null, UI.getCurrent().getLocale()), e.toString(), 500, 70));
			logger.error(applicationContext.getMessage("config.ldap.erreur", null, UI.getCurrent().getLocale()), e);
			return false;
		}
	}

	/**
	 * Le mapper de people
	 * @author Kevin
	 */
	public class PeopleContextMapper extends AbstractContextMapper<PeopleLdap> {

		private final ConfigLdap configLdap;

		public PeopleContextMapper(final ConfigLdap configLdap) {
			this.configLdap = configLdap;
		}

		@Override
		public PeopleLdap doMapFromContext(final DirContextOperations context) {
			final PeopleLdap o = new PeopleLdap();
			o.setObjectClass(context.getStringAttributes("objectClass"));
			o.setUid(context.getStringAttribute(configLdap.getChampsUid()));
			o.setSn(context.getStringAttribute(configLdap.getChampsSn()));
			o.setCn(context.getStringAttribute(configLdap.getChampsCn()));
			o.setDisplayName(context.getStringAttribute(configLdap.getChampsDisplayName()));
			o.setMail(context.getStringAttribute(configLdap.getChampsMail()));
			o.setSupannEtuId(context.getStringAttribute(configLdap.getChampsSupannEtuId()));
			o.setGivenName(context.getStringAttribute(configLdap.getChampsGivenName()));
			o.setSupannCivilite(context.getStringAttribute(configLdap.getChampsSupannCivilite()));
			return o;
		}
	}

	public ConfigPegaseAuth getConfigPegaseAuth() {
		final List<Configuration> list = configurationRepository.findByCodConfigStartsWith(Configuration.COD_CONFIG_PEGASE_AUTH);
		final ConfigPegaseAuth config = new ConfigPegaseAuth();
		config.setUrl(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_AUTH_URL));
		config.setUser(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_AUTH_USER));
		final String pwd = getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_AUTH_PWD);
		config.setPwd(pwd != null ? CryptoUtils.decrypt(pwd, cryptoSecret, cryptoSalt) : null);
		return config;
	}

	public ConfigPegaseAuth getConfigPegaseAuthWithoutPwd() {
		final ConfigPegaseAuth config = getConfigPegaseAuth();
		config.setPwd(null);
		return config;
	}

	/**
	 * @param  order
	 * @param  code
	 * @param  msgCode
	 * @return         la liste de bean version
	 */
	public List<SimpleTablePresentation> getConfigPegaseAuthPresentation() {
		final List<SimpleTablePresentation> list = new ArrayList<>();
		final ConfigPegaseAuth config = getConfigPegaseAuth();
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_AUTH_URL, applicationContext.getMessage("config.pegaseAuth.table.url", null, UI.getCurrent().getLocale()), config.getUrl()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_AUTH_USER, applicationContext.getMessage("config.pegaseAuth.table.user", null, UI.getCurrent().getLocale()), config.getUser()));
		list.add(
			new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_AUTH_PWD, applicationContext.getMessage("config.pegaseAuth.table.pwd", null, UI.getCurrent().getLocale()), config.getPwd() != null ? "******" : null));
		return list;
	}

	public void saveConfigPegaseAuth(final ConfigPegaseAuth configPegaseAuth) {
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_AUTH_URL, configPegaseAuth.getUrl()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_AUTH_USER, configPegaseAuth.getUser()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_AUTH_PWD, CryptoUtils.encrypt(configPegaseAuth.getPwd(), cryptoSecret, cryptoSalt)));
		//ldapController.setProperties(configLdap);
		Notification.show(applicationContext.getMessage("config.save", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
	}

	public boolean testConfigPegaseAuth(final ConfigPegaseAuth configPegaseAuth) {
		return siScolService.testAuthApiPegase(configPegaseAuth);
	}

	public ConfigPegaseUrl getConfigPegaseUrl() {
		final List<Configuration> list = configurationRepository.findByCodConfigStartsWith(Configuration.COD_CONFIG_PEGASE_URL);
		final ConfigPegaseUrl config = new ConfigPegaseUrl();
		config.setCoc(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_COC));
		config.setCof(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_COF));
		config.setIns(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_INS));
		config.setMof(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_MOF));
		config.setRef(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_REF));
		return config;
	}

	/**
	 * @param  order
	 * @param  code
	 * @param  msgCode
	 * @return         la liste de bean version
	 */
	public List<SimpleTablePresentation> getConfigPegaseUrlPresentation() {
		final List<SimpleTablePresentation> list = new ArrayList<>();
		final ConfigPegaseUrl config = getConfigPegaseUrl();
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_COC, applicationContext.getMessage("config.pegaseUrl.table.coc", null, UI.getCurrent().getLocale()), config.getCoc()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_COF, applicationContext.getMessage("config.pegaseUrl.table.cof", null, UI.getCurrent().getLocale()), config.getCof()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_INS, applicationContext.getMessage("config.pegaseUrl.table.ins", null, UI.getCurrent().getLocale()), config.getIns()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_MOF, applicationContext.getMessage("config.pegaseUrl.table.mof", null, UI.getCurrent().getLocale()), config.getMof()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_REF, applicationContext.getMessage("config.pegaseUrl.table.ref", null, UI.getCurrent().getLocale()), config.getRef()));
		return list;
	}

	public void saveConfigPegaseUrl(final ConfigPegaseUrl configPegaseUrl) {
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_COC, configPegaseUrl.getCoc()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_COF, configPegaseUrl.getCof()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_INS, configPegaseUrl.getIns()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_MOF, configPegaseUrl.getMof()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_REF, configPegaseUrl.getRef()));

		//ldapController.setProperties(configLdap);
		Notification.show(applicationContext.getMessage("config.save", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
	}

	public boolean testConfigPegaseUrl(final ConfigPegaseUrl configPegaseUrl) {
		return siScolService.testUrlApiPegase(getConfigPegaseAuth(), configPegaseUrl);
	}
}
