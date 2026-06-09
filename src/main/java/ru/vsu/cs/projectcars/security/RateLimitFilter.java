package ru.vsu.cs.projectcars.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MS = 60_000;
    private static final String[] PROTECTED_PATHS = {"/login", "/register", "/api/auth/login", "/api/auth/register"};

    private final Map<String, Queue<Long>> attempts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        if ("POST".equalsIgnoreCase(method) && isProtectedPath(path)) {
            String ip = httpRequest.getRemoteAddr();
            if (!allowRequest(ip)) {
                ((HttpServletResponse) response).sendError(429, "Too Many Requests. Try again later.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isProtectedPath(String path) {
        for (String p : PROTECTED_PATHS) {
            if (path.equals(p) || path.startsWith(p + "/")) return true;
        }
        return false;
    }

    private boolean allowRequest(String ip) {
        long now = System.currentTimeMillis();
        Queue<Long> timestamps = attempts.computeIfAbsent(ip, k -> new ConcurrentLinkedQueue<>());

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && now - timestamps.peek() > WINDOW_MS) {
                timestamps.poll();
            }
            if (timestamps.size() >= MAX_REQUESTS) {
                return false;
            }
            timestamps.offer(now);
            return true;
        }
    }
}
