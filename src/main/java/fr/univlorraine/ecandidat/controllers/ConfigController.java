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
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.config.CacheConfig;
import fr.univlorraine.ecandidat.entities.ecandidat.Configuration;
import fr.univlorraine.ecandidat.repositories.ConfigurationRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.CryptoUtils;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseAuthEtab;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseUrl;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;

/**
 * Gestion des nomenclatures
 * @author Kevin Hergalant
 */
@Component
public class ConfigController {

	private final static String PROPERTY_FILE_PEGASE_URL = "configUrlServicesPegase.properties";

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;

	@Resource
	private transient ConfigurationRepository configurationRepository;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	@Value("config.crypt.secret")
	private transient String cryptoSecret;

	@Value("config.crypt.salt")
	private transient String cryptoSalt;

	@Value("${pegase.ws.username:}")
	private transient String username;

	@Value("${pegase.ws.password:}")
	private transient String password;

	@Value("${pegase.etablissement:}")
	private transient String etablissement;

	/**
	 * @param  list
	 * @param  code
	 * @return      une ligne de configuration par son code
	 */
	public String getConfigurationByCod(final List<Configuration> list, final String code) {
		return list.stream().filter(e -> e.getCodConfig().equals(code)).findFirst().map(Configuration::getValConfig).orElse(null);
	}

	/**
	 * @return l'authentification Pégase
	 */
	public ConfigPegaseAuthEtab loadConfigPegaseAuthEtab() {
		final List<Configuration> list = configurationRepository.findByCodConfigStartsWith(Configuration.COD_CONFIG_PEGASE_AUTH);
		final ConfigPegaseAuthEtab config = new ConfigPegaseAuthEtab();
		config.setUrl(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_AUTH_URL));
		config.setUser(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_AUTH_USER));
		final String pwd = getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_AUTH_PWD);
		config.setPwd(pwd != null ? CryptoUtils.decrypt(pwd, cryptoSecret, cryptoSalt) : null);
		config.setEtab(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_AUTH_ETAB));
		return config;
	}

	/**
	 * @return la config auth sans pwd
	 */
	public ConfigPegaseAuthEtab loadConfigPegaseAuthEtabWithoutPwd() {
		final ConfigPegaseAuthEtab config = loadConfigPegaseAuthEtab();
		config.setPwd(null);
		return config;
	}

	/**
	 * @param  order
	 * @param  code
	 * @param  msgCode
	 * @return         la liste de bean version
	 */
	public List<SimpleTablePresentation> getConfigPegaseAuthEtabPresentation() {
		final List<SimpleTablePresentation> list = new ArrayList<>();
		final ConfigPegaseAuthEtab config = loadConfigPegaseAuthEtab();
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_AUTH_URL, applicationContext.getMessage("config.pegaseAuthEtab.table.url", null, UI.getCurrent().getLocale()), config.getUrl()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_AUTH_USER, applicationContext.getMessage("config.pegaseAuthEtab.table.user", null, UI.getCurrent().getLocale()), config.getUser()));
		list.add(
			new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_AUTH_PWD, applicationContext.getMessage("config.pegaseAuthEtab.table.pwd", null, UI.getCurrent().getLocale()),
				config.getPwd() != null ? "******" : null));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_AUTH_ETAB, applicationContext.getMessage("config.pegaseAuthEtab.table.etab", null, UI.getCurrent().getLocale()), config.getEtab()));
		return list;
	}

	/**
	 * Enregistre la config auth pegase
	 * @param configPegaseAuthEtab
	 */
	public void saveConfigPegaseAuth(final ConfigPegaseAuthEtab configPegaseAuthEtab) {
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_AUTH_URL, configPegaseAuthEtab.getUrl()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_AUTH_USER, configPegaseAuthEtab.getUser()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_AUTH_PWD, CryptoUtils.encrypt(configPegaseAuthEtab.getPwd(), cryptoSecret, cryptoSalt)));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_AUTH_ETAB, configPegaseAuthEtab.getEtab()));
		Notification.show(applicationContext.getMessage("config.save", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		cacheController.invalidPegaseConfCache();
	}

	/**
	 * @return la configuration d'authentification
	 */
	@Cacheable(value = CacheConfig.CACHE_PEGASE_CONF, cacheManager = CacheConfig.CACHE_MANAGER_NAME)
	public ConfigPegaseAuthEtab getConfigPegaseAuthEtab() {
		final ConfigPegaseAuthEtab configAuthEtabDb = loadConfigPegaseAuthEtab();
		if (configAuthEtabDb != null && configAuthEtabDb.isValid()) {
			return configAuthEtabDb;
		} else {
			final ConfigPegaseAuthEtab configPegaseAuthEtabProp = new ConfigPegaseAuthEtab();
			configPegaseAuthEtabProp.setUser(username);
			configPegaseAuthEtabProp.setPwd(password);
			configPegaseAuthEtabProp.setEtab(etablissement);
			/* On cherche le fichier de properties dans le classpath */
			try {
				final Properties properties = new Properties();
				properties.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_PEGASE_URL));
				configPegaseAuthEtabProp.setUrl(properties.getProperty(ConstanteUtils.PEGASE_URL_AUTH));
				if (configPegaseAuthEtabProp.isValid()) {
					return configPegaseAuthEtabProp;
				}
			} catch (final Exception e) {
				throw new RuntimeException("Impossible de charger le fichier configUrlServices, ajoutez le dans le dossier ressources", e);
			}
		}
		throw new RuntimeException("Impossible de charger la configuration Pégase, ajoutez le fichier de configuration dans le dossier ressources ou enregistrez votre configuration dans le menu");
	}

	/**
	 * test la configuration
	 * @param  configPegaseAuth
	 * @return                  true si ok
	 */
	public boolean testConfigPegaseAuth(final ConfigPegaseAuthEtab configPegaseAuthEtab) {
		return siScolService.testAuthApiPegase(configPegaseAuthEtab);
	}

	/**
	 * @return la config d'url pegase
	 */
	public ConfigPegaseUrl loadConfigPegaseUrl() {
		final List<Configuration> list = configurationRepository.findByCodConfigStartsWith(Configuration.COD_CONFIG_PEGASE_URL);
		final ConfigPegaseUrl config = new ConfigPegaseUrl();
		config.setCoc(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_COC));
		config.setCof(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_COF));
		config.setIns(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_INS));
		config.setInsExt(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_INS_EXT));
		config.setMof(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_MOF));
		config.setOdf(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_ODF));
		config.setRef(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_REF));
		config.setParamTestCodEtu(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_PARAM_TEST_COD_ETU));
		config.setParamTestCodFormation(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_PARAM_TEST_COD_FORMATION));
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
		final ConfigPegaseUrl config = loadConfigPegaseUrl();
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_COC, applicationContext.getMessage("config.pegaseUrl.table.coc", null, UI.getCurrent().getLocale()), config.getCoc()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_COF, applicationContext.getMessage("config.pegaseUrl.table.cof", null, UI.getCurrent().getLocale()), config.getCof()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_INS, applicationContext.getMessage("config.pegaseUrl.table.ins", null, UI.getCurrent().getLocale()), config.getIns()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_INS_EXT, applicationContext.getMessage("config.pegaseUrl.table.insExt", null, UI.getCurrent().getLocale()), config.getInsExt()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_MOF, applicationContext.getMessage("config.pegaseUrl.table.mof", null, UI.getCurrent().getLocale()), config.getMof()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_ODF, applicationContext.getMessage("config.pegaseUrl.table.odf", null, UI.getCurrent().getLocale()), config.getOdf()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_REF, applicationContext.getMessage("config.pegaseUrl.table.ref", null, UI.getCurrent().getLocale()), config.getRef()));
//		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_PARAM_TEST_COD_ETU, applicationContext.getMessage("config.pegaseUrl.table.paramTestCodEtu", null, UI.getCurrent().getLocale()),
//			config.getParamTestCodEtu()));
//		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_PARAM_TEST_COD_FORMATION, applicationContext.getMessage("config.pegaseUrl.table.paramTestCodFormation", null, UI.getCurrent().getLocale()),
//			config.getParamTestCodFormation()));
		return list;
	}

	/**
	 * Enregistre la config Pégase
	 * @param configPegaseUrl
	 */
	public void saveConfigPegaseUrl(final ConfigPegaseUrl configPegaseUrl) {
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_COC, configPegaseUrl.getCoc()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_COF, configPegaseUrl.getCof()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_INS, configPegaseUrl.getIns()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_INS_EXT, configPegaseUrl.getInsExt()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_MOF, configPegaseUrl.getMof()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_ODF, configPegaseUrl.getOdf()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_REF, configPegaseUrl.getRef()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_PARAM_TEST_COD_ETU, configPegaseUrl.getParamTestCodEtu()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_PARAM_TEST_COD_FORMATION, configPegaseUrl.getParamTestCodFormation()));
		Notification.show(applicationContext.getMessage("config.save", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		cacheController.invalidPegaseConfCache();
	}

	/**
	 * Test la config Pegase
	 * @param  configPegaseUrl
	 * @return
	 */
	public boolean testConfigPegaseUrl(final ConfigPegaseUrl configPegaseUrl) {
		return siScolService.testUrlApiPegase(loadConfigPegaseAuthEtab(), configPegaseUrl);
	}

	/**
	 * @return la configuration Pegase
	 */
	@Cacheable(value = CacheConfig.CACHE_PEGASE_CONF, cacheManager = CacheConfig.CACHE_MANAGER_NAME)
	public ConfigPegaseUrl getConfigPegaseUrl() {
		final ConfigPegaseUrl configDb = loadConfigPegaseUrl();
		if (configDb != null && configDb.isValid()) {
			return configDb;
		} else {
			final ConfigPegaseUrl configPegaseUrlProp = new ConfigPegaseUrl();
			/* On cherche le fichier de properties dans le classpath */
			try {
				final Properties properties = new Properties();
				properties.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE_PEGASE_URL));
				configPegaseUrlProp.setCoc(properties.getProperty(ConstanteUtils.PEGASE_URL_COC));
				configPegaseUrlProp.setCof(properties.getProperty(ConstanteUtils.PEGASE_URL_COF));
				configPegaseUrlProp.setIns(properties.getProperty(ConstanteUtils.PEGASE_URL_INS));
				configPegaseUrlProp.setMof(properties.getProperty(ConstanteUtils.PEGASE_URL_MOF));
				configPegaseUrlProp.setRef(properties.getProperty(ConstanteUtils.PEGASE_URL_REF));
				if (configPegaseUrlProp.isValid()) {
					return configPegaseUrlProp;
				}
			} catch (final Exception e) {
				throw new RuntimeException("Impossible de charger le fichier configUrlServices, ajoutez le dans le dossier ressources", e);
			}
		}
		throw new RuntimeException("Impossible de charger la configuration Pégase, ajoutez le fichier de configuration dans le dossier ressources ou enregistrez votre configuration dans le menu");
	}
}
