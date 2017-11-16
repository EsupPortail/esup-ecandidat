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
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.services.file.FileCustom;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;
import fr.univlorraine.ecandidat.vaadin.components.Uploader;

/**
 * Fenêtre d'upoload de fichier
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class UploadWindow extends Window {
	private static final long serialVersionUID = 1L;

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	
	@Resource
	private transient FileController fileController;
	
	@Resource
	private transient ParametreController parametreController;
	
	/*Composants*/	
	private Upload uploaderComponent = new Upload();
	private Uploader uploader;
	private Boolean error = false;
	private HorizontalLayout infoLayout = new HorizontalLayout();
	private Label infoLabel = new Label("");

	/** Listeners */
	UploadWindowListener uploadWindowListener;
	
	public void addUploadWindowListener(UploadWindowListener uploadWindowListener){
		this.uploadWindowListener = uploadWindowListener;
	}

	/** Crée une fenêtre d'upoload de fichier
	 * @param prefixe
	 * @param typeFichier
	 */
	public UploadWindow(String prefixe, String typeFichier, Candidature candidature, Boolean commune, Boolean isOnlyImg) {
		
		/* Style */
		setWidth(680, Unit.PIXELS);
		setModal(true);
		setResizable(false);
		setClosable(false);

		/* Layout */
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);

		/* Titre */
		setCaption(applicationContext.getMessage("window.upload.title", null, Locale.getDefault()));
		
		long UPLOAD_LIMIT = parametreController.getFileMaxSize();

		/* Texte */
		HorizontalLayout hlComponent = new HorizontalLayout();
		hlComponent.setSpacing(true);
		hlComponent.setMargin(true);
		Label textLabel = new Label();
		if (isOnlyImg){
			textLabel.setValue(applicationContext.getMessage("window.upload.message.img", new Object[]{UPLOAD_LIMIT}, Locale.getDefault()));
		}else{
			textLabel.setValue(applicationContext.getMessage("window.upload.message", new Object[]{UPLOAD_LIMIT}, Locale.getDefault()));
		}
		hlComponent.addComponent(textLabel);
		hlComponent.setComponentAlignment(textLabel, Alignment.MIDDLE_LEFT);
		layout.addComponent(hlComponent);
		
		/*Info*/
		infoLayout = new HorizontalLayout();
		infoLayout.setWidth(100, Unit.PERCENTAGE);
		infoLabel = new Label("");
		infoLabel.setWidth(100, Unit.PERCENTAGE);
		infoLayout.addComponent(infoLabel);
		infoLayout.setComponentAlignment(infoLabel, Alignment.MIDDLE_CENTER);
		infoLayout.setVisible(false);

		/*Uploader*/
		uploader = new Uploader(prefixe, typeFichier, candidature,commune);
		uploaderComponent = new Upload(null, uploader);
		
		hlComponent.addComponent(uploaderComponent);
		hlComponent.setComponentAlignment(uploaderComponent, Alignment.MIDDLE_RIGHT);
		uploaderComponent.setWidth(100,Unit.PERCENTAGE);

		
		uploaderComponent.setImmediate(true);
		uploaderComponent.setButtonCaption(applicationContext.getMessage("window.upload.btn", null, Locale.getDefault()));
		
		/*Ajout du startListener*/
		uploaderComponent.addStartedListener(e->{
			Integer sizeMax = fileController.getSizeMaxFileName();
			String fileName = e.getFilename();			
			
			if (!fileController.isFileNameOk(e.getFilename(),sizeMax)){
				displayError(applicationContext.getMessage("window.upload.toolongfilename", new Object[]{sizeMax}, Locale.getDefault()));
			}
			/*Verif de l'extension*/			
			else if (!MethodUtils.checkExtension(fileName, isOnlyImg)){
				if (isOnlyImg){
					displayError(applicationContext.getMessage("window.upload.mimetype.img", null, Locale.getDefault()));
				}else{
					displayError(applicationContext.getMessage("window.upload.mimetype", null, Locale.getDefault()));
				}
				
			}
			
			else if (e.getContentLength() > UPLOAD_LIMIT*ConstanteUtils.UPLOAD_MO1) {
				displayError(applicationContext.getMessage("window.upload.toobigfile", null, Locale.getDefault()));
	        }
			else{
        		infoLabel.setValue(applicationContext.getMessage("window.upload.start", null, Locale.getDefault()));
	        	uploaderComponent.setEnabled(false);
	        	infoLayout.setVisible(true);
	        	infoLayout.setStyleName(ValoTheme.LABEL_SUCCESS);
	        }
		});
		uploaderComponent.addSucceededListener(uploader);
		uploaderComponent.addFailedListener(e->{
			if (!error){
				error = true;
				infoLabel.setValue(applicationContext.getMessage("window.upload.error", null, Locale.getDefault()));
				infoLayout.setVisible(true);
				infoLabel.setStyleName(ValoTheme.LABEL_FAILURE);
				uploader.initFile();
			}			
		});
		
		uploaderComponent.addFinishedListener(e->{
			if (!error){
				if (uploader.getCustomFile()==null){
					error = true;
					infoLabel.setValue(applicationContext.getMessage("window.upload.error", null, Locale.getDefault()));
					infoLayout.setVisible(true);
					infoLabel.setStyleName(ValoTheme.LABEL_FAILURE);
					uploader.initFile();
				}else{
					uploadWindowListener.success(uploader.getCustomFile());
				}
			}
			uploaderComponent.setEnabled(true);
			error = false;
		});
		
			
		layout.addComponent(infoLayout);
		
		
		OneClickButton btnClose = new OneClickButton(applicationContext.getMessage("btnClose", null, Locale.getDefault()),FontAwesome.TIMES);
		btnClose.addClickListener(e->close());
		layout.addComponent(btnClose);
		layout.setComponentAlignment(btnClose, Alignment.MIDDLE_CENTER);

		/* Centre la fenêtre */
		center();
	}
	
	/** Affiche les erreurs
	 * @param erreur
	 */
	private void displayError(String erreur){
		error = true;
		uploaderComponent.interruptUpload();
		infoLabel.setValue(erreur);
		infoLayout.setVisible(true);
		infoLabel.setStyleName(ValoTheme.LABEL_FAILURE);
	}

	/**
	 * Interface pour les listeners de la confirmation.
	 */
	public interface UploadWindowListener extends Serializable {

		/**
		 * Appelé lorsque le fichier a bien été téléchargé!
		 */
		public void success(FileCustom file);

	}

}
