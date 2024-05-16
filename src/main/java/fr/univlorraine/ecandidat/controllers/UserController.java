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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Resource;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.intercept.aopalliance.MethodSecurityInterceptor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.util.MethodInvocationUtils;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import fr.univlorraine.ecandidat.MainUI;
import fr.univlorraine.ecandidat.entities.ecandidat.Candidat;
import fr.univlorraine.ecandidat.entities.ecandidat.CentreCandidature;
import fr.univlorraine.ecandidat.entities.ecandidat.Commission;
import fr.univlorraine.ecandidat.entities.ecandidat.CommissionMembre;
import fr.univlorraine.ecandidat.entities.ecandidat.CompteMinima;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilFonc;
import fr.univlorraine.ecandidat.entities.ecandidat.DroitProfilInd;
import fr.univlorraine.ecandidat.entities.ecandidat.Gestionnaire;
import fr.univlorraine.ecandidat.entities.ecandidat.Individu;
import fr.univlorraine.ecandidat.entities.ecandidat.PreferenceInd;
import fr.univlorraine.ecandidat.services.ldap.PeopleLdap;
import fr.univlorraine.ecandidat.services.security.PasswordHashService;
import fr.univlorraine.ecandidat.services.security.SecurityAuthenticationProvider;
import fr.univlorraine.ecandidat.services.security.SecurityCentreCandidature;
import fr.univlorraine.ecandidat.services.security.SecurityCommission;
import fr.univlorraine.ecandidat.services.security.SecurityCommissionFonc;
import fr.univlorraine.ecandidat.services.security.SecurityCtrCandFonc;
import fr.univlorraine.ecandidat.services.security.SecurityFonc.Droit;
import fr.univlorraine.ecandidat.services.security.SecurityUser;
import fr.univlorraine.ecandidat.services.security.SecurityUserCandidat;
import fr.univlorraine.ecandidat.services.security.SecurityUserGestionnaire;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.CustomException;
import fr.univlorraine.ecandidat.utils.MethodUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;

/**
 * Gestion de l'utilisateur
 * @author Kevin Hergalant
 */
@Component
public class UserController {

	// private Logger logger = LoggerFactory.getLogger(UserController.class);

	/* Injections */
	@Resource
	private transient ApplicationContext applicationContext;
	@Resource
	private transient UserDetailsService userDetailsService;
	@Resource
	private transient MethodSecurityInterceptor methodSecurityInterceptor;
	@Resource
	private transient SecurityAuthenticationProvider authenticationManagerCandidat;
	@Resource
	private transient LdapController ldapController;
	@Resource
	private transient UiController uiController;
	@Resource
	private transient CampagneController campagneController;
	@Resource
	private transient IndividuController individuController;
	@Resource
	private transient CandidatController candidatController;
	@Resource
	private transient DroitProfilController droitProfilController;
	@Resource
	private transient CentreCandidatureController centreCandidatureController;
	@Resource
	private transient CommissionController commissionController;
	@Resource
	private transient PreferenceController preferenceController;
	@Resource
	private transient ParametreController parametreController;
	@Resource
	private transient I18nController i18nController;
	@Resource
	private transient LoadBalancingController loadBalancingController;
	@Resource
	private transient TestController testController;

	/* Variable d'envirronement */
	@Value("${admin.technique:}")
	private String adminTechnique;

	/**
	 * Récupère le securityContext dans la session.
	 * @return securityContext associé à la session
	 */
	public SecurityContext getSecurityContextFromSession() {
		if (UI.getCurrent() != null && UI.getCurrent().getSession() != null && UI.getCurrent().getSession().getSession() != null) {
			return (SecurityContext) UI.getCurrent().getSession().getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		}
		return null;
	}

	/** @return l'authentification courante */
	public Authentication getCurrentAuthentication() {
		final SecurityContext securityContext = getSecurityContextFromSession();
		if (securityContext == null) {
			return null;
		} else {
			return securityContext.getAuthentication();
		}
	}

	/**
	 * @param  viewClass
	 * @return           true si l'utilisateur peut accéder à la vue
	 */
	public boolean canCurrentUserAccessView(final Class<? extends View> viewClass, final Authentication auth) {
		if (auth == null) {
			return false;
		}
		final MethodInvocation methodInvocation = MethodInvocationUtils.createFromClass(viewClass, "enter");
		final Collection<ConfigAttribute> configAttributes = methodSecurityInterceptor.obtainSecurityMetadataSource().getAttributes(methodInvocation);
		/* Renvoie true si la vue n'est pas sécurisée */
		if (configAttributes.isEmpty()) {
			return true;
		}
		/* Vérifie que l'utilisateur a les droits requis */
		try {
			methodSecurityInterceptor.getAccessDecisionManager().decide(auth, methodInvocation, configAttributes);
		} catch (InsufficientAuthenticationException | AccessDeniedException e) {
			return false;
		}
		return true;
	}

	/** @return user utilisateur courant */
	public UserDetails getCurrentUser() {
		return getCurrentUser(getCurrentAuthentication());
	}

	/** @return user utilisateur courant */
	public UserDetails getCurrentUser(final Authentication auth) {
		if (isAnonymous(auth)) {
			return null;
		}
		return (UserDetails) auth.getPrincipal();
	}

	/** @return login de l'utilisateur courant */
	public String getCurrentUserLogin(final Authentication auth) {
		if (isAnonymous(auth)) {
			return null;
		}
		return auth.getName();
	}

	/** @return login de l'utilisateur courant */
	public String getCurrentUserLogin() {
		return getCurrentUserLogin(getCurrentAuthentication());
	}

	/** @return no dossier du candidat */
	public String getCurrentNoDossierCptMinOrLogin() {
		return getCurrentNoDossierCptMinOrLogin(getCurrentAuthentication());
	}

	/** @return no dossier du candidat */
	public String getCurrentNoDossierCptMinOrLogin(final Authentication auth) {
		final UserDetails details = getCurrentUser(auth);
		if (details instanceof SecurityUserCandidat) {
			final String noDossier = ((SecurityUserCandidat) details).getNoDossierOPI();
			if (noDossier != null && !noDossier.equals("")) {
				return noDossier;
			}
		}
		return getCurrentUserLogin(auth);
	}

	/** @return username de l'utilisateur courant */
	public String getCurrentUserName() {
		return getCurrentUserName(getCurrentAuthentication());
	}

	/** @return username de l'utilisateur courant */
	public String getCurrentUserName(final Authentication auth) {
		if (isAnonymous(auth)) {
			return applicationContext.getMessage("user.notconnected", null, UI.getCurrent().getLocale());
		} else {
			final UserDetails details = getCurrentUser(auth);
			if (details instanceof SecurityUser) {
				return ((SecurityUser) details).getDisplayName();
			}
		}
		return auth.getName();
	}

	/**
	 * Verifie si le user est anonymous
	 * @return true si le user est anonymous
	 */
	public Boolean isAnonymous(final Authentication auth) {
		if (auth == null) {
			return true;
		}
		return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(Predicate.isEqual(ConstanteUtils.ROLE_ANONYMOUS)).findAny().isPresent();
	}

	/**
	 * Verifie si le user est admin
	 * @return true si le user est admin
	 */
	public Boolean isAdmin() {
		final Authentication auth = getCurrentAuthentication();
		if (auth == null) {
			return false;
		}
		return auth.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.filter(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN).or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN_TECH)))
			.findAny()
			.isPresent();
	}

	/**
	 * Verifie si le user est admin
	 * @return true si le user est admin
	 */
	public Boolean isAdmin(final Authentication auth) {
		if (auth == null) {
			return false;
		}
		return auth.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.filter(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN).or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN_TECH)))
			.findAny()
			.isPresent();
	}

	/**
	 * Verifie si le user est un candidat
	 * @return true si le user est candidat
	 */
	public Boolean isCandidat() {
		return isCandidat(getCurrentAuthentication());
	}

	/**
	 * Verifie si le user est un candidat
	 * @param  auth
	 * @return      true si le user est candidat
	 */
	public Boolean isCandidat(final Authentication auth) {
		if (auth == null) {
			return false;
		}
		return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(Predicate.isEqual(ConstanteUtils.ROLE_CANDIDAT)).findAny().isPresent();
	}

	/** @return true si le user est scolCentrale */
	public Boolean isScolCentrale() {
		return isScolCentrale(getCurrentAuthentication());
	}

	/**
	 * Verifie si le user est scolCentrale
	 * @param  auth
	 * @return      true si le user est scolCentrale
	 */
	public Boolean isScolCentrale(final Authentication auth) {
		if (auth == null) {
			return false;
		}
		return auth.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.filter(Predicate.isEqual(ConstanteUtils.ROLE_SCOL_CENTRALE).or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN).or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN_TECH))))
			.findAny()
			.isPresent();
	}

	/**
	 * Verifie si le user est gestionnaire de candidat
	 * @return true si le user est gestionnaire
	 */
	public Boolean isGestionnaireCandidat(final Authentication auth) {
		if (auth == null) {
			return false;
		}
		if (isScolCentrale(auth)) {
			return true;
		}
		return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(Predicate.isEqual(ConstanteUtils.ROLE_GESTION_CANDIDAT)).findAny().isPresent();
	}

	/**
	 * Verifie si le user est gestionnaire de candidat
	 * @return true si le user est gestionnaire
	 */
	public Boolean isGestionnaireCandidatLS(final Authentication auth) {
		if (auth == null) {
			return false;
		}
		return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(Predicate.isEqual(ConstanteUtils.ROLE_GESTION_CANDIDAT_LS)).findAny().isPresent();
	}

	/**
	 * Verifie si le user est un personnel
	 * @return true si le user est un personnel
	 */
	public Boolean isPersonnel() {
		return isPersonnel(getCurrentAuthentication());
	}

	/**
	 * Verifie si le user est un personnel
	 * @return true si le user est un personnel
	 */
	public Boolean isPersonnel(final Authentication auth) {
		if (auth == null) {
			return false;
		}
		return auth.getAuthorities()
			.stream()
			.map(GrantedAuthority::getAuthority)
			.filter(Predicate.isEqual(ConstanteUtils.ROLE_SCOL_CENTRALE)
				.or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN)
					.or(Predicate.isEqual(ConstanteUtils.ROLE_ADMIN_TECH))
					.or(Predicate.isEqual(ConstanteUtils.ROLE_CENTRE_CANDIDATURE))
					.or(Predicate.isEqual(ConstanteUtils.ROLE_COMMISSION))
					.or(Predicate.isEqual(ConstanteUtils.ROLE_GESTION_CANDIDAT))
					.or(Predicate.isEqual(ConstanteUtils.ROLE_GESTION_CANDIDAT_LS))))
			.findAny()
			.isPresent();
	}

	/** @return true si l'utilisateur a pris le rôle d'un autre utilisateur */
	public boolean isUserSwitched() {
		final Authentication auth = getCurrentAuthentication();
		if (auth == null) {
			return false;
		}
		return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).filter(Predicate.isEqual(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR)).findAny().isPresent();
	}

	/** @return true si le candidat est valide, false sinon */
	public Boolean isCandidatValid() {
		return isCandidatValid(getCurrentAuthentication());
	}

	/** @return true si le candidat est valide, false sinon */
	public Boolean isCandidatValid(final Authentication auth) {
		final SecurityUserCandidat securityUserCand = getSecurityUserCandidat(auth);
		if (securityUserCand != null) {
			return securityUserCand.getCptMinValid() && securityUserCand.getMailValid();
		}
		return false;
	}

	/**
	 * Change le rôle de l'utilisateur courant
	 * @param username
	 *                    le nom de l'utilisateur a prendre
	 */
	public void switchToUser(final String username) {
		if (!isAdmin()) {
			return;
		}
		Assert.hasText(username, applicationContext.getMessage("assert.hasText", null, UI.getCurrent().getLocale()));

		/* Vérifie que l'utilisateur existe */
		try {
			final UserDetails details = userDetailsService.loadUserByUsername(username);
			if (details == null || details.getAuthorities() == null || details.getAuthorities().size() == 0) {
				Notification.show(applicationContext.getMessage("admin.switchUser.usernameNotFound", new Object[] { username }, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
				return;
			}
		} catch (final UsernameNotFoundException unfe) {
			Notification.show(applicationContext.getMessage("admin.switchUser.usernameNotFound", new Object[] { username }, UI.getCurrent().getLocale()), Notification.Type.WARNING_MESSAGE);
			return;
		}
		final String switchToUserUrl = MethodUtils.formatSecurityPath(loadBalancingController.getApplicationPath(false), ConstanteUtils.SECURITY_SWITCH_PATH) + "?"
			+ SwitchUserFilter.SPRING_SECURITY_SWITCH_USERNAME_KEY
			+ "="
			+ username;
		Page.getCurrent().open(switchToUserUrl, null);
	}

	/** Rétabli le rôle original de l'utilisateur */
	public void switchBackToPreviousUser() {
		Page.getCurrent().open(MethodUtils.formatSecurityPath(loadBalancingController.getApplicationPath(false), ConstanteUtils.SECURITY_SWITCH_BACK_PATH), null);
	}

	/** Dirige l'utilisateur vers la page amenant la connexion cas */
	public void connectCAS() {
		final String path = MethodUtils.formatSecurityPath(loadBalancingController.getApplicationPath(false), ConstanteUtils.SECURITY_CONNECT_PATH);
		disconnectUser();
		UI.getCurrent().getPage().setLocation(path);
	}

	/** Deconnect l'utilisateur */
	public void deconnect() {
		final String path = MethodUtils.formatSecurityPath(loadBalancingController.getApplicationPath(false), ConstanteUtils.SECURITY_LOGOUT_PATH);
		disconnectUser();
		UI.getCurrent().getPage().setLocation(path);
	}

	/** Nettoie la session */
	private void disconnectUser() {
		final SecurityContext context = SecurityContextHolder.createEmptyContext();
		SecurityContextHolder.setContext(context);
		UI.getCurrent().getSession().getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
		UI.getCurrent().getSession().close();
	}

	/**
	 * Connexion d'un candidat
	 * @param username
	 *                    login
	 * @param password
	 *                    mot de passe
	 */
	public void connectCandidatInterne(final String username, final String password) {
		if (loadBalancingController.isLoadBalancingGestionnaireMode()) {
			return;
		}
		final CompteMinima cptMin = candidatController.searchCptMinByNumDossier(username);
		if (cptMin != null) {
			if (!validPwdCandidat(password, cptMin)) {
				return;
			}
			if (!cptMin.getTemValidCptMin() || !cptMin.getTemValidMailCptMin()) {
				Notification.show(applicationContext.getMessage("compteMinima.connect.valid.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return;
			}
		} else {
			Notification.show(applicationContext.getMessage("compteMinima.connect.user.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return;
		}

		final SecurityUser user = constructSecurityUserCandidat(username, cptMin);
		if (user == null) {
			return;
		}

		// authentication
		final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(user, username, user.getAuthorities());
		final Authentication authentication = authenticationManagerCandidat.authenticate(authRequest);

		/* Se désinscrit de la réception de notifications */
		uiController.unregisterUiCandidat(MainUI.getCurrent());
		final SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
		UI.getCurrent().getSession().getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
		final MainUI current = (MainUI) UI.getCurrent();
		uiController.registerUiCandidat(current);
		i18nController.initLanguageUI(true);
		current.navigateToAccueilView();
	}

	/**
	 * Recupere un element de connexion
	 * @param  username
	 *                     le user a charger
	 * @return          le user
	 */
	public SecurityUser getSecurityUser(final String username) {
		SecurityUser user = connectAdminTech(username);
		if (user == null) {
			user = connectAdmin(username);
			if (user == null) {
				user = connectOther(username);
				if (user == null) {
					return connectCandidatCas(username);
				} else {
					return user;
				}
			} else {
				return user;
			}
		} else {
			return user;
		}
	}

	/**
	 * @param  userName
	 * @return          le displayName du Ldap, sinon le userName
	 */
	private String getDisplayNameFromLdap(final String userName) {
		try {
			final PeopleLdap p = ldapController.findByPrimaryKey(userName);
			if (p != null && p.getDisplayName() != null) {
				return p.getDisplayName();
			}
		} catch (final Exception e) {
		}
		return userName;
	}

	/**
	 * Connect un admin technique
	 * @param  username
	 *                     le username
	 * @return          le user connecte
	 */
	private SecurityUser connectAdminTech(final String username) {
		final List<GrantedAuthority> authoritiesListe = new ArrayList<>();
		/* Verif si l'utilisateur est l'admin technique initial */
		if (adminTechnique.equals(username)) {
			final SimpleGrantedAuthority sga = new SimpleGrantedAuthority(ConstanteUtils.ROLE_ADMIN_TECH);
			authoritiesListe.add(sga);
			Individu individu = individuController.getIndividu(username);
			final String libIndividu = getDisplayNameFromLdap(username);
			if (individu == null) {
				individu = new Individu(username, libIndividu, null);
				try {
					individuController.validateIndividuBean(individu, UI.getCurrent() != null ? UI.getCurrent().getLocale() : Locale.FRANCE);
					individu = individuController.saveIndividu(individu);
				} catch (final CustomException e) {
				}

			}
			final PreferenceInd pref = (individu != null ? individu.getPreferenceInd() : null);
			return new SecurityUserGestionnaire(username, libIndividu, authoritiesListe, getCtrCandForAdmin(pref), getCommissionForAdmin(pref), pref);
		}
		return null;
	}

	/**
	 * Connect un admin ou adminscolcentrale
	 * @param  username
	 *                     le username
	 * @return          le user connecte
	 */
	private SecurityUser connectAdmin(final String username) {
		final List<GrantedAuthority> authoritiesListe = new ArrayList<>();
		/* Verif si l'utilisateur est l'admin technique initial */
		/* Récupération des droits d'admin */
		droitProfilController.searchDroitAdminByLogin(username).forEach(e -> {
			final SimpleGrantedAuthority sga = new SimpleGrantedAuthority(ConstanteUtils.PREFIXE_ROLE + e.getDroitProfil().getCodProfil());
			authoritiesListe.add(sga);
		});
		/* Si admin on ne va pas plus loin */
		if (authoritiesListe.size() > 0) {
			final PreferenceInd pref = preferenceController.getPreferenceIndividu(username);
			return new SecurityUserGestionnaire(username, getDisplayNameFromLdap(username), authoritiesListe, getCtrCandForAdmin(pref), getCommissionForAdmin(pref), pref);
		}
		/* Récupération des droits scolCentral */
		droitProfilController.searchDroitScolCentralByLogin(username).forEach(e -> {
			final SimpleGrantedAuthority sga = new SimpleGrantedAuthority(ConstanteUtils.PREFIXE_ROLE + e.getDroitProfil().getCodProfil());
			authoritiesListe.add(sga);
		});
		/* Si admin on ne va pas plus loin */
		if (authoritiesListe.size() > 0) {
			final PreferenceInd pref = preferenceController.getPreferenceIndividu(username);
			return new SecurityUserGestionnaire(username, getDisplayNameFromLdap(username), authoritiesListe, getCtrCandForAdmin(pref), getCommissionForAdmin(pref), pref);
		}
		return null;
	}

	/**
	 * Connect un membre de commission ou centre cand
	 * @param  username
	 *                     le username
	 * @return          le user connecte
	 */
	private SecurityUser connectOther(final String username) {
		if (loadBalancingController.isLoadBalancingCandidatMode()) {
			return null;
		}
		List<GrantedAuthority> authoritiesListe = new ArrayList<>();
		final List<DroitProfilInd> listeDroitProfilInd = new ArrayList<>();

		final Individu ind = individuController.getIndividu(username);
		final PreferenceInd pref = (ind != null ? ind.getPreferenceInd() : null);
		if (ind != null) {
			// On recherche les profils autorisé (ctrCand ou commission pour
			// l'utilisateur suivant son login --> On ajoute à la liste
			listeDroitProfilInd.addAll(droitProfilController.searchDroitByLogin(username));
			/* Création de la liste d'autorities */
			SecurityCentreCandidature ctrCand = null;
			SecurityCommission commission = null;

			/* On place la commission favorite si elle existe en tete */
			if (pref != null && pref.getIdCommPref() != null) {
				final Optional<DroitProfilInd> optComm = listeDroitProfilInd.stream()
					.filter(droit -> droit.getCommissionMembre() != null && droit.getCommissionMembre().getCommission() != null
						&& pref.getIdCommPref().equals(droit.getCommissionMembre().getCommission().getIdComm()))
					.findFirst();
				if (optComm.isPresent()) {
					Collections.swap(listeDroitProfilInd, 0, listeDroitProfilInd.indexOf(optComm.get()));
				}
			}

			/* On place le centre de candidature favorit si il existe en tete */
			if (pref != null && pref.getIdCtrCandPref() != null) {
				final Optional<DroitProfilInd> optCtrCand = listeDroitProfilInd.stream()
					.filter(droit -> droit.getGestionnaire() != null && droit.getGestionnaire().getCentreCandidature() != null
						&& pref.getIdCtrCandPref().equals(droit.getGestionnaire().getCentreCandidature().getIdCtrCand()))
					.findFirst();
				if (optCtrCand.isPresent()) {
					Collections.swap(listeDroitProfilInd, 0, listeDroitProfilInd.indexOf(optCtrCand.get()));
				}
			}

			/* On parcourt la liste */
			for (final DroitProfilInd droitProfilInd : listeDroitProfilInd) {
				String codeRole = null;
				if (droitProfilInd.getDroitProfil().isDroitProfilGestionnaireCandidat()) {
					codeRole = ConstanteUtils.ROLE_GESTION_CANDIDAT;
				} else if (droitProfilInd.getDroitProfil().isDroitProfilGestionnaireCandidatLS()) {
					codeRole = ConstanteUtils.ROLE_GESTION_CANDIDAT_LS;
				} else if (droitProfilInd.getDroitProfil().isDroitProfilGestionnaireCtrCand()) {
					codeRole = ConstanteUtils.ROLE_CENTRE_CANDIDATURE;
					final Gestionnaire gestionnaire = droitProfilInd.getGestionnaire();
					if (ctrCand == null && gestionnaire != null && gestionnaire.getCentreCandidature() != null && gestionnaire.getCentreCandidature().getTesCtrCand()) {

						final List<Integer> listComm = new ArrayList<>();
						gestionnaire.getCommissions().forEach(e -> listComm.add(e.getIdComm()));

						ctrCand = new SecurityCentreCandidature(droitProfilInd.getGestionnaire().getCentreCandidature(),
							new ArrayList<>(droitProfilInd.getDroitProfil().getDroitProfilFoncs()),
							individuController.getCodCgeForGestionnaire(gestionnaire, username),
							false,
							gestionnaire.getTemAllCommGest(),
							listComm);
					}
				} else if (droitProfilInd.getDroitProfil().isDroitProfilCommission()) {
					codeRole = ConstanteUtils.ROLE_COMMISSION;
					final CommissionMembre membre = droitProfilInd.getCommissionMembre();
					if (commission == null && membre != null
						&& membre.getCommission() != null
						&& membre.getCommission().getTesComm()
						&& membre.getCommission().getCentreCandidature().getTesCtrCand()) {
						commission = new SecurityCommission(droitProfilInd.getCommissionMembre().getCommission(), new ArrayList<>(droitProfilInd.getDroitProfil().getDroitProfilFoncs()), false);
					}
				}
				if (codeRole != null) {
					final SimpleGrantedAuthority sga = new SimpleGrantedAuthority(codeRole);
					if (!authoritiesListe.contains(sga)) {
						authoritiesListe.add(sga);
					}
				}
			}

			// gestion des gestionnaires de candidat
			authoritiesListe = traiteDroitGestionnaireCandidat(authoritiesListe, ctrCand, commission);

			// on verifie qu'il y a bien des droits!
			if (authoritiesListe.size() > 0) {
				return new SecurityUserGestionnaire(username, getDisplayNameFromLdap(username), authoritiesListe, ctrCand, commission, pref);
			}
		}
		return null;
	}

	/**
	 * @param  authoritiesListe
	 * @param  ctrCand
	 * @param  commission
	 * @return                  la liste complétée par les droit de gestionnaire de candidat
	 */
	private List<GrantedAuthority> traiteDroitGestionnaireCandidat(final List<GrantedAuthority> authoritiesListe, final SecurityCentreCandidature ctrCand, final SecurityCommission commission) {
		final String paramGestCandCtrCand = parametreController.getModeGestionnaireCandidatCtrCand();
		final String paramGestCandComm = parametreController.getModeGestionnaireCandidatCommission();

		final SimpleGrantedAuthority authorityGestCand = new SimpleGrantedAuthority(ConstanteUtils.ROLE_GESTION_CANDIDAT);
		final SimpleGrantedAuthority authorityGestCandLS = new SimpleGrantedAuthority(ConstanteUtils.ROLE_GESTION_CANDIDAT_LS);

		// on verifie si il y a des droits individuels
		if (!authoritiesListe.contains(authorityGestCand) && !authoritiesListe.contains(authorityGestCandLS)) {
			// si le gestionnaire n'a pas les droit de gestionnaire et qu'il y a un
			// parametre global : on lui ajoute
			if (ctrCand != null) {
				if (paramGestCandCtrCand.equals(NomenclatureUtils.GEST_CANDIDATURE_READ) && !authoritiesListe.contains(authorityGestCandLS)) {
					authoritiesListe.add(authorityGestCandLS);
				} else if (paramGestCandCtrCand.equals(NomenclatureUtils.GEST_CANDIDATURE_WRITE) && !authoritiesListe.contains(authorityGestCand)) {
					authoritiesListe.add(authorityGestCand);
				}
			}
			// si le membre de commission n'a pas les droit de gestionnaire et qu'il y a un
			// parametre global : on lui ajoute
			if (commission != null) {
				if (paramGestCandComm.equals(NomenclatureUtils.GEST_CANDIDATURE_READ) && !authoritiesListe.contains(authorityGestCandLS)) {
					authoritiesListe.add(authorityGestCandLS);
				} else if (paramGestCandComm.equals(NomenclatureUtils.GEST_CANDIDATURE_WRITE) && !authoritiesListe.contains(authorityGestCand)) {
					authoritiesListe.add(authorityGestCand);
				}
			}
		}

		/* On dedoublonne la liste */
		if (authoritiesListe.contains(authorityGestCand) && authoritiesListe.contains(authorityGestCandLS)) {
			authoritiesListe.remove(authorityGestCandLS);
		}

		return authoritiesListe;
	}

	/**
	 * Valide le mot de passe candidat
	 * @param  password
	 *                        le mot de passe
	 * @param  correctHash
	 *                        le hash correct
	 * @return             true si le mot de passe correspond
	 */
	private Boolean validPwdCandidat(final String password, final CompteMinima cptMin) {
		if (testController.isTestMode()) {
			return true;
		}
		try {
			final PasswordHashService passwordHashUtils = PasswordHashService.getImplementation(cptMin.getTypGenCptMin());
			if (!passwordHashUtils.validatePassword(password, cptMin.getPwdCptMin())) {
				Notification.show(applicationContext.getMessage("compteMinima.connect.pwd.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
				return false;
			} else {
				return true;
			}
		} catch (final CustomException e) {
			Notification.show(applicationContext.getMessage("compteMinima.connect.pwd.error", null, UI.getCurrent().getLocale()), Type.WARNING_MESSAGE);
			return false;
		}
	}

	/**
	 * Connect un candidat
	 * @param  username
	 *                     le username
	 * @return          le user connecte
	 */
	private SecurityUser connectCandidatCas(final String username) {
		if (loadBalancingController.isLoadBalancingGestionnaireMode()) {
			return new SecurityUser(username, username, new ArrayList<GrantedAuthority>());
		}
		final CompteMinima cptMin = candidatController.searchCptMinByLogin(username);
		return constructSecurityUserCandidat(username, cptMin);
	}

	/**
	 * Créer un user Candidat
	 * @param  cptMin
	 *                     le compte a minima cree
	 * @param  username
	 *                     le username
	 * @return          le user connecte
	 */
	private SecurityUser constructSecurityUserCandidat(final String username, final CompteMinima cptMin) {
		Integer idCptMin = null;
		String noDossierOPI = null;
		Boolean cptMinValid = false;
		Boolean mailValid = false;

		if (cptMin != null && campagneController.isCampagneActiveCandidat(cptMin.getCampagne())) {
			idCptMin = cptMin.getIdCptMin();
			noDossierOPI = cptMin.getNumDossierOpiCptMin();
			cptMinValid = cptMin.getTemValidCptMin();
			mailValid = cptMin.getTemValidMailCptMin();
			final List<GrantedAuthority> authoritiesListe = new ArrayList<>();
			final SimpleGrantedAuthority sga = new SimpleGrantedAuthority(ConstanteUtils.ROLE_CANDIDAT);
			authoritiesListe.add(sga);
			final Candidat candidat = cptMin.getCandidat();
			String codLangue = null;
			if (candidat != null) {
				codLangue = candidat.getLangue().getCodLangue();
			}
			return new SecurityUserCandidat(username, getDisplayNameCandidat(cptMin), authoritiesListe, idCptMin, noDossierOPI, cptMinValid, mailValid, codLangue);
		} else {
			return new SecurityUser(username, username, new ArrayList<GrantedAuthority>());
		}
	}

	/** @return le user Candidat */
	public SecurityUserCandidat getSecurityUserCandidat() {
		return getSecurityUserCandidat(getCurrentAuthentication());
	}

	/** @return le user Candidat */
	public SecurityUserCandidat getSecurityUserCandidat(final Authentication auth) {
		final UserDetails details = getCurrentUser(auth);
		if (details instanceof SecurityUserCandidat) {
			return ((SecurityUserCandidat) details);
		}
		return null;
	}

	/**
	 * Alimente la session pour un compte local
	 * @param cptMin
	 *                  le compte a minima a connecter
	 */
	public void alimenteSecurityUserCptMin(final CompteMinima cptMin) {
		final SecurityUser user = (SecurityUser) getCurrentUser();
		if (user != null) {
			final List<GrantedAuthority> authoritiesListe = new ArrayList<>();
			final SimpleGrantedAuthority sga = new SimpleGrantedAuthority(ConstanteUtils.ROLE_CANDIDAT);
			authoritiesListe.add(sga);

			final SecurityUserCandidat securityUserCandidat = new SecurityUserCandidat(user.getUsername(),
				user.getDisplayName(),
				authoritiesListe,
				cptMin.getIdCptMin(),
				cptMin.getNumDossierOpiCptMin(),
				cptMin.getTemValidCptMin(),
				cptMin.getTemValidMailCptMin(),
				null);
			final UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(securityUserCandidat, securityUserCandidat.getUsername(), securityUserCandidat.getAuthorities());
			final Authentication authentication = authenticationManagerCandidat.authenticate(authRequest);
			final SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(authentication);
			SecurityContextHolder.setContext(context);
			UI.getCurrent().getSession().getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
		}
	}

	/** Valide les flags d'un compteminima connecte */
	public void validSecurityUserCptMin() {
		try {
			final String login = getCurrentUserLogin();
			if (login != null) {
				if (loadBalancingController.isLoadBalancingGestionnaireMode() || isPersonnel()) {
					return;
				}
				final CompteMinima cptMin = candidatController.searchCptMinByLogin(login);
				if (cptMin != null) {
					alimenteSecurityUserCptMin(cptMin);
				}
			}
		} catch (final Exception e) {
		}
	}

	/**
	 * Renvoi le centre de candidature à rattacher à l'utilisateur
	 * @param  id
	 *                     l'id du ctrCand
	 * @param  username
	 *                     le user
	 * @return          L'element de connexion ctrCand
	 */
	private SecurityCentreCandidature getSecurityCentreCandidature(final Integer id, final String username) {
		for (final DroitProfilInd droitProfilInd : droitProfilController.searchDroitByLoginAndIdCtrCand(id, username)) {
			if (droitProfilInd.getDroitProfil().isDroitProfilGestionnaireCtrCand()) {
				final Gestionnaire gestionnaire = droitProfilInd.getGestionnaire();
				if (gestionnaire != null && gestionnaire.getCentreCandidature() != null && gestionnaire.getCentreCandidature().getTesCtrCand()) {
					final List<Integer> listComm = new ArrayList<>();
					gestionnaire.getCommissions().forEach(e -> listComm.add(e.getIdComm()));

					return new SecurityCentreCandidature(droitProfilInd.getGestionnaire().getCentreCandidature(),
						new ArrayList<>(droitProfilInd.getDroitProfil().getDroitProfilFoncs()),
						individuController.getCodCgeForGestionnaire(gestionnaire, username),
						false,
						gestionnaire.getTemAllCommGest(),
						listComm);
				}
			}
		}
		return null;
	}

	/**
	 * Renvoi la commission à rattacher à l'utilisateur
	 * @param  id
	 * @param  username
	 *                     le user
	 * @return          L'element de connexion commission
	 */
	private SecurityCommission getSecurityCommission(final Integer id, final String username) {
		for (final DroitProfilInd droitProfilInd : droitProfilController.searchDroitByLoginAndIdComm(id, username)) {
			if (droitProfilInd.getDroitProfil().isDroitProfilCommission()) {

				final CommissionMembre membre = droitProfilInd.getCommissionMembre();
				if (membre != null && membre.getCommission() != null && membre.getCommission().getTesComm() && membre.getCommission().getCentreCandidature().getTesCtrCand()) {

					return new SecurityCommission(droitProfilInd.getCommissionMembre().getCommission(), new ArrayList<>(droitProfilInd.getDroitProfil().getDroitProfilFoncs()), false);
				}
			}
		}
		return null;
	}

	/**
	 * Recupere le premier centre de candidature pour un admin
	 * @param  pref
	 * @return      le premier centre de candidature
	 */
	public SecurityCentreCandidature getCtrCandForAdmin(final PreferenceInd pref) {
		final CentreCandidature ctrCand = droitProfilController.getCtrCandForAdmin(pref);
		if (ctrCand != null) {
			return new SecurityCentreCandidature(ctrCand, null, null, true, true, new ArrayList<Integer>());
		}
		return null;
	}

	/**
	 * Recupere la premiere commission pour un admin
	 * @param  pref
	 * @return      la premiere commission
	 */
	public SecurityCommission getCommissionForAdmin(final PreferenceInd pref) {
		final Commission commission = droitProfilController.getCommissionForAdmin(pref);
		if (commission != null) {
			return new SecurityCommission(commission, null, true);
		}
		return null;
	}

	/**
	 * Renvoie le centre de candidature préféré
	 * @return le centre de candidature de la session
	 */
	public SecurityCentreCandidature getCentreCandidature(final Authentication auth) {
		final UserDetails details = getCurrentUser(auth);
		if (details instanceof SecurityUserGestionnaire) {
			return ((SecurityUserGestionnaire) details).getCentreCandidature();
		}
		return null;
	}

	/**
	 * Renvoie le centre de candidature préféré
	 * @return le centre de candidature de la session
	 */
	public SecurityCentreCandidature getCentreCandidature() {
		return getCentreCandidature(getCurrentAuthentication());
	}

	/**
	 * Renvoie la commission préférée
	 * @return la commission de la session
	 */
	public SecurityCommission getCommission(final Authentication auth) {
		final UserDetails details = getCurrentUser(auth);
		if (details instanceof SecurityUserGestionnaire) {
			return ((SecurityUserGestionnaire) details).getCommission();
		}
		return null;
	}

	/**
	 * Renvoie la commission préférée
	 * @return la commission de la session
	 */
	public SecurityCommission getCommission() {
		return getCommission(getCurrentAuthentication());
	}

	/**
	 * Renvoie le numero dossier candidat en cours d'édition
	 * @return le numero dossier candidat en cours d'édition
	 */
	public String getNoDossierCandidat(final Authentication auth) {
		return getNoDossierCandidat(getCurrentUser(auth));
	}

	/**
	 * Renvoie le numero dossier candidat en cours d'édition
	 * @return le numero dossier candidat en cours d'édition
	 */
	public String getNoDossierCandidat(final UserDetails details) {
		if (details instanceof SecurityUserGestionnaire) {
			return ((SecurityUserGestionnaire) details).getNoDossierCandidat();
		}
		return null;
	}

	/**
	 * Renvoie le numero dossier candidat en cours d'édition
	 * @return le numero dossier candidat en cours d'édition
	 */
	public String getDisplayNameCandidat(final UserDetails details) {
		if (details instanceof SecurityUserGestionnaire) {
			return ((SecurityUserGestionnaire) details).getDisplayNameCandidat();
		}
		return null;
	}

	/**
	 * Renvoie la fonctionnalité et le centre de candidature en cours
	 * @param  codFonc
	 *                    le code de la fonctionnalite
	 * @return         l'element de session de fonctionnalite
	 */
	public SecurityCtrCandFonc getCtrCandFonctionnalite(final String codFonc) {
		return getCtrCandFonctionnalite(codFonc, getCurrentAuthentication());
	}

	/**
	 * Renvoie la fonctionnalité et le centre de candidature en cours
	 * @param  codFonc
	 *                    le code de la fonctionnalite
	 * @return         l'element de session de fonctionnalite
	 */
	public SecurityCtrCandFonc getCtrCandFonctionnalite(final String codFonc, final Authentication auth) {
		final SecurityCentreCandidature scc = getCentreCandidature(auth);
		if (scc != null) {
			final CentreCandidature ctrCand = centreCandidatureController.getCentreCandidature(scc.getIdCtrCand());
			/* Verification de la concordance entre le centre de candidature en cours et le
			 * menu chargé dans l'UI Utile lorsqu'un user a changé de centre dans un autre
			 * onglet */
			MainUI.getCurrent().checkConcordanceCentreCandidature(ctrCand);
			if (ctrCand != null) {
				final SecurityCtrCandFonc sf = new SecurityCtrCandFonc(ctrCand, null, scc.getIsGestAllCommission(), scc.getListeIdCommission());
				if (isScolCentrale(auth)) {
					sf.setDroit(Droit.WRITE);
					return sf;
				} else if (scc.getListFonctionnalite() != null && scc.getListFonctionnalite().size() != 0) {
					final Optional<DroitProfilFonc> fonc = scc.getListFonctionnalite().stream().filter(e -> e.getId().getCodFonc().equals(codFonc)).findFirst();
					if (fonc.isPresent()) {
						if (fonc.get().getTemReadOnly()) {
							sf.setDroit(Droit.READ_ONLY);
						} else {
							sf.setDroit(Droit.WRITE);
						}
						return sf;
					}
				}
			}
		} else {
			MainUI.getCurrent().checkConcordanceCentreCandidature(null);
		}
		return new SecurityCtrCandFonc(Droit.NO_RIGHT);
	}

	/**
	 * Renvoie la fonctionnalité et le centre de candidature en cours
	 * @param  codFonc
	 *                    le code de la fonctionnalite
	 * @return         l'element de session de fonctionnalite
	 */
	public SecurityCommissionFonc getCommissionFonctionnalite(final String codFonc) {
		return getCommissionFonctionnalite(codFonc, getCurrentAuthentication());
	}

	/**
	 * Renvoie la fonctionnalité et le centre de candidature en cours
	 * @param  codFonc
	 *                    le code de la fonctionnalite
	 * @return         l'element de session de fonctionnalite
	 */
	public SecurityCommissionFonc getCommissionFonctionnalite(final String codFonc, final Authentication auth) {
		final SecurityCommission scc = getCommission(auth);
		if (scc != null) {
			final Commission commission = commissionController.getCommissionById(scc.getIdComm());
			/* Verification de la concordance entre le centre de candidature en cours et le
			 * menu chargé dans l'UI Utile lorsqu'un user a changé de centre dans un autre
			 * onglet */
			MainUI.getCurrent().checkConcordanceCommission(commission);
			if (commission != null) {
				final SecurityCommissionFonc sf = new SecurityCommissionFonc(commission, null);
				if (isScolCentrale(auth)) {
					sf.setDroit(Droit.WRITE);
					return sf;
				} else if (scc.getListFonctionnalite() != null && scc.getListFonctionnalite().size() != 0) {
					final Optional<DroitProfilFonc> fonc = scc.getListFonctionnalite().stream().filter(e -> e.getId().getCodFonc().equals(codFonc)).findFirst();
					if (fonc.isPresent()) {
						if (fonc.get().getTemReadOnly()) {
							sf.setDroit(Droit.READ_ONLY);
						} else {
							sf.setDroit(Droit.WRITE);
						}
						return sf;
					}
				}
			}
		} else {
			MainUI.getCurrent().checkConcordanceCommission(null);
		}
		return new SecurityCommissionFonc(Droit.NO_RIGHT);
	}

	/**
	 * change le centre de candidature préféré
	 * @param centreCand
	 *                      le centre de candidature
	 */
	public void setCentreCandidature(final CentreCandidature centreCand) {
		final UserDetails details = getCurrentUser();
		if (details instanceof SecurityUserGestionnaire) {
			if (centreCand == null) {
				((SecurityUserGestionnaire) details).setCentreCandidature(null);
			} else {
				if (isScolCentrale()) {
					((SecurityUserGestionnaire) details).setCentreCandidature(new SecurityCentreCandidature(centreCand, null, null, true, true, new ArrayList<Integer>()));
				} else {
					final SecurityCentreCandidature centre = getSecurityCentreCandidature(centreCand.getIdCtrCand(), getCurrentUserLogin());
					((SecurityUserGestionnaire) details).setCentreCandidature(centre);
				}
				/* On modifie les preferences de l'utilisateur */
				preferenceController.setPrefCentreCandidature(centreCand);
			}
		}
		return;
	}

	/**
	 * change la commission preferee
	 * @param commission
	 *                      la commission
	 */
	public void setCommission(final Commission commission) {
		final UserDetails details = getCurrentUser();
		if (details instanceof SecurityUserGestionnaire) {
			if (commission == null) {
				((SecurityUserGestionnaire) details).setCommission(null);
			} else {
				if (isScolCentrale()) {
					((SecurityUserGestionnaire) details).setCommission(new SecurityCommission(commission, null, true));
				} else {
					((SecurityUserGestionnaire) details).setCommission(getSecurityCommission(commission.getIdComm(), getCurrentUserLogin()));
				}
				/* On modifie les preferences de l'utilisateur */
				preferenceController.setPrefCommission(commission);
			}
		}
		return;
	}

	/**
	 * Change le numero de dossier en cours d'edition
	 * @param cptMin
	 *                  le compte a minima
	 */
	public void setNoDossierNomCandidat(final CompteMinima cptMin) {
		final UserDetails details = getCurrentUser();
		if (details instanceof SecurityUserGestionnaire) {
			if (cptMin == null) {
				((SecurityUserGestionnaire) details).setNoDossierCandidat(null);
				((SecurityUserGestionnaire) details).setDisplayNameCandidat(null);
			} else {
				((SecurityUserGestionnaire) details).setNoDossierCandidat(cptMin.getNumDossierOpiCptMin());
				((SecurityUserGestionnaire) details).setDisplayNameCandidat(getDisplayNameCandidat(cptMin));
			}

		}
	}

	/**
	 * @param  idCtr
	 * @return       true si le menu des param CC est ouvert, false sinon
	 */
	public Boolean isMenuParamCCOpen(final Integer idCtr) {
		if (idCtr == null) {
			return false;
		}
		if (centreCandidatureController.getIsCtrCandParamCC(idCtr)) {
			return true;
		} else {
			final CentreCandidature ctr = centreCandidatureController.getCentreCandidature(idCtr);
			if (ctr == null) {
				return false;
			}
			/* Sinon on vérifie que le CC a des typedec, mails ou motivation paramétré */
			return ctr.getTypeDecisions().size() > 0 || ctr.getMails().size() > 0 || ctr.getMotivationAvis().size() > 0;
		}
	}

	/**
	 * @param  cpt
	 * @return     le displayName du candidat
	 */
	private String getDisplayNameCandidat(final CompteMinima cpt) {
		if (cpt == null) {
			return null;
		} else {
			return cpt.getPrenomCptMin() + " " + cpt.getNomCptMin();
		}
	}

	/** @return le numéro de dossier d'un candidat ou gestionnaire */
	public String getNoDossierOPI() {
		final UserDetails details = getCurrentUser();
		if (details instanceof SecurityUserCandidat) {
			return ((SecurityUserCandidat) details).getNoDossierOPI();
		} else if (details instanceof SecurityUserGestionnaire) {
			return ((SecurityUserGestionnaire) details).getNoDossierCandidat();
		}
		return null;
	}

	/** @return la liste des preference */
	public PreferenceInd getPreferenceIndividu() {
		final UserDetails details = getCurrentUser();
		if (details instanceof SecurityUserGestionnaire) {
			final PreferenceInd pref = ((SecurityUserGestionnaire) details).getPreferenceInd();
			if (pref != null) {
				return pref;
			}
		}
		return new PreferenceInd();
	}

	/**
	 * Modifie la liste des preference
	 * @param pref
	 */
	public void setPreferenceIndividu(final PreferenceInd pref) {
		final UserDetails details = getCurrentUser();
		if (details instanceof SecurityUserGestionnaire) {
			((SecurityUserGestionnaire) details).setPreferenceInd(pref);
		}
	}
}
