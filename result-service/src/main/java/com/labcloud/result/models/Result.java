package com.labcloud.result.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "results")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    @ToString.Include
    @EqualsAndHashCode.Include
    private String id;

    // Tenant (multi-tenancy)
    @Column(nullable = false)
    @ToString.Include
    @Setter(AccessLevel.NONE)
    private String tenantId;

    // Dados de Resultado
    @Column(nullable = false, length = 100)
    @ToString.Include
    private String parameter;

    @Column(nullable = false)
    @ToString.Include
    private String value;

    @Column(length = 20)
    @ToString.Include
    private String unit;

    private LocalDateTime measurementDate;

    @Column(length = 100)
    private String instrument;

    @Column(length = 255)
    private String method;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Builder.Default
    private Boolean isValid = true;

    @Column(columnDefinition = "TEXT")
    private String qualityControl;

    // Referências desnormalizadas (Sample Service)
    @Column(nullable = false)
    @ToString.Include
    private String sampleId;

    @Column(length = 100)
    private String sampleName;

    private String experimentId;

    @Column(length = 100)
    private String experimentName;

    // Referências desnormalizadas (Auth Service)
    @Column(nullable = false)
    @ToString.Include
    private String createdBy;

    @Column(length = 100)
    private String createdByName;

    @CreationTimestamp
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    // Método auxiliar
    public void updateTenantId(String newTenantId) {
        if (newTenantId == null || newTenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID não pode ser vazio");
        }
        this.tenantId = newTenantId;

    }
}