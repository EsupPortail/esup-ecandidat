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
package fr.univlorraine.ecandidat.utils.bean.odf;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Ojet d'affichage d'offre de formation : la formation
 * 
 * @author Kevin Hergalant
 */
@Data
@EqualsAndHashCode(of = {"idFormation"})
@SuppressWarnings("serial")
public class OdfFormation implements Serializable {

	private String title;
	private Integer idFormation;
	private String motCle;
	private String dates;
	private LocalDate dateDebut;
	private LocalDate dateFin;
	private Boolean modeCandidature;

	public OdfFormation(final String title, final Integer idFormation, final String motCle, final LocalDate dateDebut, final LocalDate dateFin, final Boolean modeCandidature) {
		super();
		this.title = title;
		this.idFormation = idFormation;
		this.motCle = motCle;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
		this.modeCandidature = modeCandidature;
	}
}
