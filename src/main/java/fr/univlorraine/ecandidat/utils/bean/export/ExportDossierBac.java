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
package fr.univlorraine.ecandidat.utils.bean.export;

import java.io.Serializable;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * @author Kevin Hergalant
 *
 */
@Data
public class ExportDossierBac implements Serializable {
	
	/**serialVersionUID**/
	private static final long serialVersionUID = 6111120936286501453L;

	private String annee;
	private String serie;
	private String mention;
	private String pays;
	private String departement;
	private String commune;
	private String etablissement;
	
	public ExportDossierBac() {
		super();
	}

	public ExportDossierBac(Candidat candidat) {
		CandidatBacOuEqu bac = candidat.getCandidatBacOuEqu();
		if (bac!=null){
			if (bac.getAnneeObtBac()!=null){
				this.annee = String.valueOf(bac.getAnneeObtBac());
			}else{
				this.annee = "";
			}
			if (bac.getSiScolBacOuxEqu()!=null){
				this.serie = bac.getSiScolBacOuxEqu().getLibBac();
			}else{
				this.serie = "";
			}
			if (bac.getSiScolMentionNivBac()!=null){
				this.mention = bac.getSiScolMentionNivBac().getLibMnb();
			}else{
				this.mention = "";
			}
			if (bac.getSiScolPays()!=null){
				this.pays = bac.getSiScolPays().getLibPay();
			}else{
				this.pays = "";
			}
			if (bac.getSiScolDepartement()!=null){
				this.departement = bac.getSiScolDepartement().getLibDep();
			}else{
				this.departement = "";
			}
			if (bac.getSiScolCommune()!=null){
				this.commune = bac.getSiScolCommune().getLibCom();
			}else{
				this.commune = "";
			}
			if (bac.getSiScolEtablissement()!=null){
				this.etablissement = bac.getSiScolEtablissement().getLibEtb();
			}else{
				this.etablissement = "";
			}
		}else{
			this.annee = "";
			this.serie = "";
			this.mention = "";
			this.pays = "";
			this.departement = "";
			this.commune = "";
			this.etablissement = "";
		}
	}
}
