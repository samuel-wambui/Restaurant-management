package HotelManagement.security;

import HotelManagement.jwt.JwtFilter;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables method-level security annotations like @PreAuthorize
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final DetailsService detailsService;
    private final CustomUserDetailsPasswordService userDetailsPasswordService;

    public SecurityConfig(JwtFilter jwtFilter, DetailsService detailsService, CustomUserDetailsPasswordService userDetailsPasswordService) {
        this.jwtFilter = jwtFilter;
        this.detailsService = detailsService;
        this.userDetailsPasswordService = userDetailsPasswordService;
    }

    // Security filter chain configuration

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless JWT auth
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/verification/send-email",
                                "/auth/register",
                                "/api/auth/**",
                                "/api/users/**",
                                "/verification/**",
                                "/user",
                                "/api/roles/**",
                                "/api/stock/**",
                                "/api/multiplier/**",
                                "/api/v1/tables/**",
                                "/api/v1/messages/**",
                                "/api/v1/orders/**",
                                "/api/v1/meals/**",
                                "/api/payments-methods/**",
                                "/api/bookings/**",
                                "/api/rooms/**",
                                "/api/rooms/**",
                                "/api/stock/add"

                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return httpSecurity.build();
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); // Allow credentials (cookies, authorization headers)
        config.setAllowedOrigins(List.of("http://localhost:4200")); // Explicitly allow localhost:4200
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "X-Requested-With")); // Limit allowed headers
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // Limit allowed HTTP methods
        config.setExposedHeaders(List.of("Authorization")); // Expose any specific headers to the client if necessary
        config.setMaxAge(3600L); // Cache the preflight response for 1 hour

        source.registerCorsConfiguration("/**", config);
        return source;
    }




    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder()); // Set the password encoder
        authenticationProvider.setUserDetailsService(detailsService);  // Set the user details service
        authenticationProvider.setUserDetailsPasswordService(userDetailsPasswordService); // Set custom password service
        return authenticationProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
