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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.repositories.CentreCandidatureRepository;
import fr.univlorraine.ecandidat.repositories.CommissionRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.StatFormationPresentation;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;

/**
 * Gestion des Stats
 *
 * @author Kevin Hergalant
 */
@Component
public class StatController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient ExportController exportController;
	@Resource
	private transient FormationRepository formationRepository;
	@Resource
	private transient CommissionRepository commissionRepository;
	@Resource
	private transient CentreCandidatureRepository centreCandidatureRepository;

	/**
	 * Retourne les stats de formation
	 *
	 * @param campagne
	 * @param securityCtrCandFonc
	 * @return les stats de formation
	 */
	public List<StatFormationPresentation> getStatFormation(final Campagne campagne, final Boolean afficheHs,
			final SecurityCtrCandFonc securityCtrCandFonc) {
		List<StatFormationPresentation> listeStat = new ArrayList<>();
		if (campagne == null) {
			return listeStat;
		}
		Integer idCtrCand = securityCtrCandFonc.getCtrCand().getIdCtrCand();

		/* Definition des Formation à afficher. Si afficheHs est coché, on affiche les Formation hors service */
		List<Formation> listeFormation;
		if (afficheHs) {
			listeFormation = formationRepository.findByCommissionCentreCandidatureIdCtrCand(idCtrCand);
		} else {
			listeFormation = formationRepository.findByCommissionCentreCandidatureIdCtrCandAndTesForm(idCtrCand, true);
		}

		listeStat.addAll(listeFormation.stream()
				.filter(e -> securityCtrCandFonc.getIsGestAllCommission() || MethodUtils
						.isIdInListId(e.getCommission().getIdComm(), securityCtrCandFonc.getListeIdCommission()))
				.map(e -> new StatFormationPresentation(e)).collect(Collectors.toList()));

		// Liste des nombre de candidature
		List<Object[]> listeNbCandidature = formationRepository.findStatNbCandidature(idCtrCand, campagne.getIdCamp());

		// Liste des nombre de candidature
		List<Object[]> listeNbCandidatureCancel = formationRepository.findStatNbCandidatureCancel(idCtrCand,
				campagne.getIdCamp());

		// Liste des type de statut
		List<Object[]> listeNbCandidatureByStatut = formationRepository.findStatNbCandidatureByStatut(idCtrCand,
				campagne.getIdCamp());

		// Liste des type de confirmation
		List<Object[]> listeNbCandidatureByConfirm = formationRepository.findStatNbCandidatureByConfirm(idCtrCand,
				campagne.getIdCamp());

		// Liste des type de statut
		List<Object[]> listeNbCandidatureByAvis = formationRepository.findStatNbCandidatureByAvis(idCtrCand,
				campagne.getIdCamp());

		return generateListStat(listeStat, listeNbCandidature, listeNbCandidatureByStatut, listeNbCandidatureByConfirm,
				listeNbCandidatureByAvis, listeNbCandidatureCancel);
	}

	/**
	 * Retourne les stats des commissions
	 *
	 * @param campagne
	 * @param securityCtrCandFonc
	 * @return les stats des commissions
	 */
	public List<StatFormationPresentation> getStatCommission(final Campagne campagne, final Boolean afficheHs,
			final SecurityCtrCandFonc securityCtrCandFonc) {
		List<StatFormationPresentation> listeStat = new ArrayList<>();
		if (campagne == null) {
			return listeStat;
		}
		Integer idCtrCand = securityCtrCandFonc.getCtrCand().getIdCtrCand();

		/* Definition des commissions à afficher. Si afficheHs est coché, on affiche les commissions hors service */
		List<Commission> listeCommission;
		if (afficheHs) {
			listeCommission = commissionRepository.findByCentreCandidatureIdCtrCand(idCtrCand);
		} else {
			listeCommission = commissionRepository.findByCentreCandidatureIdCtrCandAndTesComm(idCtrCand, true);
		}

		listeStat.addAll(listeCommission.stream()
				.filter(e -> securityCtrCandFonc.getIsGestAllCommission()
						|| MethodUtils.isIdInListId(e.getIdComm(), securityCtrCandFonc.getListeIdCommission()))
				.map(e -> new StatFormationPresentation(e)).collect(Collectors.toList()));

		// Liste des nombre de candidature
		List<Object[]> listeNbCandidature = commissionRepository.findStatNbCandidature(idCtrCand, campagne.getIdCamp());

		// Liste des nombre de candidature cancel
		List<Object[]> listeNbCandidatureCancel = commissionRepository.findStatNbCandidatureCancel(idCtrCand,
				campagne.getIdCamp());

		// Liste des type de statut
		List<Object[]> listeNbCandidatureByStatut = commissionRepository.findStatNbCandidatureByStatut(idCtrCand,
				campagne.getIdCamp());

		// Liste des type de confirmation
		List<Object[]> listeNbCandidatureByConfirm = commissionRepository.findStatNbCandidatureByConfirm(idCtrCand,
				campagne.getIdCamp());

		// Liste des type de statut
		List<Object[]> listeNbCandidatureByAvis = commissionRepository.findStatNbCandidatureByAvis(idCtrCand,
				campagne.getIdCamp());

		return generateListStat(listeStat, listeNbCandidature, listeNbCandidatureByStatut, listeNbCandidatureByConfirm,
				listeNbCandidatureByAvis, listeNbCandidatureCancel);
	}

	/**
	 * Recupere les stats par centre de candidature
	 *
	 * @param campagne
	 * @return les stats des centres de candidature
	 */
	public List<StatFormationPresentation> getStatCtrCand(final Campagne campagne, final Boolean afficheHs) {
		List<StatFormationPresentation> listeStat = new ArrayList<>();
		if (campagne == null) {
			return listeStat;
		}
		/* Definition des centre de candidature à afficher. Si afficheHs est coché, on affiche les ctrCand hors service */
		List<CentreCandidature> listeCtrCand;
		if (afficheHs) {
			listeCtrCand = centreCandidatureRepository.findAll();
		} else {
			listeCtrCand = centreCandidatureRepository.findByTesCtrCand(true);
		}

		listeStat.addAll(listeCtrCand.stream().map(e -> new StatFormationPresentation(e)).collect(Collectors.toList()));

		// Liste des nombre de candidature
		List<Object[]> listeNbCandidature = centreCandidatureRepository.findStatNbCandidature(campagne.getIdCamp());
		// Liste des nombre de candidature
		List<Object[]> listeNbCandidatureCancel = centreCandidatureRepository
				.findStatNbCandidatureCancel(campagne.getIdCamp());
		// Liste des type de statut
		List<Object[]> listeNbCandidatureByStatut = centreCandidatureRepository
				.findStatNbCandidatureByStatut(campagne.getIdCamp());

		// Liste des type de confirmation
		List<Object[]> listeNbCandidatureByConfirm = centreCandidatureRepository
				.findStatNbCandidatureByConfirm(campagne.getIdCamp());

		// Liste des type de statut
		List<Object[]> listeNbCandidatureByAvis = centreCandidatureRepository
				.findStatNbCandidatureByAvis(campagne.getIdCamp());

		return generateListStat(listeStat, listeNbCandidature, listeNbCandidatureByStatut, listeNbCandidatureByConfirm,
				listeNbCandidatureByAvis, listeNbCandidatureCancel);
	}

	/**
	 * Genere la liste de Stat
	 *
	 * @param listeFormation
	 * @param listeNbCandidature
	 * @param listeNbCandidatureByStatut
	 * @param listeNbCandidatureByConfirm
	 * @param listeNbCandidatureByAvis
	 * @param listeCapaciteAccueil
	 * @return la liste de Stat
	 */
	private List<StatFormationPresentation> generateListStat(final List<StatFormationPresentation> listeStat,
			final List<Object[]> listeNbCandidature, final List<Object[]> listeNbCandidatureByStatut,
			final List<Object[]> listeNbCandidatureByConfirm, final List<Object[]> listeNbCandidatureByAvis,
			final List<Object[]> listeNbCandidatureCancel) {
		// Liste des elements de stats à afficher
		listeStat.forEach(stat -> {
			// nombre de candidatures global
			listeNbCandidature.stream().filter(tab -> ((Integer) tab[0]).equals(stat.getId())).forEach(tab -> {
				stat.setNbCandidatureTotal((Long) tab[1]);
			});

			// nombre de candidatures cancel
			listeNbCandidatureCancel.stream().filter(tab -> ((Integer) tab[0]).equals(stat.getId())).forEach(tab -> {
				stat.setNbCandidatureCancel((Long) tab[1]);
			});

			// les statuts
			listeNbCandidatureByStatut.stream().filter(tab -> ((Integer) tab[0]).equals(stat.getId())).forEach(tab -> {
				Long nbStatut = (Long) tab[2];
				switch ((String) tab[1]) {
				case (NomenclatureUtils.TYPE_STATUT_ATT):
					stat.setNbStatutAttente(nbStatut);
					break;
				case (NomenclatureUtils.TYPE_STATUT_REC):
					stat.setNbStatutReceptionne(nbStatut);
					break;
				case (NomenclatureUtils.TYPE_STATUT_COM):
					stat.setNbStatutComplet(nbStatut);
					break;
				case (NomenclatureUtils.TYPE_STATUT_INC):
					stat.setNbStatutIncomplet(nbStatut);
					break;
				}
			});

			// les confimations/desistement
			listeNbCandidatureByConfirm.stream().filter(tab -> ((Integer) tab[0]).equals(stat.getId())).forEach(tab -> {
				Long nbConfirm = (Long) tab[2];
				Boolean value = (Boolean) tab[1];
				if (value != null) {
					if (value) {
						stat.setNbConfirm(nbConfirm);
					} else {
						stat.setNbDesist(nbConfirm);
					}
				}
			});

			// les avis
			listeNbCandidatureByAvis.stream().filter(tab -> ((Integer) tab[0]).equals(stat.getId())).forEach(tab -> {
				Boolean valide = (Boolean) tab[2];
				Long nb = (Long) tab[3];
				switch ((String) tab[1]) {
				case (NomenclatureUtils.TYP_AVIS_FAV):
					stat.setNbAvisFavorable(MethodUtils.getLongValue(stat.getNbAvisFavorable()) + nb);
					break;
				case (NomenclatureUtils.TYP_AVIS_DEF):
					stat.setNbAvisDefavorable(MethodUtils.getLongValue(stat.getNbAvisDefavorable()) + nb);
					break;
				case (NomenclatureUtils.TYP_AVIS_LISTE_COMP):
					stat.setNbAvisListeComp(MethodUtils.getLongValue(stat.getNbAvisListeComp()) + nb);
					break;
				case (NomenclatureUtils.TYP_AVIS_LISTE_ATTENTE):
					stat.setNbAvisListeAttente(MethodUtils.getLongValue(stat.getNbAvisListeAttente()) + nb);
					break;
				case (NomenclatureUtils.TYP_AVIS_PRESELECTION):
					stat.setNbAvisPreselection(MethodUtils.getLongValue(stat.getNbAvisPreselection()) + nb);
					break;
				}
				// le nombre d'avis total
				stat.setNbAvisTotal(MethodUtils.getLongValue(stat.getNbAvisTotal()) + nb);
				if (valide != null) {
					if (valide) {
						stat.setNbAvisTotalValide(MethodUtils.getLongValue(stat.getNbAvisTotalValide()) + nb);
					} else {
						stat.setNbAvisTotalNonValide(MethodUtils.getLongValue(stat.getNbAvisTotalNonValide()) + nb);
					}
				}
			});
		});
		return listeStat;
	}

	/**
	 * @param liste
	 * @param footerStat
	 * @param showCapaciteAccueil
	 * @param showCapacite
	 * @return le fichier d'export de stats
	 */
	public OnDemandFile generateExport(final Campagne campagne, final String code, final String libelle,
			final List<StatFormationPresentation> liste, final StatFormationPresentation footerStat,
			final String headerLibelle, final String headerLibelleSup, final Boolean showCapaciteAccueil) {
		if (liste == null || liste.size() == 0 || footerStat == null || campagne == null) {
			return null;
		}
		Map<String, Object> beans = new HashMap<>();
		beans.put("stats", liste);
		beans.put("footer", footerStat);
		beans.put("code", campagne.getCodCamp() + "-" + code);
		beans.put("headerLibelle", headerLibelle);
		beans.put("hideLibelleSup", headerLibelleSup == null);
		beans.put("headerLibelleSup", headerLibelleSup);
		beans.put("hideCapaciteAccueil", !showCapaciteAccueil);

		String libFile = applicationContext.getMessage("stat.nom.fichier",
				new Object[] {campagne.getCodCamp(), code + "(" + libelle + ")",
						DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now())},
				UI.getCurrent().getLocale());

		return exportController.generateXlsxExport(beans, "stats_template", libFile);
	}
}
