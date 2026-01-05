package com.example.User.controller;

import com.example.User.entities.User;
import com.example.User.entities.models.LoginRequest;
import com.example.User.entities.models.LoginResponse;
import com.example.User.entities.models.RefreshTokenRequest;
import com.example.User.entities.request.UserRequestDTO;
import com.example.User.entities.response.UserResponseDTO;
import com.example.User.service.RefreshTokenService;
import com.example.User.service.UserService;
import com.example.User.utility.JWTUtility;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTUtility jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * Endpoint to register a new user.
     * @param userRequestDTO the user request data transfer object containing user details.
     * @return ResponseEntity<User> containing the created user or an error response.
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO userRequestDTO) {
            User user = userService.register(userRequestDTO);
            return new ResponseEntity<>(user.toResponseDTO(), HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE,
           consumes = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(), loginRequest.getPassword()
                )
        );

        String username = auth.getName();
        User user = userService.getUserByEmail(username);

        String token = jwtUtil.generateToken(username, Map.of("roles", auth.getAuthorities()));
        String refreshToken = refreshTokenService.createRefreshToken(user);
        
        return new LoginResponse(token, refreshToken, "Bearer", jwtUtil.getJwtExpirationInMs());
    }

    /**
     * Endpoint to refresh access token using refresh token.
     * @param refreshTokenRequest the refresh token request containing the refresh token.
     * @return LoginResponse containing new access token and refresh token.
     */
    @PostMapping(value = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public LoginResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        var refreshToken = refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        // Fetch user explicitly to avoid lazy loading issues
        User user = userService.getUserByEmail(refreshToken.getUser().getEmail());
        
        // Convert roles to authorities format for consistency with login
        var authorities = user.getRoles().stream()
                .map(role -> (org.springframework.security.core.GrantedAuthority) () -> role)
                .toList();
        
        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(user.getEmail(), 
                Map.of("roles", authorities));
        
        // Generate new refresh token (rotate refresh token for security)
        String newRefreshToken = refreshTokenService.createRefreshToken(user);
        
        return new LoginResponse(newAccessToken, newRefreshToken, "Bearer", 
                jwtUtil.getJwtExpirationInMs());
    }

    @GetMapping(value="/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user.toResponseDTO());
    }
}
