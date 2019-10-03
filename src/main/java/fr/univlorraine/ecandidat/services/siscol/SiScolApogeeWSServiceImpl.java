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
package fr.univlorraine.ecandidat.services.siscol;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.mail.util.ByteArrayDataSource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.axis.AxisFault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import fr.univlorraine.ecandidat.controllers.CacheController;
import fr.univlorraine.ecandidat.controllers.CandidatureController;
import fr.univlorraine.ecandidat.controllers.MailController;
import fr.univlorraine.ecandidat.controllers.OpiController;
import fr.univlorraine.ecandidat.controllers.ParametreController;
import fr.univlorraine.ecandidat.entities.ecandidat.Adresse;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Fichier;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;
import fr.univlorraine.ecandidat.entities.ecandidat.PjOpi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCatExoExt;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolComBdi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolUtilisateur;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;
import fr.univlorraine.ecandidat.entities.siscol.AnneeUni;
import fr.univlorraine.ecandidat.entities.siscol.BacOuxEqu;
import fr.univlorraine.ecandidat.entities.siscol.CatExoExt;
import fr.univlorraine.ecandidat.entities.siscol.CentreGestion;
import fr.univlorraine.ecandidat.entities.siscol.ComBdi;
import fr.univlorraine.ecandidat.entities.siscol.Commune;
import fr.univlorraine.ecandidat.entities.siscol.Departement;
import fr.univlorraine.ecandidat.entities.siscol.DipAutCur;
import fr.univlorraine.ecandidat.entities.siscol.Diplome;
import fr.univlorraine.ecandidat.entities.siscol.Etablissement;
import fr.univlorraine.ecandidat.entities.siscol.IndOpi;
import fr.univlorraine.ecandidat.entities.siscol.Mention;
import fr.univlorraine.ecandidat.entities.siscol.MentionNivBac;
import fr.univlorraine.ecandidat.entities.siscol.Pays;
import fr.univlorraine.ecandidat.entities.siscol.TypDiplome;
import fr.univlorraine.ecandidat.entities.siscol.TypResultat;
import fr.univlorraine.ecandidat.entities.siscol.Utilisateur;
import fr.univlorraine.ecandidat.entities.siscol.VersionApo;
import fr.univlorraine.ecandidat.entities.siscol.Vet;
import fr.univlorraine.ecandidat.entities.siscol.VoeuxIns;
import fr.univlorraine.ecandidat.entities.siscol.WSAdresse;
import fr.univlorraine.ecandidat.entities.siscol.WSBac;
import fr.univlorraine.ecandidat.entities.siscol.WSCursusInterne;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.entities.siscol.WSPjInfo;
import fr.univlorraine.ecandidat.services.siscol.SiScolRestUtils.SiScolRestException;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;
import gouv.education.apogee.commun.client.utils.WSUtils;
import gouv.education.apogee.commun.client.ws.etudiantmetier.EtudiantMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.opimetier.OpiMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.opipj.PjOpiMetierServiceInterface;
import gouv.education.apogee.commun.client.ws.pedagogiquemetier.PedagogiqueMetierServiceInterface;
import gouv.education.apogee.commun.transverse.dto.etudiant.AdresseDTO2;
import gouv.education.apogee.commun.transverse.dto.etudiant.CoordonneesDTO2;
import gouv.education.apogee.commun.transverse.dto.etudiant.IdentifiantsEtudiantDTO;
import gouv.education.apogee.commun.transverse.dto.etudiant.IndBacDTO;
import gouv.education.apogee.commun.transverse.dto.etudiant.InfoAdmEtuDTO2;
import gouv.education.apogee.commun.transverse.dto.opi.DonneesOpiDTO9;
import gouv.education.apogee.commun.transverse.dto.opi.MAJDonneesNaissanceDTO2;
import gouv.education.apogee.commun.transverse.dto.opi.MAJDonneesPersonnellesDTO3;
import gouv.education.apogee.commun.transverse.dto.opi.MAJEtatCivilDTO2;
import gouv.education.apogee.commun.transverse.dto.opi.MAJOpiAdresseDTO;
import gouv.education.apogee.commun.transverse.dto.opi.MAJOpiBacDTO;
import gouv.education.apogee.commun.transverse.dto.opi.MAJOpiIndDTO6;
import gouv.education.apogee.commun.transverse.dto.opi.MAJOpiVoeuDTO3;
import gouv.education.apogee.commun.transverse.dto.pedagogique.ContratPedagogiqueResultatVdiVetDTO2;
import gouv.education.apogee.commun.transverse.dto.pedagogique.EtapeResVdiVetDTO2;
import gouv.education.apogee.commun.transverse.dto.pedagogique.ResultatVetDTO;
import gouv.education.apogee.commun.transverse.exception.WebBaseException;

/**
 * Gestion du SI Scol Apogee
 * @author Kevin Hergalant
 */
@Component(value = "siScolApogeeWSServiceImpl")
@SuppressWarnings({ "unchecked", "serial" })
public class SiScolApogeeWSServiceImpl implements SiScolGenericService, Serializable {

	private final Logger logger = LoggerFactory.getLogger(SiScolApogeeWSServiceImpl.class);

	/** proxy pour faire appel aux infos sur les résultats du WS . */
	private PedagogiqueMetierServiceInterface monProxyPedagogique;

	/** proxy pour faire appel aux infos concernant un étudiant. */
	private EtudiantMetierServiceInterface monProxyEtu;

	/** proxy pour faire appel aux infos géographique du WS . */
	private OpiMetierServiceInterface monProxyOpi;

	/** proxy pour faire appel aux infos PjOPI du WS . */
	private PjOpiMetierServiceInterface monProxyPjOpi;

	/** service pour faire appel aux services Rest generiques */
	@Resource
	private transient SiScolRestServiceInterface siScolRestServiceInterface;

	@Resource
	private transient ParametreController parametreController;

	@Resource
	private transient CacheController cacheController;

	@Resource
	private transient OpiController opiController;

	@Resource
	private transient CandidatureController candidatureController;

	@Resource
	private transient MailController mailController;

	@Resource
	private transient DateTimeFormatter formatterDateTimeApo;

	@Resource
	private transient String urlWsPjApogee;

	@Resource
	private transient String urlWsCheckInes;

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#isImplementationApogee() */
	@Override
	public Boolean isImplementationApogee() {
		return true;
	}

	/**
	 * Execute la requete et ramene l'ensemble des elements d'une table
	 * @param  className
	 *                             la class concernée
	 * @return                 la liste d'objet
	 * @throws SiScolException
	 */
	private <T> List<T> executeQueryListEntity(final Class<T> className) throws SiScolException {
		try {
			final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
			final EntityManager em = emf.createEntityManager();
			final Query query = em.createQuery("Select a from " + className.getName() + " a", className);
			final List<T> listeSiScol = query.getResultList();
			em.close();
			return listeSiScol;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on execute query list entity", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolBacOuxEqu() */
	@Override
	public List<SiScolBacOuxEqu> getListSiScolBacOuxEqu() throws SiScolException {

		try {
			final List<SiScolBacOuxEqu> liste = new ArrayList<>();
			executeQueryListEntity(BacOuxEqu.class).forEach(bac -> {
				liste.add(new SiScolBacOuxEqu(bac.getCodBac(),
					bac.getLibBac(),
					bac.getLicBac(),
					MethodUtils.getBooleanFromTemoin(bac.getTemEnSveBac()),
					MethodUtils.getBooleanFromTemoin(bac.getTemNatBac()),
					bac.getDaaDebVldBac(),
					bac.getDaaFinVldBac(),
					MethodUtils.getBooleanFromTemoin(bac.getTemCtrlIne()),
					bac.getAnnCtrlIne()));
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolBacOuxEqu", e.getCause());
		}

	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCentreGestion() */
	@Override
	public List<SiScolCentreGestion> getListSiScolCentreGestion() throws SiScolException {
		final List<SiScolCentreGestion> liste = new ArrayList<>();
		try {
			executeQueryListEntity(CentreGestion.class).forEach(centreGestion -> {
				liste.add(new SiScolCentreGestion(centreGestion.getCodCge(),
					centreGestion.getLibCge(),
					centreGestion.getLicCge(),
					MethodUtils.getBooleanFromTemoin(centreGestion.getTemEnSveCge())));
			});
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolCentreGestion", e.getCause());
		}
		return liste;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCommune() */
	@Override
	public List<SiScolCommune> getListSiScolCommune() throws SiScolException {
		final List<SiScolCommune> liste = new ArrayList<>();
		try {
			executeQueryListEntity(Commune.class).forEach(commune -> {

				final SiScolCommune siScolCommune =
					new SiScolCommune(commune.getCodCom(), commune.getLibCom(), MethodUtils.getBooleanFromTemoin(commune.getTemEnSveCom()));
				if (commune.getDepartement() != null) {
					siScolCommune.setSiScolDepartement(new SiScolDepartement(commune.getDepartement().getCodDep()));
				}
				liste.add(siScolCommune);

			});
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolCommune", e.getCause());
		}
		return liste;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDepartement() */
	@Override
	public List<SiScolDepartement> getListSiScolDepartement() throws SiScolException {
		final List<SiScolDepartement> liste = new ArrayList<>();
		try {
			executeQueryListEntity(Departement.class).forEach(departement -> {
				liste.add(new SiScolDepartement(departement.getCodDep(),
					departement.getLibDep(),
					departement.getLicDep(),
					MethodUtils.getBooleanFromTemoin(departement.getTemEnSveDep())));
			});
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolDepartement", e.getCause());
		}
		return liste;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDipAutCur() */
	@Override
	public List<SiScolDipAutCur> getListSiScolDipAutCur() throws SiScolException {
		try {
			final List<SiScolDipAutCur> liste = new ArrayList<>();
			executeQueryListEntity(DipAutCur.class).forEach(dipAutCur -> {
				liste.add(new SiScolDipAutCur(dipAutCur.getCodDac(),
					dipAutCur.getLibDac(),
					dipAutCur.getLicDac(),
					MethodUtils.getBooleanFromTemoin(dipAutCur.getTemEnSveDac())));
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database erroron getListSiScolDipAutCur", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolEtablissement() */
	@Override
	public List<SiScolEtablissement> getListSiScolEtablissement() throws SiScolException {
		try {
			final List<SiScolEtablissement> liste = new ArrayList<>();
			executeQueryListEntity(Etablissement.class).forEach(etablissement -> {
				final SiScolEtablissement siScolEtablissement = new SiScolEtablissement(etablissement.getCodEtb(),
					etablissement.getCodTpe(),
					etablissement.getLibEtb(),
					etablissement.getLibWebEtb(),
					etablissement.getLicEtb(),
					MethodUtils.getBooleanFromTemoin(etablissement.getTemEnSveEtb()));
				if (etablissement.getDepartement() != null) {
					siScolEtablissement.setSiScolDepartement(new SiScolDepartement(etablissement.getDepartement().getCodDep()));
				}
				if (etablissement.getCommune() != null) {
					siScolEtablissement.setSiScolCommune(new SiScolCommune(etablissement.getCommune().getCodCom()));
				}
				liste.add(siScolEtablissement);
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolEtablissement", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolMention() */
	@Override
	public List<SiScolMention> getListSiScolMention() throws SiScolException {
		try {
			final List<SiScolMention> liste = new ArrayList<>();
			executeQueryListEntity(Mention.class).forEach(mention -> {
				liste.add(new SiScolMention(mention.getCodMen(),
					mention.getLibMen(),
					mention.getLicMen(),
					MethodUtils.getBooleanFromTemoin(mention.getTemEnSveMen())));
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolMention", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolTypResultat() */
	@Override
	public List<SiScolTypResultat> getListSiScolTypResultat() throws SiScolException {
		try {
			final List<SiScolTypResultat> liste = new ArrayList<>();
			executeQueryListEntity(TypResultat.class).forEach(typResultat -> {
				liste.add(new SiScolTypResultat(typResultat.getCodTre(),
					typResultat.getLibTre(),
					typResultat.getLicTre(),
					MethodUtils.getBooleanFromTemoin(typResultat.getTemEnSveTre())));
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolTypResultat", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolMentionNivBac() */
	@Override
	public List<SiScolMentionNivBac> getListSiScolMentionNivBac() throws SiScolException {
		try {
			final List<SiScolMentionNivBac> liste = new ArrayList<>();
			executeQueryListEntity(MentionNivBac.class).forEach(mentionNivBac -> {
				liste.add(new SiScolMentionNivBac(mentionNivBac.getCodMnb(),
					mentionNivBac.getLibMnb(),
					mentionNivBac.getLicMnb(),
					MethodUtils.getBooleanFromTemoin(mentionNivBac.getTemEnSveMnb())));
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolMentionNivBac", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolPays() */
	@Override
	public List<SiScolPays> getListSiScolPays() throws SiScolException {
		try {
			final List<SiScolPays> liste = new ArrayList<>();
			executeQueryListEntity(Pays.class).forEach(pays -> {
				liste.add(new SiScolPays(pays
					.getCodPay(), pays.getLibNat(), pays.getLibPay(), pays.getLicPay(), MethodUtils.getBooleanFromTemoin(pays.getTemEnSvePay())));
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolPays", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolTypDiplome() */
	@Override
	public List<SiScolTypDiplome> getListSiScolTypDiplome() throws SiScolException {
		try {
			final List<SiScolTypDiplome> liste = new ArrayList<>();
			executeQueryListEntity(TypDiplome.class).forEach(typDiplome -> {
				liste.add(new SiScolTypDiplome(typDiplome.getCodTpdEtb(),
					typDiplome.getLibTpd(),
					typDiplome.getLicTpd(),
					MethodUtils.getBooleanFromTemoin(typDiplome.getTemEnSveTpd())));
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolTypDiplome", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolUtilisateur() */
	@Override
	public List<SiScolUtilisateur> getListSiScolUtilisateur() throws SiScolException {
		try {
			final List<SiScolUtilisateur> liste = new ArrayList<>();
			executeQueryListEntity(Utilisateur.class).forEach(utilisateur -> {
				final SiScolUtilisateur siScolUtilisateur = new SiScolUtilisateur(utilisateur.getCodUti(),
					utilisateur.getAdrMailUti(),
					utilisateur.getLibCmtUti(),
					MethodUtils.getBooleanFromTemoin(utilisateur.getTemEnSveUti()));
				if (utilisateur.getCentreGestion() != null) {
					siScolUtilisateur.setSiScolCentreGestion(new SiScolCentreGestion(utilisateur.getCentreGestion().getCodCge()));
				}
				liste.add(siScolUtilisateur);
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolUtilisateur", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolComBdi() */
	@Override
	public List<SiScolComBdi> getListSiScolComBdi() throws SiScolException {
		try {
			final List<SiScolComBdi> liste = new ArrayList<>();
			executeQueryListEntity(ComBdi.class).forEach(comBdi -> {
				liste.add(new SiScolComBdi(comBdi.getId().getCodCom(), comBdi.getId().getCodBdi()));
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolComBdi", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolAnneeUni() */
	@Override
	public List<SiScolAnneeUni> getListSiScolAnneeUni() throws SiScolException {
		try {
			final List<SiScolAnneeUni> liste = new ArrayList<>();
			executeQueryListEntity(AnneeUni.class).forEach(anneeUni -> {
				liste.add(new SiScolAnneeUni(anneeUni.getCodAnu(), anneeUni.getEtaAnuIae(), anneeUni.getLibAnu(), anneeUni.getLicAnu()));
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListSiScolAnneeUni", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListCatExoExt() */
	@Override
	public List<SiScolCatExoExt> getListCatExoExt() throws SiScolException {
		try {
			final List<SiScolCatExoExt> liste = new ArrayList<>();
			executeQueryListEntity(CatExoExt.class).forEach(catExoExt -> {
				liste.add(new SiScolCatExoExt(catExoExt.getCodCatExoExt(), catExoExt.getLicCatExoExt(), catExoExt.getLibCatExoExt(), catExoExt.getCodSisCatExoExt(), MethodUtils.getBooleanFromTemoin(catExoExt.getTemEnSveCatExoExt())));
			});
			return liste;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getListCatExoExt", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getVersion() */
	@Override
	public Version getVersion() throws SiScolException {
		try {
			final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
			final EntityManager em = emf.createEntityManager();
			final Query query = em.createQuery("Select a from VersionApo a where a.datCre is not null order by a.datCre desc", VersionApo.class).setMaxResults(1);
			final List<VersionApo> listeVersionApo = query.getResultList();
			em.close();
			if (listeVersionApo != null && listeVersionApo.size() > 0) {
				final VersionApo versionApo = listeVersionApo.get(0);
				return new Version(versionApo.getId().getCodVer());
			} else {
				return null;
			}
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getVersion", e.getCause());
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListFormation(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public List<Vet> getListFormation(final String codeCge, String search) throws SiScolException {
		try {
			final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
			final EntityManager em = emf.createEntityManager();
			if (search != null && search.length() > 0) {
				search = "%" + search.toLowerCase() + "%";
			}
			String sqlString = "select * from (select distinct " + "version_etape.cod_etp as codEtpVet, "
				+ "version_etape.cod_vrs_vet as codVrsVet, "
				+ "version_etape.lib_web_vet as libVet, "
				+ "etp_gerer_cge.cod_cge as codCge, "
				+ "diplome.cod_tpd_etb as codTpd, "
				+ "typ_diplome.lib_tpd as libTypDip "
				+ "from version_etape, diplome, vdi_fractionner_vet, etp_gerer_cge, typ_diplome "
				+ "where "
				+ "vdi_fractionner_vet.cod_dip = diplome.cod_dip "
				+ "and diplome.cod_tpd_etb = typ_diplome.cod_tpd_etb "
				+ "and vdi_fractionner_vet.cod_etp = version_etape.cod_etp "
				+ "and vdi_fractionner_vet.cod_vrs_vet = version_etape.cod_vrs_vet "
				+ "and vdi_fractionner_vet.daa_deb_rct_vet<=(select max(cod_anu) from annee_uni where eta_anu_iae in ('O','I')) "
				+ "and vdi_fractionner_vet.daa_fin_rct_vet>=(select min(cod_anu) from annee_uni where eta_anu_iae in ('O','I')) "
				+ "and etp_gerer_cge.cod_etp = version_etape.cod_etp "
				+ "and "
				+ "(LOWER(version_etape.lib_web_vet) like ?1 "
				+ "or "
				+ "LOWER(version_etape.cod_etp||'-'||version_etape.cod_vrs_vet) like ?1)";

			if (codeCge != null) {
				sqlString += " and etp_gerer_cge.cod_cge =?2";
			}
			sqlString += ") where rownum <= " + ConstanteUtils.NB_MAX_RECH_FORM;
			logger.debug("Requete de recherche de formation : " + sqlString);
			final Query query = em.createNativeQuery(sqlString, Vet.class);
			query.setParameter(1, search);
			if (codeCge != null) {
				query.setParameter(2, codeCge);
			}

			return query.getResultList();
		} catch (final Exception e) {
			logger.error("erreur", e);
			throw new SiScolException("SiScol database error on getListFormationApogee", e.getCause());
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListDiplome(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Diplome> getListDiplome(final String codEtpVet, final String codVrsVet) throws SiScolException {
		try {
			final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
			final EntityManager em = emf.createEntityManager();

			final String sqlString =
				"select distinct vdi_fractionner_vet.cod_dip, vdi_fractionner_vet.cod_vrs_vdi, diplome.lib_dip from vdi_fractionner_vet, diplome\r\n"
					+ "where vdi_fractionner_vet.cod_dip = diplome.cod_dip\r\n"
					+ "and cod_etp = ?1 and cod_vrs_vet = ?2";

			logger.debug("Requete de recherche de diplome : " + sqlString);
			final Query query = em.createNativeQuery(sqlString, Diplome.class);
			query.setParameter(1, codEtpVet);
			query.setParameter(2, codVrsVet);

			return query.getResultList();
		} catch (final Exception e) {
			logger.error("erreur", e);
			throw new SiScolException("SiScol database error on getListDiplome", e.getCause());
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getIndividu(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public WSIndividu getIndividu(final String codEtu, String ine, String cleIne) throws SiScolException {
		if (monProxyEtu == null) {
			monProxyEtu = (EtudiantMetierServiceInterface) WSUtils.getService(WSUtils.ETUDIANT_SERVICE_NAME);
		}
		try {
			/* Mise en majuscule de l'ine et de la cle */
			if (ine != null) {
				ine = ine.toUpperCase();
			}
			if (cleIne != null) {
				cleIne = cleIne.toUpperCase();
			}
			final IdentifiantsEtudiantDTO etudiant = monProxyEtu.recupererIdentifiantsEtudiant(codEtu, null, ine, cleIne, null, null, null, null, null, "N");
			if (etudiant != null && etudiant.getCodEtu() != null) {
				final InfoAdmEtuDTO2 data = monProxyEtu.recupererInfosAdmEtu_v2(etudiant.getCodEtu().toString());
				if (data != null) {
					String civilite = "";
					if (data.getSexe() != null) {
						if (data.getSexe().equals("F")) {
							civilite = "2";
						} else {
							civilite = "1";
						}
					}
					/* civilite */
					final WSIndividu individu = new WSIndividu(etudiant.getCodInd(),
						civilite,
						new BigDecimal(etudiant.getCodEtu()),
						etudiant.getNumeroINE(),
						etudiant.getCleINE(),
						data.getDateNaissance().getTime(),
						data.getNomPatronymique(),
						data.getNomUsuel(),
						data.getPrenom1(),
						data.getPrenom2(),
						data.getLibVilleNaissance());

					if (data.getDepartementNaissance() != null) {
						individu.setCodDepNai(data.getDepartementNaissance().getCodeDept());
					}
					if (data.getPaysNaissance() != null) {
						individu.setCodPayNai(data.getPaysNaissance().getCodPay());
					} else {
						individu.setCodPayNai(ConstanteUtils.PAYS_CODE_FRANCE);
					}
					if (data.getNationaliteDTO() != null) {
						individu.setCodPayNat(data.getNationaliteDTO().getCodeNationalite());
					} else {
						individu.setCodPayNat(ConstanteUtils.PAYS_CODE_FRANCE);
					}

					/* Recuperation du bac */
					if (data.getListeBacs() != null) {
						final List<IndBacDTO> liste = Arrays.asList(data.getListeBacs());
						final Optional<IndBacDTO> optBac = liste.stream()
							.filter(e1 -> e1.getAnneeObtentionBac() != null && e1.getCodBac() != null)
							.sorted((e1, e2) -> e2.getAnneeObtentionBac().compareTo(e1.getAnneeObtentionBac()))
							.findFirst();
						if (optBac.isPresent()) {
							final IndBacDTO bacDTO = optBac.get();

							final WSBac bac = new WSBac();
							bac.setCodBac(bacDTO.getCodBac());
							bac.setDaaObtBacIba(bacDTO.getAnneeObtentionBac());
							if (bacDTO.getDepartementBac() != null) {
								bac.setCodDep(bacDTO.getDepartementBac().getCodeDept());
							}
							if (bacDTO.getEtbBac() != null) {
								bac.setCodEtb(bacDTO.getEtbBac().getCodeEtb());
							}
							if (bacDTO.getMentionBac() != null) {
								bac.setCodMnb(bacDTO.getMentionBac().getCodMention());
							}
							individu.setBac(bac);
						}
					}

					/* Recuperation de l'adresse */
					individu.setAdresse(getAdresse(etudiant.getCodEtu().toString()));

					/* Recuperation du cursus */
					individu.setListCursusInterne(getCursusInterne(etudiant.getCodEtu().toString()));

					return individu;
				}
			}
			return null;
		} catch (final AxisFault ex) {
			if (ex.getMessage().equals("technical.data.nullretrieve.etudiant")) {
				return null;
			} else if (ex.getMessage().equals("technical.parameter.noncoherentinput.codEtu")) {
				return null;
			} else {
				logger.error("Probleme avec le WS lors de la recherche complete de l'etudiant (individu, bac, adresse, cursus) dont codetu est : " + codEtu
					+ " et codIne est : "
					+ ine, ex);
				throw new SiScolException(
					"Probleme avec le WS lors de la recherche complete de l'etudiant (individu, bac, adresse, cursus) dont codetu est : " + codEtu
						+ " et codIne est : "
						+ ine,
					ex);
			}
		} catch (final Exception ex) {
			logger.error("Probleme avec le WS lors de la recherche complete de l'etudiant (individu, bac, adresse, cursus) dont codetu est : " + codEtu
				+ " et codIne est : "
				+ ine, ex);
			throw new SiScolException(
				"Probleme avec le WS lors de la recherche complete de l'etudiant (individu, bac, adresse, cursus) dont codetu est : " + codEtu
					+ " et codIne est : "
					+ ine,
				ex);
		}
	}

	/**
	 * Recupere l'adresse de l'individu par WS
	 * @param  codEtu
	 * @return                 l'adresse du WS
	 * @throws SiScolException
	 */
	public WSAdresse getAdresse(final String codEtu) throws SiScolException {
		if (monProxyEtu == null) {
			monProxyEtu = (EtudiantMetierServiceInterface) WSUtils.getService(WSUtils.ETUDIANT_SERVICE_NAME);
		}

		try {
			final CoordonneesDTO2 cdto = monProxyEtu.recupererAdressesEtudiant_v2(codEtu, null, "N");

			if (cdto == null) {
				return null;
			} else {
				WSAdresse adresse = null;
				final AdresseDTO2 ada = cdto.getAdresseAnnuelle();
				adresse = transformAdresseWS(ada, cdto.getNumTelPortable());
				if (adresse != null) {
					return adresse;
				}
				final AdresseDTO2 adf = cdto.getAdresseFixe();
				return transformAdresseWS(adf, cdto.getNumTelPortable());
			}
		} catch (final AxisFault ex) {
			if (ex.getMessage().equals("technical.data.nullretrieve.findIAA")) {
				return null;
			}
			logger.error("erreur", ex);
			throw new SiScolException("Probleme lors de la recherche de l'adresse pour etudiant dont codetu est : " + codEtu, ex);
		} catch (final WebBaseException ex) {
			// Si on est dans un cas d'erreur non expliqué
			if (ex.getNature().equals("technical.ws.remoteerror.global")) {
				logger.error("erreur", ex);
				throw new SiScolException("Probleme avec le WS lors de la recherche de l'adresse pour etudiant dont codetu est : " + codEtu, ex);
			} else {
				return null;
			}
		} catch (final Exception ex) {
			logger.error("erreur", ex);
			throw new SiScolException("Probleme lors de la recherche de l'adresse pour etudiant dont codetu est : " + codEtu, ex);
		}
	}

	/**
	 * transforme une adresse provenant du WS en adresse provenant d'apogee par
	 * requete
	 * @param  adrWs
	 * @param  numPortable
	 * @return             l'adresse formatée
	 */
	private WSAdresse transformAdresseWS(final AdresseDTO2 adrWs, final String numPortable) {
		if (adrWs == null) {
			return null;
		}
		final WSAdresse adresse = new WSAdresse();
		adresse.setCodAdr(null);
		adresse.setLibAd1(adrWs.getLibAd1());
		adresse.setLibAd2(adrWs.getLibAd2());
		adresse.setLibAd3(adrWs.getLibAd3());
		adresse.setNumTel(adrWs.getNumTel());
		adresse.setNumTelPort(numPortable);
		adresse.setLibAde(adrWs.getLibAde());
		if (adrWs.getCommune() != null) {
			adresse.setCodCom(adrWs.getCommune().getCodeInsee());
			adresse.setCodBdi(adrWs.getCommune().getCodePostal());
		}
		if (adrWs.getPays() != null) {
			adresse.setCodPay(adrWs.getPays().getCodPay());
		}
		return adresse;
	}

	/**
	 * Recupere le cursus interne d'un individu par WS
	 * @param  codEtu
	 * @return                 le cursus du WS
	 * @throws SiScolException
	 */
	public List<WSCursusInterne> getCursusInterne(final String codEtu) throws SiScolException {
		try {
			if (monProxyPedagogique == null) {
				monProxyPedagogique = (PedagogiqueMetierServiceInterface) WSUtils.getService(WSUtils.PEDAGOGIQUE_SERVICE_NAME);
			}
			final List<WSCursusInterne> liste = new ArrayList<>();
			final ContratPedagogiqueResultatVdiVetDTO2[] resultatVdiVet =
				monProxyPedagogique.recupererContratPedagogiqueResultatVdiVet_v2(codEtu, "toutes", "Apogee", "T", "toutes", "tous", "E");
			/* Utiliser AET a la place de ET?? */
			if (resultatVdiVet != null && resultatVdiVet.length > 0) {
				for (final ContratPedagogiqueResultatVdiVetDTO2 rdto : resultatVdiVet) {
					// information sur les etapes:
					final EtapeResVdiVetDTO2[] etapes = rdto.getEtapes();
					if (etapes != null && etapes.length > 0) {

						for (final EtapeResVdiVetDTO2 etape : etapes) {
							// résultats de l'étape:
							final ResultatVetDTO[] tabresetape = etape.getResultatVet();
							if (tabresetape != null && tabresetape.length > 0) {
								for (final ResultatVetDTO ret : tabresetape) {
									final WSCursusInterne cursus = new WSCursusInterne(etape.getEtape().getCodEtp() + "/" + etape.getEtape().getCodVrsVet(),
										etape.getEtape().getLibWebVet() + " - " + ret.getSession().getLibSes(),
										etape.getCodAnu(),
										(ret.getMention() != null) ? ret.getMention().getCodMen() : null,
										(ret.getTypResultat() != null) ? ret.getTypResultat().getCodTre() : null,
										ret.getNotVet(),
										ret.getBarNotVet());
									liste.add(cursus);
								}
							} else {
								final WSCursusInterne cursus = new WSCursusInterne(etape.getEtape().getCodEtp() + "/" + etape.getEtape().getCodVrsVet(),
									etape.getEtape().getLibWebVet(),
									etape.getCodAnu());
								liste.add(cursus);
							}

						}
					}
				}

			}
			return liste;
		} catch (final AxisFault ex) {
			if (ex.getMessage().equals("technical.data.nullretrieve.findIAA")) {
				return null;
			} else if (ex.getMessage().equals("technical.data.nullretrieve.codAnu")) {
				return null;
			}

			logger.error("erreur", ex);
			throw new SiScolException("Probleme lors de la recherche du cursus interne pour etudiant dont codetu est : " + codEtu, ex);
		} catch (final WebBaseException ex) {
			// Si on est dans un cas d'erreur non expliqué
			if (ex.getNature().equals("technical.ws.remoteerror.global")) {
				logger.error("erreur", ex);
				throw new SiScolException("Probleme avec le WS lors de la recherche cursus interne pour etudiant dont codetu est : " + codEtu, ex);
			} else {
				return null;
			}
		} catch (final Exception ex) {
			logger.error("erreur", ex);
			throw new SiScolException("Probleme lors de la recherche cursus interne pour etudiant dont codetu est : " + codEtu, ex);
		}
	}

	/* (non-Javadoc)
	 *
	 * @see
	 * fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#creerOpiViaWS(
	 * fr.univlorraine.ecandidat.entities.ecandidat.Candidat) */
	@Override
	public void creerOpiViaWS(final Candidat candidat, final Boolean isBatch) {
		/* Erreur à afficher dans les logs */
		final String logComp = " - candidat " + candidat.getCompteMinima().getNumDossierOpiCptMin();

		logger.debug("creerOpiViaWS" + logComp);
		// Test que l'année d'obtention du bac est correcte.

		final CandidatBacOuEqu bacOuEqu = candidat.getCandidatBacOuEqu();

		if (bacOuEqu != null && bacOuEqu.getAnneeObtBac() != null) {
			final int anneeObtBac = candidat.getCandidatBacOuEqu().getAnneeObtBac();
			final int anneeEnCours = (LocalDate.now()).getYear();
			if (anneeObtBac > anneeEnCours) {
				mailController.sendErrorToAdminFonctionnel("Erreur OPI, bac non conforme" + logComp,
					"Erreur OPI : bac non conforme, la date est supérieur à l'année courante" + logComp,
					logger);
				logger.debug("bac non conforme" + logComp);
				return;
			}
		}

		// Donnees de l'individu
		final String codOpiIntEpo = parametreController.getPrefixeOPI() + candidat.getCompteMinima().getNumDossierOpiCptMin();

		// Voeux-->On cherche tout les voeuyx soumis à OPI-->Recherche des OPI du
		// candidat
		final List<Opi> listeOpi = opiController.getListOpiByCandidat(candidat, isBatch);
		final List<MAJOpiVoeuDTO3> listeMAJOpiVoeuDTO = new ArrayList<>();

		/* Au moins 1 opi n'est pas passé pour lancer l'opi */
		Boolean opiToPass = false;
		for (final Opi opi : listeOpi) {
			final MAJOpiVoeuDTO3 mAJOpiVoeuDTO = getVoeuByCandidature(opi.getCandidature());
			if (opi.getDatPassageOpi() == null) {
				opiToPass = true;
			}
			if (mAJOpiVoeuDTO != null) {
				listeMAJOpiVoeuDTO.add(mAJOpiVoeuDTO);
			}
		}

		/* Au moins 1 opi n'est pas passé pour lancer l'opi */
		if (!opiToPass) {
			logger.debug("aucun OPI a passer" + logComp);
			return;
		}

		/* Creation des objets DTO */
		final DonneesOpiDTO9 donneesOPI = new DonneesOpiDTO9();
		final MAJOpiIndDTO6 individu = new MAJOpiIndDTO6();
		final MAJEtatCivilDTO2 etatCivil = getEtatCivil(candidat);
		final MAJDonneesNaissanceDTO2 donneesNaissance = getDonneesNaissance(candidat);
		final MAJDonneesPersonnellesDTO3 donneesPersonnelles = new MAJDonneesPersonnellesDTO3();
		final MAJOpiBacDTO bac = new MAJOpiBacDTO();

		/* Informations de verification */
		individu.setCodOpiIntEpo(codOpiIntEpo);
		individu.setCodEtuOpi(null);
		if (candidat.getCompteMinima() != null && candidat.getCompteMinima().getSupannEtuIdCptMin() != null) {
			try {
				individu.setCodEtuOpi(Integer.valueOf(candidat.getCompteMinima().getSupannEtuIdCptMin()));
			} catch (final Exception e) {
			}
		}

		// donnees personnelles
		donneesPersonnelles.setAdrMailOpi(candidat.getCompteMinima().getMailPersoCptMin());
		donneesPersonnelles.setNumTelPorOpi(candidat.getTelPortCandidat());

		// BAC
		if (bacOuEqu != null && bacOuEqu.getSiScolBacOuxEqu() != null) {
			bac.setCodBac(bacOuEqu.getSiScolBacOuxEqu().getCodBac());
			bac.setCodDep((bacOuEqu.getSiScolDepartement()) != null ? bacOuEqu.getSiScolDepartement().getCodDep() : null);
			bac.setCodEtb((bacOuEqu.getSiScolEtablissement()) != null ? bacOuEqu.getSiScolEtablissement().getCodEtb() : null);
			bac.setCodTpe((bacOuEqu.getSiScolEtablissement()) != null ? bacOuEqu.getSiScolEtablissement().getCodTpeEtb() : null);
			bac.setCodMention((bacOuEqu.getSiScolMentionNivBac()) != null ? bacOuEqu.getSiScolMentionNivBac().getCodMnb() : null);

			// calcul de l'année
			if (bacOuEqu.getAnneeObtBac() != null) {
				logger.debug("bac avec annee" + logComp);
				bac.setDaaObtBacOba(bacOuEqu.getAnneeObtBac().toString());
			} else {
				logger.debug("bac sans annee" + logComp);
				bac.setDaaObtBacOba(getDefaultBacAnneeObt());
			}
		} else {
			final String codNoBac = parametreController.getSiscolCodeSansBac();
			if (codNoBac != null && !codNoBac.equals("")) {
				logger.debug("bac par defaut" + logComp);
				bac.setCodBac(codNoBac);
				bac.setDaaObtBacOba(getDefaultBacAnneeObt());
			} else {
				logger.debug("aucun bac" + logComp);
			}
		}

		individu.setEtatCivil(etatCivil);
		individu.setDonneesNaissance(donneesNaissance);
		individu.setDonneesPersonnelles(donneesPersonnelles);
		donneesOPI.setIndividu(individu);
		donneesOPI.setBac(bac);

		/* Donnes d'adresse */
		if (parametreController.getIsUtiliseOpiAdr()) {
			donneesOPI.setAdresseFixe(getAdresseOPI(candidat.getAdresse(), candidat));
		}

		/* Les voeux */
		int rang = 0;
		if (listeMAJOpiVoeuDTO != null) {
			MAJOpiVoeuDTO3[] tabDonneesVoeux = new MAJOpiVoeuDTO3[listeMAJOpiVoeuDTO.size()];
			for (final MAJOpiVoeuDTO3 v : listeMAJOpiVoeuDTO) {
				tabDonneesVoeux[rang] = v;
				rang++;
			}
			/**
			 * TODO Voir avec l'amue pour la supression des voeux --> hack : passer un
			 * tableau avec un voeu vide
			 */
			if (tabDonneesVoeux.length == 0) {
				tabDonneesVoeux = new MAJOpiVoeuDTO3[1];
				tabDonneesVoeux[0] = new MAJOpiVoeuDTO3();
				logger.debug("suppression des voeux" + logComp);
			}
			/** Fin TODO */
			donneesOPI.setVoeux(tabDonneesVoeux);
		} /* else{ logger.debug("aucun OPI a passer"+logComp); return; } */
		logger.debug("listVoeux " + rang + logComp);

		boolean actionWSok = false;
		try {
			if (monProxyOpi == null) {
				monProxyOpi = (OpiMetierServiceInterface) WSUtils.getService(WSUtils.OPI_SERVICE_NAME);
			}
			logger.debug("lancement ws OPI" + logComp);
			monProxyOpi.mettreajourDonneesOpi_v9(donneesOPI);
			logger.debug("fin ws OPI" + logComp);
			actionWSok = true;
		} catch (final Exception e) {
			logger.error("erreur ws OPI" + logComp, e);
			return;
		}

		// Si l'appel au WS s'est bien passé
		if (actionWSok) {
			// Vérifie si l'OPI est passé
			final List<IndOpi> listIndOpi = findNneIndOpiByCodOpiIntEpo(codOpiIntEpo, individu.getCodEtuOpi(), etatCivil, candidat.getDatNaissCandidat());

			// Test si on n'a pas réussi a recuprer l'opi qu'on vient de créer/mettre a jour
			// dans apogee
			if (listIndOpi == null || listIndOpi.size() == 0) {
				mailController.sendErrorToAdminFonctionnel("Erreur OPI" + logComp,
					"Erreur OPI : Probleme d'insertion de l'OPI dans Apogée, pas de données OPI" + logComp,
					logger);
				return;
			}

			IndOpi indOpi = null;

			// Test si plusieurs indopi trouvé
			if (listIndOpi.size() > 1) {
				// on recherche celui qu'on vient d'inserer
				final List<IndOpi> listeFromEcandidat = listIndOpi.stream()
					.filter(e -> e.getCodOpiIntEpo() != null && e.getCodOpiIntEpo().toUpperCase().equals(codOpiIntEpo.toUpperCase()))
					.collect(Collectors.toList());
				// si il y en a plusieurs-->erreur
				if (listeFromEcandidat.size() > 1) {
					mailController.sendErrorToAdminFonctionnel("Erreur OPI" + logComp,
						"Erreur OPI : Probleme d'insertion de l'OPI dans Apogée, plusieurs données OPI trouvées avec le même CodOpiIntEpo = "
							+ codOpiIntEpo.toUpperCase()
							+ logComp,
						logger);
					return;
				}
				// si il n'y en a aucun, cela veut dire que dans la liste listIndOpi on en a
				// plusieurs sans le notre et qu'on ne sait pas sur quel opi on a raccroché la
				// candiature
				else if (listeFromEcandidat.size() == 0) {
					mailController.sendErrorToAdminFonctionnel("Erreur OPI" + logComp,
						"Erreur OPI : Probleme d'insertion de l'OPI dans Apogée, plusieurs données OPI trouvées" + logComp,
						logger);
					return;
				}
				// si un seul, on a raccroché la candiature sur celui-ci
				else if (listeFromEcandidat.size() == 1) {
					indOpi = listeFromEcandidat.get(0);
				}
			}
			/* On a trouvé un seul OPI, c'est ok, on continue avec celui là */
			else if (listIndOpi.size() == 1) {
				indOpi = listIndOpi.get(0);
			}

			/* Verification que l'opi est bien trouvé et que son code n'ets pas null */
			if (indOpi == null) {
				logger.error("Erreur OPI : opi null ou non trouvé " + logComp);
				return;
			}

			/* Mise a jour de la date de passage de l'opi */
			/* On vérifie aussi que tout s'est bien passé */
			try {
				final List<VoeuxIns> listeVoeux = getVoeuxApogee(indOpi);
				final List<Opi> listeOpiATraiter = new ArrayList<>();
				listeVoeux.forEach(voeu -> {
					listeOpi.stream()
						.filter(
							opi -> opi.getDatPassageOpi() == null && voeu.getId().getCodEtp().equals(opi.getCandidature().getFormation().getCodEtpVetApoForm())
								&& String.valueOf(voeu.getId().getCodVrsVet()).equals(opi.getCandidature().getFormation().getCodVrsVetApoForm())
								&& voeu.getId().getCodCge().equals(opi.getCandidature().getFormation().getSiScolCentreGestion().getCodCge()))
						.collect(Collectors.toList())
						.forEach(opiFiltre -> {
							listeOpiATraiter.add(opiFiltre);
						});
				});

				/* Traitement des desistements apres confirmation */
				final List<Opi> listeOpiDesistementATraiter = new ArrayList<>();
				listeOpi.stream()
					.filter(
						opi -> opi.getDatPassageOpi() == null && opi.getCandidature().getTemAcceptCand() != null && !opi.getCandidature().getTemAcceptCand())
					.forEach(opiDesist -> {
						final long nbvoeuxDesist = listeVoeux.stream()
							.filter(voeu -> voeu.getId().getCodEtp().equals(opiDesist.getCandidature().getFormation().getCodEtpVetApoForm())
								&& String.valueOf(voeu.getId().getCodVrsVet()).equals(opiDesist.getCandidature().getFormation().getCodVrsVetApoForm())
								&& voeu.getId().getCodCge().equals(opiDesist.getCandidature().getFormation().getSiScolCentreGestion().getCodCge()))
							.count();
						// si il existe un voeu ayant les bonnes caracteristiques, on ne le traite pas,
						// sinon on le traite
						if (nbvoeuxDesist == 0) {
							listeOpiDesistementATraiter.add(opiDesist);
						}
					});

				/* On verifie que le code OPI provient de ecandidat */
				final Boolean isCodOpiIntEpoFromEcandidat = codOpiIntEpo.toUpperCase().equals(indOpi.getCodOpiIntEpo().toUpperCase());

				/* On traite les OPI */
				opiController.traiteListOpiCandidat(candidat, listeOpiATraiter, isCodOpiIntEpoFromEcandidat, indOpi.getCodOpiIntEpo(), logComp);

				/* On traite les OPI en desistement */
				opiController.traiteListOpiDesistCandidat(candidat, listeOpiDesistementATraiter, logComp);

				/* Traitement des PJ */
				opiController.traiteListOpiPjCandidat(listeOpiATraiter, indOpi.getCodOpiIntEpo(), indOpi.getCodIndOpi(), logComp, isBatch);

			} catch (final SiScolException e) {
				logger.error("Erreur OPI : Probleme d'insertion des voeux dans Apogée" + logComp, e);
				// Affichage message dans l'interface
				return;
			}
		}
		return;
	}

	/**
	 * Renvoie les voeux OPI d'un individu
	 * @param  indOpi
	 * @return
	 * @throws SiScolException
	 */
	private List<VoeuxIns> getVoeuxApogee(final IndOpi indOpi) throws SiScolException {
		try {
			final String queryString = "Select a from VoeuxIns a where a.id.codIndOpi = " + indOpi.getCodIndOpi();
			logger.debug("Vérification des voeux " + queryString);
			final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
			final EntityManager em = emf.createEntityManager();
			final Query query = em.createQuery(queryString, VoeuxIns.class);
			final List<VoeuxIns> listeSiScol = query.getResultList();
			em.close();
			return listeSiScol;
		} catch (final Exception e) {
			throw new SiScolException("SiScol database error on getVoeuxApogee", e);
		}

	}

	/**
	 * @param  codOpiIntEpo
	 * @param  codEtuOpi
	 * @param  etatCivil
	 * @param  dateNaissance
	 * @return               l'individu OPI recherché
	 */
	public List<IndOpi>
		findNneIndOpiByCodOpiIntEpo(final String codOpiIntEpo, final Integer codEtuOpi, final MAJEtatCivilDTO2 etatCivil, final LocalDate dateNaissance) {
		final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
		final EntityManager em = emf.createEntityManager();

		/* Verification par codOpiIntEpo ou codEtuOpi ou INE */
		String requete = "Select a from IndOpi a where a.codOpiIntEpo='" + codOpiIntEpo + "'";
		if (codEtuOpi != null) {
			requete = requete + " or a.codEtuOpi=" + codEtuOpi;
		}
		if (etatCivil.getCodNneIndOpi() != null) {
			requete = requete + " or (a.codNneIndOpi='"
				+ MethodUtils.getIne(etatCivil.getCodNneIndOpi())
				+ "' and a.codCleNneIndOpi='"
				+ MethodUtils.getCleIne(etatCivil.getCodNneIndOpi())
				+ "')";
		}

		logger.debug(requete);

		/* Verification par nom et prenom et DtNaissance */
		Query query = em.createQuery(requete, IndOpi.class);
		List<IndOpi> lindopi = query.getResultList();

		if (lindopi != null && lindopi.size() > 0) {
			em.close();
			return lindopi;
		}

		requete = "Select a from IndOpi a where a.libNomPatIndOpi=?1 and a.libPr1IndOpi=?2 and a.dateNaiIndOpi=?3";
		logger.debug(requete);
		query = em.createQuery(requete, IndOpi.class);
		query.setParameter(1, etatCivil.getLibNomPatIndOpi().toUpperCase());
		query.setParameter(2, etatCivil.getLibPr1IndOpi().toUpperCase());
		query.setParameter(3, MethodUtils.convertLocalDateToDate(dateNaissance));
		lindopi = query.getResultList();

		if (lindopi != null && lindopi.size() > 0) {
			em.close();
			return lindopi;
		}

		em.close();
		return null;
	}

	/**
	 * Transforme une candidature en voeuy OPI
	 * @param  candidature
	 * @return             transforme une candidature en voeu
	 */
	private MAJOpiVoeuDTO3 getVoeuByCandidature(final Candidature candidature) {
		final Formation formation = candidature.getFormation();
		if (formation.getCodEtpVetApoForm() == null || formation.getCodVrsVetApoForm() == null || formation.getSiScolCentreGestion() == null) {
			return null;
		}
		if (candidature.getTemAcceptCand() == null || !candidature.getTemAcceptCand()) {
			return null;
		}

		final MAJOpiVoeuDTO3 voeu = new MAJOpiVoeuDTO3();
		voeu.setNumCls(1);
		voeu.setCodCmp(null);
		voeu.setCodCge(formation.getSiScolCentreGestion().getCodCge());
		if (formation.getCodDipApoForm() != null && formation.getCodVrsVdiApoForm() != null) {
			voeu.setCodDip(formation.getCodDipApoForm());
			voeu.setCodVrsVdi(Integer.parseInt(formation.getCodVrsVdiApoForm()));
		} else {
			voeu.setCodDip(null);
			voeu.setCodVrsVdi(null);
		}
		voeu.setCodEtp(formation.getCodEtpVetApoForm());
		voeu.setCodVrsVet(Integer.parseInt(formation.getCodVrsVetApoForm()));
		voeu.setCodSpe1Opi(null);
		voeu.setCodSpe2Opi(null);
		voeu.setCodSpe3Opi(null);
		voeu.setCodTyd(null);
		voeu.setCodAttDec(null);
		voeu.setCodDecVeu("F");
		voeu.setCodDemDos("C");
		voeu.setCodMfo(null);
		voeu.setTemValPsd("N");
		voeu.setLibCmtJur(null);
		voeu.setTitreAccesExterne(null);
		voeu.setConvocation(null);

		/* Exonération */
		if (candidature.getSiScolCatExoExt() != null) {
			voeu.setCodCatExoExt(candidature.getSiScolCatExoExt().getCodCatExoExt());
			if (candidature.getMntChargeCand() != null) {
				final String montant = MethodUtils.parseBigDecimalAsString(candidature.getMntChargeCand());
				if (montant != null && montant.length() <= 15) {
					voeu.setComExoExt(String.valueOf(candidature.getMntChargeCand()));
				}
			}
		}

		return voeu;
	}

	/**
	 * @param  candidat
	 * @return          l'etat civil
	 */
	@Override
	public MAJEtatCivilDTO2 getEtatCivil(final Candidat candidat) {
		final MAJEtatCivilDTO2 etatCivil = new MAJEtatCivilDTO2();
		// Etat Civil
		etatCivil.setLibNomPatIndOpi(MethodUtils.cleanForApogeeWS(candidat.getNomPatCandidat()));
		etatCivil.setLibNomUsuIndOpi(MethodUtils.cleanForApogeeWS(candidat.getNomUsuCandidat()));
		etatCivil.setLibPr1IndOpi(MethodUtils.cleanForApogeeWS(candidat.getPrenomCandidat()));
		etatCivil.setLibPr2IndOpi(MethodUtils.cleanForApogeeWS(candidat.getAutrePrenCandidat()));
		// separer le clé du code nne
		if (StringUtils.hasText(candidat.getIneCandidat()) && StringUtils.hasText(candidat.getCleIneCandidat())) {
			etatCivil.setCodNneIndOpi(MethodUtils.cleanForApogee(candidat.getIneCandidat()) + MethodUtils.cleanForApogee(candidat.getCleIneCandidat()));
		}

		if (candidat.getCivilite() != null && candidat.getCivilite().getCodApo() != null) {
			String codSex = "";
			if (candidat.getCivilite().getCodApo().equals("1")) {
				codSex = "M";
			} else {
				codSex = "F";
			}
			etatCivil.setCodSexEtuOpi(codSex);
		}
		return etatCivil;
	}

	/**
	 * @param  candidat
	 * @return          les données de naissance
	 */
	public MAJDonneesNaissanceDTO2 getDonneesNaissance(final Candidat candidat) {
		final MAJDonneesNaissanceDTO2 donneesNaissance = new MAJDonneesNaissanceDTO2();
		// Donnees Naissance
		donneesNaissance.setDateNaiIndOpi(formatterDateTimeApo.format(candidat.getDatNaissCandidat()));
		donneesNaissance.setTemDateNaiRelOpi("N");
		if (candidat.getSiScolPaysNat() != null) {
			donneesNaissance.setCodPayNat(candidat.getSiScolPaysNat().getCodPay());
		}
		donneesNaissance.setLibVilNaiEtuOpi(MethodUtils.cleanForApogee(candidat.getLibVilleNaissCandidat()));

		if (candidat.getSiScolDepartement() == null) {
			donneesNaissance.setCodTypDepPayNai("P");
			donneesNaissance.setCodDepPayNai(candidat.getSiScolPaysNaiss().getCodPay());
		} else {
			donneesNaissance.setCodTypDepPayNai("D");
			donneesNaissance.setCodDepPayNai(candidat.getSiScolDepartement().getCodDep());
		}
		return donneesNaissance;
	}

	/**
	 * @param  adresseCandidat
	 * @param  candidat
	 * @return                 l'adresse transformée
	 */
	private MAJOpiAdresseDTO getAdresseOPI(final Adresse adresseCandidat, final Candidat candidat) {
		final MAJOpiAdresseDTO adresse = new MAJOpiAdresseDTO();
		adresse.setCodBdi(adresseCandidat.getCodBdiAdr());
		if (adresseCandidat.getSiScolPays() != null) {
			adresse.setCodPay(adresseCandidat.getSiScolPays().getCodPay());
		}
		if (adresseCandidat.getSiScolCommune() != null) {
			adresse.setCodCom(adresseCandidat.getSiScolCommune().getCodCom());
		}

		adresse.setLib1(adresseCandidat.getAdr1Adr());
		adresse.setLib2(adresseCandidat.getAdr2Adr());
		adresse.setLib3(adresseCandidat.getAdr3Adr());
		adresse.setLibAde(adresseCandidat.getLibComEtrAdr());
		if (candidat.getTelCandidat() != null) {
			adresse.setNumTel(candidat.getTelCandidat());
		} else if (candidat.getTelPortCandidat() != null) {
			adresse.setNumTel(candidat.getTelPortCandidat());
		}

		return adresse;
	}

	/** @return l'année univ default pour l'opi bac */
	private String getDefaultBacAnneeObt() {
		if (cacheController.getListeAnneeUni() != null) {
			final Optional<SiScolAnneeUni> annee = cacheController.getListeAnneeUni()
				.stream()
				.filter(e -> (e.getEtaAnuIae() != null && e.getEtaAnuIae().equals(ConstanteUtils.TYP_BOOLEAN_YES)))
				.findFirst();
			if (annee.isPresent()) {
				return annee.get().getCodAnu();
			}
		}
		return String.valueOf(LocalDate.now().getYear());
	}

	/**
	 * @param  codAnu
	 * @param  codEtu
	 * @param  codPj
	 * @return                 l'information d'un fichier PJ d'Apogée
	 * @throws SiScolException
	 */
	@Override
	public WSPjInfo getPjInfoFromApogee(final String codAnu, final String codEtu, final String codPj) throws SiScolException {
		// select * from APOGEE.TELEM_IAA_TPJ where COD_ANU = 2016;
		if (urlWsPjApogee == null || urlWsPjApogee.equals("")) {
			return null;
		}

		try {
			final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			if (codAnu != null) {
				params.add("codAnu", codAnu);
			}
			params.add("codEtu", codEtu);
			params.add("codTpj", codPj);
			final List<WSPjInfo> liste = siScolRestServiceInterface.getList(urlWsPjApogee, ConstanteUtils.WS_APOGEE_PJ_INFO, WSPjInfo[].class, params);
			if (liste == null) {
				return null;
			}
			/* Obligé de tester avec le code transmis et le libellé car l'AMUE s'est planté
			 * à la première livraison et a fourni le libelle à la place du code */
			final Optional<WSPjInfo> optWsInfo = liste.stream()
				.filter(e -> e.getTemDemPJ() && e.getNomFic() != null
					&& !e.getNomFic().equals("")
					&& e.getStuPj() != null
					&& e.getStuPj().substring(0, 1).toUpperCase().equals(ConstanteUtils.WS_APOGEE_PJ_TEM_VALID_CODE))
				.sorted((e1, e2) -> (e2.getCodAnu().compareTo(e1.getCodAnu())))
				.findFirst();
			if (optWsInfo.isPresent()) {
				return optWsInfo.get();
			}
			return null;
		} catch (final SiScolRestException e) {
			if (e.getErreurType().equals("nullretrieve.etudiantinexistant")) {
				return null;
			}
			// traiter les autres cas
			return null;
		} catch (final Exception ex) {
			logger.error("Probleme avec le WS de demande d'info d'une PJ : codEtu=" + codEtu + ", codAnu=" + codAnu + ", codPj=" + codPj, ex);
			throw new SiScolException("Probleme avec le WS de demande d'info d'une PJ : codEtu=" + codEtu + ", codAnu=" + codAnu + ", codPj=" + codPj, ex);
		}
	}

	/**
	 * @param  codAnu
	 * @param  codEtu
	 * @param  codPj
	 * @return                 le fichier PJ d'Apogée
	 * @throws SiScolException
	 */
	@Override
	public InputStream getPjFichierFromApogee(final String codAnu, final String codEtu, final String codPj) throws SiScolException {
		// http://apogee-ws-test.univ-lorraine.fr/apo-ws/services/PJ/fichier?codAnu=2016&codEtu=xxx&codTpj=xxx
		if (urlWsPjApogee == null || urlWsPjApogee.equals("")) {
			return null;
		}
		try {
			final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("codAnu", codAnu);
			params.add("codEtu", codEtu);
			params.add("codTpj", codPj);
			return siScolRestServiceInterface.getFile(urlWsPjApogee, ConstanteUtils.WS_APOGEE_PJ_FILE, params);
		} catch (final SiScolRestException e) {
			if (e.getErreurType().equals("nullretrieve.etudiantinexistant")) {
				return null;
			}
			// traiter les autres cas
			return null;
		} catch (final Exception ex) {
			logger.error("Probleme avec le WS de demande de fichier d'une PJ : codEtu=" + codEtu + ", codAnu=" + codAnu + ", codPj=" + codPj, ex);
			throw new SiScolException("Probleme avec le WS de demande de fichier d'une PJ : codEtu=" + codEtu + ", codAnu=" + codAnu + ", codPj=" + codPj, ex);
		}
	}

	/* (non-Javadoc)
	 *
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#
	 * creerOpiPjViaWS(fr.univlorraine.ecandidat.entities.ecandidat.OpiPj) */
	@Override
	public void creerOpiPjViaWS(final PjOpi pjOpi, final Fichier file, final InputStream is) throws SiScolException {
		if (monProxyPjOpi == null) {
			monProxyPjOpi = (PjOpiMetierServiceInterface) WSUtils.getService(WSUtils.PJOPI_SERVICE_NAME);
		}
		if (is == null) {
			return;
		}
		final Candidat candidat = pjOpi.getCandidat();
		final String titleLogError = "Erreur WS OPI_PJ - ";
		final String complementLogError =
			"Parametres : codOpi=" + pjOpi.getId().getCodOpi() + ", codApoPj=" + pjOpi.getId().getCodApoPj() + ", idCandidat=" + candidat.getIdCandidat();
		try {
			final String codOpi = pjOpi.getId().getCodOpi();
			final String nomPatCandidat = MethodUtils.cleanForApogeeWS(candidat.getNomPatCandidat());
			final String prenomCandidat = MethodUtils.cleanForApogeeWS(candidat.getPrenomCandidat());
			final String codApoPj = pjOpi.getId().getCodApoPj();
			final String nomFichier = file.getNomFichier();

			logger.debug("Creation OPI_PJ WS Apogée : codOpi = " + codOpi
				+ ", nomPatCandidat = "
				+ nomPatCandidat
				+ ", prenomCandidat = "
				+ prenomCandidat
				+ ", codApoPj = "
				+ codApoPj
				+ ", nomFichier = "
				+ nomFichier);
			if (codOpi == null || nomPatCandidat == null || prenomCandidat == null || codApoPj == null || nomFichier == null) {
				throw new SiScolException(titleLogError + "Parametre null - " + complementLogError);
			}
			monProxyPjOpi.recupererPiecesJustificativesOPIWS(codOpi,
				nomPatCandidat,
				prenomCandidat,
				codApoPj,
				nomFichier,
				new DataHandler(new ByteArrayDataSource(is, MethodUtils.getMimeType(file.getNomFichier()))));
		} catch (final WebBaseException e) {
			throw new SiScolException(titleLogError + "Code=" + e.toString() + ", Message =" + e.getLastErrorMsg() + " - " + complementLogError, e);
		} catch (final AxisFault e) {
			throw new SiScolException(titleLogError + complementLogError, e);
		} catch (final Exception e) {
			throw new SiScolException(titleLogError + complementLogError, e);
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#checkStudentINES(java.lang.String, java.lang.String) */
	@Override
	public Boolean checkStudentINES(final String ine, final String cle) throws SiScolException {
		try {

			if (urlWsCheckInes == null || urlWsCheckInes.equals("")) {
				return true;
			}
			/* Definition de l'uri */
			final URI uri = SiScolRestUtils.getURIForPostService(urlWsCheckInes, ConstanteUtils.WS_INES_CHECK_SERVICE);
			/* Ajout des parametres */
			final Map<String, String> mapPostParameter = new HashMap<>();
			mapPostParameter.put(ConstanteUtils.WS_INES_PARAM_TYPE, ConstanteUtils.WS_INES_PARAM_TYPE_INES);
			mapPostParameter.put(ConstanteUtils.WS_INES_PARAM_INE, ine);
			mapPostParameter.put(ConstanteUtils.WS_INES_PARAM_KEY, cle);
			return new RestTemplate().postForObject(uri, mapPostParameter, Boolean.class);
		} catch (final Exception e) {
			logger.error("Erreur à l'appel du service de vérification INES", e);
			throw new SiScolException("Erreur à l'appel du service de vérification INES", e);
		}
	}

	/**
	 * @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getVersionWSCheckIne()
	 */
	@Override
	public String getVersionWSCheckIne() {
		try {
			if (urlWsCheckInes == null || urlWsCheckInes.equals("")) {
				return NomenclatureUtils.VERSION_NO_VERSION_VAL;
			}
			/* Definition de l'uri */
			final URI uri = SiScolRestUtils.getURIForPostService(urlWsCheckInes, ConstanteUtils.WS_INES_VERSION);
			final String res = new RestTemplate().getForObject(uri, String.class);
			if (res == null) {
				return NomenclatureUtils.VERSION_NO_VERSION_VAL;
			}
			return res;
		} catch (final Exception e) {
			logger.warn("Erreur à l'appel du service de version de checkine");
			return NomenclatureUtils.VERSION_NO_VERSION_VAL;
		}
	}

	@Override
	public void deleteOpiPJ(final String codIndOpi, final String codTpj) throws SiScolException {
		try {
			if (codIndOpi == null || codTpj == null) {
				return;
			}
			final EntityManagerFactory emf = Persistence.createEntityManagerFactory("pun-jpa-siscol");
			final EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			final Query query = em.createNativeQuery("DELETE FROM OPI_PJ WHERE COD_IND_OPI = " + codIndOpi + " AND COD_TPJ = '" + codTpj + "'");
			query.executeUpdate();
			em.getTransaction().commit();
			em.close();
		} catch (final Exception e) {
			logger.error("Erreur à la suppression d'une OPI_PJ - codIndOpi=" + codIndOpi + ", codTpj=" + codTpj, e);
			throw new SiScolException("Erreur à l'appel du service de vérification INES", e);
		}
	}
}
