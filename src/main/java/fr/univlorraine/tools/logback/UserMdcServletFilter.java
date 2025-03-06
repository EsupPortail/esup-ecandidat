/**
 *
 *  ESUP-Portail MONDOSSIERWEB - Copyright (c) 2016 ESUP-Portail consortium
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
 *
 */
package fr.univlorraine.tools.logback;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

/**
 * Filtre permettant de passer l'utilisateur Spring Security à Logback.
 * @author Adrien Colson
 */
public class UserMdcServletFilter implements Filter {

	/**
	 * Nom de la variable utilisateur dans le MDC.
	 */
	public static final String USER_KEY = "username";

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		boolean successfulRegistration = false;

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth instanceof Authentication) {
			final String username = auth.getName();
			successfulRegistration = registerUsername(username);
		}

		try {
			chain.doFilter(request, response);
		} finally {
			if (successfulRegistration) {
				MDC.remove(USER_KEY);
			}
		}
	}

	@Override
	public void init(final FilterConfig config) throws ServletException {
	}

	/**
	 * Register the user in the MDC under USER_KEY.
	 *
	 * @param username user name
	 * @return true id the user can be successfully registered
	 */
	private boolean registerUsername(final String username) {
		if (username instanceof String && !username.isEmpty()) {
			MDC.put(USER_KEY, username);
			return true;
		}
		return false;
	}

}
