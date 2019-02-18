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

import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 *
 * @author Kevin Hergalant
 */
@Data
@SuppressWarnings("serial")
public class ExportCommission implements Serializable {

	private String cod;
	private String lib;
	private String tes;
	private String tel;
	private String mail;
	private String fax;
	private String commentRetour;
	private String signataire;

	private String temEditLettre;
	private String temMailLettre;
	private String temAlertProp;
	private String temAlertAnnul;
	private String temAlertTrans;
	private String temAlertDesist;
	private String temAlertListePrinc;

	private String adresse;

	private String datCre;
	private String userCre;
	private String datMod;
	private String userMod;

	private List<ExportMembre> membres = new ArrayList<>();

	public ExportCommission() {
		super();
	}

	public ExportCommission(final Commission comm, final DateTimeFormatter formatterDateTime) {
		super();
		this.cod = comm.getCodComm();
		this.commentRetour = comm.getCommentRetourComm();
		this.fax = comm.getFaxComm();
		this.lib = comm.getLibComm();
		this.mail = comm.getMailComm();
		this.signataire = comm.getSignataireComm();
		this.tel = comm.getTelComm();

		this.tes = MethodUtils.getTemoinFromBoolean(comm.getTesComm());
		this.temEditLettre = MethodUtils.getTemoinFromBoolean(comm.getTemEditLettreComm());
		this.temMailLettre = MethodUtils.getTemoinFromBoolean(comm.getTemMailLettreComm());
		this.temAlertProp = MethodUtils.getTemoinFromBoolean(comm.getTemAlertPropComm());
		this.temAlertAnnul = MethodUtils.getTemoinFromBoolean(comm.getTemAlertAnnulComm());
		this.temAlertTrans = MethodUtils.getTemoinFromBoolean(comm.getTemAlertTransComm());
		this.temAlertDesist = MethodUtils.getTemoinFromBoolean(comm.getTemAlertDesistComm());
		this.temAlertListePrinc = MethodUtils.getTemoinFromBoolean(comm.getTemAlertListePrincComm());

		this.userCre = comm.getUserCreComm();
		this.userMod = comm.getUserModComm();

		this.datCre = MethodUtils.formatDate(comm.getDatCreComm(), formatterDateTime);
		this.datMod = MethodUtils.formatDate(comm.getDatModComm(), formatterDateTime);

		comm.getCommissionMembres().forEach(e -> {
			membres.add(new ExportMembre(e.getDroitProfilInd()));
		});
	}
}
