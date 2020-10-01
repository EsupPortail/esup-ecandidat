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
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.entities.ecandidat.Civilite;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissementPK;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeAvis;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeStatutPiece;
import fr.univlorraine.ecandidat.entities.ecandidat.TypeTraitement;
import fr.univlorraine.ecandidat.repositories.CiviliteRepository;
import fr.univlorraine.ecandidat.repositories.LangueRepository;
import fr.univlorraine.ecandidat.repositories.ParametreRepository;
import fr.univlorraine.ecandidat.repositories.SiScolAnneeUniRepository;
import fr.univlorraine.ecandidat.repositories.SiScolBacOuxEquRepository;
import fr.univlorraine.ecandidat.repositories.SiScolCentreGestionRepository;
import fr.univlorraine.ecandidat.repositories.SiScolCommuneRepository;
import fr.univlorraine.ecandidat.repositories.SiScolDepartementRepository;
import fr.univlorraine.ecandidat.repositories.SiScolDipAutCurRepository;
import fr.univlorraine.ecandidat.repositories.SiScolEtablissementRepository;
import fr.univlorraine.ecandidat.repositories.SiScolMentionNivBacRepository;
import fr.univlorraine.ecandidat.repositories.SiScolMentionRepository;
import fr.univlorraine.ecandidat.repositories.SiScolPaysRepository;
import fr.univlorraine.ecandidat.repositories.SiScolTypDiplomeRepository;
import fr.univlorraine.ecandidat.repositories.SiScolTypResultatRepository;
import fr.univlorraine.ecandidat.repositories.TypeAvisRepository;
import fr.univlorraine.ecandidat.repositories.TypeStatutPieceRepository;
import fr.univlorraine.ecandidat.repositories.TypeStatutRepository;
import fr.univlorraine.ecandidat.repositories.TypeTraitementRepository;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import fr.univlorraine.ecandidat.utils.bean.presentation.SimpleBeanPresentation;

/**
 * Gestion des tables ref
 * @author Kevin Hergalant
 */
@Component
public class TableRefController {
	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient CacheController cacheController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient TypeAvisRepository typeAvisRepository;
	@Resource
	private transient TypeStatutPieceRepository typeStatutPieceRepository;
	@Resource
	private transient TypeStatutRepository typeStatutRepository;
	@Resource
	private transient TypeTraitementRepository typeTraitementRepository;
	@Resource
	private transient LangueRepository langueRepository;
	@Resource
	private transient CiviliteRepository civiliteRepository;
	@Resource
	private transient ParametreRepository parametreRepository;
	@Resource
	private transient SiScolPaysRepository siScolPaysRepository;
	@Resource
	private transient SiScolDepartementRepository siScolDepartementRepository;
	@Resource
	private transient SiScolEtablissementRepository siScolEtablissementRepository;
	@Resource
	private transient SiScolCommuneRepository siScolCommuneRepository;
	@Resource
	private transient SiScolTypDiplomeRepository siScolTypDiplomeRepository;
//	@Resource
//	private transient SiScolCatExoExtRepository siScolCatExoExtRepository;
	@Resource
	private transient SiScolCentreGestionRepository siScolCentreGestionRepository;

	@Resource
	private transient SiScolBacOuxEquRepository siScolBacOuxEquRepository;
	@Resource
	private transient SiScolDipAutCurRepository siScolDipAutCurRepository;
	@Resource
	private transient SiScolMentionNivBacRepository siScolMentionNivBacRepository;
	@Resource
	private transient SiScolMentionRepository siScolMentionRepository;
	@Resource
	private transient SiScolTypResultatRepository siScolTypResultatRepository;
	@Resource
	private transient SiScolAnneeUniRepository siScolAnneeUniRepository;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/**
	 * @return la liste de types d'avis
	 */
	public List<TypeAvis> getListeTypeAvisToCache() {
		return typeAvisRepository.findAll();
	}

	/**
	 * @return la liste de civilité
	 */
	public List<Civilite> getListeCivilteToCache() {
		return civiliteRepository.findAll();
	}

	/**
	 * @return la liste de types de statut de pièce
	 */
	public List<TypeStatutPiece> getListeTypeStatutPieceToCache() {
		return typeStatutPieceRepository.findAll();
	}

	/**
	 * @return la liste de types de statut
	 */
	public List<TypeStatut> getListeTypeStatutToCache() {
		return typeStatutRepository.findAll();
	}

	/**
	 * @return la liste de types de statut de pièce
	 */
	public List<TypeTraitement> getListeTypeTraitementToCache() {
		return typeTraitementRepository.findAll();
	}

	/**
	 * @return la liste de types de diplome
	 */
	public List<SiScolTypDiplome> getListeTypDiplomeToCache() {
		return siScolTypDiplomeRepository.findByIdTypSiScol(siScolService.getTypSiscol());
	}

	/**
	 * @return la liste des catégorie exonération/extracommunautaire
	 */
//	public List<SiScolCatExoExt> getListeCatExoExtToCache() {
//		return siScolCatExoExtRepository.findAll();
//	}

	/**
	 * @return la liste des centres de gestion
	 */
	public List<SiScolCentreGestion> getListeCentreGestionToCache() {
		return siScolCentreGestionRepository.findByIdTypSiScol(siScolService.getTypSiscol());
	}

	/**
	 * Cherche les année univ valides
	 * @return les années univ valides
	 */
	public List<SiScolAnneeUni> getListeAnneeUnisToCache() {
		return siScolAnneeUniRepository.findByIdTypSiScol(siScolService.getTypSiscol());
	}

	/**
	 * @return la liste des departements apogée
	 */
	public List<SiScolDepartement> getListDepartementToCache() {
		return siScolDepartementRepository.findByIdTypSiScol(siScolService.getTypSiscol());
	}

	/**
	 * @return la liste des bac ou equ
	 */
	public List<SiScolBacOuxEqu> getListeBacOuxEquToCache() {
		return siScolBacOuxEquRepository.findByIdTypSiScolOrderByLibBacAsc(siScolService.getTypSiscol());
	}

	/**
	 * @return la liste des dip aut cur
	 */
	public List<SiScolDipAutCur> getListeDipAutCurToCache() {
		return siScolDipAutCurRepository.findByIdTypSiScolOrderByLibDacAsc(siScolService.getTypSiscol());
	}

	/**
	 * @return la liste des Mention Niv Bac
	 */
	public List<SiScolMentionNivBac> getListeMentionNivBacToCache() {
		return siScolMentionNivBacRepository.findByIdTypSiScol(siScolService.getTypSiscol());
	}

	/**
	 * @return la liste des Mention
	 */
	public List<SiScolMention> getListeMentionToCache() {
		return siScolMentionRepository.findByIdTypSiScol(siScolService.getTypSiscol());
	}

	/**
	 * @return la liste des Types de resultats
	 */
	public List<SiScolTypResultat> getListeTypeResultatToCache() {
		return siScolTypResultatRepository.findByIdTypSiScol(siScolService.getTypSiscol());
	}

	/**
	 * @return la liste des pays apogée
	 */
	public List<SiScolPays> getListPaysToCache() {
		final List<SiScolPays> listeSiScolPays = new ArrayList<>();
		final SiScolPays paysFrance = siScolPaysRepository.findByIdTypSiScolAndIdCodPay(siScolService.getTypSiscol(), siScolService.getCodPaysFrance());
		if (paysFrance != null) {
			listeSiScolPays.add(paysFrance);
		}
		listeSiScolPays.addAll(siScolPaysRepository.findByIdTypSiScolAndIdCodPayNotOrderByLibPay(siScolService.getTypSiscol(), siScolService.getCodPaysFrance()));
		return listeSiScolPays;
	}

	/**
	 * @return la france
	 */
	public SiScolPays getPaysFranceToCache() {
		final List<SiScolPays> liste = cacheController.getListePays();
		if (liste != null && liste.size() > 0) {
			return getPaysByCode(siScolService.getCodPaysFrance());
		} else {
			return null;
		}
	}

	/**
	 * @return Le type avis favorable
	 */
	public TypeAvis getTypeAvisFavorable() {
		return cacheController.getListeTypeAvis().stream().filter(e -> e.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_FAV)).findFirst().get();
	}

	/**
	 * @return Le type avis liste completmentairee
	 */
	public TypeAvis getTypeAvisListComp() {
		return cacheController.getListeTypeAvis().stream().filter(e -> e.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_LISTE_COMP)).findFirst().get();
	}

	/**
	 * @return Le type avis defavorable
	 */
	public TypeAvis getTypeAvisDefavorable() {
		return cacheController.getListeTypeAvis().stream().filter(e -> e.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_DEF)).findFirst().get();
	}

	/**
	 * @return Le type avis preselect
	 */
	public TypeAvis getTypeAvisPreselect() {
		return cacheController.getListeTypeAvis().stream().filter(e -> e.getCodTypAvis().equals(NomenclatureUtils.TYP_AVIS_PRESELECTION)).findFirst().get();
	}

	/**
	 * @return le type de statut en attente
	 */
	public TypeStatut getTypeStatutEnAttente() {
		return cacheController.getListeTypeStatut().stream().filter(e -> e.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_ATT)).findFirst().get();
	}

	/**
	 * @return le type de statut complet
	 */
	public TypeStatut getTypeStatutComplet() {
		return cacheController.getListeTypeStatut().stream().filter(e -> e.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_COM)).findFirst().get();
	}

	/**
	 * @return le type de statut complet
	 */
	public TypeStatut getTypeStatutIncomplet() {
		return cacheController.getListeTypeStatut().stream().filter(e -> e.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_INC)).findFirst().get();
	}

	/**
	 * @return le type de statut complet
	 */
	public TypeStatut getTypeStatutReceptionne() {
		return cacheController.getListeTypeStatut().stream().filter(e -> e.getCodTypStatut().equals(NomenclatureUtils.TYPE_STATUT_REC)).findFirst().get();
	}

	/**
	 * @return la liste de types de statut de pièce
	 */
	public List<TypeStatutPiece> getListeTypeStatutPieceActif() {
		return cacheController.getListeTypeStatutPiece()
			.stream()
			.filter(e -> !e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE) && !e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE))
			.collect(Collectors.toList());
	}

	/**
	 * @return le type de statut piece transmis
	 */
	public TypeStatutPiece getTypeStatutPieceTransmis() {
		return cacheController.getListeTypeStatutPiece().stream().filter(e -> e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_TRANSMIS)).findFirst().get();
	}

	/**
	 * @return le type de statut piece validé
	 */
	public TypeStatutPiece getTypeStatutPieceValide() {
		return cacheController.getListeTypeStatutPiece().stream().filter(e -> e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_VALIDE)).findFirst().get();
	}

	/**
	 * @return le type de statut piece validé
	 */
	public TypeStatutPiece getTypeStatutPieceRefuse() {
		return cacheController.getListeTypeStatutPiece().stream().filter(e -> e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_REFUSE)).findFirst().get();
	}

	/**
	 * @return le type de statut piece validé
	 */
	public TypeStatutPiece getTypeStatutPieceAttente() {
		return cacheController.getListeTypeStatutPiece().stream().filter(e -> e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_ATTENTE)).findFirst().get();
	}

	/**
	 * @return le type de statut piece validé
	 */
	public TypeStatutPiece getTypeStatutPieceNonConcerne() {
		return cacheController.getListeTypeStatutPiece().stream().filter(e -> e.getCodTypStatutPiece().equals(NomenclatureUtils.TYP_STATUT_PIECE_NON_CONCERNE)).findFirst().get();
	}

	/**
	 * @return type de traitement en attente
	 */
	public TypeTraitement getTypeTraitementEnAttente() {
		return cacheController.getListeTypeTraitement().stream().filter(e -> e.getCodTypTrait().equals(NomenclatureUtils.TYP_TRAIT_AT)).findFirst().get();
	}

	/**
	 * @return type de traitement en acces direct
	 */
	public TypeTraitement getTypeTraitementAccesDirect() {
		return cacheController.getListeTypeTraitement().stream().filter(e -> e.getCodTypTrait().equals(NomenclatureUtils.TYP_TRAIT_AD)).findFirst().get();
	}

	/**
	 * @return type de traitement en acces controle
	 */
	public TypeTraitement getTypeTraitementAccesControle() {
		return cacheController.getListeTypeTraitement().stream().filter(e -> e.getCodTypTrait().equals(NomenclatureUtils.TYP_TRAIT_AC)).findFirst().get();
	}

	/**
	 * @param  codPostal
	 * @return           la Liste les communes par leur code postal
	 */
	public List<SiScolCommune> listeCommuneByCodePostal(final String codPostal) {
		final List<SiScolCommune> listeCommune = siScolCommuneRepository.getCommuneByCodePostal(siScolService.getTypSiscol(), codPostal);
		listeCommune.sort((c1, c2) -> c1.getLibCom().compareTo(c2.getLibCom()));
		return listeCommune;
	}

	/**
	 * @param  siScolDepartement
	 * @return                   Liste les commune par le code commune et là ou il y a des etablissments
	 */
	public List<SiScolCommune> listeCommuneByDepartement(final SiScolDepartement siScolDepartement) {
		final List<SiScolCommune> listeCommune = siScolCommuneRepository.getCommuneByDepartement(siScolService.getTypSiscol(), siScolDepartement.getId().getCodDep());
		listeCommune.sort((c1, c2) -> c1.getLibCom().compareTo(c2.getLibCom()));
		return listeCommune;
	}

	/**
	 * @param  commune
	 * @return         Liste les etablissment par code commune
	 */
	public List<SiScolEtablissement> listeEtablissementByCommuneEnService(final SiScolCommune commune) {
		return siScolEtablissementRepository.getEtablissementByCommuneEnService(siScolService.getTypSiscol(), commune.getId().getCodCom(), true);
	}

	/**
	 * @param  code
	 * @return      le pays equivalent au code
	 */
	public SiScolPays getPaysByCode(final String code) {
		if (code == null) {
			return null;
		}
		final List<SiScolPays> liste = cacheController.getListePays();
		if (liste == null || liste.size() == 0) {
			return null;
		}
		final Optional<SiScolPays> fr = liste.stream().filter(e -> e.getId().getCodPay().equals(code)).findFirst();
		if (fr.isPresent()) {
			return fr.get();
		} else {
			return null;
		}
	}

	/**
	 * Renvoie le departement par son code
	 * @param  cod
	 * @return     le departement par son code
	 */
	public SiScolDepartement getDepartementByCode(final String cod) {
		if (cod == null) {
			return null;
		}
		final List<SiScolDepartement> liste = cacheController.getListDepartement();
		final Optional<SiScolDepartement> dep = liste.stream().filter(e -> e.getId().getCodDep().equals(cod)).findFirst();
		if (dep.isPresent()) {
			return dep.get();
		} else {
			return null;
		}
	}

	/**
	 * @param  codApo
	 * @return        la civilite par son code siscol
	 */
	public Civilite getCiviliteByCodeSiScol(final String codSiScol) {
		if (codSiScol == null) {
			return null;
		}
		final List<Civilite> liste = cacheController.getListeCivilte();
		final Optional<Civilite> civ = liste.stream().filter(e -> e.getCodSiScol().contains(codSiScol)).findFirst();
		if (civ.isPresent()) {
			return civ.get();
		} else {
			return null;
		}
	}

	/**
	 * CHerche la commune par son code postal et son code commune
	 * @param  codBdi
	 * @param  codCom
	 * @return        la commune
	 */
	public SiScolCommune getCommuneByCodePostalAndCodeCom(final String codBdi,
		final String codCom) {
		if (codBdi == null || codCom == null) {
			return null;
		}
		final List<SiScolCommune> listeCommuneByCodePostal = listeCommuneByCodePostal(codBdi);
		if (listeCommuneByCodePostal != null && listeCommuneByCodePostal.size() > 0) {
			final Optional<SiScolCommune> com = listeCommuneByCodePostal.stream().filter(e -> e.getId().getCodCom().equals(codCom)).findFirst();
			if (com.isPresent()) {
				return com.get();
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * Cherche l'etablissement par son code
	 * @param  codEtb
	 * @return        l'etablissement
	 */
	public SiScolEtablissement getEtablissementByCode(final String codEtb) {
		if (codEtb == null) {
			return null;
		}
		return siScolEtablissementRepository.findOne(new SiScolEtablissementPK(siScolService.getTypSiscol(), codEtb));
	}

	/**
	 * Cherche le bac Ou Equ par son code
	 * @param  codBac
	 * @return        le bac ou equ
	 */
	public SiScolBacOuxEqu getBacOuEquByCode(final String codBac) {
		if (codBac == null) {
			return null;
		}
		final List<SiScolBacOuxEqu> liste = cacheController.getListeBacOuxEqu();
		final Optional<SiScolBacOuxEqu> bac = liste.stream().filter(e -> e.getId().getCodBac().equals(codBac)).findFirst();
		if (bac.isPresent()) {
			return bac.get();
		} else {
			return null;
		}
	}

	/**
	 * Cherche la mention niveau bac par son code
	 * @param  codMnb
	 * @return        la mention niveau bac
	 */
	public SiScolMentionNivBac getMentionNivBacByCode(final String codMnb) {
		if (codMnb == null) {
			return null;
		}
		final List<SiScolMentionNivBac> liste = cacheController.getListeMentionNivBac();
		final Optional<SiScolMentionNivBac> mention = liste.stream().filter(e -> e.getId().getCodMnb().equals(codMnb)).findFirst();
		if (mention.isPresent()) {
			return mention.get();
		} else {
			return null;
		}
	}

	/**
	 * Cherche la mention par son code
	 * @param  codMen
	 * @return        la mention
	 */
	public SiScolMention getMentionByCode(final String codMen) {
		if (codMen == null) {
			return null;
		}
		final List<SiScolMention> liste = cacheController.getListeMention();
		final Optional<SiScolMention> mention = liste.stream().filter(e -> e.getId().getCodMen().equals(codMen)).findFirst();
		if (mention.isPresent()) {
			return mention.get();
		} else {
			return null;
		}
	}

	/**
	 * Cherche le type de resultat par son code
	 * @param  codTre
	 * @return        le type de resultat
	 */
	public SiScolTypResultat getTypeResultatByCode(final String codTre) {
		if (codTre == null) {
			return null;
		}
		final List<SiScolTypResultat> liste = cacheController.getListeTypeResultat();
		final Optional<SiScolTypResultat> result = liste.stream().filter(e -> e.getId().getCodTre().equals(codTre)).findFirst();
		if (result.isPresent()) {
			return result.get();
		} else {
			return null;
		}
	}

	/**
	 * @param  codCGE
	 * @return        le SiScolCentreGestion lie au code CGE
	 */
	public SiScolCentreGestion getSiScolCentreGestionByCode(final String codCGE) {
		if (codCGE == null) {
			return null;
		}
		final List<SiScolCentreGestion> liste = cacheController.getListeCentreGestion();
		final Optional<SiScolCentreGestion> result = liste.stream().filter(e -> e.getId().getCodCge().equals(codCGE)).findFirst();
		if (result.isPresent()) {
			return result.get();
		} else {
			return null;
		}
	}

	/**
	 * @param  codTpd
	 * @return        le SiScolTypDiplome lie au code tpd
	 */
	public SiScolTypDiplome getSiScolTypDiplomeByCode(final String codTpd) {
		if (codTpd == null) {
			return null;
		}
		final List<SiScolTypDiplome> liste = cacheController.getListeTypDiplome();
		final Optional<SiScolTypDiplome> result = liste.stream().filter(e -> e.getId().getCodTpdEtb().equals(codTpd)).findFirst();
		if (result.isPresent()) {
			return result.get();
		} else {
			return null;
		}
	}

	/**
	 * @return la liste de type "obtenu" du cursus externe
	 */
	public List<SimpleBeanPresentation> getListeObtenuCursus() {
		final List<SimpleBeanPresentation> liste = new ArrayList<>();
		liste.add(new SimpleBeanPresentation(ConstanteUtils.CURSUS_EXTERNE_OBTENU, applicationContext.getMessage("cursusexterne.obtenu.choix.obtenu", null, UI.getCurrent().getLocale())));
		liste.add(new SimpleBeanPresentation(ConstanteUtils.CURSUS_EXTERNE_NON_OBTENU, applicationContext.getMessage("cursusexterne.obtenu.choix.nonobtenu", null, UI.getCurrent().getLocale())));
		liste.add(new SimpleBeanPresentation(ConstanteUtils.CURSUS_EXTERNE_EN_COURS, applicationContext.getMessage("cursusexterne.obtenu.choix.encours", null, UI.getCurrent().getLocale())));
		return liste;
	}

	/**
	 * @param  code
	 * @return      le libelle du "obtenu" du cursus externe
	 */
	public String getLibelleObtenuCursusByCode(final String code) {
		if (code == null) {
			return null;
		} else if (code.equals(ConstanteUtils.CURSUS_EXTERNE_OBTENU)) {
			return applicationContext.getMessage("cursusexterne.obtenu.choix.obtenu.lib", null, UI.getCurrent().getLocale());
		} else if (code.equals(ConstanteUtils.CURSUS_EXTERNE_NON_OBTENU)) {
			return applicationContext.getMessage("cursusexterne.obtenu.choix.nonobtenu.lib", null, UI.getCurrent().getLocale());
		} else if (code.equals(ConstanteUtils.CURSUS_EXTERNE_EN_COURS)) {
			return applicationContext.getMessage("cursusexterne.obtenu.choix.encours.lib", null, UI.getCurrent().getLocale());
		}
		return null;
	}

	/**
	 * @return le bac 'sans bac'
	 */
	public SiScolBacOuxEqu getBacNoBac() {
		final String codeSansBac = parametreController.getSiscolCodeSansBac();
		if (codeSansBac != null && !codeSansBac.equals("")) {
			final Optional<SiScolBacOuxEqu> bacOpt = cacheController.getListeBacOuxEqu().stream().filter(e -> e.getId().getCodBac().equals(codeSansBac)).findFirst();
			if (bacOpt.isPresent()) {
				return bacOpt.get();
			}
		}
		return null;
	}
}
