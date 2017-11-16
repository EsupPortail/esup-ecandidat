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

import java.io.InputStream;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.controllers.FileController;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.vaadin.components.CustomPanel;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import fr.univlorraine.ecandidat.vaadin.components.OneClickButton;

/**
 * Fenêtre de confirmation
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class ImageViewerWindow extends Window {

	/** serialVersionUID **/
	private static final long serialVersionUID = 3632242925880712176L;

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient FileController fileController;
	
	/* Les options d'affichage*/
	private OptionGroup optionGroupAffichage = new OptionGroup();

	/** Crée une fenêtre de viewer d'image
	 * @param file
	 * @param info
	 */
	public ImageViewerWindow(OnDemandFile file, String info) {
		/* Style */
		setModal(true);
		setResizable(true);
		setClosable(true);
		
		/*Layout de contenu*/
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);		
		
		/* Titre */
		setCaption(applicationContext.getMessage("img.viewer.title", new Object[]{file.getFileName()}, UI.getCurrent().getLocale()));
		
		/*Info*/
		CustomPanel panelInfo = new CustomPanel(applicationContext.getMessage("img.viewer.opt.title", null, UI.getCurrent().getLocale()), FontAwesome.GEAR);
		layout.addComponent(panelInfo);
		panelInfo.setMargin(true);
		panelInfo.addStyleName(StyleConstants.CONTAINER_WIDTH_100);
		
		/*Le container d'options d'affichage*/
		BeanItemContainer<SimpleTablePresentation> optContainer = new BeanItemContainer<SimpleTablePresentation>(SimpleTablePresentation.class);
		SimpleTablePresentation optionOriginale = new SimpleTablePresentation(ConstanteUtils.OPTION_IMG_AFF_ORIGINAL, applicationContext.getMessage("img.viewer.opt.original", null, UI.getCurrent().getLocale()),null);
		optContainer.addItem(optionOriginale);
		optContainer.addItem(new SimpleTablePresentation(ConstanteUtils.OPTION_IMG_AFF_OPTIMISE, applicationContext.getMessage("img.viewer.opt.optimise", null, UI.getCurrent().getLocale()),null));		
		optionGroupAffichage.setContainerDataSource(optContainer);
		optionGroupAffichage.addStyleName(StyleConstants.OPTION_GROUP_HORIZONTAL);
		optionGroupAffichage.setItemCaptionPropertyId(SimpleTablePresentation.CHAMPS_TITLE);
		optionGroupAffichage.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		
		panelInfo.setComponent(optionGroupAffichage);		
		
		StreamResource imageResource =  new StreamResource(new StreamSource() {			
			/**serialVersionUID**/
			private static final long serialVersionUID = 1508370478171833309L;

			@Override
			public InputStream getStream() {
				return file.getInputStream();
			}
		}, file.getFileName());
		
		/*Panel de l'image*/
		Panel contentPanel = new Panel();
		contentPanel.setSizeFull();
		layout.addComponent(contentPanel);
		layout.setExpandRatio(contentPanel, 1);
		
		/*L'image*/
		Image img = new Image(null, imageResource);
		contentPanel.setContent(img);
		
		/*Changement de mode*/
		optionGroupAffichage.addValueChangeListener(e->{
			SimpleTablePresentation option = (SimpleTablePresentation)optionGroupAffichage.getValue();
			if (option != null && option.getCode().equals(ConstanteUtils.OPTION_IMG_AFF_OPTIMISE)){
				img.addStyleName(StyleConstants.IMG_MAX_WIDTH);
				setSizeFull();
				layout.setHeightUndefined();
				layout.setWidth(100, Unit.PERCENTAGE);
			}else{
				img.removeStyleName(StyleConstants.IMG_MAX_WIDTH);
				/*layout.setHeightUndefined();
				layout.setWidth(100, Unit.PERCENTAGE);*/
				layout.setSizeUndefined();
				setSizeUndefined();
			}
			center();
		});
		optionGroupAffichage.setValue(optionOriginale);
		
		/* Le bouton close*/
		OneClickButton btnColse = new OneClickButton(applicationContext.getMessage("btnClose", null, UI.getCurrent().getLocale()), FontAwesome.TIMES);
		btnColse.addClickListener(e -> close());
		
		/* Layout du bouton*/
		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.addStyleName(StyleConstants.CONTAINER_WIDTH_100);
		buttonsLayout.setSpacing(true);
		buttonsLayout.addComponent(btnColse);
		buttonsLayout.setComponentAlignment(btnColse, Alignment.MIDDLE_CENTER);
		layout.addComponent(buttonsLayout);
	}

}
