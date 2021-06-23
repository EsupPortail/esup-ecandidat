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
package fr.univlorraine.ecandidat.vaadin.components;

import java.io.OutputStream;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.services.file.FileCustom;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import lombok.Data;

/**
 * Uploader d'un fichier
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Data
@Configurable(preConstruction = true)
public class Uploader implements Receiver, SucceededListener {

	private String prefixe;
	private String typeFichier;
	private FileCustom customFile;
	private ByteArrayInOutStream file = null;
	private Candidature candidature;
	private Boolean commune = false;

	@Resource
	private transient FileController fileController;

	@Override
	public OutputStream receiveUpload(final String filename, final String mimeType) {
		this.file = new ByteArrayInOutStream();
		return file;
	}

	@Override
	public void uploadSucceeded(final SucceededEvent event) {
		this.customFile = fileController.createFileFromUpload(file, event.getMIMEType(), event.getFilename(), event.getLength(), this.typeFichier, this.prefixe, this.candidature, this.commune);
	}

	public Uploader(final String prefixe, final String typeFichier, final Candidature candidature, final Boolean commune) {
		this.prefixe = prefixe;
		this.typeFichier = typeFichier;
		this.file = new ByteArrayInOutStream();
		this.candidature = candidature;
		this.commune = commune;
	}

	public void initFile() {
		this.file = new ByteArrayInOutStream();
	}
}
