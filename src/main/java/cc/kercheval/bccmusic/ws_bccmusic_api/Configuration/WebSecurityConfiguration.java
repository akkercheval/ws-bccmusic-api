package cc.kercheval.bccmusic.ws_bccmusic_api.Configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;

import cc.kercheval.bccmusic.ws_bccmusic_api.Enum.Role;
import cc.kercheval.bccmusic.ws_bccmusic_api.security.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {
	
	private final CustomUserDetailsService userDetailsService;
	
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            
            .cors(cors -> cors.configurationSource(s -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("https://bccmusic.boonecountyin.org", "http://localhost:5173"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                config.setMaxAge(3600L);
                return config;
            }))
            
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/perform_login", "/logout").permitAll()
                .requestMatchers(HttpMethod.POST, "/accounts").permitAll()
                .requestMatchers("/css/**", "/js/**", "/static/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/me").authenticated()
                .requestMatchers("/dashboard").authenticated()
                .requestMatchers(HttpMethod.GET, "/accounts").hasRole(Role.ADMINISTRATOR.name())
                .requestMatchers(HttpMethod.PUT, "/accounts").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/accounts/*/password").authenticated()
                .requestMatchers(HttpMethod.GET, "/collaborators").hasRole(Role.OWNER.name())
                .requestMatchers(HttpMethod.GET, "/collaborators/my-collaborations").authenticated()
                .requestMatchers(HttpMethod.POST, "/collaborators/*").authenticated()
                .requestMatchers(HttpMethod.PUT, "/collaborators/*").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/collaborators/*").authenticated()
                .requestMatchers(HttpMethod.GET, "/scores", "/scores/search", "/scores/my-scores").authenticated()
                .requestMatchers(HttpMethod.GET, "/scores/other-scores").authenticated()
                .requestMatchers(HttpMethod.GET, "/arrangement-types").authenticated()
                .requestMatchers(HttpMethod.GET, "/composers").authenticated()
                .requestMatchers(HttpMethod.GET, "/composers/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/composers").authenticated()	//.hasAnyAuthority(Role.OWNER.name(), Role.COLLABORATOR.name())
                .requestMatchers(HttpMethod.PUT, "/composers").hasAnyAuthority(Role.OWNER.name(), Role.COLLABORATOR.name())
                .requestMatchers(HttpMethod.GET, "/score-tags").authenticated()
                .requestMatchers("/scores/**").authenticated()
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                    .loginPage("/login")
                    .loginProcessingUrl("/perform_login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .permitAll()
                    .successHandler((request, response, authentication) -> {
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"success\": true, \"message\": \"Login successful\"}");
                        response.getWriter().flush();
                    })
                    .failureHandler((request, response, exception) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"success\": false, \"message\": \"Invalid credentials\"}");
                        response.getWriter().flush();
                    })
                )
            
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )

            .logout(logout -> logout
            	    .logoutUrl("/logout")
            	    .logoutSuccessHandler((request, response, authentication) -> {
            	        response.setStatus(HttpServletResponse.SC_OK);
            	        response.setContentType("application/json");
            	        response.getWriter().write("{\"success\": true, \"message\": \"Logged out\"}");
            	        response.getWriter().flush();
            	    })
            	    .permitAll()
                );

        return http.build();
    }
}
