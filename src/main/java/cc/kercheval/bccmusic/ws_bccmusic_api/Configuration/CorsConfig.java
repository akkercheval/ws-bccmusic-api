package cc.kercheval.bccmusic.ws_bccmusic_api.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")                          // allow all endpoints
                .allowedOrigins("http://localhost:5173")    // Vite dev server **  Note: this will change for production **
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)                     // if you ever use cookies/sessions
                .maxAge(3600);                              // cache preflight for 1 hour
    }
}
