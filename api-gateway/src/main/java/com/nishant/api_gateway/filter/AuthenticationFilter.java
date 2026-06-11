package com.nishant.api_gateway.filter;

import com.nishant.api_gateway.utils.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    // 1. Tell Spring MVC which routes are PUBLIC (Bypass JWT checking)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.contains("/auth/login") || 
               path.contains("/auth/register") || 
               path.contains("/health");
    }

    // 2. Protect all other paths
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Extract Authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Missing or malformed Authorization Token");
            return;
        }

        String token = authHeader.substring(7);

        // Validate the JWT
        if (!jwtUtil.validateToken(token)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token claims or signature");
            return;
        }

        // Token is good! Extract identity information
        String username = jwtUtil.getUsernameFromToken(token);

        // Wrap the request to inject user email downstream to microservices
        var wrappedRequest = new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if ("X-User-Email".equalsIgnoreCase(name)) {
                    return username;
                }
                return super.getHeader(name);
            }
        };

        // Forward down the pipeline
        filterChain.doFilter(wrappedRequest, response);
    }
}