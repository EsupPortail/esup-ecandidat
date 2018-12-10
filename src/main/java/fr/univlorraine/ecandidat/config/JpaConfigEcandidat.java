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

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.tools.LocalTimePersistenceConverter;
import fr.univlorraine.ecandidat.repositories.CandidatRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.migration.FlywayCallbackMigration;

/**
 * Configuration JPA
 *
 * @author Adrien Colson
 */
@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@EnableJpaRepositories(basePackageClasses = CandidatRepository.class, entityManagerFactoryRef = "entityManagerFactoryEcandidat", transactionManagerRef = "transactionManagerEcandidat")
public class JpaConfigEcandidat {

	public final static String PERSISTENCE_UNIT_NAME = "pun-jpa-ecandidat";

	private Logger logger = LoggerFactory.getLogger(JpaConfigEcandidat.class);

	/**
	 * @return Source de données
	 */
	@Bean
	public DataSource dataSourceEcandidat() {
		JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		return dsLookup.getDataSource("java:/comp/env/jdbc/dbEcandidat");
	}

	/**
	 * Initialise Flyway
	 *
	 * @param ds
	 */
	private void initFlyway(final DataSource ds) {
		try {
			logger.info("Database analysis: in progress...");
			Flyway flyway = new Flyway();
			flyway.setDataSource(ds);
			flyway.setCallbacks(new FlywayCallbackMigration());
			flyway.setBaselineOnMigrate(true);
			flyway.setValidateOnMigrate(true);
			flyway.repair();
			flyway.migrate();
			logger.info("Database analysis: finish...");
		} catch (Exception e) {
			logger.error("Database analysis: ERROR", e);
			throw e;
		}
	}

	/**
	 * @return EntityManager Factory
	 */
	@Bean(name = "entityManagerFactoryEcandidat")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryEcandidat() {
		DataSource ds = dataSourceEcandidat();
		/* Si l'appli s'initialise, il faut lancer Flyway */
		/**
		 * TODO:problème avec tomcat8 qui reinitialise les beans au shutdown et met
		 * flyway en erreur
		 */
		String init = System.getProperty(ConstanteUtils.STARTUP_INIT_FLYWAY);
		if (init == null || !init.equals(ConstanteUtils.STARTUP_INIT_FLYWAY_OK)) {
			initFlyway(ds);
			System.setProperty(ConstanteUtils.STARTUP_INIT_FLYWAY, ConstanteUtils.STARTUP_INIT_FLYWAY_OK);
		}

		LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		localContainerEntityManagerFactoryBean.setPackagesToScan(Candidat.class.getPackage().getName(),
				LocalTimePersistenceConverter.class.getPackage().getName());
		localContainerEntityManagerFactoryBean.setDataSource(ds);
		localContainerEntityManagerFactoryBean.setJpaDialect(new EclipseLinkJpaDialect());

		Properties jpaProperties = new Properties();
		/* Active le static weaving d'EclipseLink */
		jpaProperties.put(PersistenceUnitProperties.WEAVING, "static");
		/* Désactive le cache partagé */
		jpaProperties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, String.valueOf(false));
		localContainerEntityManagerFactoryBean.setJpaProperties(jpaProperties);

		EclipseLinkJpaVendorAdapter jpaVendorAdapter = new EclipseLinkJpaVendorAdapter();
		jpaVendorAdapter.setGenerateDdl(false);
		jpaVendorAdapter.setShowSql(false);
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
