package com.bookexchange.security;

import com.bookexchange.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Runs before every request. If a valid "Authorization: Bearer <token>" header is present,
 * the user is loaded from MongoDB and placed in the SecurityContext.
 * Blocked users are treated as anonymous, so blocking takes effect immediately.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = jwtUtil.parse(header.substring(7));
                userRepository.findById(claims.getSubject())
                        .filter(u -> !u.isBlocked())
                        .ifPresent(u -> {
                            var auth = new UsernamePasswordAuthenticationToken(
                                    u, null, List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole())));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        });
            } catch (JwtException | IllegalArgumentException e) {
                // invalid / expired token -> request continues as anonymous and gets 401 if the route is protected
            }
        }
        chain.doFilter(request, response);
    }
}
