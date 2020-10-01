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

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCentreGestion;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolComBdi;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommune;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolCommunePK;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDepartement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolDipAutCur;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissement;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolEtablissementPK;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMention;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolMentionNivBac;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolPays;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypDiplome;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolTypResultat;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolUtilisateur;
import fr.univlorraine.ecandidat.entities.ecandidat.Version;
import fr.univlorraine.ecandidat.entities.siscol.WSAdresse;
import fr.univlorraine.ecandidat.entities.siscol.WSBac;
import fr.univlorraine.ecandidat.entities.siscol.WSIndividu;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Apprenant;
import fr.univlorraine.ecandidat.entities.siscol.pegase.ApprenantContact;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Commune;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Departement;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Etablissement;
import fr.univlorraine.ecandidat.entities.siscol.pegase.MentionBac;
import fr.univlorraine.ecandidat.entities.siscol.pegase.MentionHonorifique;
import fr.univlorraine.ecandidat.entities.siscol.pegase.NomenclaturePagination;
import fr.univlorraine.ecandidat.entities.siscol.pegase.PaysNationalite;
import fr.univlorraine.ecandidat.entities.siscol.pegase.Periode;
import fr.univlorraine.ecandidat.entities.siscol.pegase.SerieBac;
import fr.univlorraine.ecandidat.entities.siscol.pegase.TypeDiplome;
import fr.univlorraine.ecandidat.entities.siscol.pegase.TypeResultat;
import fr.univlorraine.ecandidat.repositories.SiScolCommuneRepository;
import fr.univlorraine.ecandidat.repositories.SiScolEtablissementRepository;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.MethodUtils;

/**
 * Gestion du SI Scol pégase
 * @author Kevin Hergalant
 */
@Component(value = "siScolPegaseWSServiceImpl")
@SuppressWarnings("serial")
public class SiScolPegaseWSServiceImpl implements SiScolGenericService, Serializable {

	private final Logger logger = LoggerFactory.getLogger(SiScolPegaseWSServiceImpl.class);

	private final static String PROPERTY_FILE = "configUrlServicesPegase.properties";
	private final Properties properties = new Properties();

	/** TODO à supprimer */
	@Resource
	private transient SiScolCommuneRepository siScolCommuneRepository;
	@Resource
	private transient SiScolEtablissementRepository siScolEtablissementRepository;

	@Resource
	private transient RestTemplate wsPegaseRestTemplate;
	private final RestTemplate wsPegaseJWTRestTemplate = new RestTemplate();

	@Value("${ws.pegase.username:}")
	private transient String username;

	@Value("${ws.pegase.password:}")
	private transient String password;

	@Value("${ws.pegase.etablissement:}")
	private transient String etablissement;

	private String gwtToken = null;

	@Override
	public String getTypSiscol() {
		return ConstanteUtils.SISCOL_TYP_PEGASE;
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
	 * Demande d'un nouveau token toutes les heures
	 * @return                 le token
	 * @throws SiScolException
	 */
	@Scheduled(fixedRate = 60 * 60 * 1000)
	private String askNewGwtToken() throws SiScolException {
		logger.debug("Demande d'un nouveau jeton JWT");
		try {
			final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("username", username);
			params.add("password", password);
			params.add("token", "true");

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			final URI uri = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_AUTH), ConstanteUtils.PEGASE_SUFFIXE_AUTH, null, params);
			final ResponseEntity<String> response = wsPegaseJWTRestTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(headers), String.class);
			return response.getBody();
		} catch (final Exception e) {
			throw new SiScolException(e);
		}
	}

	/**
	 * @return                 le token JWT
	 * @throws SiScolException
	 */
	private synchronized String getJwtToken() throws SiScolException {
		if (gwtToken == null) {
			gwtToken = askNewGwtToken();
		}
		return gwtToken;
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
		return getListRef(ConstanteUtils.PEGASE_URI_NOMENCLATURE + "/" + service, ConstanteUtils.PEGASE_LIMIT_DEFAULT, className);
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
				final URI uri = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_REF), ConstanteUtils.PEGASE_SUFFIXE_REF, service, currentPage, limit, null);
				logger.debug("Call ws pegase, nbPage = " + nbPage + ", service = " + service + ", URI = " + uri);

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
			e.printStackTrace();
			throw new SiScolException("SiScol call ws error on execute call list entity", e.getCause());
		}
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolBacOuxEqu() */
	@Override
	public List<SiScolBacOuxEqu> getListSiScolBacOuxEqu() throws SiScolException {
		final List<SerieBac> listSerie = getListNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_SERIE_BAC, SerieBac.class);
		return listSerie.stream().map(e -> new SiScolBacOuxEqu(e.getCode(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), e.getDateDebutValidite(), e.getDateFinValidite(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCentreGestion() */
	@Override
	public List<SiScolCentreGestion> getListSiScolCentreGestion() throws SiScolException {
		/** TODO? */
		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCommune() */
	@Override
	public List<SiScolCommune> getListSiScolCommune() throws SiScolException {
		final List<Commune> listCommune = getListNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_COMMUNE, Commune.class);
		/* On passe dans une map car on a des commune avec des bdi différents, distinct sur code insee */
		final Map<String, SiScolCommune> mapDistinct = new HashMap<>();
		listCommune.stream().filter(e -> e.getCodeInseeAncien() != null).forEach(e -> mapDistinct.put(e.getCodeInseeAncien(), new SiScolCommune(e.getCodeInseeAncien(), e.getLibelleAffichage(), false, getTypSiscol())));
		listCommune.stream().filter(e -> e.getCodeInsee() != null).forEach(e -> mapDistinct.put(e.getCodeInsee(), new SiScolCommune(e.getCodeInsee(), e.getLibelleAffichage(), e.getTemoinVisible(), getTypSiscol())));

		return mapDistinct.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDepartement() */
	@Override
	public List<SiScolDepartement> getListSiScolDepartement() throws SiScolException {
		final List<Departement> listDpt = getListNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_DEPARTEMENT, Departement.class);
		return listDpt.stream().map(e -> new SiScolDepartement(e.getCode(), e.getLibelleLong(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDipAutCur() */
	@Override
	public List<SiScolDipAutCur> getListSiScolDipAutCur() throws SiScolException {
		final List<TypeDiplome> listTypeDiplome = getListNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_TYPE_DIPLOME, TypeDiplome.class);
		return listTypeDiplome.stream().map(e -> new SiScolDipAutCur(e.getCode(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolEtablissement() */
	@Override
	public List<SiScolEtablissement> getListSiScolEtablissement() throws SiScolException {
		final List<SiScolEtablissement> listEtab = new ArrayList<>();
		getListNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_ETAB, Etablissement.class).forEach(e -> {
			if (e.getDepartement() != null && e.getCommune() != null && e.getPatronymeUai() != null && e.getLibelleAffichage() != null) {
				final SiScolEtablissement etab = new SiScolEtablissement(e.getNumeroUai(), e.getTypeUai().getTypeUai(), e.getPatronymeUai(), e.getLibelleAffichage(), e.getPatronymeUai(), e.getTemoinVisible(), getTypSiscol());
				etab.setSiScolDepartement(new SiScolDepartement(e.getDepartement().getCode(), getTypSiscol()));
				String codComm = e.getCommune();
				if (codComm.length() == 4) {
					codComm = "0" + codComm;
				}

				/** TODO à supprimer quand commune OK */
				final SiScolCommunePK pk = new SiScolCommunePK(codComm, getTypSiscol());
				final SiScolCommune comm = siScolCommuneRepository.findOne(pk);
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
		final List<MentionHonorifique> listMention = getListNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_MENTION, MentionHonorifique.class);
		return listMention.stream().map(e -> new SiScolMention(e.getCode(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolMentionNivBac() */
	@Override
	public List<SiScolMentionNivBac> getListSiScolMentionNivBac() throws SiScolException {
		final List<MentionBac> listTypeDiplome = getListNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_MENTION_BAC, MentionBac.class);
		return listTypeDiplome.stream().map(e -> new SiScolMentionNivBac(e.getCode(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolPays() */
	@Override
	public List<SiScolPays> getListSiScolPays() throws SiScolException {
		final List<PaysNationalite> listPaysNationalite = getListNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_PAYS_NAT, PaysNationalite.class);
		return listPaysNationalite.stream()
			.map(e -> new SiScolPays(e.getCode(), e.getLibelleNationalite() != null ? e.getLibelleNationalite() : e.getLibelleCourt(), e.getLibelleAffichage(), e.getLibelleCourt(), e.getTemoinVisible(), getTypSiscol()))
			.collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolTypDiplome() */
	@Override
	public List<SiScolTypDiplome> getListSiScolTypDiplome() throws SiScolException {
		final List<TypeDiplome> listTypeDiplome = getListNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_TYPE_DIPLOME, TypeDiplome.class);
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
		final List<Commune> listCommune = getListNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_COMMUNE, Commune.class);
		return listCommune.stream().map(e -> new SiScolComBdi(e.getCodeInsee(), e.getCodePostal(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolAnneeUni() */
	@Override
	public List<SiScolAnneeUni> getListSiScolAnneeUni() throws SiScolException {
		/* Creation du header et passage du token GWT */
		final HttpHeaders headers = createHttpHeaders();
		final HttpEntity<Apprenant> httpEntity = new HttpEntity<>(headers);

		final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("codeStructureEtablissement", etablissement);

		final URI uri = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_MOF),
			ConstanteUtils.PEGASE_SUFFIXE_MOF,
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
		// TODO Auto-generated method stub
		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolTypResultat() */
	@Override
	public List<SiScolTypResultat> getListSiScolTypResultat() throws SiScolException {
		final List<TypeResultat> listTypeResulat = getListNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_TYPE_RESULTAT, TypeResultat.class);
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
				ConstanteUtils.PEGASE_SUFFIXE_INS,
				SiScolRestUtils.getSubService(ConstanteUtils.PEGASE_URI_INS_GESTION, ConstanteUtils.PEGASE_URI_INS_APPRENANT, etablissement, codEtu),
				null);
		} else if (ine != null && cleIne != null) {
			uri = SiScolRestUtils.getURIForService(getPropertyVal(ConstanteUtils.PEGASE_URL_INS),
				ConstanteUtils.PEGASE_SUFFIXE_INS,
				SiScolRestUtils.getSubService(ConstanteUtils.PEGASE_URI_INS_GESTION, ConstanteUtils.PEGASE_URI_INS_APPRENANT, etablissement, ConstanteUtils.PEGASE_URI_INS_APPRENANT_INE, ine + cleIne),
				null);
		}

		if (uri == null) {
			return null;
		}

		logger.debug("Call ws pegase, URI = " + uri);

		final ResponseEntity<Apprenant> response = wsPegaseRestTemplate.exchange(
			uri,
			HttpMethod.GET,
			httpEntity,
			Apprenant.class);

		final Apprenant app = response.getBody();
		if (app == null || app.getEtatCivil() == null || app.getNaissance() == null) {
			return null;
		}

		final WSIndividu individu = new WSIndividu(app.getCode(),
			app.getEtatCivil().getGenre(),
			app.getNaissance().getDateDeNaissance(),
			app.getEtatCivil().getNomDeNaissance(),
			app.getEtatCivil().getNomUsuel(),
			app.getEtatCivil().getPrenom(),
			app.getEtatCivil().getDeuxiemePrenom(),
			app.getEtatCivil().getTroisiemePrenom(),
			app.getNaissance().getCommuneDeNaissance() != null ? app.getNaissance().getCommuneDeNaissance() : app.getNaissance().getCommuneDeNaissanceEtranger(),
			app.getNaissance().getPaysDeNaissance(),
			app.getNaissance().getNationalite());

		/* Recuperation du bac */
		if (app.getBac() != null) {
			final WSBac bac = new WSBac();
			bac.setCodBac(app.getBac().getSerie());
			bac.setCodPays(app.getBac().getPays());
			bac.setDaaObtBacIba(app.getBac().getAnneeObtention());
			bac.setCodMnb(app.getBac().getMention());
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
					bac.setCodDep(etabO.getSiScolDepartement().getId().getCodDep());
					bac.setCodCom(etabO.getSiScolCommune().getId().getCodCom());
				}

			}
		}

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

		return individu;
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
	public Boolean hasSearchFormation() {
		return true;
	}

//	@Override
//	public List<SiScolCatExoExt> getListCatExoExt() throws SiScolException {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
