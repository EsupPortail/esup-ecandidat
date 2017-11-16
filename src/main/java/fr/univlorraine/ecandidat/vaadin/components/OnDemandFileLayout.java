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

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.StyleConstants;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;

/**
 * Bar de gestion de fichier
 * @author Kevin Hergalant
 *
 */
@Configurable(preConstruction=true)
public class OnDemandFileLayout extends HorizontalLayout {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = -978978617045403948L;
	
	@Resource
	private transient ApplicationContext applicationContext;
	
	private OneClickButton btnDel = new OneClickButton(FontAwesome.MINUS);
	private OneClickButton btnViewer = new OneClickButton(FontAwesome.EYE);
	private OneClickButton btnDownload = new OneClickButton(FontAwesome.DOWNLOAD);
	private OneClickButton btnAdmin = new OneClickButton(FontAwesome.FLASH);

	public OnDemandFileLayout(String fileName){
		setSpacing(true);
		setWidth(100, Unit.PERCENTAGE);
		
		addStyleName(StyleConstants.ON_DEMAND_FILE_LAYOUT);
		
		btnDel.setDescription(applicationContext.getMessage("file.btnDel", null, UI.getCurrent().getLocale()));
		btnDel.setVisible(false);
		addComponent(btnDel);		
		setComponentAlignment(btnDel, Alignment.MIDDLE_CENTER);	
		
		btnViewer.setDescription(applicationContext.getMessage("img.viewer.btn", null, UI.getCurrent().getLocale()));
		btnViewer.setVisible(false);
		addComponent(btnViewer);
		setComponentAlignment(btnViewer, Alignment.MIDDLE_CENTER);
		
		btnDownload.setDescription(applicationContext.getMessage("file.btnDownload", null, UI.getCurrent().getLocale()));
		btnDownload.setVisible(false);
		addComponent(btnDownload);
		setComponentAlignment(btnDownload, Alignment.MIDDLE_CENTER);
		
		btnAdmin.setDescription(applicationContext.getMessage("pj.admin.btn", null, UI.getCurrent().getLocale()));
		btnAdmin.setVisible(false);
		addComponent(btnAdmin);
		setComponentAlignment(btnAdmin, Alignment.MIDDLE_CENTER);
		
		Label label = new Label(fileName);
		addComponent(label);
		setComponentAlignment(label, Alignment.MIDDLE_LEFT);
		setExpandRatio(label, 1.0f);
	}
	
	public void addBtnDelClickListener(ClickListener listener){
		btnDel.addClickListener(listener);
		btnDel.setVisible(true);
	}
	
	public void addBtnViewerClickListener(ClickListener listener){
		btnViewer.addClickListener(listener);
		btnViewer.setVisible(true);
	}
	
	public void addBtnViewerPdfBrowserOpener(OnDemandStreamFile file){
		new OnDemandPdfBrowserOpener(file, btnViewer);
		btnViewer.setVisible(true);
	}
	
	public void addBtnDownloadFileDownloader(OnDemandStreamFile file){
		new OnDemandFileDownloader(file, btnDownload);
		btnDownload.setVisible(true);
	}
	
	public void addBtnAdminClickListener(ClickListener listener){
		btnAdmin.addClickListener(listener);
		btnAdmin.setVisible(true);
	}
}
