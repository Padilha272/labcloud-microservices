package com.labcloud.auth.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.labcloud.auth.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String Id;

    @Column(nullable = false)
    @ToString.Include
    @Setter(AccessLevel.PROTECTED)
    private String tenantId;

    @Column(nullable = false, length = 100)
    @ToString.Include
    private String name;

    @Column(nullable = false, length = 100)
    @ToString.Include
    private String email;

    @Column(nullable = false)
    @ToString.Include
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    private UserRole role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    @Setter(AccessLevel.NONE)
    private LocalDateTime lastLogin;

    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laboratory", nullable = false)
    @ToString.Include
    private Laboratory laboratory;

    // UserDetails
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    // Métodos auxiliáres
    public void updateTenantId(String newTenantId) {
        if (newTenantId == null || newTenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID não pode ser vazio");
        }

        this.tenantId = newTenantId;
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
}
