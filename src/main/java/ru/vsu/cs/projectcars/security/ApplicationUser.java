package ru.vsu.cs.projectcars.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.vsu.cs.projectcars.model.Role;
import ru.vsu.cs.projectcars.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class ApplicationUser implements UserDetails {

    private final Integer id;
    private final String email;
    private final String firstName;
    private final String passwordHash;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public ApplicationUser(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.passwordHash = user.getPasswordHash();
        this.active = user.isActive();
        this.authorities = user.getRoles().stream()
                .map(Role::getName)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toSet());
    }

    public Integer getId() { return id; }
    public String getFirstName() { return firstName; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }

    @Override
    public String getPassword() { return passwordHash; }

    @Override
    public String getUsername() { return email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return active; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return active; }
}
