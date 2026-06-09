package ru.vsu.cs.projectcars.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "banned_at")
    private LocalDateTime bannedAt;

    @Column(name = "ban_reason", length = 500)
    private String banReason;

    @OneToMany(mappedBy = "user")
    private List<VehicleCar> cars = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getBannedAt() { return bannedAt; }
    public void setBannedAt(LocalDateTime bannedAt) { this.bannedAt = bannedAt; }
    public String getBanReason() { return banReason; }
    public void setBanReason(String banReason) { this.banReason = banReason; }
    public List<VehicleCar> getCars() { return cars; }
    public void setCars(List<VehicleCar> cars) { this.cars = cars; }

    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(r -> r.getName().equals(roleName));
    }
}
