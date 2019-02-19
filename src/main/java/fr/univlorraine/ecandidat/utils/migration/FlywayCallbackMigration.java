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
package fr.univlorraine.ecandidat.utils.migration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.CRC32;

import org.flywaydb.core.api.MigrationType;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.callback.BaseFlywayCallback;
import org.flywaydb.core.internal.dbsupport.DbSupport;
import org.flywaydb.core.internal.dbsupport.DbSupportFactory;
import org.flywaydb.core.internal.dbsupport.JdbcTemplate;
import org.flywaydb.core.internal.dbsupport.Schema;
import org.flywaydb.core.internal.metadatatable.MetaDataTable;
import org.flywaydb.core.internal.metadatatable.MetaDataTableImpl;
import org.flywaydb.core.internal.resolver.MigrationInfoHelper;
import org.flywaydb.core.internal.resolver.ResolvedMigrationImpl;
import org.flywaydb.core.internal.util.FileCopyUtils;
import org.flywaydb.core.internal.util.Pair;
import org.flywaydb.core.internal.util.jdbc.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univlorraine.ecandidat.entities.ecandidat.SchemaVersion;

/**
 * Callback permettant de faire un traitement lors de la migration FlyWay
 *
 * @author Kevin Hergalant
 */
public class FlywayCallbackMigration extends BaseFlywayCallback {

	private Logger logger = LoggerFactory.getLogger(FlywayCallbackMigration.class);
	private DbSupport dbSupport;
	private JdbcTemplate jdbcTemplate;

	@Override
	public void beforeValidate(final Connection connection) {
		super.beforeValidate(connection);

		/* On récupère les infos de connexion */
		dbSupport = DbSupportFactory.createDbSupport(connection, false);
		if (dbSupport == null) {
			throw new RuntimeException(new MigrationException("Erreur de connection"));
		}

		/* On récupère les infos la table de versions */
		@SuppressWarnings("rawtypes")
		Schema[] schemas = new Schema[flywayConfiguration.getSchemas().length];
		for (int i = 0; i < flywayConfiguration.getSchemas().length; i++) {
			schemas[i] = dbSupport.getSchema(flywayConfiguration.getSchemas()[i]);
		}

		MetaDataTable metaDataTable = new MetaDataTableImpl(dbSupport,
				schemas[0].getTable(flywayConfiguration.getTable()), null);

		/* On récupère les lignes de version */
		String query = "SELECT " + dbSupport.quote("installed_rank")
				+ "," + dbSupport.quote("description")
				+ "," + dbSupport.quote("version")
				+ "," + dbSupport.quote("script")
				+ "," + dbSupport.quote("installed_on")
				+ "," + dbSupport.quote("success")
				+ " FROM " + flywayConfiguration.getTable();

		try {
			if (metaDataTable.hasAppliedMigrations()) {
				jdbcTemplate = new JdbcTemplate(connection, Types.VARCHAR);
				if (jdbcTemplate == null) {
					throw new RuntimeException(new MigrationException("Erreur de connection"));
				}

				/* Clean database suite erreur de script */
				cleanErrorColumn();

				/* On récupère les schéma version */
				List<SchemaVersion> listeSchemaVersion = jdbcTemplate.query(query, new RowMapper<SchemaVersion>() {

					@Override
					public SchemaVersion mapRow(final ResultSet rs) throws SQLException {
						SchemaVersion sv = new SchemaVersion();
						sv.setInstalledRank(rs.getInt("installed_rank"));
						sv.setDescription(rs.getString("description"));
						sv.setVersion(rs.getString("version"));
						sv.setScript(rs.getString("script"));
						Timestamp ts = rs.getTimestamp("installed_on");
						sv.setInstalledOn(ts != null ? ts.toLocalDateTime() : null);
						sv.setSuccess(rs.getBoolean("success"));
						return sv;
					}
				});
				RealeaseVersion lastVersion = null;
				LocalDateTime v1InstalledOn = null;
				Integer nbMigration = 0;
				for (SchemaVersion schemaVersion : listeSchemaVersion) {
					if (schemaVersion.getSuccess()) {
						RealeaseVersion v = new RealeaseVersion(schemaVersion.getVersion());
						if (v.isGreatherThan(lastVersion)) {
							lastVersion = v;
						}
						if (schemaVersion.getVersion().equals("1")) {
							v1InstalledOn = schemaVersion.getInstalledOn();
						}
					} else {
						throw new MigrationException("Une de vos migration est en erreur!! Consultez votre table schema_version");
					}
					nbMigration++;
				}

				RealeaseVersion v2132 = new RealeaseVersion("2.1.3.2");
				RealeaseVersion v22014 = new RealeaseVersion("2.2.0.14");

				if (lastVersion == null) {
					throw new MigrationException("Vous avez des migrations mais aucune version! Consultez votre table schema_version");
				}

				/* Interdit d'être en dessous de la V2.1.3 */
				if (lastVersion.isLessThan(v2132)) {
					throw new MigrationException("Il est interdit de migrer à partir d'une version inférieur à 2.1.3.2, veuillez installer la version 2.1.3");
				} else if (lastVersion.isEqualThan(v2132)) {
					Integer nbMigrationExpected = 35;
					if (nbMigration.equals(nbMigrationExpected)) {
						logger.info("Migration de vos scripts sql v2.1.3.2-->v2.0.0");
						initBaseline(v1InstalledOn);
						return;
					} else {
						throw new MigrationException("Le nombre de vos migations est en erreur, attendu : " + nbMigrationExpected + ", trouvé : " + nbMigration);
					}
				} else if (lastVersion.isEqualThan(v22014)) {
					Integer nbMigrationExpected = 49;
					if (nbMigration.equals(nbMigrationExpected)) {
						logger.info("Migration de vos scripts sql v2.2.0.14-->v2.0.0");
						initV2(v1InstalledOn);
						return;
					} else {
						throw new MigrationException("Le nombre de vos migations est en erreur, attendu : " + nbMigrationExpected + ", trouvé : " + nbMigration);
					}
				}
			} else {
				logger.info("Nouvelle installation de vos scripts sql -->v2.0.0");
			}
		} catch (SQLException e) {
			throw new MigrationException(e);
		}
	}

	/**
	 * Suppression des entrées dans la table schema_version
	 *
	 * @throws SQLException
	 */
	private void cleanSchemVersionTable() throws SQLException {
		String query = "DELETE FROM " + dbSupport.quote(flywayConfiguration.getTable());
		jdbcTemplate.executeStatement(query);
	}

	/**
	 * Initialisation de la v2.1.0
	 *
	 * @param date
	 * @throws SQLException
	 * @throws MigrationException
	 */
	private void initBaseline(LocalDateTime date) throws SQLException {
		if (date == null) {
			date = LocalDateTime.now();
		}
		cleanSchemVersionTable();
		insertIntoSchemaVersion(1, "V2_1_0__Initialisation 2_1_0.sql", date, 4478);
	}

	/**
	 * Initialisation de la V2.2.0
	 *
	 * @param date
	 * @throws SQLException
	 * @throws MigrationException
	 */
	private void initV2(final LocalDateTime date) throws SQLException {
		initBaseline(date);
		insertIntoSchemaVersion(2, "V2_2_0__Upgrade 2_2_0.sql", LocalDateTime.now(), 1969);
	}

	/**
	 * Insere une version
	 *
	 * @param installedRank
	 * @param scriptFile
	 * @param installedOn
	 * @param executionTime
	 * @throws SQLException
	 * @throws MigrationException
	 */
	private void insertIntoSchemaVersion(final int installedRank, final String scriptFile, final LocalDateTime installedOn, final int executionTime) throws SQLException {
		String path = "/db/migration/";
		Pair<MigrationVersion, String> info =
				MigrationInfoHelper.extractVersionAndDescription(scriptFile, flywayConfiguration.getSqlMigrationPrefix(), flywayConfiguration.getSqlMigrationSeparator(),
						flywayConfiguration.getSqlMigrationSuffix(), false);

		ResolvedMigrationImpl migration = new ResolvedMigrationImpl();
		migration.setVersion(info.getLeft());
		migration.setDescription(info.getRight());
		migration.setScript(scriptFile);
		migration.setType(MigrationType.SQL);

		String query = "INSERT INTO " + dbSupport.quote(flywayConfiguration.getTable())
				+ " VALUES (" + installedRank
				+ ", '" + migration.getVersion() + "'"
				+ ", '" + migration.getDescription() + "'"
				+ ", '" + MigrationType.SQL + "'"
				+ ", '" + scriptFile + "'"
				+ ", " + calculateChecksum(path, scriptFile)
				+ ", 'ecandidat'"
				+ ", '" + installedOn.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'"
				+ ", " + executionTime
				+ ", 1)";
		jdbcTemplate.executeStatement(query);
	}

	/**
	 * Calcule le checksum CRC32 d'un fichier
	 *
	 * @param path
	 * @param fileName
	 * @return le checksum du fichier
	 * @throws MigrationException
	 */
	private int calculateChecksum(final String path, final String fileName) throws MigrationException {
		final CRC32 crc32 = new CRC32();

		try {
			String file = path + fileName;
			InputStream inputStream = getClass().getResourceAsStream(file);
			Reader reader = new InputStreamReader(inputStream, Charset.forName(flywayConfiguration.getEncoding()));

			String str = FileCopyUtils.copyToString(reader);
			inputStream.close();
			BufferedReader bufferedReader = new BufferedReader(new StringReader(str));

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				crc32.update(line.getBytes("UTF-8"));
			}
		} catch (Exception e) {
			throw new MigrationException("Unable to calculate checksum pour " + fileName, e);
		}

		return (int) crc32.getValue();
	}

	/* Clean database */
	private void cleanErrorColumn() {
		try {
			/* Suppression colonne dat_opi_cand, suite erreur script */
			String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE"
					+ " table_schema = '" + dbSupport.getCurrentSchemaName() + "'"
					+ " and table_name = 'candidature' and column_name='dat_opi_cand'";
			List<String> res = jdbcTemplate.queryForStringList(query);
			if (res.size() > 0) {
				jdbcTemplate.executeStatement("ALTER TABLE `candidature` DROP COLUMN `dat_opi_cand`");
				logger.debug("Clean database..");
			}
		} catch (Exception e) {
		}
	}
}
