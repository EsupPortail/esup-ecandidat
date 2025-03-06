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

import com.vaadin.server.RequestHandler;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * FileDownloader affichant l'indicateur de chargement le temps de chargement de la ressource.
 * @author Adrien Colson
 */
public class BusyIndicatorLongFileDownloader {

	/**
	 * Taille du buffer de téléchargement.
	 */
	private static final int BUFFER_SIZE = 1024;

	/**
	 * Resource à télécharger.
	 */
	private StreamResource streamResource;

	/**
	 * Constructeur.
	 * @param streamResourceSet ressource à télécharger
	 */
	public BusyIndicatorLongFileDownloader(final StreamResource streamResourceSet) {
		streamResource = streamResourceSet;
	}

	/**
	 * @return the streamResource
	 */
	public StreamResource getStreamResource() {
		return streamResource;
	}

	/**
	 * Défini la ressource à télécharger.
	 * @param streamResourceSet ressource à télécharger
	 */
	public void setStreamResource(final StreamResource streamResourceSet) {
		streamResource = streamResourceSet;
	}

	/**
	 * Télécharge la ressource dans un nouvel onglet.
	 */
	public void download() {
		final BusyIndicatorWindow busyIndicatorWindow = new BusyIndicatorWindow();
		final UI ui = UI.getCurrent();
		ui.access(() -> ui.addWindow(busyIndicatorWindow));
		new Thread(() -> {
			try {
				final ResourceDownloadRequestHandler rh = new ResourceDownloadRequestHandler(ui.getSession(), streamResource.getStreamSource().getStream(), streamResource.getFilename(), streamResource.getMIMEType());
				ui.access(() -> ui.getPage().open(rh.getUrl(), "_blank"));
			} finally {
				busyIndicatorWindow.getUI().access(() -> busyIndicatorWindow.close());
			}
		}).start();
	}

	/**
	 * Gère une requête de téléchargement.
	 * @author Adrien Colson
	 */
	@SuppressWarnings("serial")
	static class ResourceDownloadRequestHandler implements RequestHandler {

		/** Préfixe de l'url. */
		private static final String DOWNLOAD_PREFIX = "/download";
		/** Paramètre de l'identifiant de la ressource. */
		private static final String ID_PARAMETER = "id";

		/** Session Vaadin. */
		private final VaadinSession vaadinSession;
		/** Identifiant généré aléatoirement. */
		private final String id = UUID.randomUUID().toString();
		/** Flux d'entrée. */
		private final InputStream inputStream;
		/** Nom de fichier. */
		private final String filename;
		/** Type MIME. */
		private final String contentType;

		/**
		 * Constructeur.
		 * @param vaadinSessionSet Session Vaadin
		 * @param inputStreamSet Flux d'entrée
		 * @param filenameSet Nom de fichier
		 * @param contentTypeSet Type MIME
		 */
		ResourceDownloadRequestHandler(final VaadinSession vaadinSessionSet, final InputStream inputStreamSet, final String filenameSet, final String contentTypeSet) {
			super();
			vaadinSession = vaadinSessionSet;
			inputStream = inputStreamSet;
			filename = filenameSet;
			contentType = contentTypeSet;

			vaadinSessionSet.addRequestHandler(this);
		}

		/**
		 * @see com.vaadin.server.RequestHandler#handleRequest(com.vaadin.server.VaadinSession, com.vaadin.server.VaadinRequest, com.vaadin.server.VaadinResponse)
		 */
		@Override
		public boolean handleRequest(final VaadinSession session, final VaadinRequest request, final VaadinResponse response) throws IOException {
				if (DOWNLOAD_PREFIX.equals(request.getPathInfo()) && id instanceof String && id.equals(request.getParameter(ID_PARAMETER))) {
					try {
						response.setHeader("Expires", "0");
						response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
						response.setHeader("Pragma", "public");
						response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
						response.setContentType(contentType);
						final OutputStream os = response.getOutputStream();
						final byte[] buffer = new byte[BUFFER_SIZE];
						int len = inputStream.read(buffer);
						while (len > 0) {
							os.write(buffer, 0, len);
							len = inputStream.read(buffer);
						}
						inputStream.close();
						os.flush();
						os.close();
						return true;
					} finally {
						vaadinSession.removeRequestHandler(this);
					}
				}
				return false;
		}

		/**
		 * Génère l'URL du téléchargement.
		 * @return URL du téléchargement
		 */
		public String getUrl() {
			return DOWNLOAD_PREFIX + "?" + ID_PARAMETER + "=" + id;
		}

	}

}
