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

/** Ojet d'affichage d'offre de formation : le diplome
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(of={"id"})
public class OdfDiplome implements Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = 2245122946073015182L;
	
	private String id;
	private String title;
	private String codDip;
	private List<OdfFormation> listeFormation;
	
	public OdfDiplome(String id,String codDip, String title) {
		super();
		this.id = id;
		this.codDip = codDip;
		this.title = title;
		this.listeFormation = new ArrayList<OdfFormation>();
	}
}
