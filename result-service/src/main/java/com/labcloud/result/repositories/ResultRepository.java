package com.labcloud.result.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.labcloud.result.models.Result;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, String> {

    // Buscas Básicas
    List<Result> findByTenantId(String tenantId);

    Page<Result> findByTenantId(String tenantId, Pageable pageable);

    List<Result> findBySampleId(String sampleId);

    Page<Result> findBySampleId(String sampleId, Pageable pageable);

    List<Result> findByExperimentId(String experimentId);

    Page<Result> findByExperimentId(String experimentId, Pageable pageable);

    List<Result> findByCreatedBy(String createdBy);

    // Buscas de parâmetro
    List<Result> findByTenantIdAndParameter(String tenantId, String parameter);

    // Buscas por validade
    List<Result> findByTenantIdAndIsValidTrue(String tenantId);

    List<Result> findByTenantIdAndIsValidFalse(String tenantId);

    // Buscas por data
    @Query("SELECT r FROM Result r WHERE r.tenantId = :tenantId "
            + "AND r.measurementDate BETWEEN :startDate AND :endDate")
    List<Result> findResultsByMeasurementDateBetween(@Param("tenantId") String tenantId,
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Último resultado por parâmetro
    @Query("SELECT r FROM Result r WHERE r.tenantId = :tenantId "
            + "AND r.measurementDate = (SELECT MAX(r2.measurementDate) FROM Result r2 "
            + "WHERE r2.parameter = r.parameter AND r2.tenantId = :tenantId)")
    List<Result> findLatestResultsByParameter(@Param("tenantId") String tenantId);

    // Buscas por sample com detalhes
    @Query("SELECT r FROM Result r WHERE r.sampleId = :sampleId ORDER BY r.measurementDate DESC")
    List<Result> findBySampleIdOrderByMeasurementDate(@Param("sampleId") String sampleId);

    // Agregações
    @Query("SELECT r.parameter, COUNT(r) FROM Result r WHERE r.tenantId = :tenantId GROUP BY r.parameter")
    List<Object[]> countResultsByParameter(@Param("tenantId") String tenantId);

    @Query("SELECT COUNT(r) FROM Result r WHERE r.sampleId = :sampleId")
    long countBySampleId(@Param("sampleId") String sampleId);

    @Query("SELECT COUNT(r) FROM Result r WHERE r.experimentId = :experimentId")
    long countByExperimentId(@Param("experimentId") String experimentId);

    // Buscas avançadas
    @Query("SELECT r FROM Result r WHERE r.tenantId = :tenantId "
            + "AND r.isValid = true ORDER BY r.measurementDate DESC")
    List<Result> findRecentValidResults(@Param("tenantId") String tenantId, Pageable pageable);

    @Query("SELECT r FROM Result r WHERE r.sampleId = :sampleId AND r.isValid = true")
    List<Result> findValidResultsBySample(@Param("sampleId") String sampleId);

    // Verificaçõoes
    boolean existsBySampleIdAndParameter(String sampleId, String parameter);

}
