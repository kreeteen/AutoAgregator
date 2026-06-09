package ru.vsu.cs.projectcars.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class JwtPrincipal implements UserDetails {

    private final Integer userId;
    private final String email;
    private final String name;
    private final List<GrantedAuthority> authorities;

    public JwtPrincipal(Integer userId, String email, String name, List<GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.authorities = authorities;
    }

    public Integer getUserId() { return userId; }
    public String getName() { return name; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return null; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
