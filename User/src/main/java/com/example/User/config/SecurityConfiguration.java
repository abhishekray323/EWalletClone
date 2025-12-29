package com.example.User.config;

import com.example.User.filter.JsonUsernamePasswordAuthFilter;
import com.example.User.filter.JwtAuthFilter;
import com.example.User.handler.SuccessfullLoginHandler;
import com.example.User.handler.UnsuccessfulLoginHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfiguration {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SuccessfullLoginHandler successfullLoginHandler;

    @Autowired
    private UnsuccessfulLoginHandler unsuccessfulLoginHandler;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * Configures the AuthenticationManager with a UserDetailsService and PasswordEncoder.
     * @param userDetailsService the service to load user-specific data
     * @param passwordEncoder the encoder to hash passwords
     * @return AuthenticationManager instance
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }


    /**
     * Configures the security filter chain for HTTP requests.
     * It sets up CSRF protection, form login, and logout handling.
     *
     * @param http        the HttpSecurity object to configure
     * @param authManager the AuthenticationManager to handle authentication
     * @return SecurityFilterChain instance
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationManager authManager)throws Exception {
        var jsonLoginFilter = new  JsonUsernamePasswordAuthFilter(mapper, authManager);
        jsonLoginFilter.setFilterProcessesUrl("/api/users/login");
        jsonLoginFilter.setAuthenticationSuccessHandler(successfullLoginHandler);
        jsonLoginFilter.setAuthenticationFailureHandler(unsuccessfulLoginHandler);

        http
                    .csrf(csrf -> csrf
                                    .ignoringRequestMatchers("/api/users/register", "/login", "/api/users/login")
                    )
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/", "/login", "/error", "/error/**", "/favicon.ico", "/logout",
                                    "/api/users/register", "/api/users/login", "/public/**").permitAll()
                            .anyRequest().authenticated()
                    )
//                    .formLogin(form -> form
//                            .loginProcessingUrl("/api/users/login")
//                            .usernameParameter("email")// optional if you serve a custom GET /login
//                            .passwordParameter("password")
//                            .successHandler(successfullLoginHandler)
//                            .permitAll()
//                    )
//                    .addFilterAt(jsonLoginFilter, UsernamePasswordAuthenticationFilter.class)
                        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedEntryPoint()))
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/login?logout")
                            .permitAll()
                    );

        return http.build();
    }

    /**
     * Password encoder bean using BCrypt hashing algorithm.
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * Configures the entry point for unauthorized access attempts.
     * @return AuthenticationEntryPoint instance
     */
    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        };
    }
}
