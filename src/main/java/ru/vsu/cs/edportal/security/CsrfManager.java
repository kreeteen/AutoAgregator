package ru.vsu.cs.edportal.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CsrfManager {

    private static final String SESSION_ATTR = "_csrf_token";
    private final SecureRandom random = new SecureRandom();

    public String getToken(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute(SESSION_ATTR);
        if (token == null) {
            token = generateToken();
            session.setAttribute(SESSION_ATTR, token);
        }
        return token;
    }

    public boolean validateToken(HttpServletRequest request) {
        String expected = (String) request.getSession().getAttribute(SESSION_ATTR);
        if (expected == null) return false;
        String actual = request.getParameter("_csrf");
        if (actual == null) actual = request.getHeader("X-CSRF-Token");
        return expected.equals(actual);
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}