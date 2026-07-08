package cc.kercheval.bccmusic.ws_bccmusic_api.Configuration;

import java.util.function.Supplier;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CSRF token request handler for Single Page Applications.
 * 
 * Uses BREACH-resistant XOR encoding for the token value while still
 * allowing the SPA to read the plain token from the cookie and send it
 * back in a header. Spring Security 6 requires this dual-handler approach
 * for cookie-based CSRF with SPAs.
 */
public final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    private final CsrfTokenRequestAttributeHandler plain = new CsrfTokenRequestAttributeHandler();
    private final XorCsrfTokenRequestAttributeHandler xor = new XorCsrfTokenRequestAttributeHandler();

    public SpaCsrfTokenRequestHandler() {
        // Make the plain token available as a request attribute for the cookie filter
        this.plain.setCsrfRequestAttributeName(null);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        // Delegates to the plain handler to set the request attribute (triggers cookie write)
        this.plain.handle(request, response, csrfToken);
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        String headerValue = request.getHeader(csrfToken.getHeaderName());

        // If the header contains a raw (non-XOR-encoded) token, resolve it plainly.
        // Otherwise, use XOR resolution for BREACH protection.
        if (StringUtils.hasText(headerValue)) {
            return this.xor.resolveCsrfTokenValue(request, csrfToken);
        }

        return this.plain.resolveCsrfTokenValue(request, csrfToken);
    }
}
