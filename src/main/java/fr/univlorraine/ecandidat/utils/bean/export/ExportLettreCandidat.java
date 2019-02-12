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

import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
 * 
 * @author Kevin Hergalant
 */
@Data
@SuppressWarnings("serial")
public class ExportLettreCandidat implements Serializable {

	private String numeroDossierCandidat;
	private String civiliteCandidat;
	private String nomPatCandidat;
	private String nomUsuCandidat;
	private String prenomCandidat;
	private String dateNaissanceCandidat;
	private String adresseCandidat;
	private String libelleCampagne;
	private String libelleCommission;
	private String adresseCommission;
	private String libelleFormation;
	private String codeFormation;
	private String dateJuryFormation;
	private String libelleSignature;
	private String libelleAvis;
	private String commentaireAvis;
	private String dateValidationAvis;
	private String motifRefus;
	private String dateLimiteConfirm;
	private String dateHeure;
	private String date;

	public ExportLettreCandidat() {
		super();
	}

	public ExportLettreCandidat(final String numeroDossier, final String civilite, final String nomPatronymique, final String nomUsage,
			final String prenom, final String dateNaissance, final String adresseCandidat, final String campagne, final String commission,
			final String adresseCommission, final String codeFormation, final String libelleFormation, final String libelleSignature,
			final String libelleAvis, final String commentaireAvis, final String motifRefus, final String dateLimiteConfirm,
			final String dateJuryFormation, final String dateValidationAvis) {
		this.setNumeroDossierCandidat(MethodUtils.formatToExport(numeroDossier));
		this.setCiviliteCandidat(MethodUtils.formatToExport(civilite));
		this.setNomPatCandidat(MethodUtils.formatToExport(nomPatronymique));
		this.setNomUsuCandidat(MethodUtils.formatToExport(nomUsage));
		this.setPrenomCandidat(MethodUtils.formatToExport(prenom));
		this.setDateNaissanceCandidat(MethodUtils.formatToExport(dateNaissance));
		this.setAdresseCandidat(MethodUtils.formatToExport(adresseCandidat));
		this.setLibelleCampagne(MethodUtils.formatToExport(campagne));
		this.setLibelleCommission(MethodUtils.formatToExport(commission));
		this.setAdresseCommission(MethodUtils.formatToExport(adresseCommission));
		this.setCodeFormation(MethodUtils.formatToExport(codeFormation));
		this.setLibelleFormation(MethodUtils.formatToExport(libelleFormation));
		this.setLibelleSignature(MethodUtils.formatToExport(libelleSignature));
		this.setMotifRefus(MethodUtils.formatToExport(motifRefus));
		this.setDateLimiteConfirm(MethodUtils.formatToExport(dateLimiteConfirm));
		this.setDateJuryFormation(MethodUtils.formatToExport(dateJuryFormation));
		this.setLibelleAvis(MethodUtils.formatToExport(libelleAvis));
		this.setCommentaireAvis(commentaireAvis);
		this.setDateValidationAvis(MethodUtils.formatToExport(dateValidationAvis));
	}
}
