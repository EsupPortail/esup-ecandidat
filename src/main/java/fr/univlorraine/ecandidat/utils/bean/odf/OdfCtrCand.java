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

/** Ojet d'affichage d'offre de formation : le centre de candidature
 * @author Kevin Hergalant
 *
 */
@Data
@EqualsAndHashCode(of={"idCtrCand"})
public class OdfCtrCand implements Serializable {

	/**serialVersionUID**/
	private static final long serialVersionUID = -2310540796434503996L;
	
	private String title;
	private Integer idCtrCand;
	private List<OdfDiplome> listeDiplome;	
	
	public OdfCtrCand(Integer idCtrCand, String title) {
		super();
		this.idCtrCand = idCtrCand;
		this.title = title;
		this.listeDiplome = new ArrayList<OdfDiplome>();
		
	}
}
