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

import java.io.InputStream;

import com.vaadin.server.StreamResource.StreamSource;

/** Class du fichier à télécharger
 * @author Kevin Hergalant
 *
 */

public class OnDemandFileUtils {
	
	public static class OnDemandStreamSource implements OnDemandStreamSourceInterface{
		/**serialVersionUID**/
		private static final long serialVersionUID = -2044890362898772249L;
		
		private OnDemandFile onDemandFile;
		private OnDemandStreamFile onDemandStreamFile;
		
		public OnDemandStreamSource(OnDemandStreamFile onDemandStreamFile){
			this.onDemandStreamFile = onDemandStreamFile;
		}
		
		public void loadOndemandFile(){
			this.onDemandFile = onDemandStreamFile.getOnDemandFile();		
		}
		
		public String getFileName(){
			if (this.onDemandFile == null){
				return "";
			}
			return this.onDemandFile.getFileName();
		}
		
		@Override
		public InputStream getStream() {
			if (this.onDemandFile == null){
				return null;
			}
			return this.onDemandFile.getInputStream();
		}
	}
	
	
	/** Interface du fichier de resource obligeant de donner le fichier
	 * @author Kevin Hergalant
	 *
	 */
	public interface OnDemandStreamFile{		
		OnDemandFile getOnDemandFile();
	}
	
	/** Interface de la resource permettant d'obliger de demander le stream et le fichier
	 * @author Kevin Hergalant
	 *
	 */
	public interface OnDemandStreamSourceInterface extends StreamSource{
		void loadOndemandFile();
		InputStream getStream();
		String getFileName();		
	}
}
