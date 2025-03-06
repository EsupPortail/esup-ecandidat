package fr.univlorraine.ecandidat.utils;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

/**
 * Filtre permettant de passer l'utilisateur Spring Security à Logback.
 * @author Adrien Colson
 */
public class UserMdcServletFilter implements Filter {

	/**
	 * Nom de la variable utilisateur dans le MDC.
	 */
	public static final String USER_KEY = "username";

	/**
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/**
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
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

	/**
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(final FilterConfig config) throws ServletException {
	}

	/**
	 * Register the user in the MDC under USER_KEY.
	 * @param  username user name
	 * @return          true id the user can be successfully registered
	 */
	private boolean registerUsername(final String username) {
		if (username instanceof String && !username.isEmpty()) {
			MDC.put(USER_KEY, username);
			return true;
		}
		return false;
	}

}
