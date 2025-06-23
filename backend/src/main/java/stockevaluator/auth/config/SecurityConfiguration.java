package stockevaluator.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import stockevaluator.config.FrontendConfig;

import java.util.List;

/**
 * Security configuration class for the application.
 *
 * <p>This class configures the security settings, including CORS, authentication, and
 * authorization rules.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final FrontendConfig frontendConfig;

  /**
   * Configures the security filter chain for the application.
   *
   * @param http                        The HttpSecurity object to configure
   * @param jwtAuthenticationEntryPoint The JwtAuthenticationEntryPoint to use
   * @param jwtAuthFilter               The JwtAuthenticationFilter to use
   * @param authenticationProvider      The AuthenticationProvider to use
   * @return The security filter chain
   * @throws Exception if an error occurs
   */
  @Bean
  public DefaultSecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
      JwtAuthenticationFilter jwtAuthFilter,
      AuthenticationProvider authenticationProvider
  ) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(auth -> auth
            // Reddit API endpoints - publicly accessible
            .requestMatchers(HttpMethod.GET, "/api/reddit/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/reddit/collect").permitAll() 
            
            // Existing permitted endpoints
      
            .requestMatchers("/ws/**")
            .permitAll()
           
            .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh",
                "/api/auth/request-password-reset", "/api/auth/complete-password-reset")
            .permitAll()
            .requestMatchers(HttpMethod.POST, "/api/email/**", "/api/auth/verify-email",
                "/api/auth/verify-password-reset").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/register/admin",
                "/api/auth/admin/reset-password-link").permitAll()
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            .anyRequest().authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .exceptionHandling(
            handler -> handler.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Configures CORS (Cross-Origin Resource Sharing) for the application.
   *
   * @return The CorsConfigurationSource for the application
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    String origin = frontendConfig.getUrl();
    if (origin == null || origin.isBlank()) {
        origin = "http://localhost:5173";
    }
    configuration.setAllowedOrigins(List.of(origin));    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}