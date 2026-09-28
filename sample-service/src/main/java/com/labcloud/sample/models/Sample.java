package com.labcloud.sample.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity 
@Table(name = "samples")
@AllArgsConstructor 
@NoArgsConstructor
@Getter
@Setter 
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder 
public class Sample {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String id;

    //Tenant (multi-tenancy)
    @Column(nullable = false)
    @ToString.Include
    @Setter(AccessLevel.NONE)
    private String tenantId;

    //Campos desnormalizados (referências a outros serviços)
    @Column(nullable = false)
    @ToString.Include
    private String createdBy;  // userId

    //Campos desnormalizados (referências a outros serviços)
    @Column(length = 100)
    private String createdByName;

    @Column(nullable = false, length = 100)
    @ToString.Include
    private String name;

    @Column(nullable = false, length = 50)
    @ToString.Include
    private String type;

    private LocalDateTime collectionDate;

    @Column(length = 100)
    private String collectionMethod;

    private Double quantity;

    @Column(length = 20)
    private String unit;

    @Column(length = 255)
    private String storageConditions;

    @Column(length = 100)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    //Relacionamentos internos (dentro do Sample Service) - Um experimento pode ter várias amostras
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "experiment_id", nullable = false)
    @ToString.Exclude
    private Experiment experiment;

    //Métodos auxiliares
    public void updateTenantId(String newTenantId) {
        if (newTenantId == null || newTenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID não pode ser vazio");
        }
        this.tenantId = newTenantId;
    }

}
