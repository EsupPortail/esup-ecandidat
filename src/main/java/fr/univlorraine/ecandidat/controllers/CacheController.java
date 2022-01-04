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
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.AlertSva;
import fr.univlorraine.ecandidat.entities.ecandidat.Campagne;
import fr.univlorraine.ecandidat.entities.ecandidat.Civilite;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitFonctionnalite;
import fr.univlorraine.ecandidat.entities.ecandidat.Faq;
import fr.univlorraine.ecandidat.entities.ecandidat.Langue;
import fr.univlorraine.ecandidat.entities.ecandidat.Message;
import fr.univlorraine.ecandidat.entities.ecandidat.Parametre;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOptBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacSpeBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCatExoExt;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolOptionBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolSpecialiteBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.utils.ConcurrentCache;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfCtrCand;
import fr.univlorraine.ecandidat.utils.bean.odf.OdfDiplome;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleTablePresentation;
import fr.univlorraine.ecandidat.views.windows.ConfirmWindow;

/**
 * Gestion du cache
 * @author Kevin Hergalant
 */
@Component
@SuppressWarnings("unchecked")
public class CacheController {

	@Resource
	private transient ApplicationContext applicationContext;

	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient MessageController messageController;
	@Resource
	private transient AlertSvaController alertSvaController;
	@Resource
	private transient TagController tagController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient FaqController faqController;
	@Resource
	private transient LangueController langueController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient OffreFormationController offreFormationController;

	private final ConcurrentCache mapCache = new ConcurrentCache();

	/**
	 * @return la liste des messages du cache
	 */
	public List<Message> getMessages() {
		final List<Message> liste = mapCache.getFromCache(ConstanteUtils.CACHE_MSG, List.class);
		if (liste == null) {
			final List<Message> listeLoad = messageController.getMessagesToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_MSG, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les messages du cache
	 */
	public void reloadMessages(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_MSG, messageController.getMessagesToCache(), List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_MSG, needToPushToCandidat);
	}

	/**
	 * @return la campagne en service du cache
	 */
	public Campagne getCampagneEnService() {
		final Campagne campagne = mapCache.getFromCache(ConstanteUtils.CACHE_CAMP, Campagne.class);
		if (campagne == null) {
			final Campagne campagneLoad = campagneController.getCampagneEnServiceToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_CAMP, campagneLoad, Campagne.class);
			return campagneLoad;
		} else {
			return campagne;
		}
	}

	/**
	 * recharge la campagne en service du cache
	 */
	public void reloadCampagneEnService(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_CAMP,
			campagneController.getCampagneEnServiceToCache(),
			Campagne.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_CAMP, needToPushToCandidat);
	}

	/**
	 * @return la liste les alertes SVA du cache
	 */
	public List<Faq> getFaq() {
		final List<Faq> liste = mapCache.getFromCache(ConstanteUtils.CACHE_FAQ, List.class);
		if (liste == null) {
			final List<Faq> listeLoad = faqController.getFaqToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_FAQ, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les alertes SVA du cache
	 */
	public void reloadFaq(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_FAQ, faqController.getFaqToCache(), List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_FAQ, needToPushToCandidat);
	}

	/**
	 * @return les langues en service du cache
	 */
	public List<Langue> getLangueEnServiceWithoutDefault() {
		final List<Langue> liste = mapCache.getFromCache(ConstanteUtils.CACHE_LANGUE, List.class);
		if (liste == null) {
			final List<Langue> listeLoad = langueController.getLanguesActivesWithoutDefaultToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_LANGUE, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * @return la langne par défaut du cache
	 */
	public Langue getLangueDefault() {
		final Langue langue = mapCache.getFromCache(ConstanteUtils.CACHE_LANGUE_DEFAULT, Langue.class);
		if (langue == null) {
			final Langue langueLoad = langueController.getLangueDefaultToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_LANGUE_DEFAULT, langueLoad, Langue.class);
			return langueLoad;
		} else {
			return langue;
		}
	}

	/**
	 * Recharge les langues et langues par défaut
	 * @param needToPushToCandidat
	 */
	public void reloadLangues(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_LANGUE,
			langueController.getLanguesActivesWithoutDefaultToCache(),
			List.class);
		mapCache.putToCache(ConstanteUtils.CACHE_LANGUE_DEFAULT,
			langueController.getLangueDefaultToCache(),
			Langue.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_LANGUE, needToPushToCandidat);
	}

	/**
	 * @return la liste les alertes SVA du cache
	 */
	public Map<String, Parametre> getMapParametre() {
		final Map<String, Parametre> map = mapCache.getFromCache(ConstanteUtils.CACHE_PARAM, Map.class);
		if (map == null) {
			final Map<String, Parametre> mapLoad = parametreController.getMapParametreToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_PARAM, mapLoad, Map.class);
			return mapLoad;
		} else {
			return map;
		}
	}

	/**
	 * recharge les alertes SVA du cache
	 */
	public void reloadMapParametre(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_PARAM, parametreController.getMapParametreToCache(), Map.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_PARAM, needToPushToCandidat);
	}

	/**
	 * @return les pays en service du cache
	 */
	public List<SiScolPays> getListePays() {
		final List<SiScolPays> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_PAYS, List.class);
		if (liste == null) {
			final List<SiScolPays> listeLoad = tableRefController.getListPaysToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_PAYS, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * @return la france du cache
	 */
	public SiScolPays getPaysFrance() {
		final SiScolPays pays = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_PAYS_FRANCE, SiScolPays.class);
		if (pays == null) {
			final SiScolPays paysLoad = tableRefController.getPaysFranceToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_PAYS_FRANCE, paysLoad, SiScolPays.class);
			return paysLoad;
		} else {
			return pays;
		}
	}

	/**
	 * recharge les pays en service du cache
	 */
	public void reloadListePays(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_PAYS, tableRefController.getListPaysToCache(), List.class);
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_PAYS_FRANCE,
			tableRefController.getPaysFranceToCache(),
			SiScolPays.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_PAYS, needToPushToCandidat);
	}

	/**
	 * @return la liste des types d'avis du cache
	 */
	public List<TypeAvis> getListeTypeAvis() {
		final List<TypeAvis> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_TYPAVIS, List.class);
		if (liste == null) {
			final List<TypeAvis> listeLoad = tableRefController.getListeTypeAvisToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPAVIS, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge lla liste des types d'avis
	 */
	private void reloadListeTypeAvis() {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPAVIS,
			tableRefController.getListeTypeAvisToCache(),
			List.class);
	}

	/**
	 * @return la liste des civilite du cache
	 */
	public List<Civilite> getListeCivilte() {
		final List<Civilite> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_CIVILITE, List.class);
		if (liste == null) {
			final List<Civilite> listeLoad = tableRefController.getListeCivilteToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_CIVILITE, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge lla liste des civilités
	 */
	private void reloadListeCivilte() {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_CIVILITE,
			tableRefController.getListeCivilteToCache(),
			List.class);
	}

	/**
	 * @return la liste des types de statut de pieces du cache
	 */
	public List<TypeStatutPiece> getListeTypeStatutPiece() {
		final List<TypeStatutPiece> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_TYPSTATUT_PJ, List.class);
		if (liste == null) {
			final List<TypeStatutPiece> listeLoad = tableRefController.getListeTypeStatutPieceToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPSTATUT_PJ, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge lla liste des types de statut de pieces du cache
	 */
	public void reloadListeTypeStatutPiece(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPSTATUT_PJ,
			tableRefController.getListeTypeStatutPieceToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_TYPSTATUT_PJ, needToPushToCandidat);
	}

	/**
	 * @return la liste des types de statut du cache
	 */
	public List<TypeStatut> getListeTypeStatut() {
		final List<TypeStatut> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_TYPSTATUT, List.class);
		if (liste == null) {
			final List<TypeStatut> listeLoad = tableRefController.getListeTypeStatutToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPSTATUT, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * @return la liste des types de statut du cache
	 */
	public List<TypeStatut> getListeCodTypeStatutVisibleToCommission() {
		return getListeTypeStatut().stream()
			.filter(e -> e.getTemCommVisible())
			// .map(TypeStatut::getCodTypStatut)
			.collect(Collectors.toList());
	}

	/**
	 * recharge la liste des types de statut du cache
	 */
	public void reloadListeTypeStatut(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPSTATUT,
			tableRefController.getListeTypeStatutToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_TYPSTATUT, needToPushToCandidat);
	}

	/**
	 * @return la liste des types de traitement du cache
	 */
	public List<TypeTraitement> getListeTypeTraitement() {
		final List<TypeTraitement> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_TYPTRAIT, List.class);
		if (liste == null) {
			final List<TypeTraitement> listeLoad = tableRefController.getListeTypeTraitementToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPTRAIT, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge la liste des types de traitement du cache
	 */
	public void reloadListeTypeTraitement(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPTRAIT,
			tableRefController.getListeTypeTraitementToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_TYPTRAIT, needToPushToCandidat);
	}

	/**
	 * @return la liste des type de diplome
	 */
	public List<SiScolTypDiplome> getListeTypDiplome() {
		final List<SiScolTypDiplome> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_TYPDIP, List.class);
		if (liste == null) {
			final List<SiScolTypDiplome> listeLoad = tableRefController.getListeTypDiplomeToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPDIP, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge la liste des types de diplomes
	 */
	public void reloadListeTypDiplome(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPDIP,
			tableRefController.getListeTypDiplomeToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_TYPDIP, needToPushToCandidat);
	}

	/**
	 * @return la liste des catégorie exonération/extracommunautaire
	 */
	public List<SiScolCatExoExt> getListeCatExoExt() {
		final List<SiScolCatExoExt> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_CATEXOEXT, List.class);
		if (liste == null) {
			final List<SiScolCatExoExt> listeLoad = tableRefController.getListeCatExoExtToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_CATEXOEXT, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge la liste des catégorie exonération/extracommunautaire
	 */
	public void reloadListeCatExoExt(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_CATEXOEXT,
			tableRefController.getListeCatExoExtToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_CATEXOEXT, needToPushToCandidat);
	}

	/**
	 * @return la liste des cge du cache
	 */
	public List<SiScolCentreGestion> getListeCentreGestion() {
		final List<SiScolCentreGestion> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_CGE, List.class);
		if (liste == null) {
			final List<SiScolCentreGestion> listeLoad = tableRefController.getListeCentreGestionToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_CGE, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les cge du cache
	 */
	public void reloadListeCentreGestion(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_CGE,
			tableRefController.getListeCentreGestionToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_CGE, needToPushToCandidat);
	}

	/**
	 * @return la liste des année univ du cache
	 */
	public List<SiScolAnneeUni> getListeAnneeUni() {
		final List<SiScolAnneeUni> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_ANNEE_UNI, List.class);
		if (liste == null) {
			final List<SiScolAnneeUni> listeLoad = tableRefController.getListeAnneeUnisToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_ANNEE_UNI, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les années universitaires
	 * @param needToPushToCandidat
	 */
	public void reloadListeAnneeUni(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_ANNEE_UNI,
			tableRefController.getListeAnneeUnisToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_ANNEE_UNI, needToPushToCandidat);
	}

	/**
	 * @return la liste des options de bac
	 */
	public List<SiScolOptionBac> getListeOptionBac() {
		final List<SiScolOptionBac> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_OPTION_BAC, List.class);
		if (liste == null) {
			final List<SiScolOptionBac> listeLoad = tableRefController.getListeOptionBacToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_OPTION_BAC, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les options de bac
	 * @param needToPushToCandidat
	 */
	public void reloadListeOptionBac(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_OPTION_BAC,
			tableRefController.getListeOptionBacToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_OPTION_BAC, needToPushToCandidat);
	}

	/**
	 * @return la liste des specialités de bac
	 */
	public List<SiScolSpecialiteBac> getListeSpecialiteBac() {
		final List<SiScolSpecialiteBac> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_SPECIALITE_BAC, List.class);
		if (liste == null) {
			final List<SiScolSpecialiteBac> listeLoad = tableRefController.getListeSpecialiteBacToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_SPECIALITE_BAC, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les specialités de bac
	 * @param needToPushToCandidat
	 */
	public void reloadListeSpecialiteBac(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_SPECIALITE_BAC,
			tableRefController.getListeSpecialiteBacToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_SPECIALITE_BAC, needToPushToCandidat);
	}

	/**
	 * @return la liste des relations bac/options
	 */
	public List<SiScolBacOptBac> getListeBacOptBac() {
		final List<SiScolBacOptBac> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_BAC_OPT_BAC, List.class);
		if (liste == null) {
			final List<SiScolBacOptBac> listeLoad = tableRefController.getListeBacOptBacToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_BAC_OPT_BAC, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les relations bac/options
	 * @param needToPushToCandidat
	 */
	public void reloadListeBacOptBac(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_BAC_OPT_BAC,
			tableRefController.getListeBacOptBacToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_BAC_OPT_BAC, needToPushToCandidat);
	}

	/**
	 * @return la liste des relations bac/spécialités
	 */
	public List<SiScolBacSpeBac> getListeBacSpeBac() {
		final List<SiScolBacSpeBac> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_BAC_SPE_BAC, List.class);
		if (liste == null) {
			final List<SiScolBacSpeBac> listeLoad = tableRefController.getListeBacSpeBacToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_BAC_SPE_BAC, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les relations bac/spécialités
	 * @param needToPushToCandidat
	 */
	public void reloadListeBacSpeBac(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_BAC_SPE_BAC,
			tableRefController.getListeBacSpeBacToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_BAC_SPE_BAC, needToPushToCandidat);
	}

	/**
	 * @return la liste des departements du cache
	 */
	public List<SiScolDepartement> getListDepartement() {
		final List<SiScolDepartement> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_DPT, List.class);
		if (liste == null) {
			final List<SiScolDepartement> listeLoad = tableRefController.getListDepartementToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_DPT, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les departements du cache
	 */
	public void reloadListeDepartement(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_DPT,
			tableRefController.getListDepartementToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_DPT, needToPushToCandidat);
	}

	/**
	 * @return la liste des bacs du cache
	 */
	public List<SiScolBacOuxEqu> getListeBacOuxEqu() {
		final List<SiScolBacOuxEqu> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_BAC, List.class);
		if (liste == null) {
			final List<SiScolBacOuxEqu> listeLoad = tableRefController.getListeBacOuxEquToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_BAC, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les bacs du cache
	 */
	public void reloadListeBacOuxEqu(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_BAC,
			tableRefController.getListeBacOuxEquToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_BAC, needToPushToCandidat);
	}

	/**
	 * @return la liste des diplomes du cache
	 */
	public List<SiScolDipAutCur> getListeDipAutCur() {
		final List<SiScolDipAutCur> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_DIP, List.class);
		if (liste == null) {
			final List<SiScolDipAutCur> listeLoad = tableRefController.getListeDipAutCurToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_DIP, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les diplomes du cache
	 */
	public void reloadListeDipAutCur(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_DIP,
			tableRefController.getListeDipAutCurToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_DIP, needToPushToCandidat);
	}

	/**
	 * @return la liste des mentions niv bac du cache
	 */
	public List<SiScolMentionNivBac> getListeMentionNivBac() {
		final List<SiScolMentionNivBac> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_MENTBAC, List.class);
		if (liste == null) {
			final List<SiScolMentionNivBac> listeLoad = tableRefController.getListeMentionNivBacToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_MENTBAC, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les mentions niv bac du cache
	 */
	public void reloadListeMentionNivBac(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_MENTBAC,
			tableRefController.getListeMentionNivBacToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_MENTBAC, needToPushToCandidat);
	}

	/**
	 * @return la liste des mentions du cache
	 */
	public List<SiScolMention> getListeMention() {
		final List<SiScolMention> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_MENTION, List.class);
		if (liste == null) {
			final List<SiScolMention> listeLoad = tableRefController.getListeMentionToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_MENTION, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les mentions du cache
	 */
	public void reloadListeMention(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_MENTION,
			tableRefController.getListeMentionToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_MENTION, needToPushToCandidat);
	}

	/**
	 * @return la liste des types de resultats du cache
	 */
	public List<SiScolTypResultat> getListeTypeResultat() {
		final List<SiScolTypResultat> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_TYPRES, List.class);
		if (liste == null) {
			final List<SiScolTypResultat> listeLoad = tableRefController.getListeTypeResultatToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPRES, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les type de resultat du cache
	 */
	public void reloadListeTypeResultat(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_TYPRES,
			tableRefController.getListeTypeResultatToCache(),
			List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_TABLE_REF_TYPRES, needToPushToCandidat);
	}

	/**
	 * @return la liste des foncitonnalite
	 */
	public List<DroitFonctionnalite> getListeDroitFonctionnaliteCandidature() {
		final List<DroitFonctionnalite> liste = mapCache.getFromCache(ConstanteUtils.CACHE_TABLE_REF_FONCTIONNALITE,
			List.class);
		if (liste == null) {
			final List<DroitFonctionnalite> listeLoad = droitProfilController.getListeDroitFonctionnaliteCandidatureToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_FONCTIONNALITE, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	private void reloadListeDroitFonctionnaliteCandidature() {
		mapCache.putToCache(ConstanteUtils.CACHE_TABLE_REF_FONCTIONNALITE,
			droitProfilController.getListeDroitFonctionnaliteCandidatureToCache(),
			List.class);
	}

	/**
	 * @return la liste de l'offre de formation
	 */
	public List<OdfCtrCand> getOdf() {
		final List<OdfCtrCand> liste = mapCache.getFromCache(ConstanteUtils.CACHE_ODF, List.class);
		if (liste == null) {
			final List<OdfCtrCand> listeLoad = offreFormationController.getOdfToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_ODF, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * Met a jour l'offre de formation
	 * @param liste
	 */
	public void updateOdf(final List<OdfCtrCand> liste) {
		mapCache.putToCache(ConstanteUtils.CACHE_ODF, liste, List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_ODF, true);
	}

	/**
	 * recharge les bacs du cache
	 */
	public void reloadOdf(final Boolean needToPushToCandidat) {
		mapCache.putToCache(ConstanteUtils.CACHE_ODF, offreFormationController.getOdfToCache(), List.class);
		loadBalancingController.askToReloadData(ConstanteUtils.CACHE_ODF, needToPushToCandidat);
	}

	/**
	 * @return la liste les alertes SVA du cache
	 */
	public List<AlertSva> getAlertesSva() {
		final List<AlertSva> liste = mapCache.getFromCache(ConstanteUtils.CACHE_SVA, List.class);
		if (liste == null) {
			final List<AlertSva> listeLoad = alertSvaController.getAlertSvaToCache();
			mapCache.putToCache(ConstanteUtils.CACHE_SVA, listeLoad, List.class);
			return listeLoad;
		} else {
			return liste;
		}
	}

	/**
	 * recharge les alertes SVA du cache
	 */
	public void reloadAlertesSva() {
		mapCache.putToCache(ConstanteUtils.CACHE_SVA, alertSvaController.getAlertSvaToCache(), List.class);
	}

	/** Load tout les caches au demarrage */
	public void loadAllCaches() {
		getListePays();
		getPaysFrance();
		getListDepartement();
		getListeTypDiplome();
		getListeCatExoExt();
		getListeCentreGestion();
		getListeAnneeUni();
		getListeBacOuxEqu();
		getListeOptionBac();
		getListeBacOptBac();
		getListeSpecialiteBac();
		getListeBacSpeBac();
		getListeDipAutCur();
		getListeMention();
		getListeMentionNivBac();
		getListeTypeResultat();
		getMapParametre();
		getLangueEnServiceWithoutDefault();
		getLangueDefault();
		getListeTypeTraitement();
		getListeTypeAvis();
		getListeTypeStatut();
		getListeTypeStatutPiece();
		getListeCivilte();
		getListeDroitFonctionnaliteCandidature();
		getFaq();
		getOdf();
		getCampagneEnService();
		getMessages();
	}

	/**
	 * Recharge les données suivant un code
	 * @param code
	 */
	public void reloadData(final String code, final Boolean needToPushToCandidat) {
		switch (code) {
		case ConstanteUtils.CACHE_TABLE_REF_PAYS:
			reloadListePays(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_DPT:
			reloadListeDepartement(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_TYPDIP:
			reloadListeTypDiplome(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_CATEXOEXT:
			reloadListeCatExoExt(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_CGE:
			reloadListeCentreGestion(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_ANNEE_UNI:
			reloadListeAnneeUni(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_BAC:
			reloadListeBacOuxEqu(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_OPTION_BAC:
			reloadListeOptionBac(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_BAC_OPT_BAC:
			reloadListeBacOptBac(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_SPECIALITE_BAC:
			reloadListeSpecialiteBac(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_BAC_SPE_BAC:
			reloadListeBacSpeBac(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_DIP:
			reloadListeDipAutCur(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_MENTION:
			reloadListeMention(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_MENTBAC:
			reloadListeMentionNivBac(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_TYPRES:
			reloadListeTypeResultat(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_TYPTRAIT:
			reloadListeTypeTraitement(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_TYPSTATUT:
			reloadListeTypeStatut(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_TABLE_REF_TYPSTATUT_PJ:
			reloadListeTypeStatutPiece(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_PARAM:
			reloadMapParametre(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_LANGUE:
			reloadLangues(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_FAQ:
			reloadFaq(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_ODF:
			reloadOdf(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_CAMP:
			reloadCampagneEnService(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_MSG:
			reloadMessages(needToPushToCandidat);
			break;
		case ConstanteUtils.CACHE_SVA:
			reloadAlertesSva();
			break;
		case ConstanteUtils.CACHE_TABLE_REF_FONCTIONNALITE:
			reloadListeDroitFonctionnaliteCandidature();
			break;
		case ConstanteUtils.CACHE_TABLE_REF_CIVILITE:
			reloadListeCivilte();
			break;
		case ConstanteUtils.CACHE_TABLE_REF_TYPAVIS:
			reloadListeTypeAvis();
			break;
		default:
			break;
		}
	}

	/**
	 * Rechqrge tout le cqche
	 */
	private void reloadAllCache() {
		mapCache.forEach((e, f) -> {
			reloadData(e, true);
		});
	}

	/**
	 * @return la liste de presentation
	 */
	@SuppressWarnings("rawtypes")
	public List<SimpleTablePresentation> getListPresentation() {
		final List<SimpleTablePresentation> liste = new ArrayList<>();
		mapCache.forEach((e, f) -> {
			if (!e.equals(ConstanteUtils.CACHE_LANGUE_DEFAULT)
				&& !e.equals(ConstanteUtils.CACHE_TABLE_REF_PAYS_FRANCE)) {
				String element = "1";
				if (f instanceof List) {
					if (e.equals(ConstanteUtils.CACHE_ODF)) {
						Integer i = 0;
						if (f != null) {
							final List<OdfCtrCand> listeOdf = (List<OdfCtrCand>) f;
							for (final OdfCtrCand ctr : listeOdf) {
								if (ctr.getListeDiplome() != null) {
									for (final OdfDiplome dip : ctr.getListeDiplome()) {
										if (dip.getListeFormation() != null) {
											i = i + dip.getListeFormation().size();
										}
									}
								}
							}
						}
						element = String.valueOf(i);
					} else {
						element = String.valueOf(((List) f).size());
					}
				}
				if (f instanceof Map) {
					element = String.valueOf(((Map) f).size());
				}
				final SimpleTablePresentation stp = new SimpleTablePresentation(liste.size() + 1,
					e,
					applicationContext.getMessage("cache.libelle." + e, null, UI.getCurrent().getLocale()),
					element,
					null);
				liste.add(stp);
			}
		});
		return liste;
	}

	/**
	 * Demande de rechargement du cache
	 * @param code
	 */
	public void askToReloadData(final String code) {
		final ConfirmWindow cw = new ConfirmWindow(
			applicationContext.getMessage("cache.reload.confirm", null, UI.getCurrent().getLocale()));
		cw.addBtnOuiListener(e -> {
			if (code == null) {
				reloadAllCache();
			} else {
				reloadData(code, true);
			}
			Notification.show(applicationContext.getMessage("cache.reload.ok", null, UI.getCurrent().getLocale()),
				Type.TRAY_NOTIFICATION);
		});
		UI.getCurrent().addWindow(cw);
	}
}
