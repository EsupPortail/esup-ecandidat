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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.univlorraine.ecandidat.services.file.FileManager;
import fr.univlorraine.ecandidat.services.file.FileManagerCmisImpl;
import fr.univlorraine.ecandidat.services.file.FileManagerFileSystemImpl;
import fr.univlorraine.ecandidat.services.file.FileManagerNone;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Configuration de dematerialisation
 * @author Kevin Hergalant
 */
@Configuration
public class FileConfig {

	private final Logger logger = LoggerFactory.getLogger(FileConfig.class);

	/* Variables CMIS */
	@Value("${file.cmis.user:}")
	private transient String userCmis;

	@Value("${file.cmis.pwd:}")
	private transient String passwordCmis;

	@Value("${file.cmis.atompub.url:}")
	private transient String urlCmis;

	@Value("${file.cmis.repository:}")
	private transient String repositoryCmis;

	@Value("${file.cmis.gestionnaire.id:}")
	private transient String folderGestionnaireCmis;

	@Value("${file.cmis.candidat.id:}")
	private transient String folderCandidatCmis;

	@Value("${file.cmis.apocandidature.id:}")
	private transient String folderApoCandidatureCmis;

	@Value("${file.cmis.enableVersioning:}")
	private transient Boolean enableVersioningCmis;

	/* Variables FileSystem */

	@Value("${file.filesystem.candidat.path:}")
	private transient String pathCandidatFs;

	@Value("${file.filesystem.gestionnaire.path:}")
	private transient String pathGestFs;

	@Value("${file.filesystem.apocandidature.path:}")
	private transient String pathApoCandidatureFs;

	/* Mode FileSystem principal */
	@Value("${file.mode.principal:}")
	private transient String modePrincipal;

	/** @return le fileManager principal de l'application */
	@Bean
	public FileManager fileManager() {
		/* On vérifie si il n'existe pas d'incohérence dans les variables de context */
		if (modePrincipal == null && (StringUtils.isNotBlank(userCmis) || StringUtils.isNotBlank(passwordCmis)
			|| StringUtils.isNotBlank(urlCmis)
			|| StringUtils.isNotBlank(repositoryCmis)
			|| StringUtils.isNotBlank(folderGestionnaireCmis)
			|| StringUtils.isNotBlank(folderCandidatCmis)) && (StringUtils.isNotBlank(pathCandidatFs) || StringUtils.isNotBlank(pathGestFs))) {
			logger.error("Stockage de fichier - Il existe des incoherences dans la definition de vos variables de dematerialisation - Mode de stockage de fichier : Aucun");
			return null;
		}
		final String log = "principal";
		if (StringUtils.isNotBlank(modePrincipal)) {
			if (modePrincipal.equals(ConstanteUtils.TYPE_FICHIER_STOCK_CMIS)) {
				final FileManager fm = generateFileManagerCmis(log);
				if (fm != null) {
					return fm;
				}
			} else if (modePrincipal.equals(ConstanteUtils.TYPE_FICHIER_STOCK_FILE_SYSTEM)) {
				final FileManager fm = generateFileManagerFileSystem(log);
				if (fm != null) {
					return fm;
				}
			}
		} else {
			FileManager fm = generateFileManagerCmis(log);
			if (fm != null) {
				return fm;
			}
			fm = generateFileManagerFileSystem(log);
			if (fm != null) {
				return fm;
			}
		}
		logger.info("Stockage de fichier " + log + " - Mode de stockage de fichier : Aucun");
		return new FileManagerNone();
	}

	/** @return le fileManager secondaire de l'application */
	@Bean
	public FileManager fileManagerSecondaire() {
		final String log = "secondaire";
		if (StringUtils.isNotBlank(modePrincipal) && modePrincipal.equals(ConstanteUtils.TYPE_FICHIER_STOCK_FILE_SYSTEM)) {
			final FileManager fm = generateFileManagerCmis(log);
			if (fm != null) {
				return fm;
			}
		} else if (StringUtils.isNotBlank(modePrincipal) && modePrincipal.equals(ConstanteUtils.TYPE_FICHIER_STOCK_CMIS)) {
			final FileManager fm = generateFileManagerFileSystem(log);
			if (fm != null) {
				return fm;
			}
		}
		return new FileManagerNone();
	}

	/**
	 * Genere un FileManager CMIS
	 * @param  log
	 * @return     le file Manager CMIS
	 */
	private FileManager generateFileManagerCmis(final String log) {
		if (StringUtils.isNotBlank(userCmis) && StringUtils.isNotBlank(passwordCmis)
			&& StringUtils.isNotBlank(urlCmis)
			&& StringUtils.isNotBlank(repositoryCmis)
			&& StringUtils.isNotBlank(folderGestionnaireCmis)
			&& StringUtils.isNotBlank(folderCandidatCmis)) {
			final FileManager fm = new FileManagerCmisImpl(userCmis, passwordCmis, urlCmis, repositoryCmis, folderGestionnaireCmis, folderCandidatCmis, folderApoCandidatureCmis, enableVersioningCmis);
			if (fm.testSession()) {
				logger.info("Stockage de fichier " + log
					+ " - Mode de stockage de fichier : CMIS ("
					+ urlCmis
					+ ", gestionnaire : "
					+ folderGestionnaireCmis
					+ ", candidat : "
					+ folderCandidatCmis
					+ ", enableVersioningCmis : "
					+ enableVersioningCmis
					+ ")");
				return fm;
			} else {
				logger.error("Stockage de fichier " + log + " - impossible d'accéder au System CMIS, vérifiez vos paramètres");
			}
		}
		return null;
	}

	/**
	 * Genere un FileManager FileSystem
	 * @param  log
	 * @return     le file Manager FileSystem
	 */
	private FileManager generateFileManagerFileSystem(final String log) {
		if (StringUtils.isNotBlank(pathCandidatFs) && StringUtils.isNotBlank(pathGestFs)) {
			final FileManager fm = new FileManagerFileSystemImpl(pathGestFs, pathCandidatFs, pathApoCandidatureFs);
			if (fm.testSession()) {
				logger.info("Stockage de fichier " + log + " - Mode de stockage de fichier : FileSystem (gestionnaire : " + pathGestFs + ", candidat : " + pathCandidatFs + ")");
				return fm;
			} else {
				logger.error("Stockage de fichier " + log + " - impossible d'accéder au FileSystem, vérifiez vos paramètres");
				return fm;
			}
		}
		return null;
	}
}
