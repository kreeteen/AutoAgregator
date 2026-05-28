package ru.vsu.cs.edportal.security;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
@Order(1)
public class JwtAuthFilter implements Filter {

    public static final String COOKIE_NAME = "jwt";
    public static final String HEADER_PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;
    private final UserContext userContext;
    private final JwtTokenBlacklist tokenBlacklist;

    public JwtAuthFilter(JwtTokenProvider tokenProvider, UserContext userContext,
                         JwtTokenBlacklist tokenBlacklist) {
        this.tokenProvider = tokenProvider;
        this.userContext = userContext;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = extractToken(httpRequest);

        if (token != null && !tokenBlacklist.isRevoked(token) && tokenProvider.validate(token)) {
            Integer userId = tokenProvider.getUserId(token);
            String email = tokenProvider.getEmail(token);
            String name = tokenProvider.getName(token);
            userContext.setUserId(userId);
            userContext.setEmail(email);
            userContext.setName(name);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }

    public static void setJwtCookie(HttpServletResponse response, String token, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Lax")
                .path("/")
                .maxAge(86400)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void setJwtCookie(HttpServletResponse response, String token) {
        setJwtCookie(response, token, false);
    }

    public static void clearJwtCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static String extractTokenFromRequest(HttpServletRequest request) {
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

    private String extractToken(HttpServletRequest request) {
        return extractTokenFromRequest(request);
    }
}
