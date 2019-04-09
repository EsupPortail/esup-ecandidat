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

import java.io.InputStream;

import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.PjOpi;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Class d'implementation de l'interface de manager de fichier par défaut
 *
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Component(value = "fileManagerFileNoneImpl")
public class FileManagerNone implements FileManager {

	/** Constructeur par défaut */
	public FileManagerNone() {
		super();
	}

	/** @see fr.univlorraine.ecandidat.services.file.FileManager#getType() */
	@Override
	public String getType() {
		return ConstanteUtils.TYPE_FICHIER_STOCK_NONE;
	}

	/** @see fr.univlorraine.ecandidat.services.file.FileManager#testSession() */
	@Override
	public Boolean testSession() {
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#createFileFromUpload(fr.univlorraine.ecandidat.utils.ByteArrayInOutStream, java.lang.String, java.lang.String, long, java.lang.String, java.lang.String, fr.univlorraine.ecandidat.entities.ecandidat.Candidature, java.lang.Boolean)
	 */
	@Override
	public FileCustom createFileFromUpload(final ByteArrayInOutStream file, final String mimeType, final String filename, final long length, final String typeFichier, final String prefixe,
			final Candidature candidature, final Boolean commune) throws FileException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#deleteFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier, java.lang.Boolean)
	 */
	@Override
	public void deleteFile(final Fichier fichier, final Boolean sendErrorLog) throws FileException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#getInputStreamFromFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier, java.lang.Boolean)
	 */
	@Override
	public InputStream getInputStreamFromFile(final Fichier file, final Boolean logAction) throws FileException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#existFile(fr.univlorraine.ecandidat.entities.ecandidat.Fichier)
	 */
	@Override
	public Boolean existFile(final Fichier file) throws FileException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#deleteCampagneFolder(java.lang.String)
	 */
	@Override
	public Boolean deleteCampagneFolder(final String codCampagne) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.univlorraine.ecandidat.services.file.FileManager#isFileCandidatureOpiExist(fr.univlorraine.ecandidat.entities.ecandidat.PjOpi, fr.univlorraine.ecandidat.entities.ecandidat.Fichier, java.lang.String)
	 */
	@Override
	public Boolean isFileCandidatureOpiExist(final PjOpi pjOpi, final Fichier file, final String complementLog) throws FileException {
		// TODO Auto-generated method stub
		return null;
	}

}
