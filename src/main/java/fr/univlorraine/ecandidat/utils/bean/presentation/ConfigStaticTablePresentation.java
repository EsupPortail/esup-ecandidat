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
package fr.univlorraine.ecandidat.utils.bean.presentation;

import java.io.File;
import java.io.Serializable;

import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Objet servant à la présentation basique de donnée
 * @author Kevin Hergalant
 */
@Data
@EqualsAndHashCode(of = { "code" })
@SuppressWarnings("serial")
public class ConfigStaticTablePresentation implements Serializable {

	private String code;
	private String fileName;
	private String description;
	private String externalRessource;
	private String externalRessourceFolder;
	private String extension;
	private File file;

	public final static String CHAMPS_CODE = "code";
	public final static String CHAMPS_FILE_NAME = "fileName";
	public final static String CHAMPS_DESCRIPTION = "description";
	public final static String CHAMPS_FILE = "file";

	public ConfigStaticTablePresentation(final String code, final String fileName, final String description, final String externalRessource, final String externalRessourceFolder, final String extension) {
		super();
		this.code = code;
		this.fileName = fileName;
		this.description = description;
		this.externalRessource = externalRessource;
		this.externalRessourceFolder = externalRessourceFolder;
		this.extension = extension;
		this.file = MethodUtils.getExternalResource(externalRessource, externalRessourceFolder, fileName);
	}
}
