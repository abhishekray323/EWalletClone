package com.example.User.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@AllArgsConstructor
public class UnsuccessfulLoginHandler implements AuthenticationFailureHandler {
    @Autowired
    private final ObjectMapper mapper;

    /**
     * Handles authentication failure by sending an error response with a 401 status code.
     *
     * @param request   the HttpServletRequest
     * @param response  the HttpServletResponse
     * @param exception the AuthenticationException that occurred
     * @throws IOException      if an input or output error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        mapper.writeValue(response.getOutputStream(),
                Map.of(
                        "error", "Authentication failed",
                        "message", exception.getMessage()
                )
        );
    }
}
