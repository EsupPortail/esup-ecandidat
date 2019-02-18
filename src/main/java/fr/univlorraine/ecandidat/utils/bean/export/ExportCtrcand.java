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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 *
 * @author Kevin Hergalant
 */
@Data
@SuppressWarnings("serial")
public class ExportCtrcand implements Serializable {

	private String cod;
	private String lib;
	private String tes;
	private String temParam;
	private String temSendMail;
	private String mailContact;

	private String typeDecisionFav;
	private String temListComp;
	private String typeDecisionFavListComp;
	private Integer nbMaxVoeux;
	private String temDemat;
	private String infoComp;

	private String datDebDepot;
	private String datFinDepot;
	private String datAnalyse;
	private String datRetour;
	private String datJury;
	private String datPubli;
	private String datConfirm;
	private Integer delaiConfirm;
	private String datConfirmListComp;
	private Integer delaiConfirmListComp;

	private String datCre;
	private String userCre;
	private String datMod;
	private String userMod;

	private List<ExportMembre> membres = new ArrayList<>();

	public ExportCtrcand() {
		super();
	}

	public ExportCtrcand(final CentreCandidature ctr, final DateTimeFormatter formatterDate) {
		super();
		this.cod = ctr.getCodCtrCand();
		this.lib = ctr.getLibCtrCand();
		this.tes = MethodUtils.getTemoinFromBoolean(ctr.getTesCtrCand());
		this.temParam = MethodUtils.getTemoinFromBoolean(ctr.getTemParamCtrCand());
		this.temSendMail = MethodUtils.getTemoinFromBoolean(ctr.getTemSendMailCtrCand());
		this.mailContact = ctr.getMailContactCtrCand();

		this.typeDecisionFav = ctr.getTypeDecisionFav() == null ? "" : ctr.getTypeDecisionFav().getLibTypDec();
		this.temListComp = MethodUtils.getTemoinFromBoolean(ctr.getTemListCompCtrCand());
		this.typeDecisionFavListComp = ctr.getTypeDecisionFavListComp() == null ? "" : ctr.getTypeDecisionFavListComp().getLibTypDec();
		this.nbMaxVoeux = ctr.getNbMaxVoeuxCtrCand();
		this.temDemat = MethodUtils.getTemoinFromBoolean(ctr.getTemDematCtrCand());
		this.infoComp = ctr.getInfoCompCtrCand();

		this.datDebDepot = MethodUtils.formatDate(ctr.getDatDebDepotCtrCand(), formatterDate);
		this.datFinDepot = MethodUtils.formatDate(ctr.getDatFinDepotCtrCand(), formatterDate);
		this.datAnalyse = MethodUtils.formatDate(ctr.getDatAnalyseCtrCand(), formatterDate);
		this.datRetour = MethodUtils.formatDate(ctr.getDatRetourCtrCand(), formatterDate);
		this.datJury = MethodUtils.formatDate(ctr.getDatJuryCtrCand(), formatterDate);
		this.datPubli = MethodUtils.formatDate(ctr.getDatPubliCtrCand(), formatterDate);
		this.datConfirm = MethodUtils.formatDate(ctr.getDatConfirmCtrCand(), formatterDate);
		this.delaiConfirm = ctr.getDelaiConfirmCtrCand();
		this.datConfirmListComp = MethodUtils.formatDate(ctr.getDatConfirmListCompCtrCand(), formatterDate);
		this.delaiConfirmListComp = ctr.getDelaiConfirmListCompCtrCand();

		this.userCre = ctr.getUserCreCtrCand();
		this.datCre = MethodUtils.formatDate(ctr.getDatCreCtrCand(), formatterDate);
		this.userMod = ctr.getUserModCtrCand();
		this.datMod = MethodUtils.formatDate(ctr.getDatModCtrCand(), formatterDate);

		ctr.getGestionnaires().forEach(e -> {
			membres.add(new ExportMembre(e.getDroitProfilInd()));
		});
	}
}
