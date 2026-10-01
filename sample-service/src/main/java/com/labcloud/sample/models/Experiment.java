package com.labcloud.sample.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.labcloud.sample.enums.ExperimentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "experiments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Experiment {

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

    // Campos desnormalizados (refências a outros serviços)
    @Column(nullable = false)
    @ToString.Include
    private String laboratoryId;

    // Campos desnormalizados (refências a outros serviços)
    @Column(nullable = false, length = 100)
    @ToString.Include
    private String laboratoryName;

    @Column(nullable = false, length = 100)
    @ToString.Include
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    private ExperimentStatus status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Column(columnDefinition = "TEXT")
    private String objective;

    @Column(columnDefinition = "TEXT")
    private String methodology;

    @Column(nullable = false)
    @ToString.Include
    private String createdBy; // UserId

    @Column(length = 100)
    private String createdByName;

    @CreationTimestamp
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    // Relacionamentos internos do sample-service - Um experimento tem várias
    // amostras
    @OneToMany(mappedBy = "experiment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Include
    private List<Sample> samples = new ArrayList<>();

    // Métodos auxiliares
    public void updateTenantId(String newTenantId) {
        if (newTenantId == null || newTenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID não pode ser vazio");
        }
        this.tenantId = newTenantId;
    }

    public void complete() {
        this.status = ExperimentStatus.COMPLETED;
        this.endDate = LocalDateTime.now();
    }

    public void cancel() {
        this.status = ExperimentStatus.CANCELLED;
        this.endDate = LocalDateTime.now();
    }

    public void activate() {
        this.status = ExperimentStatus.ACTIVE;
        this.startDate = LocalDateTime.now();
    }

}
