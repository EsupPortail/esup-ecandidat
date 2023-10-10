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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Configuration;
import fr.univlorraine.ecandidat.repositories.ConfigurationRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.CryptoUtils;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseAuth;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseUrl;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;

/**
 * Gestion des nomenclatures
 * @author Kevin Hergalant
 */
@Component
public class ConfigController {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
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
		Notification.show(applicationContext.getMessage("config.save", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
	}

	public boolean testConfigPegaseUrl(final ConfigPegaseUrl configPegaseUrl) {
		return siScolService.testUrlApiPegase(getConfigPegaseAuth(), configPegaseUrl);
	}
}
