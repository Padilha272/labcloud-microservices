package com.labcloud.sample.repositories;

import com.labcloud.sample.models.Sample;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository 
public interface SampleRepository extends JpaRepository<Sample, String>{

    //Buscas básicas 
    List<Sample> findByTenantId(String tenantId);
    Page<Sample> findByTenantId(String tenantId, Pageable pageable);

    List<Sample> findByExperimentId(String experimentId);
    Page<Sample> findByExperimentId(String experimentId, Pageable pageable);

    List<Sample> findByCreatedBy(String userId);

    //Buscas por tipo
    List<Sample> findByTenantIdAndType(String tenantId, String type);

    //Buscas por nome
    List<Sample> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name);

    //Buscas avançadas
    @Query("SELECT s FROM Sample s WHERE s.tenantId = :tenantId " +
           "AND s.collectionDate BETWEEN :startDate AND :endDate")
    List<Sample> findSamplesByCollectionDateBetween(
            @Param("tenantId") String tenantId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT s FROM Sample s JOIN FETCH s.experiment WHERE s.id = :id")
    Optional<Sample> findByIdWithExperiment(@Param("id") String id);

    //Contagens
    @Query("SELECT s.type, COUNT(s) FROM Sample s WHERE s.tenantId = :tenantId GROUP BY s.type")
    List<Object[]> countSamplesByType(@Param("tenantId") String tenantId);

    @Query("SELECT COUNT(s) FROM Sample s WHERE s.experiment.id = :experimentId")
    long countByExperimentId(@Param("experimentId") String experimentId);

}
