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
package fr.univlorraine.ecandidat.services.file;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class custom représentant un fichier
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(of="id")
public class FileCustom {
	private String id;
	private String cod;
	private String fileName;
	private String mimeType;
	
	/** Constructeur
	 * @param id
	 * @param cod
	 * @param fileName
	 * @param mimeType
	 */
	public FileCustom(String id, String cod, String fileName, String mimeType) {
		super();
		this.id = id;
		this.cod = cod;
		this.fileName = fileName;
		this.mimeType = mimeType;
	}
	
}
