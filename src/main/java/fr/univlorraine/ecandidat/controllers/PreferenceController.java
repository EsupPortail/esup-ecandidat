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
import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.v7.data.sort.SortOrder;

import fr.univlorraine.ecandidat.entities.ecandidat.Candidature_;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.entities.ecandidat.PreferenceInd;
import fr.univlorraine.ecandidat.repositories.PreferenceIndRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import jakarta.annotation.Resource;

/**
 * Gestion des preferences
 * @author Kevin Hergalant
 */
@Component
public class PreferenceController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserController userController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient PreferenceIndRepository preferenceIndRepository;

	/**
	 * @param  login
	 * @return       charge les preference d'un login
	 */
	public PreferenceInd getPreferenceIndividu(final String login) {
		return preferenceIndRepository.findByLoginInd(login);
	}

	/**
	 * prepare les preferences d'un individu à être enregsitré, si null, c'est que l'individu n'existe pas
	 * @return les preferences d'un individu
	 */
	public PreferenceInd preparePreferenceToSaveInDb() {
		final String login = userController.getCurrentUserLogin();
		final Individu individu = individuController.getIndividu(login);
		if (individu == null) {
			return null;
		}
		PreferenceInd pref = getPreferenceIndividu(login);
		if (pref == null) {
			pref = new PreferenceInd(individu);
		}
		return pref;
	}

	/** Initialise les preference de la vue */
	public void initPrefCand() {
		savePrefCandInSession(null, null, null, null, false);
	}

	/**
	 * Modifie les preferences de vue dans la session
	 * @param listeColonne
	 * @param listColonneOrder
	 * @param frozen
	 * @param listeSortOrder
	 * @param log
	 */
	public void savePrefCandInSession(final String listeColonne, final String listColonneOrder, final Integer frozen, final List<SortOrder> listeSortOrder, final Boolean log) {
		final PreferenceInd pref = userController.getPreferenceIndividu();
		pref.setCandColVisiblePref(listeColonne);
		pref.setCandColOrderPref(listColonneOrder);
		pref.setCandColFrozenPref(frozen);
		pref.setCandColSortPref(getSortOrder(listeSortOrder));
		userController.setPreferenceIndividu(pref);
		if (log) {
			Notification.show(applicationContext.getMessage("preference.notif.session.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		}
	}

	/**
	 * Modifie les preferences de vue dans la session
	 * @param listeColonne
	 * @param listColonneOrder
	 * @param frozen
	 */
	public void savePrefCandInDb(final String listeColonne, final String listColonneOrder, final Integer frozen, final List<SortOrder> listeSortOrder) {
		final PreferenceInd pref = preparePreferenceToSaveInDb();
		if (pref == null) {
			return;
		}
		pref.setCandColVisiblePref(listeColonne);
		pref.setCandColOrderPref(listColonneOrder);
		pref.setCandColFrozenPref(frozen);
		pref.setCandColSortPref(getSortOrder(listeSortOrder));
		preferenceIndRepository.save(pref);
		savePrefCandInSession(listeColonne, listColonneOrder, frozen, listeSortOrder, false);
		Notification.show(applicationContext.getMessage("preference.notif.db.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
	}

	/**
	 * @param  listeSortOrder
	 * @return                la liste de SortOrder
	 */
	private String getSortOrder(final List<SortOrder> listeSortOrder) {
		if (listeSortOrder == null || isDefaultSortOrder(listeSortOrder)) {
			return null;
		}
		String sortColonne = null;
		for (final SortOrder sort : listeSortOrder) {
			if (sortColonne == null) {
				sortColonne = "";
			}
			sortColonne = sortColonne + sort.getPropertyId()
				+ ConstanteUtils.PREFERENCE_SORT_DIRECTION_DELIMITER
				+ (sort.getDirection().equals(SortDirection.ASCENDING) ? ConstanteUtils.PREFERENCE_SORT_DIRECTION_ASCENDING
					: ConstanteUtils.PREFERENCE_SORT_DIRECTION_DESCENDING)
				+ ";";
		}
		return sortColonne;
	}

	/**
	 * Verifie si on a la liste par défaut de SortOrder
	 * @param  listeSortOrder
	 * @return                true si vrai
	 */
	public Boolean isDefaultSortOrder(final List<SortOrder> listeSortOrder) {
		if (listeSortOrder != null && listeSortOrder.size() > 0 && listeSortOrder.get(0).getPropertyId().equals(Candidature_.idCand.getName())) {
			return true;
		}
		return false;
	}

	/** @return la liste par défaut de SortOrder */
	public List<SortOrder> getDefaultSortOrder() {
		final List<SortOrder> listSortOrder = new ArrayList<>();
		listSortOrder.add(new SortOrder(Candidature_.idCand.getName(), SortDirection.ASCENDING));
		return listSortOrder;
	}

	/**
	 * @param  fields
	 * @param  fieldsToCompare
	 * @return                 vérifie qu'un champs est bien dans la liste des champs, le supprime sinon
	 */
	private String[] transformArrayContainsField(final String[] fields, final String[] fieldsToCompare) {
		final List<String> listeFields = Arrays.asList(fields);
		final List<String> listeFieldsToCompare = Arrays.asList(fieldsToCompare);
		final List<String> listeFieldsToReturn = new ArrayList<>();
		listeFields.forEach(e -> {
			if (listeFieldsToCompare.contains(e)) {
				listeFieldsToReturn.add(e);
			}
		});
		return listeFieldsToReturn.stream().toArray(String[]::new);
	}

	/**
	 * @param  defaultValue
	 * @param  fields
	 * @return              les colonnes de la vue
	 */
	public String[] getPrefCandColonnesVisible(final String[] defaultValue, final String[] fields) {
		final PreferenceInd pref = userController.getPreferenceIndividu();
		if (pref.getCandColVisiblePref() != null) {
			return transformArrayContainsField(pref.getCandColVisiblePref().split(";"), fields);
		} else {
			return defaultValue;
		}
	}

	/**
	 * @param  defaultValue
	 * @return              les colonnes de la vue
	 */
	public String[] getPrefCandColonnesOrder(final String[] defaultValue) {
		final PreferenceInd pref = userController.getPreferenceIndividu();
		if (pref.getCandColOrderPref() != null) {
			return transformArrayContainsField(pref.getCandColOrderPref().split(";"), defaultValue);
		} else {
			return defaultValue;
		}
	}

	/** @return le nombre de colonne gelees */
	public Integer getPrefCandFrozenColonne(final Integer defaultValue) {
		final PreferenceInd pref = userController.getPreferenceIndividu();
		if (pref.getCandColVisiblePref() != null) {
			return pref.getCandColFrozenPref();
		} else {
			return defaultValue;
		}
	}

	/**
	 * @param  fieldsOrder
	 * @return             la colonne de trie
	 */
	public List<SortOrder> getPrefCandSortColonne(final String[] fieldsOrder) {
		final String propertyAsc = ConstanteUtils.PREFERENCE_SORT_DIRECTION_DELIMITER + ConstanteUtils.PREFERENCE_SORT_DIRECTION_ASCENDING;
		final String propertyDesc = ConstanteUtils.PREFERENCE_SORT_DIRECTION_DELIMITER + ConstanteUtils.PREFERENCE_SORT_DIRECTION_DESCENDING;

		final String sortColonne = userController.getPreferenceIndividu().getCandColSortPref();
		if (sortColonne != null) {
			try {
				/* Tableau contenant les property et direction */
				final String[] sortColonneWithDir = sortColonne.split(";");

				/* vérification des éléments et constitution du tableau de field excluant les property qui n'existent plus */
				final String[] arraySort = transformArrayContainsField(sortColonne.replaceAll(propertyAsc, "").replaceAll(propertyDesc, "").split(";"), fieldsOrder);

				/* On parcourt la liste des property et direction, si la property est contenu dans arraySort, on ajoute a la liste finale */
				final List<String> finalList = new ArrayList<>();
				for (final String str : sortColonneWithDir) {
					if (Arrays.asList(arraySort).contains(str.replaceAll(propertyAsc, "").replaceAll(propertyDesc, ""))) {
						finalList.add(str);
					}
				}
				/* Constitution de la liste de sort à retourner */
				final List<SortOrder> listSortOrder = new ArrayList<>();
				finalList.forEach(str -> {
					final String[] arrayOneSort = str.split(":");
					listSortOrder.add(new SortOrder(arrayOneSort[0], arrayOneSort[1].equals(ConstanteUtils.PREFERENCE_SORT_DIRECTION_ASCENDING) ? SortDirection.ASCENDING : SortDirection.DESCENDING));
				});
				return listSortOrder;
			} catch (final Exception e) {
			}

		}
		return getDefaultSortOrder();
	}

	/** @return la commission favorite */
	public Integer getPrefCandIdComm() {
		return userController.getPreferenceIndividu().getCandIdCommPref();
	}

	/**
	 * Modifie la commission favorite
	 * @param commission
	 */
	public void setPrefCandIdComm(final Commission commission) {
		/* On le modifie en session.. */
		final PreferenceInd prefInSession = userController.getPreferenceIndividu();
		if (commission == null) {
			prefInSession.setCandIdCommPref(null);
			return;
		} else {
			prefInSession.setCandIdCommPref(commission.getIdComm());
		}
		/* .. et en base */
		final PreferenceInd pref = preparePreferenceToSaveInDb();
		if (pref == null) {
			return;
		}
		pref.setCandIdCommPref(commission.getIdComm());
		preferenceIndRepository.save(pref);
	}

	/** Initialise les preference de l'export */
	public void initPrefExport() {
		savePrefExportInSession(null, true, false);
	}

	/**
	 * Enregistre les preference d'export en session
	 * @param valeurColonneCoche
	 * @param temFooter
	 * @param log
	 */
	public void savePrefExportInSession(final String valeurColonneCoche, final Boolean temFooter, final Boolean log) {
		final PreferenceInd pref = userController.getPreferenceIndividu();
		pref.setExportColPref(valeurColonneCoche);
		pref.setExportTemFooterPref(temFooter);
		userController.setPreferenceIndividu(pref);
		if (log) {
			Notification.show(applicationContext.getMessage("preference.notif.session.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
		}
	}

	/**
	 * Enregistre les preference d'export en base
	 * @param valeurColonneCoche
	 * @param temFooter
	 */
	public void savePrefExportInDb(final String valeurColonneCoche, final Boolean temFooter) {
		final PreferenceInd pref = preparePreferenceToSaveInDb();
		if (pref == null) {
			return;
		}
		pref.setExportColPref(valeurColonneCoche);
		pref.setExportTemFooterPref(temFooter);
		preferenceIndRepository.save(pref);
		savePrefExportInSession(valeurColonneCoche, temFooter, false);
		Notification.show(applicationContext.getMessage("preference.notif.db.ok", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
	}

	/** @return les colonnes de l'export */
	public String[] getPrefExportColonnes() {
		final PreferenceInd pref = userController.getPreferenceIndividu();
		if (pref.getExportColPref() != null) {
			return pref.getExportColPref().split(";");
		}
		return null;
	}

	/** @return lee footer de l'export */
	public Boolean getPrefExportFooter() {
		final PreferenceInd pref = userController.getPreferenceIndividu();
		if (pref.getExportTemFooterPref() != null && !pref.getExportTemFooterPref()) {
			return false;
		}
		return true;
	}

	/**
	 * Modifie la commission favorite en session et en base
	 * @param commission
	 */
	public void setPrefCommission(final Commission commission) {
		/* On le modifie en session.. */
		final PreferenceInd prefInSession = userController.getPreferenceIndividu();
		if (commission == null) {
			return;
		} else {
			prefInSession.setIdCommPref(commission.getIdComm());
		}
		/* .. et en base */
		final PreferenceInd pref = preparePreferenceToSaveInDb();
		if (pref == null) {
			return;
		}
		pref.setIdCommPref(commission.getIdComm());
		preferenceIndRepository.save(pref);
	}

	/**
	 * Modifie le centre de candidature favorit en session et en base
	 * @param ctrCand
	 */
	public void setPrefCentreCandidature(final CentreCandidature ctrCand) {
		/* On le modifie en session.. */
		final PreferenceInd prefInSession = userController.getPreferenceIndividu();
		if (ctrCand == null) {
			return;
		} else {
			prefInSession.setIdCtrCandPref(ctrCand.getIdCtrCand());
		}
		/* .. et en base */
		final PreferenceInd pref = preparePreferenceToSaveInDb();
		if (pref == null) {
			return;
		}
		pref.setIdCtrCandPref(ctrCand.getIdCtrCand());
		preferenceIndRepository.save(pref);
	}
}
