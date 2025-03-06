/**
 *
 *  ESUP-Portail MONDOSSIERWEB - Copyright (c) 2016 ESUP-Portail consortium
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
 *
 */
package fr.univlorraine.tools.vaadin;



import com.vaadin.server.FileDownloader;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.UI;

import java.io.IOException;

/**
 * FileDownloader affichant l'indicateur de chargement le temps de chargement de la ressource.
 * Le message warning affiché lors de la fermeture de l'indicateur de chargement peut être ignoré. (cf. http://dev.vaadin.com/ticket/12909)
 * @author Adrien Colson
 */
@SuppressWarnings("serial")
public class BusyIndicatorFileDownloader extends FileDownloader {

	/**
	 * Constructeur.
	 * @param resource ressource à télécharger
	 */
	public BusyIndicatorFileDownloader(final Resource resource) {
		super(resource);
	}

	/**
	 * Constructeur.
	 * @param resource ressource à télécharger
	 * @param target composant étendu
	 */
	public BusyIndicatorFileDownloader(final Resource resource, final AbstractComponent target) {
		super(resource);
		extend(target);
	}

	/**
	 * @see com.vaadin.server.FileDownloader#handleConnectorRequest(com.vaadin.server.VaadinRequest, com.vaadin.server.VaadinResponse, String)
	 */
	@Override
	public boolean handleConnectorRequest(final VaadinRequest request, final VaadinResponse response, final String path) throws IOException {
		final BusyIndicatorWindow busyIndicatorWindow = new BusyIndicatorWindow();
		final UI ui = UI.getCurrent();
		ui.access(() -> ui.addWindow(busyIndicatorWindow));
		try {
			return super.handleConnectorRequest(request, response, path);
		} finally {
			busyIndicatorWindow.close();
		}
	}

}
