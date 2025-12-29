package com.example.User.filter;
import com.example.User.service.JPAUserDetailService;
import com.example.User.utility.JWTUtility;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JWTUtility jwtUtil;
    private final JPAUserDetailService userDetailsService;

    public JwtAuthFilter(JWTUtility jwtUtil, JPAUserDetailService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response , FilterChain chain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        log.info("JwtAuthFilter: Extracted authHeader: {}", authHeader);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.info("JwtAuthFilter: Extracted token: {}", token);

            try {
                username = jwtUtil.extractUsername(token);
                log.info("JwtAuthFilter: Extracted username: {}", username);
            } catch (JwtException e) {
                // invalid token -> let it fall through and the AuthenticationEntryPoint will handle
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.info("JwtAuthFilter: Loaded userDetails for username: {}", username);
            if (jwtUtil.isTokenValid(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("JwtAuthFilter: Set authentication for user: {}", username);
            }
        }
        chain.doFilter(request, response);
    }
}
