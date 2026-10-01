package com.labcloud.sample.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.labcloud.sample.enums.ExperimentStatus;
import com.labcloud.sample.models.Experiment;

@Repository
public interface ExperimentRepository extends JpaRepository<Experiment,String>{

    //Buscas básicas
    List<Experiment> findByTenantId(String tenantId);
    Page<Experiment> findByTenantId(String tenantId, Pageable pageable);

    List<Experiment> findByLaboratoryId(String laboratoryId);
    Page<Experiment> findByLaboratoryId(String laboratoryId, Pageable pageable);

    List<Experiment> findByCreatedBy(String userId);


    //Buscas por status
    List<Experiment> findByTenantIdAndStatus(String tenantId, ExperimentStatus status);
    long countByTenantIdAndStatus(String tenantId, ExperimentStatus status);

    //Busca por nome
    List<Experiment> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name);


    //Buscas avançadas
    @Query("SELECT e FROM Experiment e WHERE e.tenantId = :tenantId AND e.status IN ('PLANNED', 'ACTIVE')")
    List<Experiment> findActiveExperiments(@Param("tenantId") String tenantId);

    @Query("SELECT e FROM Experiment e WHERE e.tenantId = :tenantId " +
           "AND e.status = 'COMPLETED' AND e.endDate BETWEEN :startDate AND :endDate")
    List<Experiment> findCompletedExperimentsBetweenDates(
            @Param("tenantId") String tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT e FROM Experiment e LEFT JOIN FETCH e.samples WHERE e.id = :id")
    Optional<Experiment> findByIdWithSamples(@Param("id") String id);

    @Query("SELECT e FROM Experiment e WHERE e.tenantId = :tenantId ORDER BY e.createdAt DESC")
    List<Experiment> findRecentExperiments(@Param("tenantId") String tenantId, Pageable pageable);

    //Contagem
    @Query("SELECT e.status, COUNT(e) FROM Experiment e WHERE e.tenantId = :tenantId GROUP BY e.status")
    List<Object[]> countExperimentsByStatus(@Param("tenantId") String tenantId);
}
