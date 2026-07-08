package cc.kercheval.bccmusic.ws_bccmusic_api.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cc.kercheval.bccmusic.ws_bccmusic_api.Exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Rate-limits account creation (POST /accounts) to prevent abuse.
 * 
 * Uses CF-Connecting-IP header to identify the real client IP because
 * this server runs behind a Cloudflare Tunnel. All incoming connections
 * appear to come from Cloudflare's infrastructure, so remoteAddr is not
 * useful for per-client rate limiting.
 * 
 * If we ever move away from Cloudflare, this header extraction will need
 * to be updated (e.g., to X-Forwarded-For or remoteAddr).
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

	private static final int MAX_REQUESTS = 3;
	private static final long WINDOW_MILLIS = 15 * 60 * 1000L; // 15 minutes

	private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (!isAccountCreationRequest(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		String clientIp = resolveClientIp(request);
		TokenBucket bucket = buckets.computeIfAbsent(clientIp, k -> new TokenBucket(MAX_REQUESTS, WINDOW_MILLIS));

		if (!bucket.tryConsume()) {
			writeRateLimitResponse(response, request.getRequestURI());
			return;
		}

		filterChain.doFilter(request, response);
	}

	private boolean isAccountCreationRequest(HttpServletRequest request) {
		return HttpMethod.POST.matches(request.getMethod())
				&& "/accounts".equals(request.getRequestURI());
	}

	/**
	 * Resolves the real client IP from the CF-Connecting-IP header.
	 * This header is set by Cloudflare and cannot be spoofed by end users
	 * because Cloudflare overwrites it on every request.
	 * 
	 * If the header is missing (e.g., local development without Cloudflare),
	 * falls back to remoteAddr.
	 */
	private String resolveClientIp(HttpServletRequest request) {
		String cfIp = request.getHeader("CF-Connecting-IP");
		if (cfIp != null && !cfIp.isBlank()) {
			return cfIp.trim();
		}
		return request.getRemoteAddr();
	}

	private void writeRateLimitResponse(HttpServletResponse response, String path) throws IOException {
		ErrorResponse errorResponse = new ErrorResponse(
				HttpStatus.TOO_MANY_REQUESTS.value(),
				"Too many requests",
				"Account creation rate limit exceeded. Please try again later.",
				path,
				LocalDateTime.now()
		);

		response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setHeader("Retry-After", String.valueOf(WINDOW_MILLIS / 1000));
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}

	/**
	 * Simple token bucket: allows {@code maxTokens} requests per {@code windowMillis}.
	 * Refills completely when the window expires.
	 */
	private static class TokenBucket {
		private final int maxTokens;
		private final long windowMillis;
		private int tokens;
		private long windowStart;

		TokenBucket(int maxTokens, long windowMillis) {
			this.maxTokens = maxTokens;
			this.windowMillis = windowMillis;
			this.tokens = maxTokens;
			this.windowStart = System.currentTimeMillis();
		}

		synchronized boolean tryConsume() {
			long now = System.currentTimeMillis();
			if (now - windowStart > windowMillis) {
				// Window expired — reset
				tokens = maxTokens;
				windowStart = now;
			}

			if (tokens > 0) {
				tokens--;
				return true;
			}
			return false;
		}
	}
}
