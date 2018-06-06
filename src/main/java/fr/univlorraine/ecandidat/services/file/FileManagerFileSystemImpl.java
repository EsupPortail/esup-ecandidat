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
package fr.univlorraine.ecandidat.services.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/** Class d'implementation de l'interface de manager de fichier pour le File System
 *
 * @author Kevin Hergalant */
@Component(value = "fileManagerFileSystemImpl")
public class FileManagerFileSystemImpl implements FileManager {
	/** serialVersionUID */
	private static final long serialVersionUID = -5678691565524342452L;

	private Logger logger = LoggerFactory.getLogger(FileManagerFileSystemImpl.class);

	/* applicationContext pour les messages */
	@Resource
	private transient ApplicationContext applicationContext;

	@Resource
	private transient DateTimeFormatter formatterDateTimeFile;

	/* Informations de context */
	private String folderCandidat;
	private String folderGestionnaire;

	/** Constructeur par défaut */
	public FileManagerFileSystemImpl() {
		super();
	}

	/** Constructeur et affectation des variables
	 *
	 * @param folderGestionnaire
	 * @param folderCandidat
	 */
	public FileManagerFileSystemImpl(final String folderGestionnaire, final String folderCandidat) {
		super();
		this.folderGestionnaire = folderGestionnaire;
		this.folderCandidat = folderCandidat;
	}

	/** @see fr.univlorraine.ecandidat.services.file.FileManager#getType() */
	@Override
	public String getType() {
		return ConstanteUtils.TYPE_FICHIER_STOCK_FILE_SYSTEM;
	}

	/** @see fr.univlorraine.ecandidat.services.file.FileManager#testSession() */
	@Override
	public Boolean testSession() {
		Boolean testGest = directoryExistFileSystem(folderCandidat);
		Boolean testCand = directoryExistFileSystem(folderCandidat);
		if (!testGest || !testCand) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#deleteFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier, java.lang.Boolean)
	 */
	@Override
	public void deleteFile(final Fichier fichier, final Boolean sendErrorLog) throws FileException {
		String path = getFilePath(fichier);
		try {
			File file = new File(path);
			if (file == null || !file.delete()) {
				throw new FileException(applicationContext.getMessage("file.error.delete", null, MethodUtils.getLocale()));
			}
		} catch (Exception e) {
			if (sendErrorLog) {
				logger.error("Stockage de fichier - FileSystem : erreur de suppression du fichier : " + path, e);
			}
			throw new FileException(applicationContext.getMessage("file.error.delete", null, MethodUtils.getLocale()), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#getInputStreamFromFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier, java.lang.Boolean)
	 */
	@Override
	public InputStream getInputStreamFromFile(final Fichier fichier, final Boolean logAction)
			throws FileException {
		try {
			return new FileInputStream(new File(getFilePath(fichier)));
		} catch (FileNotFoundException e) {
			if (logAction) {
				logger.error("Stockage de fichier - FileSystem : erreur de recuperation du fichier ", e);
			}
			throw new FileException(applicationContext.getMessage("file.error.stream", new Object[] {fichier.getNomFichier()}, UI.getCurrent().getLocale()), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#createFileFromUpload(fr.univlorraine.ecandidat.utils.ByteArrayInOutStream, java.lang.String, java.lang.String, long, java.lang.String,
	 * java.lang.String, fr.univlorraine.ecandidat.entities.ecandidat.Candidature, java.lang.Boolean)
	 */
	@Override
	public FileCustom createFileFromUpload(final ByteArrayInOutStream file, final String mimeType,
			final String filename, final long length, final String typeFichier, final String prefixe, final Candidature candidature, final Boolean commune) throws FileException {
		String name = prefixe + "_" + formatterDateTimeFile.format(LocalDateTime.now()) + "_" + MethodUtils.cleanFileName(filename);
		String rootPath = "";
		String path = "";
		if (typeFichier.equals(ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE)) {
			rootPath = folderGestionnaire;
		} else {
			rootPath = folderCandidat;
			if (candidature != null) {
				path = candidature.getCandidat().getCompteMinima().getCampagne().getCodCamp();
				path = path + "/" + candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin();
				if (commune) {
					path = path + "/" + ConstanteUtils.PJ_FOLDER_COMMUNES + "/";
				} else {
					path = path + "/" + candidature.getFormation().getCodForm() + "/";
				}
			}

			/* Creation du chemin de fichiers */
			try {
				Path directory = Paths.get(rootPath + path);
				Files.createDirectories(directory);
			} catch (IOException e) {
				logger.error("Stockage de fichier - FileSystem : erreur de creation du fichier ", e);
				MethodUtils.closeRessource(file);
				throw new FileException(applicationContext.getMessage("file.error.create", null, UI.getCurrent().getLocale()), e);
			}
		}
		path = path + name;
		String finalPath = rootPath + path;
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(finalPath);
			file.writeTo(outputStream);
		} catch (Exception e) {
			logger.error("Stockage de fichier - FileSystem : erreur de creation du fichier ", e);
			throw new FileException(applicationContext.getMessage("file.error.create", null, UI.getCurrent().getLocale()), e);
		} finally {
			MethodUtils.closeRessource(outputStream);
			MethodUtils.closeRessource(file);
		}
		/* On vérifie que le fichier a bien été créé, sinon, erreur!! */
		if (!checkFileExists(finalPath)) {
			throw new FileException(applicationContext.getMessage("file.error.create", null, UI.getCurrent().getLocale()));
		}

		return getFileFromDoc(path, filename, prefixe);
	}

	/** @param path
	 * @return true si le fichier existe */
	private Boolean checkFileExists(final String path) {
		File f = new File(path);
		if (f.exists() && !f.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#existFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier)
	 */
	@Override
	public Boolean existFile(final Fichier file) throws FileException {
		return checkFileExists(getFilePath(file));
	}

	/** Retourne le path d'un fichier suivant son type
	 *
	 * @param fichier
	 * @return le path d'un fichier suivant son type */
	private String getFilePath(final Fichier fichier) {
		if (fichier.getTypFichier().equals(ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE)) {
			return folderGestionnaire + fichier.getFileFichier();
		} else {
			return folderCandidat + fichier.getFileFichier();
		}
	}

	/** Renvoi un customFIle a partir d'un document fileSystem
	 *
	 * @param doc
	 * @return un customFIle a partir d'un document fileSystem */
	private FileCustom getFileFromDoc(final String id, final String fileName, final String cod) {
		return new FileCustom(id, cod, fileName, "");
	}

	/** Verifie qu'un dossier existe en mode filesystem
	 *
	 * @param path
	 * @return true si le directory exist */
	private Boolean directoryExistFileSystem(final String path) {
		if (path == null || path.equals("")) {
			return false;
		}
		File f = new File(path);
		if (f.exists() && f.isDirectory()) {
			return true;
		}
		logger.error("Stockage de fichier - FileSystem : l'arborescence de dossier est invalide pour " + path);
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#deleteCampagneFolder(java.lang.String)
	 */
	@Override
	public Boolean deleteCampagneFolder(final String codCampagne) {
		logger.debug("Suppression du dossier de campagne : " + codCampagne);
		String path = folderCandidat + codCampagne;
		File folderCamp = new File(path);
		logger.debug("Suppression du dossier de campagne, Path=" + folderCamp.getPath());
		if (folderCamp.exists() && folderCamp.isDirectory()) {
			try {
				FileUtils.deleteDirectory(folderCamp);
				return true;
			} catch (IOException e) {
				logger.error("Impossible de supprimer le dossier de campagne : " + path + ", vous devez le supprimer à la main", e);
			}
		} else {
			logger.error("Impossible de supprimer le dossier de campagne : " + path + ", celui-ci n'existe pas");
		}
		return false;
	}
}
