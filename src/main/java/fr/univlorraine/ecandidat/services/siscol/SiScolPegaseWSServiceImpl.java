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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.opencsv.ICSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.controllers.BatchController;
import fr.univlorraine.ecandidat.controllers.CandidatureController;
import fr.univlorraine.ecandidat.controllers.MailController;
import fr.univlorraine.ecandidat.controllers.OpiController;
import fr.univlorraine.ecandidat.controllers.TableRefController;
import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CandidatBacOuEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Formation;
import fr.univlorraine.ecandidat.entities.ecandidat.Opi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOptBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacSpeBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCatExoExt;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolComBdi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissementPK;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolOptionBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolRegime;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolSpecialiteBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolStatut;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolUtilisateur;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;
import fr.univlorraine.ecandidat.entities.siscol.WSAdresse;
import fr.univlorraine.ecandidat.entities.siscol.WSBac;
import fr.univlorraine.ecandidat.entities.siscol.WSCursusInterne;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Apprenant;
import fr.univlorraine.ecandidat.entities.siscol.pegase.ApprenantContact;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Commune;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Departement;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Etablissement;
import fr.univlorraine.ecandidat.entities.siscol.pegase.FormationPegase;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Inscription;
import fr.univlorraine.ecandidat.entities.siscol.pegase.MentionBac;
import fr.univlorraine.ecandidat.entities.siscol.pegase.MentionHonorifique;
import fr.univlorraine.ecandidat.entities.siscol.pegase.NomenclatureDispo;
import fr.univlorraine.ecandidat.entities.siscol.pegase.NomenclaturePagination;
import fr.univlorraine.ecandidat.entities.siscol.pegase.ObjetMaquettePagination;
import fr.univlorraine.ecandidat.entities.siscol.pegase.OpiCandidat;
import fr.univlorraine.ecandidat.entities.siscol.pegase.OpiVoeu;
import fr.univlorraine.ecandidat.entities.siscol.pegase.PaysNationalite;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Periode;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Publication;
import fr.univlorraine.ecandidat.entities.siscol.pegase.SerieBac;
import fr.univlorraine.ecandidat.entities.siscol.pegase.SpecialiteBacGeneral;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Structure;
import fr.univlorraine.ecandidat.entities.siscol.pegase.TypeDiplome;
import fr.univlorraine.ecandidat.entities.siscol.pegase.TypeResultat;
import fr.univlorraine.ecandidat.repositories.SiScolCommuneRepository;
import fr.univlorraine.ecandidat.repositories.SiScolEtablissementRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.PegaseMappingStrategy;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseAuth;
import fr.univlorraine.ecandidat.utils.bean.config.ConfigPegaseUrl;
import fr.univlorraine.ecandidat.utils.bean.presentation.FileOpi;
import fr.univlorraine.ecandidat.views.windows.InfoWindow;

/**
 * Gestion du SI Scol pégase
 * @author Kevin Hergalant
 */
@Component(value = "siScolPegaseWSServiceImpl")
@SuppressWarnings("serial")
public class SiScolPegaseWSServiceImpl implements SiScolGenericService, Serializable {

	private final Logger logger = LoggerFactory.getLogger(SiScolPegaseWSServiceImpl.class);

	/* Constantes OPI */
	private final char OPI_SEPARATOR = ';';
	private final String OPI_FILE_EXT = ".csv";
	private final String OPI_FILE_EXT_ZIP = ".zip";
	private final String OPI_FILE_CANDIDAT = "candidat";
	private final String OPI_ORIGINE = "EC";
	private final String OPI_FILE_CANDIDATURE = "candidature";

	private final static String PROPERTY_FILE = "configUrlServicesPegase.properties";
	private final Properties properties = new Properties();

	@Resource
	private transient ApplicationContext applicationContext;
	/** TODO à supprimer */
	@Resource
	private transient SiScolCommuneRepository siScolCommuneRepository;
	@Resource
	private transient SiScolEtablissementRepository siScolEtablissementRepository;
	@Resource
	private transient TableRefController tableRefController;
	@Resource
	private transient BatchController batchController;
	@Resource
	private transient OpiController opiController;
	@Resource
	private transient CandidatureController candidatureController;
	@Resource
	private transient MailController mailController;

	@Resource
	private transient RestTemplate wsPegaseRestTemplate;

	@Resource
	private transient RestTemplate wsPegaseJwtRestTemplate;

	@Resource
	private transient DateTimeFormatter formatterDate;
	@Resource
	private transient DateTimeFormatter formatterDateFile;

	@Value("${pegase.ws.username:}")
	private transient String username;

	@Value("${pegase.ws.password:}")
	private transient String password;

	@Value("${pegase.opi.path:}")
	private transient String opiPath;

	@Value("${pegase.etablissement:}")
	private transient String etablissement;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	private String jwtToken = null;

	@Override
	public String getTypSiscol() {
		return ConstanteUtils.SISCOL_TYP_PEGASE;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#isImplementationPegase() */
	@Override
	public Boolean isImplementationPegase() {
		return true;
	}

	@Override
	public String getCodPaysFrance() {
		return ConstanteUtils.PAYS_CODE_FRANCE_PEGASE;
	}

	/**
	 * @return le fichier de properties
	 */
	private Properties getProperties() {
		if (properties.isEmpty()) {
			/* On cherche le fichier de properties dans le classpath */
			try {
				properties.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE));
				return properties;
			} catch (final Exception e) {
				throw new RuntimeException("Impossible de charger le fichier configUrlServices, ajoutez le dans le dossier ressources", e);
			}
		} else {
			return properties;
		}
	}

	/**
	 * @param  key
	 * @return     la valeur d'une key de configUrlServices.properties
	 */
	private String getPropertyVal(final String key) {
		return getProperties().getProperty(key);
	}

	/**
	 * @param  username
	 * @param  password
	 * @param  url
	 * @return                 un nouveau jeton JWT
	 * @throws SiScolException
	 */
	private String getNewJwtToken(final String username, final String password, final String url) throws SiScolException {
		if (!siScolService.isImplementationPegase() || username == null || password == null || url == null) {
			return null;
		}
		logger.debug("Demande d'un nouveau jeton JWT");
		try {
			final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("username", username);
			params.add("password", password);
			params.add("token", "true");

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			final URI uri = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_AUTH), null, params);
			final ResponseEntity<String> response = wsPegaseJwtRestTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(headers), String.class);
			final String jwtToken = response.getBody();
			if (jwtToken == null) {
				throw new SiScolException("Token JWT null, impossible de continuer");
			}
			logger.debug("Demande d'un nouveau jeton JWT effectuée, taille = " + jwtToken.length());
			return jwtToken;
		} catch (final Exception e) {
			throw new SiScolException(e);
		}
	}

	/**
	 * Demande d'un nouveau token toutes les heures
	 */
	@Scheduled(fixedRate = 60 * 60 * 1000)
	private synchronized void scheduledNewJwtToken() {
		try {
			jwtToken = getNewJwtToken(username, password, getPropertyVal(ConstanteUtils.PEGASE_URL_AUTH));
		} catch (final SiScolException e) {
			logger.debug("Synchronisation d'un nouveau jeton JWT en erreur", e);
		}
	}

	/**
	 * @return                 le token JWT
	 * @throws SiScolException
	 */
	private synchronized String getJwtToken() throws SiScolException {
		if (jwtToken == null) {
			jwtToken = getNewJwtToken(username, password, getPropertyVal(ConstanteUtils.PEGASE_URL_AUTH));
		}
		return jwtToken;
	}

	/**
	 * @return
	 * @throws SiScolException
	 */
	private HttpHeaders createHttpHeaders() throws SiScolException {
		/* Creation du header et passage du token GWT */
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + getJwtToken());
		return headers;
	}

	/**
	 * Execute un appel au WS Pegase pour récupérer une liste d'entité
	 * @param  <T>             le type de la nomenclature
	 * @param  service         le service à appeler
	 * @param  className       la class
	 * @return                 une liste d'entité
	 * @throws SiScolException
	 */
	private <T> List<T> getListNomenclature(final String service, final Class<T> className) throws SiScolException {
		return getListRef(ConstanteUtils.PEGASE_URI_REF + "/" + service, ConstanteUtils.PEGASE_LIMIT_DEFAULT, className);
	}

	/**
	 * Execute un appel au WS Pegase pour récupérer une liste d'entité
	 * @param  <T>             le type de la nomenclature
	 * @param  service         le service à appeler
	 * @param  className       la class
	 * @return                 une liste d'entité
	 * @throws SiScolException
	 */
//	private <T> List<T> getListParametrage(final String service, final Class<T> className) throws SiScolException {
//		return getListRef(ConstanteUtils.PEGASE_URI_PARAMETRAGE + "/" + service, ConstanteUtils.PEGASE_LIMIT_DEFAULT, className);
//	}

	/**
	 * Execute un appel au WS Pegase pour récupérer une liste d'entité
	 * @param  <T>             le type de la nomenclature
	 * @param  service         le service à appeler
	 * @param  limit           le limit
	 * @param  className       la class
	 * @return                 une liste d'entité
	 * @throws SiScolException
	 */
	private <T> List<T> getListRef(final String service, final Long limit, final Class<T> className) throws SiScolException {
		try {
			/* Liste a retourner */
			final List<T> listToRetrun = new ArrayList<>();

			/* Creation du header et passage du token GWT */
			final HttpHeaders headers = createHttpHeaders();

			/* Construction de la requete */
			final ResolvableType resolvableType = ResolvableType.forClassWithGenerics(NomenclaturePagination.class, className);
			final ParameterizedTypeReference<NomenclaturePagination<T>> typeRef = ParameterizedTypeReference.forType(resolvableType.getType());
			final HttpEntity<NomenclaturePagination<T>> httpEntity = new HttpEntity<>(headers);

			/* Permet de gérer la pagination */
			Long currentPage = 0L;
			Long nbPage = 1L;

			/* Execution des requetes paginées */
			while (currentPage < nbPage) {
				final URI uri = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_REF), service, currentPage, limit, null);
				logger.debug("Call ws pegase, numPage = " + currentPage + ", nbPage = " + nbPage + ", service = " + service + ", URI = " + uri);

				final ResponseEntity<NomenclaturePagination<T>> response = wsPegaseRestTemplate.exchange(
					uri,
					HttpMethod.GET,
					httpEntity,
					typeRef);

				currentPage = currentPage + 1;
				nbPage = response.getBody().getNbTotalPages();
				listToRetrun.addAll(response.getBody().getNomenclatures());
			}
			return listToRetrun;
		} catch (final Exception e) {
			throw new SiScolException("SiScol call ws error on execute call list entity", e);
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolBacOuxEqu() */
	@Override
	public List<SiScolBacOuxEqu> getListSiScolBacOuxEqu() throws SiScolException {
		final List<SerieBac> listSerie = getListNomenclature(ConstanteUtils.PEGASE_URI_REF_SERIE_BAC, SerieBac.class);
		return listSerie.stream().map(e -> new SiScolBacOuxEqu(e.getCode(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), e.getDateDebutValidite(), e.getDateFinValidite(), getTypSiscol()))
			.collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCentreGestion() */
	@Override
	public List<SiScolCentreGestion> getListSiScolCentreGestion() throws SiScolException {
		if (!hasCge()) {
			return new ArrayList<>();
		}
		/* Creation du header et passage du token GWT */
		final HttpHeaders headers = createHttpHeaders();
		final HttpEntity<Structure> httpEntity = new HttpEntity<>(headers);
		final URI uri = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_REF), ConstanteUtils.PEGASE_URI_REF_STRUCTURE, null);
		logger.debug("Call ws pegase, , service = " + ConstanteUtils.PEGASE_URI_REF_STRUCTURE + ", URI = " + uri);

		final ResponseEntity<List<Structure>> response = wsPegaseRestTemplate.exchange(
			uri,
			HttpMethod.GET,
			httpEntity,
			new ParameterizedTypeReference<List<Structure>>() {
			});

		return response.getBody().stream().map(e -> new SiScolCentreGestion(e.getCode(), e.getAppellationOfficielle(), e.getDenominationPrincipale(), true, getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCommune() */
	@Override
	public List<SiScolCommune> getListSiScolCommune() throws SiScolException {
		final List<Commune> listCommune = getListNomenclature(ConstanteUtils.PEGASE_URI_REF_COMMUNE, Commune.class);
		/* On passe dans une map car on a des commune avec des bdi différents, distinct sur code insee */
		final Map<String, SiScolCommune> mapDistinct = new HashMap<>();
		listCommune.stream().filter(e -> e.getCodeInseeAncien() != null).forEach(e -> mapDistinct.put(e.getCodeInseeAncien(), new SiScolCommune(e.getCodeInseeAncien(), e.getLibelleAffichage(), false, getTypSiscol())));
		listCommune.stream().filter(e -> e.getCodeInsee() != null).forEach(e -> mapDistinct.put(e.getCodeInsee(), new SiScolCommune(e.getCodeInsee(), e.getLibelleAffichage(), e.getTemoinVisible(), getTypSiscol())));

		return mapDistinct.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDepartement() */
	@Override
	public List<SiScolDepartement> getListSiScolDepartement() throws SiScolException {
		final List<Departement> listDpt = getListNomenclature(ConstanteUtils.PEGASE_URI_REF_DEPARTEMENT, Departement.class);
		return listDpt.stream().map(e -> new SiScolDepartement(e.getCode(), e.getLibelleLong(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDipAutCur() */
	@Override
	public List<SiScolDipAutCur> getListSiScolDipAutCur() throws SiScolException {
		final List<TypeDiplome> listTypeDiplome = getListNomenclature(ConstanteUtils.PEGASE_URI_REF_TYPE_DIPLOME, TypeDiplome.class);
		return listTypeDiplome.stream().map(e -> new SiScolDipAutCur(e.getCode(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolEtablissement() */
	@Override
	public List<SiScolEtablissement> getListSiScolEtablissement() throws SiScolException {
		final List<SiScolEtablissement> listEtab = new ArrayList<>();
		getListNomenclature(ConstanteUtils.PEGASE_URI_REF_ETAB, Etablissement.class).forEach(e -> {
			if (e.getDepartement() != null && e.getCommune() != null && e.getLibelleAffichage() != null) {
				final SiScolEtablissement etab =
					new SiScolEtablissement(e.getNumeroUai(), e.getTypeUai().getTypeUai(), e.getLibelleAffichage(), e.getLibelleAffichage(), e.getLibelleAffichage(), e.getTemoinVisible(), getTypSiscol());
				etab.setSiScolDepartement(new SiScolDepartement(e.getDepartement().getCode(), getTypSiscol()));
				String codComm = e.getCommune();
				if (codComm.length() == 4) {
					codComm = "0" + codComm;
				}

				/** TODO à supprimer quand commune OK */
				final SiScolCommune comm = tableRefController.getCommuneByCode(codComm);
				if (comm == null) {
					logger.warn("Commune absente : " + codComm);
					return;
				}
				etab.setSiScolCommune(new SiScolCommune(codComm, getTypSiscol()));
				listEtab.add(etab);
			}
		});
		return listEtab;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolMention() */
	@Override
	public List<SiScolMention> getListSiScolMention() throws SiScolException {
		final List<MentionHonorifique> listMention = getListNomenclature(ConstanteUtils.PEGASE_URI_REF_MENTION, MentionHonorifique.class);
		return listMention.stream().map(e -> new SiScolMention(e.getCode(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolMentionNivBac() */
	@Override
	public List<SiScolMentionNivBac> getListSiScolMentionNivBac() throws SiScolException {
		final List<MentionBac> listTypeDiplome = getListNomenclature(ConstanteUtils.PEGASE_URI_REF_MENTION_BAC, MentionBac.class);
		return listTypeDiplome.stream().map(e -> new SiScolMentionNivBac(e.getCode(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolPays() */
	@Override
	public List<SiScolPays> getListSiScolPays() throws SiScolException {
		final List<PaysNationalite> listPaysNationalite = getListNomenclature(ConstanteUtils.PEGASE_URI_REF_PAYS_NAT, PaysNationalite.class);
		return listPaysNationalite.stream()
			.map(e -> new SiScolPays(e.getCode(), e.getLibelleNationalite() != null ? e.getLibelleNationalite() : e.getLibelleCourt(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol()))
			.collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolTypDiplome() */
	@Override
	public List<SiScolTypDiplome> getListSiScolTypDiplome() throws SiScolException {
		final List<TypeDiplome> listTypeDiplome = getListNomenclature(ConstanteUtils.PEGASE_URI_REF_TYPE_DIPLOME, TypeDiplome.class);
		return listTypeDiplome.stream().map(e -> new SiScolTypDiplome(e.getCode(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolUtilisateur() */
	@Override
	public List<SiScolUtilisateur> getListSiScolUtilisateur() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolComBdi() */
	@Override
	public List<SiScolComBdi> getListSiScolComBdi() throws SiScolException {
		final List<Commune> listCommune = getListNomenclature(ConstanteUtils.PEGASE_URI_REF_COMMUNE, Commune.class);
		return listCommune.stream().map(e -> new SiScolComBdi(e.getCodeInsee(), e.getCodePostal(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolAnneeUni() */
	@Override
	public List<SiScolAnneeUni> getListSiScolAnneeUni() throws SiScolException {
		/* Creation du header et passage du token GWT */
		final HttpHeaders headers = createHttpHeaders();
		final HttpEntity<List<SiScolAnneeUni>> httpEntity = new HttpEntity<>(headers);

		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("codeStructureEtablissement", etablissement);

		final URI uri = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_MOF),
			ConstanteUtils.PEGASE_URI_MOF_PERIODE,
			params);

		logger.debug("Call ws pegase, URI = " + uri);

		final ResponseEntity<List<Periode>> response = wsPegaseRestTemplate.exchange(
			uri,
			HttpMethod.GET,
			httpEntity,
			new ParameterizedTypeReference<List<Periode>>() {
			});

		return response.getBody().stream().map(e -> new SiScolAnneeUni(e.getCode(), e.getLibelleLong(), e.getLibelleCourt(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getVersion() */
	@Override
	public Version getVersion() throws SiScolException {
		return new Version("1.0.0");
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolTypResultat() */
	@Override
	public List<SiScolTypResultat> getListSiScolTypResultat() throws SiScolException {
		final List<TypeResultat> listTypeResulat = getListNomenclature(ConstanteUtils.PEGASE_URI_REF_TYPE_RESULTAT, TypeResultat.class);
		return listTypeResulat.stream().map(e -> new SiScolTypResultat(e.getCode(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol())).collect(Collectors.toList());
	}

	@Override
	public WSIndividu getIndividu(final String codEtu, final String ine, final String cleIne) throws SiScolException {
		/* Creation du header et passage du token GWT */
		final HttpHeaders headers = createHttpHeaders();
		final HttpEntity<Apprenant> httpEntity = new HttpEntity<>(headers);

		URI uri = null;
		if (codEtu != null) {
			uri = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_INS),
				SiScolRestUtils.getSubService(ConstanteUtils.PEGASE_URI_INS_GESTION, ConstanteUtils.PEGASE_URI_INS_APPRENANT, etablissement, codEtu),
				null);
		} else if (ine != null && cleIne != null) {
			uri = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_INS),
				SiScolRestUtils.getSubService(ConstanteUtils.PEGASE_URI_INS_GESTION, ConstanteUtils.PEGASE_URI_INS_APPRENANT, etablissement, ConstanteUtils.PEGASE_URI_INS_APPRENANT_INE, ine + cleIne),
				null);
		}

		if (uri == null) {
			return null;
		}

		logger.debug("Call ws pegase, URI = " + uri);

		Apprenant app = null;
		try {
			final ResponseEntity<Apprenant> response = wsPegaseRestTemplate.exchange(
				uri,
				HttpMethod.GET,
				httpEntity,
				Apprenant.class);

			app = response.getBody();
		} catch (final HttpClientErrorException e) {
			if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				return null;
			}
			throw e;
		}

		if (app == null || app.getEtatCivil() == null || app.getNaissance() == null) {
			return null;
		}

		/* Récupération commune de naissance */
		String libCommuneNaissance = null;
		if (app.getNaissance().getCommuneDeNaissance() != null) {
			final SiScolCommune comm = tableRefController.getCommuneByCode(app.getNaissance().getCommuneDeNaissance());
			if (comm == null) {
				logger.warn("Commune absente : " + app.getNaissance().getCommuneDeNaissance());
			} else {
				libCommuneNaissance = comm.getLibCom();
			}
		} else if (app.getNaissance().getCommuneDeNaissanceEtranger() != null) {
			libCommuneNaissance = app.getNaissance().getCommuneDeNaissanceEtranger();
		}

		/* Creation de l'individu */
		final WSIndividu individu = new WSIndividu(app.getCode(),
			app.getEtatCivil().getGenre(),
			app.getNaissance().getDateDeNaissance(),
			app.getEtatCivil().getNomDeNaissance(),
			app.getEtatCivil().getNomUsuel(),
			app.getEtatCivil().getPrenom(),
			app.getEtatCivil().getDeuxiemePrenom(),
			app.getEtatCivil().getTroisiemePrenom(),
			libCommuneNaissance,
			app.getNaissance().getPaysDeNaissance(),
			app.getNaissance().getNationalite());

		/* Recuperation du bac */
		if (app.getBac() != null) {
			final WSBac bac = new WSBac();
			bac.setCodBac(app.getBac().getSerie());
			bac.setCodPays(app.getBac().getPays());
			bac.setDaaObtBacIba(app.getBac().getAnneeObtention());
			bac.setCodMnb(app.getBac().getMention());
			bac.setCodSpe1Bac(app.getBac().getPremiereSpecialiteBac());
			bac.setCodSpe2Bac(app.getBac().getDeuxiemeSpecialiteBac());
			individu.setBac(bac);
			if (app.getBac().getIne() != null) {
				individu.setCodNneInd(MethodUtils.getIne(app.getBac().getIne()));
				individu.setCodCleNneInd(MethodUtils.getCleIne(app.getBac().getIne()));
			}
			if (app.getBac().getEtablissement() != null) {
				final SiScolEtablissementPK pkEtab = new SiScolEtablissementPK();
				pkEtab.setCodEtb(app.getBac().getEtablissement());
				pkEtab.setTypSiScol(getTypSiscol());

				final SiScolEtablissement etabO = siScolEtablissementRepository.findOne(pkEtab);
				if (etabO != null) {
					bac.setCodEtb(etabO.getId().getCodEtb());
					if (etabO.getSiScolDepartement() != null && etabO.getSiScolDepartement().getId() != null) {
						bac.setCodDep(etabO.getSiScolDepartement().getId().getCodDep());
					}
					if (etabO.getSiScolCommune() != null && etabO.getSiScolCommune().getId() != null) {
						bac.setCodCom(etabO.getSiScolCommune().getId().getCodCom());
					}
				}
			}

		}

		/* Recupération de l'adresse */
		final Optional<ApprenantContact> contactAdrO = app.getContacts().stream().filter(e -> e.getCanalCommunication().equals(ConstanteUtils.PEGASE_URI_INS_APPRENANT_CONTACT_ADR)).findFirst();
		if (contactAdrO.isPresent()) {
			final ApprenantContact contactAdr = contactAdrO.get();

			final Optional<ApprenantContact> contactTelO = app.getContacts().stream().filter(e -> e.getCanalCommunication().equals(ConstanteUtils.PEGASE_URI_INS_APPRENANT_CONTACT_TEL)).findFirst();

			final WSAdresse adresse = new WSAdresse();
			adresse.setCodAdr(contactAdr.getDemandeDeContact().getCode());
			adresse.setLibAd1(contactAdr.getLigne3OuVoie());
			adresse.setLibAd2(contactAdr.getLigne1OuEtage());
			adresse.setLibAd3(contactAdr.getLigne2OuBatiment());
			adresse.setLibAd4(contactAdr.getLigne4OuComplement());
			adresse.setLibAd5(contactAdr.getLigne5Etranger());
			adresse.setNumTel(contactTelO.isPresent() ? contactTelO.get().getTelephone() : null);
			adresse.setCodCom(contactAdr.getCommune());
			adresse.setCodBdi(contactAdr.getCodePostal());
			adresse.setCodPay(contactAdr.getPays());
			individu.setAdresse(adresse);
		}
		/* Code apprenant */
		final String codApp = app.getCode();
		final List<WSCursusInterne> listCursusInterne = new ArrayList<>();

		/* Recupération du cursus interne */

		/* D'abord on récupère ses inscriptions */
		final URI uriIns = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_INS_EXT),
			SiScolRestUtils.getSubService(ConstanteUtils.PEGASE_URI_INS_GESTION, ConstanteUtils.PEGASE_URI_INS_INSCRIPTION, etablissement, app.getCode()),
			null);
		logger.debug("Call ws pegase, URI = " + uriIns);
		wsPegaseRestTemplate.exchange(
			uriIns,
			HttpMethod.GET,
			httpEntity,
			Inscription.class).getBody().getInscriptions().forEach(ins -> {
				/* Pour chaque inscription on ajoute dans la liste et on consulte le module COC de publication */
				logger.debug("**Inscription** " + ins);
				listCursusInterne.add(new WSCursusInterne(ins.getCode(), ins.getLibelleCourtFormation() + "/" + ins.getLibelleCourt(), ins.getAnneeUniv(), null, null, null, null));
				final URI uriPubli = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_COC),
					SiScolRestUtils.getSubServiceWhithoutSlash(ConstanteUtils.PEGASE_URI_COC_ETABLISSEMENT,
						etablissement,
						ConstanteUtils.PEGASE_URI_COC_PER,
						ins.getCodePeriode(),
						ConstanteUtils.PEGASE_URI_COC_APP,
						codApp,
						ConstanteUtils.PEGASE_URI_COC_CHEM,
						ins.getCodeChemin()),
					null);
				logger.debug("Call ws pegase, URI = " + uriPubli);

				/* On tente de récupérer le cursus interne : try catch a cause erreur etudiant non trouvé */
				try {
					final Publication[] jsonObj = wsPegaseRestTemplate.exchange(
						uriPubli,
						HttpMethod.GET,
						httpEntity,
						Publication[].class).getBody();
					/* Pour chaque publication d'un niveau 3 dans l'arbre on ajoute dans la liste */
					for (final Publication pub : jsonObj) {
						if (pub.hasResults()) {
							logger.debug("**Publication** " + pub);
							listCursusInterne
								.add(new WSCursusInterne(pub.getCodeFeuille(), ins.getLibelleCourtFormation() + "/" + ins.getLibelleCourt() + "/" + pub.getLibCourtFeuille(), ins.getAnneeUniv(), null, pub.getCodRes(),
									pub.getNote(),
									pub.getBareme()));
						}
					}
				} catch (final Exception ex) {

				}

			});
		individu.setListCursusInterne(listCursusInterne);

		return individu;
	}

	@Override
	public List<FormationPegase> getListFormationPegase(final String searchCode, final String searchLib) throws SiScolException {
		/* Formations parentes */
		final List<FormationPegase> listeFormTmp = getListFormation(searchCode, searchLib, ConstanteUtils.PEGASE_URI_COF_STATUT_FORM);
		/* Formations enfants */
		listeFormTmp.addAll(getListFormation(searchCode, searchLib, ConstanteUtils.PEGASE_URI_COF_STATUT_FORM_PARENTE));

		/* Distinct sur les 2 listes */
		final Set<String> codeSet = new HashSet<>();
		final List<FormationPegase> listeForm = listeFormTmp
			.stream()
			.filter(e -> codeSet.add(e.getCode() + "_" + e.getLibelle()))
			.collect(Collectors.toList());

		/* Trie sur le code */
		listeForm.sort(Comparator.comparing(FormationPegase::getCode));
		return listeForm;
	}

	/**
	 * @param  searchCode
	 * @param  searchLib
	 * @param  keyParamFormation
	 * @return                   une liste de formation
	 * @throws SiScolException
	 */
	public List<FormationPegase> getListFormation(final String searchCode, final String searchLib, final String keyParamFormation) throws SiScolException {
		/* Creation du header et passage du token GWT */
		final HttpHeaders headers = createHttpHeaders();
		final HttpEntity<FormationPegase> httpEntity = new HttpEntity<>(headers);

		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		/* Ajout param taille */
		params.add("taille", "50");
		if (StringUtils.isNotBlank(searchCode)) {
			params.add("code", "*" + searchCode + "*");
		}
		if (StringUtils.isNotBlank(searchLib)) {
			params.add("libelle", "*" + searchLib + "*");
		}
		/* Si pas de recherche, on ne ramène rien */
		if (!params.containsKey("code") && !params.containsKey("libelle")) {
			return new ArrayList<>();
		}
		params.add(keyParamFormation, ConstanteUtils.PEGASE_URI_COF_STATUT_FORM_VAL);

		final URI uri = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_COF),
			SiScolRestUtils.getSubServiceWhithoutSlash(ConstanteUtils.PEGASE_URI_COF_ETABLISSEMENT, etablissement, ConstanteUtils.PEGASE_URI_COF_OBJ_MAQUETTE),
			params);

		logger.debug("Call ws pegase, service = " + ConstanteUtils.PEGASE_URI_MOF_FORMATION + ", URI = " + uri);

		final ResponseEntity<ObjetMaquettePagination> response = wsPegaseRestTemplate.exchange(
			uri,
			HttpMethod.GET,
			httpEntity,
			new ParameterizedTypeReference<ObjetMaquettePagination>() {
			});

		/* Distinct */
		final Set<String> codeSet = new HashSet<>();
		final List<FormationPegase> listeForm = response.getBody()
			.getItems()
			.stream()
			.filter(e -> codeSet.add(e.getCode()))
			.collect(Collectors.toList());

		/* On ajoute le type de diplome */
		listeForm.forEach(e -> {
			final SiScolTypDiplome typDip = tableRefController.getSiScolTypDiplomeByCode(e.getCodeTypeDiplome());
			e.setLibTypeDiplome(typDip != null ? typDip.getLibTpd() : null);
		});

		return listeForm;
	}

	@Override
	public Boolean hasSyncEtudiant() {
		return true;
	}

	@Override
	public Boolean hasSearchAnneeUni() {
		return true;
	}

	@Override
	public Boolean hasDepartementNaissance() {
		return false;
	}

	@Override
	public Boolean hasBacASable() {
		return true;
	}

	@Override
	public Boolean hasCge() {
		return false;
	}

	private String getFilePathOpi(final String file) {
		String delimitter = "/";
		if (opiPath.endsWith(delimitter)) {
			delimitter = "";
		}
		return opiPath + delimitter + file + "-" + formatterDateFile.format(LocalDate.now()) + OPI_FILE_EXT;
	}

	@Override
	public Integer launchBatchOpi(final List<Candidat> listeCandidat, final BatchHisto batchHisto) {
		if (opiPath == null) {
			return 0;
		}

		/* On parcourt la liste des candidats pour rechercher leurs OPI */
		Integer i = 0;
		Integer cpt = 0;

		/* Liste pour ecriture CSV */
		final List<OpiCandidat> opiCandidats = new ArrayList<>();
		final List<OpiVoeu> opiVoeux = new ArrayList<>();

		/* Liste pour enregistrer l'opi */
		final List<Opi> opiVoeuxToSave = new ArrayList<>();

		for (final Candidat candidat : listeCandidat) {

			/* Erreur à afficher dans les logs */
			final String logComp = " - candidat " + candidat.getCompteMinima().getNumDossierOpiCptMin();

			logger.debug("creation OPI pégase" + logComp);
			// Test que l'année d'obtention du bac est correcte.

			final CandidatBacOuEqu bacOuEqu = candidat.getCandidatBacOuEqu();

			/* Controle du bac */
			if (bacOuEqu != null && bacOuEqu.getAnneeObtBac() != null) {
				final int anneeObtBac = candidat.getCandidatBacOuEqu().getAnneeObtBac();
				final int anneeEnCours = (LocalDate.now()).getYear();
				if (anneeObtBac > anneeEnCours) {
					mailController.sendErrorToAdminFonctionnel("Erreur OPI, bac non conforme" + logComp,
						"Erreur OPI : bac non conforme, la date est supérieur à l'année courante" + logComp,
						logger);
					logger.debug("bac non conforme" + logComp);
					continue;
				}
			}

			/* Controle des valeurs obligatoires */
			if (candidat.getAdresse() == null) {
				continue;
			}

			// Voeux-->On cherche tout les voeux soumis à OPI-->Recherche des OPI du
			// candidat
			final List<Opi> listeOpi = opiController.getListOpiByCandidat(candidat, true);

			final List<OpiVoeu> opiVoeuxCandidat = new ArrayList<>();
			for (final Opi opi : listeOpi) {
				final OpiVoeu voeu = getVoeuByCandidature(candidat, opi.getCandidature());
				if (voeu != null) {
					opiVoeuxCandidat.add(voeu);

					/* Pour enregistrement OPI */
					opi.setDatPassageOpi(LocalDateTime.now());
					opi.setCodOpi(candidat.getCompteMinima().getNumDossierOpiCptMin());
					opiVoeuxToSave.add(opi);
				}
			}

			/* Vérification que le candidat possède des voeux, si c'est le cas on l'ajoute aux fichiers à exporter */
			if (opiVoeuxCandidat.size() > 0) {
				opiCandidats.add(new OpiCandidat(candidat, formatterDate));
				opiVoeux.addAll(opiVoeuxCandidat);
			} else {
				continue;
			}

			i++;
			cpt++;
			if (i.equals(ConstanteUtils.BATCH_LOG_NB_SHORT)) {
				batchController.addDescription(batchHisto, "Deversement de " + cpt + " OPI");
				i = 0;
			}
		}

		if (opiCandidats.size() == 0 || opiVoeux.size() == 0) {
			return 0;
		}

		/* Ecriture des fichiers */
		try {
			/* Fichier candidat */
			final OutputStreamWriter writerCandidat = new OutputStreamWriter(new FileOutputStream(getFilePathOpi(OPI_FILE_CANDIDAT)), StandardCharsets.UTF_8);
			final StatefulBeanToCsv<OpiCandidat> sbcCandidat = new StatefulBeanToCsvBuilder<OpiCandidat>(writerCandidat)
				.withSeparator(OPI_SEPARATOR)
				.withQuotechar(ICSVWriter.NO_QUOTE_CHARACTER)
				.withMappingStrategy(new PegaseMappingStrategy<>(OpiCandidat.class))
				.withOrderedResults(true)
				.build();

			sbcCandidat.write(opiCandidats);
			writerCandidat.close();

			/* Fichier candidatures */
			final OutputStreamWriter writerCandidature = new OutputStreamWriter(new FileOutputStream(getFilePathOpi(OPI_FILE_CANDIDATURE)), StandardCharsets.UTF_8);
			final StatefulBeanToCsv<OpiVoeu> sbcCandidature = new StatefulBeanToCsvBuilder<OpiVoeu>(writerCandidature)
				.withSeparator(OPI_SEPARATOR)
				.withQuotechar(ICSVWriter.NO_QUOTE_CHARACTER)
				.withMappingStrategy(new PegaseMappingStrategy<>(OpiVoeu.class))
				.withOrderedResults(true)
				.build();

			sbcCandidature.write(opiVoeux);
			writerCandidature.close();

			/* Enregistrement des opi */
			opiController.traiteListOpiCandidat(opiVoeuxToSave);

		} catch (final Exception e) {
			logger.error("Erreur OPI : Probleme d'insertion des voeux dans Pegase", e);
		}

		return cpt;
	}

	/**
	 * Transforme une candidature en voeuy OPI
	 * @param  candidat
	 * @param  candidature
	 * @param  prefixeOpi
	 * @return             transforme une candidature en voeu
	 */
	private OpiVoeu getVoeuByCandidature(final Candidat candidat, final Candidature candidature) {
		final Formation formation = candidature.getFormation();
		if (formation == null || formation.getCodPegaseForm() == null || !getTypSiscol().equals(formation.getTypSiScol())) {
			return null;
		}
		if (candidature.getTemAcceptCand() == null || !candidature.getTemAcceptCand()) {
			return null;
		}

		final OpiVoeu voeu = new OpiVoeu();
		voeu.setNumeroCandidat(candidat.getCompteMinima().getNumDossierOpiCptMin());
		voeu.setOrigine_admission(OPI_ORIGINE);
		voeu.setCodeVoeu(formation.getCodPegaseForm());
		voeu.setCodePeriode(candidat.getCompteMinima().getCampagne().getCodCamp());
		return voeu;
	}

	/**
	 * @return la liste des fichiers dans le repertoire d'opi
	 */
	private List<File> scanFilesOpi() {
		final File rootOpi = new File(opiPath);
		if (rootOpi.isDirectory()) {
			return Arrays.asList(rootOpi.listFiles());
		}
		return new ArrayList<>();
	}

	/**
	 * Retourne les fichiers d'OPI
	 */
	@Override
	public List<FileOpi> getFilesOpi() {
		try {
			final String libDownload = applicationContext.getMessage("btnDownload", null, UI.getCurrent().getLocale());
			final List<File> listeFiles = scanFilesOpi();
			final List<String> listJour = listeFiles.stream()
				.map(e -> e.getName().replaceAll(OPI_FILE_CANDIDATURE, "").replaceAll(OPI_FILE_CANDIDAT, "").replaceAll(OPI_FILE_EXT, "").replaceAll("-", ""))
				.distinct()
				.collect(Collectors.toList());

			return listJour.stream().map(e -> {
				final Optional<File> fileCandidat = listeFiles.stream().filter(f -> f.getName().equals(OPI_FILE_CANDIDAT + "-" + e + OPI_FILE_EXT)).findFirst();
				final Optional<File> fileVoeu = listeFiles.stream().filter(f -> f.getName().equals(OPI_FILE_CANDIDATURE + "-" + e + OPI_FILE_EXT)).findFirst();

				final FileOpi file = new FileOpi();
				file.setDate(LocalDate.parse(e, formatterDateFile));

				file.setLibFileCandidat(fileCandidat.map(f -> f.getName()).orElse(null));
				file.setLibFileVoeux(fileVoeu.map(f -> f.getName()).orElse(null));
				file.setLibFileBoth(fileVoeu.map(f -> e + OPI_FILE_EXT_ZIP).orElse(null));

				file.setLibButtonCandidat(fileCandidat.map(f -> libDownload + " " + f.getName()).orElse(null));
				file.setLibButtonVoeux(fileVoeu.map(f -> libDownload + " " + f.getName()).orElse(null));
				file.setLibButtonBoth(fileVoeu.map(f -> libDownload + " " + e + OPI_FILE_EXT_ZIP).orElse(null));

				file.setPathToCandidat(fileCandidat.map(f -> f.getPath()).orElse(null));
				file.setPathToVoeux(fileVoeu.map(f -> f.getPath()).orElse(null));
				return file;
			})
				.collect(Collectors.toList());

		} catch (final Exception e) {
			logger.error("Impossible de lire les fichiers OPI", e);
		}
		return new ArrayList<>();
	}

	/**
	 * Supprime les fichiers d'OPI
	 */
	@Override
	public void deleteFileOpi(final List<FileOpi> listFileOpi) {
		try {
			listFileOpi.stream()
				.forEach(fileOpi -> {
					/* Suppression fichier candidat */
					if (fileOpi.getPathToCandidat() != null) {
						final File fileCandidat = new File(fileOpi.getPathToCandidat());
						if (fileCandidat != null) {
							fileCandidat.delete();
						}
					}
					/* Suppression fichier voeux */
					if (fileOpi.getPathToVoeux() != null) {
						final File fileVoeux = new File(fileOpi.getPathToVoeux());
						if (fileVoeux != null) {
							fileVoeux.delete();
						}
					}

				});
		} catch (final Exception e) {

		}
	}

	@Override
	public List<SiScolOptionBac> getListSiScolOptionBac() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SiScolSpecialiteBac> getListSiScolSpecialiteBac() throws SiScolException {
		final List<SpecialiteBacGeneral> listSerie = getListNomenclature(ConstanteUtils.PEGASE_URI_REF_SPECIALITE_BAC, SpecialiteBacGeneral.class);
		return listSerie.stream().map(e -> new SiScolSpecialiteBac(e.getCode(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), e.getDateDebutValidite(), e.getDateFinValidite(), getTypSiscol()))
			.collect(Collectors.toList());
	}

	@Override
	public List<SiScolBacOptBac> getListSiScolBacOptBac() throws SiScolException {
		return null;
	}

	@Override
	public List<SiScolBacSpeBac> getListSiScolBacSpeBac() throws SiScolException {
		return null;
	}

	@Override
	public Boolean hasSpecialiteRequired() {
		return false;
	}

	@Override
	public String checkBacSpecialiteOption(final CandidatBacOuEqu bac) {
		return null;
	}

	@Override
	public List<SiScolCatExoExt> getListSiScolCatExoExt() throws SiScolException {
		return null;
	}

	@Override
	public List<SiScolRegime> getListRegime() throws SiScolException {
		return null;
	}

	@Override
	public List<SiScolStatut> getListStatut() throws SiScolException {
		return null;
	}

	@Override
	public int getSizeFieldAdresse() {
		return ConstanteUtils.SIZE_FIELD_ADRESSE_PEGASE;
	}

	@Override
	public Boolean testAuthApiPegase(final ConfigPegaseAuth configPegaseAuth) {
		try {
			final String jwtToken = getNewJwtToken(configPegaseAuth.getUser(), configPegaseAuth.getPwd(), configPegaseAuth.getUrl());
			if (jwtToken == null) {
				throw new SiScolException("JWT token null, vérifiéez vos paramètres");
			}
			if (jwtToken.length() > 0) {
				Notification.show(applicationContext.getMessage("config.pegaseAuth.test.success", null, UI.getCurrent().getLocale()), Type.TRAY_NOTIFICATION);
			}
			return jwtToken.length() > 0;
		} catch (final Exception e) {
			UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("config.pegaseAuth.test.result", null, UI.getCurrent().getLocale()), e.toString(), 500, 70));
			logger.error(applicationContext.getMessage("config.pegaseAuth.erreur", null, UI.getCurrent().getLocale()), e);
			return false;
		}
	}

	@Override
	public Boolean testUrlApiPegase(final ConfigPegaseAuth configPegaseAuth, final ConfigPegaseUrl configPegaseUrl) {
		final StringBuilder ret = new StringBuilder();
		try {
			final String jwtToken = getNewJwtToken(configPegaseAuth.getUser(), configPegaseAuth.getPwd(), configPegaseAuth.getUrl());

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + jwtToken);

			final HttpEntity<String> httpEntity = new HttpEntity<>(headers);
			final URI uri = SiScolRestUtils.getURIForService(configPegaseUrl.getRef(), ConstanteUtils.PEGASE_URI_REF_NOMENCLATURE_DISPO, new LinkedMultiValueMap<>());
			ret.append("<u>" + applicationContext.getMessage("config.pegaseUrl.testRef.result", null, UI.getCurrent().getLocale()) + "</u><br/>");
			final ResponseEntity<List<NomenclatureDispo>> responseRef = wsPegaseRestTemplate.exchange(
				uri,
				HttpMethod.GET,
				httpEntity,
				new ParameterizedTypeReference<List<NomenclatureDispo>>() {
				});
			ret.append(applicationContext.getMessage("config.pegaseUrl.testRef.resultDetail", new Object[] { responseRef.getBody().size() }, UI.getCurrent().getLocale()));
			UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("config.pegaseUrl.test.result", null, UI.getCurrent().getLocale()), ret.toString(), 500, 70));
			return true;
		} catch (final Exception ex) {
			ret.append(ex.toString());
			UI.getCurrent().addWindow(new InfoWindow(applicationContext.getMessage("config.pegaseUrl.test.result", null, UI.getCurrent().getLocale()), ret.toString(), 500, 70));
			logger.error(applicationContext.getMessage("config.pegaseUrl.erreur", null, UI.getCurrent().getLocale()), ex);
		}
		return false;
	}

}
