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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.repositories.CentreCandidatureRepository;
import fr.univlorraine.ecandidat.repositories.CommissionRepository;
import fr.univlorraine.ecandidat.repositories.FormationRepository;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.utils.ByteArrayInOutStream;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.StatFormationPresentation;
import fr.univlorraine.ecandidat.vaadin.components.OnDemandFile;
import net.sf.jett.event.SheetEvent;
import net.sf.jett.event.SheetListener;
import net.sf.jett.transform.ExcelTransformer;

/**
 * Gestion des Stats
 * 
 * @author Kevin Hergalant
 */
@Component
public class StatController {
	private Logger logger = LoggerFactory.getLogger(StatController.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CampagneController campagneController;
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
	public List<StatFormationPresentation> getStatFormation(Campagne campagne,
			SecurityCtrCandFonc securityCtrCandFonc) {
		List<StatFormationPresentation> listeStat = new ArrayList<StatFormationPresentation>();
		if (campagne == null) {
			return listeStat;
		}
		Integer idCtrCand = securityCtrCandFonc.getCtrCand().getIdCtrCand();

		List<Formation> listeFormation = formationRepository
				.findByCommissionCentreCandidatureIdCtrCandAndTesForm(idCtrCand, true);
		listeStat.addAll(listeFormation.stream()
				.filter(e -> securityCtrCandFonc.getIsGestAllCommission() || MethodUtils
						.isIdInListId(e.getCommission().getIdComm(), securityCtrCandFonc.getListeIdCommission()))
				.map(e -> new StatFormationPresentation(e)).collect(Collectors.toList()));

		// Liste des nombre de candidature
		List<Object[]> listeNbCandidature = formationRepository.findStatNbCandidature(idCtrCand, campagne.getIdCamp());

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
				listeNbCandidatureByAvis);
	}

	/**
	 * Retourne les stats des commissions
	 * 
	 * @param campagne
	 * @param securityCtrCandFonc
	 * @return les stats des commissions
	 */
	public List<StatFormationPresentation> getStatCommission(Campagne campagne,
			SecurityCtrCandFonc securityCtrCandFonc) {
		List<StatFormationPresentation> listeStat = new ArrayList<StatFormationPresentation>();
		if (campagne == null) {
			return listeStat;
		}
		Integer idCtrCand = securityCtrCandFonc.getCtrCand().getIdCtrCand();

		List<Commission> listeCommission = commissionRepository.findByCentreCandidatureIdCtrCandAndTesComm(idCtrCand,
				true);
		listeStat.addAll(listeCommission.stream()
				.filter(e -> securityCtrCandFonc.getIsGestAllCommission()
						|| MethodUtils.isIdInListId(e.getIdComm(), securityCtrCandFonc.getListeIdCommission()))
				.map(e -> new StatFormationPresentation(e)).collect(Collectors.toList()));

		// Liste des nombre de candidature
		List<Object[]> listeNbCandidature = commissionRepository.findStatNbCandidature(idCtrCand, campagne.getIdCamp());

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
				listeNbCandidatureByAvis);
	}

	public List<StatFormationPresentation> getStatCtrCand(Campagne campagne) {
		List<StatFormationPresentation> listeStat = new ArrayList<StatFormationPresentation>();
		if (campagne == null) {
			return listeStat;
		}

		List<CentreCandidature> listeCtrCand = centreCandidatureRepository.findByTesCtrCand(true);
		listeStat.addAll(listeCtrCand.stream().map(e -> new StatFormationPresentation(e)).collect(Collectors.toList()));

		// Liste des nombre de candidature
		List<Object[]> listeNbCandidature = centreCandidatureRepository.findStatNbCandidature(campagne.getIdCamp());
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
				listeNbCandidatureByAvis);
	}

	/**
	 * Genere la liste de Stat
	 * 
	 * @param listeFormation
	 * @param listeNbCandidature
	 * @param listeNbCandidatureByStatut
	 * @param listeNbCandidatureByConfirm
	 * @param listeNbCandidatureByAvis
	 * @return la liste de Stat
	 */
	private List<StatFormationPresentation> generateListStat(List<StatFormationPresentation> listeStat,
			List<Object[]> listeNbCandidature, List<Object[]> listeNbCandidatureByStatut,
			List<Object[]> listeNbCandidatureByConfirm, List<Object[]> listeNbCandidatureByAvis) {
		// Liste des elements de stats à afficher
		listeStat.forEach(stat -> {
			// nombre de candidatures global
			listeNbCandidature.stream().filter(tab -> ((Integer) tab[0]).equals(stat.getId())).forEach(tab -> {
				stat.setNbCandidatureTotal((Long) tab[1]);
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
	 * @return le fichier d'export de stats
	 */
	public OnDemandFile generateExport(Campagne campagne, String code, String libelle,
			List<StatFormationPresentation> liste, StatFormationPresentation footerStat, String headerLibelle,
			String headerLibelleSup) {
		if (liste == null || liste.size() == 0 || footerStat == null || campagne == null) {
			return null;
		}
		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("stats", liste);
		beans.put("footer", footerStat);
		beans.put("code", campagne.getCodCamp() + "-" + code);
		beans.put("headerLibelle", headerLibelle);
		beans.put("hideHeaderLibelleSup", headerLibelleSup == null);
		beans.put("headerLibelleSup", headerLibelleSup);

		ByteArrayInOutStream bos = null;
		InputStream fileIn = null;
		Workbook workbook = null;
		try {
			/* Récupération du template */
			fileIn = new BufferedInputStream(new ClassPathResource("template/stats_template.xlsx").getInputStream());
			/* Génération du fichier excel */
			ExcelTransformer transformer = new ExcelTransformer();
			transformer.setSilent(true);
			transformer.setLenient(true);
			transformer.setDebug(false);
			transformer.addSheetListener(new SheetListener() {
				/**
				 * @see net.sf.jett.event.SheetListener#beforeSheetProcessed(net.sf.jett.event.SheetEvent)
				 */
				@Override
				public boolean beforeSheetProcessed(final SheetEvent sheetEvent) {
					return true;
				}

				/**
				 * @see net.sf.jett.event.SheetListener#sheetProcessed(net.sf.jett.event.SheetEvent)
				 */
				@Override
				public void sheetProcessed(final SheetEvent sheetEvent) {
					/* Ajuste la largeur des colonnes */
					final Sheet sheet = sheetEvent.getSheet();
					// for (int i = 0; i < sheet.getRow(0).getLastCellNum(); i++) {
					for (int i = 0; i < 3; i++) {
						sheet.autoSizeColumn(i);
					}
				}
			});

			workbook = transformer.transform(fileIn, beans);
			bos = new ByteArrayInOutStream();
			workbook.write(bos);
			return new OnDemandFile(applicationContext.getMessage("stat.nom.fichier",
					new Object[] { campagne.getCodCamp(), code + "(" + libelle + ")",
							DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now()) },
					UI.getCurrent().getLocale()), bos.getInputStream());
		} catch (Exception e) {
			Notification.show(applicationContext.getMessage("export.error", null, UI.getCurrent().getLocale()),
					Type.WARNING_MESSAGE);
			logger.error("erreur a la création du report de stats", e);
			return null;
		} finally {
			MethodUtils.closeRessource(bos);
			MethodUtils.closeRessource(fileIn);
			MethodUtils.closeRessource(workbook);
		}
	}
}