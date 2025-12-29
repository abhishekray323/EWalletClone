package com.example.User.filter;

import com.example.User.entities.models.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
@Setter
public class JsonUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;


    /**
     * Attempts to authenticate the user based on the provided JSON credentials.
     * @param request the HttpServletRequest containing the login credentials in JSON format.
     * @param response  the HttpServletResponse to send the authentication result.
     * @return Authentication object if authentication is successful.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        log.error("Abhishek Ray logging in JsonUsernamePasswordAuthFilter");
        String contentType = request.getContentType();
        log.error("Abhishek Ray logging in JsonUsernamePasswordAuthFilter with JSON content type {}", contentType);

        if (contentType != null && contentType.startsWith("application/json")) {
            log.error("Abhishek Ray logging in JsonUsernamePasswordAuthFilter with JSON content type {}", contentType);
            try {
                LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword()
                );
                log.error("Abhishek Ray logging in JsonUsernamePasswordAuthFilter with authToken: {}", authToken);
                Authentication authenticate = authenticationManager.authenticate(authToken);
                log.error("Abhishek Ray logging in JsonUsernamePasswordAuthFilter with authenticate: {}", authenticate);

                return  authenticate;
            } catch (IOException e) {
                log.error("Abhishek Ray Error reading login request from input stream", e);
                throw new RuntimeException(e);
            }
        }
        return super.attemptAuthentication(request, response);
    }
}
