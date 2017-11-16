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

import java.io.IOException;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamFile;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFileUtils.OnDemandStreamSource;
import fr.univlorraine.tools.vaadin.BusyIndicatorWindow;

/**
 * This specializes {@link FileDownloader} in a way, such that both the file
 * name and content can be determined on-demand, i.e. when the user has clicked
 * the component.
 */
public class OnDemandFileDownloader extends FileDownloader {

	
	/**serialVersionUID**/
	private static final long serialVersionUID = 3213624369678134741L;
	
	/** Constructeur
	 * @param onDemandStreamFile
	 * @param target
	 */
	public OnDemandFileDownloader(OnDemandStreamFile onDemandStreamFile, AbstractComponent target) {
		super(new CustomStreamResource(new OnDemandStreamSource(onDemandStreamFile),""));
		setOverrideContentType(false);
		extend(target);
	}
	
	/* (non-Javadoc)
	 * @see com.vaadin.server.FileDownloader#handleConnectorRequest(com.vaadin.server.VaadinRequest, com.vaadin.server.VaadinResponse, java.lang.String)
	 */
	@Override
	public boolean handleConnectorRequest(VaadinRequest request,
			VaadinResponse response, String path) throws IOException {		
		final BusyIndicatorWindow busyIndicatorWindow = new BusyIndicatorWindow();
		final UI ui = UI.getCurrent();
		ui.access(() -> ui.addWindow(busyIndicatorWindow));
		try {
			//on charge le fichier
			getStreamSource().loadOndemandFile();
			if (getStreamSource().getStream()==null){
				return true;
			}
			getResource().setFilename(getStreamSource().getFileName());
			return super.handleConnectorRequest(request, response, path);
		}catch(Exception e){
			return true;
		}
		finally {
			busyIndicatorWindow.close();
		}		
	}
	
	/**
	 * @return la streamSource
	 */
	private OnDemandStreamSource getStreamSource(){
		CustomStreamResource customSource = (CustomStreamResource) this.getFileDownloadResource();		
		return (OnDemandStreamSource) customSource.getStreamSource();
	}
	
	/**
	 * @return la resource
	 */
	private StreamResource getResource() {
		return (StreamResource) this.getResource("dl");
	}

}