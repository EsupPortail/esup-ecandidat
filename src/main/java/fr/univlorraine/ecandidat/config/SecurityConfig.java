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
package fr.univlorraine.ecandidat.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import fr.univlorraine.ecandidat.services.security.SecurityAuthenticationProvider;
import fr.univlorraine.ecandidat.services.security.SecurityJwtFilter;
import fr.univlorraine.ecandidat.services.security.SecurityUserDetailsService;
import fr.univlorraine.ecandidat.utils.ConstanteUtils;
import fr.univlorraine.ecandidat.utils.NomenclatureUtils;

/**
 * Configuration Spring Security
 * @author Adrien Colson
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

	@Value("${cas.url:}")
	private transient String casUrl;

	@Value("${app.url:}")
	private transient String appUrl;

	@Value("${jwt.secret:}")
	private String jwtSecret;

	@Autowired
	private AuthenticationConfiguration configuration;

	@Bean
	AuthenticationManager authenticationManager() throws Exception {
		return configuration.getAuthenticationManager();
	}

	/**
	 * @return           authenticationManager candidat
	 * @throws Exception
	 */
	@Bean(name = "authenticationManagerCandidat")
	public SecurityAuthenticationProvider authenticationManagerCandidatBean() throws Exception {
		return new SecurityAuthenticationProvider();
	}

	@Bean
	public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((requests) -> requests
				/* Path connect authentifié */
				.requestMatchers(new AntPathRequestMatcher(ConstanteUtils.SECURITY_CONNECT_PATH + "/**")).authenticated()

				/* Autorise l'usurpation de compte pour les admins */
				.requestMatchers(new AntPathRequestMatcher(ConstanteUtils.SECURITY_SWITCH_PATH))
				.hasAnyAuthority(ConstanteUtils.SECURITY_ROLE_PREFIXE + NomenclatureUtils.DROIT_PROFIL_ADMIN_TECH, ConstanteUtils.SECURITY_ROLE_PREFIXE + NomenclatureUtils.DROIT_PROFIL_ADMIN)
				.requestMatchers(new AntPathRequestMatcher(ConstanteUtils.SECURITY_SWITCH_BACK_PATH)).hasAuthority(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR)

				/* Securise les appels rest */
				.requestMatchers(new AntPathRequestMatcher(SecurityJwtFilter.SECURITY_REST_ROUTE + "/*")).hasRole(SecurityJwtFilter.SECURITY_ROLE_REST)

				/* Path de base */
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll()

				/* Les autres requêtes doivent être authentifiées */
				.anyRequest().authenticated());

		http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(casEntryPoint()));
		http.authorizeHttpRequests(authz -> authz.anyRequest().authenticated());

		http.addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);
		http.addFilter(new LogoutFilter(casUrl + "/logout", new SecurityContextLogoutHandler()));
		http.addFilter(casAuthenticationFilter());

		// La protection Spring Security contre le Cross Scripting Request Forgery est désactivée, Vaadin implémente sa propre protection
		http.csrf(c -> c.disable());

		/* Supprime le "continue" de l'url */
		http.requestCache(Customizer.withDefaults());

		/* La protection Spring Security contre le Cross Scripting Request Forgery est désactivée, Vaadin implémente sa propre protection */
		http.csrf(csrf -> csrf.disable());

		/* Autorise l'affichage en iFrame */
		http.headers((headers) -> headers.frameOptions(Customizer.withDefaults()));

		/* Renvoie vers la page d'accueil en cas de déconnexion */
		http.logout(logout -> logout.logoutSuccessUrl("/"));

		return http.build();
	}

	@Autowired
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(casAuthenticationProvider());
	}

	/* Configuration CAS */
	@Bean
	public SingleSignOutFilter singleSignOutFilter() {
		final SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
		singleSignOutFilter.setIgnoreInitConfiguration(true);
		return singleSignOutFilter;
	}

	@Bean
	public ServiceProperties casServiceProperties() {
		final ServiceProperties casServiceProperties = new ServiceProperties();
		casServiceProperties.setService(appUrl + "/login/cas");
		casServiceProperties.setSendRenew(false);
		return casServiceProperties;
	}

	@Bean
	public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
		final CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
		casAuthenticationFilter.setAuthenticationManager(authenticationManager());
		return casAuthenticationFilter;
	}

	@Bean
	public CasAuthenticationEntryPoint casEntryPoint() {
		final CasAuthenticationEntryPoint casEntryPoint = new CasAuthenticationEntryPoint();
		casEntryPoint.setLoginUrl(casUrl + "/login");
		casEntryPoint.setServiceProperties(casServiceProperties());
		return casEntryPoint;
	}

	@Bean
	public CasAuthenticationProvider casAuthenticationProvider() throws Exception {
		final CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
		casAuthenticationProvider.setKey(UUID.randomUUID().toString());
		casAuthenticationProvider.setAuthenticationUserDetailsService(securityUserDetailsService());
		casAuthenticationProvider.setServiceProperties(casServiceProperties());
		casAuthenticationProvider.setTicketValidator(new Cas20ServiceTicketValidator(casUrl));
		return casAuthenticationProvider;
	}

	@Bean
	public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> securityUserDetailsService() {
		return new SecurityUserDetailsService();
	}

	/* Filtre permettant de prendre le rôle d'un autre utilisateur */
	@Bean
	public SwitchUserFilter switchUserFilter() throws Exception {
		final SwitchUserFilter switchUserFilter = new SwitchUserFilter();
		//switchUserFilter.setUserDetailsService(userDetailsServiceBean());
		switchUserFilter.setSwitchUserUrl(ConstanteUtils.SECURITY_SWITCH_PATH);
		switchUserFilter.setExitUserUrl(ConstanteUtils.SECURITY_SWITCH_BACK_PATH);
		switchUserFilter.setTargetUrl("/");
		return switchUserFilter;
	}

	/**
	 * @return                          jwtAuthenticationFilter
	 * @throws IllegalArgumentException IllegalArgumentException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	@Bean
	public SecurityJwtFilter jwtAuthenticationFilter() throws IllegalArgumentException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		final SecurityJwtFilter filter = new SecurityJwtFilter();
		if (StringUtils.isBlank(jwtSecret)) {
			return filter;
		}
		final Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
		final JWTVerifier verifier = JWT.require(algorithm).build();
		filter.setJWTVerifier(verifier);
		return filter;
	}

}