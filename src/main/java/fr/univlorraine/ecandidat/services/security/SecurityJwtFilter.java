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
package fr.univlorraine.ecandidat.services.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWTVerifier;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;

/**
 * Le filtre JWT
 * @author Kevin Hergalant
 */
public class SecurityJwtFilter extends OncePerRequestFilter {

	@Setter
	private JWTVerifier jWTVerifier;

	/* JWT */
	public static final String SECURITY_JWT_HEADER = "Authorization";
	public static final String SECURITY_JWT_PREFIXE = "Bearer ";

	/* Spring security */
	public static final String SECURITY_REST_ROUTE = "/rest/user";
	public static final String SECURITY_USER = "user";
	public static final String SECURITY_ROLE_REST = "REST";
	public static final String SECURITY_ROLE = "ROLE_" + SECURITY_ROLE_REST;

	/**
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse,
	 *      javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse, final FilterChain filterChain) throws IOException, ServletException {
		if (!httpRequest.getRequestURI().startsWith(SECURITY_REST_ROUTE) || jWTVerifier == null) {
			filterChain.doFilter(httpRequest, httpResponse);
			return;
		}
		try {
			final String header = httpRequest.getHeader(SECURITY_JWT_HEADER);
			if (header == null || !header.startsWith(SECURITY_JWT_PREFIXE)) {
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			final String authToken = header.substring(7);
			jWTVerifier.verify(authToken);
			// authentication une fois que le token est vérifié
			final List<GrantedAuthority> liste = new ArrayList<>();
			liste.add(new SimpleGrantedAuthority(SECURITY_ROLE));
			final User user = new User(SECURITY_USER, "X", liste);
			final Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getUsername(), user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(httpRequest, httpResponse);
		} catch (final Exception e) {
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
