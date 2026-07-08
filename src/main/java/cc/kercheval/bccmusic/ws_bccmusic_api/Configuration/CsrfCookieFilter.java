package cc.kercheval.bccmusic.ws_bccmusic_api.Configuration;

import java.io.IOException;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Forces the deferred CSRF token to be loaded on every request so that
 * the CookieCsrfTokenRepository actually writes the XSRF-TOKEN cookie.
 * 
 * In Spring Security 6, the CSRF token is lazy-loaded by default. Without
 * this filter, the cookie won't be set until something else triggers the
 * token to load — which never happens for GET requests in an SPA.
 */
public class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            // Force the token to load, which triggers the repository to write the cookie
            csrfToken.getToken();
        }

        filterChain.doFilter(request, response);
    }
}
