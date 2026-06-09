package ru.vsu.cs.projectcars.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String COOKIE_NAME = "jwt";
    public static final String HEADER_PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null && tokenProvider.validate(token)) {
            Integer userId = tokenProvider.getUserId(token);
            String email = tokenProvider.getEmail(token);
            String name = tokenProvider.getName(token);
            List<String> roles = tokenProvider.getRoles(token);
            if (roles == null) {
                chain.doFilter(request, response);
                return;
            }

            List<GrantedAuthority> authorities = roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toList());

            JwtPrincipal principal = new JwtPrincipal(userId, email, name, authorities);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith(HEADER_PREFIX)) {
            return header.substring(HEADER_PREFIX.length());
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (COOKIE_NAME.equals(c.getName())) {
                    return c.getValue();
                }
            }
        }
        return null;
    }
}
