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
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Ojet d'affichage d'offre de formation : le diplome
 * @author Kevin Hergalant
 */
@Data
@EqualsAndHashCode(of =
{ "id" })
@SuppressWarnings("serial")
public class OdfDiplome implements Serializable {

	private String id;
	private String title;
	private String codDip;
	private List<OdfFormation> listeFormation;

	/* Type de diplome fake pour les etablissements n'utilisant pas le diplome dans l'odf */
	public static final String TYP_DIP_FAKE = "codtypDipFaxe-zPe8Do59iHX-unique";

	public OdfDiplome(final String id, final String codDip, final String title) {
		super();
		this.id = id;
		this.codDip = codDip;
		this.title = title;
		this.listeFormation = new ArrayList<>();
	}

	public Boolean isDipFaxe() {
		return TYP_DIP_FAKE.equals(codDip);
	}
}
