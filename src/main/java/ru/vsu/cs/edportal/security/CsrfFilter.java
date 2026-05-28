package ru.vsu.cs.edportal.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
@Order(2)
public class CsrfFilter implements Filter {

    private static final Set<String> SAFE_METHODS = Set.of("GET", "HEAD", "OPTIONS", "TRACE");
    private static final Set<String> SKIP_PATHS = Set.of("/login", "/register", "/api/auth/login", "/api/auth/register");

    private final CsrfManager csrfManager;

    public CsrfFilter(CsrfManager csrfManager) {
        this.csrfManager = csrfManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        if (!SAFE_METHODS.contains(method) && !isSkipped(path)) {
            if (!csrfManager.validateToken(httpRequest)) {
                ((HttpServletResponse) response).sendError(403, "CSRF token invalid or missing");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isSkipped(String path) {
        for (String p : SKIP_PATHS) {
            if (path.equals(p) || path.startsWith(p + "/")) return true;
        }
        return false;
    }
}