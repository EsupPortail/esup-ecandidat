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
package fr.univlorraine.ecandidat.views.windows;

import java.io.Serializable;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.services.file.FileCustom;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.ConfigStaticTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.UploaderConfig;

/**
 * Fenêtre d'upoload de fichier
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Configurable(preConstruction = true)
public class UploadConfigWindow extends Window {

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	@Resource
	private transient FileController fileController;

	@Resource
	private transient ParametreController parametreController;

	/* Composants */
	private Upload uploaderComponent = new Upload();
	private final UploaderConfig uploader;
	private Boolean error = false;
	private HorizontalLayout infoLayout = new HorizontalLayout();
	private Label infoLabel = new Label("");

	/** Listeners */
	UploadWindowListener uploadWindowListener;

	public void addUploadWindowListener(final UploadWindowListener uploadWindowListener) {
		this.uploadWindowListener = uploadWindowListener;
	}

	/**
	 * Crée une fenêtre d'upoload de fichier
	 * @param prefixe
	 * @param typeFichier
	 */
	public UploadConfigWindow(final ConfigStaticTablePresentation ressourceStatic) {

		/* Style */
		setWidth(720, Unit.PIXELS);
		setModal(true);
		setResizable(false);
		setClosable(false);

		/* Layout */
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("window.upload.title", null, UI.getCurrent().getLocale()));

		final long UPLOAD_LIMIT = 20;

		/* Texte */
		final HorizontalLayout hlComponent = new HorizontalLayout();
		hlComponent.setSpacing(true);
		hlComponent.setMargin(true);
		final Label textLabel = new Label();
		textLabel.setValue(applicationContext.getMessage("window.upload.config.message", new Object[] { UPLOAD_LIMIT, ressourceStatic.getExtension() }, UI.getCurrent().getLocale()));
		hlComponent.addComponent(textLabel);
		hlComponent.setComponentAlignment(textLabel, Alignment.MIDDLE_LEFT);
		layout.addComponent(hlComponent);

		/* Info */
		infoLayout = new HorizontalLayout();
		infoLayout.setWidth(100, Unit.PERCENTAGE);
		infoLabel = new Label("");
		infoLabel.setWidth(100, Unit.PERCENTAGE);
		infoLayout.addComponent(infoLabel);
		infoLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);
		infoLayout.setVisible(false);

		/* Uploader */
		uploader = new UploaderConfig(ressourceStatic);
		uploaderComponent = new Upload(null, uploader);

		hlComponent.addComponent(uploaderComponent);
		hlComponent.setComponentAlignment(uploaderComponent, Alignment.MIDDLE_RIGHT);
		uploaderComponent.setWidth(100, Unit.PERCENTAGE);

		uploaderComponent.setImmediate(true);
		uploaderComponent.setButtonCaption(applicationContext.getMessage("window.upload.btn", null, UI.getCurrent().getLocale()));

		/* Ajout du startListener */
		uploaderComponent.addStartedListener(e -> {
			final String fileName = e.getFilename();
			// verifie si le fichier est vide
			if (e.getContentLength() == 0) {
				displayError(applicationContext.getMessage("window.upload.emptyfile", null, UI.getCurrent().getLocale()));
			}
			// verifie si le fichier est trop volumineux
			else if (e.getContentLength() > UPLOAD_LIMIT * ConstanteUtils.UPLOAD_MO1) {
				displayError(applicationContext.getMessage("window.upload.toobigfile", null, UI.getCurrent().getLocale()));
			}
			/* Verif de l'extension */
			else if (!MethodUtils.checkExtension(fileName, ressourceStatic.getExtension())) {
				displayError(applicationContext.getMessage("window.upload.config.mimetype", new Object[] { ressourceStatic.getExtension() }, UI.getCurrent().getLocale()));
			} else {
				infoLabel.setValue(applicationContext.getMessage("window.upload.start", null, UI.getCurrent().getLocale()));
				uploaderComponent.setEnabled(false);
				infoLayout.setVisible(true);
				infoLayout.setStyleName(ValoTheme.LABEL_SUCCESS);
			}
		});
		uploaderComponent.addSucceededListener(uploader);
		uploaderComponent.addFailedListener(e -> {
			if (!error) {
				error = true;
				infoLabel.setValue(applicationContext.getMessage("window.upload.error", null, UI.getCurrent().getLocale()));
				infoLayout.setVisible(true);
				infoLabel.setStyleName(ValoTheme.LABEL_FAILURE);
				uploader.initFile();
			}
		});

		uploaderComponent.addFinishedListener(e -> {
			if (!error) {
				if (uploader.getCustomFile() == null) {
					error = true;
					infoLabel.setValue(applicationContext.getMessage("window.upload.error", null, UI.getCurrent().getLocale()));
					infoLayout.setVisible(true);
					infoLabel.setStyleName(ValoTheme.LABEL_FAILURE);
					uploader.initFile();
				} else {
					uploadWindowListener.success(uploader.getCustomFile());
				}
			}
			uploaderComponent.setEnabled(true);
			error = false;
		});

		layout.addComponent(infoLayout);

		final OneClickButton btnClose = new OneClickButton(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnClose.addClickListener(e -> close());
		layout.addComponent(btnClose);
		layout.setComponentAlignment(btnClose, Alignment.MIDDLE_CENTER);

		/* Centre la fenêtre */
		center();
	}

	/**
	 * Affiche les erreurs
	 * @param erreur
	 */
	private void displayError(final String erreur) {
		error = true;
		uploaderComponent.interruptUpload();
		infoLabel.setValue(erreur);
		infoLayout.setVisible(true);
		infoLabel.setStyleName(ValoTheme.LABEL_FAILURE);
	}

	/** Interface pour les listeners de la confirmation. */
	public interface UploadWindowListener extends Serializable {

		/** Appelé lorsque le fichier a bien été téléchargé! */
		void success(FileCustom file);

	}

}
