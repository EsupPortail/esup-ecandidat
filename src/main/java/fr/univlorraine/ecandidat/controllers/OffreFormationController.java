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
package fr.univlorraine.ecandidat.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfCtrCand;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfDiplome;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfFormation;

/**
 * Gestion de l'offre de formation
 * @author Kevin Hergalant
 */
@Component
public class OffreFormationController {
	/* Injections */
	@Resource
	private transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient FormationController formationController;
	@Resource
	private transient ParametreController parametreController;
	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/**
	 * Recupere l'offre de formation
	 * @return la liste d'odf
	 */
	public List<OdfCtrCand> getOdfToCache() {
		final List<OdfCtrCand> offreDeFormation = new ArrayList<>();
		centreCandidatureController.getCentreCandidaturesEnService().forEach(ctr -> {
			addInternalCtrCand(ctr, offreDeFormation);
		});
		return offreDeFormation;
	}

	/**
	 * Recupere un objet de type OdfCtrCand suivant son id
	 * @param  ctrCand
	 * @return         l'odf du ctrCand
	 */
	private OdfCtrCand getCtrCandFromOffre(final CentreCandidature ctrCand, final List<OdfCtrCand> offreDeFormation) {
		final Optional<OdfCtrCand> centreOpt = offreDeFormation.stream().filter(odfCtr -> odfCtr.getIdCtrCand().equals(ctrCand.getIdCtrCand())).findFirst();
		if (centreOpt.isPresent()) {
			return centreOpt.get();
		}
		return null;
	}

	/**
	 * Ajoute un centre de candidature à l'offre
	 * @param ctrCand
	 */
	public void addCtrCand(final CentreCandidature ctrCand) {
		final List<OdfCtrCand> offreDeFormation = cacheController.getOdf();
		addInternalCtrCand(ctrCand, offreDeFormation);
		cacheController.updateOdf(offreDeFormation);
	}

	/**
	 * Ajoute un centre de candidature à l'offre
	 * @param ctrCand
	 * @param offreDeFormation
	 * @param initialLoading
	 */
	private void addInternalCtrCand(final CentreCandidature ctrCand, final List<OdfCtrCand> offreDeFormation) {
		if (getCtrCandFromOffre(ctrCand, offreDeFormation) != null) {
			removeInternalCtrCand(ctrCand, offreDeFormation);
		}
		if (!ctrCand.getTesCtrCand()) {
			removeInternalCtrCand(ctrCand, offreDeFormation);
			return;
		}
		final List<Formation> formations = new ArrayList<>();
		formations.addAll(formationController.getFormationsByCtrCand(ctrCand));

		if (formations.size() > 0) {
			final OdfCtrCand ctrCandOffre = new OdfCtrCand(ctrCand.getIdCtrCand(), ctrCand.getLibCtrCand());
			ctrCandOffre.setListeDiplome(getDiplomesByCtrCand(ctrCand.getIdCtrCand(), formations));
			offreDeFormation.add(ctrCandOffre);
		}
		offreDeFormation.sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
	}

	/**
	 * Supprime un centre de candidature de l'offre
	 * @param ctrCand
	 */
	public void removeCtrCand(final CentreCandidature ctrCand) {
		final List<OdfCtrCand> offreDeFormation = cacheController.getOdf();
		removeInternalCtrCand(ctrCand, offreDeFormation);
		cacheController.updateOdf(offreDeFormation);
	}

	/**
	 * Supprime un centre de candidature de l'offre
	 * @param ctrCand
	 */
	private void removeInternalCtrCand(final CentreCandidature ctrCand, final List<OdfCtrCand> offreDeFormation) {
		offreDeFormation.remove(new OdfCtrCand(ctrCand.getIdCtrCand(), ctrCand.getLibCtrCand()));
	}

	/**
	 * Recupere un objet de type OdfDiplome suivant son code
	 * @param  ctrCand
	 * @param  siScolTypDiplome
	 * @return                  l'OdfDiplome
	 */
	private OdfDiplome getDiplomeFromOffre(final OdfCtrCand ctrCand, final String typDiplome) {
		if (ctrCand.getListeDiplome() == null || ctrCand.getListeDiplome().size() == 0) {
			return null;
		}

		final Optional<OdfDiplome> dipOpt = ctrCand.getListeDiplome().stream().filter(dip -> dip.getCodDip().equals(typDiplome)).findFirst();
		if (dipOpt.isPresent()) {
			return dipOpt.get();
		}
		return null;
	}

	/**
	 * Renvoi la liste de diplome d'un centre de candidature
	 * @param  formations
	 * @return            la liste d'odf diplomes
	 */
	private List<OdfDiplome> getDiplomesByCtrCand(final Integer idCtr, final List<Formation> formations) {
		final List<OdfDiplome> diplomes = new ArrayList<>();
		/* Mode de type de diplome */
		final String modeTypForm = parametreController.getModeTypeFormation();

		/* Parcourt des formations */
		formations.forEach(formation -> {
			if (formation.getTesForm()) {
				final String codDip = getCodDiplome(formation, modeTypForm);
				final String libDip = getLibDiplome(formation, modeTypForm);
				/* Verification que le diplome est deja présent dans la liste des diplomes */
				final Optional<OdfDiplome> dipOpt = diplomes.stream().filter(dip -> dip.getCodDip().equals(codDip)).findAny();
				OdfDiplome leDiplome = null;
				/* Si deja présent-->on le recupere et on ajoute la formation à ce diplome */
				if (dipOpt.isPresent()) {
					leDiplome = dipOpt.get();
				}
				/* Si pas present on en créé un nouveau */
				else {
					leDiplome = new OdfDiplome(idCtr + "-" + codDip, codDip, libDip);
					diplomes.add(leDiplome);
				}
				leDiplome.getListeFormation().add(new OdfFormation(formation.getLibForm(), formation.getIdForm(), formation.getMotCleForm(), formation.getDatDebDepotForm(), formation.getDatFinDepotForm(), formation.getTemDematForm()));
				leDiplome.getListeFormation().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
			}
		});
		diplomes.sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
		return diplomes;
	}

	/**
	 * Recupere un objet de type OdfDiplome suivant son code
	 * @param  ctrCand
	 * @param  siScolTypDiplome
	 * @return                  l'odfFormation
	 */
	private OdfFormation getFormationFromOffre(final OdfDiplome odfDiplome, final Formation formation) {
		if (odfDiplome.getListeFormation() == null || odfDiplome.getListeFormation().size() == 0) {
			return null;
		}

		final Optional<OdfFormation> formOpt = odfDiplome.getListeFormation().stream().filter(form -> form.getIdFormation().equals(formation.getIdForm())).findFirst();
		if (formOpt.isPresent()) {
			return formOpt.get();
		}
		return null;
	}

	/**
	 * Ajoute une formation à l'offre
	 * @param formation
	 */
	public void addFormation(final Formation formation) {
		final List<OdfCtrCand> offreDeFormation = cacheController.getOdf();
		final CentreCandidature ctrCand = formation.getCommission().getCentreCandidature();
		/* Suppression de la formation du centre */
		removeInternalFormation(formation, offreDeFormation);

		if (formation.getTesForm()) {
			final OdfCtrCand odfCtrCand = getCtrCandFromOffre(ctrCand, offreDeFormation);
			if (odfCtrCand != null) {
				/* Mode de type de diplome */
				final String modeTypForm = parametreController.getModeTypeFormation();

				/* On défini le code grace au type de diplome */
				final String codDip = getCodDiplome(formation, modeTypForm);
				final String libDip = getLibDiplome(formation, modeTypForm);
				OdfDiplome odfDiplome = getDiplomeFromOffre(odfCtrCand, codDip);
				if (odfDiplome == null) {
					odfDiplome = new OdfDiplome(ctrCand.getIdCtrCand() + "-" + codDip, codDip, libDip);
					odfCtrCand.getListeDiplome().add(odfDiplome);
					odfCtrCand.getListeDiplome().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
				}
				final OdfFormation odfFormation = getFormationFromOffre(odfDiplome, formation);
				if (odfFormation == null) {
					odfDiplome.getListeFormation().add(new OdfFormation(formation.getLibForm(), formation.getIdForm(), formation.getMotCleForm(), formation.getDatDebDepotForm(), formation.getDatFinDepotForm(), formation.getTemDematForm()));
					odfDiplome.getListeFormation().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
				} else {
					odfFormation.setTitle(formation.getLibForm());
					odfFormation.setMotCle(formation.getMotCleForm());
					odfFormation.setDateDebut(formation.getDatDebDepotForm());
					odfFormation.setDateFin(formation.getDatFinDepotForm());
					odfFormation.setModeCandidature(formation.getTemDematForm());
					odfDiplome.getListeFormation().sort((p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
				}
			} else {
				addInternalCtrCand(ctrCand, offreDeFormation);
			}
		}
		cacheController.updateOdf(offreDeFormation);
	}

	/**
	 * Supprime une formation à l'offre
	 * @param formation
	 */
	public void removeFormation(final Formation formation) {
		final List<OdfCtrCand> offreDeFormation = cacheController.getOdf();
		removeInternalFormation(formation, offreDeFormation);
		cacheController.updateOdf(offreDeFormation);
	}

	/**
	 * Supprime une formation de l'offre
	 * @param formation
	 */
	private void removeInternalFormation(final Formation formation, final List<OdfCtrCand> offreDeFormation) {
		final OdfCtrCand odfCtrCand = getCtrCandFromOffre(formation.getCommission().getCentreCandidature(), offreDeFormation);
		if (odfCtrCand != null) {
			/* Parcours des diplomes du centre */
			odfCtrCand.getListeDiplome().forEach(dip -> {
				/* Suppression de la formation du diplome */
				dip.getListeFormation().removeIf(form -> form.getIdFormation().equals(formation.getIdForm()));
			});

			/* Nettoyage des diplomes sans formation */
			odfCtrCand.getListeDiplome().removeIf(dip -> dip.getListeFormation().size() == 0);

			/* Nettoyage d'un centre de candidature sans diplome */
			if (odfCtrCand.getListeDiplome().size() == 0) {
				offreDeFormation.remove(odfCtrCand);
			}
		}
	}

	/**
	 * @param  formation
	 * @param  modeTypForm
	 * @return             calcul le code de diplome
	 */
	private String getCodDiplome(final Formation formation, final String modeTypForm) {
		/* On défini le type de diplome */
		String codDip = OdfDiplome.TYP_DIP_FAKE;
		if (ConstanteUtils.PARAM_MODE_TYPE_FORMATION_TYPE_DIP.equals(modeTypForm) && formation.getSiScolTypDiplome() != null) {
			codDip = formation.getSiScolTypDiplome().getId().getCodTpdEtb();
		} else if (ConstanteUtils.PARAM_MODE_TYPE_FORMATION_NOMENCLATURE.equals(modeTypForm) && formation.getTypeFormation() != null) {
			codDip = formation.getTypeFormation().getCodTypeForm();
		}
		return codDip;
	}

	/**
	 * @param  formation
	 * @param  modeTypForm
	 * @return             calcul le libellé du diplome
	 */
	private String getLibDiplome(final Formation formation, final String modeTypForm) {

		/* On défini le type de diplome */
		String libDip = OdfDiplome.TYP_DIP_FAKE;
		if (ConstanteUtils.PARAM_MODE_TYPE_FORMATION_TYPE_DIP.equals(modeTypForm) && formation.getSiScolTypDiplome() != null) {
			libDip = formation.getSiScolTypDiplome().getLibTpd();
		} else if (ConstanteUtils.PARAM_MODE_TYPE_FORMATION_NOMENCLATURE.equals(modeTypForm) && formation.getTypeFormation() != null) {
			libDip = formation.getTypeFormation().getLibTypeForm();
		}
		return libDip;
	}
}
