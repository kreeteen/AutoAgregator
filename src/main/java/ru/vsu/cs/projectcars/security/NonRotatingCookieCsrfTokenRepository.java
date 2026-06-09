package ru.vsu.cs.projectcars.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class NonRotatingCookieCsrfTokenRepository implements CsrfTokenRepository {

    private static final String DEFAULT_CSRF_COOKIE_NAME = "XSRF-TOKEN";
    private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";
    private static final String DEFAULT_CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        CsrfToken existing = loadToken(request);
        if (existing != null) {
            return existing;
        }
        return new DefaultCsrfToken(DEFAULT_CSRF_HEADER_NAME, DEFAULT_CSRF_PARAMETER_NAME,
                UUID.randomUUID().toString());
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        String tokenValue = (token != null) ? token.getToken() : "";
        CsrfToken existing = loadToken(request);
        if (existing != null && token != null
                && existing.getToken().equals(token.getToken())) {
            return;
        }
        Cookie cookie = new Cookie(DEFAULT_CSRF_COOKIE_NAME, tokenValue);
        cookie.setSecure(request.isSecure());
        cookie.setPath(getRequestContext(request));
        cookie.setHttpOnly(false);
        if (tokenValue.isEmpty()) {
            cookie.setMaxAge(0);
        }
        response.addCookie(cookie);
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Optional<Cookie> csrfCookie = Arrays.stream(cookies)
                    .filter(c -> DEFAULT_CSRF_COOKIE_NAME.equals(c.getName()))
                    .findFirst();
            if (csrfCookie.isPresent()) {
                String value = csrfCookie.get().getValue();
                if (StringUtils.hasText(value)) {
                    return new DefaultCsrfToken(
                            DEFAULT_CSRF_HEADER_NAME,
                            DEFAULT_CSRF_PARAMETER_NAME,
                            value
                    );
                }
            }
        }
        return null;
    }

    private String getRequestContext(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return (contextPath != null) ? contextPath : "/";
    }
}
