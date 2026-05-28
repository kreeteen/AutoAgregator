package ru.vsu.cs.edportal.security;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenBlacklist {

    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    public void revoke(String token) {
        if (token != null) blacklist.add(token);
    }

    public boolean isRevoked(String token) {
        return token != null && blacklist.contains(token);
    }
}