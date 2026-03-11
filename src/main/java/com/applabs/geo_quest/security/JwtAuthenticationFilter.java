/**
 * JWT authentication filter for GeoQuest backend.
 * <p>
 * Parses and validates JWT tokens from Authorization headers, sets authentication context.
 * <p>
 * Key features:
 * <ul>
 *   <li>Validates JWT tokens using JwtUtil</li>
 *   <li>Sets user authentication in SecurityContext</li>
 *   <li>Handles invalid/expired tokens with 401 response</li>
 * </ul>
 * <p>
 * Usage:
 * <ul>
 *   <li>Added to security filter chain before UsernamePasswordAuthenticationFilter.</li>
 *   <li>Used for stateless authentication of API requests.</li>
 * </ul>
 *
 * @author fl4nk3r
 * @since 2026-03-11
 * @version 3.0
 */
package com.applabs.geo_quest.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtUtil.parse(header.substring(7));
            String uid = claims.getSubject();

            var auth = new UsernamePasswordAuthenticationToken(
                    uid, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}