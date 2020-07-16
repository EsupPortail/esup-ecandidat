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
import java.util.List;
import java.util.stream.Collectors;

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
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import fr.univlorraine.ecandidat.entities.ecandidat.SiScolAnneeUni;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolBacOuxEqu;
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
import fr.univlorraine.ecandidat.entities.siscol.pegase.Departement;
import fr.univlorraine.ecandidat.entities.siscol.pegase.NomenclaturePagination;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;

/**
 * Gestion du SI Scol pégase
 * @author Kevin Hergalant
 */
@Component(value = "siScolPegaseWSServiceImpl")
@SuppressWarnings("serial")
public class SiScolPegaseWSServiceImpl implements SiScolGenericService, Serializable {

	private final Logger logger = LoggerFactory.getLogger(SiScolPegaseWSServiceImpl.class);

	@Value("${ws.pegase.urlauth:}")
	private transient String urlauth;

	@Value("${ws.pegase.username:}")
	private transient String username;

	@Value("${ws.pegase.password:}")
	private transient String password;

	@Value("${ws.pegase.urlref:}")
	private transient String urlRef;

	private final RestTemplate wsPegaseRestTemplate = new RestTemplate();

	@Override
	public String getTypSiscol() {
		return ConstanteUtils.SISCOL_TYP_PEGASE;
	}

	@Override
	public String getCodPaysFrance() {
		return ConstanteUtils.PAYS_CODE_FRANCE_PEGASE;
	}

	private String getGwtToken() throws SiScolException {
		try {
			final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("username", username);
			params.add("password", password);
			params.add("token", "true");

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			final URI uri = SiScolRestUtils.getURIForService(urlauth, null, params);
			final ResponseEntity<String> response = wsPegaseRestTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(headers), String.class);
			return response.getBody();
		} catch (final Exception e) {
			throw new SiScolException(e);
		}
	}

	/**
	 * @param  service
	 * @return         l'uri d'un service referentiel
	 */
	private String getUriNomenclature(final String service) {
		return ConstanteUtils.PEGASE_URI_NOMENCLATURE + "/" + service;
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
		return getListNomenclature(service, ConstanteUtils.PEGASE_LIMIT_DEFAULT, className);
	}

	/**
	 * Execute un appel au WS Pegase pour récupérer une liste d'entité
	 * @param  <T>             le type de la nomenclature
	 * @param  service         le service à appeler
	 * @param  limit           le limit
	 * @param  className       la class
	 * @return                 une liste d'entité
	 * @throws SiScolException
	 */
	private <T> List<T> getListNomenclature(final String service, final Long limit, final Class<T> className) throws SiScolException {
		try {
			/* Liste a retourner */
			final List<T> listToRetrun = new ArrayList<>();

			/* Creation du header et passage du token GWT */
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + getGwtToken());

			/* Construction de la requete */
			final ResolvableType resolvableType = ResolvableType.forClassWithGenerics(NomenclaturePagination.class, className);
			final ParameterizedTypeReference<NomenclaturePagination<T>> typeRef = ParameterizedTypeReference.forType(resolvableType.getType());
			final HttpEntity<NomenclaturePagination<T>> httpEntity = new HttpEntity<>(headers);

			/* Permet de gérer la pagination */
			Long currentPage = 0L;
			Long nbPage = 1L;

			/* Execution des requetes paginées */
			while (currentPage < nbPage) {
				final URI uri = SiScolRestUtils.getURIForService(urlRef, service, currentPage, limit, null);
				logger.debug("Call ws pegase, url = " + urlRef + ", service = " + service + ", URI = " + uri);

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

		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCentreGestion() */
	@Override
	public List<SiScolCentreGestion> getListSiScolCentreGestion() throws SiScolException {
		final String token = getGwtToken();
		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolCommune() */
	@Override
	public List<SiScolCommune> getListSiScolCommune() throws SiScolException {
		final String token = getGwtToken();
		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDepartement() */
	@Override
	public List<SiScolDepartement> getListSiScolDepartement() throws SiScolException {
		final List<Departement> listDpt = getListNomenclature(getUriNomenclature(ConstanteUtils.PEGASE_URI_NOMENCLATURE_DEPARTEMENT), Departement.class);
		return listDpt.stream().map(e -> new SiScolDepartement(e.getCode(), e.getLibelleLong(), e.getLibelleAffichage(), e.getTemoinVisible(), getTypSiscol())).collect(Collectors.toList());
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolDipAutCur() */
	@Override
	public List<SiScolDipAutCur> getListSiScolDipAutCur() throws SiScolException {
		final String token = getGwtToken();
		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolEtablissement() */
	@Override
	public List<SiScolEtablissement> getListSiScolEtablissement() throws SiScolException {
		final String token = getGwtToken();
		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolMention() */
	@Override
	public List<SiScolMention> getListSiScolMention() throws SiScolException {
		final String token = getGwtToken();
		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolMentionNivBac() */
	@Override
	public List<SiScolMentionNivBac> getListSiScolMentionNivBac() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolPays() */
	@Override
	public List<SiScolPays> getListSiScolPays() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolTypDiplome() */
	@Override
	public List<SiScolTypDiplome> getListSiScolTypDiplome() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	/** @see fr.univlorraine.ecandidat.services.siscol.SiScolGenericService#getListSiScolAnneeUni() */
	@Override
	public List<SiScolAnneeUni> getListSiScolAnneeUni() throws SiScolException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public List<SiScolCatExoExt> getListCatExoExt() throws SiScolException {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
