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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.client.util.FileUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/** Class d'implementation de l'interface de manager de fichier pour CMIS
 * 
 * @author Kevin Hergalant */
@Component(value = "fileManagerCmisImpl")
public class FileManagerCmisImpl implements FileManager {

	/** serialVersionUID */
	private static final long serialVersionUID = 4858252315964410899L;

	private Logger logger = LoggerFactory.getLogger(FileManagerCmisImpl.class);

	/* applicationContext pour les messages */
	@Resource
	private transient ApplicationContext applicationContext;

	/* La session CMIS */
	/*
	 * CMIS itself is stateless. OpenCMIS uses the concept of a session to cache data across calls and to deal with user authentication.
	 * The session object is also used as entry point to all CMIS operations and objects.
	 * Because a session is only a client side concept, the session object needs not to be closed or released when it's not needed anymore.
	 **/
	private Session cmisSession;

	/* Les informations de context */
	String user;
	String password;
	String url;
	String repository;
	String idFolderGestionnaire;
	String idFolderCandidat;
	Boolean enableVersioningCmis;

	/** Constructeur par défaut */
	public FileManagerCmisImpl() {
		super();
	}

	/** Constructeur et affectation des variables
	 * 
	 * @param user
	 * @param password
	 * @param url
	 * @param repository
	 * @param idFolderGestionnaire
	 * @param idFolderCandidat
	 * @param enableVersioningCmis
	 */
	public FileManagerCmisImpl(final String user, final String password, final String url, final String repository, final String idFolderGestionnaire,
			final String idFolderCandidat, final Boolean enableVersioningCmis) {
		super();
		this.user = user;
		this.password = password;
		this.url = url;
		this.repository = repository;
		this.idFolderGestionnaire = idFolderGestionnaire;
		this.idFolderCandidat = idFolderCandidat;
		this.enableVersioningCmis = enableVersioningCmis;
	}

	/** @return la session CMIS */
	public Session getCmisSession() {
		if (cmisSession == null) {
			cmisSession = cmisSession();
		}
		return cmisSession;
	}

	/** @return la session CMIS */
	private Session cmisSession() {
		if (url == null || url.equals("") || repository == null || repository.equals("") || user == null || user.equals("")) {
			return null;
		}

		try {
			// default factory implementation
			SessionFactory factory = SessionFactoryImpl.newInstance();
			Map<String, String> parameter = new HashMap<>();

			// user credentials
			parameter.put(SessionParameter.USER, user);
			parameter.put(SessionParameter.PASSWORD, password);

			// connection settings
			parameter.put(SessionParameter.ATOMPUB_URL, url);
			parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			parameter.put(SessionParameter.REPOSITORY_ID, repository);
			// create session
			Session session = factory.createSession(parameter);
			if (session == null) {
				logger.error("Stockage de fichier - Impossible de se connecter au serveur de fichier CMIS");
				return null;
			} else {
				if (directoryExistCMIS(idFolderGestionnaire, session) && directoryExistCMIS(idFolderCandidat, session)) {
					return session;
				}
			}
			return null;
		} catch (Exception e) {
			logger.error("Stockage de fichier - Impossible de se connecter au serveur de fichier CMIS", e);
			return null;
		}
	}

	@Override
	public Boolean testSession() {
		Session cmisSession = cmisSession();
		if (cmisSession != null) {
			Boolean testGest = directoryExistCMIS(idFolderGestionnaire, getCmisSession());
			Boolean testCand = directoryExistCMIS(idFolderCandidat, getCmisSession());
			if (!testGest || !testCand) {
				return false;
			}
			return true;
		}
		return false;
	}

	/** Verifie qu'un dossier existe en mode CMIS
	 * 
	 * @param idFolder
	 * @return */
	private Boolean directoryExistCMIS(final String idFolder, final Session cmisSession) {
		if (idFolder == null || idFolder.equals("")) {
			return false;
		}
		try {
			CmisObject object = cmisSession.getObject(cmisSession.createObjectId(idFolder));
			if (!(object instanceof Folder)) {
				logger.error("Stockage de fichier - CMIS : l'object CMIS " + idFolder + " n'est pas un dossier");
				return false;
			}
		} catch (Exception e) {
			logger.error("Stockage de fichier - CMIS : erreur sur l'object CMIS " + idFolder, e);
			return false;
		}
		return true;
	}

	/** @see fr.univlorraine.ecandidat.services.file.FileManager#getType() */
	@Override
	public String getType() {
		return ConstanteUtils.TYPE_FICHIER_STOCK_CMIS;
	}

	/** @param id
	 * @return l'objet CMIS par son id
	 * @throws FileException
	 * @throws NoSuchMessageException
	 */
	public CmisObject getObjectById(final String id) {
		Session session = getCmisSession();
		CmisObject object = session.getObject(session.createObjectId(id));
		return object;
	}

	/** @return le folder CMIS des candidat
	 * @throws FileException
	 * @throws NoSuchMessageException
	 */
	public Folder getFolderCandidat() throws NoSuchMessageException, FileException {
		CmisObject object = getObjectById(idFolderCandidat);
		Folder folder = (Folder) object;
		return folder;
	}

	/** @return le folder CMIS des gestionnaires
	 * @throws FileException
	 * @throws NoSuchMessageException
	 */
	public Folder getFolderGestionnaire() throws NoSuchMessageException, FileException {
		CmisObject object = getObjectById(idFolderGestionnaire);
		Folder folder = (Folder) object;
		return folder;
	}

	/** Renvoi un customFile a partir d'un document cmis
	 * 
	 * @param doc
	 * @return le fichier */
	private FileCustom getFileFromDoc(final Document doc, final String fileName, final String cod) {
		return new FileCustom(doc.getId(), cod, fileName, doc.getContentStreamMimeType());
	}

	/** Vérifie si l'arborescence demandée existe, sinon, la créé
	 * 
	 * @param candidature
	 * @return le folder folderCandidat/CodCamp/NumDossierOpiCptMin/CodForm
	 * @throws FileException
	 */
	public Folder getFolderCandidature(final Candidature candidature, final Boolean isPjCommune) throws FileException {
		try {
			/* Recuperation des noms de dossier */
			String codCampagne = candidature.getCandidat().getCompteMinima().getCampagne().getCodCamp();
			String noDossier = candidature.getCandidat().getCompteMinima().getNumDossierOpiCptMin();
			String codFormationOuCommune = candidature.getFormation().getCodForm();

			Session session = getCmisSession();

			/* Dossier de base pour les candidats */
			Folder master = getFolderCandidat();

			/* On défini les 3 dossier qu'on aura éventuellement à créer */
			Folder folderCampagne;
			Folder folderDossier;
			Folder folderFormation;

			if (isPjCommune) {
				codFormationOuCommune = ConstanteUtils.PJ_FOLDER_COMMUNES;
			}

			/* Verification que l'arborescence complete existe-->Si elle existe on la récupère, sinon on la créé */
			if (!session.existsPath(master.getPath() + "/" + codCampagne + "/" + noDossier + "/" + codFormationOuCommune)) {
				/* Verification que l'arborescence campagne + noDossier existe-->Si elle existe on la récupère, sinon on la créé */
				if (!session.existsPath(master.getPath() + "/" + codCampagne + "/" + noDossier)) {
					/* Verification que l'arborescence campagne existe-->Si elle existe on la récupère, sinon on la créé */
					if (!session.existsPath(master.getPath() + "/" + codCampagne)) {
						folderCampagne = FileUtils.createFolder(idFolderCandidat, codCampagne, null, session);
					} else {
						folderCampagne = FileUtils.getFolder(master.getPath() + "/" + codCampagne, session);
					}
					folderDossier = FileUtils.createFolder(folderCampagne.getId(), noDossier, null, session);
				} else {
					folderDossier = FileUtils.getFolder(master.getPath() + "/" + codCampagne + "/" + noDossier, session);
				}
				folderFormation = FileUtils.createFolder(folderDossier.getId(), codFormationOuCommune, null, session);
			} else {
				folderFormation = FileUtils.getFolder(master.getPath() + "/" + codCampagne + "/" + noDossier + "/" + codFormationOuCommune, session);
			}

			return folderFormation;
		} catch (NoSuchMessageException | FileException e) {
			throw new FileException(applicationContext.getMessage("file.error.create", null, UI.getCurrent().getLocale()), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#createFileFromUpload(fr.univlorraine.ecandidat.utils.ByteArrayInOutStream, java.lang.String, java.lang.String, long, java.lang.String,
	 * java.lang.String, fr.univlorraine.ecandidat.entities.ecandidat.Candidature, java.lang.Boolean)
	 */
	@Override
	public FileCustom createFileFromUpload(final ByteArrayInOutStream file, final String mimeType, final String filename,
			final long length, final String typeFichier, final String prefixe, final Candidature candidature, final Boolean commune) throws FileException {
		ByteArrayInputStream bis = null;
		try {
			String name = prefixe + "_" + filename;
			Map<String, Object> properties = new HashMap<>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
			properties.put(PropertyIds.NAME, name);

			bis = file.getInputStream();
			ContentStream contentStream = new ContentStreamImpl(name, BigInteger.valueOf(length), mimeType, bis);
			Folder master;
			if (typeFichier.equals(ConstanteUtils.TYPE_FICHIER_GESTIONNAIRE)) {
				master = getFolderGestionnaire();
			} else {
				master = getFolderCandidature(candidature, commune);
			}

			// versioning
			VersioningState versioningState = VersioningState.NONE;
			if (enableVersioningCmis != null && enableVersioningCmis) {
				versioningState = VersioningState.MINOR;
			}

			Document d = master.createDocument(properties, contentStream, versioningState);
			return getFileFromDoc(d, filename, prefixe);
		} catch (Exception e) {
			logger.error("Stockage de fichier - CMIS : erreur de creation du fichier ", e);
			throw new FileException(applicationContext.getMessage("file.error.create", null, UI.getCurrent().getLocale()), e);
		} finally {
			MethodUtils.closeRessource(bis);
			MethodUtils.closeRessource(file);
		}
	}

	/*
	 * Supprime un fichier par son id
	 * @param id
	 * public void deleteFileById(String id) {
	 * FileUtils.delete(id, getCmisSession());
	 * }
	 */

	/*
	 * (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#deleteFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier, java.lang.Boolean)
	 */
	@Override
	public void deleteFile(final Fichier fichier, final Boolean sendErrorLog) throws FileException {
		try {
			Document doc = (Document) getObjectById(fichier.getFileFichier());
			doc.delete(true);
		} catch (Exception e) {
			if (sendErrorLog) {
				logger.error("Stockage de fichier - CMIS : erreur de suppression du fichier ", e);
			}
			throw new FileException(applicationContext.getMessage("file.error.delete", null, MethodUtils.getLocale()), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#getInputStreamFromFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier, java.lang.Boolean)
	 */
	@Override
	public InputStream getInputStreamFromFile(final Fichier file, final Boolean logAction) throws FileException {
		try {
			Document doc = (Document) getObjectById(file.getFileFichier());
			return doc.getContentStream().getStream();
		} catch (Exception e) {
			if (logAction) {
				logger.error("Stockage de fichier - CMIS : erreur de recuperation du fichier ", e);
			}
			throw new FileException(applicationContext.getMessage("file.error.stream", new Object[] {file.getNomFichier()}, UI.getCurrent().getLocale()), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#existFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier)
	 */
	@Override
	public Boolean existFile(final Fichier file) throws FileException {
		try {
			Session session = getCmisSession();
			return session.exists(file.getFileFichier());
		} catch (CmisObjectNotFoundException e) {
			return false;
		} catch (Exception ex) {
			throw new FileException(applicationContext.getMessage("file.error.stream", new Object[] {file.getNomFichier()}, UI.getCurrent().getLocale()), ex);
		}
	}

	@Override
	public Boolean deleteCampagneFolder(final String codCampagne) {
		logger.debug("Suppression du dossier de campagne : " + codCampagne);
		Session session = getCmisSession();
		try {
			/* Dossier de base pour les candidats */
			Folder master = getFolderCandidat();
			/* Le dossier de la campagne */
			Folder folderCampagne = FileUtils.getFolder(master.getPath() + "/" + codCampagne, session);
			logger.debug("Suppression du dossier de campagne, path=" + folderCampagne.getPath() + ", id=" + folderCampagne.getId());
			List<String> liste = folderCampagne.deleteTree(true, UnfileObject.DELETE, true);
			if (liste != null && liste.size() > 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			logger.error("Impossible de supprimer le dossier de campagne : " + codCampagne + ", vous devez le supprimer à la main", e);
			return false;
		}
	}
}
