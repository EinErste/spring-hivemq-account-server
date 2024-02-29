package org.ein.erste.iot.account.settings.auth;

import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.ein.erste.iot.account.repositories.UserRepository;
import org.ein.erste.iot.account.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.ein.erste.iot.account.settings.auth.AuthConstants.HEADER_STRING;
import static org.ein.erste.iot.account.utils.Utils.writeObjectToResponse;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class WebSecurity {

    private final UserDetailsService userDetailsService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final UserRepository usersRepository;
    private final EntityManagerFactory entityManagerFactory;
    private final PasswordEncoder passwordEncoder;
    @Value("${server.swagger.enabled}")
    private Boolean SWAGGER_ENABLED;
    @Value("${jwt.access.token.secret}")
    private String SECRET;
    @Value("${jwt.access.token.prefix}")
    private String TOKEN_PREFIX;

    @Bean
    @Autowired
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exp -> exp
                        .authenticationEntryPoint(
                                (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
                                    writeObjectToResponse(Response.of(HttpServletResponse.SC_UNAUTHORIZED,"error.auth.required"),response);
                                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        if (SWAGGER_ENABLED)
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**", "/configuration/**", "/swagger-ui.html/**", "/webjars/**").permitAll());
        http.authorizeHttpRequests(auth -> auth.requestMatchers("/error").permitAll());
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, ApiUtils.User.MAIN_USER_API + ApiUtils.User.SIGN_UP_START).permitAll()
                .requestMatchers(HttpMethod.POST, ApiUtils.User.MAIN_USER_API + ApiUtils.User.SIGN_UP_END).permitAll()
                .anyRequest().authenticated()
        );
        http.addFilter(new JWTAuthenticationFilter(localeResolver, messageSource, authenticationManager, entityManagerFactory, usersRepository))
                .addFilter(new JWTAuthorizationFilter(authenticationManager, SECRET, TOKEN_PREFIX));
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @SuppressWarnings("deprecation")
    @Autowired
    void registerProvider(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowCredentials(false);
        configuration.setAllowedHeaders(getCorsAllowedHeaders());
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", configuration.applyPermitDefaultValues());
        return source;
    }

    private List<String> getCorsAllowedHeaders() {
        return Arrays.asList(HEADER_STRING, "User-Agent", "Cache-Control", "X-Remote-IP", "X-Forwarded-List", "X-FORWARDED-FOR",
                "Content-Type", "Accept-Language", "X-Requested-With", HttpHeaders.IF_MODIFIED_SINCE, HttpHeaders.LAST_MODIFIED);
    }
}
