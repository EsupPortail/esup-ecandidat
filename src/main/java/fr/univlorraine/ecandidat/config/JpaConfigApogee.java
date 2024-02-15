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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import fr.univlorraine.ecandidat.entities.siscol.apogee.AnneeUni;
import fr.univlorraine.ecandidat.utils.JpaConfigApogeeCondition;

/**
 * Configuration JPA
 * @author Kevin Hergalant
 */
@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@Conditional(JpaConfigApogeeCondition.class)
public class JpaConfigApogee {

	public final static String PERSISTENCE_UNIT_NAME = "pun-jpa-apogee";

	private final Logger logger = LoggerFactory.getLogger(JpaConfigApogee.class);

	@Value("${showSql:false}")
	private transient Boolean showSql;

	@Value("${datasource.apogee.url:}")
	private transient String dataSourceUrl;

	@Value("${datasource.apogee.username:}")
	private transient String dataSourceUserName;

	@Value("${datasource.apogee.password:}")
	private transient String dataSourcePassword;

	@Value("${datasource.apogee.driver-class-name:}")
	private transient String driverClassName;

	@Value("${datasource.apogee.properties.max-pool-size:}")
	private transient String maximumPoolSize;

	@Value("${datasource.apogee.properties.test-query:}")
	private transient String connectionTestQuery;

	/**
	 * @return Source de données
	 */
	@Bean(name = "dataSourceApogee")
	public DataSource dataSourceApogee() {
		if (StringUtils.isNotBlank(dataSourceUrl)) {
			logger.info("Manually datasource configuration...");
			final HikariConfig hikariConfig = new HikariConfig();
			hikariConfig.setPoolName("poolHikariApogee");
			hikariConfig.setJdbcUrl(dataSourceUrl);
			hikariConfig.setUsername(dataSourceUserName);
			hikariConfig.setPassword(dataSourcePassword);

			/* Driver */
			if (StringUtils.isNotBlank(driverClassName)) {
				hikariConfig.setDriverClassName(driverClassName);
			} else {
				hikariConfig.setDriverClassName("oracle.jdbc.OracleDriver");
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
			logger.info("Automatic datasource Apogee configuration...");
			final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
			return dsLookup.getDataSource("java:/comp/env/jdbc/dbSiScol");
		}
	}

	/**
	 * @return EntityManager Factory
	 */
	@Bean(name = "entityManagerFactoryApogee")
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryApogee() {
		final DataSource ds = dataSourceApogee();

		final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
		localContainerEntityManagerFactoryBean.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		localContainerEntityManagerFactoryBean.setPackagesToScan(AnneeUni.class.getPackage().getName());
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
	@Bean(name = "transactionManagerApogee")
	@Primary
	public JpaTransactionManager transactionManagerApogee(final EntityManagerFactory entityManagerFactoryApogee) {
		return new JpaTransactionManager(entityManagerFactoryApogee);
	}
}
