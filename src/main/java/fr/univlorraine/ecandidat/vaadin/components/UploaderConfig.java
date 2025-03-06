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

import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.v7.ui.Upload.Receiver;
import com.vaadin.v7.ui.Upload.SucceededEvent;
import com.vaadin.v7.ui.Upload.SucceededListener;

import fr.univlorraine.ecandidat.controllers.ConfigController;
import fr.univlorraine.ecandidat.services.file.FileCustom;
import fr.univlorraine.ecandidat.services.file.FileException;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.bean.presentation.ConfigStaticTablePresentation;
import jakarta.annotation.Resource;
import lombok.Data;

/**
 * Uploader d'un fichier
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Data
@Configurable(preConstruction = true)
public class UploaderConfig implements Receiver, SucceededListener {

	private ConfigStaticTablePresentation ressourceStatic;
	private FileCustom customFile;
	private ByteArrayInOutStream file = null;
	@Resource
	private transient ConfigController configController;

	@Override
	public OutputStream receiveUpload(final String filename, final String mimeType) {
		this.file = new ByteArrayInOutStream();
		return file;
	}

	@Override
	public void uploadSucceeded(final SucceededEvent event) {
		try {
			this.customFile = configController.createExternalFileFromUpload(file, ressourceStatic);
		} catch (final FileException e) {
			Notification.show(e.getMessage(), Type.WARNING_MESSAGE);
		}
	}

	public UploaderConfig(final ConfigStaticTablePresentation ressourceStatic) {
		this.file = new ByteArrayInOutStream();
		this.ressourceStatic = ressourceStatic;
	}

	public void initFile() {
		this.file = new ByteArrayInOutStream();
	}
}
