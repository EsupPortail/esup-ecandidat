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

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import fr.univlorraine.ecandidat.entities.ecandidat.BatchHisto;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.entities.ecandidat.SiScolUtilisateur;
import fr.univlorraine.ecandidat.repositories.IndividuRepository;
import fr.univlorraine.ecandidat.repositories.SiScolUtilisateurRepository;
import fr.univlorraine.ecandidat.services.ldap.PeopleLdap;
import fr.univlorraine.ecandidat.services.siscol.SiScolGenericService;
import fr.univlorraine.ecandidat.utils.CustomException;

/**
 * Gestion des individus
 * @author Kevin Hergalant
 */
@Component
public class IndividuController {

	/**
	 * Logger
	 */
	private final Logger logger = LoggerFactory.getLogger(IndividuController.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;

	@Resource
	private transient LockController lockController;

	@Resource
	private transient LdapController ldapController;

	@Resource
	private transient BatchController batchController;

	@Resource
	private transient IndividuRepository individuRepository;

	@Resource
	private transient SiScolUtilisateurRepository siScolUtilisateurRepository;

	/* Le service SI Scol */
	@Resource(name = "${siscol.implementation}")
	private SiScolGenericService siScolService;

	/**
	 * Enregistre un individu
	 * @param  individu
	 * @return          l'individu
	 */
	public Individu saveIndividu(final Individu individu) {
		final Individu ind = individuRepository.findOne(individu.getLoginInd());
		if (ind == null) {
			return individuRepository.save(individu);
		} else {
			ind.setLibelleInd(individu.getLibelleInd());
			ind.setMailInd(individu.getMailInd());
			return individuRepository.save(ind);
		}
	}

	/**
	 * @param  user
	 * @return      le libellé de l'individu
	 */
	public String getLibIndividu(final String user) {
		if (user == null) {
			return "";
		} else {
			final Individu ind = getIndividu(user);
			if (ind != null && ind.getLibelleInd() != null) {
				return ind.getLibelleInd();
			}
		}
		return user;
	}

	/**
	 * Retourne un individu
	 * @param  login
	 * @return       l'individu
	 */
	public Individu getIndividu(final String login) {
		return individuRepository.findOne(login);
	}

	/**
	 * Valide un bean d'individu
	 * @param  ind
	 * @throws CustomException
	 */
	public void validateIndividuBean(final Individu ind, final Locale locale) throws CustomException {
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();
		final Set<ConstraintViolation<Individu>> constraintViolations = validator.validate(ind);
		if (constraintViolations != null && constraintViolations.size() > 0) {
			String erreur = "";
			for (final ConstraintViolation<?> violation : constraintViolations) {
				erreur += (" *** " + violation.getPropertyPath().toString() + " : " + violation.getMessage());
			}
			throw new CustomException(applicationContext.getMessage("droitprofil.individu.error", null, locale) + " : " + erreur);
		}
	}

	/**
	 * Supprime un individu
	 * @param individu
	 */
	public void deleteIndividu(final Individu individu) {
		individuRepository.delete(individu);
	}

	/**
	 * @param  gest
	 * @param  user
	 * @return      le code CGE d'un gestionnaire
	 */
	public String getCodCgeForGestionnaire(final Gestionnaire gest, final String user) {
		if (gest != null && user != null) {
			if (gest.getSiScolCentreGestion() != null) {
				return gest.getSiScolCentreGestion().getId().getCodCge();
			}
			if (gest.getLoginApoGest() != null && !gest.getLoginApoGest().equals("")) {
				return getCodCgeUserByLogin(gest.getLoginApoGest());
			}
			return getCodCgeUserByLogin(user);
		}
		return null;
	}

	/**
	 * Renvoi le cod cge pour un user
	 * @param  userName
	 * @return          le cod cge pour un user
	 */
	private String getCodCgeUserByLogin(final String userName) {
		final List<SiScolUtilisateur> listeUser = siScolUtilisateurRepository.findByTypSiScolAndCodUtiAndTemEnSveUtiAndSiScolCentreGestionIsNotNull(siScolService.getTypSiscol(), userName, true);
		if (listeUser.size() > 0) {
			final SiScolUtilisateur user = listeUser.get(0);
			if (user != null && user.getSiScolCentreGestion() != null) {
				return user.getSiScolCentreGestion().getId().getCodCge();
			}
		}
		return null;
	}

	/**
	 * Synchronise les informations des individus
	 * @param batchHisto
	 */
	public void syncGestionnaire(final BatchHisto batchHisto) {
		final AtomicInteger cptMaj = new AtomicInteger(0);
		final AtomicInteger cptTes = new AtomicInteger(0);
		try {
			individuRepository.findAll().forEach(individu -> {
				final String login = individu.getLoginInd();
				final PeopleLdap people = ldapController.findByPrimaryKeyWithException(login);
				try {
					/* People non trouvé --> tes à false */
					if (people == null) {
						if (individu.getTesInd()) {
							logger.debug("Desactivation de l'individu : " + login);
							individu.setTesInd(false);
							individuRepository.save(individu);
							cptTes.incrementAndGet();
						}
					} else {
						final Individu individuLdap = new Individu(people);
						/* Validation du bean */
						validateIndividuBean(individuLdap, Locale.FRANCE);
						/* Comparaison des données */
						if (!StringUtils.equals(individu.getLibelleInd(), individuLdap.getLibelleInd()) ||
							!StringUtils.equals(individu.getMailInd(), individuLdap.getMailInd())) {
							logger.debug("Mise a jour de l'individu : " + login);
							individu.setLibelleInd(individuLdap.getLibelleInd());
							individu.setMailInd(individuLdap.getMailInd());
							individu.setTesInd(true);

							individuRepository.save(individu);
							cptMaj.incrementAndGet();
						}
					}
				} catch (final CustomException exInd) {
					logger.warn("Impossible de synchroniser le user " + login, exInd);
				}
			});
		} catch (final Exception ex) {
			logger.error("ldap.search.error", ex);
		}

		batchController.addDescription(batchHisto, "Mise à jour de " + cptMaj.get() + " individu(s)");
		batchController.addDescription(batchHisto, "Desactivation de " + cptTes.get() + " individu(s)");
	}
}
