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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.vaadin.server.FileResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.config.CacheConfig;
import fr.univlorraine.ecandidat.entities.ecandidat.Configuration;
import fr.univlorraine.ecandidat.repositories.ConfigurationRepository;
import fr.univlorraine.ecandidat.services.file.FileCustom;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.CryptoUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigEtab;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseAuthEtab;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseUrl;
import fr.univlorraine.ecandidat.utils.bean.presentation.ConfigStaticTablePresentation;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import jakarta.annotation.Resource;

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
	private transient CacheController cacheController;
	@Resource
	private transient ConfigController self;

	@Resource
	private transient ConfigurationRepository configurationRepository;

	/* Resources externes */
	@Value("${external.ressource:}")
	private transient String externalRessource;

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

	@Value("${assistance.documentation.url.candidat:}")
	private transient String assistDocUrlCand;

	@Value("${assistance.documentation.url.candidat.en:}")
	private transient String assistDocUrlCandEn;

	@Value("${assistance.documentation.url:}")
	private transient String assistDocUrl;

	@Value("${assistance.helpdesk.url:}")
	private transient String assistHelpdeskUrl;

	@Value("${assistance.contact.mail:}")
	private transient String assistContactMail;

	@Value("${assistance.contact.url:}")
	private transient String assistContactUrl;

	/**
	 * Permet de précharger au démarrage les caches
	 */
	public void loadConfigCache() {
		self.getFaviconBase64();
		self.getLogoRessource();
		self.getXDocReportTemplate(ConstanteUtils.TEMPLATE_DOSSIER, null, null);
		self.getConfigEtab();
		try {
			self.getPropertiesPegase();
			self.getConfigPegaseAuthEtab();
			self.getConfigPegaseUrl();
		} catch (final Exception e) {
			//Au démarrage de l'application, si la config n'a pas été enregistrée, il ne faut pas lever d'erreur
		}
	}

	/**
	 * @return le fichier de properties
	 */
	@Cacheable(value = CacheConfig.CACHE_CONF_RESSOURCE, cacheManager = CacheConfig.CACHE_MANAGER_NAME)
	public Properties getPropertiesPegase() {
		final Properties properties = new Properties();

		/* On cherche le fichier de properties dans le filesystem avec le paramètre système PROPERTY_FILE_PATH */
		if (StringUtils.isNotBlank(externalRessource)) {
			final String configLocation = externalRessource + ConstanteUtils.EXTERNAL_RESSOURCE_SISCOL_FOLDER + File.separator;
			try {
				FileInputStream file;
				if (Files.isDirectory(Paths.get(configLocation))) {
					/* Dans ce cas on est dans un dossier et on recherche configUrlServices.properties dans ce dossier */
					file = new FileInputStream(configLocation + ConstanteUtils.PROPERTY_FILE_PEGASE_URL);
				} else {
					/* Dans ce cas le fichier est déclaré */
					file = new FileInputStream(configLocation);
				}
				properties.load(file);
				file.close();
				logger.debug("Chargement du fichier configUrlServicesPegase.properties sur le fileSystem termine");
				return properties;
			} catch (final Exception e) {
				throw new RuntimeException(
					"Impossible de charger le fichier configUrlServicesPegase.properties, ajoutez le dans le dossier ressources ou ajoutez le paramètre configUrlServices.location au lancement de la JVM", e);
			}
		}

		/* Si on ne le trouve pas, on cherche le fichier de properties dans le classpath */
		try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(ConstanteUtils.PROPERTY_FILE_PEGASE_URL));
			logger.debug("Chargement du fichier configUrlServicesPegase.properties dans le classpath termine");
			return properties;
		} catch (final Exception e) {
			throw new RuntimeException("Impossible de charger le fichier configUrlServicesPegase.properties, ajoutez le dans le dossier ressources ou ajoutez le paramètre configUrlServices.location au lancement de la JVM",
				e);
		}
	}

	/**
	 * @return le favicon (filesystem ou classpath)
	 */
	@Cacheable(value = CacheConfig.CACHE_CONF_RESSOURCE, cacheManager = CacheConfig.CACHE_MANAGER_NAME)
	public String getFaviconBase64() {
		try {
			final File fileExternal = MethodUtils.getExternalResource(externalRessource,
				ConstanteUtils.EXTERNAL_RESSOURCE_IMG_FOLDER,
				ConstanteUtils.EXTERNAL_RESSOURCE_IMG_FAV_FILE);
			if (fileExternal != null) {
				final byte[] fileContent = FileUtils.readFileToByteArray(fileExternal);
				logger.debug("Chargement du fichier favicon termine");
				return Base64.getEncoder().encodeToString(fileContent);
			}
		} catch (final Exception e) {
		}
		return null;
	}

	/**
	 * @return le logo (filesystem ou classpath)
	 */
	@Cacheable(value = CacheConfig.CACHE_CONF_RESSOURCE, cacheManager = CacheConfig.CACHE_MANAGER_NAME)
	public com.vaadin.server.Resource getLogoRessource() {
		try {
			final File fileExternal = MethodUtils.getExternalResource(externalRessource, ConstanteUtils.EXTERNAL_RESSOURCE_IMG_FOLDER, ConstanteUtils.EXTERNAL_RESSOURCE_IMG_LOGO_FILE);
			if (fileExternal != null) {
				logger.debug("Chargement du fichier logo termine");
				return new FileResource(fileExternal);
			}
		} catch (final Exception e) {
		}
		return new ThemeResource("logo.png");
	}

	/**
	 * @param  fileNameDefault
	 * @param  codeLangue
	 * @param  codLangueDefault
	 * @return                  le template XDocReport
	 */
	public byte[] getXDocReportTemplate(final String fileNameDefault, final String codeLangue, final String codLangueDefault) {
		return self.getXDocReportTemplate(fileNameDefault, codeLangue, codLangueDefault, null);
	}

	/**
	 * @param  fileNameDefault
	 * @param  codeLangue
	 * @param  codLangueDefault
	 * @param  subPath
	 * @param  suffixe
	 * @return                  le template XDocReport
	 */
	public byte[] getXDocReportTemplate(final String fileNameDefault, final String codeLangue, final String codLangueDefault, final String subPath, final String suffixe) {
		/* On cherche le fichier du suffixe "séparé par _ " */
		byte[] in = self.getXDocReportTemplate(fileNameDefault + "_" + suffixe, codeLangue, codLangueDefault, subPath);

		/* Si il n'existe pas on renvoit le fichier par défaut */
		if (in == null) {
			in = self.getXDocReportTemplate(fileNameDefault, codeLangue, codLangueDefault);
		}
		return in;
	}

	/**
	 * @param  fileNameDefault
	 * @param  codeLangue
	 * @param  codLangueDefault
	 * @param  subPath
	 * @return                  le template XDocReport
	 */
	@Cacheable(value = CacheConfig.CACHE_CONF_RESSOURCE, cacheManager = CacheConfig.CACHE_MANAGER_NAME)
	public byte[] getXDocReportTemplate(final String fileNameDefault, final String codeLangue, final String codLangueDefault, final String subPath) {
		String resourcePath = ConstanteUtils.TEMPLATE_PATH + File.separator;
		if (subPath != null) {
			resourcePath = resourcePath + subPath + File.separator;
		}
		final String extension = ConstanteUtils.TEMPLATE_EXTENSION;

		/* Recherche dans les ressources externes */
		/* On essaye de trouver le template lié à la langue */
		if (codeLangue != null && !codeLangue.equals(codLangueDefault)) {
			final File fileExternal = MethodUtils.getExternalResource(externalRessource, resourcePath + fileNameDefault + "_" + codeLangue + extension);
			if (fileExternal != null) {
				try {
					logger.debug("Demande de template FileSystem : " + fileExternal.getAbsolutePath());
					return FileUtils.readFileToByteArray(fileExternal);
				} catch (final Exception e) {
				}
			}
		}

		/* Template langue non trouvé, on utilise le template par défaut */
		final File fileExternal = MethodUtils.getExternalResource(externalRessource, resourcePath + fileNameDefault + extension);
		if (fileExternal != null) {
			try {
				logger.debug("Demande de template FileSystem : " + fileExternal.getAbsolutePath());
				return FileUtils.readFileToByteArray(fileExternal);
			} catch (final Exception e) {
			}
		}

		/* Recherche dans le classpath */
		/* On essaye de trouver le template lié à la langue */
		if (codeLangue != null && !codeLangue.equals(codLangueDefault)) {
			try {
				final InputStream in = new ClassPathResource(resourcePath + fileNameDefault + "_" + codeLangue + extension).getInputStream();
				if (in != null) {
					logger.debug("Demande de template Classpath : " + resourcePath + fileNameDefault + "_" + codeLangue + extension);
					return in.readAllBytes();
				}
			} catch (final Exception e) {
			}
		}

		/* Template langue non trouvé, on utilise le template par défaut */
		try {
			final InputStream in = new ClassPathResource(resourcePath + fileNameDefault + extension).getInputStream();
			if (in != null) {
				logger.debug("Demande de template Classpath : " + resourcePath + fileNameDefault + extension);
				return in.readAllBytes();
			}
		} catch (final Exception e) {
		}
		String error = "Impossible de trouver le fichier " + fileNameDefault + (codeLangue != null ? ("_" + codeLangue) : "") + extension;
		if (codeLangue != null) {
			error = error + " ni le fichier " + fileNameDefault + extension;
		}
		throw new RuntimeException(error + ". Ajoutez le dans le dossier ressources externe ou dans le classpath.");
	}

	/**
	 * @param  list
	 * @param  code
	 * @return      une ligne de configuration par son code
	 */
	public String getConfigurationByCod(final List<Configuration> list, final String code) {
		return list.stream().filter(e -> e.getCodConfig().equals(code)).findFirst().map(Configuration::getValConfig).orElse(null);
	}

	/**
	 * @return la config etablissement
	 */
	public ConfigEtab loadConfigEtab() {
		final List<Configuration> list = configurationRepository.findByCodConfigStartsWith(Configuration.COD_CONFIG_ETAB);
		final ConfigEtab config = new ConfigEtab();
		config.setAssistDocUrl(getConfigurationByCod(list, Configuration.COD_CONFIG_ETAB_ASSIST_DOC_URL));
		config.setAssistDocUrlCand(getConfigurationByCod(list, Configuration.COD_CONFIG_ETAB_ASSIST_DOC_URL_CAND));
		config.setAssistDocUrlCandEn(getConfigurationByCod(list, Configuration.COD_CONFIG_ETAB_ASSIST_DOC_URL_CAND_EN));
		config.setAssistHelpdeskUrl(getConfigurationByCod(list, Configuration.COD_CONFIG_ETAB_ASSIST_HELPDESK_URL));
		config.setAssistContactMail(getConfigurationByCod(list, Configuration.COD_CONFIG_ETAB_ASSIST_CONTACT_MAIL));
		config.setAssistContactUrl(getConfigurationByCod(list, Configuration.COD_CONFIG_ETAB_ASSIST_CONTACT_URL));
		return config;
	}

	/**
	 * @param  order
	 * @param  code
	 * @param  msgCode
	 * @return         la liste de bean version
	 */
	public List<SimpleTablePresentation> getConfigEtabPresentation() {
		final List<SimpleTablePresentation> list = new ArrayList<>();
		final ConfigEtab config = loadConfigEtab();
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_ETAB_ASSIST_DOC_URL, applicationContext.getMessage("config.etab.table.assistDocUrl", null, UI.getCurrent().getLocale()), config.getAssistDocUrl()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_ETAB_ASSIST_DOC_URL_CAND, applicationContext.getMessage("config.etab.table.assistDocUrlCand", null, UI.getCurrent().getLocale()),
			config.getAssistDocUrlCand()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_ETAB_ASSIST_DOC_URL_CAND_EN, applicationContext.getMessage("config.etab.table.assistDocUrlCandEn", null, UI.getCurrent().getLocale()),
			config.getAssistDocUrlCandEn()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_ETAB_ASSIST_HELPDESK_URL, applicationContext.getMessage("config.etab.table.assistHelpdeskUrl", null, UI.getCurrent().getLocale()),
			config.getAssistHelpdeskUrl()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_ETAB_ASSIST_CONTACT_MAIL, applicationContext.getMessage("config.etab.table.assistContactMail", null, UI.getCurrent().getLocale()),
			config.getAssistContactMail()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_ETAB_ASSIST_CONTACT_URL, applicationContext.getMessage("config.etab.table.assistContactUrl", null, UI.getCurrent().getLocale()),
			config.getAssistContactUrl()));
		return list;
	}

	/**
	 * Enregistre la config de l'etablissement
	 * @param configPegaseUrl
	 */
	public void saveConfigEtab(final ConfigEtab config) {
		/* Enregistrement Url assistance */
		saveConfigEtabItem(config.getAssistDocUrl(), Configuration.COD_CONFIG_ETAB_ASSIST_DOC_URL);

		/* Enregistrement Url assistance candidat */
		saveConfigEtabItem(config.getAssistDocUrlCand(), Configuration.COD_CONFIG_ETAB_ASSIST_DOC_URL_CAND);

		/* Enregistrement Url assistance candidat anglais */
		saveConfigEtabItem(config.getAssistDocUrlCandEn(), Configuration.COD_CONFIG_ETAB_ASSIST_DOC_URL_CAND_EN);

		/* Enregistrement Url helpdesk */
		saveConfigEtabItem(config.getAssistHelpdeskUrl(), Configuration.COD_CONFIG_ETAB_ASSIST_HELPDESK_URL);

		/* Enregistrement Contact Mail */
		saveConfigEtabItem(config.getAssistContactMail(), Configuration.COD_CONFIG_ETAB_ASSIST_CONTACT_MAIL);

		/* Enregistrement Contact Url */
		saveConfigEtabItem(config.getAssistContactUrl(), Configuration.COD_CONFIG_ETAB_ASSIST_CONTACT_URL);

		Notification.show(applicationContext.getMessage("config.save", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		cacheController.invalidConfCache(true);
	}

	/**
	 * Enregistre un item de conf etab
	 * @param value
	 * @param code
	 */
	private void saveConfigEtabItem(final String value, final String code) {
		if (StringUtils.isBlank(value)) {
			final Configuration configValue = configurationRepository.findById(code).orElse(null);
			if (configValue != null) {
				configurationRepository.delete(configValue);
			}
		} else {
			configurationRepository.saveAndFlush(new Configuration(code, value));
		}
	}

	/**
	 * @return la configuration Pegase
	 */
	@Cacheable(value = CacheConfig.CACHE_CONF_ETAB, cacheManager = CacheConfig.CACHE_MANAGER_NAME)
	public ConfigEtab getConfigEtab() {
		final ConfigEtab config = loadConfigEtab();
		/* Page d'assistance */
		if (StringUtils.isBlank(config.getAssistDocUrl())) {
			config.setAssistDocUrl(assistDocUrl);
		}
		/* Page d'assistance candidat */
		if (StringUtils.isBlank(config.getAssistDocUrlCand())) {
			config.setAssistDocUrlCand(assistDocUrlCand);
		}

		/* Page d'assistance candidat anglais */
		if (StringUtils.isBlank(config.getAssistDocUrlCandEn())) {
			config.setAssistDocUrlCandEn(assistDocUrlCandEn);
		}

		/* Page helpdesk */
		if (StringUtils.isBlank(config.getAssistHelpdeskUrl())) {
			config.setAssistHelpdeskUrl(assistHelpdeskUrl);
		}

		/* Page contact mail */
		if (StringUtils.isBlank(config.getAssistContactMail())) {
			config.setAssistContactMail(assistContactMail);
		}

		/* Page contact url */
		if (StringUtils.isBlank(config.getAssistContactUrl())) {
			config.setAssistContactUrl(assistContactUrl);
		}

		return config;
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
		cacheController.invalidConfCache(true);
	}

	/**
	 * @return la configuration d'authentification
	 */
	@Cacheable(value = CacheConfig.CACHE_CONF_ETAB, cacheManager = CacheConfig.CACHE_MANAGER_NAME)
	public ConfigPegaseAuthEtab getConfigPegaseAuthEtab() {
		if (!siScolService.isImplementationPegase()) {
			return null;
		}
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
				configPegaseAuthEtabProp.setUrl(self.getPropertiesPegase().getProperty(ConstanteUtils.PEGASE_URL_AUTH));
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
		config.setIns(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_INS));
		config.setInsExt(getConfigurationByCod(list, Configuration.COD_CONFIG_PEGASE_URL_INS_EXT));
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
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_INS, applicationContext.getMessage("config.pegaseUrl.table.ins", null, UI.getCurrent().getLocale()), config.getIns()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_INS_EXT, applicationContext.getMessage("config.pegaseUrl.table.insExt", null, UI.getCurrent().getLocale()), config.getInsExt()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_ODF, applicationContext.getMessage("config.pegaseUrl.table.odf", null, UI.getCurrent().getLocale()), config.getOdf()));
		list.add(new SimpleTablePresentation(Configuration.COD_CONFIG_PEGASE_URL_REF, applicationContext.getMessage("config.pegaseUrl.table.ref", null, UI.getCurrent().getLocale()), config.getRef()));
		return list;
	}

	/**
	 * Enregistre la config Pégase
	 * @param configPegaseUrl
	 */
	public void saveConfigPegaseUrl(final ConfigPegaseUrl configPegaseUrl) {
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_COC, configPegaseUrl.getCoc()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_INS, configPegaseUrl.getIns()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_INS_EXT, configPegaseUrl.getInsExt()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_ODF, configPegaseUrl.getOdf()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_REF, configPegaseUrl.getRef()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_PARAM_TEST_COD_ETU, configPegaseUrl.getParamTestCodEtu()));
		configurationRepository.saveAndFlush(new Configuration(Configuration.COD_CONFIG_PEGASE_URL_PARAM_TEST_COD_FORMATION, configPegaseUrl.getParamTestCodFormation()));
		Notification.show(applicationContext.getMessage("config.save", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		cacheController.invalidConfCache(true);
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
	@Cacheable(value = CacheConfig.CACHE_CONF_ETAB, cacheManager = CacheConfig.CACHE_MANAGER_NAME)
	public ConfigPegaseUrl getConfigPegaseUrl() {
		if (!siScolService.isImplementationPegase()) {
			return null;
		}
		final ConfigPegaseUrl configDb = loadConfigPegaseUrl();
		if (configDb != null && configDb.isValid()) {
			return configDb;
		} else {
			final ConfigPegaseUrl configPegaseUrlProp = new ConfigPegaseUrl();
			/* On cherche le fichier de properties dans le classpath */
			try {
				final Properties properties = self.getPropertiesPegase();
				configPegaseUrlProp.setCoc(properties.getProperty(ConstanteUtils.PEGASE_URL_COC));
				configPegaseUrlProp.setIns(properties.getProperty(ConstanteUtils.PEGASE_URL_INS));
				configPegaseUrlProp.setInsExt(properties.getProperty(ConstanteUtils.PEGASE_URL_INS_EXT));
				configPegaseUrlProp.setRef(properties.getProperty(ConstanteUtils.PEGASE_URL_REF));
				configPegaseUrlProp.setOdf(properties.getProperty(ConstanteUtils.PEGASE_URL_ODF));
				if (configPegaseUrlProp.isValid()) {
					return configPegaseUrlProp;
				}
				throw new RuntimeException("Fichier de configuration Pégase incomplet, vérifiez le fichier de configuration");
			} catch (final Exception e) {
				throw new RuntimeException("Impossible de charger la configuration Pégase", e);
			}
		}
	}

	public List<ConfigStaticTablePresentation> getConfigStaticPresentation() {
		final List<ConfigStaticTablePresentation> list = new ArrayList<>();

		/* Fichier de logo */
		list.add(new ConfigStaticTablePresentation(Configuration.COD_CONFIG_ETAB_STATIC_LOGO, ConstanteUtils.EXTERNAL_RESSOURCE_IMG_LOGO_FILE,
			applicationContext.getMessage("config.etab.static.logo", null, UI.getCurrent().getLocale()), externalRessource,
			ConstanteUtils.EXTERNAL_RESSOURCE_IMG_FOLDER, "png"));

		/* Fichier de favicon */
		list.add(new ConfigStaticTablePresentation(Configuration.COD_CONFIG_ETAB_STATIC_FAVICON, ConstanteUtils.EXTERNAL_RESSOURCE_IMG_FAV_FILE,
			applicationContext.getMessage("config.etab.static.favicon", null, UI.getCurrent().getLocale()), externalRessource,
			ConstanteUtils.EXTERNAL_RESSOURCE_IMG_FOLDER, "ico"));

		/* Fichier de message */
		list.add(new ConfigStaticTablePresentation(Configuration.COD_CONFIG_ETAB_STATIC_MESSAGE, ConstanteUtils.EXTERNAL_RESSOURCE_I18N_FILE,
			applicationContext.getMessage("config.etab.static.msg", null, UI.getCurrent().getLocale()), externalRessource,
			ConstanteUtils.EXTERNAL_RESSOURCE_I18N_FOLDER, "properties"));

		/* Fichier de message langue anglaise */
		list.add(new ConfigStaticTablePresentation(Configuration.COD_CONFIG_ETAB_STATIC_MESSAGE_EN, ConstanteUtils.EXTERNAL_RESSOURCE_I18N_EN_FILE,
			applicationContext.getMessage("config.etab.static.msg-en", null, UI.getCurrent().getLocale()), externalRessource,
			ConstanteUtils.EXTERNAL_RESSOURCE_I18N_FOLDER, "properties"));

		/* Fichier de template dossier */
		list.add(new ConfigStaticTablePresentation(Configuration.COD_CONFIG_ETAB_STATIC_TEMPLATE_DOSSIER, ConstanteUtils.TEMPLATE_DOSSIER + ConstanteUtils.TEMPLATE_EXTENSION,
			applicationContext.getMessage("config.etab.static.dossier", null, UI.getCurrent().getLocale()), externalRessource,
			ConstanteUtils.TEMPLATE_PATH, "docx"));

		/* Fichier de template de lettre d'admission */
		list.add(new ConfigStaticTablePresentation(Configuration.COD_CONFIG_ETAB_STATIC_TEMPLATE_LETTRE_ADM, ConstanteUtils.TEMPLATE_LETTRE_ADM + ConstanteUtils.TEMPLATE_EXTENSION,
			applicationContext.getMessage("config.etab.static.lettre-adm", null, UI.getCurrent().getLocale()), externalRessource,
			ConstanteUtils.TEMPLATE_PATH, "docx"));

		/* Fichier de template de lettre de refus */
		list.add(new ConfigStaticTablePresentation(Configuration.COD_CONFIG_ETAB_STATIC_TEMPLATE_LETTRE_REFUS, ConstanteUtils.TEMPLATE_LETTRE_REFUS + ConstanteUtils.TEMPLATE_EXTENSION,
			applicationContext.getMessage("config.etab.static.lettre-refus", null, UI.getCurrent().getLocale()), externalRessource,
			ConstanteUtils.TEMPLATE_PATH, "docx"));

		/* Fichier de config SiScol Apogée */
		list.add(new ConfigStaticTablePresentation(Configuration.COD_CONFIG_ETAB_STATIC_SISCOL_CONFIG_APO, ConstanteUtils.WS_APOGEE_PROP_FILE + ConstanteUtils.WS_APOGEE_PROP_EXTENSION,
			applicationContext.getMessage("config.etab.static.siscol-config-apo", null, UI.getCurrent().getLocale()), externalRessource,
			ConstanteUtils.EXTERNAL_RESSOURCE_SISCOL_FOLDER, "properties"));
		return list;
	}

	public FileCustom createExternalFileFromUpload(final ByteArrayInOutStream file, final ConfigStaticTablePresentation ressourceStatic) throws FileException {
		String path = externalRessource
			+ (externalRessource.endsWith(File.separator) ? "" : File.separator)
			+ File.separator
			+ ressourceStatic.getExternalRessourceFolder()
			+ File.separator;

		/* Creation du chemin de fichiers */
		try {
			final Path directory = Paths.get(path);
			Files.createDirectories(directory);
		} catch (final IOException e) {
			logger.error("Stockage de fichier - FileSystem : erreur de creation du fichier ", e);
			MethodUtils.closeRessource(file);
			throw new FileException(applicationContext.getMessage("file.error.create", null, UI.getCurrent().getLocale()), e);
		}

		path = path + ressourceStatic.getFileName();
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(path);
			file.writeTo(outputStream);
		} catch (final Exception e) {
			logger.error("Stockage de fichier - FileSystem : erreur de creation du fichier ", e);
			throw new FileException(applicationContext.getMessage("file.error.create", null, UI.getCurrent().getLocale()), e);
		} finally {
			MethodUtils.closeRessource(outputStream);
			MethodUtils.closeRessource(file);
		}
		/* On vérifie que le fichier a bien été créé, sinon, erreur!! */
		if (!MethodUtils.checkFileExists(path)) {
			throw new FileException(applicationContext.getMessage("file.error.create", null, UI.getCurrent().getLocale()));
		}
		cacheController.invalidRessourcesCache(true);
		return new FileCustom(ressourceStatic.getFileName());
	}

	public void deleteExternalFile(final ConfigStaticTablePresentation ressourceStatic) {
		ressourceStatic.getFile().delete();
		cacheController.invalidRessourcesCache(true);
	}
}
