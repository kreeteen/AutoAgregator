package ru.vsu.cs.edportal.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Component
@Order(0)
public class RateLimitFilter implements Filter {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MS = 60_000;

    private final Map<String, PriorityBlockingQueue<Long>> attempts = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        boolean isAuthEndpoint = path.equals("/login") || path.equals("/register")
                || path.equals("/api/auth/login") || path.equals("/api/auth/register");

        if (isAuthEndpoint && method.equalsIgnoreCase("POST")) {
            String ip = httpRequest.getRemoteAddr();
            if (!allowRequest(ip)) {
                ((HttpServletResponse) response).sendError(429, "Too Many Requests. Try again later.");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean allowRequest(String ip) {
        long now = System.currentTimeMillis();
        PriorityBlockingQueue<Long> timestamps = attempts.computeIfAbsent(ip,
                k -> new PriorityBlockingQueue<>(11, Comparator.naturalOrder()));

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