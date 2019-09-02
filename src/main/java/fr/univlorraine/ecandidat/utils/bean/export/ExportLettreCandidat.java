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
import java.math.BigDecimal;

import fr.univlorraine.ecandidat.utils.MethodUtils;
import lombok.Data;

/**
 * Objet contenant les infos d'un candidat pour l'export
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
	private Boolean isAppel;
	private String montantFraisIns;

	public ExportLettreCandidat() {
		super();
	}

	public ExportLettreCandidat(final String numeroDossier,
		final String civilite,
		final String nomPatronymique,
		final String nomUsage,
		final String prenom,
		final String dateNaissance,
		final String adresseCandidat,
		final String campagne,
		final String commission,
		final String adresseCommission,
		final String codeFormation,
		final String libelleFormation,
		final String libelleSignature,
		final String libelleAvis,
		final String commentaireAvis,
		final String motifRefus,
		final String dateLimiteConfirm,
		final String dateJuryFormation,
		final String dateValidationAvis,
		final Boolean isAppel,
		final BigDecimal montantFraisIns) {
		setNumeroDossierCandidat(MethodUtils.formatToExport(numeroDossier));
		setCiviliteCandidat(MethodUtils.formatToExport(civilite));
		setNomPatCandidat(MethodUtils.formatToExport(nomPatronymique));
		setNomUsuCandidat(MethodUtils.formatToExport(nomUsage));
		setPrenomCandidat(MethodUtils.formatToExport(prenom));
		setDateNaissanceCandidat(MethodUtils.formatToExport(dateNaissance));
		setAdresseCandidat(MethodUtils.formatToExport(adresseCandidat));
		setLibelleCampagne(MethodUtils.formatToExport(campagne));
		setLibelleCommission(MethodUtils.formatToExport(commission));
		setAdresseCommission(MethodUtils.formatToExport(adresseCommission));
		setCodeFormation(MethodUtils.formatToExport(codeFormation));
		setLibelleFormation(MethodUtils.formatToExport(libelleFormation));
		setLibelleSignature(MethodUtils.formatToExport(libelleSignature));
		setMotifRefus(MethodUtils.formatToExport(motifRefus));
		setDateLimiteConfirm(MethodUtils.formatToExport(dateLimiteConfirm));
		setDateJuryFormation(MethodUtils.formatToExport(dateJuryFormation));
		setLibelleAvis(MethodUtils.formatToExport(libelleAvis));
		setCommentaireAvis(commentaireAvis);
		setDateValidationAvis(MethodUtils.formatToExport(dateValidationAvis));
		this.isAppel = isAppel;
		this.montantFraisIns = MethodUtils.parseBigDecimalAsString(montantFraisIns);
	}
}
