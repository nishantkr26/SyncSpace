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

        // TEMPORARY DEBUG LINE: Look at your console when you hit send!
        System.out.println("Gateway received request path: ---> " + path);

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

        // FIX: Wrap the request and override BOTH getHeader AND getHeaderNames
        // This forces the Gateway's routing client to discover and forward your custom header!
        var wrappedRequest = new jakarta.servlet.http.HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if ("X-User-Email".equalsIgnoreCase(name)) {
                    return username;
                }
                return super.getHeader(name);
            }

            @Override
            public java.util.Enumeration<String> getHeaderNames() {
                java.util.List<String> names = java.util.Collections.list(super.getHeaderNames());
                if (!names.contains("X-User-Email")) {
                    names.add("X-User-Email");
                }
                return java.util.Collections.enumeration(names);
            }

            @Override
            public java.util.Enumeration<String> getHeaders(String name) {
                if ("X-User-Email".equalsIgnoreCase(name)) {
                    java.util.List<String> values = java.util.Collections.singletonList(username);
                    return java.util.Collections.enumeration(values);
                }
                return super.getHeaders(name);
            }
        };

        // Forward down the pipeline
        filterChain.doFilter(wrappedRequest, response);
    }
}