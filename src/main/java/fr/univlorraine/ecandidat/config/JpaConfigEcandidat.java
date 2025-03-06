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

import java.util.Properties;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.tools.LocalTimePersistenceConverter;
import fr.univlorraine.ecandidat.repositories.CandidatRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.migration.FlywayCallbackMigration;

/**
 * Configuration JPA
 * @author Adrien Colson
 */
@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EnableJpaRepositories(basePackageClasses = CandidatRepository.class, entityManagerFactoryRef = "entityManagerFactoryEcandidat", transactionManagerRef = "transactionManagerEcandidat")
public class JpaConfigEcandidat {

	public final static String PERSISTENCE_UNIT_NAME = "pun-jpa-ecandidat";

	private final Logger logger = LoggerFactory.getLogger(JpaConfigEcandidat.class);

	@Value("${showSql:false}")
	private transient Boolean showSql;

	@Value("${datasource.ecandidat.url:}")
	private transient String dataSourceUrl;

	@Value("${datasource.ecandidat.username:}")
	private transient String dataSourceUserName;

	@Value("${datasource.ecandidat.password:}")
	private transient String dataSourcePassword;

	@Value("${datasource.ecandidat.driver-class-name:}")
	private transient String driverClassName;

	@Value("${datasource.ecandidat.properties.max-pool-size:}")
	private transient String maximumPoolSize;

	@Value("${datasource.ecandidat.properties.test-query:}")
	private transient String connectionTestQuery;

	@Value("${load.balancing.gestionnaire.mode:true}")
	private transient Boolean gestionnaireMode;

	/**
	 * @return Source de données
	 */
	@Bean
	@Primary
	public DataSource dataSourceEcandidat() {
		if (StringUtils.isNotBlank(dataSourceUrl)) {
			logger.info("Manually datasource configuration...");
			final HikariConfig hikariConfig = new HikariConfig();
			hikariConfig.setPoolName("poolHikariEcandidat");
			hikariConfig.setJdbcUrl(dataSourceUrl);
			hikariConfig.setUsername(dataSourceUserName);
			hikariConfig.setPassword(dataSourcePassword);

			/* Driver */
			if (StringUtils.isNotBlank(driverClassName)) {
				hikariConfig.setDriverClassName(driverClassName);
			} else {
				hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
			}

			/* Gestion des properties */

			/* Max pool Size */
			if (StringUtils.isNotBlank(maximumPoolSize)) {
				try {
					hikariConfig.setMaximumPoolSize(Integer.parseInt(maximumPoolSize));
				} catch (final Exception e) {
				}
			}
			/* Test Query */
			if (StringUtils.isNotBlank(connectionTestQuery)) {
				hikariConfig.setConnectionTestQuery(connectionTestQuery);
			}
			return new HikariDataSource(hikariConfig);
		} else {
			logger.info("Automatic datasource configuration...");
			final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
			return dsLookup.getDataSource("java:/comp/env/jdbc/dbEcandidat");
		}
	}

	/**
	 * Initialise Flyway
	 * @param ds
	 */
	private void initFlyway(final DataSource ds) {
		if (!gestionnaireMode) {
			logger.info("Database analysis canceled in candidat mode");
			return;
		}
		try {
			logger.info("Database analysis: in progress...");
			final Flyway flyway = new Flyway();
			flyway.setDataSource(ds);
			flyway.setCallbacks(new FlywayCallbackMigration());
			flyway.setBaselineOnMigrate(true);
			flyway.setValidateOnMigrate(true);
			flyway.repair();
			flyway.migrate();
			logger.info("Database analysis: finish...");
		} catch (final Exception e) {
			logger.error("Database analysis: ERROR", e);
			throw e;
		}
	}

	/**
	 * @return EntityManager Factory
	 */
	@Bean(name = "entityManagerFactoryEcandidat")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryEcandidat() {
		final DataSource ds = dataSourceEcandidat();
		/* Si l'appli s'initialise, il faut lancer Flyway */
		/**
		 * TODO:problème avec tomcat8 qui reinitialise les beans au shutdown et met
		 * flyway en erreur
		 */
		final String init = System.getProperty(ConstanteUtils.STARTUP_INIT_FLYWAY);
		if (init == null || !init.equals(ConstanteUtils.STARTUP_INIT_FLYWAY_OK)) {
			initFlyway(ds);
			System.setProperty(ConstanteUtils.STARTUP_INIT_FLYWAY, ConstanteUtils.STARTUP_INIT_FLYWAY_OK);
		}

		final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		localContainerEntityManagerFactoryBean.setPackagesToScan(Candidat.class.getPackage().getName(),
			LocalTimePersistenceConverter.class.getPackage().getName());
		localContainerEntityManagerFactoryBean.setDataSource(ds);
		localContainerEntityManagerFactoryBean.setJpaDialect(new EclipseLinkJpaDialect());

		final Properties jpaProperties = new Properties();
		/* Active le static weaving d'EclipseLink */
		jpaProperties.put(PersistenceUnitProperties.WEAVING, "static");
		/* Désactive le cache partagé */
		jpaProperties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, String.valueOf(false));
		localContainerEntityManagerFactoryBean.setJpaProperties(jpaProperties);

		final EclipseLinkJpaVendorAdapter jpaVendorAdapter = new EclipseLinkJpaVendorAdapter();
		jpaVendorAdapter.setGenerateDdl(false);
		jpaVendorAdapter.setShowSql(showSql);
		localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);

		return localContainerEntityManagerFactoryBean;
	}

	/**
	 * @return Transaction Manager
	 */
	@Bean(name = "transactionManagerEcandidat")
	@Primary
	public JpaTransactionManager transactionManagerEcandidat(final EntityManagerFactory entityManagerFactoryEcandidat) {
		return new JpaTransactionManager(entityManagerFactoryEcandidat);
	}
}
