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

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Classe de presentation d'un boolean
 * @author Kevin Hergalant
 */
@SuppressWarnings("serial")
@Data
@EqualsAndHashCode(of = "date")
public class FileOpi implements Serializable {

	public static String CHAMPS_CANDIDAT = "libButtonCandidat";
	public static String CHAMPS_VOEUX = "libButtonVoeux";
	public static String CHAMPS_BOTH = "libButtonBoth";

	public static String CHAMPS_DATE = "date";

	private String pathToCandidat;
	private String pathToVoeux;

	private String libButtonCandidat;
	private String libButtonVoeux;
	private String libButtonBoth;

	private String libFileCandidat;
	private String libFileVoeux;
	private String libFileBoth;

	private LocalDate date;

	public FileOpi() {
		super();
	}
}
