package com.example.User.handler;

import com.example.User.entities.User;
import com.example.User.entities.response.UserResponseDTO;
import com.example.User.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class SuccessfullLoginHandler implements AuthenticationSuccessHandler {
    @Autowired
    private final ObjectMapper mapper;

    @Autowired
    private final UserRepository userRepository;

    /**
     * Handles successful authentication by retrieving user details and sending them in the response.
     *
     * @param request        the HttpServletRequest
     * @param response       the HttpServletResponse
     * @param authentication the Authentication object containing user details
     * @throws IOException      if an input or output error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow();
        UserResponseDTO userResponseDTO = user.toResponseDTO();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), userResponseDTO);
    }
}
