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
package fr.univlorraine.ecandidat.entities.siscol.pegase;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import lombok.Data;

@Data
public class Nomenclature {

	private String code;

	private String libelleCourt;

	private String libelleLong;

	private String libelleAffichage;

	private String prioriteAffichage;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstanteUtils.PEGASE_DAT_FORMAT)
	private LocalDate dateDebutValidite;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ConstanteUtils.PEGASE_DAT_FORMAT)
	private LocalDate dateFinValidite;

	private Boolean temoinVisible;

	private Boolean temoinLivre;

	public String getLibelleCourt() {
		return libelleCourt != null ? libelleCourt.toUpperCase() : null;
	}

	public String getLibelleLong() {
		return libelleLong != null ? libelleLong.toUpperCase() : null;
	}

	public String getLibelleAffichage() {
		return libelleAffichage != null ? libelleAffichage.toUpperCase() : null;
	}
}
